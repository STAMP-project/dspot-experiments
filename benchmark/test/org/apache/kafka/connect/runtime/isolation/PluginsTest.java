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
package org.apache.kafka.connect.runtime.isolation;


import ClassLoaderUsage.CURRENT_CLASSLOADER;
import ClassLoaderUsage.PLUGINS;
import ConverterType.HEADER;
import JsonConverterConfig.SCHEMAS_ENABLE_CONFIG;
import WorkerConfig.HEADER_CONVERTER_CLASS_CONFIG;
import WorkerConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG;
import WorkerConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG;
import WorkerConfig.KEY_CONVERTER_CLASS_CONFIG;
import WorkerConfig.REST_EXTENSION_CLASSES_CONFIG;
import WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.rest.ConnectRestExtension;
import org.apache.kafka.connect.rest.ConnectRestExtensionContext;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.storage.HeaderConverter;
import org.apache.kafka.connect.storage.SimpleHeaderConverter;
import org.junit.Assert;
import org.junit.Test;


public class PluginsTest {
    private static Map<String, String> pluginProps;

    private static Plugins plugins;

    private Map<String, String> props;

    private AbstractConfig config;

    private PluginsTest.TestConverter converter;

    private PluginsTest.TestHeaderConverter headerConverter;

    private PluginsTest.TestInternalConverter internalConverter;

