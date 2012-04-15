package org.jikesrvm.parallelizer.stm.alternatives;

import java.util.concurrent.atomic.AtomicInteger;

import org.jikesrvm.VM;
import org.jikesrvm.parallelizer.stm.BloomFilter;
import org.jikesrvm.runtime.Magic;
import org.vmmagic.unboxed.Address;

import stm.STMException;

/**
 * STM implementation using RingSTM algorithm and uses arrays for the write-set
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
public class RingSTM {
	public static boolean enableSTM = false;
	private static stm.STMException STM_EXCEPTION;
	private static class ThreadMetaData{
		private static final int MAX =4096;
		long[] writeSetVals = null;
		int[] writeSetRefs = null;
		byte[] writeSetSizes = null;
		int writeSetLenght = 0;
		boolean isNotActive = true;
		Object[] objects = null;
		int objectsLenght = 0;
		boolean isReadOnly = true;
		BloomFilter readSig = null;
		BloomFilter writeSig = null;
		int startTime;
		public boolean isEarlyAborted = false;
		public ThreadMetaData(){
			writeSetVals = new long[MAX];
			writeSetRefs = new int[MAX];
			writeSetSizes = new byte[MAX];
			
			objects = new Object[MAX];
			
			writeSig = new BloomFilter();
			readSig = new BloomFilter();
		}
		public void clear(){
			writeSetLenght = 0;
			objectsLenght = 0;
			isReadOnly = true;
			writeSig.clear();
			readSig.clear();
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){
					return (int) data.writeSetVals[i];
				}
			}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){
					return data.writeSetVals[i];
				}
			}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){
					return (byte) data.writeSetVals[i];
				}
			}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){
					return (short) data.writeSetVals[i];
				}
			}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 4;
			data.writeSetLenght++;
		}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 4;
			data.writeSetLenght++;
		}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 8;
			data.writeSetLenght++;
		}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 1;
			data.writeSetLenght++;
		}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 2;
			data.writeSetLenght++;
		}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == offset){
					return (int) data.writeSetVals[i];
				}
			}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == offset){
					return data.writeSetVals[i];
				}
			}
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
		
		boolean writtenBefore = data.writeSig.lookup(offset);
		
		//write signature update
		data.writeSig.add(offset);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==offset){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = offset;
			data.writeSetSizes[data.writeSetLenght] = size;
			data.writeSetLenght++;
		}
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

		boolean writtenBefore = data.writeSig.lookup(offset);

		//write signature update
		data.writeSig.add(offset);
		
		boolean isFalsePositive = true;
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==offset){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = offset;
			data.writeSetSizes[data.writeSetLenght] = 8;
			data.writeSetLenght++;
		}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){

					return (int) data.writeSetVals[i];
				}
			}
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
			for (int i =0; i < data.writeSetLenght; i++){
				if (data.writeSetRefs[i] == ref){
					return data.writeSetVals[i];
				}
			}
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
		
		boolean writtenBefore = data.writeSig.lookup(ref);
		
		//write signature update
		data.writeSig.add(ref);
		boolean isFalsePositive = true;
		
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = size;
			data.writeSetLenght++;
		}
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

		boolean writtenBefore = data.writeSig.lookup(ref);

		//write signature update
		data.writeSig.add(ref);
		
		boolean isFalsePositive = true;
		//add it to write set
		if (writtenBefore){//replace
			for (int i=0; i<data.writeSetLenght;i++){
				if (data.writeSetRefs[i]==ref){
					data.writeSetVals[i] = val;
					isFalsePositive = false;
					break;
				}
			}
		}
		if (isFalsePositive){
			data.writeSetVals[data.writeSetLenght] = val;
			data.writeSetRefs[data.writeSetLenght] = ref;
			data.writeSetSizes[data.writeSetLenght] = 8;
			data.writeSetLenght++;
		}

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
			totalEarlyReties++;
			return 1;
		}
		//read only transactions do nothing
		if (data.isReadOnly){
		    data.isNotActive = true;
			committedTrans++;
			return 0;
		}
		
	      int commitTime;
	      do {
	          commitTime = timestamp.get();
	          // get the latest ring entry, return if we've seen it already
	          if (commitTime != data.startTime) {
	              // wait for the latest entry to be initialized
	              while (lastInit < commitTime)
					;//try {Thread.sleep(0,1);} catch (InterruptedException e) {	e.printStackTrace();}
					//basy wait :( find solution to make it fast in java

	              // intersect against all new entries
	              for (int i = commitTime; i >= data.startTime + 1; i--)
	                  if (ringWriteFilters[i % RING_ELEMENTS].intersect(data.readSig)){
	                	  totalReties++;
	                      return 1;
	                  }

	              // wait for newest entry to be wb-complete before continuing
	              while (lastComplete < commitTime)
	            	  ;//try {Thread.sleep(0,1);} catch (InterruptedException e) {	e.printStackTrace();}
	            	  //basy wait :( find solution to make it fast in java

	              // detect ring rollover: start.ts must not have changed
	              if (timestamp.get() > (data.startTime + RING_ELEMENTS)){
	            	  totalReties++;
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
		  for (int i=0; i<data.writeSetLenght; i++){
				Address dstPtr = Address.fromIntSignExtend(data.writeSetRefs[i]);
				switch(data.writeSetSizes[i]){
				case 1:
					dstPtr.store((byte)data.writeSetVals[i]);
					break;
				case 2:
					dstPtr.store((short)data.writeSetVals[i]);
					break;
				case 4:
					dstPtr.store((int)data.writeSetVals[i]);
					break;
				case 8:
					dstPtr.store(data.writeSetVals[i]);
					break;
				}
		  }
		  
	      lastComplete = commitTime + 1;

	      // clean up
	    data.isNotActive = true;
		committedTrans++;
		return 0;
	}

	public static void printStatistics() {
		VM.sysWriteln("STM: committed: " + committedTrans + ", Reties: " + totalReties + ", Early: " + totalEarlyReties/*+ ", In loop: " + inTheLoop + ", In begin: "+ inBeginingOfCommit*/);
	}
}
