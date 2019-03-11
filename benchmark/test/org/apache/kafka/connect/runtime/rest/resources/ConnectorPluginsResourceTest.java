/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.rest.resources;


import ConnectorType.SINK;
import ConnectorType.SOURCE;
import ConnectorType.UNKNOWN;
import Importance.HIGH;
import Importance.LOW;
import Importance.MEDIUM;
import Type.INT;
import Type.LIST;
import Type.STRING;
import Width.LONG;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.BadRequestException;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Recommender;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.runtime.AbstractHerder;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.TestSinkConnector;
import org.apache.kafka.connect.runtime.TestSourceConnector;
import org.apache.kafka.connect.runtime.isolation.PluginClassLoader;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.runtime.rest.RestClient;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfos;
import org.apache.kafka.connect.runtime.rest.entities.ConfigKeyInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConfigValueInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorPluginInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorType;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.tools.MockConnector;
import org.apache.kafka.connect.tools.MockSinkConnector;
import org.apache.kafka.connect.tools.MockSourceConnector;
import org.apache.kafka.connect.tools.SchemaSourceConnector;
import org.apache.kafka.connect.tools.VerifiableSinkConnector;
import org.apache.kafka.connect.tools.VerifiableSourceConnector;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(RestClient.class)
@PowerMockIgnore("javax.management.*")
public class ConnectorPluginsResourceTest {
    private static Map<String, String> props;

    private static Map<String, String> partialProps = new HashMap<>();

    static {
        ConnectorPluginsResourceTest.partialProps.put("name", "test");
        ConnectorPluginsResourceTest.partialProps.put("test.string.config", "testString");
        ConnectorPluginsResourceTest.partialProps.put("test.int.config", "1");
        ConnectorPluginsResourceTest.partialProps.put("test.list.config", "a,b");
        ConnectorPluginsResourceTest.props = new HashMap<>(ConnectorPluginsResourceTest.partialProps);
        ConnectorPluginsResourceTest.props.put("connector.class", ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getSimpleName());
        ConnectorPluginsResourceTest.props.put("plugin.path", null);
    }

    private static final ConfigInfos CONFIG_INFOS;

    private static final ConfigInfos PARTIAL_CONFIG_INFOS;

    private static final int ERROR_COUNT = 0;

    private static final int PARTIAL_CONFIG_ERROR_COUNT = 1;

    private static final Set<PluginDesc<Connector>> CONNECTOR_PLUGINS = new TreeSet<>();

