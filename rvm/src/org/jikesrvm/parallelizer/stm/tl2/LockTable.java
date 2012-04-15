package org.jikesrvm.parallelizer.stm.tl2;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Implements Bloom filter map
 * 
 * @author Guy Korland (from Deuce)
 * @since 1.0
 *
 */

public class LockTable {

	// Failure transaction 
	private static stm.STMException FAILURE_EXCEPTION;
	final public static int LOCKS_SIZE = 1<<20; // amount of locks - TODO add system property
	final public static int MASK = 0xFFFFF;
	final private static int LOCK = 1 << 31;
	final private static int UNLOCK = ~LOCK;
	
	final static private int MODULE_8 = 7; //Used for %8
	final static private int DIVIDE_8 = 3; //Used for /8

	public static AtomicIntegerArray locks ;// array of 2^20 entries of 32-bit lock words
	public static void staticInit(){
		FAILURE_EXCEPTION = new stm.STMException();
		locks =  new AtomicIntegerArray(LOCKS_SIZE);
	}
	public static void lock( int lockIndex, byte[] contextLocks) throws stm.STMException{
		final int lock = locks.get(lockIndex); 
		final int selfLockIndex = lockIndex>>>DIVIDE_8;
		final byte selfLockByte = contextLocks[selfLockIndex];
		final byte selfLockBit = (byte)(1 << (lockIndex & MODULE_8));
		
		if( (lock & LOCK) != 0){  //is already locked?
			if( (selfLockByte & selfLockBit) != 0) // check for self locking
				return;
			throw FAILURE_EXCEPTION; 
		}

		boolean isLocked = locks.compareAndSet(lockIndex, lock, lock | LOCK);
		
		if( !isLocked)
			throw FAILURE_EXCEPTION;
		
		contextLocks[selfLockIndex] |= selfLockBit; //mark in self locks
	}

	public static int checkLock(int lockIndex, int clock) {
		int lock = locks.get(lockIndex);

		if( clock < (lock & UNLOCK)) // check the clock without lock, TODO check if this is the best way
			throw FAILURE_EXCEPTION;
		
		return lock;
	}
	

	public static void checkLock(int lockIndex, int clock, int expected) {
		int lock = checkLock( lockIndex, clock);
		if( lock != expected || (lock & LOCK) != 0)
			throw FAILURE_EXCEPTION;
	}

	public static void unLock( int lockIndex, byte[] contextLocks){
		int lockedValue = locks.get( lockIndex);
		int unlockedValue = lockedValue & UNLOCK;
		locks.set(lockIndex, unlockedValue);
		
		clearSelfLock(lockIndex, contextLocks);
	}

	public static void setAndReleaseLock( int hash, int newClock, byte[] contextLocks){
		int lockIndex = hash & MASK;
		locks.set(lockIndex, newClock);
		clearSelfLock( lockIndex, contextLocks);
	}
	
	/**
	* Clears lock marker from self locking array
	*/
	private static void clearSelfLock( int lockIndex, byte[] contextLocks){
		// clear marker TODO might clear all bits
		contextLocks[lockIndex>>>DIVIDE_8] &= ~(1 << (lockIndex & MODULE_8)); 
	}
}
