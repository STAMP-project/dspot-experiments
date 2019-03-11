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


import com.taobao.metamorphosis.network.RemotingUtils;
import com.taobao.metamorphosis.utils.JSONUtils;
import com.taobao.metamorphosis.utils.ZkUtils;
import java.util.Map;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.junit.Assert;
import org.junit.Test;


public class BrokerZooKeeperUnitTest {
    private ZkClient client;

    private BrokerZooKeeper brokerZooKeeper;

    private BrokerZooKeeper slaveBrokerZooKeeper;

    @Test
    public void testRegisterBrokerInZk_master() throws Exception {
        String path = "/meta/brokers/ids/0/master";
        Assert.assertFalse(ZkUtils.pathExists(this.client, path));
        this.brokerZooKeeper.registerBrokerInZk();
        Assert.assertTrue(ZkUtils.pathExists(this.client, path));
        Assert.assertEquals((("meta://" + (RemotingUtils.getLocalHost())) + ":8123"), ZkUtils.readData(this.client, path));
        // register twice
        try {
            this.brokerZooKeeper.registerBrokerInZk();
            Assert.fail();
        } catch (ZkNodeExistsException e) {
        }
    }

    @Test
    public void testRegisterMasterConfigFileChecksumInZk() throws Exception {
        String path = "/meta/brokers/ids/0/master_config_checksum";
        Assert.assertFalse(ZkUtils.pathExists(this.client, path));
        this.brokerZooKeeper.registerMasterConfigFileChecksumInZk();
        Assert.assertTrue(ZkUtils.pathExists(this.client, path));
        Assert.assertEquals(String.valueOf(this.brokerZooKeeper.getConfig().getConfigFileChecksum()), ZkUtils.readData(this.client, path));
        // register twice
        try {
            this.brokerZooKeeper.registerMasterConfigFileChecksumInZk();
            Assert.fail();
        } catch (ZkNodeExistsException e) {
        }
    }

    @Test
    public void testConfigFileChecksumChanged() throws Exception {
        String path = "/meta/brokers/ids/0/master_config_checksum";
        this.brokerZooKeeper.registerMasterConfigFileChecksumInZk();
        long old = this.brokerZooKeeper.getConfig().getConfigFileChecksum();
        Assert.assertEquals(String.valueOf(old), ZkUtils.readData(this.client, path));
        // set a new value
        int newValue = 9999;
        this.brokerZooKeeper.getConfig().setConfigFileChecksum(newValue);
        Thread.sleep(1000);
        Assert.assertEquals(String.valueOf(newValue), ZkUtils.readData(this.client, path));
        Assert.assertFalse((newValue == old));
    }