    static {
        List<ConfigInfo> configs = new LinkedList<>();
        List<ConfigInfo> partialConfigs = new LinkedList<>();
        ConfigDef connectorConfigDef = ConnectorConfig.configDef();
        List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.props);
        List<ConfigValue> partialConnectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.partialProps);
        ConfigInfos result = AbstractHerder.generateResult(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getName(), connectorConfigDef.configKeys(), connectorConfigValues, Collections.<String>emptyList());
        ConfigInfos partialResult = AbstractHerder.generateResult(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getName(), connectorConfigDef.configKeys(), partialConnectorConfigValues, Collections.<String>emptyList());
        configs.addAll(result.values());
        partialConfigs.addAll(partialResult.values());
        ConfigKeyInfo configKeyInfo = new ConfigKeyInfo("test.string.config", "STRING", true, null, "HIGH", "Test configuration for string type.", null, (-1), "NONE", "test.string.config", Collections.<String>emptyList());
        ConfigValueInfo configValueInfo = new ConfigValueInfo("test.string.config", "testString", Collections.<String>emptyList(), Collections.<String>emptyList(), true);
        ConfigInfo configInfo = new ConfigInfo(configKeyInfo, configValueInfo);
        configs.add(configInfo);
        partialConfigs.add(configInfo);
        configKeyInfo = new ConfigKeyInfo("test.int.config", "INT", true, null, "MEDIUM", "Test configuration for integer type.", "Test", 1, "MEDIUM", "test.int.config", Collections.<String>emptyList());
        configValueInfo = new ConfigValueInfo("test.int.config", "1", Arrays.asList("1", "2", "3"), Collections.<String>emptyList(), true);
        configInfo = new ConfigInfo(configKeyInfo, configValueInfo);
        configs.add(configInfo);
        partialConfigs.add(configInfo);
        configKeyInfo = new ConfigKeyInfo("test.string.config.default", "STRING", false, "", "LOW", "Test configuration with default value.", null, (-1), "NONE", "test.string.config.default", Collections.<String>emptyList());
        configValueInfo = new ConfigValueInfo("test.string.config.default", "", Collections.<String>emptyList(), Collections.<String>emptyList(), true);
        configInfo = new ConfigInfo(configKeyInfo, configValueInfo);
        configs.add(configInfo);
        partialConfigs.add(configInfo);
        configKeyInfo = new ConfigKeyInfo("test.list.config", "LIST", true, null, "HIGH", "Test configuration for list type.", "Test", 2, "LONG", "test.list.config", Collections.<String>emptyList());
        configValueInfo = new ConfigValueInfo("test.list.config", "a,b", Arrays.asList("a", "b", "c"), Collections.<String>emptyList(), true);
        configInfo = new ConfigInfo(configKeyInfo, configValueInfo);
        configs.add(configInfo);
        partialConfigs.add(configInfo);
        CONFIG_INFOS = new ConfigInfos(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getName(), ConnectorPluginsResourceTest.ERROR_COUNT, Collections.singletonList("Test"), configs);
        PARTIAL_CONFIG_INFOS = new ConfigInfos(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getName(), ConnectorPluginsResourceTest.PARTIAL_CONFIG_ERROR_COUNT, Collections.singletonList("Test"), partialConfigs);
        Class<?>[] abstractConnectorClasses = new Class<?>[]{ Connector.class, SourceConnector.class, SinkConnector.class };
        Class<?>[] connectorClasses = new Class<?>[]{ VerifiableSourceConnector.class, VerifiableSinkConnector.class, MockSourceConnector.class, MockSinkConnector.class, MockConnector.class, SchemaSourceConnector.class, ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class };
        try {
            for (Class<?> klass : abstractConnectorClasses) {
                @SuppressWarnings("unchecked")
                ConnectorPluginsResourceTest.MockConnectorPluginDesc pluginDesc = new ConnectorPluginsResourceTest.MockConnectorPluginDesc(((Class<? extends Connector>) (klass)), "0.0.0");
                ConnectorPluginsResourceTest.CONNECTOR_PLUGINS.add(pluginDesc);
            }
            for (Class<?> klass : connectorClasses) {
                @SuppressWarnings("unchecked")
                ConnectorPluginsResourceTest.MockConnectorPluginDesc pluginDesc = new ConnectorPluginsResourceTest.MockConnectorPluginDesc(((Class<? extends Connector>) (klass)));
                ConnectorPluginsResourceTest.CONNECTOR_PLUGINS.add(pluginDesc);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private Herder herder;

    @Mock
    private Plugins plugins;

    private ConnectorPluginsResource connectorPluginsResource;

    @Test
    public void testValidateConfigWithSingleErrorDueToMissingConnectorClassname() throws Throwable {
        herder.validateConnectorConfig(EasyMock.eq(ConnectorPluginsResourceTest.partialProps));
        PowerMock.expectLastCall().andAnswer(((IAnswer<ConfigInfos>) (() -> {
            ConfigDef connectorConfigDef = ConnectorConfig.configDef();
            List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.partialProps);
            Connector connector = new org.apache.kafka.connect.runtime.rest.resources.ConnectorPluginsResourceTestConnector();
            Config config = connector.validate(ConnectorPluginsResourceTest.partialProps);
            ConfigDef configDef = connector.config();
            Map<String, ConfigDef.ConfigKey> configKeys = configDef.configKeys();
            List<ConfigValue> configValues = config.configValues();
            Map<String, ConfigDef.ConfigKey> resultConfigKeys = new HashMap<>(configKeys);
            resultConfigKeys.putAll(connectorConfigDef.configKeys());
            configValues.addAll(connectorConfigValues);
            return AbstractHerder.generateResult(.class.getName(), resultConfigKeys, configValues, Collections.singletonList("Test"));
        })));
        PowerMock.replayAll();
        // This call to validateConfigs does not throw a BadRequestException because we've mocked
        // validateConnectorConfig.
        ConfigInfos configInfos = connectorPluginsResource.validateConfigs(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getSimpleName(), ConnectorPluginsResourceTest.partialProps);
        Assert.assertEquals(ConnectorPluginsResourceTest.PARTIAL_CONFIG_INFOS.name(), configInfos.name());
        Assert.assertEquals(ConnectorPluginsResourceTest.PARTIAL_CONFIG_INFOS.errorCount(), configInfos.errorCount());
        Assert.assertEquals(ConnectorPluginsResourceTest.PARTIAL_CONFIG_INFOS.groups(), configInfos.groups());
        Assert.assertEquals(new java.util.HashSet(ConnectorPluginsResourceTest.PARTIAL_CONFIG_INFOS.values()), new java.util.HashSet(configInfos.values()));
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateConfigWithSimpleName() throws Throwable {
        herder.validateConnectorConfig(EasyMock.eq(ConnectorPluginsResourceTest.props));
        PowerMock.expectLastCall().andAnswer(((IAnswer<ConfigInfos>) (() -> {
            ConfigDef connectorConfigDef = ConnectorConfig.configDef();
            List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.props);
            Connector connector = new org.apache.kafka.connect.runtime.rest.resources.ConnectorPluginsResourceTestConnector();
            Config config = connector.validate(ConnectorPluginsResourceTest.props);
            ConfigDef configDef = connector.config();
            Map<String, ConfigDef.ConfigKey> configKeys = configDef.configKeys();
            List<ConfigValue> configValues = config.configValues();
            Map<String, ConfigDef.ConfigKey> resultConfigKeys = new HashMap<>(configKeys);
            resultConfigKeys.putAll(connectorConfigDef.configKeys());
            configValues.addAll(connectorConfigValues);
            return AbstractHerder.generateResult(.class.getName(), resultConfigKeys, configValues, Collections.singletonList("Test"));
        })));
        PowerMock.replayAll();
        // make a request to connector-plugins resource using just the simple class name.
        ConfigInfos configInfos = connectorPluginsResource.validateConfigs(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getSimpleName(), ConnectorPluginsResourceTest.props);
        Assert.assertEquals(ConnectorPluginsResourceTest.CONFIG_INFOS.name(), configInfos.name());
        Assert.assertEquals(0, configInfos.errorCount());
        Assert.assertEquals(ConnectorPluginsResourceTest.CONFIG_INFOS.groups(), configInfos.groups());
        Assert.assertEquals(new java.util.HashSet(ConnectorPluginsResourceTest.CONFIG_INFOS.values()), new java.util.HashSet(configInfos.values()));
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateConfigWithAlias() throws Throwable {
        herder.validateConnectorConfig(EasyMock.eq(ConnectorPluginsResourceTest.props));
        PowerMock.expectLastCall().andAnswer(((IAnswer<ConfigInfos>) (() -> {
            ConfigDef connectorConfigDef = ConnectorConfig.configDef();
            List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.props);
            Connector connector = new org.apache.kafka.connect.runtime.rest.resources.ConnectorPluginsResourceTestConnector();
            Config config = connector.validate(ConnectorPluginsResourceTest.props);
            ConfigDef configDef = connector.config();
            Map<String, ConfigDef.ConfigKey> configKeys = configDef.configKeys();
            List<ConfigValue> configValues = config.configValues();
            Map<String, ConfigDef.ConfigKey> resultConfigKeys = new HashMap<>(configKeys);
            resultConfigKeys.putAll(connectorConfigDef.configKeys());
            configValues.addAll(connectorConfigValues);
            return AbstractHerder.generateResult(.class.getName(), resultConfigKeys, configValues, Collections.singletonList("Test"));
        })));
        PowerMock.replayAll();
        // make a request to connector-plugins resource using a valid alias.
        ConfigInfos configInfos = connectorPluginsResource.validateConfigs("ConnectorPluginsResourceTest", ConnectorPluginsResourceTest.props);
        Assert.assertEquals(ConnectorPluginsResourceTest.CONFIG_INFOS.name(), configInfos.name());
        Assert.assertEquals(0, configInfos.errorCount());
        Assert.assertEquals(ConnectorPluginsResourceTest.CONFIG_INFOS.groups(), configInfos.groups());
        Assert.assertEquals(new java.util.HashSet(ConnectorPluginsResourceTest.CONFIG_INFOS.values()), new java.util.HashSet(configInfos.values()));
        PowerMock.verifyAll();
    }

    @Test(expected = BadRequestException.class)
    public void testValidateConfigWithNonExistentName() throws Throwable {
        herder.validateConnectorConfig(EasyMock.eq(ConnectorPluginsResourceTest.props));
        PowerMock.expectLastCall().andAnswer(((IAnswer<ConfigInfos>) (() -> {
            ConfigDef connectorConfigDef = ConnectorConfig.configDef();
            List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.props);
            Connector connector = new org.apache.kafka.connect.runtime.rest.resources.ConnectorPluginsResourceTestConnector();
            Config config = connector.validate(ConnectorPluginsResourceTest.props);
            ConfigDef configDef = connector.config();
            Map<String, ConfigDef.ConfigKey> configKeys = configDef.configKeys();
            List<ConfigValue> configValues = config.configValues();
            Map<String, ConfigDef.ConfigKey> resultConfigKeys = new HashMap<>(configKeys);
            resultConfigKeys.putAll(connectorConfigDef.configKeys());
            configValues.addAll(connectorConfigValues);
            return AbstractHerder.generateResult(.class.getName(), resultConfigKeys, configValues, Collections.singletonList("Test"));
        })));
        PowerMock.replayAll();
        // make a request to connector-plugins resource using a non-loaded connector with the same
        // simple name but different package.
        String customClassname = "com.custom.package." + (ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class.getSimpleName());
        connectorPluginsResource.validateConfigs(customClassname, ConnectorPluginsResourceTest.props);
        PowerMock.verifyAll();
    }

    @Test(expected = BadRequestException.class)
    public void testValidateConfigWithNonExistentAlias() throws Throwable {
        herder.validateConnectorConfig(EasyMock.eq(ConnectorPluginsResourceTest.props));
        PowerMock.expectLastCall().andAnswer(((IAnswer<ConfigInfos>) (() -> {
            ConfigDef connectorConfigDef = ConnectorConfig.configDef();
            List<ConfigValue> connectorConfigValues = connectorConfigDef.validate(ConnectorPluginsResourceTest.props);
            Connector connector = new org.apache.kafka.connect.runtime.rest.resources.ConnectorPluginsResourceTestConnector();
            Config config = connector.validate(ConnectorPluginsResourceTest.props);
            ConfigDef configDef = connector.config();
            Map<String, ConfigDef.ConfigKey> configKeys = configDef.configKeys();
            List<ConfigValue> configValues = config.configValues();
            Map<String, ConfigDef.ConfigKey> resultConfigKeys = new HashMap<>(configKeys);
            resultConfigKeys.putAll(connectorConfigDef.configKeys());
            configValues.addAll(connectorConfigValues);
            return AbstractHerder.generateResult(.class.getName(), resultConfigKeys, configValues, Collections.singletonList("Test"));
        })));
        PowerMock.replayAll();
        connectorPluginsResource.validateConfigs("ConnectorPluginsTest", ConnectorPluginsResourceTest.props);
        PowerMock.verifyAll();
    }

    @Test
    public void testListConnectorPlugins() throws Exception {
        expectPlugins();
        Set<ConnectorPluginInfo> connectorPlugins = new java.util.HashSet(connectorPluginsResource.listConnectorPlugins());
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(Connector.class, "0.0")));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(SourceConnector.class, "0.0")));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(SinkConnector.class, "0.0")));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(VerifiableSourceConnector.class)));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(VerifiableSinkConnector.class)));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(MockSourceConnector.class)));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(MockSinkConnector.class)));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(MockConnector.class)));
        Assert.assertFalse(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(SchemaSourceConnector.class)));
        Assert.assertTrue(connectorPlugins.contains(ConnectorPluginsResourceTest.newInfo(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class)));
        PowerMock.verifyAll();
    }

    @Test
    public void testConnectorPluginsIncludesTypeAndVersionInformation() throws Exception {
        expectPlugins();
        ConnectorPluginInfo sinkInfo = ConnectorPluginsResourceTest.newInfo(TestSinkConnector.class);
        ConnectorPluginInfo sourceInfo = ConnectorPluginsResourceTest.newInfo(TestSourceConnector.class);
        ConnectorPluginInfo unknownInfo = ConnectorPluginsResourceTest.newInfo(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.class);
        Assert.assertEquals(SINK, sinkInfo.type());
        Assert.assertEquals(SOURCE, sourceInfo.type());
        Assert.assertEquals(UNKNOWN, unknownInfo.type());
        Assert.assertEquals(TestSinkConnector.VERSION, sinkInfo.version());
        Assert.assertEquals(TestSourceConnector.VERSION, sourceInfo.version());
        final ObjectMapper objectMapper = new ObjectMapper();
        String serializedSink = objectMapper.writeValueAsString(SINK);
        String serializedSource = objectMapper.writeValueAsString(SOURCE);
        String serializedUnknown = objectMapper.writeValueAsString(UNKNOWN);
        Assert.assertTrue(serializedSink.contains("sink"));
        Assert.assertTrue(serializedSource.contains("source"));
        Assert.assertTrue(serializedUnknown.contains("unknown"));
        Assert.assertEquals(SINK, objectMapper.readValue(serializedSink, ConnectorType.class));
        Assert.assertEquals(SOURCE, objectMapper.readValue(serializedSource, ConnectorType.class));
        Assert.assertEquals(UNKNOWN, objectMapper.readValue(serializedUnknown, ConnectorType.class));
    }

    public static class MockPluginClassLoader extends PluginClassLoader {
        public MockPluginClassLoader(URL pluginLocation, URL[] urls, ClassLoader parent) {
            super(pluginLocation, urls, parent);
        }

        public MockPluginClassLoader(URL pluginLocation, URL[] urls) {
            super(pluginLocation, urls);
        }

        @Override
        public String location() {
            return "/tmp/mockpath";
        }
    }

    public static class MockConnectorPluginDesc extends PluginDesc<Connector> {
        public MockConnectorPluginDesc(Class<? extends Connector> klass, String version) throws Exception {
            super(klass, version, new ConnectorPluginsResourceTest.MockPluginClassLoader(null, new URL[0]));
        }

        public MockConnectorPluginDesc(Class<? extends Connector> klass) throws Exception {
            super(klass, version(), new ConnectorPluginsResourceTest.MockPluginClassLoader(null, new URL[0]));
        }
    }

    /* Name here needs to be unique as we are testing the aliasing mechanism */
    public static class ConnectorPluginsResourceTestConnector extends Connector {
        private static final String TEST_STRING_CONFIG = "test.string.config";

        private static final String TEST_INT_CONFIG = "test.int.config";

        private static final String TEST_STRING_CONFIG_DEFAULT = "test.string.config.default";

        private static final String TEST_LIST_CONFIG = "test.list.config";

        private static final String GROUP = "Test";

        private static final ConfigDef CONFIG_DEF = new ConfigDef().define(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_STRING_CONFIG, STRING, HIGH, "Test configuration for string type.").define(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_INT_CONFIG, INT, MEDIUM, "Test configuration for integer type.", ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.GROUP, 1, Width.MEDIUM, ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_INT_CONFIG, new ConnectorPluginsResourceTest.IntegerRecommender()).define(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_STRING_CONFIG_DEFAULT, STRING, "", LOW, "Test configuration with default value.").define(ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_LIST_CONFIG, LIST, HIGH, "Test configuration for list type.", ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.GROUP, 2, LONG, ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.TEST_LIST_CONFIG, new ConnectorPluginsResourceTest.ListRecommender());

        @Override
        public String version() {
            return "1.0";
        }

        @Override
        public void start(Map<String, String> props) {
        }

        @Override
        public Class<? extends Task> taskClass() {
            return null;
        }

        @Override
        public List<Map<String, String>> taskConfigs(int maxTasks) {
            return null;
        }

        @Override
        public void stop() {
        }

        @Override
        public ConfigDef config() {
            return ConnectorPluginsResourceTest.ConnectorPluginsResourceTestConnector.CONFIG_DEF;
        }
    }

    private static class IntegerRecommender implements Recommender {
        @Override
        public List<Object> validValues(String name, Map<String, Object> parsedConfig) {
            return Arrays.asList(1, 2, 3);
        }

        @Override
        public boolean visible(String name, Map<String, Object> parsedConfig) {
            return true;
        }
    }

    private static class ListRecommender implements Recommender {
        @Override
        public List<Object> validValues(String name, Map<String, Object> parsedConfig) {
            return Arrays.asList("a", "b", "c");
        }

        @Override
        public boolean visible(String name, Map<String, Object> parsedConfig) {
            return true;
        }
    }
}

