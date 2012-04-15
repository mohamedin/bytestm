package org.deuce.transaction.ringstm;

import java.util.Arrays;

import org.deuce.reflection.UnsafeHolder;

public class WriteSetHashOnly {
	Object[] objects; 
	public long[] refs;
	 byte[] sizes;
	 long[] vals;
	 Object objValues[];
	 public int[] indexes;
	 public int indexSize = 0;
	 int tableSize = 1024;
	 int modMask = 1023;
	 int elementsSize =0;
	 int threshold = tableSize*2/3;
	public long lastVal;
//	public Object lastValObj;
	int objValuesIndex = 0;
	public WriteSetHashOnly(){
		refs = new long[tableSize];
		sizes = new byte[tableSize];
		vals = new long[tableSize];
		objects = new Object[tableSize];
		indexes = new int[4096];
		objValues = new Object[1024];
	}
	
	public static final byte BYTE = 1;
	public static final byte INT = 2;
	public static final byte SHORT = 3;
	public static final byte LONG = 4;
	public static final byte BOOLEAN = 5;
	public static final byte CHAR = 6;
	public static final byte DOUBLE = 7;
	public static final byte FLOAT = 8;
	public static final byte OBJECT = 9;
	
	public void add(Object obj, long ref, long val, byte size, Object real){
//		Statistics.writesCountMax++;
		if (elementsSize > threshold)
			extendTable();
		int hash = (System.identityHashCode( obj) + (int)ref) & modMask;
		while (sizes[hash]!=0){
			if (objects[hash] == obj && refs[hash] == ref){
				if (sizes[hash] == OBJECT)
					objValues[(int) vals[hash]] = real;
				else
					vals[hash] = val;
				return;
			}
			hash = (hash+1) & modMask;
		}
//		Statistics.writeSetSizeMax++;
		sizes[hash] = size;
		refs[hash] = ref;
		objects[hash] = obj; 
		if (size == OBJECT){
			objValues[objValuesIndex] = real;
			vals[hash] = objValuesIndex;
			objValuesIndex++;
		}
		else
			vals[hash] = val;
		elementsSize++;
		indexes[indexSize++] = hash;
	}

	public boolean contains(Object obj, long ref){
		int hash = (System.identityHashCode( obj) + (int)ref) & modMask;
		while (sizes[hash]!=0){
			if (objects[hash] == obj && refs[hash] == ref){
				lastVal = vals[hash];
//				lastValObj = objects[hash];
//				Statistics.selfReadsCountMax++;
				return true;
			}
			hash = (hash+1) & modMask;
		}
		return false;
	}
	
	
	public boolean isEmpty() {
		return elementsSize ==0;
	}
	
	 void extendTable() {
//		VM.sysWriteln("Extending hash");
		long[] tempRefs = refs;
		byte[] tempSizes = sizes;
		long[] tempVals = vals;
		Object[] tempObjects = objects;
		tableSize = tableSize << 1;
		modMask = tableSize -1;
		threshold = tableSize*2/3;
		refs = new long[tableSize];
		sizes = new byte[tableSize];
		vals = new long[tableSize];
		objects = new Object[tableSize];
		
		for (int i=0; i<tempRefs.length; i++){
			if (tempSizes[i] != 0){
				int hash = (System.identityHashCode( tempObjects[i]) + (int)tempRefs[i])  & modMask;
				while (sizes[hash]!=0){
					hash = (hash+1) & modMask;
				}
				sizes[hash] = tempSizes[i];
				refs[hash] = tempRefs[i];
				vals[hash] = tempVals[i];
				objects[hash] = tempObjects[i];
			}
		}
//		if (indexSize > )
	}
	public void clear(){
		elementsSize = 0;
		Arrays.fill(sizes, (byte) 0);
		indexSize = 0;
		objValuesIndex =0;
	}
	
//	public void writeBackTL2(int newClock, byte[] locksMarker){
//		for (int i=0; i<indexSize; i++){
//			int index = indexes[i];
//			switch(sizes[index]){
//			case BYTE:
//				UnsafeHolder.getUnsafe().putByte(objects[index], refs[index], (byte)vals[index]);
//				break;
//			case BOOLEAN:
//				UnsafeHolder.getUnsafe().putBoolean(objects[index], refs[index], vals[index]>0?true:false);
//				break;
//			case CHAR:
//				UnsafeHolder.getUnsafe().putChar(objects[index], refs[index], (char)vals[index]);
//				break;
//			case DOUBLE:
//				UnsafeHolder.getUnsafe().putDouble(objects[index], refs[index], Double.longBitsToDouble(vals[index]));
//				break;
//			case FLOAT:
//				UnsafeHolder.getUnsafe().putFloat(objects[index], refs[index], Float.intBitsToFloat((int)vals[index]));
//				break;
//			case INT:
//				UnsafeHolder.getUnsafe().putInt(objects[index], refs[index], (int)vals[index]);
//				break;
//			case LONG:
//				UnsafeHolder.getUnsafe().putLong(objects[index], refs[index], vals[index]);
//				break;
//			case SHORT:
//				UnsafeHolder.getUnsafe().putShort(objects[index], refs[index], (short)vals[index]);
//				break;
//			case OBJECT:
//				UnsafeHolder.getUnsafe().putObject(objects[index], refs[index], objValues[(int) vals[index]]);
//				break;
//			}
//			LockTable.setAndReleaseLock( (System.identityHashCode( objects[index]) + (int)refs[index]) & LockTable.MASK, newClock, locksMarker);
//		}
//	}
	
	public void writeBack(){
		for (int i=0; i<indexSize; i++){
			int index = indexes[i];
			switch(sizes[index]){
			case BYTE:
				UnsafeHolder.getUnsafe().putByte(objects[index], refs[index], (byte)vals[index]);
				break;
			case BOOLEAN:
				UnsafeHolder.getUnsafe().putBoolean(objects[index], refs[index], vals[index]>0?true:false);
				break;
			case CHAR:
				UnsafeHolder.getUnsafe().putChar(objects[index], refs[index], (char)vals[index]);
				break;
			case DOUBLE:
				UnsafeHolder.getUnsafe().putDouble(objects[index], refs[index], Double.longBitsToDouble(vals[index]));
				break;
			case FLOAT:
				UnsafeHolder.getUnsafe().putFloat(objects[index], refs[index], Float.intBitsToFloat((int)vals[index]));
				break;
			case INT:
				UnsafeHolder.getUnsafe().putInt(objects[index], refs[index], (int)vals[index]);
				break;
			case LONG:
				UnsafeHolder.getUnsafe().putLong(objects[index], refs[index], vals[index]);
				break;
			case SHORT:
				UnsafeHolder.getUnsafe().putShort(objects[index], refs[index], (short)vals[index]);
				break;
			case OBJECT:
				UnsafeHolder.getUnsafe().putObject(objects[index], refs[index], objValues[(int) vals[index]]);
				break;
			}
		}
	}

}
