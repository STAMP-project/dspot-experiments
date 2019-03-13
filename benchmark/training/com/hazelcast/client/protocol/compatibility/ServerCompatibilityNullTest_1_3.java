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
package com.hazelcast.client.protocol.compatibility;


import ClientAuthenticationCodec.RequestParameters;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ServerCompatibilityNullTest_1_3 {
    private static final int FRAME_LEN_FIELD_SIZE = 4;

    @Test
    public void test() throws IOException {
        InputStream input = getClass().getResourceAsStream("/1.3.protocol.compatibility.null.binary");
        DataInputStream inputStream = new DataInputStream(input);
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAuthenticationCodec.RequestParameters params = ClientAuthenticationCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.username));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.password));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isOwnerConnection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientHazelcastVersion));
            Assert.assertFalse(params.clientNameExist);
            Assert.assertFalse(params.labelsExist);
            Assert.assertFalse(params.partitionCountExist);
            Assert.assertFalse(params.clusterIdExist);
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCodec.encodeResponse(ReferenceObjects.aByte, null, null, null, ReferenceObjects.aByte, ReferenceObjects.aString, null, ReferenceObjects.anInt, ReferenceObjects.aString);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.8), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAuthenticationCustomCodec.RequestParameters params = ClientAuthenticationCustomCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.credentials));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isOwnerConnection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientHazelcastVersion));
            Assert.assertFalse(params.clientNameExist);
            Assert.assertFalse(params.labelsExist);
            Assert.assertFalse(params.partitionCountExist);
            Assert.assertFalse(params.clusterIdExist);
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCustomCodec.encodeResponse(ReferenceObjects.aByte, null, null, null, ReferenceObjects.aByte, ReferenceObjects.aString, null, ReferenceObjects.anInt, ReferenceObjects.aString);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.8), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddMembershipListenerCodec.RequestParameters params = ClientAddMembershipListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberEvent(ReferenceObjects.aMember, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberListEvent(ReferenceObjects.members);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberAttributeChangeEvent(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt, null);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientCreateProxyCodec.RequestParameters params = ClientCreateProxyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serviceName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.target));
        }
        {
            ClientMessage clientMessage = ClientCreateProxyCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientDestroyProxyCodec.RequestParameters params = ClientDestroyProxyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serviceName));
        }
        {
            ClientMessage clientMessage = ClientDestroyProxyCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientGetPartitionsCodec.RequestParameters params = ClientGetPartitionsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientGetPartitionsCodec.encodeResponse(ReferenceObjects.aPartitionTable, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.5), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemoveAllListenersCodec.RequestParameters params = ClientRemoveAllListenersCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientRemoveAllListenersCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddPartitionLostListenerCodec.RequestParameters params = ClientAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodePartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.anInt, null);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemovePartitionLostListenerCodec.RequestParameters params = ClientRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ClientRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientGetDistributedObjectsCodec.RequestParameters params = ClientGetDistributedObjectsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientGetDistributedObjectsCodec.encodeResponse(ReferenceObjects.distributedObjectInfos);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddDistributedObjectListenerCodec.RequestParameters params = ClientAddDistributedObjectListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeDistributedObjectEvent(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemoveDistributedObjectListenerCodec.RequestParameters params = ClientRemoveDistributedObjectListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ClientRemoveDistributedObjectListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientPingCodec.RequestParameters params = ClientPingCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientPingCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutCodec.RequestParameters params = MapPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetCodec.RequestParameters params = MapGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveCodec.RequestParameters params = MapRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapRemoveCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReplaceCodec.RequestParameters params = MapReplaceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapReplaceCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReplaceIfSameCodec.RequestParameters params = MapReplaceIfSameCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.testValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapReplaceIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapContainsKeyCodec.RequestParameters params = MapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapContainsValueCodec.RequestParameters params = MapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = MapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveIfSameCodec.RequestParameters params = MapRemoveIfSameCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapRemoveIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapDeleteCodec.RequestParameters params = MapDeleteCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapDeleteCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFlushCodec.RequestParameters params = MapFlushCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapFlushCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryRemoveCodec.RequestParameters params = MapTryRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = MapTryRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryPutCodec.RequestParameters params = MapTryPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = MapTryPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutTransientCodec.RequestParameters params = MapPutTransientCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutTransientCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutIfAbsentCodec.RequestParameters params = MapPutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSetCodec.RequestParameters params = MapSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapSetCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLockCodec.RequestParameters params = MapLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapLockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryLockCodec.RequestParameters params = MapTryLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapIsLockedCodec.RequestParameters params = MapIsLockedCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = MapIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapUnlockCodec.RequestParameters params = MapUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddInterceptorCodec.RequestParameters params = MapAddInterceptorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.interceptor));
        }
        {
            ClientMessage clientMessage = MapAddInterceptorCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveInterceptorCodec.RequestParameters params = MapRemoveInterceptorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.id));
        }
        {
            ClientMessage clientMessage = MapRemoveInterceptorCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters params = MapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerWithPredicateCodec.RequestParameters params = MapAddEntryListenerWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerToKeyCodec.RequestParameters params = MapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerCodec.RequestParameters params = MapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddEntryListenerCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddNearCacheEntryListenerCodec.RequestParameters params = MapAddNearCacheEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.4), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeIMapInvalidationEvent(null, ReferenceObjects.aString, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                int length = inputStream.readInt();
                // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
                // (version 1.4), only the bytes after frame length fields are compared
                int frameLength = clientMessage.getFrameLength();
                Assert.assertTrue((frameLength >= length));
                inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
                byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
            }
            {
                ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeIMapBatchInvalidationEvent(ReferenceObjects.datas, ReferenceObjects.strings, ReferenceObjects.uuids, ReferenceObjects.longs);
                int length = inputStream.readInt();
                // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
                // (version 1.4), only the bytes after frame length fields are compared
                int frameLength = clientMessage.getFrameLength();
                Assert.assertTrue((frameLength >= length));
                inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
                byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveEntryListenerCodec.RequestParameters params = MapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddPartitionLostListenerCodec.RequestParameters params = MapAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeMapPartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.aString);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemovePartitionLostListenerCodec.RequestParameters params = MapRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MapRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetEntryViewCodec.RequestParameters params = MapGetEntryViewCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapGetEntryViewCodec.encodeResponse(null, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.7), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEvictCodec.RequestParameters params = MapEvictCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapEvictCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEvictAllCodec.RequestParameters params = MapEvictAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapEvictAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLoadAllCodec.RequestParameters params = MapLoadAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = MapLoadAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLoadGivenKeysCodec.RequestParameters params = MapLoadGivenKeysCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = MapLoadGivenKeysCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetCodec.RequestParameters params = MapKeySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetAllCodec.RequestParameters params = MapGetAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapGetAllCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesCodec.RequestParameters params = MapValuesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapValuesCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntrySetCodec.RequestParameters params = MapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetWithPredicateCodec.RequestParameters params = MapKeySetWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesWithPredicateCodec.RequestParameters params = MapValuesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapValuesWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntriesWithPredicateCodec.RequestParameters params = MapEntriesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddIndexCodec.RequestParameters params = MapAddIndexCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.attribute));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.ordered));
        }
        {
            ClientMessage clientMessage = MapAddIndexCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSizeCodec.RequestParameters params = MapSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapIsEmptyCodec.RequestParameters params = MapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutAllCodec.RequestParameters params = MapPutAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = MapPutAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapClearCodec.RequestParameters params = MapClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnKeyCodec.RequestParameters params = MapExecuteOnKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeyCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSubmitToKeyCodec.RequestParameters params = MapSubmitToKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapSubmitToKeyCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnAllKeysCodec.RequestParameters params = MapExecuteOnAllKeysCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
        }
        {
            ClientMessage clientMessage = MapExecuteOnAllKeysCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteWithPredicateCodec.RequestParameters params = MapExecuteWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapExecuteWithPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnKeysCodec.RequestParameters params = MapExecuteOnKeysCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeysCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapForceUnlockCodec.RequestParameters params = MapForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapForceUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetWithPagingPredicateCodec.RequestParameters params = MapKeySetWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPagingPredicateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesWithPagingPredicateCodec.RequestParameters params = MapValuesWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapValuesWithPagingPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntriesWithPagingPredicateCodec.RequestParameters params = MapEntriesWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPagingPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapClearNearCacheCodec.RequestParameters params = MapClearNearCacheCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.target));
        }
        {
            ClientMessage clientMessage = MapClearNearCacheCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFetchKeysCodec.RequestParameters params = MapFetchKeysCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = MapFetchKeysCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFetchEntriesCodec.RequestParameters params = MapFetchEntriesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = MapFetchEntriesCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapPutCodec.RequestParameters params = MultiMapPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapGetCodec.RequestParameters params = MultiMapGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapGetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveCodec.RequestParameters params = MultiMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapKeySetCodec.RequestParameters params = MultiMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapValuesCodec.RequestParameters params = MultiMapValuesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapEntrySetCodec.RequestParameters params = MultiMapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsKeyCodec.RequestParameters params = MultiMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsValueCodec.RequestParameters params = MultiMapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = MultiMapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsEntryCodec.RequestParameters params = MultiMapContainsEntryCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapContainsEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapSizeCodec.RequestParameters params = MultiMapSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapClearCodec.RequestParameters params = MultiMapClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapValueCountCodec.RequestParameters params = MultiMapValueCountCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapValueCountCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapAddEntryListenerToKeyCodec.RequestParameters params = MultiMapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapAddEntryListenerCodec.RequestParameters params = MultiMapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveEntryListenerCodec.RequestParameters params = MultiMapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapLockCodec.RequestParameters params = MultiMapLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapLockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapTryLockCodec.RequestParameters params = MultiMapTryLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapIsLockedCodec.RequestParameters params = MultiMapIsLockedCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = MultiMapIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapUnlockCodec.RequestParameters params = MultiMapUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapForceUnlockCodec.RequestParameters params = MultiMapForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapForceUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveEntryCodec.RequestParameters params = MultiMapRemoveEntryCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueOfferCodec.RequestParameters params = QueueOfferCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeoutMillis));
        }
        {
            ClientMessage clientMessage = QueueOfferCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePutCodec.RequestParameters params = QueuePutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueuePutCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueSizeCodec.RequestParameters params = QueueSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemoveCodec.RequestParameters params = QueueRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueueRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePollCodec.RequestParameters params = QueuePollCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeoutMillis));
        }
        {
            ClientMessage clientMessage = QueuePollCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueTakeCodec.RequestParameters params = QueueTakeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueTakeCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePeekCodec.RequestParameters params = QueuePeekCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueuePeekCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueIteratorCodec.RequestParameters params = QueueIteratorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueIteratorCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueDrainToCodec.RequestParameters params = QueueDrainToCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueDrainToCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueDrainToMaxSizeCodec.RequestParameters params = QueueDrainToMaxSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
        }
        {
            ClientMessage clientMessage = QueueDrainToMaxSizeCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueContainsCodec.RequestParameters params = QueueContainsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueueContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueContainsAllCodec.RequestParameters params = QueueContainsAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueCompareAndRemoveAllCodec.RequestParameters params = QueueCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueCompareAndRetainAllCodec.RequestParameters params = QueueCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueClearCodec.RequestParameters params = QueueClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueAddAllCodec.RequestParameters params = QueueAddAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueAddListenerCodec.RequestParameters params = QueueAddListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = QueueAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = QueueAddListenerCodec.encodeItemEvent(null, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemoveListenerCodec.RequestParameters params = QueueRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = QueueRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemainingCapacityCodec.RequestParameters params = QueueRemainingCapacityCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueRemainingCapacityCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueIsEmptyCodec.RequestParameters params = QueueIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicPublishCodec.RequestParameters params = TopicPublishCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.message));
        }
        {
            ClientMessage clientMessage = TopicPublishCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicAddMessageListenerCodec.RequestParameters params = TopicAddMessageListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeTopicEvent(ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aString);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicRemoveMessageListenerCodec.RequestParameters params = TopicRemoveMessageListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = TopicRemoveMessageListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSizeCodec.RequestParameters params = ListSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListContainsCodec.RequestParameters params = ListContainsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListContainsAllCodec.RequestParameters params = ListContainsAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddCodec.RequestParameters params = ListAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveCodec.RequestParameters params = ListRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddAllCodec.RequestParameters params = ListAddAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = ListAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListCompareAndRemoveAllCodec.RequestParameters params = ListCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListCompareAndRetainAllCodec.RequestParameters params = ListCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListClearCodec.RequestParameters params = ListClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListGetAllCodec.RequestParameters params = ListGetAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListGetAllCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddListenerCodec.RequestParameters params = ListAddListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ListAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ListAddListenerCodec.encodeItemEvent(null, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveListenerCodec.RequestParameters params = ListRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ListRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIsEmptyCodec.RequestParameters params = ListIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddAllWithIndexCodec.RequestParameters params = ListAddAllWithIndexCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = ListAddAllWithIndexCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListGetCodec.RequestParameters params = ListGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSetCodec.RequestParameters params = ListSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListSetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddWithIndexCodec.RequestParameters params = ListAddWithIndexCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListAddWithIndexCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveWithIndexCodec.RequestParameters params = ListRemoveWithIndexCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListRemoveWithIndexCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListLastIndexOfCodec.RequestParameters params = ListLastIndexOfCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListLastIndexOfCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIndexOfCodec.RequestParameters params = ListIndexOfCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListIndexOfCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSubCodec.RequestParameters params = ListSubCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.from));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.to));
        }
        {
            ClientMessage clientMessage = ListSubCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIteratorCodec.RequestParameters params = ListIteratorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListIteratorCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListListIteratorCodec.RequestParameters params = ListListIteratorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListListIteratorCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetSizeCodec.RequestParameters params = SetSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetContainsCodec.RequestParameters params = SetContainsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetContainsAllCodec.RequestParameters params = SetContainsAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
        }
        {
            ClientMessage clientMessage = SetContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddCodec.RequestParameters params = SetAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetRemoveCodec.RequestParameters params = SetRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddAllCodec.RequestParameters params = SetAddAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = SetAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetCompareAndRemoveAllCodec.RequestParameters params = SetCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = SetCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetCompareAndRetainAllCodec.RequestParameters params = SetCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = SetCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetClearCodec.RequestParameters params = SetClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetGetAllCodec.RequestParameters params = SetGetAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetGetAllCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddListenerCodec.RequestParameters params = SetAddListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = SetAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = SetAddListenerCodec.encodeItemEvent(null, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetRemoveListenerCodec.RequestParameters params = SetRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = SetRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetIsEmptyCodec.RequestParameters params = SetIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockIsLockedCodec.RequestParameters params = LockIsLockedCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockIsLockedByCurrentThreadCodec.RequestParameters params = LockIsLockedByCurrentThreadCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = LockIsLockedByCurrentThreadCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockGetLockCountCodec.RequestParameters params = LockGetLockCountCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockGetLockCountCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockGetRemainingLeaseTimeCodec.RequestParameters params = LockGetRemainingLeaseTimeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockGetRemainingLeaseTimeCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockLockCodec.RequestParameters params = LockLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.leaseTime));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockLockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockUnlockCodec.RequestParameters params = LockUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockForceUnlockCodec.RequestParameters params = LockForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockForceUnlockCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockTryLockCodec.RequestParameters params = LockTryLockCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionAwaitCodec.RequestParameters params = ConditionAwaitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = ConditionAwaitCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionBeforeAwaitCodec.RequestParameters params = ConditionBeforeAwaitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = ConditionBeforeAwaitCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionSignalCodec.RequestParameters params = ConditionSignalCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
        }
        {
            ClientMessage clientMessage = ConditionSignalCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionSignalAllCodec.RequestParameters params = ConditionSignalAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
        }
        {
            ClientMessage clientMessage = ConditionSignalAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceShutdownCodec.RequestParameters params = ExecutorServiceShutdownCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ExecutorServiceShutdownCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceIsShutdownCodec.RequestParameters params = ExecutorServiceIsShutdownCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ExecutorServiceIsShutdownCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceCancelOnPartitionCodec.RequestParameters params = ExecutorServiceCancelOnPartitionCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.interrupt));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnPartitionCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceCancelOnAddressCodec.RequestParameters params = ExecutorServiceCancelOnAddressCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.interrupt));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnAddressCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceSubmitToPartitionCodec.RequestParameters params = ExecutorServiceSubmitToPartitionCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToPartitionCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceSubmitToAddressCodec.RequestParameters params = ExecutorServiceSubmitToAddressCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToAddressCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongApplyCodec.RequestParameters params = AtomicLongApplyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongApplyCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAlterCodec.RequestParameters params = AtomicLongAlterCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAlterAndGetCodec.RequestParameters params = AtomicLongAlterAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndAlterCodec.RequestParameters params = AtomicLongGetAndAlterCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAlterCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAddAndGetCodec.RequestParameters params = AtomicLongAddAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delta));
        }
        {
            ClientMessage clientMessage = AtomicLongAddAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongCompareAndSetCodec.RequestParameters params = AtomicLongCompareAndSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.expected));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.updated));
        }
        {
            ClientMessage clientMessage = AtomicLongCompareAndSetCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongDecrementAndGetCodec.RequestParameters params = AtomicLongDecrementAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongDecrementAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetCodec.RequestParameters params = AtomicLongGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongGetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndAddCodec.RequestParameters params = AtomicLongGetAndAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delta));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAddCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndSetCodec.RequestParameters params = AtomicLongGetAndSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndSetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongIncrementAndGetCodec.RequestParameters params = AtomicLongIncrementAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongIncrementAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndIncrementCodec.RequestParameters params = AtomicLongGetAndIncrementCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndIncrementCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongSetCodec.RequestParameters params = AtomicLongSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicLongSetCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceApplyCodec.RequestParameters params = AtomicReferenceApplyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceApplyCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceAlterCodec.RequestParameters params = AtomicReferenceAlterCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceAlterAndGetCodec.RequestParameters params = AtomicReferenceAlterAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterAndGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetAndAlterCodec.RequestParameters params = AtomicReferenceGetAndAlterCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndAlterCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceContainsCodec.RequestParameters params = AtomicReferenceContainsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expected));
        }
        {
            ClientMessage clientMessage = AtomicReferenceContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceCompareAndSetCodec.RequestParameters params = AtomicReferenceCompareAndSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expected));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.updated));
        }
        {
            ClientMessage clientMessage = AtomicReferenceCompareAndSetCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetCodec.RequestParameters params = AtomicReferenceGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceSetCodec.RequestParameters params = AtomicReferenceSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceClearCodec.RequestParameters params = AtomicReferenceClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetAndSetCodec.RequestParameters params = AtomicReferenceGetAndSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndSetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceSetAndGetCodec.RequestParameters params = AtomicReferenceSetAndGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetAndGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceIsNullCodec.RequestParameters params = AtomicReferenceIsNullCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceIsNullCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchAwaitCodec.RequestParameters params = CountDownLatchAwaitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = CountDownLatchAwaitCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchCountDownCodec.RequestParameters params = CountDownLatchCountDownCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CountDownLatchCountDownCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchGetCountCodec.RequestParameters params = CountDownLatchGetCountCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CountDownLatchGetCountCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchTrySetCountCodec.RequestParameters params = CountDownLatchTrySetCountCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.count));
        }
        {
            ClientMessage clientMessage = CountDownLatchTrySetCountCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreInitCodec.RequestParameters params = SemaphoreInitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreInitCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreAcquireCodec.RequestParameters params = SemaphoreAcquireCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreAcquireCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreAvailablePermitsCodec.RequestParameters params = SemaphoreAvailablePermitsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SemaphoreAvailablePermitsCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreDrainPermitsCodec.RequestParameters params = SemaphoreDrainPermitsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SemaphoreDrainPermitsCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreReducePermitsCodec.RequestParameters params = SemaphoreReducePermitsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.reduction));
        }
        {
            ClientMessage clientMessage = SemaphoreReducePermitsCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreReleaseCodec.RequestParameters params = SemaphoreReleaseCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreReleaseCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreTryAcquireCodec.RequestParameters params = SemaphoreTryAcquireCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = SemaphoreTryAcquireCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapPutCodec.RequestParameters params = ReplicatedMapPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapSizeCodec.RequestParameters params = ReplicatedMapSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapIsEmptyCodec.RequestParameters params = ReplicatedMapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapContainsKeyCodec.RequestParameters params = ReplicatedMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapContainsValueCodec.RequestParameters params = ReplicatedMapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapGetCodec.RequestParameters params = ReplicatedMapGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapRemoveCodec.RequestParameters params = ReplicatedMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapPutAllCodec.RequestParameters params = ReplicatedMapPutAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapClearCodec.RequestParameters params = ReplicatedMapClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters params = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters params = ReplicatedMapAddEntryListenerWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters params = ReplicatedMapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerCodec.RequestParameters params = ReplicatedMapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapRemoveEntryListenerCodec.RequestParameters params = ReplicatedMapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapKeySetCodec.RequestParameters params = ReplicatedMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapValuesCodec.RequestParameters params = ReplicatedMapValuesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapEntrySetCodec.RequestParameters params = ReplicatedMapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters params = ReplicatedMapAddNearCacheEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeEntryEvent(null, null, null, null, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceCancelCodec.RequestParameters params = MapReduceCancelCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
        }
        {
            ClientMessage clientMessage = MapReduceCancelCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceJobProcessInformationCodec.RequestParameters params = MapReduceJobProcessInformationCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
        }
        {
            ClientMessage clientMessage = MapReduceJobProcessInformationCodec.encodeResponse(ReferenceObjects.jobPartitionStates, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForMapCodec.RequestParameters params = MapReduceForMapCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForMapCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForListCodec.RequestParameters params = MapReduceForListCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.listName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForListCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForSetCodec.RequestParameters params = MapReduceForSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.setName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForSetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForMultiMapCodec.RequestParameters params = MapReduceForMultiMapCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.multiMapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForMultiMapCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForCustomCodec.RequestParameters params = MapReduceForCustomCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.keyValueSource));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForCustomCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapContainsKeyCodec.RequestParameters params = TransactionalMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapGetCodec.RequestParameters params = TransactionalMapGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapGetForUpdateCodec.RequestParameters params = TransactionalMapGetForUpdateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetForUpdateCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapSizeCodec.RequestParameters params = TransactionalMapSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapIsEmptyCodec.RequestParameters params = TransactionalMapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapPutCodec.RequestParameters params = TransactionalMapPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapSetCodec.RequestParameters params = TransactionalMapSetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapSetCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapPutIfAbsentCodec.RequestParameters params = TransactionalMapPutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutIfAbsentCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapReplaceCodec.RequestParameters params = TransactionalMapReplaceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapReplaceIfSameCodec.RequestParameters params = TransactionalMapReplaceIfSameCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.oldValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapRemoveCodec.RequestParameters params = TransactionalMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapDeleteCodec.RequestParameters params = TransactionalMapDeleteCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapDeleteCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapRemoveIfSameCodec.RequestParameters params = TransactionalMapRemoveIfSameCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapKeySetCodec.RequestParameters params = TransactionalMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapKeySetWithPredicateCodec.RequestParameters params = TransactionalMapKeySetWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapValuesCodec.RequestParameters params = TransactionalMapValuesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapValuesWithPredicateCodec.RequestParameters params = TransactionalMapValuesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapPutCodec.RequestParameters params = TransactionalMultiMapPutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapGetCodec.RequestParameters params = TransactionalMultiMapGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapGetCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapRemoveCodec.RequestParameters params = TransactionalMultiMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapRemoveEntryCodec.RequestParameters params = TransactionalMultiMapRemoveEntryCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapValueCountCodec.RequestParameters params = TransactionalMultiMapValueCountCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapValueCountCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapSizeCodec.RequestParameters params = TransactionalMultiMapSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetAddCodec.RequestParameters params = TransactionalSetAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalSetAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetRemoveCodec.RequestParameters params = TransactionalSetRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalSetRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetSizeCodec.RequestParameters params = TransactionalSetSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalSetSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListAddCodec.RequestParameters params = TransactionalListAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalListAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListRemoveCodec.RequestParameters params = TransactionalListRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalListRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListSizeCodec.RequestParameters params = TransactionalListSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalListSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueOfferCodec.RequestParameters params = TransactionalQueueOfferCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueueOfferCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueTakeCodec.RequestParameters params = TransactionalQueueTakeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalQueueTakeCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueuePollCodec.RequestParameters params = TransactionalQueuePollCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePollCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueuePeekCodec.RequestParameters params = TransactionalQueuePeekCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePeekCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueSizeCodec.RequestParameters params = TransactionalQueueSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalQueueSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddEntryListenerCodec.RequestParameters params = CacheAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeCacheEvent(ReferenceObjects.anInt, ReferenceObjects.cacheEventDatas, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddInvalidationListenerCodec.RequestParameters params = CacheAddInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.4), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            {
                ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeCacheInvalidationEvent(ReferenceObjects.aString, null, null, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                int length = inputStream.readInt();
                // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
                // (version 1.4), only the bytes after frame length fields are compared
                int frameLength = clientMessage.getFrameLength();
                Assert.assertTrue((frameLength >= length));
                inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
                byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
            }
            {
                ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeCacheBatchInvalidationEvent(ReferenceObjects.aString, ReferenceObjects.datas, null, ReferenceObjects.uuids, ReferenceObjects.longs);
                int length = inputStream.readInt();
                // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
                // (version 1.4), only the bytes after frame length fields are compared
                int frameLength = clientMessage.getFrameLength();
                Assert.assertTrue((frameLength >= length));
                inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
                byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheClearCodec.RequestParameters params = CacheClearCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheClearCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveAllKeysCodec.RequestParameters params = CacheRemoveAllKeysCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllKeysCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveAllCodec.RequestParameters params = CacheRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheContainsKeyCodec.RequestParameters params = CacheContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = CacheContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheCreateConfigCodec.RequestParameters params = CacheCreateConfigCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.cacheConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.createAlsoOnOthers));
        }
        {
            ClientMessage clientMessage = CacheCreateConfigCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheDestroyCodec.RequestParameters params = CacheDestroyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheDestroyCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheEntryProcessorCodec.RequestParameters params = CacheEntryProcessorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.arguments));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheEntryProcessorCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAllCodec.RequestParameters params = CacheGetAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
        }
        {
            ClientMessage clientMessage = CacheGetAllCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAndRemoveCodec.RequestParameters params = CacheGetAndRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheGetAndRemoveCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAndReplaceCodec.RequestParameters params = CacheGetAndReplaceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheGetAndReplaceCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetConfigCodec.RequestParameters params = CacheGetConfigCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.simpleName));
        }
        {
            ClientMessage clientMessage = CacheGetConfigCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetCodec.RequestParameters params = CacheGetCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
        }
        {
            ClientMessage clientMessage = CacheGetCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheIterateCodec.RequestParameters params = CacheIterateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = CacheIterateCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheListenerRegistrationCodec.RequestParameters params = CacheListenerRegistrationCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.listenerConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.shouldRegister));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = CacheListenerRegistrationCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheLoadAllCodec.RequestParameters params = CacheLoadAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = CacheLoadAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheManagementConfigCodec.RequestParameters params = CacheManagementConfigCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isStat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.enabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = CacheManagementConfigCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutIfAbsentCodec.RequestParameters params = CachePutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutIfAbsentCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutCodec.RequestParameters params = CachePutCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.get));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveEntryListenerCodec.RequestParameters params = CacheRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveInvalidationListenerCodec.RequestParameters params = CacheRemoveInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemoveInvalidationListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveCodec.RequestParameters params = CacheRemoveCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.currentValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheReplaceCodec.RequestParameters params = CacheReplaceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.oldValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheReplaceCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheSizeCodec.RequestParameters params = CacheSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheSizeCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddPartitionLostListenerCodec.RequestParameters params = CacheAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeCachePartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.aString);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemovePartitionLostListenerCodec.RequestParameters params = CacheRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutAllCodec.RequestParameters params = CachePutAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutAllCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheIterateEntriesCodec.RequestParameters params = CacheIterateEntriesCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = CacheIterateEntriesCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionClearRemoteCodec.RequestParameters params = XATransactionClearRemoteCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
        }
        {
            ClientMessage clientMessage = XATransactionClearRemoteCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCollectTransactionsCodec.RequestParameters params = XATransactionCollectTransactionsCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCollectTransactionsCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionFinalizeCodec.RequestParameters params = XATransactionFinalizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isCommit));
        }
        {
            ClientMessage clientMessage = XATransactionFinalizeCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCommitCodec.RequestParameters params = XATransactionCommitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.onePhase));
        }
        {
            ClientMessage clientMessage = XATransactionCommitCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCreateCodec.RequestParameters params = XATransactionCreateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = XATransactionCreateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionPrepareCodec.RequestParameters params = XATransactionPrepareCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
        }
        {
            ClientMessage clientMessage = XATransactionPrepareCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionRollbackCodec.RequestParameters params = XATransactionRollbackCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
        }
        {
            ClientMessage clientMessage = XATransactionRollbackCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionCommitCodec.RequestParameters params = TransactionCommitCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionCommitCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionCreateCodec.RequestParameters params = TransactionCreateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.durability));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.transactionType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionCreateCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionRollbackCodec.RequestParameters params = TransactionRollbackCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionRollbackCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryPublisherCreateWithValueCodec.RequestParameters params = ContinuousQueryPublisherCreateWithValueCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batchSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.bufferSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delaySeconds));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.populate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.coalesce));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateWithValueCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryPublisherCreateCodec.RequestParameters params = ContinuousQueryPublisherCreateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batchSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.bufferSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delaySeconds));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.populate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.coalesce));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateCodec.encodeResponse(ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryMadePublishableCodec.RequestParameters params = ContinuousQueryMadePublishableCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
        }
        {
            ClientMessage clientMessage = ContinuousQueryMadePublishableCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryAddListenerCodec.RequestParameters params = ContinuousQueryAddListenerCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.listenerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            {
                ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheSingleEvent(ReferenceObjects.aQueryCacheEventData);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
            {
                ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheBatchEvent(ReferenceObjects.queryCacheEventDatas, ReferenceObjects.aString, ReferenceObjects.anInt);
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
            }
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQuerySetReadCursorCodec.RequestParameters params = ContinuousQuerySetReadCursorCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.sequence));
        }
        {
            ClientMessage clientMessage = ContinuousQuerySetReadCursorCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryDestroyCacheCodec.RequestParameters params = ContinuousQueryDestroyCacheCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
        }
        {
            ClientMessage clientMessage = ContinuousQueryDestroyCacheCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferSizeCodec.RequestParameters params = RingbufferSizeCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferSizeCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferTailSequenceCodec.RequestParameters params = RingbufferTailSequenceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferTailSequenceCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferHeadSequenceCodec.RequestParameters params = RingbufferHeadSequenceCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferHeadSequenceCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferCapacityCodec.RequestParameters params = RingbufferCapacityCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferCapacityCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferRemainingCapacityCodec.RequestParameters params = RingbufferRemainingCapacityCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferRemainingCapacityCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferAddCodec.RequestParameters params = RingbufferAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.overflowPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = RingbufferAddCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferReadOneCodec.RequestParameters params = RingbufferReadOneCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.sequence));
        }
        {
            ClientMessage clientMessage = RingbufferReadOneCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferAddAllCodec.RequestParameters params = RingbufferAddAllCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.overflowPolicy));
        }
        {
            ClientMessage clientMessage = RingbufferAddAllCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferReadManyCodec.RequestParameters params = RingbufferReadManyCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.startSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.minCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxCount));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.filter));
        }
        {
            ClientMessage clientMessage = RingbufferReadManyCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas, null, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.3) which is earlier than latest change in the message
            // (version 1.6), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ServerCompatibilityNullTest_1_3.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorShutdownCodec.RequestParameters params = DurableExecutorShutdownCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = DurableExecutorShutdownCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorIsShutdownCodec.RequestParameters params = DurableExecutorIsShutdownCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = DurableExecutorIsShutdownCodec.encodeResponse(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorSubmitToPartitionCodec.RequestParameters params = DurableExecutorSubmitToPartitionCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
        }
        {
            ClientMessage clientMessage = DurableExecutorSubmitToPartitionCodec.encodeResponse(ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorRetrieveResultCodec.RequestParameters params = DurableExecutorRetrieveResultCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveResultCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorDisposeResultCodec.RequestParameters params = DurableExecutorDisposeResultCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorDisposeResultCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters params = DurableExecutorRetrieveAndDisposeResultCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveAndDisposeResultCodec.encodeResponse(null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CardinalityEstimatorAddCodec.RequestParameters params = CardinalityEstimatorAddCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.hash));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorAddCodec.encodeResponse();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CardinalityEstimatorEstimateCodec.RequestParameters params = CardinalityEstimatorEstimateCodec.decodeRequest(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorEstimateCodec.encodeResponse(ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        inputStream.close();
        input.close();
    }
}

