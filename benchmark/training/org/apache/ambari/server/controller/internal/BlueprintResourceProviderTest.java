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


import BlueprintResourceProvider.CONFIGURATION_LIST_CHECK_ERROR_MESSAGE;
import BlueprintResourceProvider.CONFIGURATION_MAP_CHECK_ERROR_MESSAGE;
import BlueprintResourceProvider.CONFIGURATION_MAP_SIZE_CHECK_ERROR_MESSAGE;
import BlueprintResourceProvider.HOST_GROUP_PROPERTY_ID;
import BlueprintResourceProvider.PROPERTIES_ATTRIBUTES_PROPERTY_ID;
import BlueprintResourceProvider.PROPERTIES_PROPERTY_ID;
import BlueprintResourceProvider.REQUEST_BODY_EMPTY_ERROR_MESSAGE;
import BlueprintResourceProvider.SCHEMA_IS_NOT_SUPPORTED_MESSAGE;
import PropertyInfo.PropertyType;
import PropertyInfo.PropertyType.PASSWORD;
import Request.REQUEST_INFO_BODY_PROPERTY;
import ResourceProviderEvent.Type.Create;
import ResourceProviderEvent.Type.Delete;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.internal.BlueprintResourceProvider.BlueprintConfigPopulationStrategy;
import org.apache.ambari.server.controller.internal.BlueprintResourceProvider.BlueprintConfigPopulationStrategyV1;
import org.apache.ambari.server.controller.internal.BlueprintResourceProvider.BlueprintConfigPopulationStrategyV2;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceAlreadyExistsException;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.spi.UnsupportedPropertyException;
import org.apache.ambari.server.orm.dao.BlueprintDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.dao.TopologyRequestDAO;
import org.apache.ambari.server.orm.entities.BlueprintConfigEntity;
import org.apache.ambari.server.orm.entities.BlueprintConfiguration;
import org.apache.ambari.server.orm.entities.BlueprintEntity;
import org.apache.ambari.server.orm.entities.BlueprintSettingEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.orm.entities.TopologyRequestEntity;
import org.apache.ambari.server.state.PropertyInfo;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.BlueprintFactory;
import org.apache.ambari.server.topology.InvalidTopologyException;
import org.apache.ambari.server.topology.SecurityConfiguration;
import org.apache.ambari.server.topology.SecurityConfigurationFactory;
import org.apache.ambari.server.topology.Setting;
import org.apache.ambari.server.utils.StageUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static BlueprintResourceProvider.BLUEPRINT_NAME_PROPERTY_ID;


/**
 * BlueprintResourceProvider unit tests.
 */
@SuppressWarnings("unchecked")
public class BlueprintResourceProviderTest {
    private static String BLUEPRINT_NAME = "test-blueprint";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final BlueprintDAO blueprintDao = createStrictMock(BlueprintDAO.class);

    private static final TopologyRequestDAO topologyRequestDAO = createMock(TopologyRequestDAO.class);

    private static final StackDAO stackDAO = createNiceMock(StackDAO.class);

    private static final BlueprintEntity entity = createStrictMock(BlueprintEntity.class);

    private static final Blueprint blueprint = createMock(Blueprint.class);

    private static final AmbariMetaInfo metaInfo = createMock(AmbariMetaInfo.class);

    private static final BlueprintFactory blueprintFactory = createMock(BlueprintFactory.class);

    private static final SecurityConfigurationFactory securityFactory = createMock(SecurityConfigurationFactory.class);

    private static final BlueprintResourceProvider provider = BlueprintResourceProviderTest.createProvider();

    private static final Gson gson = new Gson();

