/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config;


import Genesis.PremineAccount;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.NotThreadSafe;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.ethereum.config.blockchain.OlympicConfig;
import org.ethereum.config.net.BlockchainNetConfig;
import org.ethereum.config.net.TestNetConfig;
import org.ethereum.core.AccountState;
import org.ethereum.core.Genesis;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.net.rlpx.Node;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;


/**
 * Not thread safe - testUseOnlySprintConfig temporarily sets a static flag that may influence other tests.
 * Not thread safe - testGeneratedNodePrivateKey temporarily removes the nodeId.properties file which may influence other tests.
 */
@SuppressWarnings("ConstantConditions")
@NotThreadSafe
public class SystemPropertiesTest {
    private static final Logger logger = LoggerFactory.getLogger(SystemPropertiesTest.class);

    @Test
    public void testPunchBindIp() {
        SystemProperties.getDefault().overrideParams("peer.bind.ip", "");
        long st = System.currentTimeMillis();
        String ip = SystemProperties.getDefault().bindIp();
        long t = (System.currentTimeMillis()) - st;
        SystemPropertiesTest.logger.info((((ip + " in ") + t) + " msec"));
        Assert.assertTrue((t < (10 * 1000)));
        Assert.assertFalse(ip.isEmpty());
    }

    @Test
    public void testExternalIp() {
        SystemProperties.getDefault().overrideParams("peer.discovery.external.ip", "");
        long st = System.currentTimeMillis();
        String ip = SystemProperties.getDefault().externalIp();
        long t = (System.currentTimeMillis()) - st;
        SystemPropertiesTest.logger.info((((ip + " in ") + t) + " msec"));
        Assert.assertTrue((t < (10 * 1000)));
        Assert.assertFalse(ip.isEmpty());
    }

    @Test
    public void testExternalIpWhenSpecificallyConfigured() {
        SystemProperties props = SystemProperties.getDefault();
        props.overrideParams("peer.discovery.external.ip", "1.1.1.1");
        Assert.assertEquals("1.1.1.1", props.externalIp());
        props.overrideParams("peer.discovery.external.ip", "no_validation_rules_on_this_value");
        Assert.assertEquals("no_validation_rules_on_this_value", props.externalIp());
    }

    @Test
    public void testBlockchainNetConfig() {
        assertConfigNameResolvesToType("main", MainNetConfig.class);
        assertConfigNameResolvesToType("olympic", OlympicConfig.class);
        assertConfigNameResolvesToType("morden", MordenNetConfig.class);
        assertConfigNameResolvesToType("ropsten", RopstenNetConfig.class);
        assertConfigNameResolvesToType("testnet", TestNetConfig.class);
    }

    @Test
    public void testConfigNamesAreCaseSensitive() {
        assertConfigNameIsUnsupported("mAin");
        assertConfigNameIsUnsupported("Main");
    }

    @Test
    public void testGarbageConfigNamesTriggerExceptions() {
        assertConfigNameIsUnsupported("\t");
        assertConfigNameIsUnsupported("\n");
        assertConfigNameIsUnsupported("");
        assertConfigNameIsUnsupported("fake");
    }

    @Test
    public void testUseOnlySprintConfig() {
        boolean originalValue = SystemProperties.isUseOnlySpringConfig();
        try {
            SystemProperties.setUseOnlySpringConfig(false);
            TestCase.assertNotNull(SystemProperties.getDefault());
            SystemProperties.setUseOnlySpringConfig(true);
            Assert.assertNull(SystemProperties.getDefault());
        } finally {
            SystemProperties.setUseOnlySpringConfig(originalValue);
        }
    }

    @Test
    public void testValidateMeAnnotatedGetters() {
        assertIncorrectValueTriggersConfigException("peer.discovery.enabled", "not_a_boolean");
        assertIncorrectValueTriggersConfigException("peer.discovery.persist", "not_a_boolean");
        assertIncorrectValueTriggersConfigException("peer.discovery.workers", "not_a_number");
        assertIncorrectValueTriggersConfigException("peer.discovery.touchPeriod", "not_a_number");
        assertIncorrectValueTriggersConfigException("peer.connection.timeout", "not_a_number");
        assertIncorrectValueTriggersConfigException("peer.p2p.version", "not_a_number");
        assertIncorrectValueTriggersConfigException("peer.p2p.framing.maxSize", "not_a_number");
        assertIncorrectValueTriggersConfigException("transaction.approve.timeout", "not_a_number");
        assertIncorrectValueTriggersConfigException("peer.discovery.ip.list", "not_a_ip");
        assertIncorrectValueTriggersConfigException("database.reset", "not_a_boolean");
        assertIncorrectValueTriggersConfigException("database.resetBlock", "not_a_number");
        assertIncorrectValueTriggersConfigException("database.prune.enabled", "not_a_boolean");
        assertIncorrectValueTriggersConfigException("database.resetBlock", "not_a_number");
    }

