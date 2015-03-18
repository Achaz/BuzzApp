package com.mormon.net.xmpp;

import com.mormon.net.entities.Account;
import com.mormon.net.xmpp.stanzas.MessagePacket;

public interface OnMessagePacketReceived extends PacketReceived {
	public void onMessagePacketReceived(Account account, MessagePacket packet);
}
