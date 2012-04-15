public class Test {
	long counter;

	public static void main(String[] args) {
		final Test tt = new Test();
		new Thread() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
					try {
						stm.STM.xBegin();
						tt.counter++;
					} catch (stm.STMException e) {
					} finally {
						stm.STM.xCommit();
					}
				}
				System.out.println("Thread: " + tt.counter);
			}
		}.start();
		for (int i = 0; i < 10000; i++) {
			try {
				stm.STM.xBegin();
				tt.counter++;
			} catch (stm.STMException e) {
			} finally {
				stm.STM.xCommit();
			}
		}
		System.out.println("Main:" + tt.counter);

	}
}