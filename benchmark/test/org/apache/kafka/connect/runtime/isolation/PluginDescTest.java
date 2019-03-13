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


import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.Assert;
import org.junit.Test;


public class PluginDescTest {
    private final ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

    private final String regularVersion = "1.0.0";

    private final String newerVersion = "1.0.1";

    private final String snaphotVersion = "1.0.0-SNAPSHOT";

    private final String noVersion = "undefined";

    private PluginClassLoader pluginLoader;

    @Test
    public void testRegularPluginDesc() {
        PluginDesc<Connector> connectorDesc = new PluginDesc(Connector.class, regularVersion, pluginLoader);
        PluginDescTest.assertPluginDesc(connectorDesc, Connector.class, regularVersion, pluginLoader.location());
        PluginDesc<Converter> converterDesc = new PluginDesc(Converter.class, snaphotVersion, pluginLoader);
        PluginDescTest.assertPluginDesc(converterDesc, Converter.class, snaphotVersion, pluginLoader.location());
        PluginDesc<Transformation> transformDesc = new PluginDesc(Transformation.class, noVersion, pluginLoader);
        PluginDescTest.assertPluginDesc(transformDesc, Transformation.class, noVersion, pluginLoader.location());
    }

    @Test
    public void testPluginDescWithSystemClassLoader() {
        String location = "classpath";
        PluginDesc<SinkConnector> connectorDesc = new PluginDesc(SinkConnector.class, regularVersion, systemLoader);
        PluginDescTest.assertPluginDesc(connectorDesc, SinkConnector.class, regularVersion, location);
        PluginDesc<Converter> converterDesc = new PluginDesc(Converter.class, snaphotVersion, systemLoader);
        PluginDescTest.assertPluginDesc(converterDesc, Converter.class, snaphotVersion, location);
        PluginDesc<Transformation> transformDesc = new PluginDesc(Transformation.class, noVersion, systemLoader);
        PluginDescTest.assertPluginDesc(transformDesc, Transformation.class, noVersion, location);
    }

    @Test
    public void testPluginDescWithNullVersion() {
        String nullVersion = "null";
        PluginDesc<SourceConnector> connectorDesc = new PluginDesc(SourceConnector.class, null, pluginLoader);
        PluginDescTest.assertPluginDesc(connectorDesc, SourceConnector.class, nullVersion, pluginLoader.location());
        String location = "classpath";
        PluginDesc<Converter> converterDesc = new PluginDesc(Converter.class, null, systemLoader);
        PluginDescTest.assertPluginDesc(converterDesc, Converter.class, nullVersion, location);
    }

    @Test
    public void testPluginDescEquality() {
        PluginDesc<Connector> connectorDescPluginPath = new PluginDesc(Connector.class, snaphotVersion, pluginLoader);
        PluginDesc<Connector> connectorDescClasspath = new PluginDesc(Connector.class, snaphotVersion, systemLoader);
        Assert.assertEquals(connectorDescPluginPath, connectorDescClasspath);
        Assert.assertEquals(connectorDescPluginPath.hashCode(), connectorDescClasspath.hashCode());
        PluginDesc<Converter> converterDescPluginPath = new PluginDesc(Converter.class, noVersion, pluginLoader);
        PluginDesc<Converter> converterDescClasspath = new PluginDesc(Converter.class, noVersion, systemLoader);
        Assert.assertEquals(converterDescPluginPath, converterDescClasspath);
        Assert.assertEquals(converterDescPluginPath.hashCode(), converterDescClasspath.hashCode());
        PluginDesc<Transformation> transformDescPluginPath = new PluginDesc(Transformation.class, null, pluginLoader);
        PluginDesc<Transformation> transformDescClasspath = new PluginDesc(Transformation.class, noVersion, pluginLoader);
        Assert.assertNotEquals(transformDescPluginPath, transformDescClasspath);
    }

    @Test
    public void testPluginDescComparison() {
        PluginDesc<Connector> connectorDescPluginPath = new PluginDesc(Connector.class, regularVersion, pluginLoader);
        PluginDesc<Connector> connectorDescClasspath = new PluginDesc(Connector.class, newerVersion, systemLoader);
        PluginDescTest.assertNewer(connectorDescPluginPath, connectorDescClasspath);
        PluginDesc<Converter> converterDescPluginPath = new PluginDesc(Converter.class, noVersion, pluginLoader);
        PluginDesc<Converter> converterDescClasspath = new PluginDesc(Converter.class, snaphotVersion, systemLoader);
        PluginDescTest.assertNewer(converterDescPluginPath, converterDescClasspath);
        PluginDesc<Transformation> transformDescPluginPath = new PluginDesc(Transformation.class, null, pluginLoader);
        PluginDesc<Transformation> transformDescClasspath = new PluginDesc(Transformation.class, regularVersion, systemLoader);
        PluginDescTest.assertNewer(transformDescPluginPath, transformDescClasspath);
    }
}

