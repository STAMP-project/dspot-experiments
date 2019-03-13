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


import AbstractKerberosDescriptor.Type.AUTH_TO_LOCAL_PROPERTY;
import AbstractKerberosDescriptor.Type.CONFIGURATION;
import AbstractKerberosDescriptor.Type.IDENTITY;
import AbstractKerberosDescriptor.Type.SERVICE;
import com.google.gson.Gson;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.state.kerberos.KerberosPrincipalDescriptorTest;
import org.apache.ambari.server.state.kerberos.KerberosServiceDescriptorTest;
import org.easymock.EasyMockSupport;
import org.junit.Test;


/**
 * ClusterKerberosDescriptorResourceProviderTest unit tests.
 */
@SuppressWarnings("unchecked")
public class ClusterKerberosDescriptorResourceProviderTest extends EasyMockSupport {
    private static final Gson GSON = new Gson();

    private static final Map<String, Object> STACK_MAP;

    private static final Map<String, Object> USER_MAP;

    private static final Map<String, Object> COMPOSITE_MAP;

    static {
        TreeMap<String, Object> stackProperties = new TreeMap<>();
        stackProperties.put("realm", "EXAMPLE.COM");
        stackProperties.put("some.property", "Hello World");
        Collection<String> authToLocalRules = new ArrayList<>();
        authToLocalRules.add("global.name.rules");
        TreeMap<String, Object> stackServices = new TreeMap<>();
        stackServices.put(((String) (KerberosServiceDescriptorTest.MAP_VALUE.get("name"))), KerberosServiceDescriptorTest.MAP_VALUE);
        TreeMap<String, Object> stackClusterConfProperties = new TreeMap<>();
        stackClusterConfProperties.put("property1", "red");
        TreeMap<String, Object> stackClusterConf = new TreeMap<>();
        stackClusterConf.put("cluster-conf", stackClusterConfProperties);
        TreeMap<String, Object> stackConfigurations = new TreeMap<>();
        stackConfigurations.put("cluster-conf", stackClusterConf);
        TreeMap<String, Object> stackSharedIdentityKeytabOwner = new TreeMap<>();
        stackSharedIdentityKeytabOwner.put("name", "root");
        stackSharedIdentityKeytabOwner.put("access", "rw");
        TreeMap<String, Object> sharedIdentityKeytabGroup = new TreeMap<>();
        sharedIdentityKeytabGroup.put("name", "hadoop");
        sharedIdentityKeytabGroup.put("access", "r");
        TreeMap<String, Object> stackSharedIdentityKeytab = new TreeMap<>();
        stackSharedIdentityKeytab.put("file", "/etc/security/keytabs/subject.service.keytab");
        stackSharedIdentityKeytab.put("owner", stackSharedIdentityKeytabOwner);
        stackSharedIdentityKeytab.put("group", sharedIdentityKeytabGroup);
        stackSharedIdentityKeytab.put("configuration", "service-site/service2.component.keytab.file");
        TreeMap<String, Object> stackSharedIdentity = new TreeMap<>();
        stackSharedIdentity.put("name", "shared");
        stackSharedIdentity.put("principal", new TreeMap<>(KerberosPrincipalDescriptorTest.MAP_VALUE));
        stackSharedIdentity.put("keytab", stackSharedIdentityKeytab);
        TreeMap<String, Object> stackIdentities = new TreeMap<>();
        stackIdentities.put("shared", stackSharedIdentity);
        STACK_MAP = new TreeMap<>();
        ClusterKerberosDescriptorResourceProviderTest.STACK_MAP.put("properties", stackProperties);
        ClusterKerberosDescriptorResourceProviderTest.STACK_MAP.put(AUTH_TO_LOCAL_PROPERTY.getDescriptorPluralName(), authToLocalRules);
        ClusterKerberosDescriptorResourceProviderTest.STACK_MAP.put(SERVICE.getDescriptorPluralName(), stackServices.values());
        ClusterKerberosDescriptorResourceProviderTest.STACK_MAP.put(CONFIGURATION.getDescriptorPluralName(), stackConfigurations.values());
        ClusterKerberosDescriptorResourceProviderTest.STACK_MAP.put(IDENTITY.getDescriptorPluralName(), stackIdentities.values());
        TreeMap<String, Object> userProperties = new TreeMap<>();
        userProperties.put("realm", "HWX.COM");
        userProperties.put("some.property", "Hello World");
        TreeMap<String, Object> userClusterConfProperties = new TreeMap<>();
        userClusterConfProperties.put("property1", "blue");
        userClusterConfProperties.put("property2", "orange");
        TreeMap<String, Object> userClusterConf = new TreeMap<>();
        userClusterConf.put("cluster-conf", userClusterConfProperties);
        TreeMap<String, Object> userConfigurations = new TreeMap<>();
        userConfigurations.put("cluster-conf", userClusterConf);
        TreeMap<String, Object> userSharedIdentityKeytabOwner = new TreeMap<>();
        userSharedIdentityKeytabOwner.put("name", "root");
        userSharedIdentityKeytabOwner.put("access", "rw");
        TreeMap<String, Object> userSharedIdentityKeytabGroup = new TreeMap<>();
        userSharedIdentityKeytabGroup.put("name", "hadoop");
        userSharedIdentityKeytabGroup.put("access", "r");
        TreeMap<String, Object> userSharedIdentityKeytab = new TreeMap<>();
        userSharedIdentityKeytab.put("file", "/etc/security/keytabs/subject.service.keytab");
        userSharedIdentityKeytab.put("owner", userSharedIdentityKeytabOwner);
        userSharedIdentityKeytab.put("group", userSharedIdentityKeytabGroup);
        userSharedIdentityKeytab.put("configuration", "service-site/service2.component.keytab.file");
        TreeMap<String, Object> userSharedIdentity = new TreeMap<>();
        userSharedIdentity.put("name", "shared");
        userSharedIdentity.put("principal", new TreeMap<>(KerberosPrincipalDescriptorTest.MAP_VALUE));
        userSharedIdentity.put("keytab", userSharedIdentityKeytab);
        TreeMap<String, Object> userIdentities = new TreeMap<>();
        userIdentities.put("shared", userSharedIdentity);
        USER_MAP = new TreeMap<>();
        ClusterKerberosDescriptorResourceProviderTest.USER_MAP.put("properties", userProperties);
        ClusterKerberosDescriptorResourceProviderTest.USER_MAP.put(CONFIGURATION.getDescriptorPluralName(), userConfigurations.values());
        ClusterKerberosDescriptorResourceProviderTest.USER_MAP.put(IDENTITY.getDescriptorPluralName(), userIdentities.values());
        COMPOSITE_MAP = new TreeMap<>();
        ClusterKerberosDescriptorResourceProviderTest.COMPOSITE_MAP.putAll(ClusterKerberosDescriptorResourceProviderTest.STACK_MAP);
        ClusterKerberosDescriptorResourceProviderTest.COMPOSITE_MAP.putAll(ClusterKerberosDescriptorResourceProviderTest.USER_MAP);
    }

