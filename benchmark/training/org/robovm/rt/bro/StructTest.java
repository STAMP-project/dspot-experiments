/**
 * Copyright (C) 2012 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.rt.bro;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.VM;
import org.robovm.rt.bro.annotation.Array;
import org.robovm.rt.bro.annotation.ByRef;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.MachineSizedSInt;
import org.robovm.rt.bro.annotation.MachineSizedUInt;
import org.robovm.rt.bro.annotation.Marshaler;
import org.robovm.rt.bro.annotation.MarshalsPointer;
import org.robovm.rt.bro.annotation.StructMember;
import org.robovm.rt.bro.ptr.BytePtr;
import org.robovm.rt.bro.ptr.CharPtr;
import org.robovm.rt.bro.ptr.DoublePtr;
import org.robovm.rt.bro.ptr.FloatPtr;
import org.robovm.rt.bro.ptr.IntPtr;
import org.robovm.rt.bro.ptr.LongPtr;
import org.robovm.rt.bro.ptr.Ptr;
import org.robovm.rt.bro.ptr.ShortPtr;

import static Bro.IS_32BIT;
import static Bro.IS_64BIT;


/**
 *
 */
public class StructTest {
    public enum SimpleEnum {

        V1,
        V2,
        V3;}

    public enum TestValuedEnum implements ValuedEnum {

        V100(100),
        V1000(1000),
        V10000(10000);
        private final int n;

        private TestValuedEnum(int n) {
            this.n = n;
        }

        public long value() {
            return n;
        }
    }

    public static final class TestBits extends Bits<StructTest.TestBits> {
        public static final StructTest.TestBits V1 = new StructTest.TestBits(1);

        public static final StructTest.TestBits V2 = new StructTest.TestBits(2);

        public static final StructTest.TestBits V4 = new StructTest.TestBits(4);

        public static final StructTest.TestBits V8 = new StructTest.TestBits(8);

        private static final StructTest.TestBits[] VALUES = _values(StructTest.TestBits.class);

        private TestBits(long value) {
            super(value);
        }

        private TestBits(long value, long mask) {
            super(value, mask);
        }

        @Override
        protected StructTest.TestBits wrap(long value, long mask) {
            return new StructTest.TestBits(value, mask);
        }

        @Override
        protected StructTest.TestBits[] _values() {
            return StructTest.TestBits.VALUES;
        }
    }

    public static class StringMarshaler {
        static List<String> calls = new ArrayList<String>();