    @Test
    public void testDatabasePruneShouldAdhereToMax() {
        SystemProperties props = new SystemProperties();
        props.overrideParams("database.prune.enabled", "false");
        Assert.assertEquals("When database.prune.maxDepth is not set, defaults to -1", (-1), props.databasePruneDepth());
        props.overrideParams("database.prune.enabled", "true", "database.prune.maxDepth", "42");
        Assert.assertEquals(42, props.databasePruneDepth());
    }

    @Test
    public void testRequireEitherNameOrClassConfiguration() {
        try {
            SystemProperties props = new SystemProperties();
            props.overrideParams("blockchain.config.name", "test", "blockchain.config.class", "org.ethereum.config.net.TestNetConfig");
            props.getBlockchainConfig();
            Assert.fail("Should've thrown exception because not 'Only one of two options should be defined'");
        } catch (RuntimeException e) {
            Assert.assertEquals("Only one of two options should be defined: 'blockchain.config.name' and 'blockchain.config.class'", e.getMessage());
        }
    }

    @Test
    public void testRequireTypeBlockchainNetConfigOnManualClass() {
        SystemProperties props = new SystemProperties();
        props.overrideParams("blockchain.config.name", null, "blockchain.config.class", "org.ethereum.config.net.TestNetConfig");
        Assert.assertTrue(props.getBlockchainConfig().getClass().isAssignableFrom(TestNetConfig.class));
    }

    @Test
    public void testNonExistentBlockchainNetConfigClass() {
        SystemProperties props = new SystemProperties();
        try {
            props.overrideParams("blockchain.config.name", null, "blockchain.config.class", "org.ethereum.config.net.NotExistsConfig");
            props.getBlockchainConfig();
            Assert.fail("Should throw exception for invalid class");
        } catch (RuntimeException expected) {
            Assert.assertEquals("The class specified via blockchain.config.class 'org.ethereum.config.net.NotExistsConfig' not found", expected.getMessage());
        }
    }

    @Test
    public void testNotInstanceOfBlockchainForkConfig() {
        SystemProperties props = new SystemProperties(ConfigFactory.empty(), getClass().getClassLoader());
        try {
            props.overrideParams("blockchain.config.name", null, "blockchain.config.class", "org.ethereum.config.NodeFilter");
            props.getBlockchainConfig();
            Assert.fail("Should throw exception for invalid class");
        } catch (RuntimeException expected) {
            Assert.assertEquals("The class specified via blockchain.config.class 'org.ethereum.config.NodeFilter' is not instance of org.ethereum.config.BlockchainForkConfig", expected.getMessage());
        }
    }

    @Test
    public void testEmptyListOnEmptyPeerActiveConfiguration() {
        SystemProperties props = new SystemProperties();
        props.overrideParams("peer.active", null);
        Assert.assertEquals(Lists.newArrayList(), props.peerActive());
    }

    @Test
    public void testPeerActive() {
        SystemPropertiesTest.ActivePeer node1 = SystemPropertiesTest.ActivePeer.asEnodeUrl("node-1", "1.1.1.1");
        SystemPropertiesTest.ActivePeer node2 = SystemPropertiesTest.ActivePeer.asNode("node-2", "2.2.2.2");
        Config config = SystemPropertiesTest.createActivePeersConfig(node1, node2);
        SystemProperties props = new SystemProperties();
        props.overrideParams(config);
        List<Node> activePeers = props.peerActive();
        Assert.assertEquals(2, activePeers.size());
    }

    @Test
    public void testRequire64CharsNodeId() {
        assertInvalidNodeId(RandomStringUtils.randomAlphanumeric(63));
        assertInvalidNodeId(RandomStringUtils.randomAlphanumeric(1));
        assertInvalidNodeId(RandomStringUtils.randomAlphanumeric(65));
    }

