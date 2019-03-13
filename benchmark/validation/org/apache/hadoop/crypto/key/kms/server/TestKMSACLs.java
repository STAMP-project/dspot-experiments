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
package org.apache.hadoop.crypto.key.kms.server;


import KMSACLs.Type;
import KeyOpType.ALL;
import KeyOpType.DECRYPT_EEK;
import KeyOpType.GENERATE_EEK;
import KeyOpType.MANAGEMENT;
import KeyOpType.READ;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestKMSACLs {
    @Rule
    public final Timeout globalTimeout = new Timeout(180000);

    @Test
    public void testDefaults() {
        final KMSACLs acls = new KMSACLs(new Configuration(false));
        for (KMSACLs.Type type : Type.values()) {
            Assert.assertTrue(acls.hasAccess(type, UserGroupInformation.createRemoteUser("foo")));
        }
    }

    @Test
    public void testCustom() {
        final Configuration conf = new Configuration(false);
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), ((type.toString()) + " "));
        }
        final KMSACLs acls = new KMSACLs(conf);
        for (KMSACLs.Type type : Type.values()) {
            Assert.assertTrue(acls.hasAccess(type, UserGroupInformation.createRemoteUser(type.toString())));
            Assert.assertFalse(acls.hasAccess(type, UserGroupInformation.createRemoteUser("foo")));
        }
    }

    @Test
    public void testKeyAclConfigurationLoad() {
        final Configuration conf = new Configuration(false);
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_1.MANAGEMENT"), "CREATE");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_2.ALL"), "CREATE");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_3.NONEXISTOPERATION"), "CREATE");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "ROLLOVER");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), "DECRYPT_EEK");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "ALL"), "invalid");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "ALL"), "invalid");
        final KMSACLs acls = new KMSACLs(conf);
        Assert.assertTrue(("expected key ACL size is 2 but got " + (acls.keyAcls.size())), ((acls.keyAcls.size()) == 2));
        Assert.assertTrue(("expected whitelist ACL size is 1 but got " + (acls.whitelistKeyAcls.size())), ((acls.whitelistKeyAcls.size()) == 1));
        Assert.assertFalse("ALL should not be allowed for whitelist ACLs.", acls.whitelistKeyAcls.containsKey(ALL));
        Assert.assertTrue(("expected default ACL size is 1 but got " + (acls.defaultKeyAcls.size())), ((acls.defaultKeyAcls.size()) == 1));
        Assert.assertTrue("ALL should not be allowed for default ACLs.", ((acls.defaultKeyAcls.size()) == 1));
    }

    @Test
    public void testKeyAclDuplicateEntries() {
        final Configuration conf = new Configuration(false);
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_1.DECRYPT_EEK"), "decrypt1");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_2.ALL"), "all2");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_1.DECRYPT_EEK"), "decrypt2");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "test_key_2.ALL"), "all1,all3");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "default1");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "*");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "whitelist1");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "*");
        final KMSACLs acls = new KMSACLs(conf);
        Assert.assertTrue(("expected key ACL size is 2 but got " + (acls.keyAcls.size())), ((acls.keyAcls.size()) == 2));
        assertKeyAcl("test_key_1", acls, DECRYPT_EEK, "decrypt2");
        assertKeyAcl("test_key_2", acls, ALL, "all1", "all3");
        assertDefaultKeyAcl(acls, MANAGEMENT);
        assertDefaultKeyAcl(acls, DECRYPT_EEK);
        AccessControlList acl = acls.whitelistKeyAcls.get(DECRYPT_EEK);
        Assert.assertNotNull(acl);
        Assert.assertTrue(acl.isAllAllowed());
    }

    @Test
    public void testKeyAclReload() {
        Configuration conf = new Configuration(false);
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "READ"), "read1");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), "*");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "decrypt1");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "testuser1.ALL"), "testkey1");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "READ"), "admin_read1");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), "");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), "*");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "admin_decrypt1");
        final KMSACLs acls = new KMSACLs(conf);
        // update config and hot-reload.
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "READ"), "read2");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "mgmt1,mgmt2");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), "");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "decrypt2");
        conf.set(((KeyAuthorizationKeyProvider.KEY_ACL) + "testkey1.ALL"), "testkey1,testkey2");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "READ"), "admin_read2");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), "admin_mgmt,admin_mgmt1");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), "");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "admin_decrypt2");
        acls.setKeyACLs(conf);
        assertDefaultKeyAcl(acls, READ, "read2");
        assertDefaultKeyAcl(acls, MANAGEMENT, "mgmt1", "mgmt2");
        assertDefaultKeyAcl(acls, GENERATE_EEK);
        assertDefaultKeyAcl(acls, DECRYPT_EEK, "decrypt2");
        assertKeyAcl("testuser1", acls, ALL, "testkey1");
        assertWhitelistKeyAcl(acls, READ, "admin_read2");
        assertWhitelistKeyAcl(acls, MANAGEMENT, "admin_mgmt", "admin_mgmt1");
        assertWhitelistKeyAcl(acls, GENERATE_EEK);
        assertWhitelistKeyAcl(acls, DECRYPT_EEK, "admin_decrypt2");
        // reloading same config, nothing should change.
        acls.setKeyACLs(conf);
        assertDefaultKeyAcl(acls, READ, "read2");
        assertDefaultKeyAcl(acls, MANAGEMENT, "mgmt1", "mgmt2");
        assertDefaultKeyAcl(acls, GENERATE_EEK);
        assertDefaultKeyAcl(acls, DECRYPT_EEK, "decrypt2");
        assertKeyAcl("testuser1", acls, ALL, "testkey1");
        assertWhitelistKeyAcl(acls, READ, "admin_read2");
        assertWhitelistKeyAcl(acls, MANAGEMENT, "admin_mgmt", "admin_mgmt1");
        assertWhitelistKeyAcl(acls, GENERATE_EEK);
        assertWhitelistKeyAcl(acls, DECRYPT_EEK, "admin_decrypt2");
        // test wildcard.
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "*");
        acls.setKeyACLs(conf);
        AccessControlList acl = acls.defaultKeyAcls.get(DECRYPT_EEK);
        Assert.assertTrue(acl.isAllAllowed());
        Assert.assertTrue(acl.getUsers().isEmpty());
        // everything else should still be the same.
        assertDefaultKeyAcl(acls, READ, "read2");
        assertDefaultKeyAcl(acls, MANAGEMENT, "mgmt1", "mgmt2");
        assertDefaultKeyAcl(acls, GENERATE_EEK);
        assertKeyAcl("testuser1", acls, ALL, "testkey1");
        assertWhitelistKeyAcl(acls, READ, "admin_read2");
        assertWhitelistKeyAcl(acls, MANAGEMENT, "admin_mgmt", "admin_mgmt1");
        assertWhitelistKeyAcl(acls, GENERATE_EEK);
        assertWhitelistKeyAcl(acls, DECRYPT_EEK, "admin_decrypt2");
        // test new configuration should clear other items
        conf = new Configuration();
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), "new");
        acls.setKeyACLs(conf);
        assertDefaultKeyAcl(acls, DECRYPT_EEK, "new");
        Assert.assertTrue(acls.keyAcls.isEmpty());
        Assert.assertTrue(acls.whitelistKeyAcls.isEmpty());
        Assert.assertEquals(("Got unexpected sized acls:" + (acls.defaultKeyAcls)), 1, acls.defaultKeyAcls.size());
    }
}

