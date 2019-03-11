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
import org.eclipse.jetty.client.api.Request;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;


/**
 * Pax-Exam to make sure the jetty-osgi-boot can be started along with the
 * httpservice web-bundle. Then make sure we can deploy an OSGi service on the
 * top of this.
 */
@RunWith(PaxExam.class)
public class TestJettyOSGiBootWithAnnotations {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    BundleContext bundleContext = null;

    @Test
    public void testIndex() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            assertAllBundlesActiveOrResolved();

        HttpClient client = new HttpClient();
        try {
            client.start();
            String port = System.getProperty("boot.annotations.port");
            Assert.assertNotNull(port);
            ContentResponse response = client.GET((("http://127.0.0.1:" + port) + "/index.html"));
            Assert.assertEquals(OK_200, response.getStatus());
            String content = response.getContentAsString();
            Assert.assertTrue(content.contains("<h1>Servlet 3.1 Test WebApp</h1>"));
            Request req = client.POST((("http://127.0.0.1:" + port) + "/test"));
            response = req.send();
            content = response.getContentAsString();
            Assert.assertTrue(content.contains("<p><b>Result: <span class=\"pass\">PASS</span></p>"));
            response = client.GET((("http://127.0.0.1:" + port) + "/frag.html"));
            content = response.getContentAsString();
            Assert.assertTrue(content.contains("<h1>FRAGMENT</h1>"));
        } finally {
            client.stop();
        }
    }
}