    @Test
    public void testUnexpectedElementInNodeConfigThrowsException() {
        String nodeWithUnexpectedElement = "peer = {" + (((("active = [{\n" + "  port = 30303\n") + "  nodeName = Test\n") + "  unexpectedElement = 12345\n") + "}]}");
        Config invalidConfig = ConfigFactory.parseString(nodeWithUnexpectedElement);
        SystemProperties props = new SystemProperties();
        try {
            props.overrideParams(invalidConfig);
        } catch (RuntimeException ignore) {
        }
    }

    @Test
    public void testActivePeersUsingNodeName() {
        SystemPropertiesTest.ActivePeer node = SystemPropertiesTest.ActivePeer.asNodeWithName("node-1", "1.1.1.1", "peer-1");
        Config config = SystemPropertiesTest.createActivePeersConfig(node);
        SystemProperties props = new SystemProperties();
        props.overrideParams(config);
        List<Node> activePeers = props.peerActive();
        Assert.assertEquals(1, activePeers.size());
        Node peer = activePeers.get(0);
        String expectedKeccak512HashOfNodeName = "fcaf073315aa0fe284dd6d76200ede5cc9277f3cb1fd7649ddab3b6a61e96ee91e957" + "0b14932be6d6cd837027d50d9521923962909e5a9fdcdcabc3fe29408bb";
        String actualHexEncodedId = Hex.toHexString(peer.getId());
        Assert.assertEquals(expectedKeccak512HashOfNodeName, actualHexEncodedId);
    }

    @Test
    public void testRequireEitherNodeNameOrNodeId() {
        SystemPropertiesTest.ActivePeer node = SystemPropertiesTest.ActivePeer.asNodeWithName("node-1", "1.1.1.1", null);
        Config config = SystemPropertiesTest.createActivePeersConfig(node);
        SystemProperties props = new SystemProperties();
        try {
            props.overrideParams(config);
            Assert.fail("Should require either nodeName or nodeId");
        } catch (RuntimeException ignore) {
        }
    }

    @Test
    public void testPeerTrusted() throws Exception {
        SystemPropertiesTest.TrustedPeer peer1 = SystemPropertiesTest.TrustedPeer.asNode("node-1", "1.1.1.1");
        SystemPropertiesTest.TrustedPeer peer2 = SystemPropertiesTest.TrustedPeer.asNode("node-2", "2.1.1.*");
        SystemPropertiesTest.TrustedPeer peer3 = SystemPropertiesTest.TrustedPeer.asNode("node-2", "3.*");
        Config config = SystemPropertiesTest.createTrustedPeersConfig(peer1, peer2, peer3);
        SystemProperties props = new SystemProperties();
        props.overrideParams(config);
        NodeFilter filter = props.peerTrusted();
        Assert.assertTrue(filter.accept(InetAddress.getByName("1.1.1.1")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("2.1.1.1")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("2.1.1.9")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("3.1.1.1")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("3.1.1.9")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("3.9.1.9")));
        Assert.assertFalse(filter.accept(InetAddress.getByName("4.1.1.1")));
    }

    @Test
    public void testRequire64CharsPrivateKey() {
        assertInvalidPrivateKey(RandomUtils.nextBytes(1));
        assertInvalidPrivateKey(RandomUtils.nextBytes(31));
        assertInvalidPrivateKey(RandomUtils.nextBytes(33));
        assertInvalidPrivateKey(RandomUtils.nextBytes(64));
        assertInvalidPrivateKey(RandomUtils.nextBytes(0));
        String validPrivateKey = Hex.toHexString(RandomUtils.nextBytes(32));
        SystemProperties props = new SystemProperties();
        props.overrideParams("peer.privateKey", validPrivateKey);
        Assert.assertEquals(validPrivateKey, props.privateKey());
    }

    @Test
    public void testExposeBugWhereNonHexEncodedIsAcceptedWithoutValidation() {
        SystemProperties props = new SystemProperties();
        String nonHexEncoded = RandomStringUtils.randomAlphanumeric(64);
        try {
            props.overrideParams("peer.privateKey", nonHexEncoded);
            props.privateKey();
            Assert.fail("Should've thrown exception for invalid private key");
        } catch (RuntimeException ignore) {
        }
    }

