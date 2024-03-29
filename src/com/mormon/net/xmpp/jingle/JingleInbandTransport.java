package com.mormon.net.xmpp.jingle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.util.Base64;

import com.mormon.net.entities.Account;
import com.mormon.net.entities.DownloadableFile;
import com.mormon.net.utils.CryptoHelper;
import com.mormon.net.xml.Element;
import com.mormon.net.xmpp.OnIqPacketReceived;
import com.mormon.net.xmpp.jid.Jid;
import com.mormon.net.xmpp.stanzas.IqPacket;

public class JingleInbandTransport extends JingleTransport {

	private Account account;
	private Jid counterpart;
	private int blockSize;
	private int bufferSize;
	private int seq = 0;
	private String sessionId;

	private boolean established = false;

	private boolean connected = true;

	private DownloadableFile file;
	private JingleConnection connection;

	private InputStream fileInputStream = null;
	private OutputStream fileOutputStream = null;
	private long remainingSize = 0;
	private long fileSize = 0;
	private MessageDigest digest;

	private OnFileTransmissionStatusChanged onFileTransmissionStatusChanged;

	private OnIqPacketReceived onAckReceived = new OnIqPacketReceived() {
		@Override
		public void onIqPacketReceived(Account account, IqPacket packet) {
			if (connected && packet.getType() == IqPacket.TYPE.RESULT) {
				sendNextBlock();
			}
		}
	};

	public JingleInbandTransport(final JingleConnection connection, final String sid, final int blocksize) {
		this.connection = connection;
		this.account = connection.getAccount();
		this.counterpart = connection.getCounterPart();
		this.blockSize = blocksize;
		this.bufferSize = blocksize / 4;
		this.sessionId = sid;
	}

	public void connect(final OnTransportConnected callback) {
		IqPacket iq = new IqPacket(IqPacket.TYPE.SET);
		iq.setTo(this.counterpart);
		Element open = iq.addChild("open", "http://jabber.org/protocol/ibb");
		open.setAttribute("sid", this.sessionId);
		open.setAttribute("stanza", "iq");
		open.setAttribute("block-size", Integer.toString(this.blockSize));
		this.connected = true;
		this.account.getXmppConnection().sendIqPacket(iq,
				new OnIqPacketReceived() {

					@Override
					public void onIqPacketReceived(Account account,
							IqPacket packet) {
						if (packet.getType() == IqPacket.TYPE.ERROR) {
							callback.failed();
						} else {
							callback.established();
						}
					}
				});
	}

	@Override
	public void receive(DownloadableFile file,
			OnFileTransmissionStatusChanged callback) {
		this.onFileTransmissionStatusChanged = callback;
		this.file = file;
		try {
			this.digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			file.getParentFile().mkdirs();
			file.createNewFile();
			this.fileOutputStream = file.createOutputStream();
			if (this.fileOutputStream == null) {
				callback.onFileTransferAborted();
				return;
			}
			this.remainingSize = this.fileSize = file.getExpectedSize();
		} catch (final NoSuchAlgorithmException | IOException e) {
			callback.onFileTransferAborted();
		}
    }

	@Override
	public void send(DownloadableFile file,
			OnFileTransmissionStatusChanged callback) {
		this.onFileTransmissionStatusChanged = callback;
		this.file = file;
		try {
			this.remainingSize = this.file.getSize();
			this.fileSize = this.remainingSize;
			this.digest = MessageDigest.getInstance("SHA-1");
			this.digest.reset();
			fileInputStream = this.file.createInputStream();
			if (fileInputStream == null) {
				callback.onFileTransferAborted();
				return;
			}
			if (this.connected) {
				this.sendNextBlock();
			}
		} catch (NoSuchAlgorithmException e) {
			callback.onFileTransferAborted();
		}
	}

	@Override
	public void disconnect() {
		this.connected = false;
		if (this.fileOutputStream != null) {
			try {
				this.fileOutputStream.close();
			} catch (IOException e) {

			}
		}
		if (this.fileInputStream != null) {
			try {
				this.fileInputStream.close();
			} catch (IOException e) {

			}
		}
	}

	private void sendNextBlock() {
		byte[] buffer = new byte[this.bufferSize];
		try {
			int count = fileInputStream.read(buffer);
			if (count == -1) {
				file.setSha1Sum(CryptoHelper.bytesToHex(digest.digest()));
				fileInputStream.close();
				this.onFileTransmissionStatusChanged.onFileTransmitted(file);
			} else {
				this.remainingSize -= count;
				this.digest.update(buffer);
				String base64 = Base64.encodeToString(buffer, Base64.NO_WRAP);
				IqPacket iq = new IqPacket(IqPacket.TYPE.SET);
				iq.setTo(this.counterpart);
				Element data = iq.addChild("data",
						"http://jabber.org/protocol/ibb");
				data.setAttribute("seq", Integer.toString(this.seq));
				data.setAttribute("block-size",
						Integer.toString(this.blockSize));
				data.setAttribute("sid", this.sessionId);
				data.setContent(base64);
				this.account.getXmppConnection().sendIqPacket(iq,
						this.onAckReceived);
				this.seq++;
				connection.updateProgress((int) ((((double) (this.fileSize - this.remainingSize)) / this.fileSize) * 100));
			}
		} catch (IOException e) {
			this.onFileTransmissionStatusChanged.onFileTransferAborted();
		}
	}

	private void receiveNextBlock(String data) {
		try {
			byte[] buffer = Base64.decode(data, Base64.NO_WRAP);
			if (this.remainingSize < buffer.length) {
				buffer = Arrays
						.copyOfRange(buffer, 0, (int) this.remainingSize);
			}
			this.remainingSize -= buffer.length;


			this.fileOutputStream.write(buffer);

			this.digest.update(buffer);
			if (this.remainingSize <= 0) {
				file.setSha1Sum(CryptoHelper.bytesToHex(digest.digest()));
				fileOutputStream.flush();
				fileOutputStream.close();
				this.onFileTransmissionStatusChanged.onFileTransmitted(file);
			} else {
				connection.updateProgress((int) ((((double) (this.fileSize - this.remainingSize)) / this.fileSize) * 100));
			}
		} catch (IOException e) {
			this.onFileTransmissionStatusChanged.onFileTransferAborted();
		}
	}

	public void deliverPayload(IqPacket packet, Element payload) {
		if (payload.getName().equals("open")) {
			if (!established) {
				established = true;
				connected = true;
				this.account.getXmppConnection().sendIqPacket(
						packet.generateResponse(IqPacket.TYPE.RESULT), null);
			} else {
				this.account.getXmppConnection().sendIqPacket(
						packet.generateResponse(IqPacket.TYPE.ERROR), null);
			}
		} else if (connected && payload.getName().equals("data")) {
			this.receiveNextBlock(payload.getContent());
			this.account.getXmppConnection().sendIqPacket(
					packet.generateResponse(IqPacket.TYPE.RESULT), null);
		} else {
			// TODO some sort of exception
		}
	}
}
