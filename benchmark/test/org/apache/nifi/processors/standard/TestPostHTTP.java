/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CoreAttributes.MIME_TYPE;
import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import TestServer.NEED_CLIENT_AUTH;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.processors.standard.PostHTTP.CHUNKED_ENCODING;
import org.apache.nifi.processors.standard.PostHTTP.COMPRESSION_LEVEL;
import org.apache.nifi.processors.standard.PostHTTP.CONTENT_ENCODING_GZIP_VALUE;
import org.apache.nifi.processors.standard.PostHTTP.CONTENT_ENCODING_HEADER;
import org.apache.nifi.processors.standard.PostHTTP.CONTENT_TYPE;
import org.apache.nifi.processors.standard.PostHTTP.CONTENT_TYPE_HEADER;
import org.apache.nifi.processors.standard.PostHTTP.DEFAULT_CONTENT_TYPE;
import org.apache.nifi.processors.standard.PostHTTP.MAX_BATCH_SIZE;
import org.apache.nifi.processors.standard.PostHTTP.MAX_DATA_RATE;
import org.apache.nifi.processors.standard.PostHTTP.REL_FAILURE;
import org.apache.nifi.processors.standard.PostHTTP.REL_SUCCESS;
import org.apache.nifi.processors.standard.PostHTTP.SEND_AS_FLOWFILE;
import org.apache.nifi.processors.standard.PostHTTP.SSL_CONTEXT_SERVICE;
import org.apache.nifi.processors.standard.PostHTTP.URL;
import org.apache.nifi.processors.standard.PostHTTP.USER_AGENT;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.ssl.StandardSSLContextService;
import org.apache.nifi.util.FlowFileUnpackagerV3;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.web.util.TestServer;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TestPostHTTP {
    private TestServer server;

    private TestRunner runner;

    private CaptureServlet servlet;

    private final String KEYSTORE_PATH = "src/test/resources/keystore.jks";

    private final String KEYSTORE_AND_TRUSTSTORE_PASSWORD = "passwordpassword";

    private final String TRUSTSTORE_PATH = "src/test/resources/truststore.jks";

    private final String JKS_TYPE = "JKS";

    @Test
    public void testTruststoreSSLOnly() throws Exception {
        final Map<String, String> sslProps = new HashMap<>();
        sslProps.put(NEED_CLIENT_AUTH, "false");
        sslProps.put(KEYSTORE.getName(), KEYSTORE_PATH);
        sslProps.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
        setup(sslProps);
        final SSLContextService sslContextService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, TRUSTSTORE_PATH);
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, JKS_TYPE);
        runner.enableControllerService(sslContextService);
        runner.setProperty(URL, server.getSecureUrl());
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(CHUNKED_ENCODING, "false");
        runner.enqueue("Hello world".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testTwoWaySSL() throws Exception {
        final Map<String, String> sslProps = new HashMap<>();
        sslProps.put(KEYSTORE.getName(), KEYSTORE_PATH);
        sslProps.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        sslProps.put(TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(NEED_CLIENT_AUTH, "true");
        setup(sslProps);
        final SSLContextService sslContextService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, TRUSTSTORE_PATH);
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, JKS_TYPE);
        runner.setProperty(sslContextService, KEYSTORE, KEYSTORE_PATH);
        runner.setProperty(sslContextService, KEYSTORE_PASSWORD, KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(sslContextService, KEYSTORE_TYPE, JKS_TYPE);
        runner.enableControllerService(sslContextService);
        runner.setProperty(URL, server.getSecureUrl());
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(CHUNKED_ENCODING, "false");
        runner.enqueue("Hello world".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testOneWaySSLWhenServerConfiguredForTwoWay() throws Exception {
        final Map<String, String> sslProps = new HashMap<>();
        sslProps.put(KEYSTORE.getName(), KEYSTORE_PATH);
        sslProps.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        sslProps.put(TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(NEED_CLIENT_AUTH, "true");
        setup(sslProps);
        final SSLContextService sslContextService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, "src/test/resources/truststore.jks");
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, "passwordpassword");
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, JKS_TYPE);
        runner.enableControllerService(sslContextService);
        runner.setProperty(URL, server.getSecureUrl());
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(CHUNKED_ENCODING, "false");
        runner.enqueue("Hello world".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testSendAsFlowFile() throws Exception {
        setup(null);
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(SEND_AS_FLOWFILE, "true");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("abc", "cba");
        runner.enqueue("Hello".getBytes(), attrs);
        attrs.put("abc", "abc");
        attrs.put("filename", "xyz.txt");
        runner.enqueue("World".getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final byte[] lastPost = servlet.getLastPost();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteArrayInputStream bais = new ByteArrayInputStream(lastPost);
        FlowFileUnpackagerV3 unpacker = new FlowFileUnpackagerV3();
        // unpack first flowfile received
        Map<String, String> receivedAttrs = unpacker.unpackageFlowFile(bais, baos);
        byte[] contentReceived = baos.toByteArray();
        Assert.assertEquals("Hello", new String(contentReceived));
        Assert.assertEquals("cba", receivedAttrs.get("abc"));
        Assert.assertTrue(unpacker.hasMoreData());
        baos.reset();
        receivedAttrs = unpacker.unpackageFlowFile(bais, baos);
        contentReceived = baos.toByteArray();
        Assert.assertEquals("World", new String(contentReceived));
        Assert.assertEquals("abc", receivedAttrs.get("abc"));
        Assert.assertEquals("xyz.txt", receivedAttrs.get("filename"));
        Assert.assertNull(receivedAttrs.get("Content-Length"));
    }

    @Test
    public void testSendAsFlowFileSecure() throws Exception {
        final Map<String, String> sslProps = new HashMap<>();
        sslProps.put(KEYSTORE.getName(), KEYSTORE_PATH);
        sslProps.put(KEYSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(KEYSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(TRUSTSTORE.getName(), TRUSTSTORE_PATH);
        sslProps.put(TRUSTSTORE_PASSWORD.getName(), KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        sslProps.put(TRUSTSTORE_TYPE.getName(), JKS_TYPE);
        sslProps.put(NEED_CLIENT_AUTH, "true");
        setup(sslProps);
        final SSLContextService sslContextService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.setProperty(sslContextService, TRUSTSTORE, TRUSTSTORE_PATH);
        runner.setProperty(sslContextService, TRUSTSTORE_PASSWORD, KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(sslContextService, TRUSTSTORE_TYPE, JKS_TYPE);
        runner.setProperty(sslContextService, KEYSTORE, KEYSTORE_PATH);
        runner.setProperty(sslContextService, KEYSTORE_PASSWORD, KEYSTORE_AND_TRUSTSTORE_PASSWORD);
        runner.setProperty(sslContextService, KEYSTORE_TYPE, JKS_TYPE);
        runner.enableControllerService(sslContextService);
        runner.setProperty(URL, server.getSecureUrl());
        runner.setProperty(SEND_AS_FLOWFILE, "true");
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("abc", "cba");
        runner.enqueue("Hello".getBytes(), attrs);
        attrs.put("abc", "abc");
        attrs.put("filename", "xyz.txt");
        runner.enqueue("World".getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final byte[] lastPost = servlet.getLastPost();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteArrayInputStream bais = new ByteArrayInputStream(lastPost);
        FlowFileUnpackagerV3 unpacker = new FlowFileUnpackagerV3();
        // unpack first flowfile received
        Map<String, String> receivedAttrs = unpacker.unpackageFlowFile(bais, baos);
        byte[] contentReceived = baos.toByteArray();
        Assert.assertEquals("Hello", new String(contentReceived));
        Assert.assertEquals("cba", receivedAttrs.get("abc"));
        Assert.assertTrue(unpacker.hasMoreData());
        baos.reset();
        receivedAttrs = unpacker.unpackageFlowFile(bais, baos);
        contentReceived = baos.toByteArray();
        Assert.assertEquals("World", new String(contentReceived));
        Assert.assertEquals("abc", receivedAttrs.get("abc"));
        Assert.assertEquals("xyz.txt", receivedAttrs.get("filename"));
    }

    @Test
    public void testSendWithMimeType() throws Exception {
        setup(null);
        runner.setProperty(URL, server.getUrl());
        final Map<String, String> attrs = new HashMap<>();
        final String suppliedMimeType = "text/plain";
        attrs.put(MIME_TYPE.key(), suppliedMimeType);
        runner.enqueue("Camping is great!".getBytes(), attrs);
        runner.setProperty(CHUNKED_ENCODING, "false");
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        Assert.assertEquals("17", lastPostHeaders.get("Content-Length"));
    }

    @Test
    public void testSendWithEmptyELExpression() throws Exception {
        setup(null);
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CHUNKED_ENCODING, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "");
        runner.enqueue("The wilderness.".getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(DEFAULT_CONTENT_TYPE, lastPostHeaders.get(CONTENT_TYPE_HEADER));
    }

    @Test
    public void testSendWithContentTypeProperty() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(CHUNKED_ENCODING, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue("Sending with content type property.".getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
    }

    @Test
    public void testSendWithCompressionServerAcceptGzip() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(COMPRESSION_LEVEL, "9");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/plain");
        runner.enqueue(StringUtils.repeat("Lines of sample text.", 100).getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        // Ensure that a 'Content-Encoding' header was set with a 'gzip' value
        Assert.assertEquals(CONTENT_ENCODING_GZIP_VALUE, lastPostHeaders.get(CONTENT_ENCODING_HEADER));
        Assert.assertNull(lastPostHeaders.get("Content-Length"));
    }

    @Test
    public void testSendWithoutCompressionServerAcceptGzip() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(COMPRESSION_LEVEL, "0");
        runner.setProperty(CHUNKED_ENCODING, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/plain");
        runner.enqueue(StringUtils.repeat("Lines of sample text.", 100).getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        // Ensure that the request was not sent with a 'Content-Encoding' header
        Assert.assertNull(lastPostHeaders.get(CONTENT_ENCODING_HEADER));
        Assert.assertEquals("2100", lastPostHeaders.get("Content-Length"));
    }

    @Test
    public void testSendWithCompressionServerNotAcceptGzip() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        // Specify a property to the URL to have the CaptureServlet specify it doesn't accept gzip
        runner.setProperty(URL, ((server.getUrl()) + "?acceptGzip=false"));
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(COMPRESSION_LEVEL, "9");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/plain");
        runner.enqueue(StringUtils.repeat("Lines of sample text.", 100).getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        // Ensure that the request was not sent with a 'Content-Encoding' header
        Assert.assertNull(lastPostHeaders.get(CONTENT_ENCODING_HEADER));
    }

    @Test
    public void testSendChunked() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(CHUNKED_ENCODING, "true");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/plain");
        runner.enqueue(StringUtils.repeat("Lines of sample text.", 100).getBytes(), attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        byte[] postValue = servlet.getLastPost();
        Assert.assertArrayEquals(StringUtils.repeat("Lines of sample text.", 100).getBytes(), postValue);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        Assert.assertNull(lastPostHeaders.get("Content-Length"));
        Assert.assertEquals("chunked", lastPostHeaders.get("Transfer-Encoding"));
    }

    @Test
    public void testSendWithThrottler() throws Exception {
        setup(null);
        final String suppliedMimeType = "text/plain";
        runner.setProperty(URL, server.getUrl());
        runner.setProperty(CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(CHUNKED_ENCODING, "false");
        runner.setProperty(MAX_DATA_RATE, "10kb");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/plain");
        runner.enqueue(StringUtils.repeat("This is a line of sample text. Here is another.", 100).getBytes(), attrs);
        boolean stopOnFinish = true;
        runner.run(1, stopOnFinish);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        byte[] postValue = servlet.getLastPost();
        Assert.assertArrayEquals(StringUtils.repeat("This is a line of sample text. Here is another.", 100).getBytes(), postValue);
        Map<String, String> lastPostHeaders = servlet.getLastPostHeaders();
        Assert.assertEquals(suppliedMimeType, lastPostHeaders.get(CONTENT_TYPE_HEADER));
        Assert.assertEquals("4700", lastPostHeaders.get("Content-Length"));
    }

    @Test
    public void testDefaultUserAgent() throws Exception {
        setup(null);
        Assert.assertTrue(runner.getProcessContext().getProperty(USER_AGENT).getValue().startsWith("Apache-HttpClient"));
    }

    @Test
    public void testBatchWithMultipleUrls() throws Exception {
        CaptureServlet servletA;
        CaptureServlet servletB;
        TestServer serverA;
        TestServer serverB;
        {
            // setup test servers
            setup(null);
            servletA = servlet;
            serverA = server;
            // set up second web service
            ServletHandler handler = new ServletHandler();
            handler.addServletWithMapping(CaptureServlet.class, "/*");
            // create the second service
            serverB = new TestServer(null);
            serverB.addHandler(handler);
            serverB.startServer();
            servletB = ((CaptureServlet) (handler.getServlets()[0].getServlet()));
        }
        runner.setProperty(URL, "${url}");// use EL for the URL

        runner.setProperty(SEND_AS_FLOWFILE, "true");
        runner.setProperty(MAX_BATCH_SIZE, "10 b");
        Set<String> expectedContentA = new HashSet<>();
        Set<String> expectedContentB = new HashSet<>();
        Set<String> actualContentA = new HashSet<>();
        Set<String> actualContentB = new HashSet<>();
        // enqueue 9 FlowFiles
        for (int i = 0; i < 9; i++) {
            enqueueWithURL(("a" + i), serverA.getUrl());
            enqueueWithURL(("b" + i), serverB.getUrl());
            expectedContentA.add(("a" + i));
            expectedContentB.add(("b" + i));
        }
        // MAX_BATCH_SIZE is 10 bytes, each file is 2 bytes, so 18 files should produce 4 batches
        for (int i = 0; i < 4; i++) {
            runner.run(1);
            runner.assertAllFlowFilesTransferred(REL_SUCCESS);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertFalse(successFiles.isEmpty());
            MockFlowFile mff = successFiles.get(0);
            final String urlAttr = mff.getAttribute("url");
            if (serverA.getUrl().equals(urlAttr)) {
                checkBatch(serverA, servletA, actualContentA, (actualContentA.isEmpty() ? 5 : 4));
            } else
                if (serverB.getUrl().equals(urlAttr)) {
                    checkBatch(serverB, servletB, actualContentB, (actualContentB.isEmpty() ? 5 : 4));
                } else {
                    Assert.fail("unexpected url attribute");
                }

        }
        Assert.assertEquals(expectedContentA, actualContentA);
        Assert.assertEquals(expectedContentB, actualContentB);
        // make sure everything transferred, nothing more to do
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }
}

