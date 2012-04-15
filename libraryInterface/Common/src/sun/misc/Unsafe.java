/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package sun.misc;

import java.lang.reflect.Field;

import org.jikesrvm.classloader.RVMField;
import org.jikesrvm.classloader.RVMType;
import org.jikesrvm.runtime.Magic;
import org.jikesrvm.scheduler.Synchronization;
import org.jikesrvm.scheduler.RVMThread;
import org.vmmagic.unboxed.Offset;

public final class Unsafe {
  private static final Unsafe theUnsafe = new Unsafe();

  private Unsafe() {}

  public static Unsafe getUnsafe() {
//    SecurityManager sm = System.getSecurityManager();
//    if (sm != null)
//      sm.checkPropertiesAccess();
    return theUnsafe;
  }

  private Offset longToOffset(long offset) {
    return Offset.fromIntSignExtend((int)offset);
  }

  public Object staticFieldBase(Field f){
	  return null;
  }
  
  public long objectFieldOffset(Field field) {
    RVMField vmfield = java.lang.reflect.JikesRVMSupport.getFieldOf(field);
    return vmfield.getOffset().toLong();
  }
  
  public long staticFieldOffset(Field field) {
	    RVMField vmfield = java.lang.reflect.JikesRVMSupport.getFieldOf(field);
	    return Magic.getJTOC().toLong() + vmfield.getOffset().toLong();
	  }
  

  public boolean compareAndSwapInt(Object obj,long offset,int expect,int update) {
    Offset off = longToOffset(offset);
    return Synchronization.tryCompareAndSwap(obj, off, expect, update);
  }

  public boolean compareAndSwapLong(Object obj,long offset,long expect,long update) {
    Offset off = Offset.fromIntSignExtend((int)offset);
    return Synchronization.tryCompareAndSwap(obj, off, expect, update);
  }

  public boolean compareAndSwapObject(Object obj,long offset,Object expect,Object update) {
    Offset off = Offset.fromIntSignExtend((int)offset);
    return Synchronization.tryCompareAndSwap(obj, off, expect, update);
  }

  public void putOrderedInt(Object obj,long offset,int value) {
    Offset off = longToOffset(offset);
    Magic.setIntAtOffset(obj,off,value);
  }

  public void putOrderedLong(Object obj,long offset,long value) {
    Offset off = longToOffset(offset);
    Magic.setLongAtOffset(obj,off,value);
  }

  public void putOrderedObject(Object obj,long offset,Object value) {
    Offset off = longToOffset(offset);
    Magic.setObjectAtOffset(obj,off,value);
   }

  public void putIntVolatile(Object obj,long offset,int value) {
    Offset off = longToOffset(offset);
    Magic.setIntAtOffset(obj,off,value);
  }

  public int getIntVolatile(Object obj,long offset) {
    Offset off = longToOffset(offset);
    return Magic.getIntAtOffset(obj,off);
  }

  public int getInt(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getIntAtOffset(obj,off);
	  }

  public byte getByte(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getByteAtOffset(obj,off);
	  }

  public char getChar(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getCharAtOffset(obj,off);
	  }
//workaround
  public boolean getBoolean(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getByteAtOffset(obj,off)>0;
	  }

  public short getShort(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getShortAtOffset(obj,off);
	  }
//workaround
  public float getFloat(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Float.intBitsToFloat(Magic.getIntAtOffset(obj,off));
	  }
//workaround
  public double getDouble(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Double.longBitsToDouble(Magic.getLongAtOffset(obj,off));
	  }

  public void putLongVolatile(Object obj,long offset,long value) {
    Offset off = longToOffset(offset);
    Magic.setLongAtOffset(obj,off,value);
   }

  public void putLong(Object obj,long offset,long value) {
    Offset off = longToOffset(offset);
    Magic.setLongAtOffset(obj,off,value);
  }

  public void putByte(Object obj,long offset,byte value) {
	    Offset off = longToOffset(offset);
	    Magic.setByteAtOffset(obj,off,value);
	  }
  //workaround
  public void putBoolean(Object obj,long offset,boolean value) {
	    Offset off = longToOffset(offset);
	    Magic.setByteAtOffset(obj,off,(byte)(value? 1 :0));
	  }

  public void putChar(Object obj,long offset,char value) {
	    Offset off = longToOffset(offset);
	    Magic.setCharAtOffset(obj,off,value);
	  }
	  //may cause a problem
  public void putShort(Object obj,long offset,short value) {
	    Offset off = longToOffset(offset);
	    Magic.setCharAtOffset(obj,off,(char)value);
	  }
	   //workaround
  public void putFloat(Object obj,long offset,float value) {
	    Offset off = longToOffset(offset);
	    Magic.setIntAtOffset(obj,off,Float.floatToIntBits(value));
	  }
  public void putInt(Object obj,long offset,int value) {
	    Offset off = longToOffset(offset);
	    Magic.setIntAtOffset(obj,off,value);
	  }

  public void putDouble(Object obj,long offset,double value) {
	    Offset off = longToOffset(offset);
	    Magic.setDoubleAtOffset(obj,off,value);
	  }

  public long getLongVolatile(Object obj,long offset) {
    Offset off = longToOffset(offset);
    return Magic.getLongAtOffset(obj,off);
  }

  public long getLong(Object obj,long offset) {
    Offset off = longToOffset(offset);
    return Magic.getLongAtOffset(obj,off);
  }

  public void putObjectVolatile(Object obj,long offset,Object value) {
    Offset off = longToOffset(offset);
    Magic.setObjectAtOffset(obj,off,value);
  }

  public void putObject(Object obj,long offset,Object value) {
    Offset off = longToOffset(offset);
    Magic.setObjectAtOffset(obj,off,value);
  }

  public Object getObjectVolatile(Object obj,long offset) {
    Offset off = longToOffset(offset);
    return Magic.getObjectAtOffset(obj,off);
  }

  public Object getObject(Object obj,long offset) {
	    Offset off = longToOffset(offset);
	    return Magic.getObjectAtOffset(obj,off);
	  }
  
  public int arrayBaseOffset(Class<?> arrayClass) {
    return 0;
  }

  public int arrayIndexScale(Class<?> arrayClass) {
    RVMType arrayType = java.lang.JikesRVMSupport.getTypeForClass(arrayClass);
    if (!arrayType.isArrayType()) {
      return 0;
    } else {
      return 1 << arrayType.asArray().getLogElementSize();
    }
  }

  public void unpark(Object thread) {
    RVMThread vmthread = java.lang.JikesRVMSupport.getThread((Thread)thread);
    if (vmthread != null) {
      vmthread.unpark();
    }
  }

  public void park(boolean isAbsolute,long time) throws Throwable  {
    RVMThread vmthread = java.lang.JikesRVMSupport.getThread(Thread.currentThread());
    vmthread.park(isAbsolute, time);
  }
}
