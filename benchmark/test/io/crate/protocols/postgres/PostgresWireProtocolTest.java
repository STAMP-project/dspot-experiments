/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.postgres;


import DataTypes.BOOLEAN;
import DataTypes.LONG;
import Version.CURRENT;
import io.crate.action.sql.SQLOperations;
import io.crate.action.sql.Session;
import io.crate.auth.AlwaysOKNullAuthentication;
import io.crate.auth.Authentication;
import io.crate.auth.AuthenticationMethod;
import io.crate.auth.user.User;
import io.crate.auth.user.UserManager;
import io.crate.exceptions.JobKilledException;
import io.crate.protocols.postgres.types.PGTypes;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.DummyUserManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.settings.SecureString;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static io.crate.protocols.postgres.ClientMessages.DescribeType.PORTAL;
import static io.crate.protocols.postgres.ClientMessages.DescribeType.STATEMENT;


public class PostgresWireProtocolTest extends CrateDummyClusterServiceUnitTest {
    private static final Provider<UserManager> USER_MANAGER_PROVIDER = DummyUserManager::new;

    private SQLOperations sqlOperations;

    private List<Session> sessions = new ArrayList<>();

    private EmbeddedChannel channel;

    @Test
    public void testHandleEmptySimpleQuery() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(Mockito.mock(SQLOperations.class), new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buffer = Unpooled.buffer();
        try {
            Messages.writeCString(buffer, ";".getBytes(StandardCharsets.UTF_8));
            ctx.handleSimpleQuery(buffer, channel);
        } finally {
            buffer.release();
        }
        ByteBuf firstResponse = channel.readOutbound();
        byte[] responseBytes = new byte[5];
        try {
            firstResponse.readBytes(responseBytes);
            // EmptyQueryResponse: 'I' | int32 len
            assertThat(responseBytes, Matchers.is(new byte[]{ 'I', 0, 0, 0, 4 }));
        } finally {
            firstResponse.release();
        }
        ByteBuf secondResponse = channel.readOutbound();
        try {
            responseBytes = new byte[6];
            secondResponse.readBytes(responseBytes);
            // ReadyForQuery: 'Z' | int32 len | 'I'
            assertThat(responseBytes, Matchers.is(new byte[]{ 'Z', 0, 0, 0, 5, 'I' }));
        } finally {
            secondResponse.release();
        }
    }

    @Test
    public void testFlushMessageResultsInSyncCallOnSession() throws Exception {
        SQLOperations sqlOperations = Mockito.mock(SQLOperations.class);
        Session session = Mockito.mock(Session.class);
        Mockito.when(sqlOperations.createSession(ArgumentMatchers.any(String.class), ArgumentMatchers.any(User.class))).thenReturn(session);
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buffer = Unpooled.buffer();
        ClientMessages.sendStartupMessage(buffer, "doc");
        ClientMessages.sendParseMessage(buffer, "", "select ?", new int[0]);
        ClientMessages.sendFlush(buffer);
        channel.writeInbound(buffer);
        channel.releaseInbound();
        Mockito.verify(session, Mockito.times(1)).sync();
    }

    @Test
    public void testBindMessageCanBeReadIfTypeForParamsIsUnknown() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buffer = Unpooled.buffer();
        ClientMessages.sendStartupMessage(buffer, "doc");
        ClientMessages.sendParseMessage(buffer, "S1", "select ?, ?", new int[0]);// no type hints for parameters

