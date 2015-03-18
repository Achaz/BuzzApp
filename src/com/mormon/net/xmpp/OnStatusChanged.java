package com.mormon.net.xmpp;

import com.mormon.net.entities.Account;

public interface OnStatusChanged {
	public void onStatusChanged(Account account);
}
