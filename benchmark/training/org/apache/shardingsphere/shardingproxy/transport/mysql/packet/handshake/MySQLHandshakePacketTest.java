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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.handshake;


import MySQLServerInfo.CHARSET;
import MySQLServerInfo.PROTOCOL_VERSION;
import MySQLServerInfo.SERVER_VERSION;
import MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT;
import org.apache.shardingsphere.shardingproxy.transport.mysql.constant.MySQLCapabilityFlag;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLHandshakePacketTest {
    @Mock
    private MySQLPacketPayload payload;

    private final byte[] part1 = new byte[]{ 106, 105, 55, 122, 117, 98, 115, 109 };

    private final byte[] part2 = new byte[]{ 68, 102, 53, 122, 65, 49, 84, 79, 85, 115, 116, 113 };

    @Test
    public void assertNewWithPayload() {
        Mockito.when(payload.readInt1()).thenReturn(0, PROTOCOL_VERSION, CHARSET, 0);
        Mockito.when(payload.readStringNul()).thenReturn(SERVER_VERSION);
        Mockito.when(payload.readStringNulByBytes()).thenReturn(part1, part2);
        Mockito.when(payload.readInt4()).thenReturn(1000);
        Mockito.when(payload.readInt2()).thenReturn(MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsLower(), SERVER_STATUS_AUTOCOMMIT.getValue(), MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsUpper());
        MySQLHandshakePacket actual = new MySQLHandshakePacket(payload);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(0));
        Assert.assertThat(actual.getConnectionId(), CoreMatchers.is(1000));
        Assert.assertThat(actual.getAuthPluginData().getAuthPluginDataPart1(), CoreMatchers.is(part1));
        Assert.assertThat(actual.getAuthPluginData().getAuthPluginDataPart2(), CoreMatchers.is(part2));
        Mockito.verify(payload).skipReserved(10);
    }

    @Test
    public void assertWrite() {
        MySQLAuthPluginData authPluginData = new MySQLAuthPluginData(part1, part2);
        new MySQLHandshakePacket(1000, authPluginData).write(payload);
        Mockito.verify(payload).writeInt1(PROTOCOL_VERSION);
        Mockito.verify(payload).writeStringNul(SERVER_VERSION);
        Mockito.verify(payload).writeInt4(1000);
        Mockito.verify(payload).writeStringNul(new String(authPluginData.getAuthPluginDataPart1()));
        Mockito.verify(payload).writeInt2(MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsLower());
        Mockito.verify(payload).writeInt1(CHARSET);
        Mockito.verify(payload).writeInt2(SERVER_STATUS_AUTOCOMMIT.getValue());
        Mockito.verify(payload).writeInt2(MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsUpper());
        Mockito.verify(payload).writeInt1(0);
        Mockito.verify(payload).writeReserved(10);
        Mockito.verify(payload).writeStringNul(new String(authPluginData.getAuthPluginDataPart2()));
    }
}