    @Test
    public void testCreateResources() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        expect(BlueprintResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(null, true)).andReturn(null).anyTimes();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.securityFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.securityFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testCreateResources_ReqestBodyIsEmpty() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, null);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        replay(request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        try {
            provider.createResources(request);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            // expected exception
            Assert.assertEquals(REQUEST_BODY_EMPTY_ERROR_MESSAGE, e.getMessage());
        }
        verify(request, managementController);
    }

    @Test
    public void testCreateResources_NoValidation() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        requestInfoProperties.put("validate_topology", "false");
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testCreateResources_TopologyValidationFails() throws Exception {
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expectLastCall().andThrow(new InvalidTopologyException("test"));
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.blueprint, request);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, createMock(AmbariManagementController.class));
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        try {
            provider.createResources(request);
            Assert.fail("Expected exception due to topology validation error");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request);
    }

    @Test
    public void testCreateResources_withConfiguration() throws Exception {
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        BlueprintResourceProviderTest.setConfigurationProperties(setProperties);
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testCreateResource_BlueprintFactoryThrowsException() throws Exception {
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        setProperties.iterator().next().remove(HOST_GROUP_PROPERTY_ID);
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andThrow(new IllegalArgumentException("Blueprint name must be provided"));
        expect(BlueprintResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(null, true)).andReturn(null).anyTimes();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.securityFactory, BlueprintResourceProviderTest.blueprint, request);
        // end expectations
        try {
            BlueprintResourceProviderTest.provider.createResources(request);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request);
    }

    @Test
    public void testCreateResources_withSecurityConfiguration() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = getTestRequestInfoProperties();
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        SecurityConfiguration securityConfiguration = SecurityConfiguration.withReference("testRef");
        // set expectations
        expect(BlueprintResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(EasyMock.anyObject(), anyBoolean())).andReturn(securityConfiguration).once();
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), securityConfiguration)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.securityFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testGetResourcesNoPredicate() throws NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        Request request = createNiceMock(Request.class);
        BlueprintEntity entity = createEntity(BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next());
        List<BlueprintEntity> results = new ArrayList<>();
        results.add(entity);
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintDao.findAll()).andReturn(results);
        replay(BlueprintResourceProviderTest.blueprintDao, request);
        Set<Resource> setResults = BlueprintResourceProviderTest.provider.getResources(request, null);
        Assert.assertEquals(1, setResults.size());
        verify(BlueprintResourceProviderTest.blueprintDao);
        validateResource(setResults.iterator().next(), false);
    }

    @Test
    public void testGetResourcesNoPredicate_withConfiguration() throws AmbariException, NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        StackInfo info = createMock(StackInfo.class);
        expect(info.getConfigPropertiesTypes("core-site")).andReturn(new HashMap()).anyTimes();
        expect(BlueprintResourceProviderTest.metaInfo.getStack("test-stack-name", "test-stack-version")).andReturn(info).anyTimes();
        replay(info, BlueprintResourceProviderTest.metaInfo);
        Request request = createNiceMock(Request.class);
        Set<Map<String, Object>> testProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        BlueprintResourceProviderTest.setConfigurationProperties(testProperties);
        BlueprintEntity entity = createEntity(testProperties.iterator().next());
        List<BlueprintEntity> results = new ArrayList<>();
        results.add(entity);
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintDao.findAll()).andReturn(results);
        replay(BlueprintResourceProviderTest.blueprintDao, request);
        Set<Resource> setResults = BlueprintResourceProviderTest.provider.getResources(request, null);
        Assert.assertEquals(1, setResults.size());
        verify(BlueprintResourceProviderTest.blueprintDao);
        validateResource(setResults.iterator().next(), true);
    }

    @Test
    public void testDeleteResources() throws NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        BlueprintEntity blueprintEntity = createEntity(BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next());
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(blueprintEntity);
        BlueprintResourceProviderTest.blueprintDao.removeByName(blueprintEntity.getBlueprintName());
        expectLastCall();
        expect(BlueprintResourceProviderTest.topologyRequestDAO.findAllProvisionRequests()).andReturn(ImmutableList.of());
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.topologyRequestDAO);
        Predicate predicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(BLUEPRINT_NAME_PROPERTY_ID, BlueprintResourceProviderTest.BLUEPRINT_NAME);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        BlueprintResourceProviderTest.provider.addObserver(observer);
        BlueprintResourceProviderTest.provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Delete, lastEvent.getType());
        Assert.assertNotNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteResources_clusterAlreadyProvisioned() throws NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        BlueprintEntity blueprintEntity = createEntity(BlueprintResourceProviderTest.getBlueprintTestProperties().iterator().next());
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(blueprintEntity);
        BlueprintResourceProviderTest.blueprintDao.removeByName(blueprintEntity.getBlueprintName());
        expectLastCall();
        TopologyRequestEntity topologyRequestEntity = new TopologyRequestEntity();
        topologyRequestEntity.setBlueprintName(BlueprintResourceProviderTest.BLUEPRINT_NAME);
        expect(BlueprintResourceProviderTest.topologyRequestDAO.findAllProvisionRequests()).andReturn(ImmutableList.of(topologyRequestEntity));
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.topologyRequestDAO);
        Predicate predicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(BLUEPRINT_NAME_PROPERTY_ID, BlueprintResourceProviderTest.BLUEPRINT_NAME);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        BlueprintResourceProviderTest.provider.addObserver(observer);
        BlueprintResourceProviderTest.provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
    }

    @Test
    public void testCreateResources_withEmptyConfiguration() throws Exception {
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        BlueprintResourceProviderTest.setConfigurationProperties(setProperties);
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Map<String, String> requestInfoProperties = new HashMap<>();
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, "{\"configurations\":[]}");
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testCreateResources_withSingleConfigurationType() throws Exception {
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        BlueprintResourceProviderTest.setConfigurationProperties(setProperties);
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Map<String, String> requestInfoProperties = new HashMap<>();
        Map<String, Set<HashMap<String, String>>> settingProperties = getSettingProperties();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, "{\"configurations\":[{\"configuration-type\":{\"properties\":{\"property\":\"value\"}}}]}");
        Request request = createMock(Request.class);
        Setting setting = createStrictMock(Setting.class);
        // set expectations
        expect(BlueprintResourceProviderTest.blueprintFactory.createBlueprint(setProperties.iterator().next(), null)).andReturn(BlueprintResourceProviderTest.blueprint).once();
        BlueprintResourceProviderTest.blueprint.validateRequiredProperties();
        BlueprintResourceProviderTest.blueprint.validateTopology();
        expect(BlueprintResourceProviderTest.blueprint.getSetting()).andReturn(setting).anyTimes();
        expect(setting.getProperties()).andReturn(settingProperties).anyTimes();
        expect(BlueprintResourceProviderTest.blueprint.toEntity()).andReturn(BlueprintResourceProviderTest.entity);
        expect(BlueprintResourceProviderTest.blueprint.getName()).andReturn(BlueprintResourceProviderTest.BLUEPRINT_NAME).atLeastOnce();
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        expect(BlueprintResourceProviderTest.blueprintDao.findByName(BlueprintResourceProviderTest.BLUEPRINT_NAME)).andReturn(null);
        BlueprintResourceProviderTest.blueprintDao.create(BlueprintResourceProviderTest.entity);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.metaInfo, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.blueprint, setting, request, managementController);
        // end expectations
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Resource.Type.Blueprint, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Blueprint, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.entity, BlueprintResourceProviderTest.blueprintFactory, BlueprintResourceProviderTest.metaInfo, request, managementController);
    }

    @Test
    public void testCreateResources_wrongConfigurationsStructure_withWrongConfigMapSize() throws NoSuchParentResourceException, ResourceAlreadyExistsException, SystemException, UnsupportedPropertyException {
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = new HashMap<>();
        String configurationData = "{\"configurations\":[{\"config-type1\":{\"properties\" :{\"property\":\"property-value\"}}," + ("\"config-type2\" : {\"properties_attributes\" : {\"property\" : \"property-value\"}, \"properties\" : {\"property\" : \"property-value\"}}}" + "]}");
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, configurationData);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
        // end expectations
        try {
            BlueprintResourceProviderTest.provider.createResources(request);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            // expected exception
            Assert.assertEquals(CONFIGURATION_MAP_SIZE_CHECK_ERROR_MESSAGE, e.getMessage());
        }
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
    }

    @Test
    public void testCreateResources_wrongConfigurationStructure_withoutConfigMaps() throws NoSuchParentResourceException, ResourceAlreadyExistsException, SystemException, UnsupportedPropertyException {
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = new HashMap<>();
        String configurationData = "{\"configurations\":[\"config-type1\", \"config-type2\"]}";
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, configurationData);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
        // end expectations
        try {
            BlueprintResourceProviderTest.provider.createResources(request);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            // expected exception
            Assert.assertEquals(CONFIGURATION_MAP_CHECK_ERROR_MESSAGE, e.getMessage());
        }
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
    }

    @Test
    public void testCreateResources_wrongConfigurationStructure_withoutConfigsList() throws NoSuchParentResourceException, ResourceAlreadyExistsException, SystemException, UnsupportedPropertyException {
        Request request = createMock(Request.class);
        Set<Map<String, Object>> setProperties = BlueprintResourceProviderTest.getBlueprintTestProperties();
        Map<String, String> requestInfoProperties = new HashMap<>();
        String configurationData = "{\"configurations\":{\"config-type1\": \"properties\", \"config-type2\": \"properties\"}}";
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, configurationData);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        expect(request.getRequestInfoProperties()).andReturn(requestInfoProperties);
        replay(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
        // end expectations
        try {
            BlueprintResourceProviderTest.provider.createResources(request);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            // expected exception
            Assert.assertEquals(CONFIGURATION_LIST_CHECK_ERROR_MESSAGE, e.getMessage());
        }
        verify(BlueprintResourceProviderTest.blueprintDao, BlueprintResourceProviderTest.metaInfo, request);
    }

    @Test
    public void testPopulateConfigurationEntity_oldSchema() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("global/property1", "val1");
        configuration.put("global/property2", "val2");
        BlueprintConfiguration config = new BlueprintConfigEntity();
        BlueprintResourceProviderTest.provider.populateConfigurationEntity(configuration, config);
        Assert.assertNotNull(config.getConfigData());
        Assert.assertNotNull(config.getConfigAttributes());
        Map<?, ?> configData = StageUtils.getGson().fromJson(config.getConfigData(), Map.class);
        Map<?, Map<?, ?>> configAttrs = StageUtils.getGson().fromJson(config.getConfigAttributes(), Map.class);
        Assert.assertNotNull(configData);
        Assert.assertNotNull(configAttrs);
        Assert.assertEquals(2, configData.size());
        Assert.assertTrue(configData.containsKey("property1"));
        Assert.assertTrue(configData.containsKey("property2"));
        Assert.assertEquals("val1", configData.get("property1"));
        Assert.assertEquals("val2", configData.get("property2"));
        Assert.assertEquals(0, configAttrs.size());
    }

    @Test
    public void testPopulateConfigurationEntity_newSchema() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("global/properties/property1", "val1");
        configuration.put("global/properties/property2", "val2");
        configuration.put("global/properties_attributes/final/property1", "true");
        configuration.put("global/properties_attributes/final/property2", "false");
        configuration.put("global/properties_attributes/deletable/property1", "true");
        BlueprintConfiguration config = new BlueprintConfigEntity();
        BlueprintResourceProviderTest.provider.populateConfigurationEntity(configuration, config);
        Assert.assertNotNull(config.getConfigData());
        Assert.assertNotNull(config.getConfigAttributes());
        Map<?, ?> configData = StageUtils.getGson().fromJson(config.getConfigData(), Map.class);
        Map<?, Map<?, ?>> configAttrs = StageUtils.getGson().fromJson(config.getConfigAttributes(), Map.class);
        Assert.assertNotNull(configData);
        Assert.assertNotNull(configAttrs);
        Assert.assertEquals(2, configData.size());
        Assert.assertTrue(configData.containsKey("property1"));
        Assert.assertTrue(configData.containsKey("property2"));
        Assert.assertEquals("val1", configData.get("property1"));
        Assert.assertEquals("val2", configData.get("property2"));
        Assert.assertEquals(2, configAttrs.size());
        Assert.assertTrue(configAttrs.containsKey("final"));
        Assert.assertTrue(configAttrs.containsKey("deletable"));
        Map<?, ?> finalAttrs = configAttrs.get("final");
        Assert.assertNotNull(finalAttrs);
        Assert.assertEquals(2, finalAttrs.size());
        Assert.assertTrue(finalAttrs.containsKey("property1"));
        Assert.assertTrue(finalAttrs.containsKey("property2"));
        Assert.assertEquals("true", finalAttrs.get("property1"));
        Assert.assertEquals("false", finalAttrs.get("property2"));
        Map<?, ?> deletableAttrs = configAttrs.get("deletable");
        Assert.assertNotNull(deletableAttrs);
        Assert.assertEquals(1, deletableAttrs.size());
        Assert.assertTrue(deletableAttrs.containsKey("property1"));
        Assert.assertEquals("true", deletableAttrs.get("property1"));
    }

    @Test
    public void testPopulateConfigurationEntity_configIsNull() throws Exception {
        Map<String, String> configuration = null;
        BlueprintConfiguration config = new BlueprintConfigEntity();
        BlueprintResourceProviderTest.provider.populateConfigurationEntity(configuration, config);
        Assert.assertNotNull(config.getConfigAttributes());
        Assert.assertNotNull(config.getConfigData());
    }

    @Test
    public void testPopulateConfigurationEntity_configIsEmpty() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        BlueprintConfiguration config = new BlueprintConfigEntity();
        BlueprintResourceProviderTest.provider.populateConfigurationEntity(configuration, config);
        Assert.assertNotNull(config.getConfigAttributes());
        Assert.assertNotNull(config.getConfigData());
    }

    @Test
    public void testDecidePopulationStrategy_configIsEmpty() throws Exception {
        Map<String, String> configMap = new HashMap<>();
        BlueprintConfigPopulationStrategy provisioner = BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
        Assert.assertNotNull(provisioner);
        Assert.assertTrue((provisioner instanceof BlueprintConfigPopulationStrategyV2));
    }

    @Test
    public void testDecidePopulationStrategy_configIsNull() throws Exception {
        Map<String, String> configMap = null;
        BlueprintConfigPopulationStrategy provisioner = BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
        Assert.assertNotNull(provisioner);
        Assert.assertTrue((provisioner instanceof BlueprintConfigPopulationStrategyV2));
    }

    @Test
    public void testDecidePopulationStrategy_withOldSchema() throws Exception {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("global/hive_database", "db");
        BlueprintConfigPopulationStrategy provisioner = BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
        Assert.assertNotNull(provisioner);
        Assert.assertTrue((provisioner instanceof BlueprintConfigPopulationStrategyV1));
    }

    @Test
    public void testDecidePopulationStrategy_withNewSchema_attributes() throws Exception {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("global/properties_attributes/final/foo_contact", "true");
        BlueprintConfigPopulationStrategy provisioner = BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
        Assert.assertNotNull(provisioner);
        Assert.assertTrue((provisioner instanceof BlueprintConfigPopulationStrategyV2));
    }

    @Test
    public void testDecidePopulationStrategy_withNewSchema_properties() throws Exception {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("global/properties/foo_contact", "foo@ffl.dsfds");
        BlueprintConfigPopulationStrategy provisioner = BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
        Assert.assertNotNull(provisioner);
        Assert.assertTrue((provisioner instanceof BlueprintConfigPopulationStrategyV2));
    }

    @Test
    public void testDecidePopulationStrategy_unsupportedSchema() throws Exception {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("global/properties/lot/foo_contact", "foo@ffl.dsfds");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SCHEMA_IS_NOT_SUPPORTED_MESSAGE);
        BlueprintResourceProviderTest.provider.decidePopulationStrategy(configMap);
    }

    @Test
    public void testPopulateConfigurationList() throws Exception {
        StackEntity stackEntity = new StackEntity();
        stackEntity.setStackName("test-stack-name");
        stackEntity.setStackVersion("test-stack-version");
        BlueprintEntity entity = createMock(BlueprintEntity.class);
        expect(entity.getStack()).andReturn(stackEntity).anyTimes();
        HashMap<PropertyInfo.PropertyType, Set<String>> pwdProperties = new HashMap<PropertyInfo.PropertyType, Set<String>>() {
            {
                put(PASSWORD, new HashSet<String>() {
                    {
                        add("test.password");
                    }
                });
            }
        };
        StackInfo info = createMock(StackInfo.class);
        expect(info.getConfigPropertiesTypes("type1")).andReturn(new HashMap()).anyTimes();
        expect(info.getConfigPropertiesTypes("type2")).andReturn(new HashMap()).anyTimes();
        expect(info.getConfigPropertiesTypes("type3")).andReturn(pwdProperties).anyTimes();
        expect(BlueprintResourceProviderTest.metaInfo.getStack("test-stack-name", "test-stack-version")).andReturn(info).anyTimes();
        replay(info, BlueprintResourceProviderTest.metaInfo, entity);
        // attributes is null
        BlueprintConfigEntity config1 = new BlueprintConfigEntity();
        config1.setType("type1");
        config1.setConfigData("{\"key1\":\"value1\"}");
        config1.setBlueprintEntity(entity);
        // attributes is empty
        BlueprintConfigEntity config2 = new BlueprintConfigEntity();
        config2.setType("type2");
        config2.setConfigData("{\"key2\":\"value2\"}");
        config2.setConfigAttributes("{}");
        config2.setBlueprintEntity(entity);
        // attributes is provided
        BlueprintConfigEntity config3 = new BlueprintConfigEntity();
        config3.setType("type3");
        config3.setConfigData("{\"key3\":\"value3\",\"key4\":\"value4\",\"test.password\":\"pwdValue\"}");
        config3.setConfigAttributes("{\"final\":{\"key3\":\"attrValue1\",\"key4\":\"attrValue2\"}}");
        config3.setBlueprintEntity(entity);
        List<Map<String, Map<String, Object>>> configs = BlueprintResourceProviderTest.provider.populateConfigurationList(Arrays.asList(config1, config2, config3));
        Assert.assertNotNull(configs);
        Assert.assertEquals(3, configs.size());
        Map<String, Map<String, Object>> configuration1 = configs.get(0);
        Assert.assertNotNull(configuration1);
        Assert.assertEquals(1, configuration1.size());
        Assert.assertTrue(configuration1.containsKey("type1"));
        Map<String, Object> typeConfig1 = configuration1.get("type1");
        Assert.assertNotNull(typeConfig1);
        Assert.assertEquals(1, typeConfig1.size());
        Assert.assertTrue(typeConfig1.containsKey(PROPERTIES_PROPERTY_ID));
        Map<String, String> confProperties1 = ((Map<String, String>) (typeConfig1.get(PROPERTIES_PROPERTY_ID)));
        Assert.assertNotNull(confProperties1);
        Assert.assertEquals(1, confProperties1.size());
        Assert.assertEquals("value1", confProperties1.get("key1"));
        Map<String, Map<String, Object>> configuration2 = configs.get(1);
        Assert.assertNotNull(configuration2);
        Assert.assertEquals(1, configuration2.size());
        Assert.assertTrue(configuration2.containsKey("type2"));
        Map<String, Object> typeConfig2 = configuration2.get("type2");
        Assert.assertNotNull(typeConfig2);
        Assert.assertEquals(1, typeConfig2.size());
        Assert.assertTrue(typeConfig2.containsKey(PROPERTIES_PROPERTY_ID));
        Map<String, String> confProperties2 = ((Map<String, String>) (typeConfig2.get(PROPERTIES_PROPERTY_ID)));
        Assert.assertNotNull(confProperties2);
        Assert.assertEquals(1, confProperties2.size());
        Assert.assertEquals("value2", confProperties2.get("key2"));
        Map<String, Map<String, Object>> configuration3 = configs.get(2);
        Assert.assertNotNull(configuration3);
        Assert.assertEquals(1, configuration3.size());
        Assert.assertTrue(configuration3.containsKey("type3"));
        Map<String, Object> typeConfig3 = configuration3.get("type3");
        Assert.assertNotNull(typeConfig3);
        Assert.assertEquals(2, typeConfig3.size());
        Assert.assertTrue(typeConfig3.containsKey(PROPERTIES_PROPERTY_ID));
        Map<String, String> confProperties3 = ((Map<String, String>) (typeConfig3.get(PROPERTIES_PROPERTY_ID)));
        Assert.assertNotNull(confProperties3);
        Assert.assertEquals(3, confProperties3.size());
        Assert.assertEquals("value3", confProperties3.get("key3"));
        Assert.assertEquals("value4", confProperties3.get("key4"));
        Assert.assertEquals("SECRET:type3:-1:test.password", confProperties3.get("test.password"));
        Assert.assertTrue(typeConfig3.containsKey(PROPERTIES_ATTRIBUTES_PROPERTY_ID));
        Map<String, Map<String, String>> confAttributes3 = ((Map<String, Map<String, String>>) (typeConfig3.get(PROPERTIES_ATTRIBUTES_PROPERTY_ID)));
        Assert.assertNotNull(confAttributes3);
        Assert.assertEquals(1, confAttributes3.size());
        Assert.assertTrue(confAttributes3.containsKey("final"));
        Map<String, String> finalAttrs = confAttributes3.get("final");
        Assert.assertEquals(2, finalAttrs.size());
        Assert.assertEquals("attrValue1", finalAttrs.get("key3"));
        Assert.assertEquals("attrValue2", finalAttrs.get("key4"));
    }

    @Test
    public void testPopulateSettingList() throws Exception {
        StackEntity stackEntity = new StackEntity();
        stackEntity.setStackName("test-stack-name");
        stackEntity.setStackVersion("test-stack-version");
        BlueprintEntity entity = createMock(BlueprintEntity.class);
        expect(entity.getStack()).andReturn(stackEntity).anyTimes();
        HashMap<PropertyInfo.PropertyType, Set<String>> pwdProperties = new HashMap<PropertyInfo.PropertyType, Set<String>>() {
            {
                put(PASSWORD, new HashSet<String>() {
                    {
                        add("test.password");
                    }
                });
            }
        };
        StackInfo info = createMock(StackInfo.class);
        expect(info.getConfigPropertiesTypes("type1")).andReturn(new HashMap()).anyTimes();
        expect(info.getConfigPropertiesTypes("type2")).andReturn(new HashMap()).anyTimes();
        expect(info.getConfigPropertiesTypes("type3")).andReturn(pwdProperties).anyTimes();
        expect(BlueprintResourceProviderTest.metaInfo.getStack("test-stack-name", "test-stack-version")).andReturn(info).anyTimes();
        replay(info, BlueprintResourceProviderTest.metaInfo, entity);
        // Blueprint setting entities
        // Global recovery setting
        BlueprintSettingEntity settingEntity1 = new BlueprintSettingEntity();
        settingEntity1.setSettingName("recovery_settings");
        settingEntity1.setSettingData("[{\"recovery_enabled\":\"true\"}]");
        settingEntity1.setBlueprintEntity(entity);
        // Service exceptions setting
        BlueprintSettingEntity settingEntity2 = new BlueprintSettingEntity();
        settingEntity2.setSettingName("service_settings");
        settingEntity2.setSettingData(("[{\"name\":\"HDFS\", \"recovery_enabled\":\"false\"}, " + "{\"name\":\"ZOOKEEPER\", \"recovery_enabled\":\"false\"}]"));
        settingEntity2.setBlueprintEntity(entity);
        // Service component exceptions setting
        BlueprintSettingEntity settingEntity3 = new BlueprintSettingEntity();
        settingEntity3.setSettingName("component_settings");
        settingEntity3.setSettingData(("[{\"name\":\"METRICS_MONITOR\", \"recovery_enabled\":\"false\"}," + "{\"name\":\"KAFKA_CLIENT\", \"recovery_enabled\":\"false\"}]"));
        settingEntity3.setBlueprintEntity(entity);
        List<BlueprintSettingEntity> settingEntities = new ArrayList();
        settingEntities.add(settingEntity1);
        settingEntities.add(settingEntity2);
        settingEntities.add(settingEntity3);
        List<Map<String, Object>> settings = BlueprintResourceProviderTest.provider.populateSettingList(settingEntities);
        Assert.assertNotNull(settings);
        Assert.assertEquals(settingEntities.size(), settings.size());
        // Verify global recovery setting
        Map<String, Object> setting1 = settings.get(0);
        Assert.assertNotNull(setting1);
        Assert.assertEquals(1, setting1.size());
        Assert.assertTrue(setting1.containsKey("recovery_settings"));
        List<Map<String, String>> setting1value = ((List<Map<String, String>>) (setting1.get("recovery_settings")));
        Assert.assertNotNull(setting1value);
        Assert.assertEquals(1, setting1value.size());
        Assert.assertTrue(setting1value.get(0).containsKey("recovery_enabled"));
        Assert.assertEquals(setting1value.get(0).get("recovery_enabled"), "true");
        // Verify service exceptions
        Map<String, Object> setting2 = settings.get(1);
        Assert.assertNotNull(setting2);
        Assert.assertEquals(1, setting2.size());
        Assert.assertTrue(setting2.containsKey("service_settings"));
        List<Map<String, String>> setting2value = ((List<Map<String, String>>) (setting2.get("service_settings")));
        Assert.assertNotNull(setting2value);
        Assert.assertEquals(2, setting2value.size());
        // first service exception is HDFS
        Assert.assertTrue(setting2value.get(0).containsKey("name"));
        Assert.assertEquals(setting2value.get(0).get("name"), "HDFS");
        Assert.assertTrue(setting2value.get(0).containsKey("recovery_enabled"));
        Assert.assertEquals(setting2value.get(0).get("recovery_enabled"), "false");
        // second service exception is ZOOKEEPER
        Assert.assertTrue(setting2value.get(1).containsKey("name"));
        Assert.assertEquals(setting2value.get(1).get("name"), "ZOOKEEPER");
        Assert.assertTrue(setting2value.get(1).containsKey("recovery_enabled"));
        Assert.assertEquals(setting2value.get(1).get("recovery_enabled"), "false");
        // Verify service component exceptions
        Map<String, Object> setting3 = settings.get(2);
        Assert.assertNotNull(setting3);
        Assert.assertEquals(1, setting3.size());
        Assert.assertTrue(setting3.containsKey("component_settings"));
        List<Map<String, String>> setting3value = ((List<Map<String, String>>) (setting3.get("component_settings")));
        Assert.assertNotNull(setting3value);
        Assert.assertEquals(2, setting3value.size());
        // first service component exception is METRICS_MONITOR
        Assert.assertTrue(setting3value.get(0).containsKey("name"));
        Assert.assertEquals(setting3value.get(0).get("name"), "METRICS_MONITOR");
        Assert.assertTrue(setting3value.get(0).containsKey("recovery_enabled"));
        Assert.assertEquals(setting3value.get(0).get("recovery_enabled"), "false");
        // second service component exception is KAFKA_CLIENT
        Assert.assertTrue(setting3value.get(1).containsKey("name"));
        Assert.assertEquals(setting3value.get(1).get("name"), "KAFKA_CLIENT");
        Assert.assertTrue(setting3value.get(1).containsKey("recovery_enabled"));
        Assert.assertEquals(setting3value.get(1).get("recovery_enabled"), "false");
    }
}

