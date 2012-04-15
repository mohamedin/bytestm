package org.deuce.benchmark;

import org.deuce.Atomic;

public class TestCounterArr {
	
	public static class MyInt {
		int mine;
	}
	public static class Counter {
		
		public Counter(){
//			value = new long[50];
//			value = new MyInt[50];
//			for (int i=0;i<value.length;i++)
//				value[i]=new MyInt();
		}
		@Atomic
		public void inc() {
			try{
				stm.STM.xBegin();
	//			for (int i=0;i<value.length;i++)
	//				value[i]++;
	//				value[i].mine++;
				value++;
			}catch(stm.STMException e){}
			finally{stm.STM.xCommit();}
		}
		
		public void print(){
//			for (int i=0;i<value.length;i++)
//				System.out.print(value[i] + ", ");
//				System.out.print(value[i].mine + ", ");
			System.out.println(value);
			System.out.println();
		}
		
//		private MyInt[] value;
//		private long[] value;
		private long value=0;
	  }


	public static void main(String[] args) {
	    final Counter counter = new Counter();
	    
	    
		long start = System.currentTimeMillis();
		Thread th = new Thread() {
			public void run() {
				try{

				for (int i = 0; i < 10000; i++) {
					counter.inc();
				}
				System.out.println("T1: " );
				counter.print();
			}
				catch (Exception e) {
				}
			}
		};
		
		Thread th2 = new Thread() {
			public void run() {
				try{
				for (int i = 0; i < 10000; i++) {
                        	counter.inc();
				}
				}catch (Exception e) {
				}
				System.out.println("T2: ");
				counter.print();
			}
		};
		th.start();
		th2.start();

                try{
                        th.join();
                        th2.join();
                } catch (InterruptedException ignore) {}
		System.out.println("M:" );
		counter.print();
		long end = System.currentTimeMillis();
		System.out.println("iExecution time was "+(end-start)+" ms.");

	}
}
