/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.sender;


import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.profiler.context.id.DefaultTransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.id.TransactionIdEncoder;
import com.navercorp.pinpoint.profiler.context.thrift.MessageConverter;
import com.navercorp.pinpoint.thrift.dto.TAgentInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.thrift.TBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import static ThriftUdpMessageSerializer.UDP_MAX_PACKET_LENGTH;


/**
 *
 *
 * @author emeroad
 */
public class UdpDataSenderTest {
    private static final String APP_NAME = "appName";

    private static final String AGENT_ID = "agentid";

    private static final int AGENT_START_TIME = 0;

    private static final ServiceType APP_SERVICE_TYPE = ServiceType.STAND_ALONE;

    private final int PORT = SocketUtils.findAvailableUdpPort(9009);

    private final TransactionIdEncoder transactionIdEncoder = new DefaultTransactionIdEncoder(UdpDataSenderTest.AGENT_ID, UdpDataSenderTest.AGENT_START_TIME);

    @Test
    public void sendAndFlushCheck() throws InterruptedException {
        final MessageConverter<TBase<?, ?>> messageConverter = new com.navercorp.pinpoint.profiler.context.thrift.BypassMessageConverter<TBase<?, ?>>();
        final MessageSerializer<ByteMessage> thriftMessageSerializer = new ThriftUdpMessageSerializer(messageConverter, UDP_MAX_PACKET_LENGTH);
        UdpDataSender sender = new UdpDataSender("localhost", PORT, "test", 128, 1000, ((1024 * 64) * 100), thriftMessageSerializer);
        TAgentInfo agentInfo = new TAgentInfo();
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.send(agentInfo);
        sender.stop();
    }

    // @Test
    // public void sendAndLarge() throws InterruptedException {
    // String random = RandomStringUtils.randomAlphabetic(UdpDataSender.UDP_MAX_PACKET_LENGTH);
    // TAgentInfo agentInfo = new TAgentInfo();
    // agentInfo.setAgentId(random);
    // boolean limit = sendMessage_getLimit(agentInfo, 5000);
    // Assert.assertTrue("limit overflow",limit);
    // 
    // boolean noLimit = sendMessage_getLimit(new TAgentInfo(), 5000);
    // Assert.assertFalse("success", noLimit);
    // }
    @Test
    public void sendExceedData() throws InterruptedException {
        String random = RandomStringUtils.randomAlphabetic(((UDP_MAX_PACKET_LENGTH) + 100));
        TAgentInfo agentInfo = new TAgentInfo();
        agentInfo.setAgentId(random);
        boolean limit = sendMessage_getLimit(agentInfo, 1000);
        // do not execute.
        Assert.assertFalse(limit);
    }
}

