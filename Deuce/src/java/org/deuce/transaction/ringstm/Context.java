package org.deuce.transaction.ringstm;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

/**
 * RingSTM implementation
 *
 * @author	Mohamed Mohamedin
 * @since	1.0
 */
@Exclude
final public class Context implements org.deuce.transaction.Context{

	long time;
	
	private WriteSetHashOnly writeSet = new WriteSetHashOnly();
	private BloomFilter writeSig = new BloomFilter();
	private BloomFilter readSig = new BloomFilter();
	private int startTime;
	
	
	private static int lastComplete = 0;
	private static int lastInit = 0;
	private static AtomicInteger timestamp = new AtomicInteger(0);
	private static final int RING_ELEMENTS = 1024;
	private static BloomFilter[] ringWriteFilters = new BloomFilter[RING_ELEMENTS];
	static{
		for (int i=0; i<ringWriteFilters.length;i++)
			ringWriteFilters[i] = new BloomFilter();
	}

	final private static TransactionException FAILURE_EXCEPTION = new TransactionException( "Faild in flight.");
	
	public Context(){
		startTime = lastComplete;
	}
	
	
	public void init(int atomicBlockId, String metainf){
		//time = System.currentTimeMillis();
		writeSig.clear();
		readSig.clear();
		writeSet.clear();
		startTime = lastComplete;
	}
	
	private boolean checkInflight()
	{
		int commitTime =lastInit; 
	    // intersect against all new entries
        for (int i = commitTime; i >= startTime + 1; i--)
            if (ringWriteFilters[i % RING_ELEMENTS].intersect(readSig)){
                return true;
            }
        // wait for newest entry to be writeback-complete before continuing
        while (lastComplete < commitTime)
      	  ;//basy wait :( find solution to make it fast in java


        // detect ring rollover: start.ts must not have changed
        if (timestamp.get() > (startTime + RING_ELEMENTS)){
            return true;
        }
        startTime = commitTime;
        return false;
	}
	
	public boolean commit(){
        if (writeSet.isEmpty()) // if the writeSet is empty no need to do a thing. 
        {	
//			Statistics.commits++;
//			Statistics.readOnlyCommittedTx++;
//			long diff = System.currentTimeMillis() - time;
//			Statistics.txTimeAvg += diff;
//			if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
        	return true;
        }
		
	      int commitTime;
	      do {
	          commitTime = timestamp.get();
	          // get the latest ring entry, return if we've seen it already
	          if (commitTime != startTime) {
	              // wait for the latest entry to be initialized
	              while (lastInit < commitTime)
					;//basy wait :( find solution to make it fast in java

	              // intersect against all new entries
	              for (int i = commitTime; i >= startTime + 1; i--)
	                  if (ringWriteFilters[i % RING_ELEMENTS].intersect(readSig)){
//	                	  Statistics.retries++;
	                      return false;
	                  }

	              // wait for newest entry to be wb-complete before continuing
	              while (lastComplete < commitTime)
	            	  ;//basy wait :( find solution to make it fast in java

	              // detect ring rollover: start.ts must not have changed
	              if (timestamp.get() > (startTime + RING_ELEMENTS)){
//	            	  Statistics.retries++;
                      return false;
                  }
	              // ensure this tx doesn't look at this entry again
	              startTime = commitTime;
	          }
	      } while (!timestamp.compareAndSet(commitTime, commitTime +1));
	      // copy the bits over (use SSE, not indirection)
	      ringWriteFilters[(commitTime + 1) % RING_ELEMENTS].copy(writeSig);

	      // setting this says "the bits are valid"
	      lastInit = commitTime + 1;

	      // we're committed... run redo log, then mark ring entry COMPLETE
			//Write back
	      writeSet.writeBack();
	      lastComplete = commitTime + 1;

//		Statistics.commits++;
//		long diff = System.currentTimeMillis() - time;
//		Statistics.txTimeAvg += diff;
//		if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;

		return true;
	}
	
	public void rollback(){
//		Statistics.retries++;
	}

	private boolean onReadAccess0( Object obj, long field){
//		Statistics.readSetSizeMax++;
		
		readSig.add((System.identityHashCode( obj) + (int)field));
		if (checkInflight()) throw FAILURE_EXCEPTION;
		// Check if it is already included in the write set
		return writeSet.contains(obj,field);
	}

	
	public void beforeReadAccess(Object obj, long field) {
	}
	
	public Object onReadAccess( Object obj, Object value, long field){
		boolean writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return writeSet.objValues[(int) writeSet.lastVal];  
	}
		
	public boolean onReadAccess(Object obj, boolean value, long field) {
		boolean writeAccess = onReadAccess0(obj, field);
		if( !writeAccess )
			return value;
		
		return writeSet.lastVal>0;  
	}
	
	public byte onReadAccess(Object obj, byte value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return (byte)writeSet.lastVal;  
	}
	
	public char onReadAccess(Object obj, char value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return (char)writeSet.lastVal;  
	}
	
	public short onReadAccess(Object obj, short value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return (short) writeSet.lastVal;  

	}
	
	public int onReadAccess(Object obj, int value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return (int)writeSet.lastVal;  
	}
	
	public long onReadAccess(Object obj, long value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return writeSet.lastVal;  
	}
	
	public float onReadAccess(Object obj, float value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return Float.intBitsToFloat((int)writeSet.lastVal);  
	}
	
	public double onReadAccess(Object obj, double value, long field) {
		boolean  writeAccess = onReadAccess0(obj, field);
		if( !writeAccess)
			return value;
		
		return Double.longBitsToDouble(writeSet.lastVal);  
	}
	
	public void onWriteAccess( Object obj, Object value, long field){
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,0,WriteSetHashOnly.OBJECT,value);
	}
	
	public void onWriteAccess(Object obj, boolean value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value?1:0,WriteSetHashOnly.BOOLEAN,null);
	}
	
	public void onWriteAccess(Object obj, byte value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value,WriteSetHashOnly.BYTE,null);
	}
	
	public void onWriteAccess(Object obj, char value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value,WriteSetHashOnly.CHAR,null);
	}
	
	public void onWriteAccess(Object obj, short value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value,WriteSetHashOnly.SHORT,null);
	}
	
	public void onWriteAccess(Object obj, int value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value,WriteSetHashOnly.INT,null);
	}
	
	public void onWriteAccess(Object obj, long value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,value,WriteSetHashOnly.LONG,null);
	}

	public void onWriteAccess(Object obj, float value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,Float.floatToIntBits(value),WriteSetHashOnly.FLOAT,null);
	}

	
	public void onWriteAccess(Object obj, double value, long field) {
		writeSig.add((System.identityHashCode( obj) + (int)field));
		writeSet.add(obj,field,Double.doubleToLongBits(value),WriteSetHashOnly.DOUBLE,null);
	}
	
}
