/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.discovery;


import DiscoveryMode.Client;
import GroupProperty.DISCOVERY_SPI_ENABLED;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.Node;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryService;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.hazelcast.spi.partitiongroup.PartitionGroupStrategy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class DiscoverySpiTest extends HazelcastTestSupport {
    private static final MemberVersion VERSION = MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion());

    private static final ILogger LOGGER = Logger.getLogger(DiscoverySpiTest.class);

    @Test(expected = InvalidConfigurationException.class)
    public void whenStrategyClassNameNotExist_thenFailFast() {
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        DiscoveryConfig discoveryConfig = new DiscoveryConfig();
        discoveryConfig.addDiscoveryStrategyConfig(new DiscoveryStrategyConfig("non.existing.ClassName"));
        config.getNetworkConfig().getJoin().setDiscoveryConfig(discoveryConfig);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        createHazelcastInstance(config);
    }

    @Test
    public void givenDiscoveryStrategyFactoryExistOnClassPath_whenTheSameFactoryIsConfiguredExplicitly_thenOnlyOneInstanceOfStrategyIsCreated() {
        // ParametrizedDiscoveryStrategy has a static counter and throws an exception when its instantiated  more than
        // than once.
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
        DiscoveryStrategyConfig strategyConfig = new DiscoveryStrategyConfig(new DiscoverySpiTest.ParametrizedDiscoveryStrategyFactory());
        strategyConfig.addProperty("bool-property", true);
        discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
        // this will fail when the discovery strategy throws an exception
        createHazelcastInstance(config);
    }

    public static final class ParametrizedDiscoveryStrategy extends AbstractDiscoveryStrategy {
        private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();

        public ParametrizedDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
            boolean parameterPassed = getOrDefault(DiscoverySpiTest.ParametrizedDiscoveryStrategyFactory.BOOL_PROPERTY, false);
            if (!parameterPassed) {
                throw new AssertionError("configured parameter was not passed!");
            }
            if ((DiscoverySpiTest.ParametrizedDiscoveryStrategy.INSTANCE_COUNTER.getAndIncrement()) != 0) {
                throw new AssertionError("only 1 instance of a discovery strategy should be created");
            }
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return null;
        }
    }

    public static final class ParametrizedDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        public static final PropertyDefinition BOOL_PROPERTY = new com.hazelcast.config.properties.SimplePropertyDefinition("bool-property", true, PropertyTypeConverter.BOOLEAN);

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return DiscoverySpiTest.ParametrizedDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new DiscoverySpiTest.ParametrizedDiscoveryStrategy(logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return Collections.singleton(DiscoverySpiTest.ParametrizedDiscoveryStrategyFactory.BOOL_PROPERTY);
        }
    }

    @Test
    public void test_metadata_discovery_on_node_startup() throws Exception {
        String xmlFileName = "test-hazelcast-discovery-spi-metadata.xml";
        InputStream xmlResource = DiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        Config config = new XmlConfigBuilder(xmlResource).build();
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(1);
        try {
            HazelcastInstance hazelcastInstance1 = instanceFactory.newHazelcastInstance(config);
            Assert.assertNotNull(hazelcastInstance1);
            Member localMember = hazelcastInstance1.getCluster().getLocalMember();
            Assert.assertEquals(Byte.MAX_VALUE, ((byte) (localMember.getByteAttribute("test-byte"))));
            Assert.assertEquals(Short.MAX_VALUE, ((short) (localMember.getShortAttribute("test-short"))));
            Assert.assertEquals(Integer.MAX_VALUE, ((int) (localMember.getIntAttribute("test-int"))));
            Assert.assertEquals(Long.MAX_VALUE, ((long) (localMember.getLongAttribute("test-long"))));
            Assert.assertEquals(Float.MAX_VALUE, localMember.getFloatAttribute("test-float"), 0);
            Assert.assertEquals(Double.MAX_VALUE, localMember.getDoubleAttribute("test-double"), 0);
            Assert.assertTrue(localMember.getBooleanAttribute("test-boolean"));
            Assert.assertEquals("TEST", localMember.getStringAttribute("test-string"));
        } finally {
            instanceFactory.shutdownAll();
        }
    }

    @Test
    public void testSchema() throws Exception {
        String xmlFileName = "test-hazelcast-discovery-spi.xml";
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL schemaResource = DiscoverySpiTest.class.getClassLoader().getResource("hazelcast-config-3.11.xsd");
        Assert.assertNotNull(schemaResource);
        InputStream xmlResource = DiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        Source source = new StreamSource(xmlResource);
        Schema schema = factory.newSchema(schemaResource);
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    @Test
    public void testParsing() throws Exception {
        String xmlFileName = "test-hazelcast-discovery-spi.xml";
        InputStream xmlResource = DiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        Config config = new XmlConfigBuilder(xmlResource).build();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        AwsConfig awsConfig = joinConfig.getAwsConfig();
        Assert.assertFalse(awsConfig.isEnabled());
        TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
        Assert.assertFalse(tcpIpConfig.isEnabled());
        MulticastConfig multicastConfig = joinConfig.getMulticastConfig();
        Assert.assertFalse(multicastConfig.isEnabled());
        DiscoveryConfig discoveryConfig = joinConfig.getDiscoveryConfig();
        Assert.assertTrue(discoveryConfig.isEnabled());
        Assert.assertEquals(1, discoveryConfig.getDiscoveryStrategyConfigs().size());
        DiscoveryStrategyConfig providerConfig = discoveryConfig.getDiscoveryStrategyConfigs().iterator().next();
        Assert.assertEquals(3, providerConfig.getProperties().size());
        Assert.assertEquals("foo", providerConfig.getProperties().get("key-string"));
        Assert.assertEquals("123", providerConfig.getProperties().get("key-int"));
        Assert.assertEquals("true", providerConfig.getProperties().get("key-boolean"));
    }

    @Test
    public void testNodeStartup() {
        String xmlFileName = "test-hazelcast-discovery-spi.xml";
        Config config = DiscoverySpiTest.getDiscoverySPIConfig(xmlFileName);
        try {
            final HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
            final HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);
            final HazelcastInstance hazelcastInstance3 = Hazelcast.newHazelcastInstance(config);
            Assert.assertNotNull(hazelcastInstance1);
            Assert.assertNotNull(hazelcastInstance2);
            Assert.assertNotNull(hazelcastInstance3);
            HazelcastTestSupport.assertClusterSizeEventually(3, hazelcastInstance1, hazelcastInstance2, hazelcastInstance3);
        } finally {
            Hazelcast.shutdownAll();
        }
    }

    @Test
    public void testNodeFilter_from_xml() throws Exception {
        String xmlFileName = "test-hazelcast-discovery-spi.xml";
        InputStream xmlResource = DiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        Config config = new XmlConfigBuilder(xmlResource).build();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        DiscoveryConfig discoveryConfig = joinConfig.getDiscoveryConfig();
        Address address = new Address("localhost", 5701);
        DiscoveryServiceSettings settings = buildDiscoveryServiceSettings(address, discoveryConfig, Client);
        DiscoveryServiceProvider provider = new DefaultDiscoveryServiceProvider();
        DiscoveryService discoveryService = provider.newDiscoveryService(settings);
        discoveryService.start();
        discoveryService.discoverNodes();
        discoveryService.destroy();
        Field nodeFilterField = DefaultDiscoveryService.class.getDeclaredField("nodeFilter");
        nodeFilterField.setAccessible(true);
        DiscoverySpiTest.TestNodeFilter nodeFilter = ((DiscoverySpiTest.TestNodeFilter) (nodeFilterField.get(discoveryService)));
        Assert.assertEquals(4, nodeFilter.getNodes().size());
    }

    @Test
    public void test_AbstractDiscoveryStrategy_getOrNull() throws Exception {
        PropertyDefinition first = new com.hazelcast.config.properties.SimplePropertyDefinition("first", PropertyTypeConverter.STRING);
        PropertyDefinition second = new com.hazelcast.config.properties.SimplePropertyDefinition("second", PropertyTypeConverter.BOOLEAN);
        PropertyDefinition third = new com.hazelcast.config.properties.SimplePropertyDefinition("third", PropertyTypeConverter.INTEGER);
        PropertyDefinition fourth = new com.hazelcast.config.properties.SimplePropertyDefinition("fourth", true, PropertyTypeConverter.STRING);
        Map<String, Comparable> properties = new HashMap<String, Comparable>();
        properties.put("first", "value-first");
        properties.put("second", Boolean.FALSE);
        properties.put("third", 100);
        // system property > system environment > configuration
        // property 'first' => "value-first"
        // property 'second' => true
        DiscoverySpiTest.setEnvironment("test.second", "true");
        // property 'third' => 300
        DiscoverySpiTest.setEnvironment("test.third", "200");
        System.setProperty("test.third", "300");
        // property 'fourth' => null
        DiscoverySpiTest.PropertyDiscoveryStrategy strategy = new DiscoverySpiTest.PropertyDiscoveryStrategy(DiscoverySpiTest.LOGGER, properties);
        // without lookup of environment
        Assert.assertEquals("value-first", strategy.getOrNull(first));
        Assert.assertEquals(Boolean.FALSE, strategy.getOrNull(second));
        Assert.assertEquals(100, ((Integer) (strategy.getOrNull(third))).intValue());
        Assert.assertNull(strategy.getOrNull(fourth));
        // with lookup of environment (workaround to set environment doesn't work on all JDKs)
        if ((System.getenv("test.third")) != null) {
            Assert.assertEquals("value-first", strategy.getOrNull("test", first));
            Assert.assertEquals(Boolean.TRUE, strategy.getOrNull("test", second));
            Assert.assertEquals(300, ((Integer) (strategy.getOrNull("test", third))).intValue());
            Assert.assertNull(strategy.getOrNull("test", fourth));
        }
    }

    @Test
    public void test_AbstractDiscoveryStrategy_getOrDefault() throws Exception {
        PropertyDefinition value = new com.hazelcast.config.properties.SimplePropertyDefinition("value", PropertyTypeConverter.INTEGER);
        Map<String, Comparable> properties = Collections.emptyMap();
        DiscoverySpiTest.PropertyDiscoveryStrategy strategy = new DiscoverySpiTest.PropertyDiscoveryStrategy(DiscoverySpiTest.LOGGER, properties);
        Assert.assertEquals(1111, ((long) (strategy.getOrDefault(value, 1111))));
        Assert.assertEquals(1111, ((long) (strategy.getOrDefault("test", value, 1111))));
    }

    @Test(expected = RuntimeException.class)
    public void testSPIAwareMemberGroupFactoryInvalidConfig() throws Exception {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        try {
            MemberGroupFactory groupFactory = new com.hazelcast.partition.membergroup.SPIAwareMemberGroupFactory(HazelcastTestSupport.getNode(hazelcastInstance).getDiscoveryService());
            Collection<Member> members = DiscoverySpiTest.createMembers();
            groupFactory.createMemberGroups(members);
        } finally {
            hazelcastInstance.shutdown();
        }
    }

    @Test
    public void testSPIAwareMemberGroupFactoryCreateMemberGroups() throws Exception {
        String xmlFileName = "test-hazelcast-discovery-spi-metadata.xml";
        Config config = DiscoverySpiTest.getDiscoverySPIConfig(xmlFileName);
        // we create this instance in order to fully create Node
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        Assert.assertNotNull(node);
        MemberGroupFactory groupFactory = new com.hazelcast.partition.membergroup.SPIAwareMemberGroupFactory(node.getDiscoveryService());
        Collection<Member> members = DiscoverySpiTest.createMembers();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 2, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 2, memberGroup.size());
        }
        hazelcastInstance.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_enabled_whenDiscoveryConfigIsNull() {
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        config.getNetworkConfig().getJoin().setDiscoveryConfig(null);
    }

    @Test
    public void testCustomDiscoveryService_whenDiscoveredNodes_isNull() {
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        DiscoveryServiceProvider discoveryServiceProvider = new DiscoveryServiceProvider() {
            public DiscoveryService newDiscoveryService(DiscoveryServiceSettings arg0) {
                DiscoveryService mocked = Mockito.mock(DiscoveryService.class);
                Mockito.when(mocked.discoverNodes()).thenReturn(null);
                return mocked;
            }
        };
        config.getNetworkConfig().getJoin().getDiscoveryConfig().setDiscoveryServiceProvider(discoveryServiceProvider);
        try {
            Hazelcast.newHazelcastInstance(config);
            Assert.fail("Instance should not be started!");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testCustomDiscoveryService_whenDiscoveredNodes_isEmpty() {
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        final DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        DiscoveryServiceProvider discoveryServiceProvider = new DiscoveryServiceProvider() {
            public DiscoveryService newDiscoveryService(DiscoveryServiceSettings arg0) {
                Mockito.when(discoveryService.discoverNodes()).thenReturn(Collections.<DiscoveryNode>emptyList());
                return discoveryService;
            }
        };
        config.getNetworkConfig().getJoin().getDiscoveryConfig().setDiscoveryServiceProvider(discoveryServiceProvider);
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        try {
            Mockito.verify(discoveryService, Mockito.atLeastOnce()).discoverNodes();
        } finally {
            instance.getLifecycleService().terminate();
        }
    }

    @Test
    public void testMemberGroup_givenSPIMemberGroupIsActived_whenInstanceStarting_wontThrowNPE() {
        // this test has no assert. it's a regression test checking an instance can start when a SPI-driven member group
        // strategy is configured. see #11681
        Config config = new Config();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(DiscoverySpiTest.MetadataProvidingDiscoveryStrategy.class.getName());
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        config.getPartitionGroupConfig().setGroupType(MemberGroupType.SPI).setEnabled(true);
        HazelcastInstance hazelcastInstance = null;
        try {
            // check the instance can actually be started
            hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        } finally {
            if (hazelcastInstance != null) {
                hazelcastInstance.shutdown();
            }
        }
    }

    private static class PropertyDiscoveryStrategy extends AbstractDiscoveryStrategy {
        PropertyDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return null;
        }

        @Override
        public <T extends Comparable> T getOrNull(PropertyDefinition property) {
            return super.getOrNull(property);
        }

        @Override
        public <T extends Comparable> T getOrNull(String prefix, PropertyDefinition property) {
            return super.getOrNull(prefix, property);
        }

        @Override
        public <T extends Comparable> T getOrDefault(PropertyDefinition property, T defaultValue) {
            return super.getOrDefault(property, defaultValue);
        }

        @Override
        public <T extends Comparable> T getOrDefault(String prefix, PropertyDefinition property, T defaultValue) {
            return super.getOrDefault(prefix, property, defaultValue);
        }
    }

    private static class TestDiscoveryStrategy implements DiscoveryStrategy {
        @Override
        public void start() {
        }

        @Override
        public Collection<DiscoveryNode> discoverNodes() {
            try {
                List<DiscoveryNode> discoveryNodes = new ArrayList<DiscoveryNode>(4);
                Address address = new Address("127.0.0.1", 50001);
                discoveryNodes.add(new SimpleDiscoveryNode(address));
                address = new Address("127.0.0.1", 50002);
                discoveryNodes.add(new SimpleDiscoveryNode(address));
                address = new Address("127.0.0.1", 50003);
                discoveryNodes.add(new SimpleDiscoveryNode(address));
                address = new Address("127.0.0.1", 50004);
                discoveryNodes.add(new SimpleDiscoveryNode(address));
                return discoveryNodes;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void destroy() {
        }

        @Override
        public PartitionGroupStrategy getPartitionGroupStrategy() {
            return null;
        }

        @Override
        public Map<String, Object> discoverLocalMetadata() {
            return Collections.emptyMap();
        }
    }

    public static class TestDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        private final Collection<PropertyDefinition> propertyDefinitions;

        public TestDiscoveryStrategyFactory() {
            List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
            propertyDefinitions.add(new com.hazelcast.config.properties.SimplePropertyDefinition("key-string", PropertyTypeConverter.STRING));
            propertyDefinitions.add(new com.hazelcast.config.properties.SimplePropertyDefinition("key-int", PropertyTypeConverter.INTEGER));
            propertyDefinitions.add(new com.hazelcast.config.properties.SimplePropertyDefinition("key-boolean", PropertyTypeConverter.BOOLEAN));
            propertyDefinitions.add(new com.hazelcast.config.properties.SimplePropertyDefinition("key-something", true, PropertyTypeConverter.STRING));
            this.propertyDefinitions = Collections.unmodifiableCollection(propertyDefinitions);
        }

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return DiscoverySpiTest.TestDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new DiscoverySpiTest.TestDiscoveryStrategy();
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return propertyDefinitions;
        }
    }

    public static class CollectingDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        private final List<DiscoveryNode> discoveryNodes;

        private CollectingDiscoveryStrategyFactory(List<DiscoveryNode> discoveryNodes) {
            this.discoveryNodes = discoveryNodes;
        }

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return DiscoverySpiTest.CollectingDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode node, ILogger logger, Map<String, Comparable> properties) {
            return new DiscoverySpiTest.CollectingDiscoveryStrategy(node, discoveryNodes, logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return null;
        }
    }

    private static class CollectingDiscoveryStrategy extends AbstractDiscoveryStrategy {
        private final List<DiscoveryNode> discoveryNodes;

        private final DiscoveryNode discoveryNode;

        CollectingDiscoveryStrategy(DiscoveryNode discoveryNode, List<DiscoveryNode> discoveryNodes, ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
            this.discoveryNodes = discoveryNodes;
            this.discoveryNode = discoveryNode;
        }

        @Override
        public void start() {
            super.start();
            discoveryNodes.add(discoveryNode);
            getLogger();
            getProperties();
        }

        // need to provide a custom impl
        @Override
        public PartitionGroupStrategy getPartitionGroupStrategy() {
            return new DiscoverySpiTest.SPIPartitionGroupStrategy();
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return new ArrayList<DiscoveryNode>(discoveryNodes);
        }

        @Override
        public void destroy() {
            super.destroy();
            discoveryNodes.remove(discoveryNode);
        }
    }

    public static class TestNodeFilter implements NodeFilter {
        private final List<DiscoveryNode> nodes = new ArrayList<DiscoveryNode>();

        @Override
        public boolean test(DiscoveryNode candidate) {
            nodes.add(candidate);
            return true;
        }

        private List<DiscoveryNode> getNodes() {
            return nodes;
        }
    }

    public static class MetadataProvidingDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return DiscoverySpiTest.MetadataProvidingDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new DiscoverySpiTest.MetadataProvidingDiscoveryStrategy(discoveryNode, logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return Arrays.asList(((PropertyDefinition) (new com.hazelcast.config.properties.SimplePropertyDefinition("key-string", true, PropertyTypeConverter.STRING))), new com.hazelcast.config.properties.SimplePropertyDefinition("key-int", true, PropertyTypeConverter.INTEGER), new com.hazelcast.config.properties.SimplePropertyDefinition("key-boolean", true, PropertyTypeConverter.BOOLEAN));
        }
    }

    private static class MetadataProvidingDiscoveryStrategy extends AbstractDiscoveryStrategy {
        private final DiscoveryNode discoveryNode;

        MetadataProvidingDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
            this.discoveryNode = discoveryNode;
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return Collections.singleton(discoveryNode);
        }

        @Override
        public PartitionGroupStrategy getPartitionGroupStrategy() {
            return new DiscoverySpiTest.SPIPartitionGroupStrategy();
        }

        @Override
        public Map<String, Object> discoverLocalMetadata() {
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("test-byte", Byte.MAX_VALUE);
            metadata.put("test-short", Short.MAX_VALUE);
            metadata.put("test-int", Integer.MAX_VALUE);
            metadata.put("test-long", Long.MAX_VALUE);
            metadata.put("test-float", Float.MAX_VALUE);
            metadata.put("test-double", Double.MAX_VALUE);
            metadata.put("test-boolean", Boolean.TRUE);
            metadata.put("test-string", "TEST");
            return metadata;
        }
    }

    private static class SPIPartitionGroupStrategy implements PartitionGroupStrategy {
        @Override
        public Iterable<MemberGroup> getMemberGroups() {
            List<MemberGroup> groups = new ArrayList<MemberGroup>();
            try {
                groups.add(new com.hazelcast.partition.membergroup.DefaultMemberGroup(DiscoverySpiTest.createMembers()));
                groups.add(new com.hazelcast.partition.membergroup.DefaultMemberGroup(DiscoverySpiTest.createMembers()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return groups;
        }
    }
}

