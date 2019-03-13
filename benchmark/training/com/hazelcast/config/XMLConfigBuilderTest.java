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


import CacheDeserializedValues.ALWAYS;
import CacheDeserializedValues.INDEX_ONLY;
import CacheDeserializedValues.NEVER;
import ConsistencyCheckStrategy.MERKLE_TREES;
import EndpointQualifier.MEMBER;
import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import InMemoryFormat.BINARY;
import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import LoginModuleUsage.OPTIONAL;
import LoginModuleUsage.REQUIRED;
import LoginModuleUsage.SUFFICIENT;
import MapConfig.DEFAULT_MIN_EVICTION_CHECK_MILLIS;
import MapStoreConfig.DEFAULT_WRITE_COALESCING;
import MapStoreConfig.InitialLoadMode.EAGER;
import MapStoreConfig.InitialLoadMode.LAZY;
import MaxSizeConfig.MaxSizePolicy.PER_NODE;
import MaxSizeConfig.MaxSizePolicy.PER_PARTITION;
import MetadataPolicy.CREATE_ON_UPDATE;
import MultiMapConfig.ValueCollectionType.SET;
import OnJoinPermissionOperationName.RECEIVE;
import PartitionGroupConfig.MemberGroupType.SPI;
import PartitionGroupConfig.MemberGroupType.ZONE_AWARE;
import ProbabilisticQuorumConfigBuilder.DEFAULT_HEARTBEAT_INTERVAL_MILLIS;
import ProbabilisticQuorumConfigBuilder.DEFAULT_HEARTBEAT_PAUSE_MILLIS;
import ProbabilisticQuorumConfigBuilder.DEFAULT_MIN_STD_DEVIATION;
import ProbabilisticQuorumConfigBuilder.DEFAULT_PHI_THRESHOLD;
import ProbabilisticQuorumConfigBuilder.DEFAULT_SAMPLE_SIZE;
import QuorumType.READ;
import RecentlyActiveQuorumConfigBuilder.DEFAULT_HEARTBEAT_TOLERANCE_MILLIS;
import RestEndpointGroup.CLUSTER_WRITE;
import TopicOverloadPolicy.DISCARD_OLDEST;
import UserCodeDeploymentConfig.ClassCacheMode.OFF;
import UserCodeDeploymentConfig.ProviderMode.LOCAL_CLASSES_ONLY;
import WANQueueFullBehavior.THROW_EXCEPTION;
import WANQueueFullBehavior.THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE;
import WanPublisherState.REPLICATING;
import WanPublisherState.STOPPED;
import com.google.common.collect.ImmutableSet;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.config.helpers.DummyMapStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static HotRestartClusterDataRecoveryPolicy.PARTIAL_RECOVERY_MOST_RECENT;
import static PermissionType.CACHE;
import static PermissionType.CONFIG;


