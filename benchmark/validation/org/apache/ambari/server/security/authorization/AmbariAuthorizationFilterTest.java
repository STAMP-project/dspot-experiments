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
package org.apache.ambari.server.security.authorization;


import Encryptor.NONE;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.apache.ambari.server.audit.AuditLogger;
import org.apache.ambari.server.configuration.AmbariServerConfiguration;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.hooks.HookContextFactory;
import org.apache.ambari.server.hooks.HookService;
import org.apache.ambari.server.ldap.service.AmbariLdapConfigurationProvider;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.orm.dao.UserDAO;
import org.apache.ambari.server.security.AmbariEntryPoint;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.state.stack.OsFamily;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class AmbariAuthorizationFilterTest {
    @Test
    public void testDoFilter_adminAccess() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/clusters/cluster", "GET", true);
        urlTests.put("/api/v1/clusters/cluster", "POST", true);
        urlTests.put("/api/v1/clusters/cluster/", "GET", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/clusters/cluster/", "POST", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/views", "GET", true);
        urlTests.put("/api/v1/views", "POST", true);
        urlTests.put("/api/v1/persist/SomeValue", "GET", true);
        urlTests.put("/api/v1/persist/SomeValue", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "GET", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "POST", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "GET", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "POST", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "DELETE", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "GET", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "POST", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "GET", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "POST", true);
        urlTests.put("/api/v1/users/user1", "GET", true);
        urlTests.put("/api/v1/users/user1", "POST", true);
        urlTests.put("/api/v1/users/user2", "GET", true);
        urlTests.put("/api/v1/users/user2", "POST", true);
        urlTests.put("/api/v1/groups", "GET", true);
        urlTests.put("/api/v1/ldap_sync_events", "GET", true);
        urlTests.put("/any/other/URL", "GET", true);
        urlTests.put("/any/other/URL", "POST", true);
        performGeneralDoFilterTest(TestAuthenticationFactory.createAdministrator(), urlTests, false);
    }

    @Test
    public void testDoFilter_clusterViewerAccess() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/clusters/cluster", "GET", true);
        urlTests.put("/api/v1/clusters/cluster", "POST", true);
        urlTests.put("/api/v1/clusters/cluster/", "GET", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/clusters/cluster/", "POST", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/views", "GET", true);
        urlTests.put("/api/v1/views", "POST", true);
        urlTests.put("/api/v1/persist/SomeValue", "GET", true);
        urlTests.put("/api/v1/persist/SomeValue", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "GET", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "POST", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "GET", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "POST", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "DELETE", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "GET", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "POST", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "GET", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "POST", true);
        urlTests.put("/api/v1/users/user1", "GET", true);
        urlTests.put("/api/v1/users/user1", "POST", true);
        urlTests.put("/api/v1/users/user2", "GET", true);
        urlTests.put("/api/v1/users/user2", "POST", true);
        urlTests.put("/api/v1/groups", "GET", true);
        urlTests.put("/api/v1/ldap_sync_events", "GET", false);
        urlTests.put("/any/other/URL", "GET", true);
        urlTests.put("/any/other/URL", "POST", false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createClusterUser(), urlTests, false);
    }

    @Test
    public void testDoFilter_clusterOperatorAccess() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/clusters/cluster", "GET", true);
        urlTests.put("/api/v1/clusters/cluster", "POST", true);
        urlTests.put("/api/v1/clusters/cluster/", "GET", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/clusters/cluster/", "POST", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/views", "GET", true);
        urlTests.put("/api/v1/views", "POST", true);
        urlTests.put("/api/v1/persist/SomeValue", "GET", true);
        urlTests.put("/api/v1/persist/SomeValue", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "GET", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "POST", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "GET", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "POST", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "DELETE", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "GET", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "POST", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "GET", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "POST", true);
        urlTests.put("/api/v1/users/user1", "GET", true);
        urlTests.put("/api/v1/users/user1", "POST", true);
        urlTests.put("/api/v1/users/user2", "GET", true);
        urlTests.put("/api/v1/users/user2", "POST", true);
        urlTests.put("/api/v1/groups", "GET", true);
        urlTests.put("/api/v1/ldap_sync_events", "GET", false);
        urlTests.put("/api/v1/clusters/c1/widgets", "GET", true);
        urlTests.put("/api/v1/clusters/c1/widgets", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/widgets", "POST", true);
        urlTests.put("/any/other/URL", "GET", true);
        urlTests.put("/any/other/URL", "POST", false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createClusterOperator(), urlTests, false);
    }

    @Test
    public void testDoFilter_viewUserAccess() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/clusters/cluster", "GET", true);
        urlTests.put("/api/v1/clusters/cluster", "POST", true);
        urlTests.put("/api/v1/clusters/cluster/", "GET", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/clusters/cluster/", "POST", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/views", "GET", true);
        urlTests.put("/api/v1/views", "POST", true);
        urlTests.put("/api/v1/persist/SomeValue", "GET", true);
        urlTests.put("/api/v1/persist/SomeValue", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "GET", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "POST", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "GET", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "POST", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "DELETE", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "GET", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "POST", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "GET", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "POST", true);
        urlTests.put("/api/v1/users/user1", "GET", true);
        urlTests.put("/api/v1/users/user1", "POST", true);
        urlTests.put("/api/v1/users/user2", "GET", true);
        urlTests.put("/api/v1/users/user2", "POST", true);
        urlTests.put("/api/v1/groups", "GET", true);
        urlTests.put("/api/v1/ldap_sync_events", "GET", false);
        urlTests.put("/any/other/URL", "GET", true);
        urlTests.put("/any/other/URL", "POST", false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createViewUser(99L), urlTests, false);
    }

    @Test
    public void testDoFilter_userNoPermissionsAccess() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/clusters/cluster", "GET", true);
        urlTests.put("/api/v1/clusters/cluster", "POST", true);
        urlTests.put("/api/v1/clusters/cluster/", "GET", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/clusters/cluster/", "POST", true);// This should probably be an invalid URL, but Ambari seems to allow it.

        urlTests.put("/api/v1/views", "GET", true);
        urlTests.put("/api/v1/views", "POST", true);
        urlTests.put("/api/v1/persist/SomeValue", "GET", true);
        urlTests.put("/api/v1/persist/SomeValue", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/ambari.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "POST", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "GET", true);
        urlTests.put("/api/v1/clusters/c1/credentials/cluster.credential", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "GET", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "POST", true);
        urlTests.put("/api/v1/clusters/c1/config_groups", "DELETE", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "GET", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "PUT", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "POST", true);
        urlTests.put("/api/v1/clusters/c1/configurations", "DELETE", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "GET", true);
        urlTests.put("/views/AllowedView/SomeVersion/SomeInstance", "POST", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "GET", true);
        urlTests.put("/views/DeniedView/AnotherVersion/AnotherInstance", "POST", true);
        urlTests.put("/api/v1/users/user1", "GET", true);
        urlTests.put("/api/v1/users/user1", "POST", true);
        urlTests.put("/api/v1/users/user2", "GET", true);
        urlTests.put("/api/v1/users/user2", "POST", true);
        urlTests.put("/any/other/URL", "GET", true);
        urlTests.put("/any/other/URL", "POST", false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createViewUser(null), urlTests, false);
    }

    @Test
    public void testDoFilter_viewNotLoggedIn() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/views/SomeView/SomeVersion/SomeInstance", "GET", false);
        urlTests.put("/views/SomeView/SomeVersion/SomeInstance?foo=bar", "GET", false);
        performGeneralDoFilterTest(null, urlTests, true);
    }

    @Test
    public void testDoFilter_stackAdvisorCalls() throws Exception {
        final Table<String, String, Boolean> urlTests = HashBasedTable.create();
        urlTests.put("/api/v1/stacks/HDP/versions/2.3/validations", "POST", true);
        urlTests.put("/api/v1/stacks/HDP/versions/2.3/recommendations", "POST", true);
        performGeneralDoFilterTest(TestAuthenticationFactory.createClusterAdministrator(), urlTests, false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createClusterUser(), urlTests, false);
        performGeneralDoFilterTest(TestAuthenticationFactory.createAdministrator(), urlTests, false);
    }

    @Test
    public void testDoFilter_NotLoggedIn_UseDefaultUser() throws Exception {
        final FilterChain chain = EasyMock.createStrictMock(FilterChain.class);
        final HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        final HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/uri").anyTimes();
        expect(request.getQueryString()).andReturn(null).anyTimes();
        expect(request.getMethod()).andReturn("GET").anyTimes();
        chain.doFilter(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        final Configuration configuration = EasyMock.createMock(Configuration.class);
        expect(configuration.getDefaultApiAuthenticatedUser()).andReturn("user1").once();
        User user = EasyMock.createMock(User.class);
        expect(user.getUserName()).andReturn("user1").anyTimes();
        final Users users = EasyMock.createMock(Users.class);
        expect(users.getUser("user1")).andReturn(user).once();
        expect(users.getUserAuthorities("user1")).andReturn(Collections.<AmbariGrantedAuthority>emptyList()).once();
        replay(request, response, chain, configuration, users, user);
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Configuration.class).toInstance(configuration);
                bind(Users.class).toInstance(users);
                bind(EntityManager.class).toInstance(EasyMock.createMock(EntityManager.class));
                bind(UserDAO.class).toInstance(EasyMock.createMock(UserDAO.class));
                bind(DBAccessor.class).toInstance(EasyMock.createMock(DBAccessor.class));
                bind(PasswordEncoder.class).toInstance(EasyMock.createMock(PasswordEncoder.class));
                bind(OsFamily.class).toInstance(EasyMock.createMock(OsFamily.class));
                bind(AuditLogger.class).toInstance(EasyMock.createNiceMock(AuditLogger.class));
                bind(HookService.class).toInstance(EasyMock.createMock(HookService.class));
                bind(HookContextFactory.class).toInstance(EasyMock.createMock(HookContextFactory.class));
                bind(new com.google.inject.TypeLiteral<org.apache.ambari.server.security.encryption.Encryptor<AmbariServerConfiguration>>() {}).annotatedWith(Names.named("AmbariServerConfigurationEncryptor")).toInstance(NONE);
                bind(AmbariLdapConfigurationProvider.class).toInstance(EasyMock.createMock(AmbariLdapConfigurationProvider.class));
            }
        });
        AmbariAuthorizationFilter filter = new AmbariAuthorizationFilter(createNiceMock(AmbariEntryPoint.class), injector.getInstance(Configuration.class), injector.getInstance(Users.class), injector.getInstance(AuditLogger.class), injector.getInstance(PermissionHelper.class));
        injector.injectMembers(filter);
        filter.doFilter(request, response, chain);
        Assert.assertEquals("user1", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

