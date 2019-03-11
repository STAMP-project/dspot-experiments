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


import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.VM;
import org.robovm.rt.bro.annotation.Bridge;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.Callback;
import org.robovm.rt.bro.annotation.Marshaler;
import org.robovm.rt.bro.annotation.Marshalers;
import org.robovm.rt.bro.annotation.MarshalsPointer;
import org.robovm.rt.bro.annotation.StructMember;
import org.robovm.rt.bro.ptr.BytePtr;
import org.robovm.rt.bro.ptr.LongPtr;
import org.robovm.rt.bro.ptr.Ptr;

import static Bro.IS_32BIT;


/**
 * Tests {@link Bridge} and {@link Callback} methods.
 */
public class BridgeCallbackTest {
    public static final class Point extends Struct<BridgeCallbackTest.Point> {
        @StructMember(0)
        public native int x();

        @StructMember(1)
        public native int y();

        @StructMember(0)
        public native BridgeCallbackTest.Point x(int x);

        @StructMember(1)
        public native BridgeCallbackTest.Point y(int y);
    }

    public static final class PointPtr extends Ptr<BridgeCallbackTest.Point, BridgeCallbackTest.PointPtr> {}

    public static final class PointPtrPtr extends Ptr<BridgeCallbackTest.PointPtr, BridgeCallbackTest.PointPtrPtr> {}

    public static final class Points extends Struct<BridgeCallbackTest.Points> {
        @StructMember(0)
        @ByVal
        public native BridgeCallbackTest.Point p1();

        @StructMember(0)
        public native BridgeCallbackTest.Points p1(@ByVal
        BridgeCallbackTest.Point p1);

        @StructMember(1)
        @ByVal
        public native BridgeCallbackTest.Point p2();

        @StructMember(1)
        public native BridgeCallbackTest.Points p2(@ByVal
        BridgeCallbackTest.Point p2);

        @StructMember(2)
        @ByVal
        public native BridgeCallbackTest.Point p3();

        @StructMember(2)
        public native BridgeCallbackTest.Points p3(@ByVal
        BridgeCallbackTest.Point p3);

        @StructMember(3)
        @ByVal
        public native BridgeCallbackTest.Point p4();

        @StructMember(3)
        public native BridgeCallbackTest.Points p4(@ByVal
        BridgeCallbackTest.Point p4);
    }

    public enum SimpleEnum {

        V1,
        V2,
        V3;}

    public enum TestValuedEnum implements ValuedEnum {

        VM1((-1)),
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

    public static final class TestBits extends Bits<BridgeCallbackTest.TestBits> {
        public static final BridgeCallbackTest.TestBits V1 = new BridgeCallbackTest.TestBits(1);

        public static final BridgeCallbackTest.TestBits V2 = new BridgeCallbackTest.TestBits(2);

        public static final BridgeCallbackTest.TestBits V4 = new BridgeCallbackTest.TestBits(4);

        public static final BridgeCallbackTest.TestBits V8 = new BridgeCallbackTest.TestBits(8);

        private static final BridgeCallbackTest.TestBits[] VALUES = _values(BridgeCallbackTest.TestBits.class);

        private TestBits(long value) {
            super(value);
        }

        private TestBits(long value, long mask) {
            super(value, mask);
        }

        @Override
        protected BridgeCallbackTest.TestBits wrap(long value, long mask) {
            return new BridgeCallbackTest.TestBits(value, mask);
        }

        @Override
        protected BridgeCallbackTest.TestBits[] _values() {
            return BridgeCallbackTest.TestBits.VALUES;
        }
    }

    public static final class MoreTestBits extends Bits<BridgeCallbackTest.MoreTestBits> {
        public static final BridgeCallbackTest.MoreTestBits V1 = new BridgeCallbackTest.MoreTestBits((-1));

        public static final BridgeCallbackTest.MoreTestBits V2 = new BridgeCallbackTest.MoreTestBits(2147483648L);

