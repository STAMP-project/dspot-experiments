/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.protocol;


import ClientAuthenticationCodec.ResponseParameters;
import ClientMessage.BEGIN_AND_END_FLAGS;
import ClientTypes.JAVA;
import InternalSerializationService.VERSION_1;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAuthenticationCodec;
import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Server side client protocol tests
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RawProtocolAuthenticationTest {
    static HazelcastInstance server;

    private SocketChannel channel;

    @Test
    public void testAuthenticateWithUsernameAndPassword() throws IOException, InterruptedException {
        final ByteBuffer initData = ByteBuffer.wrap("CB2".getBytes());
        channel.write(initData);
        String username = GroupConfig.DEFAULT_GROUP_NAME;
        String pass = GroupConfig.DEFAULT_GROUP_PASSWORD;
        final ClientMessage authMessage = ClientAuthenticationCodec.encodeRequest(username, pass, null, null, true, JAVA, VERSION_1, BuildInfoProvider.getBuildInfo().getVersion());
        authMessage.setCorrelationId(1).addFlag(BEGIN_AND_END_FLAGS);
        final ClientProtocolBuffer byteBuffer = authMessage.buffer();
        channel.write(ByteBuffer.wrap(byteBuffer.byteArray(), 0, authMessage.getFrameLength()));
        ClientMessage clientMessage = readMessageFromChannel();
        Assert.assertTrue(clientMessage.isComplete());
        ClientMessage cmResult = ClientMessage.createForDecode(clientMessage.buffer(), 0);
        ClientAuthenticationCodec.ResponseParameters resultParameters = ClientAuthenticationCodec.decodeResponse(cmResult);
        Assert.assertEquals(cmResult.getCorrelationId(), 1);
        Assert.assertEquals(resultParameters.status, 0);
        Assert.assertEquals(resultParameters.serializationVersion, 1);
        Assert.assertEquals(resultParameters.ownerUuid, RawProtocolAuthenticationTest.server.getCluster().getLocalMember().getUuid());
        Assert.assertNotNull(UUID.fromString(resultParameters.uuid));
        Assert.assertEquals(RawProtocolAuthenticationTest.server.getCluster().getLocalMember().getAddress(), resultParameters.address);
    }

    @Test
    public void testAuthenticateWithUsernameAndPassword_with_Invalid_Credentials() throws IOException, InterruptedException {
        final ByteBuffer initData = ByteBuffer.wrap("CB2".getBytes());
        channel.write(initData);
        String username = "TheInvalidName";
        String pass = "TheInvalidPassword";
        final ClientMessage authMessage = ClientAuthenticationCodec.encodeRequest(username, pass, null, null, true, JAVA, VERSION_1, BuildInfoProvider.getBuildInfo().getVersion());
        authMessage.setCorrelationId(1).addFlag(BEGIN_AND_END_FLAGS);
        final ClientProtocolBuffer byteBuffer = authMessage.buffer();
        channel.write(ByteBuffer.wrap(byteBuffer.byteArray(), 0, authMessage.getFrameLength()));
        ClientMessage clientMessage = readMessageFromChannel();
        Assert.assertTrue(clientMessage.isComplete());
        ClientMessage cmResult = ClientMessage.createForDecode(clientMessage.buffer(), 0);
        ClientAuthenticationCodec.ResponseParameters resultParameters = ClientAuthenticationCodec.decodeResponse(cmResult);
        Assert.assertEquals(cmResult.getCorrelationId(), 1);
        Assert.assertEquals(resultParameters.status, 1);
        Assert.assertEquals(resultParameters.serializationVersion, 1);
        Assert.assertNull(resultParameters.ownerUuid);
        Assert.assertNull(resultParameters.uuid);
        Assert.assertNull(resultParameters.address);
    }

    @Test
    public void testAuthenticateWithUsernameAndPassword_with_Invalid_SerializationVersion() throws IOException, InterruptedException {
        final ByteBuffer initData = ByteBuffer.wrap("CB2".getBytes());
        channel.write(initData);
        String username = GroupConfig.DEFAULT_GROUP_NAME;
        String pass = GroupConfig.DEFAULT_GROUP_PASSWORD;
        final ClientMessage authMessage = ClientAuthenticationCodec.encodeRequest(username, pass, null, null, true, JAVA, ((byte) (0)), BuildInfoProvider.getBuildInfo().getVersion());
        authMessage.setCorrelationId(1).addFlag(BEGIN_AND_END_FLAGS);
        final ClientProtocolBuffer byteBuffer = authMessage.buffer();
        channel.write(ByteBuffer.wrap(byteBuffer.byteArray(), 0, authMessage.getFrameLength()));
        ClientMessage clientMessage = readMessageFromChannel();
        Assert.assertTrue(clientMessage.isComplete());
        ClientMessage cmResult = ClientMessage.createForDecode(clientMessage.buffer(), 0);
        ClientAuthenticationCodec.ResponseParameters resultParameters = ClientAuthenticationCodec.decodeResponse(cmResult);
        Assert.assertEquals(cmResult.getCorrelationId(), 1);
        Assert.assertEquals(resultParameters.status, 2);
        Assert.assertEquals(resultParameters.serializationVersion, 1);
        Assert.assertNull(resultParameters.ownerUuid);
        Assert.assertNull(resultParameters.uuid);
        Assert.assertNull(resultParameters.address);
    }

    @Test
    public void testAuthenticateWithUsernameAndPassword_with_Invalid_MessageSize() throws IOException, InterruptedException {
        final ByteBuffer initData = ByteBuffer.wrap("CB2".getBytes());
        channel.write(initData);
        String username = GroupConfig.DEFAULT_GROUP_NAME;
        String pass = GroupConfig.DEFAULT_GROUP_PASSWORD;
        final ClientMessage authMessage = ClientAuthenticationCodec.encodeRequest(username, pass, null, null, true, JAVA, ((byte) (0)), BuildInfoProvider.getBuildInfo().getVersion());
        authMessage.setCorrelationId(1).addFlag(BEGIN_AND_END_FLAGS);
        // set invalid message size
        authMessage.setFrameLength(((ClientMessage.HEADER_SIZE) - 1));
        final ClientProtocolBuffer byteBuffer = authMessage.buffer();
        channel.write(ByteBuffer.wrap(byteBuffer.byteArray(), 0, authMessage.getFrameLength()));
        ClientMessage clientMessage = readMessageFromChannel();
        Assert.assertFalse(clientMessage.isComplete());
    }
}

