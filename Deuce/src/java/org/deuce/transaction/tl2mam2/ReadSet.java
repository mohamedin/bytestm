package org.deuce.transaction.tl2mam2;

import org.deuce.transform.Exclude;

/**
 * Represents the transaction read set.
 * And acts as a recycle pool of the {@link ReadFieldAccess}.
 *  
 * @author Guy Korland
 * @since 0.7
 */
@Exclude
public class ReadSet{
	
	private static final int DEFAULT_CAPACITY = 1024;
	private int[]  readSet = new int [DEFAULT_CAPACITY];
//	private long[]  readSetField = new long [DEFAULT_CAPACITY];
//	private Object[]  readSetRef = new Object[DEFAULT_CAPACITY];
//	private ReadFieldAccess[] readSet = new ReadFieldAccess[DEFAULT_CAPACITY];
	private int nextAvaliable = 0;
	
	public static class ReadFeild{
		public int currentReadFieldAccessHash;
		public long currentReadFieldAccessField;
		public Object currentReadFieldAccessRef;
	}

	private ReadFeild currentReadField;
	
	public ReadSet(){
		currentReadField = new ReadFeild();
//		fillArray( 0);
	}
	
	public void clear(){
		nextAvaliable = 0;
	}

//	private void fillArray( int offset){
//		for( int i=offset ; i < readSet.length ; ++i){
//			readSet[i] = new ReadFieldAccess();
//		}
//	}

	public int getNext(Object obj, long field){
		if( nextAvaliable >= readSet.length){
			int orignLength = readSet.length;
			int[] tmpReadSet = new int[ 2*orignLength];
			System.arraycopy(readSet, 0, tmpReadSet, 0, orignLength);
			readSet = tmpReadSet;
//			fillArray( orignLength);
			System.out.println("Extening readset");
		}
		readSet[nextAvaliable] = (System.identityHashCode( obj) + (int)field) & LockTable.MASK;
		currentReadField.currentReadFieldAccessHash = readSet[nextAvaliable];
		currentReadField.currentReadFieldAccessField = field;
		currentReadField.currentReadFieldAccessRef = obj;
		nextAvaliable++;
		return currentReadField.currentReadFieldAccessHash;
	}
	
	public ReadFeild getCurrent(){
		return currentReadField;
	}
	
    public void checkClock(int clock) {
        for (int i = 0; i < nextAvaliable; i++) {
        	LockTable.checkLock( readSet[i], clock);
        }
    }
 
}
