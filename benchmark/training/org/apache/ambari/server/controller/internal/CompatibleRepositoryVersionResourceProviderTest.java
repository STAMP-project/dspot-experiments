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


import CompatibleRepositoryVersionResourceProvider.REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID;
import CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_SERVICES;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_ID_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID;
import UpgradeType.NON_ROLLING;
import UpgradeType.ROLLING;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.predicate.OrPredicate;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepoOsEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.repository.AvailableService;
import org.apache.ambari.server.state.repository.ManifestServiceInfo;
import org.apache.ambari.server.state.repository.VersionDefinitionXml;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * CompatibleRepositoryVersionResourceProvider tests.
 */
public class CompatibleRepositoryVersionResourceProviderTest {
    private static Injector injector;

    private static final List<RepoOsEntity> osRedhat6 = new ArrayList<>();

    {
        RepoOsEntity repoOsEntity = new RepoOsEntity();
        repoOsEntity.setFamily("redhat6");
        repoOsEntity.setAmbariManaged(true);
        CompatibleRepositoryVersionResourceProviderTest.osRedhat6.add(repoOsEntity);
    }

    private static StackId stackId11 = new StackId("HDP", "1.1");

    private static StackId stackId22 = new StackId("HDP", "2.2");

    @Test
    public void testVersionInStack() {
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDP", "2.3"), "2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDPWIN", "2.3"), "2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDP", "2.3.GlusterFS"), "2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDF", "2.1"), "2.1.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDP", "2.3"), "HDP-2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDPWIN", "2.3"), "HDPWIN-2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDP", "2.3.GlusterFS"), "HDP-2.3.0.0"));
        Assert.assertTrue(RepositoryVersionEntity.isVersionInStack(new StackId("HDF", "2.1"), "HDF-2.1.0.0"));
    }

    @Test
    public void testGetResources() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("admin", 2L));
        final ResourceProvider provider = CompatibleRepositoryVersionResourceProviderTest.injector.getInstance(ResourceProviderFactory.class).getRepositoryVersionResourceProvider();
        Request getRequest = PropertyHelper.getReadRequest(REPOSITORY_VERSION_ID_PROPERTY_ID, REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID);
        Predicate predicateStackName = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        Predicate predicateStackVersion = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        // !!! non-compatible, within stack
        Assert.assertEquals(1, provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).size());
        CompatibleRepositoryVersionResourceProvider compatibleProvider = new CompatibleRepositoryVersionResourceProvider(null);
        getRequest = PropertyHelper.getReadRequest(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_ID_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID);
        predicateStackName = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        predicateStackVersion = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        // !!! compatible, across stack
        Set<Resource> resources = compatibleProvider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion));
        Assert.assertEquals(2, resources.size());
        // Test For Upgrade Types
        Map<String, List<UpgradeType>> versionToUpgradeTypesMap = new HashMap<>();
        versionToUpgradeTypesMap.put("1.1", Arrays.asList(ROLLING));
        versionToUpgradeTypesMap.put("2.2", Arrays.asList(NON_ROLLING, ROLLING));
        Assert.assertEquals(versionToUpgradeTypesMap.size(), checkUpgradeTypes(resources, versionToUpgradeTypesMap));
        // !!! verify we can get services
        RepositoryVersionDAO dao = CompatibleRepositoryVersionResourceProviderTest.injector.getInstance(RepositoryVersionDAO.class);
        List<RepositoryVersionEntity> entities = dao.findByStack(CompatibleRepositoryVersionResourceProviderTest.stackId22);
        Assert.assertEquals(1, entities.size());
        RepositoryVersionEntity entity = entities.get(0);
        Assert.assertTrue(CompatibleRepositoryVersionResourceProviderTest.ExtendedRepositoryVersionEntity.class.isInstance(entity));
        VersionDefinitionXml mockXml = EasyMock.createMock(VersionDefinitionXml.class);
        AvailableService mockAvailable = EasyMock.createMock(AvailableService.class);
        ManifestServiceInfo mockStackService = EasyMock.createMock(ManifestServiceInfo.class);
        expect(mockXml.getAvailableServices(((StackInfo) (EasyMock.anyObject())))).andReturn(Collections.singletonList(mockAvailable)).atLeastOnce();
        expect(mockXml.getStackServices(((StackInfo) (EasyMock.anyObject())))).andReturn(Collections.singletonList(mockStackService)).atLeastOnce();
        replay(mockXml);
        ((CompatibleRepositoryVersionResourceProviderTest.ExtendedRepositoryVersionEntity) (entity)).m_xml = mockXml;
        getRequest = PropertyHelper.getReadRequest(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_ID_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID, REPOSITORY_VERSION_SERVICES);
        predicateStackName = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        predicateStackVersion = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        resources = compatibleProvider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion));
        Assert.assertEquals(2, resources.size());
        for (Resource r : resources) {
            Object stackId = r.getPropertyValue(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID);
            Assert.assertNotNull(stackId);
            if (stackId.toString().equals("2.2")) {
                Assert.assertNotNull(r.getPropertyValue(REPOSITORY_VERSION_SERVICES));
            } else {
                Assert.assertNull(r.getPropertyValue(REPOSITORY_VERSION_SERVICES));
            }
        }
    }

    @Test
    public void testGetResourcesWithAmendedPredicate() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("admin", 2L));
        final ResourceProvider provider = CompatibleRepositoryVersionResourceProviderTest.injector.getInstance(ResourceProviderFactory.class).getRepositoryVersionResourceProvider();
        Request getRequest = PropertyHelper.getReadRequest(REPOSITORY_VERSION_ID_PROPERTY_ID, REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID);
        Predicate predicateStackName = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        Predicate predicateStackVersion = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        // !!! non-compatible, within stack
        Assert.assertEquals(1, provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).size());
        CompatibleRepositoryVersionResourceProvider compatibleProvider = new CompatibleRepositoryVersionResourceProvider(null);
        getRequest = PropertyHelper.getReadRequest(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_ID_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, REPOSITORY_UPGRADES_SUPPORTED_TYPES_ID);
        predicateStackName = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        predicateStackVersion = new PredicateBuilder().property(CompatibleRepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        // !!! compatible, across stack
        Predicate newPredicate = compatibleProvider.amendPredicate(new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion));
        Assert.assertEquals(OrPredicate.class, newPredicate.getClass());
        Set<Resource> resources = compatibleProvider.getResources(getRequest, newPredicate);
        Assert.assertEquals(2, resources.size());
        // Test For Upgrade Types
        Map<String, List<UpgradeType>> versionToUpgradeTypesMap = new HashMap<>();
        versionToUpgradeTypesMap.put("1.1", Arrays.asList(ROLLING));
        versionToUpgradeTypesMap.put("2.2", Arrays.asList(NON_ROLLING, ROLLING));
        Assert.assertEquals(versionToUpgradeTypesMap.size(), checkUpgradeTypes(resources, versionToUpgradeTypesMap));
    }

    private static class ExtendedRepositoryVersionEntity extends RepositoryVersionEntity {
        private VersionDefinitionXml m_xml = null;

        @Override
        public VersionDefinitionXml getRepositoryXml() throws Exception {
            return m_xml;
        }
    }
}

