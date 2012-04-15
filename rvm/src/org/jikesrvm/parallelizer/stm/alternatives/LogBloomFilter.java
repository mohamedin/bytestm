package org.jikesrvm.parallelizer.stm.alternatives;

import java.util.Arrays;

import org.jikesrvm.parallelizer.stm.Statistics;
import org.vmmagic.pragma.Inline;

/**
 * Bloom filter with added logging (slow). Used with RingSTM
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */
public class LogBloomFilter {
	private static final int BITS = 1024;
	
	private static final int FILTER_SIZE = BITS/Integer.SIZE;
	
	private final static int[] EMPTY_FILTER = new int[FILTER_SIZE];
	
	int[] filter = new int[FILTER_SIZE];
	@Inline
	public void add(int val){
		int index = (val >>> 3) % BITS;
		int block = index / Integer.SIZE;
		int offset = index % Integer.SIZE;
		filter[block] |= (1 << offset);
		Statistics.readSetSizeMax++;
	}
	@Inline
	public boolean intersect(LogBloomFilter rhs){
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
	@Inline
    public boolean lookup(int val)
    {
        int index  = (val >>> 3) % BITS;
        int block  = index / Integer.SIZE;
        int offset = index % Integer.SIZE;

        return (filter[block] & (1 << offset)) != 0;
    }
	
	public void clear() {
		System.arraycopy(EMPTY_FILTER, 0, filter, 0, FILTER_SIZE);
	}
	
	public void copy(LogBloomFilter srcFilter) {
		System.arraycopy(srcFilter.filter, 0, filter, 0, FILTER_SIZE);
	}
}
