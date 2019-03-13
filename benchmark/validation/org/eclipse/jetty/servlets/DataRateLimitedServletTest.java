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
package org.eclipse.jetty.servlets;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class DataRateLimitedServletTest {
    public static final int BUFFER = 8192;

    public static final int PAUSE = 10;

    public WorkDir testdir;

    private Server server;

    private LocalConnector connector;

    private ServletContextHandler context;

    @Test
    public void testStream() throws Exception {
        File content = testdir.getPathFile("content.txt").toFile();
        String[] results = new String[10];
        try (OutputStream out = new FileOutputStream(content)) {
            byte[] b = new byte[1024];
            for (int i = 1024; (i--) > 0;) {
                int index = i % 10;
                Arrays.fill(b, ((byte) ('0' + index)));
                out.write(b);
                out.write('\n');
                if ((results[index]) == null)
                    results[index] = new String(b, StandardCharsets.US_ASCII);

            }
        }
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String response = connector.getResponse("GET /context/stream/content.txt HTTP/1.0\r\n\r\n");
        long duration = (TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start;
        MatcherAssert.assertThat("Response", response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat("Response Length", response.length(), Matchers.greaterThan((1024 * 1024)));
        MatcherAssert.assertThat("Duration", duration, Matchers.greaterThan(((((DataRateLimitedServletTest.PAUSE) * 1024L) * 1024) / (DataRateLimitedServletTest.BUFFER))));
        for (int i = 0; i < 10; i++)
            MatcherAssert.assertThat(response, Matchers.containsString(results[i]));

    }
}

