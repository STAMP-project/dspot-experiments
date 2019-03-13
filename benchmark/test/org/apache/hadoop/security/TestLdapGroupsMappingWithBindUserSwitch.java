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
package org.apache.hadoop.security;


import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test functionality for switching bind user information if
 * AuthenticationExceptions are encountered.
 */
public class TestLdapGroupsMappingWithBindUserSwitch extends TestLdapGroupsMappingBase {
    private static final String TEST_USER_NAME = "some_user";

    @Test
    public void testIncorrectConfiguration() {
        // No bind user configured for user2
        Configuration conf = getBaseConf();
        conf.set(LdapGroupsMapping.BIND_USERS_KEY, "user1,user2");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".user1") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "bindUsername1");
        LdapGroupsMapping groupsMapping = new LdapGroupsMapping();
        try {
            groupsMapping.setConf(conf);
            groupsMapping.getGroups(TestLdapGroupsMappingWithBindUserSwitch.TEST_USER_NAME);
            Assert.fail("Should have failed with RuntimeException");
        } catch (RuntimeException e) {
            GenericTestUtils.assertExceptionContains("Bind username or password not configured for user: user2", e);
        }
    }

    @Test
    public void testBindUserSwitchPasswordPlaintext() throws Exception {
        Configuration conf = getBaseConf();
        conf.set(LdapGroupsMapping.BIND_USERS_KEY, "user1,user2");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".user1") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "bindUsername1");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".user2") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "bindUsername2");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".user1") + (LdapGroupsMapping.BIND_PASSWORD_SUFFIX)), "bindPassword1");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".user2") + (LdapGroupsMapping.BIND_PASSWORD_SUFFIX)), "bindPassword2");
        doTestBindUserSwitch(conf, 1, Arrays.asList("bindUsername1", "bindUsername2"), Arrays.asList("bindPassword1", "bindPassword2"));
    }

    @Test
    public void testBindUserSwitchPasswordFromAlias() throws Exception {
        Configuration conf = getBaseConf();
        conf.set(LdapGroupsMapping.BIND_USERS_KEY, "joe,lukas");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".joe") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "joeBindUsername");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".lukas") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "lukasBindUsername");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".joe") + (LdapGroupsMapping.BIND_PASSWORD_ALIAS_SUFFIX)), "joeBindPasswordAlias");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".lukas") + (LdapGroupsMapping.BIND_PASSWORD_ALIAS_SUFFIX)), "lukasBindPasswordAlias");
        setupCredentialProvider(conf);
        createCredentialForAlias(conf, "joeBindPasswordAlias", "joeBindPassword");
        createCredentialForAlias(conf, "lukasBindPasswordAlias", "lukasBindPassword");
        // Simulate 2 failures to test cycling through the bind users
        List<String> expectedBindUsers = Arrays.asList("joeBindUsername", "lukasBindUsername", "joeBindUsername");
        List<String> expectedBindPasswords = Arrays.asList("joeBindPassword", "lukasBindPassword", "joeBindPassword");
        doTestBindUserSwitch(conf, 2, expectedBindUsers, expectedBindPasswords);
    }

    @Test
    public void testBindUserSwitchPasswordFromFile() throws Exception {
        Configuration conf = getBaseConf();
        conf.setInt(LdapGroupsMapping.LDAP_NUM_ATTEMPTS_KEY, 10);
        conf.set(LdapGroupsMapping.BIND_USERS_KEY, "bob,alice");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".bob") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "bobUsername");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".alice") + (LdapGroupsMapping.BIND_USER_SUFFIX)), "aliceUsername");
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".bob") + (LdapGroupsMapping.BIND_PASSWORD_FILE_SUFFIX)), createPasswordFile("bobPasswordFile1.txt", "bobBindPassword"));
        conf.set((((LdapGroupsMapping.BIND_USERS_KEY) + ".alice") + (LdapGroupsMapping.BIND_PASSWORD_FILE_SUFFIX)), createPasswordFile("alicePasswordFile2.txt", "aliceBindPassword"));
        // Simulate 4 failures to test cycling through the bind users
        List<String> expectedBindUsers = Arrays.asList("bobUsername", "aliceUsername", "bobUsername", "aliceUsername", "bobUsername");
        List<String> expectedBindPasswords = Arrays.asList("bobBindPassword", "aliceBindPassword", "bobBindPassword", "aliceBindPassword", "bobBindPassword");
        doTestBindUserSwitch(conf, 4, expectedBindUsers, expectedBindPasswords);
    }
}

