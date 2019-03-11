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
package org.apache.hadoop.hdfs.tools;


import GetConf.USAGE;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.tools.GetConf.Command;
import org.apache.hadoop.hdfs.tools.GetConf.CommandHandler;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link GetConf}
 */
public class TestGetConf {
    enum TestType {

        NAMENODE,
        BACKUP,
        SECONDARY,
        NNRPCADDRESSES,
        JOURNALNODE;}

    FileSystem localFileSys;

    /**
     * Test empty configuration
     */
    @Test(timeout = 10000)
    public void testEmptyConf() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration(false);
        // Verify getting addresses fails
        getAddressListFromTool(TestGetConf.TestType.NAMENODE, conf, false);
        System.out.println(getAddressListFromTool(TestGetConf.TestType.BACKUP, conf, false));
        getAddressListFromTool(TestGetConf.TestType.SECONDARY, conf, false);
        getAddressListFromTool(TestGetConf.TestType.NNRPCADDRESSES, conf, false);
        for (Command cmd : Command.values()) {
            String arg = cmd.getName();
            CommandHandler handler = Command.getHandler(arg);
            Assert.assertNotNull(("missing handler: " + cmd), handler);
            if ((handler.key) != null) {
                // First test with configuration missing the required key
                String[] args = new String[]{ handler.key };
                runTool(conf, args, false);
            }
        }
    }

    /**
     * Test invalid argument to the tool
     */
    @Test(timeout = 10000)
    public void testInvalidArgument() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        String[] args = new String[]{ "-invalidArgument" };
        String ret = runTool(conf, args, false);
        Assert.assertTrue(ret.contains(USAGE));
    }

    /**
     * Tests to make sure the returned addresses are correct in case of default
     * configuration with no federation
     */
    @Test(timeout = 10000)
    public void testNonFederation() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration(false);
        // Returned namenode address should match default address
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, "hdfs://localhost:1000");
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, "localhost:1000");
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, "localhost:1000");
        // Returned address should match backupnode RPC address
        conf.set(DFSConfigKeys.DFS_NAMENODE_BACKUP_ADDRESS_KEY, "localhost:1001");
        verifyAddresses(conf, TestGetConf.TestType.BACKUP, false, "localhost:1001");
        // Returned address should match secondary http address
        conf.set(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "localhost:1002");
        verifyAddresses(conf, TestGetConf.TestType.SECONDARY, false, "localhost:1002");
        // Returned namenode address should match service RPC address
        conf = new HdfsConfiguration();
        conf.set(DFSConfigKeys.DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY, "localhost:1000");
        conf.set(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, "localhost:1001");
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, "localhost:1000");
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, "localhost:1000");
        // Returned address should match RPC address
        conf = new HdfsConfiguration();
        conf.set(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, "localhost:1001");
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, "localhost:1001");
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, "localhost:1001");
    }

    /**
     * Tests to make sure the returned addresses are correct in case of federation
     * of setup.
     */
    @Test(timeout = 10000)
    public void testFederation() throws Exception {
        final int nsCount = 10;
        HdfsConfiguration conf = new HdfsConfiguration(false);
        // Test to ensure namenode, backup and secondary namenode addresses are
        // returned from federation configuration. Returned namenode addresses are
        // based on service RPC address and not regular RPC address
        setupNameServices(conf, nsCount);
        String[] nnAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY, nsCount, 1000);
        setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, nsCount, 1500);
        setupStaticHostResolution(nsCount, "nn");
        String[] backupAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_BACKUP_ADDRESS_KEY, nsCount, 2000);
        String[] secondaryAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, nsCount, 3000);
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, nnAddresses);
        verifyAddresses(conf, TestGetConf.TestType.BACKUP, false, backupAddresses);
        verifyAddresses(conf, TestGetConf.TestType.SECONDARY, false, secondaryAddresses);
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, nnAddresses);
        // Test to ensure namenode, backup, secondary namenode addresses and
        // namenode rpc addresses are  returned from federation configuration.
        // Returned namenode addresses are based on regular RPC address
        // in the absence of service RPC address.
        conf = new HdfsConfiguration(false);
        setupNameServices(conf, nsCount);
        nnAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, nsCount, 1000);
        backupAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_BACKUP_ADDRESS_KEY, nsCount, 2000);
        secondaryAddresses = setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, nsCount, 3000);
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, nnAddresses);
        verifyAddresses(conf, TestGetConf.TestType.BACKUP, false, backupAddresses);
        verifyAddresses(conf, TestGetConf.TestType.SECONDARY, false, secondaryAddresses);
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, nnAddresses);
    }

    /**
     * Tests for journal node addresses.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testGetJournalNodes() throws Exception {
        final int nsCount = 3;
        final String journalsBaseUri = "qjournal://jn0:8020;jn1:8020;jn2:8020";
        setupStaticHostResolution(nsCount, "jn");
        // With out Name service Id
        HdfsConfiguration conf = new HdfsConfiguration(false);
        conf.set(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY, (journalsBaseUri + "/"));
        Set<String> expected = new HashSet<>();
        expected.add("jn0");
        expected.add("jn1");
        expected.add("jn2");
        String expected1 = "";
        StringBuilder buffer = new StringBuilder();
        for (String val : expected) {
            if ((buffer.length()) > 0) {
                buffer.append(" ");
            }
            buffer.append(val);
        }
        buffer.append(System.lineSeparator());
        expected1 = buffer.toString();
        Set<String> actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        String actual1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        Assert.assertEquals(expected1, actual1);
        conf.clear();
        // With out Name service Id
        conf.set(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY, (journalsBaseUri + "/"));
        actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        actual1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        Assert.assertEquals(expected1, actual1);
        conf.clear();
        // Federation with HA, but suffixed only with Name service Id
        setupNameServices(conf, nsCount);
        conf.set(((DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX) + ".ns0"), "nn0,nn1");
        conf.set(((DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX) + ".ns1"), "nn0, nn1");
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns0"), (journalsBaseUri + "/ns0"));
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns1"), (journalsBaseUri + "/ns1"));
        actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        expected1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        Assert.assertEquals(expected1, actual1);
        conf.clear();
        // Federation with HA
        setupNameServices(conf, nsCount);
        conf.set(((DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX) + ".ns0"), "nn0,nn1");
        conf.set(((DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX) + ".ns1"), "nn0, nn1");
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns0.nn0"), (journalsBaseUri + "/ns0"));
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns0.nn1"), (journalsBaseUri + "/ns0"));
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns1.nn2"), (journalsBaseUri + "/ns1"));
        conf.set(((DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY) + ".ns1.nn3"), (journalsBaseUri + "/ns1"));
        actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        actual1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        Assert.assertEquals(expected1, actual1);
        conf.clear();
        // Name service setup, but no journal node
        setupNameServices(conf, nsCount);
        expected = new HashSet<>();
        actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        actual1 = System.lineSeparator();
        expected1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        Assert.assertEquals(expected1, actual1);
        conf.clear();
        // name node edits dir is present, but set
        // to location of storage shared directory
        conf.set(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY, "file:///mnt/filer1/dfs/ha-name-dir-shared");
        expected = new HashSet<>();
        actual = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expected.toString(), actual.toString());
        expected1 = getAddressListFromTool(TestGetConf.TestType.JOURNALNODE, conf, true);
        actual1 = System.lineSeparator();
        Assert.assertEquals(expected1, actual1);
        conf.clear();
    }

    /* * Test for unknown journal node host exception. */
    @Test(expected = UnknownHostException.class, timeout = 10000)
    public void testUnknownJournalNodeHost() throws IOException, URISyntaxException {
        String journalsBaseUri = "qjournal://jn1:8020;jn2:8020;jn3:8020";
        HdfsConfiguration conf = new HdfsConfiguration(false);
        conf.set(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY, (journalsBaseUri + "/jndata"));
        DFSUtil.getJournalNodeAddresses(conf);
    }

    /* * Test for malformed journal node urisyntax exception. */
    @Test(expected = URISyntaxException.class, timeout = 10000)
    public void testJournalNodeUriError() throws IOException, URISyntaxException {
        final int nsCount = 3;
        String journalsBaseUri = "qjournal://jn0 :8020;jn1:8020;jn2:8020";
        setupStaticHostResolution(nsCount, "jn");
        HdfsConfiguration conf = new HdfsConfiguration(false);
        conf.set(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY, (journalsBaseUri + "/jndata"));
        DFSUtil.getJournalNodeAddresses(conf);
    }

    @Test(timeout = 10000)
    public void testGetSpecificKey() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.set("mykey", " myval ");
        String[] args = new String[]{ "-confKey", "mykey" };
        String toolResult = runTool(conf, args, true);
        Assert.assertEquals(String.format("myval%n"), toolResult);
    }

    @Test(timeout = 10000)
    public void testExtraArgsThrowsError() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.set("mykey", "myval");
        String[] args = new String[]{ "-namenodes", "unexpected-arg" };
        Assert.assertTrue(runTool(conf, args, false).contains("Did not expect argument: unexpected-arg"));
    }

    /**
     * Tests commands other than {@link Command#NAMENODE}, {@link Command#BACKUP},
     * {@link Command#SECONDARY} and {@link Command#NNRPCADDRESSES}
     */
    @Test(timeout = 10000)
    public void testTool() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration(false);
        for (Command cmd : Command.values()) {
            CommandHandler handler = Command.getHandler(cmd.getName());
            if (((handler.key) != null) && (!("-confKey".equals(cmd.getName())))) {
                // Add the key to the conf and ensure tool returns the right value
                String[] args = new String[]{ cmd.getName() };
                conf.set(handler.key, "value");
                Assert.assertTrue(runTool(conf, args, true).contains("value"));
            }
        }
    }

    @Test
    public void TestGetConfExcludeCommand() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        // Set up the hosts/exclude files.
        HostsFileWriter hostsFileWriter = new HostsFileWriter();
        hostsFileWriter.initialize(conf, "GetConf");
        Path excludeFile = hostsFileWriter.getExcludeFile();
        String[] args = new String[]{ "-excludeFile" };
        String ret = runTool(conf, args, true);
        Assert.assertEquals(excludeFile.toUri().getPath(), ret.trim());
        hostsFileWriter.cleanup();
    }

    @Test
    public void TestGetConfIncludeCommand() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        // Set up the hosts/exclude files.
        HostsFileWriter hostsFileWriter = new HostsFileWriter();
        hostsFileWriter.initialize(conf, "GetConf");
        Path hostsFile = hostsFileWriter.getIncludeFile();
        // Setup conf
        String[] args = new String[]{ "-includeFile" };
        String ret = runTool(conf, args, true);
        Assert.assertEquals(hostsFile.toUri().getPath(), ret.trim());
        hostsFileWriter.cleanup();
    }

    @Test
    public void testIncludeInternalNameServices() throws Exception {
        final int nsCount = 10;
        final int remoteNsCount = 4;
        HdfsConfiguration conf = new HdfsConfiguration();
        setupNameServices(conf, nsCount);
        setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY, nsCount, 1000);
        setupAddress(conf, DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, nsCount, 1500);
        conf.set(DFSConfigKeys.DFS_INTERNAL_NAMESERVICES_KEY, "ns1");
        setupStaticHostResolution(nsCount, "nn");
        String[] includedNN = new String[]{ "nn1:1001" };
        verifyAddresses(conf, TestGetConf.TestType.NAMENODE, false, includedNN);
        verifyAddresses(conf, TestGetConf.TestType.NNRPCADDRESSES, true, includedNN);
    }
}

