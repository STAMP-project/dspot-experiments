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
import com.hazelcast.util.RootCauseMatcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class YamlConfigImportVariableReplacementTest extends AbstractConfigImportVariableReplacementTest {
    @Override
    @Test
    public void testHazelcastElementOnlyAppearsOnce() {
        String yaml = "" + ("hazelcast:\n{}" + "hazelcast:");
        expectInvalid();
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
    }

    @Override
    @Test
    public void readVariables() {
        String yaml = "" + (((("hazelcast:\n" + "  semaphore:\n") + "    ${name}:\n") + "      initial-permits: ${initial.permits}\n") + "      backup-count: ${backupcount.part1}${backupcount.part2}\n");
        Properties properties = new Properties();
        properties.setProperty("name", "s");
        properties.setProperty("initial.permits", "25");
        properties.setProperty("backupcount.part1", "0");
        properties.setProperty("backupcount.part2", "6");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, properties);
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
        String networkConfig = "" + (((((("hazelcast:\n" + "  network:\n") + "    join:\n") + "      multicast:\n") + "        enabled: false\n") + "      tcp-ip:\n") + "        enabled: true\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = "" + (("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testImportedConfigVariableReplacement() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = "" + (((((("hazelcast:\n" + "  network:\n") + "    join:\n") + "      multicast:\n") + "        enabled: false\n") + "      tcp-ip:\n") + "        enabled: ${tcp.ip.enabled}\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = "" + (("hazelcast:\n" + "  import:\n") + "    - ${config.location}");
        Properties properties = new Properties();
        properties.setProperty("config.location", file.getAbsolutePath());
        properties.setProperty("tcp.ip.enabled", "true");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, properties);
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testTwoResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "yaml");
        File config2 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz2", "yaml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        String config1Yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (config2.getAbsolutePath());
        String config2Yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (config1.getAbsolutePath());
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Yaml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Yaml);
        expectInvalid();
        YamlConfigImportVariableReplacementTest.buildConfig(config1Yaml, null);
    }

    @Override
    @Test
    public void testThreeResourceCyclicImportThrowsException() throws Exception {
        String template = "" + (("hazelcast:\n" + "  import:\n") + "    - file:///%s");
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "yaml");
        File config2 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz2", "yaml");
        File config3 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz3", "yaml");
        String config1Yaml = String.format(template, config2.getAbsolutePath());
        String config2Yaml = String.format(template, config3.getAbsolutePath());
        String config3Yaml = String.format(template, config1.getAbsolutePath());
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config1), config1Yaml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config2), config2Yaml);
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(new FileOutputStream(config3), config3Yaml);
        expectInvalid();
        YamlConfigImportVariableReplacementTest.buildConfig(config1Yaml, null);
    }

    @Override
    @Test
    public void testImportEmptyResourceContent() throws Exception {
        File config1 = AbstractConfigImportVariableReplacementTest.createConfigFile("hz1", "yaml");
        FileOutputStream os1 = new FileOutputStream(config1);
        String config1Yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (config1.getAbsolutePath());
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, "%invalid-yaml");
        expectInvalid();
        YamlConfigImportVariableReplacementTest.buildConfig(config1Yaml, null);
    }

    @Override
    @Test
    public void testImportEmptyResourceThrowsException() {
        String yaml = "" + (("hazelcast:\n" + "  import:\n") + "    - \"\"");
        expectInvalid();
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
    }

    @Override
    @Test
    public void testImportNotExistingResourceThrowsException() {
        expectInvalid();
        String yaml = "" + (("hazelcast:\n" + "  import:\n") + "    - notexisting.yaml");
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void testImportFromNonHazelcastConfigThrowsException() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("mymap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = "" + ((("non-hazelcast:\n" + "  map:\n") + "    mymap:\n") + "      backup-count: 3");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (file.getAbsolutePath());
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
        Assert.assertNull(config.getMapConfig("mymap"));
    }

    @Override
    @Test
    public void testImportNetworkConfigFromFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = "" + (((((("hazelcast:\n" + "  network:\n") + "    join:\n") + "      multicast:\n") + "        enabled: false\n") + "      tcp-ip:\n") + "        enabled: true\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (file.getAbsolutePath());
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
        JoinConfig join = config.getNetworkConfig().getJoin();
        Assert.assertFalse(join.getMulticastConfig().isEnabled());
        Assert.assertTrue(join.getTcpIpConfig().isEnabled());
    }

    @Override
    @Test
    public void testImportMapConfigFromFile() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("mymap", "config");
        FileOutputStream os = new FileOutputStream(file);
        String mapConfig = "" + (((((((((("hazelcast:\n" + "  map:\n") + "    mymap:\n") + "      backup-count: 6\n") + "      time-to-live-seconds: 10\n") + "      map-store:\n") + "        enabled: true\n") + "        initial-mode: LAZY\n") + "        class-name: com.hazelcast.examples.MyMapStore\n") + "        write-delay-seconds: 10\n") + "        write-batch-size: 100\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String yaml = ("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (file.getAbsolutePath());
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
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
        String mapConfig = "" + ((((((((("hazelcast:\n" + "  map:\n") + "    mymap:\n") + "      backup-count: 6\n") + "      map-store:\n") + "        enabled: true\n") + "        initial-mode: LAZY\n") + "        class-name: com.hazelcast.examples.MyMapStore\n") + "        write-delay-seconds: 10\n") + "        write-batch-size: 100\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, mapConfig);
        String yaml = ((((("" + (("hazelcast:\n" + "  import:\n") + "    - file:///")) + (file.getAbsolutePath())) + "\n") + "  map:\n") + "    mymap:\n") + "      time-to-live-seconds: 10\n";
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
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
    public void testImportGroupConfigFromClassPath() {
        String yaml = "" + (("hazelcast:\n" + "  import:\n") + "    - classpath:test-hazelcast.yaml");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, null);
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
        String yaml = (((((((((((((((((("" + ((("hazelcast:\n" + "  config-replacers:\n") + "    replacers:\n") + "      - class-name: ")) + (EncryptionReplacer.class.getName())) + "\n") + "        properties:\n") + "          passwordFile: ") + (passwordFile.getAbsolutePath())) + "\n") + "          passwordUserProperties: false\n") + "          keyLengthBits: 64\n") + "          saltLengthBytes: 8\n") + "          cipherAlgorithm: DES\n") + "          secretKeyFactoryAlgorithm: PBKDF2WithHmacSHA1\n") + "          secretKeyAlgorithm: DES\n") + "      - class-name: ") + (AbstractConfigImportVariableReplacementTest.IdentityReplacer.class.getName())) + "\n") + "  group:\n") + "    name: ${java.version} $ID{dev}\n") + "    password: $ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}\n";
        GroupConfig groupConfig = YamlConfigImportVariableReplacementTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals(((System.getProperty("java.version")) + " dev"), groupConfig.getName());
        Assert.assertEquals("My very secret secret", groupConfig.getPassword());
    }

    @Override
    @Test(expected = ConfigurationException.class)
    public void testMissingReplacement() {
        String yaml = (((("" + ((("hazelcast:\n" + "  config-replacers:\n") + "    replacers:\n") + "      - class-name: ")) + (EncryptionReplacer.class.getName())) + "\n") + "  group:\n") + "    name: $ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}";
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, System.getProperties());
    }

    @Override
    @Test
    public void testBadVariableSyntaxIsIgnored() {
        String yaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: ${noSuchPropertyAvailable]");
        GroupConfig groupConfig = YamlConfigImportVariableReplacementTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable]", groupConfig.getName());
    }

    @Override
    @Test
    public void testReplacerProperties() {
        String yaml = ((((((((("" + (((("hazelcast:\n" + "  config-replacers:\n") + "    fail-if-value-missing: false\n") + "    replacers:\n") + "      - class-name: ")) + (AbstractConfigImportVariableReplacementTest.TestReplacer.class.getName())) + "\n") + "        properties:\n") + "          p1: a property\n") + "          p2: \"\"\n") + "          p3: another property\n") + "          p4: <test/>\n") + "  group:\n") + "    name: $T{p1} $T{p2} $T{p3} $T{p4} $T{p5}\n";
        GroupConfig groupConfig = YamlConfigImportVariableReplacementTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("a property  another property <test/> $T{p5}", groupConfig.getName());
    }

    /**
     * Given: No replacer is used in the configuration file<br>
     * When: A property variable is used within the file<br>
     * Then: The configuration parsing doesn't fail and the variable string remains unchanged (i.e. backward compatible
     * behavior, as if {@code fail-if-value-missing} attribute is {@code false}).
     */
    @Override
    @Test
    public void testNoConfigReplacersMissingProperties() {
        String yaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: ${noSuchPropertyAvailable}");
        GroupConfig groupConfig = YamlConfigImportVariableReplacementTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable}", groupConfig.getName());
    }

    @Override
    @Test
    public void testVariableReplacementAsSubstring() {
        String yaml = "" + ((("hazelcast:\n" + "  properties:\n") + "    ${env}-with-suffix: local-with-suffix\n") + "    with-prefix-${env}: with-prefix-local");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, "env", "local");
        Assert.assertEquals(config.getProperty("local-with-suffix"), "local-with-suffix");
        Assert.assertEquals(config.getProperty("with-prefix-local"), "with-prefix-local");
    }

    @Override
    @Test
    public void testImportWithVariableReplacementAsSubstring() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = "" + ((("hazelcast:\n" + "  properties:\n") + "    prop1: value1\n") + "    prop2: value2\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = "" + ((("hazelcast:\n" + "  import:\n") + "    - file:///") + "${file}");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, "file", file.getAbsolutePath());
        Assert.assertEquals(config.getProperty("prop1"), "value1");
        Assert.assertEquals(config.getProperty("prop2"), "value2");
    }

    @Override
    @Test
    public void testReplaceVariablesWithFileSystemConfig() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String configYaml = "" + (("hazelcast:\n" + "  properties:\n") + "    prop: ${variable}");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, configYaml);
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new FileSystemYamlConfig(file, properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithInMemoryConfig() {
        String configYaml = "" + (("hazelcast:\n" + "  properties:\n") + "    prop: ${variable}");
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new InMemoryYamlConfig(configYaml, properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithClasspathConfig() {
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new ClasspathYamlConfig("test-hazelcast-variable.yaml", properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithUrlConfig() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String configYaml = "" + (("hazelcast:\n" + "  properties:\n") + "    prop: ${variable}");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, configYaml);
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        Config config = new UrlYamlConfig(("file:///" + (file.getPath())), properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesUseSystemProperties() {
        String configYaml = "" + (("hazelcast:\n" + "  properties:\n") + "    prop: ${variable}");
        System.setProperty("variable", "foobar");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(configYaml);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Test
    public void testImportRedefinesSameConfigScalarThrows() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: name1");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: name2");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast/group/name"));
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportSameScalarConfig() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: name");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: name");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        Assert.assertEquals("name", config.getGroupConfig().getName());
    }

    @Test
    public void testImportNodeScalarVsSequenceThrows() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: name1");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + ((((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name:\n") + "      - seqName: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast/group/name"));
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeScalarVsMappingThrows() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast:\n" + "  group:\n") + "    name: name1");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast/group/name"));
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeSequenceVsMappingThrows() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + ((("hazelcast:\n" + "  group:\n") + "    name:\n") + "      - seqname");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast/group/name"));
        YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeSequenceVsSequenceMerges() throws Exception {
        File file = AbstractConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast:\n" + "  listeners:\n") + "    - com.hazelcast.examples.MembershipListener\n");
        AbstractConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast:\n" + "  import:\n") + "    - ${config.location}\n") + "  listeners:\n") + "    - com.hazelcast.examples.MigrationListener\n");
        Config config = YamlConfigImportVariableReplacementTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        List<ListenerConfig> listenerConfigs = config.getListenerConfigs();
        Assert.assertEquals(2, listenerConfigs.size());
        for (ListenerConfig listenerConfig : listenerConfigs) {
            Assert.assertTrue((("com.hazelcast.examples.MembershipListener".equals(listenerConfig.getClassName())) || ("com.hazelcast.examples.MigrationListener".equals(listenerConfig.getClassName()))));
        }
    }
}

