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
package org.eclipse.jetty;


import HttpServletResponse.SC_OK;
import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * DataSourceLoginServiceTest
 */
public class DataSourceLoginServiceTest {
    public static final String _content = "This is some protected content";

    private static File _docRoot;

    private static HttpClient _client;

    private static String __realm = "DSRealm";

    private static URI _baseUri;

    private static DatabaseLoginServiceTestServer _testServer;

    @Test
    public void testGetAndPasswordUpdate() throws Exception {
        try {
            startClient("jetty", "jetty");
            ContentResponse response = DataSourceLoginServiceTest._client.GET(DataSourceLoginServiceTest._baseUri.resolve("input.txt"));
            Assertions.assertEquals(SC_OK, response.getStatus());
            Assertions.assertEquals(DataSourceLoginServiceTest._content, response.getContentAsString());
            stopClient();
            String newpwd = String.valueOf(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
            changePassword("jetty", newpwd);
            startClient("jetty", newpwd);
            response = DataSourceLoginServiceTest._client.GET(DataSourceLoginServiceTest._baseUri.resolve("input.txt"));
            Assertions.assertEquals(SC_OK, response.getStatus());
            Assertions.assertEquals(DataSourceLoginServiceTest._content, response.getContentAsString());
        } finally {
            stopClient();
        }
    }
}

