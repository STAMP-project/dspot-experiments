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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class DebugHandlerTest {
    public static final HostnameVerifier __hostnameverifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private SSLContext sslContext;

    private Server server;

    private URI serverURI;

    private URI secureServerURI;

    @SuppressWarnings("deprecation")
    private DebugHandler debugHandler;

    private ByteArrayOutputStream capturedLog;

    @Test
    public void testThreadName() throws IOException {
        HttpURLConnection http = ((HttpURLConnection) (serverURI.resolve("/foo/bar?a=b").toURL().openConnection()));
        MatcherAssert.assertThat("Response Code", http.getResponseCode(), Matchers.is(200));
        String log = capturedLog.toString(StandardCharsets.UTF_8.name());
        String expectedThreadName = String.format("//%s:%s/foo/bar?a=b", serverURI.getHost(), serverURI.getPort());
        MatcherAssert.assertThat("ThreadName", log, Matchers.containsString(expectedThreadName));
        // Look for bad/mangled/duplicated schemes
        MatcherAssert.assertThat("ThreadName", log, Matchers.not(Matchers.containsString(("http:" + expectedThreadName))));
        MatcherAssert.assertThat("ThreadName", log, Matchers.not(Matchers.containsString(("https:" + expectedThreadName))));
    }

    @Test
    public void testSecureThreadName() throws IOException {
        HttpURLConnection http = ((HttpURLConnection) (secureServerURI.resolve("/foo/bar?a=b").toURL().openConnection()));
        MatcherAssert.assertThat("Response Code", http.getResponseCode(), Matchers.is(200));
        String log = capturedLog.toString(StandardCharsets.UTF_8.name());
        String expectedThreadName = String.format("https://%s:%s/foo/bar?a=b", secureServerURI.getHost(), secureServerURI.getPort());
        MatcherAssert.assertThat("ThreadName", log, Matchers.containsString(expectedThreadName));
        // Look for bad/mangled/duplicated schemes
        MatcherAssert.assertThat("ThreadName", log, Matchers.not(Matchers.containsString(("http:" + expectedThreadName))));
        MatcherAssert.assertThat("ThreadName", log, Matchers.not(Matchers.containsString(("https:" + expectedThreadName))));
    }
}

