package com.mormon.net.xmpp;

import com.mormon.net.entities.Contact;

public interface OnContactStatusChanged {
	public void onContactStatusChanged(final Contact contact, final boolean online);
}
