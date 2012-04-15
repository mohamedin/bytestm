package org.deuce.benchmark.intset;


/**
 * @author Pascal Felber
 * @since 0.1
 */
public class SkipListLockFree implements IntSet {
	LockFreeSkipList myList = new LockFreeSkipList();
	public SkipListLockFree() {
	}

	public boolean add(int value) {
		return myList.add(value);
	}

	public boolean remove(int value) {
		return myList.remove(value);
	}

	public boolean contains(int value) {
		return myList.contains(value);
	}
	public void validate(){
//		Node previous = m_first;
//		Node next = previous.getNext();
//		int value=previous.getValue();
//		int v;
//		int c=0;
//		while ((v = next.getValue()) > value) {
//			previous = next;
//			next = previous.getNext();
//			System.out.print(value+", ");
//			value = v;
//			if (next == null) break;
//			c++;
//		}
//		if (next!=null) System.out.println("Error not ordered");
//		System.out.println("Count="+c);
//		
	}
}
