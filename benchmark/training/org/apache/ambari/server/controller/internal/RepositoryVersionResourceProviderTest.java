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


import RepositoryVersionResourceProvider.REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_ID_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_REPOSITORY_VERSION_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID;
import RepositoryVersionResourceProvider.REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID;
import RepositoryVersionResourceProvider.SUBRESOURCE_OPERATING_SYSTEMS_PROPERTY_ID;
import com.google.gson.Gson;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.entities.RepoDefinitionEntity;
import org.apache.ambari.server.orm.entities.RepoOsEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.stack.upgrade.RepositoryVersionHelper;
import org.apache.ambari.server.state.RepositoryInfo;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * RepositoryVersionResourceProvider tests.
 */
public class RepositoryVersionResourceProviderTest {
    private static Injector injector;

    public static final List<RepoOsEntity> osRedhat6 = new ArrayList<>();

    static {
        RepoOsEntity repoOsEntity = new RepoOsEntity();
        repoOsEntity.setFamily("redhat6");
        repoOsEntity.setAmbariManaged(true);
        RepositoryVersionResourceProviderTest.osRedhat6.add(repoOsEntity);
    }

    public static final List<RepoOsEntity> osRedhat7 = new ArrayList<>();

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesAsAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesAsClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testValidateRepositoryVersion() throws Exception {
        StackDAO stackDAO = RepositoryVersionResourceProviderTest.injector.getInstance(StackDAO.class);
        StackEntity stackEntity = stackDAO.find("HDP", "1.1");
        Assert.assertNotNull(stackEntity);
        final RepositoryVersionEntity entity = new RepositoryVersionEntity();
        entity.setDisplayName("name");
        entity.setStack(stackEntity);
        entity.setVersion("1.1");
        List<RepoOsEntity> osEntities = new ArrayList<>();
        RepoDefinitionEntity repoDefinitionEntity1 = new RepoDefinitionEntity();
        repoDefinitionEntity1.setRepoID("1");
        repoDefinitionEntity1.setBaseUrl("http://example.com/repo1");
        repoDefinitionEntity1.setRepoName("1");
        repoDefinitionEntity1.setUnique(true);
        RepoOsEntity repoOsEntity = new RepoOsEntity();
        repoOsEntity.setFamily("redhat6");
        repoOsEntity.setAmbariManaged(true);
        repoOsEntity.addRepoDefinition(repoDefinitionEntity1);
        osEntities.add(repoOsEntity);
        entity.addRepoOsEntities(osEntities);
        final RepositoryVersionDAO repositoryVersionDAO = RepositoryVersionResourceProviderTest.injector.getInstance(RepositoryVersionDAO.class);
        AmbariMetaInfo info = RepositoryVersionResourceProviderTest.injector.getInstance(AmbariMetaInfo.class);
        // test valid usecases
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
        entity.setVersion("1.1-17");
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
        entity.setVersion("1.1.1.1");
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
        entity.setVersion("1.1.343432.2");
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
        entity.setVersion("1.1.343432.2-234234324");
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
        // test invalid usecases
        entity.addRepoOsEntities(new ArrayList());
        try {
            RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
            Assert.fail("Should throw exception");
        } catch (Exception ex) {
        }
        StackEntity bigtop = new StackEntity();
        bigtop.setStackName("BIGTOP");
        entity.setStack(bigtop);
        try {
            RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity);
            Assert.fail("Should throw exception");
        } catch (Exception ex) {
        }
        entity.setDisplayName("name");
        entity.setStack(stackEntity);
        entity.setVersion("1.1");
        entity.addRepoOsEntities(osEntities);
        repositoryVersionDAO.create(entity);
        final RepositoryVersionEntity entity2 = new RepositoryVersionEntity();
        entity2.setId(2L);
        entity2.setDisplayName("name2");
        entity2.setStack(stackEntity);
        entity2.setVersion("1.2");
        entity2.addRepoOsEntities(osEntities);
        try {
            RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity2);
            Assert.fail("Should throw exception: Base url http://example.com/repo1 is already defined for another repository version");
        } catch (Exception ex) {
        }
        final RepositoryVersionEntity entity3 = new RepositoryVersionEntity();
        entity3.setId(3L);
        entity3.setDisplayName("name2");
        entity3.setStack(stackEntity);
        entity3.setVersion("1.1");
        entity3.addRepoOsEntities(osEntities);
        try {
            RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity3);
            Assert.fail("Expected exception");
        } catch (AmbariException e) {
            // expected
        }
        entity3.addRepoOsEntities(osEntities);
        repoOsEntity.setAmbariManaged(false);
        RepositoryVersionResourceProvider.validateRepositoryVersion(repositoryVersionDAO, info, entity3);
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateResourcesNoManageRepos() throws Exception {
        Authentication authentication = TestAuthenticationFactory.createAdministrator();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final ResourceProvider provider = RepositoryVersionResourceProviderTest.injector.getInstance(ResourceProviderFactory.class).getRepositoryVersionResourceProvider();
        final Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID, "name");
        properties.put(SUBRESOURCE_OPERATING_SYSTEMS_PROPERTY_ID, new Gson().fromJson("[{\"OperatingSystems/os_type\":\"redhat6\",\"repositories\":[{\"Repositories/repo_id\":\"1\",\"Repositories/repo_name\":\"1\",\"Repositories/base_url\":\"http://example.com/repo1\",\"Repositories/unique\":\"true\"}]}]", Object.class));
        properties.put(REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID, "HDP");
        properties.put(REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID, "1.1");
        properties.put(REPOSITORY_VERSION_REPOSITORY_VERSION_PROPERTY_ID, "1.1.1.1");
        propertySet.add(properties);
        final Predicate predicateStackName = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).equals("HDP").toPredicate();
        final Predicate predicateStackVersion = new PredicateBuilder().property(REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).equals("1.1").toPredicate();
        final Request getRequest = PropertyHelper.getReadRequest(REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID, SUBRESOURCE_OPERATING_SYSTEMS_PROPERTY_ID);
        Assert.assertEquals(0, provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).size());
        final Request createRequest = PropertyHelper.getCreateRequest(propertySet, null);
        provider.createResources(createRequest);
        Assert.assertEquals(1, provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).size());
        Assert.assertEquals("name", provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).iterator().next().getPropertyValue(REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID));
        properties.put(REPOSITORY_VERSION_ID_PROPERTY_ID, "1");
        properties.put(REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID, "name2");
        properties.put(SUBRESOURCE_OPERATING_SYSTEMS_PROPERTY_ID, new Gson().fromJson("[{\"OperatingSystems/ambari_managed_repositories\":false, \"OperatingSystems/os_type\":\"redhat6\",\"repositories\":[{\"Repositories/repo_id\":\"1\",\"Repositories/repo_name\":\"1\",\"Repositories/base_url\":\"http://example.com/repo1\",\"Repositories/unique\":\"true\"}]}]", Object.class));
        final Request updateRequest = PropertyHelper.getUpdateRequest(properties, null);
        provider.updateResources(updateRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion));
        Assert.assertEquals("name2", provider.getResources(getRequest, new org.apache.ambari.server.controller.predicate.AndPredicate(predicateStackName, predicateStackVersion)).iterator().next().getPropertyValue(REPOSITORY_VERSION_DISPLAY_NAME_PROPERTY_ID));
        AmbariMetaInfo ambariMetaInfo = RepositoryVersionResourceProviderTest.injector.getInstance(AmbariMetaInfo.class);
        String stackName = properties.get(REPOSITORY_VERSION_STACK_NAME_PROPERTY_ID).toString();
        String stackVersion = properties.get(REPOSITORY_VERSION_STACK_VERSION_PROPERTY_ID).toString();
        Object operatingSystems = properties.get(SUBRESOURCE_OPERATING_SYSTEMS_PROPERTY_ID);
        Gson gson = new Gson();
        String operatingSystemsJson = gson.toJson(operatingSystems);
        RepositoryVersionHelper repositoryVersionHelper = new RepositoryVersionHelper();
        List<RepoOsEntity> operatingSystemEntities = repositoryVersionHelper.parseOperatingSystems(operatingSystemsJson);
        for (RepoOsEntity operatingSystemEntity : operatingSystemEntities) {
            Assert.assertFalse(operatingSystemEntity.isAmbariManaged());
            String osType = operatingSystemEntity.getFamily();
            List<RepoDefinitionEntity> repositories = operatingSystemEntity.getRepoDefinitionEntities();
            for (RepoDefinitionEntity repository : repositories) {
                RepositoryInfo repo = ambariMetaInfo.getRepository(stackName, stackVersion, osType, repository.getRepoID());
                if (repo != null) {
                    String baseUrlActual = repo.getBaseUrl();
                    String baseUrlExpected = repository.getBaseUrl();
                    Assert.assertEquals(baseUrlExpected, baseUrlActual);
                }
            }
        }
    }

    @Test
    public void testVersionInStack() {
        StackId sid = new StackId("HDP-2.3");
        StackId sid2 = new StackId("HDP-2.3.NEW");
        StackId sid3 = new StackId("HDF-2.3");
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid, "2.3"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid2, "2.3"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid3, "2.3"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid, "2.3.1"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid2, "2.3.1"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid3, "2.3.1"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid, "2.3.2.0-2300"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid2, "2.3.2.1-3562"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid3, "2.3.2.1-3562"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid, "HDP-2.3.2.0-2300"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid2, "HDP-2.3.2.1-3562"));
        Assert.assertEquals(true, RepositoryVersionEntity.isVersionInStack(sid3, "HDF-2.3.2.1-3562"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid, "2.4.2.0-2300"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid2, "2.1"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid3, "2.1"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid, "HDP-2.4.2.0-2300"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid2, "HDP-2.1"));
        Assert.assertEquals(false, RepositoryVersionEntity.isVersionInStack(sid3, "HDF-2.1"));
    }
}

