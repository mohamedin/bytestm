package org.deuce.transaction.tl2mam2;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2mam2.ReadSet.ReadFeild;
import org.deuce.transform.Exclude;

/**
 * TL2 implementation
 *
 * @author	Mohamed Mohamedin
 * @since	1.0
 */
@Exclude
final public class Context implements org.deuce.transaction.Context{
	
	final private static AtomicInteger clock = new AtomicInteger( 0);

	final private ReadSet readSet = new ReadSet();
	final private WriteSetHashOnly writeSet = new WriteSetHashOnly();
		
	//Used by the thread to mark locks it holds.
	final private byte[] locksMarker = new byte[LockTable.LOCKS_SIZE /8 + 1];
	long time;
	//Marked on beforeRead, used for the double lock check
	private int localClock;
	private int lastReadLock;
	
	public Context(){
		this.localClock = clock.get();
	}
	
	
	public void init(int atomicBlockId, String metainf){
		//time = System.currentTimeMillis();
		this.readSet.clear(); 
		this.writeSet.clear();
		this.localClock = clock.get();	
//		this.objectPool.clear();
//		this.booleanPool.clear();
//		this.bytePool.clear();
//		this.charPool.clear();
//		this.shortPool.clear();
//		this.intPool.clear();
//		this.longPool.clear();
//		this.floatPool.clear();
//		this.doublePool.clear();
	}
	
	public boolean commit(){
        if (writeSet.isEmpty()) // if the writeSet is empty no need to lock a thing. 
        {	
//			Statistics.commits++;
//			Statistics.readOnlyCommittedTx++;
//			long diff = System.currentTimeMillis() - time;
//			Statistics.txTimeAvg += diff;
//			if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
        	return true;
        }
        		
		int lockedCounter = 0;//used to count how many fields where locked if unlock is needed 
		try
		{
			for (int i=0; i<writeSet.indexSize; i++){
				int index = writeSet.indexes[i];
				LockTable.lock((System.identityHashCode( writeSet.objects[index]) + (int)writeSet.refs[index]) & LockTable.MASK, locksMarker);
				++lockedCounter;
			}
			readSet.checkClock( localClock);
		}
		catch( TransactionException exception){
			for (int i=0; i<writeSet.indexSize; i++){
				if( lockedCounter-- == 0)
					break;
				int index = writeSet.indexes[i];
				LockTable.unLock((System.identityHashCode( writeSet.objects[index]) + (int)writeSet.refs[index]) & LockTable.MASK, locksMarker);
			}
			//Statistics.retries++;
			return false;
		}

		final int newClock = clock.incrementAndGet();

		writeSet.writeBackTL2(newClock, locksMarker);
		
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
		ReadFeild current = readSet.getCurrent();
		int hash = current.currentReadFieldAccessHash;

		// Check the read is still valid
		LockTable.checkLock(hash, localClock, lastReadLock);

		// Check if it is already included in the write set
		return writeSet.contains( current.currentReadFieldAccessRef,current.currentReadFieldAccessField);
	}

//	private void addWriteAccess0(Object obj, long ref, long val, byte size, Object real){
////		Statistics.writesCountMax++;
//		// Add to write set
//		writeSet.add(obj,ref,val,size,real);
//	}
	
	public void beforeReadAccess(Object obj, long field) {
//		Statistics.readSetSizeMax++;
//		ReadFieldAccess next = 
			int hash = readSet.getNext(obj, field);
//		next.init(obj, field);

		// Check the read is still valid
		lastReadLock = LockTable.checkLock(hash, localClock);
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
		writeSet.add(obj,field,0,WriteSetHashOnly.OBJECT,value);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, boolean value, long field) {
		writeSet.add(obj,field,value?1:0,WriteSetHashOnly.BOOLEAN,null);
//		BooleanWriteFieldAccess next = booleanPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, byte value, long field) {
		writeSet.add(obj,field,value,WriteSetHashOnly.BYTE,null);
//		ByteWriteFieldAccess next = bytePool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, char value, long field) {
		writeSet.add(obj,field,value,WriteSetHashOnly.CHAR,null);
//		CharWriteFieldAccess next = charPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, short value, long field) {
		writeSet.add(obj,field,value,WriteSetHashOnly.SHORT,null);
//		ShortWriteFieldAccess next = shortPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, int value, long field) {
		writeSet.add(obj,field,value,WriteSetHashOnly.INT,null);
//		IntWriteFieldAccess next = intPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, long value, long field) {
		writeSet.add(obj,field,value,WriteSetHashOnly.LONG,null);
//		LongWriteFieldAccess next = longPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, float value, long field) {
		writeSet.add(obj,field,Float.floatToIntBits(value),WriteSetHashOnly.FLOAT,null);
//		FloatWriteFieldAccess next = floatPool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}

	
	public void onWriteAccess(Object obj, double value, long field) {
		writeSet.add(obj,field,Double.doubleToLongBits(value),WriteSetHashOnly.DOUBLE,null);
//		DoubleWriteFieldAccess next = doublePool.getNext();
//		next.set(value, obj, field);
//		addWriteAccess0(next);
	}
	
}
