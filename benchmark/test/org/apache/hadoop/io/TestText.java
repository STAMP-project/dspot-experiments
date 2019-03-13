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
package org.apache.hadoop.io;


import Text.Comparator;
import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for LargeUTF8.
 */
public class TestText {
    private static final int NUM_ITERATIONS = 100;

    private static final Random RANDOM = new Random(1);

    private static final int RAND_LEN = -1;

    @Test
    public void testWritable() throws Exception {
        for (int i = 0; i < (TestText.NUM_ITERATIONS); i++) {
            String str;
            if (i == 0)
                str = TestText.getLongString();
            else
                str = TestText.getTestString();

            TestWritable.testWritable(new Text(str));
        }
    }

    @Test
    public void testCoding() throws Exception {
        String before = "Bad \t encoding \t testcase";
        Text text = new Text(before);
        String after = text.toString();
        Assert.assertTrue(before.equals(after));
        for (int i = 0; i < (TestText.NUM_ITERATIONS); i++) {
            // generate a random string
            if (i == 0)
                before = TestText.getLongString();
            else
                before = TestText.getTestString();

            // test string to utf8
            ByteBuffer bb = Text.encode(before);
            byte[] utf8Text = bb.array();
            byte[] utf8Java = before.getBytes("UTF-8");
            Assert.assertEquals(0, WritableComparator.compareBytes(utf8Text, 0, bb.limit(), utf8Java, 0, utf8Java.length));
            // test utf8 to string
            after = Text.decode(utf8Java);
            Assert.assertTrue(before.equals(after));
        }
    }

    @Test
    public void testIO() throws Exception {
        DataOutputBuffer out = new DataOutputBuffer();
        DataInputBuffer in = new DataInputBuffer();
        for (int i = 0; i < (TestText.NUM_ITERATIONS); i++) {
            // generate a random string
            String before;
            if (i == 0)
                before = TestText.getLongString();
            else
                before = TestText.getTestString();

            // write it
            out.reset();
            Text.writeString(out, before);
            // test that it reads correctly
            in.reset(out.getData(), out.getLength());
            String after = Text.readString(in);
            Assert.assertTrue(before.equals(after));
            // Test compatibility with Java's other decoder
            int strLenSize = WritableUtils.getVIntSize(Text.utf8Length(before));
            String after2 = new String(out.getData(), strLenSize, ((out.getLength()) - strLenSize), "UTF-8");
            Assert.assertTrue(before.equals(after2));
        }
    }

    @Test
    public void testLimitedIO() throws Exception {
        doTestLimitedIO("abcd", 3);
        doTestLimitedIO("foo bar baz", 10);
        doTestLimitedIO("1", 0);
    }

    @Test
    public void testCompare() throws Exception {
        DataOutputBuffer out1 = new DataOutputBuffer();
        DataOutputBuffer out2 = new DataOutputBuffer();
        DataOutputBuffer out3 = new DataOutputBuffer();
        Text.Comparator comparator = new Text.Comparator();
        for (int i = 0; i < (TestText.NUM_ITERATIONS); i++) {
            // reset output buffer
            out1.reset();
            out2.reset();
            out3.reset();
            // generate two random strings
            String str1 = TestText.getTestString();
            String str2 = TestText.getTestString();
            if (i == 0) {
                str1 = TestText.getLongString();
                str2 = TestText.getLongString();
            } else {
                str1 = TestText.getTestString();
                str2 = TestText.getTestString();
            }
            // convert to texts
            Text txt1 = new Text(str1);
            Text txt2 = new Text(str2);
            Text txt3 = new Text(str1);
            // serialize them
            txt1.write(out1);
            txt2.write(out2);
            txt3.write(out3);
            // compare two strings by looking at their binary formats
            int ret1 = comparator.compare(out1.getData(), 0, out1.getLength(), out2.getData(), 0, out2.getLength());
            // compare two strings
            int ret2 = txt1.compareTo(txt2);
            Assert.assertEquals(ret1, ret2);
            Assert.assertEquals("Equivalence of different txt objects, same content", 0, txt1.compareTo(txt3));
            Assert.assertEquals("Equvalence of data output buffers", 0, comparator.compare(out1.getData(), 0, out3.getLength(), out3.getData(), 0, out3.getLength()));
        }
    }

