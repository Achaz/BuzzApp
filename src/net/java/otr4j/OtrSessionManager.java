package net.java.otr4j;

import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;

/**
 * 
 * @author George Politis
 * 
 */
public interface OtrSessionManager {

	/**
     * Get an OTR session.
     * @return MVN_PASS_JAVADOC_INSPECTION
     */
	public abstract Session getSession(SessionID sessionID);

	public abstract void addOtrEngineListener(OtrEngineListener l);

	public abstract void removeOtrEngineListener(OtrEngineListener l);
}
