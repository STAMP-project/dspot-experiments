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
package com.taobao.metamorphosis.metaslave;


import com.taobao.metamorphosis.client.RemotingClientWrapper;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.ConsumerZooKeeperAccessor;
import com.taobao.metamorphosis.client.consumer.FetchManager;
import com.taobao.metamorphosis.client.consumer.LoadBalanceStrategy;
import com.taobao.metamorphosis.client.consumer.SubscriberInfo;
import com.taobao.metamorphosis.client.consumer.TopicPartitionRegInfo;
import com.taobao.metamorphosis.client.consumer.storage.OffsetStorage;
import com.taobao.metamorphosis.cluster.Broker;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.cluster.json.TopicBroker;
import com.taobao.metamorphosis.metaslave.SlaveConsumerZooKeeper.SlaveZKLoadRebalanceListener;
import com.taobao.metamorphosis.utils.MetaZookeeper;
import com.taobao.metamorphosis.utils.ZkUtils;
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.I0Itec.zkclient.ZkClient;
import org.easymock.classextension.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-6-29 ????05:00:22
 */
public class SlaveConsumerZooKeeperUnitTest {
    private static final String GROUP = "meta-slave-group";

    private ZKConfig zkConfig;

    private ZkClient client;

    // private DiamondManager diamondManager;
    private RemotingClientWrapper remotingClient;

    private FetchManager fetchManager;

    private OffsetStorage offsetStorage;

    private IMocksControl mocksControl;

    private SlaveConsumerZooKeeper slaveConsumerZooKeeper;

    private final int brokerId = 0;

    private LoadBalanceStrategy loadBalanceStrategy;

    private MetaZookeeper metaZookeeper;

