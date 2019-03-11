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


import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class CharBufferTest {
    @Test
    public void allocates() {
        CharBuffer buffer = CharBuffer.allocate(100);
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
        CharBuffer.allocate((-1));
    }

    @Test
    public void wrapsArray() {
        char[] array = new char[100];
        CharBuffer buffer = CharBuffer.wrap(array, 10, 70);
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
        array[0] = 'A';
        Assert.assertThat(buffer.get(0), CoreMatchers.is('A'));
        buffer.put(1, 'B');
        Assert.assertThat(array[1], CoreMatchers.is('B'));
    }

    @Test
    public void errorWhenWrappingWithWrongParameters() {
        char[] array = new char[100];
        try {
            CharBuffer.wrap(array, (-1), 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            CharBuffer.wrap(array, 101, 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            CharBuffer.wrap(array, 98, 3);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            CharBuffer.wrap(array, 98, (-1));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test
    public void wrapsArrayWithoutOffset() {
        char[] array = new char[100];
        CharBuffer buffer = CharBuffer.wrap(array);
        Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(100));
    }

    @Test
    public void createsSlice() {
        CharBuffer buffer = CharBuffer.allocate(100);
        buffer.put(new char[60]);
        buffer.flip();
        buffer.put(new char[15]);
        CharBuffer slice = buffer.slice();
        Assert.assertThat(slice.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(slice.position(), CoreMatchers.is(0));
        Assert.assertThat(slice.capacity(), CoreMatchers.is(45));
        Assert.assertThat(slice.limit(), CoreMatchers.is(45));
        Assert.assertThat(slice.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(slice.isReadOnly(), CoreMatchers.is(false));
        slice.put(3, 'A');
        Assert.assertThat(buffer.get(18), CoreMatchers.is('A'));
        slice.put('B');
        Assert.assertThat(buffer.get(15), CoreMatchers.is('B'));
        buffer.put(16, 'C');
        Assert.assertThat(slice.get(1), CoreMatchers.is('C'));
    }

    @Test
    public void slicePropertiesSameWithOriginal() {
        CharBuffer buffer = CharBuffer.allocate(100).asReadOnlyBuffer().slice();
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(true));
    }

    @Test
    public void createsDuplicate() {
        CharBuffer buffer = CharBuffer.allocate(100);
        buffer.put(new char[60]);
        buffer.flip();
        buffer.put(new char[15]);
        CharBuffer duplicate = buffer.duplicate();
        Assert.assertThat(duplicate.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(duplicate.position(), CoreMatchers.is(15));
        Assert.assertThat(duplicate.capacity(), CoreMatchers.is(100));
        Assert.assertThat(duplicate.limit(), CoreMatchers.is(60));
        Assert.assertThat(duplicate.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(duplicate.isReadOnly(), CoreMatchers.is(false));
        duplicate.put(3, 'A');
        Assert.assertThat(buffer.get(3), CoreMatchers.is('A'));
        duplicate.put('B');
        Assert.assertThat(buffer.get(15), CoreMatchers.is('B'));
        buffer.put(1, 'C');
        Assert.assertThat(duplicate.get(1), CoreMatchers.is('C'));
        Assert.assertThat(duplicate.array(), CoreMatchers.is(CoreMatchers.sameInstance(buffer.array())));
    }

    @Test
    public void getsChar() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        Assert.assertThat(buffer.get(), CoreMatchers.is('T'));
        Assert.assertThat(buffer.get(), CoreMatchers.is('e'));
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(), CoreMatchers.is('a'));
        Assert.assertThat(buffer.get(), CoreMatchers.is('V'));
    }

    @Test
    public void gettingCharFromEmptyBufferCausesError() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
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
    public void putsChar() {
        char[] array = new char[5];
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.put('T').put('e').put('a').put('V').put('M');
        Assert.assertThat(array, CoreMatchers.is(new char[]{ 'T', 'e', 'a', 'V', 'M' }));
    }

    @Test
    public void puttingCharToEmptyBufferCausesError() {
        char[] array = new char[4];
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.limit(2);
        buffer.put('A').put('B');
        try {
            buffer.put('C');
            Assert.fail("Should have thrown error");
        } catch (BufferOverflowException e) {
            Assert.assertThat(array[2], CoreMatchers.is('\u0000'));
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingCharToReadOnlyBufferCausesError() {
        char[] array = new char[4];
        CharBuffer buffer = CharBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put('A');
    }

    @Test
    public void getsCharFromGivenLocation() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        Assert.assertThat(buffer.get(0), CoreMatchers.is('T'));
        Assert.assertThat(buffer.get(1), CoreMatchers.is('e'));
        buffer.get();
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(1), CoreMatchers.is('a'));
        Assert.assertThat(buffer.get(2), CoreMatchers.is('V'));
    }

    @Test
    public void gettingCharFromWrongLocationCausesError() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
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
    public void putsCharToGivenLocation() {
        char[] array = new char[5];
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.put(0, 'T');
        buffer.put(1, 'e');
        buffer.get();
        buffer = buffer.slice();
        buffer.put(1, 'a');
        buffer.put(2, 'V');
        buffer.put(3, 'M');
        Assert.assertThat(array, CoreMatchers.is(new char[]{ 'T', 'e', 'a', 'V', 'M' }));
    }

    @Test
    public void puttingCharToWrongLocationCausesError() {
        char[] array = new char[4];
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.limit(3);
        try {
            buffer.put((-1), 'A');
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            buffer.put(3, 'A');
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingCharToGivenLocationOfReadOnlyBufferCausesError() {
        char[] array = new char[4];
        CharBuffer buffer = CharBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put(0, 'A');
    }

    @Test
    public void getsChars() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.get();
        char[] receiver = new char[2];
        buffer.get(receiver, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(receiver, CoreMatchers.is(new char[]{ 'e', 'a' }));
    }

    @Test
    public void gettingCharsFromEmptyBufferCausesError() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.limit(3);
        char[] receiver = new char[5];
        try {
            buffer.get(receiver, 0, 4);
            Assert.fail("Error expected");
        } catch (BufferUnderflowException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new char[5]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void gettingCharsWithIllegalArgumentsCausesError() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        char[] receiver = new char[5];
        try {
            buffer.get(receiver, 0, 6);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new char[5]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, (-1), 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new char[5]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, 6, 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new char[5]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void putsChars() {
        char[] array = new char[4];
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.get();
        char[] data = new char[]{ 'A', 'B' };
        buffer.put(data, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(array, CoreMatchers.is(new char[]{ '\u0000', 'A', 'B', '\u0000' }));
    }

    @Test
    public void compacts() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.get();
        buffer.mark();
        buffer.compact();
        Assert.assertThat(array, CoreMatchers.is(new char[]{ 'e', 'a', 'V', 'M', 'M' }));
        Assert.assertThat(buffer.position(), CoreMatchers.is(4));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(5));
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(5));
        try {
            buffer.reset();
            Assert.fail("Exception expected");
        } catch (InvalidMarkException e) {
            // ok
        }
    }

    @Test
    public void marksPosition() {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        buffer.position(1);
        buffer.mark();
        buffer.position(2);
        buffer.reset();
        Assert.assertThat(buffer.position(), CoreMatchers.is(1));
    }

    @Test
    public void readsChars() throws IOException {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        CharBuffer target = CharBuffer.allocate(10);
        int charsRead = buffer.read(target);
        Assert.assertThat(charsRead, CoreMatchers.is(5));
        Assert.assertThat(target.get(0), CoreMatchers.is('T'));
        Assert.assertThat(target.get(4), CoreMatchers.is('M'));
        Assert.assertThat(target.get(5), CoreMatchers.is('\u0000'));
    }

    @Test
    public void readsCharsToSmallBuffer() throws IOException {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        CharBuffer target = CharBuffer.allocate(2);
        int charsRead = buffer.read(target);
        Assert.assertThat(charsRead, CoreMatchers.is(2));
        Assert.assertThat(target.get(0), CoreMatchers.is('T'));
        Assert.assertThat(target.get(1), CoreMatchers.is('e'));
    }

    @Test
    public void readsCharsToEmptyBuffer() throws IOException {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        CharBuffer target = CharBuffer.allocate(2);
        target.position(2);
        int charsRead = buffer.read(target);
        Assert.assertThat(charsRead, CoreMatchers.is(0));
    }

    @Test
    public void readsCharsFromEmptyBuffer() throws IOException {
        char[] array = new char[]{ 'T', 'e', 'a', 'V', 'M' };
        CharBuffer buffer = CharBuffer.wrap(array);
        CharBuffer target = CharBuffer.allocate(2);
        buffer.position(5);
        int charsRead = buffer.read(target);
        Assert.assertThat(charsRead, CoreMatchers.is((-1)));
    }

    @Test
    public void wrapsCharSequence() {
        CharBuffer buffer = CharBuffer.wrap("TeaVM", 2, 4);
        Assert.assertThat(buffer.capacity(), CoreMatchers.is(5));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(4));
        Assert.assertThat(buffer.position(), CoreMatchers.is(2));
    }

    @Test
    public void putsString() {
        CharBuffer buffer = CharBuffer.allocate(100);
        buffer.put("TeaVM");
        Assert.assertThat(buffer.flip().toString(), CoreMatchers.is("TeaVM"));
    }
}

