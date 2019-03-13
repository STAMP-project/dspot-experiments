/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static HttpWriter.MAX_OUTPUT_CHARS;


public class HttpWriterTest {
    private HttpOutput _httpOut;

    private ByteBuffer _bytes;

    @Test
    public void testSimpleUTF8() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        _writer.write("Now is the time");
        assertArrayEquals("Now is the time".getBytes(StandardCharsets.UTF_8), BufferUtil.toArray(_bytes));
    }

    @Test
    public void testUTF8() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        _writer.write("How now \uff22rown cow");
        assertArrayEquals("How now \uff22rown cow".getBytes(StandardCharsets.UTF_8), BufferUtil.toArray(_bytes));
    }

    @Test
    public void testUTF16() throws Exception {
        HttpWriter _writer = new EncodingHttpWriter(_httpOut, StringUtil.__UTF16);
        _writer.write("How now \uff22rown cow");
        assertArrayEquals("How now \uff22rown cow".getBytes(StandardCharsets.UTF_16), BufferUtil.toArray(_bytes));
    }

    @Test
    public void testNotCESU8() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        String data = "xxx\ud801\udc00xxx";
        _writer.write(data);
        Assertions.assertEquals("787878F0909080787878", TypeUtil.toHexString(BufferUtil.toArray(_bytes)));
        assertArrayEquals(data.getBytes(StandardCharsets.UTF_8), BufferUtil.toArray(_bytes));
        Assertions.assertEquals(((3 + 4) + 3), _bytes.remaining());
        Utf8StringBuilder buf = new Utf8StringBuilder();
        buf.append(BufferUtil.toArray(_bytes), 0, _bytes.remaining());
        Assertions.assertEquals(data, buf.toString());
    }

    @Test
    public void testMultiByteOverflowUTF8() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        final String singleByteStr = "a";
        final String multiByteDuplicateStr = "\uff22";
        int remainSize = 1;
        int multiByteStrByteLength = multiByteDuplicateStr.getBytes(StandardCharsets.UTF_8).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ((MAX_OUTPUT_CHARS) - multiByteStrByteLength); i++) {
            sb.append(singleByteStr);
        }
        sb.append(multiByteDuplicateStr);
        for (int i = 0; i < remainSize; i++) {
            sb.append(singleByteStr);
        }
        char[] buf = new char[(MAX_OUTPUT_CHARS) * 3];
        int length = (((MAX_OUTPUT_CHARS) - multiByteStrByteLength) + remainSize) + 1;
        sb.toString().getChars(0, length, buf, 0);
        _writer.write(buf, 0, length);
        Assertions.assertEquals(sb.toString(), new String(BufferUtil.toArray(_bytes), StandardCharsets.UTF_8));
    }

    @Test
    public void testISO8859() throws Exception {
        HttpWriter _writer = new Iso88591HttpWriter(_httpOut);
        _writer.write("How now \uff22rown cow");
        Assertions.assertEquals(new String(BufferUtil.toArray(_bytes), StandardCharsets.ISO_8859_1), "How now ?rown cow");
    }

    @Test
    public void testUTF16x2() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        String source = "\ud842\udf9f";
        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        _writer.write(source.toCharArray(), 0, source.toCharArray().length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        osw.write(source.toCharArray(), 0, source.toCharArray().length);
        osw.flush();
        myReportBytes(bytes);
        myReportBytes(baos.toByteArray());
        myReportBytes(BufferUtil.toArray(_bytes));
        assertArrayEquals(bytes, BufferUtil.toArray(_bytes));
        assertArrayEquals(baos.toByteArray(), BufferUtil.toArray(_bytes));
    }

    @Test
    public void testMultiByteOverflowUTF16x2() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        final String singleByteStr = "a";
        int remainSize = 1;
        final String multiByteDuplicateStr = "\ud842\udf9f";
        int adjustSize = -1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ((MAX_OUTPUT_CHARS) + adjustSize); i++) {
            sb.append(singleByteStr);
        }
        sb.append(multiByteDuplicateStr);
        for (int i = 0; i < remainSize; i++) {
            sb.append(singleByteStr);
        }
        String source = sb.toString();
        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        _writer.write(source.toCharArray(), 0, source.toCharArray().length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        osw.write(source.toCharArray(), 0, source.toCharArray().length);
        osw.flush();
        myReportBytes(bytes);
        myReportBytes(baos.toByteArray());
        myReportBytes(BufferUtil.toArray(_bytes));
        assertArrayEquals(bytes, BufferUtil.toArray(_bytes));
        assertArrayEquals(baos.toByteArray(), BufferUtil.toArray(_bytes));
    }

    @Test
    public void testMultiByteOverflowUTF16x2_2() throws Exception {
        HttpWriter _writer = new Utf8HttpWriter(_httpOut);
        final String singleByteStr = "a";
        int remainSize = 1;
        final String multiByteDuplicateStr = "\ud842\udf9f";
        int adjustSize = -2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ((MAX_OUTPUT_CHARS) + adjustSize); i++) {
            sb.append(singleByteStr);
        }
        sb.append(multiByteDuplicateStr);
        for (int i = 0; i < remainSize; i++) {
            sb.append(singleByteStr);
        }
        String source = sb.toString();
        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        _writer.write(source.toCharArray(), 0, source.toCharArray().length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        osw.write(source.toCharArray(), 0, source.toCharArray().length);
        osw.flush();
        myReportBytes(bytes);
        myReportBytes(baos.toByteArray());
        myReportBytes(BufferUtil.toArray(_bytes));
        assertArrayEquals(bytes, BufferUtil.toArray(_bytes));
        assertArrayEquals(baos.toByteArray(), BufferUtil.toArray(_bytes));
    }
}