        public static final BridgeCallbackTest.MoreTestBits V3 = new BridgeCallbackTest.MoreTestBits(1311768467015204864L);

        private static final BridgeCallbackTest.MoreTestBits[] VALUES = _values(BridgeCallbackTest.MoreTestBits.class);

        private MoreTestBits(long value) {
            super(value);
        }

        private MoreTestBits(long value, long mask) {
            super(value, mask);
        }

        @Override
        protected BridgeCallbackTest.MoreTestBits wrap(long value, long mask) {
            return new BridgeCallbackTest.MoreTestBits(value, mask);
        }

        @Override
        protected BridgeCallbackTest.MoreTestBits[] _values() {
            return BridgeCallbackTest.MoreTestBits.VALUES;
        }
    }

    // @AfterBridgeCall
    // public static void afterToNative(String s, long handle, long flags) {
    // calls.add("afterToNative(" + s + ", ?, " + Long.toHexString(flags) + ")");
    // }
    // @AfterCallbackCall
    // public static void afterToObject(long handle, String s, long flags) {
    // calls.add("afterToObject(?, " + s + ", " + Long.toHexString(flags) + ")");
    // }
    public static class StringMarshaler {
        static List<String> calls = new ArrayList<String>();

        @MarshalsPointer
        public static String toObject(Class<?> cls, long handle, long flags) {
            BytePtr ptr = Struct.toStruct(BytePtr.class, handle);
            String s = ptr.toStringAsciiZ();
            BridgeCallbackTest.StringMarshaler.calls.add((((("toObject(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
            return s;
        }

        @MarshalsPointer
        public static long toNative(String s, long flags) {
            BridgeCallbackTest.StringMarshaler.calls.add((((("toNative(" + s) + ", ?, ") + (Long.toHexString(flags))) + ")"));
            BytePtr ptr = BytePtr.toBytePtrAsciiZ(((String) (s)));
            return ptr.getHandle();
        }
    }

    @Marshaler(BridgeCallbackTest.StringMarshaler.class)
    public static class Inner1 {
        @Bridge
        public static native String append(String a, String b);

        @Callback
        public static String append_cb(String a, String b) {
            return a + b;
        }
    }

    @Marshalers(@Marshaler(BridgeCallbackTest.StringMarshaler.class))
    public static class Inner2 {
        @Bridge
        public static native String append(String a, String b);

        @Callback
        public static String append_cb(String a, String b) {
            return a + b;
        }
    }

    @Marshaler(BridgeCallbackTest.StringMarshaler.class)
    public static class Inner3 {
        public static class Inner4 {
            @Bridge
            public static native String append(String a, String b);

            @Callback
            public static String append_cb(String a, String b) {
                return a + b;
            }
        }
    }

    // This must be small enough to always be returned in registers on all supported platforms
    public static class SmallStruct extends Struct<BridgeCallbackTest.SmallStruct> {
        @StructMember(0)
        public native byte v1();

        @StructMember(0)
        public native BridgeCallbackTest.SmallStruct v1(byte v1);

        @StructMember(1)
        public native byte v2();

        @StructMember(1)
        public native BridgeCallbackTest.SmallStruct v2(byte v2);
    }

    // This must be large enough to never be returned in registers on all supported platforms
    public static class LargeStruct extends Struct<BridgeCallbackTest.LargeStruct> {
        @StructMember(0)
        public native long v1();

        @StructMember(0)
        public native BridgeCallbackTest.LargeStruct v1(long v1);

        @StructMember(1)
        public native long v2();

        @StructMember(1)
        public native BridgeCallbackTest.LargeStruct v2(long v2);

        @StructMember(2)
        public native long v3();

        @StructMember(2)
        public native BridgeCallbackTest.LargeStruct v3(long v3);

        @StructMember(3)
        public native long v4();

        @StructMember(3)
        public native BridgeCallbackTest.LargeStruct v4(long v4);
    }

    public static class NativeObj extends NativeObject {
        @Bridge
        public native int simpleInstanceMethod(int x, LongPtr l);

        @Callback
        public int simpleInstanceMethod_cb(int x, LongPtr l) {
            l.set(getHandle());
            return x * x;
        }

        @Bridge
        @ByVal
        public native BridgeCallbackTest.SmallStruct returnSmallStructInstanceMethod(@ByVal
        BridgeCallbackTest.SmallStruct s, LongPtr l);

        @Callback
        @ByVal
        public BridgeCallbackTest.SmallStruct returnSmallStructInstanceMethod_cb(@ByVal
        BridgeCallbackTest.SmallStruct s, LongPtr l) {
            l.set(getHandle());
            return s;
        }

        @Bridge
        @ByVal
        public native BridgeCallbackTest.LargeStruct returnLargeStructInstanceMethod(@ByVal
        BridgeCallbackTest.LargeStruct s, LongPtr l);

        @Callback
        @ByVal
        public BridgeCallbackTest.LargeStruct returnLargeStructInstanceMethod_cb(@ByVal
        BridgeCallbackTest.LargeStruct s, LongPtr l) {
            l.set(getHandle());
            return s;
        }
    }

    static {
        BridgeCallbackTest.bind(BridgeCallbackTest.class);
        BridgeCallbackTest.bind(BridgeCallbackTest.Inner1.class);
        BridgeCallbackTest.bind(BridgeCallbackTest.Inner2.class);
        BridgeCallbackTest.bind(BridgeCallbackTest.Inner3.Inner4.class);
        BridgeCallbackTest.bind(BridgeCallbackTest.NativeObj.class);
    }

    @Test
    public void testPrimitiveParameters() {
        Assert.assertEquals(8, BridgeCallbackTest.addInts(5, 3));
    }

    @Test
    public void testMarshalStructParametersAndReturnValue() {
        BridgeCallbackTest.Point p1 = new BridgeCallbackTest.Point().x(1).y(2);
        BridgeCallbackTest.Point p2 = new BridgeCallbackTest.Point().x(3).y(4);
        BridgeCallbackTest.Point sum = BridgeCallbackTest.addPoints(p1, p2);
        Assert.assertEquals(4, sum.x());
        Assert.assertEquals(6, sum.y());
    }

    @Test
    public void testMarshalStructParametersByRef() {
        BridgeCallbackTest.Point p = new BridgeCallbackTest.Point().x(1).y(2);
        BridgeCallbackTest.scalePoint1(p, 5);
        Assert.assertEquals(5, p.x());
        Assert.assertEquals(10, p.y());
    }

    @Test
    public void testMarshalStructParametersByVal() {
        BridgeCallbackTest.Point p = new BridgeCallbackTest.Point().x(1).y(2);
        BridgeCallbackTest.scalePoint2(p, 5);
        Assert.assertEquals(1, p.x());
        Assert.assertEquals(2, p.y());
    }

    @Test
    public void testMarshalStructStructRet() {
        BridgeCallbackTest.Points ps = new BridgeCallbackTest.Points();
        BridgeCallbackTest.createPoints(ps, 1, 2, 3, 4, 5, 6, 7, 8);
        Assert.assertEquals(1, ps.p1().x());
        Assert.assertEquals(2, ps.p1().y());
        Assert.assertEquals(3, ps.p2().x());
        Assert.assertEquals(4, ps.p2().y());
        Assert.assertEquals(5, ps.p3().x());
        Assert.assertEquals(6, ps.p3().y());
        Assert.assertEquals(7, ps.p4().x());
        Assert.assertEquals(8, ps.p4().y());
    }

    @Test
    public void testNullByValParameter() {
        try {
            BridgeCallbackTest.scalePoint2(null, 5);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testNullStructRet() {
        try {
            BridgeCallbackTest.createPoints(null, 1, 2, 3, 4, 5, 6, 7, 8);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testMarshalStructReturnValueByVal() {
        BridgeCallbackTest.Point p1 = new BridgeCallbackTest.Point().x(1).y(2);
        BridgeCallbackTest.Point p2 = BridgeCallbackTest.copyPoint(p1);
        Assert.assertEquals(1, p2.x());
        Assert.assertEquals(2, p2.y());
        Assert.assertFalse(p1.equals(p2));
    }

    @Test
    public void testMarshalStructPtr() {
        BridgeCallbackTest.PointPtr ptr = new BridgeCallbackTest.PointPtr();
        Assert.assertNull(get());
        BridgeCallbackTest.createPoint(10, 20, ptr);
        BridgeCallbackTest.Point p = ptr.get();
        Assert.assertNotNull(p);
        Assert.assertEquals(10, p.x());
        Assert.assertEquals(20, p.y());
    }

    @Test
    public void testMarshalLargeStructParameterByVal() {
        BridgeCallbackTest.Points ps = new BridgeCallbackTest.Points();
        ps.p1(new BridgeCallbackTest.Point().x(1).y(2));
        ps.p2(new BridgeCallbackTest.Point().x(2).y(4));
        ps.p3(new BridgeCallbackTest.Point().x(3).y(6));
        ps.p4(new BridgeCallbackTest.Point().x(4).y(8));
        BridgeCallbackTest.Point sum = BridgeCallbackTest.passLargeStructByVal(ps);
        Assert.assertEquals(1, ps.p1().x());
        Assert.assertEquals(2, ps.p1().y());
        Assert.assertEquals(2, ps.p2().x());
        Assert.assertEquals(4, ps.p2().y());
        Assert.assertEquals(3, ps.p3().x());
        Assert.assertEquals(6, ps.p3().y());
        Assert.assertEquals(4, ps.p4().x());
        Assert.assertEquals(8, ps.p4().y());
        Assert.assertEquals(10, sum.x());
        Assert.assertEquals(20, sum.y());
    }

    @Test
    public void testMarshalLargeStructReturnValueByVal() {
        LongPtr address = new LongPtr();
        BridgeCallbackTest.Points ps = BridgeCallbackTest.returnLargeStructByVal(1, 2, 3, 4, 5, 6, 7, 8, address);
        Assert.assertEquals(1, ps.p1().x());
        Assert.assertEquals(2, ps.p1().y());
        Assert.assertEquals(3, ps.p2().x());
        Assert.assertEquals(4, ps.p2().y());
        Assert.assertEquals(5, ps.p3().x());
        Assert.assertEquals(6, ps.p3().y());
        Assert.assertEquals(7, ps.p4().x());
        Assert.assertEquals(8, ps.p4().y());
        Assert.assertFalse(((address.get()) == (getHandle())));
    }

    @Test
    public void testMarshalNonNativeTypeInlineMarshaler() {
        String s = BridgeCallbackTest.append("foo", "bar");
        Assert.assertEquals("foobar", s);
    }

    @Test
    public void testMarshalNonNativeTypeMarshalerOnClass() {
        String s = BridgeCallbackTest.Inner1.append("foo", "bar");
        Assert.assertEquals("foobar", s);
    }

    @Test
    public void testMarshalNonNativeTypeMarshalersOnClass() {
        String s = BridgeCallbackTest.Inner2.append("foo", "bar");
        Assert.assertEquals("foobar", s);
    }

    @Test
    public void testMarshalerOnOuterClass() {
        String s = BridgeCallbackTest.Inner3.Inner4.append("foo", "bar");
        Assert.assertEquals("foobar", s);
    }

    // @Test
    // public void testMarshalerCallSequence() {
    // StringMarshaler.calls = new ArrayList<String>();
    // append("foo", "bar");
    // assertEquals(10, StringMarshaler.calls.size());
    // assertEquals("toNative(foo, ?, 0)", StringMarshaler.calls.get(0));
    // assertEquals("toNative(bar, ?, 0)", StringMarshaler.calls.get(1));
    // assertEquals("toObject(foo, ?, 1)", StringMarshaler.calls.get(2));
    // assertEquals("toObject(bar, ?, 1)", StringMarshaler.calls.get(3));
    // assertEquals("afterToObject(?, foo, 1)", StringMarshaler.calls.get(4));
    // assertEquals("afterToObject(?, bar, 1)", StringMarshaler.calls.get(5));
    // assertEquals("toNative(foobar, ?, 1)", StringMarshaler.calls.get(6));
    // assertEquals("afterToNative(foo, ?, 0)", StringMarshaler.calls.get(7));
    // assertEquals("afterToNative(bar, ?, 0)", StringMarshaler.calls.get(8));
    // assertEquals("toObject(foobar, ?, 0)", StringMarshaler.calls.get(9));
    // }
    @Test
    public void testMarshalSimpleEnum() {
        Assert.assertEquals(BridgeCallbackTest.SimpleEnum.V2, BridgeCallbackTest.marshalSimpleEnum(BridgeCallbackTest.SimpleEnum.V1));
        Assert.assertEquals(BridgeCallbackTest.SimpleEnum.V3, BridgeCallbackTest.marshalSimpleEnum(BridgeCallbackTest.SimpleEnum.V2));
        Assert.assertEquals(BridgeCallbackTest.SimpleEnum.V1, BridgeCallbackTest.marshalSimpleEnum(BridgeCallbackTest.SimpleEnum.V3));
    }

    @Test
    public void testMarshalValuedEnum() {
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.V100, BridgeCallbackTest.marshalValuedEnum(BridgeCallbackTest.TestValuedEnum.VM1));
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.V1000, BridgeCallbackTest.marshalValuedEnum(BridgeCallbackTest.TestValuedEnum.V100));
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.V10000, BridgeCallbackTest.marshalValuedEnum(BridgeCallbackTest.TestValuedEnum.V1000));
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.VM1, BridgeCallbackTest.marshalValuedEnum(BridgeCallbackTest.TestValuedEnum.V10000));
    }

    @Test
    public void testMarshalValuedEnumAsUnsignedInt() {
        try {
            BridgeCallbackTest.marshalValuedEnumAsUnsignedInt(BridgeCallbackTest.TestValuedEnum.VM1);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(("" + 4294967295L)));
            Assert.assertTrue(e.getMessage().contains("0xffffffff"));
        }
        try {
            BridgeCallbackTest.marshalValuedEnumAsUnsignedInt(BridgeCallbackTest.TestValuedEnum.V10000);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(("" + 4294967295L)));
            Assert.assertTrue(e.getMessage().contains("0xffffffff"));
        }
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.V1000, BridgeCallbackTest.marshalValuedEnumAsUnsignedInt(BridgeCallbackTest.TestValuedEnum.V100));
        Assert.assertEquals(BridgeCallbackTest.TestValuedEnum.V10000, BridgeCallbackTest.marshalValuedEnumAsUnsignedInt(BridgeCallbackTest.TestValuedEnum.V1000));
    }

    @Test
    public void testMarshalBits1() {
        Assert.assertEquals((1 | 8), value());
    }

    @Test
    public void testMarshalBits2() {
        Assert.assertEquals((1 | 8), BridgeCallbackTest.marshalBits2(1, 8));
    }

    @Test
    public void testMarshalStringsWithDefaultMarshaler() {
        Assert.assertEquals("a = foo, b = bar", BridgeCallbackTest.marshalStringsWithDefaultMarshaler("foo", "bar"));
        Assert.assertEquals("a = null, b = bar", BridgeCallbackTest.marshalStringsWithDefaultMarshaler(null, "bar"));
        Assert.assertEquals("a = foo, b = null", BridgeCallbackTest.marshalStringsWithDefaultMarshaler("foo", null));
        Assert.assertNull(BridgeCallbackTest.marshalStringsWithDefaultMarshaler(null, null));
    }

    @Test
    public void testMarshalBuffersWithDefaultMarshaler() {
        Assert.assertEquals("a = foo, b = bar", BridgeCallbackTest.marshalBuffersWithDefaultMarshaler(ByteBuffer.wrap("foo".getBytes()), ByteBuffer.wrap("bar".getBytes())).toStringAsciiZ());
        Assert.assertEquals("a = null, b = bar", BridgeCallbackTest.marshalBuffersWithDefaultMarshaler(null, ByteBuffer.allocateDirect(3).put("bar".getBytes())).toStringAsciiZ());
        Assert.assertEquals("a = foo, b = null", BridgeCallbackTest.marshalBuffersWithDefaultMarshaler(ByteBuffer.allocateDirect(3).put("foo".getBytes()), null).toStringAsciiZ());
        Assert.assertNull(BridgeCallbackTest.marshalBuffersWithDefaultMarshaler(null, null));
    }

    @Test
    public void testMarshal1DByteArrayWithDefaultMarshaler() {
        Assert.assertEquals("a = foo, b = bar", BridgeCallbackTest.marshal1DByteArrayWithDefaultMarshaler(new byte[]{ 'f', 'o', 'o', 0 }, new byte[]{ 'b', 'a', 'r', 0 }).toStringAsciiZ());
        Assert.assertEquals("a = null, b = bar", BridgeCallbackTest.marshal1DByteArrayWithDefaultMarshaler(null, new byte[]{ 'b', 'a', 'r', 0 }).toStringAsciiZ());
        Assert.assertEquals("a = foo, b = null", BridgeCallbackTest.marshal1DByteArrayWithDefaultMarshaler(new byte[]{ 'f', 'o', 'o', 0 }, null).toStringAsciiZ());
        Assert.assertNull(BridgeCallbackTest.marshal1DByteArrayWithDefaultMarshaler(null, null));
    }

    @Test
    public void testMarshal1DShortArrayWithDefaultMarshaler() {
        Assert.assertEquals(0, BridgeCallbackTest.marshal1DShortArrayWithDefaultMarshaler(new short[]{ 0 }));
        Assert.assertEquals(12345, BridgeCallbackTest.marshal1DShortArrayWithDefaultMarshaler(new short[]{ 10000, 2000, 300, 40, 5, 0 }));
        Assert.assertEquals((-1), BridgeCallbackTest.marshal1DShortArrayWithDefaultMarshaler(null));
    }

    @Test
    public void testMarshal1DCharArrayWithDefaultMarshaler() {
        Assert.assertEquals(0, BridgeCallbackTest.marshal1DCharArrayWithDefaultMarshaler(new char[]{ 0 }));
        Assert.assertEquals(12345, BridgeCallbackTest.marshal1DCharArrayWithDefaultMarshaler(new char[]{ 10000, 2000, 300, 40, 5, 0 }));
        Assert.assertEquals(65535, BridgeCallbackTest.marshal1DCharArrayWithDefaultMarshaler(null));
    }

    @Test
    public void testMarshal1DIntArrayWithDefaultMarshaler() {
        Assert.assertEquals(0, BridgeCallbackTest.marshal1DIntArrayWithDefaultMarshaler(new int[]{ 0 }));
        Assert.assertEquals(1000002345, BridgeCallbackTest.marshal1DIntArrayWithDefaultMarshaler(new int[]{ 1000000000, 2000, 300, 40, 5, 0 }));
        Assert.assertEquals((-1), BridgeCallbackTest.marshal1DIntArrayWithDefaultMarshaler(null));
    }

    @Test
    public void testMarshal1DLongArrayWithDefaultMarshaler() {
        Assert.assertEquals(0, BridgeCallbackTest.marshal1DLongArrayWithDefaultMarshaler(new long[]{ 0 }));
        Assert.assertEquals(1000000000000002345L, BridgeCallbackTest.marshal1DLongArrayWithDefaultMarshaler(new long[]{ 1000000000000000000L, 2000, 300, 40, 5, 0 }));
        Assert.assertEquals((-1), BridgeCallbackTest.marshal1DLongArrayWithDefaultMarshaler(null));
    }

    @Test
    public void testMarshal1DFloatArrayWithDefaultMarshaler() {
        Assert.assertEquals(0.0F, BridgeCallbackTest.marshal1DFloatArrayWithDefaultMarshaler(new float[]{ 0 }), 0);
        Assert.assertEquals(1.2345F, BridgeCallbackTest.marshal1DFloatArrayWithDefaultMarshaler(new float[]{ 1, 0.2F, 0.03F, 0.004F, 5.0E-4F, 0 }), 0.001F);
        Assert.assertEquals((-1.0F), BridgeCallbackTest.marshal1DFloatArrayWithDefaultMarshaler(null), 0);
    }

    @Test
    public void testMarshal1DDoubleArrayWithDefaultMarshaler() {
        Assert.assertEquals(0.0, BridgeCallbackTest.marshal1DDoubleArrayWithDefaultMarshaler(new double[]{ 0 }), 0);
        Assert.assertEquals(1.2345, BridgeCallbackTest.marshal1DDoubleArrayWithDefaultMarshaler(new double[]{ 1, 0.2, 0.03, 0.004, 5.0E-4, 0 }), 0.001);
        Assert.assertEquals((-1.0), BridgeCallbackTest.marshal1DDoubleArrayWithDefaultMarshaler(null), 0);
    }

    float fpi = ((float) (Math.PI));

    @Test
    public void testMarshalMachinedSizeFloatAsDouble() {
        long ldpi = Double.doubleToLongBits(Math.PI);
        long lfpi = Double.doubleToLongBits(fpi);
        Assert.assertNotEquals(ldpi, lfpi);
        if (IS_32BIT) {
            Assert.assertEquals(lfpi, Double.doubleToLongBits(BridgeCallbackTest.marshalMachinedSizeFloatAsDouble(Math.PI)));
        } else {
            Assert.assertEquals(Math.PI, BridgeCallbackTest.marshalMachinedSizeFloatAsDouble(Math.PI), 0);
        }
    }

    @Test
    public void testMarshalMachinedSizeSInt() {
        if (IS_32BIT) {
            Assert.assertEquals((-1L), BridgeCallbackTest.marshalMachinedSizeSInt((-1L)));
            Assert.assertEquals(-2147483648L, BridgeCallbackTest.marshalMachinedSizeSInt(2147483648L));
            Assert.assertEquals(-2147483648L, BridgeCallbackTest.marshalMachinedSizeSInt(1311768467015204864L));
        } else {
            // 64-bit
            Assert.assertEquals((-1L), BridgeCallbackTest.marshalMachinedSizeSInt((-1L)));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalMachinedSizeSInt(2147483648L));
            Assert.assertEquals(1311768467015204864L, BridgeCallbackTest.marshalMachinedSizeSInt(1311768467015204864L));
        }
    }

