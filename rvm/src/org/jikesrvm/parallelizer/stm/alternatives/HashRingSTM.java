package org.jikesrvm.parallelizer.stm.alternatives;

import java.util.concurrent.atomic.AtomicInteger;

import org.jikesrvm.VM;
import org.jikesrvm.parallelizer.stm.BloomFilter;
import org.jikesrvm.parallelizer.stm.Statistics;
import org.jikesrvm.parallelizer.stm.WriteSetHashOnly;
import org.jikesrvm.runtime.Magic;
import org.vmmagic.unboxed.Address;

import stm.STMException;

/**
 * STM implementation using RingSTM algorithm and uses Open addressing hashing for the write-set
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
public class HashRingSTM {
	public static boolean enableSTM = false;
	private static stm.STMException STM_EXCEPTION;
	private static int collisions =0;
	private static int maxWriteset =0;
	private static class ThreadMetaData{
		long time;
		private static final int MAX =4096;
		WriteSetHashOnly writeSet;
		boolean isNotActive = true;
		Object[] objects = null;
		int objectsLenght = 0;
		boolean isReadOnly = true;
		BloomFilter readSig = null;
		BloomFilter writeSig = null;
		int startTime;
		public boolean isEarlyAborted = false;
		public ThreadMetaData(){
			writeSet = new WriteSetHashOnly();
			objects = new Object[MAX];
			
			writeSig = new BloomFilter();
			readSig = new BloomFilter();
		}
		public void clear(){
			objectsLenght = 0;
			isReadOnly = true;
			writeSig.clear();
			readSig.clear();
			writeSet.clear();
		}
	}
	
	private static ThreadLocal<ThreadMetaData> threadMetaData = null;
	private static int lastComplete = 0;
	private static int lastInit = 0;
	private static AtomicInteger timestamp;
	private static final int RING_ELEMENTS = 1024;
	private static BloomFilter[] ringWriteFilters;

	private static long totalReties = 0;
	private static long totalEarlyReties = 0;
	private static long committedTrans = 0;
	
	private static int JTOC = 0;
	
	public static void boot(){
		VM.sysWriteln("STM booted");
		threadMetaData = new ThreadLocal<ThreadMetaData>();
		ringWriteFilters = new BloomFilter[RING_ELEMENTS];
		for (int i=0; i<ringWriteFilters.length;i++)
			ringWriteFilters[i] = new BloomFilter();
		timestamp = new AtomicInteger(0);
		JTOC = Magic.getJTOC().toInt();
		STM_EXCEPTION = new stm.STMException();
	}
	
	public static int memLoadIntArr(int ref, int index, int val) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		ref += (index<<2);

		//read signature update
		data.readSig.add(ref);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return (int) data.writeSet.lastVal;
		}
		return val;
	}

	public static long memLoadLongArr(int ref, int index, long val) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		ref += (index<<3);

		//read signature update
		data.readSig.add(ref);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return data.writeSet.lastVal;
		}
		return val;
	}

	public static byte memLoadByteArr(int ref, int index, byte val) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		ref += index;

		//read signature update
		data.readSig.add(ref);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return (byte) data.writeSet.lastVal;
		}
		return val;
	}

	public static short memLoadShortArr(int ref, int index, short val) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		ref += (index<<1);

		//read signature update
		data.readSig.add(ref);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return (short) data.writeSet.lastVal;

		}
		return val;
	}
	
	
	public static void memStoreIntArr(int ref, int index, int val){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=(index<<2);
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}

		data.isReadOnly = false;
		
		ref +=(index<<2);
		
		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 4);
	}
	
	public static void memStoreRefArr(int ref, int index, int val){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=(index<<2);
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref +=(index<<2);
		
		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 4);
		if (val !=0){
			Object obj = Magic.addressAsObject(Address.fromIntSignExtend(val));
			for (int i=0;i<data.objectsLenght;i++){
				if (data.objects[i].equals(obj))
					return;
			}
			data.objects[data.objectsLenght] = obj; 
			data.objectsLenght++;
		}
	}
	
	public static void memStoreLongArr(int ref, int index, long val){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=(index<<3);
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref +=(index<<3);
		
		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 8);
	}

	public static void memStoreByteArr(int ref, int index, byte val){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref += index;
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref += index;
		
		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 1);
	}

	public static void memStoreShortArr(int ref, int index, short val){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=(index<<1);
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref +=(index<<1);
		
		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 2);
	}

	public static int memLoadIntStatic(int val, int offset) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		offset +=JTOC;

		//read signature update
		data.readSig.add(offset);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			if (data.writeSet.contains(offset))
				return (int) data.writeSet.lastVal;
		}
		return val;
	}

	public static long memLoadLongStatic(long val, int offset) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;
		
		offset +=JTOC;
		
		data.readSig.add(offset);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			//check if it is in write set
			if (data.writeSet.contains(offset))
				return data.writeSet.lastVal;
		}
		return val;
	}
	
	public static void memStoreIntStatic(int val, int offset, byte size, boolean isObject){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			offset +=JTOC;
			Address dstPtr = Address.fromIntSignExtend(offset);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		offset +=JTOC;
		
		//write signature update
		data.writeSig.add(offset);
		
		//add it to write set
		data.writeSet.add(offset, val, size);
		if (isObject && val !=0){
			Object obj = Magic.addressAsObject(Address.fromIntSignExtend(val));
			for (int i=0;i<data.objectsLenght;i++){
				if (data.objects[i].equals(obj))
					return;
			}
			data.objects[data.objectsLenght] = obj; 
			data.objectsLenght++;
		}
	}
	
	public static void memStoreLongStatic(long val, int offset){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			offset +=JTOC;
			Address dstPtr = Address.fromIntSignExtend(offset);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		offset +=JTOC;

		//write signature update
		data.writeSig.add(offset);
		
		//add it to write set
		data.writeSet.add(offset, val, (byte) 8);
	}

	
	public static int memLoadInt(int ref, int val, int offset) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;

		ref +=offset;

		//read signature update
		data.readSig.add(ref);
		
		if (checkInflight(data)) throw STM_EXCEPTION;
		
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return (int) data.writeSet.lastVal;
		}
		return val;
	}
	

	public static long memLoadLong(int ref, long val, int offset) throws STMException{
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive)
			return val;
		
		ref +=offset;
		
		//read signature update
		data.readSig.add(ref);

		if (checkInflight(data)) throw STM_EXCEPTION;
		
		//check if it is in write set
		if (!data.isReadOnly){
			if (data.writeSet.contains(ref))
				return data.writeSet.lastVal;
		}
		return val;
	}
	
	public static void memStoreInt(int ref, int val, int offset, byte size, boolean isObject){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=offset;
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref +=offset;

		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, size);
		
		if (isObject && val !=0){
			Object obj = Magic.addressAsObject(Address.fromIntSignExtend(val));
			for (int i=0;i<data.objectsLenght;i++){
				if (data.objects[i].equals(obj))
					return;
			}
			data.objects[data.objectsLenght] = obj; 
			data.objectsLenght++;
		}
	}
	
	public static void memStoreLong(int ref, long val, int offset){
		ThreadMetaData data = threadMetaData.get();
		if (data == null || data.isNotActive){
			ref +=offset;
			Address dstPtr = Address.fromIntSignExtend(ref);
			dstPtr.store(val);
			return;
		}
		data.isReadOnly = false;
		ref +=offset;

		//write signature update
		data.writeSig.add(ref);
		
		//add it to write set
		data.writeSet.add(ref, val, (byte) 8);
	}
	
	public static void xBegin(){
		ThreadMetaData data = threadMetaData.get();
		if (data==null){
			data = new ThreadMetaData();
			threadMetaData.set(data);
		}
		else{
			data.clear();
		}
		 
		data.startTime = lastComplete;
		data.isNotActive = false;
	}
	
	public static boolean checkInflight(ThreadMetaData data)
	{
		int commitTime =lastInit; 
	    // intersect against all new entries
        for (int i = commitTime; i >= data.startTime + 1; i--)
            if (ringWriteFilters[i % RING_ELEMENTS].intersect(data.readSig)){
            	data.isEarlyAborted = true;
                return true;
            }
        // wait for newest entry to be writeback-complete before continuing
        while (lastComplete < commitTime)
      	  ;//basy wait :( find solution to make it fast in java


        // detect ring rollover: start.ts must not have changed
        if (timestamp.get() > (data.startTime + RING_ELEMENTS)){
        	data.isEarlyAborted = true;
            return true;
        }
        data.startTime = commitTime;
        return false;
	}
	
	public static int xCommit(){
		ThreadMetaData data = threadMetaData.get();
		
		if (data.isEarlyAborted){
			data.isEarlyAborted = false;
//			totalEarlyReties++;
//			Statistics.retries++;
			return 1;
		}
		//read only transactions do nothing
		if (data.isReadOnly){
		    data.isNotActive = true;
//			committedTrans++;
//			Statistics.commits++;
//			Statistics.readOnlyCommittedTx++;
//			long diff = System.currentTimeMillis() - data.time;
//			Statistics.txTimeAvg += diff;
//			if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
			return 0;
		}
		
	      int commitTime;
	      do {
	          commitTime = timestamp.get();
	          // get the latest ring entry, return if we've seen it already
	          if (commitTime != data.startTime) {
	              // wait for the latest entry to be initialized
	              while (lastInit < commitTime)
					;//basy wait :( find solution to make it fast in java

	              // intersect against all new entries
	              for (int i = commitTime; i >= data.startTime + 1; i--)
	                  if (ringWriteFilters[i % RING_ELEMENTS].intersect(data.readSig)){
//	                	  Statistics.retries++;
//	                	  totalReties++;
	                      return 1;
	                  }

	              // wait for newest entry to be wb-complete before continuing
	              while (lastComplete < commitTime)
	            	  ;//basy wait :( find solution to make it fast in java

	              // detect ring rollover: start.ts must not have changed
	              if (timestamp.get() > (data.startTime + RING_ELEMENTS)){
//	            	  totalReties++;
//	            	  Statistics.retries++;
                      return 1;
                  }
	              // ensure this tx doesn't look at this entry again
	              data.startTime = commitTime;
	          }
	      } while (!timestamp.compareAndSet(commitTime, commitTime +1));
	      ringWriteFilters[(commitTime + 1) % RING_ELEMENTS].copy(data.writeSig);

	      // setting this says "the bits are valid"
	      lastInit = commitTime + 1;

	      // we're committed... run redo log, then mark ring entry COMPLETE
			//Write back
	      data.writeSet.writeBack();
	      lastComplete = commitTime + 1;

	      // clean up
	    data.isNotActive = true;
//		committedTrans++;
//		Statistics.commits++;
//		long diff = System.currentTimeMillis() - data.time;
//		Statistics.txTimeAvg += diff;
//		if (Statistics.txTimeMax < diff) Statistics.txTimeMax=diff;
		//if (collisions < data.writeSet.collision) collisions = data.writeSet.collision;
		//if (maxWriteset < data.writeSet.elementsSize) maxWriteset = data.writeSet.elementsSize;
		return 0;
	}

	public static void printStatistics() {
		VM.sysWriteln("STM: committed: " + committedTrans + ", Reties: " + totalReties + ", Early: " + totalEarlyReties + " Collisions: " + collisions + ", Max WS size: " + maxWriteset/*+ ", In loop: " + inTheLoop + ", In begin: "+ inBeginingOfCommit*/);
		VM.sysWriteln(Statistics.asString());
	}
}
