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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.nifi.authorization.exception.AuthorizationAccessException;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class StandardManagedAuthorizerTest {
    private static final String EMPTY_FINGERPRINT = "<?xml version=\"1.0\" ?>" + ((("<managedAuthorizations>" + "<accessPolicyProvider></accessPolicyProvider>") + "<userGroupProvider></userGroupProvider>") + "</managedAuthorizations>");

    private static final String NON_EMPTY_FINGERPRINT = "<?xml version=\"1.0\" ?>" + ((((((((((((((((("<managedAuthorizations>" + "<accessPolicyProvider>") + "&lt;accessPolicies&gt;") + "&lt;policy identifier=\"policy-id-1\" resource=\"resource2\" actions=\"READ\"&gt;") + "&lt;policyUser identifier=\"user-id-1\"&gt;&lt;/policyUser&gt;") + "&lt;policyGroup identifier=\"group-id-1\"&gt;&lt;/policyGroup&gt;") + "&lt;/policy&gt;") + "&lt;/accessPolicies&gt;") + "</accessPolicyProvider>") + "<userGroupProvider>") + "&lt;tenants&gt;") + "&lt;user identifier=\"user-id-1\" identity=\"user-1\"&gt;&lt;/user&gt;") + "&lt;group identifier=\"group-id-1\" name=\"group-1\"&gt;") + "&lt;groupUser identifier=\"user-id-1\"&gt;&lt;/groupUser&gt;") + "&lt;/group&gt;") + "&lt;/tenants&gt;") + "</userGroupProvider>") + "</managedAuthorizations>");

    private static final String ACCESS_POLICY_FINGERPRINT = "<accessPolicies>" + (((("<policy identifier=\"policy-id-1\" resource=\"resource2\" actions=\"READ\">" + "<policyUser identifier=\"user-id-1\"></policyUser>") + "<policyGroup identifier=\"group-id-1\"></policyGroup>") + "</policy>") + "</accessPolicies>");

    private static final String TENANT_FINGERPRINT = "<tenants>" + (((("<user identifier=\"user-id-1\" identity=\"user-1\"></user>" + "<group identifier=\"group-id-1\" name=\"group-1\">") + "<groupUser identifier=\"user-id-1\"></groupUser>") + "</group>") + "</tenants>");

    private static final Resource TEST_RESOURCE = new Resource() {
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

    @Test(expected = AuthorizerCreationException.class)
    public void testNullAccessPolicyProvider() throws Exception {
        getStandardManagedAuthorizer(null);
    }

    @Test
    public void testEmptyFingerPrint() throws Exception {
        final UserGroupProvider userGroupProvider = Mockito.mock(UserGroupProvider.class);
        final AccessPolicyProvider accessPolicyProvider = Mockito.mock(AccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertEquals(StandardManagedAuthorizerTest.EMPTY_FINGERPRINT, managedAuthorizer.getFingerprint());
    }

    @Test
    public void testNonEmptyFingerPrint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getFingerprint()).thenReturn(StandardManagedAuthorizerTest.TENANT_FINGERPRINT);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getFingerprint()).thenReturn(StandardManagedAuthorizerTest.ACCESS_POLICY_FINGERPRINT);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertEquals(StandardManagedAuthorizerTest.NON_EMPTY_FINGERPRINT, managedAuthorizer.getFingerprint());
    }

    @Test
    public void testInheritEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.inheritFingerprint(StandardManagedAuthorizerTest.EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
        Mockito.verify(accessPolicyProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
    }

    @Test(expected = AuthorizationAccessException.class)
    public void testInheritInvalidFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.inheritFingerprint("not a valid fingerprint");
    }

    @Test
    public void testInheritNonEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.inheritFingerprint(StandardManagedAuthorizerTest.NON_EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(1)).inheritFingerprint(StandardManagedAuthorizerTest.TENANT_FINGERPRINT);
        Mockito.verify(accessPolicyProvider, Mockito.times(1)).inheritFingerprint(StandardManagedAuthorizerTest.ACCESS_POLICY_FINGERPRINT);
    }

    @Test
    public void testCheckInheritEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.checkInheritability(StandardManagedAuthorizerTest.EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
        Mockito.verify(accessPolicyProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
    }

    @Test(expected = AuthorizationAccessException.class)
    public void testCheckInheritInvalidFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.checkInheritability("not a valid fingerprint");
    }

    @Test
    public void testCheckInheritNonEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        managedAuthorizer.checkInheritability(StandardManagedAuthorizerTest.NON_EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(1)).checkInheritability(StandardManagedAuthorizerTest.TENANT_FINGERPRINT);
        Mockito.verify(accessPolicyProvider, Mockito.times(1)).checkInheritability(StandardManagedAuthorizerTest.ACCESS_POLICY_FINGERPRINT);
    }

    @Test
    public void testAuthorizationByUser() throws Exception {
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier()).addUser(userIdentifier).action(READ).build();
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getUserAndGroups(userIdentity)).thenReturn(new UserAndGroups() {
            @Override
            public User getUser() {
                return user;
            }

            @Override
            public Set<Group> getGroups() {
                return Collections.EMPTY_SET;
            }
        });
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getAccessPolicy(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(StandardManagedAuthorizerTest.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertEquals(AuthorizationResult.approved(), managedAuthorizer.authorize(request));
    }

    @Test
    public void testAuthorizationByGroup() throws Exception {
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final String groupIdentifier = "groupIdentifier1";
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        final Group group = new Group.Builder().identifier(groupIdentifier).name(groupIdentifier).addUser(user.getIdentifier()).build();
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier()).addGroup(groupIdentifier).action(READ).build();
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getUserAndGroups(userIdentity)).thenReturn(new UserAndGroups() {
            @Override
            public User getUser() {
                return user;
            }

            @Override
            public Set<Group> getGroups() {
                return Stream.of(group).collect(Collectors.toSet());
            }
        });
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getAccessPolicy(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(StandardManagedAuthorizerTest.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertEquals(AuthorizationResult.approved(), managedAuthorizer.authorize(request));
    }

    @Test
    public void testResourceNotFound() throws Exception {
        final String userIdentity = "userIdentity1";
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getAccessPolicy(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(null);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(StandardManagedAuthorizerTest.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertEquals(AuthorizationResult.resourceNotFound(), managedAuthorizer.authorize(request));
    }

    @Test
    public void testUnauthorizedDueToUnknownUser() throws Exception {
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final String notUser1Identity = "not userIdentity1";
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier()).addUser(userIdentifier).action(READ).build();
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getUserAndGroups(notUser1Identity)).thenReturn(new UserAndGroups() {
            @Override
            public User getUser() {
                return null;
            }

            @Override
            public Set<Group> getGroups() {
                return Collections.EMPTY_SET;
            }
        });
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getAccessPolicy(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(notUser1Identity).resource(StandardManagedAuthorizerTest.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertTrue(AuthorizationResult.denied().getResult().equals(managedAuthorizer.authorize(request).getResult()));
    }

    @Test
    public void testUnauthorizedDueToLackOfPermission() throws Exception {
        final String userIdentifier = "userIdentifier1";
        final String userIdentity = "userIdentity1";
        final User user = new User.Builder().identity(userIdentity).identifier(userIdentifier).build();
        final AccessPolicy policy = new AccessPolicy.Builder().identifier("1").resource(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier()).addUser("userIdentity2").action(READ).build();
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getUserAndGroups(userIdentity)).thenReturn(new UserAndGroups() {
            @Override
            public User getUser() {
                return user;
            }

            @Override
            public Set<Group> getGroups() {
                return Collections.EMPTY_SET;
            }
        });
        final ConfigurableAccessPolicyProvider accessPolicyProvider = Mockito.mock(ConfigurableAccessPolicyProvider.class);
        Mockito.when(accessPolicyProvider.getAccessPolicy(StandardManagedAuthorizerTest.TEST_RESOURCE.getIdentifier(), READ)).thenReturn(policy);
        Mockito.when(accessPolicyProvider.getUserGroupProvider()).thenReturn(userGroupProvider);
        final AuthorizationRequest request = new AuthorizationRequest.Builder().identity(userIdentity).resource(StandardManagedAuthorizerTest.TEST_RESOURCE).action(READ).accessAttempt(true).anonymous(false).build();
        final StandardManagedAuthorizer managedAuthorizer = getStandardManagedAuthorizer(accessPolicyProvider);
        Assert.assertTrue(AuthorizationResult.denied().getResult().equals(managedAuthorizer.authorize(request).getResult()));
    }
}

