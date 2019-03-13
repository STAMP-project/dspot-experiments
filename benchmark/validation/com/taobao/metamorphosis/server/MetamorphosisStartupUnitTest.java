/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server;


import com.taobao.gecko.core.util.ResourcesUtils;
import com.taobao.metamorphosis.server.exception.MetamorphosisServerStartupException;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.server.utils.TopicConfig;
import java.io.File;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MetamorphosisStartupUnitTest {
    @Test(expected = MetamorphosisServerStartupException.class)
    public void testGetConfigFilePathOneArgs() {
        final String[] args = new String[]{ "-f" };
        MetamorphosisStartup.getConfigFilePath(args);
    }

    @Test(expected = MetamorphosisServerStartupException.class)
    public void testGetConfigFilePathBlankArgs() {
        final String[] args = new String[]{ "-f", "" };
        MetamorphosisStartup.getConfigFilePath(args);
    }

    @Test
    public void testGetConfigFilePath() {
        final String[] args = new String[]{ "-f", "server.test" };
        Assert.assertEquals("server.test", MetamorphosisStartup.getConfigFilePath(args));
    }

    @Test
    public void testGetMetaConfig() throws Exception {
        final File file = ResourcesUtils.getResourceAsFile("server.ini");
        final MetaConfig config = MetamorphosisStartup.getMetaConfig(file.getAbsolutePath());
        Assert.assertEquals(1111, config.getBrokerId());
        Assert.assertEquals("test.localhost", config.getHostName());
        Assert.assertEquals(10, config.getNumPartitions());
        Assert.assertEquals(8124, config.getServerPort());
        Assert.assertEquals("/home/admin", config.getDataPath());
        Assert.assertEquals("/home/datalog", config.getDataLogPath());
        Assert.assertEquals(10000, config.getUnflushThreshold());
        Assert.assertEquals(100000, config.getUnflushInterval());
        Assert.assertEquals(((1024 * 1024) * 1024), config.getMaxSegmentSize());
        Assert.assertEquals((1024 * 1024), config.getMaxTransferSize());
        Assert.assertEquals(90, config.getGetProcessThreadCount());
        Assert.assertEquals(90, config.getPutProcessThreadCount());
        Assert.assertEquals(2, config.getTopics().size());
        Assert.assertTrue(config.getTopics().contains("test1"));
        Assert.assertTrue(config.getTopics().contains("test2"));
        final Map<String, TopicConfig> topicConfigs = config.getTopicConfigMap();
        Assert.assertEquals(2, topicConfigs.size());
        Assert.assertEquals(11, topicConfigs.get("test1").getNumPartitions());
        Assert.assertEquals(13, topicConfigs.get("test2").getNumPartitions());
        Assert.assertEquals("delete,77", config.getTopicConfig("test1").getDeletePolicy());
        Assert.assertEquals("127.0.0.1:2181", config.getZkConfig().zkConnect);
        Assert.assertEquals(30000, config.getZkConfig().zkSessionTimeoutMs);
        Assert.assertEquals(40000, config.getZkConfig().zkConnectionTimeoutMs);
        Assert.assertEquals(5000, config.getZkConfig().zkSyncTimeMs);
        Assert.assertEquals("delete,999", config.getDeletePolicy());
        final TopicConfig topicConfig1 = config.getTopicConfig("test1");
        final TopicConfig topicConfig2 = config.getTopicConfig("test2");
        Assert.assertEquals("/home/admin", topicConfig1.getDataPath());
        Assert.assertEquals("/test2", topicConfig2.getDataPath());
        Assert.assertFalse(topicConfig1.isStat());
        Assert.assertTrue(topicConfig2.isStat());
    }
}

