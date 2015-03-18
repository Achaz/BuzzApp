package com.mormon.net.xmpp.jingle.stanzas;

import com.mormon.net.xml.Element;

public class Reason extends Element {
	private Reason(String name) {
		super(name);
	}

	public Reason() {
		super("reason");
	}
}
