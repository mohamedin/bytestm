package org.jikesrvm.parallelizer.stm.alternatives;

import java.util.concurrent.atomic.AtomicInteger;

import org.jikesrvm.VM;
import org.jikesrvm.parallelizer.stm.Statistics;
import org.jikesrvm.parallelizer.stm.WriteSetHashOnly;
import org.jikesrvm.parallelizer.stm.memory.ImmortalsMemoryManager;
import org.jikesrvm.parallelizer.stm.tl2.Context;
import org.jikesrvm.parallelizer.stm.tl2.LockTable;
import org.jikesrvm.runtime.Magic;
import org.jikesrvm.scheduler.RVMThread;
import org.vmmagic.unboxed.Address;

import stm.STMException;

/**
 * STM implementation using TL2 algorithm and uses Open addressing hashing for the write-set
 * This adds thread header meta data and immutable memory
 * To use it, make it the STM class and uncomment the code
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 *
 * This is the latest version of TL2
 */

/* TODO list:
 * 1. Embed code inside the VM instruction (stop using method invocation)
 * 2. Support Exclude to exclude some classes/packages 
 * 3. Use inlining instead of code repetition
 * 4. add support for multiple packages
 */
public class TL2_Memory_ThHeader_STM {
//	private static int JTOC = 0;
//
//
//	public static boolean enableSTM = false;
//	
//	public static class ThreadMetaData{
//		public static AtomicInteger clock;
//
//		public static final int DEFAULT_CAPACITY = 4096;
//		public int[] readSet;
//		public int readSetNextAvaliable = 0;
//		public long time;
//		public final WriteSetHashOnly writeSet;
//		
//		//Used by the thread to mark locks it holds.
//		public final byte[] locksMarker;
//		
//		//Marked on beforeRead, used for the double lock check
//		public int localClock;
//		public int lastReadLock;
//		public Object[] objects;
//		public int objectsLenght =0;
//		public boolean isNotActive = true;
//		public boolean isEarlyAborted =false;
//		public ThreadMetaData(){
//			this.localClock = clock.get();
//			writeSet = (WriteSetHashOnly) ImmortalsMemoryManager.newObject(WriteSetHashOnly.class);
//			objects = (Object[]) ImmortalsMemoryManager.newObjectArray(512, Object.class);
//			readSet = ImmortalsMemoryManager.newIntArray(DEFAULT_CAPACITY);
//			locksMarker = ImmortalsMemoryManager.newByteArray(LockTable.LOCKS_SIZE /8 + 1);
//		}
//		public static void staticInit(){
//			clock = new AtomicInteger( 0);
//		}
//		public void init(){
//			
//			this.writeSet.clear();
//			this.localClock = clock.get();	
//			isNotActive = false;
//			objectsLenght =0;
//			readSetNextAvaliable = 0;
//		}
//	}
//
//	private static long totalReties = 0;
//	private static long totalEarlyReties = 0;
//	private static long committedTrans = 0;
//	
//	public static void boot(){
//		VM.sysWriteln("STM booted");
//		LockTable.staticInit();
//		ThreadMetaData.staticInit();
//		JTOC = Magic.getJTOC().toInt();
//	}
//
//	public static int memLoadIntArr(int ref, int index, int val) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//
//		ref += (index<<2);
//
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return (int) data.writeSet.lastVal;
//		}
//		
//		return val;
//	}
//
//	public static long memLoadLongArr(int ref, int index, long val) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//
//		ref += (index<<3);
//
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//
//	public static byte memLoadByteArr(int ref, int index, byte val) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//
//		ref += index;
//
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return (byte) data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//	private static void addToReadSet(ThreadMetaData data, int ref){
////		Statistics.readSetSizeMax++;
//		data.readSetNextAvaliable++;
//		if (data.readSetNextAvaliable >= data.readSet.length){
//			int orignLength = data.readSet.length;
//			int[] tmpReadSet = new int[ 2*orignLength];
//			System.arraycopy(data.readSet, 0, tmpReadSet, 0, orignLength);
//			data.readSet = tmpReadSet;
//		}
//		data.readSet[data.readSetNextAvaliable] = ref;
//	}
//	public static short memLoadShortArr(int ref, int index, short val) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//
//		ref += (index<<1);
//
//
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return (short) data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//	
//	
//	public static void memStoreIntArr(int ref, int index, int val){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=(index<<2);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<2);
//		
//		data.writeSet.add(ref, val, (byte) 4);
//	}
//	
//	public static void memStoreRefArr(int ref, int index, int val){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=(index<<2);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<2);
//		
//		data.writeSet.add(ref, val, (byte) 4);
//
//		if (val !=0){
//			Object obj2 = Magic.addressAsObject(Address.fromIntSignExtend(val));
//			for (int i=0;i<data.objectsLenght;i++){
//				if (data.objects[i].equals(obj2))
//					return;
//			}
//			data.objects[data.objectsLenght] = obj2; 
//			data.objectsLenght++;
//		}
//	}
//	
//	public static void memStoreLongArr(int ref, int index, long val){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=(index<<3);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//		
//		ref +=(index<<3);
//		
//		data.writeSet.add(ref, val, (byte) 8);
//	}
//
//	public static void memStoreByteArr(int ref, int index, byte val){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref += index;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref += index;
//		
//		data.writeSet.add(ref, val, (byte) 1);
//	}
//
//	public static void memStoreShortArr(int ref, int index, short val){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=(index<<1);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<1);
//		
//		data.writeSet.add(ref, val, (byte) 2);
//	}
//
//	public static int memLoadIntStatic(int val, int offset) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//
//		offset +=JTOC;
//		
//		addToReadSet(data, offset);
//		data.isEarlyAborted =true;
//		int hash = offset & LockTable.MASK;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(offset)){
//			return (int) data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//
//	public static long memLoadLongStatic(long val, int offset) throws STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//		
//		offset +=JTOC;
//		
//		addToReadSet(data, offset);
//		int hash = offset & LockTable.MASK;
//		data.isEarlyAborted =true;
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(offset)){
//			return data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//	
//	public static void memStoreIntStatic(int val, int offset, byte size, boolean isObject){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			offset +=JTOC;
//			Address dstPtr = Address.fromIntSignExtend(offset);
//			dstPtr.store(val);
//			return;
//		}
//		
//		offset +=JTOC;
//
//		data.writeSet.add(offset, val, size);
//
//		if (isObject && val !=0){
//			Object obj2 = Magic.addressAsObject(Address.fromIntSignExtend(val));
//			for (int i=0;i<data.objectsLenght;i++){
//				if (data.objects[i].equals(obj2))
//					return;
//			}
//			data.objects[data.objectsLenght] = obj2; 
//			data.objectsLenght++;
//		}
//	}
//	
//	public static void memStoreLongStatic(long val, int offset){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			offset +=JTOC;
//			Address dstPtr = Address.fromIntSignExtend(offset);
//			dstPtr.store(val);
//			return;
//		}
//		
//		offset +=JTOC;
//
//		data.writeSet.add(offset, val, (byte) 8);
//	}
//
//	
//	public static int memLoadInt(int ref, int val, int offset) throws stm.STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//		
//		ref+=offset;
//		
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return (int) data.writeSet.lastVal;
//		}
//
//		return val;
//	}
//	
//	
//	public static long memLoadLong(int ref, long val, int offset) throws stm.STMException{
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return val;
//		
//		ref +=offset;
//
//		addToReadSet(data, ref);
//		int hash = ref & LockTable.MASK;
//		data.isEarlyAborted =true;
//		// Check the read is still valid
//		data.lastReadLock = LockTable.locks.get(hash);
//		// Check the read is still valid
//		LockTable.checkLock(hash, data.localClock, data.lastReadLock);
//		data.isEarlyAborted =false;
//		// Check if it is already included in the write set
//		if (data.writeSet.contains(ref)){
//			return data.writeSet.lastVal;
//		}
//		return val;
//	}
//	
//	public static void memStoreInt(int ref, int val, int offset, byte size, boolean isObject){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=offset;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref+=offset;
//
//		data.writeSet.add(ref, val, size);
//
//		if (isObject && val !=0){
//			Object obj2 = Magic.addressAsObject(Address.fromIntSignExtend(val));
//			for (int i=0;i<data.objectsLenght;i++){
//				if (data.objects[i].equals(obj2))
//					return;
//			}
//			data.objects[data.objectsLenght] = obj2; 
//			data.objectsLenght++;
//		}
//	}
//	
//	public static void memStoreLong(int ref, long val, int offset){
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive){
//			ref +=offset;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//		
//		ref+=offset;
//
//		data.writeSet.add(ref, val, (byte) 8);
//	}
//	
//	public static void xBegin(){
//		RVMThread thread = RVMThread.getCurrentThread();
//
//		ThreadMetaData data = thread.STMData;
//		if (data==null){
//			data = new ThreadMetaData();
//			thread.STMData = data;
//		}
//
//		data.init();
//	}
//	
//	public static int xCommit(){
//
//		ThreadMetaData data = RVMThread.getCurrentThread().STMData;
//		if (data == null || data.isNotActive)
//			return 0;
//		
//		//early
//		if (data.isEarlyAborted){
//			data.isEarlyAborted = false;
////			totalEarlyReties++;
////			Statistics.retries++;
//			return 1;
//		}
//
//        if (data.writeSet.isEmpty()){ // if the writeSet is empty no need to lock a thing.
//        	data.isNotActive = true;
////        	committedTrans++;
////			Statistics.commits++;
////			Statistics.readOnlyCommittedTx++;
////			long diff = System.currentTimeMillis() - data.time;
////			Statistics.txTimeAvg += diff;
////			if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
//        	return 0;
//        }
//        		
//		int lockedCounter = 0;//used to count how many fields where locked if unlock is needed 
//		try
//		{
//			for (int i=0; i<data.writeSet.indexSize; i++){
//				int index = data.writeSet.indexes[i];
//				LockTable.lock(data.writeSet.refs[index] & LockTable.MASK, data.locksMarker);
//				++lockedCounter;
//			}
//	        for (int i = 0; i < data.readSetNextAvaliable; i++) {
//	        	LockTable.checkLock( data.readSet[i]& LockTable.MASK, data.localClock);
//	        	data.readSet[i]=0;
//	        }
//		}
//		catch( stm.STMException exception){
//			for (int i=0; i<data.writeSet.indexSize; i++){
//				if( lockedCounter-- == 0)
//					break;
//				int index = data.writeSet.indexes[i];
//				LockTable.unLock(data.writeSet.refs[index] & LockTable.MASK,data.locksMarker);
//			}
//
////			totalReties++;
////			Statistics.retries++;
//			return 1;
//		}
//
//		final int newClock = ThreadMetaData.clock.incrementAndGet();
//
//		data.writeSet.writeBackTL2(newClock, data.locksMarker);
//		data.isNotActive = true;
////		committedTrans++;
////		Statistics.commits++;
////		long diff = System.currentTimeMillis() - data.time;
////		Statistics.txTimeAvg += diff;
////		if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
//		return 0;
//	}
//
//	public static void printStatistics() {
//		VM.sysWriteln("STM: committed: " + committedTrans + ", Reties: " + totalReties + ", Early: " + totalEarlyReties/*+ ", In loop: " + inTheLoop + ", In begin: "+ inBeginingOfCommit*/);
//		VM.sysWriteln(Statistics.asString());
//	}
}
