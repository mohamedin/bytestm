package jstamp;

public class Test {
public static void main(String[] args) throws Exception {
	try {
		System.out.println("gsfdgsdg before");
		int z=1;
		if (z==1)throw new Exception("sdfsdf Exp");
	}
	catch (Exception e) {
		System.out.println("gsfdgsdg after");
	}
//	int c=0;
//	while(true){
//		System.out.println("first");
//		c++;
//		if (c%2==0) continue;
//		System.out.println("odd"+c);
//		if (c > 10) break;
//	}
}
}
