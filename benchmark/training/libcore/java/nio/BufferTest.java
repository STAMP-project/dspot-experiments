/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.nio;


import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import junit.framework.TestCase;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;


public class BufferTest extends TestCase {
    /**
     * Try to create a {@link MappedByteBuffer} from /dev/zero, to see if
     * we support mapping UNIX character devices.
     */
    public void testDevZeroMap() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("/dev/zero", "r");
        try {
            MappedByteBuffer mbb = raf.getChannel().map(READ_ONLY, 0, 65536);
            // Create an array initialized to all "(byte) 1"
            byte[] buf1 = new byte[65536];
            Arrays.fill(buf1, ((byte) (1)));
            // Read from mapped /dev/zero, and overwrite this array.
            mbb.get(buf1);
            // Verify that everything is zero
            for (int i = 0; i < 65536; i++) {
                TestCase.assertEquals(((byte) (0)), buf1[i]);
            }
        } finally {
            raf.close();
        }
    }

    /**
     * Same as {@link libcore.java.nio.BufferTest#testDevZeroMap()}, but try to see
     * if we can write to the UNIX character device.
     */
    public void testDevZeroMapRW() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("/dev/zero", "rw");
        try {
            MappedByteBuffer mbb = raf.getChannel().map(READ_WRITE, 65536, 131072);
            // Create an array initialized to all "(byte) 1"
            byte[] buf1 = new byte[65536];
            Arrays.fill(buf1, ((byte) (1)));
            // Put all "(byte) 1"s into the /dev/zero MappedByteBuffer.
            mbb.put(buf1);
            mbb.position(0);
            byte[] buf2 = new byte[65536];
            mbb.get(buf2);
            // Verify that everything is one
            for (int i = 0; i < 65536; i++) {
                TestCase.assertEquals(((byte) (1)), buf2[i]);
            }
        } finally {
            raf.close();
        }
    }

    public void testByteSwappedBulkGetDirect() throws Exception {
        testByteSwappedBulkGet(ByteBuffer.allocateDirect(10));
    }

    public void testByteSwappedBulkGetHeap() throws Exception {
        testByteSwappedBulkGet(ByteBuffer.allocate(10));
    }

    public void testByteSwappedBulkGetMapped() throws Exception {
        testByteSwappedBulkGet(BufferTest.allocateMapped(10));
    }

    public void testByteSwappedBulkPutDirect() throws Exception {
        testByteSwappedBulkPut(ByteBuffer.allocateDirect(10));
    }

    public void testByteSwappedBulkPutHeap() throws Exception {
        testByteSwappedBulkPut(ByteBuffer.allocate(10));
    }

    public void testByteSwappedBulkPutMapped() throws Exception {
        testByteSwappedBulkPut(BufferTest.allocateMapped(10));
    }

    public void testByteBufferByteOrderDirectRW() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocateDirect(10), false);
    }

    public void testByteBufferByteOrderHeapRW() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocate(10), false);
    }

    public void testByteBufferByteOrderMappedRW() throws Exception {
        testByteBufferByteOrder(BufferTest.allocateMapped(10), false);
    }

    public void testByteBufferByteOrderDirectRO() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocateDirect(10), true);
    }

    public void testByteBufferByteOrderHeapRO() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocate(10), true);
    }

    public void testByteBufferByteOrderMappedRO() throws Exception {
        testByteBufferByteOrder(BufferTest.allocateMapped(10), true);
    }

    public void testCharBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), CharBuffer.wrap(new char[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), CharBuffer.wrap(new char[10]).asReadOnlyBuffer().order());
    }

    public void testCharBufferByteOrderArray() throws Exception {
        testCharBufferByteOrder(CharBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testCharBufferByteOrderBE() throws Exception {
        testCharBufferByteOrder(allocateCharBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testCharBufferByteOrderLE() throws Exception {
        testCharBufferByteOrder(allocateCharBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testDoubleBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), DoubleBuffer.wrap(new double[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), DoubleBuffer.wrap(new double[10]).asReadOnlyBuffer().order());
    }

    public void testDoubleBufferByteOrderArray() throws Exception {
        testDoubleBufferByteOrder(DoubleBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testDoubleBufferByteOrderBE() throws Exception {
        testDoubleBufferByteOrder(allocateDoubleBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testDoubleBufferByteOrderLE() throws Exception {
        testDoubleBufferByteOrder(allocateDoubleBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testFloatBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), FloatBuffer.wrap(new float[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), FloatBuffer.wrap(new float[10]).asReadOnlyBuffer().order());
    }

    public void testFloatBufferByteOrderArray() throws Exception {
        testFloatBufferByteOrder(FloatBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testFloatBufferByteOrderBE() throws Exception {
        testFloatBufferByteOrder(allocateFloatBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testFloatBufferByteOrderLE() throws Exception {
        testFloatBufferByteOrder(allocateFloatBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testIntBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), IntBuffer.wrap(new int[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), IntBuffer.wrap(new int[10]).asReadOnlyBuffer().order());
    }

    public void testIntBufferByteOrderArray() throws Exception {
        testIntBufferByteOrder(IntBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testIntBufferByteOrderBE() throws Exception {
        testIntBufferByteOrder(allocateIntBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testIntBufferByteOrderLE() throws Exception {
        testIntBufferByteOrder(allocateIntBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testLongBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), LongBuffer.wrap(new long[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), LongBuffer.wrap(new long[10]).asReadOnlyBuffer().order());
    }

    public void testLongBufferByteOrderArray() throws Exception {
        testLongBufferByteOrder(LongBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testLongBufferByteOrderBE() throws Exception {
        testLongBufferByteOrder(allocateLongBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testLongBufferByteOrderLE() throws Exception {
        testLongBufferByteOrder(allocateLongBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testShortBufferByteOrderWrapped() throws Exception {
        TestCase.assertEquals(ByteOrder.nativeOrder(), ShortBuffer.wrap(new short[10]).order());
        TestCase.assertEquals(ByteOrder.nativeOrder(), ShortBuffer.wrap(new short[10]).asReadOnlyBuffer().order());
    }

    public void testShortBufferByteOrderArray() throws Exception {
        testShortBufferByteOrder(ShortBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testShortBufferByteOrderBE() throws Exception {
        testShortBufferByteOrder(allocateShortBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testShortBufferByteOrderLE() throws Exception {
        testShortBufferByteOrder(allocateShortBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testRelativePositionsHeap() throws Exception {
        testRelativePositions(ByteBuffer.allocate(10));
    }

    public void testRelativePositionsDirect() throws Exception {
        testRelativePositions(ByteBuffer.allocateDirect(10));
    }

    public void testRelativePositionsMapped() throws Exception {
        testRelativePositions(BufferTest.allocateMapped(10));
    }

    // This test will fail on the RI. Our direct buffers are cooler than theirs.
    // http://b/3384431
    public void testDirectByteBufferHasArray() throws Exception {
        ByteBuffer b = ByteBuffer.allocateDirect(10);
        TestCase.assertTrue(b.isDirect());
        // Check the buffer has an array of the right size.
        TestCase.assertTrue(b.hasArray());
        TestCase.assertEquals(0, b.arrayOffset());
        byte[] array = b.array();
        TestCase.assertEquals(10, array.length);
        // Check that writes to the array show up in the buffer.
        TestCase.assertEquals(0, b.get(0));
        array[0] = 1;
        TestCase.assertEquals(1, b.get(0));
        // Check that writes to the buffer show up in the array.
        TestCase.assertEquals(1, array[0]);
        b.put(0, ((byte) (0)));
        TestCase.assertEquals(0, array[0]);
    }

    public void testSliceOffset() throws Exception {
        // Slicing changes the array offset.
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.get();
        ByteBuffer slice = buffer.slice();
        TestCase.assertEquals(0, buffer.arrayOffset());
        TestCase.assertEquals(1, slice.arrayOffset());
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(10);
        directBuffer.get();
        ByteBuffer directSlice = directBuffer.slice();
        TestCase.assertEquals(0, directBuffer.arrayOffset());
        TestCase.assertEquals(1, directSlice.arrayOffset());
    }

    // http://code.google.com/p/android/issues/detail?id=16184
    public void testPutByteBuffer() throws Exception {
        ByteBuffer dst = ByteBuffer.allocate(10).asReadOnlyBuffer();
        // Can't put into a read-only buffer.
        try {
            dst.put(ByteBuffer.allocate(5));
            TestCase.fail();
        } catch (ReadOnlyBufferException expected) {
        }
        // Can't put a buffer into itself.
        dst = ByteBuffer.allocate(10);
        try {
            dst.put(dst);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // Can't put the null ByteBuffer.
        try {
            dst.put(((ByteBuffer) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Can't put a larger source into a smaller destination.
        try {
            dst.put(ByteBuffer.allocate(((dst.capacity()) + 1)));
            TestCase.fail();
        } catch (BufferOverflowException expected) {
        }
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocate(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocateDirect(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), BufferTest.allocateMapped(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocate(8), true);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocateDirect(8), true);
        assertPutByteBuffer(ByteBuffer.allocate(10), BufferTest.allocateMapped(8), true);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocate(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocateDirect(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), BufferTest.allocateMapped(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocate(8), true);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocateDirect(8), true);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), BufferTest.allocateMapped(8), true);
    }

    public void testCharBufferSubSequence() throws Exception {
        ByteBuffer b = ByteBuffer.allocateDirect(10).order(ByteOrder.nativeOrder());
        b.putChar('H');
        b.putChar('e');
        b.putChar('l');
        b.putChar('l');
        b.putChar('o');
        b.flip();
        TestCase.assertEquals("Hello", b.asCharBuffer().toString());
        CharBuffer cb = b.asCharBuffer();
        CharSequence cs = cb.subSequence(0, cb.length());
        TestCase.assertEquals("Hello", cs.toString());
    }

    public void testHasArrayOnJniDirectByteBuffer() throws Exception {
        // Simulate a call to JNI's NewDirectByteBuffer.
        Class<?> c = Class.forName("java.nio.DirectByteBuffer");
        Constructor<?> ctor = c.getDeclaredConstructor(long.class, int.class);
        ctor.setAccessible(true);
        ByteBuffer bb = ((ByteBuffer) (ctor.newInstance(0, 0)));
        try {
            bb.array();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            bb.arrayOffset();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        TestCase.assertFalse(bb.hasArray());
    }

    public void testBug6085292() {
        ByteBuffer b = ByteBuffer.allocateDirect(1);
        try {
            b.asCharBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asCharBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
        try {
            b.asDoubleBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asDoubleBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
        try {
            b.asFloatBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asFloatBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
        try {
            b.asIntBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asIntBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
        try {
            b.asLongBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asLongBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
        try {
            b.asShortBuffer().get();
            TestCase.fail();
        } catch (BufferUnderflowException expected) {
        }
        try {
            b.asShortBuffer().get(0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
            TestCase.assertTrue(expected.getMessage().contains("limit=0"));
        }
    }

    public void testUsingDirectBufferAsMappedBuffer() throws Exception {
        MappedByteBuffer notMapped = ((MappedByteBuffer) (ByteBuffer.allocateDirect(1)));
        try {
            notMapped.force();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            notMapped.isLoaded();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            notMapped.load();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        MappedByteBuffer mapped = ((MappedByteBuffer) (BufferTest.allocateMapped(1)));
        mapped.force();
        mapped.isLoaded();
        mapped.load();
    }

    // https://code.google.com/p/android/issues/detail?id=53637
    public void testBug53637() throws Exception {
        MappedByteBuffer mapped = ((MappedByteBuffer) (BufferTest.allocateMapped(1)));
        mapped.get();
        mapped.rewind();
        mapped.get();
        mapped.rewind();
        mapped.mark();
        mapped.get();
        mapped.reset();
        mapped.get();
        mapped.rewind();
        mapped.get();
        mapped.clear();
        mapped.get();
        mapped.rewind();
        mapped.get();
        mapped.flip();
        mapped.get();
    }
}

