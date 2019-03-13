/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.util;


import Charsets.UTF_8;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ExposedByteArrayOutputStream}.
 */
@RunWith(JUnit4.class)
public class ExposedByteArrayOutputStreamTest {
    private static final byte[] TEST_DATA = "Hello World!".getBytes(UTF_8);

    private ExposedByteArrayOutputStream exposedStream = new ExposedByteArrayOutputStream();

    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    @Test
    public void testNoWrite() {
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteZeroLengthArray() throws IOException {
        writeToBoth(new byte[0]);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteZeroLengthArrayWithOffset() {
        writeToBoth(new byte[0], 0, 0);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleByte() {
        writeToBoth(32);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleByteTwice() {
        writeToBoth(32);
        writeToBoth(32);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleArray() throws IOException {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertNotSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWriteSingleArrayFast() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWriteSingleArrayTwice() throws IOException {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleArrayTwiceFast() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleArrayTwiceFast1() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleArrayTwiceFast2() throws IOException {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteSingleArrayWithLength() {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA, 0, ExposedByteArrayOutputStreamTest.TEST_DATA.length);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertNotSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWritePartial() {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA, 0, ((ExposedByteArrayOutputStreamTest.TEST_DATA.length) - 1));
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWritePartialWithNonZeroBegin() {
        writeToBoth(ExposedByteArrayOutputStreamTest.TEST_DATA, 1, ((ExposedByteArrayOutputStreamTest.TEST_DATA.length) - 1));
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteByteAfterWriteArrayFast() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBoth(32);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteArrayFastAfterByte() throws IOException {
        writeToBoth(32);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testResetAfterWriteFast() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        resetBoth();
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteArrayFastAfterReset() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        resetBoth();
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWriteArrayFastAfterReset1() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        resetBoth();
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWriteArrayFastAfterReset2() throws IOException {
        resetBoth();
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
        Assert.assertSame(ExposedByteArrayOutputStreamTest.TEST_DATA, exposedStream.toByteArray());
    }

    @Test
    public void testWriteArrayFastTwiceAfterReset() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        resetBoth();
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteArrayFastTwiceAfterReset1() throws IOException {
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        resetBoth();
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        writeToBothFast(ExposedByteArrayOutputStreamTest.TEST_DATA);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteByteAfterReset() {
        writeToBoth(32);
        resetBoth();
        writeToBoth(32);
        assertStreamContentsEquals(stream, exposedStream);
    }

    @Test
    public void testWriteByteAfterReset1() {
        resetBoth();
        writeToBoth(32);
        assertStreamContentsEquals(stream, exposedStream);
    }
}