    /**
     * TODO: Consider using a strategy interface for #getGeneratedNodePrivateKey().
     * Anything 'File' and 'random' generation are difficult to test and assert
     */
    @Test
    public void testGeneratedNodePrivateKeyThroughECKey() throws Exception {
        File outputFile = new File("database-test/nodeId.properties");
        // noinspection ResultOfMethodCallIgnored
        outputFile.delete();
        SystemProperties props = new SystemProperties();
        props.privateKey();
        Assert.assertTrue(outputFile.exists());
        String contents = FileCopyUtils.copyToString(new FileReader(outputFile));
        String[] lines = StringUtils.tokenizeToStringArray(contents, "\n");
        Assert.assertEquals(4, lines.length);
        Assert.assertTrue(lines[0].startsWith("#Generated NodeID."));
        Assert.assertTrue(lines[1].startsWith("#"));
        Assert.assertTrue(lines[2].startsWith("nodeIdPrivateKey="));
        Assert.assertEquals((("nodeIdPrivateKey=".length()) + 64), lines[2].length());
        Assert.assertTrue(lines[3].startsWith("nodeId="));
        Assert.assertEquals((("nodeId=".length()) + 128), lines[3].length());
    }

    @Test
    public void testGeneratedNodePrivateKeyDelegatesToStrategy() {
        File outputFile = new File("database-test/nodeId.properties");
        // noinspection ResultOfMethodCallIgnored
        outputFile.delete();
        GenerateNodeIdStrategy generateNodeIdStrategyMock = Mockito.mock(GenerateNodeIdStrategy.class);
        SystemProperties props = new SystemProperties();
        props.setGenerateNodeIdStrategy(generateNodeIdStrategyMock);
        props.privateKey();
        Mockito.verify(generateNodeIdStrategyMock).getNodePrivateKey();
    }

    @Test
    public void testFastSyncPivotBlockHash() {
        SystemProperties props = new SystemProperties();
        Assert.assertNull(props.getFastSyncPivotBlockHash());
        byte[] validPivotBlockHash = RandomUtils.nextBytes(32);
        props.overrideParams("sync.fast.pivotBlockHash", Hex.toHexString(validPivotBlockHash));
        Assert.assertTrue(Arrays.equals(validPivotBlockHash, props.getFastSyncPivotBlockHash()));
        assertInvalidPivotBlockHash(RandomUtils.nextBytes(0));
        assertInvalidPivotBlockHash(RandomUtils.nextBytes(1));
        assertInvalidPivotBlockHash(RandomUtils.nextBytes(31));
        assertInvalidPivotBlockHash(RandomUtils.nextBytes(33));
    }

    @Test
    public void testUseGenesis() throws IOException {
        BigInteger mordenInitialNonse = BigInteger.valueOf(1048576);
        SystemProperties props = new SystemProperties() {
            @Override
            public BlockchainNetConfig getBlockchainConfig() {
                return new MordenNetConfig();
            }
        };
        Resource sampleGenesisBlock = new ClassPathResource("/config/genesis-sample.json");
        Genesis genesis = props.useGenesis(sampleGenesisBlock.getInputStream());
        /* Assert that MordenNetConfig is used when generating the
        premine state.
         */
        Map<ByteArrayWrapper, Genesis.PremineAccount> premine = genesis.getPremine();
        Assert.assertEquals(1, premine.size());
        Genesis.PremineAccount account = premine.values().iterator().next();
        AccountState state = account.accountState;
        Assert.assertEquals(state.getNonce(), mordenInitialNonse);
        // noinspection SpellCheckingInspection
        Assert.assertEquals("#0 (4addb5 <~ 000000) Txs:0, Unc: 0", genesis.getShortDescr());
    }

    @Test
    public void testDump() {
        SystemProperties props = new SystemProperties();
        /* No intend to test TypeSafe's render functionality. Perform
        some high-level asserts to verify that:
        - it's probably a config
        - it's probably fairly sized
        - it didn't break
         */
        String dump = props.dump().trim();
        Assert.assertTrue(dump.startsWith("{"));
        Assert.assertTrue(dump.endsWith("}"));
        Assert.assertTrue(((dump.length()) > (5 * 1024)));
        Assert.assertTrue(((StringUtils.countOccurrencesOf(dump, "{")) > 50));
        Assert.assertTrue(((StringUtils.countOccurrencesOf(dump, "{")) > 50));
    }

