/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.rpc.server;


import HealthCheckState.RECEIVED;
import HealthCheckState.RECEIVED_LEGACY;
import HealthCheckState.WAIT;
import PingPacket.PING_PACKET;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.util.Timer;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Taejin Koo
 */
public class HealthCheckManagerTest {
    private static Timer timer;

    @Test
    public void legacyPingPacketTest() throws Exception {
        ChannelGroup channelGroup = new DefaultChannelGroup();
        HealthCheckManager healthCheckManager = new HealthCheckManager(HealthCheckManagerTest.timer, 3000, channelGroup);
        healthCheckManager.start(1000);
        Channel mockChannel = createMockChannel(RECEIVED_LEGACY);
        channelGroup.add(mockChannel);
        try {
            Mockito.verify(mockChannel, Mockito.timeout(3000).atLeastOnce()).write(PING_PACKET);
        } finally {
            healthCheckManager.stop();
        }
    }

    @Test
    public void pingPacketTest() throws Exception {
        ChannelGroup channelGroup = new DefaultChannelGroup();
        HealthCheckManager healthCheckManager = new HealthCheckManager(HealthCheckManagerTest.timer, 3000, channelGroup);
        healthCheckManager.start(1000);
        Channel mockChannel = createMockChannel(RECEIVED);
        channelGroup.add(mockChannel);
        try {
            Mockito.verify(mockChannel, Mockito.timeout(3000).atLeastOnce()).write(PingSimplePacket.PING_PACKET);
        } finally {
            healthCheckManager.stop();
        }
    }

    @Test
    public void withoutPacketTest() throws Exception {
        ChannelGroup channelGroup = new DefaultChannelGroup();
        HealthCheckManager healthCheckManager = new HealthCheckManager(HealthCheckManagerTest.timer, 3000, channelGroup);
        healthCheckManager.start(1000);
        Channel mockChannel = createMockChannel(WAIT);
        channelGroup.add(mockChannel);
        try {
            Mockito.verify(mockChannel, Mockito.timeout(5000).atLeastOnce()).close();
        } finally {
            healthCheckManager.stop();
        }
    }
}

