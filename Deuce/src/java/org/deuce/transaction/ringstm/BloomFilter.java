package org.deuce.transaction.ringstm;

import java.util.Arrays;


public class BloomFilter {
	private static final int BITS = 1024;
	
	private static final int FILTER_SIZE = BITS/Integer.SIZE;
	
	private final static int[] EMPTY_FILTER = new int[FILTER_SIZE];
	
	int[] filter = new int[FILTER_SIZE];
	
//	public BloomFilter(){
//		filter = ImmortalsMemoryManager.newIntArray(FILTER_SIZE);
//	}
	
	public void add(int val){
//		int index = (val >>> 3) % BITS;
		int index = (val >>> 1) % BITS; //we have no alignment in Jikes (just all addresses are even)
		int block = index / Integer.SIZE;
		int offset = index % Integer.SIZE;
		filter[block] |= (1 << offset);
//		Statistics.readSetSizeMax++;
	}
	public boolean intersect(BloomFilter rhs){
		for (int i=0; i<filter.length; i++){
			if ((filter[i] & rhs.filter[i]) != 0)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(filter);
	}
    public boolean lookup(int val)
    {
//        int index  = (val >>> 3) % BITS;
		int index  = (val >>> 1) % BITS;
        int block  = index / Integer.SIZE;
        int offset = index % Integer.SIZE;

        return (filter[block] & (1 << offset)) != 0;
    }
	
	public void clear() {
		System.arraycopy(EMPTY_FILTER, 0, filter, 0, FILTER_SIZE);
	}
	
	public void copy(BloomFilter srcFilter) {
		System.arraycopy(srcFilter.filter, 0, filter, 0, FILTER_SIZE);
	}
}
