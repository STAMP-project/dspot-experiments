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


import com.hazelcast.config.AbstractConfigImportVariableReplacementTest.IdentityReplacer;
import com.hazelcast.config.AbstractConfigImportVariableReplacementTest.TestReplacer;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.replacer.EncryptionReplacer;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class XmlClientConfigImportVariableReplacementTest extends AbstractClientConfigImportVariableReplacementTest {
    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportElementOnlyAppersInTopLevel() {
        String xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "   <network>\n") + "        <import resource=\"\"/>\n") + "   </network>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastElementOnlyAppearsOnce() {
        String xml = (((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "   <hazelcast-client>") + "   </hazelcast-client>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test
    public void readVariables() {
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<executor-pool-size>${executor.pool.size}</executor-pool-size>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml, "executor.pool.size", "40");
        Assert.assertEquals(40, config.getExecutorPoolSize());
    }

    @Override
    @Test
    public void testImportConfigFromResourceVariables() throws IOException {
        File file = AbstractClientConfigImportVariableReplacementTest.createConfigFile("foo", "bar");
        FileOutputStream os = new FileOutputStream(file);
        String networkConfig = (((((((((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<network>") + "    <cluster-members>") + "      <address>192.168.100.100</address>") + "      <address>127.0.0.10</address>") + "    </cluster-members>") + "    <smart-routing>false</smart-routing>") + "    <redo-operation>true</redo-operation>") + "    <socket-interceptor enabled=\"true\">") + "      <class-name>com.hazelcast.examples.MySocketInterceptor</class-name>") + "      <properties>") + "        <property name=\"foo\">bar</property>") + "      </properties>") + "    </socket-interceptor>") + "  </network>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"${config.location}\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml, "config.location", file.getAbsolutePath());
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
        String networkConfig = ((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "  <network>") + "    <cluster-members>") + "      <address>${ip.address}</address>") + "    </cluster-members>") + "  </network>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, networkConfig);
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"${config.location}\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        Properties properties = new Properties();
        properties.setProperty("config.location", file.getAbsolutePath());
        properties.setProperty("ip.address", "192.168.5.5");
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml, properties);
        assertContains(config.getNetworkConfig().getAddresses(), "192.168.5.5");
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testTwoResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".xml");
        File config2 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz2", ".xml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        String config1Xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config2.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        String config2Xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config1.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Xml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Xml);
        XmlClientConfigBuilderTest.buildConfig(config1Xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testThreeResourceCyclicImportThrowsException() throws Exception {
        File config1 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".xml");
        File config2 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz2", ".xml");
        File config3 = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz3", ".xml");
        FileOutputStream os1 = new FileOutputStream(config1);
        FileOutputStream os2 = new FileOutputStream(config2);
        FileOutputStream os3 = new FileOutputStream(config2);
        String config1Xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config2.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        String config2Xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config3.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        String config3Xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config1.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os1, config1Xml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os2, config2Xml);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os3, config3Xml);
        XmlClientConfigBuilderTest.buildConfig(config1Xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportEmptyResourceContent() throws Exception {
        File config = AbstractClientConfigImportVariableReplacementTest.createConfigFile("hz1", ".xml");
        FileOutputStream os = new FileOutputStream(config);
        String configXml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"file:///") + (config.getAbsolutePath())) + "\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        AbstractClientConfigImportVariableReplacementTest.writeStringToStreamAndClose(os, "");
        XmlClientConfigBuilderTest.buildConfig(configXml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportEmptyResourceThrowsException() {
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testImportNotExistingResourceThrowsException() {
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"notexisting.xml\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
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
        String xml = (((((((((((((((((((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <config-replacers>\n") + "        <replacer class-name='") + (EncryptionReplacer.class.getName())) + "\'>\n") + "            <properties>\n") + "                <property name='passwordFile'>") + (passwordFile.getAbsolutePath())) + "</property>\n") + "                <property name=\'passwordUserProperties\'>false</property>\n") + "                <property name=\'keyLengthBits\'>64</property>\n") + "                <property name=\'saltLengthBytes\'>8</property>\n") + "                <property name=\'cipherAlgorithm\'>DES</property>\n") + "                <property name=\'secretKeyFactoryAlgorithm\'>PBKDF2WithHmacSHA1</property>\n") + "                <property name=\'secretKeyAlgorithm\'>DES</property>\n") + "            </properties>\n") + "        </replacer>\n") + "        <replacer class-name='") + (IdentityReplacer.class.getName())) + "\'/>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>${java.version} $ID{dev}</name>\n") + "        <password>$ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}</password>\n") + "    </group>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        GroupConfig groupConfig = XmlClientConfigBuilderTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals(((System.getProperty("java.version")) + " dev"), groupConfig.getName());
        Assert.assertEquals("My very secret secret", groupConfig.getPassword());
    }

    @Override
    @Test(expected = ConfigurationException.class)
    public void testMissingReplacement() {
        String xml = (((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <config-replacers>\n") + "        <replacer class-name='") + (EncryptionReplacer.class.getName())) + "\'/>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>$ENC{7JX2r/8qVVw=:10000:Jk4IPtor5n/vCb+H8lYS6tPZOlCZMtZv}</name>\n") + "    </group>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml, System.getProperties());
    }

    @Override
    @Test
    public void testReplacerProperties() {
        String xml = ((((((((((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <config-replacers fail-if-value-missing=\'false\'>\n") + "        <replacer class-name='") + (TestReplacer.class.getName())) + "\'>\n") + "            <properties>\n") + "                <property name=\'p1\'>a property</property>\n") + "                <property name=\'p2\'/>\n") + "                <property name=\'p3\'>another property</property>\n") + "                <property name=\'p4\'>&lt;test/&gt;</property>\n") + "            </properties>\n") + "        </replacer>\n") + "    </config-replacers>\n") + "    <group>\n") + "        <name>$T{p1} $T{p2} $T{p3} $T{p4} $T{p5}</name>\n") + "    </group>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        GroupConfig groupConfig = XmlClientConfigBuilderTest.buildConfig(xml, System.getProperties()).getGroupConfig();
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
        String xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <group>\n") + "        <name>${noSuchPropertyAvailable}</name>\n") + "    </group>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        GroupConfig groupConfig = XmlClientConfigBuilderTest.buildConfig(xml, System.getProperties()).getGroupConfig();
        Assert.assertEquals("${noSuchPropertyAvailable}", groupConfig.getName());
    }

    @Override
    @Test
    public void testImportGroupConfigFromClassPath() {
        String xml = ((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <import resource=\"classpath:hazelcast-client-c1.xml\"/>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml);
        GroupConfig groupConfig = config.getGroupConfig();
        Assert.assertEquals("cluster1", groupConfig.getName());
        Assert.assertEquals("cluster1pass", groupConfig.getPassword());
    }

    @Override
    @Test
    public void testReplaceVariablesUseSystemProperties() {
        String xml = ((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <properties>\n") + "        <property name=\"prop\">${variable}</property>\n") + "    </properties>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        System.setProperty("variable", "foobar");
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml);
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }

    @Override
    @Test
    public void testReplaceVariablesWithClasspathConfig() {
        System.setProperty("variable", "foobar");
        ClientConfig config = new ClientClasspathXmlConfig("test-hazelcast-client-variable.xml");
        Assert.assertEquals("foobar", config.getProperty("prop"));
    }
}