    @Test
    public void shouldInstantiateAndConfigureConverters() {
        instantiateAndConfigureConverter(KEY_CONVERTER_CLASS_CONFIG, CURRENT_CLASSLOADER);
        // Validate extra configs got passed through to overridden converters
        Assert.assertEquals("true", converter.configs.get(SCHEMAS_ENABLE_CONFIG));
        Assert.assertEquals("foo1", converter.configs.get("extra.config"));
        instantiateAndConfigureConverter(VALUE_CONVERTER_CLASS_CONFIG, PLUGINS);
        // Validate extra configs got passed through to overridden converters
        Assert.assertEquals("true", converter.configs.get(SCHEMAS_ENABLE_CONFIG));
        Assert.assertEquals("foo2", converter.configs.get("extra.config"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldInstantiateAndConfigureInternalConverters() {
        instantiateAndConfigureInternalConverter(INTERNAL_KEY_CONVERTER_CLASS_CONFIG, CURRENT_CLASSLOADER);
        // Validate schemas.enable is defaulted to false for internal converter
        Assert.assertEquals(false, internalConverter.configs.get(SCHEMAS_ENABLE_CONFIG));
        // Validate internal converter properties can still be set
        Assert.assertEquals("bar1", internalConverter.configs.get("extra.config"));
        instantiateAndConfigureInternalConverter(INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, PLUGINS);
        // Validate schemas.enable is defaulted to false for internal converter
        Assert.assertEquals(false, internalConverter.configs.get(SCHEMAS_ENABLE_CONFIG));
        // Validate internal converter properties can still be set
        Assert.assertEquals("bar2", internalConverter.configs.get("extra.config"));
    }

    @Test
    public void shouldInstantiateAndConfigureExplicitlySetHeaderConverterWithCurrentClassLoader() {
        Assert.assertNotNull(props.get(HEADER_CONVERTER_CLASS_CONFIG));
        HeaderConverter headerConverter = PluginsTest.plugins.newHeaderConverter(config, HEADER_CONVERTER_CLASS_CONFIG, CURRENT_CLASSLOADER);
        Assert.assertNotNull(headerConverter);
        Assert.assertTrue((headerConverter instanceof PluginsTest.TestHeaderConverter));
        this.headerConverter = ((PluginsTest.TestHeaderConverter) (headerConverter));
        // Validate extra configs got passed through to overridden converters
        assertConverterType(HEADER, this.headerConverter.configs);
        Assert.assertEquals("baz", this.headerConverter.configs.get("extra.config"));
        headerConverter = PluginsTest.plugins.newHeaderConverter(config, HEADER_CONVERTER_CLASS_CONFIG, PLUGINS);
        Assert.assertNotNull(headerConverter);
        Assert.assertTrue((headerConverter instanceof PluginsTest.TestHeaderConverter));
        this.headerConverter = ((PluginsTest.TestHeaderConverter) (headerConverter));
        // Validate extra configs got passed through to overridden converters
        assertConverterType(HEADER, this.headerConverter.configs);
        Assert.assertEquals("baz", this.headerConverter.configs.get("extra.config"));
    }

    @Test
    public void shouldInstantiateAndConfigureConnectRestExtension() {
        props.put(REST_EXTENSION_CLASSES_CONFIG, PluginsTest.TestConnectRestExtension.class.getName());
        createConfig();
        List<ConnectRestExtension> connectRestExtensions = PluginsTest.plugins.newPlugins(config.getList(REST_EXTENSION_CLASSES_CONFIG), config, ConnectRestExtension.class);
        Assert.assertNotNull(connectRestExtensions);
        Assert.assertEquals("One Rest Extension expected", 1, connectRestExtensions.size());
        Assert.assertNotNull(connectRestExtensions.get(0));
        Assert.assertTrue("Should be instance of TestConnectRestExtension", ((connectRestExtensions.get(0)) instanceof PluginsTest.TestConnectRestExtension));
        Assert.assertNotNull(((PluginsTest.TestConnectRestExtension) (connectRestExtensions.get(0))).configs);
        Assert.assertEquals(config.originals(), ((PluginsTest.TestConnectRestExtension) (connectRestExtensions.get(0))).configs);
    }

    @Test
    public void shouldInstantiateAndConfigureDefaultHeaderConverter() {
        props.remove(HEADER_CONVERTER_CLASS_CONFIG);
        createConfig();
        // Because it's not explicitly set on the supplied configuration, the logic to use the current classloader for the connector
        // will exit immediately, and so this method always returns null
        HeaderConverter headerConverter = PluginsTest.plugins.newHeaderConverter(config, HEADER_CONVERTER_CLASS_CONFIG, CURRENT_CLASSLOADER);
        Assert.assertNull(headerConverter);
        // But we should always find it (or the worker's default) when using the plugins classloader ...
        headerConverter = PluginsTest.plugins.newHeaderConverter(config, HEADER_CONVERTER_CLASS_CONFIG, PLUGINS);
        Assert.assertNotNull(headerConverter);
        Assert.assertTrue((headerConverter instanceof SimpleHeaderConverter));
    }

    public static class TestableWorkerConfig extends WorkerConfig {
        public TestableWorkerConfig(Map<String, String> props) {
            super(WorkerConfig.baseConfigDef(), props);
        }
    }

    public static class TestConverter implements Configurable , Converter {
        public Map<String, ?> configs;

        public ConfigDef config() {
            return JsonConverterConfig.configDef();
        }

        @Override
        public void configure(Map<String, ?> configs) {
            this.configs = configs;
            new JsonConverterConfig(configs);// requires the `converter.type` config be set

        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            this.configs = configs;
        }

        @Override
        public byte[] fromConnectData(String topic, Schema schema, Object value) {
            return new byte[0];
        }

        @Override
        public SchemaAndValue toConnectData(String topic, byte[] value) {
            return null;
        }
    }

    public static class TestHeaderConverter implements HeaderConverter {
        public Map<String, ?> configs;

        @Override
        public ConfigDef config() {
            return JsonConverterConfig.configDef();
        }

        @Override
        public void configure(Map<String, ?> configs) {
            this.configs = configs;
            new JsonConverterConfig(configs);// requires the `converter.type` config be set

        }

        @Override
        public byte[] fromConnectHeader(String topic, String headerKey, Schema schema, Object value) {
            return new byte[0];
        }

        @Override
        public SchemaAndValue toConnectHeader(String topic, String headerKey, byte[] value) {
            return null;
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static class TestConnectRestExtension implements ConnectRestExtension {
        public Map<String, ?> configs;

        @Override
        public void register(ConnectRestExtensionContext restPluginContext) {
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void configure(Map<String, ?> configs) {
            this.configs = configs;
        }

        @Override
        public String version() {
            return "test";
        }
    }

    public static class TestInternalConverter extends JsonConverter {
        public Map<String, ?> configs;

        public void configure(Map<String, ?> configs) {
            this.configs = configs;
            super.configure(configs);
        }
    }
}

