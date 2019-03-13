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


import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.VM;
import org.robovm.rt.bro.annotation.StructMember;
import org.robovm.rt.bro.ptr.Ptr;

import static Bro.IS_32BIT;


/**
 * Tests {@link GlobalValue} methods.
 */
public class GlobalValueTest {
    public static final class Point extends Struct<GlobalValueTest.Point> {
        @StructMember(0)
        public native int x();

        @StructMember(1)
        public native int y();

        @StructMember(0)
        public native GlobalValueTest.Point x(int x);

        @StructMember(1)
        public native GlobalValueTest.Point y(int y);
    }

    public static final class PointPtr extends Ptr<GlobalValueTest.Point, GlobalValueTest.PointPtr> {}

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

    public static final class TestBits extends Bits<GlobalValueTest.TestBits> {
        public static final GlobalValueTest.TestBits V1 = new GlobalValueTest.TestBits(1);

        public static final GlobalValueTest.TestBits V2 = new GlobalValueTest.TestBits(2);

        public static final GlobalValueTest.TestBits V4 = new GlobalValueTest.TestBits(4);

        public static final GlobalValueTest.TestBits V8 = new GlobalValueTest.TestBits(8);

        private static final GlobalValueTest.TestBits[] VALUES = _values(GlobalValueTest.TestBits.class);

        private TestBits(long value) {
            super(value);
        }

        private TestBits(long value, long mask) {
            super(value, mask);
        }

        @Override
        protected GlobalValueTest.TestBits wrap(long value, long mask) {
            return new GlobalValueTest.TestBits(value, mask);
        }

        @Override
        protected GlobalValueTest.TestBits[] _values() {
            return GlobalValueTest.TestBits.VALUES;
        }
    }

    static final ByteBuffer memory = ByteBuffer.allocateDirect(1024);

    private static final int EFFECTIVE_DIRECT_ADDRESS_OFFSET;

