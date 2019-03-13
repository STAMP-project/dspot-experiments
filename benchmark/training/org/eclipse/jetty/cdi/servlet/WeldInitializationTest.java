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
package org.eclipse.jetty.cdi.servlet;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class WeldInitializationTest {
    private static final Logger LOG = Log.getLogger(WeldInitializationTest.class);

    private static Server server;

    private static URI serverHttpURI;

    @Test
    public void testRequestParamServletDefault() throws Exception {
        HttpURLConnection http = ((HttpURLConnection) (WeldInitializationTest.serverHttpURI.resolve("req-info").toURL().openConnection()));
        MatcherAssert.assertThat("response code", http.getResponseCode(), Matchers.is(200));
        try (InputStream inputStream = http.getInputStream()) {
            String resp = IO.toString(inputStream);
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("request is PRESENT"));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("parameters.size = [0]"));
        }
    }

    @Test
    public void testRequestParamServletAbc() throws Exception {
        HttpURLConnection http = ((HttpURLConnection) (WeldInitializationTest.serverHttpURI.resolve("req-info?abc=123").toURL().openConnection()));
        MatcherAssert.assertThat("response code", http.getResponseCode(), Matchers.is(200));
        try (InputStream inputStream = http.getInputStream()) {
            String resp = IO.toString(inputStream);
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("request is PRESENT"));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("parameters.size = [1]"));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString(" param[abc] = [123]"));
        }
    }
}

