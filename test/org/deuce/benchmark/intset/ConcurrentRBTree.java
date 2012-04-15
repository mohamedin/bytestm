package org.deuce.benchmark.intset;

import org.amino.ds.tree.RelaxedRBTree;
import org.amino.mcas.LockFreeBSTree;
import org.amino.ds.tree.ParallelRBTree;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class ConcurrentRBTree implements IntSet {
	RelaxedRBTree<Integer> tree = new RelaxedRBTree<Integer>();
	public ConcurrentRBTree() {
	}

	public boolean add(int value) {
		tree.insert(value);
		return true;
	}

	public boolean remove(int value) {
		return tree.remove(value)!=null;
	}

	public boolean contains(int value) {
		return tree.find(value);
	}

	public void validate(){
	}
	
}
