package org.jikesrvm;

import org.jikesrvm.parallelizer.BBProfiler;
import org.jikesrvm.parallelizer.stm.STM;

abstract public class Hydra {
	
	public static void boot(){
		BBProfiler.boot();
		STM.boot();
	}

	public static void shutdown() {
		STM.printStatistics();
	}

}
