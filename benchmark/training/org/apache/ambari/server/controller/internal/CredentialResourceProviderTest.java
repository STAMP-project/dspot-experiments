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


import CredentialStoreType.PERSISTED;
import CredentialStoreType.TEMPORARY;
import Resource.Type.Credential;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.security.SecurePasswordHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.security.encryption.CredentialStoreService;
import org.apache.ambari.server.security.encryption.CredentialStoreServiceImpl;
import org.apache.ambari.server.state.stack.OsFamily;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * CredentialResourceProviderTest unit tests.
 */
@SuppressWarnings("unchecked")
public class CredentialResourceProviderTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private Injector injector;

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testCreateResources_FailMissingAlias() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        CredentialResourceProvider credentialResourceProvider = new CredentialResourceProvider(managementController);
        injector.injectMembers(credentialResourceProvider);
        Set<Map<String, Object>> setProperties = getCredentialTestProperties("c1", null, "username1", "password1", TEMPORARY);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        ResourceProviderFactory factory = createMock(ResourceProviderFactory.class);
        expect(factory.getCredentialResourceProvider(anyObject(AmbariManagementController.class))).andReturn(credentialResourceProvider);
        replay(request, factory, managementController);
        // end expectations
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("admin"));
        AbstractControllerResourceProvider.init(factory);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Credential, managementController);
        try {
            provider.createResources(request);
            Assert.fail("Expected exception due to missing alias");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(request, factory, managementController);
    }

    @Test
    public void testCreateResources_FailMissingPrincipal() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        CredentialResourceProvider credentialResourceProvider = new CredentialResourceProvider(managementController);
        injector.injectMembers(credentialResourceProvider);
        Set<Map<String, Object>> setProperties = getCredentialTestProperties("c1", "alias1", null, "password1", TEMPORARY);
        // set expectations
        expect(request.getProperties()).andReturn(setProperties);
        ResourceProviderFactory factory = createMock(ResourceProviderFactory.class);
        expect(factory.getCredentialResourceProvider(anyObject(AmbariManagementController.class))).andReturn(credentialResourceProvider);
        replay(request, factory, managementController);
        // end expectations
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("admin"));
        AbstractControllerResourceProvider.init(factory);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Credential, managementController);
        try {
            provider.createResources(request);
            Assert.fail("Expected exception due to missing alias");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(request, factory, managementController);
    }

    @Test
    public void testCreateResources_NotInitialized() throws Exception {
        // Create injector where the Configuration object does not have the persisted CredentialStore
        // details set.
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                bind(CredentialStoreService.class).to(CredentialStoreServiceImpl.class);
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(SecurePasswordHelper.class).toInstance(new SecurePasswordHelper());
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Request request = createMock(Request.class);
        CredentialResourceProvider credentialResourceProvider = new CredentialResourceProvider(managementController);
        injector.injectMembers(credentialResourceProvider);
        // Create resources requests
        expect(request.getProperties()).andReturn(getCredentialTestProperties("c1", "alias1", "username1", "password1", TEMPORARY)).once();
        expect(request.getProperties()).andReturn(getCredentialTestProperties("c1", "alias1", "username1", "password1", PERSISTED)).once();
        // Get resources request
        expect(request.getPropertyIds()).andReturn(null).anyTimes();
        ResourceProviderFactory factory = createMock(ResourceProviderFactory.class);
        expect(factory.getCredentialResourceProvider(anyObject(AmbariManagementController.class))).andReturn(credentialResourceProvider);
        replay(request, factory, managementController);
        // end expectations
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("admin"));
        AbstractControllerResourceProvider.init(factory);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(Credential, managementController);
        // The temporary store should always be initialized.... this should succeed.
        provider.createResources(request);
        try {
            provider.createResources(request);
            Assert.fail("Expected IllegalArgumentException thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(("Credentials cannot be stored in Ambari's persistent secure credential " + ("store since secure persistent storage has not yet be configured.  Use ambari-server " + "setup-security to enable this feature.")), e.getLocalizedMessage());
        }
        verify(request, factory, managementController);
    }

    @Test
    public void testGetResourcesAsAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesAsClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesAsServiceAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateAsAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesWithPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateNoResultsAsAdministrator() throws Exception {
        testGetResourcesWithPredicateNoResults(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithPredicateNoResultsAsClusterAdministrator() throws Exception {
        testGetResourcesWithPredicateNoResults(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesWithPredicateNoResultsAsServiceAdministrator() throws Exception {
        testGetResourcesWithPredicateNoResults(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithoutPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesWithoutPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testUpdateResourcesResourceNotFoundAsAdministrator() throws Exception {
        testUpdateResourcesResourceNotFound(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testUpdateResourcesResourceNotFoundAsClusterAdministrator() throws Exception {
        testUpdateResourcesResourceNotFound(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesResourceNotFoundAsServiceAdministrator() throws Exception {
        testUpdateResourcesResourceNotFound(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }
}

