package org.deuce.benchmark.intset;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class IntSetLinkedListLock implements IntSet {

	public class Node {
		final private int m_value;
//		private int m_value;
		private Node m_next;

		public Node(int value, Node next) {
			m_value = value;
			m_next = next;
		}

		public Node(int value) {
			this(value, null);
		}

		public int getValue() {
			return m_value;
		}

		public void setNext(Node next) {
			m_next = next;
		}

		public Node getNext() {
			return m_next;
		}
	}

	final private Node m_first;
//	private Node m_first;
	
	public IntSetLinkedListLock() {
		Node min = new Node(Integer.MIN_VALUE);
		Node max = new Node(Integer.MAX_VALUE);
		min.setNext(max);
		m_first = min;
	}

	synchronized
	public boolean add(int value) {
		boolean result = false; 
//		try{stm.STM.xBegin();

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v != value;
		if (result) {
			previous.setNext(new Node(value, next));
		}
//		}catch(stm.STMException e){}finally{stm.STM.xCommit();}
		return result;
	}

	synchronized
	public boolean remove(int value) {
		boolean result = false;
//		try{stm.STM.xBegin();

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v == value;
		if (result) {
			previous.setNext(next.getNext());
		}
//		}catch(stm.STMException e){}finally{stm.STM.xCommit();}
		return result;
	}

	synchronized
	public boolean contains(int value) {
		boolean result = false;
//		try{stm.STM.xBegin();

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = (v == value);
//		}catch(stm.STMException e){}finally{stm.STM.xCommit();}
		return result;
	}
	public void validate(){
		Node previous = m_first;
		Node next = previous.getNext();
		int value=previous.getValue();
		int v;
		int c=0;
		while ((v = next.getValue()) > value) {
			previous = next;
			next = previous.getNext();
			System.out.print(value+", ");
			value = v;
			if (next == null) break;
			c++;
		}
		if (next!=null) System.out.println("Error not ordered");
		System.out.println("Count="+c);
		
	}
}
