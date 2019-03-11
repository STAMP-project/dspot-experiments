/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.jimfs;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link JimfsInputStream}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
@SuppressWarnings("ResultOfMethodCallIgnored")
public class JimfsInputStreamTest {
    @Test
    public void testRead_singleByte() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(2);
        assertThat(in.read()).isEqualTo(2);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_wholeArray() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[8];
        assertThat(in.read(bytes)).isEqualTo(8);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 7, 8), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_wholeArray_arrayLarger() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[12];
        assertThat(in.read(bytes)).isEqualTo(8);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_wholeArray_arraySmaller() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[6];
        assertThat(in.read(bytes)).isEqualTo(6);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6), bytes);
        bytes = new byte[6];
        assertThat(in.read(bytes)).isEqualTo(2);
        Assert.assertArrayEquals(TestUtils.bytes(7, 8, 0, 0, 0, 0), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_partialArray() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[12];
        assertThat(in.read(bytes, 0, 8)).isEqualTo(8);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_partialArray_sliceLarger() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[12];
        assertThat(in.read(bytes, 0, 10)).isEqualTo(8);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_partialArray_sliceSmaller() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        byte[] bytes = new byte[12];
        assertThat(in.read(bytes, 0, 6)).isEqualTo(6);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 0, 0, 0, 0, 0, 0), bytes);
        assertThat(in.read(bytes, 6, 6)).isEqualTo(2);
        Assert.assertArrayEquals(TestUtils.bytes(1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0), bytes);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testRead_partialArray_invalidInput() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5);
        try {
            in.read(new byte[3], (-1), 1);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            in.read(new byte[3], 0, 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            in.read(new byte[3], 1, 3);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void testAvailable() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(in.available()).isEqualTo(8);
        assertThat(in.read()).isEqualTo(1);
        assertThat(in.available()).isEqualTo(7);
        assertThat(in.read(new byte[3])).isEqualTo(3);
        assertThat(in.available()).isEqualTo(4);
        assertThat(in.read(new byte[10], 1, 2)).isEqualTo(2);
        assertThat(in.available()).isEqualTo(2);
        assertThat(in.read(new byte[10])).isEqualTo(2);
        assertThat(in.available()).isEqualTo(0);
    }

    @Test
    public void testSkip() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(in.skip(0)).isEqualTo(0);
        assertThat(in.skip((-10))).isEqualTo(0);
        assertThat(in.skip(2)).isEqualTo(2);
        assertThat(in.read()).isEqualTo(3);
        assertThat(in.skip(3)).isEqualTo(3);
        assertThat(in.read()).isEqualTo(7);
        assertThat(in.skip(10)).isEqualTo(1);
        JimfsInputStreamTest.assertEmpty(in);
        assertThat(in.skip(10)).isEqualTo(0);
        JimfsInputStreamTest.assertEmpty(in);
    }

    @SuppressWarnings("GuardedByChecker")
    @Test
    public void testFullyReadInputStream_doesNotChangeStateWhenStoreChanges() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3, 4, 5);
        assertThat(in.read(new byte[5])).isEqualTo(5);
        JimfsInputStreamTest.assertEmpty(in);
        in.file.write(5, new byte[10], 0, 10);// append more bytes to file

        JimfsInputStreamTest.assertEmpty(in);
    }

    @Test
    public void testMark_unsupported() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3);
        assertThat(in.markSupported()).isFalse();
        // mark does nothing
        in.mark(1);
        try {
            // reset throws IOException when unsupported
            in.reset();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void testClosedInputStream_throwsException() throws IOException {
        JimfsInputStream in = JimfsInputStreamTest.newInputStream(1, 2, 3);
        in.close();
        try {
            in.read();
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            in.read(new byte[3]);
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            in.read(new byte[10], 0, 2);
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            in.skip(10);
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            in.available();
            Assert.fail();
        } catch (IOException expected) {
        }
        in.close();// does nothing

    }
}

