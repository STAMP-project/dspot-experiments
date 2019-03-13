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
package org.eclipse.jetty.util;


import Termination.CR;
import Termination.CRLF;
import Termination.LF;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReadLineInputStreamTest {
    BlockingArrayQueue<String> _queue = new BlockingArrayQueue();

    PipedInputStream _pin;

    volatile PipedOutputStream _pout;

    ReadLineInputStream _in;

    volatile Thread _writer;

    @Test
    public void testCR() throws Exception {
        _queue.add("\rHello\rWorld\r\r");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CR), _in.getLineTerminations());
    }

    @Test
    public void testLF() throws Exception {
        _queue.add("\nHello\nWorld\n\n");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(LF), _in.getLineTerminations());
    }

    @Test
    public void testCRLF() throws Exception {
        _queue.add("\r\nHello\r\nWorld\r\n\r\n");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CRLF), _in.getLineTerminations());
    }

    @Test
    public void testCRBlocking() throws Exception {
        _queue.add("");
        _queue.add("\r");
        _queue.add("Hello");
        _queue.add("\rWorld\r");
        _queue.add("\r");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CR), _in.getLineTerminations());
    }

    @Test
    public void testLFBlocking() throws Exception {
        _queue.add("");
        _queue.add("\n");
        _queue.add("Hello");
        _queue.add("\nWorld\n");
        _queue.add("\n");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(LF), _in.getLineTerminations());
    }

    @Test
    public void testCRLFBlocking() throws Exception {
        _queue.add("\r");
        _queue.add("\nHello");
        _queue.add("\r\nWorld\r");
        _queue.add("\n\r");
        _queue.add("\n");
        _queue.add("");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals("Hello", _in.readLine());
        Assertions.assertEquals("World", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CRLF), _in.getLineTerminations());
    }

    @Test
    public void testHeaderLFBodyLF() throws Exception {
        _queue.add("Header\n");
        _queue.add("\n");
        _queue.add("\nBody\n");
        _queue.add("\n");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("Header", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        byte[] body = new byte[6];
        _in.read(body);
        Assertions.assertEquals("\nBody\n", new String(body, 0, 6, StandardCharsets.UTF_8));
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(LF), _in.getLineTerminations());
    }

    @Test
    public void testHeaderCRBodyLF() throws Exception {
        _queue.add("Header\r");
        _queue.add("\r");
        _queue.add("\nBody\n");
        _queue.add("\r");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("Header", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        byte[] body = new byte[6];
        _in.read(body);
        Assertions.assertEquals("\nBody\n", new String(body, 0, 6, StandardCharsets.UTF_8));
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CR), _in.getLineTerminations());
    }

    @Test
    public void testHeaderCRLFBodyLF() throws Exception {
        _queue.add("Header\r\n");
        _queue.add("\r\n");
        _queue.add("\nBody\n");
        _queue.add("\r\n");
        _queue.add("__CLOSE__");
        Assertions.assertEquals("Header", _in.readLine());
        Assertions.assertEquals("", _in.readLine());
        byte[] body = new byte[6];
        _in.read(body);
        Assertions.assertEquals("\nBody\n", new String(body, 0, 6, StandardCharsets.UTF_8));
        Assertions.assertEquals("", _in.readLine());
        Assertions.assertEquals(null, _in.readLine());
        Assertions.assertEquals(EnumSet.of(CRLF), _in.getLineTerminations());
    }
}

