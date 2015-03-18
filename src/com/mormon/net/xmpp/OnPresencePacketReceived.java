package com.mormon.net.xmpp;

import com.mormon.net.entities.Account;
import com.mormon.net.xmpp.stanzas.PresencePacket;

public interface OnPresencePacketReceived extends PacketReceived {
	public void onPresencePacketReceived(Account account, PresencePacket packet);
}
