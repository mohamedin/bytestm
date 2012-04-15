package org.jikesrvm.parallelizer;

import org.jikesrvm.CommandLineArgs;
import org.jikesrvm.classloader.NormalMethod;

public class BBProfiler {

	// Hydra argument
	private static String profileMethod = null;
	private static String profileClass = null;
	private static String profilePackage = null;
	
	public static void boot(){
		profileClass = "Test";
		String[] args = CommandLineArgs.getHydraArgs();
		if(args!=null)
			for(String arg: args){
				String[] params = arg.split("=");
				if(params.length==2)
					if(params[0].equals("method"))
						profileMethod = params[1];
					else if(params[0].equals("class"))
						profileClass = params[1];
					else if(params[0].equals("package"))
						profilePackage = params[1];
			}

	}

	public static boolean profile(NormalMethod method) {
		if(profilePackage==null && profileClass==null && profileMethod==null)
			return false;
		if(profilePackage!=null && method.getDeclaringClass().getPackageName().startsWith(profilePackage))
			return true;
		if(profileClass!=null && method.getDeclaringClass().toString().startsWith(profileClass))
			return true;
		if(profileMethod!=null && method.getName().toString().startsWith(profileMethod))
			return true;		
		return false;
	}
}