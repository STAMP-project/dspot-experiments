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


import XFrameOptionsFilter.X_FRAME_OPTIONS;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.junit.Assert;
import org.junit.Test;


/**
 * Used TestRMWebServices as an example of web invocations of RM and added
 * test for XFS Filter.
 */
public class TestRMWithXFSFilter extends JerseyTestBase {
    private static MockRM rm;

    public TestRMWithXFSFilter() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testDefaultBehavior() throws Exception {
        createInjector();
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").get(ClientResponse.class);
        Assert.assertEquals("Should have received DENY x-frame options header", "DENY", response.getHeaders().get(X_FRAME_OPTIONS).get(0));
    }

    @Test
    public void testSameOrigin() throws Exception {
        createInjector("SAMEORIGIN");
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").get(ClientResponse.class);
        Assert.assertEquals("Should have received SAMEORIGIN x-frame options header", "SAMEORIGIN", response.getHeaders().get(X_FRAME_OPTIONS).get(0));
    }

    @Test
    public void testExplicitlyDisabled() throws Exception {
        createInjector(null, true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").get(ClientResponse.class);
        Assert.assertFalse("Should have not received x-frame options header", ((response.getHeaders().get(X_FRAME_OPTIONS)) == null));
    }
}

