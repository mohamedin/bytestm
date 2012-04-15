package org.deuce.benchmark.intset;

import org.deuce.Atomic;

public class IntSetHash implements IntSet{

	private int[] set = new int[13];
	private byte[] states = new byte[13];
	private int free = states.length;
	private int size = 0;
	private int maxSize = states.length/2;
	
	 /** flag indicating that a slot in the hashtable is available */
    private static final byte FREE = 0;

    /** flag indicating that a slot in the hashtable is occupied */
    private static final byte FULL = 1;

    /** flag indicating that the value of a slot in the hashtable 
     * was deleted */
    private static final byte REMOVED = 2;
	
    @Atomic
    public boolean add(int value) {
    	try{stm.STM.xBegin();
    	int index = findIndex(value);
//    	System.out.println(index);
    	if (index < 0) {
    		stm.STM.xCommit();
    		return false;       // already present in set, nothing to add
    	}

    	byte previousState = states[index];
    	set[index] = value;
    	states[index] = FULL;
    	postInsertHook(previousState == FREE);
    	}catch(stm.STMException e){}finally{stm.STM.xCommit();}
    	return true;         
    }
    
    @Atomic
    public boolean contains(int value) {
    	boolean ret = false;
    	try{stm.STM.xBegin();
    	ret = index(value) >= 0;
    	}catch(stm.STMException e){}finally{stm.STM.xCommit();}
    	return ret;
	}

    @Atomic
    public boolean remove(int value) {
    	try{stm.STM.xBegin();
    	int index = index(value);
    	if (index >= 0) {
    		set[index] = (int)0;
    		states[index] = REMOVED;
    		size--;
    		stm.STM.xCommit();
    		return true;
    	}
    	}catch(stm.STMException e){}finally{stm.STM.xCommit();}
    	return false;
    }
	
	private int index(int value) {

		int length = states.length;
		int hash = (value * 31) & 0x7fffffff;
		int index = hash % length;
		int probe;

		if (states[index] != FREE &&
				(states[index] == REMOVED || set[index] != value)) {
			// see Knuth, p. 529
			probe = 1 + (hash % (length - 2));

			do {
				index -= probe;
				if (index < 0) {
					index += length;
				}
			} while (states[index] != FREE &&
					(states[index] == REMOVED || set[index] != value));
		}

		return states[index] == FREE ? -1 : index;
	}
    
    int findIndex(int value){
    	int length = states.length;
    	int hash = ((value * 31) & 0x7fffffff);
    	int index = hash % length;
        int probe;
    	if (states[index] == FREE) {
    		return index;       // empty, all done
    	} else if (states[index] == FULL && set[index] == value) {
    		return -index -1;   // already stored
    	} else {                // already FULL or REMOVED, must probe
    		probe = 1 + (hash % (length - 2));

    		if (states[index] != REMOVED) {
    			do {
    				index -= probe;
    				if (index < 0) {
    					index += length;
    				}
    			} while (states[index] == FULL && set[index] != value);
    		}
    		if (states[index] == REMOVED) {
    			int firstRemoved = index;
    			while (states[index] != FREE &&
    					(states[index] == REMOVED || set[index] != value)) {
    				index -= probe;
    				if (index < 0) {
    					index += length;
    				}
    			}
    			return states[index] == FULL ? -index -1 : firstRemoved;
    		}
    		return states[index] == FULL ? -index -1 : index;
    	}
    }
    
    /**
     * After an insert, this hook is called to adjust the size/free
     * values of the set and to perform rehashing if necessary.
     */
    protected final void postInsertHook(boolean usedFreeSlot) {
        if (usedFreeSlot) {
        	free--;
        }
//        System.out.println("f:"+free);
//        System.out.println("mx:"+maxSize);
        // rehash whenever we exhaust the available space in the table
        if (++size > maxSize || free == 0) {
            // choose a new capacity suited to the new state of the table
            // if we've grown beyond our maximum size, double capacity;
            // if we've exhausted the free spots, rehash to the same capacity,
            // which will free up any stale removed slots for reuse.
//        	System.out.println("s l: "+states.length);
            int newCapacity = size > maxSize ? PrimeFinder.nextPrime(states.length << 1) : states.length;
//            System.out.println("n c:" + newCapacity);
            rehash(newCapacity);
            maxSize = states.length/2;
        }
    }
    
    protected void rehash(int newCapacity) {
        int oldCapacity = set.length;
        int oldSet[] = set;
        byte oldStates[] = states;

        set = new int[newCapacity];
        states = new byte[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(oldStates[i] == FULL) {
                int o = oldSet[i];
                int index = findIndex(o);
                set[index] = o;
                states[index] = FULL;
            }
        }
    }
    
    
    public void validate(){}


}
