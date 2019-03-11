/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.webapp;


import RestCsrfPreventionFilter.CUSTOM_METHODS_TO_IGNORE_PARAM;
import RestCsrfPreventionFilter.HEADER_USER_AGENT;
import Status.BAD_REQUEST;
import Status.OK;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.http.RestCsrfPreventionFilter;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.JAXBContextResolver;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.RMWebServices;
import org.junit.Assert;
import org.junit.Test;


/**
 * Used TestRMWebServices as an example of web invocations of RM and added
 * test for CSRF Filter.
 */
public class TestRMWithCSRFFilter extends JerseyTestBase {
    private static MockRM rm;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            Configuration conf = new Configuration();
            conf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
            TestRMWithCSRFFilter.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWithCSRFFilter.rm);
            serve("/*").with(GuiceContainer.class);
            RestCsrfPreventionFilter csrfFilter = new RestCsrfPreventionFilter();
            Map<String, String> initParams = new HashMap<>();
            // adding GET as protected method to make things a little easier...
            initParams.put(CUSTOM_METHODS_TO_IGNORE_PARAM, "OPTIONS,HEAD,TRACE");
            filter("/*").through(csrfFilter, initParams);
        }
    }

    public TestRMWithCSRFFilter() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNoCustomHeaderFromBrowser() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").header(HEADER_USER_AGENT, "Mozilla/5.0").get(ClientResponse.class);
        Assert.assertTrue("Should have been rejected", ((response.getStatus()) == (BAD_REQUEST.getStatusCode())));
    }

    @Test
    public void testIncludeCustomHeaderFromBrowser() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").header(HEADER_USER_AGENT, "Mozilla/5.0").header("X-XSRF-HEADER", "").get(ClientResponse.class);
        Assert.assertTrue("Should have been accepted", ((response.getStatus()) == (OK.getStatusCode())));
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyClusterInfoXML(xml);
    }

    @Test
    public void testAllowedMethod() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").header(HEADER_USER_AGENT, "Mozilla/5.0").head();
        Assert.assertTrue("Should have been allowed", ((response.getStatus()) == (OK.getStatusCode())));
    }

    @Test
    public void testAllowNonBrowserInteractionWithoutHeader() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").get(ClientResponse.class);
        Assert.assertTrue("Should have been accepted", ((response.getStatus()) == (OK.getStatusCode())));
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyClusterInfoXML(xml);
    }
}

