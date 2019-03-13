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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SlowClientWithPipelinedRequestTest {
    private final AtomicInteger handles = new AtomicInteger();

    private Server server;

    private ServerConnector connector;

    @Test
    public void testSlowClientWithPipelinedRequest() throws Exception {
        final int contentLength = 512 * 1024;
        startServer(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                if ("/content".equals(target)) {
                    // We simulate what the DefaultServlet does, bypassing the blocking
                    // write mechanism otherwise the test does not reproduce the bug
                    OutputStream outputStream = response.getOutputStream();
                    HttpOutput output = ((HttpOutput) (outputStream));
                    // Since the test is via localhost, we need a really big buffer to stall the write
                    byte[] bytes = new byte[contentLength];
                    Arrays.fill(bytes, ((byte) ('9')));
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    // Do a non blocking write
                    output.sendContent(buffer);
                }
            }
        });
        Socket client = new Socket("localhost", connector.getLocalPort());
        OutputStream output = client.getOutputStream();
        output.write(((((("" + ("GET /content HTTP/1.1\r\n" + "Host: localhost:")) + (connector.getLocalPort())) + "\r\n") + "\r\n") + "").getBytes(StandardCharsets.UTF_8));
        output.flush();
        InputStream input = client.getInputStream();
        int read = input.read();
        Assertions.assertTrue((read >= 0));
        // As soon as we can read the response, send a pipelined request
        // so it is a different read for the server and it will trigger NIO
        output.write(((((("" + ("GET /pipelined HTTP/1.1\r\n" + "Host: localhost:")) + (connector.getLocalPort())) + "\r\n") + "\r\n") + "").getBytes(StandardCharsets.UTF_8));
        output.flush();
        // Simulate a slow reader
        Thread.sleep(1000);
        MatcherAssert.assertThat(handles.get(), Matchers.lessThan(10));
        // We are sure we are not spinning, read the content
        StringBuilder lines = new StringBuilder().append(((char) (read)));
        int crlfs = 0;
        while (true) {
            read = input.read();
            lines.append(((char) (read)));
            if ((read == '\r') || (read == '\n'))
                ++crlfs;
            else
                crlfs = 0;

            if (crlfs == 4)
                break;

        } 
        MatcherAssert.assertThat(lines.toString(), Matchers.containsString(" 200 "));
        // Read the body
        for (int i = 0; i < contentLength; ++i)
            input.read();

        // Read the pipelined response
        lines.setLength(0);
        crlfs = 0;
        while (true) {
            read = input.read();
            lines.append(((char) (read)));
            if ((read == '\r') || (read == '\n'))
                ++crlfs;
            else
                crlfs = 0;

            if (crlfs == 4)
                break;

        } 
        MatcherAssert.assertThat(lines.toString(), Matchers.containsString(" 200 "));
        client.close();
    }
}

