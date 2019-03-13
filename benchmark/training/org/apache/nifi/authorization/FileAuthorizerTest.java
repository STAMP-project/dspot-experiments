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
import Result.Approved;
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
import static RequestAction.READ;
import static RequestAction.WRITE;


public class FileAuthorizerTest {
    private static final String EMPTY_AUTHORIZATIONS_CONCISE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<authorizations/>";

    private static final String EMPTY_TENANTS_CONCISE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<tenants/>";

    private static final String EMPTY_AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<authorizations>" + "</authorizations>");

    private static final String EMPTY_TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<tenants>" + "</tenants>");

    private static final String BAD_SCHEMA_AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<authorization>" + "</authorization>");

    private static final String BAD_SCHEMA_TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<tenant>" + "</tenant>");

    private static final String SIMPLE_AUTHORIZATION_BY_USER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (((((("<authorizations>" + "  <policies>") + "      <policy identifier=\"policy-1\" resource=\"/flow\" action=\"R\">") + "        <user identifier=\"user-1\" />") + "      </policy>") + "  </policies>") + "</authorizations>");

    private static final String SIMPLE_TENANTS_BY_USER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((("<tenants>" + "  <users>") + "    <user identifier=\"user-1\" identity=\"user-1\"/>") + "    <user identifier=\"user-2\" identity=\"user-2\"/>") + "  </users>") + "</tenants>");

    private static final String AUTHORIZATIONS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((((((((("<authorizations>" + "  <policies>") + "      <policy identifier=\"policy-1\" resource=\"/flow\" action=\"R\">") + "  <group identifier=\"group-1\" />") + "  <group identifier=\"group-2\" />") + "  <user identifier=\"user-1\" />") + "      </policy>") + "      <policy identifier=\"policy-2\" resource=\"/flow\" action=\"W\">") + "        <user identifier=\"user-2\" />") + "      </policy>") + "  </policies>") + "</authorizations>");

    private static final String TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((((((((((("<tenants>" + "  <groups>") + "    <group identifier=\"group-1\" name=\"group-1\">") + "       <user identifier=\"user-1\" />") + "    </group>") + "    <group identifier=\"group-2\" name=\"group-2\">") + "       <user identifier=\"user-2\" />") + "    </group>") + "  </groups>") + "  <users>") + "    <user identifier=\"user-1\" identity=\"user-1\" />") + "    <user identifier=\"user-2\" identity=\"user-2\" />") + "  </users>") + "</tenants>");

    private static final String TENANTS_FOR_ADMIN_AND_NODES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (((((("<tenants>" + "  <users>") + "    <user identifier=\"admin-user\" identity=\"admin-user\"/>") + "    <user identifier=\"node1\" identity=\"node1\"/>") + "    <user identifier=\"node2\" identity=\"node2\"/>") + "  </users>") + "</tenants>");

    // This is the root group id from the flow.xml.gz in src/test/resources
    private static final String ROOT_GROUP_ID = "e530e14c-adcf-41c2-b5d6-d9a59ba8765c";

    private NiFiProperties properties;

    private FileAuthorizer authorizer;

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
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-multirole.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
        UsersAndAccessPolicies usersAndAccessPolicies = authorizer.getUsersAndAccessPolicies();
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy(Flow.getValue(), READ));
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy(Controller.getValue(), READ));
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy(Controller.getValue(), WRITE));
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy(System.getValue(), READ));
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID)), READ));
        Assert.assertNotNull(usersAndAccessPolicies.getAccessPolicy((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID)), WRITE));
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedAndFlowHasNoPorts() throws Exception {
        properties = Mockito.mock(NiFiProperties.class);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flowNoPorts);
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        boolean foundDataTransferPolicy = false;
        for (AccessPolicy policy : authorizer.getAccessPolicies()) {
            if (policy.getResource().contains(DataTransfer.name())) {
                foundDataTransferPolicy = true;
                break;
            }
        }
        Assert.assertFalse(foundDataTransferPolicy);
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        // verify all users got created correctly
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(6, users.size());
        final User user1 = authorizer.getUserByIdentity("user1");
        Assert.assertNotNull(user1);
        final User user2 = authorizer.getUserByIdentity("user2");
        Assert.assertNotNull(user2);
        final User user3 = authorizer.getUserByIdentity("user3");
        Assert.assertNotNull(user3);
        final User user4 = authorizer.getUserByIdentity("user4");
        Assert.assertNotNull(user4);
        final User user5 = authorizer.getUserByIdentity("user5");
        Assert.assertNotNull(user5);
        final User user6 = authorizer.getUserByIdentity("user6");
        Assert.assertNotNull(user6);
        // verify one group got created
        final Set<Group> groups = authorizer.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group group1 = groups.iterator().next();
        Assert.assertEquals("group1", group1.getName());
        // verify more than one policy got created
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
        Assert.assertTrue(((policies.size()) > 0));
        // verify user1's policies
        final Map<String, Set<RequestAction>> user1Policies = getResourceActions(policies, user1);
        Assert.assertEquals(4, user1Policies.size());
        Assert.assertTrue(user1Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user1Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user1Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user1Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user1Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user1Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).contains(READ));
        // verify user2's policies
        final Map<String, Set<RequestAction>> user2Policies = getResourceActions(policies, user2);
        Assert.assertEquals(3, user2Policies.size());
        Assert.assertTrue(user2Policies.containsKey(Provenance.getValue()));
        Assert.assertEquals(1, user2Policies.get(Provenance.getValue()).size());
        Assert.assertTrue(user2Policies.get(Provenance.getValue()).contains(READ));
        Assert.assertTrue(user2Policies.containsKey((((ProvenanceData.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user2Policies.get((((ProvenanceData.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user2Policies.get((((ProvenanceData.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))).contains(READ));
        Assert.assertTrue(user2Policies.containsKey((((Data.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user2Policies.get((((Data.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user2Policies.get((((Data.getValue()) + "/process-groups/") + (FileAuthorizerTest.ROOT_GROUP_ID))).contains(READ));
        // verify user3's policies
        final Map<String, Set<RequestAction>> user3Policies = getResourceActions(policies, user3);
        Assert.assertEquals(6, user3Policies.size());
        Assert.assertTrue(user3Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user3Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user3Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user3Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))));
        Assert.assertEquals(2, user3Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user3Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).contains(WRITE));
        // verify user4's policies
        final Map<String, Set<RequestAction>> user4Policies = getResourceActions(policies, user4);
        Assert.assertEquals(6, user4Policies.size());
        Assert.assertTrue(user4Policies.containsKey(Flow.getValue()));
        Assert.assertEquals(1, user4Policies.get(Flow.getValue()).size());
        Assert.assertTrue(user4Policies.get(Flow.getValue()).contains(READ));
        Assert.assertTrue(user4Policies.containsKey((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))));
        Assert.assertEquals(1, user4Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).size());
        Assert.assertTrue(user4Policies.get((((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID))).contains(READ));
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
        final AccessPolicy inputPortPolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(inputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(inputPortPolicy);
        Assert.assertEquals(1, inputPortPolicy.getUsers().size());
        Assert.assertTrue(inputPortPolicy.getUsers().contains(user6.getIdentifier()));
        Assert.assertEquals(1, inputPortPolicy.getGroups().size());
        Assert.assertTrue(inputPortPolicy.getGroups().contains(group1.getIdentifier()));
        final Resource outputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(OutputPort, "2f7d1606-b090-4be7-a592-a5b70fb55532", "TCP Output"));
        final AccessPolicy outputPortPolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(outputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(outputPortPolicy);
        Assert.assertEquals(1, outputPortPolicy.getUsers().size());
        Assert.assertTrue(outputPortPolicy.getUsers().contains(user4.getIdentifier()));
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedWithIdentityMappings() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        props.setProperty("nifi.security.group.mapping.pattern.anygroup", "^(.*)$");
        props.setProperty("nifi.security.group.mapping.value.anygroup", "$1");
        props.setProperty("nifi.security.group.mapping.transform.anygroup", "UPPER");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flowWithDns);
        authorizer.setNiFiProperties(properties);
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-with-dns.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final User user1 = authorizer.getUserByIdentity("user1");
        Assert.assertNotNull(user1);
        final User user2 = authorizer.getUserByIdentity("user2");
        Assert.assertNotNull(user2);
        final User user3 = authorizer.getUserByIdentity("user3");
        Assert.assertNotNull(user3);
        final User user4 = authorizer.getUserByIdentity("user4");
        Assert.assertNotNull(user4);
        final User user5 = authorizer.getUserByIdentity("user5");
        Assert.assertNotNull(user5);
        final User user6 = authorizer.getUserByIdentity("user6");
        Assert.assertNotNull(user6);
        // verify one group got created
        final Set<Group> groups = authorizer.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group group1 = groups.iterator().next();
        Assert.assertEquals("GROUP1", group1.getName());
        final Resource inputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(InputPort, "2f7d1606-b090-4be7-a592-a5b70fb55531", "TCP Input"));
        final AccessPolicy inputPortPolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(inputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(inputPortPolicy);
        Assert.assertEquals(1, inputPortPolicy.getUsers().size());
        Assert.assertTrue(inputPortPolicy.getUsers().contains(user6.getIdentifier()));
        Assert.assertEquals(1, inputPortPolicy.getGroups().size());
        Assert.assertTrue(inputPortPolicy.getGroups().contains(group1.getIdentifier()));
        final Resource outputPortResource = ResourceFactory.getDataTransferResource(ResourceFactory.getComponentResource(OutputPort, "2f7d1606-b090-4be7-a592-a5b70fb55532", "TCP Output"));
        final AccessPolicy outputPortPolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(outputPortResource.getIdentifier(), WRITE);
        Assert.assertNotNull(outputPortPolicy);
        Assert.assertEquals(1, outputPortPolicy.getUsers().size());
        Assert.assertTrue(outputPortPolicy.getUsers().contains(user4.getIdentifier()));
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenBadLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/does-not-exist.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenInitialAdminAndLegacyUsersProvided() throws Exception {
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
    }

    @Test
    public void testOnConfiguredWhenInitialAdminNotProvided() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(0, users.size());
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
        Assert.assertEquals(0, policies.size());
    }

    @Test
    public void testOnConfiguredWhenInitialAdminProvided() throws Exception {
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
        Assert.assertEquals(12, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID);
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
        authorizer.setNiFiProperties(properties);
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
        Assert.assertEquals(8, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID);
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
        authorizer.setNiFiProperties(properties);
        final String adminIdentity = "admin-user";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
        final User adminUser = users.iterator().next();
        Assert.assertEquals(adminIdentity, adminUser.getIdentity());
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
        Assert.assertEquals(8, policies.size());
        final String rootGroupResource = ((ProcessGroup.getValue()) + "/") + (FileAuthorizerTest.ROOT_GROUP_ID);
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
        authorizer.setNiFiProperties(properties);
        final String adminIdentity = "CN=localhost, OU=Apache NiFi, O=Apache, L=Santa Monica, ST=CA, C=US";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
        final User adminUser = users.iterator().next();
        Assert.assertEquals("localhost_Apache NiFi_Apache", adminUser.getIdentity());
    }

    @Test
    public void testOnConfiguredWhenNodeIdentitiesProvided() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeIdentity1 = "node1";
        final String nodeIdentity2 = "node2";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        User adminUser = authorizer.getUserByIdentity(adminIdentity);
        Assert.assertNotNull(adminUser);
        User nodeUser1 = authorizer.getUserByIdentity(nodeIdentity1);
        Assert.assertNotNull(nodeUser1);
        User nodeUser2 = authorizer.getUserByIdentity(nodeIdentity2);
        Assert.assertNotNull(nodeUser2);
        AccessPolicy proxyWritePolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(Proxy.getValue(), WRITE);
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
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS_FOR_ADMIN_AND_NODES);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(3, authorizer.getUsers().size());
        User adminUser = authorizer.getUserByIdentity(adminIdentity);
        Assert.assertNotNull(adminUser);
        User nodeUser1 = authorizer.getUserByIdentity(nodeIdentity1);
        Assert.assertNotNull(nodeUser1);
        User nodeUser2 = authorizer.getUserByIdentity(nodeIdentity2);
        Assert.assertNotNull(nodeUser2);
        AccessPolicy proxyWritePolicy = authorizer.getUsersAndAccessPolicies().getAccessPolicy(Proxy.getValue(), WRITE);
        Assert.assertNotNull(proxyWritePolicy);
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser1.getIdentifier()));
        Assert.assertTrue(proxyWritePolicy.getUsers().contains(nodeUser2.getIdentifier()));
    }

    @Test
    public void testOnConfiguredWhenNodeIdentitiesProvidedWithIdentityMappings() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreAuthorizations.getParentFile());
        Mockito.when(properties.getFlowConfigurationFile()).thenReturn(flow);
        authorizer.setNiFiProperties(properties);
        final String adminIdentity = "CN=user1, OU=Apache NiFi, O=Apache, L=Santa Monica, ST=CA, C=US";
        final String nodeIdentity1 = "CN=node1, OU=Apache NiFi, O=Apache, L=Santa Monica, ST=CA, C=US";
        final String nodeIdentity2 = "CN=node2, OU=Apache NiFi, O=Apache, L=Santa Monica, ST=CA, C=US";
        Mockito.when(configurationContext.getProperty(Mockito.eq(PROP_INITIAL_ADMIN_IDENTITY))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(Mockito.eq(((PROP_NODE_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        User adminUser = authorizer.getUserByIdentity("user1");
        Assert.assertNotNull(adminUser);
        User nodeUser1 = authorizer.getUserByIdentity("node1");
        Assert.assertNotNull(nodeUser1);
        User nodeUser2 = authorizer.getUserByIdentity("node2");
        Assert.assertNotNull(nodeUser2);
    }

    @Test
    public void testOnConfiguredWhenTenantsAndAuthorizationsFileDoesNotExist() {
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getAccessPolicies().size());
        Assert.assertEquals(0, authorizer.getUsers().size());
        Assert.assertEquals(0, authorizer.getGroups().size());
    }

    @Test
    public void testOnConfiguredWhenAuthorizationsFileDoesNotExist() throws Exception {
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getAccessPolicies().size());
        Assert.assertEquals(0, authorizer.getUsers().size());
        Assert.assertEquals(0, authorizer.getGroups().size());
    }

    @Test
    public void testOnConfiguredWhenTenantsFileDoesNotExist() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getAccessPolicies().size());
        Assert.assertEquals(0, authorizer.getUsers().size());
        Assert.assertEquals(0, authorizer.getGroups().size());
    }

    @Test
    public void testOnConfiguredWhenRestoreDoesNotExist() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(primaryAuthorizations.length(), restoreAuthorizations.length());
        Assert.assertEquals(primaryTenants.length(), restoreTenants.length());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryDoesNotExist() throws Exception {
        FileAuthorizerTest.writeFile(restoreAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        FileAuthorizerTest.writeFile(restoreTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryAuthorizationsDifferentThanRestore() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(restoreAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS_CONCISE);
        authorizer.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryTenantsDifferentThanRestore() throws Exception {
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        FileAuthorizerTest.writeFile(restoreTenants, FileAuthorizerTest.EMPTY_TENANTS_CONCISE);
        authorizer.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWithBadAuthorizationsSchema() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.BAD_SCHEMA_AUTHORIZATIONS);
        authorizer.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWithBadTenantsSchema() throws Exception {
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.BAD_SCHEMA_TENANTS);
        authorizer.onConfigured(configurationContext);
    }

    @Test
    public void testAuthorizedUserAction() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.SIMPLE_AUTHORIZATION_BY_USER);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.SIMPLE_TENANTS_BY_USER);
        authorizer.onConfigured(configurationContext);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(ResourceFactory.getFlowResource()).identity("user-1").anonymous(false).accessAttempt(true).action(READ).build();
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertTrue(Approved.equals(result.getResult()));
    }

    @Test
    public void testUnauthorizedUser() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.SIMPLE_AUTHORIZATION_BY_USER);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.SIMPLE_TENANTS_BY_USER);
        authorizer.onConfigured(configurationContext);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(ResourceFactory.getFlowResource()).identity("user-2").anonymous(false).accessAttempt(true).action(READ).build();
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertFalse(Approved.equals(result.getResult()));
    }

    @Test
    public void testUnauthorizedAction() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.SIMPLE_AUTHORIZATION_BY_USER);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.SIMPLE_TENANTS_BY_USER);
        authorizer.onConfigured(configurationContext);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(ResourceFactory.getFlowResource()).identity("user-1").anonymous(false).accessAttempt(true).action(WRITE).build();
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertFalse(Approved.equals(result.getResult()));
    }

    @Test
    public void testGetAllUsersGroupsPolicies() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        final Set<Group> groups = authorizer.getGroups();
        Assert.assertEquals(2, groups.size());
        boolean foundGroup1 = false;
        boolean foundGroup2 = false;
        for (Group group : groups) {
            if ((((group.getIdentifier().equals("group-1")) && (group.getName().equals("group-1"))) && ((group.getUsers().size()) == 1)) && (group.getUsers().contains("user-1"))) {
                foundGroup1 = true;
            } else
                if ((((group.getIdentifier().equals("group-2")) && (group.getName().equals("group-2"))) && ((group.getUsers().size()) == 1)) && (group.getUsers().contains("user-2"))) {
                    foundGroup2 = true;
                }

        }
        Assert.assertTrue(foundGroup1);
        Assert.assertTrue(foundGroup2);
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(2, users.size());
        boolean foundUser1 = false;
        boolean foundUser2 = false;
        for (User user : users) {
            if ((user.getIdentifier().equals("user-1")) && (user.getIdentity().equals("user-1"))) {
                foundUser1 = true;
            } else
                if ((user.getIdentifier().equals("user-2")) && (user.getIdentity().equals("user-2"))) {
                    foundUser2 = true;
                }

        }
        Assert.assertTrue(foundUser1);
        Assert.assertTrue(foundUser2);
        final Set<AccessPolicy> policies = authorizer.getAccessPolicies();
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

    // --------------- User Tests ------------------------
    @Test
    public void testAddUser() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getUsers().size());
        final User user = new User.Builder().identifier("user-1").identity("user-identity-1").build();
        final User addedUser = authorizer.addUser(user);
        Assert.assertNotNull(addedUser);
        Assert.assertEquals(user.getIdentifier(), addedUser.getIdentifier());
        Assert.assertEquals(user.getIdentity(), addedUser.getIdentity());
        final Set<User> users = authorizer.getUsers();
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void testGetUserByIdentifierWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final String identifier = "user-1";
        final User user = authorizer.getUser(identifier);
        Assert.assertNotNull(user);
        Assert.assertEquals(identifier, user.getIdentifier());
    }

    @Test
    public void testGetUserByIdentifierWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final String identifier = "user-X";
        final User user = authorizer.getUser(identifier);
        Assert.assertNull(user);
    }

    @Test
    public void testGetUserByIdentityWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final String identity = "user-1";
        final User user = authorizer.getUserByIdentity(identity);
        Assert.assertNotNull(user);
        Assert.assertEquals(identity, user.getIdentifier());
    }

    @Test
    public void testGetUserByIdentityWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final String identity = "user-X";
        final User user = authorizer.getUserByIdentity(identity);
        Assert.assertNull(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        // retrieve user-1 and verify it exists
        final User user = authorizer.getUser("user-1");
        Assert.assertEquals("user-1", user.getIdentifier());
        // delete user-1
        final User deletedUser = authorizer.deleteUser(user);
        Assert.assertNotNull(deletedUser);
        Assert.assertEquals("user-1", deletedUser.getIdentifier());
        // should be one less user
        Assert.assertEquals(1, authorizer.getUsers().size());
        Assert.assertNull(authorizer.getUser(user.getIdentifier()));
    }

    @Test
    public void testDeleteUserWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        // user that doesn't exist
        final User user = new User.Builder().identifier("user-X").identity("user-identity-X").build();
        // should return null and still have 2 users because nothing was deleted
        final User deletedUser = authorizer.deleteUser(user);
        Assert.assertNull(deletedUser);
        Assert.assertEquals(2, authorizer.getUsers().size());
    }

    @Test
    public void testUpdateUserWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final User user = new User.Builder().identifier("user-1").identity("new-identity").build();
        final User updatedUser = authorizer.updateUser(user);
        Assert.assertNotNull(updatedUser);
        Assert.assertEquals(user.getIdentifier(), updatedUser.getIdentifier());
        Assert.assertEquals(user.getIdentity(), updatedUser.getIdentity());
    }

    @Test
    public void testUpdateUserWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getUsers().size());
        final User user = new User.Builder().identifier("user-X").identity("new-identity").build();
        final User updatedUser = authorizer.updateUser(user);
        Assert.assertNull(updatedUser);
    }

    // --------------- Group Tests ------------------------
    @Test
    public void testAddGroup() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-1").name("group-name-1").build();
        final Group addedGroup = authorizer.addGroup(group);
        Assert.assertNotNull(addedGroup);
        Assert.assertEquals(group.getIdentifier(), addedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), addedGroup.getName());
        Assert.assertEquals(0, addedGroup.getUsers().size());
        final Set<Group> groups = authorizer.getGroups();
        Assert.assertEquals(1, groups.size());
    }

    @Test
    public void testAddGroupWithUser() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-XXX").name("group-name-XXX").addUser("user-1").build();
        final Group addedGroup = authorizer.addGroup(group);
        Assert.assertNotNull(addedGroup);
        Assert.assertEquals(group.getIdentifier(), addedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), addedGroup.getName());
        Assert.assertEquals(1, addedGroup.getUsers().size());
        final Set<Group> groups = authorizer.getGroups();
        Assert.assertEquals(3, groups.size());
    }

    @Test
    public void testAddGroupWhenUserDoesNotExist() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-1").name("group-name-1").addUser("user1").build();
        authorizer.addGroup(group);
        Assert.assertEquals(1, authorizer.getGroups().size());
    }

    @Test
    public void testGetGroupByIdentifierWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        final String identifier = "group-1";
        final Group group = authorizer.getGroup(identifier);
        Assert.assertNotNull(group);
        Assert.assertEquals(identifier, group.getIdentifier());
    }

    @Test
    public void testGetGroupByIdentifierWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        final String identifier = "group-X";
        final Group group = authorizer.getGroup(identifier);
        Assert.assertNull(group);
    }

    @Test
    public void testDeleteGroupWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        // retrieve group-1
        final Group group = authorizer.getGroup("group-1");
        Assert.assertEquals("group-1", group.getIdentifier());
        // delete group-1
        final Group deletedGroup = authorizer.deleteGroup(group);
        Assert.assertNotNull(deletedGroup);
        Assert.assertEquals("group-1", deletedGroup.getIdentifier());
        // verify there is one less overall group
        Assert.assertEquals(1, authorizer.getGroups().size());
        // verify we can no longer retrieve group-1 by identifier
        Assert.assertNull(authorizer.getGroup(group.getIdentifier()));
    }

    @Test
    public void testDeleteGroupWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-X").name("group-name-X").build();
        final Group deletedGroup = authorizer.deleteGroup(group);
        Assert.assertNull(deletedGroup);
        Assert.assertEquals(2, authorizer.getGroups().size());
    }

    @Test
    public void testUpdateGroupWhenFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        // verify user-1 is in group-1 before the update
        final Group groupBefore = authorizer.getGroup("group-1");
        Assert.assertEquals(1, groupBefore.getUsers().size());
        Assert.assertTrue(groupBefore.getUsers().contains("user-1"));
        final Group group = new Group.Builder().identifier("group-1").name("new-name").addUser("user-2").build();
        final Group updatedGroup = authorizer.updateGroup(group);
        Assert.assertEquals(group.getIdentifier(), updatedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), updatedGroup.getName());
        Assert.assertEquals(1, updatedGroup.getUsers().size());
        Assert.assertTrue(updatedGroup.getUsers().contains("user-2"));
    }

    @Test
    public void testUpdateGroupWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getGroups().size());
        final Group group = new Group.Builder().identifier("group-X").name("group-X").build();
        final Group updatedGroup = authorizer.updateGroup(group);
        Assert.assertNull(updatedGroup);
        Assert.assertEquals(2, authorizer.getGroups().size());
    }

    // --------------- AccessPolicy Tests ------------------------
    @Test
    public void testAddAccessPolicy() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getAccessPolicies().size());
        final AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-1").resource("resource-1").addUser("user-1").addGroup("group-1").action(READ).build();
        final AccessPolicy returnedPolicy1 = authorizer.addAccessPolicy(policy1);
        Assert.assertNotNull(returnedPolicy1);
        Assert.assertEquals(policy1.getIdentifier(), returnedPolicy1.getIdentifier());
        Assert.assertEquals(policy1.getResource(), returnedPolicy1.getResource());
        Assert.assertEquals(policy1.getUsers(), returnedPolicy1.getUsers());
        Assert.assertEquals(policy1.getGroups(), returnedPolicy1.getGroups());
        Assert.assertEquals(policy1.getAction(), returnedPolicy1.getAction());
        Assert.assertEquals(1, authorizer.getAccessPolicies().size());
        // second policy for the same resource
        final AccessPolicy policy2 = new AccessPolicy.Builder().identifier("policy-2").resource("resource-1").addUser("user-1").addGroup("group-1").action(READ).build();
        final ManagedAuthorizer managedAuthorizer = ((ManagedAuthorizer) (AuthorizerFactory.installIntegrityChecks(authorizer)));
        final ConfigurableAccessPolicyProvider accessPolicyProviderWithChecks = ((ConfigurableAccessPolicyProvider) (managedAuthorizer.getAccessPolicyProvider()));
        try {
            final AccessPolicy returnedPolicy2 = accessPolicyProviderWithChecks.addAccessPolicy(policy2);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
        }
        Assert.assertEquals(1, authorizer.getAccessPolicies().size());
    }

    @Test
    public void testAddAccessPolicyWithEmptyUsersAndGroups() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.EMPTY_AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.EMPTY_TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(0, authorizer.getAccessPolicies().size());
        final AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-1").resource("resource-1").action(READ).build();
        final AccessPolicy returnedPolicy1 = authorizer.addAccessPolicy(policy1);
        Assert.assertNotNull(returnedPolicy1);
        Assert.assertEquals(policy1.getIdentifier(), returnedPolicy1.getIdentifier());
        Assert.assertEquals(policy1.getResource(), returnedPolicy1.getResource());
        Assert.assertEquals(policy1.getUsers(), returnedPolicy1.getUsers());
        Assert.assertEquals(policy1.getGroups(), returnedPolicy1.getGroups());
        Assert.assertEquals(policy1.getAction(), returnedPolicy1.getAction());
        Assert.assertEquals(1, authorizer.getAccessPolicies().size());
    }

    @Test
    public void testGetAccessPolicy() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = authorizer.getAccessPolicy("policy-1");
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
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = authorizer.getAccessPolicy("policy-X");
        Assert.assertNull(policy);
    }

    @Test
    public void testUpdateAccessPolicy() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-1").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy updateAccessPolicy = authorizer.updateAccessPolicy(policy);
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
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-XXX").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy updateAccessPolicy = authorizer.updateAccessPolicy(policy);
        Assert.assertNull(updateAccessPolicy);
    }

    @Test
    public void testDeleteAccessPolicy() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-1").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy deletedAccessPolicy = authorizer.deleteAccessPolicy(policy);
        Assert.assertNotNull(deletedAccessPolicy);
        Assert.assertEquals(policy.getIdentifier(), deletedAccessPolicy.getIdentifier());
        // should have one less policy, and get by policy id should return null
        Assert.assertEquals(1, authorizer.getAccessPolicies().size());
        Assert.assertNull(authorizer.getAccessPolicy(policy.getIdentifier()));
    }

    @Test
    public void testDeleteAccessPolicyWhenNotFound() throws Exception {
        FileAuthorizerTest.writeFile(primaryAuthorizations, FileAuthorizerTest.AUTHORIZATIONS);
        FileAuthorizerTest.writeFile(primaryTenants, FileAuthorizerTest.TENANTS);
        authorizer.onConfigured(configurationContext);
        Assert.assertEquals(2, authorizer.getAccessPolicies().size());
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("policy-XXX").resource("resource-A").addUser("user-A").addGroup("group-A").action(READ).build();
        final AccessPolicy deletedAccessPolicy = authorizer.deleteAccessPolicy(policy);
        Assert.assertNull(deletedAccessPolicy);
    }
}