        @MarshalsPointer
        public static String toObject(Class<?> cls, long handle, long flags) {
            BytePtr ptr = Struct.toStruct(BytePtr.class, handle);
            String o = (ptr != null) ? ptr.toStringAsciiZ() : null;
            String s = (o == null) ? null : ("'" + o) + "'";
            StructTest.StringMarshaler.calls.add((((("toObject(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
            return o;
        }

        @MarshalsPointer
        public static long toNative(String o, long flags) {
            String s = (o == null) ? null : ("'" + o) + "'";
            StructTest.StringMarshaler.calls.add((((("toNative(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
            if (o == null) {
                return 0L;
            }
            BytePtr ptr = BytePtr.toBytePtrAsciiZ(((String) (o)));
            return ptr.getHandle();
        }

        public static void updateObject(Object o, long handle, long flags) {
            String s = (o == null) ? null : ("'" + o) + "'";
            StructTest.StringMarshaler.calls.add((((("updateObject(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
        }

        public static void updateNative(Object o, long handle, long flags) {
            String s = (o == null) ? null : ("'" + o) + "'";
            StructTest.StringMarshaler.calls.add((((("updateNative(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
        }
    }

    public static final class Point extends Struct<StructTest.Point> {
        @StructMember(0)
        public native int x();

        @StructMember(1)
        public native int y();

        @StructMember(0)
        public native StructTest.Point x(int x);

        @StructMember(1)
        public native StructTest.Point y(int y);
    }

    public static final class PointPtr extends Ptr<StructTest.Point, StructTest.PointPtr> {}

    public static final class PointPtrPtr extends Ptr<StructTest.PointPtr, StructTest.PointPtrPtr> {}

    public static final class TestUnion extends Struct<StructTest.TestUnion> {
        @StructMember(0)
        public native byte b();

        @StructMember(0)
        public native StructTest.TestUnion b(byte b);

        @StructMember(0)
        public native long l();

        @StructMember(0)
        public native StructTest.TestUnion l(long l);

        @StructMember(0)
        public native double d();

        @StructMember(0)
        public native StructTest.TestUnion d(double l);

        @StructMember(0)
        @ByVal
        public native StructTest.Point p();

        @StructMember(0)
        public native StructTest.TestUnion p(@ByVal
        StructTest.Point p);
    }

    public static final class MixedStructUnion extends Struct<StructTest.MixedStructUnion> {
        @StructMember(0)
        public native byte b1();

        @StructMember(0)
        public native StructTest.MixedStructUnion b1(byte b);

        @StructMember(1)
        public native long l();

        @StructMember(1)
        public native StructTest.MixedStructUnion l(long l);

        @StructMember(1)
        public native double d();

        @StructMember(1)
        public native StructTest.MixedStructUnion d(double l);

        @StructMember(2)
        public native byte b2();

        @StructMember(2)
        public native StructTest.MixedStructUnion b2(byte b);
    }

    @Marshaler(StructTest.StringMarshaler.class)
    public static final class TestStruct extends Struct<StructTest.TestStruct> {
        @StructMember(0)
        public native byte b();

        @StructMember(0)
        public native StructTest.TestStruct b(byte b);

        @StructMember(0)
        public native byte getB();

        @StructMember(0)
        public native void setB(byte b);

        @StructMember(1)
        public native long l();

        @StructMember(1)
        public native StructTest.TestStruct l(long l);

        @StructMember(1)
        public native long getL();

        @StructMember(1)
        public native void setL(long b);

        @StructMember(2)
        public native char c();

        @StructMember(2)
        public native StructTest.TestStruct c(char b);

        @StructMember(2)
        public native char getC();

        @StructMember(2)
        public native void setC(char b);

        @StructMember(3)
        public native int i();

        @StructMember(3)
        public native StructTest.TestStruct i(int i);

        @StructMember(3)
        public native int getI();

        @StructMember(3)
        public native void setI(int i);

        @StructMember(4)
        @ByVal
        public native StructTest.Point pointByVal();

        @StructMember(4)
        public native StructTest.TestStruct pointByVal(@ByVal
        StructTest.Point p);

        @StructMember(5)
        @ByRef
        public native StructTest.Point pointByRef();

        @StructMember(5)
        public native StructTest.TestStruct pointByRef(@ByRef
        StructTest.Point p);

        @StructMember(6)
        public native StructTest.TestStruct recursive();

        @StructMember(6)
        public native StructTest.TestStruct recursive(StructTest.TestStruct s);

        @StructMember(7)
        public native StructTest.PointPtr pointPtr();

        @StructMember(7)
        public native StructTest.TestStruct pointPtr(StructTest.PointPtr ptr);

        @StructMember(8)
        public native StructTest.PointPtrPtr pointPtrPtr();

        @StructMember(8)
        public native StructTest.TestStruct pointPtrPtr(StructTest.PointPtrPtr ptr);

        @StructMember(9)
        public native StructTest.SimpleEnum simpleEnum();

        @StructMember(9)
        public native StructTest.TestStruct simpleEnum(StructTest.SimpleEnum e);

        @StructMember(10)
        public native StructTest.TestValuedEnum valuedEnum();

        @StructMember(10)
        public native StructTest.TestStruct valuedEnum(StructTest.TestValuedEnum e);

        @StructMember(11)
        public native StructTest.TestBits bits();

        @StructMember(11)
        public native StructTest.TestStruct bits(StructTest.TestBits bits);

        @StructMember(12)
        public native String string();

        @StructMember(12)
        public native StructTest.TestStruct string(String s);

        @StructMember(13)
        @ByVal
        public native StructTest.TestUnion unionByVal();

        @StructMember(13)
        public native StructTest.TestStruct unionByVal(@ByVal
        StructTest.TestUnion u);

        @StructMember(13)
        public native long unionByValAsLong();

        @StructMember(13)
        public native StructTest.TestStruct unionByValAsLong(long l);
    }

    public static final class StructWithArray extends Struct<StructTest.StructWithArray> {
        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native BytePtr byteArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray byteArrayAsPtr(@Array({ 2, 3, 4 })
        BytePtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native ByteBuffer byteArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray byteArrayAsBuffer(@Array({ 2, 3, 4 })
        ByteBuffer p);

        @StructMember(0)
        @Array(24)
        public native byte[] byteArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray byteArray1D(@Array(24)
        byte[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native byte[][] byteArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray byteArray2D(@Array({ 3, 8 })
        byte[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native byte[][][] byteArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray byteArray3D(@Array({ 2, 3, 4 })
        byte[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native ShortPtr shortArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray shortArrayAsPtr(@Array({ 2, 3, 4 })
        ShortPtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native ShortBuffer shortArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray shortArrayAsBuffer(@Array({ 2, 3, 4 })
        ShortBuffer p);

        @StructMember(0)
        @Array(24)
        public native short[] shortArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray shortArray1D(@Array(24)
        short[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native short[][] shortArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray shortArray2D(@Array({ 3, 8 })
        short[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native short[][][] shortArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray shortArray3D(@Array({ 2, 3, 4 })
        short[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native CharPtr charArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray charArrayAsPtr(@Array({ 2, 3, 4 })
        CharPtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native CharBuffer charArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray charArrayAsBuffer(@Array({ 2, 3, 4 })
        CharBuffer p);

        @StructMember(0)
        @Array(24)
        public native char[] charArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray charArray1D(@Array(24)
        char[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native char[][] charArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray charArray2D(@Array({ 3, 8 })
        char[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native char[][][] charArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray charArray3D(@Array({ 2, 3, 4 })
        char[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native IntPtr intArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray intArrayAsPtr(@Array({ 2, 3, 4 })
        IntPtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native IntBuffer intArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray intArrayAsBuffer(@Array({ 2, 3, 4 })
        IntBuffer p);

        @StructMember(0)
        @Array(24)
        public native int[] intArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray intArray1D(@Array(24)
        int[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native int[][] intArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray intArray2D(@Array({ 3, 8 })
        int[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native int[][][] intArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray intArray3D(@Array({ 2, 3, 4 })
        int[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native LongPtr longArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray longArrayAsPtr(@Array({ 2, 3, 4 })
        LongPtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native LongBuffer longArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray longArrayAsBuffer(@Array({ 2, 3, 4 })
        LongBuffer p);

        @StructMember(0)
        @Array(24)
        public native long[] longArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray longArray1D(@Array(24)
        long[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native long[][] longArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray longArray2D(@Array({ 3, 8 })
        long[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native long[][][] longArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray longArray3D(@Array({ 2, 3, 4 })
        long[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native FloatPtr floatArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray floatArrayAsPtr(@Array({ 2, 3, 4 })
        FloatPtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native FloatBuffer floatArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray floatArrayAsBuffer(@Array({ 2, 3, 4 })
        FloatBuffer p);

        @StructMember(0)
        @Array(24)
        public native float[] floatArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray floatArray1D(@Array(24)
        float[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native float[][] floatArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray floatArray2D(@Array({ 3, 8 })
        float[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native float[][][] floatArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray floatArray3D(@Array({ 2, 3, 4 })
        float[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native DoublePtr doubleArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray doubleArrayAsPtr(@Array({ 2, 3, 4 })
        DoublePtr p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native DoubleBuffer doubleArrayAsBuffer();

        @StructMember(0)
        public native StructTest.StructWithArray doubleArrayAsBuffer(@Array({ 2, 3, 4 })
        DoubleBuffer p);

        @StructMember(0)
        @Array(24)
        public native double[] doubleArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray doubleArray1D(@Array(24)
        double[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native double[][] doubleArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray doubleArray2D(@Array({ 3, 8 })
        double[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native double[][][] doubleArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray doubleArray3D(@Array({ 2, 3, 4 })
        double[][][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native StructTest.Point pointArrayAsPtr();

        @StructMember(0)
        public native StructTest.StructWithArray pointArrayAsPtr(@Array({ 2, 3, 4 })
        StructTest.Point p);

        @StructMember(0)
        @Array(24)
        public native StructTest.Point[] pointArray1D();

        @StructMember(0)
        public native StructTest.StructWithArray pointArray1D(@Array(24)
        StructTest.Point[] p);

        @StructMember(0)
        @Array({ 3, 8 })
        public native StructTest.Point[][] pointArray2D();

        @StructMember(0)
        public native StructTest.StructWithArray pointArray2D(@Array({ 3, 8 })
        StructTest.Point[][] p);

        @StructMember(0)
        @Array({ 2, 3, 4 })
        public native StructTest.Point[][][] pointArray3D();

        @StructMember(0)
        public native StructTest.StructWithArray pointArray3D(@Array({ 2, 3, 4 })
        StructTest.Point[][][] p);

        @StructMember(0)
        @Array(24)
        public native String byteArrayAsString();

        @StructMember(0)
        public native StructTest.StructWithArray byteArrayAsString(@Array(24)
        String s);
    }

    public static final class MachineSizedStruct extends Struct<StructTest.MachineSizedStruct> {
        @StructMember(0)
        @MachineSizedFloat
        public native double machineSizedFloatD();

        @StructMember(0)
        public native StructTest.MachineSizedStruct machineSizedFloatD(@MachineSizedFloat
        double d);

        @StructMember(0)
        @MachineSizedFloat
        public native float machineSizedFloatF();

        @StructMember(0)
        public native StructTest.MachineSizedStruct machineSizedFloatF(@MachineSizedFloat
        float f);

        @StructMember(0)
        @MachineSizedSInt
        public native long machineSizedSInt();

        @StructMember(0)
        public native StructTest.MachineSizedStruct machineSizedSInt(@MachineSizedSInt
        long l);

        @StructMember(0)
        @MachineSizedUInt
        public native long machineSizedUInt();

        @StructMember(0)
        public native StructTest.MachineSizedStruct machineSizedUInt(@MachineSizedUInt
        long l);
    }

    @Test
    public void testSimpleMembers() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertEquals(0, s.b());
        s.b(((byte) (123)));
        Assert.assertEquals(123, VM.getByte(getHandle()));
        Assert.assertEquals(123, s.b());
        Assert.assertEquals(0, s.l());
        s.l(-8065326438389885320L);
        Assert.assertEquals(-8065326438389885320L, VM.getLong(((getHandle()) + (IS_64BIT ? 8 : 4))));
        Assert.assertEquals(-8065326438389885320L, s.l());
        Assert.assertEquals(0, s.c());
        s.c(((char) (9876)));
        Assert.assertEquals(9876, VM.getChar(((getHandle()) + (IS_64BIT ? 16 : 12))));
        Assert.assertEquals(9876, s.c());
        Assert.assertEquals(0, s.i());
        s.i(305419896);
        Assert.assertEquals(305419896, VM.getInt(((getHandle()) + (IS_64BIT ? 20 : 16))));
        Assert.assertEquals(305419896, s.i());
        Assert.assertEquals(123, s.b());
        Assert.assertEquals(-8065326438389885320L, s.l());
        Assert.assertEquals(9876, s.c());
        Assert.assertEquals(305419896, s.i());
    }

    @Test
    public void testByValMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertEquals(0, s.pointByVal().x());
        Assert.assertEquals(0, s.pointByVal().y());
        s.pointByVal().x(1).y(2);
        Assert.assertEquals(1, s.pointByVal().x());
        Assert.assertEquals(2, s.pointByVal().y());
        StructTest.Point p = new StructTest.Point().x(20).y(40);
        s.pointByVal(p);
        Assert.assertTrue(((getHandle()) != (getHandle())));
        Assert.assertEquals(20, s.pointByVal().x());
        Assert.assertEquals(40, s.pointByVal().y());
        s.pointByVal().x(200).y(400);
        Assert.assertEquals(200, s.pointByVal().x());
        Assert.assertEquals(400, s.pointByVal().y());
        Assert.assertEquals(20, p.x());
        Assert.assertEquals(40, p.y());
    }

    @Test
    public void testByRefMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertNull(s.pointByRef());
        StructTest.Point p = new StructTest.Point().x(20).y(40);
        s.pointByRef(p);
        Assert.assertEquals(getHandle(), getHandle());
        Assert.assertEquals(20, s.pointByRef().x());
        Assert.assertEquals(40, s.pointByRef().y());
        s.pointByRef().x(200).y(400);
        Assert.assertEquals(200, s.pointByRef().x());
        Assert.assertEquals(400, s.pointByRef().y());
        Assert.assertEquals(200, p.x());
        Assert.assertEquals(400, p.y());
    }

    @Test
    public void testSetterChaining() {
        StructTest.TestStruct s = new StructTest.TestStruct().b(((byte) (123))).l(-8065326438389885320L).c(((char) (9876))).i(305419896);
        Assert.assertEquals(123, s.b());
        Assert.assertEquals(-8065326438389885320L, s.l());
        Assert.assertEquals(9876, s.c());
        Assert.assertEquals(305419896, s.i());
        Assert.assertEquals(123, s.getB());
        Assert.assertEquals(-8065326438389885320L, s.getL());
        Assert.assertEquals(9876, s.getC());
        Assert.assertEquals(305419896, s.getI());
    }

    @Test
    public void testJavaBeanLikeSetters() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        s.setB(((byte) (123)));
        s.setL(-8065326438389885320L);
        s.setC(((char) (9876)));
        s.setI(305419896);
        Assert.assertEquals(123, s.b());
        Assert.assertEquals(-8065326438389885320L, s.l());
        Assert.assertEquals(9876, s.c());
        Assert.assertEquals(305419896, s.i());
        Assert.assertEquals(123, s.getB());
        Assert.assertEquals(-8065326438389885320L, s.getL());
        Assert.assertEquals(9876, s.getC());
        Assert.assertEquals(305419896, s.getI());
    }

    @Test
    public void testPtrMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertNull(s.pointPtr());
        StructTest.PointPtr ptr = new StructTest.PointPtr();
        s.pointPtr(ptr);
        Assert.assertEquals(ptr, s.pointPtr());
        StructTest.Point p = new StructTest.Point().x(10).y(20);
        ptr.set(p);
        Assert.assertEquals(p, get());
        s.pointPtr().set(((StructTest.Point) (null)));
        Assert.assertNull(get());
    }

    @Test
    public void testPtrPtrMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertNull(s.pointPtrPtr());
        StructTest.PointPtrPtr ptrOuter = new StructTest.PointPtrPtr();
        s.pointPtrPtr(ptrOuter);
        Assert.assertEquals(ptrOuter, s.pointPtrPtr());
        Assert.assertNull(get());
        StructTest.PointPtr ptrInner = new StructTest.PointPtr();
        ptrOuter.set(ptrInner);
        Assert.assertEquals(ptrInner, get());
        StructTest.Point p = new StructTest.Point().x(10).y(20);
        ptrInner.set(p);
        Assert.assertEquals(p, get());
        get().set(((StructTest.Point) (null)));
        Assert.assertNull(get());
        s.pointPtrPtr().set(((StructTest.PointPtr) (null)));
        Assert.assertNull(get());
    }

    @Test
    public void testRecursiveMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertNull(s.recursive());
        s.recursive(s);
        Assert.assertEquals(s, s.recursive());
    }

    @Test
    public void testSimpleEnumMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertEquals(StructTest.SimpleEnum.V1, s.simpleEnum());
        s.simpleEnum(StructTest.SimpleEnum.V2);
        Assert.assertEquals(StructTest.SimpleEnum.V2, s.simpleEnum());
        s.simpleEnum(StructTest.SimpleEnum.V3);
        Assert.assertEquals(StructTest.SimpleEnum.V3, s.simpleEnum());
        s.simpleEnum(StructTest.SimpleEnum.V1);
        Assert.assertEquals(StructTest.SimpleEnum.V1, s.simpleEnum());
    }

    @Test
    public void testValuedEnumMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        try {
            // No constant with value 0
            s.valuedEnum();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        s.valuedEnum(StructTest.TestValuedEnum.V100);
        Assert.assertEquals(StructTest.TestValuedEnum.V100, s.valuedEnum());
        s.valuedEnum(StructTest.TestValuedEnum.V1000);
        Assert.assertEquals(StructTest.TestValuedEnum.V1000, s.valuedEnum());
        s.valuedEnum(StructTest.TestValuedEnum.V10000);
        Assert.assertEquals(StructTest.TestValuedEnum.V10000, s.valuedEnum());
    }

    @Test
    public void testBitsMember() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertEquals(0, value());
        s.bits(StructTest.TestBits.V1);
        Assert.assertEquals(StructTest.TestBits.V1, s.bits());
        s.bits(set(StructTest.TestBits.V4));
        Assert.assertEquals((1 | 4), value());
    }

    @Test
    public void testBytePtrMemberMarshaledAsString() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertEquals(null, s.string());
        s.string("Foo bar");
        Assert.assertEquals("Foo bar", s.string());
    }

    @Test
    public void testSimpleUnion() {
        Assert.assertEquals(8, sizeOf());
        StructTest.TestUnion s = new StructTest.TestUnion();
        s.d(Math.PI);
        Assert.assertEquals(Math.PI, s.d(), 1.0E-4);
        Assert.assertEquals(Double.doubleToLongBits(Math.PI), s.l());
        Assert.assertEquals(((Double.doubleToLongBits(Math.PI)) & 255), s.b());
        Assert.assertEquals(((Double.doubleToLongBits(Math.PI)) & 4294967295L), ((s.p().x()) & 4294967295L));
        Assert.assertEquals(((Double.doubleToLongBits(Math.PI)) >>> 32), ((s.p().y()) & 4294967295L));
        s.l(1311768467463790320L);
        Assert.assertEquals(Double.longBitsToDouble(1311768467463790320L), s.d(), 1.0E-4);
        Assert.assertEquals(1311768467463790320L, s.l());
        Assert.assertEquals(240, ((s.b()) & 255L));
        Assert.assertEquals(2596069104L, ((s.p().x()) & 4294967295L));
        Assert.assertEquals(305419896L, ((s.p().y()) & 4294967295L));
        s.b(((byte) (228)));
        Assert.assertEquals(Double.longBitsToDouble(1311768467463790308L), s.d(), 1.0E-4);
        Assert.assertEquals(1311768467463790308L, s.l());
        Assert.assertEquals(228, ((s.b()) & 255L));
        Assert.assertEquals(2596069092L, ((s.p().x()) & 4294967295L));
        Assert.assertEquals(305419896L, ((s.p().y()) & 4294967295L));
        s.p().x(268443648).y(805322752);
        Assert.assertEquals(Double.longBitsToDouble(3458834882833162240L), s.d(), 1.0E-4);
        Assert.assertEquals("6", 3458834882833162240L, s.l());
        Assert.assertEquals(0, ((s.b()) & 255L));
        Assert.assertEquals("7", 268443648L, ((s.p().x()) & 4294967295L));
        Assert.assertEquals("8", 805322752L, ((s.p().y()) & 4294967295L));
    }

    @Test
    public void testMixedStructUnion() {
        Assert.assertEquals((IS_64BIT ? 24 : 16), sizeOf());
        StructTest.MixedStructUnion s = new StructTest.MixedStructUnion();
        Assert.assertEquals(0, s.b1());
        s.b1(((byte) (52)));
        Assert.assertEquals(52, s.b1());
        Assert.assertEquals(52, VM.getByte(getHandle()));
        Assert.assertEquals(0, s.l());
        s.l(1311768467294899695L);
        Assert.assertEquals(52, s.b1());
        Assert.assertEquals(1311768467294899695L, s.l());
        Assert.assertEquals(1311768467294899695L, VM.getLong(((getHandle()) + (IS_64BIT ? 8 : 4))));
        Assert.assertEquals(Double.longBitsToDouble(1311768467294899695L), s.d(), 1.0E-5);
        s.d(Math.PI);
        Assert.assertEquals(52, s.b1());
        Assert.assertEquals(Double.doubleToLongBits(Math.PI), s.l());
        Assert.assertEquals(Math.PI, VM.getDouble(((getHandle()) + (IS_64BIT ? 8 : 4))), 1.0E-5);
        Assert.assertEquals(0, s.b2());
        s.b2(((byte) (67)));
        Assert.assertEquals(67, s.b2());
        Assert.assertEquals(67, VM.getByte(((getHandle()) + (IS_64BIT ? 16 : 12))));
    }

    @Test
    public void testUnionByVal() {
        StructTest.TestStruct s = new StructTest.TestStruct();
        StructTest.TestUnion u = s.unionByVal();
        s.unionByValAsLong(1311768467294899695L);
        Assert.assertEquals(1311768467294899695L, s.unionByValAsLong());
        Assert.assertEquals(1311768467294899695L, u.l());
        u.l(-81986143110479071L);
        Assert.assertEquals(-81986143110479071L, s.unionByValAsLong());
        Assert.assertEquals(-81986143110479071L, u.l());
        u.d(Math.PI);
        Assert.assertEquals(Double.doubleToLongBits(Math.PI), s.unionByValAsLong());
        Assert.assertEquals(Math.PI, u.d(), 1.0E-5);
        StructTest.TestUnion v = new StructTest.TestUnion();
        v.l(7165932427379025734L);
        s.unionByVal(v);
        Assert.assertEquals(7165932427379025734L, s.unionByValAsLong());
        Assert.assertEquals(7165932427379025734L, u.l());
    }

    @Test
    public void testStructWithArrayByteArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        BytePtr q;
        BytePtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((byte) (i + 1)));
        }
        q = s.byteArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get());
        }
        r = Struct.allocate(BytePtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set(((byte) (2 * (i + 1))));
        }
        s.byteArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 255));
        }
    }

    @Test
    public void testStructWithArrayByteArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        ByteBuffer b1;
        ByteBuffer b2;
        ByteBuffer b3;
        ByteBuffer b4;
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((byte) (i + 1)));
        }
        b1 = s.byteArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i));
        }
        b2 = ByteBuffer.allocateDirect(D1);
        for (int i = 0; i < D1; i++) {
            b2.put(i, ((byte) (2 * (i + 1))));
        }
        s.byteArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 255));
        }
        b3 = ByteBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, ((byte) (3 * (i + 1))));
        }
        s.byteArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), ((get()) & 255));
        }
        b4 = ByteBuffer.allocate(D1);
        Assert.assertFalse(b4.isDirect());
        for (int i = 0; i < D1; i++) {
            b4.put(i, ((byte) (4 * (i + 1))));
        }
        s.byteArrayAsBuffer(((ByteBuffer) (b4.asReadOnlyBuffer().flip())));
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((4 * (i + 1)), ((get()) & 255));
        }
        try {
            s.byteArrayAsBuffer(ByteBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayByteArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        byte[] array1;
        byte[] array2;
        byte[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        array1 = s.byteArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i]);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((byte) (i + 1)));
        }
        array2 = s.byteArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i]);
        }
        array3 = new byte[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = ((byte) (2 * (i + 1)));
        }
        s.byteArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 255));
        }
        try {
            s.byteArray1D(new byte[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayByteArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        byte[][] array1;
        byte[][] array2;
        byte[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        array1 = s.byteArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j]);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set(((byte) (i + 1)));
        }
        array2 = s.byteArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j]);
            }
        }
        array3 = new byte[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = ((byte) (2 * (((i * D2) + j) + 1)));
            }
        }
        s.byteArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 255));
        }
        try {
            s.byteArray2D(new byte[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.byteArray2D(new byte[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayByteArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        byte[][][] array1;
        byte[][][] array2;
        byte[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        array1 = s.byteArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k]);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set(((byte) (i + 1)));
        }
        array2 = s.byteArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k]);
                }
            }
        }
        array3 = new byte[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = ((byte) (2 * (((((i * D2) + j) * D3) + k) + 1)));
                }
            }
        }
        s.byteArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 255));
        }
        try {
            s.byteArray3D(new byte[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.byteArray3D(new byte[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.byteArray3D(new byte[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayShortArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        ShortPtr p = s.shortArrayAsPtr();
        ShortPtr q;
        ShortPtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((short) (i + 1)));
        }
        q = s.shortArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get());
        }
        r = Struct.allocate(ShortPtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set(((short) (2 * (i + 1))));
        }
        s.shortArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
    }

    @Test
    public void testStructWithArrayShortArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        ShortPtr p = s.shortArrayAsPtr();
        ShortBuffer b1;
        ShortBuffer b2;
        ShortBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((short) (i + 1)));
        }
        b1 = s.shortArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i));
        }
        b2 = ByteBuffer.allocateDirect((D1 * 2)).order(ByteOrder.nativeOrder()).asShortBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, ((short) (2 * (i + 1))));
        }
        s.shortArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        b3 = ShortBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, ((short) (3 * (i + 1))));
        }
        s.shortArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.shortArrayAsBuffer(ShortBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayShortArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        short[] array1;
        short[] array2;
        short[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        ShortPtr p = s.shortArrayAsPtr();
        array1 = s.shortArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i]);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((short) (i + 1)));
        }
        array2 = s.shortArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i]);
        }
        array3 = new short[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = ((short) (2 * (i + 1)));
        }
        s.shortArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.shortArray1D(new short[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayShortArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        short[][] array1;
        short[][] array2;
        short[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        ShortPtr p = s.shortArrayAsPtr();
        array1 = s.shortArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j]);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set(((short) (i + 1)));
        }
        array2 = s.shortArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j]);
            }
        }
        array3 = new short[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = ((short) (2 * (((i * D2) + j) + 1)));
            }
        }
        s.shortArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.shortArray2D(new short[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.shortArray2D(new short[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayShortArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        short[][][] array1;
        short[][][] array2;
        short[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        ShortPtr p = s.shortArrayAsPtr();
        array1 = s.shortArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k]);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set(((short) (i + 1)));
        }
        array2 = s.shortArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k]);
                }
            }
        }
        array3 = new short[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = ((short) (2 * (((((i * D2) + j) * D3) + k) + 1)));
                }
            }
        }
        s.shortArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.shortArray3D(new short[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.shortArray3D(new short[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.shortArray3D(new short[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayCharArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        CharPtr p = s.charArrayAsPtr();
        CharPtr q;
        CharPtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((char) (i + 1)));
        }
        q = s.charArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get());
        }
        r = Struct.allocate(CharPtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set(((char) (2 * (i + 1))));
        }
        s.charArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
    }

    @Test
    public void testStructWithArrayCharArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        CharPtr p = s.charArrayAsPtr();
        CharBuffer b1;
        CharBuffer b2;
        CharBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((char) (i + 1)));
        }
        b1 = s.charArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i));
        }
        b2 = ByteBuffer.allocateDirect((D1 * 2)).order(ByteOrder.nativeOrder()).asCharBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, ((char) (2 * (i + 1))));
        }
        s.charArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        b3 = CharBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, ((char) (3 * (i + 1))));
        }
        s.charArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.charArrayAsBuffer(CharBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayCharArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        char[] array1;
        char[] array2;
        char[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        CharPtr p = s.charArrayAsPtr();
        array1 = s.charArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i]);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set(((char) (i + 1)));
        }
        array2 = s.charArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i]);
        }
        array3 = new char[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = ((char) (2 * (i + 1)));
        }
        s.charArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.charArray1D(new char[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayCharArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        char[][] array1;
        char[][] array2;
        char[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        CharPtr p = s.charArrayAsPtr();
        array1 = s.charArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j]);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set(((char) (i + 1)));
        }
        array2 = s.charArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j]);
            }
        }
        array3 = new char[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = ((char) (2 * (((i * D2) + j) + 1)));
            }
        }
        s.charArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.charArray2D(new char[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.charArray2D(new char[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayCharArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        char[][][] array1;
        char[][][] array2;
        char[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        CharPtr p = s.charArrayAsPtr();
        array1 = s.charArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k]);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set(((char) (i + 1)));
        }
        array2 = s.charArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k]);
                }
            }
        }
        array3 = new char[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = ((char) (2 * (((((i * D2) + j) * D3) + k) + 1)));
                }
            }
        }
        s.charArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), ((get()) & 65535));
        }
        try {
            s.charArray3D(new char[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.charArray3D(new char[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.charArray3D(new char[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayIntArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        IntPtr p = s.intArrayAsPtr();
        IntPtr q;
        IntPtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        q = s.intArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get());
        }
        r = Struct.allocate(IntPtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set((2 * (i + 1)));
        }
        s.intArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
    }

    @Test
    public void testStructWithArrayIntArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        IntPtr p = s.intArrayAsPtr();
        IntBuffer b1;
        IntBuffer b2;
        IntBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        b1 = s.intArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i));
        }
        b2 = ByteBuffer.allocateDirect((D1 * 4)).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, (2 * (i + 1)));
        }
        s.intArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        b3 = IntBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, (3 * (i + 1)));
        }
        s.intArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), get());
        }
        try {
            s.intArrayAsBuffer(IntBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayIntArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        int[] array1;
        int[] array2;
        int[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        IntPtr p = s.intArrayAsPtr();
        array1 = s.intArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i]);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.intArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i]);
        }
        array3 = new int[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = 2 * (i + 1);
        }
        s.intArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.intArray1D(new int[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayIntArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        int[][] array1;
        int[][] array2;
        int[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        IntPtr p = s.intArrayAsPtr();
        array1 = s.intArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j]);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.intArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j]);
            }
        }
        array3 = new int[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = 2 * (((i * D2) + j) + 1);
            }
        }
        s.intArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.intArray2D(new int[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.intArray2D(new int[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayIntArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        int[][][] array1;
        int[][][] array2;
        int[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        IntPtr p = s.intArrayAsPtr();
        array1 = s.intArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k]);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.intArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k]);
                }
            }
        }
        array3 = new int[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = 2 * (((((i * D2) + j) * D3) + k) + 1);
                }
            }
        }
        s.intArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.intArray3D(new int[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.intArray3D(new int[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.intArray3D(new int[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayLongArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        LongPtr p = s.longArrayAsPtr();
        LongPtr q;
        LongPtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        q = s.longArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get());
        }
        r = Struct.allocate(LongPtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set((2 * (i + 1)));
        }
        s.longArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
    }

    @Test
    public void testStructWithArrayLongArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        LongPtr p = s.longArrayAsPtr();
        LongBuffer b1;
        LongBuffer b2;
        LongBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        b1 = s.longArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i));
        }
        b2 = ByteBuffer.allocateDirect((D1 * 8)).order(ByteOrder.nativeOrder()).asLongBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, (2 * (i + 1)));
        }
        s.longArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        b3 = LongBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, (3 * (i + 1)));
        }
        s.longArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), get());
        }
        try {
            s.longArrayAsBuffer(LongBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayLongArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        long[] array1;
        long[] array2;
        long[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        LongPtr p = s.longArrayAsPtr();
        array1 = s.longArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i]);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.longArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i]);
        }
        array3 = new long[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = 2 * (i + 1);
        }
        s.longArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.longArray1D(new long[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayLongArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        long[][] array1;
        long[][] array2;
        long[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        LongPtr p = s.longArrayAsPtr();
        array1 = s.longArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j]);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.longArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j]);
            }
        }
        array3 = new long[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = 2 * (((i * D2) + j) + 1);
            }
        }
        s.longArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.longArray2D(new long[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.longArray2D(new long[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayLongArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        long[][][] array1;
        long[][][] array2;
        long[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        LongPtr p = s.longArrayAsPtr();
        array1 = s.longArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k]);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.longArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k]);
                }
            }
        }
        array3 = new long[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = 2 * (((((i * D2) + j) * D3) + k) + 1);
                }
            }
        }
        s.longArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), get());
        }
        try {
            s.longArray3D(new long[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.longArray3D(new long[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.longArray3D(new long[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayFloatArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        FloatPtr p = s.floatArrayAsPtr();
        FloatPtr q;
        FloatPtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        q = s.floatArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get(), 1.0E-4F);
        }
        r = Struct.allocate(FloatPtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set((2 * (i + 1)));
        }
        s.floatArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4F);
        }
    }

    @Test
    public void testStructWithArrayFloatArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        FloatPtr p = s.floatArrayAsPtr();
        FloatBuffer b1;
        FloatBuffer b2;
        FloatBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        b1 = s.floatArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i), 1.0E-4F);
        }
        b2 = ByteBuffer.allocateDirect((D1 * 4)).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, (2 * (i + 1)));
        }
        s.floatArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4F);
        }
        b3 = FloatBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, (3 * (i + 1)));
        }
        s.floatArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), get(), 1.0E-4F);
        }
        try {
            s.floatArrayAsBuffer(FloatBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayFloatArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        float[] array1;
        float[] array2;
        float[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        FloatPtr p = s.floatArrayAsPtr();
        array1 = s.floatArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i], 1.0E-4F);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.floatArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i], 1.0E-4F);
        }
        array3 = new float[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = 2 * (i + 1);
        }
        s.floatArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4F);
        }
        try {
            s.floatArray1D(new float[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayFloatArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        float[][] array1;
        float[][] array2;
        float[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        FloatPtr p = s.floatArrayAsPtr();
        array1 = s.floatArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j], 1.0E-4F);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.floatArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j], 1.0E-4F);
            }
        }
        array3 = new float[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = 2 * (((i * D2) + j) + 1);
            }
        }
        s.floatArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4F);
        }
        try {
            s.floatArray2D(new float[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.floatArray2D(new float[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayFloatArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        float[][][] array1;
        float[][][] array2;
        float[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        FloatPtr p = s.floatArrayAsPtr();
        array1 = s.floatArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k], 1.0E-4F);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.floatArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k], 1.0E-4F);
                }
            }
        }
        array3 = new float[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = 2 * (((((i * D2) + j) * D3) + k) + 1);
                }
            }
        }
        s.floatArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4F);
        }
        try {
            s.floatArray3D(new float[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.floatArray3D(new float[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.floatArray3D(new float[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayDoubleArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        DoublePtr p = s.doubleArrayAsPtr();
        DoublePtr q;
        DoublePtr r;
        Assert.assertEquals(getHandle(), p.getHandle());
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        q = s.doubleArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), get(), 1.0E-4);
        }
        r = Struct.allocate(DoublePtr.class, D1);
        Assert.assertNotEquals(getHandle(), r.getHandle());
        for (int i = 0; i < D1; i++) {
            r.next(i).set((2 * (i + 1)));
        }
        s.doubleArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4);
        }
    }

    @Test
    public void testStructWithArrayDoubleArrayAsBuffer() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        DoublePtr p = s.doubleArrayAsPtr();
        DoubleBuffer b1;
        DoubleBuffer b2;
        DoubleBuffer b3;
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        b1 = s.doubleArrayAsBuffer();
        Assert.assertEquals(D1, b1.capacity());
        Assert.assertEquals(D1, b1.limit());
        Assert.assertEquals(0, b1.position());
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((i + 1), b1.get(i), 1.0E-4);
        }
        b2 = ByteBuffer.allocateDirect((D1 * 8)).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        for (int i = 0; i < D1; i++) {
            b2.put(i, (2 * (i + 1)));
        }
        s.doubleArrayAsBuffer(b2);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4);
        }
        b3 = DoubleBuffer.allocate(D1);
        Assert.assertFalse(b3.isDirect());
        for (int i = 0; i < D1; i++) {
            b3.put(i, (3 * (i + 1)));
        }
        s.doubleArrayAsBuffer(b3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((3 * (i + 1)), get(), 1.0E-4);
        }
        try {
            s.doubleArrayAsBuffer(DoubleBuffer.allocate((D1 / 2)));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayDoubleArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        double[] array1;
        double[] array2;
        double[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        DoublePtr p = s.doubleArrayAsPtr();
        array1 = s.doubleArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i], 1.0E-4);
        }
        for (int i = 0; i < D1; i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.doubleArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((i + 1), array2[i], 1.0E-4);
        }
        array3 = new double[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = 2 * (i + 1);
        }
        s.doubleArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4);
        }
        try {
            s.doubleArray1D(new double[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayDoubleArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        double[][] array1;
        double[][] array2;
        double[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        DoublePtr p = s.doubleArrayAsPtr();
        array1 = s.doubleArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j], 1.0E-4);
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.doubleArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((((i * D2) + j) + 1), array2[i][j], 1.0E-4);
            }
        }
        array3 = new double[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = 2 * (((i * D2) + j) + 1);
            }
        }
        s.doubleArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4);
        }
        try {
            s.doubleArray2D(new double[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.doubleArray2D(new double[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayDoubleArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        double[][][] array1;
        double[][][] array2;
        double[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        DoublePtr p = s.doubleArrayAsPtr();
        array1 = s.doubleArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k], 1.0E-4);
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            p.next(i).set((i + 1));
        }
        array2 = s.doubleArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((((((i * D2) + j) * D3) + k) + 1), array2[i][j][k], 1.0E-4);
                }
            }
        }
        array3 = new double[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = 2 * (((((i * D2) + j) * D3) + k) + 1);
                }
            }
        }
        s.doubleArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals((2 * (i + 1)), get(), 1.0E-4);
        }
        try {
            s.doubleArray3D(new double[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.doubleArray3D(new double[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.doubleArray3D(new double[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayPointArrayAsPtr() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        StructTest.Point p = s.pointArrayAsPtr();
        StructTest.Point q;
        StructTest.Point r;
        Assert.assertEquals(getHandle(), getHandle());
        for (int i = 0; i < D1; i++) {
            next(i).x((100 * i)).y(((-100) * i));
        }
        q = s.pointArrayAsPtr();
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals((100 * i), next(i).x());
            Assert.assertEquals(((-100) * i), next(i).y());
        }
        r = Struct.allocate(StructTest.Point.class, D1);
        Assert.assertNotEquals(getHandle(), getHandle());
        for (int i = 0; i < D1; i++) {
            next(i).x(((-1000) * i)).y((1000 * i));
        }
        s.pointArrayAsPtr(r);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals(((-1000) * i), next(i).x());
            Assert.assertEquals((1000 * i), next(i).y());
        }
    }

    @Test
    public void testStructWithArrayPointArrayAs1D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.Point[] array1;
        StructTest.Point[] array2;
        StructTest.Point[] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        StructTest.Point p = s.pointArrayAsPtr();
        array1 = s.pointArray1D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(0, array1[i].x());
            Assert.assertEquals(0, array1[i].y());
        }
        for (int i = 0; i < D1; i++) {
            next(i).x((100 * i)).y(((-100) * i));
        }
        array2 = s.pointArray1D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals((100 * i), array2[i].x());
            Assert.assertEquals(((-100) * i), array2[i].y());
        }
        array3 = new StructTest.Point[D1];
        for (int i = 0; i < (array3.length); i++) {
            array3[i] = new StructTest.Point().x(((-1000) * i)).y((1000 * i));
        }
        s.pointArray1D(array3);
        for (int i = 0; i < D1; i++) {
            Assert.assertEquals(((-1000) * i), next(i).x());
            Assert.assertEquals((1000 * i), next(i).y());
        }
        try {
            s.pointArray1D(new StructTest.Point[D1 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayPointArrayAs2D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 3;
        final int D2 = 8;
        StructTest.Point[][] array1;
        StructTest.Point[][] array2;
        StructTest.Point[][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        StructTest.Point p = s.pointArrayAsPtr();
        array1 = s.pointArray2D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(0, array1[i][j].x());
                Assert.assertEquals(0, array1[i][j].y());
            }
        }
        for (int i = 0; i < (D1 * D2); i++) {
            next(i).x((100 * i)).y(((-100) * i));
        }
        array2 = s.pointArray2D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals((100 * ((i * D2) + j)), array2[i][j].x());
                Assert.assertEquals(((-100) * ((i * D2) + j)), array2[i][j].y());
            }
        }
        array3 = new StructTest.Point[D1][D2];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                array3[i][j] = new StructTest.Point().x(((-1000) * ((i * D2) + j))).y((1000 * ((i * D2) + j)));
            }
        }
        s.pointArray2D(array3);
        for (int i = 0; i < (D1 * D2); i++) {
            Assert.assertEquals(((-1000) * i), next(i).x());
            Assert.assertEquals((1000 * i), next(i).y());
        }
        try {
            s.pointArray2D(new StructTest.Point[D1 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.pointArray2D(new StructTest.Point[D1][D2 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayPointArrayAs3D() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 2;
        final int D2 = 3;
        final int D3 = 4;
        StructTest.Point[][][] array1;
        StructTest.Point[][][] array2;
        StructTest.Point[][][] array3;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        StructTest.Point p = s.pointArrayAsPtr();
        array1 = s.pointArray3D();
        Assert.assertEquals(D1, array1.length);
        for (int i = 0; i < (array1.length); i++) {
            Assert.assertEquals(D2, array1[i].length);
            for (int j = 0; j < (array1[i].length); j++) {
                Assert.assertEquals(D3, array1[i][j].length);
                for (int k = 0; k < (array1[i][j].length); k++) {
                    Assert.assertEquals(0, array1[i][j][k].x());
                    Assert.assertEquals(0, array1[i][j][k].y());
                }
            }
        }
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            next(i).x((100 * i)).y(((-100) * i));
        }
        array2 = s.pointArray3D();
        Assert.assertEquals(D1, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertEquals(D2, array2[i].length);
            for (int j = 0; j < (array2[i].length); j++) {
                Assert.assertEquals(D3, array2[i][j].length);
                for (int k = 0; k < (array2[i][j].length); k++) {
                    Assert.assertEquals((100 * ((((i * D2) + j) * D3) + k)), array2[i][j][k].x());
                    Assert.assertEquals(((-100) * ((((i * D2) + j) * D3) + k)), array2[i][j][k].y());
                }
            }
        }
        array3 = new StructTest.Point[D1][D2][D3];
        for (int i = 0; i < (array3.length); i++) {
            for (int j = 0; j < (array3[i].length); j++) {
                for (int k = 0; k < (array3[i][j].length); k++) {
                    array3[i][j][k] = new StructTest.Point().x(((-1000) * ((((i * D2) + j) * D3) + k))).y((1000 * ((((i * D2) + j) * D3) + k)));
                }
            }
        }
        s.pointArray3D(array3);
        for (int i = 0; i < ((D1 * D2) * D3); i++) {
            Assert.assertEquals(((-1000) * i), next(i).x());
            Assert.assertEquals((1000 * i), next(i).y());
        }
        try {
            s.pointArray3D(new StructTest.Point[D1 / 2][][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.pointArray3D(new StructTest.Point[D1][D2 / 2][]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            s.pointArray3D(new StructTest.Point[D1][D2][D3 / 2]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testStructWithArrayByteArrayAsString() {
        Assert.assertEquals(192, sizeOf());
        final int D1 = 24;
        StructTest.StructWithArray s = new StructTest.StructWithArray();
        BytePtr p = s.byteArrayAsPtr();
        Assert.assertEquals("", s.byteArrayAsString());
        p.set(((byte) ('a')));
        p.next(1).set(((byte) ('b')));
        p.next(2).set(((byte) ('c')));
        Assert.assertEquals("abc", s.byteArrayAsString());
        p.next(2).set(((byte) (0)));
        Assert.assertEquals("ab", s.byteArrayAsString());
        s.byteArrayAsString("foo bar");
        Assert.assertEquals("foo bar", p.toStringAsciiZ());
        s.byteArrayAsString("foo");
        Assert.assertEquals("foo", p.toStringAsciiZ());
        // Note: This assumes that the byte right after the byte array is 0. It should be.
        s.byteArrayAsString("012345678901234567890123456789");
        Assert.assertEquals("012345678901234567890123", s.byteArrayAsString());
        s.byteArrayAsPtr(BytePtr.toBytePtrAsciiZ("012345678901234567890123456789"));
        Assert.assertEquals("012345678901234567890123", s.byteArrayAsString());
        p.clear(D1);
        Assert.assertEquals("", s.byteArrayAsString());
    }

    @Test
    public void testMarshalerCallSequence() {
        StructTest.StringMarshaler.calls = new ArrayList<String>();
        StructTest.TestStruct s = new StructTest.TestStruct();
        Assert.assertNull(s.string());
        s.string("foobar");
        Assert.assertEquals("foobar", s.string());
        Assert.assertEquals(3, StructTest.StringMarshaler.calls.size());
        Assert.assertEquals("toObject(null, ?, 2)", StructTest.StringMarshaler.calls.get(0));
        Assert.assertEquals("toNative('foobar', ?, 2)", StructTest.StringMarshaler.calls.get(1));
        Assert.assertEquals("toObject('foobar', ?, 2)", StructTest.StringMarshaler.calls.get(2));
    }

    float fpi = ((float) (Math.PI));

    @Test
    public void testMarshalMachineSizedFloat() {
        Assert.assertEquals((IS_64BIT ? 8 : 4), sizeOf());
        long ldpi = Double.doubleToLongBits(Math.PI);
        long lfpi = Double.doubleToLongBits(fpi);
        Assert.assertNotEquals(ldpi, lfpi);
        int ifpi = Float.floatToIntBits(fpi);
        StructTest.MachineSizedStruct s = new StructTest.MachineSizedStruct();
        Assert.assertEquals(0.0, s.machineSizedFloatD(), 0);
        s.machineSizedFloatD(Math.PI);
        if (IS_32BIT) {
            Assert.assertEquals("ifpi == VM.getInt(s.getHandle())", ifpi, VM.getInt(getHandle()));
            Assert.assertEquals("fpi == s.machineSizedFloatF()", fpi, s.machineSizedFloatF(), 0.0F);
            Assert.assertEquals("lfpi == Double.doubleToLongBits(s.machineSizedFloatD())", lfpi, Double.doubleToLongBits(s.machineSizedFloatD()));
        } else {
            // 64-bit
            Assert.assertEquals("ldpi == VM.getLong(s.getHandle())", ldpi, VM.getLong(getHandle()));
            Assert.assertEquals("Math.PI == s.machineSizedFloatD()", Math.PI, s.machineSizedFloatD(), 0);
            Assert.assertEquals("ifpi == Float.floatToIntBits(s.machineSizedFloatF())", ifpi, Float.floatToIntBits(s.machineSizedFloatF()));
        }
    }

    @Test
    public void testMachineSizedSInt() throws Exception {
        Assert.assertEquals((IS_64BIT ? 8 : 4), sizeOf());
        StructTest.MachineSizedStruct s = new StructTest.MachineSizedStruct();
        Assert.assertEquals(0, s.machineSizedSInt());
        s.machineSizedSInt((-1));
        Assert.assertEquals((-1), s.machineSizedSInt());
        if (IS_32BIT) {
            s.machineSizedSInt(2147483648L);
            Assert.assertEquals(-2147483648L, VM.getInt(getHandle()));
            Assert.assertEquals(-2147483648L, s.machineSizedSInt());
            s.machineSizedSInt(1311768467015204864L);
            Assert.assertEquals(-2147483648L, VM.getInt(getHandle()));
            Assert.assertEquals(-2147483648L, s.machineSizedSInt());
        } else {
            // 64-bit
            s.machineSizedSInt(2147483648L);
            Assert.assertEquals(2147483648L, VM.getLong(getHandle()));
            Assert.assertEquals(2147483648L, s.machineSizedSInt());
            s.machineSizedSInt(1311768467015204864L);
            Assert.assertEquals(1311768467015204864L, VM.getLong(getHandle()));
            Assert.assertEquals(1311768467015204864L, s.machineSizedSInt());
        }
    }

    @Test
    public void testMachineSizedUInt() throws Exception {
        Assert.assertEquals((IS_64BIT ? 8 : 4), sizeOf());
        StructTest.MachineSizedStruct s = new StructTest.MachineSizedStruct();
        Assert.assertEquals(0, s.machineSizedUInt());
        s.machineSizedUInt((-1));
        if (IS_32BIT) {
            Assert.assertEquals(4294967295L, s.machineSizedUInt());
            s.machineSizedUInt(2147483648L);
            Assert.assertEquals(-2147483648L, VM.getInt(getHandle()));
            Assert.assertEquals(2147483648L, s.machineSizedUInt());
            s.machineSizedUInt(1311768467015204864L);
            Assert.assertEquals(-2147483648L, VM.getInt(getHandle()));
            Assert.assertEquals(2147483648L, s.machineSizedUInt());
        } else {
            // 64-bit
            Assert.assertEquals(-1L, s.machineSizedUInt());
            s.machineSizedUInt(2147483648L);
            Assert.assertEquals(2147483648L, VM.getLong(getHandle()));
            Assert.assertEquals(2147483648L, s.machineSizedUInt());
            s.machineSizedUInt(1311768467015204864L);
            Assert.assertEquals(1311768467015204864L, VM.getLong(getHandle()));
            Assert.assertEquals(1311768467015204864L, s.machineSizedUInt());
        }
    }
}

