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
package org.apache.flume.serialization;


import DecodeErrorPolicy.FAIL;
import DecodeErrorPolicy.IGNORE;
import DecodeErrorPolicy.REPLACE;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static DecodeErrorPolicy.FAIL;
import static junit.framework.Assert.assertEquals;


public class TestResettableFileInputStream {
    private static final boolean CLEANUP = true;

    private static final File WORK_DIR = new File("target/test/work").getAbsoluteFile();

    private static final Logger logger = LoggerFactory.getLogger(TestResettableFileInputStream.class);

    private File file;

    private File meta;

    /**
     * Ensure that we can simply read bytes from a file.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testBasicRead() throws IOException {
        String output = TestResettableFileInputStream.singleLineFileInit(file, Charsets.UTF_8);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        String result = TestResettableFileInputStream.readLine(in, output.length());
        Assert.assertEquals(output, result);
        String afterEOF = TestResettableFileInputStream.readLine(in, output.length());
        Assert.assertNull(afterEOF);
        in.close();
    }

    /**
     * Ensure that we can simply read bytes from a file using InputStream.read() method.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReadByte() throws IOException {
        byte[] bytes = new byte[255];
        for (int i = 0; i < 255; i++) {
            bytes[i] = ((byte) (i));
        }
        Files.write(bytes, file);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        for (int i = 0; i < 255; i++) {
            Assert.assertEquals(i, in.read());
        }
        Assert.assertEquals((-1), in.read());
        in.close();
    }

    /**
     * Ensure that we can process lines that contain multi byte characters in weird places
     * such as at the end of a buffer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMultiByteCharRead() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("1234567".getBytes(Charsets.UTF_8));
        // write a multi byte char encompassing buffer boundaries
        generateUtf83ByteSequence(out);
        // buffer now contains 8 chars and 10 bytes total
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(8, Charsets.UTF_8, FAIL);
        String result = TestResettableFileInputStream.readLine(in, 8);
        Assert.assertEquals("1234567\u0a93\n", result);
    }

    /**
     * Ensure that we can process UTF-8 lines that contain surrogate pairs
     * even if they appear astride buffer boundaries.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testUtf8SurrogatePairRead() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("1234567".getBytes(Charsets.UTF_8));
        generateUtf8SurrogatePairSequence(out);
        // buffer now contains 9 chars (7 "normal" and 2 surrogates) and 11 bytes total
        // surrogate pair will encompass buffer boundaries
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(8, Charsets.UTF_8, FAIL);
        String result = TestResettableFileInputStream.readLine(in, 9);
        Assert.assertEquals("1234567\ud83d\ude18\n", result);
    }

    /**
     * Ensure that we can process UTF-16 lines that contain surrogate pairs, even
     * preceded by a Byte Order Mark (BOM).
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testUtf16BOMAndSurrogatePairRead() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generateUtf16SurrogatePairSequence(out);
        // buffer now contains 1 BOM and 2 chars (1 surrogate pair) and 6 bytes total
        // (including 2-byte BOM)
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(8, Charsets.UTF_16, FAIL);
        String result = TestResettableFileInputStream.readLine(in, 2);
        Assert.assertEquals("\ud83d\ude18\n", result);
    }

    /**
     * Ensure that we can process Shift_JIS lines that contain multi byte Japanese chars
     * even if they appear astride buffer boundaries.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testShiftJisSurrogateCharRead() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("1234567".getBytes(Charset.forName("Shift_JIS")));
        // write a multi byte char encompassing buffer boundaries
        generateShiftJis2ByteSequence(out);
        // buffer now contains 8 chars and 10 bytes total
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(8, Charset.forName("Shift_JIS"), FAIL);
        String result = TestResettableFileInputStream.readLine(in, 8);
        Assert.assertEquals("1234567\u4e9c\n", result);
    }

    @Test(expected = MalformedInputException.class)
    public void testUtf8DecodeErrorHandlingFailMalformed() throws IOException {
        ResettableInputStream in = initUtf8DecodeTest(FAIL);
        while ((in.readChar()) != (-1)) {
            // Do nothing... read the whole file and throw away the bytes.
        } 
        Assert.fail("Expected MalformedInputException!");
    }

    @Test
    public void testUtf8DecodeErrorHandlingIgnore() throws IOException {
        ResettableInputStream in = initUtf8DecodeTest(IGNORE);
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = in.readChar()) != (-1)) {
            sb.append(((char) (c)));
        } 
        Assert.assertEquals("Latin1: ()\nLong: ()\nNonUnicode: ()\n", sb.toString());
    }

    @Test
    public void testUtf8DecodeErrorHandlingReplace() throws IOException {
        ResettableInputStream in = initUtf8DecodeTest(REPLACE);
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = in.readChar()) != (-1)) {
            sb.append(((char) (c)));
        } 
        String preJdk8ExpectedStr = "Latin1: (X)\nLong: (XXX)\nNonUnicode: (X)\n";
        String expectedStr = "Latin1: (X)\nLong: (XXX)\nNonUnicode: (XXXXX)\n";
        String javaVersionStr = System.getProperty("java.version");
        double javaVersion = Double.parseDouble(javaVersionStr.substring(0, 3));
        if (javaVersion < 1.8) {
            Assert.assertTrue(preJdk8ExpectedStr.replaceAll("X", "\ufffd").equals(sb.toString()));
        } else {
            Assert.assertTrue(expectedStr.replaceAll("X", "\ufffd").equals(sb.toString()));
        }
    }

    @Test(expected = MalformedInputException.class)
    public void testLatin1DecodeErrorHandlingFailMalformed() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generateLatin1InvalidSequence(out);
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(FAIL);
        while ((in.readChar()) != (-1)) {
            // Do nothing... read the whole file and throw away the bytes.
        } 
        Assert.fail("Expected MalformedInputException!");
    }

    @Test
    public void testLatin1DecodeErrorHandlingReplace() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generateLatin1InvalidSequence(out);
        Files.write(out.toByteArray(), file);
        ResettableInputStream in = initInputStream(REPLACE);
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = in.readChar()) != (-1)) {
            sb.append(((char) (c)));
        } 
        Assert.assertEquals("Invalid: (X)\n".replaceAll("X", "\ufffd"), sb.toString());
    }

    /**
     * Ensure a reset() brings us back to the default mark (beginning of file)
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReset() throws IOException {
        String output = TestResettableFileInputStream.singleLineFileInit(file, Charsets.UTF_8);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        String result1 = TestResettableFileInputStream.readLine(in, output.length());
        Assert.assertEquals(output, result1);
        in.reset();
        String result2 = TestResettableFileInputStream.readLine(in, output.length());
        Assert.assertEquals(output, result2);
        String result3 = TestResettableFileInputStream.readLine(in, output.length());
        Assert.assertNull(("Should be null: " + result3), result3);
        in.close();
    }

    /**
     * Ensure that marking and resetting works.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMarkReset() throws IOException {
        List<String> expected = TestResettableFileInputStream.multiLineFileInit(file, Charsets.UTF_8);
        int MAX_LEN = 100;
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        String result0 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        Assert.assertEquals(expected.get(0), result0);
        in.reset();
        String result0a = TestResettableFileInputStream.readLine(in, MAX_LEN);
        Assert.assertEquals(expected.get(0), result0a);
        in.mark();
        String result1 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        Assert.assertEquals(expected.get(1), result1);
        in.reset();
        String result1a = TestResettableFileInputStream.readLine(in, MAX_LEN);
        Assert.assertEquals(expected.get(1), result1a);
        in.mark();
        in.close();
    }

    /**
     * Ensure that surrogate pairs work well with mark/reset.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMarkResetWithSurrogatePairs() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("foo".getBytes(Charsets.UTF_8));
        generateUtf8SurrogatePairSequence(out);
        out.write("bar".getBytes(Charsets.UTF_8));
        Files.write(out.toByteArray(), file);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        assertEquals('f', in.readChar());
        assertEquals('o', in.readChar());
        in.mark();
        assertEquals('o', in.readChar());
        // read high surrogate
        assertEquals('\ud83d', in.readChar());
        // call reset in the middle of a surrogate pair
        in.reset();
        // will read low surrogate *before* reverting back to mark, to ensure
        // surrogate pair is properly read
        assertEquals('\ude18', in.readChar());
        // now back to marked position
        assertEquals('o', in.readChar());
        // read high surrogate again
        assertEquals('\ud83d', in.readChar());
        // call mark in the middle of a surrogate pair:
        // will mark the position *after* the pair, *not* low surrogate's position
        in.mark();
        // will reset to the position *after* the pair
        in.reset();
        // read low surrogate normally despite of reset being called
        // so that the pair is entirely read
        assertEquals('\ude18', in.readChar());
        assertEquals('b', in.readChar());
        assertEquals('a', in.readChar());
        // will reset to the position *after* the pair
        in.reset();
        assertEquals('b', in.readChar());
        assertEquals('a', in.readChar());
        assertEquals('r', in.readChar());
        assertEquals((-1), in.readChar());
        in.close();
        tracker.close();// redundant

    }

    @Test
    public void testResume() throws IOException {
        List<String> expected = TestResettableFileInputStream.multiLineFileInit(file, Charsets.UTF_8);
        int MAX_LEN = 100;
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        String result0 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        String result1 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        in.mark();
        String result2 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        junit.framework.Assert.assertEquals(expected.get(2), result2);
        String result3 = TestResettableFileInputStream.readLine(in, MAX_LEN);
        junit.framework.Assert.assertEquals(expected.get(3), result3);
        in.close();
        tracker.close();// redundant

        // create new Tracker & RIS
        tracker = new DurablePositionTracker(meta, file.getPath());
        in = new ResettableFileInputStream(file, tracker);
        String result2a = TestResettableFileInputStream.readLine(in, MAX_LEN);
        String result3a = TestResettableFileInputStream.readLine(in, MAX_LEN);
        junit.framework.Assert.assertEquals(result2, result2a);
        junit.framework.Assert.assertEquals(result3, result3a);
    }

    /**
     * Ensure that surrogate pairs work well when resuming
     * reading. Specifically, this test brings up special situations
     * where a surrogate pair cannot be correctly decoded because
     * the second character is lost.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testResumeWithSurrogatePairs() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("foo".getBytes(Charsets.UTF_8));
        generateUtf8SurrogatePairSequence(out);
        out.write("bar".getBytes(Charsets.UTF_8));
        Files.write(out.toByteArray(), file);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker);
        assertEquals('f', in.readChar());
        assertEquals('o', in.readChar());
        in.mark();
        assertEquals('o', in.readChar());
        // read high surrogate
        assertEquals('\ud83d', in.readChar());
        // call reset in the middle of a surrogate pair
        in.reset();
        // close RIS - this will cause the low surrogate char
        // stored in-memory to be lost
        in.close();
        tracker.close();// redundant

        // create new Tracker & RIS
        tracker = new DurablePositionTracker(meta, file.getPath());
        in = new ResettableFileInputStream(file, tracker);
        // low surrogate char is now lost - resume from marked position
        assertEquals('o', in.readChar());
        // read high surrogate again
        assertEquals('\ud83d', in.readChar());
        // call mark in the middle of a surrogate pair:
        // will mark the position *after* the pair, *not* low surrogate's position
        in.mark();
        // close RIS - this will cause the low surrogate char
        // stored in-memory to be lost
        in.close();
        tracker.close();// redundant

        // create new Tracker & RIS
        tracker = new DurablePositionTracker(meta, file.getPath());
        in = new ResettableFileInputStream(file, tracker);
        // low surrogate char is now lost - resume from marked position
        assertEquals('b', in.readChar());
        assertEquals('a', in.readChar());
        assertEquals('r', in.readChar());
        assertEquals((-1), in.readChar());
        in.close();
        tracker.close();// redundant

    }

    @Test
    public void testSeek() throws IOException {
        int NUM_LINES = 1000;
        int LINE_LEN = 1000;
        TestResettableFileInputStream.generateData(file, Charsets.UTF_8, NUM_LINES, LINE_LEN);
        PositionTracker tracker = new DurablePositionTracker(meta, file.getPath());
        ResettableInputStream in = new ResettableFileInputStream(file, tracker, (10 * LINE_LEN), Charsets.UTF_8, FAIL);
        String line = "";
        for (int i = 0; i < 9; i++) {
            line = TestResettableFileInputStream.readLine(in, LINE_LEN);
        }
        int lineNum = Integer.parseInt(line.substring(0, 10));
        Assert.assertEquals(8, lineNum);
        // seek back within our buffer
        long pos = in.tell();
        in.seek((pos - (2 * LINE_LEN)));// jump back 2 lines

        line = TestResettableFileInputStream.readLine(in, LINE_LEN);
        lineNum = Integer.parseInt(line.substring(0, 10));
        Assert.assertEquals(7, lineNum);
        // seek forward within our buffer
        in.seek(((in.tell()) + LINE_LEN));
        line = TestResettableFileInputStream.readLine(in, LINE_LEN);
        lineNum = Integer.parseInt(line.substring(0, 10));
        Assert.assertEquals(9, lineNum);
        // seek forward outside our buffer
        in.seek(((in.tell()) + (20 * LINE_LEN)));
        line = TestResettableFileInputStream.readLine(in, LINE_LEN);
        lineNum = Integer.parseInt(line.substring(0, 10));
        Assert.assertEquals(30, lineNum);
        // seek backward outside our buffer
        in.seek(((in.tell()) - (25 * LINE_LEN)));
        line = TestResettableFileInputStream.readLine(in, LINE_LEN);
        lineNum = Integer.parseInt(line.substring(0, 10));
        Assert.assertEquals(6, lineNum);
        // test a corner-case seek which requires a buffer refill
        in.seek((100 * LINE_LEN));
        in.seek(0);// reset buffer

        in.seek((9 * LINE_LEN));
        Assert.assertEquals(9, Integer.parseInt(TestResettableFileInputStream.readLine(in, LINE_LEN).substring(0, 10)));
        Assert.assertEquals(10, Integer.parseInt(TestResettableFileInputStream.readLine(in, LINE_LEN).substring(0, 10)));
        Assert.assertEquals(11, Integer.parseInt(TestResettableFileInputStream.readLine(in, LINE_LEN).substring(0, 10)));
    }
}

