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
package org.apache.harmony.tests.java.io;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.util.Arrays;
import junit.framework.TestCase;


public class InputStreamReaderTest extends TestCase {
    static class LimitedByteArrayInputStream extends ByteArrayInputStream {
        // A ByteArrayInputStream that only returns a single byte per read
        byte[] bytes;

        int count;

        public LimitedByteArrayInputStream(int type) {
            super(new byte[0]);
            switch (type) {
                case 0 :
                    bytes = new byte[]{ 97, 114 };
                    break;
                case 1 :
                    bytes = new byte[]{ ((byte) (255)), ((byte) (254)), 97, 114 };
                    break;
                case 2 :
                    bytes = new byte[]{ '\u001b', '$', 'B', '6', 'e', 'B', 'h', '\u001b', '(', 'B' };
                    break;
            }
            count = bytes.length;
        }

        @Override
        public int available() {
            return count;
        }

        @Override
        public int read() {
            if ((count) == 0) {
                return -1;
            }
            (count)--;
            return bytes[((bytes.length) - (count))];
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            if ((count) == 0) {
                return -1;
            }
            if (length == 0) {
                return 0;
            }
            buffer[offset] = bytes[((bytes.length) - (count))];
            (count)--;
            return 1;
        }
    }

    public String fileString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\n";

    private InputStream fis;

    private InputStream in;

    private InputStreamReader is;

    private InputStreamReader reader;

    private final String source = "This is a test message with Unicode character. \u4e2d\u56fd is China\'s name in Chinese";

