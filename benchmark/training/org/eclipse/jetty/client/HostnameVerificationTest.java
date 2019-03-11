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
package org.eclipse.jetty.client;


import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLHandshakeException;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This test class runs tests to make sure that hostname verification (http://www.ietf.org/rfc/rfc2818.txt
 * section 3.1) is configurable in SslContextFactory and works as expected.
 */
@Disabled
public class HostnameVerificationTest {
    private SslContextFactory clientSslContextFactory = new SslContextFactory();

    private Server server;

    private HttpClient client;

    private NetworkConnector connector;

    /**
     * This test is supposed to verify that hostname verification works as described in:
     * http://www.ietf.org/rfc/rfc2818.txt section 3.1. It uses a certificate with a common name different to localhost
     * and sends a request to localhost. This should fail with a SSLHandshakeException.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void simpleGetWithHostnameVerificationEnabledTest() throws Exception {
        clientSslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        String uri = ("https://localhost:" + (connector.getLocalPort())) + "/";
        ExecutionException x = Assertions.assertThrows(ExecutionException.class, () -> {
            client.GET(uri);
        });
        Throwable cause = x.getCause();
        MatcherAssert.assertThat(cause, Matchers.instanceOf(SSLHandshakeException.class));
        Throwable root = cause.getCause().getCause();
        MatcherAssert.assertThat(root, Matchers.instanceOf(CertificateException.class));
    }

    /**
     * This test has hostname verification disabled and connecting, ssl handshake and sending the request should just
     * work fine.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void simpleGetWithHostnameVerificationDisabledTest() throws Exception {
        clientSslContextFactory.setEndpointIdentificationAlgorithm(null);
        String uri = ("https://localhost:" + (connector.getLocalPort())) + "/";
        try {
            client.GET(uri);
        } catch (ExecutionException e) {
            Assertions.fail("SSLHandshake should work just fine as hostname verification is disabled!", e);
        }
    }

    /**
     * This test has hostname verification disabled by setting trustAll to true and connecting,
     * ssl handshake and sending the request should just work fine.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void trustAllDisablesHostnameVerificationTest() throws Exception {
        clientSslContextFactory.setTrustAll(true);
        String uri = ("https://localhost:" + (connector.getLocalPort())) + "/";
        try {
            client.GET(uri);
        } catch (ExecutionException e) {
            Assertions.fail("SSLHandshake should work just fine as hostname verification is disabled!", e);
        }
    }
}

