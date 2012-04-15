package org.jikesrvm.parallelizer.stm.alternatives;

import org.jikesrvm.VM;
import org.jikesrvm.parallelizer.stm.tl2.Context;
import org.jikesrvm.parallelizer.stm.tl2.LockTable;
import org.jikesrvm.parallelizer.stm.tl2.WriteSet.LongVal;
import org.jikesrvm.runtime.Magic;
import org.vmmagic.unboxed.Address;

import stm.STMException;

/**
 * STM implementation using TL2 algorithm and uses arrays for the write-set
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */

/* TODO list:
 * 1. Embed code inside the VM instruction (stop using method invocation)
 * 2. Support Exclude to exclude some classes/packages 
 * 3. Use inlining instead of code repetition
 * 4. add support for multiple packages
 */
public class TL2STM {
//	private static int JTOC = 0;
//
//
//	public static boolean enableSTM = false;
//	
//	
//	private static ThreadLocal<Context> threadMetaData = null;
//	private static long totalReties = 0;
//	private static long totalEarlyReties = 0;
//	private static long committedTrans = 0;
//	
//	public static void boot(){
//		VM.sysWriteln("STM booted");
//		Context.staticInit();
//		LockTable.staticInit();
//		threadMetaData = new ThreadLocal<Context>();
//		JTOC = Magic.getJTOC().toInt();
//	}
//
//	public static int memLoadIntArr(int ref, int index, int val) throws STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return (int) writeAccess.val;  
//	}
//
//	public static long memLoadLongArr(int ref, int index, long val) throws STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return writeAccess.val;  
//	}
//
//	public static byte memLoadByteArr(int ref, int index, byte val) throws STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return (byte)writeAccess.val;  
//	}
//	private static void addToReadSet(Context data, int ref){
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
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return (short)writeAccess.val;  
//	}
//	
//	
//	public static void memStoreIntArr(int ref, int index, int val){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=(index<<2);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<2);
//		
//		data.writeSet.put(val, ref, (byte) 4);
//	}
//	
//	public static void memStoreRefArr(int ref, int index, int val){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=(index<<2);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<2);
//		
//		data.writeSet.put(val, ref, (byte) 4);
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
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=(index<<3);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//		
//		ref +=(index<<3);
//		
//		data.writeSet.put(val, ref, (byte) 8);
//	}
//
//	public static void memStoreByteArr(int ref, int index, byte val){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref += index;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref += index;
//		
//		data.writeSet.put(val, ref, (byte) 1);
//	}
//
//	public static void memStoreShortArr(int ref, int index, short val){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=(index<<1);
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref +=(index<<1);
//		
//		data.writeSet.put(val, ref, (byte) 2);
//	}
//
//	public static int memLoadIntStatic(int val, int offset) throws STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(offset);
//		if( writeAccess == null)
//			return val;
//		
//		return (int)writeAccess.val;  
//	}
//
//	public static long memLoadLongStatic(long val, int offset) throws STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(offset);
//		if( writeAccess == null)
//			return val;
//		
//		return writeAccess.val;  
//	}
//	
//	public static void memStoreIntStatic(int val, int offset, byte size, boolean isObject){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			offset +=JTOC;
//			Address dstPtr = Address.fromIntSignExtend(offset);
//			dstPtr.store(val);
//			return;
//		}
//		
//		offset +=JTOC;
//
//		data.writeSet.put(val, offset, size);
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
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			offset +=JTOC;
//			Address dstPtr = Address.fromIntSignExtend(offset);
//			dstPtr.store(val);
//			return;
//		}
//		
//		offset +=JTOC;
//
//		data.writeSet.put(val, offset, (byte) 8);
//	}
//
//	
//	public static int memLoadInt(int ref, int val, int offset) throws stm.STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return (int)writeAccess.val;  
//
//	}
//	
//	
//	public static long memLoadLong(int ref, long val, int offset) throws stm.STMException{
//		Context data = threadMetaData.get();
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
//		LongVal writeAccess = data.writeSet.contains(ref);
//		if( writeAccess == null)
//			return val;
//		
//		return writeAccess.val;  
//	}
//	
//	public static void memStoreInt(int ref, int val, int offset, byte size, boolean isObject){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=offset;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//
//		ref+=offset;
//
//		data.writeSet.put(val, ref, size);
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
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive){
//			ref +=offset;
//			Address dstPtr = Address.fromIntSignExtend(ref);
//			dstPtr.store(val);
//			return;
//		}
//		
//		ref+=offset;
//
//		data.writeSet.put(val, ref, (byte) 8);
//	}
//	
//	public static void xBegin(){
//		Context data = threadMetaData.get();
//		if (data==null){
//			data = new Context();
//			threadMetaData.set(data);
//		}
//		data.init(0);
//	}
//	
//	public static int xCommit(){
//		Context data = threadMetaData.get();
//		if (data == null || data.isNotActive)
//			return 0;
//		
//		//early
//		if (data.isEarlyAborted){
//			data.isEarlyAborted = false;
//			totalEarlyReties++;
//			return 1;
//		}
//
//        if (data.writeSet.isEmpty()){ // if the writeSet is empty no need to lock a thing.
//        	data.isNotActive = true;
//        	committedTrans++;
//        	return 0;
//        }
//        		
//		int lockedCounter = 0;//used to count how many fields where locked if unlock is needed 
//		try
//		{
//			for( int i=0;i<data.writeSet.index;i++){
//				LockTable.lock(data.writeSet.refs[i] & LockTable.MASK, data.locksMarker);
//				++lockedCounter;
//			}
//	        for (int i = 0; i < data.readSetNextAvaliable; i++) {
//	        	LockTable.checkLock( data.readSet[i]& LockTable.MASK, data.localClock);
//	        	data.readSet[i]=0;
//	        }
//		}
//		catch( stm.STMException exception){
//			for( int i=0;i<data.writeSet.index;i++){
//				if( lockedCounter-- == 0)
//					break;
//				LockTable.unLock(data.writeSet.refs[i] & LockTable.MASK,data.locksMarker);
//			}
//			totalReties++;
//			return 1;
//		}
//
//		final int newClock = Context.clock.incrementAndGet();
//
//		for( int i=0;i<data.writeSet.index;i++){
//			Address dstPtr = Address.fromIntSignExtend(data.writeSet.refs[i]);
//			// commit value to field
//			switch(data.writeSet.sizes[i]){
//			case 1:
//				dstPtr.store((byte)data.writeSet.values[i]);
//				break;
//			case 2:
//				dstPtr.store((short)data.writeSet.values[i]);
//				break;
//			case 4:
//				dstPtr.store((int)data.writeSet.values[i]);
//				break;
//			case 8:
//				dstPtr.store(data.writeSet.values[i]);
//				break;
//			}
//			LockTable.setAndReleaseLock( data.writeSet.refs[i] & LockTable.MASK, newClock, data.locksMarker);
//		}
//		data.isNotActive = true;
//		committedTrans++;
//		return 0;
//	}
//
//	public static void printStatistics() {
//		VM.sysWriteln("STM: committed: " + committedTrans + ", Reties: " + totalReties + ", Early: " + totalEarlyReties/*+ ", In loop: " + inTheLoop + ", In begin: "+ inBeginingOfCommit*/);
//	}
}