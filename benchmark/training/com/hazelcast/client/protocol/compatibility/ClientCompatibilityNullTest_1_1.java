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


import ClientAddMembershipListenerCodec.AbstractEventHandler;
import ClientAuthenticationCodec.ResponseParameters;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.core.Member;
import com.hazelcast.core.com.hazelcast.core.Member;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static ContinuousQueryAddListenerCodec.AbstractEventHandler.<init>;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientCompatibilityNullTest_1_1 {
    private static final int FRAME_LEN_FIELD_SIZE = 4;

    @Test
    public void test() throws IOException {
        InputStream input = getClass().getResourceAsStream("/1.1.protocol.compatibility.null.binary");
        DataInputStream inputStream = new DataInputStream(input);
        {
            ClientMessage clientMessage = ClientAuthenticationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, null, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.strings, null, null);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.8), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAuthenticationCodec.ResponseParameters params = ClientAuthenticationCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.status));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertFalse(params.serverHazelcastVersionExist);
            Assert.assertFalse(params.clientUnregisteredMembersExist);
            Assert.assertFalse(params.partitionCountExist);
            Assert.assertFalse(params.clusterIdExist);
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCustomCodec.encodeRequest(ReferenceObjects.aData, null, null, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.strings, null, null);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.8), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAuthenticationCustomCodec.ResponseParameters params = ClientAuthenticationCustomCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.status));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertFalse(params.serverHazelcastVersionExist);
            Assert.assertFalse(params.clientUnregisteredMembersExist);
            Assert.assertFalse(params.partitionCountExist);
            Assert.assertFalse(params.clusterIdExist);
        }
        {
            ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddMembershipListenerCodec.ResponseParameters params = ClientAddMembershipListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddMembershipListenerCodecHandler extends ClientAddMembershipListenerCodec.AbstractEventHandler {
                public void handleMemberEventV10(com.hazelcast.core.Member member, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aMember, member));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }

                public void handleMemberListEventV10(Collection<com.hazelcast.core.Member> members) {
                    Assert.assertTrue(ReferenceObjects.isEqual(members, members));
                }

                public void handleMemberAttributeChangeEventV10(String uuid, String key, int operationType, String value) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, operationType));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                }
            }
            ClientAddMembershipListenerCodecHandler handler = new ClientAddMembershipListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientCreateProxyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientCreateProxyCodec.ResponseParameters params = ClientCreateProxyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientDestroyProxyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientDestroyProxyCodec.ResponseParameters params = ClientDestroyProxyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientGetPartitionsCodec.encodeRequest();
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.5), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientGetPartitionsCodec.ResponseParameters params = ClientGetPartitionsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionTable, params.partitions));
            Assert.assertFalse(params.partitionStateVersionExist);
        }
        {
            ClientMessage clientMessage = ClientRemoveAllListenersCodec.encodeRequest();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemoveAllListenersCodec.ResponseParameters params = ClientRemoveAllListenersCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddPartitionLostListenerCodec.ResponseParameters params = ClientAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddPartitionLostListenerCodecHandler extends ClientAddPartitionLostListenerCodec.AbstractEventHandler {
                public void handlePartitionLostEventV10(int partitionId, int lostBackupCount, com.hazelcast.nio.Address source) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, lostBackupCount));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, source));
                }
            }
            ClientAddPartitionLostListenerCodecHandler handler = new ClientAddPartitionLostListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemovePartitionLostListenerCodec.ResponseParameters params = ClientRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ClientGetDistributedObjectsCodec.encodeRequest();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientGetDistributedObjectsCodec.ResponseParameters params = ClientGetDistributedObjectsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.distributedObjectInfos, params.response));
        }
        {
            ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientAddDistributedObjectListenerCodec.ResponseParameters params = ClientAddDistributedObjectListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddDistributedObjectListenerCodecHandler extends ClientAddDistributedObjectListenerCodec.AbstractEventHandler {
                public void handleDistributedObjectEventV10(String name, String serviceName, String eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, serviceName));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, eventType));
                }
            }
            ClientAddDistributedObjectListenerCodecHandler handler = new ClientAddDistributedObjectListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientRemoveDistributedObjectListenerCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientRemoveDistributedObjectListenerCodec.ResponseParameters params = ClientRemoveDistributedObjectListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ClientPingCodec.encodeRequest();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ClientPingCodec.ResponseParameters params = ClientPingCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutCodec.ResponseParameters params = MapPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetCodec.ResponseParameters params = MapGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveCodec.ResponseParameters params = MapRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReplaceCodec.ResponseParameters params = MapReplaceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapReplaceIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReplaceIfSameCodec.ResponseParameters params = MapReplaceIfSameCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapContainsKeyCodec.ResponseParameters params = MapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapContainsValueCodec.ResponseParameters params = MapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveIfSameCodec.ResponseParameters params = MapRemoveIfSameCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapDeleteCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapDeleteCodec.ResponseParameters params = MapDeleteCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapFlushCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFlushCodec.ResponseParameters params = MapFlushCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapTryRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryRemoveCodec.ResponseParameters params = MapTryRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapTryPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryPutCodec.ResponseParameters params = MapTryPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutTransientCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutTransientCodec.ResponseParameters params = MapPutTransientCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutIfAbsentCodec.ResponseParameters params = MapPutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSetCodec.ResponseParameters params = MapSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLockCodec.ResponseParameters params = MapLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapTryLockCodec.ResponseParameters params = MapTryLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapIsLockedCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapIsLockedCodec.ResponseParameters params = MapIsLockedCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapUnlockCodec.ResponseParameters params = MapUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapAddInterceptorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddInterceptorCodec.ResponseParameters params = MapAddInterceptorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveInterceptorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveInterceptorCodec.ResponseParameters params = MapRemoveInterceptorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters params = MapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerToKeyWithPredicateCodecHandler extends MapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerToKeyWithPredicateCodecHandler handler = new MapAddEntryListenerToKeyWithPredicateCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerWithPredicateCodec.ResponseParameters params = MapAddEntryListenerWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerWithPredicateCodecHandler extends MapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerWithPredicateCodecHandler handler = new MapAddEntryListenerWithPredicateCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerToKeyCodec.ResponseParameters params = MapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerToKeyCodecHandler extends MapAddEntryListenerToKeyCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerToKeyCodecHandler handler = new MapAddEntryListenerToKeyCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddEntryListenerCodec.ResponseParameters params = MapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerCodecHandler extends MapAddEntryListenerCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerCodecHandler handler = new MapAddEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.4), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddNearCacheEntryListenerCodec.ResponseParameters params = MapAddNearCacheEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddNearCacheEntryListenerCodecHandler extends MapAddNearCacheEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleIMapInvalidationEventV10(com.hazelcast.nio.serialization.Data key) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                }

                public void handleIMapInvalidationEventV14(com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleIMapBatchInvalidationEventV10(Collection<com.hazelcast.nio.serialization.Data> keys) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                }

                public void handleIMapBatchInvalidationEventV14(Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            MapAddNearCacheEntryListenerCodecHandler handler = new MapAddNearCacheEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemoveEntryListenerCodec.ResponseParameters params = MapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddPartitionLostListenerCodec.ResponseParameters params = MapAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddPartitionLostListenerCodecHandler extends MapAddPartitionLostListenerCodec.AbstractEventHandler {
                public void handleMapPartitionLostEventV10(int partitionId, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            MapAddPartitionLostListenerCodecHandler handler = new MapAddPartitionLostListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapRemovePartitionLostListenerCodec.ResponseParameters params = MapRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapGetEntryViewCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.7), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetEntryViewCodec.ResponseParameters params = MapGetEntryViewCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
            Assert.assertFalse(params.maxIdleExist);
        }
        {
            ClientMessage clientMessage = MapEvictCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEvictCodec.ResponseParameters params = MapEvictCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapEvictAllCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEvictAllCodec.ResponseParameters params = MapEvictAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapLoadAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLoadAllCodec.ResponseParameters params = MapLoadAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapLoadGivenKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapLoadGivenKeysCodec.ResponseParameters params = MapLoadGivenKeysCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetCodec.ResponseParameters params = MapKeySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapGetAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapGetAllCodec.ResponseParameters params = MapGetAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesCodec.ResponseParameters params = MapValuesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntrySetCodec.ResponseParameters params = MapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetWithPredicateCodec.ResponseParameters params = MapKeySetWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesWithPredicateCodec.ResponseParameters params = MapValuesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntriesWithPredicateCodec.ResponseParameters params = MapEntriesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapAddIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapAddIndexCodec.ResponseParameters params = MapAddIndexCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSizeCodec.ResponseParameters params = MapSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MapIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapIsEmptyCodec.ResponseParameters params = MapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapPutAllCodec.ResponseParameters params = MapPutAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapClearCodec.ResponseParameters params = MapClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnKeyCodec.ResponseParameters params = MapExecuteOnKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapSubmitToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapSubmitToKeyCodec.ResponseParameters params = MapSubmitToKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteOnAllKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnAllKeysCodec.ResponseParameters params = MapExecuteOnAllKeysCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteWithPredicateCodec.ResponseParameters params = MapExecuteWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapExecuteOnKeysCodec.ResponseParameters params = MapExecuteOnKeysCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapForceUnlockCodec.ResponseParameters params = MapForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapKeySetWithPagingPredicateCodec.ResponseParameters params = MapKeySetWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapValuesWithPagingPredicateCodec.ResponseParameters params = MapValuesWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapEntriesWithPagingPredicateCodec.ResponseParameters params = MapEntriesWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapClearNearCacheCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapClearNearCacheCodec.ResponseParameters params = MapClearNearCacheCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MapFetchKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFetchKeysCodec.ResponseParameters params = MapFetchKeysCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapFetchEntriesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapFetchEntriesCodec.ResponseParameters params = MapFetchEntriesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = MultiMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapPutCodec.ResponseParameters params = MultiMapPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapGetCodec.ResponseParameters params = MultiMapGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveCodec.ResponseParameters params = MultiMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapKeySetCodec.ResponseParameters params = MultiMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapValuesCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapValuesCodec.ResponseParameters params = MultiMapValuesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapEntrySetCodec.ResponseParameters params = MultiMapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsKeyCodec.ResponseParameters params = MultiMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsValueCodec.ResponseParameters params = MultiMapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapContainsEntryCodec.ResponseParameters params = MultiMapContainsEntryCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapSizeCodec.ResponseParameters params = MultiMapSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapClearCodec.ResponseParameters params = MultiMapClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MultiMapValueCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapValueCountCodec.ResponseParameters params = MultiMapValueCountCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapAddEntryListenerToKeyCodec.ResponseParameters params = MultiMapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MultiMapAddEntryListenerToKeyCodecHandler extends MultiMapAddEntryListenerToKeyCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MultiMapAddEntryListenerToKeyCodecHandler handler = new MultiMapAddEntryListenerToKeyCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapAddEntryListenerCodec.ResponseParameters params = MultiMapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MultiMapAddEntryListenerCodecHandler extends MultiMapAddEntryListenerCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MultiMapAddEntryListenerCodecHandler handler = new MultiMapAddEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveEntryListenerCodec.ResponseParameters params = MultiMapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapLockCodec.ResponseParameters params = MultiMapLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MultiMapTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapTryLockCodec.ResponseParameters params = MultiMapTryLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapIsLockedCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapIsLockedCodec.ResponseParameters params = MultiMapIsLockedCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapUnlockCodec.ResponseParameters params = MultiMapUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MultiMapForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapForceUnlockCodec.ResponseParameters params = MultiMapForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MultiMapRemoveEntryCodec.ResponseParameters params = MultiMapRemoveEntryCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueOfferCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueOfferCodec.ResponseParameters params = QueueOfferCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueuePutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePutCodec.ResponseParameters params = QueuePutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = QueueSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueSizeCodec.ResponseParameters params = QueueSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = QueueRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemoveCodec.ResponseParameters params = QueueRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueuePollCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePollCodec.ResponseParameters params = QueuePollCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = QueueTakeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueTakeCodec.ResponseParameters params = QueueTakeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = QueuePeekCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueuePeekCodec.ResponseParameters params = QueuePeekCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = QueueIteratorCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueIteratorCodec.ResponseParameters params = QueueIteratorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueDrainToCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueDrainToCodec.ResponseParameters params = QueueDrainToCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueDrainToMaxSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueDrainToMaxSizeCodec.ResponseParameters params = QueueDrainToMaxSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueContainsCodec.ResponseParameters params = QueueContainsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueContainsAllCodec.ResponseParameters params = QueueContainsAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueCompareAndRemoveAllCodec.ResponseParameters params = QueueCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueCompareAndRetainAllCodec.ResponseParameters params = QueueCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueClearCodec.ResponseParameters params = QueueClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = QueueAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueAddAllCodec.ResponseParameters params = QueueAddAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueAddListenerCodec.ResponseParameters params = QueueAddListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class QueueAddListenerCodecHandler extends QueueAddListenerCodec.AbstractEventHandler {
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            QueueAddListenerCodecHandler handler = new QueueAddListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = QueueRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemoveListenerCodec.ResponseParameters params = QueueRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueRemainingCapacityCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueRemainingCapacityCodec.ResponseParameters params = QueueRemainingCapacityCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = QueueIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            QueueIsEmptyCodec.ResponseParameters params = QueueIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TopicPublishCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicPublishCodec.ResponseParameters params = TopicPublishCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicAddMessageListenerCodec.ResponseParameters params = TopicAddMessageListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class TopicAddMessageListenerCodecHandler extends TopicAddMessageListenerCodec.AbstractEventHandler {
                public void handleTopicEventV10(com.hazelcast.nio.serialization.Data item, long publishTime, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, publishTime));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            TopicAddMessageListenerCodecHandler handler = new TopicAddMessageListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = TopicRemoveMessageListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TopicRemoveMessageListenerCodec.ResponseParameters params = TopicRemoveMessageListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSizeCodec.ResponseParameters params = ListSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListContainsCodec.ResponseParameters params = ListContainsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListContainsAllCodec.ResponseParameters params = ListContainsAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddCodec.ResponseParameters params = ListAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveCodec.ResponseParameters params = ListRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddAllCodec.ResponseParameters params = ListAddAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListCompareAndRemoveAllCodec.ResponseParameters params = ListCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListCompareAndRetainAllCodec.ResponseParameters params = ListCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListClearCodec.ResponseParameters params = ListClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ListGetAllCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListGetAllCodec.ResponseParameters params = ListGetAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddListenerCodec.ResponseParameters params = ListAddListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ListAddListenerCodecHandler extends ListAddListenerCodec.AbstractEventHandler {
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            ListAddListenerCodecHandler handler = new ListAddListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ListRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveListenerCodec.ResponseParameters params = ListRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIsEmptyCodec.ResponseParameters params = ListIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddAllWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddAllWithIndexCodec.ResponseParameters params = ListAddAllWithIndexCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListGetCodec.ResponseParameters params = ListGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ListSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSetCodec.ResponseParameters params = ListSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ListAddWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListAddWithIndexCodec.ResponseParameters params = ListAddWithIndexCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ListRemoveWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListRemoveWithIndexCodec.ResponseParameters params = ListRemoveWithIndexCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ListLastIndexOfCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListLastIndexOfCodec.ResponseParameters params = ListLastIndexOfCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListIndexOfCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIndexOfCodec.ResponseParameters params = ListIndexOfCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListSubCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListSubCodec.ResponseParameters params = ListSubCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListIteratorCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListIteratorCodec.ResponseParameters params = ListIteratorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListListIteratorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ListListIteratorCodec.ResponseParameters params = ListListIteratorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = SetSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetSizeCodec.ResponseParameters params = SetSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SetContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetContainsCodec.ResponseParameters params = SetContainsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetContainsAllCodec.ResponseParameters params = SetContainsAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddCodec.ResponseParameters params = SetAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetRemoveCodec.ResponseParameters params = SetRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddAllCodec.ResponseParameters params = SetAddAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetCompareAndRemoveAllCodec.ResponseParameters params = SetCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetCompareAndRetainAllCodec.ResponseParameters params = SetCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetClearCodec.ResponseParameters params = SetClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = SetGetAllCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetGetAllCodec.ResponseParameters params = SetGetAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = SetAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetAddListenerCodec.ResponseParameters params = SetAddListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class SetAddListenerCodecHandler extends SetAddListenerCodec.AbstractEventHandler {
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            SetAddListenerCodecHandler handler = new SetAddListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = SetRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetRemoveListenerCodec.ResponseParameters params = SetRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SetIsEmptyCodec.ResponseParameters params = SetIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockIsLockedCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockIsLockedCodec.ResponseParameters params = LockIsLockedCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockIsLockedByCurrentThreadCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockIsLockedByCurrentThreadCodec.ResponseParameters params = LockIsLockedByCurrentThreadCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockGetLockCountCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockGetLockCountCodec.ResponseParameters params = LockGetLockCountCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = LockGetRemainingLeaseTimeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockGetRemainingLeaseTimeCodec.ResponseParameters params = LockGetRemainingLeaseTimeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = LockLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockLockCodec.ResponseParameters params = LockLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = LockUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockUnlockCodec.ResponseParameters params = LockUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = LockForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockForceUnlockCodec.ResponseParameters params = LockForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = LockTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            LockTryLockCodec.ResponseParameters params = LockTryLockCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ConditionAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionAwaitCodec.ResponseParameters params = ConditionAwaitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ConditionBeforeAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.2), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionBeforeAwaitCodec.ResponseParameters params = ConditionBeforeAwaitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ConditionSignalCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionSignalCodec.ResponseParameters params = ConditionSignalCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ConditionSignalAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ConditionSignalAllCodec.ResponseParameters params = ConditionSignalAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ExecutorServiceShutdownCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceShutdownCodec.ResponseParameters params = ExecutorServiceShutdownCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ExecutorServiceIsShutdownCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceIsShutdownCodec.ResponseParameters params = ExecutorServiceIsShutdownCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceCancelOnPartitionCodec.ResponseParameters params = ExecutorServiceCancelOnPartitionCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceCancelOnAddressCodec.ResponseParameters params = ExecutorServiceCancelOnAddressCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceSubmitToPartitionCodec.ResponseParameters params = ExecutorServiceSubmitToPartitionCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anAddress);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ExecutorServiceSubmitToAddressCodec.ResponseParameters params = ExecutorServiceSubmitToAddressCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongApplyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongApplyCodec.ResponseParameters params = AtomicLongApplyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAlterCodec.ResponseParameters params = AtomicLongAlterCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAlterAndGetCodec.ResponseParameters params = AtomicLongAlterAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndAlterCodec.ResponseParameters params = AtomicLongGetAndAlterCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongAddAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongAddAndGetCodec.ResponseParameters params = AtomicLongAddAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongCompareAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongCompareAndSetCodec.ResponseParameters params = AtomicLongCompareAndSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongDecrementAndGetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongDecrementAndGetCodec.ResponseParameters params = AtomicLongDecrementAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetCodec.ResponseParameters params = AtomicLongGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndAddCodec.ResponseParameters params = AtomicLongGetAndAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndSetCodec.ResponseParameters params = AtomicLongGetAndSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongIncrementAndGetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongIncrementAndGetCodec.ResponseParameters params = AtomicLongIncrementAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndIncrementCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongGetAndIncrementCodec.ResponseParameters params = AtomicLongGetAndIncrementCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicLongSetCodec.ResponseParameters params = AtomicLongSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceApplyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceApplyCodec.ResponseParameters params = AtomicReferenceApplyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceAlterCodec.ResponseParameters params = AtomicReferenceAlterCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceAlterAndGetCodec.ResponseParameters params = AtomicReferenceAlterAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetAndAlterCodec.ResponseParameters params = AtomicReferenceGetAndAlterCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceContainsCodec.encodeRequest(ReferenceObjects.aString, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceContainsCodec.ResponseParameters params = AtomicReferenceContainsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceCompareAndSetCodec.encodeRequest(ReferenceObjects.aString, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceCompareAndSetCodec.ResponseParameters params = AtomicReferenceCompareAndSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetCodec.ResponseParameters params = AtomicReferenceGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetCodec.encodeRequest(ReferenceObjects.aString, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceSetCodec.ResponseParameters params = AtomicReferenceSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceClearCodec.ResponseParameters params = AtomicReferenceClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndSetCodec.encodeRequest(ReferenceObjects.aString, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceGetAndSetCodec.ResponseParameters params = AtomicReferenceGetAndSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetAndGetCodec.encodeRequest(ReferenceObjects.aString, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceSetAndGetCodec.ResponseParameters params = AtomicReferenceSetAndGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceIsNullCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            AtomicReferenceIsNullCodec.ResponseParameters params = AtomicReferenceIsNullCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchAwaitCodec.ResponseParameters params = CountDownLatchAwaitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchCountDownCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchCountDownCodec.ResponseParameters params = CountDownLatchCountDownCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CountDownLatchGetCountCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchGetCountCodec.ResponseParameters params = CountDownLatchGetCountCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchTrySetCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CountDownLatchTrySetCountCodec.ResponseParameters params = CountDownLatchTrySetCountCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreInitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreInitCodec.ResponseParameters params = SemaphoreInitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreAcquireCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreAcquireCodec.ResponseParameters params = SemaphoreAcquireCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreAvailablePermitsCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreAvailablePermitsCodec.ResponseParameters params = SemaphoreAvailablePermitsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreDrainPermitsCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreDrainPermitsCodec.ResponseParameters params = SemaphoreDrainPermitsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreReducePermitsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreReducePermitsCodec.ResponseParameters params = SemaphoreReducePermitsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreReleaseCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreReleaseCodec.ResponseParameters params = SemaphoreReleaseCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreTryAcquireCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            SemaphoreTryAcquireCodec.ResponseParameters params = SemaphoreTryAcquireCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapPutCodec.ResponseParameters params = ReplicatedMapPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapSizeCodec.ResponseParameters params = ReplicatedMapSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapIsEmptyCodec.ResponseParameters params = ReplicatedMapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapContainsKeyCodec.ResponseParameters params = ReplicatedMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapContainsValueCodec.ResponseParameters params = ReplicatedMapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapGetCodec.ResponseParameters params = ReplicatedMapGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapRemoveCodec.ResponseParameters params = ReplicatedMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapPutAllCodec.ResponseParameters params = ReplicatedMapPutAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ReplicatedMapClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapClearCodec.ResponseParameters params = ReplicatedMapClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters params = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerWithPredicateCodec.ResponseParameters params = ReplicatedMapAddEntryListenerWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerWithPredicateCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerToKeyCodec.ResponseParameters params = ReplicatedMapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerToKeyCodecHandler extends ReplicatedMapAddEntryListenerToKeyCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerToKeyCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddEntryListenerCodec.ResponseParameters params = ReplicatedMapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerCodecHandler extends ReplicatedMapAddEntryListenerCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerCodecHandler handler = new ReplicatedMapAddEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapRemoveEntryListenerCodec.ResponseParameters params = ReplicatedMapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapKeySetCodec.ResponseParameters params = ReplicatedMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapValuesCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapValuesCodec.ResponseParameters params = ReplicatedMapValuesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapEntrySetCodec.ResponseParameters params = ReplicatedMapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ReplicatedMapAddNearCacheEntryListenerCodec.ResponseParameters params = ReplicatedMapAddNearCacheEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddNearCacheEntryListenerCodecHandler extends ReplicatedMapAddNearCacheEntryListenerCodec.AbstractEventHandler {
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddNearCacheEntryListenerCodecHandler handler = new ReplicatedMapAddNearCacheEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = MapReduceCancelCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceCancelCodec.ResponseParameters params = MapReduceCancelCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceJobProcessInformationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceJobProcessInformationCodec.ResponseParameters params = MapReduceJobProcessInformationCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.jobPartitionStates, params.jobPartitionStates));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.processRecords));
        }
        {
            ClientMessage clientMessage = MapReduceForMapCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, ReferenceObjects.aData, null, null, ReferenceObjects.aString, ReferenceObjects.anInt, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForMapCodec.ResponseParameters params = MapReduceForMapCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForListCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, ReferenceObjects.aData, null, null, ReferenceObjects.aString, ReferenceObjects.anInt, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForListCodec.ResponseParameters params = MapReduceForListCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, ReferenceObjects.aData, null, null, ReferenceObjects.aString, ReferenceObjects.anInt, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForSetCodec.ResponseParameters params = MapReduceForSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForMultiMapCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, ReferenceObjects.aData, null, null, ReferenceObjects.aString, ReferenceObjects.anInt, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForMultiMapCodec.ResponseParameters params = MapReduceForMultiMapCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForCustomCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, null, ReferenceObjects.aData, null, null, ReferenceObjects.aData, ReferenceObjects.anInt, null, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            MapReduceForCustomCodec.ResponseParameters params = MapReduceForCustomCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapContainsKeyCodec.ResponseParameters params = TransactionalMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapGetCodec.ResponseParameters params = TransactionalMapGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetForUpdateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapGetForUpdateCodec.ResponseParameters params = TransactionalMapGetForUpdateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapSizeCodec.ResponseParameters params = TransactionalMapSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapIsEmptyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapIsEmptyCodec.ResponseParameters params = TransactionalMapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapPutCodec.ResponseParameters params = TransactionalMapPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapSetCodec.ResponseParameters params = TransactionalMapSetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapPutIfAbsentCodec.ResponseParameters params = TransactionalMapPutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapReplaceCodec.ResponseParameters params = TransactionalMapReplaceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapReplaceIfSameCodec.ResponseParameters params = TransactionalMapReplaceIfSameCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapRemoveCodec.ResponseParameters params = TransactionalMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapDeleteCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapDeleteCodec.ResponseParameters params = TransactionalMapDeleteCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapRemoveIfSameCodec.ResponseParameters params = TransactionalMapRemoveIfSameCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapKeySetCodec.ResponseParameters params = TransactionalMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapKeySetWithPredicateCodec.ResponseParameters params = TransactionalMapKeySetWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapValuesCodec.ResponseParameters params = TransactionalMapValuesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMapValuesWithPredicateCodec.ResponseParameters params = TransactionalMapValuesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapPutCodec.ResponseParameters params = TransactionalMultiMapPutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapGetCodec.ResponseParameters params = TransactionalMultiMapGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapRemoveCodec.ResponseParameters params = TransactionalMultiMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapRemoveEntryCodec.ResponseParameters params = TransactionalMultiMapRemoveEntryCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapValueCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapValueCountCodec.ResponseParameters params = TransactionalMultiMapValueCountCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalMultiMapSizeCodec.ResponseParameters params = TransactionalMultiMapSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetAddCodec.ResponseParameters params = TransactionalSetAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetRemoveCodec.ResponseParameters params = TransactionalSetRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalSetSizeCodec.ResponseParameters params = TransactionalSetSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListAddCodec.ResponseParameters params = TransactionalListAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListRemoveCodec.ResponseParameters params = TransactionalListRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalListSizeCodec.ResponseParameters params = TransactionalListSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueOfferCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueOfferCodec.ResponseParameters params = TransactionalQueueOfferCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueTakeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueTakeCodec.ResponseParameters params = TransactionalQueueTakeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePollCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueuePollCodec.ResponseParameters params = TransactionalQueuePollCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePeekCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueuePeekCodec.ResponseParameters params = TransactionalQueuePeekCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionalQueueSizeCodec.ResponseParameters params = TransactionalQueueSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddEntryListenerCodec.ResponseParameters params = CacheAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddEntryListenerCodecHandler extends CacheAddEntryListenerCodec.AbstractEventHandler {
                public void handleCacheEventV10(int type, Collection<com.hazelcast.cache.impl.CacheEventData> keys, int completionId) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, type));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.cacheEventDatas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, completionId));
                }
            }
            CacheAddEntryListenerCodecHandler handler = new CacheAddEntryListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.4), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddInvalidationListenerCodec.ResponseParameters params = CacheAddInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddInvalidationListenerCodecHandler extends CacheAddInvalidationListenerCodec.AbstractEventHandler {
                @Override
                public void handleCacheInvalidationEventV10(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, sourceUuid));
                }

                public void handleCacheInvalidationEventV14(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleCacheBatchInvalidationEventV10(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, sourceUuids));
                }

                public void handleCacheBatchInvalidationEventV14(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(null, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            CacheAddInvalidationListenerCodecHandler handler = new CacheAddInvalidationListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheClearCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheClearCodec.ResponseParameters params = CacheClearCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveAllKeysCodec.ResponseParameters params = CacheRemoveAllKeysCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveAllCodec.ResponseParameters params = CacheRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheContainsKeyCodec.ResponseParameters params = CacheContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheCreateConfigCodec.encodeRequest(ReferenceObjects.aData, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheCreateConfigCodec.ResponseParameters params = CacheCreateConfigCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheDestroyCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheDestroyCodec.ResponseParameters params = CacheDestroyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheEntryProcessorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.datas, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheEntryProcessorCodec.ResponseParameters params = CacheEntryProcessorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAllCodec.ResponseParameters params = CacheGetAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAndRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAndRemoveCodec.ResponseParameters params = CacheGetAndRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAndReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, null, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetAndReplaceCodec.ResponseParameters params = CacheGetAndReplaceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetConfigCodec.ResponseParameters params = CacheGetConfigCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, null);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheGetCodec.ResponseParameters params = CacheGetCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheIterateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheIterateCodec.ResponseParameters params = CacheIterateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = CacheListenerRegistrationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anAddress);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheListenerRegistrationCodec.ResponseParameters params = CacheListenerRegistrationCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheLoadAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheLoadAllCodec.ResponseParameters params = CacheLoadAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheManagementConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.anAddress);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheManagementConfigCodec.ResponseParameters params = CacheManagementConfigCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CachePutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, null, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutIfAbsentCodec.ResponseParameters params = CachePutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CachePutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, null, ReferenceObjects.aBoolean, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutCodec.ResponseParameters params = CachePutCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveEntryListenerCodec.ResponseParameters params = CacheRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveInvalidationListenerCodec.ResponseParameters params = CacheRemoveInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, null, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemoveCodec.ResponseParameters params = CacheRemoveCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, null, ReferenceObjects.aData, null, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheReplaceCodec.ResponseParameters params = CacheReplaceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = CacheSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheSizeCodec.ResponseParameters params = CacheSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheAddPartitionLostListenerCodec.ResponseParameters params = CacheAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddPartitionLostListenerCodecHandler extends CacheAddPartitionLostListenerCodec.AbstractEventHandler {
                public void handleCachePartitionLostEventV10(int partitionId, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            CacheAddPartitionLostListenerCodecHandler handler = new CacheAddPartitionLostListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheRemovePartitionLostListenerCodec.ResponseParameters params = CacheRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CachePutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry, null, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CachePutAllCodec.ResponseParameters params = CachePutAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = CacheIterateEntriesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            CacheIterateEntriesCodec.ResponseParameters params = CacheIterateEntriesCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = XATransactionClearRemoteCodec.encodeRequest(ReferenceObjects.anXid);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionClearRemoteCodec.ResponseParameters params = XATransactionClearRemoteCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCollectTransactionsCodec.encodeRequest();
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCollectTransactionsCodec.ResponseParameters params = XATransactionCollectTransactionsCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = XATransactionFinalizeCodec.encodeRequest(ReferenceObjects.anXid, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionFinalizeCodec.ResponseParameters params = XATransactionFinalizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCommitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCommitCodec.ResponseParameters params = XATransactionCommitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCreateCodec.encodeRequest(ReferenceObjects.anXid, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionCreateCodec.ResponseParameters params = XATransactionCreateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = XATransactionPrepareCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionPrepareCodec.ResponseParameters params = XATransactionPrepareCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = XATransactionRollbackCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            XATransactionRollbackCodec.ResponseParameters params = XATransactionRollbackCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = TransactionCommitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionCommitCodec.ResponseParameters params = TransactionCommitCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = TransactionCreateCodec.encodeRequest(ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionCreateCodec.ResponseParameters params = TransactionCreateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = TransactionRollbackCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            TransactionRollbackCodec.ResponseParameters params = TransactionRollbackCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateWithValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryPublisherCreateWithValueCodec.ResponseParameters params = ContinuousQueryPublisherCreateWithValueCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryPublisherCreateCodec.ResponseParameters params = ContinuousQueryPublisherCreateCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryMadePublishableCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryMadePublishableCodec.ResponseParameters params = ContinuousQueryMadePublishableCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryAddListenerCodec.ResponseParameters params = ContinuousQueryAddListenerCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ContinuousQueryAddListenerCodecHandler extends ContinuousQueryAddListenerCodec.AbstractEventHandler {
                public void handleQueryCacheSingleEventV10(com.hazelcast.map.impl.querycache.event.QueryCacheEventData data) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aQueryCacheEventData, data));
                }

                public void handleQueryCacheBatchEventV10(Collection<com.hazelcast.map.impl.querycache.event.QueryCacheEventData> events, String source, int partitionId) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.queryCacheEventDatas, events));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, source));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                }
            }
            ContinuousQueryAddListenerCodecHandler handler = new ContinuousQueryAddListenerCodecHandler();
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
            {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.read(bytes);
                handler.handle(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            }
        }
        {
            ClientMessage clientMessage = ContinuousQuerySetReadCursorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQuerySetReadCursorCodec.ResponseParameters params = ContinuousQuerySetReadCursorCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryDestroyCacheCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            ContinuousQueryDestroyCacheCodec.ResponseParameters params = ContinuousQueryDestroyCacheCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferSizeCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferSizeCodec.ResponseParameters params = RingbufferSizeCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferTailSequenceCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferTailSequenceCodec.ResponseParameters params = RingbufferTailSequenceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferHeadSequenceCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferHeadSequenceCodec.ResponseParameters params = RingbufferHeadSequenceCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferCapacityCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferCapacityCodec.ResponseParameters params = RingbufferCapacityCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferRemainingCapacityCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferRemainingCapacityCodec.ResponseParameters params = RingbufferRemainingCapacityCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferAddCodec.ResponseParameters params = RingbufferAddCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferReadOneCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferReadOneCodec.ResponseParameters params = RingbufferReadOneCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferAddAllCodec.ResponseParameters params = RingbufferAddAllCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferReadManyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, null);
            int length = inputStream.readInt();
            // Since the test is generated for protocol version (1.1) which is earlier than latest change in the message
            // (version 1.6), only the bytes after frame length fields are compared
            int frameLength = clientMessage.getFrameLength();
            Assert.assertTrue((frameLength >= length));
            inputStream.skipBytes(ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE);
            byte[] bytes = new byte[length - (ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE)];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOfRange(clientMessage.buffer().byteArray(), ClientCompatibilityNullTest_1_1.FRAME_LEN_FIELD_SIZE, length), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            RingbufferReadManyCodec.ResponseParameters params = RingbufferReadManyCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.readCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
            Assert.assertFalse(params.itemSeqsExist);
            Assert.assertFalse(params.nextSeqExist);
        }
        {
            ClientMessage clientMessage = DurableExecutorShutdownCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorShutdownCodec.ResponseParameters params = DurableExecutorShutdownCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = DurableExecutorIsShutdownCodec.encodeRequest(ReferenceObjects.aString);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorIsShutdownCodec.ResponseParameters params = DurableExecutorIsShutdownCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorSubmitToPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorSubmitToPartitionCodec.ResponseParameters params = DurableExecutorSubmitToPartitionCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorRetrieveResultCodec.ResponseParameters params = DurableExecutorRetrieveResultCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorDisposeResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorDisposeResultCodec.ResponseParameters params = DurableExecutorDisposeResultCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveAndDisposeResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            Assert.assertTrue(ReferenceObjects.isEqual(Arrays.copyOf(clientMessage.buffer().byteArray(), clientMessage.getFrameLength()), bytes));
        }
        {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            DurableExecutorRetrieveAndDisposeResultCodec.ResponseParameters params = DurableExecutorRetrieveAndDisposeResultCodec.decodeResponse(ClientMessage.createForDecode(new SafeBuffer(bytes), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(null, params.response));
        }
        inputStream.close();
        input.close();
    }
}

