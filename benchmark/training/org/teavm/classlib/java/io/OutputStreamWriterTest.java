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
package org.teavm.classlib.java.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class OutputStreamWriterTest {
    private static final int UPPER = 55296;

    private static final int BUFFER_SIZE = 10000;

    private static final String source = "This is a test message with Unicode character. " + "\u4e2d\u56fd is China\'s name in Chinese";

    private static final String[] MINIMAL_CHARSETS = new String[]{ "UTF-8" };

    OutputStreamWriter osw;

    InputStreamReader isr;

    String testString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream" + "\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\n";

    private ByteArrayOutputStream out;

    private OutputStreamWriter writer;

    private ByteArrayOutputStream fos;

    public OutputStreamWriterTest() throws UnsupportedEncodingException {
        out = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(out, "utf-8");
        fos = new ByteArrayOutputStream();
        osw = new OutputStreamWriter(fos);
    }

    @Test
    public void testClose() throws Exception {
        writer.flush();
        writer.close();
        try {
            writer.flush();
            Assert.fail();
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    public void testFlush() throws Exception {
        writer.write(OutputStreamWriterTest.source);
        writer.flush();
        String result = out.toString("utf-8");
        Assert.assertEquals(OutputStreamWriterTest.source, result);
    }

    @Test
    public void testWritecharArrayintint() throws IOException {
        char[] chars = OutputStreamWriterTest.source.toCharArray();
        // Throws IndexOutOfBoundsException if offset is negative
        try {
            writer.write(((char[]) (null)), (-1), (-1));
            Assert.fail("should throw IndexOutOfBoundsException");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // Expected
        }
        // throws NullPointerException though count is negative
        try {
            writer.write(((char[]) (null)), 1, (-1));
            Assert.fail("should throw NullPointerException");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write(((char[]) (null)), 1, 1);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            writer.write(new char[0], 0, 1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write(chars, (-1), 1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write(chars, 0, (-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write(chars, 1, chars.length);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        writer.write(chars, 1, 2);
        writer.flush();
        Assert.assertEquals("hi", out.toString("utf-8"));
        writer.write(chars, 0, chars.length);
        writer.flush();
        Assert.assertEquals(("hi" + (OutputStreamWriterTest.source)), out.toString("utf-8"));
        writer.close();
        // After the stream is closed, should throw IOException first
        try {
            writer.write(((char[]) (null)), (-1), (-1));
            Assert.fail("should throw IOException");
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    public void testWriteint() throws IOException {
        writer.write(1);
        writer.flush();
        String str = new String(out.toByteArray(), "utf-8");
        Assert.assertEquals("\u0001", str);
        writer.write(2);
        writer.flush();
        str = new String(out.toByteArray(), "utf-8");
        Assert.assertEquals("\u0001\u0002", str);
        writer.write((-1));
        writer.flush();
        str = new String(out.toByteArray(), "utf-8");
        Assert.assertEquals("\u0001\u0002\uffff", str);
        writer.write(1043915);
        writer.flush();
        str = new String(out.toByteArray(), "utf-8");
        Assert.assertEquals("\u0001\u0002\uffff\uedcb", str);
        writer.close();
        // After the stream is closed, should throw IOException
        try {
            writer.write(1);
            Assert.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testWriteStringintint() throws IOException {
        try {
            writer.write(((String) (null)), 1, 1);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            writer.write("", 0, 1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write("abc", (-1), 1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write("abc", 0, (-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write("abc", 1, 3);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Throws IndexOutOfBoundsException before NullPointerException if count
        // is negative
        try {
            writer.write(((String) (null)), (-1), (-1));
            Assert.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // Expected
        }
        // Throws NullPointerException before StringIndexOutOfBoundsException
        try {
            writer.write(((String) (null)), (-1), 0);
            Assert.fail("should throw NullPointerException");
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // expected
        }
        writer.write("abc", 1, 2);
        writer.flush();
        Assert.assertEquals("bc", out.toString("utf-8"));
        writer.write(OutputStreamWriterTest.source, 0, OutputStreamWriterTest.source.length());
        writer.flush();
        Assert.assertEquals(("bc" + (OutputStreamWriterTest.source)), out.toString("utf-8"));
        writer.close();
        // Throws IndexOutOfBoundsException first if count is negative
        try {
            writer.write(((String) (null)), 0, (-1));
            Assert.fail("should throw IndexOutOfBoundsException");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write(((String) (null)), (-1), 0);
            Assert.fail("should throw NullPointerException");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            writer.write("abc", (-1), 0);
            Assert.fail("should throw StringIndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Throws IOException
        try {
            writer.write("abc", 0, 1);
            Assert.fail("should throw IOException");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testOutputStreamWriterOutputStream() throws IOException {
        try {
            writer = new OutputStreamWriter(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        OutputStreamWriter writer2 = new OutputStreamWriter(out);
        writer2.close();
    }

    @Test
    public void testOutputStreamWriterOutputStreamString() throws IOException {
        try {
            writer = new OutputStreamWriter(null, "utf-8");
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            writer = new OutputStreamWriter(out, "");
            Assert.fail();
        } catch (UnsupportedEncodingException e) {
            // Expected
        }
        try {
            writer = new OutputStreamWriter(out, "badname");
            Assert.fail();
        } catch (UnsupportedEncodingException e) {
            // Expected
        }
        try {
            writer = new OutputStreamWriter(out, ((String) (null)));
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testSingleCharIO() throws Exception {
        InputStreamReader isr = null;
        for (int i = 0; i < (OutputStreamWriterTest.MINIMAL_CHARSETS.length); ++i) {
            try {
                out = new ByteArrayOutputStream();
                writer = new OutputStreamWriter(out, OutputStreamWriterTest.MINIMAL_CHARSETS[i]);
                int upper = OutputStreamWriterTest.UPPER;
                switch (i) {
                    case 0 :
                        upper = 128;
                        break;
                    case 1 :
                        upper = 256;
                        break;
                }
                for (int c = 0; c < upper; ++c) {
                    writer.write(c);
                }
                writer.flush();
                byte[] result = out.toByteArray();
                isr = new InputStreamReader(new ByteArrayInputStream(result), OutputStreamWriterTest.MINIMAL_CHARSETS[i]);
                for (int expected = 0; expected < upper; ++expected) {
                    Assert.assertEquals(("Error when reading bytes in " + (OutputStreamWriterTest.MINIMAL_CHARSETS[i])), expected, isr.read());
                }
            } finally {
                try {
                    isr.close();
                } catch (Exception e) {
                    // ok
                }
                try {
                    writer.close();
                } catch (Exception e) {
                    // ok
                }
            }
        }
    }

    @Test
    public void testBlockIO() throws Exception {
        InputStreamReader isr = null;
        char[] largeBuffer = new char[OutputStreamWriterTest.BUFFER_SIZE];
        for (int i = 0; i < (OutputStreamWriterTest.MINIMAL_CHARSETS.length); ++i) {
            try {
                out = new ByteArrayOutputStream();
                writer = new OutputStreamWriter(out, OutputStreamWriterTest.MINIMAL_CHARSETS[i]);
                int upper = OutputStreamWriterTest.UPPER;
                switch (i) {
                    case 0 :
                        upper = 128;
                        break;
                    case 1 :
                        upper = 256;
                        break;
                }
                int m = 0;
                for (int c = 0; c < upper; ++c) {
                    largeBuffer[(m++)] = ((char) (c));
                    if (m == (OutputStreamWriterTest.BUFFER_SIZE)) {
                        writer.write(largeBuffer);
                        m = 0;
                    }
                }
                writer.write(largeBuffer, 0, m);
                writer.flush();
                byte[] result = out.toByteArray();
                isr = new InputStreamReader(new ByteArrayInputStream(result), OutputStreamWriterTest.MINIMAL_CHARSETS[i]);
                int expected = 0;
                int read = 0;
                int j = 0;
                while (expected < upper) {
                    if (j == read) {
                        read = isr.read(largeBuffer);
                        j = 0;
                    }
                    Assert.assertEquals(("Error when reading bytes in " + (OutputStreamWriterTest.MINIMAL_CHARSETS[i])), (expected++), largeBuffer[(j++)]);
                } 
            } finally {
                try {
                    isr.close();
                } catch (Exception e) {
                    // ok
                }
                try {
                    writer.close();
                } catch (Exception e) {
                    // ok
                }
            }
        }
    }

    @Test
    public void test_ConstructorLjava_io_OutputStream() {
        Assert.assertTrue("Used in tests", true);
    }

    @Test
    public void test_ConstructorLjava_io_OutputStreamLjava_lang_String() throws UnsupportedEncodingException {
        osw = new OutputStreamWriter(fos, "UTF-8");
        try {
            osw = new OutputStreamWriter(fos, "Bogus");
            Assert.fail("Failed to throw Unsupported Encoding exception");
        } catch (UnsupportedEncodingException e) {
            // Expected
        }
    }

    @Test
    public void test_close() throws IOException {
        osw.close();
        try {
            osw.write(testString, 0, testString.length());
            Assert.fail("Chars written after close");
        } catch (IOException e) {
            // Expected
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(bout, "ISO2022JP");
            writer.write(new char[]{ 'a' });
            writer.close();
            // the default is ASCII, there should not be any mode changes
            String converted = new String(bout.toByteArray(), "ISO8859_1");
            Assert.assertTrue(("invalid conversion 1: " + converted), converted.equals("a"));
            bout.reset();
            writer = new OutputStreamWriter(bout, "ISO2022JP");
            writer.write(new char[]{ '\u3048' });
            writer.flush();
            // the byte sequence should not switch to ASCII mode until the
            // stream is closed
            converted = new String(bout.toByteArray(), "ISO8859_1");
            Assert.assertTrue(("invalid conversion 2: " + converted), converted.equals("\u001b$B$("));
            writer.close();
            converted = new String(bout.toByteArray(), "ISO8859_1");
            Assert.assertTrue(("invalid conversion 3: " + converted), converted.equals("\u001b$B$(\u001b(B"));
            bout.reset();
            writer = new OutputStreamWriter(bout, "ISO2022JP");
            writer.write(new char[]{ '\u3048' });
            writer.write(new char[]{ '\u3048' });
            writer.close();
            // there should not be a mode switch between writes
            Assert.assertEquals("invalid conversion 4", "\u001b$B$($(\u001b(B", new String(bout.toByteArray(), "ISO8859_1"));
        } catch (UnsupportedEncodingException e) {
            // Can't test missing converter
            e.printStackTrace();
        }
    }

    @Test
    public void test_flush() throws IOException {
        char[] buf = new char[testString.length()];
        osw.write(testString, 0, testString.length());
        osw.flush();
        openInputStream();
        isr.read(buf, 0, buf.length);
        Assert.assertTrue("Chars not flushed", new String(buf, 0, buf.length).equals(testString));
    }

    @Test
    public void test_write$CII() throws IOException {
        char[] buf = new char[testString.length()];
        osw.write(testString, 0, testString.length());
        osw.close();
        openInputStream();
        isr.read(buf, 0, buf.length);
        Assert.assertTrue("Incorrect chars returned", new String(buf, 0, buf.length).equals(testString));
    }

    @Test
    public void test_writeI() throws IOException {
        osw.write('T');
        osw.close();
        openInputStream();
        int c = isr.read();
        Assert.assertEquals("Incorrect char returned", 'T', ((char) (c)));
    }

    @Test
    public void test_writeLjava_lang_StringII() throws IOException {
        char[] buf = new char[testString.length()];
        osw.write(testString, 0, testString.length());
        osw.close();
        openInputStream();
        isr.read(buf);
        Assert.assertEquals("Incorrect chars returned", testString, new String(buf, 0, buf.length));
    }
}

