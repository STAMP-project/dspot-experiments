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
package org.eclipse.jetty.osgi.test;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TestJettyOSGiBootHTTP2JDK9 {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    private BundleContext bundleContext;

    @Test
    public void testHTTP2() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            assertAllBundlesActiveOrResolved();

        HttpClient httpClient = null;
        HTTP2Client http2Client = null;
        try {
            // get the port chosen for https
            String port = System.getProperty("boot.https.port");
            Assert.assertNotNull(port);
            Path path = Paths.get("src", "test", "config");
            File keys = path.resolve("etc").resolve("keystore").toFile();
            // set up client to do http2
            http2Client = new HTTP2Client();
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyManagerPassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
            sslContextFactory.setTrustStorePath(keys.getAbsolutePath());
            sslContextFactory.setKeyStorePath(keys.getAbsolutePath());
            sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
            sslContextFactory.setEndpointIdentificationAlgorithm(null);
            httpClient = new HttpClient(new org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2(http2Client), sslContextFactory);
            Executor executor = new QueuedThreadPool();
            httpClient.setExecutor(executor);
            httpClient.start();
            ContentResponse response = httpClient.GET((("https://localhost:" + port) + "/jsp/jstl.jsp"));
            Assert.assertEquals(200, response.getStatus());
            Assert.assertTrue(response.getContentAsString().contains("JSTL Example"));
        } finally {
            if (httpClient != null)
                httpClient.stop();

            if (http2Client != null)
                http2Client.stop();

        }
    }
}

