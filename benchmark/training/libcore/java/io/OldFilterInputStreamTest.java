/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.io;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import junit.framework.TestCase;
import tests.support.Support_ASimpleInputStream;


public class OldFilterInputStreamTest extends TestCase {
    static class MyFilterInputStream extends FilterInputStream {
        public MyFilterInputStream(InputStream is) {
            super(is);
        }
    }

    private String fileName;

    private FilterInputStream is;

    byte[] ibuf = new byte[4096];

    private static final String testString = "Lorem ipsum dolor sit amet,\n" + ("consectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut" + "labore et dolore magna aliqua.\n");

    private static final int testLength = OldFilterInputStreamTest.testString.length();

    public void test_Constructor() {
        // The FilterInputStream object has already been created in setUp().
        // If anything has gone wrong, closing it should throw a
        // NullPointerException.
        try {
            is.close();
        } catch (IOException e) {
            TestCase.fail(("Unexpected IOException: " + (e.getMessage())));
        } catch (NullPointerException npe) {
            TestCase.fail("Unexpected NullPointerException.");
        }
    }

    public void test_available() throws IOException {
        TestCase.assertEquals("Test 1: Returned incorrect number of available bytes;", OldFilterInputStreamTest.testLength, is.available());
        is.close();
        try {
            is.available();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_close() throws IOException {
        is.close();
        try {
            is.read();
            TestCase.fail("Test 1: Read from closed stream succeeded.");
        } catch (IOException e) {
            // Expected.
        }
        Support_ASimpleInputStream sis = new Support_ASimpleInputStream(true);
        is = new OldFilterInputStreamTest.MyFilterInputStream(sis);
        try {
            is.close();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        sis.throwExceptionOnNextUse = false;
    }

    public void test_markI() throws Exception {
        // Test for method void java.io.FilterInputStream.mark(int)
        final int bufSize = 10;
        byte[] buf1 = new byte[bufSize];
        byte[] buf2 = new byte[bufSize];
        // Purpose 1: Check that mark() does nothing if the filtered stream
        // is a FileInputStream.
        is.read(buf1, 0, bufSize);
        is.mark((2 * bufSize));
        is.read(buf1, 0, bufSize);
        try {
            is.reset();
        } catch (IOException e) {
            // Expected
        }
        is.read(buf2, 0, bufSize);
        TestCase.assertFalse("Test 1: mark() should have no effect.", Arrays.equals(buf1, buf2));
        is.close();
        // Purpose 2: Check that mark() in combination with reset() works if
        // the filtered stream is a BufferedInputStream.
        is = new OldFilterInputStreamTest.MyFilterInputStream(new BufferedInputStream(new FileInputStream(fileName), 100));
        is.read(buf1, 0, bufSize);
        is.mark((2 * bufSize));
        is.read(buf1, 0, bufSize);
        is.reset();
        is.read(buf2, 0, bufSize);
        TestCase.assertTrue("Test 2: mark() or reset() has failed.", Arrays.equals(buf1, buf2));
    }

    public void test_markSupported() throws Exception {
        // Test for method boolean java.io.FilterInputStream.markSupported()
        // Test 1: Check that markSupported() returns false for a filtered
        // input stream that is known to not support mark().
        TestCase.assertFalse(("Test 1: markSupported() incorrectly returned true " + "for a FileInputStream."), is.markSupported());
        is.close();
        // Test 2: Check that markSupported() returns true for a filtered
        // input stream that is known to support mark().
        is = new OldFilterInputStreamTest.MyFilterInputStream(new BufferedInputStream(new FileInputStream(fileName), 100));
        TestCase.assertTrue(("Test 2: markSupported() incorrectly returned false " + "for a BufferedInputStream."), is.markSupported());
    }

    public void test_read() throws IOException {
        int c = is.read();
        TestCase.assertEquals("Test 1: Read returned incorrect char;", OldFilterInputStreamTest.testString.charAt(0), c);
        is.close();
        try {
            is.read();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_read$B() throws IOException {
        // Test for method int java.io.FilterInputStream.read(byte [])
        byte[] buf1 = new byte[100];
        is.read(buf1);
        TestCase.assertTrue("Test 1: Failed to read correct data.", new String(buf1, 0, buf1.length).equals(OldFilterInputStreamTest.testString.substring(0, 100)));
        is.close();
        try {
            is.read(buf1);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_read$BII_Exception() throws IOException {
        byte[] buf = null;
        try {
            is.read(buf, (-1), 0);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        buf = new byte[1000];
        try {
            is.read(buf, (-1), 0);
            TestCase.fail("Test 2: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.read(buf, 0, (-1));
            TestCase.fail("Test 3: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.read(buf, (-1), (-1));
            TestCase.fail("Test 4: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.read(buf, 0, 1001);
            TestCase.fail("Test 5: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.read(buf, 1001, 0);
            TestCase.fail("Test 6: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            is.read(buf, 500, 501);
            TestCase.fail("Test 7: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        is.close();
        try {
            is.read(buf, 0, 100);
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_reset() throws Exception {
        // Test for method void java.io.FilterInputStream.reset()
        // Test 1: Check that reset() throws an IOException if the
        // filtered stream is a FileInputStream.
        try {
            is.reset();
            TestCase.fail("Test 1: IOException expected.");
        } catch (IOException e) {
            // expected
        }
        // Test 2: Check that reset() throws an IOException if the
        // filtered stream is a BufferedInputStream but mark() has not
        // yet been called.
        is = new OldFilterInputStreamTest.MyFilterInputStream(new BufferedInputStream(new FileInputStream(fileName), 100));
        try {
            is.reset();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // expected
        }
        // Test 3: Check that reset() in combination with mark()
        // works correctly.
        final int bufSize = 10;
        byte[] buf1 = new byte[bufSize];
        byte[] buf2 = new byte[bufSize];
        is.read(buf1, 0, bufSize);
        is.mark((2 * bufSize));
        is.read(buf1, 0, bufSize);
        try {
            is.reset();
        } catch (IOException e) {
            TestCase.fail("Test 3: Unexpected IOException.");
        }
        is.read(buf2, 0, bufSize);
        TestCase.assertTrue("Test 4: mark() or reset() has failed.", Arrays.equals(buf1, buf2));
    }

    public void test_skipJ() throws IOException {
        byte[] buf1 = new byte[10];
        is.skip(10);
        is.read(buf1, 0, buf1.length);
        TestCase.assertTrue("Test 1: Failed to skip to the correct position.", new String(buf1, 0, buf1.length).equals(OldFilterInputStreamTest.testString.substring(10, 20)));
        is.close();
        try {
            is.read();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }
}

