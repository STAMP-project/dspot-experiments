/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.nio;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ByteBufferTest {
    @Test
    public void allocatesDirect() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        Assert.assertThat(buffer.isDirect(), CoreMatchers.is(true));
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(false));
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(100));
        Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(100));
        try {
            buffer.reset();
            Assert.fail("Mark is expected to be undefined");
        } catch (InvalidMarkException e) {
            // ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorIfAllocatingDirectOfNegativeSize() {
        ByteBuffer.allocateDirect((-2));
    }

    @Test
    public void allocatesSimple() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        Assert.assertThat(buffer.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(false));
        Assert.assertThat(buffer.hasArray(), CoreMatchers.is(true));
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(100));
        Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(100));
        try {
            buffer.reset();
            Assert.fail("Mark is expected to be undefined");
        } catch (InvalidMarkException e) {
            // ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorIfAllocatingBufferOfNegativeSize() {
        ByteBuffer.allocate((-1));
    }

    @Test
    public void wrapsArray() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array, 10, 70);
        Assert.assertThat(buffer.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(false));
        Assert.assertThat(buffer.hasArray(), CoreMatchers.is(true));
        Assert.assertThat(buffer.array(), CoreMatchers.is(array));
        Assert.assertThat(buffer.arrayOffset(), CoreMatchers.is(0));
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(100));
        Assert.assertThat(buffer.position(), CoreMatchers.is(10));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(80));
        try {
            buffer.reset();
            Assert.fail("Mark is expected to be undefined");
        } catch (InvalidMarkException e) {
            // ok
        }
        array[0] = 23;
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (23))));
        buffer.put(1, ((byte) (24)));
        Assert.assertThat(array[1], CoreMatchers.is(((byte) (24))));
    }

    @Test
    public void errorWhenWrappingWithWrongParameters() {
        byte[] array = new byte[100];
        try {
            ByteBuffer.wrap(array, (-1), 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ByteBuffer.wrap(array, 101, 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ByteBuffer.wrap(array, 98, 3);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ByteBuffer.wrap(array, 98, (-1));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test
    public void wrapsArrayWithoutOffset() {
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(100));
    }

    @Test
    public void createsSlice() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put(new byte[60]);
        buffer.flip();
        buffer.put(new byte[15]);
        ByteBuffer slice = buffer.slice();
        Assert.assertThat(slice.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(slice.position(), CoreMatchers.is(0));
        Assert.assertThat(slice.capacity(), CoreMatchers.is(45));
        Assert.assertThat(slice.limit(), CoreMatchers.is(45));
        Assert.assertThat(slice.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(slice.isReadOnly(), CoreMatchers.is(false));
        slice.put(3, ((byte) (23)));
        Assert.assertThat(buffer.get(18), CoreMatchers.is(((byte) (23))));
        slice.put(((byte) (24)));
        Assert.assertThat(buffer.get(15), CoreMatchers.is(((byte) (24))));
        buffer.put(16, ((byte) (25)));
        Assert.assertThat(slice.get(1), CoreMatchers.is(((byte) (25))));
    }

    @Test
    public void slicePropertiesSameWithOriginal() {
        ByteBuffer buffer = ByteBuffer.allocate(100).asReadOnlyBuffer().slice();
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(true));
        buffer = ByteBuffer.allocateDirect(100);
        Assert.assertThat(buffer.isDirect(), CoreMatchers.is(true));
    }

    @Test
    public void createsDuplicate() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put(new byte[60]);
        buffer.flip();
        buffer.put(new byte[15]);
        ByteBuffer duplicate = buffer.duplicate();
        Assert.assertThat(duplicate.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(duplicate.position(), CoreMatchers.is(15));
        Assert.assertThat(duplicate.capacity(), CoreMatchers.is(100));
        Assert.assertThat(duplicate.limit(), CoreMatchers.is(60));
        Assert.assertThat(duplicate.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(duplicate.isReadOnly(), CoreMatchers.is(false));
        duplicate.put(3, ((byte) (23)));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) (23))));
        duplicate.put(((byte) (24)));
        Assert.assertThat(buffer.get(15), CoreMatchers.is(((byte) (24))));
        buffer.put(1, ((byte) (25)));
        Assert.assertThat(duplicate.get(1), CoreMatchers.is(((byte) (25))));
        Assert.assertThat(duplicate.array(), CoreMatchers.is(CoreMatchers.sameInstance(buffer.array())));
    }

    @Test
    public void getsByte() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.get(), CoreMatchers.is(((byte) (2))));
        Assert.assertThat(buffer.get(), CoreMatchers.is(((byte) (3))));
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(), CoreMatchers.is(((byte) (5))));
        Assert.assertThat(buffer.get(), CoreMatchers.is(((byte) (7))));
    }

    @Test
    public void gettingByteFromEmptyBufferCausesError() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(2);
        buffer.get();
        buffer.get();
        try {
            buffer.get();
            Assert.fail("Should have thrown error");
        } catch (BufferUnderflowException e) {
            // ok
        }
    }

    @Test
    public void putsByte() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.put(((byte) (2))).put(((byte) (3))).put(((byte) (5))).put(((byte) (7)));
        Assert.assertThat(array, CoreMatchers.is(new byte[]{ 2, 3, 5, 7 }));
    }

    @Test
    public void puttingByteToEmptyBufferCausesError() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(2);
        buffer.put(((byte) (2))).put(((byte) (3)));
        try {
            buffer.put(((byte) (5)));
            Assert.fail("Should have thrown error");
        } catch (BufferOverflowException e) {
            Assert.assertThat(array[2], CoreMatchers.is(((byte) (0))));
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingByteToReadOnlyBufferCausesError() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put(((byte) (2)));
    }

    @Test
    public void getsByteFromGivenLocation() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (2))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (3))));
        buffer.get();
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (5))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (7))));
    }

    @Test
    public void gettingByteFromWrongLocationCausesError() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(3);
        try {
            buffer.get((-1));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            buffer.get(3);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test
    public void putsByteToGivenLocation() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.put(0, ((byte) (2)));
        buffer.put(1, ((byte) (3)));
        buffer.get();
        buffer = buffer.slice();
        buffer.put(1, ((byte) (5)));
        buffer.put(2, ((byte) (7)));
        Assert.assertThat(array, CoreMatchers.is(new byte[]{ 2, 3, 5, 7 }));
    }

    @Test
    public void puttingByteToWrongLocationCausesError() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(3);
        try {
            buffer.put((-1), ((byte) (2)));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            buffer.put(3, ((byte) (2)));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingByteToGivenLocationOfReadOnlyBufferCausesError() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put(0, ((byte) (2)));
    }

    @Test
    public void getsBytes() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.get();
        byte[] receiver = new byte[2];
        buffer.get(receiver, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(receiver, CoreMatchers.is(new byte[]{ 3, 5 }));
    }

    @Test
    public void gettingBytesFromEmptyBufferCausesError() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(3);
        byte[] receiver = new byte[4];
        try {
            buffer.get(receiver, 0, 4);
            Assert.fail("Error expected");
        } catch (BufferUnderflowException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new byte[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void gettingBytesWithIllegalArgumentsCausesError() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        byte[] receiver = new byte[4];
        try {
            buffer.get(receiver, 0, 5);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new byte[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, (-1), 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new byte[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, 6, 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new byte[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void putsBytes() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.get();
        byte[] data = new byte[]{ 2, 3 };
        buffer.put(data, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(array, CoreMatchers.is(new byte[]{ 0, 2, 3, 0 }));
    }

    @Test
    public void putsBytesWithZeroLengthArray() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.get();
        byte[] data = new byte[]{  };
        buffer.put(data, 0, 0);
        Assert.assertThat(buffer.position(), CoreMatchers.is(1));
        Assert.assertThat(array, CoreMatchers.is(new byte[]{ 0, 0, 0, 0 }));
    }

    @Test
    public void compacts() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.get();
        buffer.mark();
        buffer.compact();
        Assert.assertThat(array, CoreMatchers.is(new byte[]{ 3, 5, 7, 7 }));
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(4));
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(4));
        try {
            buffer.reset();
            Assert.fail("Exception expected");
        } catch (InvalidMarkException e) {
            // ok
        }
    }

    @Test
    public void marksPosition() {
        byte[] array = new byte[]{ 2, 3, 5, 7 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.position(1);
        buffer.mark();
        buffer.position(2);
        buffer.reset();
        Assert.assertThat(buffer.position(), CoreMatchers.is(1));
    }

    @Test
    public void getsChar() {
        byte[] array = new byte[]{ 0, 'A', 0, 'B' };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.getChar(), CoreMatchers.is('A'));
        Assert.assertThat(buffer.getChar(), CoreMatchers.is('B'));
        try {
            buffer.getChar();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        buffer.position(3);
        try {
            buffer.getChar();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        Assert.assertThat(buffer.getChar(0), CoreMatchers.is('A'));
        Assert.assertThat(buffer.getChar(2), CoreMatchers.is('B'));
        try {
            buffer.getChar(3);
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void putsChar() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.putChar('A');
        buffer.putChar('B');
        try {
            buffer.putChar('C');
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        buffer.position(3);
        try {
            buffer.putChar('D');
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) ('A'))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) ('B'))));
        buffer.putChar(0, 'E');
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) ('E'))));
        try {
            buffer.putChar(3, 'F');
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void getsShort() {
        byte[] array = new byte[]{ 35, 36, 37, 38 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.getShort(), CoreMatchers.is(((short) (8996))));
        Assert.assertThat(buffer.getShort(), CoreMatchers.is(((short) (9510))));
        try {
            buffer.getShort();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        buffer.position(3);
        try {
            buffer.getShort();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        Assert.assertThat(buffer.getShort(0), CoreMatchers.is(((short) (8996))));
        Assert.assertThat(buffer.getShort(2), CoreMatchers.is(((short) (9510))));
        try {
            buffer.getShort(3);
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void putsShort() {
        byte[] array = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.putShort(((short) (8996)));
        buffer.putShort(((short) (9510)));
        try {
            buffer.putShort(((short) (10024)));
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        buffer.position(3);
        try {
            buffer.putShort(((short) (10538)));
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (35))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (36))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (37))));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) (38))));
        buffer.putShort(0, ((short) (11052)));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (44))));
        try {
            buffer.putShort(3, ((short) (11566)));
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void getsInt() {
        byte[] array = new byte[]{ 35, 36, 37, 38, 39, 40, 41, 48 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.getInt(), CoreMatchers.is(589571366));
        Assert.assertThat(buffer.getInt(), CoreMatchers.is(656943408));
        try {
            buffer.getInt();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        buffer.position(7);
        try {
            buffer.getInt();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        Assert.assertThat(buffer.getInt(0), CoreMatchers.is(589571366));
        Assert.assertThat(buffer.getInt(4), CoreMatchers.is(656943408));
        try {
            buffer.getInt(7);
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void getsLong() {
        byte[] array = new byte[]{ 35, 36, 37, 38, 39, 40, 41, 48, 49, 50, 51, 52, 53, 54, 55, 56 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertThat(buffer.getLong(), CoreMatchers.is(2532189736284989744L));
        Assert.assertThat(buffer.getLong(), CoreMatchers.is(3544952156018063160L));
        try {
            buffer.getLong();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        buffer.position(15);
        try {
            buffer.getLong();
            Assert.fail("Exception expected");
        } catch (BufferUnderflowException e) {
            // expected
        }
        Assert.assertThat(buffer.getLong(0), CoreMatchers.is(2532189736284989744L));
        Assert.assertThat(buffer.getLong(8), CoreMatchers.is(3544952156018063160L));
        try {
            buffer.getLong(16);
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void putsLong() {
        byte[] array = new byte[16];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.putLong(2532189736284989744L);
        buffer.putLong(3544952156018063160L);
        try {
            buffer.putLong(0L);
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        buffer.position(15);
        try {
            buffer.putLong(0L);
            Assert.fail("Exception expected");
        } catch (BufferOverflowException e) {
            // expected
        }
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((byte) (35))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (36))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((byte) (37))));
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((byte) (38))));
        Assert.assertThat(buffer.get(4), CoreMatchers.is(((byte) (39))));
        Assert.assertThat(buffer.get(5), CoreMatchers.is(((byte) (40))));
        Assert.assertThat(buffer.get(6), CoreMatchers.is(((byte) (41))));
        Assert.assertThat(buffer.get(7), CoreMatchers.is(((byte) (48))));
        buffer.putLong(0, -6144092013047382016L);
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((byte) (187))));
        try {
            buffer.putLong(15, 0L);
            Assert.fail("Exception expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
}

