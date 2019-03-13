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
package org.eclipse.jetty.server.handler;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Resource Handler test
 */
public class BufferedResponseHandlerTest {
    private static Server _server;

    private static HttpConfiguration _config;

    private static LocalConnector _local;

    private static ContextHandler _contextHandler;

    private static BufferedResponseHandler _bufferedHandler;

    private static BufferedResponseHandlerTest.TestHandler _test;

    @Test
    public void testNormal() throws Exception {
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 7"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: ")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 8")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 9")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Written: true")));
    }

    @Test
    public void testIncluded() throws Exception {
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testExcludedByPath() throws Exception {
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path.exclude HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 7"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: ")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 8")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 9")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Written: true")));
    }

    @Test
    public void testExcludedByMime() throws Exception {
        BufferedResponseHandlerTest._test._mimeType = "text/excluded";
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 7"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: ")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 8")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 9")));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Written: true")));
    }

    @Test
    public void testFlushed() throws Exception {
        BufferedResponseHandlerTest._test._flush = true;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testClosed() throws Exception {
        BufferedResponseHandlerTest._test._close = true;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Written: true")));
    }

    @Test
    public void testBufferSizeSmall() throws Exception {
        BufferedResponseHandlerTest._test._bufferSize = 16;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testBufferSizeBig() throws Exception {
        BufferedResponseHandlerTest._test._bufferSize = 4096;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: "));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testOne() throws Exception {
        BufferedResponseHandlerTest._test._writes = 1;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: "));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 1")));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testFlushEmpty() throws Exception {
        BufferedResponseHandlerTest._test._writes = 1;
        BufferedResponseHandlerTest._test._flush = true;
        BufferedResponseHandlerTest._test._close = false;
        BufferedResponseHandlerTest._test._content = new byte[0];
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: "));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Write: 1")));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
    }

    @Test
    public void testReset() throws Exception {
        BufferedResponseHandlerTest._test._reset = true;
        String response = BufferedResponseHandlerTest._local.getResponse("GET /ctx/include/path HTTP/1.1\r\nHost: localhost\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 0"));
        MatcherAssert.assertThat(response, Matchers.containsString("Write: 9"));
        MatcherAssert.assertThat(response, Matchers.containsString("Written: true"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("RESET")));
    }

    public static class TestHandler extends AbstractHandler {
        int _bufferSize;

        String _mimeType;

        byte[] _content;

        int _writes;

        boolean _flush;

        boolean _close;

        boolean _reset;

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ((_bufferSize) > 0)
                response.setBufferSize(_bufferSize);

            if ((_mimeType) != null)
                response.setContentType(_mimeType);

            if (_reset) {
                response.getOutputStream().print("THIS WILL BE RESET");
                response.getOutputStream().flush();
                response.getOutputStream().print("THIS WILL BE RESET");
                response.resetBuffer();
            }
            for (int i = 0; i < (_writes); i++) {
                response.addHeader("Write", Integer.toString(i));
                response.getOutputStream().write(_content);
                if (_flush)
                    response.getOutputStream().flush();

            }
            if (_close)
                response.getOutputStream().close();

            response.addHeader("Written", "true");
        }
    }
}

