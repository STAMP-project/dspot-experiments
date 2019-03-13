/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.memory.impl;


import com.hazelcast.internal.memory.GlobalMemoryAccessor;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import sun.misc.Unsafe;

import static UnsafeUtil.UNSAFE;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public abstract class BaseMemoryAccessorTest extends AbstractUnsafeDependentMemoryAccessorTest {
    private static final int ALLOCATED_BLOCK_SIZE = 16;

    private static final int CHAR_MISALIGNMENT = 1;

    private static final int SHORT_MISALIGNMENT = 1;

    private static final int INT_MISALIGNMENT = 2;

    private static final int FLOAT_MISALIGNMENT = 2;

    private static final int LONG_MISALIGNMENT = 4;

    private static final int DOUBLE_MISALIGNMENT = 4;

    private static final int OBJECT_MISALIGNMENT = 2;

    protected final Unsafe unsafe = UNSAFE;

    private GlobalMemoryAccessor memoryAccessor;

    private final BaseMemoryAccessorTest.SampleObject sampleObject = new BaseMemoryAccessorTest.SampleObject();

    private long baseAddress1;

    private long baseAddress2;

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_getObjectFieldOffset() throws NoSuchFieldException {
        final Class<BaseMemoryAccessorTest.SampleObjectBase> klass = BaseMemoryAccessorTest.SampleObjectBase.class;
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.BOOLEAN_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("booleanValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.BYTE_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("byteValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.CHAR_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("charValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.SHORT_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("shortValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.INT_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("intValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.FLOAT_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("floatValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.LONG_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("longValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.DOUBLE_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("doubleValue")));
        Assert.assertEquals(BaseMemoryAccessorTest.SampleObject.OBJECT_VALUE_OFFSET, memoryAccessor.objectFieldOffset(klass.getDeclaredField("objectValue")));
    }

    @Test
    public void test_getArrayBaseOffset() {
        Assert.assertEquals(unsafe.arrayBaseOffset(boolean[].class), memoryAccessor.arrayBaseOffset(boolean[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(byte[].class), memoryAccessor.arrayBaseOffset(byte[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(char[].class), memoryAccessor.arrayBaseOffset(char[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(short[].class), memoryAccessor.arrayBaseOffset(short[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(int[].class), memoryAccessor.arrayBaseOffset(int[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(float[].class), memoryAccessor.arrayBaseOffset(float[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(long[].class), memoryAccessor.arrayBaseOffset(long[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(double[].class), memoryAccessor.arrayBaseOffset(double[].class));
        Assert.assertEquals(unsafe.arrayBaseOffset(Object[].class), memoryAccessor.arrayBaseOffset(Object[].class));
    }

    @Test
    public void test_getArrayIndexScale() {
        Assert.assertEquals(unsafe.arrayIndexScale(boolean[].class), memoryAccessor.arrayIndexScale(boolean[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(byte[].class), memoryAccessor.arrayIndexScale(byte[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(char[].class), memoryAccessor.arrayIndexScale(char[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(short[].class), memoryAccessor.arrayIndexScale(short[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(int[].class), memoryAccessor.arrayIndexScale(int[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(float[].class), memoryAccessor.arrayIndexScale(float[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(long[].class), memoryAccessor.arrayIndexScale(long[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(double[].class), memoryAccessor.arrayIndexScale(double[].class));
        Assert.assertEquals(unsafe.arrayIndexScale(Object[].class), memoryAccessor.arrayIndexScale(Object[].class));
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_endianness() {
        final long address = baseAddress1;
        memoryAccessor.putInt(address, 16777225);
        if (memoryAccessor.isBigEndian()) {
            Assert.assertEquals(1, memoryAccessor.getByte(address));
        } else {
            Assert.assertEquals(9, memoryAccessor.getByte(address));
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_copyMemory_whenAligned() {
        do_test_copyMemory(true);
    }

    @Test
    public void test_copyMemory_whenUnaligned() {
        do_test_copyMemory(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_setMemory_whenAligned() {
        do_test_setMemory(true);
    }

    @Test
    public void test_setMemory_whenUnaligned() {
        do_test_setMemory(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_copyFromByteArray() {
        final byte[] ary = new byte[]{ 2, 3 };
        memoryAccessor.copyFromByteArray(ary, 0, baseAddress1, 2);
        for (int i = 0; i < (ary.length); i++) {
            Assert.assertEquals(ary[i], memoryAccessor.getByte(((baseAddress1) + i)));
        }
    }

    @Test
    public void test_copyToByteArray() {
        final byte[] ary = new byte[2];
        for (int i = 0; i < (ary.length); i++) {
            memoryAccessor.putByte(((baseAddress1) + i), ((byte) (i)));
        }
        memoryAccessor.copyToByteArray(baseAddress1, ary, 0, 2);
        for (int i = 0; i < (ary.length); i++) {
            Assert.assertEquals(ary[i], memoryAccessor.getByte(((baseAddress1) + i)));
        }
    }

    @Test
    public void test_putGetBoolean() {
        final long address = baseAddress1;
        memoryAccessor.putBoolean(address, true);
        Assert.assertTrue(memoryAccessor.getBoolean(address));
        memoryAccessor.putBooleanVolatile(address, false);
        Assert.assertFalse(memoryAccessor.getBooleanVolatile(address));
        memoryAccessor.putBoolean(sampleObject, BaseMemoryAccessorTest.SampleObject.BOOLEAN_VALUE_OFFSET, true);
        Assert.assertTrue(memoryAccessor.getBoolean(sampleObject, BaseMemoryAccessorTest.SampleObject.BOOLEAN_VALUE_OFFSET));
        memoryAccessor.putBooleanVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.BOOLEAN_VALUE_OFFSET, false);
        Assert.assertFalse(memoryAccessor.getBooleanVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.BOOLEAN_VALUE_OFFSET));
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetByte() {
        final long address = baseAddress1;
        memoryAccessor.putByte(address, ((byte) (1)));
        Assert.assertEquals(1, memoryAccessor.getByte(address));
        memoryAccessor.putByteVolatile(address, ((byte) (2)));
        Assert.assertEquals(2, memoryAccessor.getByteVolatile(address));
        memoryAccessor.putByte(sampleObject, BaseMemoryAccessorTest.SampleObject.BYTE_VALUE_OFFSET, ((byte) (3)));
        Assert.assertEquals(3, memoryAccessor.getByte(sampleObject, BaseMemoryAccessorTest.SampleObject.BYTE_VALUE_OFFSET));
        memoryAccessor.putByteVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.BYTE_VALUE_OFFSET, ((byte) (4)));
        Assert.assertEquals(4, memoryAccessor.getByteVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.BYTE_VALUE_OFFSET));
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetChar_whenAligned() {
        do_test_putGetChar(true);
        do_test_putGetCharVolatile();
    }

    // //////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetShort_whenAligned() {
        do_test_putGetShort(true);
        do_test_putGetShortVolatile();
    }

    // //////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetInt_whenAligned() {
        do_test_putGetInt(true);
        do_test_putGetIntVolatile();
    }

    // //////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetFloat_whenAligned() {
        do_test_putGetFloat(true);
        do_test_putGetFloatVolatile();
    }

    // //////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetLong_whenAligned() {
        do_test_putGetLong(true);
        do_test_putGetLongVolatile();
    }

    // //////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetDouble_whenAligned() {
        do_test_putGetDouble(true);
        do_test_putGetDoubleVolatile();
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putGetObject_whenAligned() {
        final String value1 = "a";
        memoryAccessor.putObject(sampleObject, BaseMemoryAccessorTest.SampleObject.OBJECT_VALUE_OFFSET, value1);
        Assert.assertEquals(value1, memoryAccessor.getObject(sampleObject, BaseMemoryAccessorTest.SampleObject.OBJECT_VALUE_OFFSET));
        final String value2 = "b";
        memoryAccessor.putObjectVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.OBJECT_VALUE_OFFSET, value2);
        Assert.assertEquals(value2, memoryAccessor.getObjectVolatile(sampleObject, BaseMemoryAccessorTest.SampleObject.OBJECT_VALUE_OFFSET));
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_compareAndSwapInt_whenAligned() {
        do_test_compareAndSwapInt(true);
    }

    @Test
    @RequiresUnalignedMemoryAccessSupport
    public void test_compareAndSwapInt_whenUnaligned() {
        do_test_compareAndSwapInt(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_compareAndSwapLong_whenAligned() {
        do_test_compareAndSwapLong(true);
    }

    @Test
    @RequiresUnalignedMemoryAccessSupport
    public void test_compareAndSwapLong_whenUnaligned() {
        do_test_compareAndSwapLong(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_compareAndSwapObject_whenAligned() {
        do_test_compareAndSwapObject(true);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putOrderedInt_whenAligned() {
        do_test_putOrderedInt(true);
    }

    @Test
    @RequiresUnalignedMemoryAccessSupport
    public void test_putOrderedInt_whenUnaligned() {
        do_test_putOrderedInt(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putOrderedLong_whenAligned() {
        do_test_putOrderedLong(true);
    }

    @Test
    @RequiresUnalignedMemoryAccessSupport
    public void test_putOrderedLong_whenUnaligned() {
        do_test_putOrderedLong(false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Test
    public void test_putOrderedObject_whenAligned() {
        do_test_putOrderedObject(true);
    }

    @SuppressWarnings("unused")
    private static class SampleObjectBase {
        private byte byteValue;

        private boolean booleanValue;

        private char charValue;

        private short shortValue;

        private int intValue;

        private float floatValue;

        private long longValue;

        private double doubleValue;

        private Object objectValue;
    }

    @SuppressWarnings("unused")
    private static class SampleObject extends BaseMemoryAccessorTest.SampleObjectBase {
        private static final long BYTE_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("byteValue");

        private static final long BOOLEAN_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("booleanValue");

        private static final long CHAR_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("charValue");

        private static final long SHORT_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("shortValue");

        private static final long INT_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("intValue");

        private static final long FLOAT_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("floatValue");

        private static final long LONG_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("longValue");

        private static final long DOUBLE_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("doubleValue");

        private static final long OBJECT_VALUE_OFFSET = BaseMemoryAccessorTest.getSampleObjectFieldOffset("objectValue");

        // ensures additional allocated space at the end of the object, which may be needed to test
        // unaligned access without causing heap corruption
        private long padding;
    }
}