    /**
     * java.io.InputStreamReader#close()
     */
    public void test_close() throws IOException {
        is.close();
        try {
            is.read();
            TestCase.fail("Should throw IOException");
        } catch (IOException e) {
            // Expected
        }
        reader.close();
        try {
            reader.ready();
            TestCase.fail("Should throw IOException");
        } catch (IOException e) {
            // Expected
        }
        // Should be a no-op
        reader.close();
        // Tests after reader closed
        in = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("org/apache/harmony/luni/tests/java/io/testfile-utf8.txt"));
        reader = new InputStreamReader(in, "utf-8");
        in.close();
        try {
            int count = reader.read(new char[1]);
            TestCase.fail(("count:" + count));
        } catch (IOException e) {
            // Expected
        }
        try {
            reader.read();
            TestCase.fail();
        } catch (IOException e) {
            // Expected
        }
        TestCase.assertFalse(reader.ready());
        Charset cs = Charset.forName("utf-8");
        TestCase.assertEquals(cs, Charset.forName(reader.getEncoding()));
    }

    /**
     * java.io.InputStreamReader#InputStreamReader(java.io.InputStream)
     */
    public void test_ConstructorLjava_io_InputStream() throws IOException {
        try {
            reader = new InputStreamReader(null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        InputStreamReader reader2 = new InputStreamReader(in);
        reader2.close();
    }

    /**
     * java.io.InputStreamReader#InputStreamReader(java.io.InputStream,
     * java.lang.String)
     */
    public void test_ConstructorLjava_io_InputStreamLjava_lang_String() throws IOException {
        is = new InputStreamReader(fis, "8859_1");
        try {
            is = new InputStreamReader(fis, "Bogus");
            TestCase.fail("Failed to throw Unsupported Encoding exception");
        } catch (UnsupportedEncodingException e) {
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            reader = new InputStreamReader(null, "utf-8");
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            reader = new InputStreamReader(in, ((String) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            reader = new InputStreamReader(in, "");
            TestCase.fail();
        } catch (UnsupportedEncodingException e) {
            // Expected
        }
        try {
            reader = new InputStreamReader(in, "badname");
            TestCase.fail();
        } catch (UnsupportedEncodingException e) {
            // Expected
        }
        InputStreamReader reader2 = new InputStreamReader(in, "utf-8");
        TestCase.assertEquals(Charset.forName(reader2.getEncoding()), Charset.forName("utf-8"));
        reader2.close();
        reader2 = new InputStreamReader(in, "utf8");
        TestCase.assertEquals(Charset.forName(reader2.getEncoding()), Charset.forName("utf-8"));
        reader2.close();
    }

    /**
     * java.io.InputStreamReader(java.io.InputStream,
     * java.nio.charset.Charset)
     */
    public void test_ConstructorLjava_io_InputStreamLjava_nio_charset_Charset() throws IOException {
        Charset cs = Charset.forName("utf-8");
        try {
            reader = new InputStreamReader(null, cs);
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            reader = new InputStreamReader(in, ((Charset) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        InputStreamReader reader2 = new InputStreamReader(in, cs);
        TestCase.assertEquals(Charset.forName(reader2.getEncoding()), cs);
        reader2.close();
    }

    /**
     * java.io.InputStreamReader(java.io.InputStream,
     * java.nio.charset.CharsetDecoder)
     */
    public void test_ConstructorLjava_io_InputStreamLjava_nio_charset_CharsetDecoder() throws IOException {
        CharsetDecoder decoder = Charset.forName("utf-8").newDecoder();
        try {
            reader = new InputStreamReader(null, decoder);
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            reader = new InputStreamReader(in, ((CharsetDecoder) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        InputStreamReader reader2 = new InputStreamReader(in, decoder);
        TestCase.assertEquals(Charset.forName(reader2.getEncoding()), decoder.charset());
        reader2.close();
    }

    /**
     * Unlike the RI, we return a canonical encoding name and not something
     * java specific.
     */
    public void test_getEncoding() throws IOException {
        InputStreamReader isr = new InputStreamReader(fis, "8859_1");
        TestCase.assertEquals("ISO-8859-1", isr.getEncoding());
        isr = new InputStreamReader(fis, "ISO-8859-1");
        TestCase.assertEquals("ISO-8859-1", isr.getEncoding());
        byte[] b = new byte[5];
        isr = new InputStreamReader(new ByteArrayInputStream(b), "UTF-16BE");
        isr.close();
        TestCase.assertNull(isr.getEncoding());
        try {
            isr = new InputStreamReader(System.in, "UTF-16BE");
        } catch (UnsupportedEncodingException e) {
            // Ignored
        }
        TestCase.assertEquals("UTF-16BE", isr.getEncoding());
    }

    /**
     * java.io.InputStreamReader#read()
     */
    public void test_read() throws IOException {
        TestCase.assertEquals('T', ((char) (reader.read())));
        TestCase.assertEquals('h', ((char) (reader.read())));
        TestCase.assertEquals('i', ((char) (reader.read())));
        TestCase.assertEquals('s', ((char) (reader.read())));
        TestCase.assertEquals(' ', ((char) (reader.read())));
        reader.read(new char[(source.length()) - 5], 0, ((source.length()) - 5));
        TestCase.assertEquals((-1), reader.read());
        int c = is.read();
        TestCase.assertTrue("returned incorrect char", (((char) (c)) == (fileString.charAt(0))));
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(new byte[]{ ((byte) (232)), ((byte) (157)), ((byte) (165)) }), "UTF8");
        TestCase.assertTrue("wrong double byte char", ((reader.read()) == '\u8765'));
        // Regression for HARMONY-166
        InputStream in;
        in = new InputStreamReaderTest.LimitedByteArrayInputStream(0);
        reader = new InputStreamReader(in, "UTF-16BE");
        TestCase.assertEquals("Incorrect byte UTF-16BE", '\u6172', reader.read());
        in = new InputStreamReaderTest.LimitedByteArrayInputStream(0);
        reader = new InputStreamReader(in, "UTF-16LE");
        TestCase.assertEquals("Incorrect byte UTF-16BE", '\u7261', reader.read());
        in = new InputStreamReaderTest.LimitedByteArrayInputStream(1);
        reader = new InputStreamReader(in, "UTF-16");
        TestCase.assertEquals("Incorrect byte UTF-16BE", '\u7261', reader.read());
        /* Temporarily commented out due to lack of ISO2022 support in ICU4J 3.8
        in = new LimitedByteArrayInputStream(2); reader = new
        InputStreamReader(in, "ISO2022JP"); assertEquals("Incorrect byte
        ISO2022JP 1", '\u4e5d', reader.read()); assertEquals("Incorrect byte
        ISO2022JP 2", '\u7b2c', reader.read());
         */
    }

    /* Class under test for int read() Regression for Harmony-411 */
    public void test_read_1() throws IOException {
        // if the decoder is constructed by InputStreamReader itself, the
        // decoder's default error action is REPLACE
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(new byte[]{ -32, -96 }), "UTF-8");
        TestCase.assertEquals("read() return incorrect value", 65533, isr.read());
        InputStreamReader isr2 = new InputStreamReader(new ByteArrayInputStream(new byte[]{ -32, -96 }), Charset.forName("UTF-8"));
        TestCase.assertEquals("read() return incorrect value", 65533, isr2.read());
        // if the decoder is passed in, keep its status intact
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        InputStreamReader isr3 = new InputStreamReader(new ByteArrayInputStream(new byte[]{ -32, -96 }), decoder);
        try {
            isr3.read();
            TestCase.fail("Should throw MalformedInputException");
        } catch (MalformedInputException e) {
            // expected
        }
        CharsetDecoder decoder2 = Charset.forName("UTF-8").newDecoder();
        decoder2.onMalformedInput(CodingErrorAction.IGNORE);
        InputStreamReader isr4 = new InputStreamReader(new ByteArrayInputStream(new byte[]{ -32, -96 }), decoder2);
        TestCase.assertEquals("read() return incorrect value", (-1), isr4.read());
        CharsetDecoder decoder3 = Charset.forName("UTF-8").newDecoder();
        decoder3.onMalformedInput(CodingErrorAction.REPLACE);
        InputStreamReader isr5 = new InputStreamReader(new ByteArrayInputStream(new byte[]{ -32, -96 }), decoder3);
        TestCase.assertEquals("read() return incorrect value", 65533, isr5.read());
    }

    public void test_read_specialCharset() throws IOException {
        reader.close();
        in = this.getClass().getClassLoader().getResourceAsStream("tests/api/java/io/testfile-utf8.txt");
        reader = new InputStreamReader(in, "utf-8");
        int c;
        StringBuffer sb = new StringBuffer();
        while ((c = reader.read()) != (-1)) {
            sb.append(((char) (c)));
        } 
        // delete BOM
        TestCase.assertEquals(source, sb.deleteCharAt(0).toString());
        /* J2ObjC: Encoding "gb18030" is not supported.
        sb.setLength(0);
        reader.close();
        in = this.getClass().getClassLoader().getResourceAsStream(
        "tests/api/java/io/testfile.txt");

        reader = new InputStreamReader(in, "gb18030");
        while ((c = reader.read()) != -1) {
        sb.append((char) c);
        }
        assertEquals(source, sb.toString());
         */
    }

    /**
     * java.io.InputStreamReader#read(char[], int, int)
     */
    public void test_read$CII() throws IOException {
        char[] rbuf = new char[100];
        char[] sbuf = new char[100];
        fileString.getChars(0, 100, sbuf, 0);
        is.read(rbuf, 0, 100);
        for (int i = 0; i < (rbuf.length); i++) {
            TestCase.assertTrue("returned incorrect chars", ((rbuf[i]) == (sbuf[i])));
        }
        // Test successive reads
        byte[] data = new byte[8192 * 2];
        Arrays.fill(data, ((byte) (116)));// 116 = ISO-8859-1 value for 't'

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        InputStreamReader isr = new InputStreamReader(bis, "ISO-8859-1");
        // One less than the InputStreamReader.BUFFER_SIZE
        char[] buf = new char[8191];
        int bytesRead = isr.read(buf, 0, buf.length);
        TestCase.assertFalse(((-1) == bytesRead));
        bytesRead = isr.read(buf, 0, buf.length);
        TestCase.assertFalse(((-1) == bytesRead));
        bis = new ByteArrayInputStream(source.getBytes("UTF-8"));
        isr = new InputStreamReader(in, "UTF-8");
        char[] chars = new char[source.length()];
        TestCase.assertEquals(((source.length()) - 3), isr.read(chars, 0, ((chars.length) - 3)));
        TestCase.assertEquals(3, isr.read(chars, 0, 10));
    }

    /* Class under test for int read(char[], int, int) */
    public void test_read$CII_1() throws IOException {
        try {
            reader.read(null, (-1), 1);
            TestCase.fail();
        } catch (NullPointerException expected) {
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            reader.read(null, 0, (-1));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            reader.read(null, 0, 1);
            TestCase.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            reader.read(new char[3], (-1), 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            reader.read(new char[3], 0, (-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            reader.read(new char[3], 1, 3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        TestCase.assertEquals(0, reader.read(new char[3], 3, 0));
        char[] chars = new char[source.length()];
        TestCase.assertEquals(0, reader.read(chars, 0, 0));
        TestCase.assertEquals(0, chars[0]);
        TestCase.assertEquals(3, reader.read(chars, 0, 3));
        TestCase.assertEquals(5, reader.read(chars, 3, 5));
        TestCase.assertEquals(((source.length()) - 8), reader.read(chars, 8, ((chars.length) - 8)));
        TestCase.assertTrue(Arrays.equals(chars, source.toCharArray()));
        TestCase.assertEquals((-1), reader.read(chars, 0, chars.length));
        TestCase.assertTrue(Arrays.equals(chars, source.toCharArray()));
    }

    /**
     * java.io.InputStreamReader#ready()
     */
    public void test_ready() throws IOException {
        TestCase.assertTrue("Ready test failed", is.ready());
        is.read();
        TestCase.assertTrue("More chars, but not ready", is.ready());
        TestCase.assertTrue(reader.ready());
        reader.read(new char[source.length()]);
        TestCase.assertFalse(reader.ready());
    }
}

