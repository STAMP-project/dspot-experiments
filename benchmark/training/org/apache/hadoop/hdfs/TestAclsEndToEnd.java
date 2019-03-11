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
package org.apache.hadoop.hdfs;


import KMSConfiguration.WHITELIST_KEY_ACL_PREFIX;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.kms.server.KMSConfiguration;
import org.apache.hadoop.crypto.key.kms.server.KeyAuthorizationKeyProvider;
import org.apache.hadoop.crypto.key.kms.server.MiniKMS;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the ACLs system through the full code path.  It overlaps
 * slightly with the ACL tests in common, but the approach is more holistic.
 *
 * <b>NOTE:</b> Because of the mechanics of JAXP, when the KMS config files are
 * written to disk, a config param with a blank value ("") will be written in a
 * way that the KMS will read as unset, which is different from blank. For this
 * reason, when testing the effects of blank config params, this test class
 * sets the values of those config params to a space (" ").  A whitespace value
 * will be preserved by JAXP when writing out the config files and will be
 * interpreted by KMS as a blank value. (The KMS strips whitespace from ACL
 * values before interpreting them.)
 */
public class TestAclsEndToEnd {
    private static final Logger LOG = LoggerFactory.getLogger(TestAclsEndToEnd.class.getName());

    private static final String TEXT = "The blue zone is for loading and unloading only. " + "Please park in the red zone.";

    private static final Path ZONE1 = new Path("/tmp/BLUEZONE");

    private static final Path ZONE2 = new Path("/tmp/REDZONE");

    private static final Path ZONE3 = new Path("/tmp/LOADINGZONE");

    private static final Path ZONE4 = new Path("/tmp/UNLOADINGZONE");

    private static final Path FILE1 = new Path(TestAclsEndToEnd.ZONE1, "file1");

    private static final Path FILE1A = new Path(TestAclsEndToEnd.ZONE1, "file1a");

    private static final Path FILE2 = new Path(TestAclsEndToEnd.ZONE2, "file2");

    private static final Path FILE3 = new Path(TestAclsEndToEnd.ZONE3, "file3");

    private static final Path FILE4 = new Path(TestAclsEndToEnd.ZONE4, "file4");

    private static final String KEY1 = "key1";

    private static final String KEY2 = "key2";

    private static final String KEY3 = "key3";

    private static UserGroupInformation realUgi;

    private static String realUser;

    private MiniKMS miniKMS;

    private File kmsDir;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    /**
     * Test the full life cycle of a key using a config with whitelist key ACLs.
     * The configuration used is the correct configuration to pass the full ACL
     * test in {@link #doFullAclTest()}.
     *
     * @throws Exception
     * 		thrown on test failure
     */
    @Test
    public void testGoodWithWhitelist() throws Exception {
        UserGroupInformation hdfsUgi = UserGroupInformation.createProxyUserForTesting("hdfs", TestAclsEndToEnd.realUgi, new String[]{ "supergroup" });
        UserGroupInformation keyadminUgi = UserGroupInformation.createProxyUserForTesting("keyadmin", TestAclsEndToEnd.realUgi, new String[]{ "keyadmin" });
        UserGroupInformation userUgi = UserGroupInformation.createProxyUserForTesting("user", TestAclsEndToEnd.realUgi, new String[]{ "staff" });
        Configuration conf = TestAclsEndToEnd.getBaseConf(hdfsUgi, keyadminUgi);
        TestAclsEndToEnd.setBlacklistAcls(conf, hdfsUgi);
        TestAclsEndToEnd.setKeyAcls(conf, WHITELIST_KEY_ACL_PREFIX, hdfsUgi, keyadminUgi, userUgi);
        doFullAclTest(conf, hdfsUgi, keyadminUgi, userUgi);
    }

    /**
     * Test the full life cycle of a key using a config with key ACLs.
     * The configuration used is the correct configuration to pass the full ACL
     * test in {@link #doFullAclTest()}.
     *
     * @throws Exception
     * 		thrown on test failure
     */
    @Test
    public void testGoodWithKeyAcls() throws Exception {
        UserGroupInformation hdfsUgi = UserGroupInformation.createProxyUserForTesting("hdfs", TestAclsEndToEnd.realUgi, new String[]{ "supergroup" });
        UserGroupInformation keyadminUgi = UserGroupInformation.createProxyUserForTesting("keyadmin", TestAclsEndToEnd.realUgi, new String[]{ "keyadmin" });
        UserGroupInformation userUgi = UserGroupInformation.createProxyUserForTesting("user", TestAclsEndToEnd.realUgi, new String[]{ "staff" });
        Configuration conf = TestAclsEndToEnd.getBaseConf(hdfsUgi, keyadminUgi);
        TestAclsEndToEnd.setBlacklistAcls(conf, hdfsUgi);
        TestAclsEndToEnd.setKeyAcls(conf, (((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY1)) + "."), hdfsUgi, keyadminUgi, userUgi);
        doFullAclTest(conf, hdfsUgi, keyadminUgi, userUgi);
    }

