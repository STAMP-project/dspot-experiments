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
package org.apache.nifi.ranger.authorization;


import org.apache.nifi.authorization.ConfigurableUserGroupProvider;
import org.apache.nifi.authorization.UserGroupProvider;
import org.apache.nifi.authorization.exception.AuthorizationAccessException;
import org.apache.nifi.authorization.exception.UninheritableAuthorizationsException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ManagedRangerAuthorizerTest {
    private static final String TENANT_FINGERPRINT = "<tenants>" + (((("<user identifier=\"user-id-1\" identity=\"user-1\"></user>" + "<group identifier=\"group-id-1\" name=\"group-1\">") + "<groupUser identifier=\"user-id-1\"></groupUser>") + "</group>") + "</tenants>");

    private static final String EMPTY_FINGERPRINT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + (("<managedRangerAuthorizations>" + "<userGroupProvider/>") + "</managedRangerAuthorizations>");

    private static final String NON_EMPTY_FINGERPRINT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + ((((((((("<managedRangerAuthorizations>" + "<userGroupProvider>") + "&lt;tenants&gt;") + "&lt;user identifier=\"user-id-1\" identity=\"user-1\"&gt;&lt;/user&gt;") + "&lt;group identifier=\"group-id-1\" name=\"group-1\"&gt;") + "&lt;groupUser identifier=\"user-id-1\"&gt;&lt;/groupUser&gt;") + "&lt;/group&gt;") + "&lt;/tenants&gt;") + "</userGroupProvider>") + "</managedRangerAuthorizations>");

    private final String serviceType = "nifiService";

    private final String appId = "nifiAppId";

    @Test
    public void testNonConfigurableFingerPrint() throws Exception {
        final UserGroupProvider userGroupProvider = Mockito.mock(UserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        Assert.assertEquals(ManagedRangerAuthorizerTest.EMPTY_FINGERPRINT, managedRangerAuthorizer.getFingerprint());
    }

    @Test
    public void testConfigurableEmptyFingerPrint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getFingerprint()).thenReturn("");
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        Assert.assertEquals(ManagedRangerAuthorizerTest.EMPTY_FINGERPRINT, managedRangerAuthorizer.getFingerprint());
    }

    @Test
    public void testConfigurableFingerPrint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        Mockito.when(userGroupProvider.getFingerprint()).thenReturn(ManagedRangerAuthorizerTest.TENANT_FINGERPRINT);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        Assert.assertEquals(ManagedRangerAuthorizerTest.NON_EMPTY_FINGERPRINT, managedRangerAuthorizer.getFingerprint());
    }

    @Test
    public void testInheritEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.inheritFingerprint(ManagedRangerAuthorizerTest.EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
    }

    @Test(expected = AuthorizationAccessException.class)
    public void testInheritInvalidFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.inheritFingerprint("not a valid fingerprint");
    }

    @Test
    public void testInheritNonEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.inheritFingerprint(ManagedRangerAuthorizerTest.NON_EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(1)).inheritFingerprint(ManagedRangerAuthorizerTest.TENANT_FINGERPRINT);
    }

    @Test
    public void testCheckInheritEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.checkInheritability(ManagedRangerAuthorizerTest.EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(0)).inheritFingerprint(ArgumentMatchers.anyString());
    }

    @Test(expected = AuthorizationAccessException.class)
    public void testCheckInheritInvalidFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.checkInheritability("not a valid fingerprint");
    }

    @Test
    public void testCheckInheritNonEmptyFingerprint() throws Exception {
        final ConfigurableUserGroupProvider userGroupProvider = Mockito.mock(ConfigurableUserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.checkInheritability(ManagedRangerAuthorizerTest.NON_EMPTY_FINGERPRINT);
        Mockito.verify(userGroupProvider, Mockito.times(1)).checkInheritability(ManagedRangerAuthorizerTest.TENANT_FINGERPRINT);
    }

    @Test(expected = UninheritableAuthorizationsException.class)
    public void testCheckInheritNonConfigurableUserGroupProvider() throws Exception {
        final UserGroupProvider userGroupProvider = Mockito.mock(UserGroupProvider.class);
        final ManagedRangerAuthorizer managedRangerAuthorizer = getStandardManagedAuthorizer(userGroupProvider);
        managedRangerAuthorizer.checkInheritability(ManagedRangerAuthorizerTest.NON_EMPTY_FINGERPRINT);
    }
}

