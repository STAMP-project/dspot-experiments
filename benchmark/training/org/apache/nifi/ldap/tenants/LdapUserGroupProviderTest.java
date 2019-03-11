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
package org.apache.nifi.ldap.tenants;


import SearchScope.OBJECT;
import SearchScope.SUBTREE;
import java.util.Properties;
import java.util.Set;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.authorization.AuthorizerConfigurationContext;
import org.apache.nifi.authorization.Group;
import org.apache.nifi.authorization.UserAndGroups;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@CreateDS(name = "nifi-example", partitions = { @CreatePartition(name = "example", suffix = "o=nifi") })
@ApplyLdifFiles("nifi-example.ldif")
public class LdapUserGroupProviderTest extends AbstractLdapTestUnit {
    private static final String USER_SEARCH_BASE = "ou=users,o=nifi";

    private static final String GROUP_SEARCH_BASE = "ou=groups,o=nifi";

    private LdapUserGroupProvider ldapUserGroupProvider;

    @Test(expected = AuthorizerCreationException.class)
    public void testNoSearchBasesSpecified() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, null);
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testUserSearchBaseSpecifiedButNoUserObjectClass() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_OBJECT_CLASS)).thenReturn(new StandardPropertyValue(null, null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testUserSearchBaseSpecifiedButNoUserSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(null, null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testInvalidUserSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue("not-valid", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testSearchUsersWithNoIdentityAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("cn=User 1,ou=users,o=nifi"));
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersWithUidIdentityAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("user1"));
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersWithCnIdentityAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("User 1"));
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersObjectSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(OBJECT.name(), null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertTrue(ldapUserGroupProvider.getUsers().isEmpty());
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersSubtreeSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("o=nifi", null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(SUBTREE.name(), null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(9, ldapUserGroupProvider.getUsers().size());
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersWithFilter() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(uid=user1)", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(1, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("user1"));
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersWithPaging() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_PAGE_SIZE)).thenReturn(new StandardPropertyValue("1", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchUsersWithGroupingNoGroupName() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of memberof

        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertEquals(2, ldapUserGroupProvider.getGroups().size());
        final UserAndGroups userAndGroups = ldapUserGroupProvider.getUserAndGroups("user4");
        Assert.assertNotNull(userAndGroups.getUser());
        Assert.assertEquals(1, userAndGroups.getGroups().size());
        Assert.assertEquals("cn=team1,ou=groups,o=nifi", userAndGroups.getGroups().iterator().next().getName());
    }

    @Test
    public void testSearchUsersWithGroupingAndGroupName() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of memberof

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        Assert.assertEquals(2, ldapUserGroupProvider.getGroups().size());
        final UserAndGroups userAndGroups = ldapUserGroupProvider.getUserAndGroups("user4");
        Assert.assertNotNull(userAndGroups.getUser());
        Assert.assertEquals(1, userAndGroups.getGroups().size());
        Assert.assertEquals("team1", userAndGroups.getGroups().iterator().next().getName());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testSearchGroupsWithoutMemberAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testGroupSearchBaseSpecifiedButNoGroupObjectClass() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_OBJECT_CLASS)).thenReturn(new StandardPropertyValue(null, null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testUserSearchBaseSpecifiedButNoGroupSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(null, null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testInvalidGroupSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue("not-valid", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testSearchGroupsWithNoNameAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        Assert.assertEquals(1, groups.stream().filter(( group) -> "cn=admins,ou=groups,o=nifi".equals(group.getName())).count());
    }

    @Test
    public void testSearchGroupsWithPaging() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_PAGE_SIZE)).thenReturn(new StandardPropertyValue("1", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(4, ldapUserGroupProvider.getGroups().size());
    }

    @Test
    public void testSearchGroupsObjectSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(OBJECT.name(), null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertTrue(ldapUserGroupProvider.getUsers().isEmpty());
        Assert.assertTrue(ldapUserGroupProvider.getGroups().isEmpty());
    }

    @Test
    public void testSearchGroupsSubtreeSearchScope() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, "o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_SCOPE)).thenReturn(new StandardPropertyValue(SUBTREE.name(), null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(4, ldapUserGroupProvider.getGroups().size());
    }

    @Test
    public void testSearchGroupsWithNameAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group admins = groups.stream().filter(( group) -> "admins".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(admins);
        Assert.assertFalse(admins.getUsers().isEmpty());
        Assert.assertEquals(1, admins.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "cn=User 1,ou=users,o=nifi".equals(user.getIdentity())).count());
    }

    @Test
    public void testSearchGroupsWithNoNameAndUserIdentityUidAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group admins = groups.stream().filter(( group) -> "cn=admins,ou=groups,o=nifi".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(admins);
        Assert.assertFalse(admins.getUsers().isEmpty());
        Assert.assertEquals(1, admins.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user1".equals(user.getIdentity())).count());
    }

    @Test
    public void testSearchGroupsWithNameAndUserIdentityCnAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group admins = groups.stream().filter(( group) -> "admins".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(admins);
        Assert.assertFalse(admins.getUsers().isEmpty());
        Assert.assertEquals(1, admins.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "User 1".equals(user.getIdentity())).count());
    }

    @Test
    public void testSearchGroupsWithFilter() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(cn=admins)", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        Assert.assertEquals(1, groups.stream().filter(( group) -> "cn=admins,ou=groups,o=nifi".equals(group.getName())).count());
    }

    @Test
    public void testSearchUsersAndGroupsNoMembership() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        groups.forEach(( group) -> assertTrue(group.getUsers().isEmpty()));
    }

    @Test
    public void testSearchUsersAndGroupsMembershipThroughUsers() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of memberof

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group team1 = groups.stream().filter(( group) -> "team1".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team1);
        Assert.assertEquals(2, team1.getUsers().size());
        Assert.assertEquals(2, team1.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> ("user4".equals(user.getIdentity())) || ("user5".equals(user.getIdentity()))).count());
        final Group team2 = groups.stream().filter(( group) -> "team2".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team2);
        Assert.assertEquals(2, team2.getUsers().size());
        Assert.assertEquals(2, team2.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> ("user6".equals(user.getIdentity())) || ("user7".equals(user.getIdentity()))).count());
    }

    @Test
    public void testSearchUsersAndGroupsMembershipThroughGroups() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group admins = groups.stream().filter(( group) -> "admins".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(admins);
        Assert.assertEquals(2, admins.getUsers().size());
        Assert.assertEquals(2, admins.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> ("user1".equals(user.getIdentity())) || ("user3".equals(user.getIdentity()))).count());
        final Group readOnly = groups.stream().filter(( group) -> "read-only".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(readOnly);
        Assert.assertEquals(1, readOnly.getUsers().size());
        Assert.assertEquals(1, readOnly.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user2".equals(user.getIdentity())).count());
        final Group team1 = groups.stream().filter(( group) -> "team1".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team1);
        Assert.assertEquals(1, team1.getUsers().size());
        Assert.assertEquals(1, team1.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user1".equals(user.getIdentity())).count());
        final Group team2 = groups.stream().filter(( group) -> "team2".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team2);
        Assert.assertEquals(1, team2.getUsers().size());
        Assert.assertEquals(1, team2.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user1".equals(user.getIdentity())).count());
    }

    @Test
    public void testSearchUsersAndGroupsMembershipThroughUsersAndGroups() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of memberof

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("member", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(8, ldapUserGroupProvider.getUsers().size());
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(4, groups.size());
        final Group admins = groups.stream().filter(( group) -> "admins".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(admins);
        Assert.assertEquals(2, admins.getUsers().size());
        Assert.assertEquals(2, admins.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> ("user1".equals(user.getIdentity())) || ("user3".equals(user.getIdentity()))).count());
        final Group readOnly = groups.stream().filter(( group) -> "read-only".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(readOnly);
        Assert.assertEquals(1, readOnly.getUsers().size());
        Assert.assertEquals(1, readOnly.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user2".equals(user.getIdentity())).count());
        final Group team1 = groups.stream().filter(( group) -> "team1".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team1);
        Assert.assertEquals(3, team1.getUsers().size());
        Assert.assertEquals(3, team1.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> (("user1".equals(user.getIdentity())) || ("user4".equals(user.getIdentity()))) || ("user5".equals(user.getIdentity()))).count());
        final Group team2 = groups.stream().filter(( group) -> "team2".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team2);
        Assert.assertEquals(3, team2.getUsers().size());
        Assert.assertEquals(3, team2.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> (("user1".equals(user.getIdentity())) || ("user6".equals(user.getIdentity()))) || ("user7".equals(user.getIdentity()))).count());
    }

    @Test
    public void testUserIdentityMapping() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^cn=(.*?),o=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        final NiFiProperties properties = getNiFiProperties(props);
        ldapUserGroupProvider.setNiFiProperties(properties);
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(uid=user1)", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(1, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("User 1,ou=users"));
    }

    @Test
    public void testUserIdentityMappingWithTransforms() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^cn=(.*?),ou=(.*?),o=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        props.setProperty("nifi.security.identity.mapping.transform.dn1", "UPPER");
        final NiFiProperties properties = getNiFiProperties(props);
        ldapUserGroupProvider.setNiFiProperties(properties);
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(uid=user1)", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(1, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("USER 1"));
    }

    @Test
    public void testUserIdentityAndGroupMappingWithTransforms() throws Exception {
        final Properties props = new Properties();
        props.setProperty("nifi.security.identity.mapping.pattern.dn1", "^cn=(.*?),ou=(.*?),o=(.*?)$");
        props.setProperty("nifi.security.identity.mapping.value.dn1", "$1");
        props.setProperty("nifi.security.identity.mapping.transform.dn1", "UPPER");
        props.setProperty("nifi.security.group.mapping.pattern.dn1", "^cn=(.*?),ou=(.*?),o=(.*?)$");
        props.setProperty("nifi.security.group.mapping.value.dn1", "$1");
        props.setProperty("nifi.security.group.mapping.transform.dn1", "UPPER");
        final NiFiProperties properties = getNiFiProperties(props);
        ldapUserGroupProvider.setNiFiProperties(properties);
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(LdapUserGroupProviderTest.USER_SEARCH_BASE, LdapUserGroupProviderTest.GROUP_SEARCH_BASE);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(uid=user1)", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_SEARCH_FILTER)).thenReturn(new StandardPropertyValue("(cn=admins)", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        Assert.assertEquals(1, ldapUserGroupProvider.getUsers().size());
        Assert.assertNotNull(ldapUserGroupProvider.getUserByIdentity("USER 1"));
        Assert.assertEquals(1, ldapUserGroupProvider.getGroups().size());
        Assert.assertEquals("ADMINS", ldapUserGroupProvider.getGroups().iterator().next().getName());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testReferencedGroupAttributeWithoutGroupSearchBase() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("ou=users-2,o=nifi", null);
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_REFERENCED_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testReferencedGroupWithoutDefiningReferencedAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("ou=users-2,o=nifi", "ou=groups-2,o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_OBJECT_CLASS)).thenReturn(new StandardPropertyValue("room", null));// using room due to reqs of groupOfNames

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of member

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_OBJECT_CLASS)).thenReturn(new StandardPropertyValue("room", null));// using room due to reqs of groupOfNames

        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group team3 = groups.stream().filter(( group) -> "team3".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team3);
        Assert.assertTrue(team3.getUsers().isEmpty());
    }

    @Test
    public void testReferencedGroupUsingReferencedAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("ou=users-2,o=nifi", "ou=groups-2,o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of member

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_GROUP_REFERENCED_GROUP_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_OBJECT_CLASS)).thenReturn(new StandardPropertyValue("room", null));// using room because groupOfNames requires a member

        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group team3 = groups.stream().filter(( group) -> "team3".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team3);
        Assert.assertEquals(1, team3.getUsers().size());
        Assert.assertEquals(1, team3.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "user9".equals(user.getIdentity())).count());
    }

    @Test(expected = AuthorizerCreationException.class)
    public void testReferencedUserWithoutUserSearchBase() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration(null, "ou=groups-2,o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_REFERENCED_USER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
    }

    @Test
    public void testReferencedUserWithoutDefiningReferencedAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("ou=users-2,o=nifi", "ou=groups-2,o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_OBJECT_CLASS)).thenReturn(new StandardPropertyValue("room", null));// using room due to reqs of groupOfNames

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of member

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group team3 = groups.stream().filter(( group) -> "team3".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team3);
        Assert.assertTrue(team3.getUsers().isEmpty());
    }

    @Test
    public void testReferencedUserUsingReferencedAttribute() throws Exception {
        final AuthorizerConfigurationContext configurationContext = getBaseConfiguration("ou=users-2,o=nifi", "ou=groups-2,o=nifi");
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_USER_IDENTITY_ATTRIBUTE)).thenReturn(new StandardPropertyValue("sn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_OBJECT_CLASS)).thenReturn(new StandardPropertyValue("room", null));// using room due to reqs of groupOfNames

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("description", null));// using description in lieu of member

        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_NAME_ATTRIBUTE)).thenReturn(new StandardPropertyValue("cn", null));
        Mockito.when(configurationContext.getProperty(LdapUserGroupProvider.PROP_GROUP_MEMBER_REFERENCED_USER_ATTRIBUTE)).thenReturn(new StandardPropertyValue("uid", null));// does not need to be the same as user id attr

        ldapUserGroupProvider.onConfigured(configurationContext);
        final Set<Group> groups = ldapUserGroupProvider.getGroups();
        Assert.assertEquals(1, groups.size());
        final Group team3 = groups.stream().filter(( group) -> "team3".equals(group.getName())).findFirst().orElse(null);
        Assert.assertNotNull(team3);
        Assert.assertEquals(1, team3.getUsers().size());
        Assert.assertEquals(1, team3.getUsers().stream().map(( userIdentifier) -> ldapUserGroupProvider.getUser(userIdentifier)).filter(( user) -> "User9".equals(user.getIdentity())).count());
    }
}

