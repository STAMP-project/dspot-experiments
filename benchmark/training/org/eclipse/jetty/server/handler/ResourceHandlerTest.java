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


import HttpStatus.PRECONDITION_FAILED_412;
import HttpTester.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;


/**
 * Resource Handler test
 *
 * TODO: increase the testing going on here
 */
public class ResourceHandlerTest {
    private static String LN = System.getProperty("line.separator");

    private static Server _server;

    private static HttpConfiguration _config;

    private static ServerConnector _connector;

    private static LocalConnector _local;

    private static ContextHandler _contextHandler;

    private static ResourceHandler _resourceHandler;

    @Test
    public void testJettyDirRedirect() throws Exception {
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(302));
        MatcherAssert.assertThat(response.get(HttpHeader.LOCATION), Matchers.containsString("/resource/"));
    }

    @Test
    public void testJettyDirListing() throws Exception {
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/ HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("jetty-dir.css"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<H1>Directory: /resource/"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("big.txt"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("bigger.txt"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("directory"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("simple.txt"));
    }

    @Test
    public void testHeaders() throws Exception {
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/simple.txt HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.get(HttpHeader.CONTENT_TYPE), Matchers.equalTo("text/plain"));
        MatcherAssert.assertThat(response.get(HttpHeader.LAST_MODIFIED), Matchers.notNullValue());
        MatcherAssert.assertThat(response.get(HttpHeader.CONTENT_LENGTH), Matchers.equalTo("11"));
        MatcherAssert.assertThat(response.get(HttpHeader.SERVER), Matchers.containsString("Jetty"));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("simple text"));
    }

    @Test
    public void testIfModifiedSince() throws Exception {
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/simple.txt HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.get(HttpHeader.LAST_MODIFIED), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("simple text"));
        String last_modified = response.get(HttpHeader.LAST_MODIFIED);
        response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse((((("GET /resource/simple.txt HTTP/1.0\r\n" + "If-Modified-Since: ") + last_modified) + "\r\n") + "\r\n")));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(304));
    }

    @Test
    public void testBigFile() throws Exception {
        ResourceHandlerTest._config.setOutputBufferSize(2048);
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/big.txt HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.getContent(), Matchers.startsWith("     1\tThis is a big file"));
        MatcherAssert.assertThat(response.getContent(), Matchers.endsWith(("   400\tThis is a big file" + (ResourceHandlerTest.LN))));
    }

    @Test
    public void testBigFileBigBuffer() throws Exception {
        ResourceHandlerTest._config.setOutputBufferSize((16 * 1024));
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/big.txt HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.getContent(), Matchers.startsWith("     1\tThis is a big file"));
        MatcherAssert.assertThat(response.getContent(), Matchers.endsWith(("   400\tThis is a big file" + (ResourceHandlerTest.LN))));
    }

    @Test
    public void testBigFileLittleBuffer() throws Exception {
        ResourceHandlerTest._config.setOutputBufferSize(8);
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/big.txt HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.getContent(), Matchers.startsWith("     1\tThis is a big file"));
        MatcherAssert.assertThat(response.getContent(), Matchers.endsWith(("   400\tThis is a big file" + (ResourceHandlerTest.LN))));
    }

    @Test
    public void testBigger() throws Exception {
        try (Socket socket = new Socket("localhost", ResourceHandlerTest._connector.getLocalPort())) {
            socket.getOutputStream().write("GET /resource/bigger.txt HTTP/1.0\n\n".getBytes());
            Thread.sleep(1000);
            String response = IO.toString(socket.getInputStream());
            MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString((("   400\tThis is a big file" + (ResourceHandlerTest.LN)) + "     1\tThis is a big file")));
            MatcherAssert.assertThat(response, Matchers.endsWith(("   400\tThis is a big file" + (ResourceHandlerTest.LN))));
        }
    }

    @Test
    public void testWelcome() throws Exception {
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/directory/ HTTP/1.0\r\n\r\n"));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Hello"));
    }

    @Test
    public void testWelcomeRedirect() throws Exception {
        try {
            ResourceHandlerTest._resourceHandler.setRedirectWelcome(true);
            HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse("GET /resource/directory/ HTTP/1.0\r\n\r\n"));
            MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(302));
            MatcherAssert.assertThat(response.get(HttpHeader.LOCATION), Matchers.containsString("/resource/directory/welcome.txt"));
        } finally {
            ResourceHandlerTest._resourceHandler.setRedirectWelcome(false);
        }
    }

    // TODO: SLOW, needs review
    @Test
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testSlowBiggest() throws Exception {
        ResourceHandlerTest._connector.setIdleTimeout(9000);
        File dir = MavenTestingUtils.getTargetFile("test-classes/simple");
        File biggest = new File(dir, "biggest.txt");
        try (OutputStream out = new FileOutputStream(biggest)) {
            for (int i = 0; i < 10; i++) {
                try (InputStream in = new FileInputStream(new File(dir, "bigger.txt"))) {
                    IO.copy(in, out);
                }
            }
            out.write("\nTHE END\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        biggest.deleteOnExit();
        try (Socket socket = new Socket("localhost", ResourceHandlerTest._connector.getLocalPort());OutputStream out = socket.getOutputStream();InputStream in = socket.getInputStream()) {
            socket.getOutputStream().write("GET /resource/biggest.txt HTTP/1.0\n\n".getBytes());
            byte[] array = new byte[102400];
            ByteBuffer buffer = null;
            while (true) {
                Thread.sleep(25);
                int len = in.read(array);
                if (len < 0)
                    break;

                buffer = BufferUtil.toBuffer(array, 0, len);
                // System.err.println(++i+": "+BufferUtil.toDetailString(buffer));
            } 
            Assertions.assertEquals('E', buffer.get(((buffer.limit()) - 4)));
            Assertions.assertEquals('N', buffer.get(((buffer.limit()) - 3)));
            Assertions.assertEquals('D', buffer.get(((buffer.limit()) - 2)));
        }
    }

    @Test
    public void testConditionalGetResponseCommitted() throws Exception {
        ResourceHandlerTest._config.setOutputBufferSize(8);
        ResourceHandlerTest._resourceHandler.setEtags(true);
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse(("GET /resource/big.txt HTTP/1.0\r\n" + ("If-Match: \"NO_MATCH\"\r\n" + "\r\n"))));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(PRECONDITION_FAILED_412));
    }

    @Test
    public void testConditionalHeadResponseCommitted() throws Exception {
        ResourceHandlerTest._config.setOutputBufferSize(8);
        ResourceHandlerTest._resourceHandler.setEtags(true);
        HttpTester.Response response = HttpTester.parseResponse(ResourceHandlerTest._local.getResponse(("HEAD /resource/big.txt HTTP/1.0\r\n" + ("If-Match: \"NO_MATCH\"\r\n" + "\r\n"))));
        MatcherAssert.assertThat(response.getStatus(), Matchers.equalTo(PRECONDITION_FAILED_412));
    }
}

