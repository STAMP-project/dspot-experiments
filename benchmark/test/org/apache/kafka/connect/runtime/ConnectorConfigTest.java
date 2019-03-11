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
package org.apache.kafka.connect.runtime;


import ConfigDef.Importance.HIGH;
import ConfigDef.NO_DEFAULT_VALUE;
import ConfigDef.Range;
import ConfigDef.Type.INT;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.Assert;
import org.junit.Test;


public class ConnectorConfigTest<R extends ConnectRecord<R>> {
    private static final Plugins MOCK_PLUGINS = new Plugins(new HashMap<String, String>()) {
        @Override
        public Set<PluginDesc<Transformation>> transformations() {
            return Collections.emptySet();
        }
    };

    public abstract static class TestConnector extends Connector {}

    public static class SimpleTransformation<R extends ConnectRecord<R>> implements Transformation<R> {
        int magicNumber = 0;

        @Override
        public void configure(Map<String, ?> props) {
            magicNumber = Integer.parseInt(((String) (props.get("magic.number"))));
        }

        @Override
        public R apply(R record) {
            return null;
        }

        @Override
        public void close() {
            magicNumber = 0;
        }

        @Override
        public ConfigDef config() {
            return new ConfigDef().define("magic.number", INT, NO_DEFAULT_VALUE, Range.atLeast(42), HIGH, "");
        }
    }

    @Test
    public void noTransforms() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test(expected = ConfigException.class)
    public void danglingTransformAlias() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "dangler");
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test(expected = ConfigException.class)
    public void emptyConnectorName() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test(expected = ConfigException.class)
    public void wrongTransformationType() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a");
        props.put("transforms.a.type", "uninstantiable");
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test(expected = ConfigException.class)
    public void unconfiguredTransform() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a");
        props.put("transforms.a.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test
    public void misconfiguredTransform() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a");
        props.put("transforms.a.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        props.put("transforms.a.magic.number", "40");
        try {
            new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
            Assert.fail();
        } catch (ConfigException e) {
            Assert.assertTrue(e.getMessage().contains("Value must be at least 42"));
        }
    }

    @Test
    public void singleTransform() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a");
        props.put("transforms.a.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        props.put("transforms.a.magic.number", "42");
        final ConnectorConfig config = new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
        final List<Transformation<R>> transformations = config.transformations();
        Assert.assertEquals(1, transformations.size());
        final ConnectorConfigTest.SimpleTransformation xform = ((ConnectorConfigTest.SimpleTransformation) (transformations.get(0)));
        Assert.assertEquals(42, xform.magicNumber);
    }

    @Test(expected = ConfigException.class)
    public void multipleTransformsOneDangling() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a, b");
        props.put("transforms.a.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        props.put("transforms.a.magic.number", "42");
        new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
    }

    @Test
    public void multipleTransforms() {
        Map<String, String> props = new HashMap<>();
        props.put("name", "test");
        props.put("connector.class", ConnectorConfigTest.TestConnector.class.getName());
        props.put("transforms", "a, b");
        props.put("transforms.a.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        props.put("transforms.a.magic.number", "42");
        props.put("transforms.b.type", ConnectorConfigTest.SimpleTransformation.class.getName());
        props.put("transforms.b.magic.number", "84");
        final ConnectorConfig config = new ConnectorConfig(ConnectorConfigTest.MOCK_PLUGINS, props);
        final List<Transformation<R>> transformations = config.transformations();
        Assert.assertEquals(2, transformations.size());
        Assert.assertEquals(42, ((ConnectorConfigTest.SimpleTransformation) (transformations.get(0))).magicNumber);
        Assert.assertEquals(84, ((ConnectorConfigTest.SimpleTransformation) (transformations.get(1))).magicNumber);
    }
}

