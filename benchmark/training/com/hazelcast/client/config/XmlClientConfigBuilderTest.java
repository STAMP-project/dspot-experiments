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


import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import InMemoryFormat.NATIVE;
import TopicOverloadPolicy.BLOCK;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This class tests the usage of {@link XmlClientConfigBuilder}
 */
// tests need to be executed sequentially because of system properties being set/unset
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class XmlClientConfigBuilderTest extends AbstractClientConfigBuilderTest {
    static final String HAZELCAST_CLIENT_START_TAG = "<hazelcast-client xmlns=\"http://www.hazelcast.com/schema/client-config\">\n";

    static final String HAZELCAST_CLIENT_END_TAG = "</hazelcast-client>";

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidRootElement() {
        String xml = "<hazelcast>" + (((("<group>" + "<name>dev</name>") + "<password>clusterpass</password>") + "</group>") + "</hazelcast>");
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void loadingThroughSystemProperty_nonExistingFile() throws IOException {
        File file = File.createTempFile("foo", ".xml");
        IOUtil.delete(file);
        System.setProperty("hazelcast.client.config", file.getAbsolutePath());
        new XmlClientConfigBuilder();
    }

    @Override
    @Test
    public void loadingThroughSystemProperty_existingFile() throws IOException {
        String xml = (((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "    <group>\n") + "        <name>foobar</name>\n") + "        <password>dev-pass</password>\n") + "    </group>\n") + "</hazelcast-client>";
        File file = File.createTempFile("foo", ".xml");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(xml);
        writer.close();
        System.setProperty("hazelcast.client.config", file.getAbsolutePath());
        XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder();
        ClientConfig config = configBuilder.build();
        Assert.assertEquals("foobar", config.getGroupConfig().getName());
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void loadingThroughSystemProperty_nonExistingClasspathResource() throws IOException {
        System.setProperty("hazelcast.client.config", "classpath:idontexist.xml");
        new XmlClientConfigBuilder();
    }

    @Override
    @Test
    public void loadingThroughSystemProperty_existingClasspathResource() throws IOException {
        System.setProperty("hazelcast.client.config", "classpath:test-hazelcast-client.xml");
        XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder();
        ClientConfig config = configBuilder.build();
        Assert.assertEquals("foobar", config.getGroupConfig().getName());
        Assert.assertEquals("com.hazelcast.nio.ssl.BasicSSLContextFactory", config.getNetworkConfig().getSSLConfig().getFactoryClassName());
        Assert.assertEquals(128, config.getNetworkConfig().getSocketOptions().getBufferSize());
        Assert.assertFalse(config.getNetworkConfig().getSocketOptions().isKeepAlive());
        Assert.assertFalse(config.getNetworkConfig().getSocketOptions().isTcpNoDelay());
        Assert.assertEquals(3, config.getNetworkConfig().getSocketOptions().getLingerSeconds());
    }

    @Override
    @Test
    public void testFlakeIdGeneratorConfig() {
        String xml = (((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<flake-id-generator name='gen'>") + "  <prefetch-count>3</prefetch-count>") + "  <prefetch-validity-millis>10</prefetch-validity-millis>") + "</flake-id-generator>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml);
        ClientFlakeIdGeneratorConfig fConfig = config.findFlakeIdGeneratorConfig("gen");
        Assert.assertEquals("gen", fConfig.getName());
        Assert.assertEquals(3, fConfig.getPrefetchCount());
        Assert.assertEquals(10L, fConfig.getPrefetchValidityMillis());
    }

    @Override
    @Test
    public void testSecurityConfig_onlyFactory() {
        String xml = ((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "  <security>\n") + "        <credentials-factory class-name=\"com.hazelcast.examples.MyCredentialsFactory\">\n") + "            <properties>\n") + "                <property name=\"property\">value</property>\n") + "            </properties>\n") + "        </credentials-factory>\n") + "    </security>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml);
        ClientSecurityConfig securityConfig = config.getSecurityConfig();
        CredentialsFactoryConfig credentialsFactoryConfig = securityConfig.getCredentialsFactoryConfig();
        Assert.assertEquals("com.hazelcast.examples.MyCredentialsFactory", credentialsFactoryConfig.getClassName());
        Properties properties = credentialsFactoryConfig.getProperties();
        Assert.assertEquals("value", properties.getProperty("property"));
    }

    @Test
    public void testXSDDefaultXML() throws IOException, SAXException {
        testXSDConfigXML("hazelcast-client-default.xml");
    }

    @Test
    public void testFullConfigXML() throws IOException, SAXException {
        testXSDConfigXML("hazelcast-client-full.xml");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testMissingNamespace() {
        String xml = "<hazelcast-client/>";
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidNamespace() {
        String xml = "<hazelcast-client xmlns=\"http://foo.bar\"/>";
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Test
    public void testValidNamespace() {
        String xml = (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "</hazelcast-client>";
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastClientTagAppearsTwice() {
        String xml = (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<hazelcast-client/><hazelcast-client/>";
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test
    public void testNearCacheInMemoryFormatNative_withKeysByReference() {
        String mapName = "testMapNearCacheInMemoryFormatNative";
        String xml = (((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "  <near-cache name=\"") + mapName) + "\">\n") + "    <in-memory-format>NATIVE</in-memory-format>\n") + "    <serialize-keys>false</serialize-keys>\n") + "  </near-cache>\n") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig clientConfig = XmlClientConfigBuilderTest.buildConfig(xml);
        NearCacheConfig ncConfig = clientConfig.getNearCacheConfig(mapName);
        Assert.assertEquals(NATIVE, ncConfig.getInMemoryFormat());
        Assert.assertTrue(ncConfig.isSerializeKeys());
    }

    @Override
    @Test
    public void testNearCacheEvictionPolicy() {
        String xml = (((((((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<near-cache name=\"lfu\">") + "  <eviction eviction-policy=\"LFU\"/>") + "</near-cache>") + "<near-cache name=\"lru\">") + "  <eviction eviction-policy=\"LRU\"/>") + "</near-cache>") + "<near-cache name=\"none\">") + "  <eviction eviction-policy=\"NONE\"/>") + "</near-cache>") + "<near-cache name=\"random\">") + "  <eviction eviction-policy=\"RANDOM\"/>") + "</near-cache>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig clientConfig = XmlClientConfigBuilderTest.buildConfig(xml);
        Assert.assertEquals(LFU, getNearCacheEvictionPolicy("lfu", clientConfig));
        Assert.assertEquals(LRU, getNearCacheEvictionPolicy("lru", clientConfig));
        Assert.assertEquals(NONE, getNearCacheEvictionPolicy("none", clientConfig));
        Assert.assertEquals(RANDOM, getNearCacheEvictionPolicy("random", clientConfig));
    }

    @Override
    @Test
    public void testClientUserCodeDeploymentConfig() {
        String xml = ((((((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<user-code-deployment enabled=\"true\">\n") + "        <jarPaths>\n") + "            <jarPath>/User/test/test.jar</jarPath>\n") + "        </jarPaths>\n") + "        <classNames>\n") + "            <className>test.testClassName</className>\n") + "            <className>test.testClassName2</className>\n") + "        </classNames>\n") + "    </user-code-deployment>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig clientConfig = XmlClientConfigBuilderTest.buildConfig(xml);
        ClientUserCodeDeploymentConfig userCodeDeploymentConfig = clientConfig.getUserCodeDeploymentConfig();
        Assert.assertTrue(userCodeDeploymentConfig.isEnabled());
        List<String> classNames = userCodeDeploymentConfig.getClassNames();
        Assert.assertEquals(2, classNames.size());
        Assert.assertTrue(classNames.contains("test.testClassName"));
        Assert.assertTrue(classNames.contains("test.testClassName2"));
        List<String> jarPaths = userCodeDeploymentConfig.getJarPaths();
        Assert.assertEquals(1, jarPaths.size());
        Assert.assertTrue(jarPaths.contains("/User/test/test.jar"));
    }

    @Override
    @Test
    public void testReliableTopic_defaults() {
        String xml = (((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<reliable-topic name=\"rel-topic\">") + "</reliable-topic>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        ClientConfig config = XmlClientConfigBuilderTest.buildConfig(xml);
        ClientReliableTopicConfig reliableTopicConfig = config.getReliableTopicConfig("rel-topic");
        Assert.assertEquals("rel-topic", reliableTopicConfig.getName());
        Assert.assertEquals(10, reliableTopicConfig.getReadBatchSize());
        Assert.assertEquals(BLOCK, reliableTopicConfig.getTopicOverloadPolicy());
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testQueryCacheBothPredicateDefinedThrows() {
        String xml = (((((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<query-caches>") + "  <query-cache name=\"cache-name\" mapName=\"map-name\">") + "    <predicate type=\"class-name\">com.hazelcast.example.Predicate</predicate>") + "    <predicate type=\"sql\">%age=40</predicate>") + "  </query-cache>") + "</query-caches>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testQueryCacheNoPredicateDefinedThrows() {
        String xml = (((((XmlClientConfigBuilderTest.HAZELCAST_CLIENT_START_TAG) + "<query-caches>") + "  <query-cache name=\"cache-name\" mapName=\"map-name\">") + "  </query-cache>") + "</query-caches>") + (XmlClientConfigBuilderTest.HAZELCAST_CLIENT_END_TAG);
        XmlClientConfigBuilderTest.buildConfig(xml);
    }
}

