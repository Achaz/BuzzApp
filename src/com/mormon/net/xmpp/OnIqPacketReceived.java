package com.mormon.net.xmpp;

import com.mormon.net.entities.Account;
import com.mormon.net.xmpp.stanzas.IqPacket;

public interface OnIqPacketReceived extends PacketReceived {
	public void onIqPacketReceived(Account account, IqPacket packet);
}