    /**
     * Test the full life cycle of a key using a config with whitelist key ACLs
     * and without blacklist ACLs.  The configuration used is the correct
     * configuration to pass the full ACL test in {@link #doFullAclTest()}.
     *
     * @throws Exception
     * 		thrown on test failure
     */
    @Test
    public void testGoodWithWhitelistWithoutBlacklist() throws Exception {
        UserGroupInformation hdfsUgi = UserGroupInformation.createProxyUserForTesting("hdfs", TestAclsEndToEnd.realUgi, new String[]{ "supergroup" });
        UserGroupInformation keyadminUgi = UserGroupInformation.createProxyUserForTesting("keyadmin", TestAclsEndToEnd.realUgi, new String[]{ "keyadmin" });
        UserGroupInformation userUgi = UserGroupInformation.createProxyUserForTesting("user", TestAclsEndToEnd.realUgi, new String[]{ "staff" });
        Configuration conf = TestAclsEndToEnd.getBaseConf(hdfsUgi, keyadminUgi);
        TestAclsEndToEnd.setKeyAcls(conf, WHITELIST_KEY_ACL_PREFIX, hdfsUgi, keyadminUgi, userUgi);
        doFullAclTest(conf, hdfsUgi, keyadminUgi, userUgi);
    }

    /**
     * Test the full life cycle of a key using a config with whitelist key ACLs
     * and without blacklist ACLs. The configuration used is the correct
     * configuration to pass the full ACL test in {@link #doFullAclTest()}.
     *
     * @throws Exception
     * 		thrown on test failure
     */
    @Test
    public void testGoodWithKeyAclsWithoutBlacklist() throws Exception {
        UserGroupInformation hdfsUgi = UserGroupInformation.createProxyUserForTesting("hdfs", TestAclsEndToEnd.realUgi, new String[]{ "supergroup" });
        UserGroupInformation keyadminUgi = UserGroupInformation.createProxyUserForTesting("keyadmin", TestAclsEndToEnd.realUgi, new String[]{ "keyadmin" });
        UserGroupInformation userUgi = UserGroupInformation.createProxyUserForTesting("user", TestAclsEndToEnd.realUgi, new String[]{ "staff" });
        Configuration conf = TestAclsEndToEnd.getBaseConf(hdfsUgi, keyadminUgi);
        TestAclsEndToEnd.setKeyAcls(conf, (((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY1)) + "."), hdfsUgi, keyadminUgi, userUgi);
        doFullAclTest(conf, hdfsUgi, keyadminUgi, userUgi);
    }

