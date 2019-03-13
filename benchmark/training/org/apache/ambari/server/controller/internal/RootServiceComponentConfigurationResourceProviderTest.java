/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import AmbariServerConfigurationCategory.LDAP_CONFIGURATION;
import AmbariServerConfigurationCategory.SSO_CONFIGURATION;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.orm.dao.AmbariConfigurationDAO;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileUtils.class, AmbariServerConfigurationHandler.class })
public class RootServiceComponentConfigurationResourceProviderTest extends EasyMockSupport {
    private static final String LDAP_CONFIG_CATEGORY = LDAP_CONFIGURATION.getCategoryName();

    private static final String SSO_CONFIG_CATEGORY = SSO_CONFIGURATION.getCategoryName();

    private Predicate predicate;

    private ResourceProvider resourceProvider;

    private RootServiceComponentConfigurationHandlerFactory factory;

    private Request request;

    private AmbariConfigurationDAO dao;

    private AmbariEventPublisher publisher;

    private AmbariServerLDAPConfigurationHandler ambariServerLDAPConfigurationHandler;

    private AmbariServerSSOConfigurationHandler ambariServerSSOConfigurationHandler;

    @Test
    public void testCreateResources_Administrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_ClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_ClusterOperator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterOperator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_ServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_ServiceOperator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceOperator(), null);
    }

    @Test
    public void testCreateResourcesWithDirective_Administrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithDirective_ClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithDirective_ClusterOperator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterOperator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithDirective_ServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesWithDirective_ServiceOperator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceOperator(), "test-directive");
    }

    @Test
    public void testDeleteResources_Administrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_ClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_ClusterOperator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_ServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_ServiceOperator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResources_Administrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_ClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_ClusterOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_ServiceAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_ServiceOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testUpdateResources_Administrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_ClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_ClusterOperator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterOperator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_ServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator(), null);
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_ServiceOperator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceOperator(), null);
    }

    @Test
    public void testUpdateResourcesWithDirective_Administrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithDirective_ClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithDirective_ClusterOperator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterOperator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithDirective_ServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator(), "test-directive");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesWithDirective_ServiceOperator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceOperator(), "test-directive");
    }
}

