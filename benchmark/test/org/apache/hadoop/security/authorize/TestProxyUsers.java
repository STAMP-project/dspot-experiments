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
package org.apache.hadoop.security.authorize;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_GROUP_MAPPING;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_IMPERSONATION_PROVIDER_CLASS;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestProxyUsers {
    private static final Logger LOG = LoggerFactory.getLogger(TestProxyUsers.class);

    private static final String REAL_USER_NAME = "proxier";

    private static final String PROXY_USER_NAME = "proxied_user";

    private static final String AUTHORIZED_PROXY_USER_NAME = "authorized_proxied_user";

    private static final String[] GROUP_NAMES = new String[]{ "foo_group" };

    private static final String[] NETGROUP_NAMES = new String[]{ "@foo_group" };

    private static final String[] OTHER_GROUP_NAMES = new String[]{ "bar_group" };

    private static final String[] SUDO_GROUP_NAMES = new String[]{ "sudo_proxied_user" };

    private static final String PROXY_IP = "1.2.3.4";

    private static final String PROXY_IP_RANGE = "10.222.0.0/16,10.113.221.221";

    /**
     * Test the netgroups (groups in ACL rules that start with @)
     *
     * This is a  manual test because it requires:
     *   - host setup
     *   - native code compiled
     *   - specify the group mapping class
     *
     * Host setup:
     *
     * /etc/nsswitch.conf should have a line like this:
     * netgroup: files
     *
     * /etc/netgroup should be (the whole file):
     * foo_group (,proxied_user,)
     *
     * To run this test:
     *
     * export JAVA_HOME='path/to/java'
     * mvn test \
     *   -Dtest=TestProxyUsers \
     *   -DTestProxyUsersGroupMapping=$className \
     *
     * where $className is one of the classes that provide group
     * mapping services, i.e. classes that implement
     * GroupMappingServiceProvider interface, at this time:
     *   - org.apache.hadoop.security.JniBasedUnixGroupsNetgroupMapping
     *   - org.apache.hadoop.security.ShellBasedUnixGroupsNetgroupMapping
     */
    @Test
    public void testNetgroups() throws IOException {
        if (!(NativeCodeLoader.isNativeCodeLoaded())) {
            TestProxyUsers.LOG.info(("Not testing netgroups, " + "this test only runs when native code is compiled"));
            return;
        }
        String groupMappingClassName = System.getProperty("TestProxyUsersGroupMapping");
        if (groupMappingClassName == null) {
            TestProxyUsers.LOG.info(("Not testing netgroups, no group mapping class specified, " + (("use -DTestProxyUsersGroupMapping=$className to specify " + "group mapping class (must implement GroupMappingServiceProvider ") + "interface and support netgroups)")));
            return;
        }
        TestProxyUsers.LOG.info(("Testing netgroups using: " + groupMappingClassName));
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_GROUP_MAPPING, groupMappingClassName);
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.NETGROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        Groups groups = Groups.getUserToGroupsMappingService(conf);
        // try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, groups.getGroups(TestProxyUsers.PROXY_USER_NAME).toArray(new String[groups.getGroups(TestProxyUsers.PROXY_USER_NAME).size()]));
        assertAuthorized(proxyUserUgi, TestProxyUsers.PROXY_IP);
    }

    @Test
    public void testProxyUsers() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.GROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a group that's not allowed
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.OTHER_GROUP_NAMES);
        // From good IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testProxyUsersWithUserConf() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserUserConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a user that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a user that's not allowed
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testWildcardGroup() {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a different group (just to make sure we aren't getting spill over
        // from the other test case!)
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.OTHER_GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testWildcardUser() {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserUserConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a user that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a different user (just to make sure we aren't getting spill over
        // from the other test case!)
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.OTHER_GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testWildcardIP() {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.GROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From either IP should be fine
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        assertAuthorized(proxyUserUgi, "1.2.3.5");
        // Now set up an unallowed group
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.OTHER_GROUP_NAMES);
        // Neither IP should be OK
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testIPRange() {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP_RANGE);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "10.222.0.0");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "10.221.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUser() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP_RANGE);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // user is null
        ProxyUsers.authorize(null, "10.222.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullIpAddress() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), "*");
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP_RANGE);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // remote address is null
        ProxyUsers.authorize(proxyUserUgi, null);
    }

    @Test
    public void testWithDuplicateProxyGroups() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.GROUP_NAMES, TestProxyUsers.GROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        Collection<String> groupsToBeProxied = ProxyUsers.getDefaultImpersonationProvider().getProxyGroups().get(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME));
        Assert.assertEquals(1, groupsToBeProxied.size());
    }

    @Test
    public void testWithDuplicateProxyHosts() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.GROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.PROXY_IP, TestProxyUsers.PROXY_IP)));
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        Collection<String> hosts = ProxyUsers.getDefaultImpersonationProvider().getProxyHosts().get(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME));
        Assert.assertEquals(1, hosts.size());
    }

    @Test
    public void testProxyUsersWithProviderOverride() throws Exception {
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_IMPERSONATION_PROVIDER_CLASS, "org.apache.hadoop.security.authorize.TestProxyUsers$TestDummyImpersonationProvider");
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        // First try proxying a group that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createUserForTesting(TestProxyUsers.REAL_USER_NAME, TestProxyUsers.SUDO_GROUP_NAMES);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a group that's not allowed
        realUserUgi = UserGroupInformation.createUserForTesting(TestProxyUsers.REAL_USER_NAME, TestProxyUsers.GROUP_NAMES);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testWithProxyGroupsAndUsersWithSpaces() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserUserConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(((TestProxyUsers.PROXY_USER_NAME) + " "), TestProxyUsers.AUTHORIZED_PROXY_USER_NAME, "ONEMORE")));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME), StringUtils.join(",", Arrays.asList(TestProxyUsers.GROUP_NAMES)));
        conf.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(TestProxyUsers.REAL_USER_NAME), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        Collection<String> groupsToBeProxied = ProxyUsers.getDefaultImpersonationProvider().getProxyGroups().get(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(TestProxyUsers.REAL_USER_NAME));
        Assert.assertEquals(TestProxyUsers.GROUP_NAMES.length, groupsToBeProxied.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyUsersWithNullPrefix() throws Exception {
        ProxyUsers.refreshSuperUserGroupsConfiguration(new Configuration(false), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyUsersWithEmptyPrefix() throws Exception {
        ProxyUsers.refreshSuperUserGroupsConfiguration(new Configuration(false), "");
    }

    @Test
    public void testProxyUsersWithCustomPrefix() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set((("x." + (TestProxyUsers.REAL_USER_NAME)) + ".users"), StringUtils.join(",", Arrays.asList(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME)));
        conf.set((("x." + (TestProxyUsers.REAL_USER_NAME)) + ".hosts"), TestProxyUsers.PROXY_IP);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf, "x");
        // First try proxying a user that's allowed
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
        // Now try proxying a user that's not allowed
        realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // From good IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
        // From bad IP
        assertNotAuthorized(proxyUserUgi, "1.2.3.5");
    }

    @Test
    public void testNoHostsForUsers() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set((("y." + (TestProxyUsers.REAL_USER_NAME)) + ".users"), StringUtils.join(",", Arrays.asList(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME)));
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf, "y");
        UserGroupInformation realUserUgi = UserGroupInformation.createRemoteUser(TestProxyUsers.REAL_USER_NAME);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUserForTesting(TestProxyUsers.AUTHORIZED_PROXY_USER_NAME, realUserUgi, TestProxyUsers.GROUP_NAMES);
        // IP doesn't matter
        assertNotAuthorized(proxyUserUgi, "1.2.3.4");
    }

    static class TestDummyImpersonationProvider implements ImpersonationProvider {
        @Override
        public void init(String configurationPrefix) {
        }

        /**
         * Authorize a user (superuser) to impersonate another user (user1) if the
         * superuser belongs to the group "sudo_user1" .
         */
        public void authorize(UserGroupInformation user, String remoteAddress) throws AuthorizationException {
            UserGroupInformation superUser = user.getRealUser();
            String sudoGroupName = "sudo_" + (user.getShortUserName());
            if (!(Arrays.asList(superUser.getGroupNames()).contains(sudoGroupName))) {
                throw new AuthorizationException(((("User: " + (superUser.getUserName())) + " is not allowed to impersonate ") + (user.getUserName())));
            }
        }

        @Override
        public void setConf(Configuration conf) {
        }

        @Override
        public Configuration getConf() {
            return null;
        }
    }
}