    static {
        try {
            Field f1 = Buffer.class.getDeclaredField("effectiveDirectAddress");
            if ((f1.getType()) != (long.class)) {
                throw new Error("java.nio.Buffer.effectiveDirectAddress should be a long");
            }
            EFFECTIVE_DIRECT_ADDRESS_OFFSET = VM.getInstanceFieldOffset(VM.getFieldAddress(f1));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
        GlobalValueTest.bind(GlobalValueTest.class);
    }

    @Test
    public void testByte() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.get(0));
        Assert.assertEquals(0, GlobalValueTest.byteGetter());
        GlobalValueTest.byteSetter(((byte) (255)));
        Assert.assertEquals(255, ((GlobalValueTest.memory.get(0)) & 255));
        Assert.assertEquals(255, ((GlobalValueTest.byteGetter()) & 255));
    }

    @Test
    public void testShort() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asShortBuffer().get(0));
        Assert.assertEquals(0, GlobalValueTest.shortGetter());
        GlobalValueTest.shortSetter(((short) (65535)));
        Assert.assertEquals(65535, ((GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asShortBuffer().get(0)) & 65535));
        Assert.assertEquals(65535, ((GlobalValueTest.shortGetter()) & 65535));
    }

    @Test
    public void testChar() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asCharBuffer().get(0));
        Assert.assertEquals(0, GlobalValueTest.charGetter());
        GlobalValueTest.charSetter(((char) (65535)));
        Assert.assertEquals(65535, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asCharBuffer().get(0));
        Assert.assertEquals(65535, GlobalValueTest.charGetter());
    }

    @Test
    public void testInt() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(0, GlobalValueTest.intGetter());
        GlobalValueTest.intSetter(-1);
        Assert.assertEquals(-1, ((GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0)) & -1));
        Assert.assertEquals(-1, ((GlobalValueTest.intGetter()) & -1));
    }

    @Test
    public void testLong() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
        Assert.assertEquals(0, GlobalValueTest.longGetter());
        GlobalValueTest.longSetter(-1L);
        Assert.assertEquals(-1L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
        Assert.assertEquals(-1L, GlobalValueTest.longGetter());
    }

    @Test
    public void testFloat() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asFloatBuffer().get(0), 0);
        Assert.assertEquals(0, GlobalValueTest.floatGetter(), 0);
        GlobalValueTest.floatSetter(((float) (Math.PI)));
        Assert.assertEquals(((float) (Math.PI)), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asFloatBuffer().get(0), 1.0E-5F);
        Assert.assertEquals(((float) (Math.PI)), GlobalValueTest.floatGetter(), 1.0E-5F);
    }

    @Test
    public void testDouble() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asDoubleBuffer().get(0), 0);
        Assert.assertEquals(0, GlobalValueTest.doubleGetter(), 0);
        GlobalValueTest.doubleSetter(Math.PI);
        Assert.assertEquals(Math.PI, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asDoubleBuffer().get(0), 1.0E-5);
        Assert.assertEquals(Math.PI, GlobalValueTest.doubleGetter(), 1.0E-5);
    }

    @Test
    public void testStructByVal() throws Exception {
        IntBuffer memoryAsIntBuffer = GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer();
        Assert.assertEquals(0, memoryAsIntBuffer.get(0));
        Assert.assertEquals(0, memoryAsIntBuffer.get(1));
        Assert.assertEquals(0, GlobalValueTest.structByValGetter().x());
        Assert.assertEquals(0, GlobalValueTest.structByValGetter().y());
        GlobalValueTest.structByValGetter().x(9876);
        GlobalValueTest.structByValGetter().y(5432);
        Assert.assertEquals(9876, memoryAsIntBuffer.get(0));
        Assert.assertEquals(5432, memoryAsIntBuffer.get(1));
        Assert.assertEquals(9876, GlobalValueTest.structByValGetter().x());
        Assert.assertEquals(5432, GlobalValueTest.structByValGetter().y());
        GlobalValueTest.structByValSetter(new GlobalValueTest.Point().x(1234).y(5678));
        Assert.assertEquals(1234, memoryAsIntBuffer.get(0));
        Assert.assertEquals(5678, memoryAsIntBuffer.get(1));
        Assert.assertEquals(1234, GlobalValueTest.structByValGetter().x());
        Assert.assertEquals(5678, GlobalValueTest.structByValGetter().y());
    }

    @Test
    public void testStructByRef() throws Exception {
        LongBuffer memoryAsLongBuffer = GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer();
        Assert.assertEquals(0, memoryAsLongBuffer.get(0));
        Assert.assertNull(GlobalValueTest.structByRefGetter());
        GlobalValueTest.Point p = new GlobalValueTest.Point();
        GlobalValueTest.structByRefSetter(p);
        Assert.assertEquals(getHandle(), memoryAsLongBuffer.get(0));
    }

    @Test
    public void testEnum() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(GlobalValueTest.SimpleEnum.V1, GlobalValueTest.enumGetter());
        GlobalValueTest.enumSetter(GlobalValueTest.SimpleEnum.V3);
        Assert.assertEquals(2, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(GlobalValueTest.SimpleEnum.V3, GlobalValueTest.enumGetter());
    }

    @Test
    public void testValuedEnum() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        try {
            GlobalValueTest.valuedEnumGetter();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        GlobalValueTest.valuedEnumSetter(GlobalValueTest.TestValuedEnum.V100);
        Assert.assertEquals(100, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(GlobalValueTest.TestValuedEnum.V100, GlobalValueTest.valuedEnumGetter());
    }

    @Test
    public void testBits() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(0, value());
        GlobalValueTest.bitsSetter(GlobalValueTest.TestBits.V8);
        Assert.assertEquals(8, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
        Assert.assertEquals(GlobalValueTest.TestBits.V8, GlobalValueTest.bitsGetter());
    }

    @Test
    public void testPtr() {
        LongBuffer memoryAsLongBuffer = GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer();
        Assert.assertEquals(0, memoryAsLongBuffer.get(0));
        Assert.assertNull(GlobalValueTest.ptrGetter());
        GlobalValueTest.PointPtr ptr = new GlobalValueTest.PointPtr();
        Assert.assertNull(get());
        GlobalValueTest.Point p = new GlobalValueTest.Point();
        set(p);
        GlobalValueTest.ptrSetter(ptr);
        Assert.assertEquals(getHandle(), memoryAsLongBuffer.get(0));
        Assert.assertEquals(ptr, GlobalValueTest.ptrGetter());
        Assert.assertEquals(get(), get());
    }

    @Test
    public void testArrayAsString() throws Exception {
        Assert.assertEquals(0, GlobalValueTest.memory.get(0));
        Assert.assertEquals("", GlobalValueTest.arrayAsStringGetter());
        GlobalValueTest.arrayAsStringSetter("foobar");
        byte[] bytes = new byte[7];
        GlobalValueTest.memory.get(bytes);
        Assert.assertEquals("foobar", new String(bytes, 0, 6));
        Assert.assertEquals("foobar", GlobalValueTest.arrayAsStringGetter());
        Assert.assertEquals(0, bytes[6]);
    }

    @Test
    public void testArrayAsByteBuffer() throws Exception {
        byte[] bytes1 = new byte[10];
        byte[] bytes2 = new byte[10];
        GlobalValueTest.memory.get(bytes1);
        GlobalValueTest.memory.clear();
        Assert.assertTrue(Arrays.equals(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, bytes1));
        GlobalValueTest.arrayAsByteBufferGetter().get(bytes2);
        Assert.assertTrue(Arrays.equals(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, bytes2));
        GlobalValueTest.arrayAsByteBufferSetter(ByteBuffer.wrap(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }));
        GlobalValueTest.memory.get(bytes1);
        GlobalValueTest.memory.clear();
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, bytes1));
        GlobalValueTest.arrayAsByteBufferGetter().get(bytes2);
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, bytes2));
    }

    @Test
    public void testArrayAsByteArray() throws Exception {
        byte[] bytes1 = new byte[10];
        GlobalValueTest.memory.get(bytes1);
        GlobalValueTest.memory.clear();
        Assert.assertTrue(Arrays.equals(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, bytes1));
        Assert.assertTrue(Arrays.equals(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, GlobalValueTest.arrayAsByteArrayGetter()));
        GlobalValueTest.arrayAsByteArraySetter(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        GlobalValueTest.memory.get(bytes1);
        GlobalValueTest.memory.clear();
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, bytes1));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, GlobalValueTest.arrayAsByteArrayGetter()));
    }

    float fpi = ((float) (Math.PI));

    @Test
    public void testMachineSizedFloat() throws Exception {
        long ldpi = Double.doubleToLongBits(Math.PI);
        long lfpi = Double.doubleToLongBits(fpi);
        Assert.assertNotEquals(ldpi, lfpi);
        if (IS_32BIT) {
            Assert.assertEquals(0.0F, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asFloatBuffer().get(0), 0.0F);
            Assert.assertEquals(0.0, GlobalValueTest.machineSizedFloatGetterD(), 0);
            Assert.assertEquals(0.0, GlobalValueTest.machineSizedFloatGetterF(), 0);
            GlobalValueTest.machineSizedFloatSetterD(Math.PI);
            Assert.assertEquals(fpi, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asFloatBuffer().get(0), 0.0F);
            Assert.assertEquals(fpi, GlobalValueTest.machineSizedFloatGetterF(), 0);
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            Assert.assertEquals(lfpi, Double.doubleToLongBits(GlobalValueTest.machineSizedFloatGetterD()));
            GlobalValueTest.machineSizedFloatSetterF(fpi);
            Assert.assertEquals(fpi, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asFloatBuffer().get(0), 0.0F);
            Assert.assertEquals(fpi, GlobalValueTest.machineSizedFloatGetterF(), 0);
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            Assert.assertEquals(lfpi, Double.doubleToLongBits(GlobalValueTest.machineSizedFloatGetterD()));
        } else {
            // 64-bit
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asDoubleBuffer().get(0), 0);
            Assert.assertEquals(0.0, GlobalValueTest.machineSizedFloatGetterD(), 0);
            Assert.assertEquals(0.0, GlobalValueTest.machineSizedFloatGetterF(), 0);
            GlobalValueTest.machineSizedFloatSetterD(Math.PI);
            Assert.assertEquals(Math.PI, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asDoubleBuffer().get(0), 0.0F);
            Assert.assertEquals(fpi, GlobalValueTest.machineSizedFloatGetterF(), 0);
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            Assert.assertEquals(ldpi, Double.doubleToLongBits(GlobalValueTest.machineSizedFloatGetterD()));
            GlobalValueTest.machineSizedFloatSetterF(fpi);
            Assert.assertEquals(((double) (fpi)), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asDoubleBuffer().get(0), 0.0F);
            Assert.assertEquals(fpi, GlobalValueTest.machineSizedFloatGetterF(), 0);
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            Assert.assertEquals(lfpi, Double.doubleToLongBits(GlobalValueTest.machineSizedFloatGetterD()));
        }
    }

    @Test
    public void testMachineSizedSInt() throws Exception {
        if (IS_32BIT) {
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(0, GlobalValueTest.machineSizedSIntGetter());
            GlobalValueTest.machineSizedSIntSetter((-1));
            Assert.assertEquals((-1), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals((-1), GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            GlobalValueTest.machineSizedSIntSetter(2147483648L);
            Assert.assertEquals(-2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(-2147483648L, GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            GlobalValueTest.machineSizedSIntSetter(1311768467015204864L);
            Assert.assertEquals(-2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(-2147483648L, GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
        } else {
            // 64-bit
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(0, GlobalValueTest.machineSizedSIntGetter());
            GlobalValueTest.machineSizedSIntSetter((-1));
            Assert.assertEquals((-1), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals((-1), GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            GlobalValueTest.machineSizedSIntSetter(2147483648L);
            Assert.assertEquals(2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(2147483648L, GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            GlobalValueTest.machineSizedSIntSetter(1311768467015204864L);
            Assert.assertEquals(1311768467015204864L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(1311768467015204864L, GlobalValueTest.machineSizedSIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
        }
    }

    @Test
    public void testMachineSizedUInt() throws Exception {
        if (IS_32BIT) {
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(0, GlobalValueTest.machineSizedUIntGetter());
            GlobalValueTest.machineSizedUIntSetter((-1));
            Assert.assertEquals((-1), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(4294967295L, GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            GlobalValueTest.machineSizedUIntSetter(2147483648L);
            Assert.assertEquals(-2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(2147483648L, GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
            GlobalValueTest.machineSizedUIntSetter(1311768467015204864L);
            Assert.assertEquals(-2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(0));
            Assert.assertEquals(2147483648L, GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asIntBuffer().get(1));
        } else {
            // 64-bit
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(0, GlobalValueTest.machineSizedUIntGetter());
            GlobalValueTest.machineSizedUIntSetter((-1));
            Assert.assertEquals((-1), GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals((-1), GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            GlobalValueTest.machineSizedUIntSetter(2147483648L);
            Assert.assertEquals(2147483648L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(2147483648L, GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
            GlobalValueTest.machineSizedUIntSetter(1311768467015204864L);
            Assert.assertEquals(1311768467015204864L, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(0));
            Assert.assertEquals(1311768467015204864L, GlobalValueTest.machineSizedUIntGetter());
            Assert.assertEquals(0, GlobalValueTest.memory.order(ByteOrder.nativeOrder()).asLongBuffer().get(1));
        }
    }
}

