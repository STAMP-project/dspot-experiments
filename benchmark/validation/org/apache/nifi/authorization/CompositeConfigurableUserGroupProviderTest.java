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


import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CompositeConfigurableUserGroupProviderTest extends CompositeUserGroupProviderTestBase {
    public static final String USER_5_IDENTIFIER = "user-identifier-5";

    public static final String USER_5_IDENTITY = "user-identity-5";

    public static final String CONFIGURABLE_USER_GROUP_PROVIDER = "configurable-user-group-provider";

    public static final String NOT_CONFIGURABLE_USER_GROUP_PROVIDER = "not-configurable-user-group-provider";

    @Test(expected = AuthorizerCreationException.class)
    public void testNoConfigurableUserGroupProviderSpecified() throws Exception {
        initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), null, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(null, null));
        });
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testUnknownConfigurableUserGroupProvider() throws Exception {
        initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), null, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue("unknown-user-group-provider", null));
        });
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testNonConfigurableUserGroupProvider() throws Exception {
        initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), ( lookup) -> {
            when(lookup.getUserGroupProvider(eq(NOT_CONFIGURABLE_USER_GROUP_PROVIDER))).thenReturn(mock(.class));
        }, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(NOT_CONFIGURABLE_USER_GROUP_PROVIDER, null));
        });
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testDuplicateProviders() throws Exception {
        // Mock UserGroupProviderLookup
        UserGroupProvider configurableUserGroupProvider = getConfigurableUserGroupProvider();
        final UserGroupProviderLookup ugpLookup = Mockito.mock(UserGroupProviderLookup.class);
        Mockito.when(ugpLookup.getUserGroupProvider(ArgumentMatchers.eq(CompositeConfigurableUserGroupProviderTest.CONFIGURABLE_USER_GROUP_PROVIDER))).thenReturn(configurableUserGroupProvider);
        // Mock AuthorizerInitializationContext
        final AuthorizerInitializationContext initializationContext = Mockito.mock(AuthorizerInitializationContext.class);
        Mockito.when(initializationContext.getUserGroupProviderLookup()).thenReturn(ugpLookup);
        // Mock AuthorizerConfigurationContext to introduce the duplicate provider ids
        final AuthorizerConfigurationContext configurationContext = Mockito.mock(AuthorizerConfigurationContext.class);
        Mockito.when(configurationContext.getProperty(CompositeConfigurableUserGroupProvider.PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(CompositeConfigurableUserGroupProviderTest.CONFIGURABLE_USER_GROUP_PROVIDER, null));
        Map<String, String> configurationContextProperties = new HashMap<>();
        configurationContextProperties.put(((CompositeUserGroupProvider.PROP_USER_GROUP_PROVIDER_PREFIX) + "1"), CompositeConfigurableUserGroupProviderTest.CONFIGURABLE_USER_GROUP_PROVIDER);
        configurationContextProperties.put(((CompositeUserGroupProvider.PROP_USER_GROUP_PROVIDER_PREFIX) + "2"), CompositeConfigurableUserGroupProviderTest.NOT_CONFIGURABLE_USER_GROUP_PROVIDER);
        Mockito.when(configurationContext.getProperties()).thenReturn(configurationContextProperties);
        // configure (should throw exception)
        CompositeConfigurableUserGroupProvider provider = new CompositeConfigurableUserGroupProvider();
        provider.initialize(initializationContext);
        provider.onConfigured(configurationContext);
    }

    @Test
    public void testConfigurableUserGroupProviderOnly() throws Exception {
        final UserGroupProvider userGroupProvider = initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), ( lookup) -> {
            when(lookup.getUserGroupProvider(eq(CONFIGURABLE_USER_GROUP_PROVIDER))).thenReturn(getConfigurableUserGroupProvider());
        }, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(CONFIGURABLE_USER_GROUP_PROVIDER, null));
        });
        // users and groups
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        Assert.assertEquals(1, userGroupProvider.getGroups().size());
        // unknown
        Assert.assertNull(userGroupProvider.getUser(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTIFIER));
        Assert.assertNull(userGroupProvider.getUserByIdentity(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY));
        final UserAndGroups unknownUserAndGroups = userGroupProvider.getUserAndGroups(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY);
        Assert.assertNotNull(unknownUserAndGroups);
        Assert.assertNull(unknownUserAndGroups.getUser());
        Assert.assertNull(unknownUserAndGroups.getGroups());
        // providers
        testConfigurableUserGroupProvider(userGroupProvider);
    }

    @Test
    public void testConfigurableUserGroupProviderWithConflictingUserGroupProvider() throws Exception {
        final UserGroupProvider userGroupProvider = initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), ( lookup) -> {
            when(lookup.getUserGroupProvider(eq(CONFIGURABLE_USER_GROUP_PROVIDER))).thenReturn(getConfigurableUserGroupProvider());
        }, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(CONFIGURABLE_USER_GROUP_PROVIDER, null));
        }, getConflictingUserGroupProvider());
        // users and groups
        Assert.assertEquals(3, userGroupProvider.getUsers().size());
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        // unknown
        Assert.assertNull(userGroupProvider.getUser(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTIFIER));
        Assert.assertNull(userGroupProvider.getUserByIdentity(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY));
        final UserAndGroups unknownUserAndGroups = userGroupProvider.getUserAndGroups(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY);
        Assert.assertNotNull(unknownUserAndGroups);
        Assert.assertNull(unknownUserAndGroups.getUser());
        Assert.assertNull(unknownUserAndGroups.getGroups());
        // providers
        try {
            testConfigurableUserGroupProvider(userGroupProvider);
            Assert.assertTrue("Should never get here as we expect the line above to throw an exception", false);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalStateException));
            Assert.assertTrue(e.getMessage().contains(CompositeUserGroupProviderTestBase.USER_1_IDENTITY));
        }
        try {
            testConflictingUserGroupProvider(userGroupProvider);
            Assert.assertTrue("Should never get here as we expect the line above to throw an exception", false);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalStateException));
            Assert.assertTrue(e.getMessage().contains(CompositeUserGroupProviderTestBase.USER_1_IDENTITY));
        }
    }

    @Test
    public void testConfigurableUserGroupProviderWithCollaboratingUserGroupProvider() throws Exception {
        final UserGroupProvider userGroupProvider = initCompositeUserGroupProvider(new CompositeConfigurableUserGroupProvider(), ( lookup) -> {
            when(lookup.getUserGroupProvider(eq(CONFIGURABLE_USER_GROUP_PROVIDER))).thenReturn(getConfigurableUserGroupProvider());
        }, ( configurationContext) -> {
            when(configurationContext.getProperty(PROP_CONFIGURABLE_USER_GROUP_PROVIDER)).thenReturn(new StandardPropertyValue(CONFIGURABLE_USER_GROUP_PROVIDER, null));
        }, getCollaboratingUserGroupProvider());
        // users and groups
        Assert.assertEquals(3, userGroupProvider.getUsers().size());
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        // unknown
        Assert.assertNull(userGroupProvider.getUser(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTIFIER));
        Assert.assertNull(userGroupProvider.getUserByIdentity(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY));
        final UserAndGroups unknownUserAndGroups = userGroupProvider.getUserAndGroups(CompositeUserGroupProviderTestBase.NOT_A_REAL_USER_IDENTITY);
        Assert.assertNotNull(unknownUserAndGroups);
        Assert.assertNull(unknownUserAndGroups.getUser());
        Assert.assertNull(unknownUserAndGroups.getGroups());
        // providers
        final UserAndGroups user1AndGroups = userGroupProvider.getUserAndGroups(CompositeUserGroupProviderTestBase.USER_1_IDENTITY);
        Assert.assertNotNull(user1AndGroups);
        Assert.assertNotNull(user1AndGroups.getUser());
        Assert.assertEquals(2, user1AndGroups.getGroups().size());// from CollaboratingUGP

    }
}

