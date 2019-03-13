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
package org.eclipse.jetty.http;


import HttpGenerator.CHUNK_SIZE;
import HttpGenerator.Result;
import HttpGenerator.Result.CONTINUE;
import HttpGenerator.Result.DONE;
import HttpGenerator.Result.FLUSH;
import HttpGenerator.Result.NEED_CHUNK;
import HttpGenerator.Result.NEED_HEADER;
import HttpGenerator.Result.NEED_INFO;
import HttpGenerator.State.COMMITTED;
import HttpGenerator.State.COMPLETING;
import HttpGenerator.State.END;
import HttpGenerator.State.START;
import HttpVersion.HTTP_1_1;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpGeneratorClientTest {
    public static final String[] connect = new String[]{ null, "keep-alive", "close" };

    class Info extends MetaData.Request {
        Info(String method, String uri) {
            super(method, new HttpURI(uri), HTTP_1_1, new HttpFields(), (-1));
        }

        public Info(String method, String uri, int contentLength) {
            super(method, new HttpURI(uri), HTTP_1_1, new HttpFields(), contentLength);
        }
    }

    @Test
    public void testGETRequestNoContent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(2048);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("GET", "/index.html");
        getFields().add("Host", "something");
        getFields().add("User-Agent", "test");
        Assertions.assertTrue((!(gen.isChunking())));
        result = gen.generateRequest(info, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(out, Matchers.containsString("GET /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
    }

    @Test
    public void testEmptyHeaders() throws Exception {
        ByteBuffer header = BufferUtil.allocate(2048);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("GET", "/index.html");
        getFields().add("Host", "something");
        getFields().add("Null", null);
        getFields().add("Empty", "");
        Assertions.assertTrue((!(gen.isChunking())));
        result = gen.generateRequest(info, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(out, Matchers.containsString("GET /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
        MatcherAssert.assertThat(out, Matchers.containsString("Empty:"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Null:")));
    }

    @Test
    public void testPOSTRequestNoContent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(2048);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("POST", "/index.html");
        getFields().add("Host", "something");
        getFields().add("User-Agent", "test");
        Assertions.assertTrue((!(gen.isChunking())));
        result = gen.generateRequest(info, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(out, Matchers.containsString("POST /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 0"));
    }

    @Test
    public void testRequestWithContent() throws Exception {
        String out;
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World. The quick brown fox jumped over the lazy dog.");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, content0, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("POST", "/index.html");
        getFields().add("Host", "something");
        getFields().add("User-Agent", "test");
        result = gen.generateRequest(info, null, null, content0, true);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, content0, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        MatcherAssert.assertThat(out, Matchers.containsString("POST /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.containsString("Host: something"));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 58"));
        MatcherAssert.assertThat(out, Matchers.containsString("Hello World. The quick brown fox jumped over the lazy dog."));
        Assertions.assertEquals(58, gen.getContentPrepared());
    }

    @Test
    public void testRequestWithChunkedContent() throws Exception {
        String out;
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World. ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog.");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("POST", "/index.html");
        getFields().add("Host", "something");
        getFields().add("User-Agent", "test");
        result = gen.generateRequest(info, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        Assertions.assertTrue(gen.isChunking());
        out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateRequest(null, header, null, content1, false);
        Assertions.assertEquals(NEED_CHUNK, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        result = gen.generateRequest(null, null, chunk, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        Assertions.assertTrue(gen.isChunking());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue(gen.isChunking());
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        Assertions.assertTrue((!(gen.isChunking())));
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("POST /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.containsString("Host: something"));
        MatcherAssert.assertThat(out, Matchers.containsString("Transfer-Encoding: chunked"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\nD\r\nHello World. \r\n"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n2D\r\nThe quick brown fox jumped over the lazy dog.\r\n"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n0\r\n\r\n"));
        Assertions.assertEquals(58, gen.getContentPrepared());
    }

    @Test
    public void testRequestWithKnownContent() throws Exception {
        String out;
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World. ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog.");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateRequest(null, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        HttpGeneratorClientTest.Info info = new HttpGeneratorClientTest.Info("POST", "/index.html", 58);
        getFields().add("Host", "something");
        getFields().add("User-Agent", "test");
        result = gen.generateRequest(info, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateRequest(info, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateRequest(null, null, null, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        Assertions.assertTrue((!(gen.isChunking())));
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        MatcherAssert.assertThat(out, Matchers.containsString("POST /index.html HTTP/1.1"));
        MatcherAssert.assertThat(out, Matchers.containsString("Host: something"));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 58"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n\r\nHello World. The quick brown fox jumped over the lazy dog."));
        Assertions.assertEquals(58, gen.getContentPrepared());
    }
}

