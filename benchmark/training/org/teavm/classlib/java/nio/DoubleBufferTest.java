/**
 * Copyright 2014 Alexey Andreev.
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
import java.nio.DoubleBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class DoubleBufferTest {
    @Test
    public void allocatesSimple() {
        DoubleBuffer buffer = DoubleBuffer.allocate(100);
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
        DoubleBuffer.allocate((-1));
    }

    @Test
    public void wrapsArray() {
        double[] array = new double[100];
        DoubleBuffer buffer = DoubleBuffer.wrap(array, 10, 70);
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
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((double) (23))));
        buffer.put(1, 24);
        Assert.assertThat(array[1], CoreMatchers.is(((double) (24))));
    }

    @Test
    public void errorWhenWrappingWithWrongParameters() {
        double[] array = new double[100];
        try {
            DoubleBuffer.wrap(array, (-1), 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            DoubleBuffer.wrap(array, 101, 10);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            DoubleBuffer.wrap(array, 98, 3);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            DoubleBuffer.wrap(array, 98, (-1));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test
    public void wrapsArrayWithoutOffset() {
        double[] array = new double[100];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        Assert.assertThat(buffer.limit(), CoreMatchers.is(100));
    }

    @Test
    public void createsSlice() {
        DoubleBuffer buffer = DoubleBuffer.allocate(100);
        buffer.put(new double[60]);
        buffer.flip();
        buffer.put(new double[15]);
        DoubleBuffer slice = buffer.slice();
        Assert.assertThat(slice.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(slice.position(), CoreMatchers.is(0));
        Assert.assertThat(slice.capacity(), CoreMatchers.is(45));
        Assert.assertThat(slice.limit(), CoreMatchers.is(45));
        Assert.assertThat(slice.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(slice.isReadOnly(), CoreMatchers.is(false));
        slice.put(3, 23);
        Assert.assertThat(buffer.get(18), CoreMatchers.is(((double) (23))));
        slice.put(24);
        Assert.assertThat(buffer.get(15), CoreMatchers.is(((double) (24))));
        buffer.put(16, 25);
        Assert.assertThat(slice.get(1), CoreMatchers.is(((double) (25))));
    }

    @Test
    public void slicePropertiesSameWithOriginal() {
        DoubleBuffer buffer = DoubleBuffer.allocate(100).asReadOnlyBuffer().slice();
        Assert.assertThat(buffer.isReadOnly(), CoreMatchers.is(true));
    }

    @Test
    public void createsDuplicate() {
        DoubleBuffer buffer = DoubleBuffer.allocate(100);
        buffer.put(new double[60]);
        buffer.flip();
        buffer.put(new double[15]);
        DoubleBuffer duplicate = buffer.duplicate();
        Assert.assertThat(duplicate.array(), CoreMatchers.is(buffer.array()));
        Assert.assertThat(duplicate.position(), CoreMatchers.is(15));
        Assert.assertThat(duplicate.capacity(), CoreMatchers.is(100));
        Assert.assertThat(duplicate.limit(), CoreMatchers.is(60));
        Assert.assertThat(duplicate.isDirect(), CoreMatchers.is(false));
        Assert.assertThat(duplicate.isReadOnly(), CoreMatchers.is(false));
        duplicate.put(3, 23);
        Assert.assertThat(buffer.get(3), CoreMatchers.is(((double) (23))));
        duplicate.put(24);
        Assert.assertThat(buffer.get(15), CoreMatchers.is(((double) (24))));
        buffer.put(1, 25);
        Assert.assertThat(duplicate.get(1), CoreMatchers.is(((double) (25))));
        Assert.assertThat(duplicate.array(), CoreMatchers.is(CoreMatchers.sameInstance(buffer.array())));
    }

    @Test
    public void getsDouble() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        Assert.assertThat(buffer.get(), CoreMatchers.is(((double) (2))));
        Assert.assertThat(buffer.get(), CoreMatchers.is(((double) (3))));
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(), CoreMatchers.is(((double) (5))));
        Assert.assertThat(buffer.get(), CoreMatchers.is(((double) (7))));
    }

    @Test
    public void gettingDoubleFromEmptyBufferCausesError() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
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
    public void putsDouble() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.put(2).put(3).put(5).put(7);
        Assert.assertThat(array, CoreMatchers.is(new double[]{ 2, 3, 5, 7 }));
    }

    @Test
    public void puttingDoubleToEmptyBufferCausesError() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.limit(2);
        buffer.put(2).put(3);
        try {
            buffer.put(5);
            Assert.fail("Should have thrown error");
        } catch (BufferOverflowException e) {
            Assert.assertThat(array[2], CoreMatchers.is(((double) (0))));
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingDoubleToReadOnlyBufferCausesError() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put(2);
    }

    @Test
    public void getsDoubleFromGivenLocation() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        Assert.assertThat(buffer.get(0), CoreMatchers.is(((double) (2))));
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((double) (3))));
        buffer.get();
        buffer = buffer.slice();
        Assert.assertThat(buffer.get(1), CoreMatchers.is(((double) (5))));
        Assert.assertThat(buffer.get(2), CoreMatchers.is(((double) (7))));
    }

    @Test
    public void gettingDoubleFromWrongLocationCausesError() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
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
    public void putsDoubleToGivenLocation() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.put(0, 2);
        buffer.put(1, 3);
        buffer.get();
        buffer = buffer.slice();
        buffer.put(1, 5);
        buffer.put(2, 7);
        Assert.assertThat(array, CoreMatchers.is(new double[]{ 2, 3, 5, 7 }));
    }

    @Test
    public void puttingDoubleToWrongLocationCausesError() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.limit(3);
        try {
            buffer.put((-1), 2);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            buffer.put(3, 2);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void puttingDoubleToGivenLocationOfReadOnlyBufferCausesError() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array).asReadOnlyBuffer();
        buffer.put(0, 2);
    }

    @Test
    public void getsDoubles() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.get();
        double[] receiver = new double[2];
        buffer.get(receiver, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(receiver, CoreMatchers.is(new double[]{ 3, 5 }));
    }

    @Test
    public void gettingDoublesFromEmptyBufferCausesError() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.limit(3);
        double[] receiver = new double[4];
        try {
            buffer.get(receiver, 0, 4);
            Assert.fail("Error expected");
        } catch (BufferUnderflowException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new double[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void gettingDoublesWithIllegalArgumentsCausesError() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        double[] receiver = new double[4];
        try {
            buffer.get(receiver, 0, 5);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new double[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, (-1), 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new double[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
        try {
            buffer.get(receiver, 6, 3);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertThat(receiver, CoreMatchers.is(new double[4]));
            Assert.assertThat(buffer.position(), CoreMatchers.is(0));
        }
    }

    @Test
    public void putsDoubles() {
        double[] array = new double[4];
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.get();
        double[] data = new double[]{ 2, 3 };
        buffer.put(data, 0, 2);
        Assert.assertThat(buffer.position(), CoreMatchers.is(3));
        Assert.assertThat(array, CoreMatchers.is(new double[]{ 0, 2, 3, 0 }));
    }

    @Test
    public void compacts() {
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.get();
        buffer.mark();
        buffer.compact();
        Assert.assertThat(array, CoreMatchers.is(new double[]{ 3, 5, 7, 7 }));
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
        double[] array = new double[]{ 2, 3, 5, 7 };
        DoubleBuffer buffer = DoubleBuffer.wrap(array);
        buffer.position(1);
        buffer.mark();
        buffer.position(2);
        buffer.reset();
        Assert.assertThat(buffer.position(), CoreMatchers.is(1));
    }
}

