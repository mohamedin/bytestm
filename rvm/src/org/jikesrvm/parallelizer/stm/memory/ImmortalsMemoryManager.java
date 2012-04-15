package org.jikesrvm.parallelizer.stm.memory;

import org.jikesrvm.classloader.RVMArray;
import org.jikesrvm.classloader.RVMClass;
import org.jikesrvm.mm.mminterface.MemoryManager;
import org.jikesrvm.objectmodel.ObjectModel;
import org.jikesrvm.objectmodel.TIB;
import org.jikesrvm.parallelizer.stm.WriteSetHashOnly;
import org.jikesrvm.runtime.Reflection;
import org.jikesrvm.runtime.RuntimeEntrypoints;
import org.mmtk.plan.Plan;

/**
 * 
 * Allocate memory in immutable space so that GC has no control on it
 * @author Mohamed Mohamedin
 * Copyrights 2012
 *
 */

public class ImmortalsMemoryManager {
	//New Immortal Object (must have only one constructor with no parameters)
	public static Object newObject(Class<?> klass){
		RVMClass rvmClass = java.lang.JikesRVMSupport.getTypeForClass(klass).asClass();
		Object obj = (WriteSetHashOnly) RuntimeEntrypoints.resolvedImmortalNewScalar(rvmClass);// new WriteSetHashOnly();
		Reflection.invoke(rvmClass.getConstructorMethods()[0], null, obj, null, true);
		return obj;
	}

	public static byte[] newByteArray(int size) {
		return (byte[]) newArray(size, RVMArray.ByteArray); 
	}

	public static int[] newIntArray(int size) {
		return (int[]) newArray(size, RVMArray.IntArray); 
	}

	public static long[] newLongArray(int size) {
		return (long[]) newArray(size, RVMArray.LongArray); 
	}
	
	public static Object newObjectArray(int size, Class<?> klass){
		return newArray(size, java.lang.JikesRVMSupport.getTypeForClass(klass).getArrayTypeForElementType());
	}
	
	private static Object newArray(int size, RVMArray arrayType) {
		int headerSize = ObjectModel.computeArrayHeaderSize(arrayType);
		int align = ObjectModel.getAlignment(arrayType);
		int offset = ObjectModel.getOffsetForAlignment(arrayType, false);
		int width = arrayType.getLogElementSize();
		TIB arrayTib = arrayType.getTypeInformationBlock();

		return MemoryManager.allocateArray(size, width, headerSize, arrayTib,
				Plan.ALLOC_IMMORTAL, align, offset, Plan.DEFAULT_SITE);

	}
}
