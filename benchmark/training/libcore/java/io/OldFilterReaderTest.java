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


import java.io.ByteArrayInputStream;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.TestCase;


public class OldFilterReaderTest extends TestCase {
    private boolean called;

    private FilterReader fr;

    static class MyFilterReader extends FilterReader {
        public MyFilterReader(Reader reader) {
            super(reader);
        }
    }

    class MockReader extends Reader {
        public MockReader() {
        }

        public void close() throws IOException {
            called = true;
        }

        public void mark(int readLimit) throws IOException {
            called = true;
        }

        public boolean markSupported() {
            called = true;
            return false;
        }

        public int read() throws IOException {
            called = true;
            return 0;
        }

        public int read(char[] buffer, int offset, int count) throws IOException {
            called = true;
            return 0;
        }

        public boolean ready() throws IOException {
            called = true;
            return true;
        }

        public void reset() throws IOException {
            called = true;
        }

        public long skip(long count) throws IOException {
            called = true;
            return 0;
        }
    }

    public void test_ConstructorLjava_io_Reader() {
        FilterReader myReader = null;
        called = true;
        try {
            myReader = new OldFilterReaderTest.MyFilterReader(null);
            TestCase.fail("NullPointerException expected.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_close() throws IOException {
        fr.close();
        TestCase.assertTrue("close() has not been called.", called);
    }

    public void test_markI() throws IOException {
        fr.mark(0);
        TestCase.assertTrue("mark(int) has not been called.", called);
    }

    public void test_markSupported() {
        fr.markSupported();
        TestCase.assertTrue("markSupported() has not been called.", called);
    }

    public void test_read() throws IOException {
        fr.read();
        TestCase.assertTrue("read() has not been called.", called);
    }

    public void test_read$CII() throws IOException {
        char[] buffer = new char[5];
        fr.read(buffer, 0, 5);
        TestCase.assertTrue("read(char[], int, int) has not been called.", called);
    }

    public void test_read$CII_Exception() throws IOException {
        byte[] bbuffer = new byte[20];
        char[] buffer = new char[10];
        fr = new OldFilterReaderTest.MyFilterReader(new InputStreamReader(new ByteArrayInputStream(bbuffer)));
        try {
            fr.read(buffer, 0, (-1));
            TestCase.fail("Test 1: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            fr.read(buffer, (-1), 1);
            TestCase.fail("Test 2: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            fr.read(buffer, 10, 1);
            TestCase.fail("Test 3: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
    }

    public void test_ready() throws IOException {
        fr.ready();
        TestCase.assertTrue("ready() has not been called.", called);
    }

    public void test_reset() throws IOException {
        fr.reset();
        TestCase.assertTrue("reset() has not been called.", called);
    }

    public void test_skip() throws IOException {
        fr.skip(10);
        TestCase.assertTrue("skip(long) has not been called.", called);
    }
}