        List<Object> params = Arrays.asList(10, 20);
        ClientMessages.sendBindMessage(buffer, "P1", "S1", params);
        channel.writeInbound(buffer);
        channel.releaseInbound();
        Session session = sessions.get(0);
        // If the query can be retrieved via portalName it means bind worked
        assertThat(session.getQuery("P1"), Matchers.is("select ?, ?"));
    }

    @Test
    public void testDescribePortalMessage() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        {
            ByteBuf buffer = Unpooled.buffer();
            ClientMessages.sendStartupMessage(buffer, "doc");
            ClientMessages.sendParseMessage(buffer, "S1", "select ? in (1, 2, 3)", new int[]{ PGTypes.get(LONG).oid() });
            ClientMessages.sendBindMessage(buffer, "P1", "S1", Collections.singletonList(1));
            channel.writeInbound(buffer);
            channel.releaseInbound();
            // we're not interested in the startup, parse, or bind replies
            channel.flushOutbound();
            channel.releaseOutbound();
            channel.outboundMessages().clear();
        }
        {
            // try portal describe message
            ByteBuf buffer = Unpooled.buffer();
            ClientMessages.sendDescribeMessage(buffer, PORTAL, "P1");
            channel.writeInbound(buffer);
            channel.releaseInbound();
            // we should get back a RowDescription message
            ByteBuf response = channel.readOutbound();
            try {
                assertThat(response.readByte(), Matchers.is(((byte) ('T'))));
                assertThat(response.readInt(), Matchers.is(42));
                assertThat(response.readShort(), Matchers.is(((short) (1))));
                assertThat(PostgresWireProtocol.readCString(response), Matchers.is("($1 IN (1, 2, 3))"));
                assertThat(response.readInt(), Matchers.is(0));
                assertThat(response.readShort(), Matchers.is(((short) (0))));
                assertThat(response.readInt(), Matchers.is(PGTypes.get(BOOLEAN).oid()));
                assertThat(response.readShort(), Matchers.is(((short) (PGTypes.get(BOOLEAN).typeLen()))));
                assertThat(response.readInt(), Matchers.is(PGTypes.get(LONG).typeMod()));
                assertThat(response.readShort(), Matchers.is(((short) (0))));
            } finally {
                response.release();
            }
        }
    }

    @Test
    public void testDescribeStatementMessage() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        {
            ByteBuf buffer = Unpooled.buffer();
            ClientMessages.sendStartupMessage(buffer, "doc");
            ClientMessages.sendParseMessage(buffer, "S1", "select ? in (1, 2, 3)", new int[0]);
            channel.writeInbound(buffer);
            channel.releaseInbound();
            // we're not interested in the startup, parse, or bind replies
            channel.flushOutbound();
            channel.releaseOutbound();
            channel.outboundMessages().clear();
        }
        {
            // try the describe statement variant
            ByteBuf buffer = Unpooled.buffer();
            ClientMessages.sendDescribeMessage(buffer, STATEMENT, "S1");
            channel.writeInbound(buffer);
            channel.releaseInbound();
            // we should get back a ParameterDescription message
            ByteBuf response = channel.readOutbound();
            try {
                assertThat(response.readByte(), Matchers.is(((byte) ('t'))));
                assertThat(response.readInt(), Matchers.is(10));
                assertThat(response.readShort(), Matchers.is(((short) (1))));
                assertThat(response.readInt(), Matchers.is(PGTypes.get(LONG).oid()));
            } finally {
                response.release();
            }
            // we should get back a RowDescription message
            response = channel.readOutbound();
            try {
                assertThat(response.readByte(), Matchers.is(((byte) ('T'))));
                assertThat(response.readInt(), Matchers.is(42));
                assertThat(response.readShort(), Matchers.is(((short) (1))));
                assertThat(PostgresWireProtocol.readCString(response), Matchers.is("($1 IN (1, 2, 3))"));
                assertThat(response.readInt(), Matchers.is(0));
                assertThat(response.readShort(), Matchers.is(((short) (0))));
                assertThat(response.readInt(), Matchers.is(PGTypes.get(BOOLEAN).oid()));
                assertThat(response.readShort(), Matchers.is(((short) (PGTypes.get(BOOLEAN).typeLen()))));
                assertThat(response.readInt(), Matchers.is(PGTypes.get(LONG).typeMod()));
                assertThat(response.readShort(), Matchers.is(((short) (0))));
            } finally {
                response.release();
            }
        }
    }

    @Test
    public void testSslRejection() {
        PostgresWireProtocol ctx = new PostgresWireProtocol(Mockito.mock(SQLOperations.class), new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buffer = Unpooled.buffer();
        ClientMessages.sendSslReqMessage(buffer);
        channel.writeInbound(buffer);
        // We should get back an 'N'...
        ByteBuf responseBuffer = channel.readOutbound();
        try {
            byte response = responseBuffer.readByte();
            assertEquals(response, 'N');
        } finally {
            responseBuffer.release();
        }
        // ...and continue unencrypted (no ssl handler)
        for (Map.Entry<String, ChannelHandler> entry : channel.pipeline()) {
            assertThat(entry.getValue(), Matchers.isOneOf(ctx.decoder, ctx.handler));
        }
    }

    @Test
    public void testCrateServerVersionIsReceivedOnStartup() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buf = Unpooled.buffer();
        ClientMessages.sendStartupMessage(buf, "doc");
        channel.writeInbound(buf);
        channel.releaseInbound();
        ByteBuf respBuf;
        respBuf = channel.readOutbound();
        try {
            assertThat(((char) (respBuf.readByte())), Matchers.is('R'));// Auth OK

        } finally {
            respBuf.release();
        }
        respBuf = channel.readOutbound();
        try {
            assertThat(((char) (respBuf.readByte())), Matchers.is('S'));// ParameterStatus

            respBuf.readInt();// length

            String key = PostgresWireProtocol.readCString(respBuf);
            String value = PostgresWireProtocol.readCString(respBuf);
            assertThat(key, Matchers.is("crate_version"));
            assertThat(value, Matchers.is(CURRENT.externalNumber()));
        } finally {
            respBuf.release();
        }
    }

    @Test
    public void testPasswordMessageAuthenticationProcess() throws Exception {
        PostgresWireProtocol ctx = new PostgresWireProtocol(Mockito.mock(SQLOperations.class), new Authentication() {
            @Nullable
            @Override
            public AuthenticationMethod resolveAuthenticationType(String user, ConnectionProperties connectionProperties) {
                return new AuthenticationMethod() {
                    @Nullable
                    @Override
                    public User authenticate(String userName, @Nullable
                    SecureString passwd, ConnectionProperties connProperties) {
                        return null;
                    }

                    @Override
                    public String name() {
                        return "password";
                    }
                };
            }
        }, null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf respBuf;
        ByteBuf buffer = Unpooled.buffer();
        ClientMessages.sendStartupMessage(buffer, "doc");
        channel.writeInbound(buffer);
        respBuf = channel.readOutbound();
        try {
            assertThat(((char) (respBuf.readByte())), Matchers.is('R'));// AuthenticationCleartextPassword

        } finally {
            respBuf.release();
        }
        buffer = Unpooled.buffer();
        ClientMessages.sendPasswordMessage(buffer, "pw");
        channel.writeInbound(buffer);
        respBuf = channel.readOutbound();
        try {
            assertThat(((char) (respBuf.readByte())), Matchers.is('R'));// Auth OK

        } finally {
            respBuf.release();
        }
    }

    @Test
    public void testSessionCloseOnTerminationMessage() throws Exception {
        SQLOperations sqlOperations = Mockito.mock(SQLOperations.class);
        Session session = Mockito.mock(Session.class);
        Mockito.when(sqlOperations.createSession(ArgumentMatchers.any(String.class), ArgumentMatchers.any(User.class))).thenReturn(session);
        PostgresWireProtocol ctx = new PostgresWireProtocol(sqlOperations, new AlwaysOKNullAuthentication(), null);
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        ByteBuf buffer = Unpooled.buffer();
        ClientMessages.sendStartupMessage(buffer, "doc");
        ClientMessages.sendTermination(buffer);
        channel.writeInbound(buffer);
        channel.releaseInbound();
        Mockito.verify(session, Mockito.times(1)).close();
    }

    @Test
    public void testHandleMultipleSimpleQueries() {
        submitQueriesThroughSimpleQueryMode("first statement; second statement;", null);
        PostgresWireProtocolTest.readReadyForQueryMessage(channel);
        assertThat(channel.outboundMessages().size(), Matchers.is(0));
    }

    @Test
    public void testHandleMultipleSimpleQueriesWithQueryFailure() {
        submitQueriesThroughSimpleQueryMode("first statement; second statement;", new RuntimeException("fail"));
        PostgresWireProtocolTest.readErrorResponse(channel, ((byte) (110)));
        PostgresWireProtocolTest.readReadyForQueryMessage(channel);
        assertThat(channel.outboundMessages().size(), Matchers.is(0));
    }

    @Test
    public void testKillExceptionSendsReadyForQuery() {
        submitQueriesThroughSimpleQueryMode("some statement;", new JobKilledException());
        PostgresWireProtocolTest.readErrorResponse(channel, ((byte) (104)));
        PostgresWireProtocolTest.readReadyForQueryMessage(channel);
    }
}

