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


import MySQLCapabilityFlag.CLIENT_CONNECT_WITH_DB;
import MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA;
import MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION;
import MySQLServerInfo.CHARSET;
import org.apache.shardingsphere.shardingproxy.transport.mysql.constant.MySQLServerInfo;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLHandshakeResponse41PacketTest {
    @Mock
    private MySQLPacketPayload payload;

    @Test
    public void assertNewWithPayloadWithDatabase() {
        Mockito.when(payload.readInt1()).thenReturn(1, CHARSET);
        Mockito.when(payload.readInt4()).thenReturn(CLIENT_CONNECT_WITH_DB.getValue(), 1000);
        Mockito.when(payload.readStringNul()).thenReturn("root", "sharding_db");
        Mockito.when(payload.readStringNulByBytes()).thenReturn(new byte[]{ 1 });
        MySQLHandshakeResponse41Packet actual = new MySQLHandshakeResponse41Packet(payload);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getAuthResponse(), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertThat(actual.getDatabase(), CoreMatchers.is("sharding_db"));
        Mockito.verify(payload).skipReserved(23);
    }

    @Test
    public void assertNewWithPayloadWithClientPluginAuthLenencClientData() {
        Mockito.when(payload.readInt1()).thenReturn(1, CHARSET);
        Mockito.when(payload.readInt4()).thenReturn(CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue(), 1000);
        Mockito.when(payload.readStringNul()).thenReturn("root");
        Mockito.when(payload.readStringLenencByBytes()).thenReturn(new byte[]{ 1 });
        MySQLHandshakeResponse41Packet actual = new MySQLHandshakeResponse41Packet(payload);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getAuthResponse(), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertNull(actual.getDatabase());
        Mockito.verify(payload).skipReserved(23);
    }

    @Test
    public void assertNewWithPayloadWithClientSecureConnection() {
        Mockito.when(payload.readInt1()).thenReturn(1, CHARSET, 1);
        Mockito.when(payload.readInt4()).thenReturn(CLIENT_SECURE_CONNECTION.getValue(), 1000);
        Mockito.when(payload.readStringNul()).thenReturn("root");
        Mockito.when(payload.readStringFixByBytes(1)).thenReturn(new byte[]{ 1 });
        MySQLHandshakeResponse41Packet actual = new MySQLHandshakeResponse41Packet(payload);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getAuthResponse(), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertNull(actual.getDatabase());
        Mockito.verify(payload).skipReserved(23);
    }

    @Test
    public void assertWriteWithDatabase() {
        new MySQLHandshakeResponse41Packet(1, CLIENT_CONNECT_WITH_DB.getValue(), 100, MySQLServerInfo.CHARSET, "root", new byte[]{ 1 }, "sharding_db").write(payload);
        Mockito.verify(payload).writeInt4(CLIENT_CONNECT_WITH_DB.getValue());
        Mockito.verify(payload).writeInt4(100);
        Mockito.verify(payload).writeInt1(CHARSET);
        Mockito.verify(payload).writeReserved(23);
        Mockito.verify(payload).writeStringNul("root");
        Mockito.verify(payload).writeStringNul(new String(new byte[]{ 1 }));
        Mockito.verify(payload).writeStringNul("sharding_db");
    }

    @Test
    public void assertWriteWithClientPluginAuthLenencClientData() {
        new MySQLHandshakeResponse41Packet(1, CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue(), 100, MySQLServerInfo.CHARSET, "root", new byte[]{ 1 }, null).write(payload);
        Mockito.verify(payload).writeInt4(CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue());
        Mockito.verify(payload).writeInt4(100);
        Mockito.verify(payload).writeInt1(CHARSET);
        Mockito.verify(payload).writeReserved(23);
        Mockito.verify(payload).writeStringNul("root");
        Mockito.verify(payload).writeStringLenenc(new String(new byte[]{ 1 }));
    }

    @Test
    public void assertWriteWithClientSecureConnection() {
        new MySQLHandshakeResponse41Packet(1, CLIENT_SECURE_CONNECTION.getValue(), 100, MySQLServerInfo.CHARSET, "root", new byte[]{ 1 }, null).write(payload);
        Mockito.verify(payload).writeInt4(CLIENT_SECURE_CONNECTION.getValue());
        Mockito.verify(payload).writeInt4(100);
        Mockito.verify(payload).writeInt1(CHARSET);
        Mockito.verify(payload).writeReserved(23);
        Mockito.verify(payload).writeStringNul("root");
        Mockito.verify(payload).writeInt1(1);
        Mockito.verify(payload).writeBytes(new byte[]{ 1 });
    }
}

