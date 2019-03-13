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
package org.apache.ambari.server.controller;


import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.ObjectNotFoundException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.configuration.Configuration;
import org.junit.Test;

import static RootServiceResponseFactory.NOT_APPLICABLE;


public class RootServiceResponseFactoryTest {
    private Injector injector;

    @Inject
    private RootServiceResponseFactory responseFactory;

    @Inject
    private AmbariMetaInfo ambariMetaInfo;

    @Inject
    private Configuration config;

    @Test
    public void getReturnsAllServicesForNullServiceName() throws Exception {
        // Request a null service name
        RootServiceRequest request = new RootServiceRequest(null);
        Set<RootServiceResponse> rootServices = responseFactory.getRootServices(request);
        Assert.assertEquals(RootService.values().length, rootServices.size());
    }

    @Test
    public void getReturnsAllServicesForNullRequest() throws Exception {
        // null request
        Set<RootServiceResponse> rootServices = responseFactory.getRootServices(null);
        Assert.assertEquals(RootService.values().length, rootServices.size());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void getThrowsForNonExistentService() throws Exception {
        // Request nonexistent service
        RootServiceRequest request = new RootServiceRequest("XXX");
        responseFactory.getRootServices(request);
    }

    @Test
    public void getReturnsSingleServiceForValidServiceName() throws Exception {
        // Request existent service
        RootServiceRequest request = new RootServiceRequest(RootService.AMBARI.name());
        Set<RootServiceResponse> rootServices = responseFactory.getRootServices(request);
        Assert.assertEquals(Collections.singleton(new RootServiceResponse(RootService.AMBARI.name())), rootServices);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void getThrowsForNullServiceNameNullComponentName() throws Exception {
        // Request null service name, null component name
        RootServiceComponentRequest request = new RootServiceComponentRequest(null, null);
        responseFactory.getRootServiceComponents(request);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void getThrowsForNullServiceNameValidComponentName() throws Exception {
        // Request null service name, not-null component name
        RootServiceComponentRequest request = new RootServiceComponentRequest(null, RootComponent.AMBARI_SERVER.name());
        responseFactory.getRootServiceComponents(request);
    }

    @Test
    public void getReturnsAllComponentsForValidServiceNameNullComponentName() throws Exception {
        // Request existent service name, null component name
        RootServiceComponentRequest request = new RootServiceComponentRequest(RootService.AMBARI.name(), null);
        Set<RootServiceComponentResponse> rootServiceComponents = responseFactory.getRootServiceComponents(request);
        Assert.assertEquals(RootService.AMBARI.getComponents().length, rootServiceComponents.size());
        for (int i = 0; i < (RootService.AMBARI.getComponents().length); i++) {
            RootComponent component = RootService.AMBARI.getComponents()[i];
            if (component.name().equals(RootComponent.AMBARI_SERVER.name())) {
                for (RootServiceComponentResponse response : rootServiceComponents) {
                    if (response.getComponentName().equals(RootComponent.AMBARI_SERVER.name())) {
                        verifyResponseForAmbariServer(response);
                    }
                }
            } else {
                Assert.assertTrue(rootServiceComponents.contains(new RootServiceComponentResponse(RootService.AMBARI.name(), component.name(), NOT_APPLICABLE, Collections.emptyMap())));
            }
        }
    }

    @Test
    public void getReturnsSingleComponentForValidServiceAndComponentName() throws Exception {
        // Request existent service name, existent component name
        RootServiceComponentRequest request = new RootServiceComponentRequest(RootService.AMBARI.name(), RootComponent.AMBARI_SERVER.name());
        Set<RootServiceComponentResponse> rootServiceComponents = responseFactory.getRootServiceComponents(request);
        Assert.assertEquals(1, rootServiceComponents.size());
        for (RootServiceComponentResponse response : rootServiceComponents) {
            verifyResponseForAmbariServer(response);
        }
    }

    @Test(expected = ObjectNotFoundException.class)
    public void getThrowsForNonexistentComponent() throws Exception {
        // Request existent service name, and component, not belongs to requested service
        RootServiceComponentRequest request = new RootServiceComponentRequest(RootService.AMBARI.name(), "XXX");
        responseFactory.getRootServiceComponents(request);
    }
}

