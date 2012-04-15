package org.jikesrvm.parallelizer.stm;

import java.util.Arrays;

import org.jikesrvm.VM;
import org.jikesrvm.parallelizer.stm.memory.ImmortalsMemoryManager;
import org.jikesrvm.parallelizer.stm.tl2.LockTable;
import org.vmmagic.unboxed.Address;

/**
 * Write-set using Open addressing hashing
 * Immutable memory is used also
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */
public class WriteSetHashOnly {
	 public int[] refs;
	 byte[] sizes;
	 long[] vals;
	 public int[] indexes;
	 public int indexSize = 0;
	 int tableSize = 1024;
	 int modMask = 1023;
	 int elementsSize =0;
	 int threshold = tableSize*2/3;
	public long lastVal;
	public WriteSetHashOnly(){
		refs = ImmortalsMemoryManager.newIntArray(tableSize);//new int[tableSize];
		sizes = ImmortalsMemoryManager.newByteArray(tableSize);//new byte[tableSize];
		vals = ImmortalsMemoryManager.newLongArray(tableSize);//new long[tableSize];
		indexes = ImmortalsMemoryManager.newIntArray(4096);//new int[4096];
	}
	
	public void add(int ref, long val, byte size){
//		Statistics.writesCountMax++;
		if (elementsSize > threshold)
			extendTable();
		int hash = ref & modMask;
		while (sizes[hash]!=0){
			if (refs[hash] == ref){
				vals[hash] = val;
				return;
			}
			hash = (hash+1) & modMask;
		}
//		Statistics.writeSetSizeMax++;
		sizes[hash] = size;
		refs[hash] = ref;
		vals[hash] = val;
		elementsSize++;
		indexes[indexSize++] = hash;
	}

	public boolean contains(int ref){
		int hash = ref & modMask;
		while (sizes[hash]!=0){
			if (refs[hash] == ref){
				lastVal = vals[hash];
//				Statistics.selfReadsCountMax++;
				return true;
			}
			hash = (hash+1) & modMask;
		}
		return false;
	}
	
	
	public boolean isEmpty() {
		return elementsSize ==0;
	}
	
	 void extendTable() {
		VM.sysWriteln("Extending hash");
		int[] tempRefs = refs;
		byte[] tempSizes = sizes;
		long[] tempVals = vals;
		tableSize = tableSize << 1;
		modMask = tableSize -1;
		threshold = tableSize*2/3;
		refs = new int[tableSize];
		sizes = new byte[tableSize];
		vals = new long[tableSize];
		for (int i=0; i<tempRefs.length; i++){
			if (tempSizes[i] != 0){
				int hash = tempRefs[i] & modMask;
				while (sizes[hash]!=0){
					hash = (hash+1) & modMask;
				}
				sizes[hash] = tempSizes[i];
				refs[hash] = tempRefs[i];
				vals[hash] = tempVals[i];
			}
		}
	}
	public void clear(){
		elementsSize = 0;
		Arrays.fill(sizes, (byte) 0);
		indexSize = 0;
	}
	
	public void writeBackTL2(int newClock, byte[] locksMarker){
		for (int i=0; i<indexSize; i++){
			int index = indexes[i];
			Address dstPtr = Address.fromIntSignExtend(refs[index]);
			switch(sizes[index]){
			case 1:
				dstPtr.store((byte)vals[index]);
				break;
			case 2:
				dstPtr.store((short)vals[index]);
				break;
			case 4:
				dstPtr.store((int)vals[index]);
				break;
			case 8:
				dstPtr.store(vals[index]);
				break;
			}
			LockTable.setAndReleaseLock( refs[index] & LockTable.MASK, newClock, locksMarker);
		}
	}

	public void writeBack(){
		for (int i=0; i<indexSize; i++){
			int index = indexes[i];
			Address dstPtr = Address.fromIntSignExtend(refs[index]);
			switch(sizes[index]){
			case 1:
				dstPtr.store((byte)vals[index]);
				break;
			case 2:
				dstPtr.store((short)vals[index]);
				break;
			case 4:
				dstPtr.store((int)vals[index]);
				break;
			case 8:
				dstPtr.store(vals[index]);
				break;
			}
		}
	}
}
