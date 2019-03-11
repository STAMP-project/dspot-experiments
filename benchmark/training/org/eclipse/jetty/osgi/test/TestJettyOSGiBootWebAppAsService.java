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


import HttpStatus.OK_200;
import javax.inject.Inject;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * TestJettyOSGiBootWebAppAsService
 *
 * Tests deployment of a WebAppContext as an osgi Service.
 *
 * Tests the ServiceWebAppProvider.
 *
 * Pax-Exam to make sure the jetty-osgi-boot can be started along with the
 * httpservice web-bundle. Then make sure we can deploy an OSGi service on the
 * top of this.
 */
@RunWith(PaxExam.class)
public class TestJettyOSGiBootWebAppAsService {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    BundleContext bundleContext = null;

    @Test
    public void testBundle() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            assertAllBundlesActiveOrResolved();

        // now test getting a static file
        HttpClient client = new HttpClient();
        try {
            client.start();
            String port = System.getProperty("boot.webapp.service.port");
            Assert.assertNotNull(port);
            ContentResponse response = client.GET((("http://127.0.0.1:" + port) + "/acme/index.html"));
            Assert.assertEquals(OK_200, response.getStatus());
            String content = response.getContentAsString();
            Assert.assertTrue(((content.indexOf("<h1>Test OSGi WebAppA</h1>")) != (-1)));
            response = client.GET((("http://127.0.0.1:" + port) + "/acme/mime"));
            Assert.assertEquals(OK_200, response.getStatus());
            content = response.getContentAsString();
            Assert.assertTrue(((content.indexOf("MIMETYPE=application/gzip")) != (-1)));
            port = System.getProperty("bundle.server.port");
            Assert.assertNotNull(port);
            response = client.GET((("http://127.0.0.1:" + port) + "/acme/index.html"));
            Assert.assertEquals(OK_200, response.getStatus());
            content = response.getContentAsString();
            Assert.assertTrue(((content.indexOf("<h1>Test OSGi WebAppB</h1>")) != (-1)));
        } finally {
            client.stop();
        }
        ServiceReference<?>[] refs = bundleContext.getServiceReferences(WebAppContext.class.getName(), null);
        Assert.assertNotNull(refs);
        Assert.assertEquals(2, refs.length);
        WebAppContext wac = ((WebAppContext) (bundleContext.getService(refs[0])));
        Assert.assertEquals("/acme", wac.getContextPath());
        wac = ((WebAppContext) (bundleContext.getService(refs[1])));
        Assert.assertEquals("/acme", wac.getContextPath());
    }
}