    @Test
    public void testFind() throws Exception {
        Text text = new Text("abcd\u20acbdcd\u20ac");
        Assert.assertTrue(((text.find("abd")) == (-1)));
        Assert.assertTrue(((text.find("ac")) == (-1)));
        Assert.assertTrue(((text.find("\u20ac")) == 4));
        Assert.assertTrue(((text.find("\u20ac", 5)) == 11));
    }

    @Test
    public void testFindAfterUpdatingContents() throws Exception {
        Text text = new Text("abcd");
        text.set("a".getBytes());
        Assert.assertEquals(text.getLength(), 1);
        Assert.assertEquals(text.find("a"), 0);
        Assert.assertEquals(text.find("b"), (-1));
    }

    @Test
    public void testValidate() throws Exception {
        Text text = new Text("abcd\u20acbdcd\u20ac");
        byte[] utf8 = text.getBytes();
        int length = text.getLength();
        Text.validateUTF8(utf8, 0, length);
    }

    @Test
    public void testClear() throws Exception {
        // Test lengths on an empty text object
        Text text = new Text();
        Assert.assertEquals("Actual string on an empty text object must be an empty string", "", text.toString());
        Assert.assertEquals("Underlying byte array length must be zero", 0, text.getBytes().length);
        Assert.assertEquals("String's length must be zero", 0, text.getLength());
        // Test if clear works as intended
        text = new Text("abcd\u20acbdcd\u20ac");
        int len = text.getLength();
        text.clear();
        Assert.assertEquals("String must be empty after clear()", "", text.toString());
        Assert.assertTrue("Length of the byte array must not decrease after clear()", ((text.getBytes().length) >= len));
        Assert.assertEquals("Length of the string must be reset to 0 after clear()", 0, text.getLength());
    }

    @Test
    public void testTextText() throws CharacterCodingException {
        Text a = new Text("abc");
        Text b = new Text("a");
        b.set(a);
        Assert.assertEquals("abc", b.toString());
        a.append("xdefgxxx".getBytes(), 1, 4);
        Assert.assertEquals("modified aliased string", "abc", b.toString());
        Assert.assertEquals("appended string incorrectly", "abcdefg", a.toString());
        // add an extra byte so that capacity = 14 and length = 8
        a.append(new byte[]{ 'd' }, 0, 1);
        Assert.assertEquals(14, a.getBytes().length);
        Assert.assertEquals(8, a.copyBytes().length);
    }