    private Injector injector;

    @Test(expected = SystemException.class)
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = SystemException.class)
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = SystemException.class)
    public void testCreateResourcesAsServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesAsAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesAsClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesAsClusterOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesAsServiceAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesAsServiceOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesAsClusterUser() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetResourcesWithPredicateAsAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateAsClusterOperator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesWithPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateAsServiceOperator() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesWithPredicateAsClusterUser() throws Exception {
        testGetResourcesWithPredicate(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsAdministrator() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsClusterAdministrator() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsClusterOperator() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsServiceAdministrator() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsServiceOperator() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesWithPredicateAndDirectivesAsClusterUser() throws Exception {
        testGetResourcesWithPredicateAndDirectives(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesWithInvalidKerberosDescriptorTypeAsAdministrator() throws Exception {
        testGetResourcesWithInvalidKerberosDescriptorType(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesWithInvalidKerberosDescriptorTypeAsClusterAdministrator() throws Exception {
        testGetResourcesWithInvalidKerberosDescriptorType(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesWithInvalidKerberosDescriptorTypeAsServiceAdministrator() throws Exception {
        testGetResourcesWithInvalidKerberosDescriptorType(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test
    public void testGetResourcesWithoutPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsClusterOperator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsServiceOperator() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesWithoutPredicateAsClusterUser() throws Exception {
        testGetResourcesWithoutPredicate(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = SystemException.class)
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = SystemException.class)
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = SystemException.class)
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = SystemException.class)
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = SystemException.class)
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = SystemException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }
}

