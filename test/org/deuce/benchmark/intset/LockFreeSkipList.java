package org.deuce.benchmark.intset;

import java.util.concurrent.atomic.AtomicMarkableReference;

public final class LockFreeSkipList {
	static final int MAX_LEVEL = 32;
	final Node head = new Node(Integer.MIN_VALUE);
	final Node tail = new Node(Integer.MAX_VALUE);

	public LockFreeSkipList() {
		for (int i = 0; i < head.next.length; i++) {
			head.next[i] = new AtomicMarkableReference<LockFreeSkipList.Node>(
					tail, false);
		}
	}

	public static final class Node {
		final int key;
		final AtomicMarkableReference<Node>[] next;
		private int topLevel;

		// constructor for sentinel nodes
		public Node(int key) {
			this.key = key;
			next = (AtomicMarkableReference<Node>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
			for (int i = 0; i < next.length; i++) {
				next[i] = new AtomicMarkableReference<Node>(null, false);
			}
			topLevel = MAX_LEVEL;
		}

		// constructor for ordinary nodes
		public Node(int x, int height) {
			key = x;
			next = (AtomicMarkableReference<Node>[]) new AtomicMarkableReference[height + 1];
			for (int i = 0; i < next.length; i++) {
				next[i] = new AtomicMarkableReference<Node>(null, false);
			}
			topLevel = height;
		}
	}
	int randomSeed = (int) java.lang.System.currentTimeMillis() | 256;
	  private int randomLevel() {
		    int x = randomSeed;
		    x ^= x << 13;
		    x ^= x >>> 17;
		    randomSeed = x ^= x << 5;
		    if ((x & 0x8001) != 0) // test highest and lowest bits
		      return 0;
		    int level = 1;
		    while (((x >>>= 1) & 1) != 0) ++level;
		    return Math.min(level, MAX_LEVEL-2);
		  }

	boolean add(int x) {
		int topLevel = randomLevel();
		int bottomLevel = 0;
		Node[] preds = (Node[]) new Node[MAX_LEVEL + 1];
		Node[] succs = (Node[]) new Node[MAX_LEVEL + 1];
		while (true) {
			boolean found = find(x, preds, succs);
			if (found) {
				return false;
			} else {
				Node newNode = new Node(x, topLevel);
				for (int level = bottomLevel; level <= topLevel; level++) {
					Node succ = succs[level];
					newNode.next[level].set(succ, false);
				}
				Node pred = preds[bottomLevel];
				Node succ = succs[bottomLevel];
//				newNode.next[bottomLevel].set(succ, false);
				if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false,
						false)) {
					continue;
				}
				for (int level = bottomLevel + 1; level <= topLevel; level++) {
					while (true) {
						pred = preds[level];
						succ = succs[level];
						if (pred.next[level].compareAndSet(succ, newNode,
								false, false))
							break;
						find(x, preds, succs);
					}
				}
				return true;
			}
		}
	}

	boolean remove(int x) {
		int bottomLevel = 0;
		Node[] preds = (Node[]) new Node[MAX_LEVEL + 1];
		Node[] succs = (Node[]) new Node[MAX_LEVEL + 1];
		Node succ;
		while (true) {
			boolean found = find(x, preds, succs);
			if (!found) {
				return false;
			} else {
				Node nodeToRemove = succs[bottomLevel];
				for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
					boolean[] marked = { false };
					succ = nodeToRemove.next[level].get(marked);
					while (!marked[0]) {
//						nodeToRemove.next[level].attemptMark(succ, true);
						nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
						succ = nodeToRemove.next[level].get(marked);
					}
				}
				boolean[] marked = { false };
				succ = nodeToRemove.next[bottomLevel].get(marked);
				while (true) {
					boolean iMarkedIt = nodeToRemove.next[bottomLevel]
							.compareAndSet(succ, succ, false, true);
					succ = succs[bottomLevel].next[bottomLevel].get(marked);
					if (iMarkedIt) {
						find(x, preds, succs);
						return true;
					} else if (marked[0])
						return false;
				}
			}
		}
	}

	boolean find(int x, Node[] preds, Node[] succs) {
		int bottomLevel = 0;
		int key = x;
		boolean[] marked = { false };
		boolean snip;
		Node pred = null, curr = null, succ = null;
		retry: while (true) {
			pred = head;
			for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
				curr = pred.next[level].getReference();
				while (true) {
					succ = curr.next[level].get(marked);
					while (marked[0]) {
						snip = pred.next[level].compareAndSet(curr, succ,
								false, false);
						if (!snip)
							continue retry;
						curr = pred.next[level].getReference();
						succ = curr.next[level].get(marked);
					}
					if (curr.key < key) {
						pred = curr;
						curr = succ;
					} else {
						break;
					}
				}
				preds[level] = pred;
				succs[level] = curr;
			}
			return (curr.key == key);
		}
	}

	boolean contains(int x) {
		Node[] preds = (Node[]) new Node[MAX_LEVEL + 1];
		Node[] succs = (Node[]) new Node[MAX_LEVEL + 1];
		return find(x, preds, succs);

//		int bottomLevel = 0;
//		int v = x;
//		boolean[] marked = { false };
//		Node pred = head, curr = null, succ = null;
//		for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
//			curr = curr.next[ level ]. getReference();
//			while (true) {
//				succ = curr.next[level].get(marked);
//				while (marked[0]) {
//					curr = pred.next[level].getReference();
//					succ = curr.next[level].get(marked);
//				}
//				if (curr.key < v) {
//					pred = curr;
//					curr = succ;
//				} else {
//					break;
//				}
//			}
//		}
//		return (curr.key == v);
	}

}
