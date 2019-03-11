/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.authorization;


import FileAccessPolicyProvider.PROP_INITIAL_ADMIN_IDENTITY;
import FileAccessPolicyProvider.PROP_NODE_GROUP_NAME;
import FileAuthorizer.PROP_LEGACY_AUTHORIZED_USERS_FILE;
import RequestAction.READ;
import RequestAction.WRITE;
import ResourceType.Controller;
import ResourceType.Data;
import ResourceType.DataTransfer;
import ResourceType.Flow;
import ResourceType.InputPort;
import ResourceType.OutputPort;
import ResourceType.Policy;
import ResourceType.ProcessGroup;
import ResourceType.Provenance;
import ResourceType.ProvenanceData;
import ResourceType.Proxy;
import ResourceType.SiteToSite;
import ResourceType.System;
import ResourceType.Tenant;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.apache.nifi.authorization.resource.ResourceFactory;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static FileAccessPolicyProvider.PROP_NODE_IDENTITY_PREFIX;
import static FileUserGroupProvider.PROP_INITIAL_USER_IDENTITY_PREFIX;
import static RequestAction.READ;
import static RequestAction.WRITE;


public class FileAccessPolicyProviderTest {
    private static final String EMPTY_AUTHORIZATIONS_CONCISE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<authorizations/>";

    private static final String EMPTY_TENANTS_CONCISE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<tenants/>";

    private static final String EMPTY_AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<authorizations>" + "</authorizations>");

    private static final String EMPTY_TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<tenants>" + "</tenants>");

    private static final String BAD_SCHEMA_AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<authorization>" + "</authorization>");

