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
package org.apache.ambari.server.api.resources;


import BaseResourceDefinition.DirectiveType;
import BaseResourceDefinition.DirectiveType.CREATE;
import BaseResourceDefinition.DirectiveType.DELETE;
import BaseResourceDefinition.DirectiveType.READ;
import BaseResourceDefinition.DirectiveType.UPDATE;
import Resource.Type;
import ResourceDefinition.PostProcessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.handlers.BaseManagementHandler;
import org.apache.ambari.server.api.query.render.DefaultRenderer;
import org.apache.ambari.server.api.query.render.MinimalRenderer;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.internal.AbstractControllerResourceProvider;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.state.Service;
import org.junit.Assert;
import org.junit.Test;


/**
 * BaseResourceDefinition tests.
 */
public class BaseResourceDefinitionTest {
    @Test
    public void testGetPostProcessors() throws AmbariException {
        BaseResourceDefinition resourceDefinition = getResourceDefinition();
        List<ResourceDefinition.PostProcessor> postProcessors = resourceDefinition.getPostProcessors();
        Assert.assertEquals(1, postProcessors.size());
        ResourceDefinition.PostProcessor processor = postProcessors.iterator().next();
        Resource service = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.Service);
        service.setProperty("ServiceInfo/service_name", "Service1");
        TreeNode<Resource> parentNode = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, "services");
        TreeNode<Resource> serviceNode = new org.apache.ambari.server.api.util.TreeNodeImpl(parentNode, service, "service1");
        parentNode.setProperty("isCollection", "true");
        ResourceProviderFactory factory = createMock(ResourceProviderFactory.class);
        MaintenanceStateHelper maintenanceStateHelper = createNiceMock(MaintenanceStateHelper.class);
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);
        expect(maintenanceStateHelper.isOperationAllowed(anyObject(Type.class), anyObject(Service.class))).andReturn(true).anyTimes();
        ResourceProvider serviceResourceProvider = new org.apache.ambari.server.controller.internal.ServiceResourceProvider(managementController, maintenanceStateHelper, repositoryVersionDAO);
        expect(factory.getServiceResourceProvider(anyObject(AmbariManagementController.class))).andReturn(serviceResourceProvider);
        AbstractControllerResourceProvider.init(factory);
        replay(factory, managementController, maintenanceStateHelper);
        processor.process(null, serviceNode, "http://c6401.ambari.apache.org:8080/api/v1/clusters/c1/services");
        String href = serviceNode.getStringProperty("href");
        Assert.assertEquals("http://c6401.ambari.apache.org:8080/api/v1/clusters/c1/services/Service1", href);
        Resource configGroup = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.ConfigGroup);
        configGroup.setProperty("ConfigGroup/id", "2");
        TreeNode<Resource> resourcesNode = new org.apache.ambari.server.api.util.TreeNodeImpl(null, null, BaseManagementHandler.RESOURCES_NODE_NAME);
        TreeNode<Resource> configGroupNode = new org.apache.ambari.server.api.util.TreeNodeImpl(resourcesNode, configGroup, "configGroup1");
        resourcesNode.setProperty("isCollection", "true");
        processor.process(null, configGroupNode, "http://c6401.ambari.apache.org:8080/api/v1/clusters/c1/config_groups");
        href = configGroupNode.getStringProperty("href");
        Assert.assertEquals("http://c6401.ambari.apache.org:8080/api/v1/clusters/c1/config_groups/2", href);
    }

    @Test
    public void testGetRenderer() {
        ResourceDefinition resource = getResourceDefinition();
        Assert.assertTrue(((resource.getRenderer(null)) instanceof DefaultRenderer));
        Assert.assertTrue(((resource.getRenderer("default")) instanceof DefaultRenderer));
        Assert.assertTrue(((resource.getRenderer("minimal")) instanceof MinimalRenderer));
        try {
            resource.getRenderer("foo");
            Assert.fail("Should have thrown an exception due to invalid renderer type");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid renderer name for resource of type Service", e.getMessage());
        }
    }

    @Test
    public void testReadDirectives() {
        ResourceDefinition resource = getResourceDefinition();
        Assert.assertEquals(Collections.emptySet(), resource.getReadDirectives());
        Map<BaseResourceDefinition.DirectiveType, List<String>> directives = new HashMap<>();
        directives.put(DELETE, Arrays.asList("do_something_delete", "do_something_else_delete"));
        directives.put(READ, Arrays.asList("do_something_get", "do_something_else_get"));
        directives.put(CREATE, Arrays.asList("do_something_post", "do_something_else_post"));
        directives.put(UPDATE, Arrays.asList("do_something_put", "do_something_else_put"));
        resource = getResourceDefinition(directives);
        Assert.assertEquals(new HashSet<String>() {
            {
                add("do_something_delete");
                add("do_something_else_delete");
            }
        }, resource.getDeleteDirectives());
        Assert.assertEquals(new HashSet<String>() {
            {
                add("do_something_get");
                add("do_something_else_get");
            }
        }, resource.getReadDirectives());
        Assert.assertEquals(new HashSet<String>() {
            {
                add("do_something_post");
                add("do_something_else_post");
            }
        }, resource.getCreateDirectives());
        Assert.assertEquals(new HashSet<String>() {
            {
                add("do_something_put");
                add("do_something_else_put");
            }
        }, resource.getUpdateDirectives());
    }
}

