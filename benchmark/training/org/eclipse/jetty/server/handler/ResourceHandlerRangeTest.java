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


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled("Unfixed range bug - Issue #107")
public class ResourceHandlerRangeTest {
    private static Server server;

    private static URI serverUri;

    @Test
    public void testGetRange() throws Exception {
        URI uri = ResourceHandlerRangeTest.serverUri.resolve("range.txt");
        HttpURLConnection uconn = ((HttpURLConnection) (uri.toURL().openConnection()));
        uconn.setRequestMethod("GET");
        uconn.addRequestProperty("Range", (("bytes=" + 5) + "-"));
        int contentLength = Integer.parseInt(uconn.getHeaderField("Content-Length"));
        String response;
        try (InputStream is = uconn.getInputStream()) {
            response = IO.toString(is);
        }
        MatcherAssert.assertThat("Content Length", contentLength, Matchers.is(5));
        MatcherAssert.assertThat("Response Content", response, Matchers.is("56789"));
    }
}

