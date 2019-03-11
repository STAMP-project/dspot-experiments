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
package org.apache.rocketmq.acl.plain;


import RequestCode.CONSUMER_SEND_MSG_BACK;
import RequestCode.GET_ALL_CONSUMER_OFFSET;
import RequestCode.GET_ALL_DELAY_OFFSET;
import RequestCode.GET_ALL_SUBSCRIPTIONGROUP_CONFIG;
import RequestCode.GET_ALL_TOPIC_CONFIG;
import RequestCode.GET_CONSUMER_LIST_BY_GROUP;
import RequestCode.HEART_BEAT;
import RequestCode.PULL_MESSAGE;
import RequestCode.QUERY_MESSAGE;
import RequestCode.SEND_MESSAGE;
import RequestCode.SEND_MESSAGE_V2;
import RequestCode.UNREGISTER_CLIENT;
import RequestCode.UPDATE_CONSUMER_OFFSET;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.AclException;
import org.apache.rocketmq.acl.common.AclUtils;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumerData;
import org.apache.rocketmq.common.protocol.heartbeat.HeartbeatData;
import org.apache.rocketmq.common.protocol.heartbeat.ProducerData;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.junit.Assert;
import org.junit.Test;


public class PlainAccessValidatorTest {
    private PlainAccessValidator plainAccessValidator;

    private AclClientRPCHook aclClient;

    private SessionCredentials sessionCredentials;

