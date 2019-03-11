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


import HttpStatus.OK_200;
import HttpTester.Response;
import HttpVersion.HTTP_1_0;
import HttpVersion.HTTP_1_1;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpVersionCustomizerTest {
    @Test
    public void testCustomizeHttpVersion() throws Exception {
        Server server = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.addCustomizer(( connector, config, request) -> request.setHttpVersion(HttpVersion.HTTP_1_1));
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        server.addConnector(connector);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.setStatus(500);
                Assertions.assertEquals(HTTP_1_1.asString(), request.getProtocol());
                response.setStatus(200);
                response.getWriter().println("OK");
            }
        });
        server.start();
        try {
            try (SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", connector.getLocalPort()))) {
                HttpTester.Request request = HttpTester.newRequest();
                request.setVersion(HTTP_1_0);
                socket.write(request.generate());
                HttpTester.Response response = HttpTester.parseResponse(HttpTester.from(socket));
                Assertions.assertNotNull(response);
                MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(OK_200));
            }
        } finally {
            server.stop();
        }
    }
}

