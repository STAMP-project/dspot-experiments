/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.util.zip;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import junit.framework.TestCase;
import libcore.io.Streams;


public class DeflaterInputStreamTest extends TestCase {
    private static final String TEST_STR = "Hi,this is a test";

    private static final byte[] TEST_STRING_DEFLATED_BYTES = new byte[]{ 120, -100, -13, -56, -44, 41, -55, -56, 44, 86, 0, -94, 68, -123, -110, -44, -30, 18, 0, 52, 34, 5, -13 };

    private InputStream is;

    /**
     * DeflaterInputStream#available()
     */
    public void testAvailable() throws IOException {
        byte[] buf = new byte[1024];
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(120, dis.read());
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(22, dis.read(buf, 0, 1024));
        TestCase.assertEquals(0, dis.available());
        TestCase.assertEquals((-1), dis.read());
        TestCase.assertEquals(0, dis.available());
        dis.close();
        try {
            dis.available();
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * DeflaterInputStream#close()
     */
    public void testClose() throws IOException {
        byte[] buf = new byte[1024];
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        dis.close();
        try {
            dis.available();
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(buf, 0, 1024);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        // can close after close
        dis.close();
    }

    /**
     * DeflaterInputStream#mark()
     */
    public void testMark() throws IOException {
        // mark do nothing
        DeflaterInputStream dis = new DeflaterInputStream(is);
        dis.mark((-1));
        dis.mark(0);
        dis.mark(1);
        dis.close();
        dis.mark(1);
    }

    /**
     * DeflaterInputStream#markSupported()
     */
    public void testMarkSupported() throws IOException {
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertFalse(dis.markSupported());
        dis.close();
        TestCase.assertFalse(dis.markSupported());
    }

    /**
     * DeflaterInputStream#read()
     */
    public void testRead() throws IOException {
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(120, dis.read());
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(156, dis.read());
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(243, dis.read());
        TestCase.assertEquals(1, dis.available());
        dis.close();
        try {
            dis.read();
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    public void testRead_golden() throws Exception {
        DeflaterInputStream dis = new DeflaterInputStream(is);
        byte[] contents = Streams.readFully(dis);
        TestCase.assertTrue(Arrays.equals(DeflaterInputStreamTest.TEST_STRING_DEFLATED_BYTES, contents));
        byte[] result = new byte[32];
        dis = new DeflaterInputStream(new ByteArrayInputStream(DeflaterInputStreamTest.TEST_STR.getBytes("UTF-8")));
        int count = 0;
        int bytesRead = 0;
        while ((bytesRead = dis.read(result, count, 4)) != (-1)) {
            count += bytesRead;
        } 
        TestCase.assertEquals(23, count);
        byte[] splicedResult = new byte[23];
        System.arraycopy(result, 0, splicedResult, 0, 23);
        TestCase.assertTrue(Arrays.equals(DeflaterInputStreamTest.TEST_STRING_DEFLATED_BYTES, splicedResult));
    }

    public void testRead_leavesBufUnmodified() throws Exception {
        DeflaterInputStreamTest.DeflaterInputStreamWithPublicBuffer dis = new DeflaterInputStreamTest.DeflaterInputStreamWithPublicBuffer(is);
        byte[] contents = Streams.readFully(dis);
        TestCase.assertTrue(Arrays.equals(DeflaterInputStreamTest.TEST_STRING_DEFLATED_BYTES, contents));
        // protected field buf is a part of the public API of this class.
        // we guarantee that it's only used as an input buffer, and not for
        // anything else.
        byte[] buf = dis.getBuffer();
        byte[] expected = DeflaterInputStreamTest.TEST_STR.getBytes("UTF-8");
        byte[] splicedBuf = new byte[expected.length];
        System.arraycopy(buf, 0, splicedBuf, 0, splicedBuf.length);
        TestCase.assertTrue(Arrays.equals(expected, splicedBuf));
    }

    /**
     * DeflaterInputStream#read(byte[], int, int)
     */
    public void testReadByteArrayIntInt() throws IOException {
        byte[] buf1 = new byte[256];
        byte[] buf2 = new byte[256];
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertEquals(23, dis.read(buf1, 0, 256));
        dis = new DeflaterInputStream(is);
        TestCase.assertEquals(8, dis.read(buf2, 0, 256));
        is = new ByteArrayInputStream(DeflaterInputStreamTest.TEST_STR.getBytes("UTF-8"));
        dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(120, dis.read());
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(22, dis.read(buf2, 0, 256));
        TestCase.assertEquals(0, dis.available());
        TestCase.assertEquals((-1), dis.read());
        TestCase.assertEquals(0, dis.available());
        try {
            dis.read(buf1, 0, 512);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            dis.read(null, 0, 0);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            dis.read(null, (-1), 0);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            dis.read(null, (-1), (-1));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            dis.read(buf1, (-1), (-1));
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            dis.read(buf1, 0, (-1));
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        dis.close();
        try {
            dis.read(buf1, 0, 512);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(buf1, 0, 1);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(null, 0, 0);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(null, (-1), 0);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(null, (-1), (-1));
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(buf1, (-1), (-1));
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        try {
            dis.read(buf1, 0, (-1));
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * DeflaterInputStream#reset()
     */
    public void testReset() throws IOException {
        DeflaterInputStream dis = new DeflaterInputStream(is);
        try {
            dis.reset();
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        dis.close();
        try {
            dis.reset();
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * DeflaterInputStream#skip()
     */
    public void testSkip() throws IOException {
        byte[] buf = new byte[1024];
        DeflaterInputStream dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        dis.skip(1);
        TestCase.assertEquals(1, dis.available());
        TestCase.assertEquals(22, dis.read(buf, 0, 1024));
        TestCase.assertEquals(0, dis.available());
        TestCase.assertEquals(0, dis.available());
        is = new ByteArrayInputStream(DeflaterInputStreamTest.TEST_STR.getBytes("UTF-8"));
        dis = new DeflaterInputStream(is);
        TestCase.assertEquals(1, dis.available());
        dis.skip(56);
        TestCase.assertEquals(0, dis.available());
        TestCase.assertEquals((-1), dis.read(buf, 0, 1024));
        TestCase.assertEquals(0, dis.available());
        // can still skip
        dis.skip(1);
        dis.close();
        try {
            dis.skip(1);
            TestCase.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
        is = new ByteArrayInputStream(DeflaterInputStreamTest.TEST_STR.getBytes("UTF-8"));
        dis = new DeflaterInputStream(is);
        TestCase.assertEquals(23, dis.skip(Long.MAX_VALUE));
        TestCase.assertEquals(0, dis.available());
    }

    /**
     * DeflaterInputStream#DeflaterInputStream(InputStream)
     */
    public void testDeflaterInputStreamInputStream() {
        // ok
        new DeflaterInputStream(is);
        // fail
        try {
            new DeflaterInputStream(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * DataFormatException#DataFormatException()
     */
    public void testDataFormatException() {
        new DataFormatException();
    }

    /**
     * DeflaterInputStream#DeflaterInputStream(InputStream, Deflater)
     */
    public void testDeflaterInputStreamInputStreamDeflater() {
        // ok
        new DeflaterInputStream(is, new Deflater());
        // fail
        try {
            new DeflaterInputStream(is, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new DeflaterInputStream(null, new Deflater());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * DeflaterInputStream#DeflaterInputStream(InputStream, Deflater, int)
     */
    public void testDeflaterInputStreamInputStreamDeflaterInt() {
        // ok
        new DeflaterInputStream(is, new Deflater(), 1024);
        // fail
        try {
            new DeflaterInputStream(is, null, 1024);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new DeflaterInputStream(null, new Deflater(), 1024);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new DeflaterInputStream(is, new Deflater(), (-1));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new DeflaterInputStream(null, new Deflater(), (-1));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new DeflaterInputStream(is, null, (-1));
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public static final class DeflaterInputStreamWithPublicBuffer extends DeflaterInputStream {
        public DeflaterInputStreamWithPublicBuffer(InputStream in) {
            super(in);
        }

        public byte[] getBuffer() {
            return buf;
        }
    }
}

