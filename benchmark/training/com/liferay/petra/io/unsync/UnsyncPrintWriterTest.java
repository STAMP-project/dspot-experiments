/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.io.unsync;


import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class UnsyncPrintWriterTest extends BaseWriterTestCase {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testAppend() throws Exception {
        StringWriter stringWriter = new StringWriter();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(stringWriter);
        unsyncPrintWriter.append('A');
        Assert.assertEquals("A", stringWriter.toString());
        unsyncPrintWriter.append(null);
        Assert.assertEquals("Anull", stringWriter.toString());
        unsyncPrintWriter.append("B");
        Assert.assertEquals("AnullB", stringWriter.toString());
        unsyncPrintWriter.append(null, 0, 4);
        Assert.assertEquals("AnullBnull", stringWriter.toString());
        unsyncPrintWriter.append("C", 0, 1);
        Assert.assertEquals("AnullBnullC", stringWriter.toString());
    }

    @Test
    public void testCheckError() {
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(new StringWriter());
        unsyncPrintWriter.close();
        unsyncPrintWriter.println();
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
        unsyncPrintWriter.write(new char[0], 0, 0);
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
        unsyncPrintWriter.write('c');
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
        unsyncPrintWriter.write("test");
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
        unsyncPrintWriter.write("test", 0, 4);
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
    }

    @Test
    public void testClose() {
        AtomicBoolean calledClose = new AtomicBoolean();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(new StringWriter() {
            @Override
            public void close() {
                calledClose.set(true);
            }
        });
        unsyncPrintWriter.close();
        Assert.assertTrue(calledClose.get());
        calledClose.set(false);
        unsyncPrintWriter.close();
        Assert.assertFalse(calledClose.get());
        unsyncPrintWriter.reset(new StringWriter() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
        unsyncPrintWriter.close();
        Assert.assertTrue(unsyncPrintWriter.checkError());
    }

    @Test
    public void testConstructor() throws Exception {
        // UnsyncPrintWriter(File file)
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(new File(UnsyncPrintWriterTest._TEST_FILE_NAME));
        Assert.assertTrue(((UnsyncPrintWriterTest._getOut(unsyncPrintWriter)) instanceof FileWriter));
        // UnsyncPrintWriter(File file, String charSequence)
        unsyncPrintWriter = new UnsyncPrintWriter(new File(UnsyncPrintWriterTest._TEST_FILE_NAME), "UTF8");
        Assert.assertTrue(((UnsyncPrintWriterTest._getOut(unsyncPrintWriter)) instanceof OutputStreamWriter));
        OutputStreamWriter outputStreamWriter = ((OutputStreamWriter) (UnsyncPrintWriterTest._getOut(unsyncPrintWriter)));
        Assert.assertEquals("UTF8", outputStreamWriter.getEncoding());
        // UnsyncPrintWriter(OutputStream outputStream)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        unsyncPrintWriter = new UnsyncPrintWriter(byteArrayOutputStream);
        Assert.assertTrue(((UnsyncPrintWriterTest._getOut(unsyncPrintWriter)) instanceof OutputStreamWriter));
        // UnsyncPrintWriter(String fileName)
        unsyncPrintWriter = new UnsyncPrintWriter(UnsyncPrintWriterTest._TEST_FILE_NAME);
        Assert.assertTrue(((UnsyncPrintWriterTest._getOut(unsyncPrintWriter)) instanceof FileWriter));
        // UnsyncPrintWriter(String fileName, String csn)
        unsyncPrintWriter = new UnsyncPrintWriter(UnsyncPrintWriterTest._TEST_FILE_NAME, "UTF8");
        Assert.assertTrue(((UnsyncPrintWriterTest._getOut(unsyncPrintWriter)) instanceof OutputStreamWriter));
        outputStreamWriter = ((OutputStreamWriter) (UnsyncPrintWriterTest._getOut(unsyncPrintWriter)));
        Assert.assertEquals("UTF8", outputStreamWriter.getEncoding());
        // UnsyncPrintWriter(Writer writer)
        StringWriter stringWriter = new StringWriter();
        unsyncPrintWriter = new UnsyncPrintWriter(stringWriter);
        Assert.assertEquals(stringWriter, UnsyncPrintWriterTest._getOut(unsyncPrintWriter));
    }

    @Test
    public void testFlush() {
        AtomicBoolean calledFlush = new AtomicBoolean();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(new StringWriter() {
            @Override
            public void flush() {
                calledFlush.set(true);
            }
        });
        unsyncPrintWriter.flush();
        Assert.assertTrue(calledFlush.get());
        calledFlush.set(false);
        unsyncPrintWriter.close();
        unsyncPrintWriter.flush();
        Assert.assertFalse(calledFlush.get());
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(null);
        Assert.assertFalse(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(new Writer() {
            @Override
            public void close() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void flush() throws IOException {
                throw new IOException();
            }

            @Override
            public void write(char[] cbuf, int off, int len) {
                throw new UnsupportedOperationException();
            }
        });
        unsyncPrintWriter.flush();
        Assert.assertTrue(unsyncPrintWriter.checkError());
    }

    @Test
    public void testInterruptedIOException() throws Exception {
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(new Writer() {
            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new InterruptedIOException();
            }

            @Override
            public void write(int c) throws IOException {
                throw new InterruptedIOException();
            }

            @Override
            public void write(String s) throws IOException {
                throw new InterruptedIOException();
            }

            @Override
            public void write(String s, int offset, int length) throws IOException {
                throw new InterruptedIOException();
            }
        });
        unsyncPrintWriter.println();
        Assert.assertTrue(Thread.interrupted());
        unsyncPrintWriter.write(new char[0], 0, 0);
        Assert.assertTrue(Thread.interrupted());
        unsyncPrintWriter.write('c');
        Assert.assertTrue(Thread.interrupted());
        unsyncPrintWriter.write("test");
        Assert.assertTrue(Thread.interrupted());
        unsyncPrintWriter.write("test", 0, 4);
        Assert.assertTrue(Thread.interrupted());
    }

    @Test
    public void testIOException() {
        Writer writer = new Writer() {
            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException();
            }

            @Override
            public void write(int c) throws IOException {
                throw new IOException();
            }

            @Override
            public void write(String s) throws IOException {
                throw new IOException();
            }

            @Override
            public void write(String s, int offset, int length) throws IOException {
                throw new IOException();
            }
        };
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(writer);
        unsyncPrintWriter.println();
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(writer);
        unsyncPrintWriter.write(new char[0], 0, 0);
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(writer);
        unsyncPrintWriter.write('c');
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(writer);
        unsyncPrintWriter.write("test");
        Assert.assertTrue(unsyncPrintWriter.checkError());
        unsyncPrintWriter.reset(writer);
        unsyncPrintWriter.write("test", 0, 4);
        Assert.assertTrue(unsyncPrintWriter.checkError());
    }

    @Test
    public void testPrintf() {
        StringWriter stringWriter = new StringWriter();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(stringWriter);
        unsyncPrintWriter.printf("%2$2d %1$2s", "a", 1);
        Assert.assertEquals(" 1  a", stringWriter.toString());
        StringBuffer stringBuffer = stringWriter.getBuffer();
        stringBuffer.setLength(0);
        unsyncPrintWriter.printf("%1$s", "reuse formatter");
        Assert.assertEquals("reuse formatter", stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.printf(Locale.CANADA, "%1$s", "replace formatter");
        Assert.assertEquals("replace formatter", stringWriter.toString());
        unsyncPrintWriter.close();
        unsyncPrintWriter.printf(Locale.getDefault(), null);
        Assert.assertTrue(unsyncPrintWriter.checkError());
    }

    @Test
    public void testPrintln() {
        StringWriter stringWriter = new StringWriter();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(stringWriter);
        unsyncPrintWriter.println();
        String lineSeparator = System.getProperty("line.separator");
        Assert.assertEquals(lineSeparator, stringWriter.toString());
        StringBuffer stringBuffer = stringWriter.getBuffer();
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(true);
        Assert.assertEquals(("true" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(false);
        Assert.assertEquals(("false" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println('c');
        Assert.assertEquals(("c" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(new char[]{ 'a', 'b', 'c' });
        Assert.assertEquals(("abc" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(3.14);
        Assert.assertEquals(("3.14" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(1.62F);
        Assert.assertEquals(("1.62" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(10);
        Assert.assertEquals(("10" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(100L);
        Assert.assertEquals(("100" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(new Object() {
            @Override
            public String toString() {
                return "test";
            }
        });
        Assert.assertEquals(("test" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println(((String) (null)));
        Assert.assertEquals(("null" + lineSeparator), stringWriter.toString());
        stringBuffer.setLength(0);
        unsyncPrintWriter.println("???");
        Assert.assertEquals(("???" + lineSeparator), stringWriter.toString());
        unsyncPrintWriter.close();
        unsyncPrintWriter.println();
        Assert.assertTrue(unsyncPrintWriter.checkError());
    }

    @Test
    public void testWrite() {
        StringWriter stringWriter = new StringWriter();
        UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(stringWriter);
        // write(int c)
        unsyncPrintWriter.write('a');
        Assert.assertEquals("a", stringWriter.toString());
        unsyncPrintWriter.write('b');
        Assert.assertEquals("ab", stringWriter.toString());
        // write(char[] chars)
        unsyncPrintWriter.write(new char[]{ 'c', 'd' });
        Assert.assertEquals("abcd", stringWriter.toString());
        unsyncPrintWriter.write(new char[]{ 'e', 'f' });
        Assert.assertEquals("abcdef", stringWriter.toString());
        // write(char[] chars, int offset, int length)
        unsyncPrintWriter.write(new char[]{ 'e', 'f', 'g', 'h', 'i', 'j' }, 2, 2);
        Assert.assertEquals("abcdefgh", stringWriter.toString());
        unsyncPrintWriter.write(new char[]{ 'g', 'h', 'i', 'j', 'k', 'l' }, 2, 2);
        Assert.assertEquals("abcdefghij", stringWriter.toString());
        // write(String string)
        unsyncPrintWriter.write("kl");
        Assert.assertEquals("abcdefghijkl", stringWriter.toString());
        unsyncPrintWriter.write("mn");
        Assert.assertEquals("abcdefghijklmn", stringWriter.toString());
        // write(String string, int offset, int length)
        unsyncPrintWriter.write("mnopqr", 2, 2);
        Assert.assertEquals("abcdefghijklmnop", stringWriter.toString());
        unsyncPrintWriter.write("opqrst", 2, 2);
        Assert.assertEquals("abcdefghijklmnopqr", stringWriter.toString());
    }

    private static final String _TEST_FILE_NAME = "UnsyncPrintWriterTest.testFile";

    private static final Field _writerField;

    static {
        try {
            _writerField = UnsyncPrintWriter.class.getDeclaredField("_writer");
            UnsyncPrintWriterTest._writerField.setAccessible(true);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

