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
package org.apache.shardingsphere.shardingproxy.frontend.mysql;


import io.netty.channel.ChannelHandlerContext;
import org.apache.shardingsphere.core.rule.Authentication;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.generic.MySQLErrPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.generic.MySQLOKPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.handshake.MySQLHandshakePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLFrontendEngineTest {
    private MySQLProtocolFrontendEngine mysqlFrontendEngine;

    @Mock
    private ChannelHandlerContext context;

    @Mock
    private MySQLPacketPayload payload;

    @Test
    public void assertHandshake() {
        mysqlFrontendEngine.getAuthEngine().handshake(context, Mockito.mock(BackendConnection.class));
        Mockito.verify(context).writeAndFlush(ArgumentMatchers.isA(MySQLHandshakePacket.class));
    }

    @Test
    public void assertAuthWhenLoginSuccess() {
        Authentication authentication = new Authentication("root", "");
        setAuthentication(authentication);
        Mockito.when(payload.readStringNul()).thenReturn("root");
        Assert.assertTrue(mysqlFrontendEngine.getAuthEngine().auth(context, payload, Mockito.mock(BackendConnection.class)));
        Mockito.verify(context).writeAndFlush(ArgumentMatchers.isA(MySQLOKPacket.class));
    }

    @Test
    public void assertAuthWhenLoginFailure() {
        Authentication authentication = new Authentication("root", "error");
        setAuthentication(authentication);
        Mockito.when(payload.readStringNul()).thenReturn("root");
        Mockito.when(payload.readStringNulByBytes()).thenReturn("root".getBytes());
        Assert.assertTrue(mysqlFrontendEngine.getAuthEngine().auth(context, payload, Mockito.mock(BackendConnection.class)));
        Mockito.verify(context).writeAndFlush(ArgumentMatchers.isA(MySQLErrPacket.class));
    }
}

