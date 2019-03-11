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


import FileAuthorizer.PROP_LEGACY_AUTHORIZED_USERS_FILE;
import java.io.File;
import java.util.Properties;
import java.util.Set;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static FileUserGroupProvider.PROP_INITIAL_USER_IDENTITY_PREFIX;


public class FileUserGroupProviderTest {
    private static final String EMPTY_TENANTS_CONCISE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<tenants/>";

    private static final String EMPTY_TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<tenants>" + "</tenants>");

    private static final String BAD_SCHEMA_TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ("<tenant>" + "</tenant>");

    private static final String SIMPLE_TENANTS_BY_USER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((("<tenants>" + "  <users>") + "    <user identifier=\"user-1\" identity=\"user-1\"/>") + "    <user identifier=\"user-2\" identity=\"user-2\"/>") + "  </users>") + "</tenants>");

    private static final String TENANTS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((((((((((("<tenants>" + "  <groups>") + "    <group identifier=\"group-1\" name=\"group-1\">") + "       <user identifier=\"user-1\" />") + "    </group>") + "    <group identifier=\"group-2\" name=\"group-2\">") + "       <user identifier=\"user-2\" />") + "    </group>") + "  </groups>") + "  <users>") + "    <user identifier=\"user-1\" identity=\"user-1\" />") + "    <user identifier=\"user-2\" identity=\"user-2\" />") + "  </users>") + "</tenants>");

    private NiFiProperties properties;

    private FileUserGroupProvider userGroupProvider;

    private File primaryTenants;

    private File restoreTenants;

    private AuthorizerConfigurationContext configurationContext;

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users.xml", null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        // verify all users got created correctly
        final Set<User> users = userGroupProvider.getUsers();
        Assert.assertEquals(6, users.size());
        final User user1 = userGroupProvider.getUserByIdentity("user1");
        Assert.assertNotNull(user1);
        final User user2 = userGroupProvider.getUserByIdentity("user2");
        Assert.assertNotNull(user2);
        final User user3 = userGroupProvider.getUserByIdentity("user3");
        Assert.assertNotNull(user3);
        final User user4 = userGroupProvider.getUserByIdentity("user4");
        Assert.assertNotNull(user4);
        final User user5 = userGroupProvider.getUserByIdentity("user5");
        Assert.assertNotNull(user5);
        final User user6 = userGroupProvider.getUserByIdentity("user6");
        Assert.assertNotNull(user6);
        // verify one group got created
        final Set<Group> groups = userGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group group1 = groups.iterator().next();
        Assert.assertEquals("group1", group1.getName());
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedWithIdentityMappings() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreTenants.getParentFile());
        userGroupProvider.setNiFiProperties(properties);
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-with-dns.xml", null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        final User user1 = userGroupProvider.getUserByIdentity("user1");
        Assert.assertNotNull(user1);
        final User user2 = userGroupProvider.getUserByIdentity("user2");
        Assert.assertNotNull(user2);
        final User user3 = userGroupProvider.getUserByIdentity("user3");
        Assert.assertNotNull(user3);
        final User user4 = userGroupProvider.getUserByIdentity("user4");
        Assert.assertNotNull(user4);
        final User user5 = userGroupProvider.getUserByIdentity("user5");
        Assert.assertNotNull(user5);
        final User user6 = userGroupProvider.getUserByIdentity("user6");
        Assert.assertNotNull(user6);
        // verify one group got created
        final Set<Group> groups = userGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group group1 = groups.iterator().next();
        Assert.assertEquals("group1", group1.getName());
    }

    @Test
    public void testOnConfiguredWhenLegacyUsersFileProvidedWithIdentityMappingsAndTransforms() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^CN=(.*?), OU=(.*?), O=(.*?), L=(.*?), ST=(.*?), C=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        props.setProperty("nifi.security.identity.mapping.transform.dn1", "UPPER");
        props.setProperty("nifi.security.group.mapping.pattern.anygroup", "^(.*)$");
        props.setProperty("nifi.security.group.mapping.value.anygroup", "$1");
        props.setProperty("nifi.security.group.mapping.transform.anygroup", "UPPER");
        properties = getNiFiProperties(props);
        Mockito.when(properties.getRestoreDirectory()).thenReturn(restoreTenants.getParentFile());
        userGroupProvider.setNiFiProperties(properties);
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/authorized-users-with-dns.xml", null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        final User user1 = userGroupProvider.getUserByIdentity("USER1");
        Assert.assertNotNull(user1);
        final User user2 = userGroupProvider.getUserByIdentity("USER2");
        Assert.assertNotNull(user2);
        final User user3 = userGroupProvider.getUserByIdentity("USER3");
        Assert.assertNotNull(user3);
        final User user4 = userGroupProvider.getUserByIdentity("USER4");
        Assert.assertNotNull(user4);
        final User user5 = userGroupProvider.getUserByIdentity("USER5");
        Assert.assertNotNull(user5);
        final User user6 = userGroupProvider.getUserByIdentity("USER6");
        Assert.assertNotNull(user6);
        // verify one group got created
        final Set<Group> groups = userGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group group1 = groups.iterator().next();
        Assert.assertEquals("GROUP1", group1.getName());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenBadLegacyUsersFileProvided() throws Exception {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(PROP_LEGACY_AUTHORIZED_USERS_FILE))).thenReturn(new StandardPropertyValue("src/test/resources/does-not-exist.xml", null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testOnConfiguredWhenInitialUsersNotProvided() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        Assert.assertEquals(0, users.size());
    }

    @Test
    public void testOnConfiguredWhenInitialUsersProvided() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeIdentity1 = "node-identity-1";
        final String nodeIdentity2 = "node-identity-2";
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "3")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        Assert.assertEquals(3, users.size());
        Assert.assertTrue(users.contains(new User.Builder().identifierGenerateFromSeed(adminIdentity).identity(adminIdentity).build()));
        Assert.assertTrue(users.contains(new User.Builder().identifierGenerateFromSeed(nodeIdentity1).identity(nodeIdentity1).build()));
        Assert.assertTrue(users.contains(new User.Builder().identifierGenerateFromSeed(nodeIdentity2).identity(nodeIdentity2).build()));
    }

    @Test
    public void testOnConfiguredWhenTenantsExistAndInitialUsersProvided() throws Exception {
        final String adminIdentity = "admin-user";
        final String nodeIdentity1 = "node-identity-1";
        final String nodeIdentity2 = "node-identity-2";
        // despite setting initial users, they will not be loaded as the tenants file is non-empty
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "1")))).thenReturn(new StandardPropertyValue(adminIdentity, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "2")))).thenReturn(new StandardPropertyValue(nodeIdentity1, null));
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(((PROP_INITIAL_USER_IDENTITY_PREFIX) + "3")))).thenReturn(new StandardPropertyValue(nodeIdentity2, null));
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.SIMPLE_TENANTS_BY_USER);
        userGroupProvider.onConfigured(configurationContext);
        final Set<User> users = userGroupProvider.getUsers();
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.contains(new User.Builder().identifier("user-1").identity("user-1").build()));
        Assert.assertTrue(users.contains(new User.Builder().identifier("user-2").identity("user-2").build()));
    }

    @Test
    public void testOnConfiguredWhenTenantsFileDoesNotExist() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, userGroupProvider.getUsers().size());
        Assert.assertEquals(0, userGroupProvider.getGroups().size());
    }

    @Test
    public void testOnConfiguredWhenRestoreDoesNotExist() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(primaryTenants.length(), restoreTenants.length());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryDoesNotExist() throws Exception {
        FileUserGroupProviderTest.writeFile(restoreTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWhenPrimaryTenantsDifferentThanRestore() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS);
        FileUserGroupProviderTest.writeFile(restoreTenants, FileUserGroupProviderTest.EMPTY_TENANTS_CONCISE);
        userGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testOnConfiguredWithBadTenantsSchema() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.BAD_SCHEMA_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testGetAllUsersGroupsPolicies() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = userGroupProvider.getGroups();
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
        final Set<User> users = userGroupProvider.getUsers();
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
    }

    // --------------- User Tests ------------------------
    @Test
    public void testAddUser() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, userGroupProvider.getUsers().size());
        final User user = new User.Builder().identifier("user-1").identity("user-identity-1").build();
        final User addedUser = userGroupProvider.addUser(user);
        Assert.assertNotNull(addedUser);
        Assert.assertEquals(user.getIdentifier(), addedUser.getIdentifier());
        Assert.assertEquals(user.getIdentity(), addedUser.getIdentity());
        final Set<User> users = userGroupProvider.getUsers();
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void testGetUserByIdentifierWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final String identifier = "user-1";
        final User user = userGroupProvider.getUser(identifier);
        Assert.assertNotNull(user);
        Assert.assertEquals(identifier, user.getIdentifier());
    }

    @Test
    public void testGetUserByIdentifierWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final String identifier = "user-X";
        final User user = userGroupProvider.getUser(identifier);
        Assert.assertNull(user);
    }

    @Test
    public void testGetUserByIdentityWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final String identity = "user-1";
        final User user = userGroupProvider.getUserByIdentity(identity);
        Assert.assertNotNull(user);
        Assert.assertEquals(identity, user.getIdentifier());
    }

    @Test
    public void testGetUserByIdentityWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final String identity = "user-X";
        final User user = userGroupProvider.getUserByIdentity(identity);
        Assert.assertNull(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        // retrieve user-1 and verify it exists
        final User user = userGroupProvider.getUser("user-1");
        Assert.assertEquals("user-1", user.getIdentifier());
        // delete user-1
        final User deletedUser = userGroupProvider.deleteUser(user);
        Assert.assertNotNull(deletedUser);
        Assert.assertEquals("user-1", deletedUser.getIdentifier());
        // should be one less user
        Assert.assertEquals(1, userGroupProvider.getUsers().size());
        Assert.assertNull(userGroupProvider.getUser(user.getIdentifier()));
    }

    @Test
    public void testDeleteUserWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        // user that doesn't exist
        final User user = new User.Builder().identifier("user-X").identity("user-identity-X").build();
        // should return null and still have 2 users because nothing was deleted
        final User deletedUser = userGroupProvider.deleteUser(user);
        Assert.assertNull(deletedUser);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
    }

    @Test
    public void testUpdateUserWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final User user = new User.Builder().identifier("user-1").identity("new-identity").build();
        final User updatedUser = userGroupProvider.updateUser(user);
        Assert.assertNotNull(updatedUser);
        Assert.assertEquals(user.getIdentifier(), updatedUser.getIdentifier());
        Assert.assertEquals(user.getIdentity(), updatedUser.getIdentity());
    }

    @Test
    public void testUpdateUserWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getUsers().size());
        final User user = new User.Builder().identifier("user-X").identity("new-identity").build();
        final User updatedUser = userGroupProvider.updateUser(user);
        Assert.assertNull(updatedUser);
    }

    // --------------- Group Tests ------------------------
    @Test
    public void testAddGroup() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, userGroupProvider.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-1").name("group-name-1").build();
        final Group addedGroup = userGroupProvider.addGroup(group);
        Assert.assertNotNull(addedGroup);
        Assert.assertEquals(group.getIdentifier(), addedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), addedGroup.getName());
        Assert.assertEquals(0, addedGroup.getUsers().size());
        final Set<Group> groups = userGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
    }

    @Test
    public void testAddGroupWithUser() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-XXX").name("group-name-XXX").addUser("user-1").build();
        final Group addedGroup = userGroupProvider.addGroup(group);
        Assert.assertNotNull(addedGroup);
        Assert.assertEquals(group.getIdentifier(), addedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), addedGroup.getName());
        Assert.assertEquals(1, addedGroup.getUsers().size());
        final Set<Group> groups = userGroupProvider.getGroups();
        Assert.assertEquals(3, groups.size());
    }

    @Test
    public void testAddGroupWhenUserDoesNotExist() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.EMPTY_TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(0, userGroupProvider.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-1").name("group-name-1").addUser("user1").build();
        userGroupProvider.addGroup(group);
        Assert.assertEquals(1, userGroupProvider.getGroups().size());
    }

    @Test
    public void testGetGroupByIdentifierWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        final String identifier = "group-1";
        final Group group = userGroupProvider.getGroup(identifier);
        Assert.assertNotNull(group);
        Assert.assertEquals(identifier, group.getIdentifier());
    }

    @Test
    public void testGetGroupByIdentifierWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        final String identifier = "group-X";
        final Group group = userGroupProvider.getGroup(identifier);
        Assert.assertNull(group);
    }

    @Test
    public void testDeleteGroupWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        // retrieve group-1
        final Group group = userGroupProvider.getGroup("group-1");
        Assert.assertEquals("group-1", group.getIdentifier());
        // delete group-1
        final Group deletedGroup = userGroupProvider.deleteGroup(group);
        Assert.assertNotNull(deletedGroup);
        Assert.assertEquals("group-1", deletedGroup.getIdentifier());
        // verify there is one less overall group
        Assert.assertEquals(1, userGroupProvider.getGroups().size());
        // verify we can no longer retrieve group-1 by identifier
        Assert.assertNull(userGroupProvider.getGroup(group.getIdentifier()));
    }

    @Test
    public void testDeleteGroupWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        final Group group = new Group.Builder().identifier("group-id-X").name("group-name-X").build();
        final Group deletedGroup = userGroupProvider.deleteGroup(group);
        Assert.assertNull(deletedGroup);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
    }

    @Test
    public void testUpdateGroupWhenFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        // verify user-1 is in group-1 before the update
        final Group groupBefore = userGroupProvider.getGroup("group-1");
        Assert.assertEquals(1, groupBefore.getUsers().size());
        Assert.assertTrue(groupBefore.getUsers().contains("user-1"));
        final Group group = new Group.Builder().identifier("group-1").name("new-name").addUser("user-2").build();
        final Group updatedGroup = userGroupProvider.updateGroup(group);
        Assert.assertEquals(group.getIdentifier(), updatedGroup.getIdentifier());
        Assert.assertEquals(group.getName(), updatedGroup.getName());
        Assert.assertEquals(1, updatedGroup.getUsers().size());
        Assert.assertTrue(updatedGroup.getUsers().contains("user-2"));
    }

    @Test
    public void testUpdateGroupWhenNotFound() throws Exception {
        FileUserGroupProviderTest.writeFile(primaryTenants, FileUserGroupProviderTest.TENANTS);
        userGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
        final Group group = new Group.Builder().identifier("group-X").name("group-X").build();
        final Group updatedGroup = userGroupProvider.updateGroup(group);
        Assert.assertNull(updatedGroup);
        Assert.assertEquals(2, userGroupProvider.getGroups().size());
    }
}

