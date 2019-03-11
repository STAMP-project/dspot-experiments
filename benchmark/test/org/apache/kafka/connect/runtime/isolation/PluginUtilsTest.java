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


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class PluginUtilsTest {
    @Rule
    public TemporaryFolder rootDir = new TemporaryFolder();

    private Path pluginPath;

    @Test
    public void testJavaLibraryClasses() {
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("java."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("java.lang.Object"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("java.lang.String"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("java.util.HashMap$Entry"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("java.io.Serializable"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("javax.rmi."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("javax.management.loading.ClassLoaderRepository"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.omg.CORBA."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.omg.CORBA.Object"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.w3c.dom."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.w3c.dom.traversal.TreeWalker"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.xml.sax."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.xml.sax.EntityResolver"));
    }

    @Test
    public void testThirdPartyClasses() {
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.slf4j."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.slf4j.LoggerFactory"));
    }

    @Test
    public void testConnectFrameworkClasses() {
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.config.AbstractConfig"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.config.ConfigDef$Type"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.serialization.Deserializer"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect."));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.connector.Connector"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.source.SourceConnector"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.sink.SinkConnector"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.connector.Task"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.source.SourceTask"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.sink.SinkTask"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.transforms.Transformation"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.storage.Converter"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.storage.OffsetBackingStore"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.clients.producer.ProducerConfig"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.clients.consumer.ConsumerConfig"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.clients.admin.KafkaAdminClient"));
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.rest.ConnectRestExtension"));
    }

    @Test
    public void testAllowedConnectFrameworkClasses() {
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.transforms."));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.transforms.ExtractField"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.transforms.ExtractField$Key"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.json."));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.json.JsonConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.json.JsonConverter$21"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.file."));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.file.FileStreamSourceTask"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.file.FileStreamSinkConnector"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters."));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.ByteArrayConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.DoubleConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.FloatConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.IntegerConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.LongConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.converters.ShortConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.storage.StringConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.storage.SimpleHeaderConverter"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.connect.rest.basic.auth.extension.BasicAuthSecurityRestExtension"));
    }

    @Test
    public void testClientConfigProvider() {
        Assert.assertFalse(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.config.provider.ConfigProvider"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.config.provider.FileConfigProvider"));
        Assert.assertTrue(PluginUtils.shouldLoadInIsolation("org.apache.kafka.common.config.provider.FutureConfigProvider"));
    }

    @Test
    public void testEmptyPluginUrls() throws Exception {
        Assert.assertEquals(Collections.<Path>emptyList(), PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testEmptyStructurePluginUrls() throws Exception {
        createBasicDirectoryLayout();
        Assert.assertEquals(Collections.<Path>emptyList(), PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testPluginUrlsWithJars() throws Exception {
        createBasicDirectoryLayout();
        List<Path> expectedUrls = createBasicExpectedUrls();
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testOrderOfPluginUrlsWithJars() throws Exception {
        createBasicDirectoryLayout();
        // Here this method is just used to create the files. The result is not used.
        createBasicExpectedUrls();
        List<Path> actual = PluginUtils.pluginUrls(pluginPath);
        // 'simple-transform.jar' is created first. In many cases, without sorting within the
        // PluginUtils, this jar will be placed before 'another-transform.jar'. However this is
        // not guaranteed because a DirectoryStream does not maintain a certain order in its
        // results. Besides this test case, sorted order in every call to assertUrls below.
        int i = Arrays.toString(actual.toArray()).indexOf("another-transform.jar");
        int j = Arrays.toString(actual.toArray()).indexOf("simple-transform.jar");
        Assert.assertTrue((i < j));
    }

    @Test
    public void testPluginUrlsWithZips() throws Exception {
        createBasicDirectoryLayout();
        List<Path> expectedUrls = new ArrayList<>();
        expectedUrls.add(Files.createFile(pluginPath.resolve("connectorA/my-sink.zip")));
        expectedUrls.add(Files.createFile(pluginPath.resolve("connectorB/a-source.zip")));
        expectedUrls.add(Files.createFile(pluginPath.resolve("transformC/simple-transform.zip")));
        expectedUrls.add(Files.createFile(pluginPath.resolve("transformC/deps/another-transform.zip")));
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testPluginUrlsWithClasses() throws Exception {
        Files.createDirectories(pluginPath.resolve("org/apache/kafka/converters"));
        Files.createDirectories(pluginPath.resolve("com/mycompany/transforms"));
        Files.createDirectories(pluginPath.resolve("edu/research/connectors"));
        Files.createFile(pluginPath.resolve("org/apache/kafka/converters/README.txt"));
        Files.createFile(pluginPath.resolve("org/apache/kafka/converters/AlienFormat.class"));
        Files.createDirectories(pluginPath.resolve("com/mycompany/transforms/Blackhole.class"));
        Files.createDirectories(pluginPath.resolve("edu/research/connectors/HalSink.class"));
        List<Path> expectedUrls = new ArrayList<>();
        expectedUrls.add(pluginPath);
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testPluginUrlsWithAbsoluteSymlink() throws Exception {
        createBasicDirectoryLayout();
        Path anotherPath = rootDir.newFolder("moreplugins").toPath().toRealPath();
        Files.createDirectories(anotherPath.resolve("connectorB-deps"));
        Files.createSymbolicLink(pluginPath.resolve("connectorB/deps/symlink"), anotherPath.resolve("connectorB-deps"));
        List<Path> expectedUrls = createBasicExpectedUrls();
        expectedUrls.add(Files.createFile(anotherPath.resolve("connectorB-deps/converter.jar")));
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testPluginUrlsWithRelativeSymlinkBackwards() throws Exception {
        createBasicDirectoryLayout();
        Path anotherPath = rootDir.newFolder("moreplugins").toPath().toRealPath();
        Files.createDirectories(anotherPath.resolve("connectorB-deps"));
        Files.createSymbolicLink(pluginPath.resolve("connectorB/deps/symlink"), Paths.get("../../../moreplugins/connectorB-deps"));
        List<Path> expectedUrls = createBasicExpectedUrls();
        expectedUrls.add(Files.createFile(anotherPath.resolve("connectorB-deps/converter.jar")));
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }

    @Test
    public void testPluginUrlsWithRelativeSymlinkForwards() throws Exception {
        // Since this test case defines a relative symlink within an already included path, the main
        // assertion of this test is absence of exceptions and correct resolution of paths.
        createBasicDirectoryLayout();
        Files.createDirectories(pluginPath.resolve("connectorB/deps/more"));
        Files.createSymbolicLink(pluginPath.resolve("connectorB/deps/symlink"), Paths.get("more"));
        List<Path> expectedUrls = createBasicExpectedUrls();
        expectedUrls.add(Files.createFile(pluginPath.resolve("connectorB/deps/more/converter.jar")));
        assertUrls(expectedUrls, PluginUtils.pluginUrls(pluginPath));
    }
}