    @Test
    public void testReigsterSlaveConsumer() throws Exception {
        final ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setGroup(SlaveConsumerZooKeeperUnitTest.GROUP);
        final ConcurrentHashMap<String, SubscriberInfo> topicSubcriberRegistry = new ConcurrentHashMap<String, SubscriberInfo>();
        topicSubcriberRegistry.put("topic1", new SubscriberInfo(null, null, (1024 * 1024)));
        topicSubcriberRegistry.put("topic2", new SubscriberInfo(null, null, (1024 * 1024)));
        // ????????????master,topic1??master????3??????;
        // topic2??master????1??????,?????????????master????1??????
        ZkUtils.createEphemeralPath(this.client, ((this.metaZookeeper.brokerIdsPath) + "/0/master"), "meta://localhost:0");
        ZkUtils.createEphemeralPath(this.client, ((this.metaZookeeper.brokerIdsPath) + "/1/master"), "meta://localhost:1");
        this.client.createEphemeral(((this.metaZookeeper.brokerTopicsSubPath) + "/topic1/0-m"), new TopicBroker(3, "0-m").toJson());
        this.client.createEphemeral(((this.metaZookeeper.brokerTopicsSubPath) + "/topic2/0-m"), new TopicBroker(1, "0-m").toJson());
        this.client.createEphemeral(((this.metaZookeeper.brokerTopicsSubPath) + "/topic2/1-m"), new TopicBroker(1, "1-m").toJson());
        this.mockConnect("meta://localhost:0");
        // this.mockConnect("meta://localhost:1");??????????????master
        this.mockCommitOffsets(SlaveConsumerZooKeeperUnitTest.GROUP, new ArrayList<TopicPartitionRegInfo>());
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-0"));
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-1"));
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-2"));
        this.mockLoadNullInitOffset("topic2", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-0"));
        // this.mockLoadNullInitOffset("topic2", GROUP, new
        // Partition("1-0"));??load???????master?????
        this.mockFetchManagerRestart();
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-0"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-1"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-2"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic2", new Partition("0-0"), 0), (1024 * 1024)));
        // this.mockAddFetchRequest(new FetchRequest(new Broker(1,
        // "meta://localhost:1"), 0, new TopicPartitionRegInfo(
        // "topic2", new Partition("1-0"), 0), 1024 * 1024));???????????master?????
        this.mocksControl.replay();
        this.slaveConsumerZooKeeper.registerConsumer(consumerConfig, this.fetchManager, topicSubcriberRegistry, this.offsetStorage, this.loadBalanceStrategy);
        this.mocksControl.verify();
        // ????????????,????????master????????????
        final SlaveZKLoadRebalanceListener listener = this.checkTopic();
        final Set<Broker> brokerSet = ConsumerZooKeeperAccessor.getOldBrokerSet(listener);
        Assert.assertEquals(1, brokerSet.size());
        Assert.assertTrue(brokerSet.contains(new Broker(0, "meta://localhost:0")));
        Assert.assertFalse(brokerSet.contains(new Broker(1, "meta://localhost:1")));
    }

    @Test
    public void testReigsterSlaveConsumer_thenMasterDown() throws Exception {
        this.testReigsterSlaveConsumer();
        this.mocksControl.reset();
        this.mockCommitOffsets(SlaveConsumerZooKeeperUnitTest.GROUP, ConsumerZooKeeperAccessor.getTopicPartitionRegInfos(this.slaveConsumerZooKeeper, this.fetchManager));
        this.mockFetchManagerRestartAnyTimes();
        this.mockConnectCloseAnyTimes("meta://localhost:0");
        this.mocksControl.replay();
        // master down
        ZkUtils.deletePath(this.client, ((this.metaZookeeper.brokerIdsPath) + "/0/master"));
        // ????topic?????????(???????????),???????????????????
        ZkUtils.deletePath(this.client, ((this.metaZookeeper.brokerTopicsSubPath) + "/topic1/0-m"));
        ZkUtils.deletePath(this.client, ((this.metaZookeeper.brokerTopicsSubPath) + "/topic2/0-m"));
        Thread.sleep(5000);
        this.mocksControl.verify();
        // master ???????????,TopicPartitionRegInfo?????
        final SlaveZKLoadRebalanceListener listener = ((SlaveZKLoadRebalanceListener) (ConsumerZooKeeperAccessor.getBrokerConnectionListenerForTest(this.slaveConsumerZooKeeper, this.fetchManager)));
        Assert.assertNotNull(listener);
        final ConcurrentHashMap<String, ConcurrentHashMap<Partition, TopicPartitionRegInfo>> topicRegistry = ConsumerZooKeeperAccessor.getTopicRegistry(listener);
        Assert.assertNotNull(topicRegistry);
        Assert.assertTrue(topicRegistry.isEmpty());
        final Set<Broker> brokerSet = ConsumerZooKeeperAccessor.getOldBrokerSet(listener);
        Assert.assertEquals(0, brokerSet.size());
        Assert.assertFalse(brokerSet.contains(new Broker(0, "meta://localhost:0")));
        Assert.assertFalse(brokerSet.contains(new Broker(1, "meta://localhost:1")));
    }

    @Test
    public void testReigsterSlaveConsumer_thenMasterDown_thenMasterStart() throws Exception {
        this.testReigsterSlaveConsumer_thenMasterDown();
        this.mocksControl.reset();
        this.mockConnect("meta://localhost:0");
        this.mockCommitOffsets(SlaveConsumerZooKeeperUnitTest.GROUP, new ArrayList<TopicPartitionRegInfo>());
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-0"));
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-1"));
        this.mockLoadNullInitOffset("topic1", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-2"));
        this.mockLoadNullInitOffset("topic2", SlaveConsumerZooKeeperUnitTest.GROUP, new Partition("0-0"));
        this.mockFetchManagerRestart();
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-0"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-1"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic1", new Partition("0-2"), 0), (1024 * 1024)));
        this.mockAddFetchRequest(new com.taobao.metamorphosis.client.consumer.FetchRequest(new Broker(0, "meta://localhost:0"), 0, new TopicPartitionRegInfo("topic2", new Partition("0-0"), 0), (1024 * 1024)));
        this.mocksControl.replay();
        ZkUtils.createEphemeralPath(this.client, ((this.metaZookeeper.brokerIdsPath) + "/0/master"), "meta://localhost:0");
        this.client.createEphemeral(((this.metaZookeeper.brokerTopicsSubPath) + "/topic1/0-m"), new TopicBroker(3, "0-m").toJson());
        this.client.createEphemeral(((this.metaZookeeper.brokerTopicsSubPath) + "/topic2/0-m"), new TopicBroker(1, "0-m").toJson());
        Thread.sleep(5000);
        this.mocksControl.verify();
        // ?????testReigsterSlaveConsumer?????
        final SlaveZKLoadRebalanceListener listener = this.checkTopic();
        final Set<Broker> brokerSet = ConsumerZooKeeperAccessor.getOldBrokerSet(listener);
        Assert.assertEquals(1, brokerSet.size());
        Assert.assertTrue(brokerSet.contains(new Broker(0, "meta://localhost:0")));
        Assert.assertFalse(brokerSet.contains(new Broker(1, "meta://localhost:1")));
    }

    @Test
    public void testReigsterSlaveConsumer_thenOtherMasterDown() throws Exception {
        this.testReigsterSlaveConsumer();
        this.mocksControl.reset();
        // mock nothing
        this.mocksControl.replay();
        // other master down,no problem
        ZkUtils.deletePath(this.client, ((this.metaZookeeper.brokerIdsPath) + "/1/master"));
        ZkUtils.deletePath(this.client, ((this.metaZookeeper.brokerTopicsSubPath) + "/topic2/1-m"));
        Thread.sleep(5000);
        this.mocksControl.verify();
        final SlaveZKLoadRebalanceListener listener = this.checkTopic();
        final Set<Broker> brokerSet = ConsumerZooKeeperAccessor.getOldBrokerSet(listener);
        Assert.assertEquals(1, brokerSet.size());
        Assert.assertTrue(brokerSet.contains(new Broker(0, "meta://localhost:0")));
        Assert.assertFalse(brokerSet.contains(new Broker(1, "meta://localhost:1")));
    }
}

