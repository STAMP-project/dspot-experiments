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


import ActionResourceProvider.ACTION_NAME_PROPERTY_ID;
import ActionResourceProvider.ACTION_TYPE_PROPERTY_ID;
import ActionResourceProvider.DEFAULT_TIMEOUT_PROPERTY_ID;
import ActionResourceProvider.DESCRIPTION_PROPERTY_ID;
import ActionResourceProvider.INPUTS_PROPERTY_ID;
import ActionResourceProvider.TARGET_COMPONENT_PROPERTY_ID;
import ActionResourceProvider.TARGET_HOST_PROPERTY_ID;
import ActionResourceProvider.TARGET_SERVICE_PROPERTY_ID;
import Resource.Type;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ambari.server.actionmanager.ActionType;
import org.apache.ambari.server.actionmanager.TargetHostType;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.ActionResponse;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.customactions.ActionDefinition;
import org.junit.Assert;
import org.junit.Test;


public class ActionResourceProviderTest {
    private Injector injector;

    @Test
    public void testGetResources() throws Exception {
        Resource.Type type = Type.Action;
        AmbariMetaInfo am = createNiceMock(AmbariMetaInfo.class);
        AmbariManagementController managementController = createNiceMock(AmbariManagementController.class);
        expect(managementController.getAmbariMetaInfo()).andReturn(am).anyTimes();
        List<ActionDefinition> allDefinition = new ArrayList<>();
        allDefinition.add(new ActionDefinition("a1", ActionType.SYSTEM, "fileName", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        allDefinition.add(new ActionDefinition("a2", ActionType.SYSTEM, "fileName", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        allDefinition.add(new ActionDefinition("a3", ActionType.SYSTEM, "fileName", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null));
        Set<ActionResponse> allResponse = new HashSet<>();
        for (ActionDefinition definition : allDefinition) {
            allResponse.add(definition.convertToResponse());
        }
        ActionDefinition namedDefinition = new ActionDefinition("a1", ActionType.SYSTEM, "fileName", "HDFS", "DATANODE", "Does file exist", TargetHostType.ANY, Short.valueOf("100"), null);
        Set<ActionResponse> nameResponse = new HashSet<>();
        nameResponse.add(namedDefinition.convertToResponse());
        expect(am.getAllActionDefinition()).andReturn(allDefinition).once();
        expect(am.getActionDefinition("a1")).andReturn(namedDefinition).once();
        replay(managementController, am);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(ACTION_NAME_PROPERTY_ID);
        propertyIds.add(ACTION_TYPE_PROPERTY_ID);
        propertyIds.add(DEFAULT_TIMEOUT_PROPERTY_ID);
        propertyIds.add(DESCRIPTION_PROPERTY_ID);
        propertyIds.add(INPUTS_PROPERTY_ID);
        propertyIds.add(TARGET_COMPONENT_PROPERTY_ID);
        propertyIds.add(TARGET_HOST_PROPERTY_ID);
        propertyIds.add(TARGET_SERVICE_PROPERTY_ID);
        // create the request
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // get all ... no predicate
        Set<Resource> resources = provider.getResources(request, null);
        Assert.assertEquals(allResponse.size(), resources.size());
        for (Resource resource : resources) {
            String actionName = ((String) (resource.getPropertyValue(ACTION_NAME_PROPERTY_ID)));
            String actionType = ((String) (resource.getPropertyValue(ACTION_TYPE_PROPERTY_ID)));
            String defaultTimeout = ((String) (resource.getPropertyValue(DEFAULT_TIMEOUT_PROPERTY_ID)));
            String description = ((String) (resource.getPropertyValue(DESCRIPTION_PROPERTY_ID)));
            String inputs = ((String) (resource.getPropertyValue(INPUTS_PROPERTY_ID)));
            String comp = ((String) (resource.getPropertyValue(TARGET_COMPONENT_PROPERTY_ID)));
            String svc = ((String) (resource.getPropertyValue(TARGET_SERVICE_PROPERTY_ID)));
            String host = ((String) (resource.getPropertyValue(TARGET_HOST_PROPERTY_ID)));
            Assert.assertTrue(allResponse.contains(new ActionResponse(actionName, actionType, inputs, svc, comp, description, host, defaultTimeout)));
        }
        // get actions named a1
        Predicate predicate = new PredicateBuilder().property(ACTION_NAME_PROPERTY_ID).equals("a1").toPredicate();
        resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("a1", resources.iterator().next().getPropertyValue(ACTION_NAME_PROPERTY_ID));
        // verify
        verify(managementController);
    }
}

