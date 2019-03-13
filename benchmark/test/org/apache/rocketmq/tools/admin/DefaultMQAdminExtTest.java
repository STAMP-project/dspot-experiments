/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.tools.admin;


import ConsumeType.CONSUME_PASSIVELY;
import MessageModel.CLUSTERING;
import NamesrvUtil.NAMESPACE_ORDER_TOPIC_CONFIG;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.body.ConsumeStatsList;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.body.GroupList;
import org.apache.rocketmq.common.protocol.body.KVTable;
import org.apache.rocketmq.common.protocol.body.ProducerConnection;
import org.apache.rocketmq.common.protocol.body.QueueTimeSpan;
import org.apache.rocketmq.common.protocol.body.SubscriptionGroupWrapper;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.common.protocol.route.TopicRouteData;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.tools.admin.api.MessageTrack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQAdminExtTest {
    private static DefaultMQAdminExt defaultMQAdminExt;

    private static DefaultMQAdminExtImpl defaultMQAdminExtImpl;

    private static MQClientInstance mqClientInstance = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    private static MQClientAPIImpl mQClientAPIImpl;

    private static Properties properties = new Properties();

    private static TopicList topicList = new TopicList();

    private static TopicRouteData topicRouteData = new TopicRouteData();

    private static KVTable kvTable = new KVTable();

    private static ClusterInfo clusterInfo = new ClusterInfo();

    @Test
    public void testUpdateBrokerConfig() throws UnsupportedEncodingException, InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        Properties result = DefaultMQAdminExtTest.defaultMQAdminExt.getBrokerConfig("127.0.0.1:10911");
        assertThat(result.getProperty("maxMessageSize")).isEqualTo("5000000");
        assertThat(result.getProperty("flushDelayOffsetInterval")).isEqualTo("15000");
        assertThat(result.getProperty("serverSocketRcvBufSize")).isEqualTo("655350");
    }

    @Test
    public void testFetchAllTopicList() throws InterruptedException, MQClientException, RemotingException {
        TopicList topicList = DefaultMQAdminExtTest.defaultMQAdminExt.fetchAllTopicList();
        assertThat(topicList.getTopicList().size()).isEqualTo(2);
        assertThat(topicList.getTopicList()).contains("topic_one");
    }

    @Test
    public void testFetchBrokerRuntimeStats() throws InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        KVTable brokerStats = DefaultMQAdminExtTest.defaultMQAdminExt.fetchBrokerRuntimeStats("127.0.0.1:10911");
        assertThat(brokerStats.getTable().get("id")).isEqualTo("1234");
        assertThat(brokerStats.getTable().get("brokerName")).isEqualTo("default-broker");
    }

    @Test
    public void testExamineBrokerClusterInfo() throws InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        ClusterInfo clusterInfo = DefaultMQAdminExtTest.defaultMQAdminExt.examineBrokerClusterInfo();
        HashMap<String, BrokerData> brokerList = clusterInfo.getBrokerAddrTable();
        assertThat(brokerList.get("default-broker").getBrokerName()).isEqualTo("default-broker");
        assertThat(brokerList.containsKey("broker-test")).isTrue();
        HashMap<String, Set<String>> clusterMap = new HashMap<>();
        Set<String> brokers = new HashSet<>();
        brokers.add("default-broker");
        brokers.add("broker-test");
        clusterMap.put("default-cluster", brokers);
        ClusterInfo cInfo = Mockito.mock(ClusterInfo.class);
        Mockito.when(cInfo.getClusterAddrTable()).thenReturn(clusterMap);
        HashMap<String, Set<String>> clusterAddress = cInfo.getClusterAddrTable();
        assertThat(clusterAddress.containsKey("default-cluster")).isTrue();
        assertThat(clusterAddress.get("default-cluster").size()).isEqualTo(2);
    }

    @Test
    public void testExamineConsumeStats() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        ConsumeStats consumeStats = DefaultMQAdminExtTest.defaultMQAdminExt.examineConsumeStats("default-consumer-group", "unit-test");
        assertThat(consumeStats.getConsumeTps()).isEqualTo(1234);
    }

    @Test
    public void testExamineConsumerConnectionInfo() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        ConsumerConnection consumerConnection = DefaultMQAdminExtTest.defaultMQAdminExt.examineConsumerConnectionInfo("default-consumer-group");
        assertThat(consumerConnection.getConsumeType()).isEqualTo(CONSUME_PASSIVELY);
        assertThat(consumerConnection.getMessageModel()).isEqualTo(CLUSTERING);
    }

    @Test
    public void testExamineProducerConnectionInfo() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        ProducerConnection producerConnection = DefaultMQAdminExtTest.defaultMQAdminExt.examineProducerConnectionInfo("default-producer-group", "unit-test");
        assertThat(producerConnection.getConnectionSet().size()).isEqualTo(1);
    }

    @Test
    public void testWipeWritePermOfBroker() throws InterruptedException, MQClientException, RemotingCommandException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        int result = DefaultMQAdminExtTest.defaultMQAdminExt.wipeWritePermOfBroker("127.0.0.1:9876", "default-broker");
        assertThat(result).isEqualTo(6);
    }

    @Test
    public void testExamineTopicRouteInfo() throws InterruptedException, MQClientException, RemotingException {
        TopicRouteData topicRouteData = DefaultMQAdminExtTest.defaultMQAdminExt.examineTopicRouteInfo("UnitTest");
        assertThat(topicRouteData.getBrokerDatas().get(0).getBrokerName()).isEqualTo("default-broker");
        assertThat(topicRouteData.getBrokerDatas().get(0).getCluster()).isEqualTo("default-cluster");
    }

    @Test
    public void testGetNameServerAddressList() {
        List<String> result = new ArrayList<>();
        result.add("default-name-one");
        result.add("default-name-two");
        Mockito.when(DefaultMQAdminExtTest.mqClientInstance.getMQClientAPIImpl().getNameServerAddressList()).thenReturn(result);
        List<String> nameList = DefaultMQAdminExtTest.defaultMQAdminExt.getNameServerAddressList();
        assertThat(nameList.get(0)).isEqualTo("default-name-one");
        assertThat(nameList.get(1)).isEqualTo("default-name-two");
    }

    @Test
    public void testPutKVConfig() throws InterruptedException, MQClientException, RemotingException {
        String topicConfig = DefaultMQAdminExtTest.defaultMQAdminExt.getKVConfig(NAMESPACE_ORDER_TOPIC_CONFIG, "UnitTest");
        assertThat(topicConfig).isEqualTo("topicListConfig");
        KVTable kvs = DefaultMQAdminExtTest.defaultMQAdminExt.getKVListByNamespace(NAMESPACE_ORDER_TOPIC_CONFIG);
        assertThat(kvs.getTable().get("broker-name")).isEqualTo("broker-one");
        assertThat(kvs.getTable().get("cluster-name")).isEqualTo("default-cluster");
    }

    @Test
    public void testQueryTopicConsumeByWho() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        GroupList groupList = DefaultMQAdminExtTest.defaultMQAdminExt.queryTopicConsumeByWho("UnitTest");
        assertThat(groupList.getGroupList().contains("consumer-group-two")).isTrue();
    }

    @Test
    public void testQueryConsumeTimeSpan() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        List<QueueTimeSpan> result = DefaultMQAdminExtTest.defaultMQAdminExt.queryConsumeTimeSpan("unit-test", "default-broker-group");
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testCleanExpiredConsumerQueue() throws InterruptedException, MQClientException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        boolean result = DefaultMQAdminExtTest.defaultMQAdminExt.cleanExpiredConsumerQueue("default-cluster");
        assertThat(result).isFalse();
    }

    @Test
    public void testCleanExpiredConsumerQueueByAddr() throws InterruptedException, MQClientException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        boolean clean = DefaultMQAdminExtTest.defaultMQAdminExt.cleanExpiredConsumerQueueByAddr("127.0.0.1:10911");
        assertThat(clean).isTrue();
    }

    @Test
    public void testCleanUnusedTopic() throws InterruptedException, MQClientException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        boolean result = DefaultMQAdminExtTest.defaultMQAdminExt.cleanUnusedTopic("default-cluster");
        assertThat(result).isFalse();
    }

    @Test
    public void testGetConsumerRunningInfo() throws InterruptedException, MQClientException, RemotingException {
        ConsumerRunningInfo consumerRunningInfo = DefaultMQAdminExtTest.defaultMQAdminExt.getConsumerRunningInfo("consumer-group", "cid_123", false);
        assertThat(consumerRunningInfo.getJstack()).isEqualTo("test");
    }

    @Test
    public void testMessageTrackDetail() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        MessageExt messageExt = new MessageExt();
        messageExt.setMsgId("msgId");
        messageExt.setTopic("unit-test");
        List<MessageTrack> messageTrackList = DefaultMQAdminExtTest.defaultMQAdminExt.messageTrackDetail(messageExt);
        assertThat(messageTrackList.size()).isEqualTo(2);
    }

    @Test
    public void testGetConsumeStatus() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Map<String, Map<MessageQueue, Long>> result = DefaultMQAdminExtTest.defaultMQAdminExt.getConsumeStatus("unit-test", "default-broker-group", "127.0.0.1:10911");
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testGetTopicClusterList() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Set<String> result = DefaultMQAdminExtTest.defaultMQAdminExt.getTopicClusterList("unit-test");
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testGetClusterList() throws InterruptedException, MQClientException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        Set<String> clusterlist = DefaultMQAdminExtTest.defaultMQAdminExt.getClusterList("UnitTest");
        assertThat(clusterlist.contains("default-cluster-one")).isTrue();
        assertThat(clusterlist.contains("default-cluster-two")).isTrue();
    }

    @Test
    public void testFetchConsumeStatsInBroker() throws InterruptedException, MQClientException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        ConsumeStatsList result = new ConsumeStatsList();
        result.setBrokerAddr("127.0.0.1:10911");
        Mockito.when(DefaultMQAdminExtTest.mqClientInstance.getMQClientAPIImpl().fetchConsumeStatsInBroker("127.0.0.1:10911", false, 10000)).thenReturn(result);
        ConsumeStatsList consumeStatsList = DefaultMQAdminExtTest.defaultMQAdminExt.fetchConsumeStatsInBroker("127.0.0.1:10911", false, 10000);
        assertThat(consumeStatsList.getBrokerAddr()).isEqualTo("127.0.0.1:10911");
    }

    @Test
    public void testGetAllSubscriptionGroup() throws InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        SubscriptionGroupWrapper subscriptionGroupWrapper = DefaultMQAdminExtTest.defaultMQAdminExt.getAllSubscriptionGroup("127.0.0.1:10911", 10000);
        assertThat(subscriptionGroupWrapper.getSubscriptionGroupTable().get("Consumer-group-one").getBrokerId()).isEqualTo(1234);
        assertThat(subscriptionGroupWrapper.getSubscriptionGroupTable().get("Consumer-group-one").getGroupName()).isEqualTo("Consumer-group-one");
        assertThat(subscriptionGroupWrapper.getSubscriptionGroupTable().get("Consumer-group-one").isConsumeBroadcastEnable()).isTrue();
    }
}