    @Test
    public void testMarshalMachinedSizeUInt() {
        if (IS_32BIT) {
            Assert.assertEquals(4294967295L, BridgeCallbackTest.marshalMachinedSizeUInt((-1L)));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalMachinedSizeUInt(2147483648L));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalMachinedSizeUInt(1311768467015204864L));
        } else {
            // 64-bit
            Assert.assertEquals((-1L), BridgeCallbackTest.marshalMachinedSizeUInt((-1L)));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalMachinedSizeUInt(2147483648L));
            Assert.assertEquals(1311768467015204864L, BridgeCallbackTest.marshalMachinedSizeUInt(1311768467015204864L));
        }
    }

    @Test
    public void testMarshalBitsAsMachineSizedIntToNative() {
        if (IS_32BIT) {
            Assert.assertEquals(4294967295L, BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V1));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V2));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V3));
        } else {
            // 64-bit
            Assert.assertEquals((-1), BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V1));
            Assert.assertEquals(2147483648L, BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V2));
            Assert.assertEquals(1311768467015204864L, BridgeCallbackTest.marshalBitsAsMachineSizedInt1(BridgeCallbackTest.MoreTestBits.V3));
        }
    }

    @Test
    public void testMarshalBitsAsMachineSizedIntFromNative() {
        if (IS_32BIT) {
            Assert.assertEquals(new BridgeCallbackTest.MoreTestBits(4294967295L), BridgeCallbackTest.marshalBitsAsMachineSizedInt2((-1)));
            Assert.assertEquals(BridgeCallbackTest.MoreTestBits.V2, BridgeCallbackTest.marshalBitsAsMachineSizedInt2(-2147483648L));
            Assert.assertEquals(BridgeCallbackTest.MoreTestBits.V2, BridgeCallbackTest.marshalBitsAsMachineSizedInt2(1311768467015204864L));
        } else {
            // 64-bit
            Assert.assertEquals(BridgeCallbackTest.MoreTestBits.V1, BridgeCallbackTest.marshalBitsAsMachineSizedInt2((-1)));
            Assert.assertEquals(new BridgeCallbackTest.MoreTestBits(-2147483648L), BridgeCallbackTest.marshalBitsAsMachineSizedInt2(-2147483648L));
            Assert.assertEquals(BridgeCallbackTest.MoreTestBits.V3, BridgeCallbackTest.marshalBitsAsMachineSizedInt2(1311768467015204864L));
        }
    }

    @Test
    public void testInstanceMethods() throws Exception {
        BridgeCallbackTest.NativeObj obj = new BridgeCallbackTest.NativeObj();
        Method setHandle = NativeObject.class.getDeclaredMethod("setHandle", long.class);
        setHandle.setAccessible(true);
        setHandle.invoke(obj, 305419896);
        LongPtr l = new LongPtr();
        Assert.assertEquals((100 * 100), obj.simpleInstanceMethod(100, l));
        Assert.assertEquals(getHandle(), l.get());
        BridgeCallbackTest.SmallStruct ss1 = new BridgeCallbackTest.SmallStruct().v1(((byte) (64))).v2(((byte) (128)));
        BridgeCallbackTest.SmallStruct ss2 = obj.returnSmallStructInstanceMethod(ss1, l);
        Assert.assertNotEquals(getHandle(), getHandle());
        Assert.assertEquals(64, ((ss2.v1()) & 255));
        Assert.assertEquals(128, ((ss2.v2()) & 255));
        Assert.assertEquals(getHandle(), l.get());
        BridgeCallbackTest.LargeStruct ls1 = new BridgeCallbackTest.LargeStruct().v1(18).v2(4660).v3(305419896).v4(1311768467463790320L);
        BridgeCallbackTest.LargeStruct ls2 = obj.returnLargeStructInstanceMethod(ls1, l);
        Assert.assertNotEquals(getHandle(), getHandle());
        Assert.assertEquals(18, ls2.v1());
        Assert.assertEquals(4660, ls2.v2());
        Assert.assertEquals(305419896, ls2.v3());
        Assert.assertEquals(1311768467463790320L, ls2.v4());
        Assert.assertEquals(getHandle(), l.get());
    }

    @Test
    public void testDynamicBridge() throws Exception {
        long targetFnPtr = VM.getCallbackMethodImpl(this.getClass().getDeclaredMethod("dynamicBridge_target", int.class, int.class));
        Assert.assertEquals(10, BridgeCallbackTest.dynamicBridge(targetFnPtr, 2, 8));
    }
}