    private static final String AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((((((((("<authorizations>" + "  <policies>") + "      <policy identifier=\"policy-1\" resource=\"/flow\" action=\"R\">") + "  <group identifier=\"group-1\" />") + "  <group identifier=\"group-2\" />") + "  <user identifier=\"user-1\" />") + "      </policy>") + "      <policy identifier=\"policy-2\" resource=\"/flow\" action=\"W\">") + "        <user identifier=\"user-2\" />") + "      </policy>") + "  </policies>") + "</authorizations>");

    private static final String TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((((((((((("<tenants>" + "  <groups>") + "    <group identifier=\"group-1\" name=\"group-1\">") + "       <user identifier=\"user-1\" />") + "    </group>") + "    <group identifier=\"group-2\" name=\"group-2\">") + "       <user identifier=\"user-2\" />") + "    </group>") + "  </groups>") + "  <users>") + "    <user identifier=\"user-1\" identity=\"user-1\" />") + "    <user identifier=\"user-2\" identity=\"user-2\" />") + "  </users>") + "</tenants>");

    private static final String TENANTS_FOR_ADMIN_AND_NODES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (((((("<tenants>" + "  <users>") + "    <user identifier=\"admin-user\" identity=\"admin-user\"/>") + "    <user identifier=\"node1\" identity=\"node1\"/>") + "    <user identifier=\"node2\" identity=\"node2\"/>") + "  </users>") + "</tenants>");

    private static final String TENANTS_FOR_ADMIN_AND_NODE_GROUP = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (((((((((((("<tenants>" + "  <groups>") + "    <group identifier=\"cluster-nodes\" name=\"Cluster Nodes\">") + "       <user identifier=\"node1\" />") + "       <user identifier=\"node2\" />") + "    </group>") + "  </groups>") + "  <users>") + "    <user identifier=\"admin-user\" identity=\"admin-user\"/>") + "    <user identifier=\"node1\" identity=\"node1\"/>") + "    <user identifier=\"node2\" identity=\"node2\"/>") + "  </users>") + "</tenants>");

    // This is the root group id from the flow.xml.gz in src/test/resources
    private static final String ROOT_GROUP_ID = "e530e14c-adcf-41c2-b5d6-d9a59ba8765c";

    private NiFiProperties properties;

    private FileAccessPolicyProvider accessPolicyProvider;

    private FileUserGroupProvider userGroupProvider;

    private File primaryAuthorizations;

    private File primaryTenants;

    private File restoreAuthorizations;

    private File restoreTenants;

    private File flow;

    private File flowNoPorts;

    private File flowWithDns;

    private AuthorizerConfigurationContext configurationContext;

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedWithOverlappingRoles() throws Exception {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-multirole.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy(Flow.getValue(), READ));
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy(Controller.getValue(), READ));
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy(Controller.getValue(), WRITE));
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy(System.getValue(), READ));
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID)), READ));
        Assert.assertNotNull(accessPolicyProvider.getAccessPolicy((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID)), WRITE));
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedAndFlowHasNoPorts() throws Exception {
        properties = Mockito.mock(NiFiProperties.class);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flowNoPorts);
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        boolean foundDataTransferPolicy = false;
        for (AccessPolicy policy : accessPolicyProvider.getAccessPolicies()) {
            if (policy.getResource().contains(DataTransfer.name())) {
                foundDataTransferPolicy = true;
                break;
            }
        }
        Assert.assertFalse(foundDataTransferPolicy);
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final User user1 = userGroupProvider.getUserByIdentity("user1");
        final User user2 = userGroupProvider.getUserByIdentity("user2");
        final User user3 = userGroupProvider.getUserByIdentity("user3");
        final User user4 = userGroupProvider.getUserByIdentity("user4");
        final User user5 = userGroupProvider.getUserByIdentity("user5");
        final User user6 = userGroupProvider.getUserByIdentity("user6");
        // verify one group got created
        final Set<Group> groups = userGroupProvider.getGroups();
        final Group group1 = groups.iterator().next();
        // verify more than one policy got created
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertTrue(((policies.size()) > 0));
        // verify user1's policies
        final Map<String, Set<RequestAction>> user1Policies = getResourceActions(policies, user1);
        Assert.assertEquals(4, user1Policies.size());
        Assert.assertTrue(user1Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user1Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user1Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user1Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user1Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user1Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).contains(READ));
        // verify user2's policies
        final Map<String, Set<RequestAction>> user2Policies = getResourceActions(policies, user2);
        Assert.assertEquals(3, user2Policies.size());
        Assert.assertTrue(user2Policies.containsKey(Provenance.getValue()));
        Assert.assertEquals(1, user2Policies.get(Provenance.getValue()).size());
        Assert.assertTrue(user2Policies.get(Provenance.getValue()).contains(READ));
        Assert.assertTrue(user2Policies.containsKey((((ProvenanceData.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user2Policies.get((((ProvenanceData.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user2Policies.get((((ProvenanceData.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).contains(READ));
        Assert.assertTrue(user2Policies.containsKey((((Data.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user2Policies.get((((Data.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user2Policies.get((((Data.getValue()) + "/process-groups/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).contains(READ));
        // verify user3's policies
        final Map<String, Set<RequestAction>> user3Policies = getResourceActions(policies, user3);
        Assert.assertEquals(6, user3Policies.size());
        Assert.assertTrue(user3Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user3Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user3Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user3Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))));
        Assert.assertEquals(2, user3Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user3Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).contains(WRITE));
        // verify user4's policies
        final Map<String, Set<RequestAction>> user4Policies = getResourceActions(policies, user4);
        Assert.assertEquals(6, user4Policies.size());
        Assert.assertTrue(user4Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user4Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user4Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user4Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user4Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user4Policies.get((((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID))).contains(READ));
        Assert.assertTrue(user4Policies.containsKey(Tenant.getValue()));
        Assert.assertEquals(2, user4Policies.get(Tenant.getValue()).size());
        Assert.assertTrue(user4Policies.get(Tenant.getValue()).contains(WRITE));
        Assert.assertTrue(user4Policies.containsKey(Policy.getValue()));
        Assert.assertEquals(2, user4Policies.get(Policy.getValue()).size());
        Assert.assertTrue(user4Policies.get(Policy.getValue()).contains(WRITE));
        // verify user5's policies
        final Map<String, Set<RequestAction>> user5Policies = getResourceActions(policies, user5);
        Assert.assertEquals(2, user5Policies.size());
        Assert.assertTrue(user5Policies.containsKey(Proxy.getValue()));
        Assert.assertEquals(1, user5Policies.get(Proxy.getValue()).size());
        Assert.assertTrue(user5Policies.get(Proxy.getValue()).contains(WRITE));
        // verify user6's policies
        final Map<String, Set<RequestAction>> user6Policies = getResourceActions(policies, user6);
        Assert.assertEquals(3, user6Policies.size());
        Assert.assertTrue(user6Policies.containsKey(SiteToSite.getValue()));
        Assert.assertEquals(1, user6Policies.get(SiteToSite.getValue()).size());
        Assert.assertTrue(user6Policies.get(SiteToSite.getValue()).contains(READ));
        final Resource inputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(InputPort, "2f7d1606-b090-4be7-a592-a5b70fb55531", "TCP Input"));
        final AccessPolicy inputPortPolicy = accessPolicyProvider.getAccessPolicy(inputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(inputPortPolicy);
        Assert.assertEquals(1, inputPortPolicy.getUsers().size());
        Assert.assertTrue(inputPortPolicy.getUsers().contains(user6.getIdentifier()));
        Assert.assertEquals(1, inputPortPolicy.getGroups().size());
        Assert.assertTrue(inputPortPolicy.getGroups().contains(group1.getIdentifier()));
        final Resource outputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(OutputPort, "2f7d1606-b090-4be7-a592-a5b70fb55532", "TCP Output"));
        final AccessPolicy outputPortPolicy = accessPolicyProvider.getAccessPolicy(outputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(outputPortPolicy);
        Assert.assertEquals(1, outputPortPolicy.getUsers().size());
        Assert.assertTrue(outputPortPolicy.getUsers().contains(user4.getIdentifier()));
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedWithIdentityMappings() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flowWithDns);
        userGroupProvider.setNiFiProperties(properties);
        accessPolicyProvider.setNiFiProperties(properties);
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-with-dns.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final User user4 = userGroupProvider.getUserByIdentity("user4");
        final User user6 = userGroupProvider.getUserByIdentity("user6");
        // verify one group got created
        final Set<Group> groups = userGroupProvider.getGroups();
        final Group group1 = groups.iterator().next();
        final Resource inputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(InputPort, "2f7d1606-b090-4be7-a592-a5b70fb55531", "TCP Input"));
        final AccessPolicy inputPortPolicy = accessPolicyProvider.getAccessPolicy(inputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(inputPortPolicy);
        Assert.assertEquals(1, inputPortPolicy.getUsers().size());
        Assert.assertTrue(inputPortPolicy.getUsers().contains(user6.getIdentifier()));
        Assert.assertEquals(1, inputPortPolicy.getGroups().size());
        Assert.assertTrue(inputPortPolicy.getGroups().contains(group1.getIdentifier()));
        final Resource outputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(OutputPort, "2f7d1606-b090-4be7-a592-a5b70fb55532", "TCP Output"));
        final AccessPolicy outputPortPolicy = accessPolicyProvider.getAccessPolicy(outputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(outputPortPolicy);
        Assert.assertEquals(1, outputPortPolicy.getUsers().size());
        Assert.assertTrue(outputPortPolicy.getUsers().contains(user4.getIdentifier()));
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenBadLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/does-not-exist.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenInitialAdminAndLegacyUsersProvided() throws Exception {
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test
    public void testOnConfiguredWhenInitialAdminNotProvided() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertEquals(0, policies.size());
    }

    @Test
    public void testOnConfiguredWhenInitialAdminProvided() throws Exception {
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertEquals(12, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID);
        boolean foundRootGroupPolicy = false;
        for (AccessPolicy policy : policies) {
            if (policy.getResource().equals(rootGroupResource)) {
                foundRootGroupPolicy = true;
                break;
            }
        }
        Assert.assertTrue(foundRootGroupPolicy);
    }

    @Test
    public void testOnConfiguredWhenInitialAdminProvidedAndNoFlowExists() throws Exception {
        // setup NiFi properties to return a file that does not exist
        properties = Mockito.mock(NiFiProperties.class);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(new File("src/test/resources/does-not-exist.xml.gz"));
        userGroupProvider.setNiFiProperties(properties);
        accessPolicyProvider.setNiFiProperties(properties);
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertEquals(8, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID);
        boolean foundRootGroupPolicy = false;
        for (AccessPolicy policy : policies) {
            if (policy.getResource().equals(rootGroupResource)) {
                foundRootGroupPolicy = true;
                break;
            }
        }
        Assert.assertFalse(foundRootGroupPolicy);
    }

    @Test
    public void testOnConfiguredWhenInitialAdminProvidedAndFlowIsNull() throws Exception {
        // setup NiFi properties to return a file that does not exist
        properties = Mockito.mock(NiFiProperties.class);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(null);
        userGroupProvider.setNiFiProperties(properties);
        accessPolicyProvider.setNiFiProperties(properties);
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertEquals(8, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAccessPolicyProviderTest.ROOT_GROUP_ID);
        boolean foundRootGroupPolicy = false;
        for (AccessPolicy policy : policies) {
            if (policy.getResource().equals(rootGroupResource)) {
                foundRootGroupPolicy = true;
                break;
            }
        }
        Assert.assertFalse(foundRootGroupPolicy);
    }

    @Test
    public void testOnConfiguredWhenInitialAdminProvidedWithIdentityMapping() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1_$2_$3");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flow);
        userGroupProvider.setNiFiProperties(properties);
        accessPolicyProvider.setNiFiProperties(properties);
        final String adminIdentity = "CN=localhost, OU=Apache NiFi, O=Apache, L=Santa Monica, ST=CA, C=US";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        final User adminUser = users.iterator().next();
        Assert.assertEquals("localhost_Apache NiFi_Apache", adminUser.getIdentity());
    }

    @Test
    public void testOnConfiguredWhenNodeIdentitiesProvided() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeIdentity1 = "node1";
        final String nodeIdentity2 = "node2";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_NODE_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_NODE_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "3")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        User nodeUser1 = userGroupProvider.getUserByIdentity(nodeIdentity1);
        User nodeUser2 = userGroupProvider.getUserByIdentity(nodeIdentity2);
        AccessPolicy proxyWritePolicy = accessPolicyProvider.getAccessPolicy(Proxy.getValue(), WRITE);
        Assert.assertNotNull(proxyWritePolicy);
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser1.getIdentifier()));
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser2.getIdentifier()));
    }

    @Test
    public void testOnConfiguredWhenNodeIdentitiesProvidedAndUsersAlreadyExist() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeIdentity1 = "node1";
        final String nodeIdentity2 = "node2";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "3")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS_FOR_ADMIN_AND_NODES);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        User nodeUser1 = userGroupProvider.getUserByIdentity(nodeIdentity1);
        User nodeUser2 = userGroupProvider.getUserByIdentity(nodeIdentity2);
        AccessPolicy proxyWritePolicy = accessPolicyProvider.getAccessPolicy(Proxy.getValue(), WRITE);
        Assert.assertNotNull(proxyWritePolicy);
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser1.getIdentifier()));
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser2.getIdentifier()));
    }

    @Test
    public void testOnConfiguredWhenNodeGroupProvided() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeGroupName = "Cluster Nodes";
        final String nodeGroupIdentifier = "cluster-nodes";
        final String nodeIdentity1 = "node1";
        final String nodeIdentity2 = "node2";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_NODE_GROUP_NAME))).thenReturn(new StandardPropertyValue(nodeGroupName, null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS_FOR_ADMIN_AND_NODE_GROUP);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertNotNull(userGroupProvider.getUserByIdentity(nodeIdentity1));
        Assert.assertNotNull(userGroupProvider.getUserByIdentity(nodeIdentity2));
        AccessPolicy proxyWritePolicy = accessPolicyProvider.getAccessPolicy(Proxy.getValue(), WRITE);
        Assert.assertNotNull(proxyWritePolicy);
        Assert.assertTrue(proxyWritePolicy.getGroups().contains(nodeGroupIdentifier));
    }

    @Test
    public void testOnConfiguredWhenNodeGroupEmpty() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeGroupIdentifier = "cluster-nodes";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_NODE_GROUP_NAME))).thenReturn(new StandardPropertyValue("", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS_FOR_ADMIN_AND_NODE_GROUP);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertNull(accessPolicyProvider.getAccessPolicy(Proxy.getValue(), WRITE));
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenNodeGroupDoesNotExist() throws Exception {
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_NODE_GROUP_NAME))).thenReturn(new StandardPropertyValue("nonexistent", null));
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS_FOR_ADMIN_AND_NODE_GROUP);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test
    public void testOnConfiguredWhenTenantsAndAuthorizationsFileDoesNotExist() {
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, accessPolicyProvider.getAccessPolicies().size());
    }

    @Test
    public void testOnConfiguredWhenAuthorizationsFileDoesNotExist() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, accessPolicyProvider.getAccessPolicies().size());
    }

    @Test
    public void testOnConfiguredWhenTenantsFileDoesNotExist() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, accessPolicyProvider.getAccessPolicies().size());
    }

    @Test
    public void testOnConfiguredWhenRestoreDoesNotExist() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(primaryAuthorizations.length(), restoreAuthorizations.length());
        Assert.assertEquals(primaryTenants.length(), restoreTenants.length());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryDoesNotExist() throws Exception {
        FileAccessPolicyProviderTest.writeFile(restoreAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryAuthorizationsDifferentThanRestore() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(restoreAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWithBadAuthorizationsSchema() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.BAD_SCHEMA_AUTHORIZATIONS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
    }

    @Test
    public void testGetAllUsersGroupsPolicies() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        final Set<AccessPolicy> policies = accessPolicyProvider.getAccessPolicies();
        Assert.assertEquals(2, policies.size());
        boolean foundPolicy1 = false;
        boolean foundPolicy2 = false;
        for (AccessPolicy policy : policies) {
            if ((((((((policy.getIdentifier().equals("policy-1")) && (policy.getResource().equals("/flow"))) && ((policy.getAction()) == (READ))) && ((policy.getGroups().size()) == 2)) && (policy.getGroups().contains("group-1"))) && (policy.getGroups().contains("group-2"))) && ((policy.getUsers().size()) == 1)) && (policy.getUsers().contains("user-1"))) {
                foundPolicy1 = true;
            } else
                if ((((((policy.getIdentifier().equals("policy-2")) && (policy.getResource().equals("/flow"))) && ((policy.getAction()) == (WRITE))) && ((policy.getGroups().size()) == 0)) && ((policy.getUsers().size()) == 1)) && (policy.getUsers().contains("user-2"))) {
                    foundPolicy2 = true;
                }

        }
        Assert.assertTrue(foundPolicy1);
        Assert.assertTrue(foundPolicy2);
    }

    // --------------- AccessPolicy Tests ------------------------
    @Test
    public void testAddAccessPolicy() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-1").resource("resource-1").addUser("user-1").addGroup("group-1").action(READ).build();
        final AccessPolicy returnedPolicy1 = accessPolicyProvider.addAccessPolicy(policy1);
        Assert.assertNotNull(returnedPolicy1);
        Assert.assertEquals(policy1.getIdentifier(), returnedPolicy1.getIdentifier());
        Assert.assertEquals(policy1.getResource(), returnedPolicy1.getResource());
        Assert.assertEquals(policy1.getUsers(), returnedPolicy1.getUsers());
        Assert.assertEquals(policy1.getGroups(), returnedPolicy1.getGroups());
        Assert.assertEquals(policy1.getAction(), returnedPolicy1.getAction());
        Assert.assertEquals(1, accessPolicyProvider.getAccessPolicies().size());
    }

    @Test
    public void testAddAccessPolicyWithEmptyUsersAndGroups() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.EMPTY_AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.EMPTY_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-1").resource("resource-1").action(READ).build();
        final AccessPolicy returnedPolicy1 = accessPolicyProvider.addAccessPolicy(policy1);
        Assert.assertNotNull(returnedPolicy1);
        Assert.assertEquals(policy1.getIdentifier(), returnedPolicy1.getIdentifier());
        Assert.assertEquals(policy1.getResource(), returnedPolicy1.getResource());
        Assert.assertEquals(policy1.getUsers(), returnedPolicy1.getUsers());
        Assert.assertEquals(policy1.getGroups(), returnedPolicy1.getGroups());
        Assert.assertEquals(policy1.getAction(), returnedPolicy1.getAction());
        Assert.assertEquals(1, accessPolicyProvider.getAccessPolicies().size());
    }

    @Test
    public void testGetAccessPolicy() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = accessPolicyProvider.getAccessPolicy("policy-1");
        Assert.assertNotNull(policy);
        Assert.assertEquals("policy-1", policy.getIdentifier());
        Assert.assertEquals("/flow", policy.getResource());
        Assert.assertEquals(READ, policy.getAction());
        Assert.assertEquals(1, policy.getUsers().size());
        Assert.assertTrue(policy.getUsers().contains("user-1"));
        Assert.assertEquals(2, policy.getGroups().size());
        Assert.assertTrue(policy.getGroups().contains("group-1"));
        Assert.assertTrue(policy.getGroups().contains("group-2"));
    }

    @Test
    public void testGetAccessPolicyWhenNotFound() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = accessPolicyProvider.getAccessPolicy("policy-X");
        Assert.assertNull(policy);
    }

    @Test
    public void testUpdateAccessPolicy() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-1").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy updateAccessPolicy = accessPolicyProvider.updateAccessPolicy(policy);
        Assert.assertNotNull(updateAccessPolicy);
        Assert.assertEquals("policy-1", updateAccessPolicy.getIdentifier());
        Assert.assertEquals("/flow", updateAccessPolicy.getResource());
        Assert.assertEquals(1, updateAccessPolicy.getUsers().size());
        Assert.assertTrue(updateAccessPolicy.getUsers().contains("user-A"));
        Assert.assertEquals(1, updateAccessPolicy.getGroups().size());
        Assert.assertTrue(updateAccessPolicy.getGroups().contains("group-A"));
        Assert.assertEquals(READ, updateAccessPolicy.getAction());
    }

    @Test
    public void testUpdateAccessPolicyWhenResourceNotFound() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-XXX").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy updateAccessPolicy = accessPolicyProvider.updateAccessPolicy(policy);
        Assert.assertNull(updateAccessPolicy);
    }

    @Test
    public void testDeleteAccessPolicy() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-1").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy deletedAccessPolicy = accessPolicyProvider.deleteAccessPolicy(policy);
        Assert.assertNotNull(deletedAccessPolicy);
        Assert.assertEquals(policy.getIdentifier(), deletedAccessPolicy.getIdentifier());
        // should have one less policy, and get by policy id should return null
        Assert.assertEquals(1, accessPolicyProvider.getAccessPolicies().size());
        Assert.assertNull(accessPolicyProvider.getAccessPolicy(policy.getIdentifier()));
    }

    @Test
    public void testDeleteAccessPolicyWhenNotFound() throws Exception {
        FileAccessPolicyProviderTest.writeFile(primaryAuthorizations, FileAccessPolicyProviderTest.AUTHORIZATIONS);
        FileAccessPolicyProviderTest.writeFile(primaryTenants, FileAccessPolicyProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        accessPolicyProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, accessPolicyProvider.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-XXX").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy deletedAccessPolicy = accessPolicyProvider.deleteAccessPolicy(policy);
        Assert.assertNull(deletedAccessPolicy);
    }
}

