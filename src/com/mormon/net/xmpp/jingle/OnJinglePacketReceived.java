package com.mormon.net.xmpp.jingle;

import com.mormon.net.entities.Account;
import com.mormon.net.xmpp.PacketReceived;
import com.mormon.net.xmpp.jingle.stanzas.JinglePacket;

public interface OnJinglePacketReceived extends PacketReceived {
	public void onJinglePacketReceived(Account account, JinglePacket packet);
}