/**
 * XML specific implementation of the tests that should be maintained in
 * both XML and YAML configuration builder tests.
 * <p/>
 *
 * NOTE: This test class must not define test cases, it is meant only to
 * implement test cases defined in {@link AbstractConfigBuilderTest}.
 * <p/>
 *
 * NOTE2: Test cases specific to XML should be added to {@link XmlOnlyConfigBuilderTest}
 *
 * @see AbstractConfigBuilderTest
 * @see YamlConfigBuilderTest
 * @see XmlOnlyConfigBuilderTest
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings({ "WeakerAccess", "deprecation" })
public class XMLConfigBuilderTest extends AbstractConfigBuilderTest {
    static final String HAZELCAST_START_TAG = "<hazelcast xmlns=\"http://www.hazelcast.com/schema/config\">\n";

    static final String HAZELCAST_END_TAG = "</hazelcast>\n";

    static final String SECURITY_START_TAG = "<security enabled=\"true\">\n";

    static final String SECURITY_END_TAG = "</security>\n";

    static final String ACTIONS_FRAGMENT = "<actions>" + (((("<action>create</action>" + "<action>destroy</action>") + "<action>add</action>") + "<action>remove</action>") + "</actions>");

    @Override
    @Test
    public void testConfigurationURL() throws Exception {
        URL configURL = getClass().getClassLoader().getResource("hazelcast-default.xml");
        Config config = new XmlConfigBuilder(configURL).build();
        Assert.assertEquals(configURL, config.getConfigurationUrl());
    }

    @Override
    @Test
    public void testConfigurationWithFileName() throws Exception {
        HazelcastTestSupport.assumeThatNotZingJDK6();// https://github.com/hazelcast/hazelcast/issues/9044

        File file = File.createTempFile("foo", "bar");
        file.deleteOnExit();
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <group>\n") + "        <name>foobar</name>\n") + "        <password>dev-pass</password>\n") + "    </group>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Writer writer = new PrintWriter(file, "UTF-8");
        writer.write(xml);
        writer.close();
        String path = file.getAbsolutePath();
        Config config = new XmlConfigBuilder(path).build();
        Assert.assertEquals(path, config.getConfigurationFile().getAbsolutePath());
    }

    @Override
    @Test(expected = IllegalArgumentException.class)
    public void testConfiguration_withNullInputStream() {
        new XmlConfigBuilder(((InputStream) (null)));
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidRootElement() {
        String xml = "<hazelcast-client>" + (((("<group>" + "<name>dev</name>") + "<password>clusterpass</password>") + "</group>") + "</hazelcast-client>");
        buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testJoinValidation() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"true\"/>\n") + "            <tcp-ip enabled=\"true\"/>\n") + "        </join>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testSecurityInterceptorConfig() {
        String xml = ((((((((((((((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<security enabled=\"true\">") + "  <security-interceptors>") + "    <interceptor class-name=\"foo\"/>") + "    <interceptor class-name=\"bar\"/>") + "  </security-interceptors>") + "  <client-block-unmapped-actions>false</client-block-unmapped-actions>") + "  <member-credentials-factory class-name=\"MyCredentialsFactory\">\n") + "    <properties>\n") + "      <property name=\"property\">value</property>\n") + "    </properties>\n") + "  </member-credentials-factory>\n") + "  <member-login-modules>\n") + "    <login-module class-name=\"MyRequiredLoginModule\" usage=\"REQUIRED\">\n") + "      <properties>\n") + "        <property name=\"login-property\">login-value</property>\n") + "      </properties>\n") + "    </login-module>\n") + "    <login-module class-name=\"MyRequiredLoginModule2\" usage=\"SUFFICIENT\">\n") + "      <properties>\n") + "        <property name=\"login-property2\">login-value2</property>\n") + "      </properties>\n") + "    </login-module>\n") + "  </member-login-modules>\n") + "  <client-login-modules>\n") + "    <login-module class-name=\"MyOptionalLoginModule\" usage=\"OPTIONAL\">\n") + "      <properties>\n") + "        <property name=\"client-property\">client-value</property>\n") + "      </properties>\n") + "    </login-module>\n") + "    <login-module class-name=\"MyRequiredLoginModule\" usage=\"REQUIRED\">\n") + "      <properties>\n") + "        <property name=\"client-property2\">client-value2</property>\n") + "      </properties>\n") + "    </login-module>\n") + "  </client-login-modules>\n") + "  <client-permission-policy class-name=\"MyPermissionPolicy\">\n") + "    <properties>\n") + "      <property name=\"permission-property\">permission-value</property>\n") + "    </properties>\n") + "  </client-permission-policy>") + "</security>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SecurityConfig securityConfig = config.getSecurityConfig();
        List<SecurityInterceptorConfig> interceptorConfigs = securityConfig.getSecurityInterceptorConfigs();
        Assert.assertEquals(2, interceptorConfigs.size());
        Assert.assertEquals("foo", interceptorConfigs.get(0).className);
        Assert.assertEquals("bar", interceptorConfigs.get(1).className);
        Assert.assertFalse(securityConfig.getClientBlockUnmappedActions());
        // member-credentials-factory
        CredentialsFactoryConfig memberCredentialsConfig = securityConfig.getMemberCredentialsConfig();
        Assert.assertEquals("MyCredentialsFactory", memberCredentialsConfig.getClassName());
        Assert.assertEquals(1, memberCredentialsConfig.getProperties().size());
        Assert.assertEquals("value", memberCredentialsConfig.getProperties().getProperty("property"));
        // member-login-modules
        List<LoginModuleConfig> memberLoginModuleConfigs = securityConfig.getMemberLoginModuleConfigs();
        Assert.assertEquals(2, memberLoginModuleConfigs.size());
        Iterator<LoginModuleConfig> memberLoginIterator = memberLoginModuleConfigs.iterator();
        LoginModuleConfig memberLoginModuleCfg1 = memberLoginIterator.next();
        Assert.assertEquals("MyRequiredLoginModule", memberLoginModuleCfg1.getClassName());
        Assert.assertEquals(REQUIRED, memberLoginModuleCfg1.getUsage());
        Assert.assertEquals(1, memberLoginModuleCfg1.getProperties().size());
        Assert.assertEquals("login-value", memberLoginModuleCfg1.getProperties().getProperty("login-property"));
        LoginModuleConfig memberLoginModuleCfg2 = memberLoginIterator.next();
        Assert.assertEquals("MyRequiredLoginModule2", memberLoginModuleCfg2.getClassName());
        Assert.assertEquals(SUFFICIENT, memberLoginModuleCfg2.getUsage());
        Assert.assertEquals(1, memberLoginModuleCfg2.getProperties().size());
        Assert.assertEquals("login-value2", memberLoginModuleCfg2.getProperties().getProperty("login-property2"));
        // client-login-modules
        List<LoginModuleConfig> clientLoginModuleConfigs = securityConfig.getClientLoginModuleConfigs();
        Assert.assertEquals(2, clientLoginModuleConfigs.size());
        Iterator<LoginModuleConfig> clientLoginIterator = clientLoginModuleConfigs.iterator();
        LoginModuleConfig clientLoginModuleCfg1 = clientLoginIterator.next();
        Assert.assertEquals("MyOptionalLoginModule", clientLoginModuleCfg1.getClassName());
        Assert.assertEquals(OPTIONAL, clientLoginModuleCfg1.getUsage());
        Assert.assertEquals(1, clientLoginModuleCfg1.getProperties().size());
        Assert.assertEquals("client-value", clientLoginModuleCfg1.getProperties().getProperty("client-property"));
        LoginModuleConfig clientLoginModuleCfg2 = clientLoginIterator.next();
        Assert.assertEquals("MyRequiredLoginModule", clientLoginModuleCfg2.getClassName());
        Assert.assertEquals(REQUIRED, clientLoginModuleCfg2.getUsage());
        Assert.assertEquals(1, clientLoginModuleCfg2.getProperties().size());
        Assert.assertEquals("client-value2", clientLoginModuleCfg2.getProperties().getProperty("client-property2"));
        // client-permission-policy
        PermissionPolicyConfig permissionPolicyConfig = securityConfig.getClientPolicyConfig();
        Assert.assertEquals("MyPermissionPolicy", permissionPolicyConfig.getClassName());
        Assert.assertEquals(1, permissionPolicyConfig.getProperties().size());
        Assert.assertEquals("permission-value", permissionPolicyConfig.getProperties().getProperty("permission-property"));
    }

    @Override
    @Test
    public void readAwsConfig() {
        String xml = ((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <group>\n") + "        <name>dev</name>\n") + "        <password>dev-pass</password>\n") + "    </group>\n") + "    <network>\n") + "        <port auto-increment=\"true\">5701</port>\n") + "        <join>\n") + "            <multicast enabled=\"false\">\n") + "                <multicast-group>224.2.2.3</multicast-group>\n") + "                <multicast-port>54327</multicast-port>\n") + "            </multicast>\n") + "            <tcp-ip enabled=\"false\">\n") + "                <interface>127.0.0.1</interface>\n") + "            </tcp-ip>\n") + "            <aws enabled=\"true\" connection-timeout-seconds=\"10\" >\n") + "                <access-key>sample-access-key</access-key>\n") + "                <secret-key>sample-secret-key</secret-key>\n") + "                <iam-role>sample-role</iam-role>\n") + "                <region>sample-region</region>\n") + "                <host-header>sample-header</host-header>\n") + "                <security-group-name>sample-group</security-group-name>\n") + "                <tag-key>sample-tag-key</tag-key>\n") + "                <tag-value>sample-tag-value</tag-value>\n") + "            </aws>\n") + "        </join>\n") + "        <interfaces enabled=\"false\">\n") + "            <interface>10.10.1.*</interface>\n") + "        </interfaces>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        AwsConfig awsConfig = config.getNetworkConfig().getJoin().getAwsConfig();
        Assert.assertTrue(awsConfig.isEnabled());
        AbstractConfigBuilderTest.assertAwsConfig(awsConfig);
    }

    @Override
    @Test
    public void readGcpConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"false\"/>\n") + "            <gcp enabled=\"true\">\n") + "                <use-public-ip>true</use-public-ip>\n") + "                <zones>us-east1-b</zones>\n") + "            </gcp>\n") + "        </join>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        GcpConfig gcpConfig = config.getNetworkConfig().getJoin().getGcpConfig();
        Assert.assertTrue(gcpConfig.isEnabled());
        Assert.assertTrue(gcpConfig.isUsePublicIp());
        Assert.assertEquals("us-east1-b", gcpConfig.getProperty("zones"));
    }

    @Override
    @Test
    public void readAzureConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"false\"/>\n") + "            <azure enabled=\"true\">\n") + "                <client-id>123456789!</client-id>\n") + "                <use-public-ip>true</use-public-ip>\n") + "            </azure>\n") + "        </join>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        AzureConfig azureConfig = config.getNetworkConfig().getJoin().getAzureConfig();
        Assert.assertTrue(azureConfig.isEnabled());
        Assert.assertTrue(azureConfig.isUsePublicIp());
        Assert.assertEquals("123456789!", azureConfig.getProperty("client-id"));
    }

    @Override
    @Test
    public void readKubernetesConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"false\"/>\n") + "            <kubernetes enabled=\"true\">\n") + "                <use-public-ip>true</use-public-ip>\n") + "                <namespace>hazelcast</namespace>\n") + "            </kubernetes>\n") + "        </join>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        KubernetesConfig kubernetesConfig = config.getNetworkConfig().getJoin().getKubernetesConfig();
        Assert.assertTrue(kubernetesConfig.isEnabled());
        Assert.assertTrue(kubernetesConfig.isUsePublicIp());
        Assert.assertEquals("hazelcast", kubernetesConfig.getProperty("namespace"));
    }

    @Override
    @Test
    public void readEurekaConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"false\"/>\n") + "            <eureka enabled=\"true\">\n") + "                <use-public-ip>true</use-public-ip>\n") + "                <namespace>hazelcast</namespace>\n") + "            </eureka>\n") + "        </join>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        EurekaConfig eurekaConfig = config.getNetworkConfig().getJoin().getEurekaConfig();
        Assert.assertTrue(eurekaConfig.isEnabled());
        Assert.assertTrue(eurekaConfig.isUsePublicIp());
        Assert.assertEquals("hazelcast", eurekaConfig.getProperty("namespace"));
    }

    @Override
    @Test
    public void readDiscoveryConfig() {
        String xml = ((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <group>\n") + "        <name>dev</name>\n") + "        <password>dev-pass</password>\n") + "    </group>\n") + "    <network>\n") + "        <port auto-increment=\"true\">5701</port>\n") + "        <join>\n") + "            <multicast enabled=\"false\">\n") + "                <multicast-group>224.2.2.3</multicast-group>\n") + "                <multicast-port>54327</multicast-port>\n") + "            </multicast>\n") + "            <tcp-ip enabled=\"false\">\n") + "                <interface>127.0.0.1</interface>\n") + "            </tcp-ip>\n") + "            <discovery-strategies>\n") + "                <node-filter class=\"DummyFilterClass\" />\n") + "                <discovery-strategy class=\"DummyDiscoveryStrategy1\" enabled=\"true\">\n") + "                    <properties>\n") + "                        <property name=\"key-string\">foo</property>\n") + "                        <property name=\"key-int\">123</property>\n") + "                        <property name=\"key-boolean\">true</property>\n") + "                    </properties>\n") + "                </discovery-strategy>\n") + "            </discovery-strategies>\n") + "        </join>\n") + "        <interfaces enabled=\"false\">\n") + "            <interface>10.10.1.*</interface>\n") + "        </interfaces>\n") + "    </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        DiscoveryConfig discoveryConfig = config.getNetworkConfig().getJoin().getDiscoveryConfig();
        Assert.assertTrue(discoveryConfig.isEnabled());
        assertDiscoveryConfig(discoveryConfig);
    }

    @Override
    @Test
    public void testSSLConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <ssl enabled=\"true\">\r\n") + "          <factory-class-name>\r\n") + "              com.hazelcast.nio.ssl.BasicSSLContextFactory\r\n") + "          </factory-class-name>\r\n") + "          <properties>\r\n") + "            <property name=\"protocol\">TLS</property>\r\n") + "          </properties>\r\n") + "        </ssl>\r\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SSLConfig sslConfig = config.getNetworkConfig().getSSLConfig();
        Assert.assertTrue(sslConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.nio.ssl.BasicSSLContextFactory", sslConfig.getFactoryClassName());
        Assert.assertEquals(1, sslConfig.getProperties().size());
        Assert.assertEquals("TLS", sslConfig.getProperties().get("protocol"));
    }

    @Override
    @Test
    public void testSymmetricEncryptionConfig() {
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "      <symmetric-encryption enabled=\"true\">\n") + "        <algorithm>AES</algorithm>\n") + "        <salt>some-salt</salt>\n") + "        <password>some-pass</password>\n") + "        <iteration-count>7531</iteration-count>\n") + "      </symmetric-encryption>") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SymmetricEncryptionConfig symmetricEncryptionConfig = config.getNetworkConfig().getSymmetricEncryptionConfig();
        Assert.assertTrue(symmetricEncryptionConfig.isEnabled());
        Assert.assertEquals("AES", symmetricEncryptionConfig.getAlgorithm());
        Assert.assertEquals("some-salt", symmetricEncryptionConfig.getSalt());
        Assert.assertEquals("some-pass", symmetricEncryptionConfig.getPassword());
        Assert.assertEquals(7531, symmetricEncryptionConfig.getIterationCount());
    }

    @Override
    @Test
    public void readPortCount() {
        // check when it is explicitly set
        Config config = buildConfig((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <port port-count=\"200\">5702</port>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
        Assert.assertEquals(200, config.getNetworkConfig().getPortCount());
        Assert.assertEquals(5702, config.getNetworkConfig().getPort());
        // check if the default is passed in correctly
        config = buildConfig((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <port>5701</port>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
        Assert.assertEquals(100, config.getNetworkConfig().getPortCount());
    }

    @Override
    @Test
    public void readPortAutoIncrement() {
        // explicitly set
        Config config = buildConfig((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <port auto-increment=\"false\">5701</port>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
        Assert.assertFalse(config.getNetworkConfig().isPortAutoIncrement());
        // check if the default is picked up correctly
        config = buildConfig((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <port>5701</port>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
        Assert.assertTrue(config.getNetworkConfig().isPortAutoIncrement());
    }

    @Override
    @Test
    public void networkReuseAddress() {
        Config config = buildConfig((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <reuse-address>true</reuse-address>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
        Assert.assertTrue(config.getNetworkConfig().isReuseAddress());
    }

    @Override
    @Test
    public void readSemaphoreConfig() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <semaphore name=\"default\">\n") + "        <initial-permits>1</initial-permits>\n") + "    </semaphore>") + "    <semaphore name=\"custom\">\n") + "        <initial-permits>10</initial-permits>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "    </semaphore>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SemaphoreConfig defaultConfig = config.getSemaphoreConfig("default");
        SemaphoreConfig customConfig = config.getSemaphoreConfig("custom");
        Assert.assertEquals(1, defaultConfig.getInitialPermits());
        Assert.assertEquals(10, customConfig.getInitialPermits());
        Assert.assertEquals("customQuorumRule", customConfig.getQuorumName());
    }

    @Override
    @Test
    public void readQueueConfig() {
        String xml = (((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <queue name=\"custom\">") + "        <statistics-enabled>false</statistics-enabled>") + "        <max-size>100</max-size>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <empty-queue-ttl>1</empty-queue-ttl>") + "        <item-listeners>") + "            <item-listener include-value=\"false\">com.hazelcast.examples.ItemListener</item-listener>") + "        </item-listeners>") + "        <queue-store enabled=\"false\">") + "            <class-name>com.hazelcast.QueueStoreImpl</class-name>") + "            <properties>") + "                <property name=\"binary\">false</property>") + "                <property name=\"memory-limit\">1000</property>") + "                <property name=\"bulk-load\">500</property>") + "            </properties>") + "        </queue-store>") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <merge-policy batch-size=\"23\">CustomMergePolicy</merge-policy>") + "    </queue>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QueueConfig queueConfig = config.getQueueConfig("custom");
        Assert.assertFalse(queueConfig.isStatisticsEnabled());
        Assert.assertEquals(100, queueConfig.getMaxSize());
        Assert.assertEquals(2, queueConfig.getBackupCount());
        Assert.assertEquals(1, queueConfig.getAsyncBackupCount());
        Assert.assertEquals(1, queueConfig.getEmptyQueueTtl());
        MergePolicyConfig mergePolicyConfig = queueConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(23, mergePolicyConfig.getBatchSize());
        Assert.assertEquals(1, queueConfig.getItemListenerConfigs().size());
        ItemListenerConfig listenerConfig = queueConfig.getItemListenerConfigs().iterator().next();
        Assert.assertEquals("com.hazelcast.examples.ItemListener", listenerConfig.getClassName());
        Assert.assertFalse(listenerConfig.isIncludeValue());
        QueueStoreConfig storeConfig = queueConfig.getQueueStoreConfig();
        Assert.assertNotNull(storeConfig);
        Assert.assertFalse(storeConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.QueueStoreImpl", storeConfig.getClassName());
        Properties storeConfigProperties = storeConfig.getProperties();
        Assert.assertEquals(3, storeConfigProperties.size());
        Assert.assertEquals("500", storeConfigProperties.getProperty("bulk-load"));
        Assert.assertEquals("1000", storeConfigProperties.getProperty("memory-limit"));
        Assert.assertEquals("false", storeConfigProperties.getProperty("binary"));
        Assert.assertEquals("customQuorumRule", queueConfig.getQuorumName());
    }

    @Override
    @Test
    public void readListConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <list name=\"myList\">") + "        <statistics-enabled>false</statistics-enabled>") + "        <max-size>100</max-size>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <item-listeners>") + "            <item-listener include-value=\"false\">com.hazelcast.examples.ItemListener</item-listener>") + "        </item-listeners>") + "        <merge-policy batch-size=\"4223\">PassThroughMergePolicy</merge-policy>") + "    </list>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ListConfig listConfig = config.getListConfig("myList");
        Assert.assertEquals("myList", listConfig.getName());
        Assert.assertFalse(listConfig.isStatisticsEnabled());
        Assert.assertEquals(100, listConfig.getMaxSize());
        Assert.assertEquals(2, listConfig.getBackupCount());
        Assert.assertEquals(1, listConfig.getAsyncBackupCount());
        Assert.assertEquals(1, listConfig.getItemListenerConfigs().size());
        ItemListenerConfig listenerConfig = listConfig.getItemListenerConfigs().iterator().next();
        Assert.assertEquals("com.hazelcast.examples.ItemListener", listenerConfig.getClassName());
        Assert.assertFalse(listenerConfig.isIncludeValue());
        MergePolicyConfig mergePolicyConfig = listConfig.getMergePolicyConfig();
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(4223, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void readSetConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <set name=\"mySet\">") + "        <statistics-enabled>false</statistics-enabled>") + "        <max-size>100</max-size>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <item-listeners>") + "            <item-listener>com.hazelcast.examples.ItemListener</item-listener>") + "        </item-listeners>") + "        <merge-policy batch-size=\"4223\">PassThroughMergePolicy</merge-policy>") + "    </set>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SetConfig setConfig = config.getSetConfig("mySet");
        Assert.assertEquals("mySet", setConfig.getName());
        Assert.assertFalse(setConfig.isStatisticsEnabled());
        Assert.assertEquals(100, setConfig.getMaxSize());
        Assert.assertEquals(2, setConfig.getBackupCount());
        Assert.assertEquals(1, setConfig.getAsyncBackupCount());
        Assert.assertEquals(1, setConfig.getItemListenerConfigs().size());
        ItemListenerConfig listenerConfig = setConfig.getItemListenerConfigs().iterator().next();
        Assert.assertEquals("com.hazelcast.examples.ItemListener", listenerConfig.getClassName());
        MergePolicyConfig mergePolicyConfig = setConfig.getMergePolicyConfig();
        Assert.assertEquals("PassThroughMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(4223, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void readLockConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <lock name=\"default\">") + "        <quorum-ref>quorumRuleWithThreeNodes</quorum-ref>") + "  </lock>") + "  <lock name=\"custom\">") + "       <quorum-ref>customQuorumRule</quorum-ref>") + "  </lock>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        LockConfig defaultConfig = config.getLockConfig("default");
        LockConfig customConfig = config.getLockConfig("custom");
        Assert.assertEquals("quorumRuleWithThreeNodes", defaultConfig.getQuorumName());
        Assert.assertEquals("customQuorumRule", customConfig.getQuorumName());
    }

    @Override
    @Test
    public void readReliableTopic() {
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <reliable-topic name=\"custom\">") + "           <read-batch-size>35</read-batch-size>") + "           <statistics-enabled>false</statistics-enabled>") + "           <topic-overload-policy>DISCARD_OLDEST</topic-overload-policy>") + "           <message-listeners>") + "               <message-listener>MessageListenerImpl</message-listener>") + "           </message-listeners>") + "    </reliable-topic>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ReliableTopicConfig topicConfig = config.getReliableTopicConfig("custom");
        Assert.assertEquals(35, topicConfig.getReadBatchSize());
        Assert.assertFalse(topicConfig.isStatisticsEnabled());
        Assert.assertEquals(DISCARD_OLDEST, topicConfig.getTopicOverloadPolicy());
        // checking listener configuration
        Assert.assertEquals(1, topicConfig.getMessageListenerConfigs().size());
        ListenerConfig listenerConfig = topicConfig.getMessageListenerConfigs().get(0);
        Assert.assertEquals("MessageListenerImpl", listenerConfig.getClassName());
        Assert.assertNull(listenerConfig.getImplementation());
    }

    @Override
    @Test
    public void readRingbuffer() {
        String xml = ((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <ringbuffer name=\"custom\">") + "        <capacity>10</capacity>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <time-to-live-seconds>9</time-to-live-seconds>") + "        <in-memory-format>OBJECT</in-memory-format>") + "        <ringbuffer-store enabled=\"false\">") + "            <class-name>com.hazelcast.RingbufferStoreImpl</class-name>") + "            <properties>") + "                <property name=\"store-path\">.//tmp//bufferstore</property>") + "            </properties>") + "        </ringbuffer-store>") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <merge-policy batch-size=\"2342\">CustomMergePolicy</merge-policy>") + "    </ringbuffer>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        RingbufferConfig ringbufferConfig = config.getRingbufferConfig("custom");
        Assert.assertEquals(10, ringbufferConfig.getCapacity());
        Assert.assertEquals(2, ringbufferConfig.getBackupCount());
        Assert.assertEquals(1, ringbufferConfig.getAsyncBackupCount());
        Assert.assertEquals(9, ringbufferConfig.getTimeToLiveSeconds());
        Assert.assertEquals(OBJECT, ringbufferConfig.getInMemoryFormat());
        RingbufferStoreConfig ringbufferStoreConfig = ringbufferConfig.getRingbufferStoreConfig();
        Assert.assertFalse(ringbufferStoreConfig.isEnabled());
        Assert.assertEquals("com.hazelcast.RingbufferStoreImpl", ringbufferStoreConfig.getClassName());
        Properties ringbufferStoreProperties = ringbufferStoreConfig.getProperties();
        Assert.assertEquals(".//tmp//bufferstore", ringbufferStoreProperties.get("store-path"));
        Assert.assertEquals("customQuorumRule", ringbufferConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = ringbufferConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void readAtomicLong() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <atomic-long name=\"custom\">") + "        <merge-policy batch-size=\"23\">CustomMergePolicy</merge-policy>") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "    </atomic-long>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        AtomicLongConfig atomicLongConfig = config.getAtomicLongConfig("custom");
        Assert.assertEquals("custom", atomicLongConfig.getName());
        Assert.assertEquals("customQuorumRule", atomicLongConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = atomicLongConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(23, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void readAtomicReference() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <atomic-reference name=\"custom\">") + "        <merge-policy batch-size=\"23\">CustomMergePolicy</merge-policy>") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "    </atomic-reference>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        AtomicReferenceConfig atomicReferenceConfig = config.getAtomicReferenceConfig("custom");
        Assert.assertEquals("custom", atomicReferenceConfig.getName());
        Assert.assertEquals("customQuorumRule", atomicReferenceConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = atomicReferenceConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(23, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void readCountDownLatch() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <count-down-latch name=\"custom\">") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "    </count-down-latch>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        CountDownLatchConfig countDownLatchConfig = config.getCountDownLatchConfig("custom");
        Assert.assertEquals("custom", countDownLatchConfig.getName());
        Assert.assertEquals("customQuorumRule", countDownLatchConfig.getQuorumName());
    }

    @Override
    @Test
    public void testCaseInsensitivityOfSettings() {
        String xml = ((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"testCaseInsensitivity\">") + "    <in-memory-format>BINARY</in-memory-format>") + "    <backup-count>1</backup-count>") + "    <async-backup-count>0</async-backup-count>") + "    <time-to-live-seconds>0</time-to-live-seconds>") + "    <max-idle-seconds>0</max-idle-seconds>    ") + "    <eviction-policy>NONE</eviction-policy>  ") + "    <max-size policy=\"per_partition\">0</max-size>") + "    <eviction-percentage>25</eviction-percentage>") + "    <merge-policy batch-size=\"2342\">CustomMergePolicy</merge-policy>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("testCaseInsensitivity");
        Assert.assertEquals(BINARY, mapConfig.getInMemoryFormat());
        Assert.assertEquals(NONE, mapConfig.getEvictionPolicy());
        Assert.assertEquals(PER_PARTITION, mapConfig.getMaxSizeConfig().getMaxSizePolicy());
        MergePolicyConfig mergePolicyConfig = mapConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void testManagementCenterConfig() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center enabled=\"true\" scripting-enabled=\'false\'>") + "someUrl") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertTrue(manCenterCfg.isEnabled());
        Assert.assertFalse(manCenterCfg.isScriptingEnabled());
        Assert.assertEquals("someUrl", manCenterCfg.getUrl());
    }

    @Override
    @Test
    public void testManagementCenterConfigComplex() {
        String xml = ((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center enabled=\"true\">") + "<url>wowUrl</url>") + "<mutual-auth enabled=\"true\">") + "<properties>") + "<property name=\"keyStore\">/tmp/foo_keystore</property>") + "<property name=\"trustStore\">/tmp/foo_truststore</property>") + "</properties>") + "</mutual-auth>") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertTrue(manCenterCfg.isEnabled());
        Assert.assertEquals("wowUrl", manCenterCfg.getUrl());
        Assert.assertTrue(manCenterCfg.getMutualAuthConfig().isEnabled());
        Assert.assertEquals("/tmp/foo_keystore", manCenterCfg.getMutualAuthConfig().getProperty("keyStore"));
        Assert.assertEquals("/tmp/foo_truststore", manCenterCfg.getMutualAuthConfig().getProperty("trustStore"));
    }

    @Override
    @Test
    public void testNullManagementCenterConfig() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center>") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertFalse(manCenterCfg.isEnabled());
        Assert.assertNull(manCenterCfg.getUrl());
    }

    @Override
    @Test
    public void testEmptyManagementCenterConfig() {
        String xml = (XMLConfigBuilderTest.HAZELCAST_START_TAG) + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertFalse(manCenterCfg.isEnabled());
        Assert.assertNull(manCenterCfg.getUrl());
    }

    @Override
    @Test
    public void testNotEnabledManagementCenterConfig() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center enabled=\"false\">") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertFalse(manCenterCfg.isEnabled());
        Assert.assertNull(manCenterCfg.getUrl());
    }

    @Override
    @Test
    public void testNotEnabledWithURLManagementCenterConfig() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center enabled=\"false\">") + "http://localhost:8080/mancenter") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertFalse(manCenterCfg.isEnabled());
        Assert.assertEquals("http://localhost:8080/mancenter", manCenterCfg.getUrl());
    }

    @Override
    @Test
    public void testManagementCenterConfigComplexDisabledMutualAuth() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<management-center enabled=\"true\">") + "<url>wowUrl</url>") + "<mutual-auth enabled=\"false\">") + "</mutual-auth>") + "</management-center>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ManagementCenterConfig manCenterCfg = config.getManagementCenterConfig();
        Assert.assertTrue(manCenterCfg.isEnabled());
        Assert.assertEquals("wowUrl", manCenterCfg.getUrl());
        Assert.assertFalse(manCenterCfg.getMutualAuthConfig().isEnabled());
    }

    @Override
    @Test
    public void testMapStoreInitialModeLazy() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<map-store enabled=\"true\" initial-mode=\"LAZY\"></map-store>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapStoreConfig mapStoreConfig = config.getMapConfig("mymap").getMapStoreConfig();
        Assert.assertTrue(mapStoreConfig.isEnabled());
        Assert.assertEquals(LAZY, mapStoreConfig.getInitialLoadMode());
    }

    @Override
    @Test
    public void testMapConfig_minEvictionCheckMillis() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<min-eviction-check-millis>123456789</min-eviction-check-millis>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(123456789L, mapConfig.getMinEvictionCheckMillis());
    }

    @Override
    @Test
    public void testMapConfig_minEvictionCheckMillis_defaultValue() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(DEFAULT_MIN_EVICTION_CHECK_MILLIS, mapConfig.getMinEvictionCheckMillis());
    }

    @Override
    @Test
    public void testMapConfig_metadataPolicy() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<metadata-policy>CREATE_ON_UPDATE</metadata-policy>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(CREATE_ON_UPDATE, mapConfig.getMetadataPolicy());
    }

    @Override
    @Test
    public void testMapConfig_metadataPolicy_defaultValue() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(CREATE_ON_UPDATE, mapConfig.getMetadataPolicy());
    }

    @Override
    @Test
    public void testMapConfig_evictions() {
        String xml = (((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"lruMap\">") + "        <eviction-policy>LRU</eviction-policy>\n") + "    </map>\n") + "<map name=\"lfuMap\">") + "        <eviction-policy>LFU</eviction-policy>\n") + "    </map>\n") + "<map name=\"noneMap\">") + "        <eviction-policy>NONE</eviction-policy>\n") + "    </map>\n") + "<map name=\"randomMap\">") + "        <eviction-policy>RANDOM</eviction-policy>\n") + "    </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertEquals(LRU, config.getMapConfig("lruMap").getEvictionPolicy());
        Assert.assertEquals(LFU, config.getMapConfig("lfuMap").getEvictionPolicy());
        Assert.assertEquals(NONE, config.getMapConfig("noneMap").getEvictionPolicy());
        Assert.assertEquals(RANDOM, config.getMapConfig("randomMap").getEvictionPolicy());
    }

    @Override
    @Test
    public void testMapConfig_optimizeQueries() {
        String xml1 = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap1\">") + "<optimize-queries>true</optimize-queries>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config1 = buildConfig(xml1);
        MapConfig mapConfig1 = config1.getMapConfig("mymap1");
        Assert.assertEquals(ALWAYS, mapConfig1.getCacheDeserializedValues());
        String xml2 = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap2\">") + "<optimize-queries>false</optimize-queries>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config2 = buildConfig(xml2);
        MapConfig mapConfig2 = config2.getMapConfig("mymap2");
        Assert.assertEquals(INDEX_ONLY, mapConfig2.getCacheDeserializedValues());
    }

    @Override
    @Test
    public void testMapConfig_cacheValueConfig_defaultValue() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(INDEX_ONLY, mapConfig.getCacheDeserializedValues());
    }

    @Override
    @Test
    public void testMapConfig_cacheValueConfig_never() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<cache-deserialized-values>NEVER</cache-deserialized-values>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(NEVER, mapConfig.getCacheDeserializedValues());
    }

    @Override
    @Test
    public void testMapConfig_cacheValueConfig_always() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<cache-deserialized-values>ALWAYS</cache-deserialized-values>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(ALWAYS, mapConfig.getCacheDeserializedValues());
    }

    @Override
    @Test
    public void testMapConfig_cacheValueConfig_indexOnly() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<cache-deserialized-values>INDEX-ONLY</cache-deserialized-values>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("mymap");
        Assert.assertEquals(INDEX_ONLY, mapConfig.getCacheDeserializedValues());
    }

    @Override
    @Test
    public void testMapStoreInitialModeEager() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<map-store enabled=\"true\" initial-mode=\"EAGER\"></map-store>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapStoreConfig mapStoreConfig = config.getMapConfig("mymap").getMapStoreConfig();
        Assert.assertTrue(mapStoreConfig.isEnabled());
        Assert.assertEquals(EAGER, mapStoreConfig.getInitialLoadMode());
    }

    @Override
    @Test
    public void testMapStoreWriteBatchSize() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<map-store >") + "<write-batch-size>23</write-batch-size>") + "</map-store>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapStoreConfig mapStoreConfig = config.getMapConfig("mymap").getMapStoreConfig();
        Assert.assertEquals(23, mapStoreConfig.getWriteBatchSize());
    }

    @Override
    @Test
    public void testMapStoreConfig_writeCoalescing_whenDefault() {
        MapStoreConfig mapStoreConfig = getWriteCoalescingMapStoreConfig(DEFAULT_WRITE_COALESCING, true);
        Assert.assertTrue(mapStoreConfig.isWriteCoalescing());
    }

    @Override
    @Test
    public void testMapStoreConfig_writeCoalescing_whenSetFalse() {
        MapStoreConfig mapStoreConfig = getWriteCoalescingMapStoreConfig(false, false);
        Assert.assertFalse(mapStoreConfig.isWriteCoalescing());
    }

    @Override
    @Test
    public void testMapStoreConfig_writeCoalescing_whenSetTrue() {
        MapStoreConfig mapStoreConfig = getWriteCoalescingMapStoreConfig(true, false);
        Assert.assertTrue(mapStoreConfig.isWriteCoalescing());
    }

    @Override
    @Test
    public void testNearCacheInMemoryFormat() {
        String mapName = "testMapNearCacheInMemoryFormat";
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"") + mapName) + "\">\n") + "    <near-cache>\n") + "      <in-memory-format>OBJECT</in-memory-format>\n") + "    </near-cache>\n") + "  </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig(mapName);
        NearCacheConfig ncConfig = mapConfig.getNearCacheConfig();
        Assert.assertEquals(OBJECT, ncConfig.getInMemoryFormat());
    }

    @Override
    @Test
    public void testNearCacheInMemoryFormatNative_withKeysByReference() {
        String mapName = "testMapNearCacheInMemoryFormatNative";
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"") + mapName) + "\">\n") + "    <near-cache>\n") + "      <in-memory-format>NATIVE</in-memory-format>\n") + "      <serialize-keys>false</serialize-keys>\n") + "    </near-cache>\n") + "  </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig(mapName);
        NearCacheConfig ncConfig = mapConfig.getNearCacheConfig();
        Assert.assertEquals(NATIVE, ncConfig.getInMemoryFormat());
        Assert.assertTrue(ncConfig.isSerializeKeys());
    }

    @Override
    @Test
    public void testNearCacheEvictionPolicy() {
        String xml = (((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"lfuNearCache\">") + "    <near-cache>") + "      <eviction eviction-policy=\"LFU\"/>") + "    </near-cache>") + "  </map>") + "  <map name=\"lruNearCache\">") + "    <near-cache>") + "      <eviction eviction-policy=\"LRU\"/>") + "    </near-cache>") + "  </map>") + "  <map name=\"noneNearCache\">") + "    <near-cache>") + "      <eviction eviction-policy=\"NONE\"/>") + "    </near-cache>") + "  </map>") + "  <map name=\"randomNearCache\">") + "    <near-cache>") + "      <eviction eviction-policy=\"RANDOM\"/>") + "    </near-cache>") + "  </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertEquals(LFU, getNearCacheEvictionPolicy("lfuNearCache", config));
        Assert.assertEquals(LRU, getNearCacheEvictionPolicy("lruNearCache", config));
        Assert.assertEquals(NONE, getNearCacheEvictionPolicy("noneNearCache", config));
        Assert.assertEquals(RANDOM, getNearCacheEvictionPolicy("randomNearCache", config));
    }

    @Override
    @Test
    public void testPartitionGroupZoneAware() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<partition-group enabled=\"true\" group-type=\"ZONE_AWARE\" />") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        PartitionGroupConfig partitionGroupConfig = config.getPartitionGroupConfig();
        Assert.assertTrue(partitionGroupConfig.isEnabled());
        Assert.assertEquals(ZONE_AWARE, partitionGroupConfig.getGroupType());
    }

    @Override
    @Test
    public void testPartitionGroupSPI() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<partition-group enabled=\"true\" group-type=\"SPI\" />") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertEquals(SPI, config.getPartitionGroupConfig().getGroupType());
    }

    @Override
    @Test
    public void testPartitionGroupMemberGroups() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<partition-group enabled=\"true\" group-type=\"SPI\">") + "  <member-group>") + "    <interface>10.10.1.1</interface>") + "    <interface>10.10.1.2</interface>") + "  </member-group>") + "  <member-group>") + "    <interface>10.10.1.3</interface>") + "    <interface>10.10.1.4</interface>") + "  </member-group>") + "</partition-group>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Collection<MemberGroupConfig> memberGroupConfigs = config.getPartitionGroupConfig().getMemberGroupConfigs();
        Assert.assertEquals(2, memberGroupConfigs.size());
        Iterator<MemberGroupConfig> iterator = memberGroupConfigs.iterator();
        MemberGroupConfig memberGroupConfig1 = iterator.next();
        Assert.assertEquals(2, memberGroupConfig1.getInterfaces().size());
        Assert.assertTrue(memberGroupConfig1.getInterfaces().contains("10.10.1.1"));
        Assert.assertTrue(memberGroupConfig1.getInterfaces().contains("10.10.1.2"));
        MemberGroupConfig memberGroupConfig2 = iterator.next();
        Assert.assertEquals(2, memberGroupConfig2.getInterfaces().size());
        Assert.assertTrue(memberGroupConfig2.getInterfaces().contains("10.10.1.3"));
        Assert.assertTrue(memberGroupConfig2.getInterfaces().contains("10.10.1.4"));
    }

    @Override
    @Test
    public void testNearCacheFullConfig() {
        String mapName = "testNearCacheFullConfig";
        String xml = ((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"") + mapName) + "\">\n") + "    <near-cache name=\"test\">\n") + "      <in-memory-format>OBJECT</in-memory-format>\n") + "      <serialize-keys>false</serialize-keys>\n") + "      <max-size>1234</max-size>\n") + "      <time-to-live-seconds>77</time-to-live-seconds>\n") + "      <max-idle-seconds>92</max-idle-seconds>\n") + "      <eviction-policy>LFU</eviction-policy>\n") + "      <invalidate-on-change>false</invalidate-on-change>\n") + "      <cache-local-entries>false</cache-local-entries>\n") + "      <eviction eviction-policy=\"LRU\" max-size-policy=\"ENTRY_COUNT\" size=\"3333\"/>\n") + "    </near-cache>\n") + "  </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig(mapName);
        NearCacheConfig nearCacheConfig = mapConfig.getNearCacheConfig();
        Assert.assertEquals(OBJECT, nearCacheConfig.getInMemoryFormat());
        Assert.assertEquals(1234, nearCacheConfig.getMaxSize());
        Assert.assertEquals(77, nearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(92, nearCacheConfig.getMaxIdleSeconds());
        Assert.assertEquals("LFU", nearCacheConfig.getEvictionPolicy());
        Assert.assertFalse(nearCacheConfig.isInvalidateOnChange());
        Assert.assertFalse(nearCacheConfig.isCacheLocalEntries());
        Assert.assertEquals(EvictionPolicy.LRU, nearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(MaxSizePolicy.ENTRY_COUNT, nearCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(3333, nearCacheConfig.getEvictionConfig().getSize());
        Assert.assertEquals("test", nearCacheConfig.getName());
    }

    @Override
    @Test
    public void testMapWanReplicationRef() {
        String mapName = "testMapWanReplicationRef";
        String refName = "test";
        String mergePolicy = "TestMergePolicy";
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"") + mapName) + "\">\n") + "    <wan-replication-ref name=\"test\">\n") + "      <merge-policy>TestMergePolicy</merge-policy>\n") + "      <filters>\n") + "        <filter-impl>com.example.SampleFilter</filter-impl>\n") + "      </filters>\n") + "    </wan-replication-ref>\n") + "  </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig(mapName);
        WanReplicationRef wanRef = mapConfig.getWanReplicationRef();
        Assert.assertEquals(refName, wanRef.getName());
        Assert.assertEquals(mergePolicy, wanRef.getMergePolicy());
        Assert.assertTrue(wanRef.isRepublishingEnabled());
        Assert.assertEquals(1, wanRef.getFilters().size());
        Assert.assertEquals("com.example.SampleFilter", wanRef.getFilters().get(0));
    }

    @Override
    @Test
    public void testWanReplicationConfig() {
        String configName = "test";
        String xml = (((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <wan-replication name=\"") + configName) + "\">\n") + "        <wan-publisher group-name=\"nyc\" publisher-id=\"publisherId\">\n") + "            <class-name>PublisherClassName</class-name>\n") + "            <queue-capacity>15000</queue-capacity>\n") + "            <queue-full-behavior>DISCARD_AFTER_MUTATION</queue-full-behavior>\n") + "            <initial-publisher-state>STOPPED</initial-publisher-state>\n") + "            <properties>\n") + "                <property name=\"propName1\">propValue1</property>\n") + "            </properties>\n") + "            <endpoint>nyc-endpoint</endpoint>\n") + "        </wan-publisher>\n") + "        <wan-consumer>\n") + "            <class-name>ConsumerClassName</class-name>\n") + "            <properties>\n") + "                <property name=\"propName1\">propValue1</property>\n") + "            </properties>\n") + "        </wan-consumer>\n") + "    </wan-replication>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        WanReplicationConfig wanReplicationConfig = config.getWanReplicationConfig(configName);
        Assert.assertEquals(configName, wanReplicationConfig.getName());
        WanConsumerConfig consumerConfig = wanReplicationConfig.getWanConsumerConfig();
        Assert.assertNotNull(consumerConfig);
        Assert.assertEquals("ConsumerClassName", consumerConfig.getClassName());
        Map<String, Comparable> properties = consumerConfig.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("propValue1", properties.get("propName1"));
        List<WanPublisherConfig> publishers = wanReplicationConfig.getWanPublisherConfigs();
        Assert.assertNotNull(publishers);
        Assert.assertEquals(1, publishers.size());
        WanPublisherConfig publisherConfig = publishers.get(0);
        Assert.assertEquals("PublisherClassName", publisherConfig.getClassName());
        Assert.assertEquals("nyc", publisherConfig.getGroupName());
        Assert.assertEquals("publisherId", publisherConfig.getPublisherId());
        Assert.assertEquals(15000, publisherConfig.getQueueCapacity());
        Assert.assertEquals(WANQueueFullBehavior.DISCARD_AFTER_MUTATION, publisherConfig.getQueueFullBehavior());
        Assert.assertEquals(STOPPED, publisherConfig.getInitialPublisherState());
        Assert.assertEquals("nyc-endpoint", publisherConfig.getEndpoint());
        properties = publisherConfig.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("propValue1", properties.get("propName1"));
    }

    @Override
    @Test
    public void testDefaultOfPersistWanReplicatedDataIsFalse() {
        String configName = "test";
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <wan-replication name=\"") + configName) + "\">\n") + "        <wan-consumer>\n") + "        </wan-consumer>\n") + "    </wan-replication>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        WanReplicationConfig wanReplicationConfig = config.getWanReplicationConfig(configName);
        WanConsumerConfig consumerConfig = wanReplicationConfig.getWanConsumerConfig();
        Assert.assertFalse(consumerConfig.isPersistWanReplicatedData());
    }

    @Override
    @Test
    public void testWanReplicationSyncConfig() {
        String configName = "test";
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <wan-replication name=\"") + configName) + "\">\n") + "        <wan-publisher group-name=\"nyc\">\n") + "            <class-name>PublisherClassName</class-name>\n") + "            <wan-sync>\n") + "                <consistency-check-strategy>MERKLE_TREES</consistency-check-strategy>\n") + "            </wan-sync>\n") + "        </wan-publisher>\n") + "    </wan-replication>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        WanReplicationConfig wanReplicationConfig = config.getWanReplicationConfig(configName);
        Assert.assertEquals(configName, wanReplicationConfig.getName());
        List<WanPublisherConfig> publishers = wanReplicationConfig.getWanPublisherConfigs();
        Assert.assertNotNull(publishers);
        Assert.assertEquals(1, publishers.size());
        WanPublisherConfig publisherConfig = publishers.get(0);
        Assert.assertEquals(MERKLE_TREES, publisherConfig.getWanSyncConfig().getConsistencyCheckStrategy());
    }

    @Override
    @Test
    public void testMapEventJournalConfig() {
        String journalName = "mapName";
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<event-journal enabled=\"false\">\n") + "    <mapName>") + journalName) + "</mapName>\n") + "    <capacity>120</capacity>\n") + "    <time-to-live-seconds>20</time-to-live-seconds>\n") + "</event-journal>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        EventJournalConfig journalConfig = config.getMapEventJournalConfig(journalName);
        Assert.assertFalse(journalConfig.isEnabled());
        Assert.assertEquals(120, journalConfig.getCapacity());
        Assert.assertEquals(20, journalConfig.getTimeToLiveSeconds());
    }

    @Override
    @Test
    public void testMapMerkleTreeConfig() {
        String mapName = "mapName";
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<merkle-tree enabled=\"true\">\n") + "    <mapName>") + mapName) + "</mapName>\n") + "    <depth>20</depth>\n") + "</merkle-tree>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MerkleTreeConfig treeConfig = config.getMapMerkleTreeConfig(mapName);
        Assert.assertTrue(treeConfig.isEnabled());
        Assert.assertEquals(20, treeConfig.getDepth());
    }

    @Override
    @Test
    public void testCacheEventJournalConfig() {
        String journalName = "cacheName";
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<event-journal enabled=\"true\">\n") + "    <cacheName>") + journalName) + "</cacheName>\n") + "    <capacity>120</capacity>\n") + "    <time-to-live-seconds>20</time-to-live-seconds>\n") + "</event-journal>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        EventJournalConfig journalConfig = config.getCacheEventJournalConfig(journalName);
        Assert.assertTrue(journalConfig.isEnabled());
        Assert.assertEquals(120, journalConfig.getCapacity());
        Assert.assertEquals(20, journalConfig.getTimeToLiveSeconds());
    }

    @Override
    @Test
    public void testFlakeIdGeneratorConfig() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<flake-id-generator name='gen'>") + "  <prefetch-count>3</prefetch-count>") + "  <prefetch-validity-millis>10</prefetch-validity-millis>") + "  <id-offset>20</id-offset>") + "  <node-id-offset>30</node-id-offset>") + "  <statistics-enabled>false</statistics-enabled>") + "</flake-id-generator>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        FlakeIdGeneratorConfig fConfig = config.findFlakeIdGeneratorConfig("gen");
        Assert.assertEquals("gen", fConfig.getName());
        Assert.assertEquals(3, fConfig.getPrefetchCount());
        Assert.assertEquals(10L, fConfig.getPrefetchValidityMillis());
        Assert.assertEquals(20L, fConfig.getIdOffset());
        Assert.assertEquals(30L, fConfig.getNodeIdOffset());
        Assert.assertFalse(fConfig.isStatisticsEnabled());
    }

    @Override
    @Test
    public void testParseExceptionIsNotSwallowed() {
        String invalidXml = (XMLConfigBuilderTest.HAZELCAST_START_TAG) + "</hazelcast";
        expected.expect(InvalidConfigurationException.class);
        buildConfig(invalidXml);
    }

    @Override
    @Test
    public void setMapStoreConfigImplementationTest() {
        String mapName = "mapStoreImpObjTest";
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"") + mapName) + "\">\n") + "    <map-store enabled=\"true\">\n") + "        <class-name>com.hazelcast.config.helpers.DummyMapStore</class-name>\n") + "        <write-delay-seconds>5</write-delay-seconds>\n") + "    </map-store>\n") + "</map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        HazelcastInstance hz = createHazelcastInstance(config);
        IMap<String, String> map = hz.getMap(mapName);
        // MapStore is not instantiated until the MapContainer is created lazily
        map.put("sample", "data");
        MapConfig mapConfig = hz.getConfig().getMapConfig(mapName);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        Object o = mapStoreConfig.getImplementation();
        Assert.assertNotNull(o);
        Assert.assertTrue((o instanceof DummyMapStore));
    }

    @Override
    @Test
    public void testMapPartitionLostListenerConfig() {
        String mapName = "map1";
        String listenerName = "DummyMapPartitionLostListenerImpl";
        String xml = createMapPartitionLostListenerConfiguredXml(mapName, listenerName);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("map1");
        assertMapPartitionLostListener(listenerName, mapConfig);
    }

    @Override
    @Test
    public void testMapPartitionLostListenerConfigReadOnly() {
        String mapName = "map1";
        String listenerName = "DummyMapPartitionLostListenerImpl";
        String xml = createMapPartitionLostListenerConfiguredXml(mapName, listenerName);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.findMapConfig("map1");
        assertMapPartitionLostListener(listenerName, mapConfig);
    }

    @Override
    @Test
    public void testCachePartitionLostListenerConfig() {
        String cacheName = "cache1";
        String listenerName = "DummyCachePartitionLostListenerImpl";
        String xml = createCachePartitionLostListenerConfiguredXml(cacheName, listenerName);
        Config config = buildConfig(xml);
        CacheSimpleConfig cacheConfig = config.getCacheConfig("cache1");
        assertCachePartitionLostListener(listenerName, cacheConfig);
    }

    @Override
    @Test
    public void testCachePartitionLostListenerConfigReadOnly() {
        String cacheName = "cache1";
        String listenerName = "DummyCachePartitionLostListenerImpl";
        String xml = createCachePartitionLostListenerConfiguredXml(cacheName, listenerName);
        Config config = buildConfig(xml);
        CacheSimpleConfig cacheConfig = config.findCacheConfig("cache1");
        assertCachePartitionLostListener(listenerName, cacheConfig);
    }

    @Override
    @Test
    public void readMulticastConfig() {
        String xml = (((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <network>\n") + "        <join>\n") + "            <multicast enabled=\"false\" loopbackModeEnabled=\"true\">\n") + "                <multicast-group>224.2.2.4</multicast-group>\n") + "                <multicast-port>65438</multicast-port>\n") + "                <multicast-timeout-seconds>4</multicast-timeout-seconds>\n") + "                <multicast-time-to-live>42</multicast-time-to-live>\n") + "                <trusted-interfaces>\n") + "                  <interface>127.0.0.1</interface>\n") + "                  <interface>0.0.0.0</interface>\n") + "                </trusted-interfaces>\n") + "            </multicast>\n") + "        </join>\n") + "    </network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MulticastConfig multicastConfig = config.getNetworkConfig().getJoin().getMulticastConfig();
        Assert.assertFalse(multicastConfig.isEnabled());
        Assert.assertTrue(multicastConfig.isLoopbackModeEnabled());
        Assert.assertEquals("224.2.2.4", multicastConfig.getMulticastGroup());
        Assert.assertEquals(65438, multicastConfig.getMulticastPort());
        Assert.assertEquals(4, multicastConfig.getMulticastTimeoutSeconds());
        Assert.assertEquals(42, multicastConfig.getMulticastTimeToLive());
        Assert.assertEquals(2, multicastConfig.getTrustedInterfaces().size());
        Assert.assertTrue(multicastConfig.getTrustedInterfaces().containsAll(ImmutableSet.of("127.0.0.1", "0.0.0.0")));
    }

    @Override
    @Test
    public void testWanConfig() {
        String xml = (((((((((((((((((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <wan-replication name=\"my-wan-cluster\">\n") + "      <wan-publisher group-name=\"istanbul\" publisher-id=\"istanbulPublisherId\">\n") + "         <class-name>com.hazelcast.wan.custom.WanPublisher</class-name>\n") + "         <queue-full-behavior>THROW_EXCEPTION</queue-full-behavior>\n") + "         <queue-capacity>21</queue-capacity>\n") + "         <aws enabled=\"false\" connection-timeout-seconds=\"10\" >\n") + "            <access-key>sample-access-key</access-key>\n") + "            <secret-key>sample-secret-key</secret-key>\n") + "            <iam-role>sample-role</iam-role>\n") + "            <region>sample-region</region>\n") + "            <host-header>sample-header</host-header>\n") + "            <security-group-name>sample-group</security-group-name>\n") + "            <tag-key>sample-tag-key</tag-key>\n") + "            <tag-value>sample-tag-value</tag-value>\n") + "         </aws>\n") + "         <discovery-strategies>\n") + "            <node-filter class=\"DummyFilterClass\" />\n") + "            <discovery-strategy class=\"DummyDiscoveryStrategy1\" enabled=\"true\">\n") + "               <properties>\n") + "                  <property name=\"key-string\">foo</property>\n") + "                  <property name=\"key-int\">123</property>\n") + "                  <property name=\"key-boolean\">true</property>\n") + "               </properties>\n") + "            </discovery-strategy>\n") + "         </discovery-strategies>\n") + "         <properties>\n") + "            <property name=\"custom.prop.publisher\">prop.publisher</property>\n") + "            <property name=\"discovery.period\">5</property>\n") + "            <property name=\"maxEndpoints\">2</property>\n") + "         </properties>\n") + "      </wan-publisher>\n") + "      <wan-publisher group-name=\"ankara\">\n") + "         <class-name>com.hazelcast.wan.custom.WanPublisher</class-name>\n") + "         <queue-full-behavior>THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE</queue-full-behavior>\n") + "         <initial-publisher-state>STOPPED</initial-publisher-state>\n") + "      </wan-publisher>\n") + "      <wan-consumer>\n") + "         <class-name>com.hazelcast.wan.custom.WanConsumer</class-name>\n") + "         <properties>\n") + "            <property name=\"custom.prop.consumer\">prop.consumer</property>\n") + "         </properties>\n") + "      <persist-wan-replicated-data>true</persist-wan-replicated-data>\n") + "      </wan-consumer>\n") + "   </wan-replication>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        WanReplicationConfig wanConfig = config.getWanReplicationConfig("my-wan-cluster");
        Assert.assertNotNull(wanConfig);
        List<WanPublisherConfig> publisherConfigs = wanConfig.getWanPublisherConfigs();
        Assert.assertEquals(2, publisherConfigs.size());
        WanPublisherConfig publisherConfig1 = publisherConfigs.get(0);
        Assert.assertEquals("istanbul", publisherConfig1.getGroupName());
        Assert.assertEquals("istanbulPublisherId", publisherConfig1.getPublisherId());
        Assert.assertEquals("com.hazelcast.wan.custom.WanPublisher", publisherConfig1.getClassName());
        Assert.assertEquals(THROW_EXCEPTION, publisherConfig1.getQueueFullBehavior());
        Assert.assertEquals(REPLICATING, publisherConfig1.getInitialPublisherState());
        Assert.assertEquals(21, publisherConfig1.getQueueCapacity());
        Map<String, Comparable> pubProperties = publisherConfig1.getProperties();
        Assert.assertEquals("prop.publisher", pubProperties.get("custom.prop.publisher"));
        Assert.assertEquals("5", pubProperties.get("discovery.period"));
        Assert.assertEquals("2", pubProperties.get("maxEndpoints"));
        Assert.assertFalse(publisherConfig1.getAwsConfig().isEnabled());
        AbstractConfigBuilderTest.assertAwsConfig(publisherConfig1.getAwsConfig());
        Assert.assertFalse(publisherConfig1.getGcpConfig().isEnabled());
        Assert.assertFalse(publisherConfig1.getAzureConfig().isEnabled());
        Assert.assertFalse(publisherConfig1.getKubernetesConfig().isEnabled());
        Assert.assertFalse(publisherConfig1.getEurekaConfig().isEnabled());
        assertDiscoveryConfig(publisherConfig1.getDiscoveryConfig());
        WanPublisherConfig publisherConfig2 = publisherConfigs.get(1);
        Assert.assertEquals("ankara", publisherConfig2.getGroupName());
        Assert.assertNull(publisherConfig2.getPublisherId());
        Assert.assertEquals(THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE, publisherConfig2.getQueueFullBehavior());
        Assert.assertEquals(STOPPED, publisherConfig2.getInitialPublisherState());
        WanConsumerConfig consumerConfig = wanConfig.getWanConsumerConfig();
        Assert.assertEquals("com.hazelcast.wan.custom.WanConsumer", consumerConfig.getClassName());
        Map<String, Comparable> consProperties = consumerConfig.getProperties();
        Assert.assertEquals("prop.consumer", consProperties.get("custom.prop.consumer"));
        Assert.assertTrue(consumerConfig.isPersistWanReplicatedData());
    }

    @Override
    @Test
    public void testQuorumConfig() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <quorum-function-class-name>com.my.quorum.function</quorum-function-class-name>\n") + "        <quorum-type>READ</quorum-type>\n") + "      </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        Assert.assertTrue("quorum should be enabled", quorumConfig.isEnabled());
        Assert.assertEquals(3, quorumConfig.getSize());
        Assert.assertEquals(READ, quorumConfig.getType());
        Assert.assertEquals("com.my.quorum.function", quorumConfig.getQuorumFunctionClassName());
        Assert.assertTrue(quorumConfig.getListenerConfigs().isEmpty());
    }

    @Override
    @Test
    public void testQuorumListenerConfig() {
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <quorum-listeners>") + "           <quorum-listener>com.abc.my.quorum.listener</quorum-listener>") + "           <quorum-listener>com.abc.my.second.listener</quorum-listener>") + "       </quorum-listeners> ") + "        <quorum-function-class-name>com.hazelcast.SomeQuorumFunction</quorum-function-class-name>") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        Assert.assertFalse(quorumConfig.getListenerConfigs().isEmpty());
        Assert.assertEquals("com.abc.my.quorum.listener", quorumConfig.getListenerConfigs().get(0).getClassName());
        Assert.assertEquals("com.abc.my.second.listener", quorumConfig.getListenerConfigs().get(1).getClassName());
        Assert.assertEquals("com.hazelcast.SomeQuorumFunction", quorumConfig.getQuorumFunctionClassName());
    }

    @Override
    @Test
    public void testQuorumConfig_whenClassNameAndRecentlyActiveQuorumDefined_exceptionIsThrown() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <quorum-function-class-name>com.hazelcast.SomeQuorumFunction</quorum-function-class-name>") + "        <recently-active-quorum />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(ConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testQuorumConfig_whenClassNameAndProbabilisticQuorumDefined_exceptionIsThrown() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <quorum-function-class-name>com.hazelcast.SomeQuorumFunction</quorum-function-class-name>") + "        <probabilistic-quorum />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(ConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testQuorumConfig_whenBothBuiltinQuorumsDefined_exceptionIsThrown() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <probabilistic-quorum />") + "        <recently-active-quorum />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testQuorumConfig_whenRecentlyActiveQuorum_withDefaultValues() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <recently-active-quorum />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        HazelcastTestSupport.assertInstanceOf(RecentlyActiveQuorumFunction.class, quorumConfig.getQuorumFunctionImplementation());
        RecentlyActiveQuorumFunction quorumFunction = ((RecentlyActiveQuorumFunction) (quorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(DEFAULT_HEARTBEAT_TOLERANCE_MILLIS, quorumFunction.getHeartbeatToleranceMillis());
    }

    @Override
    @Test
    public void testQuorumConfig_whenRecentlyActiveQuorum_withCustomValues() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <recently-active-quorum heartbeat-tolerance-millis=\"13000\" />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        Assert.assertEquals(3, quorumConfig.getSize());
        HazelcastTestSupport.assertInstanceOf(RecentlyActiveQuorumFunction.class, quorumConfig.getQuorumFunctionImplementation());
        RecentlyActiveQuorumFunction quorumFunction = ((RecentlyActiveQuorumFunction) (quorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(13000, quorumFunction.getHeartbeatToleranceMillis());
    }

    @Override
    @Test
    public void testQuorumConfig_whenProbabilisticQuorum_withDefaultValues() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <probabilistic-quorum />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        HazelcastTestSupport.assertInstanceOf(ProbabilisticQuorumFunction.class, quorumConfig.getQuorumFunctionImplementation());
        ProbabilisticQuorumFunction quorumFunction = ((ProbabilisticQuorumFunction) (quorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(DEFAULT_HEARTBEAT_INTERVAL_MILLIS, quorumFunction.getHeartbeatIntervalMillis());
        Assert.assertEquals(DEFAULT_HEARTBEAT_PAUSE_MILLIS, quorumFunction.getAcceptableHeartbeatPauseMillis());
        Assert.assertEquals(DEFAULT_MIN_STD_DEVIATION, quorumFunction.getMinStdDeviationMillis());
        Assert.assertEquals(DEFAULT_PHI_THRESHOLD, quorumFunction.getSuspicionThreshold(), 0.01);
        Assert.assertEquals(DEFAULT_SAMPLE_SIZE, quorumFunction.getMaxSampleSize());
    }

    @Override
    @Test
    public void testQuorumConfig_whenProbabilisticQuorum_withCustomValues() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "      <quorum enabled=\"true\" name=\"myQuorum\">\n") + "        <quorum-size>3</quorum-size>\n") + "        <probabilistic-quorum acceptable-heartbeat-pause-millis=\"37400\" suspicion-threshold=\"3.14592\" ") + "                 max-sample-size=\"42\" min-std-deviation-millis=\"1234\"") + "                 heartbeat-interval-millis=\"4321\" />") + "    </quorum>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QuorumConfig quorumConfig = config.getQuorumConfig("myQuorum");
        HazelcastTestSupport.assertInstanceOf(ProbabilisticQuorumFunction.class, quorumConfig.getQuorumFunctionImplementation());
        ProbabilisticQuorumFunction quorumFunction = ((ProbabilisticQuorumFunction) (quorumConfig.getQuorumFunctionImplementation()));
        Assert.assertEquals(4321, quorumFunction.getHeartbeatIntervalMillis());
        Assert.assertEquals(37400, quorumFunction.getAcceptableHeartbeatPauseMillis());
        Assert.assertEquals(1234, quorumFunction.getMinStdDeviationMillis());
        Assert.assertEquals(3.14592, quorumFunction.getSuspicionThreshold(), 0.001);
        Assert.assertEquals(42, quorumFunction.getMaxSampleSize());
    }

    @Override
    @Test
    public void testCacheConfig() {
        String xml = (((((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <cache name=\"foobar\">\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <key-type class-name=\"java.lang.Object\"/>") + "        <value-type class-name=\"java.lang.Object\"/>") + "        <statistics-enabled>false</statistics-enabled>") + "        <management-enabled>false</management-enabled>") + "        <read-through>true</read-through>") + "        <write-through>true</write-through>") + "        <cache-loader-factory class-name=\"com.example.cache.MyCacheLoaderFactory\"/>") + "        <cache-writer-factory class-name=\"com.example.cache.MyCacheWriterFactory\"/>") + "        <expiry-policy-factory class-name=\"com.example.cache.MyExpirePolicyFactory\"/>") + "        <in-memory-format>BINARY</in-memory-format>") + "        <backup-count>1</backup-count>") + "        <async-backup-count>0</async-backup-count>") + "        <eviction size=\"1000\" max-size-policy=\"ENTRY_COUNT\" eviction-policy=\"LFU\"/>") + "        <merge-policy>com.hazelcast.cache.merge.LatestAccessCacheMergePolicy</merge-policy>") + "        <disable-per-entry-invalidation-events>true</disable-per-entry-invalidation-events>") + "        <hot-restart enabled=\"false\">\n") + "            <fsync>false</fsync>\n") + "          </hot-restart>") + "        <partition-lost-listeners>\n") + "            <partition-lost-listener>com.your-package.YourPartitionLostListener</partition-lost-listener>\n") + "          </partition-lost-listeners>") + "        <cache-entry-listeners>\n") + "            <cache-entry-listener old-value-required=\"false\" synchronous=\"false\">\n") + "                <cache-entry-listener-factory\n") + "                        class-name=\"com.example.cache.MyEntryListenerFactory\"/>\n") + "                <cache-entry-event-filter-factory\n") + "                        class-name=\"com.example.cache.MyEntryEventFilterFactory\"/>\n") + "            </cache-entry-listener>\n") + "        </cache-entry-listeners>") + "    </cache>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        CacheSimpleConfig cacheConfig = config.getCacheConfig("foobar");
        Assert.assertFalse(config.getCacheConfigs().isEmpty());
        Assert.assertEquals("customQuorumRule", cacheConfig.getQuorumName());
        Assert.assertEquals("java.lang.Object", cacheConfig.getKeyType());
        Assert.assertEquals("java.lang.Object", cacheConfig.getValueType());
        Assert.assertFalse(cacheConfig.isStatisticsEnabled());
        Assert.assertFalse(cacheConfig.isManagementEnabled());
        Assert.assertTrue(cacheConfig.isReadThrough());
        Assert.assertTrue(cacheConfig.isWriteThrough());
        Assert.assertEquals("com.example.cache.MyCacheLoaderFactory", cacheConfig.getCacheLoaderFactory());
        Assert.assertEquals("com.example.cache.MyCacheWriterFactory", cacheConfig.getCacheWriterFactory());
        Assert.assertEquals("com.example.cache.MyExpirePolicyFactory", cacheConfig.getExpiryPolicyFactoryConfig().getClassName());
        Assert.assertEquals(BINARY, cacheConfig.getInMemoryFormat());
        Assert.assertEquals(1, cacheConfig.getBackupCount());
        Assert.assertEquals(0, cacheConfig.getAsyncBackupCount());
        Assert.assertEquals(1000, cacheConfig.getEvictionConfig().getSize());
        Assert.assertEquals(ENTRY_COUNT, cacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(LFU, cacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals("com.hazelcast.cache.merge.LatestAccessCacheMergePolicy", cacheConfig.getMergePolicy());
        Assert.assertTrue(cacheConfig.isDisablePerEntryInvalidationEvents());
        Assert.assertFalse(cacheConfig.getHotRestartConfig().isEnabled());
        Assert.assertFalse(cacheConfig.getHotRestartConfig().isFsync());
        Assert.assertEquals(1, cacheConfig.getPartitionLostListenerConfigs().size());
        Assert.assertEquals("com.your-package.YourPartitionLostListener", cacheConfig.getPartitionLostListenerConfigs().get(0).getClassName());
        Assert.assertEquals(1, cacheConfig.getCacheEntryListeners().size());
        Assert.assertEquals("com.example.cache.MyEntryListenerFactory", cacheConfig.getCacheEntryListeners().get(0).getCacheEntryListenerFactory());
        Assert.assertEquals("com.example.cache.MyEntryEventFilterFactory", cacheConfig.getCacheEntryListeners().get(0).getCacheEntryEventFilterFactory());
    }

    @Override
    @Test
    public void testExecutorConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <executor-service name=\"foobar\">\n") + "        <pool-size>2</pool-size>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <statistics-enabled>true</statistics-enabled>") + "        <queue-capacity>0</queue-capacity>") + "    </executor-service>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ExecutorConfig executorConfig = config.getExecutorConfig("foobar");
        Assert.assertFalse(config.getExecutorConfigs().isEmpty());
        Assert.assertEquals(2, executorConfig.getPoolSize());
        Assert.assertEquals("customQuorumRule", executorConfig.getQuorumName());
        Assert.assertTrue(executorConfig.isStatisticsEnabled());
        Assert.assertEquals(0, executorConfig.getQueueCapacity());
    }

    @Override
    @Test
    public void testDurableExecutorConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <durable-executor-service name=\"foobar\">\n") + "        <pool-size>2</pool-size>\n") + "        <durability>3</durability>\n") + "        <capacity>4</capacity>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "    </durable-executor-service>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        DurableExecutorConfig durableExecutorConfig = config.getDurableExecutorConfig("foobar");
        Assert.assertFalse(config.getDurableExecutorConfigs().isEmpty());
        Assert.assertEquals(2, durableExecutorConfig.getPoolSize());
        Assert.assertEquals(3, durableExecutorConfig.getDurability());
        Assert.assertEquals(4, durableExecutorConfig.getCapacity());
        Assert.assertEquals("customQuorumRule", durableExecutorConfig.getQuorumName());
    }

    @Override
    @Test
    public void testScheduledExecutorConfig() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <scheduled-executor-service name=\"foobar\">\n") + "        <durability>4</durability>\n") + "        <pool-size>5</pool-size>\n") + "        <capacity>2</capacity>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <merge-policy batch-size='99'>PutIfAbsent</merge-policy>") + "    </scheduled-executor-service>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ScheduledExecutorConfig scheduledExecutorConfig = config.getScheduledExecutorConfig("foobar");
        Assert.assertFalse(config.getScheduledExecutorConfigs().isEmpty());
        Assert.assertEquals(4, scheduledExecutorConfig.getDurability());
        Assert.assertEquals(5, scheduledExecutorConfig.getPoolSize());
        Assert.assertEquals(2, scheduledExecutorConfig.getCapacity());
        Assert.assertEquals("customQuorumRule", scheduledExecutorConfig.getQuorumName());
        Assert.assertEquals(99, scheduledExecutorConfig.getMergePolicyConfig().getBatchSize());
        Assert.assertEquals("PutIfAbsent", scheduledExecutorConfig.getMergePolicyConfig().getPolicy());
    }

    @Override
    @Test
    public void testCardinalityEstimatorConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <cardinality-estimator name=\"foobar\">\n") + "        <backup-count>2</backup-count>\n") + "        <async-backup-count>3</async-backup-count>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <merge-policy>com.hazelcast.spi.merge.HyperLogLogMergePolicy</merge-policy>") + "    </cardinality-estimator>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        CardinalityEstimatorConfig cardinalityEstimatorConfig = config.getCardinalityEstimatorConfig("foobar");
        Assert.assertFalse(config.getCardinalityEstimatorConfigs().isEmpty());
        Assert.assertEquals(2, cardinalityEstimatorConfig.getBackupCount());
        Assert.assertEquals(3, cardinalityEstimatorConfig.getAsyncBackupCount());
        Assert.assertEquals("com.hazelcast.spi.merge.HyperLogLogMergePolicy", cardinalityEstimatorConfig.getMergePolicyConfig().getPolicy());
        Assert.assertEquals("customQuorumRule", cardinalityEstimatorConfig.getQuorumName());
    }

    @Override
    @Test
    public void testCardinalityEstimatorConfigWithInvalidMergePolicy() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <cardinality-estimator name=\"foobar\">\n") + "        <backup-count>2</backup-count>\n") + "        <async-backup-count>3</async-backup-count>\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <merge-policy>CustomMergePolicy</merge-policy>") + "    </cardinality-estimator>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testPNCounterConfig() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <pn-counter name=\"pn-counter-1\">\n") + "        <replica-count>100</replica-count>\n") + "        <quorum-ref>quorumRuleWithThreeMembers</quorum-ref>\n") + "        <statistics-enabled>false</statistics-enabled>\n") + "    </pn-counter>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        PNCounterConfig pnCounterConfig = config.getPNCounterConfig("pn-counter-1");
        Assert.assertFalse(config.getPNCounterConfigs().isEmpty());
        Assert.assertEquals(100, pnCounterConfig.getReplicaCount());
        Assert.assertEquals("quorumRuleWithThreeMembers", pnCounterConfig.getQuorumName());
        Assert.assertFalse(pnCounterConfig.isStatisticsEnabled());
    }

    @Override
    @Test
    public void testMultiMapConfig() {
        String xml = ((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <multimap name=\"myMultiMap\">") + "        <backup-count>2</backup-count>") + "        <async-backup-count>3</async-backup-count>") + "        <binary>false</binary>") + "        <value-collection-type>SET</value-collection-type>") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <entry-listeners>\n") + "            <entry-listener include-value=\"true\" local=\"true\">com.hazelcast.examples.EntryListener</entry-listener>\n") + "          </entry-listeners>") + "        <merge-policy batch-size=\"23\">CustomMergePolicy</merge-policy>") + "  </multimap>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertFalse(config.getMultiMapConfigs().isEmpty());
        MultiMapConfig multiMapConfig = config.getMultiMapConfig("myMultiMap");
        Assert.assertEquals(2, multiMapConfig.getBackupCount());
        Assert.assertEquals(3, multiMapConfig.getAsyncBackupCount());
        Assert.assertFalse(multiMapConfig.isBinary());
        Assert.assertEquals(SET, multiMapConfig.getValueCollectionType());
        Assert.assertEquals(1, multiMapConfig.getEntryListenerConfigs().size());
        Assert.assertEquals("com.hazelcast.examples.EntryListener", multiMapConfig.getEntryListenerConfigs().get(0).getClassName());
        Assert.assertTrue(multiMapConfig.getEntryListenerConfigs().get(0).isIncludeValue());
        Assert.assertTrue(multiMapConfig.getEntryListenerConfigs().get(0).isLocal());
        MergePolicyConfig mergePolicyConfig = multiMapConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals("customQuorumRule", multiMapConfig.getQuorumName());
        Assert.assertEquals(23, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void testReplicatedMapConfig() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <replicatedmap name=\"foobar\">\n") + "        <in-memory-format>BINARY</in-memory-format>\n") + "        <async-fillup>false</async-fillup>\n") + "        <statistics-enabled>false</statistics-enabled>\n") + "        <quorum-ref>CustomQuorumRule</quorum-ref>\n") + "        <merge-policy batch-size=\"2342\">CustomMergePolicy</merge-policy>\n") + "    </replicatedmap>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig("foobar");
        Assert.assertFalse(config.getReplicatedMapConfigs().isEmpty());
        Assert.assertEquals(BINARY, replicatedMapConfig.getInMemoryFormat());
        Assert.assertFalse(replicatedMapConfig.isAsyncFillup());
        Assert.assertFalse(replicatedMapConfig.isStatisticsEnabled());
        Assert.assertEquals("CustomQuorumRule", replicatedMapConfig.getQuorumName());
        MergePolicyConfig mergePolicyConfig = replicatedMapConfig.getMergePolicyConfig();
        Assert.assertEquals("CustomMergePolicy", mergePolicyConfig.getPolicy());
        Assert.assertEquals(2342, mergePolicyConfig.getBatchSize());
    }

    @Override
    @Test
    public void testListConfig() {
        String xml = ((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <list name=\"foobar\">\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <statistics-enabled>false</statistics-enabled>") + "        <max-size>42</max-size>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <merge-policy batch-size=\"100\">SplitBrainMergePolicy</merge-policy>") + "        <item-listeners>\n") + "            <item-listener include-value=\"true\">com.hazelcast.examples.ItemListener</item-listener>\n") + "        </item-listeners>") + "    </list>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        ListConfig listConfig = config.getListConfig("foobar");
        Assert.assertFalse(config.getListConfigs().isEmpty());
        Assert.assertEquals("customQuorumRule", listConfig.getQuorumName());
        Assert.assertEquals(42, listConfig.getMaxSize());
        Assert.assertEquals(2, listConfig.getBackupCount());
        Assert.assertEquals(1, listConfig.getAsyncBackupCount());
        Assert.assertEquals(1, listConfig.getItemListenerConfigs().size());
        Assert.assertEquals("com.hazelcast.examples.ItemListener", listConfig.getItemListenerConfigs().get(0).getClassName());
        MergePolicyConfig mergePolicyConfig = listConfig.getMergePolicyConfig();
        Assert.assertEquals(100, mergePolicyConfig.getBatchSize());
        Assert.assertEquals("SplitBrainMergePolicy", mergePolicyConfig.getPolicy());
    }

    @Override
    @Test
    public void testSetConfig() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <set name=\"foobar\">\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <max-size>42</max-size>") + "        <merge-policy batch-size=\"42\">SplitBrainMergePolicy</merge-policy>") + "        <item-listeners>\n") + "            <item-listener include-value=\"true\">com.hazelcast.examples.ItemListener</item-listener>\n") + "          </item-listeners>") + "    </set>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        SetConfig setConfig = config.getSetConfig("foobar");
        Assert.assertFalse(config.getSetConfigs().isEmpty());
        Assert.assertEquals("customQuorumRule", setConfig.getQuorumName());
        Assert.assertEquals(2, setConfig.getBackupCount());
        Assert.assertEquals(1, setConfig.getAsyncBackupCount());
        Assert.assertEquals(42, setConfig.getMaxSize());
        Assert.assertEquals(1, setConfig.getItemListenerConfigs().size());
        Assert.assertTrue(setConfig.getItemListenerConfigs().get(0).isIncludeValue());
        Assert.assertEquals("com.hazelcast.examples.ItemListener", setConfig.getItemListenerConfigs().get(0).getClassName());
        MergePolicyConfig mergePolicyConfig = setConfig.getMergePolicyConfig();
        Assert.assertEquals(42, mergePolicyConfig.getBatchSize());
        Assert.assertEquals("SplitBrainMergePolicy", mergePolicyConfig.getPolicy());
    }

    @Override
    @Test
    public void testMapConfig() {
        String xml = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <map name=\"foobar\">\n") + "        <quorum-ref>customQuorumRule</quorum-ref>") + "        <in-memory-format>BINARY</in-memory-format>") + "        <statistics-enabled>true</statistics-enabled>") + "        <optimize-queries>false</optimize-queries>") + "        <cache-deserialized-values>INDEX-ONLY</cache-deserialized-values>") + "        <backup-count>2</backup-count>") + "        <async-backup-count>1</async-backup-count>") + "        <time-to-live-seconds>42</time-to-live-seconds>") + "        <max-idle-seconds>42</max-idle-seconds>") + "        <eviction-policy>RANDOM</eviction-policy>") + "        <max-size policy=\"PER_NODE\">42</max-size>") + "        <eviction-percentage>25</eviction-percentage>") + "        <min-eviction-check-millis>256</min-eviction-check-millis>") + "        <read-backup-data>true</read-backup-data>") + "        <hot-restart enabled=\"false\">\n") + "            <fsync>false</fsync>\n") + "          </hot-restart>") + "        <map-store enabled=\"true\" initial-mode=\"LAZY\">\n") + "            <class-name>com.hazelcast.examples.DummyStore</class-name>\n") + "            <write-delay-seconds>42</write-delay-seconds>\n") + "            <write-batch-size>42</write-batch-size>\n") + "            <write-coalescing>true</write-coalescing>\n") + "            <properties>\n") + "                <property name=\"jdbc_url\">my.jdbc.com</property>\n") + "            </properties>\n") + "          </map-store>") + "        <near-cache>\n") + "            <max-size>5000</max-size>\n") + "            <time-to-live-seconds>42</time-to-live-seconds>\n") + "            <max-idle-seconds>42</max-idle-seconds>\n") + "            <eviction-policy>LRU</eviction-policy>\n") + "            <invalidate-on-change>true</invalidate-on-change>\n") + "            <in-memory-format>BINARY</in-memory-format>\n") + "            <cache-local-entries>false</cache-local-entries>\n") + "            <eviction size=\"1000\" max-size-policy=\"ENTRY_COUNT\" eviction-policy=\"LFU\"/>\n") + "          </near-cache>") + "        <wan-replication-ref name=\"my-wan-cluster-batch\">\n") + "            <merge-policy>com.hazelcast.map.merge.PassThroughMergePolicy</merge-policy>\n") + "            <filters>\n") + "                <filter-impl>com.example.SampleFilter</filter-impl>\n") + "            </filters>\n") + "            <republishing-enabled>false</republishing-enabled>\n") + "          </wan-replication-ref>") + "        <indexes>\n") + "            <index ordered=\"true\">age</index>\n") + "          </indexes>") + "        <attributes>\n") + "            <attribute extractor=\"com.bank.CurrencyExtractor\">currency</attribute>\n") + "           </attributes>") + "        <partition-lost-listeners>\n") + "            <partition-lost-listener>com.your-package.YourPartitionLostListener</partition-lost-listener>\n") + "          </partition-lost-listeners>") + "        <entry-listeners>\n") + "            <entry-listener include-value=\"false\" local=\"false\">com.your-package.MyEntryListener</entry-listener>\n") + "          </entry-listeners>") + "    </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("foobar");
        Assert.assertFalse(config.getMapConfigs().isEmpty());
        Assert.assertEquals("customQuorumRule", mapConfig.getQuorumName());
        Assert.assertEquals(BINARY, mapConfig.getInMemoryFormat());
        Assert.assertTrue(mapConfig.isStatisticsEnabled());
        Assert.assertFalse(mapConfig.isOptimizeQueries());
        Assert.assertEquals(INDEX_ONLY, mapConfig.getCacheDeserializedValues());
        Assert.assertEquals(2, mapConfig.getBackupCount());
        Assert.assertEquals(1, mapConfig.getAsyncBackupCount());
        Assert.assertEquals(1, mapConfig.getAsyncBackupCount());
        Assert.assertEquals(42, mapConfig.getTimeToLiveSeconds());
        Assert.assertEquals(42, mapConfig.getMaxIdleSeconds());
        Assert.assertEquals(RANDOM, mapConfig.getEvictionPolicy());
        Assert.assertEquals(PER_NODE, mapConfig.getMaxSizeConfig().getMaxSizePolicy());
        Assert.assertEquals(42, mapConfig.getMaxSizeConfig().getSize());
        Assert.assertEquals(25, mapConfig.getEvictionPercentage());
        Assert.assertEquals(256, mapConfig.getMinEvictionCheckMillis());
        Assert.assertTrue(mapConfig.isReadBackupData());
        Assert.assertEquals(1, mapConfig.getMapIndexConfigs().size());
        Assert.assertEquals("age", mapConfig.getMapIndexConfigs().get(0).getAttribute());
        Assert.assertTrue(mapConfig.getMapIndexConfigs().get(0).isOrdered());
        Assert.assertEquals(1, mapConfig.getMapAttributeConfigs().size());
        Assert.assertEquals("com.bank.CurrencyExtractor", mapConfig.getMapAttributeConfigs().get(0).getExtractor());
        Assert.assertEquals("currency", mapConfig.getMapAttributeConfigs().get(0).getName());
        Assert.assertEquals(1, mapConfig.getPartitionLostListenerConfigs().size());
        Assert.assertEquals("com.your-package.YourPartitionLostListener", mapConfig.getPartitionLostListenerConfigs().get(0).getClassName());
        Assert.assertEquals(1, mapConfig.getEntryListenerConfigs().size());
        Assert.assertFalse(mapConfig.getEntryListenerConfigs().get(0).isIncludeValue());
        Assert.assertFalse(mapConfig.getEntryListenerConfigs().get(0).isLocal());
        Assert.assertEquals("com.your-package.MyEntryListener", mapConfig.getEntryListenerConfigs().get(0).getClassName());
        Assert.assertFalse(mapConfig.getHotRestartConfig().isEnabled());
        Assert.assertFalse(mapConfig.getHotRestartConfig().isFsync());
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        Assert.assertNotNull(mapStoreConfig);
        Assert.assertTrue(mapStoreConfig.isEnabled());
        Assert.assertEquals(LAZY, mapStoreConfig.getInitialLoadMode());
        Assert.assertEquals(42, mapStoreConfig.getWriteDelaySeconds());
        Assert.assertEquals(42, mapStoreConfig.getWriteBatchSize());
        Assert.assertTrue(mapStoreConfig.isWriteCoalescing());
        Assert.assertEquals("com.hazelcast.examples.DummyStore", mapStoreConfig.getClassName());
        Assert.assertEquals(1, mapStoreConfig.getProperties().size());
        Assert.assertEquals("my.jdbc.com", mapStoreConfig.getProperties().getProperty("jdbc_url"));
        NearCacheConfig nearCacheConfig = mapConfig.getNearCacheConfig();
        Assert.assertNotNull(nearCacheConfig);
        Assert.assertEquals(5000, nearCacheConfig.getMaxSize());
        Assert.assertEquals(42, nearCacheConfig.getMaxIdleSeconds());
        Assert.assertEquals(42, nearCacheConfig.getTimeToLiveSeconds());
        Assert.assertEquals(BINARY, nearCacheConfig.getInMemoryFormat());
        Assert.assertFalse(nearCacheConfig.isCacheLocalEntries());
        Assert.assertTrue(nearCacheConfig.isInvalidateOnChange());
        Assert.assertEquals(1000, nearCacheConfig.getEvictionConfig().getSize());
        Assert.assertEquals(LFU, nearCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(ENTRY_COUNT, nearCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        WanReplicationRef wanReplicationRef = mapConfig.getWanReplicationRef();
        Assert.assertNotNull(wanReplicationRef);
        Assert.assertFalse(wanReplicationRef.isRepublishingEnabled());
        Assert.assertEquals("com.hazelcast.map.merge.PassThroughMergePolicy", wanReplicationRef.getMergePolicy());
        Assert.assertEquals(1, wanReplicationRef.getFilters().size());
        Assert.assertEquals("com.example.SampleFilter".toLowerCase(), wanReplicationRef.getFilters().get(0).toLowerCase());
    }

    @Override
    @Test
    public void testIndexesConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <indexes>\n") + "           <index ordered=\"false\">name</index>\n") + "           <index ordered=\"true\">age</index>\n") + "       </indexes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("people");
        Assert.assertFalse(mapConfig.getMapIndexConfigs().isEmpty());
        XMLConfigBuilderTest.assertIndexEqual("name", false, mapConfig.getMapIndexConfigs().get(0));
        XMLConfigBuilderTest.assertIndexEqual("age", true, mapConfig.getMapIndexConfigs().get(1));
    }

    @Override
    @Test
    public void testAttributeConfig() {
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute extractor=\"com.car.PowerExtractor\">power</attribute>\n") + "           <attribute extractor=\"com.car.WeightExtractor\">weight</attribute>\n") + "       </attributes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("people");
        Assert.assertFalse(mapConfig.getMapAttributeConfigs().isEmpty());
        XMLConfigBuilderTest.assertAttributeEqual("power", "com.car.PowerExtractor", mapConfig.getMapAttributeConfigs().get(0));
        XMLConfigBuilderTest.assertAttributeEqual("weight", "com.car.WeightExtractor", mapConfig.getMapAttributeConfigs().get(1));
    }

    @Override
    @Test
    public void testAttributeConfig_noName_emptyTag() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute extractor=\"com.car.WeightExtractor\"></attribute>\n") + "       </attributes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(IllegalArgumentException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testAttributeConfig_noName_singleTag() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute extractor=\"com.car.WeightExtractor\"/>\n") + "       </attributes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(IllegalArgumentException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testAttributeConfig_noExtractor() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute>weight</attribute>\n") + "       </attributes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(IllegalArgumentException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testAttributeConfig_emptyExtractor() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute extractor=\"\">weight</attribute>\n") + "       </attributes>") + "   </map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(IllegalArgumentException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testQueryCacheFullConfig() {
        String xml = ((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"test\">") + "<query-caches>") + "<query-cache name=\"cache-name\">") + "<entry-listeners>") + "<entry-listener include-value=\"true\" local=\"false\">com.hazelcast.examples.EntryListener</entry-listener>") + "</entry-listeners>") + "<include-value>true</include-value>") + "<batch-size>1</batch-size>") + "<buffer-size>16</buffer-size>") + "<delay-seconds>0</delay-seconds>") + "<in-memory-format>BINARY</in-memory-format>") + "<coalesce>false</coalesce>") + "<populate>true</populate>") + "<indexes>") + "<index ordered=\"false\">name</index>") + "</indexes>") + "<predicate type=\"class-name\"> ") + "com.hazelcast.examples.SimplePredicate") + "</predicate>") + "<eviction eviction-policy=\"LRU\" max-size-policy=\"ENTRY_COUNT\" size=\"133\"/>") + "</query-cache>") + "</query-caches>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QueryCacheConfig queryCacheConfig = config.getMapConfig("test").getQueryCacheConfigs().get(0);
        EntryListenerConfig entryListenerConfig = queryCacheConfig.getEntryListenerConfigs().get(0);
        Assert.assertEquals("cache-name", queryCacheConfig.getName());
        Assert.assertTrue(entryListenerConfig.isIncludeValue());
        Assert.assertFalse(entryListenerConfig.isLocal());
        Assert.assertEquals("com.hazelcast.examples.EntryListener", entryListenerConfig.getClassName());
        Assert.assertTrue(queryCacheConfig.isIncludeValue());
        Assert.assertEquals(1, queryCacheConfig.getBatchSize());
        Assert.assertEquals(16, queryCacheConfig.getBufferSize());
        Assert.assertEquals(0, queryCacheConfig.getDelaySeconds());
        Assert.assertEquals(BINARY, queryCacheConfig.getInMemoryFormat());
        Assert.assertFalse(queryCacheConfig.isCoalesce());
        Assert.assertTrue(queryCacheConfig.isPopulate());
        assertIndexesEqual(queryCacheConfig);
        Assert.assertEquals("com.hazelcast.examples.SimplePredicate", queryCacheConfig.getPredicateConfig().getClassName());
        Assert.assertEquals(EvictionPolicy.LRU, queryCacheConfig.getEvictionConfig().getEvictionPolicy());
        Assert.assertEquals(MaxSizePolicy.ENTRY_COUNT, queryCacheConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(133, queryCacheConfig.getEvictionConfig().getSize());
    }

    @Override
    @Test
    public void testMapQueryCachePredicate() {
        String xml = (((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <map name=\"test\">\n") + "    <query-caches>\n") + "      <query-cache name=\"cache-class-name\">\n") + "        <predicate type=\"class-name\">com.hazelcast.examples.SimplePredicate</predicate>\n") + "      </query-cache>\n") + "      <query-cache name=\"cache-sql\">\n") + "        <predicate type=\"sql\">%age=40</predicate>\n") + "      </query-cache>\n") + "    </query-caches>\n") + "  </map>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        QueryCacheConfig queryCacheClassNameConfig = config.getMapConfig("test").getQueryCacheConfigs().get(0);
        Assert.assertEquals("com.hazelcast.examples.SimplePredicate", queryCacheClassNameConfig.getPredicateConfig().getClassName());
        QueryCacheConfig queryCacheSqlConfig = config.getMapConfig("test").getQueryCacheConfigs().get(1);
        Assert.assertEquals("%age=40", queryCacheSqlConfig.getPredicateConfig().getSql());
    }

    @Override
    @Test
    public void testLiteMemberConfig() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <lite-member enabled=\"true\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertTrue(config.isLiteMember());
    }

    @Override
    @Test
    public void testNonLiteMemberConfig() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <lite-member enabled=\"false\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertFalse(config.isLiteMember());
    }

    @Override
    @Test
    public void testNonLiteMemberConfigWithoutEnabledField() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <lite-member/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testInvalidLiteMemberConfig() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <lite-member enabled=\"dummytext\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testDuplicateLiteMemberConfig() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <lite-member enabled=\"true\"/>\n") + "    <lite-member enabled=\"true\"/>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testMapNativeMaxSizePolicy() {
        String xmlFormat = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"mymap\">") + "<in-memory-format>NATIVE</in-memory-format>") + "<max-size policy=\"{0}\">9991</max-size>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        MessageFormat messageFormat = new MessageFormat(xmlFormat);
        MaxSizeConfig[] maxSizePolicies = MaxSizeConfig.MaxSizePolicy.values();
        for (MaxSizeConfig.MaxSizePolicy maxSizePolicy : maxSizePolicies) {
            Object[] objects = new Object[]{ maxSizePolicy.toString() };
            String xml = messageFormat.format(objects);
            Config config = buildConfig(xml);
            MapConfig mapConfig = config.getMapConfig("mymap");
            MaxSizeConfig maxSizeConfig = mapConfig.getMaxSizeConfig();
            Assert.assertEquals(9991, maxSizeConfig.getSize());
            Assert.assertEquals(maxSizePolicy, maxSizeConfig.getMaxSizePolicy());
        }
    }

    @Override
    @Test
    public void testInstanceName() {
        String name = HazelcastTestSupport.randomName();
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<instance-name>") + name) + "</instance-name>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertEquals(name, config.getInstanceName());
    }

    @Override
    @Test
    public void testUserCodeDeployment() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<user-code-deployment enabled=\"true\">") + "<class-cache-mode>OFF</class-cache-mode>") + "<provider-mode>LOCAL_CLASSES_ONLY</provider-mode>") + "<blacklist-prefixes>com.blacklisted,com.other.blacklisted</blacklist-prefixes>") + "<whitelist-prefixes>com.whitelisted,com.other.whitelisted</whitelist-prefixes>") + "<provider-filter>HAS_ATTRIBUTE:foo</provider-filter>") + "</user-code-deployment>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = new InMemoryXmlConfig(xml);
        UserCodeDeploymentConfig dcConfig = config.getUserCodeDeploymentConfig();
        Assert.assertTrue(dcConfig.isEnabled());
        Assert.assertEquals(OFF, dcConfig.getClassCacheMode());
        Assert.assertEquals(LOCAL_CLASSES_ONLY, dcConfig.getProviderMode());
        Assert.assertEquals("com.blacklisted,com.other.blacklisted", dcConfig.getBlacklistedPrefixes());
        Assert.assertEquals("com.whitelisted,com.other.whitelisted", dcConfig.getWhitelistedPrefixes());
        Assert.assertEquals("HAS_ATTRIBUTE:foo", dcConfig.getProviderFilter());
    }

    @Override
    @Test
    public void testCRDTReplicationConfig() {
        final String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<crdt-replication>\n") + "        <max-concurrent-replication-targets>10</max-concurrent-replication-targets>\n") + "        <replication-period-millis>2000</replication-period-millis>\n") + "</crdt-replication>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        final Config config = new InMemoryXmlConfig(xml);
        final CRDTReplicationConfig replicationConfig = config.getCRDTReplicationConfig();
        Assert.assertEquals(10, replicationConfig.getMaxConcurrentReplicationTargets());
        Assert.assertEquals(2000, replicationConfig.getReplicationPeriodMillis());
    }

    @Override
    @Test
    public void testGlobalSerializer() {
        String name = HazelcastTestSupport.randomName();
        String val = "true";
        String xml = ((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <serialization>\n") + "      <serializers>\n") + "          <global-serializer override-java-serialization=\"") + val) + "\">") + name) + "</global-serializer>\n") + "      </serializers>\n") + "  </serialization>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = new InMemoryXmlConfig(xml);
        GlobalSerializerConfig globalSerializerConfig = config.getSerializationConfig().getGlobalSerializerConfig();
        Assert.assertEquals(name, globalSerializerConfig.getClassName());
        Assert.assertTrue(globalSerializerConfig.isOverrideJavaSerialization());
    }

    @Override
    @Test
    public void testJavaSerializationFilter() {
        String xml = (((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <serialization>\n") + "      <java-serialization-filter defaults-disabled=\'true\'>\n") + "          <whitelist>\n") + "              <class>java.lang.String</class>\n") + "              <class>example.Foo</class>\n") + "              <package>com.acme.app</package>\n") + "              <package>com.acme.app.subpkg</package>\n") + "              <prefix>java</prefix>\n") + "              <prefix>com.hazelcast.</prefix>\n") + "              <prefix>[</prefix>\n") + "          </whitelist>\n") + "          <blacklist>\n") + "              <class>com.acme.app.BeanComparator</class>\n") + "          </blacklist>\n") + "      </java-serialization-filter>\n") + "  </serialization>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = new InMemoryXmlConfig(xml);
        JavaSerializationFilterConfig javaSerializationFilterConfig = config.getSerializationConfig().getJavaSerializationFilterConfig();
        Assert.assertNotNull(javaSerializationFilterConfig);
        ClassFilter blackList = javaSerializationFilterConfig.getBlacklist();
        Assert.assertNotNull(blackList);
        ClassFilter whiteList = javaSerializationFilterConfig.getWhitelist();
        Assert.assertNotNull(whiteList);
        Assert.assertTrue(whiteList.getClasses().contains("java.lang.String"));
        Assert.assertTrue(whiteList.getClasses().contains("example.Foo"));
        Assert.assertTrue(whiteList.getPackages().contains("com.acme.app"));
        Assert.assertTrue(whiteList.getPackages().contains("com.acme.app.subpkg"));
        Assert.assertTrue(whiteList.getPrefixes().contains("java"));
        Assert.assertTrue(whiteList.getPrefixes().contains("["));
        Assert.assertTrue(blackList.getClasses().contains("com.acme.app.BeanComparator"));
    }

    @Override
    @Test
    public void testHotRestart() {
        String dir = "/mnt/hot-restart-root/";
        String backupDir = "/mnt/hot-restart-backup/";
        int parallelism = 3;
        int validationTimeout = 13131;
        int dataLoadTimeout = 45454;
        HotRestartClusterDataRecoveryPolicy policy = PARTIAL_RECOVERY_MOST_RECENT;
        String xml = ((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<hot-restart-persistence enabled=\"true\">") + "    <base-dir>") + dir) + "</base-dir>") + "    <backup-dir>") + backupDir) + "</backup-dir>") + "    <parallelism>") + parallelism) + "</parallelism>") + "    <validation-timeout-seconds>") + validationTimeout) + "</validation-timeout-seconds>") + "    <data-load-timeout-seconds>") + dataLoadTimeout) + "</data-load-timeout-seconds>") + "    <cluster-data-recovery-policy>") + policy) + "</cluster-data-recovery-policy>") + "    <auto-remove-stale-data>false</auto-remove-stale-data>") + "</hot-restart-persistence>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = new InMemoryXmlConfig(xml);
        HotRestartPersistenceConfig hotRestartPersistenceConfig = config.getHotRestartPersistenceConfig();
        Assert.assertTrue(hotRestartPersistenceConfig.isEnabled());
        Assert.assertEquals(new File(dir).getAbsolutePath(), hotRestartPersistenceConfig.getBaseDir().getAbsolutePath());
        Assert.assertEquals(new File(backupDir).getAbsolutePath(), hotRestartPersistenceConfig.getBackupDir().getAbsolutePath());
        Assert.assertEquals(parallelism, hotRestartPersistenceConfig.getParallelism());
        Assert.assertEquals(validationTimeout, hotRestartPersistenceConfig.getValidationTimeoutSeconds());
        Assert.assertEquals(dataLoadTimeout, hotRestartPersistenceConfig.getDataLoadTimeoutSeconds());
        Assert.assertEquals(policy, hotRestartPersistenceConfig.getClusterDataRecoveryPolicy());
        Assert.assertFalse(hotRestartPersistenceConfig.isAutoRemoveStaleData());
    }

    @Override
    @Test
    public void testMapEvictionPolicyClassName() {
        String mapEvictionPolicyClassName = "com.hazelcast.map.eviction.LRUEvictionPolicy";
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"test\">") + "<map-eviction-policy-class-name>") + mapEvictionPolicyClassName) + "</map-eviction-policy-class-name> ") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("test");
        Assert.assertEquals(mapEvictionPolicyClassName, mapConfig.getMapEvictionPolicy().getClass().getName());
    }

    @Override
    @Test
    public void testMapEvictionPolicyIsSelected_whenEvictionPolicySet() {
        String mapEvictionPolicyClassName = "com.hazelcast.map.eviction.LRUEvictionPolicy";
        String xml = (((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<map name=\"test\">") + "<map-eviction-policy-class-name>") + mapEvictionPolicyClassName) + "</map-eviction-policy-class-name> ") + "<eviction-policy>LFU</eviction-policy>") + "</map>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MapConfig mapConfig = config.getMapConfig("test");
        Assert.assertEquals(mapEvictionPolicyClassName, mapConfig.getMapEvictionPolicy().getClass().getName());
    }

    @Override
    @Test
    public void testOnJoinPermissionOperation() {
        for (OnJoinPermissionOperationName onJoinOp : OnJoinPermissionOperationName.values()) {
            String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + (XMLConfigBuilderTest.SECURITY_START_TAG)) + "  <client-permissions on-join-operation='") + (onJoinOp.name())) + "'/>") + (XMLConfigBuilderTest.SECURITY_END_TAG)) + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
            Config config = buildConfig(xml);
            Assert.assertSame(onJoinOp, config.getSecurityConfig().getOnJoinPermissionOperation());
        }
    }

    @Override
    @Test
    public void testCachePermission() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + (XMLConfigBuilderTest.SECURITY_START_TAG)) + "  <client-permissions>") + "    <cache-permission name=\"/hz/cachemanager1/cache1\" principal=\"dev\">") + (XMLConfigBuilderTest.ACTIONS_FRAGMENT)) + "    </cache-permission>\n") + "  </client-permissions>") + (XMLConfigBuilderTest.SECURITY_END_TAG)) + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertSame("Receive is expected to be default on-join-operation", RECEIVE, config.getSecurityConfig().getOnJoinPermissionOperation());
        PermissionConfig expected = new PermissionConfig(CACHE, "/hz/cachemanager1/cache1", "dev");
        expected.addAction("create").addAction("destroy").addAction("add").addAction("remove");
        AbstractConfigBuilderTest.assertPermissionConfig(expected, config);
    }

    @Override
    @Test
    public void testConfigPermission() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + (XMLConfigBuilderTest.SECURITY_START_TAG)) + "  <client-permissions>") + "    <config-permission principal=\"dev\">") + "       <endpoints><endpoint>127.0.0.1</endpoint></endpoints>") + "    </config-permission>\n") + "  </client-permissions>") + (XMLConfigBuilderTest.SECURITY_END_TAG)) + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        PermissionConfig expected = new PermissionConfig(CONFIG, "*", "dev");
        expected.getEndpoints().add("127.0.0.1");
        AbstractConfigBuilderTest.assertPermissionConfig(expected, config);
    }

    @Override
    @Test
    public void testAllPermissionsCovered() {
        InputStream xmlResource = XMLConfigBuilderTest.class.getClassLoader().getResourceAsStream("hazelcast-fullconfig.xml");
        Config config = null;
        try {
            config = new XmlConfigBuilder(xmlResource).build();
        } finally {
            IOUtil.closeResource(xmlResource);
        }
        Set<com.hazelcast.config.PermissionConfig.PermissionType> permTypes = new HashSet<com.hazelcast.config.PermissionConfig.PermissionType>(Arrays.asList(com.hazelcast.config.PermissionConfig.PermissionType.values()));
        for (PermissionConfig pc : config.getSecurityConfig().getClientPermissionConfigs()) {
            permTypes.remove(pc.getType());
        }
        Assert.assertTrue(("All permission types should be listed in hazelcast-fullconfig.xml. Not found ones: " + permTypes), permTypes.isEmpty());
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testCacheConfig_withNativeInMemoryFormat_failsFastInOSS() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <cache name=\"cache\">") + "        <eviction size=\"10000000\" max-size-policy=\"ENTRY_COUNT\" eviction-policy=\"LFU\"/>") + "        <in-memory-format>NATIVE</in-memory-format>\n") + "    </cache>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        buildConfig(xml);
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testMemberAddressProvider_classNameIsMandatory() {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network> ") + "  <member-address-provider enabled=\"true\">") + "  </member-address-provider>") + "</network> ") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testMemberAddressProviderEnabled() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network> ") + "  <member-address-provider enabled=\"true\">") + "    <class-name>foo.bar.Clazz</class-name>") + "  </member-address-provider>") + "</network> ") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MemberAddressProviderConfig memberAddressProviderConfig = config.getNetworkConfig().getMemberAddressProviderConfig();
        Assert.assertTrue(memberAddressProviderConfig.isEnabled());
        Assert.assertEquals("foo.bar.Clazz", memberAddressProviderConfig.getClassName());
    }

    @Override
    @Test
    public void testMemberAddressProviderEnabled_withProperties() {
        String xml = (((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network> ") + "  <member-address-provider enabled=\"true\">") + "    <class-name>foo.bar.Clazz</class-name>") + "    <properties>") + "       <property name=\"propName1\">propValue1</property>") + "    </properties>") + "  </member-address-provider>") + "</network> ") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MemberAddressProviderConfig memberAddressProviderConfig = config.getNetworkConfig().getMemberAddressProviderConfig();
        Properties properties = memberAddressProviderConfig.getProperties();
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("propValue1", properties.get("propName1"));
    }

    @Override
    @Test
    public void testFailureDetector_withProperties() {
        String xml = (((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network>") + "  <failure-detector>\n") + "            <icmp enabled=\"true\">\n") + "                <timeout-milliseconds>42</timeout-milliseconds>\n") + "                <fail-fast-on-startup>true</fail-fast-on-startup>\n") + "                <interval-milliseconds>4200</interval-milliseconds>\n") + "                <max-attempts>42</max-attempts>\n") + "                <parallel-mode>true</parallel-mode>\n") + "                <ttl>255</ttl>\n") + "            </icmp>\n") + "  </failure-detector>") + "</network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        NetworkConfig networkConfig = config.getNetworkConfig();
        IcmpFailureDetectorConfig icmpFailureDetectorConfig = networkConfig.getIcmpFailureDetectorConfig();
        Assert.assertNotNull(icmpFailureDetectorConfig);
        Assert.assertTrue(icmpFailureDetectorConfig.isEnabled());
        Assert.assertTrue(icmpFailureDetectorConfig.isParallelMode());
        Assert.assertTrue(icmpFailureDetectorConfig.isFailFastOnStartup());
        Assert.assertEquals(42, icmpFailureDetectorConfig.getTimeoutMilliseconds());
        Assert.assertEquals(42, icmpFailureDetectorConfig.getMaxAttempts());
        Assert.assertEquals(4200, icmpFailureDetectorConfig.getIntervalMilliseconds());
    }

    @Override
    @Test
    public void testCPSubsystemConfig() {
        String xml = ((((((((((((((((((((((((((((((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<cp-subsystem>\n") + "  <cp-member-count>10</cp-member-count>\n") + "  <group-size>5</group-size>\n") + "  <session-time-to-live-seconds>15</session-time-to-live-seconds>\n") + "  <session-heartbeat-interval-seconds>3</session-heartbeat-interval-seconds>\n") + "  <missing-cp-member-auto-removal-seconds>120</missing-cp-member-auto-removal-seconds>\n") + "  <fail-on-indeterminate-operation-state>true</fail-on-indeterminate-operation-state>\n") + "  <raft-algorithm>\n") + "    <leader-election-timeout-in-millis>500</leader-election-timeout-in-millis>\n") + "    <leader-heartbeat-period-in-millis>100</leader-heartbeat-period-in-millis>\n") + "    <max-missed-leader-heartbeat-count>3</max-missed-leader-heartbeat-count>\n") + "    <append-request-max-entry-count>25</append-request-max-entry-count>\n") + "    <commit-index-advance-count-to-snapshot>250</commit-index-advance-count-to-snapshot>\n") + "    <uncommitted-entry-count-to-reject-new-appends>75</uncommitted-entry-count-to-reject-new-appends>\n") + "    <append-request-backoff-timeout-in-millis>50</append-request-backoff-timeout-in-millis>\n") + "  </raft-algorithm>\n") + "  <semaphores>\n") + "    <cp-semaphore>\n") + "      <name>sem1</name>\n") + "      <jdk-compatible>true</jdk-compatible>\n") + "    </cp-semaphore>\n") + "    <cp-semaphore>\n") + "      <name>sem2</name>\n") + "      <jdk-compatible>false</jdk-compatible>\n") + "    </cp-semaphore>\n") + "  </semaphores>\n") + "  <locks>\n") + "    <fenced-lock>\n") + "      <name>lock1</name>\n") + "      <lock-acquire-limit>1</lock-acquire-limit>\n") + "    </fenced-lock>\n") + "    <fenced-lock>\n") + "      <name>lock2</name>\n") + "      <lock-acquire-limit>2</lock-acquire-limit>\n") + "    </fenced-lock>\n") + "  </locks>\n") + "</cp-subsystem>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = new InMemoryXmlConfig(xml);
        CPSubsystemConfig cpSubsystemConfig = config.getCPSubsystemConfig();
        Assert.assertEquals(10, cpSubsystemConfig.getCPMemberCount());
        Assert.assertEquals(5, cpSubsystemConfig.getGroupSize());
        Assert.assertEquals(15, cpSubsystemConfig.getSessionTimeToLiveSeconds());
        Assert.assertEquals(3, cpSubsystemConfig.getSessionHeartbeatIntervalSeconds());
        Assert.assertEquals(120, cpSubsystemConfig.getMissingCPMemberAutoRemovalSeconds());
        Assert.assertTrue(cpSubsystemConfig.isFailOnIndeterminateOperationState());
        RaftAlgorithmConfig raftAlgorithmConfig = cpSubsystemConfig.getRaftAlgorithmConfig();
        Assert.assertEquals(500, raftAlgorithmConfig.getLeaderElectionTimeoutInMillis());
        Assert.assertEquals(100, raftAlgorithmConfig.getLeaderHeartbeatPeriodInMillis());
        Assert.assertEquals(3, raftAlgorithmConfig.getMaxMissedLeaderHeartbeatCount());
        Assert.assertEquals(25, raftAlgorithmConfig.getAppendRequestMaxEntryCount());
        Assert.assertEquals(250, raftAlgorithmConfig.getCommitIndexAdvanceCountToSnapshot());
        Assert.assertEquals(75, raftAlgorithmConfig.getUncommittedEntryCountToRejectNewAppends());
        Assert.assertEquals(50, raftAlgorithmConfig.getAppendRequestBackoffTimeoutInMillis());
        CPSemaphoreConfig semaphoreConfig1 = cpSubsystemConfig.findSemaphoreConfig("sem1");
        CPSemaphoreConfig semaphoreConfig2 = cpSubsystemConfig.findSemaphoreConfig("sem2");
        Assert.assertNotNull(semaphoreConfig1);
        Assert.assertNotNull(semaphoreConfig2);
        Assert.assertTrue(semaphoreConfig1.isJDKCompatible());
        Assert.assertFalse(semaphoreConfig2.isJDKCompatible());
        FencedLockConfig lockConfig1 = cpSubsystemConfig.findLockConfig("lock1");
        FencedLockConfig lockConfig2 = cpSubsystemConfig.findLockConfig("lock2");
        Assert.assertNotNull(lockConfig1);
        Assert.assertNotNull(lockConfig2);
        Assert.assertEquals(1, lockConfig1.getLockAcquireLimit());
        Assert.assertEquals(2, lockConfig2.getLockAcquireLimit());
    }

    @Override
    @Test
    public void testMemcacheProtocolEnabled() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network><memcache-protocol enabled=\'true\'/></network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        MemcacheProtocolConfig memcacheProtocolConfig = config.getNetworkConfig().getMemcacheProtocolConfig();
        Assert.assertNotNull(memcacheProtocolConfig);
        Assert.assertTrue(memcacheProtocolConfig.isEnabled());
    }

    @Override
    @Test
    public void testRestApiDefaults() {
        String xml = ((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network><rest-api enabled=\'false\'/></network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        RestApiConfig restApiConfig = config.getNetworkConfig().getRestApiConfig();
        Assert.assertNotNull(restApiConfig);
        Assert.assertFalse(restApiConfig.isEnabled());
        for (RestEndpointGroup group : RestEndpointGroup.values()) {
            Assert.assertEquals(("Unexpected status of group " + group), group.isEnabledByDefault(), restApiConfig.isGroupEnabled(group));
        }
    }

    @Override
    @Test
    public void testRestApiEndpointGroups() {
        String xml = ((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network>\n") + "<rest-api enabled=\'true\'>\n") + "  <endpoint-group name=\'HEALTH_CHECK\' enabled=\'true\'/>\n") + "  <endpoint-group name=\'DATA\' enabled=\'true\'/>\n") + "  <endpoint-group name=\'CLUSTER_READ\' enabled=\'false\'/>\n") + "</rest-api>\n") + "</network>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        RestApiConfig restApiConfig = config.getNetworkConfig().getRestApiConfig();
        Assert.assertTrue(restApiConfig.isEnabled());
        Assert.assertTrue(restApiConfig.isGroupEnabled(RestEndpointGroup.HEALTH_CHECK));
        Assert.assertFalse(restApiConfig.isGroupEnabled(RestEndpointGroup.CLUSTER_READ));
        Assert.assertEquals(CLUSTER_WRITE.isEnabledByDefault(), restApiConfig.isGroupEnabled(CLUSTER_WRITE));
    }

    @Override
    @Test(expected = InvalidConfigurationException.class)
    public void testUnknownRestApiEndpointGroup() {
        String xml = ((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<rest-api enabled=\'true\'>\n") + "  <endpoint-group name=\'TEST\' enabled=\'true\'/>\n") + "</rest-api>\n") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testDefaultAdvancedNetworkConfig() {
        String xml = (((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<advanced-network>") + "</advanced-network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        AdvancedNetworkConfig advancedNetworkConfig = config.getAdvancedNetworkConfig();
        JoinConfig joinConfig = advancedNetworkConfig.getJoin();
        IcmpFailureDetectorConfig fdConfig = advancedNetworkConfig.getIcmpFailureDetectorConfig();
        MemberAddressProviderConfig providerConfig = advancedNetworkConfig.getMemberAddressProviderConfig();
        Assert.assertFalse(advancedNetworkConfig.isEnabled());
        Assert.assertTrue(joinConfig.getMulticastConfig().isEnabled());
        Assert.assertNull(fdConfig);
        Assert.assertFalse(providerConfig.isEnabled());
        Assert.assertTrue(advancedNetworkConfig.getEndpointConfigs().containsKey(MEMBER));
        Assert.assertEquals(1, advancedNetworkConfig.getEndpointConfigs().size());
    }

    @Override
    @Test
    public void testAmbiguousNetworkConfig_throwsException() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <advanced-network enabled=\"true\">\n") + "  </advanced-network>\n") + "  <network>\n") + "    <port>9999</port>\n") + "  </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        expected.expect(InvalidConfigurationException.class);
        buildConfig(xml);
    }

    @Override
    @Test
    public void testNetworkConfigUnambiguous_whenAdvancedNetworkDisabled() {
        String xml = ((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "  <advanced-network>\n") + "  </advanced-network>\n") + "  <network>\n") + "    <port>9999</port>\n") + "  </network>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        Config config = buildConfig(xml);
        Assert.assertFalse(config.getAdvancedNetworkConfig().isEnabled());
        Assert.assertEquals(9999, config.getNetworkConfig().getPort());
    }
}

