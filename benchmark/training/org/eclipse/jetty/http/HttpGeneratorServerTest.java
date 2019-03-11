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


import DateGenerator.__01Jan1970;
import HttpGenerator.CHUNK_SIZE;
import HttpGenerator.CONTINUE_100_INFO;
import HttpGenerator.Result;
import HttpGenerator.Result.CONTINUE;
import HttpGenerator.Result.DONE;
import HttpGenerator.Result.FLUSH;
import HttpGenerator.Result.NEED_CHUNK;
import HttpGenerator.Result.NEED_CHUNK_TRAILER;
import HttpGenerator.Result.NEED_HEADER;
import HttpGenerator.Result.NEED_INFO;
import HttpGenerator.Result.SHUTDOWN_OUT;
import HttpGenerator.State.COMMITTED;
import HttpGenerator.State.COMPLETING;
import HttpGenerator.State.COMPLETING_1XX;
import HttpGenerator.State.END;
import HttpGenerator.State.START;
import HttpHeader.CONNECTION;
import HttpHeader.SERVER;
import HttpHeader.TRANSFER_ENCODING;
import HttpHeader.X_POWERED_BY;
import HttpHeaderValue.CHUNKED;
import HttpHeaderValue.KEEP_ALIVE;
import MetaData.Response;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static HttpVersion.HTTP_0_9;
import static HttpVersion.HTTP_1_0;
import static HttpVersion.HTTP_1_1;


