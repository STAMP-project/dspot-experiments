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
package org.apache.harmony.luni.tests.java.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import junit.framework.TestCase;


public class ScannerTest extends TestCase {
    private Scanner s;

    private ServerSocket server;

    private SocketAddress address;

    private SocketChannel client;

    private Socket serverSocket;

    private OutputStream os;

    private static class MockCloseable implements Closeable , Readable {
        public void close() throws IOException {
            throw new IOException();
        }

        public int read(CharBuffer cb) throws IOException {
            throw new EOFException();
        }
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(File)
     */
    public void test_ConstructorLjava_io_File() throws IOException {
        File tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        s = new Scanner(tmpFile);
        TestCase.assertNotNull(s);
        s.close();
        TestCase.assertTrue(tmpFile.delete());
        try {
            s = new Scanner(tmpFile);
            TestCase.fail();
        } catch (FileNotFoundException expected) {
        }
        tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        FileOutputStream fos = new FileOutputStream(tmpFile);
        fos.write("test".getBytes());
        fos.close();
        s = new Scanner(tmpFile);
        s.close();
        tmpFile.delete();
        // Scanner(File = null)
        try {
            s = new Scanner(((File) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // TODO: test if the default charset is used.
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(File, String)
     */
    public void test_ConstructorLjava_io_FileLjava_lang_String() throws IOException {
        File tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        s = new Scanner(tmpFile, Charset.defaultCharset().name());
        TestCase.assertNotNull(s);
        s.close();
        TestCase.assertTrue(tmpFile.delete());
        try {
            s = new Scanner(tmpFile, Charset.defaultCharset().name());
            TestCase.fail();
        } catch (FileNotFoundException expected) {
        }
        try {
            s = new Scanner(tmpFile, null);
            TestCase.fail();
        } catch (FileNotFoundException expected) {
        }
        tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        try {
            s = new Scanner(tmpFile, "invalid charset");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // fail on RI. File is opened but not closed when exception is thrown on
        // RI.
        TestCase.assertTrue(tmpFile.delete());
        // Scanner(File = null, Charset = null)
        try {
            s = new Scanner(((File) (null)), null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Scanner(File = null, Charset = UTF-8)
        try {
            s = new Scanner(((File) (null)), "UTF-8");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Scanner(File = null, Charset = invalid)
        try {
            s = new Scanner(((File) (null)), "invalid");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Scanner(File, Charset = null)
        try {
            File f = File.createTempFile("test", ".tmp");
            s = new Scanner(f, null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // TODO: test if the specified charset is used.
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(InputStream)
     */
    public void test_ConstructorLjava_io_InputStream() {
        s = new Scanner(new PipedInputStream());
        TestCase.assertNotNull(s);
        s.close();
        // Scanner(InputStream)
        try {
            s = new Scanner(((InputStream) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // TODO: test if the default charset is used.
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(InputStream, String)
     */
    public void test_ConstructorLjava_io_InputStreamLjava_lang_String() {
        s = new Scanner(new PipedInputStream(), Charset.defaultCharset().name());
        TestCase.assertNotNull(s);
        s.close();
        try {
            s = new Scanner(((PipedInputStream) (null)), "invalid charset");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            s = new Scanner(new PipedInputStream(), null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            s = new Scanner(new PipedInputStream(), "invalid charset");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // TODO: test if the specified charset is used.
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(Readable)
     */
    public void test_ConstructorLjava_lang_Readable() {
        s = new Scanner(new StringReader("test string"));
        TestCase.assertNotNull(s);
        s.close();
        // Scanner(Readable)
        try {
            s = new Scanner(((Readable) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(ReadableByteChannel)
     */
    public void test_ConstructorLjava_nio_channels_ReadableByteChannel() throws IOException {
        File tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        FileChannel fc = new FileOutputStream(tmpFile).getChannel();
        s = new Scanner(fc);
        TestCase.assertNotNull(s);
        s.close();
        TestCase.assertTrue(tmpFile.delete());
        // Scanner(ReadableByteChannel)
        try {
            s = new Scanner(((ReadableByteChannel) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Test if the default charset is used.
        String sampleData = "1 2 3 4 5 6 7 8 9 10";
        File tempFile = File.createTempFile("harmony", "test");
        tempFile.deleteOnExit();
        FileOutputStream os = new FileOutputStream(tempFile);
        os.write(sampleData.getBytes());
        os.close();
        FileInputStream is = new FileInputStream(tempFile);
        FileChannel channel = is.getChannel();
        Scanner s = new Scanner(channel);
        int count = 0;
        while (s.hasNextInt()) {
            s.nextInt();
            count++;
        } 
        channel.close();
        TestCase.assertEquals(10, count);
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(ReadableByteChannel, String)
     */
    public void test_ConstructorLjava_nio_channels_ReadableByteChannelLjava_lang_String() throws IOException {
        File tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        FileChannel fc = new FileOutputStream(tmpFile).getChannel();
        s = new Scanner(fc, Charset.defaultCharset().name());
        TestCase.assertNotNull(s);
        s.close();
        fc = new FileOutputStream(tmpFile).getChannel();
        try {
            s = new Scanner(fc, "invalid charset");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        fc.close();
        TestCase.assertTrue(tmpFile.delete());
        // Scanner(ReadableByteChannel = null, Charset = null)
        try {
            s = new Scanner(((ReadableByteChannel) (null)), null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Scanner(ReadableByteChannel = null, Charset = invalid)
        try {
            s = new Scanner(((ReadableByteChannel) (null)), "invalid");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Scanner(ReadableByteChannel, Charset = null)
        try {
            s = new Scanner(fc, null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // TODO: test if the specified charset is used.
    }

    public void test_Constructor_LReadableByteChannel() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(null);
        SocketChannel sc = SocketChannel.open();
        sc.connect(ssc.socket().getLocalSocketAddress());
        sc.configureBlocking(false);
        TestCase.assertFalse(sc.isBlocking());
        ssc.accept().close();
        ssc.close();
        TestCase.assertFalse(sc.isBlocking());
        Scanner s = new Scanner(sc);
        try {
            s.hasNextInt();
            TestCase.fail();
        } catch (IllegalBlockingModeException expected) {
        }
        sc.close();
    }

    /**
     *
     *
     * @unknown java.util.Scanner#Scanner(String)
     */
    public void test_ConstructorLjava_lang_String() {
        s = new Scanner("test string");
        TestCase.assertNotNull(s);
        s.close();
        // Scanner(String)
        try {
            s = new Scanner(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     *
     *
     * @unknown java.util.Scanner#close()
     */
    public void test_close() throws IOException {
        File tmpFile = File.createTempFile("TestFileForScanner", ".tmp");
        FileOutputStream fos = new FileOutputStream(tmpFile);
        FileChannel fc = fos.getChannel();
        s = new Scanner(fc);
        // Write out a int before the scanner is closed, should be OK.
        fos.write(12);
        s.close();
        TestCase.assertFalse(fc.isOpen());
        // Write out a int after the scanner is closed, IOException should be
        // thrown out.
        try {
            fos.write(12);
            TestCase.fail();
        } catch (IOException expected) {
        }
        s.close();// no exception should be thrown

        TestCase.assertTrue(tmpFile.delete());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#ioException()
     */
    public void test_ioException() throws IOException {
        ScannerTest.MockCloseable mc = new ScannerTest.MockCloseable();
        s = new Scanner(mc);
        TestCase.assertNull(s.ioException());// No operation, no exception

        s.close();// IOException should be cached

        TestCase.assertNotNull(s.ioException());
        TestCase.assertTrue(((s.ioException()) instanceof IOException));
    }

    /**
     *
     *
     * @unknown java.util.Scanner#delimiter()
     */
    public void test_delimiter() {
        s = new Scanner("test");
        Pattern pattern = s.delimiter();
        TestCase.assertEquals("\\p{javaWhitespace}+", pattern.toString());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#useDelimiter(Pattern)
     */
    public void test_useDelimiter_LPattern() {
        s = new Scanner("test");
        s.useDelimiter(Pattern.compile("\\w+"));
        TestCase.assertEquals("\\w+", s.delimiter().toString());
        s = new Scanner("test");
        s.useDelimiter(((Pattern) (null)));
        TestCase.assertNull(s.delimiter());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#useDelimiter(String)
     */
    public void test_useDelimiter_String() {
        s = new Scanner("test");
        try {
            s.useDelimiter(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        s = new Scanner("test");
        s.useDelimiter("\\w+");
        TestCase.assertEquals("\\w+", s.delimiter().toString());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#locale()
     */
    public void test_locale() {
        s = new Scanner("test");
        TestCase.assertEquals(Locale.getDefault(), s.locale());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#useLocale(Locale)
     */
    public void test_useLocale_LLocale() {
        s = new Scanner("test");
        try {
            s.useLocale(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        s.useLocale(new Locale("test", "test"));
        TestCase.assertEquals(new Locale("test", "test"), s.locale());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#radix()
     */
    public void test_radix() {
        s = new Scanner("test");
        TestCase.assertEquals(10, s.radix());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#useRadix()
     */
    public void test_useRadix_I() {
        s = new Scanner("test");
        try {
            s.useRadix(((Character.MIN_RADIX) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            s.useRadix(((Character.MAX_RADIX) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        s.useRadix(11);
        TestCase.assertEquals(11, s.radix());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#remove()
     */
    public void test_remove() {
        s = new Scanner("aab*b*").useDelimiter("\\*");
        try {
            s.remove();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     *
     *
     * @unknown java.util.Scanner#match()
     */
    public void test_match() {
        MatchResult result;
        s = new Scanner("1 2 ");
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        result = s.match();
        TestCase.assertEquals(2, result.start());
        TestCase.assertEquals(3, result.end());
        TestCase.assertEquals(2, result.start(0));
        TestCase.assertEquals(3, result.end(0));
        TestCase.assertEquals("2", result.group());
        TestCase.assertEquals("2", result.group(0));
        TestCase.assertEquals(0, result.groupCount());
        try {
            result.start(1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("True faLse");
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertTrue(s.nextBoolean());
        result = s.match();
        TestCase.assertEquals(0, result.start());
        TestCase.assertEquals(4, result.end());
        TestCase.assertEquals(0, result.start(0));
        TestCase.assertEquals(4, result.end(0));
        TestCase.assertEquals("True", result.group());
        TestCase.assertEquals(0, result.groupCount());
        TestCase.assertFalse(s.nextBoolean());
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("True faLse");
        TestCase.assertTrue(s.nextBoolean());
        result = s.match();
        TestCase.assertEquals(0, result.start());
        TestCase.assertEquals(4, result.end());
        TestCase.assertEquals(0, result.start(0));
        TestCase.assertEquals(4, result.end(0));
        TestCase.assertEquals("True", result.group());
        TestCase.assertEquals(0, result.groupCount());
        s.close();
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        result = s.match();
        TestCase.assertEquals(0, result.start());
        TestCase.assertEquals(4, result.end());
        TestCase.assertEquals(0, result.start(0));
        TestCase.assertEquals(4, result.end(0));
        TestCase.assertEquals("True", result.group());
        TestCase.assertEquals(0, result.groupCount());
        s = new Scanner("True fase");
        TestCase.assertTrue(s.nextBoolean());
        TestCase.assertEquals(0, result.groupCount());
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("True fase");
        TestCase.assertTrue(s.nextBoolean());
        try {
            s.next(((Pattern) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        result = s.match();
        TestCase.assertEquals(0, result.start());
        TestCase.assertEquals(4, result.end());
        TestCase.assertEquals(0, result.start(0));
        TestCase.assertEquals(4, result.end(0));
        TestCase.assertEquals("True", result.group());
        TestCase.assertEquals(0, result.groupCount());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#next()
     */
    public void test_next() throws IOException {
        // use special delimiter
        s = new Scanner("1**2").useDelimiter("\\*");
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("", s.next());
        TestCase.assertEquals("2", s.next());
        s = new Scanner(" \t 1 \t 2").useDelimiter("\\s*");
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("a").useDelimiter("a?");
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("aa").useDelimiter("a?");
        TestCase.assertEquals("", s.next());
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("word( )test( )").useDelimiter("\\( \\)");
        TestCase.assertEquals("word", s.next());
        TestCase.assertEquals("test", s.next());
        s = new Scanner("? next  ").useDelimiter("( )");
        TestCase.assertEquals("?", s.next());
        TestCase.assertEquals("next", s.next());
        TestCase.assertEquals("", s.next());
        s = new Scanner("word1 word2  ");
        TestCase.assertEquals("word1", s.next());
        TestCase.assertEquals("word2", s.next());
        // test boundary case
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // just delimiter exists in this scanner
        s = new Scanner(" ");
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // nothing exists in this scanner
        s = new Scanner("");
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // no delimiter exists in this scanner
        s = new Scanner("test");
        TestCase.assertEquals("test", s.next());
        // input resourse starts with delimiter
        s = new Scanner("  test");
        TestCase.assertEquals("test", s.next());
        // input resource ends with delimiter
        s = new Scanner("  test  ");
        TestCase.assertEquals("test", s.next());
        // Harmony uses 1024 as default buffer size,
        // What if a sentence can not be read in all in once.
        StringBuilder longSentence = new StringBuilder(1025);
        for (int i = 0; i < 11; i++) {
            longSentence.append(" ");
        }
        for (int i = 11; i < 1026; i++) {
            longSentence.append("a");
        }
        s = new Scanner(longSentence.toString());
        TestCase.assertEquals(longSentence.toString().trim(), s.next());
        s = new Scanner(" test test");
        TestCase.assertEquals("test", s.next());
        TestCase.assertEquals("test", s.next());
        // What if use a delimiter of length 0.
        s = new Scanner("test\ntest").useDelimiter(Pattern.compile("^", Pattern.MULTILINE));
        TestCase.assertEquals("test\n", s.next());
        TestCase.assertEquals("test", s.next());
        s = new Scanner("").useDelimiter(Pattern.compile("^", Pattern.MULTILINE));
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("").useDelimiter(Pattern.compile("^*", Pattern.MULTILINE));
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("test\ntest").useDelimiter(Pattern.compile("^*", Pattern.MULTILINE));
        TestCase.assertEquals("t", s.next());
        TestCase.assertEquals("e", s.next());
        s = new Scanner("\ntest\ntest").useDelimiter(Pattern.compile("$", Pattern.MULTILINE));
        TestCase.assertEquals("\ntest", s.next());
        TestCase.assertEquals("\ntest", s.next());
        // test socket inputStream
        // Harmony uses 1024 as default buffer size,
        // what if the leading delimiter is larger than 1023
        for (int i = 0; i < 1024; i++) {
            os.write(" ".getBytes());
        }
        os.write("  1 2 ".getBytes());
        s = new Scanner(client);
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        os.write("  1 2".getBytes());
        serverSocket.close();
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#next(Pattern)
     */
    public void test_nextLPattern() throws IOException {
        Pattern pattern;
        s = new Scanner("aab*2*").useDelimiter("\\*");
        pattern = Pattern.compile("a*b");
        TestCase.assertEquals("aab", s.next(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word ? ");
        pattern = Pattern.compile("\\w+");
        TestCase.assertEquals("word", s.next(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word1 word2  ");
        pattern = Pattern.compile("\\w+");
        TestCase.assertEquals("word1", s.next(pattern));
        TestCase.assertEquals("word2", s.next(pattern));
        // test boundary case
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // test socket inputStream
        os.write("aab 2".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        pattern = Pattern.compile("a*b");
        TestCase.assertEquals("aab", s.next(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#next(String)
     */
    public void test_nextLString() throws IOException {
        s = new Scanner("b*a*").useDelimiter("\\*");
        TestCase.assertEquals("b", s.next("a*b"));
        try {
            s.next("a*b");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word ? ");
        TestCase.assertEquals("word", s.next("\\w+"));
        try {
            s.next("\\w+");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word1 next  ");
        TestCase.assertEquals("word1", s.next("\\w+"));
        TestCase.assertEquals("next", s.next("\\w+"));
        // test boundary case
        try {
            s.next("\\w+");
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // test socket inputStream
        os.write("aab 2".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        TestCase.assertEquals("aab", s.next("a*b"));
        try {
            s.next("a*b");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextBoolean()
     */
    public void test_nextBoolean() throws IOException {
        // case insensitive
        s = new Scanner("TRue");
        TestCase.assertTrue(s.nextBoolean());
        s = new Scanner("tRue false");
        TestCase.assertTrue(s.nextBoolean());
        TestCase.assertFalse(s.nextBoolean());
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("true1");
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        try {
            s = new Scanner("");
            s.nextBoolean();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // test socket inputStream
        os.write("true false".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        TestCase.assertTrue(s.nextBoolean());
        TestCase.assertFalse(s.nextBoolean());
        // ues '*' as delimiter
        s = new Scanner("true**false").useDelimiter("\\*");
        TestCase.assertTrue(s.nextBoolean());
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("false( )").useDelimiter("\\( \\)");
        TestCase.assertFalse(s.nextBoolean());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextInt(int)
     */
    public void test_nextIntI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextInt(10));
        TestCase.assertEquals(456, s.nextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertEquals(38, s.nextInt(5));
        try {
            s.nextInt(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt(10));
        TestCase.assertEquals(23456, s.nextInt(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt(10));
        TestCase.assertEquals(23456, s.nextInt(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextInt(10));
        try {
            s.nextInt(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertEquals(162, s.nextInt(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt(10));
        TestCase.assertEquals(23456, s.nextInt(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextInt(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextInt(10));
        s = new Scanner("E3456");
        TestCase.assertEquals(930902, s.nextInt(16));
        // The following test case fails on RI, because RI does not support
        // letter as leading digit
        s = new Scanner("E3,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(930902, s.nextInt(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt(10));
        /* There are three types of negative prefix all in all. '' '-' '(' There
        are three types of negative suffix all in all. '' '-' ')' '(' and ')'
        must be used togethor. Prefix '-' and suffix '-' must be used
        exclusively.
         */
        /* According to Integer regular expression: Integer :: = ( [-+]? (*
        Numeral ) ) | LocalPositivePrefix Numeral LocalPositiveSuffix |
        LocalNegativePrefix Numeral LocalNegativeSuffix 123- should be
        recognized by scanner with locale ar_AE, (123) shouble be recognized
        by scanner with locale mk_MK. But this is not the case on RI.
         */
        s = new Scanner("-123 123- -123-");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextInt(10));
        // The following test case fails on RI
        // RoboVM note: Also fails on Darwin with RoboVM so commented out.
        // assertEquals(-123, s.nextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("-123 123-");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextInt(10));
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
        // If the parameter radix is illegal, the following test cases fail on
        // RI
        try {
            s.nextInt(((Character.MIN_RADIX) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            s.nextInt(((Character.MAX_RADIX) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextInt()
     */
    public void test_nextInt() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextInt());
        TestCase.assertEquals(456, s.nextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertEquals(38, s.nextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt());
        TestCase.assertEquals(23456, s.nextInt());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt());
        TestCase.assertEquals(23456, s.nextInt());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextInt());
        s.useRadix(5);
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertEquals(162, s.nextInt());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextInt());
        TestCase.assertEquals(23456, s.nextInt());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextInt());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextInt());
        s = new Scanner("E3456");
        s.useRadix(16);
        TestCase.assertEquals(930902, s.nextInt());
        // The following test case fails on RI, because RI does not support
        // letter as leading digit
        s = new Scanner("E3,456");
        s.useLocale(Locale.ENGLISH);
        s.useRadix(16);
        TestCase.assertEquals(930902, s.nextInt());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextInt());
        /* There are three types of negative prefix all in all. '' '-' '(' There
        are three types of negative suffix all in all. '' '-' ')' '(' and ')'
        must be used togethor. Prefix '-' and suffix '-' must be used
        exclusively.
         */
        /* According to Integer regular expression: Integer :: = ( [-+]? (*
        Numeral ) ) | LocalPositivePrefix Numeral LocalPositiveSuffix |
        LocalNegativePrefix Numeral LocalNegativeSuffix 123- should be
        recognized by scanner with locale ar_AE, (123) shouble be recognized
        by scanner with locale mk_MK. But this is not the case on RI.
         */
        s = new Scanner("-123 123- -123-");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextInt());
        // The following test case fails on RI
        // RoboVM note: Also fails on Darwin with RoboVM so commented out.
        // assertEquals(-123, s.nextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("-123 123-");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextByte(int)
     */
    public void test_nextByteI() throws IOException {
        s = new Scanner("123 126");
        TestCase.assertEquals(123, s.nextByte(10));
        TestCase.assertEquals(126, s.nextByte(10));
        try {
            s.nextByte(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 126");
        TestCase.assertEquals(38, s.nextByte(5));
        try {
            s.nextByte(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("1234");
        try {
            s.nextByte(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 12\u0666");
        TestCase.assertEquals(102, s.nextByte(10));
        try {
            s.nextByte(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertEquals(126, s.nextByte(10));
        s = new Scanner("012");
        TestCase.assertEquals(12, s.nextByte(10));
        s = new Scanner("E");
        TestCase.assertEquals(14, s.nextByte(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("100");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("1\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("1\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextByte(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextByte(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextByte()
     */
    public void test_nextByte() throws IOException {
        s = new Scanner("123 126");
        TestCase.assertEquals(123, s.nextByte());
        TestCase.assertEquals(126, s.nextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 126");
        s.useRadix(5);
        TestCase.assertEquals(38, s.nextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("1234");
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 12\u0666");
        TestCase.assertEquals(102, s.nextByte());
        s.useRadix(5);
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertEquals(126, s.nextByte());
        s = new Scanner("012");
        TestCase.assertEquals(12, s.nextByte());
        s = new Scanner("E");
        s.useRadix(16);
        TestCase.assertEquals(14, s.nextByte());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("100");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("1\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("1\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextByte());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextByte());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextFloat()
     */
    public void test_nextFloat() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (123.0)), s.nextFloat());
        TestCase.assertEquals(((float) (456.0)), s.nextFloat());
        TestCase.assertEquals(((float) (123.4)), s.nextFloat());
        TestCase.assertEquals(((float) (0.123)), s.nextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (123.4)), s.nextFloat());
        TestCase.assertEquals(((float) (-456.7)), s.nextFloat());
        TestCase.assertEquals(((float) (123456.789)), s.nextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (1.234E12)), s.nextFloat());
        TestCase.assertEquals(((float) (-4.567E14)), s.nextFloat());
        TestCase.assertEquals(((float) (1.23456789E-5)), s.nextFloat());
        s = new Scanner("NaN Infinity -Infinity");
        TestCase.assertEquals(Float.NaN, s.nextFloat());
        TestCase.assertEquals(Float.POSITIVE_INFINITY, s.nextFloat());
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, s.nextFloat());
        String str = String.valueOf(((Float.MAX_VALUE) * 2));
        s = new Scanner(str);
        TestCase.assertEquals(Float.POSITIVE_INFINITY, s.nextFloat());
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (23456.0)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(((float) (23.456)), s.nextFloat());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (23.456)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(((float) (23456.0)), s.nextFloat());
        s = new Scanner("23,456.7 23.456,7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(((float) (23456.7)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(((float) (23456.7)), s.nextFloat());
        s = new Scanner("-123.4 123.4- -123.4-");
        s.useLocale(new Locale("ar", "AE"));
        // FIXME
        // assertEquals((float)-123.4, s.nextFloat());
        // //The following test case fails on RI
        // assertEquals((float)-123.4, s.nextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("123- -123");
        s.useLocale(new Locale("mk", "MK"));
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
        TestCase.assertEquals(((float) (-123.0)), s.nextFloat());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextBigInteger(int)
     */
    public void test_nextBigIntegerI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(new BigInteger("123"), s.nextBigInteger(10));
        TestCase.assertEquals(new BigInteger("456"), s.nextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertEquals(new BigInteger("38"), s.nextBigInteger(5));
        try {
            s.nextBigInteger(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(new BigInteger("102"), s.nextBigInteger(10));
        try {
            s.nextBigInteger(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertEquals(new BigInteger("162"), s.nextBigInteger(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger(10));
        s = new Scanner("E34");
        TestCase.assertEquals(new BigInteger("3636"), s.nextBigInteger(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextBigInteger()
     */
    public void test_nextBigInteger() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(new BigInteger("123"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("456"), s.nextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertEquals(new BigInteger("38"), s.nextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(new BigInteger("102"), s.nextBigInteger());
        s.useRadix(5);
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertEquals(new BigInteger("162"), s.nextBigInteger());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertEquals(new BigInteger("3636"), s.nextBigInteger());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextShort(int)
     */
    public void test_nextShortI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextShort(10));
        TestCase.assertEquals(456, s.nextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertEquals(38, s.nextShort(5));
        try {
            s.nextShort(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789");
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextShort(10));
        try {
            s.nextShort(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertEquals(162, s.nextShort(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextShort(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextShort(10));
        s = new Scanner("E34");
        TestCase.assertEquals(3636, s.nextShort(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextShort(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextShort(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextShort()
     */
    public void test_nextShort() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextShort());
        TestCase.assertEquals(456, s.nextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertEquals(38, s.nextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789");
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertEquals(23456, s.nextShort());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertEquals(23456, s.nextShort());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextShort());
        s.useRadix(5);
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertEquals(162, s.nextShort());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertEquals(23456, s.nextShort());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextShort());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextShort());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertEquals(3636, s.nextShort());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextShort());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextShort());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextLong(int)
     */
    public void test_nextLongI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextLong(10));
        TestCase.assertEquals(456, s.nextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertEquals(38, s.nextLong(5));
        try {
            s.nextLong(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextLong(10));
        try {
            s.nextLong(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertEquals(162, s.nextLong(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextLong(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextLong(10));
        s = new Scanner("E34");
        TestCase.assertEquals(3636, s.nextLong(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextLong(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextLong(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextLong()
     */
    public void test_nextLong() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextLong());
        TestCase.assertEquals(456, s.nextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertEquals(38, s.nextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertEquals(23456, s.nextLong());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertEquals(23456, s.nextLong());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextLong());
        s.useRadix(5);
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertEquals(162, s.nextLong());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertEquals(23456, s.nextLong());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertEquals(3456, s.nextLong());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextLong());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertEquals(3636, s.nextLong());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertEquals((-123), s.nextLong());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertEquals((-123), s.nextLong());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNext()
     */
    public void test_hasNext() throws IOException {
        s = new Scanner("1##2").useDelimiter("\\#");
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("", s.next());
        TestCase.assertEquals("2", s.next());
        TestCase.assertFalse(s.hasNext());
        s.close();
        try {
            s.hasNext();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("1( )2( )").useDelimiter("\\( \\)");
        TestCase.assertTrue(s.hasNext());
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        s = new Scanner("1 2  ").useDelimiter("( )");
        TestCase.assertEquals("1", s.next());
        TestCase.assertEquals("2", s.next());
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("", s.next());
        s = new Scanner("1\n2  ");
        TestCase.assertEquals("1", s.next());
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("2", s.next());
        TestCase.assertFalse(s.hasNext());
        // test boundary case
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("1\'\n\'2  ");
        TestCase.assertEquals("1'", s.next());
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("'2", s.next());
        TestCase.assertFalse(s.hasNext());
        // test boundary case
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("  ");
        TestCase.assertFalse(s.hasNext());
        // test socket inputStream
        os.write("1 2".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        TestCase.assertEquals("1", s.next());
        TestCase.assertTrue(s.hasNext());
        TestCase.assertEquals("2", s.next());
        TestCase.assertFalse(s.hasNext());
        try {
            s.next();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNext(Pattern)
     */
    public void test_hasNextLPattern() throws IOException {
        Pattern pattern;
        s = new Scanner("aab@2@abb@").useDelimiter("\\@");
        pattern = Pattern.compile("a*b");
        TestCase.assertTrue(s.hasNext(pattern));
        TestCase.assertEquals("aab", s.next(pattern));
        TestCase.assertFalse(s.hasNext(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word ? ");
        pattern = Pattern.compile("\\w+");
        TestCase.assertTrue(s.hasNext(pattern));
        TestCase.assertEquals("word", s.next(pattern));
        TestCase.assertFalse(s.hasNext(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word1 WorD2  ");
        pattern = Pattern.compile("\\w+");
        TestCase.assertTrue(s.hasNext(pattern));
        TestCase.assertEquals("word1", s.next(pattern));
        TestCase.assertTrue(s.hasNext(pattern));
        TestCase.assertEquals("WorD2", s.next(pattern));
        TestCase.assertFalse(s.hasNext(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("word1 WorD2  ");
        pattern = Pattern.compile("\\w+");
        try {
            s.hasNext(((Pattern) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        s.close();
        try {
            s.hasNext(pattern);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        // test socket inputStream
        os.write("aab b".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        pattern = Pattern.compile("a+b");
        TestCase.assertTrue(s.hasNext(pattern));
        TestCase.assertEquals("aab", s.next(pattern));
        TestCase.assertFalse(s.hasNext(pattern));
        try {
            s.next(pattern);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNext(String)
     */
    public void test_hasNextLString() throws IOException {
        s = new Scanner("aab@2@abb@").useDelimiter("\\@");
        try {
            s.hasNext(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        s = new Scanner("aab*b*").useDelimiter("\\*");
        TestCase.assertTrue(s.hasNext("a+b"));
        TestCase.assertEquals("aab", s.next("a+b"));
        TestCase.assertFalse(s.hasNext("a+b"));
        try {
            s.next("a+b");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.close();
        try {
            s.hasNext("a+b");
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("WORD ? ");
        TestCase.assertTrue(s.hasNext("\\w+"));
        TestCase.assertEquals("WORD", s.next("\\w+"));
        TestCase.assertFalse(s.hasNext("\\w+"));
        try {
            s.next("\\w+");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("word1 word2  ");
        TestCase.assertEquals("word1", s.next("\\w+"));
        TestCase.assertEquals("word2", s.next("\\w+"));
        // test boundary case
        try {
            s.next("\\w+");
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // test socket inputStream
        os.write("aab 2".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        TestCase.assertTrue(s.hasNext("a*b"));
        TestCase.assertEquals("aab", s.next("a*b"));
        TestCase.assertFalse(s.hasNext("a*b"));
        try {
            s.next("a*b");
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextBoolean()
     */
    public void test_hasNextBoolean() throws IOException {
        s = new Scanner("TRue");
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertTrue(s.nextBoolean());
        s = new Scanner("tRue false");
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertTrue(s.nextBoolean());
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertFalse(s.nextBoolean());
        s = new Scanner("");
        TestCase.assertFalse(s.hasNextBoolean());
        // test socket inputStream
        os.write("true false ".getBytes());
        serverSocket.close();
        s = new Scanner(client);
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertTrue(s.nextBoolean());
        // ues '*' as delimiter
        s = new Scanner("true**false").useDelimiter("\\*");
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertTrue(s.nextBoolean());
        TestCase.assertFalse(s.hasNextBoolean());
        try {
            s.nextBoolean();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("false( )").useDelimiter("\\( \\)");
        TestCase.assertTrue(s.hasNextBoolean());
        TestCase.assertFalse(s.nextBoolean());
        TestCase.assertFalse(s.hasNextBoolean());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextByte(int)
     */
    public void test_hasNextByteI() throws IOException {
        s = new Scanner("123 126");
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(123, s.nextByte(10));
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(126, s.nextByte(10));
        TestCase.assertFalse(s.hasNextByte(10));
        try {
            s.nextByte(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 126");
        TestCase.assertTrue(s.hasNextByte(5));
        TestCase.assertEquals(38, s.nextByte(5));
        TestCase.assertFalse(s.hasNextByte(5));
        try {
            s.nextByte(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("1234");
        TestCase.assertFalse(s.hasNextByte(10));
        try {
            s.nextByte(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 12\u0666");
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(102, s.nextByte(10));
        TestCase.assertFalse(s.hasNextByte(5));
        try {
            s.nextByte(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(126, s.nextByte(10));
        s = new Scanner("012");
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(12, s.nextByte(10));
        s = new Scanner("E");
        TestCase.assertTrue(s.hasNextByte(16));
        TestCase.assertEquals(14, s.nextByte(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("100");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("1\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("1\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals(100, s.nextByte(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals((-123), s.nextByte(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertEquals((-123), s.nextByte(10));
    }

    public void test_hasNextByteI_cache() throws IOException {
        // regression for HARMONY-2063
        s = new Scanner("123 45");
        TestCase.assertTrue(s.hasNextByte(8));
        TestCase.assertEquals(83, s.nextByte());
        TestCase.assertEquals(45, s.nextByte());
        s = new Scanner("123 45");
        TestCase.assertTrue(s.hasNextByte(10));
        TestCase.assertTrue(s.hasNextByte(8));
        TestCase.assertEquals(83, s.nextByte());
        TestCase.assertEquals(45, s.nextByte());
        s = new Scanner("-123 -45");
        TestCase.assertTrue(s.hasNextByte(8));
        TestCase.assertEquals((-123), s.nextInt());
        TestCase.assertEquals((-45), s.nextByte());
        s = new Scanner("123 45");
        TestCase.assertTrue(s.hasNextByte());
        s.close();
        try {
            s.nextByte();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void test_hasNextByte() throws IOException {
        s = new Scanner("123 126");
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(123, s.nextByte());
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(126, s.nextByte());
        TestCase.assertFalse(s.hasNextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 126");
        s.useRadix(5);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(38, s.nextByte());
        TestCase.assertFalse(s.hasNextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("1234");
        TestCase.assertFalse(s.hasNextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 12\u0666");
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(102, s.nextByte());
        s.useRadix(5);
        TestCase.assertFalse(s.hasNextByte());
        try {
            s.nextByte();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(126, s.nextByte());
        s = new Scanner("012");
        TestCase.assertEquals(12, s.nextByte());
        s = new Scanner("E");
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(14, s.nextByte());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("100");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("1\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("1\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals(100, s.nextByte());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals((-123), s.nextByte());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextByte());
        TestCase.assertEquals((-123), s.nextByte());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextBigInteger(int)
     */
    public void test_hasNextBigIntegerI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("123"), s.nextBigInteger(10));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("456"), s.nextBigInteger(10));
        TestCase.assertFalse(s.hasNextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextBigInteger(5));
        TestCase.assertEquals(new BigInteger("38"), s.nextBigInteger(5));
        TestCase.assertFalse(s.hasNextBigInteger(5));
        try {
            s.nextBigInteger(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("102"), s.nextBigInteger(10));
        TestCase.assertFalse(s.hasNextBigInteger(5));
        try {
            s.nextBigInteger(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("162"), s.nextBigInteger(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextBigInteger(10));
        try {
            s.nextBigInteger(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger(10));
        s = new Scanner("E34");
        TestCase.assertTrue(s.hasNextBigInteger(16));
        TestCase.assertEquals(new BigInteger("3636"), s.nextBigInteger(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextBigInteger(int)
     */
    public void test_hasNextBigIntegerI_cache() throws IOException {
        // regression for HARMONY-2063
        s = new Scanner("123 123456789123456789");
        TestCase.assertTrue(s.hasNextBigInteger(16));
        TestCase.assertEquals(new BigInteger("291"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("123456789123456789"), s.nextBigInteger());
        s = new Scanner("123456789123456789 456");
        TestCase.assertTrue(s.hasNextBigInteger(16));
        TestCase.assertTrue(s.hasNextBigInteger(10));
        TestCase.assertEquals(new BigInteger("123456789123456789"), s.nextBigInteger());
        TestCase.assertEquals(new BigInteger("456"), s.nextBigInteger());
        s = new Scanner("-123 -123456789123456789");
        TestCase.assertTrue(s.hasNextBigInteger(8));
        TestCase.assertEquals((-123), s.nextShort());
        TestCase.assertEquals(new BigInteger("-123456789123456789"), s.nextBigInteger());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextBigInteger());
        s.close();
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextBigInteger()
     */
    public void test_hasNextBigInteger() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("123"), s.nextBigInteger());
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("456"), s.nextBigInteger());
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("38"), s.nextBigInteger());
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(new BigInteger("102"), s.nextBigInteger());
        s.useRadix(5);
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("162"), s.nextBigInteger());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("23456"), s.nextBigInteger());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextBigInteger());
        try {
            s.nextBigInteger();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("3456"), s.nextBigInteger());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("3636"), s.nextBigInteger());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("12300"), s.nextBigInteger());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextBigInteger());
        TestCase.assertEquals(new BigInteger("-123"), s.nextBigInteger());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextInt(int)
     */
    public void test_hasNextIntI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertEquals(123, s.nextInt(10));
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(456, s.nextInt(10));
        TestCase.assertFalse(s.hasNextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextInt(5));
        TestCase.assertEquals(38, s.nextInt(5));
        TestCase.assertFalse(s.hasNextInt(5));
        try {
            s.nextInt(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        TestCase.assertFalse(s.hasNextInt(10));
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextInt(10));
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextInt(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextInt(10));
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextInt(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06662");
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertFalse(s.hasNextInt(5));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextInt(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(3456, s.nextInt(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(3456, s.nextInt(10));
        s = new Scanner("E3456");
        TestCase.assertTrue(s.hasNextInt(16));
        TestCase.assertEquals(930902, s.nextInt(16));
        // The following test case fails on RI, because RI does not support
        // letter as leading digit
        s = new Scanner("E3,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextInt(16));
        TestCase.assertEquals(930902, s.nextInt(16));
        // If parameter radix is illegal, the following test case fails on RI
        try {
            s.hasNextInt(((Character.MIN_RADIX) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(12300, s.nextInt(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(12300, s.nextInt(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals(12300, s.nextInt(10));
        /* There are three types of negative prefix all in all. '' '-' '(' There
        are three types of negative suffix all in all. '' '-' ')' '(' and ')'
        must be used together. Prefix '-' and suffix '-' must be used
        exclusively.
         */
        /* According to Integer regular expression: Integer :: = ( [-+]? (*
        Numeral ) ) | LocalPositivePrefix Numeral LocalPositiveSuffix |
        LocalNegativePrefix Numeral LocalNegativeSuffix 123- should be
        recognized by scanner with locale ar_AE, (123) should be recognized
        by scanner with locale mk_MK. But this is not the case on RI.
         */
        s = new Scanner("-123 123- -123-");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals((-123), s.nextInt(10));
        // The following test case fails on RI
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals((-123), s.nextInt(10));
        TestCase.assertFalse(s.hasNextInt(10));
        try {
            s.nextInt(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("-123 123-");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextInt(10));
        TestCase.assertEquals((-123), s.nextInt(10));
        TestCase.assertFalse(s.hasNextInt(10));
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextInt(int)
     */
    public void test_hasNextIntI_cache() throws IOException {
        // regression for HARMONY-2063
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextInt(16));
        TestCase.assertEquals(291, s.nextInt(10));
        TestCase.assertEquals(456, s.nextInt());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextInt(16));
        TestCase.assertTrue(s.hasNextInt(8));
        TestCase.assertEquals(83, s.nextInt());
        TestCase.assertEquals(456, s.nextInt());
        s = new Scanner("-123 -456 -789");
        TestCase.assertTrue(s.hasNextInt(8));
        TestCase.assertEquals((-123), s.nextShort());
        TestCase.assertEquals((-456), s.nextInt());
        TestCase.assertTrue(s.hasNextShort(16));
        TestCase.assertEquals((-789), s.nextInt());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextInt());
        s.close();
        try {
            s.nextInt();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextInt()
     */
    public void test_hasNextInt() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(123, s.nextInt());
        TestCase.assertEquals(456, s.nextInt());
        TestCase.assertFalse(s.hasNextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(38, s.nextInt());
        TestCase.assertFalse(s.hasNextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        TestCase.assertFalse(s.hasNextInt());
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextInt());
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextInt());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextInt());
        s.useLocale(new Locale("it", "CH"));
        TestCase.assertTrue(s.hasNextInt());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06662");
        s.useRadix(5);
        TestCase.assertFalse(s.hasNextInt());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextInt());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextInt());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(3456, s.nextInt());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(3456, s.nextInt());
        s = new Scanner("E3456");
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(930902, s.nextInt());
        // The following test case fails on RI, because RI does not support
        // letter as leading digit
        s = new Scanner("E3,456");
        s.useLocale(Locale.ENGLISH);
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(930902, s.nextInt());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(12300, s.nextInt());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(12300, s.nextInt());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals(12300, s.nextInt());
        /* There are three types of negative prefix all in all. '' '-' '(' There
        are three types of negative suffix all in all. '' '-' ')' '(' and ')'
        must be used togethor. Prefix '-' and suffix '-' must be used
        exclusively.
         */
        /* According to Integer regular expression: Integer :: = ( [-+]? (*
        Numeral ) ) | LocalPositivePrefix Numeral LocalPositiveSuffix |
        LocalNegativePrefix Numeral LocalNegativeSuffix 123- should be
        recognized by scanner with locale ar_AE, (123) shouble be recognized
        by scanner with locale mk_MK. But this is not the case on RI.
         */
        s = new Scanner("-123 123- -123-");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals((-123), s.nextInt());
        // The following test case fails on RI
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals((-123), s.nextInt());
        TestCase.assertFalse(s.hasNextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("-123 123-");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextInt());
        TestCase.assertEquals((-123), s.nextInt());
        try {
            s.nextInt();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextFloat()
     */
    public void test_hasNextFloat() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (123.0)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (456.0)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (123.4)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (0.123)), s.nextFloat());
        TestCase.assertFalse(s.hasNextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (123.4)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (-456.7)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (123456.789)), s.nextFloat());
        TestCase.assertFalse(s.hasNextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (1.234E12)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (-4.567E14)), s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (1.23456789E-5)), s.nextFloat());
        s = new Scanner("NaN Infinity -Infinity");
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(Float.NaN, s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(Float.POSITIVE_INFINITY, s.nextFloat());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, s.nextFloat());
        String str = String.valueOf(((Float.MAX_VALUE) * 2));
        s = new Scanner(str);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(Float.POSITIVE_INFINITY, s.nextFloat());
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23456.0)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23.456)), s.nextFloat());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23.456)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23456.0)), s.nextFloat());
        s = new Scanner("23,456.7 23.456,7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23456.7)), s.nextFloat());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (23456.7)), s.nextFloat());
        // FIXME
        // s = new Scanner("-123.4 123.4- -123.4-");
        // s.useLocale(new Locale("ar", "AE"));
        // assertTrue(s.hasNextFloat());
        // assertEquals((float)-123.4, s.nextFloat());
        // //The following test case fails on RI
        // assertTrue(s.hasNextFloat());
        // assertEquals((float)-123.4, s.nextFloat());
        // try {
        // s.nextFloat();
        // fail();
        // } catch (InputMismatchException expected) {
        // }
        s = new Scanner("123- -123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertFalse(s.hasNextFloat());
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Skip the un-recognizable token 123-.
        TestCase.assertEquals("123-", s.next());
        TestCase.assertTrue(s.hasNextFloat());
        TestCase.assertEquals(((float) (-123.0)), s.nextFloat());
        s = new Scanner("+123.4 -456.7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextFloat());
        s.close();
        try {
            s.nextFloat();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextShort(int)
     */
    public void test_hasNextShortI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(123, s.nextShort(10));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(456, s.nextShort(10));
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort(5));
        TestCase.assertEquals(38, s.nextShort(5));
        TestCase.assertFalse(s.hasNextShort(5));
        try {
            s.nextShort(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789");
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextInt(10));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextInt(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(102, s.nextShort(10));
        TestCase.assertFalse(s.hasNextShort(5));
        try {
            s.nextShort(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(162, s.nextShort(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(23456, s.nextShort(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextShort(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(3456, s.nextShort(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(3456, s.nextShort(10));
        s = new Scanner("E34");
        TestCase.assertTrue(s.hasNextShort(16));
        TestCase.assertEquals(3636, s.nextShort(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals(12300, s.nextShort(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals((-123), s.nextShort(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextShort(10));
        TestCase.assertEquals((-123), s.nextShort(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextShort()
     */
    public void test_hasNextShort() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(123, s.nextShort());
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(456, s.nextShort());
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(38, s.nextShort());
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789");
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextShort());
        s.useRadix(5);
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(162, s.nextShort());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(23456, s.nextShort());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextShort());
        try {
            s.nextShort();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(3456, s.nextShort());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(3456, s.nextShort());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(3636, s.nextShort());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals(12300, s.nextShort());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals((-123), s.nextShort());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextShort());
        TestCase.assertEquals((-123), s.nextShort());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextShort(int)
     */
    public void test_hasNextShortI_cache() throws IOException {
        // regression for HARMONY-2063
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort(16));
        TestCase.assertEquals(291, s.nextShort());
        TestCase.assertEquals(456, s.nextShort());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort(16));
        TestCase.assertTrue(s.hasNextShort(8));
        TestCase.assertEquals(83, s.nextShort());
        TestCase.assertEquals(456, s.nextShort());
        s = new Scanner("-123 -456 -789");
        TestCase.assertTrue(s.hasNextShort(8));
        TestCase.assertEquals((-123), s.nextInt());
        TestCase.assertEquals((-456), s.nextShort());
        TestCase.assertTrue(s.hasNextInt(16));
        TestCase.assertEquals((-789), s.nextShort());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextShort());
        s.close();
        try {
            s.nextShort();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextLong(int)
     */
    public void test_hasNextLongI() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(123, s.nextLong(10));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(456, s.nextLong(10));
        TestCase.assertFalse(s.hasNextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong(5));
        TestCase.assertEquals(38, s.nextLong(5));
        TestCase.assertFalse(s.hasNextLong(5));
        try {
            s.nextLong(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        TestCase.assertFalse(s.hasNextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextShort(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(102, s.nextLong(10));
        TestCase.assertFalse(s.hasNextLong(5));
        try {
            s.nextLong(5);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(162, s.nextLong(10));
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(23456, s.nextLong(10));
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextLong(10));
        try {
            s.nextLong(10);
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(3456, s.nextLong(10));
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(3456, s.nextLong(10));
        s = new Scanner("E34");
        TestCase.assertTrue(s.hasNextLong(16));
        TestCase.assertEquals(3636, s.nextLong(16));
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals(12300, s.nextLong(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals((-123), s.nextLong(10));
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextLong(10));
        TestCase.assertEquals((-123), s.nextLong(10));
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextLong(int)
     */
    public void test_hasNextLongI_cache() throws IOException {
        // regression for HARMONY-2063
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong(16));
        TestCase.assertEquals(291, s.nextLong());
        TestCase.assertEquals(456, s.nextLong());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong(16));
        TestCase.assertTrue(s.hasNextLong(8));
        TestCase.assertEquals(83, s.nextLong());
        TestCase.assertEquals(456, s.nextLong());
        s = new Scanner("-123 -456 -789");
        TestCase.assertTrue(s.hasNextLong(8));
        TestCase.assertEquals((-123), s.nextInt());
        TestCase.assertEquals((-456), s.nextLong());
        TestCase.assertTrue(s.hasNextShort(16));
        TestCase.assertEquals((-789), s.nextLong());
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong());
        s.close();
        try {
            s.nextLong();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextLong()
     */
    public void test_hasNextLong() throws IOException {
        s = new Scanner("123 456");
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(123, s.nextLong());
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(456, s.nextLong());
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // If the radix is different from 10
        s = new Scanner("123 456");
        s.useRadix(5);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(38, s.nextLong());
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // If the number is out of range
        s = new Scanner("123456789123456789123456789123456789");
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.ENGLISH);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        /* ''' is used in many locales as group separator. */
        s = new Scanner("23'456 23'456");
        s.useLocale(Locale.GERMANY);
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(new Locale("it", "CH"));
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        /* The input string has Arabic-Indic digits. */
        s = new Scanner("1\u06602 1\u06662");
        TestCase.assertEquals(102, s.nextLong());
        s.useRadix(5);
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useRadix(10);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(162, s.nextLong());
        /* '.' is used in many locales as group separator. The input string
        has Arabic-Indic digits .
         */
        s = new Scanner("23.45\u0666 23.456");
        s.useLocale(Locale.CHINESE);
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s.useLocale(Locale.GERMANY);
        // If exception is thrown out, input will not be advanced.
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(23456, s.nextLong());
        // The input string starts with zero
        s = new Scanner("03,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertFalse(s.hasNextLong());
        try {
            s.nextLong();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        s = new Scanner("03456");
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(3456, s.nextLong());
        s = new Scanner("\u06603,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(3456, s.nextLong());
        s = new Scanner("E34");
        s.useRadix(16);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(3636, s.nextLong());
        /* There are 3 types of zero digit in all locales, '0' '\u0966' '\u0e50'
        respectively, but they are not differentiated.
         */
        s = new Scanner("12300");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("123\u0966\u0966");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("123\u0e50\u0e50");
        s.useLocale(Locale.CHINESE);
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals(12300, s.nextLong());
        s = new Scanner("-123");
        s.useLocale(new Locale("ar", "AE"));
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals((-123), s.nextLong());
        s = new Scanner("-123");
        s.useLocale(new Locale("mk", "MK"));
        TestCase.assertTrue(s.hasNextLong());
        TestCase.assertEquals((-123), s.nextLong());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextDouble()
     */
    public void test_hasNextDouble() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(123.0, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(456.0, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(123.4, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(0.123, s.nextDouble());
        TestCase.assertFalse(s.hasNextDouble());
        try {
            s.nextDouble();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(123.4, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals((-456.7), s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(123456.789, s.nextDouble());
        TestCase.assertFalse(s.hasNextDouble());
        try {
            s.nextDouble();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(1.234E12, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals((-4.567E14), s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(1.23456789E-5, s.nextDouble());
        s = new Scanner("NaN Infinity -Infinity");
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(Double.NaN, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, s.nextDouble());
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, s.nextDouble());
        String str = String.valueOf(((Double.MAX_VALUE) * 2));
        s = new Scanner(str);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, s.nextDouble());
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23456.0, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23.456, s.nextDouble());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23.456, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23456.0, s.nextDouble());
        s = new Scanner("23,456.7 23.456,7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23456.7, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals(23456.7, s.nextDouble());
        s = new Scanner("-123.4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        TestCase.assertEquals((-123.4), s.nextDouble());
        s = new Scanner("+123.4 -456.7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextDouble());
        s.close();
        try {
            s.nextDouble();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#hasNextBigDecimal()
     */
    public void test_hasNextBigDecimal() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("456"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123.4"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("0.123"), s.nextBigDecimal());
        TestCase.assertFalse(s.hasNextBigDecimal());
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123.4"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("-456.7"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123456.789"), s.nextBigDecimal());
        TestCase.assertFalse(s.hasNextBigDecimal());
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("1.234E12"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("-4.567E14"), s.nextBigDecimal());
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("1.23456789E-5"), s.nextBigDecimal());
        s = new Scanner("NaN");
        TestCase.assertFalse(s.hasNextBigDecimal());
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("23456"), s.nextBigDecimal());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("23.456"), s.nextBigDecimal());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("23.456"), s.nextBigDecimal());
        s.useLocale(Locale.GERMANY);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("23456"), s.nextBigDecimal());
        s = new Scanner("23,456.7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("23456.7"), s.nextBigDecimal());
        s = new Scanner("-123.4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertTrue(s.hasNextBigDecimal());
        TestCase.assertEquals(new BigDecimal("-123.4"), s.nextBigDecimal());
    }

    private static class MockStringReader extends StringReader {
        public MockStringReader(String param) {
            super(param);
        }

        public int read(CharBuffer target) throws IOException {
            target.append('t');
            target.append('e');
            target.append('s');
            target.append('t');
            throw new IOException();
        }
    }

    private static class MockStringReader2Read extends StringReader {
        int timesRead = 0;

        public MockStringReader2Read(String param) {
            super(param);
        }

        public int read(CharBuffer target) throws IOException {
            if ((timesRead) == 0) {
                target.append('1');
                target.append('2');
                target.append('3');
                (timesRead)++;
                return 3;
            } else
                if ((timesRead) == 1) {
                    target.append('t');
                    (timesRead)++;
                    return 1;
                } else {
                    throw new IOException();
                }

        }
    }

    // https://code.google.com/p/android/issues/detail?id=40555
    public void test_40555() throws Exception {
        ScannerTest.MockStringReader2Read reader = new ScannerTest.MockStringReader2Read("MockStringReader");
        s = new Scanner(reader);
        // We should get a match straight away.
        String result = s.findWithinHorizon("1", 0);
        TestCase.assertEquals("1", result);
        // The stream should not be consumed as there's already a match after the first read.
        TestCase.assertEquals(1, reader.timesRead);
    }

    /**
     *
     *
     * @unknown java.util.Scanner#findWithinHorizon(Pattern, int)
     */
    public void test_findWithinHorizon_LPatternI() {
        // This method searches through the input up to the specified search
        // horizon(exclusive).
        s = new Scanner("123test");
        String result = s.findWithinHorizon(Pattern.compile("\\p{Lower}"), 5);
        TestCase.assertEquals("t", result);
        MatchResult mresult = s.match();
        TestCase.assertEquals(3, mresult.start());
        TestCase.assertEquals(4, mresult.end());
        s = new Scanner("12345test1234test next");
        /* If the pattern is found the scanner advances past the input that
        matched and returns the string that matched the pattern.
         */
        result = s.findWithinHorizon(Pattern.compile("\\p{Digit}+"), 2);
        TestCase.assertEquals("12", result);
        mresult = s.match();
        TestCase.assertEquals(0, mresult.start());
        TestCase.assertEquals(2, mresult.end());
        // Position is now pointing at the bar. "12|345test1234test next"
        result = s.findWithinHorizon(Pattern.compile("\\p{Digit}+"), 6);
        TestCase.assertEquals("345", result);
        mresult = s.match();
        TestCase.assertEquals(2, mresult.start());
        TestCase.assertEquals(5, mresult.end());
        // Position is now pointing at the bar. "12345|test1234test next"
        // If no such pattern is detected then the null is returned and the
        // scanner's position remains unchanged.
        result = s.findWithinHorizon(Pattern.compile("\\p{Digit}+"), 3);
        TestCase.assertNull(result);
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("345", mresult.group());
        TestCase.assertEquals(2, mresult.start());
        TestCase.assertEquals(5, mresult.end());
        // Position is now still pointing at the bar. "12345|test1234test next"
        // If horizon is 0, then the horizon is ignored and this method
        // continues to search through the input looking for the specified
        // pattern without bound.
        result = s.findWithinHorizon(Pattern.compile("\\p{Digit}+"), 0);
        mresult = s.match();
        TestCase.assertEquals(9, mresult.start());
        TestCase.assertEquals(13, mresult.end());
        // Position is now pointing at the bar. "12345test1234|test next"
        TestCase.assertEquals("test", s.next());
        mresult = s.match();
        TestCase.assertEquals(13, mresult.start());
        TestCase.assertEquals(17, mresult.end());
        TestCase.assertEquals("next", s.next());
        mresult = s.match();
        TestCase.assertEquals(18, mresult.start());
        TestCase.assertEquals(22, mresult.end());
        try {
            s.findWithinHorizon(((Pattern) (null)), (-1));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            s.findWithinHorizon(Pattern.compile("\\p{Digit}+"), (-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        s.close();
        try {
            s.findWithinHorizon(((Pattern) (null)), (-1));
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("test");
        result = s.findWithinHorizon(Pattern.compile("\\w+"), 10);
        TestCase.assertEquals("test", result);
        s = new Scanner("aa\n\rb");
        result = s.findWithinHorizon(Pattern.compile("a"), 5);
        TestCase.assertEquals("a", result);
        mresult = s.match();
        TestCase.assertEquals(0, mresult.start());
        TestCase.assertEquals(1, mresult.end());
        result = s.findWithinHorizon(Pattern.compile("^(a)$", Pattern.MULTILINE), 5);
        TestCase.assertNull(result);
        try {
            mresult = s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("");
        result = s.findWithinHorizon(Pattern.compile("^"), 0);
        TestCase.assertEquals("", result);
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        result = s.findWithinHorizon(Pattern.compile("$"), 0);
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        s = new Scanner("1 fish 2 fish red fish blue fish");
        result = s.findWithinHorizon(Pattern.compile("(\\d+) fish (\\d+) fish (\\w+) fish (\\w+)"), 10);
        TestCase.assertNull(result);
        try {
            mresult = s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals(0, mresult.groupCount());
        result = s.findWithinHorizon(Pattern.compile("(\\d+) fish (\\d+) fish (\\w+) fish (\\w+)"), 100);
        TestCase.assertEquals("1 fish 2 fish red fish blue", result);
        mresult = s.match();
        TestCase.assertEquals(4, mresult.groupCount());
        TestCase.assertEquals("1", mresult.group(1));
        TestCase.assertEquals("2", mresult.group(2));
        TestCase.assertEquals("red", mresult.group(3));
        TestCase.assertEquals("blue", mresult.group(4));
        s = new Scanner("test");
        s.close();
        try {
            s.findWithinHorizon(Pattern.compile("test"), 1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("word1 WorD2  ");
        s.close();
        try {
            s.findWithinHorizon(Pattern.compile("\\d+"), 10);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("word1 WorD2 wOrd3 ");
        Pattern pattern = Pattern.compile("\\d+");
        TestCase.assertEquals("1", s.findWithinHorizon(pattern, 10));
        TestCase.assertEquals("WorD2", s.next());
        TestCase.assertEquals("3", s.findWithinHorizon(pattern, 15));
        // Regression test
        s = new Scanner(new ScannerTest.MockStringReader("MockStringReader"));
        pattern = Pattern.compile("test");
        result = s.findWithinHorizon(pattern, 10);
        TestCase.assertEquals("test", result);
        // Test the situation when input length is longer than buffer size.
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1026; i++) {
            stringBuilder.append('a');
        }
        s = new Scanner(stringBuilder.toString());
        pattern = Pattern.compile("\\p{Lower}+");
        result = s.findWithinHorizon(pattern, 1026);
        TestCase.assertEquals(stringBuilder.toString().length(), result.length());
        TestCase.assertEquals(stringBuilder.toString(), result);
        // Test the situation when input length is longer than buffer size and
        // set horizon to buffer size.
        stringBuilder = new StringBuilder();
        for (int i = 0; i < 1026; i++) {
            stringBuilder.append('a');
        }
        s = new Scanner(stringBuilder.toString());
        pattern = Pattern.compile("\\p{Lower}+");
        result = s.findWithinHorizon(pattern, 1022);
        TestCase.assertEquals(1022, result.length());
        TestCase.assertEquals(stringBuilder.subSequence(0, 1022), result);
        // Test the situation, under which pattern is clipped by buffer.
        stringBuilder = new StringBuilder();
        for (int i = 0; i < 1022; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append("bbc");
        TestCase.assertEquals(1025, stringBuilder.length());
        s = new Scanner(stringBuilder.toString());
        pattern = Pattern.compile("bbc");
        result = s.findWithinHorizon(pattern, 1025);
        TestCase.assertEquals(3, result.length());
        TestCase.assertEquals(stringBuilder.subSequence(1022, 1025), result);
        stringBuilder = new StringBuilder();
        for (int i = 0; i < 1026; i++) {
            stringBuilder.append('a');
        }
        s = new Scanner(stringBuilder.toString());
        pattern = Pattern.compile("\\p{Lower}+");
        result = s.findWithinHorizon(pattern, 0);
        TestCase.assertEquals(stringBuilder.toString(), result);
        stringBuilder = new StringBuilder();
        for (int i = 0; i < 10240; i++) {
            stringBuilder.append('-');
        }
        stringBuilder.replace(0, 2, "aa");
        s = new Scanner(stringBuilder.toString());
        result = s.findWithinHorizon(Pattern.compile("aa"), 0);
        TestCase.assertEquals("aa", result);
        s = new Scanner("aaaa");
        result = s.findWithinHorizon(Pattern.compile("a*"), 0);
        TestCase.assertEquals("aaaa", result);
    }

    /**
     *
     *
     * @unknown java.util.Scanner#findInLine(Pattern)
     */
    public void test_findInLine_LPattern() {
        Scanner s = new Scanner("");
        try {
            s.findInLine(((Pattern) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        String result = s.findInLine(Pattern.compile("^"));
        TestCase.assertEquals("", result);
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        result = s.findInLine(Pattern.compile("$"));
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        /* When we use the operation of findInLine(Pattern), the match region
        should not span the line separator.
         */
        s = new Scanner("aa\nb.b");
        result = s.findInLine(Pattern.compile("a\nb*"));
        TestCase.assertNull(result);
        s = new Scanner("aa\nbb.b");
        result = s.findInLine(Pattern.compile("\\."));
        TestCase.assertNull(result);
        s = new Scanner("abcd1234test\n");
        result = s.findInLine(Pattern.compile("\\p{Lower}+"));
        TestCase.assertEquals("abcd", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        result = s.findInLine(Pattern.compile("\\p{Digit}{5}"));
        TestCase.assertNull(result);
        try {
            matchResult = s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        result = s.findInLine(Pattern.compile("\\p{Lower}+"));
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(8, matchResult.start());
        TestCase.assertEquals(12, matchResult.end());
        char[] chars = new char[2048];
        Arrays.fill(chars, 'a');
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        stringBuilder.append("1234");
        s = new Scanner(stringBuilder.toString());
        result = s.findInLine(Pattern.compile("\\p{Digit}+"));
        TestCase.assertEquals("1234", result);
        matchResult = s.match();
        TestCase.assertEquals(2048, matchResult.start());
        TestCase.assertEquals(2052, matchResult.end());
        s = new Scanner("test");
        s.close();
        try {
            s.findInLine(((Pattern) (null)));
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("test1234\n1234 test");
        result = s.findInLine(Pattern.compile("test"));
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        int number = s.nextInt();
        TestCase.assertEquals(1234, number);
        matchResult = s.match();
        TestCase.assertEquals(4, matchResult.start());
        TestCase.assertEquals(8, matchResult.end());
        result = s.next();
        TestCase.assertEquals("1234", result);
        matchResult = s.match();
        TestCase.assertEquals(9, matchResult.start());
        TestCase.assertEquals(13, matchResult.end());
        result = s.findInLine(Pattern.compile("test"));
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(14, matchResult.start());
        TestCase.assertEquals(18, matchResult.end());
        s = new Scanner("test\u0085\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        s = new Scanner("test\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        s = new Scanner("test\n123\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
        // RI fails. It is a RI's bug.
        TestCase.assertNull(result);
        s = new Scanner("   *\n");
        result = s.findInLine(Pattern.compile("^\\s*(?:\\*(?=[^/]))"));
        TestCase.assertEquals("   *", result);
    }

    public void test_findInLine_LString_NPEs() {
        s = new Scanner("test");
        try {
            s.findInLine(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        s.close();
        try {
            s.findInLine(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            s.findInLine("test");
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void test_findInLine_LString() {
        Scanner s = new Scanner("");
        String result = s.findInLine("^");
        TestCase.assertEquals("", result);
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        result = s.findInLine("$");
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(0, matchResult.end());
        // When we use the operation of findInLine(Pattern), the match region
        // should not span the line separator.
        s = new Scanner("aa\nb.b");
        result = s.findInLine("a\nb*");
        TestCase.assertNull(result);
        s = new Scanner("aa\nbb.b");
        result = s.findInLine("\\.");
        TestCase.assertNull(result);
        s = new Scanner("abcd1234test\n");
        result = s.findInLine("\\p{Lower}+");
        TestCase.assertEquals("abcd", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        result = s.findInLine("\\p{Digit}{5}");
        TestCase.assertNull(result);
        try {
            matchResult = s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        result = s.findInLine("\\p{Lower}+");
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(8, matchResult.start());
        TestCase.assertEquals(12, matchResult.end());
        char[] chars = new char[2048];
        Arrays.fill(chars, 'a');
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        stringBuilder.append("1234");
        s = new Scanner(stringBuilder.toString());
        result = s.findInLine("\\p{Digit}+");
        TestCase.assertEquals("1234", result);
        matchResult = s.match();
        TestCase.assertEquals(2048, matchResult.start());
        TestCase.assertEquals(2052, matchResult.end());
        s = new Scanner("test1234\n1234 test");
        result = s.findInLine("test");
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        int number = s.nextInt();
        TestCase.assertEquals(1234, number);
        matchResult = s.match();
        TestCase.assertEquals(4, matchResult.start());
        TestCase.assertEquals(8, matchResult.end());
        result = s.next();
        TestCase.assertEquals("1234", result);
        matchResult = s.match();
        TestCase.assertEquals(9, matchResult.start());
        TestCase.assertEquals(13, matchResult.end());
        result = s.findInLine("test");
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(14, matchResult.start());
        TestCase.assertEquals(18, matchResult.end());
        s = new Scanner("test\u0085\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        s = new Scanner("test\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        s = new Scanner("test\n123\ntest");
        result = s.findInLine("est");
        TestCase.assertEquals("est", result);
        result = s.findInLine("est");
    }

    /**
     *
     *
     * @unknown java.util.Scanner#skip(Pattern)
     */
    public void test_skip_LPattern() {
        s = new Scanner("test");
        try {
            s.skip(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // If pattern does not match, NoSuchElementException will be thrown out.
        s = new Scanner("1234");
        try {
            s.skip(Pattern.compile("\\p{Lower}"));
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        // Then, no matchResult will be thrown out.
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s.skip(Pattern.compile("\\p{Digit}"));
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s.skip(Pattern.compile("\\p{Digit}+"));
        matchResult = s.match();
        TestCase.assertEquals(1, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        s.close();
        try {
            s.skip(Pattern.compile("test"));
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        ScannerTest.MockStringReader2Read reader = new ScannerTest.MockStringReader2Read("test");
        s = new Scanner(reader);
        try {
            s.skip(Pattern.compile("\\p{Digit}{4}"));
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s.skip(Pattern.compile("\\p{Digit}{3}\\p{Lower}"));
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(4, matchResult.end());
        s.close();
        try {
            s.skip(((Pattern) (null)));
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = new char[1024];
        Arrays.fill(chars, 'a');
        stringBuilder.append(chars);
        stringBuilder.append('3');
        s = new Scanner(stringBuilder.toString());
        s.skip(Pattern.compile("\\p{Lower}+\\p{Digit}"));
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1025, matchResult.end());
        // Large amount of input may be cached
        chars = new char[102400];
        Arrays.fill(chars, 'a');
        stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        s = new Scanner(stringBuilder.toString());
        s.skip(Pattern.compile(".*"));
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(102400, matchResult.end());
        // skip something without risking a NoSuchElementException
        s.skip(Pattern.compile("[ \t]*"));
        matchResult = s.match();
        TestCase.assertEquals(102400, matchResult.start());
        TestCase.assertEquals(102400, matchResult.end());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#skip(String)
     */
    public void test_skip_LString() {
        s = new Scanner("test");
        try {
            s.skip(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextDouble()
     */
    public void test_nextDouble() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(123.0, s.nextDouble());
        TestCase.assertEquals(456.0, s.nextDouble());
        TestCase.assertEquals(123.4, s.nextDouble());
        TestCase.assertEquals(0.123, s.nextDouble());
        try {
            s.nextDouble();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(123.4, s.nextDouble());
        TestCase.assertEquals((-456.7), s.nextDouble());
        TestCase.assertEquals(123456.789, s.nextDouble());
        try {
            s.nextDouble();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(1.234E12, s.nextDouble());
        TestCase.assertEquals((-4.567E14), s.nextDouble());
        TestCase.assertEquals(1.23456789E-5, s.nextDouble());
        s = new Scanner("NaN Infinity -Infinity");
        TestCase.assertEquals(Double.NaN, s.nextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, s.nextDouble());
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, s.nextDouble());
        // The following test case fails on RI
        s = new Scanner("\u221e");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(Double.POSITIVE_INFINITY, s.nextDouble());
        String str = String.valueOf(((Double.MAX_VALUE) * 2));
        s = new Scanner(str);
        TestCase.assertEquals(Double.POSITIVE_INFINITY, s.nextDouble());
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(23456.0, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(23.456, s.nextDouble());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(23.456, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(23456.0, s.nextDouble());
        s = new Scanner("23,456.7 23.456,7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(23456.7, s.nextDouble());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(23456.7, s.nextDouble());
        s = new Scanner("-123.4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals((-123.4), s.nextDouble());
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @unknown java.util.Scanner#nextBigDecimal()
     */
    public void test_nextBigDecimal() throws IOException {
        s = new Scanner("123 45\u0666. 123.4 .123 ");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("123"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("456"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123.4"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("0.123"), s.nextBigDecimal());
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        s = new Scanner("+123.4 -456.7 123,456.789 0.1\u06623,4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("123.4"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("-456.7"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("123456.789"), s.nextBigDecimal());
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        // Scientific notation
        s = new Scanner("+123.4E10 -456.7e+12 123,456.789E-10");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("1.234E12"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("-4.567E14"), s.nextBigDecimal());
        TestCase.assertEquals(new BigDecimal("1.23456789E-5"), s.nextBigDecimal());
        s = new Scanner("NaN");
        try {
            s.nextBigDecimal();
            TestCase.fail();
        } catch (InputMismatchException expected) {
        }
        /* Different locale can only recognize corresponding locale sensitive
        string. ',' is used in many locales as group separator.
         */
        s = new Scanner("23,456 23,456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("23456"), s.nextBigDecimal());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(new BigDecimal("23.456"), s.nextBigDecimal());
        s = new Scanner("23.456 23.456");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("23.456"), s.nextBigDecimal());
        s.useLocale(Locale.GERMANY);
        TestCase.assertEquals(new BigDecimal("23456"), s.nextBigDecimal());
        s = new Scanner("23,456.7");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("23456.7"), s.nextBigDecimal());
        s = new Scanner("-123.4");
        s.useLocale(Locale.ENGLISH);
        TestCase.assertEquals(new BigDecimal("-123.4"), s.nextBigDecimal());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#toString()
     */
    public void test_toString() {
        s = new Scanner("test");
        TestCase.assertNotNull(s.toString());
    }

    /**
     *
     *
     * @unknown java.util.Scanner#nextLine()
     */
    public void test_nextLine() {
        s = new Scanner("");
        s.close();
        try {
            s.nextLine();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("test\r\ntest");
        String result = s.nextLine();
        TestCase.assertEquals("test", result);
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(6, matchResult.end());
        s = new Scanner("\u0085");
        result = s.nextLine();
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("\u2028");
        result = s.nextLine();
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("\u2029");
        result = s.nextLine();
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("");
        try {
            result = s.nextLine();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
        try {
            s.match();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("Ttest");
        result = s.nextLine();
        TestCase.assertEquals("Ttest", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(5, matchResult.end());
        s = new Scanner("\r\n");
        result = s.nextLine();
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(2, matchResult.end());
        char[] chars = new char[1024];
        Arrays.fill(chars, 'a');
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        chars = new char[]{ '+', '-' };
        stringBuilder.append(chars);
        stringBuilder.append("\u2028");
        s = new Scanner(stringBuilder.toString());
        result = s.nextLine();
        TestCase.assertEquals(stringBuilder.toString().substring(0, 1026), result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1027, matchResult.end());
        chars = new char[1023];
        Arrays.fill(chars, 'a');
        stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        stringBuilder.append("\r\n");
        s = new Scanner(stringBuilder.toString());
        result = s.nextLine();
        TestCase.assertEquals(stringBuilder.toString().substring(0, 1023), result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1025, matchResult.end());
        s = new Scanner("  ");
        result = s.nextLine();
        TestCase.assertEquals("  ", result);
        s = new Scanner("test\n\n\n");
        result = s.nextLine();
        TestCase.assertEquals("test", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(5, matchResult.end());
        result = s.nextLine();
        matchResult = s.match();
        TestCase.assertEquals(5, matchResult.start());
        TestCase.assertEquals(6, matchResult.end());
        s = new Scanner("\n\n\n");
        result = s.nextLine();
        TestCase.assertEquals("", result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        result = s.nextLine();
        matchResult = s.match();
        TestCase.assertEquals(1, matchResult.start());
        TestCase.assertEquals(2, matchResult.end());
        s = new Scanner("123 test\n   ");
        int value = s.nextInt();
        TestCase.assertEquals(123, value);
        result = s.nextLine();
        TestCase.assertEquals(" test", result);
        s = new Scanner("test\n ");
        result = s.nextLine();
        TestCase.assertEquals("test", result);
        // Regression test for Harmony-4774
        class CountReadable implements Readable {
            int counter = 0;

            public int read(CharBuffer charBuffer) throws IOException {
                (counter)++;
                charBuffer.append("hello\n");
                return 6;
            }
        }
        CountReadable cr = new CountReadable();
        s = new Scanner(cr);
        result = s.nextLine();
        // We expect read() to be called only once, otherwise we see the problem
        // when reading from System.in described in Harmony-4774
        TestCase.assertEquals(1, cr.counter);
        TestCase.assertEquals("hello", result);
    }

    /**
     *
     *
     * @unknown java.util.Scanner#hasNextLine()
     */
    public void test_hasNextLine() {
        s = new Scanner("");
        s.close();
        try {
            s.hasNextLine();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        s = new Scanner("test\r\ntest");
        boolean result = s.hasNextLine();
        TestCase.assertTrue(result);
        MatchResult matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(6, matchResult.end());
        s = new Scanner("\u0085");
        result = s.hasNextLine();
        TestCase.assertTrue(result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("\u2028");
        result = s.hasNextLine();
        TestCase.assertTrue(result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("\u2029");
        result = s.hasNextLine();
        TestCase.assertTrue(result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        s = new Scanner("test\n");
        TestCase.assertTrue(s.hasNextLine());
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(5, matchResult.end());
        char[] chars = new char[2048];
        Arrays.fill(chars, 'a');
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars);
        s = new Scanner(stringBuilder.toString());
        result = s.hasNextLine();
        TestCase.assertTrue(result);
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(2048, matchResult.end());
        s = new Scanner("\n\n\n");
        TestCase.assertTrue(s.hasNextLine());
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
        // The scanner will not advance any input.
        TestCase.assertTrue(s.hasNextLine());
        matchResult = s.match();
        TestCase.assertEquals(0, matchResult.start());
        TestCase.assertEquals(1, matchResult.end());
    }

    public void test_hasNextLine_sequence() throws IOException {
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream();
        final Scanner scanner = new Scanner(pis);
        pis.connect(pos);
        final List<String> result = new ArrayList<String>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.add(line);
                } 
            }
        });
        thread.start();
        for (int index = 0; index < 5; index++) {
            String line = ("line" + index) + "\n";
            pos.write(line.getBytes());
            pos.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            TestCase.assertEquals((index + 1), result.size());
        }
        pis.close();
        pos.close();
        try {
            thread.join(1000);
        } catch (InterruptedException ignored) {
        }
        TestCase.assertFalse(scanner.hasNextLine());
    }

    // http://code.google.com/p/android/issues/detail?id=57050
    public void testPerformance() throws Exception {
        int count = 100000;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(baos));
        for (int i = 0; i < count; ++i) {
            out.write(((Integer.toString(123)) + " "));
        }
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        bais.mark((-1));
        Scanner s = new Scanner(new BufferedReader(new InputStreamReader(bais)));
        for (int i = 0; i < count; ++i) {
            if ((s.nextInt()) != 123) {
                TestCase.fail();
            }
        }
        bais.reset();
        s = new Scanner(new BufferedReader(new InputStreamReader(bais)));
        for (int i = 0; i < count; ++i) {
            if ((s.nextFloat()) != 123.0) {
                TestCase.fail();
            }
        }
    }
}

