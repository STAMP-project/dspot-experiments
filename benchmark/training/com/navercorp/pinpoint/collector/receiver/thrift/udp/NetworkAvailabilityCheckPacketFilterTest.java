/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.receiver.thrift.udp;


import NetworkAvailabilityCheckPacket.DATA_OK;
import NetworkAvailabilityCheckPacket.DATA_OK.length;
import TBaseFilter.BREAK;
import TBaseFilter.CONTINUE;
import com.navercorp.pinpoint.thrift.dto.TSpan;
import com.navercorp.pinpoint.thrift.io.NetworkAvailabilityCheckPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class NetworkAvailabilityCheckPacketFilterTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TBaseFilter<? super SocketAddress> filter;

    private DatagramSocket senderSocket;

    private DatagramSocket receiverSocket;

    @Test
    public void testFilter() throws Exception {
        SocketAddress localSocketAddress = senderSocket.getLocalSocketAddress();
        logger.debug("localSocket:{}", localSocketAddress);
        NetworkAvailabilityCheckPacket packet = new NetworkAvailabilityCheckPacket();
        SocketAddress inetSocketAddress = new InetSocketAddress("localhost", senderSocket.getLocalPort());
        boolean skipResult = filter.filter(receiverSocket, packet, inetSocketAddress);
        Assert.assertEquals(skipResult, BREAK);
        DatagramPacket receivePacket = new DatagramPacket(new byte[100], 100);
        senderSocket.receive(receivePacket);
        Assert.assertEquals(receivePacket.getLength(), length);
        Assert.assertArrayEquals(Arrays.copyOf(receivePacket.getData(), length), DATA_OK);
    }

    @Test
    public void testFilter_Continue() throws Exception {
        SocketAddress localSocketAddress = senderSocket.getLocalSocketAddress();
        logger.debug("localSocket:{}", localSocketAddress);
        TSpan skip = new TSpan();
        boolean skipResult = filter.filter(receiverSocket, skip, null);
        Assert.assertEquals(skipResult, CONTINUE);
    }
}