    @Test
    public void testMergeConfigs1() {
        String firstConfig = "";
        SystemProperties props = new SystemProperties();
        Config config = props.mergeConfigs(firstConfig, ConfigFactory::parseString);
        Assert.assertFalse(config.hasPath("peer.listen.port"));
    }

    @Test
    public void testMergeConfigs2() {
        String firstConfig = "peer.listen.port=30123";
        SystemProperties props = new SystemProperties();
        Config config = props.mergeConfigs(firstConfig, ConfigFactory::parseString);
        Assert.assertEquals(30123, config.getInt("peer.listen.port"));
    }

    @Test
    public void testMergeConfigs3() {
        String firstConfig = "peer.listen.port=30123,peer.listen.port=30145";
        SystemProperties props = new SystemProperties();
        Config config = props.mergeConfigs(firstConfig, ConfigFactory::parseString);
        Assert.assertEquals(30145, config.getInt("peer.listen.port"));
    }

    @Test
    public void testMergeConfigs4() {
        String firstConfig = "peer.listen.port=30123,sync.enabled=true";
        SystemProperties props = new SystemProperties();
        Config config = props.mergeConfigs(firstConfig, ConfigFactory::parseString);
        Assert.assertEquals(30123, config.getInt("peer.listen.port"));
        Assert.assertEquals(Boolean.TRUE, config.getBoolean("sync.enabled"));
    }

    @SuppressWarnings("SameParameterValue")
    static class ActivePeer {
        boolean asEnodeUrl;

        String node;

        String host;

        String nodeId;

        String nodeName;

        static SystemPropertiesTest.ActivePeer asEnodeUrl(String node, String host) {
            return new SystemPropertiesTest.ActivePeer(true, node, host, "e437a4836b77ad9d9ffe73ee782ef2614e6d8370fcf62191a6e488276e23717147073a7ce0b444d485fff5a0c34c4577251a7a990cf80d8542e21b95aa8c5e6c", null);
        }

        static SystemPropertiesTest.ActivePeer asNode(String node, String host) {
            return SystemPropertiesTest.ActivePeer.asNodeWithId(node, host, "e437a4836b77ad9d9ffe73ee782ef2614e6d8370fcf62191a6e488276e23717147073a7ce0b444d485fff5a0c34c4577251a7a990cf80d8542e21b95aa8c5e6c");
        }

        static SystemPropertiesTest.ActivePeer asNodeWithId(String node, String host, String nodeId) {
            return new SystemPropertiesTest.ActivePeer(false, node, host, nodeId, null);
        }

        static SystemPropertiesTest.ActivePeer asNodeWithName(String node, String host, String name) {
            return new SystemPropertiesTest.ActivePeer(false, node, host, null, name);
        }

        private ActivePeer(boolean asEnodeUrl, String node, String host, String nodeId, String nodeName) {
            this.asEnodeUrl = asEnodeUrl;
            this.node = node;
            this.host = host;
            this.nodeId = nodeId;
            this.nodeName = nodeName;
        }

        public String toString() {
            String hexEncodedNode = Hex.toHexString(node.getBytes());
            if (asEnodeUrl) {
                return ((((("{\n" + "  url = \"enode://") + hexEncodedNode) + "@") + (host)) + ".com:30303\" \n") + "}";
            }
            if (StringUtils.hasText(nodeName)) {
                return ((((((("{\n" + "  ip = ") + (host)) + "\n") + "  port = 30303\n") + "  nodeName = ") + (nodeName)) + "\n") + "}\n";
            }
            return ((((((("{\n" + "  ip = ") + (host)) + "\n") + "  port = 30303\n") + "  nodeId = ") + (nodeId)) + "\n") + "}\n";
        }
    }

    @SuppressWarnings("SameParameterValue")
    static class TrustedPeer {
        String ip;

        String nodeId;

        static SystemPropertiesTest.TrustedPeer asNode(String nodeId, String ipPattern) {
            return new SystemPropertiesTest.TrustedPeer(nodeId, ipPattern);
        }

        private TrustedPeer(String nodeId, String ipPattern) {
            this.ip = ipPattern;
            this.nodeId = Hex.toHexString(nodeId.getBytes());
        }

        public String toString() {
            return (((((("{\n" + "  ip = \"") + (ip)) + "\"\n") + "  nodeId = ") + (nodeId)) + "\n") + "}\n";
        }
    }
}

