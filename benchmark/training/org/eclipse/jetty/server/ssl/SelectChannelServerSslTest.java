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
package org.eclipse.jetty.server.ssl;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpServerTestBase;
import org.eclipse.jetty.server.HttpServerTestFixture;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * HttpServer Tester.
 */
public class SelectChannelServerSslTest extends HttpServerTestBase {
    private SSLContext _sslContext;

    public SelectChannelServerSslTest() {
        _scheme = "https";
    }

    @Test
    public void testRequest2FixedFragments() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        byte[] bytes = HttpServerTestBase.REQUEST2.getBytes();
        int[] points = new int[]{ 74, 325 };
        // Sort the list
        Arrays.sort(points);
        URI uri = _server.getURI();
        Socket client = newSocket(uri.getHost(), uri.getPort());
        try {
            OutputStream os = client.getOutputStream();
            int last = 0;
            // Write out the fragments
            for (int j = 0; j < (points.length); ++j) {
                int point = points[j];
                os.write(bytes, last, (point - last));
                last = point;
                os.flush();
                Thread.sleep(HttpServerTestFixture.PAUSE);
            }
            // Write the last fragment
            os.write(bytes, last, ((bytes.length) - last));
            os.flush();
            Thread.sleep(HttpServerTestFixture.PAUSE);
            // Read the response
            String response = HttpServerTestBase.readResponse(client);
            // Check the response
            Assertions.assertEquals(HttpServerTestBase.RESPONSE2, response);
        } finally {
            client.close();
        }
    }

    @Test
    public void testSecureRequestCustomizer() throws Exception {
        configureServer(new SelectChannelServerSslTest.SecureRequestHandler());
        try (Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort())) {
            OutputStream os = client.getOutputStream();
            os.write("GET / HTTP/1.0\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1));
            os.flush();
            // Read the response.
            String response = HttpServerTestBase.readResponse(client);
            MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString("Hello world"));
            MatcherAssert.assertThat(response, Matchers.containsString("scheme='https'"));
            MatcherAssert.assertThat(response, Matchers.containsString("isSecure='true'"));
            MatcherAssert.assertThat(response, Matchers.containsString("X509Certificate='null'"));
            Matcher matcher = Pattern.compile("cipher_suite='([^']*)'").matcher(response);
            matcher.find();
            MatcherAssert.assertThat(matcher.group(1), Matchers.allOf(Matchers.not(Matchers.isEmptyOrNullString()), Matchers.not(Matchers.is("null"))));
            matcher = Pattern.compile("key_size='([^']*)'").matcher(response);
            matcher.find();
            MatcherAssert.assertThat(matcher.group(1), Matchers.allOf(Matchers.not(Matchers.isEmptyOrNullString()), Matchers.not(Matchers.is("null"))));
            matcher = Pattern.compile("ssl_session_id='([^']*)'").matcher(response);
            matcher.find();
            MatcherAssert.assertThat(matcher.group(1), Matchers.allOf(Matchers.not(Matchers.isEmptyOrNullString()), Matchers.not(Matchers.is("null"))));
            matcher = Pattern.compile("ssl_session='([^']*)'").matcher(response);
            matcher.find();
            MatcherAssert.assertThat(matcher.group(1), Matchers.allOf(Matchers.not(Matchers.isEmptyOrNullString()), Matchers.not(Matchers.is("null"))));
        }
    }

    public static class SecureRequestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            response.getOutputStream().println("Hello world");
            response.getOutputStream().println((("scheme='" + (request.getScheme())) + "'"));
            response.getOutputStream().println((("isSecure='" + (request.isSecure())) + "'"));
            response.getOutputStream().println((("X509Certificate='" + (request.getAttribute("javax.servlet.request.X509Certificate"))) + "'"));
            response.getOutputStream().println((("cipher_suite='" + (request.getAttribute("javax.servlet.request.cipher_suite"))) + "'"));
            response.getOutputStream().println((("key_size='" + (request.getAttribute("javax.servlet.request.key_size"))) + "'"));
            response.getOutputStream().println((("ssl_session_id='" + (request.getAttribute("javax.servlet.request.ssl_session_id"))) + "'"));
            SSLSession sslSession = ((SSLSession) (request.getAttribute("SSL_SESSION")));
            response.getOutputStream().println((("ssl_session='" + sslSession) + "'"));
        }
    }
}