    @Test
    public void testRegisterBrokerInZk_slave() throws Exception {
        String path = "/meta/brokers/ids/0/slave0";
        this.createSlaveBrokerZooKeeper();
        Assert.assertFalse(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), path));
        this.slaveBrokerZooKeeper.registerBrokerInZk();
        Assert.assertTrue(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), path));
        Assert.assertEquals((("meta://" + (RemotingUtils.getLocalHost())) + ":8123"), ZkUtils.readData(this.slaveBrokerZooKeeper.getZkClient(), path));
        // register twice
        try {
            this.slaveBrokerZooKeeper.registerBrokerInZk();
            Assert.fail();
        } catch (ZkNodeExistsException e) {
        }
    }

    @Test
    public void testRegisterTopicInZk() throws Exception {
        final String topic = "test";
        final String path = "/meta/brokers/topics/test/0-m";
        final String pubPath = "/meta/brokers/topics-pub/test/0-m";
        final String subPath = "/meta/brokers/topics-sub/test/0-m";
        Assert.assertFalse(ZkUtils.pathExists(this.client, path));
        Assert.assertFalse(ZkUtils.pathExists(this.client, pubPath));
        Assert.assertFalse(ZkUtils.pathExists(this.client, subPath));
        this.brokerZooKeeper.registerTopicInZk(topic, false);
        Assert.assertTrue(ZkUtils.pathExists(this.client, path));
        Assert.assertTrue(ZkUtils.pathExists(this.client, pubPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, subPath));
        Assert.assertEquals("1", ZkUtils.readData(this.client, path));
        Assert.assertEquals(this.deserializeMap("{\"numParts\":1,\"broker\":\"0-m\"}"), JSONUtils.deserializeObject(ZkUtils.readData(this.client, pubPath), Map.class));
        Assert.assertEquals(this.deserializeMap("{\"numParts\":1,\"broker\":\"0-m\"}"), JSONUtils.deserializeObject(ZkUtils.readData(this.client, subPath), Map.class));
    }

    @Test
    public void testRegisterTopicInZk_slave() throws Exception {
        final String topic = "test";
        final String path = "/meta/brokers/topics/test/0-s0";
        final String pubPath = "/meta/brokers/topics-pub/test/0-s0";
        final String subPath = "/meta/brokers/topics-sub/test/0-s0";
        Assert.assertFalse(ZkUtils.pathExists(this.client, path));
        Assert.assertFalse(ZkUtils.pathExists(this.client, pubPath));
        Assert.assertFalse(ZkUtils.pathExists(this.client, subPath));
        this.createSlaveBrokerZooKeeper();
        this.slaveBrokerZooKeeper.registerTopicInZk(topic, false);
        Assert.assertTrue(ZkUtils.pathExists(this.client, path));
        Assert.assertTrue(ZkUtils.pathExists(this.client, pubPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, subPath));
        Assert.assertEquals("1", ZkUtils.readData(this.client, path));
        Assert.assertEquals(this.deserializeMap("{\"numParts\":1,\"broker\":\"0-s0\"}"), this.deserializeMap(ZkUtils.readData(this.client, pubPath)));
        Assert.assertEquals(this.deserializeMap("{\"numParts\":1,\"broker\":\"0-s0\"}"), this.deserializeMap(ZkUtils.readData(this.client, subPath)));
    }

    @Test
    public void testMasterClose_slaveNotClose() throws Exception {
        String masterBrokerPath = "/meta/brokers/ids/0/master";
        String masterTopicPath = "/meta/brokers/topics/test/0-m";
        String slaveBrokerPath = "/meta/brokers/ids/0/slave0";
        String slaveTopicPath = "/meta/brokers/topics/test/0-s0";
        this.testRegisterBrokerInZk_master();
        this.testRegisterTopicInZk();
        this.testRegisterBrokerInZk_slave();
        this.testRegisterTopicInZk_slave();
        this.brokerZooKeeper.close(true);
        this.brokerZooKeeper = null;
        // master????????????
        Assert.assertFalse(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), masterBrokerPath));
        Assert.assertFalse(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), masterTopicPath));
        // slave????????????
        Assert.assertTrue(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), slaveBrokerPath));
        Assert.assertEquals((("meta://" + (RemotingUtils.getLocalHost())) + ":8123"), ZkUtils.readData(this.slaveBrokerZooKeeper.getZkClient(), slaveBrokerPath));
        Assert.assertTrue(ZkUtils.pathExists(this.slaveBrokerZooKeeper.getZkClient(), slaveTopicPath));
        Assert.assertEquals("1", ZkUtils.readData(this.slaveBrokerZooKeeper.getZkClient(), slaveTopicPath));
    }

    @Test
    public void testSlaveClose_masterNotClose() throws Exception {
        String masterBrokerPath = "/meta/brokers/ids/0/master";
        String masterTopicPath = "/meta/brokers/topics/test/0-m";
        String slaveBrokerPath = "/meta/brokers/ids/0/slave0";
        String slaveTopicPath = "/meta/brokers/topics/test/0-s0";
        this.testRegisterBrokerInZk_master();
        this.testRegisterTopicInZk();
        this.testRegisterBrokerInZk_slave();
        this.testRegisterTopicInZk_slave();
        this.slaveBrokerZooKeeper.close(true);
        this.slaveBrokerZooKeeper = null;
        // slave????????????
        Assert.assertFalse(ZkUtils.pathExists(this.brokerZooKeeper.getZkClient(), slaveBrokerPath));
        Assert.assertFalse(ZkUtils.pathExists(this.brokerZooKeeper.getZkClient(), slaveTopicPath));
        // master????????????
        Assert.assertTrue(ZkUtils.pathExists(this.brokerZooKeeper.getZkClient(), masterBrokerPath));
        Assert.assertEquals((("meta://" + (RemotingUtils.getLocalHost())) + ":8123"), ZkUtils.readData(this.brokerZooKeeper.getZkClient(), masterBrokerPath));
        Assert.assertTrue(ZkUtils.pathExists(this.brokerZooKeeper.getZkClient(), masterTopicPath));
        Assert.assertEquals("1", ZkUtils.readData(this.brokerZooKeeper.getZkClient(), masterTopicPath));
    }

    @Test
    public void testReRegisterEverthing() throws Exception {
        this.testRegisterBrokerInZk_master();
        this.testRegisterTopicInZk();
        final String brokerPath = "/meta/brokers/ids/0/master";
        final String topicPath = "/meta/brokers/topics/test/0-m";
        final String topicPubPath = "/meta/brokers/topics-pub/test/0-m";
        final String topicSubPath = "/meta/brokers/topics-sub/test/0-m";
        Assert.assertTrue(ZkUtils.pathExists(this.client, brokerPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicPubPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicSubPath));
        ZkUtils.deletePath(this.client, brokerPath);
        ZkUtils.deletePath(this.client, topicPath);
        ZkUtils.deletePath(this.client, topicPubPath);
        ZkUtils.deletePath(this.client, topicSubPath);
        Assert.assertFalse(ZkUtils.pathExists(this.client, brokerPath));
        Assert.assertFalse(ZkUtils.pathExists(this.client, topicPath));
        Assert.assertFalse(ZkUtils.pathExists(this.client, topicPubPath));
        Assert.assertFalse(ZkUtils.pathExists(this.client, topicSubPath));
        this.brokerZooKeeper.reRegisterEveryThing();
        Assert.assertTrue(ZkUtils.pathExists(this.client, brokerPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicPubPath));
        Assert.assertTrue(ZkUtils.pathExists(this.client, topicSubPath));
    }
}

