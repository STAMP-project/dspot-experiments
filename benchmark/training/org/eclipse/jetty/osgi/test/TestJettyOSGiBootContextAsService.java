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
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * TestJettyOSGiBootContextAsService
 *
 * Tests deployment of a ContextHandler as an osgi Service.
 *
 * Tests the ServiceContextProvider.
 */
@RunWith(PaxExam.class)
public class TestJettyOSGiBootContextAsService {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    BundleContext bundleContext = null;

    /**
     *
     */
    @Test
    public void testContextHandlerAsOSGiService() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            TestOSGiUtil.assertAllBundlesActiveOrResolved(bundleContext);

        // now test the context
        HttpClient client = new HttpClient();
        try {
            client.start();
            String tmp = System.getProperty("boot.context.service.port");
            Assert.assertNotNull(tmp);
            int port = Integer.valueOf(tmp).intValue();
            ContentResponse response = client.GET((("http://127.0.0.1:" + port) + "/acme/index.html"));
            Assert.assertEquals(OK_200, response.getStatus());
            String content = new String(response.getContent());
            Assert.assertTrue(((content.indexOf("<h1>Test OSGi Context</h1>")) != (-1)));
        } finally {
            client.stop();
        }
        ServiceReference[] refs = bundleContext.getServiceReferences(ContextHandler.class.getName(), null);
        Assert.assertNotNull(refs);
        Assert.assertEquals(1, refs.length);
        ContextHandler ch = ((ContextHandler) (bundleContext.getService(refs[0])));
        Assert.assertEquals("/acme", ch.getContextPath());
        // Stop the bundle with the ContextHandler in it and check the jetty
        // Context is destroyed for it.
        // TODO: think of a better way to communicate this to the test, other
        // than checking stderr output
        Bundle testWebBundle = TestOSGiUtil.getBundle(bundleContext, "org.eclipse.jetty.osgi.testcontext");
        Assert.assertNotNull("Could not find the org.eclipse.jetty.test-jetty-osgi-context.jar bundle", testWebBundle);
        Assert.assertTrue("The bundle org.eclipse.jetty.testcontext is not correctly resolved", ((testWebBundle.getState()) == (Bundle.ACTIVE)));
        testWebBundle.stop();
    }
}

