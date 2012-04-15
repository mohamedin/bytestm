package org.deuce.benchmark.intset;

//import java.util.Hashtable;
import org.deuce.benchmark.java.util.HashMap;

public class HashTableSet implements IntSet{
	
	// Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
//	private final Hashtable<Integer,Object > map = new Hashtable<Integer,Object>();
    private final HashMap<Integer,Object > map = new HashMap<Integer,Object>();
	
	public boolean add(int value) {
//		System.out.println("add");
		return map.put(value, PRESENT)==null;
	}

	public boolean contains(int value) {
//		System.out.println("find");
		return map.containsKey(value);
	}

	public boolean remove(int value) {
//		System.out.println("delete");
		return map.remove(value) != null;
	}
	public void validate(){}

}
