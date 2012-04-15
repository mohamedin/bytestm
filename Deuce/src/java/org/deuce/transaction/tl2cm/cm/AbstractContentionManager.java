package org.deuce.transaction.tl2cm.cm;

abstract public class AbstractContentionManager implements ContentionManager {

	public boolean requiresPriorities() {
		return false;
	}
	
	public boolean requiresTimestamps() {
		return false;
	}

}
