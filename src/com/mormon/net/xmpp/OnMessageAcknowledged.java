package com.mormon.net.xmpp;

import com.mormon.net.entities.Account;

public interface OnMessageAcknowledged {
	public void onMessageAcknowledged(Account account, String id);
}
