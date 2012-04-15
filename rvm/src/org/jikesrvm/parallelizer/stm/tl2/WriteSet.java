package org.jikesrvm.parallelizer.stm.tl2;


/**
 * 
 * Write-set implementation using arrays 
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */

public class WriteSet{
	
	public int index =0;
	public final long[] values = new long[Context.DEFAULT_CAPACITY];
	public final int[] refs = new int[Context.DEFAULT_CAPACITY];
	public final byte[] sizes = new byte[Context.DEFAULT_CAPACITY];
	final private BloomFilter bloomFilter = new BloomFilter();
	public static class LongVal{
		public long val;
	}
	private LongVal ret = new LongVal();
	
	public void clear() {
		bloomFilter.clear();
		index =0;
	}

	public boolean isEmpty() {
		return index ==0;
	}

	public void put(long val, int ref, byte size) {
		if (bloomFilter.contains(ref & LockTable.MASK)){
			for (int i =0;i<index;i++){
				if (refs[i]==ref){
					values[i]=val;
					return;
				}
			}
		}
		else
			// Add to bloom filter
			bloomFilter.add( ref & LockTable.MASK);

		// Add to write set
		refs[index] = ref;
		values[index] = val;
		sizes[index] = size;
		index++;
	}
	
	public WriteSet.LongVal contains(int readRef) {
		// Check if it is already included in the write set
		if (bloomFilter.contains(readRef & LockTable.MASK)){
			for (int i =0;i<index;i++){
				if (refs[i]==readRef){
					ret.val = values[i];
					return ret;
				}
			}
		}
		return null;
	}

	
	public int size() {
		return index;
	}
	
}
