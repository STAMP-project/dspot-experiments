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
package org.apache.rocketmq.console.service.impl;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.common.protocol.body.Connection;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.console.model.ConsumerGroupRollBackStat;
import org.apache.rocketmq.console.model.GroupConsumeInfo;
import org.apache.rocketmq.console.model.TopicConsumerInfo;
import org.apache.rocketmq.console.model.request.ConsumerConfigInfo;
import org.apache.rocketmq.console.model.request.DeleteSubGroupRequest;
import org.apache.rocketmq.console.model.request.ResetOffsetRequest;
import org.apache.rocketmq.console.testbase.RocketMQConsoleTestBase;
import org.apache.rocketmq.console.testbase.TestConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class ConsumerServiceImplTest extends RocketMQConsoleTestBase {
    @Test
    public void queryGroupList() throws Exception {
        List<GroupConsumeInfo> consumeInfoList = consumerService.queryGroupList();
        Assert.assertTrue(CollectionUtils.isNotEmpty(consumeInfoList));
    }

    @Test
    public void queryGroup() throws Exception {
        GroupConsumeInfo consumeInfo = consumerService.queryGroup(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
        // if (consumeInfo.getCount() < 1) {
        // Thread.sleep(2000);
        // continue;
        // }
        Assert.assertNotNull(consumeInfo);
        Assert.assertEquals(consumeInfo.getGroup(), RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
        // Assert.assertTrue(consumeInfo.getCount() == 1);
    }

    @Test
    public void queryConsumeStatsListByGroupName() throws Exception {
        // todo can't use if don't consume a message
        List<TopicConsumerInfo> topicConsumerInfoList = new RocketMQConsoleTestBase.RetryTempLate<List<TopicConsumerInfo>>() {
            @Override
            protected List<TopicConsumerInfo> process() throws Exception {
                return consumerService.queryConsumeStatsListByGroupName(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
            }
        }.execute(10, 2000);
        Assert.assertNotNull(topicConsumerInfoList);
        Assert.assertTrue(CollectionUtils.isNotEmpty(topicConsumerInfoList));
    }

    @Test
    public void queryConsumeStatsList() throws Exception {
        List<TopicConsumerInfo> topicConsumerInfoList = new RocketMQConsoleTestBase.RetryTempLate<List<TopicConsumerInfo>>() {
            @Override
            protected List<TopicConsumerInfo> process() throws Exception {
                return consumerService.queryConsumeStatsList(RocketMQConsoleTestBase.TEST_CONSOLE_TOPIC, RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
            }
        }.execute(10, 2000);
        Assert.assertNotNull(topicConsumerInfoList);
        Assert.assertTrue(CollectionUtils.isNotEmpty(topicConsumerInfoList));
    }

    @Test
    public void queryConsumeStatsListByTopicName() throws Exception {
        Map<String, TopicConsumerInfo> consumeTopicMap = new RocketMQConsoleTestBase.RetryTempLate<Map<String, TopicConsumerInfo>>() {
            @Override
            protected Map<String, TopicConsumerInfo> process() throws Exception {
                return consumerService.queryConsumeStatsListByTopicName(RocketMQConsoleTestBase.TEST_CONSOLE_TOPIC);
            }
        }.execute(10, 2000);
        Assert.assertNotNull(consumeTopicMap);
        Assert.assertNotNull(consumeTopicMap.get(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP));
    }

    @Test
    public void resetOffset() throws Exception {
        ResetOffsetRequest resetOffsetRequest = new ResetOffsetRequest();
        resetOffsetRequest.setConsumerGroupList(Lists.<String>newArrayList(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP));
        resetOffsetRequest.setForce(true);
        resetOffsetRequest.setTopic(RocketMQConsoleTestBase.TEST_CONSOLE_TOPIC);
        resetOffsetRequest.setResetTime(((System.currentTimeMillis()) - 1000));
        Map<String, ConsumerGroupRollBackStat> consumerGroupRollBackStatMap = consumerService.resetOffset(resetOffsetRequest);
        Assert.assertNotNull(consumerGroupRollBackStatMap);
        Assert.assertNotNull(consumerGroupRollBackStatMap.get(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP));
    }

    @Test
    public void examineSubscriptionGroupConfig() throws Exception {
        List<ConsumerConfigInfo> configInfoList = consumerService.examineSubscriptionGroupConfig(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
        Assert.assertTrue(((configInfoList.size()) == 1));
        Assert.assertTrue(configInfoList.get(0).getSubscriptionGroupConfig().getGroupName().equals(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP));
        Assert.assertTrue(((configInfoList.get(0).getSubscriptionGroupConfig().getRetryQueueNums()) == (RocketMQConsoleTestBase.RETRY_QUEUE_NUMS)));
    }

    @Test
    public void deleteSubGroup() throws Exception {
        createAndUpdateSubscriptionGroupConfig();
        DeleteSubGroupRequest deleteSubGroupRequest = new DeleteSubGroupRequest();
        deleteSubGroupRequest.setBrokerNameList(Lists.<String>newArrayList(TestConstant.TEST_BROKER_NAME));
        deleteSubGroupRequest.setGroupName(RocketMQConsoleTestBase.TEST_CREATE_DELETE_CONSUMER_GROUP);
        Assert.assertTrue(consumerService.deleteSubGroup(deleteSubGroupRequest));
        List<ConsumerConfigInfo> groupConsumeInfoList = consumerService.examineSubscriptionGroupConfig(RocketMQConsoleTestBase.TEST_CREATE_DELETE_CONSUMER_GROUP);
        Assert.assertTrue(CollectionUtils.isEmpty(groupConsumeInfoList));
    }

    @Test
    public void createAndUpdateSubscriptionGroupConfig() throws Exception {
        ConsumerConfigInfo consumerConfigInfoForCreate = new ConsumerConfigInfo();
        BeanUtils.copyProperties(consumerConfigInfo, consumerConfigInfoForCreate);
        consumerConfigInfoForCreate.getSubscriptionGroupConfig().setGroupName(RocketMQConsoleTestBase.TEST_CREATE_DELETE_CONSUMER_GROUP);
        Assert.assertTrue(consumerService.createAndUpdateSubscriptionGroupConfig(consumerConfigInfoForCreate));
        Assert.assertTrue(CollectionUtils.isNotEmpty(consumerService.examineSubscriptionGroupConfig(RocketMQConsoleTestBase.TEST_CREATE_DELETE_CONSUMER_GROUP)));
    }

    @Test
    public void fetchBrokerNameSetBySubscriptionGroup() throws Exception {
        Set<String> xx = consumerService.fetchBrokerNameSetBySubscriptionGroup(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
        Assert.assertTrue(xx.contains(TestConstant.TEST_BROKER_NAME));
    }

    @Test
    public void getConsumerConnection() throws Exception {
        ConsumerConnection consumerConnection = new RocketMQConsoleTestBase.RetryTempLate<ConsumerConnection>() {
            @Override
            protected ConsumerConnection process() throws Exception {
                return consumerService.getConsumerConnection(RocketMQConsoleTestBase.TEST_CONSUMER_GROUP);
            }
        }.execute(10, 2000);
        Assert.assertNotNull(consumerConnection);
        Assert.assertTrue(CollectionUtils.isNotEmpty(consumerConnection.getConnectionSet()));
        Assert.assertTrue(Lists.transform(Lists.newArrayList(consumerConnection.getConnectionSet()), new Function<Connection, String>() {
            @Override
            public String apply(Connection input) {
                return input.getClientAddr().split(":")[0];
            }
        }).contains(TestConstant.BROKER_IP));
    }
}

