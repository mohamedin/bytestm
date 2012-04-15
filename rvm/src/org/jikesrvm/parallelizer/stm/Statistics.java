package org.jikesrvm.parallelizer.stm;

public class Statistics {
	public static  double writeSetSizeAvg;
	public static int writeSetSizeMax;
	public static double writesCountAvg;
	public static int writesCountMax;
	
	public static double readSetSizeAvg;
	public static int readSetSizeMax;
	public static double selfReadsCountAvg;
	public static int selfReadsCountMax;
	
	public static int commits;
	public static int retries;
	public static int readOnlyCommittedTx;
	
	public static double txTimeAvg;
	public static long txTimeMax;
	
	public static String asString() {
		return "writeSetSizeAvg: " + writeSetSizeAvg + ", writeSetSizeMax: " +writeSetSizeMax/(double)commits
			+ "\n, writesCountAvg: " + writesCountAvg + ", writesCountMax: " + writesCountMax/(double)commits
			+ "\n, readSetSizeAvg: " + readSetSizeAvg + ", readSetSizeMax: " + readSetSizeMax/(double)commits
			+ "\n, selfReadsCountAvg: " + selfReadsCountAvg + ", selfReadsCountMax: " + selfReadsCountMax/(double)commits
			+ "\n, commits: " + commits + ", readOnlyCommittedTx: " + readOnlyCommittedTx + ", retries: " + retries + ", ratio"+((retries/(double)commits)*100)
			+ "\n, txTimeAvg: " + txTimeAvg/commits + ", txTimeMax: " + txTimeMax;
	}
}
