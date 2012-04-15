package org.deuce.transaction.tl2;

import java.util.HashMap;
import java.util.Iterator;

import org.deuce.transaction.Statistics;
import org.deuce.transaction.tl2.field.ReadFieldAccess;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * Represents the transaction write set.
 *  
 * @author Guy Korland
 * @since 0.7
 */
@Exclude
public class WriteSet implements Iterable<WriteFieldAccess>{
	
	final private HashMap<WriteFieldAccess,WriteFieldAccess> writeSet = new HashMap<WriteFieldAccess,WriteFieldAccess>( 50);
//	final private WriteFieldAccess[] writeSet= new WriteFieldAccess[1024];
//	private int index = 0;
	
	final private BloomFilter bloomFilter = new BloomFilter();
	
	public void clear() {
		bloomFilter.clear();
//		index = 0;
		writeSet.clear();
	}

	public boolean isEmpty() {
//		return index == 0;
		return writeSet.isEmpty();
	}

	public Iterator<WriteFieldAccess> iterator() {
		// Use the value and not the key since the key might hold old key.
		// Might happen if the same field was update more than once.
		return writeSet.values().iterator();
	}

	public void put(WriteFieldAccess write) {
		// Add to bloom filter
		bloomFilter.add( write.hashCode());

		// Add to write set
		/*if (*/writeSet.put( write, write);//==null) Statistics.writeSetSizeMax++;
	}
	
	public WriteFieldAccess contains(ReadFieldAccess read) {
		// Check if it is already included in the write set
		if (bloomFilter.contains(read.hashCode())){
//			Statistics.selfReadsCountMax++;
			return writeSet.get( read);
		}
		else
			return  null;
	}
	
	public int size() {
		return writeSet.size();
	}
	
}
