/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kura;


import ServiceStatus.Stopped;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;


public class KuraRouterTest extends Assert {
    KuraRouterTest.TestKuraRouter router = new KuraRouterTest.TestKuraRouter();

    BundleContext bundleContext = Mockito.mock(BundleContext.class, Mockito.RETURNS_DEEP_STUBS);

    ConfigurationAdmin configurationAdmin = Mockito.mock(ConfigurationAdmin.class);

    Configuration configuration = Mockito.mock(Configuration.class);

    @Test
    public void shouldCloseCamelContext() throws Exception {
        // When
        stop(bundleContext);
        // Then
        Assert.assertEquals(Stopped, router.camelContext.getStatus());
    }

    @Test
    public void shouldStartCamelContext() throws Exception {
        // Given
        String message = "foo";
        MockEndpoint mockEndpoint = router.camelContext.getEndpoint("mock:test", MockEndpoint.class);
        mockEndpoint.expectedBodiesReceived(message);
        // When
        router.producerTemplate.sendBody("direct:start", message);
        // Then
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldCreateConsumerTemplate() throws Exception {
        Assert.assertNotNull(router.consumerTemplate);
    }

    @Test
    public void shouldReturnNoService() {
        BDDMockito.given(bundleContext.getServiceReference(ArgumentMatchers.any(String.class))).willReturn(null);
        Assert.assertNull(service(ConfigurationAdmin.class));
    }

    @Test
    public void shouldReturnService() {
        Assert.assertNotNull(service(ConfigurationAdmin.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldValidateLackOfService() {
        BDDMockito.given(bundleContext.getServiceReference(ArgumentMatchers.any(String.class))).willReturn(null);
        requiredService(ConfigurationAdmin.class);
    }

    @Test
    public void shouldLoadXmlRoutes() throws Exception {
        // Given
        BDDMockito.given(configurationAdmin.getConfiguration(ArgumentMatchers.anyString())).willReturn(configuration);
        Dictionary<String, Object> properties = new Hashtable<>();
        String routeDefinition = IOUtils.toString(getClass().getResource("/route.xml"), StandardCharsets.UTF_8);
        properties.put("kura.camel.symbolic_name.route", routeDefinition);
        BDDMockito.given(configuration.getProperties()).willReturn(properties);
        // When
        router.start(router.bundleContext);
        // Then
        Assert.assertNotNull(router.camelContext.adapt(ModelCamelContext.class).getRouteDefinition("loaded"));
    }

    static class TestKuraRouter extends KuraRouter {
        @Override
        public void configure() throws Exception {
            from("direct:start").to("mock:test");
        }

        @Override
        protected CamelContext createCamelContext() {
            return new DefaultCamelContext();
        }
    }
}

