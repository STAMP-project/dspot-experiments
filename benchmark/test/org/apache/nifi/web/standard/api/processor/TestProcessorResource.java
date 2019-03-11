/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.standard.api.processor;


import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.nifi.web.ComponentDescriptor;
import org.apache.nifi.web.ComponentDetails;
import org.apache.nifi.web.NiFiWebConfigurationContext;
import org.apache.nifi.web.NiFiWebConfigurationRequestContext;
import org.apache.nifi.web.NiFiWebRequestContext;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestProcessorResource extends JerseyTest {
    public static final ServletContext servletContext = Mockito.mock(ServletContext.class);

    public static final HttpServletRequest requestContext = Mockito.mock(HttpServletRequest.class);

    @Test
    public void testSetProperties() {
        final NiFiWebConfigurationContext niFiWebConfigurationContext = Mockito.mock(NiFiWebConfigurationContext.class);
        final Map<String, String> properties = new HashMap<>();
        properties.put("jolt-transform", "jolt-transform-chain");
        final ComponentDetails componentDetails = new ComponentDetails.Builder().properties(properties).build();
        Mockito.when(TestProcessorResource.servletContext.getAttribute(Mockito.anyString())).thenReturn(niFiWebConfigurationContext);
        Mockito.when(niFiWebConfigurationContext.updateComponent(ArgumentMatchers.any(NiFiWebConfigurationRequestContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Map.class))).thenReturn(componentDetails);
        Response response = client().target(getBaseUri()).path("/standard/processor/properties").queryParam("processorId", "1").queryParam("clientId", "1").queryParam("revisionId", "1").request().put(Entity.json(JsonUtils.toJsonString(properties)));
        Assert.assertNotNull(response);
        JsonNode jsonNode = response.readEntity(JsonNode.class);
        Assert.assertNotNull(jsonNode);
        Assert.assertTrue(jsonNode.get("properties").get("jolt-transform").asText().equals("jolt-transform-chain"));
    }

    @Test
    public void testGetProcessorDetails() {
        final NiFiWebConfigurationContext niFiWebConfigurationContext = Mockito.mock(NiFiWebConfigurationContext.class);
        final Map<String, String> allowableValues = new HashMap<>();
        final ComponentDescriptor descriptor = new ComponentDescriptor.Builder().name("test-name").allowableValues(allowableValues).build();
        final Map<String, ComponentDescriptor> descriptors = new HashMap<>();
        descriptors.put("jolt-transform", descriptor);
        final ComponentDetails componentDetails = new ComponentDetails.Builder().name("mytransform").type("org.apache.nifi.processors.standard.JoltTransformJSON").descriptors(descriptors).build();
        Mockito.when(TestProcessorResource.servletContext.getAttribute(Mockito.anyString())).thenReturn(niFiWebConfigurationContext);
        Mockito.when(niFiWebConfigurationContext.getComponentDetails(ArgumentMatchers.any(NiFiWebRequestContext.class))).thenReturn(componentDetails);
        JsonNode value = client().target(getBaseUri()).path("/standard/processor/details").queryParam("processorId", "1").request().get(JsonNode.class);
        Assert.assertNotNull(value);
        try {
            Assert.assertTrue(value.get("name").asText().equals("mytransform"));
        } catch (Exception e) {
            Assert.fail(("Failed due to: " + (e.toString())));
        }
    }

    public static class MockRequestContext implements Factory<HttpServletRequest> {
        @Override
        public HttpServletRequest provide() {
            return TestProcessorResource.requestContext;
        }

        @Override
        public void dispose(HttpServletRequest t) {
        }
    }

    public static class MockServletContext implements Factory<ServletContext> {
        @Override
        public ServletContext provide() {
            return TestProcessorResource.servletContext;
        }

        @Override
        public void dispose(ServletContext t) {
        }
    }
}

