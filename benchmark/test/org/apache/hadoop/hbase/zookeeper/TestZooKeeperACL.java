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
package org.apache.hadoop.hbase.zookeeper;


import HConstants.ZK_CLIENT_KERBEROS_PRINCIPAL;
import HConstants.ZK_CLIENT_KEYTAB_FILE;
import HConstants.ZK_SERVER_KERBEROS_PRINCIPAL;
import HConstants.ZK_SERVER_KEYTAB_FILE;
import ZooDefs.Perms.ALL;
import ZooDefs.Perms.READ;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ZKTests.class, MediumTests.class })
public class TestZooKeeperACL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZooKeeperACL.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZooKeeperACL.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static ZKWatcher zkw;

    private static boolean secureZKAvailable;

    /**
     * Create a node and check its ACL. When authentication is enabled on
     * ZooKeeper, all nodes (except /hbase/root-region-server, /hbase/master
     * and /hbase/hbaseid) should be created so that only the hbase server user
     * (master or region server user) that created them can access them, and
     * this user should have all permissions on this node. For
     * /hbase/root-region-server, /hbase/master, and /hbase/hbaseid the
     * permissions should be as above, but should also be world-readable. First
     * we check the general case of /hbase nodes in the following test, and
     * then check the subset of world-readable nodes in the three tests after
     * that.
     */
    @Test
    public void testHBaseRootZNodeACL() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        List<ACL> acls = TestZooKeeperACL.zkw.getRecoverableZooKeeper().getZooKeeper().getACL("/hbase", new Stat());
        Assert.assertEquals(1, acls.size());
        Assert.assertEquals("sasl", acls.get(0).getId().getScheme());
        Assert.assertEquals("hbase", acls.get(0).getId().getId());
        Assert.assertEquals(ALL, acls.get(0).getPerms());
    }

    /**
     * When authentication is enabled on ZooKeeper, /hbase/root-region-server
     * should be created with 2 ACLs: one specifies that the hbase user has
     * full access to the node; the other, that it is world-readable.
     */
    @Test
    public void testHBaseRootRegionServerZNodeACL() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        List<ACL> acls = TestZooKeeperACL.zkw.getRecoverableZooKeeper().getZooKeeper().getACL("/hbase/root-region-server", new Stat());
        Assert.assertEquals(2, acls.size());
        boolean foundWorldReadableAcl = false;
        boolean foundHBaseOwnerAcl = false;
        for (int i = 0; i < 2; i++) {
            if ((acls.get(i).getId().getScheme().equals("world")) == true) {
                Assert.assertEquals("anyone", acls.get(0).getId().getId());
                Assert.assertEquals(READ, acls.get(0).getPerms());
                foundWorldReadableAcl = true;
            } else {
                if ((acls.get(i).getId().getScheme().equals("sasl")) == true) {
                    Assert.assertEquals("hbase", acls.get(1).getId().getId());
                    Assert.assertEquals("sasl", acls.get(1).getId().getScheme());
                    foundHBaseOwnerAcl = true;
                } else {
                    // error: should not get here: test fails.
                    Assert.assertTrue(false);
                }
            }
        }
        Assert.assertTrue(foundWorldReadableAcl);
        Assert.assertTrue(foundHBaseOwnerAcl);
    }

    /**
     * When authentication is enabled on ZooKeeper, /hbase/master should be
     * created with 2 ACLs: one specifies that the hbase user has full access
     * to the node; the other, that it is world-readable.
     */
    @Test
    public void testHBaseMasterServerZNodeACL() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        List<ACL> acls = TestZooKeeperACL.zkw.getRecoverableZooKeeper().getZooKeeper().getACL("/hbase/master", new Stat());
        Assert.assertEquals(2, acls.size());
        boolean foundWorldReadableAcl = false;
        boolean foundHBaseOwnerAcl = false;
        for (int i = 0; i < 2; i++) {
            if ((acls.get(i).getId().getScheme().equals("world")) == true) {
                Assert.assertEquals("anyone", acls.get(0).getId().getId());
                Assert.assertEquals(READ, acls.get(0).getPerms());
                foundWorldReadableAcl = true;
            } else {
                if ((acls.get(i).getId().getScheme().equals("sasl")) == true) {
                    Assert.assertEquals("hbase", acls.get(1).getId().getId());
                    Assert.assertEquals("sasl", acls.get(1).getId().getScheme());
                    foundHBaseOwnerAcl = true;
                } else {
                    // error: should not get here: test fails.
                    Assert.assertTrue(false);
                }
            }
        }
        Assert.assertTrue(foundWorldReadableAcl);
        Assert.assertTrue(foundHBaseOwnerAcl);
    }

    /**
     * When authentication is enabled on ZooKeeper, /hbase/hbaseid should be
     * created with 2 ACLs: one specifies that the hbase user has full access
     * to the node; the other, that it is world-readable.
     */
    @Test
    public void testHBaseIDZNodeACL() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        List<ACL> acls = TestZooKeeperACL.zkw.getRecoverableZooKeeper().getZooKeeper().getACL("/hbase/hbaseid", new Stat());
        Assert.assertEquals(2, acls.size());
        boolean foundWorldReadableAcl = false;
        boolean foundHBaseOwnerAcl = false;
        for (int i = 0; i < 2; i++) {
            if ((acls.get(i).getId().getScheme().equals("world")) == true) {
                Assert.assertEquals("anyone", acls.get(0).getId().getId());
                Assert.assertEquals(READ, acls.get(0).getPerms());
                foundWorldReadableAcl = true;
            } else {
                if ((acls.get(i).getId().getScheme().equals("sasl")) == true) {
                    Assert.assertEquals("hbase", acls.get(1).getId().getId());
                    Assert.assertEquals("sasl", acls.get(1).getId().getScheme());
                    foundHBaseOwnerAcl = true;
                } else {
                    // error: should not get here: test fails.
                    Assert.assertTrue(false);
                }
            }
        }
        Assert.assertTrue(foundWorldReadableAcl);
        Assert.assertTrue(foundHBaseOwnerAcl);
    }

    /**
     * Finally, we check the ACLs of a node outside of the /hbase hierarchy and
     * verify that its ACL is simply 'hbase:Perms.ALL'.
     */
    @Test
    public void testOutsideHBaseNodeACL() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        ZKUtil.createWithParents(TestZooKeeperACL.zkw, "/testACLNode");
        List<ACL> acls = TestZooKeeperACL.zkw.getRecoverableZooKeeper().getZooKeeper().getACL("/testACLNode", new Stat());
        Assert.assertEquals(1, acls.size());
        Assert.assertEquals("sasl", acls.get(0).getId().getScheme());
        Assert.assertEquals("hbase", acls.get(0).getId().getId());
        Assert.assertEquals(ALL, acls.get(0).getPerms());
    }

    /**
     * Check if ZooKeeper JaasConfiguration is valid.
     */
    @Test
    public void testIsZooKeeperSecure() throws Exception {
        boolean testJaasConfig = ZKUtil.isSecureZooKeeper(new org.apache.hadoop.conf.Configuration(TestZooKeeperACL.TEST_UTIL.getConfiguration()));
        Assert.assertEquals(testJaasConfig, TestZooKeeperACL.secureZKAvailable);
        // Define Jaas configuration without ZooKeeper Jaas config
        File saslConfFile = File.createTempFile("tmp", "fakeJaas.conf");
        try (OutputStreamWriter fwriter = new OutputStreamWriter(new FileOutputStream(saslConfFile), StandardCharsets.UTF_8)) {
            fwriter.write("");
        }
        System.setProperty("java.security.auth.login.config", saslConfFile.getAbsolutePath());
        testJaasConfig = ZKUtil.isSecureZooKeeper(new org.apache.hadoop.conf.Configuration(TestZooKeeperACL.TEST_UTIL.getConfiguration()));
        Assert.assertFalse(testJaasConfig);
        saslConfFile.delete();
    }

    /**
     * Check if Programmatic way of setting zookeeper security settings is valid.
     */
    @Test
    public void testIsZooKeeperSecureWithProgrammaticConfig() throws Exception {
        Configuration.setConfiguration(new TestZooKeeperACL.DummySecurityConfiguration());
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration(HBaseConfiguration.create());
        boolean testJaasConfig = ZKUtil.isSecureZooKeeper(config);
        Assert.assertFalse(testJaasConfig);
        // Now set authentication scheme to Kerberos still it should return false
        // because no configuration set
        config.set("hbase.security.authentication", "kerberos");
        testJaasConfig = ZKUtil.isSecureZooKeeper(config);
        Assert.assertFalse(testJaasConfig);
        // Now set programmatic options related to security
        config.set(ZK_CLIENT_KEYTAB_FILE, "/dummy/file");
        config.set(ZK_CLIENT_KERBEROS_PRINCIPAL, "dummy");
        config.set(ZK_SERVER_KEYTAB_FILE, "/dummy/file");
        config.set(ZK_SERVER_KERBEROS_PRINCIPAL, "dummy");
        testJaasConfig = ZKUtil.isSecureZooKeeper(config);
        Assert.assertTrue(testJaasConfig);
    }

    private static class DummySecurityConfiguration extends Configuration {
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return null;
        }
    }

    @Test
    public void testAdminDrainAllowedOnSecureZK() throws Exception {
        if (!(TestZooKeeperACL.secureZKAvailable)) {
            return;
        }
        List<ServerName> decommissionedServers = new ArrayList<>(1);
        decommissionedServers.add(ServerName.parseServerName("ZZZ,123,123"));
        // If unable to connect to secure ZK cluster then this operation would fail.
        TestZooKeeperACL.TEST_UTIL.getAdmin().decommissionRegionServers(decommissionedServers, false);
        decommissionedServers = TestZooKeeperACL.TEST_UTIL.getAdmin().listDecommissionedRegionServers();
        Assert.assertEquals(1, decommissionedServers.size());
        Assert.assertEquals(ServerName.parseServerName("ZZZ,123,123"), decommissionedServers.get(0));
        TestZooKeeperACL.TEST_UTIL.getAdmin().recommissionRegionServer(decommissionedServers.get(0), null);
        decommissionedServers = TestZooKeeperACL.TEST_UTIL.getAdmin().listDecommissionedRegionServers();
        Assert.assertEquals(0, decommissionedServers.size());
    }
}

