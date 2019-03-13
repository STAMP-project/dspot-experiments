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


import RequestAction.READ;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestAbstractPolicyBasedAuthorizer {
    static final Resource TEST_RESOURCE = new Resource() {
        @Override
        public String getIdentifier() {
            return "1";
        }

        @Override
        public String getName() {
            return "resource1";
        }

        @Override
        public String getSafeDescription() {
            return "description1";
        }
    };

    @Test
    public void testApproveBasedOnUser() {
        AbstractPolicyBasedAuthorizer authorizer = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        UsersAndAccessPolicies usersAndAccessPolicies = Mockito.mock(UsersAndAccessPolicies.class);
        Mockito.when(authorizer.getUsersAndAccessPolicies()).thenReturn(usersAndAccessPolicies);
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier()).addUser(userIdentifier).action(READ).build();
        Mockito.when(usersAndAccessPolicies.getAccessPolicy(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        Mockito.when(usersAndAccessPolicies.getUser(userIdentity)).thenReturn(user);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        Assert.assertEquals(AuthorizationResult.approved(), authorizer.authorize(request));
    }

    @Test
    public void testApprovedBasedOnGroup() {
        AbstractPolicyBasedAuthorizer authorizer = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        UsersAndAccessPolicies usersAndAccessPolicies = Mockito.mock(UsersAndAccessPolicies.class);
        Mockito.when(authorizer.getUsersAndAccessPolicies()).thenReturn(usersAndAccessPolicies);
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final String groupIdentifier = "groupIdentifier1";
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier()).addGroup(groupIdentifier).action(READ).build();
        Mockito.when(usersAndAccessPolicies.getAccessPolicy(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        Mockito.when(usersAndAccessPolicies.getUser(userIdentity)).thenReturn(user);
        final Group group = new Group.Builder().identifier(groupIdentifier).name(groupIdentifier).addUser(user.getIdentifier()).build();
        Mockito.when(usersAndAccessPolicies.getGroups(userIdentity)).thenReturn(Collections.singleton(group));
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        Assert.assertEquals(AuthorizationResult.approved(), authorizer.authorize(request));
    }

    @Test
    public void testDeny() {
        AbstractPolicyBasedAuthorizer authorizer = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        UsersAndAccessPolicies usersAndAccessPolicies = Mockito.mock(UsersAndAccessPolicies.class);
        Mockito.when(authorizer.getUsersAndAccessPolicies()).thenReturn(usersAndAccessPolicies);
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier()).addUser("NOT_USER_1").action(READ).build();
        Mockito.when(usersAndAccessPolicies.getAccessPolicy(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        Mockito.when(usersAndAccessPolicies.getUser(userIdentity)).thenReturn(user);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        Assert.assertEquals(AuthorizationResult.denied().getResult(), authorizer.authorize(request).getResult());
    }

    @Test
    public void testResourceNotFound() {
        AbstractPolicyBasedAuthorizer authorizer = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        UsersAndAccessPolicies usersAndAccessPolicies = Mockito.mock(UsersAndAccessPolicies.class);
        Mockito.when(authorizer.getUsersAndAccessPolicies()).thenReturn(usersAndAccessPolicies);
        Mockito.when(usersAndAccessPolicies.getAccessPolicy(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(null);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity("userIdentity").resource(TestAbstractPolicyBasedAuthorizer.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        Assert.assertEquals(AuthorizationResult.resourceNotFound(), authorizer.authorize(request));
    }

    @Test
    public void testGetFingerprint() {
        // create the users, groups, and policies
        User user1 = new User.Builder().identifier("user-id-1").identity("user-1").build();
        User user2 = new User.Builder().identifier("user-id-2").identity("user-2").build();
        Group group1 = new Group.Builder().identifier("group-id-1").name("group-1").addUser(user1.getIdentifier()).build();
        Group group2 = new Group.Builder().identifier("group-id-2").name("group-2").addUser(user2.getIdentifier()).build();
        AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-id-1").resource("resource1").action(READ).addUser(user1.getIdentifier()).addUser(user2.getIdentifier()).build();
        AccessPolicy policy2 = new AccessPolicy.Builder().identifier("policy-id-2").resource("resource2").action(READ).addGroup(group1.getIdentifier()).addGroup(group2.getIdentifier()).addUser(user1.getIdentifier()).addUser(user2.getIdentifier()).build();
        // create the first Authorizer
        Set<Group> groups1 = new LinkedHashSet<>();
        groups1.add(group1);
        groups1.add(group2);
        Set<User> users1 = new LinkedHashSet<>();
        users1.add(user1);
        users1.add(user2);
        Set<AccessPolicy> policies1 = new LinkedHashSet<>();
        policies1.add(policy1);
        policies1.add(policy2);
        AbstractPolicyBasedAuthorizer authorizer1 = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        Mockito.when(authorizer1.getGroups()).thenReturn(groups1);
        Mockito.when(authorizer1.getUsers()).thenReturn(users1);
        Mockito.when(authorizer1.getAccessPolicies()).thenReturn(policies1);
        // create the second Authorizer
        Set<Group> groups2 = new LinkedHashSet<>();
        groups2.add(group2);
        groups2.add(group1);
        Set<User> users2 = new LinkedHashSet<>();
        users2.add(user2);
        users2.add(user1);
        Set<AccessPolicy> policies2 = new LinkedHashSet<>();
        policies2.add(policy2);
        policies2.add(policy1);
        AbstractPolicyBasedAuthorizer authorizer2 = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        Mockito.when(authorizer2.getGroups()).thenReturn(groups2);
        Mockito.when(authorizer2.getUsers()).thenReturn(users2);
        Mockito.when(authorizer2.getAccessPolicies()).thenReturn(policies2);
        // compare the fingerprints
        Assert.assertEquals(authorizer1.getFingerprint(), authorizer2.getFingerprint());
    }

    @Test
    public void testInheritFingerprint() {
        User user1 = new User.Builder().identifier("user-id-1").identity("user-1").build();
        User user2 = new User.Builder().identifier("user-id-2").identity("user-2").build();
        Group group1 = new Group.Builder().identifier("group-id-1").name("group-1").addUser(user1.getIdentifier()).build();
        Group group2 = new Group.Builder().identifier("group-id-2").name("group-2").build();
        AccessPolicy policy1 = new AccessPolicy.Builder().identifier("policy-id-1").resource("resource1").action(READ).addUser(user1.getIdentifier()).addUser(user2.getIdentifier()).build();
        AccessPolicy policy2 = new AccessPolicy.Builder().identifier("policy-id-2").resource("resource2").action(READ).addGroup(group1.getIdentifier()).addGroup(group2.getIdentifier()).addUser(user1.getIdentifier()).addUser(user2.getIdentifier()).build();
        // create the first Authorizer
        Set<Group> groups1 = new LinkedHashSet<>();
        groups1.add(group1);
        groups1.add(group2);
        Set<User> users1 = new LinkedHashSet<>();
        users1.add(user1);
        users1.add(user2);
        Set<AccessPolicy> policies1 = new LinkedHashSet<>();
        policies1.add(policy1);
        policies1.add(policy2);
        AbstractPolicyBasedAuthorizer authorizer1 = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        Mockito.when(authorizer1.getGroups()).thenReturn(groups1);
        Mockito.when(authorizer1.getUsers()).thenReturn(users1);
        Mockito.when(authorizer1.getAccessPolicies()).thenReturn(policies1);
        final String fingerprint1 = authorizer1.getFingerprint();
        // make a second authorizer using the memory-backed implementation so we can inherit the fingerprint
        // and then compute a new fingerprint to compare them
        AbstractPolicyBasedAuthorizer authorizer2 = new MockPolicyBasedAuthorizer();
        authorizer2.inheritFingerprint(fingerprint1);
        // computer the fingerprint of the second authorizer and it should be the same as the first
        final String fingerprint2 = authorizer2.getFingerprint();
        Assert.assertEquals(fingerprint1, fingerprint2);
        // all the sets should be equal now after inheriting
        Assert.assertEquals(authorizer1.getUsers(), authorizer2.getUsers());
        Assert.assertEquals(authorizer1.getGroups(), authorizer2.getGroups());
        Assert.assertEquals(authorizer1.getAccessPolicies(), authorizer2.getAccessPolicies());
    }

    @Test
    public void testEmptyAuthorizer() {
        AbstractPolicyBasedAuthorizer authorizer = Mockito.mock(AbstractPolicyBasedAuthorizer.class);
        Mockito.when(authorizer.getGroups()).thenReturn(new HashSet<Group>());
        Mockito.when(authorizer.getUsers()).thenReturn(new HashSet<User>());
        Mockito.when(authorizer.getAccessPolicies()).thenReturn(new HashSet<AccessPolicy>());
        final String fingerprint = authorizer.getFingerprint();
        Assert.assertNotNull(fingerprint);
        Assert.assertTrue(((fingerprint.length()) > 0));
    }
}

