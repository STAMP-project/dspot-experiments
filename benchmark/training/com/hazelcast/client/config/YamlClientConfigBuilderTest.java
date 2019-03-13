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
import com.hazelcast.client.util.RandomLB;
import com.hazelcast.client.util.RoundRobinLB;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


/**
 * This class tests the usage of {@link YamlClientConfigBuilder}
 */
// tests need to be executed sequentially because of system properties being set/unset
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class YamlClientConfigBuilderTest extends AbstractClientConfigBuilderTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidRootElement() {
        String yaml = "" + ((("hazelcast:\n" + "  group:\n") + "    name: dev\n") + "    password: clusterpass");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void loadingThroughSystemProperty_nonExistingFile() throws IOException {
        File file = File.createTempFile("foo", ".yaml");
        IOUtil.delete(file);
        System.setProperty("hazelcast.client.config", file.getAbsolutePath());
        new YamlClientConfigBuilder();
    }

    @Override
    @Test
    public void loadingThroughSystemProperty_existingFile() throws IOException {
        String yaml = "" + ((("hazelcast-client:\n" + "  group:\n") + "    name: foobar\n") + "    password: dev-pass");
        File file = File.createTempFile("foo", ".yaml");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(yaml);
        writer.close();
        System.setProperty("hazelcast.client.config", file.getAbsolutePath());
        YamlClientConfigBuilder configBuilder = new YamlClientConfigBuilder();
        ClientConfig config = configBuilder.build();
        Assert.assertEquals("foobar", config.getGroupConfig().getName());
    }

    @Override
    @Test(expected = HazelcastException.class)
    public void loadingThroughSystemProperty_nonExistingClasspathResource() throws IOException {
        System.setProperty("hazelcast.client.config", "classpath:idontexist.yaml");
        new YamlClientConfigBuilder();
    }

    @Override
    @Test
    public void loadingThroughSystemProperty_existingClasspathResource() throws IOException {
        System.setProperty("hazelcast.client.config", "classpath:test-hazelcast-client.yaml");
        YamlClientConfigBuilder configBuilder = new YamlClientConfigBuilder();
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
        String yaml = "" + (((("hazelcast-client:\n" + "  flake-id-generator:\n") + "    gen:\n") + "      prefetch-count: 3\n") + "      prefetch-validity-millis: 10");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        ClientFlakeIdGeneratorConfig fConfig = config.findFlakeIdGeneratorConfig("gen");
        Assert.assertEquals("gen", fConfig.getName());
        Assert.assertEquals(3, fConfig.getPrefetchCount());
        Assert.assertEquals(10L, fConfig.getPrefetchValidityMillis());
    }

    @Override
    @Test
    public void testSecurityConfig_onlyFactory() {
        String yaml = "" + ((((("hazelcast-client:\n" + "  security:\n") + "    credentials-factory:\n") + "      class-name: com.hazelcast.examples.MyCredentialsFactory\n") + "      properties:\n") + "        property: value");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        ClientSecurityConfig securityConfig = config.getSecurityConfig();
        CredentialsFactoryConfig credentialsFactoryConfig = securityConfig.getCredentialsFactoryConfig();
        Assert.assertEquals("com.hazelcast.examples.MyCredentialsFactory", credentialsFactoryConfig.getClassName());
        Properties properties = credentialsFactoryConfig.getProperties();
        Assert.assertEquals("value", properties.getProperty("property"));
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastClientTagAppearsTwice() {
        String yaml = "" + ("hazelcast-client: {}\n" + "hazelcast-client: {}");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test
    public void testNearCacheInMemoryFormatNative_withKeysByReference() {
        String mapName = "testMapNearCacheInMemoryFormatNative";
        String yaml = (((("" + (("hazelcast-client:\n" + "  near-cache:\n") + "    ")) + mapName) + ":\n") + "      in-memory-format: NATIVE\n") + "      serialize-keys: false";
        ClientConfig clientConfig = YamlClientConfigBuilderTest.buildConfig(yaml);
        NearCacheConfig ncConfig = clientConfig.getNearCacheConfig(mapName);
        Assert.assertEquals(NATIVE, ncConfig.getInMemoryFormat());
        Assert.assertTrue(ncConfig.isSerializeKeys());
    }

    @Override
    @Test
    public void testNearCacheEvictionPolicy() {
        String yaml = "" + ((((((((((((("hazelcast-client:\n" + "  near-cache:\n") + "    lfu:\n") + "      eviction:\n") + "        eviction-policy: LFU\n") + "    lru:\n") + "      eviction:\n") + "        eviction-policy: LRU\n") + "    none:\n") + "      eviction:\n") + "        eviction-policy: NONE\n") + "    random:\n") + "      eviction:\n") + "        eviction-policy: RANDOM");
        ClientConfig clientConfig = YamlClientConfigBuilderTest.buildConfig(yaml);
        Assert.assertEquals(LFU, getNearCacheEvictionPolicy("lfu", clientConfig));
        Assert.assertEquals(LRU, getNearCacheEvictionPolicy("lru", clientConfig));
        Assert.assertEquals(NONE, getNearCacheEvictionPolicy("none", clientConfig));
        Assert.assertEquals(RANDOM, getNearCacheEvictionPolicy("random", clientConfig));
    }

    @Override
    @Test
    public void testClientUserCodeDeploymentConfig() {
        String yaml = "" + ((((((("hazelcast-client:\n" + "  user-code-deployment:\n") + "    enabled: true\n") + "    jarPaths:\n") + "      - /User/test/test.jar\n") + "    classNames:\n") + "      - test.testClassName\n") + "      - test.testClassName2");
        ClientConfig clientConfig = YamlClientConfigBuilderTest.buildConfig(yaml);
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
        String yaml = "" + (("hazelcast-client:\n" + "  reliable-topic:\n") + "    rel-topic: {}");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        ClientReliableTopicConfig reliableTopicConfig = config.getReliableTopicConfig("rel-topic");
        Assert.assertEquals("rel-topic", reliableTopicConfig.getName());
        Assert.assertEquals(10, reliableTopicConfig.getReadBatchSize());
        Assert.assertEquals(BLOCK, reliableTopicConfig.getTopicOverloadPolicy());
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testQueryCacheBothPredicateDefinedThrows() {
        String yaml = "" + (((((("hazelcast-client:\n" + "  query-caches:\n") + "    query-cache-name:\n") + "      map-name: map-name\n") + "      predicate:\n") + "        class-name: com.hazelcast.example.Predicate\n") + "        sql: \"%age=40\"");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testQueryCacheNoPredicateDefinedThrows() {
        String yaml = "" + ((("hazelcast-client:\n" + "  query-caches:\n") + "    query-cache-name:\n") + "      predicate: {}");
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Override
    @Test
    public void testLoadBalancerRandom() {
        String yaml = "" + (("hazelcast-client:\n" + "  load-balancer:\n") + "    type: random");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        assertInstanceOf(RandomLB.class, config.getLoadBalancer());
    }

    @Override
    @Test
    public void testLoadBalancerRoundRobin() {
        String yaml = "" + (("hazelcast-client:\n" + "  load-balancer:\n") + "    type: round-robin");
        ClientConfig config = YamlClientConfigBuilderTest.buildConfig(yaml);
        assertInstanceOf(RoundRobinLB.class, config.getLoadBalancer());
    }

    @Test
    public void testNullInMapThrows() {
        String yaml = "" + (("hazelcast-client:\n" + "  group:\n") + "  name: instanceName");
        expected.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group"));
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Test
    public void testNullInSequenceThrows() {
        String yaml = "" + ((("hazelcast-client:\n" + "  client-labels:\n") + "    - admin\n") + "    -\n");
        expected.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/client-labels"));
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }

    @Test
    public void testExplicitNullScalarThrows() {
        String yaml = "" + (("hazelcast-client:\n" + "  group:\n") + "   name: !!null");
        expected.expect(new RootCauseMatcher(InvalidConfigurationException.class, "hazelcast-client/group/name"));
        YamlClientConfigBuilderTest.buildConfig(yaml);
    }
}

