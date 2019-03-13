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
package org.apache.camel.component.jbpm.server;


import JBPMConstants.CAMEL_CONTEXT_BUILDER_KEY;
import JBPMConstants.GLOBAL_CAMEL_CONTEXT_SERVICE_KEY;
import java.util.HashMap;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jbpm.JBPMConstants;
import org.apache.camel.component.jbpm.config.CamelContextBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.hamcrest.CoreMatchers;
import org.jbpm.services.api.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.server.services.api.KieContainerInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CamelKieServerExtensionTest {
    @Mock
    InternalRuntimeManager runtimeManager;

    @Mock
    RuntimeEnvironment runtimeEnvironment;

    @Mock
    private KieContainerInstance kieContainerInstance;

    @Mock
    private KieContainer kieContainer;

    private String identifier = "test";

    @Test
    public void testInit() {
        CamelKieServerExtension extension = new CamelKieServerExtension();
        extension.init(null, null);
        DefaultCamelContext globalCamelContext = ((DefaultCamelContext) (ServiceRegistry.get().service(GLOBAL_CAMEL_CONTEXT_SERVICE_KEY)));
        List<RouteDefinition> globalRestDefinitions = globalCamelContext.getRouteDefinitions();
        Assert.assertThat(globalRestDefinitions.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(globalCamelContext.getRouteDefinition("unitTestRoute"), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testCreateContainer() {
        CamelKieServerExtension extension = new CamelKieServerExtension();
        final String containerId = "testContainer";
        Mockito.when(kieContainerInstance.getKieContainer()).thenReturn(kieContainer);
        Mockito.when(kieContainer.getClassLoader()).thenReturn(this.getClass().getClassLoader());
        extension.createContainer(containerId, kieContainerInstance, new HashMap<String, Object>());
        DefaultCamelContext camelContext = ((DefaultCamelContext) (ServiceRegistry.get().service(("testContainer" + (JBPMConstants.DEPLOYMENT_CAMEL_CONTEXT_SERVICE_KEY_POSTFIX)))));
        List<RouteDefinition> restDefinitions = camelContext.getRouteDefinitions();
        Assert.assertThat(restDefinitions.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(camelContext.getRoute("unitTestRoute"), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testDefaultSetup() {
        CamelKieServerExtension extension = new CamelKieServerExtension();
        Assert.assertNull(extension.getCamelContextBuilder());
    }

    @Test
    public void testDefaultSetupCustomDiscovery() {
        CamelKieServerExtension extension = new CamelKieServerExtension() {
            @Override
            protected CamelContextBuilder discoverCamelContextBuilder() {
                return new CamelContextBuilder() {
                    @Override
                    public CamelContext buildCamelContext() {
                        // for test purpose return simply null as camel context
                        return null;
                    }
                };
            }
        };
        Assert.assertNotNull(extension.getCamelContextBuilder());
        Assert.assertNull(extension.getCamelContextBuilder().buildCamelContext());
    }

    @Test
    public void testBuildGlobalCamelContext() throws Exception {
        CamelKieServerExtension extension = new CamelKieServerExtension();
        CamelContext context = extension.buildGlobalContext();
        Assert.assertNotNull(context);
        context.stop();
    }

    @Test
    public void testBuildGlobalCamelContextCustomBuilder() throws Exception {
        CamelKieServerExtension extension = new CamelKieServerExtension(new CamelContextBuilder() {
            @Override
            public CamelContext buildCamelContext() {
                // for test purpose return simply null as camel context
                return null;
            }
        });
        CamelContext context = extension.buildGlobalContext();
        Assert.assertNull(context);
    }

    @Test
    public void testBuildDeploymentCamelContext() throws Exception {
        Mockito.when(runtimeManager.getIdentifier()).thenReturn(identifier);
        Mockito.when(runtimeManager.getEnvironment()).thenReturn(runtimeEnvironment);
        Environment environment = KieServices.get().newEnvironment();
        Mockito.when(runtimeEnvironment.getEnvironment()).thenReturn(environment);
        RuntimeManagerRegistry.get().register(runtimeManager);
        CamelKieServerExtension extension = new CamelKieServerExtension();
        CamelContext context = extension.buildDeploymentContext(identifier, this.getClass().getClassLoader());
        Assert.assertNotNull(context);
        context.stop();
    }

    @Test
    public void testBuildDeploymentCamelContextCustomBuilder() throws Exception {
        Mockito.when(runtimeManager.getIdentifier()).thenReturn(identifier);
        Mockito.when(runtimeManager.getEnvironment()).thenReturn(runtimeEnvironment);
        Environment environment = KieServices.get().newEnvironment();
        environment.set(CAMEL_CONTEXT_BUILDER_KEY, new CamelContextBuilder() {
            @Override
            public CamelContext buildCamelContext() {
                // for test purpose return simply null as camel context
                return null;
            }
        });
        Mockito.when(runtimeEnvironment.getEnvironment()).thenReturn(environment);
        RuntimeManagerRegistry.get().register(runtimeManager);
        CamelKieServerExtension extension = new CamelKieServerExtension();
        CamelContext context = extension.buildDeploymentContext(identifier, this.getClass().getClassLoader());
        Assert.assertNull(context);
    }
}

