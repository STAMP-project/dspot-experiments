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


import com.navercorp.pinpoint.thrift.dto.TAgentInfo;
import java.io.IOException;
import java.net.DatagramSocket;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import static ThriftUdpMessageSerializer.UDP_MAX_PACKET_LENGTH;


/**
 *
 *
 * @author Taejin Koo
 */
public class NioUdpDataSenderTest {
    // The correct maximum UDP message size is 65507, as determined by the following formula:
    // 0xffff - (sizeof(IP Header) + sizeof(UDP Header)) = 65535-(20+8) = 65507
    private static int AcceptedSize = 65507;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    // port conflict against base port. so increased 5
    private int PORT = SocketUtils.findAvailableUdpPort(61112);

    private DatagramSocket receiver;

    @Test
    public void sendTest1() throws Exception {
        NioUDPDataSender sender = newNioUdpDataSender();
        int sendMessageCount = 10;
        TAgentInfo agentInfo = new TAgentInfo();
        for (int i = 0; i < 10; i++) {
            sender.send(agentInfo);
        }
        try {
            waitMessageReceived(sendMessageCount);
        } finally {
            sender.stop();
        }
    }

    @Test(expected = IOException.class)
    public void exceedMessageSendTest() throws IOException {
        String random = RandomStringUtils.randomAlphabetic(((UDP_MAX_PACKET_LENGTH) + 100));
        TAgentInfo agentInfo = new TAgentInfo();
        agentInfo.setAgentId(random);
        NioUDPDataSender sender = newNioUdpDataSender();
        sender.send(agentInfo);
        waitMessageReceived(1);
    }
}