public class HttpGeneratorServerTest {
    @Test
    public void test_0_9() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        ByteBuffer content = BufferUtil.toBuffer("0123456789");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_0_9, 200, null, new HttpFields(), 10);
        info.getFields().add("Content-Type", "test/data");
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String response = BufferUtil.toString(header);
        BufferUtil.clear(header);
        response += BufferUtil.toString(content);
        BufferUtil.clear(content);
        result = gen.generateResponse(null, false, null, null, content, false);
        Assertions.assertEquals(SHUTDOWN_OUT, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(10, gen.getContentPrepared());
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("200 OK")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 10")));
        MatcherAssert.assertThat(response, Matchers.containsString("0123456789"));
    }

    @Test
    public void testSimple() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        ByteBuffer content = BufferUtil.toBuffer("0123456789");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), 10);
        info.getFields().add("Content-Type", "test/data");
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content, true);
        Assertions.assertEquals(NEED_HEADER, result);
        result = gen.generateResponse(info, false, header, null, content, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String response = BufferUtil.toString(header);
        BufferUtil.clear(header);
        response += BufferUtil.toString(content);
        BufferUtil.clear(content);
        result = gen.generateResponse(null, false, null, null, content, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(10, gen.getContentPrepared());
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: 10"));
        MatcherAssert.assertThat(response, Matchers.containsString("\r\n0123456789"));
    }

    @Test
    public void test204() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        ByteBuffer content = BufferUtil.toBuffer("0123456789");
        HttpGenerator gen = new HttpGenerator();
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 204, "Foo", new HttpFields(), 10);
        info.getFields().add("Content-Type", "test/data");
        info.getFields().add("Last-Modified", __01Jan1970);
        HttpGenerator.Result result = gen.generateResponse(info, false, header, null, content, true);
        Assertions.assertEquals(gen.isNoContent(), true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String responseheaders = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, content, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(responseheaders, Matchers.containsString("HTTP/1.1 204 Foo"));
        MatcherAssert.assertThat(responseheaders, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(responseheaders, Matchers.not(Matchers.containsString("Content-Length: 10")));
        // Note: the HttpConnection.process() method is responsible for actually
        // excluding the content from the response based on generator.isNoContent()==true
    }

    @Test
    public void testComplexChars() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        ByteBuffer content = BufferUtil.toBuffer("0123456789");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, "??", new HttpFields(), 10);
        info.getFields().add("Content-Type", "test/data;\r\nextra=value");
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content, true);
        Assertions.assertEquals(NEED_HEADER, result);
        result = gen.generateResponse(info, false, header, null, content, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String response = BufferUtil.toString(header);
        BufferUtil.clear(header);
        response += BufferUtil.toString(content);
        BufferUtil.clear(content);
        result = gen.generateResponse(null, false, null, null, content, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(10, gen.getContentPrepared());
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200 ??"));
        MatcherAssert.assertThat(response, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: test/data;  extra=value"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: 10"));
        MatcherAssert.assertThat(response, Matchers.containsString("\r\n0123456789"));
    }

    @Test
    public void testSendServerXPoweredBy() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        HttpFields fields = new HttpFields();
        fields.add(SERVER, "SomeServer");
        fields.add(X_POWERED_BY, "SomePower");
        MetaData.Response infoF = new MetaData.Response(HTTP_1_1, 200, null, fields, (-1));
        String head;
        HttpGenerator gen = new HttpGenerator(true, true);
        gen.generateResponse(info, false, header, null, null, true);
        head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.containsString("Server: Jetty(9.x.x)"));
        MatcherAssert.assertThat(head, Matchers.containsString("X-Powered-By: Jetty(9.x.x)"));
        gen.reset();
        gen.generateResponse(infoF, false, header, null, null, true);
        head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.not(Matchers.containsString("Server: Jetty(9.x.x)")));
        MatcherAssert.assertThat(head, Matchers.containsString("Server: SomeServer"));
        MatcherAssert.assertThat(head, Matchers.containsString("X-Powered-By: Jetty(9.x.x)"));
        MatcherAssert.assertThat(head, Matchers.containsString("X-Powered-By: SomePower"));
        gen.reset();
        gen = new HttpGenerator(false, false);
        gen.generateResponse(info, false, header, null, null, true);
        head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.not(Matchers.containsString("Server: Jetty(9.x.x)")));
        MatcherAssert.assertThat(head, Matchers.not(Matchers.containsString("X-Powered-By: Jetty(9.x.x)")));
        gen.reset();
        gen.generateResponse(infoF, false, header, null, null, true);
        head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.not(Matchers.containsString("Server: Jetty(9.x.x)")));
        MatcherAssert.assertThat(head, Matchers.containsString("Server: SomeServer"));
        MatcherAssert.assertThat(head, Matchers.not(Matchers.containsString("X-Powered-By: Jetty(9.x.x)")));
        MatcherAssert.assertThat(head, Matchers.containsString("X-Powered-By: SomePower"));
        gen.reset();
    }

    @Test
    public void testResponseIncorrectContentLength() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), 10);
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add("Content-Length", "11");
        result = gen.generateResponse(info, false, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        BadMessageException e = Assertions.assertThrows(BadMessageException.class, () -> {
            gen.generateResponse(info, false, header, null, null, true);
        });
        Assertions.assertEquals(500, e._code);
    }

    @Test
    public void testResponseNoContentPersistent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), 0);
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        result = gen.generateResponse(info, false, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(head, Matchers.containsString("Content-Length: 0"));
    }

    @Test
    public void testResponseKnownNoContentNotPersistent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), 0);
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add("Connection", "close");
        result = gen.generateResponse(info, false, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        result = gen.generateResponse(info, false, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(SHUTDOWN_OUT, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(head, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(head, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(head, Matchers.containsString("Connection: close"));
    }

    @Test
    public void testResponseUpgrade() throws Exception {
        ByteBuffer header = BufferUtil.allocate(8096);
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 101, null, new HttpFields(), (-1));
        info.getFields().add("Upgrade", "WebSocket");
        info.getFields().add("Connection", "Upgrade");
        info.getFields().add("Sec-WebSocket-Accept", "123456789==");
        result = gen.generateResponse(info, false, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String head = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(info, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        Assertions.assertEquals(0, gen.getContentPrepared());
        MatcherAssert.assertThat(head, Matchers.startsWith("HTTP/1.1 101 Switching Protocols"));
        MatcherAssert.assertThat(head, Matchers.containsString("Upgrade: WebSocket\r\n"));
        MatcherAssert.assertThat(head, Matchers.containsString("Connection: Upgrade\r\n"));
    }

    @Test
    public void testResponseWithChunkedContent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, chunk, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
        MatcherAssert.assertThat(out, Matchers.containsString("Transfer-Encoding: chunked"));
        MatcherAssert.assertThat(out, Matchers.endsWith(("\r\n\r\nD\r\n" + (((("Hello World! \r\n" + "2E\r\n") + "The quick brown fox jumped over the lazy dog. \r\n") + "0\r\n") + "\r\n"))));
    }

    @Test
    public void testResponseWithHintedChunkedContent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        gen.setPersistent(false);
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add(TRANSFER_ENCODING, CHUNKED);
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, content1, false);
        Assertions.assertEquals(NEED_CHUNK, result);
        result = gen.generateResponse(null, false, null, chunk, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(SHUTDOWN_OUT, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
        MatcherAssert.assertThat(out, Matchers.containsString("Transfer-Encoding: chunked"));
        MatcherAssert.assertThat(out, Matchers.endsWith(("\r\n\r\nD\r\n" + (((("Hello World! \r\n" + "2E\r\n") + "The quick brown fox jumped over the lazy dog. \r\n") + "0\r\n") + "\r\n"))));
    }

    @Test
    public void testResponseWithContentAndTrailer() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer trailer = BufferUtil.allocate(4096);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        gen.setPersistent(false);
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add(TRANSFER_ENCODING, CHUNKED);
        info.setTrailerSupplier(new Supplier<HttpFields>() {
            @Override
            public HttpFields get() {
                HttpFields trailer = new HttpFields();
                trailer.add("T-Name0", "T-ValueA");
                trailer.add("T-Name0", "T-ValueB");
                trailer.add("T-Name1", "T-ValueC");
                return trailer;
            }
        });
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, content1, false);
        Assertions.assertEquals(NEED_CHUNK, result);
        result = gen.generateResponse(null, false, null, chunk, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(chunk);
        BufferUtil.clear(chunk);
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(NEED_CHUNK_TRAILER, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, trailer, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        out += BufferUtil.toString(trailer);
        BufferUtil.clear(trailer);
        result = gen.generateResponse(null, false, null, trailer, null, true);
        Assertions.assertEquals(SHUTDOWN_OUT, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
        MatcherAssert.assertThat(out, Matchers.containsString("Transfer-Encoding: chunked"));
        MatcherAssert.assertThat(out, Matchers.endsWith(("\r\n\r\nD\r\n" + ((((((("Hello World! \r\n" + "2E\r\n") + "The quick brown fox jumped over the lazy dog. \r\n") + "0\r\n") + "T-Name0: T-ValueA\r\n") + "T-Name0: T-ValueB\r\n") + "T-Name1: T-ValueC\r\n") + "\r\n"))));
    }

    @Test
    public void testResponseWithTrailer() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer chunk = BufferUtil.allocate(CHUNK_SIZE);
        ByteBuffer trailer = BufferUtil.allocate(4096);
        HttpGenerator gen = new HttpGenerator();
        gen.setPersistent(false);
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add(TRANSFER_ENCODING, CHUNKED);
        info.setTrailerSupplier(new Supplier<HttpFields>() {
            @Override
            public HttpFields get() {
                HttpFields trailer = new HttpFields();
                trailer.add("T-Name0", "T-ValueA");
                trailer.add("T-Name0", "T-ValueB");
                trailer.add("T-Name1", "T-ValueC");
                return trailer;
            }
        });
        result = gen.generateResponse(info, false, null, null, null, true);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(NEED_CHUNK_TRAILER, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, chunk, null, true);
        Assertions.assertEquals(NEED_CHUNK_TRAILER, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, trailer, null, true);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        out += BufferUtil.toString(trailer);
        BufferUtil.clear(trailer);
        result = gen.generateResponse(null, false, null, trailer, null, true);
        Assertions.assertEquals(SHUTDOWN_OUT, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("Content-Length")));
        MatcherAssert.assertThat(out, Matchers.containsString("Transfer-Encoding: chunked"));
        MatcherAssert.assertThat(out, Matchers.endsWith(("\r\n\r\n" + (((("0\r\n" + "T-Name0: T-ValueA\r\n") + "T-Name0: T-ValueB\r\n") + "T-Name1: T-ValueC\r\n") + "\r\n"))));
    }

    @Test
    public void testResponseWithKnownContentLengthFromMetaData() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), 59);
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("chunked")));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 59"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n\r\nHello World! The quick brown fox jumped over the lazy dog. "));
    }

    @Test
    public void testResponseWithKnownContentLengthFromHeader() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), (-1));
        info.getFields().add("Last-Modified", __01Jan1970);
        info.getFields().add("Content-Length", ("" + ((content0.remaining()) + (content1.remaining()))));
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        String out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("chunked")));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 59"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n\r\nHello World! The quick brown fox jumped over the lazy dog. "));
    }

    @Test
    public void test100ThenResponseWithContent() throws Exception {
        ByteBuffer header = BufferUtil.allocate(4096);
        ByteBuffer content0 = BufferUtil.toBuffer("Hello World! ");
        ByteBuffer content1 = BufferUtil.toBuffer("The quick brown fox jumped over the lazy dog. ");
        HttpGenerator gen = new HttpGenerator();
        HttpGenerator.Result result = gen.generateResponse(CONTINUE_100_INFO, false, null, null, null, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(CONTINUE_100_INFO, false, header, null, null, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMPLETING_1XX, gen.getState());
        String out = BufferUtil.toString(header);
        result = gen.generateResponse(null, false, null, null, null, false);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(START, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 100 Continue"));
        result = gen.generateResponse(null, false, null, null, content0, false);
        Assertions.assertEquals(NEED_INFO, result);
        Assertions.assertEquals(START, gen.getState());
        MetaData.Response info = new MetaData.Response(HTTP_1_1, 200, null, new HttpFields(), ((BufferUtil.length(content0)) + (BufferUtil.length(content1))));
        info.getFields().add("Last-Modified", __01Jan1970);
        result = gen.generateResponse(info, false, null, null, content0, false);
        Assertions.assertEquals(NEED_HEADER, result);
        Assertions.assertEquals(START, gen.getState());
        result = gen.generateResponse(info, false, header, null, content0, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out = BufferUtil.toString(header);
        BufferUtil.clear(header);
        out += BufferUtil.toString(content0);
        BufferUtil.clear(content0);
        result = gen.generateResponse(null, false, null, null, content1, false);
        Assertions.assertEquals(FLUSH, result);
        Assertions.assertEquals(COMMITTED, gen.getState());
        out += BufferUtil.toString(content1);
        BufferUtil.clear(content1);
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(CONTINUE, result);
        Assertions.assertEquals(COMPLETING, gen.getState());
        result = gen.generateResponse(null, false, null, null, null, true);
        Assertions.assertEquals(DONE, result);
        Assertions.assertEquals(END, gen.getState());
        MatcherAssert.assertThat(out, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(out, Matchers.containsString("Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT"));
        MatcherAssert.assertThat(out, Matchers.not(Matchers.containsString("chunked")));
        MatcherAssert.assertThat(out, Matchers.containsString("Content-Length: 59"));
        MatcherAssert.assertThat(out, Matchers.containsString("\r\n\r\nHello World! The quick brown fox jumped over the lazy dog. "));
    }

    @Test
    public void testConnectionKeepAliveWithAdditionalCustomValue() throws Exception {
        HttpGenerator generator = new HttpGenerator();
        HttpFields fields = new HttpFields();
        fields.put(CONNECTION, KEEP_ALIVE);
        String customValue = "test";
        fields.add(CONNECTION, customValue);
        MetaData.Response info = new MetaData.Response(HTTP_1_0, 200, "OK", fields, (-1));
        ByteBuffer header = BufferUtil.allocate(4096);
        HttpGenerator.Result result = generator.generateResponse(info, false, header, null, null, true);
        Assertions.assertSame(FLUSH, result);
        String headers = BufferUtil.toString(header);
        MatcherAssert.assertThat(headers, Matchers.containsString(KEEP_ALIVE.asString()));
        MatcherAssert.assertThat(headers, Matchers.containsString(customValue));
    }
}

