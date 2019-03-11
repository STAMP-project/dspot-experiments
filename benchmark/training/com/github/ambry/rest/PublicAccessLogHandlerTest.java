/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.rest;


import ClientAuth.REQUIRE;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import org.junit.Test;


/**
 * Unit tests for {@link PublicAccessLogHandler}
 */
public class PublicAccessLogHandlerTest {
    private MockPublicAccessLogger publicAccessLogger;

    private static final String REQUEST_HEADERS = (((HttpHeaderNames.HOST) + ",") + (HttpHeaderNames.CONTENT_LENGTH)) + ",x-ambry-content-type";

    private static final String RESPONSE_HEADERS = ((EchoMethodHandler.RESPONSE_HEADER_KEY_1) + ",") + (EchoMethodHandler.RESPONSE_HEADER_KEY_2);

    private static final String NOT_LOGGED_HEADER_KEY = "headerKey";

    private static final X509Certificate PEER_CERT;

    private static final SslContext SSL_CONTEXT;

    static {
        try {
            PEER_CERT = new SelfSignedCertificate().cert();
            SelfSignedCertificate localCert = new SelfSignedCertificate();
            SSL_CONTEXT = SslContextBuilder.forServer(localCert.certificate(), localCert.privateKey()).clientAuth(REQUIRE).build();
        } catch (CertificateException | SSLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sets up the mock public access logger that {@link PublicAccessLogHandler} can use.
     */
    public PublicAccessLogHandlerTest() {
        publicAccessLogger = new MockPublicAccessLogger(PublicAccessLogHandlerTest.REQUEST_HEADERS.split(","), PublicAccessLogHandlerTest.RESPONSE_HEADERS.split(","));
    }

    /**
     * Tests for the common case request handling flow.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestHandleWithGoodInputTest() throws Exception {
        doRequestHandleTest(POST, "POST", false, false);
        doRequestHandleTest(GET, "GET", false, false);
        doRequestHandleTest(DELETE, "DELETE", false, false);
        // SSL enabled
        doRequestHandleTest(POST, "POST", false, true);
        doRequestHandleTest(GET, "GET", false, true);
        doRequestHandleTest(DELETE, "DELETE", false, true);
    }

    /**
     * Tests for multiple requests with keep alive.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestHandleWithGoodInputTestWithKeepAlive() throws Exception {
        doRequestHandleWithKeepAliveTest(POST, "POST", false);
        doRequestHandleWithKeepAliveTest(GET, "GET", false);
        doRequestHandleWithKeepAliveTest(DELETE, "DELETE", false);
        // SSL enabled
        doRequestHandleWithKeepAliveTest(POST, "POST", true);
        doRequestHandleWithKeepAliveTest(GET, "GET", true);
        doRequestHandleWithKeepAliveTest(DELETE, "DELETE", true);
    }

    /**
     * Tests two successive request without completing first request
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestHandleWithTwoSuccessiveRequest() throws Exception {
        doRequestHandleWithMultipleRequest(POST, "POST", false);
        doRequestHandleWithMultipleRequest(GET, "GET", false);
        doRequestHandleWithMultipleRequest(DELETE, "DELETE", false);
        // SSL enabled
        doRequestHandleWithMultipleRequest(POST, "POST", true);
        doRequestHandleWithMultipleRequest(GET, "GET", true);
        doRequestHandleWithMultipleRequest(DELETE, "DELETE", true);
    }

    /**
     * Tests for the request handling flow for close
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestHandleOnCloseTest() throws Exception {
        doRequestHandleTest(POST, EchoMethodHandler.CLOSE_URI, true, false);
        doRequestHandleTest(GET, EchoMethodHandler.CLOSE_URI, true, false);
        doRequestHandleTest(DELETE, EchoMethodHandler.CLOSE_URI, true, false);
        // SSL enabled
        doRequestHandleTest(POST, EchoMethodHandler.CLOSE_URI, true, true);
        doRequestHandleTest(GET, EchoMethodHandler.CLOSE_URI, true, true);
        doRequestHandleTest(DELETE, EchoMethodHandler.CLOSE_URI, true, true);
    }

    /**
     * Tests for the request handling flow on disconnect
     *
     * @throws Exception
     * 		
     */
    @Test
    public void requestHandleOnDisconnectTest() throws Exception {
        // disonnecting the embedded channel, calls close of PubliAccessLogRequestHandler
        doRequestHandleTest(POST, EchoMethodHandler.DISCONNECT_URI, true, false);
        doRequestHandleTest(GET, EchoMethodHandler.DISCONNECT_URI, true, false);
        doRequestHandleTest(DELETE, EchoMethodHandler.DISCONNECT_URI, true, false);
        // SSL enabled
        doRequestHandleTest(POST, EchoMethodHandler.DISCONNECT_URI, true, true);
        doRequestHandleTest(GET, EchoMethodHandler.DISCONNECT_URI, true, true);
        doRequestHandleTest(DELETE, EchoMethodHandler.DISCONNECT_URI, true, true);
    }

    /**
     * Tests for the request handling flow with transfer encoding chunked
     */
    @Test
    public void requestHandleWithChunkedResponse() throws Exception {
        doRequestHandleWithChunkedResponse(false);
        // SSL enabled
        doRequestHandleWithChunkedResponse(true);
    }
}