    @Test
    public void contentTest() {
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE, messageRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "127.0.0.1")));
        String signature = AclUtils.calSignature(accessResource.getContent(), sessionCredentials.getSecretKey());
        Assert.assertEquals(accessResource.getSignature(), signature);
    }

    @Test
    public void validateTest() {
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicB");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE, messageRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateSendMessageTest() {
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicB");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE, messageRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateSendMessageV2Test() {
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicC");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE_V2, SendMessageRequestHeaderV2.createSendMessageRequestHeaderV2(messageRequestHeader));
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test(expected = AclException.class)
    public void validateForAdminCommandWithOutAclRPCHook() {
        RemotingCommand consumerOffsetAdminRequest = RemotingCommand.createRequestCommand(GET_ALL_CONSUMER_OFFSET, null);
        plainAccessValidator.parse(consumerOffsetAdminRequest, "192.168.0.1:9876");
        RemotingCommand subscriptionGroupAdminRequest = RemotingCommand.createRequestCommand(GET_ALL_SUBSCRIPTIONGROUP_CONFIG, null);
        plainAccessValidator.parse(subscriptionGroupAdminRequest, "192.168.0.1:9876");
        RemotingCommand delayOffsetAdminRequest = RemotingCommand.createRequestCommand(GET_ALL_DELAY_OFFSET, null);
        plainAccessValidator.parse(delayOffsetAdminRequest, "192.168.0.1:9876");
        RemotingCommand allTopicConfigAdminRequest = RemotingCommand.createRequestCommand(GET_ALL_TOPIC_CONFIG, null);
        plainAccessValidator.parse(allTopicConfigAdminRequest, "192.168.0.1:9876");
    }

    @Test
    public void validatePullMessageTest() {
        PullMessageRequestHeader pullMessageRequestHeader = new PullMessageRequestHeader();
        pullMessageRequestHeader.setTopic("topicC");
        pullMessageRequestHeader.setConsumerGroup("consumerGroupA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(PULL_MESSAGE, pullMessageRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateConsumeMessageBackTest() {
        ConsumerSendMsgBackRequestHeader consumerSendMsgBackRequestHeader = new ConsumerSendMsgBackRequestHeader();
        consumerSendMsgBackRequestHeader.setOriginTopic("topicC");
        consumerSendMsgBackRequestHeader.setGroup("consumerGroupA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(CONSUMER_SEND_MSG_BACK, consumerSendMsgBackRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateQueryMessageTest() {
        QueryMessageRequestHeader queryMessageRequestHeader = new QueryMessageRequestHeader();
        queryMessageRequestHeader.setTopic("topicC");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(QUERY_MESSAGE, queryMessageRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateHeartBeatTest() {
        HeartbeatData heartbeatData = new HeartbeatData();
        Set<ProducerData> producerDataSet = new HashSet<>();
        Set<ConsumerData> consumerDataSet = new HashSet<>();
        Set<SubscriptionData> subscriptionDataSet = new HashSet<>();
        ProducerData producerData = new ProducerData();
        producerData.setGroupName("producerGroupA");
        ConsumerData consumerData = new ConsumerData();
        consumerData.setGroupName("consumerGroupA");
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setTopic("topicC");
        producerDataSet.add(producerData);
        consumerDataSet.add(consumerData);
        subscriptionDataSet.add(subscriptionData);
        consumerData.setSubscriptionDataSet(subscriptionDataSet);
        heartbeatData.setProducerDataSet(producerDataSet);
        heartbeatData.setConsumerDataSet(consumerDataSet);
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(HEART_BEAT, null);
        remotingCommand.setBody(heartbeatData.encode());
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encode();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateUnRegisterClientTest() {
        UnregisterClientRequestHeader unregisterClientRequestHeader = new UnregisterClientRequestHeader();
        unregisterClientRequestHeader.setConsumerGroup("consumerGroupA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(UNREGISTER_CLIENT, unregisterClientRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateGetConsumerListByGroupTest() {
        GetConsumerListByGroupRequestHeader getConsumerListByGroupRequestHeader = new GetConsumerListByGroupRequestHeader();
        getConsumerListByGroupRequestHeader.setConsumerGroup("consumerGroupA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(GET_CONSUMER_LIST_BY_GROUP, getConsumerListByGroupRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test
    public void validateUpdateConsumerOffSetTest() {
        UpdateConsumerOffsetRequestHeader updateConsumerOffsetRequestHeader = new UpdateConsumerOffsetRequestHeader();
        updateConsumerOffsetRequestHeader.setConsumerGroup("consumerGroupA");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(UPDATE_CONSUMER_OFFSET, updateConsumerOffsetRequestHeader);
        aclClient.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.0.1:9876")));
        plainAccessValidator.validate(accessResource);
    }

    @Test(expected = AclException.class)
    public void validateNullAccessKeyTest() {
        SessionCredentials sessionCredentials = new SessionCredentials();
        sessionCredentials.setAccessKey("RocketMQ1");
        sessionCredentials.setSecretKey("1234");
        AclClientRPCHook aclClientRPCHook = new AclClientRPCHook(sessionCredentials);
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicB");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE, messageRequestHeader);
        aclClientRPCHook.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.1.1")));
        plainAccessValidator.validate(accessResource);
    }

    @Test(expected = AclException.class)
    public void validateErrorSecretKeyTest() {
        SessionCredentials sessionCredentials = new SessionCredentials();
        sessionCredentials.setAccessKey("RocketMQ");
        sessionCredentials.setSecretKey("1234");
        AclClientRPCHook aclClientRPCHook = new AclClientRPCHook(sessionCredentials);
        SendMessageRequestHeader messageRequestHeader = new SendMessageRequestHeader();
        messageRequestHeader.setTopic("topicB");
        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(SEND_MESSAGE, messageRequestHeader);
        aclClientRPCHook.doBeforeRequest("", remotingCommand);
        ByteBuffer buf = remotingCommand.encodeHeader();
        buf.getInt();
        buf = ByteBuffer.allocate(((buf.limit()) - (buf.position()))).put(buf);
        buf.position(0);
        PlainAccessResource accessResource = ((PlainAccessResource) (plainAccessValidator.parse(RemotingCommand.decode(buf), "192.168.1.1")));
        plainAccessValidator.validate(accessResource);
    }
}

