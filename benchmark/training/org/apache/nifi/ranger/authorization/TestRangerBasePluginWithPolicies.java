/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.ranger.authorization;


import RequestAction.READ;
import RequestAction.WRITE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.nifi.authorization.AccessPolicy;
import org.apache.nifi.authorization.AuthorizerConfigurationContext;
import org.apache.nifi.authorization.Group;
import org.apache.nifi.authorization.User;
import org.apache.nifi.authorization.UserAndGroups;
import org.apache.nifi.authorization.UserGroupProvider;
import org.apache.nifi.authorization.UserGroupProviderInitializationContext;
import org.apache.nifi.authorization.exception.AuthorizationAccessException;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.apache.nifi.authorization.exception.AuthorizerDestructionException;
import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItem;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyResource;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.util.ServicePolicies;
import org.junit.Assert;
import org.junit.Test;


public class TestRangerBasePluginWithPolicies {
    @Test
    public void testPoliciesWithoutUserGroupProvider() {
        final String user1 = "user-1";
        final String group1 = "group-1";
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ")).collect(Collectors.toList()));
        policy1Item.setUsers(Stream.of(user1).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final String resourceIdentifier2 = "/resource-2";
        RangerPolicyResource resource2 = new RangerPolicyResource(resourceIdentifier2);
        final Map<String, RangerPolicyResource> policy2Resources = new HashMap<>();
        policy2Resources.put(resourceIdentifier2, resource2);
        final RangerPolicyItem policy2Item = new RangerPolicyItem();
        policy2Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ"), new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        policy2Item.setGroups(Stream.of(group1).collect(Collectors.toList()));
        final RangerPolicy policy2 = new RangerPolicy();
        policy2.setResources(policy2Resources);
        policy2.setPolicyItems(Stream.of(policy2Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        policies.add(policy2);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the two ranger policies converted into 3 nifi access policies
        final Set<AccessPolicy> accessPolicies = pluginWithPolicies.getAccessPolicies();
        Assert.assertEquals(3, accessPolicies.size());
        // resource 1 -> read but no write
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, READ));
        // read
        final AccessPolicy readResource1 = pluginWithPolicies.getAccessPolicy(resourceIdentifier1, READ);
        Assert.assertNotNull(readResource1);
        Assert.assertTrue(accessPolicies.contains(readResource1));
        Assert.assertTrue(readResource1.equals(pluginWithPolicies.getAccessPolicy(readResource1.getIdentifier())));
        Assert.assertEquals(1, readResource1.getUsers().size());
        Assert.assertTrue(readResource1.getUsers().contains(new User.Builder().identifierGenerateFromSeed(user1).identity(user1).build().getIdentifier()));
        Assert.assertTrue(readResource1.getGroups().isEmpty());
        // but no write
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
        // resource 2 -> read and write
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier2, WRITE));
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier2, READ));
        // read
        final AccessPolicy readResource2 = pluginWithPolicies.getAccessPolicy(resourceIdentifier2, READ);
        Assert.assertNotNull(readResource2);
        Assert.assertTrue(accessPolicies.contains(readResource2));
        Assert.assertTrue(readResource2.equals(pluginWithPolicies.getAccessPolicy(readResource2.getIdentifier())));
        Assert.assertTrue(readResource2.getUsers().isEmpty());
        Assert.assertEquals(1, readResource2.getGroups().size());
        Assert.assertTrue(readResource2.getGroups().contains(new Group.Builder().identifierGenerateFromSeed(group1).name(group1).build().getIdentifier()));
        // and write
        final AccessPolicy writeResource2 = pluginWithPolicies.getAccessPolicy(resourceIdentifier2, READ);
        Assert.assertNotNull(writeResource2);
        Assert.assertTrue(accessPolicies.contains(writeResource2));
        Assert.assertTrue(writeResource2.equals(pluginWithPolicies.getAccessPolicy(writeResource2.getIdentifier())));
        Assert.assertTrue(writeResource2.getUsers().isEmpty());
        Assert.assertEquals(1, writeResource2.getGroups().size());
        Assert.assertTrue(writeResource2.getGroups().contains(new Group.Builder().identifierGenerateFromSeed(group1).name(group1).build().getIdentifier()));
        // resource 3 -> no read or write
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist("resource-3", WRITE));
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist("resource-3", READ));
        // no read or write
        Assert.assertNull(pluginWithPolicies.getAccessPolicy("resource-3", WRITE));
        Assert.assertNull(pluginWithPolicies.getAccessPolicy("resource-3", READ));
    }

    @Test
    public void testNoPolicies() {
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist("non-existent-resource", READ));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy("non-existent-identifier"));
        Assert.assertNull(pluginWithPolicies.getAccessPolicy("non-existent-resource", READ));
    }

    @Test
    public void testDisabledPolicy() {
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ")).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setIsEnabled(false);
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the policy was skipped
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, READ));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, READ));
    }

    @Test
    public void testMissingResourceValue() {
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource();
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the policy was skipped
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
    }

    @Test
    public void testWildcardResourceValue() {
        final String resourceIdentifier1 = "*";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the policy was skipped
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
    }

    @Test
    public void testExcludesPolicy() {
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        resource1.setIsExcludes(true);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the policy was skipped
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
    }

    @Test
    public void testRecursivePolicy() {
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        resource1.setIsRecursive(true);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the policy was skipped
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.getAccessPolicies().isEmpty());
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
    }

    @Test
    public void testDelegateAdmin() {
        final String user1 = "user-1";
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ"), new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        policy1Item.setUsers(Stream.of(user1).collect(Collectors.toList()));
        policy1Item.setDelegateAdmin(true);
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi");
        pluginWithPolicies.setPolicies(servicePolicies);
        Assert.assertEquals(4, pluginWithPolicies.getAccessPolicies().size());
        Assert.assertNotNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, READ));
        Assert.assertNotNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
        Assert.assertNotNull(pluginWithPolicies.getAccessPolicy(("/policies" + resourceIdentifier1), READ));
        Assert.assertNotNull(pluginWithPolicies.getAccessPolicy(("/policies" + resourceIdentifier1), WRITE));
    }

    @Test
    public void testPoliciesWithUserGroupProvider() {
        final String user1 = "user-1";// unknown according to user group provider

        final String user2 = "user-2";// known according to user group provider

        final String group1 = "group-1";// unknown according to user group provider

        final String group2 = "group-2";// known according to user group provider

        final UserGroupProvider userGroupProvider = new UserGroupProvider() {
            @Override
            public Set<User> getUsers() throws AuthorizationAccessException {
                return Stream.of(new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build()).collect(Collectors.toSet());
            }

            @Override
            public User getUser(String identifier) throws AuthorizationAccessException {
                final User u2 = new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build();
                if (u2.getIdentifier().equals(identifier)) {
                    return u2;
                } else {
                    return null;
                }
            }

            @Override
            public User getUserByIdentity(String identity) throws AuthorizationAccessException {
                if (user2.equals(identity)) {
                    return new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build();
                } else {
                    return null;
                }
            }

            @Override
            public Set<Group> getGroups() throws AuthorizationAccessException {
                return Stream.of(new Group.Builder().identifierGenerateFromSeed(group2).name(group2).build()).collect(Collectors.toSet());
            }

            @Override
            public Group getGroup(String identifier) throws AuthorizationAccessException {
                final Group g2 = new Group.Builder().identifierGenerateFromSeed(group2).name(group2).build();
                if (g2.getIdentifier().equals(identifier)) {
                    return g2;
                } else {
                    return null;
                }
            }

            @Override
            public UserAndGroups getUserAndGroups(String identity) throws AuthorizationAccessException {
                if (user2.equals(identity)) {
                    return new UserAndGroups() {
                        @Override
                        public User getUser() {
                            return new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build();
                        }

                        @Override
                        public Set<Group> getGroups() {
                            return Collections.EMPTY_SET;
                        }
                    };
                } else {
                    return null;
                }
            }

            @Override
            public void initialize(UserGroupProviderInitializationContext initializationContext) throws AuthorizerCreationException {
            }

            @Override
            public void onConfigured(AuthorizerConfigurationContext configurationContext) throws AuthorizerCreationException {
            }

            @Override
            public void preDestruction() throws AuthorizerDestructionException {
            }
        };
        final String resourceIdentifier1 = "/resource-1";
        RangerPolicyResource resource1 = new RangerPolicyResource(resourceIdentifier1);
        final Map<String, RangerPolicyResource> policy1Resources = new HashMap<>();
        policy1Resources.put(resourceIdentifier1, resource1);
        final RangerPolicyItem policy1Item = new RangerPolicyItem();
        policy1Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ")).collect(Collectors.toList()));
        policy1Item.setUsers(Stream.of(user1).collect(Collectors.toList()));
        policy1Item.setGroups(Stream.of(group2).collect(Collectors.toList()));
        final RangerPolicy policy1 = new RangerPolicy();
        policy1.setResources(policy1Resources);
        policy1.setPolicyItems(Stream.of(policy1Item).collect(Collectors.toList()));
        final String resourceIdentifier2 = "/resource-2";
        RangerPolicyResource resource2 = new RangerPolicyResource(resourceIdentifier2);
        final Map<String, RangerPolicyResource> policy2Resources = new HashMap<>();
        policy2Resources.put(resourceIdentifier2, resource2);
        final RangerPolicyItem policy2Item = new RangerPolicyItem();
        policy2Item.setAccesses(Stream.of(new RangerPolicyItemAccess("READ"), new RangerPolicyItemAccess("WRITE")).collect(Collectors.toList()));
        policy2Item.setUsers(Stream.of(user2).collect(Collectors.toList()));
        policy2Item.setGroups(Stream.of(group1).collect(Collectors.toList()));
        final RangerPolicy policy2 = new RangerPolicy();
        policy2.setResources(policy2Resources);
        policy2.setPolicyItems(Stream.of(policy2Item).collect(Collectors.toList()));
        final List<RangerPolicy> policies = new ArrayList<>();
        policies.add(policy1);
        policies.add(policy2);
        final RangerServiceDef serviceDef = new RangerServiceDef();
        serviceDef.setName("nifi");
        final ServicePolicies servicePolicies = new ServicePolicies();
        servicePolicies.setPolicies(policies);
        servicePolicies.setServiceDef(serviceDef);
        // set all the policies in the plugin
        final RangerBasePluginWithPolicies pluginWithPolicies = new RangerBasePluginWithPolicies("nifi", "nifi", userGroupProvider);
        pluginWithPolicies.setPolicies(servicePolicies);
        // ensure the two ranger policies converted into 3 nifi access policies
        final Set<AccessPolicy> accessPolicies = pluginWithPolicies.getAccessPolicies();
        Assert.assertEquals(3, accessPolicies.size());
        // resource 1 -> read but no write
        Assert.assertFalse(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, WRITE));
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier1, READ));
        // read
        final AccessPolicy readResource1 = pluginWithPolicies.getAccessPolicy(resourceIdentifier1, READ);
        Assert.assertNotNull(readResource1);
        Assert.assertTrue(accessPolicies.contains(readResource1));
        Assert.assertTrue(readResource1.equals(pluginWithPolicies.getAccessPolicy(readResource1.getIdentifier())));
        Assert.assertTrue(readResource1.getUsers().isEmpty());
        Assert.assertEquals(1, readResource1.getGroups().size());
        Assert.assertTrue(readResource1.getGroups().contains(new Group.Builder().identifierGenerateFromSeed(group2).name(group2).build().getIdentifier()));
        // but no write
        Assert.assertNull(pluginWithPolicies.getAccessPolicy(resourceIdentifier1, WRITE));
        // resource 2 -> read and write
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier2, WRITE));
        Assert.assertTrue(pluginWithPolicies.doesPolicyExist(resourceIdentifier2, READ));
        // read
        final AccessPolicy readResource2 = pluginWithPolicies.getAccessPolicy(resourceIdentifier2, READ);
        Assert.assertNotNull(readResource2);
        Assert.assertTrue(accessPolicies.contains(readResource2));
        Assert.assertTrue(readResource2.equals(pluginWithPolicies.getAccessPolicy(readResource2.getIdentifier())));
        Assert.assertEquals(1, readResource2.getUsers().size());
        Assert.assertTrue(readResource2.getUsers().contains(new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build().getIdentifier()));
        Assert.assertTrue(readResource2.getGroups().isEmpty());
        // and write
        final AccessPolicy writeResource2 = pluginWithPolicies.getAccessPolicy(resourceIdentifier2, READ);
        Assert.assertNotNull(writeResource2);
        Assert.assertTrue(accessPolicies.contains(writeResource2));
        Assert.assertTrue(writeResource2.equals(pluginWithPolicies.getAccessPolicy(writeResource2.getIdentifier())));
        Assert.assertEquals(1, writeResource2.getUsers().size());
        Assert.assertTrue(writeResource2.getUsers().contains(new User.Builder().identifierGenerateFromSeed(user2).identity(user2).build().getIdentifier()));
        Assert.assertTrue(writeResource2.getGroups().isEmpty());
    }
}