    /**
     * Test that key creation is correctly governed by ACLs.
     *
     * @throws Exception
     * 		thrown if setup fails
     */
    @Test
    public void testCreateKey() throws Exception {
        Configuration conf = new Configuration();
        // Correct config with whitelist ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertTrue(("Exception during key creation with correct config" + " using whitelist key ACLs"), createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Correct config with default ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertTrue(("Exception during key creation with correct config" + " using default key ACLs"), createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY2, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertFalse("Allowed key creation with blacklist for CREATE", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Missing KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), " ");
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertFalse("Allowed key creation without CREATE KMS ACL", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Missing key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertFalse("Allowed key creation without MANAGMENT key ACL", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because the key ACL set ignores the default ACL set for key3
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set((((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY3)) + ".DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertFalse(("Allowed key creation when default key ACL should have been" + " overridden by key ACL"), createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Allowed because the default setting for KMS ACLs is fully permissive
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertTrue("Exception during key creation with default KMS ACLs", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
    }

    /**
     * Test that zone creation is correctly governed by ACLs.
     *
     * @throws Exception
     * 		thrown if setup fails
     */
    @Test
    public void testCreateEncryptionZone() throws Exception {
        Configuration conf = new Configuration();
        // Create a test key
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, conf));
        } finally {
            teardown();
        }
        // We tear everything down and then restart it with the ACLs we want to
        // test so that there's no contamination from the ACLs needed for setup.
        // To make that work, we have to tell the setup() method not to create a
        // new KMS directory.
        conf = new Configuration();
        // Correct config with whitelist ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE1);
            Assert.assertTrue(("Exception during zone creation with correct config using" + " whitelist key ACLs"), createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE1));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            teardown();
        }
        conf = new Configuration();
        // Correct config with default ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE2);
            Assert.assertTrue(("Exception during zone creation with correct config using" + " default key ACLs"), createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE2));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE2, true);
            teardown();
        }
        conf = new Configuration();
        // Denied because the key ACL set ignores the default ACL set for key1
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set((((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY1)) + ".DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE3);
            Assert.assertFalse(("Allowed creation of zone when default key ACLs should have" + " been overridden by key ACL"), createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE3));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            teardown();
        }
        conf = new Configuration();
        // Correct config with blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE3);
            Assert.assertFalse("Allowed zone creation of zone with blacklisted GET_METADATA", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE3));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            teardown();
        }
        conf = new Configuration();
        // Correct config with blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE3);
            Assert.assertFalse("Allowed zone creation of zone with blacklisted GENERATE_EEK", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE3));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            teardown();
        }
        conf = new Configuration();
        // Missing KMS ACL but works because defaults for KMS ACLs are fully
        // permissive
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE3);
            Assert.assertTrue("Exception during zone creation with default KMS ACLs", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE3));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            teardown();
        }
        conf = new Configuration();
        // Missing GET_METADATA KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), " ");
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE4);
            Assert.assertFalse("Allowed zone creation without GET_METADATA KMS ACL", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE4));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE4, true);
            teardown();
        }
        conf = new Configuration();
        // Missing GET_METADATA KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), " ");
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE4);
            Assert.assertFalse("Allowed zone creation without GENERATE_EEK KMS ACL", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE4));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE4, true);
            teardown();
        }
        conf = new Configuration();
        // Missing READ key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE4);
            Assert.assertFalse("Allowed zone creation without READ ACL", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE4));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE4, true);
            teardown();
        }
        conf = new Configuration();
        // Missing GENERATE_EEK key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            fs.mkdirs(TestAclsEndToEnd.ZONE4);
            Assert.assertFalse("Allowed zone creation without GENERATE_EEK ACL", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE4));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE4, true);
            teardown();
        }
    }

    /**
     * Test that in-zone file creation is correctly governed by ACLs.
     *
     * @throws Exception
     * 		thrown if setup fails
     */
    @Test
    public void testCreateFileInEncryptionZone() throws Exception {
        Configuration conf = new Configuration();
        // Create a test key
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        Assert.assertTrue(((new File(kmsDir, "kms.keystore").length()) == 0));
        try {
            setup(conf);
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, conf));
            fs.mkdirs(TestAclsEndToEnd.ZONE1);
            Assert.assertTrue("Exception during zone creation", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE1));
            fs.mkdirs(TestAclsEndToEnd.ZONE2);
            Assert.assertTrue("Exception during zone creation", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE2));
            fs.mkdirs(TestAclsEndToEnd.ZONE3);
            Assert.assertTrue("Exception during zone creation", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE3));
            fs.mkdirs(TestAclsEndToEnd.ZONE4);
            Assert.assertTrue("Exception during zone creation", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE4));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            fs.delete(TestAclsEndToEnd.ZONE2, true);
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            fs.delete(TestAclsEndToEnd.ZONE4, true);
            throw ex;
        } finally {
            teardown();
        }
        // We tear everything down and then restart it with the ACLs we want to
        // test so that there's no contamination from the ACLs needed for setup.
        // To make that work, we have to tell the setup() method not to create a
        // new KMS directory or DFS dierctory.
        conf = new Configuration();
        // Correct config with whitelist ACLs
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue(("Exception during file creation with correct config" + " using whitelist ACL"), createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            teardown();
        }
        conf = new Configuration();
        // Correct config with default ACLs
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue(("Exception during file creation with correct config" + " using whitelist ACL"), createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE2, TestAclsEndToEnd.TEXT));
        } finally {
            fs.delete(TestAclsEndToEnd.ZONE2, true);
            teardown();
        }
        conf = new Configuration();
        // Denied because the key ACL set ignores the default ACL set for key1
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set((((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY1)) + ".READ"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse(("Allowed file creation when default key ACLs should have been" + " overridden by key ACL"), createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied by blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation with blacklist for GENERATE_EEK", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied by blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation with blacklist for DECRYPT_EEK", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Allowed because default KMS ACLs are fully permissive
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue("Exception during file creation with default KMS ACLs", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of missing GENERATE_EEK KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), " ");
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation without GENERATE_EEK KMS ACL", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE4, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of missing DECRYPT_EEK KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), " ");
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation without DECRYPT_EEK KMS ACL", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of missing GENERATE_EEK key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation without GENERATE_EEK key ACL", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of missing DECRYPT_EEK key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file creation without DECRYPT_EEK key ACL", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE3, TestAclsEndToEnd.TEXT));
        } catch (Exception ex) {
            fs.delete(TestAclsEndToEnd.ZONE3, true);
            throw ex;
        } finally {
            teardown();
        }
    }

    /**
     * Test that in-zone file read is correctly governed by ACLs.
     *
     * @throws Exception
     * 		thrown if setup fails
     */
    @Test
    public void testReadFileInEncryptionZone() throws Exception {
        Configuration conf = new Configuration();
        // Create a test key
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GET_METADATA"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "READ"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "GENERATE_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        Assert.assertTrue(((new File(kmsDir, "kms.keystore").length()) == 0));
        try {
            setup(conf);
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, conf));
            fs.mkdirs(TestAclsEndToEnd.ZONE1);
            Assert.assertTrue("Exception during zone creation", createEncryptionZone(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, TestAclsEndToEnd.ZONE1));
            Assert.assertTrue("Exception during file creation", createFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        // We tear everything down and then restart it with the ACLs we want to
        // test so that there's no contamination from the ACLs needed for setup.
        // To make that work, we have to tell the setup() method not to create a
        // new KMS directory or DFS dierctory.
        conf = new Configuration();
        // Correct config with whitelist ACLs
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue(("Exception while reading file with correct config with" + " whitelist ACLs"), compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Correct config with default ACLs
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue(("Exception while reading file with correct config" + " with default ACLs"), compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because the key ACL set ignores the default ACL set for key1
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set((((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY1)) + ".READ"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse(("Allowed file read when default key ACLs should have been" + " overridden by key ACL"), compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied by blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file read with blacklist for DECRYPT_EEK", compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Allowed because default KMS ACLs are fully permissive
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertTrue("Exception while reading file with default KMS ACLs", compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of missing DECRYPT_EEK KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DECRYPT_EEK"), " ");
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file read without DECRYPT_EEK KMS ACL", compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
        // Denied because of missing DECRYPT_EEK key ACL
        conf = new Configuration();
        try {
            setup(conf, false, false);
            Assert.assertFalse("Allowed file read without DECRYPT_EEK key ACL", compareFile(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.FILE1, TestAclsEndToEnd.TEXT));
        } catch (Throwable ex) {
            fs.delete(TestAclsEndToEnd.ZONE1, true);
            throw ex;
        } finally {
            teardown();
        }
    }

    /**
     * Test that key deletion is correctly governed by ACLs.
     *
     * @throws Exception
     * 		thrown if setup fails
     */
    @Test
    public void testDeleteKey() throws Exception {
        Configuration conf = new Configuration();
        // Create a test key
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.CREATE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf);
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1, conf));
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY2, conf));
            Assert.assertTrue("Exception during key creation", createKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3, conf));
        } finally {
            teardown();
        }
        // We tear everything down and then restart it with the ACLs we want to
        // test so that there's no contamination from the ACLs needed for setup.
        // To make that work, we have to tell the setup() method not to create a
        // new KMS directory.
        conf = new Configuration();
        // Correct config with whitelist ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertTrue(("Exception during key deletion with correct config" + " using whitelist key ACLs"), deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY1));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Correct config with default ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertTrue(("Exception during key deletion with correct config" + " using default key ACLs"), deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY2));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because of blacklist
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "blacklist.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertFalse("Allowed key deletion with blacklist for DELETE", deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Missing KMS ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), " ");
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertFalse("Allowed key deletion without DELETE KMS ACL", deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Missing key ACL
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertFalse("Allowed key deletion without MANAGMENT key ACL", deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Denied because the key ACL set ignores the default ACL set for key3
        conf.set(((KMSConfiguration.CONFIG_PREFIX) + "acl.DELETE"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set(((KMSConfiguration.DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        conf.set((((KeyAuthorizationKeyProvider.KEY_ACL) + (TestAclsEndToEnd.KEY3)) + ".DECRYPT_EEK"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertFalse(("Allowed key deletion when default key ACL should have been" + " overridden by key ACL"), deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3));
        } finally {
            teardown();
        }
        conf = new Configuration();
        // Allowed because the default setting for KMS ACLs is fully permissive
        conf.set(((KMSConfiguration.WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), TestAclsEndToEnd.realUgi.getUserName());
        try {
            setup(conf, false);
            Assert.assertTrue("Exception during key deletion with default KMS ACLs", deleteKey(TestAclsEndToEnd.realUgi, TestAclsEndToEnd.KEY3));
        } finally {
            teardown();
        }
    }

    /**
     * Simple interface that defines an operation to perform.
     */
    private static interface UserOp {
        public void execute() throws IOException;
    }
}