    private class ConcurrentEncodeDecodeThread extends Thread {
        public ConcurrentEncodeDecodeThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            final String name = this.getName();
            DataOutputBuffer out = new DataOutputBuffer();
            DataInputBuffer in = new DataInputBuffer();
            for (int i = 0; i < 1000; ++i) {
                try {
                    out.reset();
                    WritableUtils.writeString(out, name);
                    in.reset(out.getData(), out.getLength());
                    String s = WritableUtils.readString(in);
                    Assert.assertEquals(("input buffer reset contents = " + name), name, s);
                } catch (Exception ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
    }

    @Test
    public void testConcurrentEncodeDecode() throws Exception {
        Thread thread1 = new TestText.ConcurrentEncodeDecodeThread("apache");
        Thread thread2 = new TestText.ConcurrentEncodeDecodeThread("hadoop");
        thread1.start();
        thread2.start();
        thread2.join();
        thread2.join();
    }

    @Test
    public void testAvroReflect() throws Exception {
        AvroTestUtil.testReflect(new Text("foo"), "{\"type\":\"string\",\"java-class\":\"org.apache.hadoop.io.Text\"}");
    }

    /**
     *
     */
    @Test
    public void testCharAt() {
        String line = "adsawseeeeegqewgasddga";
        Text text = new Text(line);
        for (int i = 0; i < (line.length()); i++) {
            Assert.assertTrue("testCharAt error1 !!!", ((text.charAt(i)) == (line.charAt(i))));
        }
        Assert.assertEquals("testCharAt error2 !!!", (-1), text.charAt((-1)));
        Assert.assertEquals("testCharAt error3 !!!", (-1), text.charAt(100));
    }

    /**
     * test {@code Text} readFields/write operations
     */
    @Test
    public void testReadWriteOperations() {
        String line = "adsawseeeeegqewgasddga";
        byte[] inputBytes = line.getBytes();
        inputBytes = Bytes.concat(new byte[]{ ((byte) (22)) }, inputBytes);
        DataInputBuffer in = new DataInputBuffer();
        DataOutputBuffer out = new DataOutputBuffer();
        Text text = new Text(line);
        try {
            in.reset(inputBytes, inputBytes.length);
            text.readFields(in);
        } catch (Exception ex) {
            Assert.fail("testReadFields error !!!");
        }
        try {
            text.write(out);
        } catch (IOException ex) {
        } catch (Exception ex) {
            Assert.fail("testReadWriteOperations error !!!");
        }
    }

    @Test
    public void testReadWithKnownLength() throws IOException {
        String line = "hello world";
        byte[] inputBytes = line.getBytes(Charsets.UTF_8);
        DataInputBuffer in = new DataInputBuffer();
        Text text = new Text();
        in.reset(inputBytes, inputBytes.length);
        text.readWithKnownLength(in, 5);
        Assert.assertEquals("hello", text.toString());
        // Read longer length, make sure it lengthens
        in.reset(inputBytes, inputBytes.length);
        text.readWithKnownLength(in, 7);
        Assert.assertEquals("hello w", text.toString());
        // Read shorter length, make sure it shortens
        in.reset(inputBytes, inputBytes.length);
        text.readWithKnownLength(in, 2);
        Assert.assertEquals("he", text.toString());
    }

    /**
     * test {@code Text.bytesToCodePoint(bytes)}
     * with {@code BufferUnderflowException}
     */
    @Test
    public void testBytesToCodePoint() {
        try {
            ByteBuffer bytes = ByteBuffer.wrap(new byte[]{ -2, 45, 23, 12, 76, 89 });
            Text.bytesToCodePoint(bytes);
            Assert.assertTrue("testBytesToCodePoint error !!!", ((bytes.position()) == 6));
        } catch (BufferUnderflowException ex) {
            Assert.fail("testBytesToCodePoint unexp exception");
        } catch (Exception e) {
            Assert.fail("testBytesToCodePoint unexp exception");
        }
    }

    @Test
    public void testbytesToCodePointWithInvalidUTF() {
        try {
            Text.bytesToCodePoint(ByteBuffer.wrap(new byte[]{ -2 }));
            Assert.fail("testbytesToCodePointWithInvalidUTF error unexp exception !!!");
        } catch (BufferUnderflowException ex) {
        } catch (Exception e) {
            Assert.fail("testbytesToCodePointWithInvalidUTF error unexp exception !!!");
        }
    }

    @Test
    public void testUtf8Length() {
        Assert.assertEquals("testUtf8Length1 error   !!!", 1, Text.utf8Length(new String(new char[]{ ((char) (1)) })));
        Assert.assertEquals("testUtf8Length127 error !!!", 1, Text.utf8Length(new String(new char[]{ ((char) (127)) })));
        Assert.assertEquals("testUtf8Length128 error !!!", 2, Text.utf8Length(new String(new char[]{ ((char) (128)) })));
        Assert.assertEquals("testUtf8Length193 error !!!", 2, Text.utf8Length(new String(new char[]{ ((char) (193)) })));
        Assert.assertEquals("testUtf8Length225 error !!!", 2, Text.utf8Length(new String(new char[]{ ((char) (225)) })));
        Assert.assertEquals("testUtf8Length254 error !!!", 2, Text.utf8Length(new String(new char[]{ ((char) (254)) })));
    }
}

