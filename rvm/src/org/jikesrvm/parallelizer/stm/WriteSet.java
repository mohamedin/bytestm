package org.jikesrvm.parallelizer.stm;

import java.util.Arrays;

import org.jikesrvm.VM;
import org.vmmagic.unboxed.Address;

/**
 * Write-set using arrays for RingSTM
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */

public class WriteSet {
	 int[] refs;
	 byte[] sizes;
	 long[] vals;
	 int[] indexes;
	 int indexSize = 0;
	 int tableSize = 1024;
	 int modMask = 1023;
	 int elementsSize =0;
	 int threshold = tableSize*2/3;
	public long lastVal;
	int collision = 0;
	private int[] trefs;
	private byte[] tsizes;
	private long[] tvals;
	private static final int TINY_WS_SIZE = 10;
	int tinySize = 0;
	public WriteSet(){
		refs = new int[tableSize];
		sizes = new byte[tableSize];
		vals = new long[tableSize];
		trefs = new int[TINY_WS_SIZE];
		tsizes = new byte[TINY_WS_SIZE];
		tvals = new long[TINY_WS_SIZE];
		indexes = new int[4096];
	}
	
	public void add(int ref, long val, byte size){
		for (int i=0; i<tinySize;i++){
			if (trefs[i]==ref){
				tvals[i] = val;
				return;
			}
		}
		if (tinySize<TINY_WS_SIZE){
			tvals[tinySize] = val;
			trefs[tinySize] = ref;
			tsizes[tinySize] = 4;
			tinySize++;
		}
		else{
			if (elementsSize > threshold)
				extendTable();
			int hash = ref & modMask;
			while (sizes[hash]!=0){
				if (refs[hash] == ref){
					vals[hash] = val;
					return;
				}
				//collision++;
				hash = (hash+1) & modMask;
			}
			sizes[hash] = size;
			refs[hash] = ref;
			vals[hash] = val;
			elementsSize++;
			indexes[indexSize++] = hash;
		}
	}

	public boolean contains(int ref){
		for (int i=0; i<tinySize;i++){
			if (trefs[i]==ref){
				lastVal = tvals[i];
				return true;
			}
		}
		int hash = ref & modMask;
		while (sizes[hash]!=0){
			if (refs[hash] == ref){
				lastVal = vals[hash];
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
		collision = 0;
		tinySize = 0;
	}
	public void writeBack(){
		for (int i=0; i<tinySize; i++){
			Address dstPtr = Address.fromIntSignExtend(trefs[i]);
			switch(tsizes[i]){
			case 1:
				dstPtr.store((byte)tvals[i]);
				break;
			case 2:
				dstPtr.store((short)tvals[i]);
				break;
			case 4:
				dstPtr.store((int)tvals[i]);
				break;
			case 8:
				dstPtr.store(tvals[i]);
				break;
			}
		}
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
