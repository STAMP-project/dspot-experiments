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
package org.apache.ambari.server.topology;


import BlueprintResourceProvider.BLUEPRINT_NAME_PROPERTY_ID;
import BlueprintResourceProvider.COMPONENT_PROPERTY_ID;
import BlueprintResourceProvider.HOST_GROUP_PROPERTY_ID;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.ObjectNotFoundException;
import org.apache.ambari.server.controller.internal.BlueprintResourceProviderTest;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.orm.dao.BlueprintDAO;
import org.apache.ambari.server.orm.entities.BlueprintConfigEntity;
import org.apache.ambari.server.orm.entities.BlueprintEntity;
import org.apache.ambari.server.stack.NoSuchStackException;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * BlueprintFactory unit tests.
 */
@SuppressWarnings("unchecked")
public class BlueprintFactoryTest {
    private static final String BLUEPRINT_NAME = "test-blueprint";

    BlueprintFactory factory = new BlueprintFactory();

    Stack stack = createNiceMock(Stack.class);

    BlueprintFactory testFactory = new BlueprintFactoryTest.TestBlueprintFactory(stack);

    BlueprintDAO dao = createStrictMock(BlueprintDAO.class);

    BlueprintEntity entity = createStrictMock(BlueprintEntity.class);

    BlueprintConfigEntity configEntity = createStrictMock(BlueprintConfigEntity.class);

    // todo: implement
    // @Test
    // public void testGetBlueprint() throws Exception {
    // 
    // Collection<BlueprintConfigEntity> configs = new ArrayList<BlueprintConfigEntity>();
    // configs.add(configEntity);
    // 
    // expect(dao.findByName(BLUEPRINT_NAME)).andReturn(entity).once();
    // expect(entity.getBlueprintName()).andReturn(BLUEPRINT_NAME).atLeastOnce();
    // expect(entity.getConfigurations()).andReturn(configs).atLeastOnce();
    // 
    // replay(dao, entity);
    // 
    // Blueprint blueprint = factory.getBlueprint(BLUEPRINT_NAME);
    // 
    // 
    // }
    @Test
    public void testGetBlueprint_NotFound() throws Exception {
        expect(dao.findByName(BlueprintFactoryTest.BLUEPRINT_NAME)).andReturn(null).once();
        replay(dao, entity, configEntity);
        Assert.assertNull(factory.getBlueprint(BlueprintFactoryTest.BLUEPRINT_NAME));
    }

    @Test
    public void testCreateBlueprint() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        replay(stack, dao, entity, configEntity);
        Blueprint blueprint = testFactory.createBlueprint(props, null);
        Assert.assertEquals(BlueprintFactoryTest.BLUEPRINT_NAME, blueprint.getName());
        Assert.assertSame(stack, blueprint.getStack());
        Assert.assertEquals(2, blueprint.getHostGroups().size());
        Map<String, HostGroup> hostGroups = blueprint.getHostGroups();
        HostGroup group1 = hostGroups.get("group1");
        Assert.assertEquals("group1", group1.getName());
        Assert.assertEquals("1", group1.getCardinality());
        Collection<String> components = group1.getComponentNames();
        Assert.assertEquals(2, components.size());
        Assert.assertTrue(components.contains("component1"));
        Assert.assertTrue(components.contains("component2"));
        Collection<String> services = group1.getServices();
        Assert.assertEquals(2, services.size());
        Assert.assertTrue(services.contains("test-service1"));
        Assert.assertTrue(services.contains("test-service2"));
        Assert.assertTrue(group1.containsMasterComponent());
        // todo: add configurations/attributes to properties
        Configuration configuration = group1.getConfiguration();
        Assert.assertTrue(configuration.getProperties().isEmpty());
        Assert.assertTrue(configuration.getAttributes().isEmpty());
        HostGroup group2 = hostGroups.get("group2");
        Assert.assertEquals("group2", group2.getName());
        Assert.assertEquals("2", group2.getCardinality());
        components = group2.getComponentNames();
        Assert.assertEquals(1, components.size());
        Assert.assertTrue(components.contains("component1"));
        services = group2.getServices();
        Assert.assertEquals(1, services.size());
        Assert.assertTrue(services.contains("test-service1"));
        Assert.assertTrue(group2.containsMasterComponent());
        // todo: add configurations/attributes to properties
        // todo: test both v1 and v2 config syntax
        configuration = group2.getConfiguration();
        Assert.assertTrue(configuration.getProperties().isEmpty());
        Assert.assertTrue(configuration.getAttributes().isEmpty());
        verify(dao, entity, configEntity);
    }

    @Test(expected = NoSuchStackException.class)
    public void testCreateInvalidStack() throws Exception {
        EasyMockSupport mockSupport = new EasyMockSupport();
        StackFactory mockStackFactory = mockSupport.createMock(StackFactory.class);
        // setup mock to throw exception, to simulate invalid stack request
        expect(mockStackFactory.createStack("null", "null", null)).andThrow(new ObjectNotFoundException("Invalid Stack"));
        mockSupport.replayAll();
        BlueprintFactory factoryUnderTest = new BlueprintFactory(mockStackFactory);
        factoryUnderTest.createStack(new HashMap());
        mockSupport.verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_NoBlueprintName() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        props.remove(BLUEPRINT_NAME_PROPERTY_ID);
        replay(stack, dao, entity, configEntity);
        testFactory.createBlueprint(props, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_NoHostGroups() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        // remove all host groups
        ((Set<Map<String, Object>>) (props.get(HOST_GROUP_PROPERTY_ID))).clear();
        replay(stack, dao, entity, configEntity);
        testFactory.createBlueprint(props, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_MissingHostGroupName() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        // remove the name property for one of the host groups
        ((Set<Map<String, Object>>) (props.get(HOST_GROUP_PROPERTY_ID))).iterator().next().remove("name");
        replay(stack, dao, entity, configEntity);
        testFactory.createBlueprint(props, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_HostGroupWithNoComponents() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        // remove the components for one of the host groups
        ((Set<Map<String, Object>>) (props.get(HOST_GROUP_PROPERTY_ID))).iterator().next().remove(COMPONENT_PROPERTY_ID);
        replay(stack, dao, entity, configEntity);
        testFactory.createBlueprint(props, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_HostGroupWithInvalidComponent() throws Exception {
        Map<String, Object> props = BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next();
        // change a component name to an invalid name
        ((Set<Map<String, Object>>) (((Set<Map<String, Object>>) (props.get(HOST_GROUP_PROPERTY_ID))).iterator().next().get(COMPONENT_PROPERTY_ID))).iterator().next().put("name", "INVALID_COMPONENT");
        replay(stack, dao, entity, configEntity);
        testFactory.createBlueprint(props, null);
    }

    private class TestBlueprintFactory extends BlueprintFactory {
        private Stack stack;

        public TestBlueprintFactory(Stack stack) {
            this.stack = stack;
        }

        @Override
        protected Stack createStack(Map<String, Object> properties) throws NoSuchStackException {
            return stack;
        }
    }
}

