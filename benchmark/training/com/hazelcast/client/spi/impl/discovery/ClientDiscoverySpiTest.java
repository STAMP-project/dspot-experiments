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
package com.hazelcast.client.spi.impl.discovery;


import GroupProperty.DISCOVERY_SPI_ENABLED;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientClasspathXmlConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.connection.AddressTranslator;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.spi.discovery.NodeFilter;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryService;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.hazelcast.spi.partitiongroup.PartitionGroupStrategy;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
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


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClientDiscoverySpiTest extends HazelcastTestSupport {
    private static final ILogger LOGGER = Logger.getLogger(ClientDiscoverySpiTest.class);

    @Test
    public void testSchema() throws Exception {
        String xmlFileName = "hazelcast-client-discovery-spi-test.xml";
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL schemaResource = ClientDiscoverySpiTest.class.getClassLoader().getResource("hazelcast-client-config-3.11.xsd");
        Schema schema = factory.newSchema(schemaResource);
        InputStream xmlResource = ClientDiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        Source source = new StreamSource(xmlResource);
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    @Test
    public void testParsing() {
        String xmlFileName = "hazelcast-client-discovery-spi-test.xml";
        InputStream xmlResource = ClientDiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        ClientConfig clientConfig = new XmlClientConfigBuilder(xmlResource).build();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        DiscoveryConfig discoveryConfig = networkConfig.getDiscoveryConfig();
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
        Config config = new Config();
        config.setProperty("hazelcast.discovery.enabled", "true");
        config.getNetworkConfig().setPort(50001);
        InterfacesConfig interfaces = config.getNetworkConfig().getInterfaces();
        interfaces.clear();
        interfaces.setEnabled(true);
        interfaces.addInterface("127.0.0.1");
        List<DiscoveryNode> discoveryNodes = new CopyOnWriteArrayList<DiscoveryNode>();
        DiscoveryStrategyFactory factory = new ClientDiscoverySpiTest.CollectingDiscoveryStrategyFactory(discoveryNodes);
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
        discoveryConfig.getDiscoveryStrategyConfigs().clear();
        DiscoveryStrategyConfig strategyConfig = new DiscoveryStrategyConfig(factory, Collections.<String, Comparable>emptyMap());
        discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
        final HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance hazelcastInstance3 = Hazelcast.newHazelcastInstance(config);
        try {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setProperty("hazelcast.discovery.enabled", "true");
            discoveryConfig = clientConfig.getNetworkConfig().getDiscoveryConfig();
            discoveryConfig.getDiscoveryStrategyConfigs().clear();
            strategyConfig = new DiscoveryStrategyConfig(factory, Collections.<String, Comparable>emptyMap());
            discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
            final HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
            Assert.assertNotNull(hazelcastInstance1);
            Assert.assertNotNull(hazelcastInstance2);
            Assert.assertNotNull(hazelcastInstance3);
            Assert.assertNotNull(client);
            assertClusterSizeEventually(3, hazelcastInstance1, hazelcastInstance2, hazelcastInstance3, client);
        } finally {
            HazelcastClient.shutdownAll();
            Hazelcast.shutdownAll();
        }
    }

    @Test
    public void testDiscoveryServiceLifecycleMethodsCalledWhenClientAndServerStartAndShutdown() {
        // Given
        Config config = new Config();
        config.setProperty("hazelcast.discovery.enabled", "true");
        config.getNetworkConfig().setPort(50001);
        InterfacesConfig interfaces = config.getNetworkConfig().getInterfaces();
        interfaces.clear();
        interfaces.setEnabled(true);
        interfaces.addInterface("127.0.0.1");
        // Both server and client are using the same LifecycleDiscoveryStrategyFactory so latch count is set to 2.
        CountDownLatch startLatch = new CountDownLatch(2);
        CountDownLatch stopLatch = new CountDownLatch(2);
        List<DiscoveryNode> discoveryNodes = new CopyOnWriteArrayList<DiscoveryNode>();
        DiscoveryStrategyFactory factory = new ClientDiscoverySpiTest.LifecycleDiscoveryStrategyFactory(startLatch, stopLatch, discoveryNodes);
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
        discoveryConfig.getDiscoveryStrategyConfigs().clear();
        DiscoveryStrategyConfig strategyConfig = new DiscoveryStrategyConfig(factory, Collections.<String, Comparable>emptyMap());
        discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty("hazelcast.discovery.enabled", "true");
        discoveryConfig = clientConfig.getNetworkConfig().getDiscoveryConfig();
        discoveryConfig.getDiscoveryStrategyConfigs().clear();
        strategyConfig = new DiscoveryStrategyConfig(factory, Collections.<String, Comparable>emptyMap());
        discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        Assert.assertNotNull(hazelcastInstance);
        Assert.assertNotNull(client);
        // When
        HazelcastClient.shutdownAll();
        Hazelcast.shutdownAll();
        // Then
        assertOpenEventually(startLatch);
        assertOpenEventually(stopLatch);
    }

    @Test
    public void testClientCanConnect_afterDiscoveryStrategyThrowsException() {
        Config config = new Config();
        config.getNetworkConfig().setPort(50001);
        Hazelcast.newHazelcastInstance(config);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        DiscoveryConfig discoveryConfig = clientConfig.getNetworkConfig().getDiscoveryConfig();
        DiscoveryStrategyFactory factory = new ClientDiscoverySpiTest.ExceptionThrowingDiscoveryStrategyFactory();
        DiscoveryStrategyConfig strategyConfig = new DiscoveryStrategyConfig(factory, Collections.<String, Comparable>emptyMap());
        discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);
        HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Test
    public void testNodeFilter_from_xml() throws Exception {
        String xmlFileName = "hazelcast-client-discovery-spi-test.xml";
        InputStream xmlResource = ClientDiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        ClientConfig clientConfig = new XmlClientConfigBuilder(xmlResource).build();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        DiscoveryConfig discoveryConfig = networkConfig.getDiscoveryConfig();
        DiscoveryServiceProvider provider = new DefaultDiscoveryServiceProvider();
        DiscoveryService discoveryService = provider.newDiscoveryService(buildDiscoveryServiceSettings(discoveryConfig));
        discoveryService.start();
        discoveryService.discoverNodes();
        discoveryService.destroy();
        Field nodeFilterField = DefaultDiscoveryService.class.getDeclaredField("nodeFilter");
        nodeFilterField.setAccessible(true);
        ClientDiscoverySpiTest.TestNodeFilter nodeFilter = ((ClientDiscoverySpiTest.TestNodeFilter) (nodeFilterField.get(discoveryService)));
        Assert.assertEquals(4, nodeFilter.getNodes().size());
    }

    @Test
    public void test_discovery_address_translator() throws Exception {
        String xmlFileName = "hazelcast-client-discovery-spi-test.xml";
        InputStream xmlResource = ClientDiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        ClientConfig clientConfig = new XmlClientConfigBuilder(xmlResource).build();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        DiscoveryConfig discoveryConfig = networkConfig.getDiscoveryConfig();
        DiscoveryServiceProvider provider = new DefaultDiscoveryServiceProvider();
        DiscoveryService discoveryService = provider.newDiscoveryService(buildDiscoveryServiceSettings(discoveryConfig));
        AddressTranslator translator = new DiscoveryAddressTranslator(discoveryService, false);
        Address address = new Address("127.0.0.1", 50001);
        Assert.assertNull(translator.translate(null));
        Assert.assertEquals(address, translator.translate(address));
        // Enforce refresh of the internal mapping
        Assert.assertEquals(address, translator.translate(address));
    }

    @Test
    public void test_discovery_address_translator_with_public_ip() throws Exception {
        String xmlFileName = "hazelcast-client-discovery-spi-test.xml";
        InputStream xmlResource = ClientDiscoverySpiTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        ClientConfig clientConfig = new XmlClientConfigBuilder(xmlResource).build();
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        DiscoveryConfig discoveryConfig = networkConfig.getDiscoveryConfig();
        DiscoveryServiceProvider provider = new DefaultDiscoveryServiceProvider();
        DiscoveryService discoveryService = provider.newDiscoveryService(buildDiscoveryServiceSettings(discoveryConfig));
        AddressTranslator translator = new DiscoveryAddressTranslator(discoveryService, true);
        Address publicAddress = new Address("127.0.0.1", 50001);
        Address privateAddress = new Address("127.0.0.1", 1);
        // Enforce refresh of the internal mapping
        Assert.assertEquals(publicAddress, translator.translate(privateAddress));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_enabled_whenDiscoveryConfigIsNull() {
        ClientConfig config = new ClientConfig();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setDiscoveryConfig(null);
    }

    @Test
    public void test_enabled_whenDiscoveryConfigIsEmpty() {
        ClientConfig config = new ClientConfig();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setConnectionAttemptLimit(1);
        networkConfig.setConnectionAttemptPeriod(1);
        try {
            HazelcastClient.newHazelcastClient(config);
        } catch (IllegalStateException expected) {
            // no server available
        }
    }

    @Test
    public void test_CustomDiscoveryService_whenDiscoveredNodes_isNull() {
        ClientConfig config = new ClientConfig();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        final DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        Mockito.when(discoveryService.discoverNodes()).thenReturn(null);
        DiscoveryServiceProvider discoveryServiceProvider = new DiscoveryServiceProvider() {
            public DiscoveryService newDiscoveryService(DiscoveryServiceSettings arg0) {
                return discoveryService;
            }
        };
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setConnectionAttemptLimit(1);
        networkConfig.setConnectionAttemptPeriod(1);
        networkConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(new DiscoveryStrategyConfig());
        networkConfig.getDiscoveryConfig().setDiscoveryServiceProvider(discoveryServiceProvider);
        try {
            HazelcastClient.newHazelcastClient(config);
            Assert.fail("Client cannot start, discovery nodes is null!");
        } catch (NullPointerException expected) {
            // discovered nodes is null
        }
        Mockito.verify(discoveryService).discoverNodes();
    }

    @Test
    public void test_CustomDiscoveryService_whenDiscoveredNodes_isEmpty() {
        ClientConfig config = new ClientConfig();
        config.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        final DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        Mockito.when(discoveryService.discoverNodes()).thenReturn(Collections.<DiscoveryNode>emptyList());
        DiscoveryServiceProvider discoveryServiceProvider = new DiscoveryServiceProvider() {
            public DiscoveryService newDiscoveryService(DiscoveryServiceSettings arg0) {
                return discoveryService;
            }
        };
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setConnectionAttemptLimit(1);
        networkConfig.setConnectionAttemptPeriod(1);
        networkConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(new DiscoveryStrategyConfig());
        networkConfig.getDiscoveryConfig().setDiscoveryServiceProvider(discoveryServiceProvider);
        try {
            HazelcastClient.newHazelcastClient(config);
        } catch (IllegalStateException expected) {
            // no server available
        }
        Mockito.verify(discoveryService).discoverNodes();
    }

    @Test(expected = IllegalStateException.class)
    public void testDiscoveryEnabledNoLocalhost() {
        Hazelcast.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(DISCOVERY_SPI_ENABLED.getName(), "true");
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        networkConfig.setConnectionAttemptLimit(1);
        networkConfig.setConnectionAttemptPeriod(1);
        networkConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(new DiscoveryStrategyConfig(new ClientDiscoverySpiTest.NoMemberDiscoveryStrategyFactory(), Collections.<String, Comparable>emptyMap()));
        HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Test
    public void testDiscoveryDisabledLocalhost() {
        Hazelcast.newHazelcastInstance();
        // should not throw any exception, localhost is added into the list of addresses
        HazelcastClient.newHazelcastClient();
    }

    @Test(expected = IllegalStateException.class)
    public void testMulticastDiscoveryEnabledNoLocalhost() {
        Hazelcast.newHazelcastInstance();
        ClientClasspathXmlConfig clientConfig = new ClientClasspathXmlConfig("hazelcast-client-dummy-multicast-discovery-test.xml");
        HazelcastClient.newHazelcastClient(clientConfig);
    }

    private static class TestDiscoveryStrategy implements DiscoveryStrategy {
        @Override
        public void start() {
        }

        @Override
        public Collection<DiscoveryNode> discoverNodes() {
            try {
                List<DiscoveryNode> discoveryNodes = new ArrayList<DiscoveryNode>(4);
                Address privateAddress = new Address("127.0.0.1", 1);
                Address publicAddress = new Address("127.0.0.1", 50001);
                discoveryNodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(privateAddress, publicAddress));
                privateAddress = new Address("127.0.0.1", 2);
                publicAddress = new Address("127.0.0.1", 50002);
                discoveryNodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(privateAddress, publicAddress));
                privateAddress = new Address("127.0.0.1", 3);
                publicAddress = new Address("127.0.0.1", 50003);
                discoveryNodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(privateAddress, publicAddress));
                privateAddress = new Address("127.0.0.1", 4);
                publicAddress = new Address("127.0.0.1", 50004);
                discoveryNodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(privateAddress, publicAddress));
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
            return ClientDiscoverySpiTest.TestDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new ClientDiscoverySpiTest.TestDiscoveryStrategy();
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return propertyDefinitions;
        }
    }

    private static class FirstCallExceptionThrowingDiscoveryStrategy implements DiscoveryStrategy {
        AtomicBoolean firstCall = new AtomicBoolean(true);

        @Override
        public void start() {
        }

        @Override
        public Collection<DiscoveryNode> discoverNodes() {
            if (firstCall.compareAndSet(true, false)) {
                throw new RuntimeException();
            }
            try {
                List<DiscoveryNode> discoveryNodes = new ArrayList<DiscoveryNode>(1);
                Address privateAddress = new Address("127.0.0.1", 50001);
                discoveryNodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(privateAddress));
                return discoveryNodes;
            } catch (UnknownHostException e) {
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

    public static class ExceptionThrowingDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        public ExceptionThrowingDiscoveryStrategyFactory() {
        }

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return ClientDiscoverySpiTest.FirstCallExceptionThrowingDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new ClientDiscoverySpiTest.FirstCallExceptionThrowingDiscoveryStrategy();
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return Collections.EMPTY_LIST;
        }
    }

    public static class CollectingDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        private final List<DiscoveryNode> discoveryNodes;

        private CollectingDiscoveryStrategyFactory(List<DiscoveryNode> discoveryNodes) {
            this.discoveryNodes = discoveryNodes;
        }

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return ClientDiscoverySpiTest.CollectingDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new ClientDiscoverySpiTest.CollectingDiscoveryStrategy(discoveryNode, discoveryNodes, logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return null;
        }
    }

    private static class CollectingDiscoveryStrategy extends AbstractDiscoveryStrategy {
        private final List<DiscoveryNode> discoveryNodes;

        private final DiscoveryNode discoveryNode;

        public CollectingDiscoveryStrategy(DiscoveryNode discoveryNode, List<DiscoveryNode> discoveryNodes, ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
            this.discoveryNodes = discoveryNodes;
            this.discoveryNode = discoveryNode;
        }

        @Override
        public void start() {
            super.start();
            if ((discoveryNode) != null) {
                discoveryNodes.add(discoveryNode);
            }
            getLogger();
            getProperties();
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

    public static class LifecycleDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        private final CountDownLatch startLatch;

        private final CountDownLatch stopLatch;

        private final List<DiscoveryNode> discoveryNodes;

        private LifecycleDiscoveryStrategyFactory(CountDownLatch startLatch, CountDownLatch stopLatch, List<DiscoveryNode> discoveryNodes) {
            this.startLatch = startLatch;
            this.stopLatch = stopLatch;
            this.discoveryNodes = discoveryNodes;
        }

        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return ClientDiscoverySpiTest.LifecycleDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new ClientDiscoverySpiTest.LifecycleDiscoveryStrategy(startLatch, stopLatch, discoveryNode, discoveryNodes, logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return null;
        }
    }

    private static class LifecycleDiscoveryStrategy extends AbstractDiscoveryStrategy {
        private final CountDownLatch startLatch;

        private final CountDownLatch stopLatch;

        private final List<DiscoveryNode> discoveryNodes;

        private final DiscoveryNode discoveryNode;

        public LifecycleDiscoveryStrategy(CountDownLatch startLatch, CountDownLatch stopLatch, DiscoveryNode discoveryNode, List<DiscoveryNode> discoveryNodes, ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
            this.startLatch = startLatch;
            this.stopLatch = stopLatch;
            this.discoveryNodes = discoveryNodes;
            this.discoveryNode = discoveryNode;
        }

        @Override
        public void start() {
            super.start();
            startLatch.countDown();
            if ((discoveryNode) != null) {
                discoveryNodes.add(discoveryNode);
            }
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return new ArrayList<DiscoveryNode>(discoveryNodes);
        }

        @Override
        public void destroy() {
            super.destroy();
            stopLatch.countDown();
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

    private static class NoMemberDiscoveryStrategy extends AbstractDiscoveryStrategy {
        public NoMemberDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
            super(logger, properties);
        }

        @Override
        public Iterable<DiscoveryNode> discoverNodes() {
            return null;
        }
    }

    public static class NoMemberDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
        @Override
        public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
            return ClientDiscoverySpiTest.NoMemberDiscoveryStrategy.class;
        }

        @Override
        public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
            return new ClientDiscoverySpiTest.NoMemberDiscoveryStrategy(logger, properties);
        }

        @Override
        public Collection<PropertyDefinition> getConfigurationProperties() {
            return null;
        }
    }
}

