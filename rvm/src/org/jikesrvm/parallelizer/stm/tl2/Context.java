package org.jikesrvm.parallelizer.stm.tl2;

import java.util.concurrent.atomic.AtomicInteger;

import org.jikesrvm.parallelizer.stm.WriteSetHashOnly;
import org.jikesrvm.parallelizer.stm.memory.ImmortalsMemoryManager;


final public class Context{
	
	public static AtomicInteger clock;

	public static final int DEFAULT_CAPACITY = 4096;
	public int[] readSet;
	public int readSetNextAvaliable = 0;
	public long time;
	public final WriteSetHashOnly writeSet;
	
	//Used by the thread to mark locks it holds.
	public final byte[] locksMarker;
	
	//Marked on beforeRead, used for the double lock check
	public int localClock;
	public int lastReadLock;
	public Object[] objects;
	public int objectsLenght =0;
	public boolean isNotActive = true;
	public boolean isEarlyAborted =false;
	public Context(){
		this.localClock = clock.get();
		writeSet = (WriteSetHashOnly) ImmortalsMemoryManager.newObject(WriteSetHashOnly.class);
		objects = (Object[]) ImmortalsMemoryManager.newObjectArray(512, Object.class);
		readSet = ImmortalsMemoryManager.newIntArray(DEFAULT_CAPACITY);
		locksMarker = ImmortalsMemoryManager.newByteArray(LockTable.LOCKS_SIZE /8 + 1);
	}
	public static void staticInit(){
		clock = new AtomicInteger( 0);
	}
	public void init(int atomicBlockId){
		
		this.writeSet.clear();
		this.localClock = clock.get();	
		isNotActive = false;
		objectsLenght =0;
		readSetNextAvaliable = 0;
	}
}
