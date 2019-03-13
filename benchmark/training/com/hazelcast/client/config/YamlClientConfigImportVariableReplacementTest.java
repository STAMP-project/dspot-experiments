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
package com.hazelcast.client.config;


import AbstractConfigImportVariableReplacementTest.IdentityReplacer;
import AbstractConfigImportVariableReplacementTest.TestReplacer;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.replacer.EncryptionReplacer;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class YamlClientConfigImportVariableReplacementTest extends AbstractClientConfigImportVariableReplacementTest {
    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportElementOnlyAppersInTopLevel() {
        String yaml = "" + ((("hazelcast:\n" + "  network:\n") + "    import:\n") + "      resource: \"\"");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastElementOnlyAppearsOnce() {
        String yaml = "" + ("hazelcast-client: {}\n" + "hazelcast-client: {}");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test
    public void readVariables() {
        String yaml = "" + ("hazelcast-client:\n" + "  executor-pool-size: ${executor.pool.size}");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml, "executor.pool.size", "40");
        Assert.assertEquals(40, config.getExecutorPoolSize());
    }

    @Override
    @Test
    public void testImportConfigFromResourceVariables() throws IOException {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = "" + ((((((((((("hazelcast-client:\n" + "  network:\n") + "    cluster-members:\n") + "      - 192.168.100.100\n") + "      - 127.0.0.10\n") + "    smart-routing: false\n") + "    redo-operation: true\n") + "    socket-interceptor:\n") + "      enabled: true\n") + "      class-name: com.hazelcast.examples.MySocketInterceptor\n") + "      properties:\n") + "        foo: bar");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = "" + (("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        Assert.assertFalse(config.getNetworkConfig().isSmartRouting());
        Assert.assertTrue(config.getNetworkConfig().isRedoOperation());
        assertContains(config.getNetworkConfig().getAddresses(), "192.168.100.100");
        assertContains(config.getNetworkConfig().getAddresses(), "127.0.0.10");
    }

    @Override
    @Test
    public void testImportedConfigVariableReplacement() throws IOException {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = "" + ((("hazelcast-client:\n" + "  network:\n") + "    cluster-members:\n") + "      - ${ip.address}");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String yaml = "" + (("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}");
        Properties properties = new Properties();
        properties.setProperty("config.location", file.getAbsolutePath());
        properties.setProperty("ip.address", "192.168.5.5");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml, properties);
        assertContains(config.getNetworkConfig().getAddresses(), "192.168.5.5");
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testTwoResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".yaml");
        File config2 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz2", ".yaml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        String config1Yaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config2.getAbsolutePath())) + "\"";
        String config2Yaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config1.getAbsolutePath())) + "\"";
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Yaml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Yaml);
        YamlClientConfigBuilderTest.buildConfig(config1Yaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testThreeResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".yaml");
        File config2 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz2", ".yaml");
        File config3 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz3", ".yaml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        FileOutputStream os3 = new FileOutputStream(config2);
        String config1Yaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config2.getAbsolutePath())) + "\"";
        String config2Yaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config3.getAbsolutePath())) + "\"";
        String config3Yaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config1.getAbsolutePath())) + "\"";
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Yaml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Yaml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os3, config3Yaml);
        YamlClientConfigBuilderTest.buildConfig(config1Yaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportEmptyResourceContent() throws Exception {
        File config = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".yaml");
        FileOutputStream os = new FileOutputStream(config);
        String configYaml = (("" + (("hazelcast-client:\n" + "  import:\n") + "    - file:///")) + (config.getAbsolutePath())) + "\"";
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, "");
        YamlClientConfigBuilderTest.buildConfig(configYaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportEmptyResourceThrowsException() {
        String yaml = "" + (("hazelcast-client:\n" + "  import:\n") + "    - \"\"");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportNotExistingResourceThrowsException() {
        String yaml = "" + (("hazelcast-client:\n" + "  import:\n") + "    - notexisting.yaml");
        YamlClientConfigBuilderTest.buildConfig(yaml);
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
        String yaml = (((((((((((((((((("" + ((("hazelcast-client:\n" + "  config-replacers:\n") + "    replacers:\n") + "      - class-name: ")) + (EncryptionReplacer.class.getName())) + "\n") + "        properties:\n") + "          passwordFile: ") + (passwordFile.getAbsolutePath())) + "\n") + "          passwordUserProperties: false\n") + "          keyLengthBits: 64\n") + "          saltLengthBytes: 8\n") + "          cipherAlgorithm: DES\n") + "          secretKeyFactoryAlgorithm: PBKDF2WithHmacSHA1\n") + "          secretKeyAlgorithm: DES\n") + "      - class-name: ") + (IdentityReplacer.class.getName())) + "\n") + "  group:\n") + "    name: ${java.version} $ID{dev}\n") + "    password: $ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}";
        GroupConfig groupConfig = YamlClientConfigBuilderTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals(((System.getProperty("java.version")) + " dev"), groupConfig.getName());
        Assert.assertEquals("My very secret secret", groupConfig.getPassword());
    }

    @Override
    @Test(expected = ConfigurationException.class)
    public void testMissingReplacement() {
        String yaml = (((("" + ((("hazelcast-client:\n" + "  config-replacers:\n") + "    replacers:\n") + "      - class-name: ")) + (EncryptionReplacer.class.getName())) + "\n") + "    group:\n") + "      name: $ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}\n";
        YamlClientConfigBuilderTest.buildConfig(yaml, System.getProperties());
    }

    @Override
    @Test
    public void testReplacerProperties() {
        String yaml = ((((((((("" + (((("hazelcast-client:\n" + "  config-replacers:\n") + "    fail-if-value-missing: false\n") + "    replacers:\n") + "      - class-name: ")) + (TestReplacer.class.getName())) + "\n") + "        properties:\n") + "          p1: a property\n") + "          p2: \"\"\n") + "          p3: another property\n") + "          p4: <test/>\n") + "  group:\n") + "    name: $T{p1} $T{p2} $T{p3} $T{p4} $T{p5}";
        GroupConfig groupConfig = YamlClientConfigBuilderTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("a property  another property <test/> $T{p5}", groupConfig.getName());
    }

    @Override
    @Test
    public void testNoConfigReplacersMissingProperties() {
        String yaml = "" + (("hazelcast-client:\n" + "  group:\n") + "    name: ${noSuchPropertyAvailable}");
        GroupConfig groupConfig = YamlClientConfigBuilderTest.buildConfig(yaml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable}", groupConfig.getName());
    }

    @Override
    @Test
    public void testImportGroupConfigFromClassPath() {
        String yaml = "" + (("hazelcast-client:\n" + "  import:\n") + "    - classpath:hazelcast-client-c1.yaml");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        GroupConfig groupConfig = config.getGroupConfig();
        Assert.assertEquals("cluster1", groupConfig.getName());
        Assert.assertEquals("cluster1pass", groupConfig.getPassword());
    }

    @Override
    @Test
    public void testReplaceVariablesUseSystemProperties() {
        String yaml = "" + (("hazelcast-client:\n" + "  properties:\n") + "    prop: ${variable}");
        System.setProperty("variable", "foobar");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithClasspathConfig() {
        Properties properties = new Properties();
        properties.put("variable", "foobar");
        ClientConfig config = new ClientClasspathYamlConfig("test-hazelcast-client-variable.yaml", properties);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Test
    public void testImportRedefinesSameConfigScalarThrows() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast-client:\n" + "  group:\n") + "    name: name1");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: name2");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group/name"));
        YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportSameScalarConfig() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast-client:\n" + "  group:\n") + "    name: name");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: name");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        Assert.assertEquals("name", config.getGroupConfig().getName());
    }

    @Test
    public void testImportNodeScalarVsSequenceThrows() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast-client:\n" + "  group:\n") + "    name: name1");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + ((((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name:\n") + "      - seqName: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group/name"));
        YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeScalarVsMappingThrows() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast-client:\n" + "  group:\n") + "    name: name1");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group/name"));
        YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeSequenceVsMappingThrows() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + ((("hazelcast-client:\n" + "  group:\n") + "    name:\n") + "      - seqname");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  group:\n") + "    name: {}");
        rule.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group/name"));
        YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
    }

    @Test
    public void testImportNodeSequenceVsSequenceMerges() throws Exception {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String importedYaml = "" + (("hazelcast-client:\n" + "  client-labels:\n") + "    - label1\n");
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, importedYaml);
        String yaml = "" + (((("hazelcast-client:\n" + "  import:\n") + "    - ${config.location}\n") + "  client-labels:\n") + "    - label2\n");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml, "config.location", file.getAbsolutePath());
        Set<String> labels = config.getLabels();
        Assert.assertEquals(2, labels.size());
        Assert.assertTrue(labels.contains("label1"));
        Assert.assertTrue(labels.contains("label2"));
    }
}

