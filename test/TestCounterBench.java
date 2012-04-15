import org.deuce.Atomic;
public class TestCounterBench {
	
	public static class Counter {
		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		private int value;
	  }



public static class Incrementer{
	public Incrementer() {
		counter = new Counter();
	}
	private Counter counter;
  @Atomic
	public void inc(){
	try{stm.STM.xBegin();
		counter.setValue(counter.getValue()+1);
	}catch(stm.STMException e){}finally{stm.STM.xCommit();}
	}
	public void printVal(){
		System.out.println("val: " + counter.getValue());
	}
}

	public static void main(String[] args) {
	    final Incrementer inc = new Incrementer();
		long start = System.currentTimeMillis();
		Thread th = new Thread() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
                        	inc.inc();
				}
				System.out.println("T1: " );
				inc.printVal();
			}
		};
		
		Thread th2 = new Thread() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
                        	inc.inc();
				}
				System.out.println("T2: ");
				inc.printVal();
			}
		};
		th.start();
		th2.start();

                try{
                        th.join();
                        th2.join();
                } catch (InterruptedException ignore) {}
		System.out.println("M:" );
		inc.printVal();
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

	}
}