/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.manualmode.undertow;


import javax.inject.Inject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.as.test.module.util.TestModule;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.core.testrunner.ServerControl;
import org.wildfly.core.testrunner.ServerController;
import org.wildfly.core.testrunner.WildflyTestRunner;


/**
 * Tests custom http handler(s) configured as {@code custom-filter}s in the undertow subsystem
 *
 * @author Jaikiran Pai
 */
@RunWith(WildflyTestRunner.class)
@ServerControl(manual = true)
public class CustomUndertowFilterTestCase {
    private static final Logger logger = Logger.getLogger(CustomUndertowFilterTestCase.class);

    private static final String CUSTOM_FILTER_MODULE_NAME = "custom-undertow-filter-module";

    private static final String CUSTOM_FILTER_CLASSNAME = CustomHttpHandler.class.getName();

    private static final String CUSTOM_FILTER_RESOURCE_NAME = "testcase-added-custom-undertow-filter";

    private static final String WAR_DEPLOYMENT_NAME = "test-tccl-in-custom-undertow-handler-construction";

    @Inject
    private static ServerController serverController;

    private static TestModule customHandlerModule;

    /**
     * Tests that the {@link Thread#getContextClassLoader() TCCL} that's set when a
     * custom {@link io.undertow.server.HttpHandler}, part of a (JBoss) module, configured in the undertow subsystem
     * is initialized/constructed, the classloader is the same as the classloader of the module to which
     * the handler belongs
     */
    @Test
    public void testTCCLInHttpHandlerInitialization() throws Exception {
        final String url = ((((("http://" + (TestSuiteEnvironment.getHttpAddress())) + ":") + (TestSuiteEnvironment.getHttpPort())) + "/") + (CustomUndertowFilterTestCase.WAR_DEPLOYMENT_NAME)) + "/index.html";
        CustomUndertowFilterTestCase.logger.debug(("Invoking request at " + url));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet(url);
            final HttpResponse response = httpClient.execute(httpget);
            final StatusLine statusLine = response.getStatusLine();
            Assert.assertEquals(("Unexpected HTTP response status code for request " + url), 200, statusLine.getStatusCode());
            // make sure the custom http handler was invoked and it was initialized with the right TCCL
            final Header[] headerOneValues = response.getHeaders(CustomHttpHandler.RESPONSE_HEADER_ONE_NAME);
            Assert.assertEquals(("Unexpected number of response header value for header " + (CustomHttpHandler.RESPONSE_HEADER_ONE_NAME)), 1, headerOneValues.length);
            Assert.assertEquals(("Unexpected response header value for header " + (CustomHttpHandler.RESPONSE_HEADER_ONE_NAME)), true, Boolean.valueOf(headerOneValues[0].getValue()));
            final Header[] headerTwoValues = response.getHeaders(CustomHttpHandler.RESPONSE_HEADER_TWO_NAME);
            Assert.assertEquals(("Unexpected number of response header value for header " + (CustomHttpHandler.RESPONSE_HEADER_TWO_NAME)), 1, headerTwoValues.length);
            Assert.assertEquals(("Unexpected response header value for header " + (CustomHttpHandler.RESPONSE_HEADER_TWO_NAME)), true, Boolean.valueOf(headerTwoValues[0].getValue()));
        }
    }
}

