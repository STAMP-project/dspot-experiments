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
package com.hazelcast.config;


import com.hazelcast.config.replacer.EncryptionReplacer;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class XmlConfigImportVariableReplacementTest extends AbstractConfigImportVariableReplacementTest {
    @Test
    public void testImportElementOnlyAppearsInTopLevel() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <network>") + "        <import resource=\"\"/>\n") + "   </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
    }

    @Override
    @Test
    public void testHazelcastElementOnlyAppearsOnce() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <hazelcast>") + "   </hazelcast>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
    }

    @Override
    @Test
    public void readVariables() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <semaphore name=\"${name}\">\n") + "        <initial-permits>${initial.permits}</initial-permits>\n") + "        <backup-count>${backupcount.part1}${backupcount.part2}</backup-count>\n") + "    </semaphore>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Properties properties = new Properties();
        properties.setProperty("name", "s");
        properties.setProperty("initial.permits", "25");
        properties.setProperty("backupcount.part1", "0");
        properties.setProperty("backupcount.part2", "6");
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, properties);
        SemaphoreConfig semaphoreConfig = config.getSemaphoreConfig("s");
        Assert.assertEquals(25, semaphoreConfig.getInitialPermits());
        Assert.assertEquals(6, semaphoreConfig.getBackupCount());
        Assert.assertEquals(0, semaphoreConfig.getAsyncBackupCount());
    }

    @Override
    @Test
    public void testImportConfigFromResourceVariables() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"true\"/>\n") + "        </join>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"${config.location}\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, "config.location", file.getAbsolutePath());
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testImportedConfigVariableReplacement() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"${tcp.ip.enabled}\"/>\n") + "        </join>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"${config.location}\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Properties properties = new Properties();
        properties.setProperty("config.location", file.getAbsolutePath());
        properties.setProperty("tcp.ip.enabled", "true");
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, properties);
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testTwoResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "xml");
        File config2 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz2", "xml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        String config1Xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (config2.getAbsolutePath())) + "\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        String config2Xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (config1.getAbsolutePath())) + "\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Xml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Xml);
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(config1Xml, null);
    }

    @Override
    @Test
    public void testThreeResourceCyclicImportThrowsException() throws Exception {
        String template = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///%s\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "xml");
        File config2 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz2", "xml");
        File config3 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz3", "xml");
        String config1Xml = String.format(template, config2.getAbsolutePath());
        String config2Xml = String.format(template, config3.getAbsolutePath());
        String config3Xml = String.format(template, config1.getAbsolutePath());
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config1), config1Xml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config2), config2Xml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config3), config3Xml);
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(config1Xml, null);
    }

    @Override
    @Test
    public void testImportEmptyResourceContent() throws Exception {
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "xml");
        FileOutputStream os1 = new FileOutputStream(config1);
        String config1Xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource='file:///") + (config1.getAbsolutePath())) + "\'/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, "");
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(config1Xml, null);
    }

    @Override
    @Test
    public void testImportEmptyResourceThrowsException() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expectInvalid();
        XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
    }

    @Override
    @Test
    public void testImportNotExistingResourceThrowsException() {
        expectInvalid();
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"notexisting.xml\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void testImportFromNonHazelcastConfigThrowsException() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("mymap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = ((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <map name=\"mymap\">\n") + "       <backup-count>6</backup-count>") + "       <time-to-live-seconds>10</time-to-live-seconds>") + "       <map-store enabled=\"true\" initial-mode=\"LAZY\">\n") + "            <class-name>com.hazelcast.examples.MyMapStore</class-name>\n") + "            <write-delay-seconds>10</write-delay-seconds>\n") + "            <write-batch-size>100</write-batch-size>\n") + "        </map-store>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String xml = ((("" + ("<non-hazelcast>\n" + "  <import resource=\"file:///")) + (file.getAbsolutePath())) + "\"/>\n") + "</non-hazelcast>";
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        Assert.assertNull(config.getMapConfig("mymap"));
    }

    @Override
    @Test
    public void testImportNetworkConfigFromFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"true\"/>\n") + "        </join>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (file.getAbsolutePath())) + "\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testImportMapConfigFromFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("mymap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = ((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <map name=\"mymap\">\n") + "       <backup-count>6</backup-count>") + "       <time-to-live-seconds>10</time-to-live-seconds>") + "       <map-store enabled=\"true\" initial-mode=\"LAZY\">\n") + "            <class-name>com.hazelcast.examples.MyMapStore</class-name>\n") + "            <write-delay-seconds>10</write-delay-seconds>\n") + "            <write-batch-size>100</write-batch-size>\n") + "        </map-store>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (file.getAbsolutePath())) + "\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        MapConfig myMapConfig = config.getMapConfig("mymap");
        Assert.assertEquals("mymap", myMapConfig.getName());
        Assert.assertEquals(6, myMapConfig.getBackupCount());
        Assert.assertEquals(10, myMapConfig.getTimeToLiveSeconds());
        MapStoreConfig myMapStoreConfig = myMapConfig.getMapStoreConfig();
        Assert.assertEquals(10, myMapStoreConfig.getWriteDelaySeconds());
        Assert.assertEquals(100, myMapStoreConfig.getWriteBatchSize());
        Assert.assertEquals("com.hazelcast.examples.MyMapStore", myMapStoreConfig.getClassName());
    }

    @Override
    @Test
    public void testImportOverlappingMapConfigFromFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("mymap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <map name=\"mymap\">\n") + "       <backup-count>6</backup-count>") + "       <map-store enabled=\"true\" initial-mode=\"LAZY\">\n") + "            <class-name>com.hazelcast.examples.MyMapStore</class-name>\n") + "            <write-delay-seconds>10</write-delay-seconds>\n") + "            <write-batch-size>100</write-batch-size>\n") + "        </map-store>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (file.getAbsolutePath())) + "\"/>\n") + "    <map name=\"mymap\">\n") + "       <time-to-live-seconds>10</time-to-live-seconds>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        MapConfig myMapConfig = config.getMapConfig("mymap");
        Assert.assertEquals("mymap", myMapConfig.getName());
        Assert.assertEquals(10, myMapConfig.getTimeToLiveSeconds());
        // these are the defaults here not overridden with the content of
        // the imported document
        // this is a difference between importing overlapping XML and YAML
        // configuration
        // YAML recursively merges the two files
        Assert.assertEquals(1, myMapConfig.getBackupCount());
        MapStoreConfig myMapStoreConfig = myMapConfig.getMapStoreConfig();
        Assert.assertEquals(0, myMapStoreConfig.getWriteDelaySeconds());
        Assert.assertEquals(1, myMapStoreConfig.getWriteBatchSize());
        Assert.assertNull(myMapStoreConfig.getClassName());
    }

    @Override
    @Test
    public void testMapConfigFromMainAndImportedFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("importmap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = ((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <map name=\"importedMap\">\n") + "       <backup-count>6</backup-count>") + "       <time-to-live-seconds>10</time-to-live-seconds>") + "       <map-store enabled=\"true\" initial-mode=\"LAZY\">\n") + "            <class-name>com.hazelcast.examples.MyMapStore</class-name>\n") + "            <write-delay-seconds>10</write-delay-seconds>\n") + "            <write-batch-size>100</write-batch-size>\n") + "        </map-store>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + (file.getAbsolutePath())) + "\"/>\n") + "    <map name=\"mapInMain\">\n") + "       <backup-count>2</backup-count>") + "       <time-to-live-seconds>5</time-to-live-seconds>") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        MapConfig mapInMainMapConfig = config.getMapConfig("mapInMain");
        Assert.assertEquals("mapInMain", mapInMainMapConfig.getName());
        Assert.assertEquals(5, mapInMainMapConfig.getTimeToLiveSeconds());
        Assert.assertEquals(2, mapInMainMapConfig.getBackupCount());
        MapConfig importedMap = config.getMapConfig("importedMap");
        Assert.assertEquals("importedMap", importedMap.getName());
        Assert.assertEquals(10, importedMap.getTimeToLiveSeconds());
        Assert.assertEquals(6, importedMap.getBackupCount());
        MapStoreConfig myMapStoreConfig = importedMap.getMapStoreConfig();
        Assert.assertEquals(10, myMapStoreConfig.getWriteDelaySeconds());
        Assert.assertEquals(100, myMapStoreConfig.getWriteBatchSize());
        Assert.assertEquals("com.hazelcast.examples.MyMapStore", myMapStoreConfig.getClassName());
    }

    @Override
    @Test
    public void testImportGroupConfigFromClassPath() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"classpath:test-hazelcast.xml\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, null);
        GroupConfig groupConfig = config.getGroupConfig();
        Assert.assertEquals("foobar", groupConfig.getName());
        Assert.assertEquals("dev-pass", groupConfig.getPassword());
    }

    @Override
    @Test
    public void testReplacers() throws Exception {
        File passwordFile = tempFolder.newFile(((getClass().getSimpleName()) + ".pwd"));
        PrintWriter out = new PrintWriter(passwordFile);
        try {
            out.print("This is a password");
        } finally {
            IOUtil.closeResource(out);
        }
        String xml = (((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <config-replacers>\n") + "        <replacer class-name='") + (EncryptionReplacer.class.getName())) + "\'>\n") + "            <properties>\n") + "                <property name='passwordFile'>") + (passwordFile.getAbsolutePath())) + "</property>\n") + "                <property name=\'passwordUserProperties\'>false</property>\n") + "                <property name=\'keyLengthBits\'>64</property>\n") + "                <property name=\'saltLengthBytes\'>8</property>\n") + "                <property name=\'cipherAlgorithm\'>DES</property>\n") + "                <property name=\'secretKeyFactoryAlgorithm\'>PBKDF2WithHmacSHA1</property>\n") + "                <property name=\'secretKeyAlgorithm\'>DES</property>\n") + "            </properties>\n") + "        </replacer>\n") + "        <replacer class-name='") + (AbstractConfigImportVariableReplacementTest.IdentityReplacer.class.getName())) + "\'/>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>${java.version} $ID{dev}</name>\n") + "        <password>$ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}</password>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        GroupConfig groupConfig = XmlConfigImportVariableReplacementTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals(((System.getProperty("java.version")) + " dev"), groupConfig.getName());
        Assert.assertEquals("My very secret secret", groupConfig.getPassword());
    }

    @Override
    @Test(expected = ConfigurationException.class)
    public void testMissingReplacement() throws Exception {
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <config-replacers>\n") + "        <replacer class-name='") + (EncryptionReplacer.class.getName())) + "\'/>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>$ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}</name>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        XmlConfigImportVariableReplacementTest.buildConfig(xml, System.getProperties());
    }

    @Override
    @Test
    public void testBadVariableSyntaxIsIgnored() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <group>\n") + "        <name>${noSuchPropertyAvailable]</name>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        GroupConfig groupConfig = XmlConfigImportVariableReplacementTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable]", groupConfig.getName());
    }

    @Override
    @Test
    public void testReplacerProperties() throws Exception {
        String xml = ((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <config-replacers fail-if-value-missing=\'false\'>\n") + "        <replacer class-name='") + (AbstractConfigImportVariableReplacementTest.TestReplacer.class.getName())) + "\'>\n") + "            <properties>\n") + "                <property name=\'p1\'>a property</property>\n") + "                <property name=\'p2\'/>\n") + "                <property name=\'p3\'>another property</property>\n") + "                <property name=\'p4\'>&lt;test/&gt;</property>\n") + "            </properties>\n") + "        </replacer>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>$T{p1} $T{p2} $T{p3} $T{p4} $T{p5}</name>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        GroupConfig groupConfig = XmlConfigImportVariableReplacementTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("a property  another property <test/> $T{p5}", groupConfig.getName());
    }

    @Override
    @Test
    public void testNoConfigReplacersMissingProperties() throws Exception {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <group>\n") + "        <name>${noSuchPropertyAvailable}</name>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        GroupConfig groupConfig = XmlConfigImportVariableReplacementTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable}", groupConfig.getName());
    }

    @Override
    @Test
    public void testVariableReplacementAsSubstring() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"${env}-with-suffix\">local-with-suffix</property>\n") + "        <property name=\"with-prefix-${env}\">with-prefix-local</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, "env", "local");
        Assert.assertEquals(config.getProperty("local-with-suffix"), "local-with-suffix");
        Assert.assertEquals(config.getProperty("with-prefix-local"), "with-prefix-local");
    }

    @Override
    @Test
    public void testImportWithVariableReplacementAsSubstring() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"prop1\">value1</property>\n") + "        <property name=\"prop2\">value2</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <import resource=\"file:///") + "${file}") + "\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(xml, "file", file.getAbsolutePath());
        Assert.assertEquals(config.getProperty("prop1"), "value1");
        Assert.assertEquals(config.getProperty("prop2"), "value2");
    }

    @Override
    @Test
    public void testReplaceVariablesWithFileSystemConfig() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String configXml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"prop\">${variable}</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, configXml);
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new FileSystemXmlConfig(file, properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithInMemoryConfig() {
        String configXml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"prop\">${variable}</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new InMemoryXmlConfig(configXml, properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithClasspathConfig() {
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new ClasspathXmlConfig("test-hazelcast-variable.xml", properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithUrlConfig() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String configXml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"prop\">${variable}</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, configXml);
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new UrlXmlConfig(("file:///" + (file.getPath())), properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesUseSystemProperties() {
        String configXml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <properties>\n") + "        <property name=\"prop\">${variable}</property>\n") + "    </properties>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        System.setProperty("variable", "foobar");
        Config config = XmlConfigImportVariableReplacementTest.buildConfig(configXml);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }
}

