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
package org.apache.ambari.server.controller.internal;


import ValidationResourceProvider.VALIDATION_ID_PROPERTY_ID;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorHelper;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorRequest;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorResponse.Version;
import org.apache.ambari.server.api.services.stackadvisor.validations.ValidationResponse;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.state.Clusters;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ValidationResourceProviderTest {
    @Test
    public void testCreateResources_checkRequestId() throws Exception {
        Map<Resource.Type, String> keyPropertyIds = Collections.emptyMap();
        Set<String> propertyIds = Collections.singleton(VALIDATION_ID_PROPERTY_ID);
        AmbariManagementController ambariManagementController = Mockito.mock(AmbariManagementController.class);
        ValidationResourceProvider provider = Mockito.spy(new ValidationResourceProvider(ambariManagementController));
        StackAdvisorRequest stackAdvisorRequest = Mockito.mock(StackAdvisorRequest.class);
        Request request = Mockito.mock(Request.class);
        Mockito.doReturn(stackAdvisorRequest).when(provider).prepareStackAdvisorRequest(request);
        StackAdvisorHelper saHelper = Mockito.mock(StackAdvisorHelper.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        ValidationResponse response = Mockito.mock(ValidationResponse.class);
        Version version = Mockito.mock(Version.class);
        Mockito.doReturn(3).when(response).getId();
        Mockito.doReturn(version).when(response).getVersion();
        Mockito.doReturn(response).when(saHelper).validate(ArgumentMatchers.any(StackAdvisorRequest.class));
        ValidationResourceProvider.init(saHelper, configuration, Mockito.mock(Clusters.class), Mockito.mock(AmbariMetaInfo.class));
        RequestStatus status = provider.createResources(request);
        Set<Resource> associatedResources = status.getAssociatedResources();
        Assert.assertNotNull(associatedResources);
        Assert.assertEquals(1, associatedResources.size());
        Resource resource = associatedResources.iterator().next();
        Object requestId = resource.getPropertyValue(VALIDATION_ID_PROPERTY_ID);
        Assert.assertNotNull(requestId);
        Assert.assertEquals(3, requestId);
    }
}

