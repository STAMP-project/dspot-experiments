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
import ClientAuthenticationCodec.RequestParameters;
import ClientAuthenticationCodec.ResponseParameters;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.client.impl.protocol.ClientMessage;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static ContinuousQueryAddListenerCodec.AbstractEventHandler.<init>;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EncodeDecodeCompatibilityTest {
    @Test
    public void test() {
        {
            ClientMessage clientMessage = ClientAuthenticationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.strings, ReferenceObjects.anInt, ReferenceObjects.aString);
            ClientAuthenticationCodec.RequestParameters params = ClientAuthenticationCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.username));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.password));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isOwnerConnection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientHazelcastVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, params.labels));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clusterId));
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCodec.encodeResponse(ReferenceObjects.aByte, ReferenceObjects.anAddress, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.members, ReferenceObjects.anInt, ReferenceObjects.aString);
            ClientAuthenticationCodec.ResponseParameters params = ClientAuthenticationCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.status));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serverHazelcastVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.members, params.clientUnregisteredMembers));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clusterId));
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCustomCodec.encodeRequest(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.strings, ReferenceObjects.anInt, ReferenceObjects.aString);
            ClientAuthenticationCustomCodec.RequestParameters params = ClientAuthenticationCustomCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.credentials));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isOwnerConnection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientHazelcastVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clientName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, params.labels));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clusterId));
        }
        {
            ClientMessage clientMessage = ClientAuthenticationCustomCodec.encodeResponse(ReferenceObjects.aByte, ReferenceObjects.anAddress, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.members, ReferenceObjects.anInt, ReferenceObjects.aString);
            ClientAuthenticationCustomCodec.ResponseParameters params = ClientAuthenticationCustomCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.status));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.ownerUuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.serializationVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serverHazelcastVersion));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.members, params.clientUnregisteredMembers));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.clusterId));
        }
        {
            ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            ClientAddMembershipListenerCodec.RequestParameters params = ClientAddMembershipListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeResponse(ReferenceObjects.aString);
            ClientAddMembershipListenerCodec.ResponseParameters params = ClientAddMembershipListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddMembershipListenerCodecHandler extends ClientAddMembershipListenerCodec.AbstractEventHandler {
                @Override
                public void handleMemberEventV10(com.hazelcast.core.Member member, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aMember, member));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }

                @Override
                public void handleMemberListEventV10(Collection<com.hazelcast.core.Member> members) {
                    Assert.assertTrue(ReferenceObjects.isEqual(members, members));
                }

                @Override
                public void handleMemberAttributeChangeEventV10(String uuid, String key, int operationType, String value) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, operationType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, value));
                }
            }
            ClientAddMembershipListenerCodecHandler handler = new ClientAddMembershipListenerCodecHandler();
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberEvent(ReferenceObjects.aMember, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberListEvent(ReferenceObjects.members);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = ClientAddMembershipListenerCodec.encodeMemberAttributeChangeEvent(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aString);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientCreateProxyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ClientCreateProxyCodec.RequestParameters params = ClientCreateProxyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serviceName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.target));
        }
        {
            ClientMessage clientMessage = ClientCreateProxyCodec.encodeResponse();
            ClientCreateProxyCodec.ResponseParameters params = ClientCreateProxyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientDestroyProxyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ClientDestroyProxyCodec.RequestParameters params = ClientDestroyProxyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.serviceName));
        }
        {
            ClientMessage clientMessage = ClientDestroyProxyCodec.encodeResponse();
            ClientDestroyProxyCodec.ResponseParameters params = ClientDestroyProxyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientGetPartitionsCodec.encodeRequest();
            ClientGetPartitionsCodec.RequestParameters params = ClientGetPartitionsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientGetPartitionsCodec.encodeResponse(ReferenceObjects.aPartitionTable, ReferenceObjects.anInt);
            ClientGetPartitionsCodec.ResponseParameters params = ClientGetPartitionsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionTable, params.partitions));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionStateVersion));
        }
        {
            ClientMessage clientMessage = ClientRemoveAllListenersCodec.encodeRequest();
            ClientRemoveAllListenersCodec.RequestParameters params = ClientRemoveAllListenersCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientRemoveAllListenersCodec.encodeResponse();
            ClientRemoveAllListenersCodec.ResponseParameters params = ClientRemoveAllListenersCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            ClientAddPartitionLostListenerCodec.RequestParameters params = ClientAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            ClientAddPartitionLostListenerCodec.ResponseParameters params = ClientAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddPartitionLostListenerCodecHandler extends ClientAddPartitionLostListenerCodec.AbstractEventHandler {
                @Override
                public void handlePartitionLostEventV10(int partitionId, int lostBackupCount, com.hazelcast.nio.Address source) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, lostBackupCount));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, source));
                }
            }
            ClientAddPartitionLostListenerCodecHandler handler = new ClientAddPartitionLostListenerCodecHandler();
            {
                ClientMessage clientMessage = ClientAddPartitionLostListenerCodec.encodePartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anAddress);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString);
            ClientRemovePartitionLostListenerCodec.RequestParameters params = ClientRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ClientRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            ClientRemovePartitionLostListenerCodec.ResponseParameters params = ClientRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ClientGetDistributedObjectsCodec.encodeRequest();
            ClientGetDistributedObjectsCodec.RequestParameters params = ClientGetDistributedObjectsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientGetDistributedObjectsCodec.encodeResponse(ReferenceObjects.distributedObjectInfos);
            ClientGetDistributedObjectsCodec.ResponseParameters params = ClientGetDistributedObjectsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.distributedObjectInfos, params.response));
        }
        {
            ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeRequest(ReferenceObjects.aBoolean);
            ClientAddDistributedObjectListenerCodec.RequestParameters params = ClientAddDistributedObjectListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeResponse(ReferenceObjects.aString);
            ClientAddDistributedObjectListenerCodec.ResponseParameters params = ClientAddDistributedObjectListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ClientAddDistributedObjectListenerCodecHandler extends ClientAddDistributedObjectListenerCodec.AbstractEventHandler {
                @Override
                public void handleDistributedObjectEventV10(String name, String serviceName, String eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, serviceName));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, eventType));
                }
            }
            ClientAddDistributedObjectListenerCodecHandler handler = new ClientAddDistributedObjectListenerCodecHandler();
            {
                ClientMessage clientMessage = ClientAddDistributedObjectListenerCodec.encodeDistributedObjectEvent(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientRemoveDistributedObjectListenerCodec.encodeRequest(ReferenceObjects.aString);
            ClientRemoveDistributedObjectListenerCodec.RequestParameters params = ClientRemoveDistributedObjectListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ClientRemoveDistributedObjectListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            ClientRemoveDistributedObjectListenerCodec.ResponseParameters params = ClientRemoveDistributedObjectListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ClientPingCodec.encodeRequest();
            ClientPingCodec.RequestParameters params = ClientPingCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientPingCodec.encodeResponse();
            ClientPingCodec.ResponseParameters params = ClientPingCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientStatisticsCodec.encodeRequest(ReferenceObjects.aString);
            ClientStatisticsCodec.RequestParameters params = ClientStatisticsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.stats));
        }
        {
            ClientMessage clientMessage = ClientStatisticsCodec.encodeResponse();
            ClientStatisticsCodec.ResponseParameters params = ClientStatisticsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientDeployClassesCodec.encodeRequest(ReferenceObjects.aListOfStringToByteArrEntry);
            ClientDeployClassesCodec.RequestParameters params = ClientDeployClassesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToByteArrEntry, params.classDefinitions));
        }
        {
            ClientMessage clientMessage = ClientDeployClassesCodec.encodeResponse();
            ClientDeployClassesCodec.ResponseParameters params = ClientDeployClassesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionListenerCodec.encodeRequest();
            ClientAddPartitionListenerCodec.RequestParameters params = ClientAddPartitionListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientAddPartitionListenerCodec.encodeResponse();
            ClientAddPartitionListenerCodec.ResponseParameters params = ClientAddPartitionListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            class ClientAddPartitionListenerCodecHandler extends ClientAddPartitionListenerCodec.AbstractEventHandler {
                @Override
                public void handlePartitionsEventV15(Collection<Map.Entry<com.hazelcast.nio.Address, List<Integer>>> partitions, int partitionStateVersion) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionTable, partitions));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionStateVersion));
                }
            }
            ClientAddPartitionListenerCodecHandler handler = new ClientAddPartitionListenerCodecHandler();
            {
                ClientMessage clientMessage = ClientAddPartitionListenerCodec.encodePartitionsEvent(ReferenceObjects.aPartitionTable, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ClientCreateProxiesCodec.encodeRequest(ReferenceObjects.aListOfStringToString);
            ClientCreateProxiesCodec.RequestParameters params = ClientCreateProxiesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToString, params.proxies));
        }
        {
            ClientMessage clientMessage = ClientCreateProxiesCodec.encodeResponse();
            ClientCreateProxiesCodec.ResponseParameters params = ClientCreateProxiesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientIsFailoverSupportedCodec.encodeRequest();
            ClientIsFailoverSupportedCodec.RequestParameters params = ClientIsFailoverSupportedCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ClientIsFailoverSupportedCodec.encodeResponse(ReferenceObjects.aBoolean);
            ClientIsFailoverSupportedCodec.ResponseParameters params = ClientIsFailoverSupportedCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutCodec.RequestParameters params = MapPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutCodec.encodeResponse(ReferenceObjects.aData);
            MapPutCodec.ResponseParameters params = MapPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapGetCodec.RequestParameters params = MapGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapGetCodec.encodeResponse(ReferenceObjects.aData);
            MapGetCodec.ResponseParameters params = MapGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapRemoveCodec.RequestParameters params = MapRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapRemoveCodec.encodeResponse(ReferenceObjects.aData);
            MapRemoveCodec.ResponseParameters params = MapRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapReplaceCodec.RequestParameters params = MapReplaceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapReplaceCodec.encodeResponse(ReferenceObjects.aData);
            MapReplaceCodec.ResponseParameters params = MapReplaceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapReplaceIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapReplaceIfSameCodec.RequestParameters params = MapReplaceIfSameCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.testValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapReplaceIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapReplaceIfSameCodec.ResponseParameters params = MapReplaceIfSameCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapContainsKeyCodec.RequestParameters params = MapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapContainsKeyCodec.ResponseParameters params = MapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapContainsValueCodec.RequestParameters params = MapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = MapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapContainsValueCodec.ResponseParameters params = MapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapRemoveIfSameCodec.RequestParameters params = MapRemoveIfSameCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapRemoveIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapRemoveIfSameCodec.ResponseParameters params = MapRemoveIfSameCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapDeleteCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapDeleteCodec.RequestParameters params = MapDeleteCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapDeleteCodec.encodeResponse();
            MapDeleteCodec.ResponseParameters params = MapDeleteCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapFlushCodec.encodeRequest(ReferenceObjects.aString);
            MapFlushCodec.RequestParameters params = MapFlushCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapFlushCodec.encodeResponse();
            MapFlushCodec.ResponseParameters params = MapFlushCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapTryRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapTryRemoveCodec.RequestParameters params = MapTryRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = MapTryRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapTryRemoveCodec.ResponseParameters params = MapTryRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapTryPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapTryPutCodec.RequestParameters params = MapTryPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = MapTryPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapTryPutCodec.ResponseParameters params = MapTryPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutTransientCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutTransientCodec.RequestParameters params = MapPutTransientCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutTransientCodec.encodeResponse();
            MapPutTransientCodec.ResponseParameters params = MapPutTransientCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutIfAbsentCodec.RequestParameters params = MapPutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentCodec.encodeResponse(ReferenceObjects.aData);
            MapPutIfAbsentCodec.ResponseParameters params = MapPutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapSetCodec.RequestParameters params = MapSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapSetCodec.encodeResponse();
            MapSetCodec.ResponseParameters params = MapSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapLockCodec.RequestParameters params = MapLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapLockCodec.encodeResponse();
            MapLockCodec.ResponseParameters params = MapLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapTryLockCodec.RequestParameters params = MapTryLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapTryLockCodec.ResponseParameters params = MapTryLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapIsLockedCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapIsLockedCodec.RequestParameters params = MapIsLockedCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = MapIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapIsLockedCodec.ResponseParameters params = MapIsLockedCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapUnlockCodec.RequestParameters params = MapUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapUnlockCodec.encodeResponse();
            MapUnlockCodec.ResponseParameters params = MapUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapAddInterceptorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapAddInterceptorCodec.RequestParameters params = MapAddInterceptorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.interceptor));
        }
        {
            ClientMessage clientMessage = MapAddInterceptorCodec.encodeResponse(ReferenceObjects.aString);
            MapAddInterceptorCodec.ResponseParameters params = MapAddInterceptorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = MapRemoveInterceptorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MapRemoveInterceptorCodec.RequestParameters params = MapRemoveInterceptorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.id));
        }
        {
            ClientMessage clientMessage = MapRemoveInterceptorCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapRemoveInterceptorCodec.ResponseParameters params = MapRemoveInterceptorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters params = MapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            MapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters params = MapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerToKeyWithPredicateCodecHandler extends MapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerToKeyWithPredicateCodecHandler handler = new MapAddEntryListenerToKeyWithPredicateCodecHandler();
            {
                ClientMessage clientMessage = MapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddEntryListenerWithPredicateCodec.RequestParameters params = MapAddEntryListenerWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            MapAddEntryListenerWithPredicateCodec.ResponseParameters params = MapAddEntryListenerWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerWithPredicateCodecHandler extends MapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerWithPredicateCodecHandler handler = new MapAddEntryListenerWithPredicateCodecHandler();
            {
                ClientMessage clientMessage = MapAddEntryListenerWithPredicateCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddEntryListenerToKeyCodec.RequestParameters params = MapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            MapAddEntryListenerToKeyCodec.ResponseParameters params = MapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerToKeyCodecHandler extends MapAddEntryListenerToKeyCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerToKeyCodecHandler handler = new MapAddEntryListenerToKeyCodecHandler();
            {
                ClientMessage clientMessage = MapAddEntryListenerToKeyCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddEntryListenerCodec.RequestParameters params = MapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            MapAddEntryListenerCodec.ResponseParameters params = MapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddEntryListenerCodecHandler extends MapAddEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MapAddEntryListenerCodecHandler handler = new MapAddEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = MapAddEntryListenerCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddNearCacheEntryListenerCodec.RequestParameters params = MapAddNearCacheEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            MapAddNearCacheEntryListenerCodec.ResponseParameters params = MapAddNearCacheEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddNearCacheEntryListenerCodecHandler extends MapAddNearCacheEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleIMapInvalidationEventV10(com.hazelcast.nio.serialization.Data key) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                }

                @Override
                public void handleIMapInvalidationEventV14(com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleIMapBatchInvalidationEventV10(Collection<com.hazelcast.nio.serialization.Data> keys) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                }

                @Override
                public void handleIMapBatchInvalidationEventV14(Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            MapAddNearCacheEntryListenerCodecHandler handler = new MapAddNearCacheEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeIMapInvalidationEvent(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = MapAddNearCacheEntryListenerCodec.encodeIMapBatchInvalidationEvent(ReferenceObjects.datas, ReferenceObjects.strings, ReferenceObjects.uuids, ReferenceObjects.longs);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MapRemoveEntryListenerCodec.RequestParameters params = MapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapRemoveEntryListenerCodec.ResponseParameters params = MapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            MapAddPartitionLostListenerCodec.RequestParameters params = MapAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            MapAddPartitionLostListenerCodec.ResponseParameters params = MapAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddPartitionLostListenerCodecHandler extends MapAddPartitionLostListenerCodec.AbstractEventHandler {
                @Override
                public void handleMapPartitionLostEventV10(int partitionId, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            MapAddPartitionLostListenerCodecHandler handler = new MapAddPartitionLostListenerCodecHandler();
            {
                ClientMessage clientMessage = MapAddPartitionLostListenerCodec.encodeMapPartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.aString);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MapRemovePartitionLostListenerCodec.RequestParameters params = MapRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MapRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapRemovePartitionLostListenerCodec.ResponseParameters params = MapRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapGetEntryViewCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapGetEntryViewCodec.RequestParameters params = MapGetEntryViewCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapGetEntryViewCodec.encodeResponse(ReferenceObjects.anEntryView, ReferenceObjects.aLong);
            MapGetEntryViewCodec.ResponseParameters params = MapGetEntryViewCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anEntryView, params.response));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.maxIdle));
        }
        {
            ClientMessage clientMessage = MapEvictCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapEvictCodec.RequestParameters params = MapEvictCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapEvictCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapEvictCodec.ResponseParameters params = MapEvictCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapEvictAllCodec.encodeRequest(ReferenceObjects.aString);
            MapEvictAllCodec.RequestParameters params = MapEvictAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapEvictAllCodec.encodeResponse();
            MapEvictAllCodec.ResponseParameters params = MapEvictAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapLoadAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            MapLoadAllCodec.RequestParameters params = MapLoadAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = MapLoadAllCodec.encodeResponse();
            MapLoadAllCodec.ResponseParameters params = MapLoadAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapLoadGivenKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aBoolean);
            MapLoadGivenKeysCodec.RequestParameters params = MapLoadGivenKeysCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = MapLoadGivenKeysCodec.encodeResponse();
            MapLoadGivenKeysCodec.ResponseParameters params = MapLoadGivenKeysCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            MapKeySetCodec.RequestParameters params = MapKeySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            MapKeySetCodec.ResponseParameters params = MapKeySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapGetAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            MapGetAllCodec.RequestParameters params = MapGetAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapGetAllCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapGetAllCodec.ResponseParameters params = MapGetAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesCodec.encodeRequest(ReferenceObjects.aString);
            MapValuesCodec.RequestParameters params = MapValuesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapValuesCodec.encodeResponse(ReferenceObjects.datas);
            MapValuesCodec.ResponseParameters params = MapValuesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            MapEntrySetCodec.RequestParameters params = MapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapEntrySetCodec.ResponseParameters params = MapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapKeySetWithPredicateCodec.RequestParameters params = MapKeySetWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            MapKeySetWithPredicateCodec.ResponseParameters params = MapKeySetWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapValuesWithPredicateCodec.RequestParameters params = MapValuesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapValuesWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            MapValuesWithPredicateCodec.ResponseParameters params = MapValuesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapEntriesWithPredicateCodec.RequestParameters params = MapEntriesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapEntriesWithPredicateCodec.ResponseParameters params = MapEntriesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapAddIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean);
            MapAddIndexCodec.RequestParameters params = MapAddIndexCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.attribute));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.ordered));
        }
        {
            ClientMessage clientMessage = MapAddIndexCodec.encodeResponse();
            MapAddIndexCodec.ResponseParameters params = MapAddIndexCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapSizeCodec.encodeRequest(ReferenceObjects.aString);
            MapSizeCodec.RequestParameters params = MapSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            MapSizeCodec.ResponseParameters params = MapSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MapIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            MapIsEmptyCodec.RequestParameters params = MapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapIsEmptyCodec.ResponseParameters params = MapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry);
            MapPutAllCodec.RequestParameters params = MapPutAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = MapPutAllCodec.encodeResponse();
            MapPutAllCodec.ResponseParameters params = MapPutAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapClearCodec.encodeRequest(ReferenceObjects.aString);
            MapClearCodec.RequestParameters params = MapClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapClearCodec.encodeResponse();
            MapClearCodec.ResponseParameters params = MapClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapExecuteOnKeyCodec.RequestParameters params = MapExecuteOnKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeyCodec.encodeResponse(ReferenceObjects.aData);
            MapExecuteOnKeyCodec.ResponseParameters params = MapExecuteOnKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapSubmitToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapSubmitToKeyCodec.RequestParameters params = MapSubmitToKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MapSubmitToKeyCodec.encodeResponse(ReferenceObjects.aData);
            MapSubmitToKeyCodec.ResponseParameters params = MapSubmitToKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteOnAllKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapExecuteOnAllKeysCodec.RequestParameters params = MapExecuteOnAllKeysCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
        }
        {
            ClientMessage clientMessage = MapExecuteOnAllKeysCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapExecuteOnAllKeysCodec.ResponseParameters params = MapExecuteOnAllKeysCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            MapExecuteWithPredicateCodec.RequestParameters params = MapExecuteWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapExecuteWithPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapExecuteWithPredicateCodec.ResponseParameters params = MapExecuteWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.datas);
            MapExecuteOnKeysCodec.RequestParameters params = MapExecuteOnKeysCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapExecuteOnKeysCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapExecuteOnKeysCodec.ResponseParameters params = MapExecuteOnKeysCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapForceUnlockCodec.RequestParameters params = MapForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MapForceUnlockCodec.encodeResponse();
            MapForceUnlockCodec.ResponseParameters params = MapForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapKeySetWithPagingPredicateCodec.RequestParameters params = MapKeySetWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapKeySetWithPagingPredicateCodec.encodeResponse(ReferenceObjects.datas);
            MapKeySetWithPagingPredicateCodec.ResponseParameters params = MapKeySetWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapValuesWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapValuesWithPagingPredicateCodec.RequestParameters params = MapValuesWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapValuesWithPagingPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapValuesWithPagingPredicateCodec.ResponseParameters params = MapValuesWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPagingPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapEntriesWithPagingPredicateCodec.RequestParameters params = MapEntriesWithPagingPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapEntriesWithPagingPredicateCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapEntriesWithPagingPredicateCodec.ResponseParameters params = MapEntriesWithPagingPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapClearNearCacheCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress);
            MapClearNearCacheCodec.RequestParameters params = MapClearNearCacheCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.target));
        }
        {
            ClientMessage clientMessage = MapClearNearCacheCodec.encodeResponse();
            MapClearNearCacheCodec.ResponseParameters params = MapClearNearCacheCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapFetchKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            MapFetchKeysCodec.RequestParameters params = MapFetchKeysCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = MapFetchKeysCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas);
            MapFetchKeysCodec.ResponseParameters params = MapFetchKeysCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = MapFetchEntriesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            MapFetchEntriesCodec.RequestParameters params = MapFetchEntriesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = MapFetchEntriesCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.aListOfEntry);
            MapFetchEntriesCodec.ResponseParameters params = MapFetchEntriesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = MapAggregateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapAggregateCodec.RequestParameters params = MapAggregateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.aggregator));
        }
        {
            ClientMessage clientMessage = MapAggregateCodec.encodeResponse(ReferenceObjects.aData);
            MapAggregateCodec.ResponseParameters params = MapAggregateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapAggregateWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            MapAggregateWithPredicateCodec.RequestParameters params = MapAggregateWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.aggregator));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapAggregateWithPredicateCodec.encodeResponse(ReferenceObjects.aData);
            MapAggregateWithPredicateCodec.ResponseParameters params = MapAggregateWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapProjectCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapProjectCodec.RequestParameters params = MapProjectCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.projection));
        }
        {
            ClientMessage clientMessage = MapProjectCodec.encodeResponse(ReferenceObjects.datas);
            MapProjectCodec.ResponseParameters params = MapProjectCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapProjectWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            MapProjectWithPredicateCodec.RequestParameters params = MapProjectWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.projection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapProjectWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            MapProjectWithPredicateCodec.ResponseParameters params = MapProjectWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MapFetchNearCacheInvalidationMetadataCodec.encodeRequest(ReferenceObjects.strings, ReferenceObjects.anAddress);
            MapFetchNearCacheInvalidationMetadataCodec.RequestParameters params = MapFetchNearCacheInvalidationMetadataCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, params.names));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = MapFetchNearCacheInvalidationMetadataCodec.encodeResponse(ReferenceObjects.aNamePartitionSequenceList, ReferenceObjects.aPartitionUuidList);
            MapFetchNearCacheInvalidationMetadataCodec.ResponseParameters params = MapFetchNearCacheInvalidationMetadataCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aNamePartitionSequenceList, params.namePartitionSequenceList));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionUuidList, params.partitionUuidList));
        }
        {
            ClientMessage clientMessage = MapAssignAndGetUuidsCodec.encodeRequest();
            MapAssignAndGetUuidsCodec.RequestParameters params = MapAssignAndGetUuidsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapAssignAndGetUuidsCodec.encodeResponse(ReferenceObjects.aPartitionUuidList);
            MapAssignAndGetUuidsCodec.ResponseParameters params = MapAssignAndGetUuidsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionUuidList, params.partitionUuidList));
        }
        {
            ClientMessage clientMessage = MapRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MapRemoveAllCodec.RequestParameters params = MapRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapRemoveAllCodec.encodeResponse();
            MapRemoveAllCodec.ResponseParameters params = MapRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MapAddNearCacheInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            MapAddNearCacheInvalidationListenerCodec.RequestParameters params = MapAddNearCacheInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.listenerFlags));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MapAddNearCacheInvalidationListenerCodec.encodeResponse(ReferenceObjects.aString);
            MapAddNearCacheInvalidationListenerCodec.ResponseParameters params = MapAddNearCacheInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MapAddNearCacheInvalidationListenerCodecHandler extends MapAddNearCacheInvalidationListenerCodec.AbstractEventHandler {
                @Override
                public void handleIMapInvalidationEventV10(com.hazelcast.nio.serialization.Data key) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                }

                @Override
                public void handleIMapInvalidationEventV14(com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleIMapBatchInvalidationEventV10(Collection<com.hazelcast.nio.serialization.Data> keys) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                }

                @Override
                public void handleIMapBatchInvalidationEventV14(Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            MapAddNearCacheInvalidationListenerCodecHandler handler = new MapAddNearCacheInvalidationListenerCodecHandler();
            {
                ClientMessage clientMessage = MapAddNearCacheInvalidationListenerCodec.encodeIMapInvalidationEvent(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = MapAddNearCacheInvalidationListenerCodec.encodeIMapBatchInvalidationEvent(ReferenceObjects.datas, ReferenceObjects.strings, ReferenceObjects.uuids, ReferenceObjects.longs);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapFetchWithQueryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aData, ReferenceObjects.aData);
            MapFetchWithQueryCodec.RequestParameters params = MapFetchWithQueryCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.projection));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = MapFetchWithQueryCodec.encodeResponse(ReferenceObjects.datas, ReferenceObjects.anInt);
            MapFetchWithQueryCodec.ResponseParameters params = MapFetchWithQueryCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.results));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.nextTableIndexToReadFrom));
        }
        {
            ClientMessage clientMessage = MapEventJournalSubscribeCodec.encodeRequest(ReferenceObjects.aString);
            MapEventJournalSubscribeCodec.RequestParameters params = MapEventJournalSubscribeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MapEventJournalSubscribeCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapEventJournalSubscribeCodec.ResponseParameters params = MapEventJournalSubscribeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.oldestSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newestSequence));
        }
        {
            ClientMessage clientMessage = MapEventJournalReadCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aData, ReferenceObjects.aData);
            MapEventJournalReadCodec.RequestParameters params = MapEventJournalReadCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.startSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.minSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.projection));
        }
        {
            ClientMessage clientMessage = MapEventJournalReadCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.arrLongs, ReferenceObjects.aLong);
            MapEventJournalReadCodec.ResponseParameters params = MapEventJournalReadCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.readCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.arrLongs, params.itemSeqs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.nextSeq));
        }
        {
            ClientMessage clientMessage = MapSetTtlCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MapSetTtlCodec.RequestParameters params = MapSetTtlCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = MapSetTtlCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapSetTtlCodec.ResponseParameters params = MapSetTtlCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapPutWithMaxIdleCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutWithMaxIdleCodec.RequestParameters params = MapPutWithMaxIdleCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.maxIdle));
        }
        {
            ClientMessage clientMessage = MapPutWithMaxIdleCodec.encodeResponse(ReferenceObjects.aData);
            MapPutWithMaxIdleCodec.ResponseParameters params = MapPutWithMaxIdleCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapPutTransientWithMaxIdleCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutTransientWithMaxIdleCodec.RequestParameters params = MapPutTransientWithMaxIdleCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.maxIdle));
        }
        {
            ClientMessage clientMessage = MapPutTransientWithMaxIdleCodec.encodeResponse(ReferenceObjects.aData);
            MapPutTransientWithMaxIdleCodec.ResponseParameters params = MapPutTransientWithMaxIdleCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentWithMaxIdleCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapPutIfAbsentWithMaxIdleCodec.RequestParameters params = MapPutIfAbsentWithMaxIdleCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.maxIdle));
        }
        {
            ClientMessage clientMessage = MapPutIfAbsentWithMaxIdleCodec.encodeResponse(ReferenceObjects.aData);
            MapPutIfAbsentWithMaxIdleCodec.ResponseParameters params = MapPutIfAbsentWithMaxIdleCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MapSetWithMaxIdleCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MapSetWithMaxIdleCodec.RequestParameters params = MapSetWithMaxIdleCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.maxIdle));
        }
        {
            ClientMessage clientMessage = MapSetWithMaxIdleCodec.encodeResponse(ReferenceObjects.aData);
            MapSetWithMaxIdleCodec.ResponseParameters params = MapSetWithMaxIdleCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapPutCodec.RequestParameters params = MultiMapPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapPutCodec.ResponseParameters params = MultiMapPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapGetCodec.RequestParameters params = MultiMapGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapGetCodec.encodeResponse(ReferenceObjects.datas);
            MultiMapGetCodec.ResponseParameters params = MultiMapGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapRemoveCodec.RequestParameters params = MultiMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveCodec.encodeResponse(ReferenceObjects.datas);
            MultiMapRemoveCodec.ResponseParameters params = MultiMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            MultiMapKeySetCodec.RequestParameters params = MultiMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            MultiMapKeySetCodec.ResponseParameters params = MultiMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapValuesCodec.encodeRequest(ReferenceObjects.aString);
            MultiMapValuesCodec.RequestParameters params = MultiMapValuesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            MultiMapValuesCodec.ResponseParameters params = MultiMapValuesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            MultiMapEntrySetCodec.RequestParameters params = MultiMapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MultiMapEntrySetCodec.ResponseParameters params = MultiMapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapContainsKeyCodec.RequestParameters params = MultiMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapContainsKeyCodec.ResponseParameters params = MultiMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MultiMapContainsValueCodec.RequestParameters params = MultiMapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = MultiMapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapContainsValueCodec.ResponseParameters params = MultiMapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapContainsEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapContainsEntryCodec.RequestParameters params = MultiMapContainsEntryCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapContainsEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapContainsEntryCodec.ResponseParameters params = MultiMapContainsEntryCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapSizeCodec.encodeRequest(ReferenceObjects.aString);
            MultiMapSizeCodec.RequestParameters params = MultiMapSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            MultiMapSizeCodec.ResponseParameters params = MultiMapSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapClearCodec.encodeRequest(ReferenceObjects.aString);
            MultiMapClearCodec.RequestParameters params = MultiMapClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = MultiMapClearCodec.encodeResponse();
            MultiMapClearCodec.ResponseParameters params = MultiMapClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MultiMapValueCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapValueCountCodec.RequestParameters params = MultiMapValueCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapValueCountCodec.encodeResponse(ReferenceObjects.anInt);
            MultiMapValueCountCodec.ResponseParameters params = MultiMapValueCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            MultiMapAddEntryListenerToKeyCodec.RequestParameters params = MultiMapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            MultiMapAddEntryListenerToKeyCodec.ResponseParameters params = MultiMapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MultiMapAddEntryListenerToKeyCodecHandler extends MultiMapAddEntryListenerToKeyCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MultiMapAddEntryListenerToKeyCodecHandler handler = new MultiMapAddEntryListenerToKeyCodecHandler();
            {
                ClientMessage clientMessage = MultiMapAddEntryListenerToKeyCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            MultiMapAddEntryListenerCodec.RequestParameters params = MultiMapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            MultiMapAddEntryListenerCodec.ResponseParameters params = MultiMapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class MultiMapAddEntryListenerCodecHandler extends MultiMapAddEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            MultiMapAddEntryListenerCodecHandler handler = new MultiMapAddEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = MultiMapAddEntryListenerCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MultiMapRemoveEntryListenerCodec.RequestParameters params = MultiMapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapRemoveEntryListenerCodec.ResponseParameters params = MultiMapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MultiMapLockCodec.RequestParameters params = MultiMapLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapLockCodec.encodeResponse();
            MultiMapLockCodec.ResponseParameters params = MultiMapLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MultiMapTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MultiMapTryLockCodec.RequestParameters params = MultiMapTryLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapTryLockCodec.ResponseParameters params = MultiMapTryLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapIsLockedCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            MultiMapIsLockedCodec.RequestParameters params = MultiMapIsLockedCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = MultiMapIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapIsLockedCodec.ResponseParameters params = MultiMapIsLockedCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            MultiMapUnlockCodec.RequestParameters params = MultiMapUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapUnlockCodec.encodeResponse();
            MultiMapUnlockCodec.ResponseParameters params = MultiMapUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MultiMapForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapForceUnlockCodec.RequestParameters params = MultiMapForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = MultiMapForceUnlockCodec.encodeResponse();
            MultiMapForceUnlockCodec.ResponseParameters params = MultiMapForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapRemoveEntryCodec.RequestParameters params = MultiMapRemoveEntryCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapRemoveEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            MultiMapRemoveEntryCodec.ResponseParameters params = MultiMapRemoveEntryCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MultiMapDeleteCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            MultiMapDeleteCodec.RequestParameters params = MultiMapDeleteCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = MultiMapDeleteCodec.encodeResponse();
            MultiMapDeleteCodec.ResponseParameters params = MultiMapDeleteCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = QueueOfferCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong);
            QueueOfferCodec.RequestParameters params = QueueOfferCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeoutMillis));
        }
        {
            ClientMessage clientMessage = QueueOfferCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueOfferCodec.ResponseParameters params = QueueOfferCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueuePutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            QueuePutCodec.RequestParameters params = QueuePutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueuePutCodec.encodeResponse();
            QueuePutCodec.ResponseParameters params = QueuePutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = QueueSizeCodec.encodeRequest(ReferenceObjects.aString);
            QueueSizeCodec.RequestParameters params = QueueSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueSizeCodec.encodeResponse(ReferenceObjects.anInt);
            QueueSizeCodec.ResponseParameters params = QueueSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = QueueRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            QueueRemoveCodec.RequestParameters params = QueueRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueueRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueRemoveCodec.ResponseParameters params = QueueRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueuePollCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            QueuePollCodec.RequestParameters params = QueuePollCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeoutMillis));
        }
        {
            ClientMessage clientMessage = QueuePollCodec.encodeResponse(ReferenceObjects.aData);
            QueuePollCodec.ResponseParameters params = QueuePollCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = QueueTakeCodec.encodeRequest(ReferenceObjects.aString);
            QueueTakeCodec.RequestParameters params = QueueTakeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueTakeCodec.encodeResponse(ReferenceObjects.aData);
            QueueTakeCodec.ResponseParameters params = QueueTakeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = QueuePeekCodec.encodeRequest(ReferenceObjects.aString);
            QueuePeekCodec.RequestParameters params = QueuePeekCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueuePeekCodec.encodeResponse(ReferenceObjects.aData);
            QueuePeekCodec.ResponseParameters params = QueuePeekCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = QueueIteratorCodec.encodeRequest(ReferenceObjects.aString);
            QueueIteratorCodec.RequestParameters params = QueueIteratorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueIteratorCodec.encodeResponse(ReferenceObjects.datas);
            QueueIteratorCodec.ResponseParameters params = QueueIteratorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueDrainToCodec.encodeRequest(ReferenceObjects.aString);
            QueueDrainToCodec.RequestParameters params = QueueDrainToCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueDrainToCodec.encodeResponse(ReferenceObjects.datas);
            QueueDrainToCodec.ResponseParameters params = QueueDrainToCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueDrainToMaxSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            QueueDrainToMaxSizeCodec.RequestParameters params = QueueDrainToMaxSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
        }
        {
            ClientMessage clientMessage = QueueDrainToMaxSizeCodec.encodeResponse(ReferenceObjects.datas);
            QueueDrainToMaxSizeCodec.ResponseParameters params = QueueDrainToMaxSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = QueueContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            QueueContainsCodec.RequestParameters params = QueueContainsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = QueueContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueContainsCodec.ResponseParameters params = QueueContainsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            QueueContainsAllCodec.RequestParameters params = QueueContainsAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueContainsAllCodec.ResponseParameters params = QueueContainsAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            QueueCompareAndRemoveAllCodec.RequestParameters params = QueueCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueCompareAndRemoveAllCodec.ResponseParameters params = QueueCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            QueueCompareAndRetainAllCodec.RequestParameters params = QueueCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueCompareAndRetainAllCodec.ResponseParameters params = QueueCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueClearCodec.encodeRequest(ReferenceObjects.aString);
            QueueClearCodec.RequestParameters params = QueueClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueClearCodec.encodeResponse();
            QueueClearCodec.ResponseParameters params = QueueClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = QueueAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            QueueAddAllCodec.RequestParameters params = QueueAddAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.dataList));
        }
        {
            ClientMessage clientMessage = QueueAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueAddAllCodec.ResponseParameters params = QueueAddAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            QueueAddListenerCodec.RequestParameters params = QueueAddListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = QueueAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            QueueAddListenerCodec.ResponseParameters params = QueueAddListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class QueueAddListenerCodecHandler extends QueueAddListenerCodec.AbstractEventHandler {
                @Override
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            QueueAddListenerCodecHandler handler = new QueueAddListenerCodecHandler();
            {
                ClientMessage clientMessage = QueueAddListenerCodec.encodeItemEvent(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = QueueRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            QueueRemoveListenerCodec.RequestParameters params = QueueRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = QueueRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueRemoveListenerCodec.ResponseParameters params = QueueRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = QueueRemainingCapacityCodec.encodeRequest(ReferenceObjects.aString);
            QueueRemainingCapacityCodec.RequestParameters params = QueueRemainingCapacityCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueRemainingCapacityCodec.encodeResponse(ReferenceObjects.anInt);
            QueueRemainingCapacityCodec.ResponseParameters params = QueueRemainingCapacityCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = QueueIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            QueueIsEmptyCodec.RequestParameters params = QueueIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = QueueIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            QueueIsEmptyCodec.ResponseParameters params = QueueIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TopicPublishCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            TopicPublishCodec.RequestParameters params = TopicPublishCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.message));
        }
        {
            ClientMessage clientMessage = TopicPublishCodec.encodeResponse();
            TopicPublishCodec.ResponseParameters params = TopicPublishCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            TopicAddMessageListenerCodec.RequestParameters params = TopicAddMessageListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeResponse(ReferenceObjects.aString);
            TopicAddMessageListenerCodec.ResponseParameters params = TopicAddMessageListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class TopicAddMessageListenerCodecHandler extends TopicAddMessageListenerCodec.AbstractEventHandler {
                @Override
                public void handleTopicEventV10(com.hazelcast.nio.serialization.Data item, long publishTime, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, publishTime));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            TopicAddMessageListenerCodecHandler handler = new TopicAddMessageListenerCodecHandler();
            {
                ClientMessage clientMessage = TopicAddMessageListenerCodec.encodeTopicEvent(ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aString);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = TopicRemoveMessageListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            TopicRemoveMessageListenerCodec.RequestParameters params = TopicRemoveMessageListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = TopicRemoveMessageListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            TopicRemoveMessageListenerCodec.ResponseParameters params = TopicRemoveMessageListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListSizeCodec.encodeRequest(ReferenceObjects.aString);
            ListSizeCodec.RequestParameters params = ListSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListSizeCodec.encodeResponse(ReferenceObjects.anInt);
            ListSizeCodec.ResponseParameters params = ListSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ListContainsCodec.RequestParameters params = ListContainsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListContainsCodec.ResponseParameters params = ListContainsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            ListContainsAllCodec.RequestParameters params = ListContainsAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListContainsAllCodec.ResponseParameters params = ListContainsAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ListAddCodec.RequestParameters params = ListAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListAddCodec.ResponseParameters params = ListAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ListRemoveCodec.RequestParameters params = ListRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListRemoveCodec.ResponseParameters params = ListRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            ListAddAllCodec.RequestParameters params = ListAddAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = ListAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListAddAllCodec.ResponseParameters params = ListAddAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            ListCompareAndRemoveAllCodec.RequestParameters params = ListCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListCompareAndRemoveAllCodec.ResponseParameters params = ListCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            ListCompareAndRetainAllCodec.RequestParameters params = ListCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = ListCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListCompareAndRetainAllCodec.ResponseParameters params = ListCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListClearCodec.encodeRequest(ReferenceObjects.aString);
            ListClearCodec.RequestParameters params = ListClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListClearCodec.encodeResponse();
            ListClearCodec.ResponseParameters params = ListClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ListGetAllCodec.encodeRequest(ReferenceObjects.aString);
            ListGetAllCodec.RequestParameters params = ListGetAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListGetAllCodec.encodeResponse(ReferenceObjects.datas);
            ListGetAllCodec.ResponseParameters params = ListGetAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            ListAddListenerCodec.RequestParameters params = ListAddListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ListAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            ListAddListenerCodec.ResponseParameters params = ListAddListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ListAddListenerCodecHandler extends ListAddListenerCodec.AbstractEventHandler {
                @Override
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            ListAddListenerCodecHandler handler = new ListAddListenerCodecHandler();
            {
                ClientMessage clientMessage = ListAddListenerCodec.encodeItemEvent(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ListRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ListRemoveListenerCodec.RequestParameters params = ListRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ListRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListRemoveListenerCodec.ResponseParameters params = ListRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            ListIsEmptyCodec.RequestParameters params = ListIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListIsEmptyCodec.ResponseParameters params = ListIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListAddAllWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas);
            ListAddAllWithIndexCodec.RequestParameters params = ListAddAllWithIndexCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = ListAddAllWithIndexCodec.encodeResponse(ReferenceObjects.aBoolean);
            ListAddAllWithIndexCodec.ResponseParameters params = ListAddAllWithIndexCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ListGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            ListGetCodec.RequestParameters params = ListGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListGetCodec.encodeResponse(ReferenceObjects.aData);
            ListGetCodec.ResponseParameters params = ListGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ListSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            ListSetCodec.RequestParameters params = ListSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListSetCodec.encodeResponse(ReferenceObjects.aData);
            ListSetCodec.ResponseParameters params = ListSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ListAddWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            ListAddWithIndexCodec.RequestParameters params = ListAddWithIndexCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListAddWithIndexCodec.encodeResponse();
            ListAddWithIndexCodec.ResponseParameters params = ListAddWithIndexCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ListRemoveWithIndexCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            ListRemoveWithIndexCodec.RequestParameters params = ListRemoveWithIndexCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListRemoveWithIndexCodec.encodeResponse(ReferenceObjects.aData);
            ListRemoveWithIndexCodec.ResponseParameters params = ListRemoveWithIndexCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ListLastIndexOfCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ListLastIndexOfCodec.RequestParameters params = ListLastIndexOfCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListLastIndexOfCodec.encodeResponse(ReferenceObjects.anInt);
            ListLastIndexOfCodec.ResponseParameters params = ListLastIndexOfCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListIndexOfCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ListIndexOfCodec.RequestParameters params = ListIndexOfCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ListIndexOfCodec.encodeResponse(ReferenceObjects.anInt);
            ListIndexOfCodec.ResponseParameters params = ListIndexOfCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ListSubCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt);
            ListSubCodec.RequestParameters params = ListSubCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.from));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.to));
        }
        {
            ClientMessage clientMessage = ListSubCodec.encodeResponse(ReferenceObjects.datas);
            ListSubCodec.ResponseParameters params = ListSubCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListIteratorCodec.encodeRequest(ReferenceObjects.aString);
            ListIteratorCodec.RequestParameters params = ListIteratorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ListIteratorCodec.encodeResponse(ReferenceObjects.datas);
            ListIteratorCodec.ResponseParameters params = ListIteratorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ListListIteratorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            ListListIteratorCodec.RequestParameters params = ListListIteratorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.index));
        }
        {
            ClientMessage clientMessage = ListListIteratorCodec.encodeResponse(ReferenceObjects.datas);
            ListListIteratorCodec.ResponseParameters params = ListListIteratorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = SetSizeCodec.encodeRequest(ReferenceObjects.aString);
            SetSizeCodec.RequestParameters params = SetSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetSizeCodec.encodeResponse(ReferenceObjects.anInt);
            SetSizeCodec.ResponseParameters params = SetSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SetContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            SetContainsCodec.RequestParameters params = SetContainsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetContainsCodec.ResponseParameters params = SetContainsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetContainsAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            SetContainsAllCodec.RequestParameters params = SetContainsAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
        }
        {
            ClientMessage clientMessage = SetContainsAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetContainsAllCodec.ResponseParameters params = SetContainsAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            SetAddCodec.RequestParameters params = SetAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetAddCodec.ResponseParameters params = SetAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            SetRemoveCodec.RequestParameters params = SetRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = SetRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetRemoveCodec.ResponseParameters params = SetRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            SetAddAllCodec.RequestParameters params = SetAddAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
        }
        {
            ClientMessage clientMessage = SetAddAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetAddAllCodec.ResponseParameters params = SetAddAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetCompareAndRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            SetCompareAndRemoveAllCodec.RequestParameters params = SetCompareAndRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = SetCompareAndRemoveAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetCompareAndRemoveAllCodec.ResponseParameters params = SetCompareAndRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetCompareAndRetainAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas);
            SetCompareAndRetainAllCodec.RequestParameters params = SetCompareAndRetainAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.values));
        }
        {
            ClientMessage clientMessage = SetCompareAndRetainAllCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetCompareAndRetainAllCodec.ResponseParameters params = SetCompareAndRetainAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetClearCodec.encodeRequest(ReferenceObjects.aString);
            SetClearCodec.RequestParameters params = SetClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetClearCodec.encodeResponse();
            SetClearCodec.ResponseParameters params = SetClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = SetGetAllCodec.encodeRequest(ReferenceObjects.aString);
            SetGetAllCodec.RequestParameters params = SetGetAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetGetAllCodec.encodeResponse(ReferenceObjects.datas);
            SetGetAllCodec.ResponseParameters params = SetGetAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = SetAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            SetAddListenerCodec.RequestParameters params = SetAddListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = SetAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            SetAddListenerCodec.ResponseParameters params = SetAddListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class SetAddListenerCodecHandler extends SetAddListenerCodec.AbstractEventHandler {
                @Override
                public void handleItemEventV10(com.hazelcast.nio.serialization.Data item, String uuid, int eventType) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, item));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                }
            }
            SetAddListenerCodecHandler handler = new SetAddListenerCodecHandler();
            {
                ClientMessage clientMessage = SetAddListenerCodec.encodeItemEvent(ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = SetRemoveListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            SetRemoveListenerCodec.RequestParameters params = SetRemoveListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = SetRemoveListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetRemoveListenerCodec.ResponseParameters params = SetRemoveListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SetIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            SetIsEmptyCodec.RequestParameters params = SetIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SetIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            SetIsEmptyCodec.ResponseParameters params = SetIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockIsLockedCodec.encodeRequest(ReferenceObjects.aString);
            LockIsLockedCodec.RequestParameters params = LockIsLockedCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockIsLockedCodec.encodeResponse(ReferenceObjects.aBoolean);
            LockIsLockedCodec.ResponseParameters params = LockIsLockedCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockIsLockedByCurrentThreadCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            LockIsLockedByCurrentThreadCodec.RequestParameters params = LockIsLockedByCurrentThreadCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = LockIsLockedByCurrentThreadCodec.encodeResponse(ReferenceObjects.aBoolean);
            LockIsLockedByCurrentThreadCodec.ResponseParameters params = LockIsLockedByCurrentThreadCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = LockGetLockCountCodec.encodeRequest(ReferenceObjects.aString);
            LockGetLockCountCodec.RequestParameters params = LockGetLockCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockGetLockCountCodec.encodeResponse(ReferenceObjects.anInt);
            LockGetLockCountCodec.ResponseParameters params = LockGetLockCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = LockGetRemainingLeaseTimeCodec.encodeRequest(ReferenceObjects.aString);
            LockGetRemainingLeaseTimeCodec.RequestParameters params = LockGetRemainingLeaseTimeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = LockGetRemainingLeaseTimeCodec.encodeResponse(ReferenceObjects.aLong);
            LockGetRemainingLeaseTimeCodec.ResponseParameters params = LockGetRemainingLeaseTimeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = LockLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            LockLockCodec.RequestParameters params = LockLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.leaseTime));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockLockCodec.encodeResponse();
            LockLockCodec.ResponseParameters params = LockLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = LockUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            LockUnlockCodec.RequestParameters params = LockUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockUnlockCodec.encodeResponse();
            LockUnlockCodec.ResponseParameters params = LockUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = LockForceUnlockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            LockForceUnlockCodec.RequestParameters params = LockForceUnlockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockForceUnlockCodec.encodeResponse();
            LockForceUnlockCodec.ResponseParameters params = LockForceUnlockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = LockTryLockCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            LockTryLockCodec.RequestParameters params = LockTryLockCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lease));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = LockTryLockCodec.encodeResponse(ReferenceObjects.aBoolean);
            LockTryLockCodec.ResponseParameters params = LockTryLockCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ConditionAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aString, ReferenceObjects.aLong);
            ConditionAwaitCodec.RequestParameters params = ConditionAwaitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = ConditionAwaitCodec.encodeResponse(ReferenceObjects.aBoolean);
            ConditionAwaitCodec.ResponseParameters params = ConditionAwaitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ConditionBeforeAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString, ReferenceObjects.aLong);
            ConditionBeforeAwaitCodec.RequestParameters params = ConditionBeforeAwaitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.referenceId));
        }
        {
            ClientMessage clientMessage = ConditionBeforeAwaitCodec.encodeResponse();
            ConditionBeforeAwaitCodec.ResponseParameters params = ConditionBeforeAwaitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ConditionSignalCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString);
            ConditionSignalCodec.RequestParameters params = ConditionSignalCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
        }
        {
            ClientMessage clientMessage = ConditionSignalCodec.encodeResponse();
            ConditionSignalCodec.ResponseParameters params = ConditionSignalCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ConditionSignalAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aString);
            ConditionSignalAllCodec.RequestParameters params = ConditionSignalAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.lockName));
        }
        {
            ClientMessage clientMessage = ConditionSignalAllCodec.encodeResponse();
            ConditionSignalAllCodec.ResponseParameters params = ConditionSignalAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ExecutorServiceShutdownCodec.encodeRequest(ReferenceObjects.aString);
            ExecutorServiceShutdownCodec.RequestParameters params = ExecutorServiceShutdownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ExecutorServiceShutdownCodec.encodeResponse();
            ExecutorServiceShutdownCodec.ResponseParameters params = ExecutorServiceShutdownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ExecutorServiceIsShutdownCodec.encodeRequest(ReferenceObjects.aString);
            ExecutorServiceIsShutdownCodec.RequestParameters params = ExecutorServiceIsShutdownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ExecutorServiceIsShutdownCodec.encodeResponse(ReferenceObjects.aBoolean);
            ExecutorServiceIsShutdownCodec.ResponseParameters params = ExecutorServiceIsShutdownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean);
            ExecutorServiceCancelOnPartitionCodec.RequestParameters params = ExecutorServiceCancelOnPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.interrupt));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnPartitionCodec.encodeResponse(ReferenceObjects.aBoolean);
            ExecutorServiceCancelOnPartitionCodec.ResponseParameters params = ExecutorServiceCancelOnPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress, ReferenceObjects.aBoolean);
            ExecutorServiceCancelOnAddressCodec.RequestParameters params = ExecutorServiceCancelOnAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.interrupt));
        }
        {
            ClientMessage clientMessage = ExecutorServiceCancelOnAddressCodec.encodeResponse(ReferenceObjects.aBoolean);
            ExecutorServiceCancelOnAddressCodec.ResponseParameters params = ExecutorServiceCancelOnAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt);
            ExecutorServiceSubmitToPartitionCodec.RequestParameters params = ExecutorServiceSubmitToPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToPartitionCodec.encodeResponse(ReferenceObjects.aData);
            ExecutorServiceSubmitToPartitionCodec.ResponseParameters params = ExecutorServiceSubmitToPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anAddress);
            ExecutorServiceSubmitToAddressCodec.RequestParameters params = ExecutorServiceSubmitToAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.uuid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ExecutorServiceSubmitToAddressCodec.encodeResponse(ReferenceObjects.aData);
            ExecutorServiceSubmitToAddressCodec.ResponseParameters params = ExecutorServiceSubmitToAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongApplyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicLongApplyCodec.RequestParameters params = AtomicLongApplyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongApplyCodec.encodeResponse(ReferenceObjects.aData);
            AtomicLongApplyCodec.ResponseParameters params = AtomicLongApplyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicLongAlterCodec.RequestParameters params = AtomicLongAlterCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterCodec.encodeResponse();
            AtomicLongAlterCodec.ResponseParameters params = AtomicLongAlterCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicLongAlterAndGetCodec.RequestParameters params = AtomicLongAlterAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongAlterAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongAlterAndGetCodec.ResponseParameters params = AtomicLongAlterAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicLongGetAndAlterCodec.RequestParameters params = AtomicLongGetAndAlterCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAlterCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongGetAndAlterCodec.ResponseParameters params = AtomicLongGetAndAlterCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongAddAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            AtomicLongAddAndGetCodec.RequestParameters params = AtomicLongAddAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delta));
        }
        {
            ClientMessage clientMessage = AtomicLongAddAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongAddAndGetCodec.ResponseParameters params = AtomicLongAddAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongCompareAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            AtomicLongCompareAndSetCodec.RequestParameters params = AtomicLongCompareAndSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.expected));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.updated));
        }
        {
            ClientMessage clientMessage = AtomicLongCompareAndSetCodec.encodeResponse(ReferenceObjects.aBoolean);
            AtomicLongCompareAndSetCodec.ResponseParameters params = AtomicLongCompareAndSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongDecrementAndGetCodec.encodeRequest(ReferenceObjects.aString);
            AtomicLongDecrementAndGetCodec.RequestParameters params = AtomicLongDecrementAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongDecrementAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongDecrementAndGetCodec.ResponseParameters params = AtomicLongDecrementAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetCodec.encodeRequest(ReferenceObjects.aString);
            AtomicLongGetCodec.RequestParameters params = AtomicLongGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongGetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongGetCodec.ResponseParameters params = AtomicLongGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            AtomicLongGetAndAddCodec.RequestParameters params = AtomicLongGetAndAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delta));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndAddCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongGetAndAddCodec.ResponseParameters params = AtomicLongGetAndAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            AtomicLongGetAndSetCodec.RequestParameters params = AtomicLongGetAndSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndSetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongGetAndSetCodec.ResponseParameters params = AtomicLongGetAndSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongIncrementAndGetCodec.encodeRequest(ReferenceObjects.aString);
            AtomicLongIncrementAndGetCodec.RequestParameters params = AtomicLongIncrementAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongIncrementAndGetCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongIncrementAndGetCodec.ResponseParameters params = AtomicLongIncrementAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndIncrementCodec.encodeRequest(ReferenceObjects.aString);
            AtomicLongGetAndIncrementCodec.RequestParameters params = AtomicLongGetAndIncrementCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicLongGetAndIncrementCodec.encodeResponse(ReferenceObjects.aLong);
            AtomicLongGetAndIncrementCodec.ResponseParameters params = AtomicLongGetAndIncrementCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = AtomicLongSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            AtomicLongSetCodec.RequestParameters params = AtomicLongSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicLongSetCodec.encodeResponse();
            AtomicLongSetCodec.ResponseParameters params = AtomicLongSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceApplyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceApplyCodec.RequestParameters params = AtomicReferenceApplyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceApplyCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceApplyCodec.ResponseParameters params = AtomicReferenceApplyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceAlterCodec.RequestParameters params = AtomicReferenceAlterCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterCodec.encodeResponse();
            AtomicReferenceAlterCodec.ResponseParameters params = AtomicReferenceAlterCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceAlterAndGetCodec.RequestParameters params = AtomicReferenceAlterAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceAlterAndGetCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceAlterAndGetCodec.ResponseParameters params = AtomicReferenceAlterAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndAlterCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceGetAndAlterCodec.RequestParameters params = AtomicReferenceGetAndAlterCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.function));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndAlterCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceGetAndAlterCodec.ResponseParameters params = AtomicReferenceGetAndAlterCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceContainsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceContainsCodec.RequestParameters params = AtomicReferenceContainsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expected));
        }
        {
            ClientMessage clientMessage = AtomicReferenceContainsCodec.encodeResponse(ReferenceObjects.aBoolean);
            AtomicReferenceContainsCodec.ResponseParameters params = AtomicReferenceContainsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceCompareAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            AtomicReferenceCompareAndSetCodec.RequestParameters params = AtomicReferenceCompareAndSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expected));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.updated));
        }
        {
            ClientMessage clientMessage = AtomicReferenceCompareAndSetCodec.encodeResponse(ReferenceObjects.aBoolean);
            AtomicReferenceCompareAndSetCodec.ResponseParameters params = AtomicReferenceCompareAndSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetCodec.encodeRequest(ReferenceObjects.aString);
            AtomicReferenceGetCodec.RequestParameters params = AtomicReferenceGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceGetCodec.ResponseParameters params = AtomicReferenceGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceSetCodec.RequestParameters params = AtomicReferenceSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetCodec.encodeResponse();
            AtomicReferenceSetCodec.ResponseParameters params = AtomicReferenceSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceClearCodec.encodeRequest(ReferenceObjects.aString);
            AtomicReferenceClearCodec.RequestParameters params = AtomicReferenceClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceClearCodec.encodeResponse();
            AtomicReferenceClearCodec.ResponseParameters params = AtomicReferenceClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceGetAndSetCodec.RequestParameters params = AtomicReferenceGetAndSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceGetAndSetCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceGetAndSetCodec.ResponseParameters params = AtomicReferenceGetAndSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetAndGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            AtomicReferenceSetAndGetCodec.RequestParameters params = AtomicReferenceSetAndGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
        }
        {
            ClientMessage clientMessage = AtomicReferenceSetAndGetCodec.encodeResponse(ReferenceObjects.aData);
            AtomicReferenceSetAndGetCodec.ResponseParameters params = AtomicReferenceSetAndGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = AtomicReferenceIsNullCodec.encodeRequest(ReferenceObjects.aString);
            AtomicReferenceIsNullCodec.RequestParameters params = AtomicReferenceIsNullCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = AtomicReferenceIsNullCodec.encodeResponse(ReferenceObjects.aBoolean);
            AtomicReferenceIsNullCodec.ResponseParameters params = AtomicReferenceIsNullCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchAwaitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            CountDownLatchAwaitCodec.RequestParameters params = CountDownLatchAwaitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = CountDownLatchAwaitCodec.encodeResponse(ReferenceObjects.aBoolean);
            CountDownLatchAwaitCodec.ResponseParameters params = CountDownLatchAwaitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchCountDownCodec.encodeRequest(ReferenceObjects.aString);
            CountDownLatchCountDownCodec.RequestParameters params = CountDownLatchCountDownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CountDownLatchCountDownCodec.encodeResponse();
            CountDownLatchCountDownCodec.ResponseParameters params = CountDownLatchCountDownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CountDownLatchGetCountCodec.encodeRequest(ReferenceObjects.aString);
            CountDownLatchGetCountCodec.RequestParameters params = CountDownLatchGetCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CountDownLatchGetCountCodec.encodeResponse(ReferenceObjects.anInt);
            CountDownLatchGetCountCodec.ResponseParameters params = CountDownLatchGetCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CountDownLatchTrySetCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            CountDownLatchTrySetCountCodec.RequestParameters params = CountDownLatchTrySetCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.count));
        }
        {
            ClientMessage clientMessage = CountDownLatchTrySetCountCodec.encodeResponse(ReferenceObjects.aBoolean);
            CountDownLatchTrySetCountCodec.ResponseParameters params = CountDownLatchTrySetCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreInitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            SemaphoreInitCodec.RequestParameters params = SemaphoreInitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreInitCodec.encodeResponse(ReferenceObjects.aBoolean);
            SemaphoreInitCodec.ResponseParameters params = SemaphoreInitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreAcquireCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            SemaphoreAcquireCodec.RequestParameters params = SemaphoreAcquireCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreAcquireCodec.encodeResponse();
            SemaphoreAcquireCodec.ResponseParameters params = SemaphoreAcquireCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreAvailablePermitsCodec.encodeRequest(ReferenceObjects.aString);
            SemaphoreAvailablePermitsCodec.RequestParameters params = SemaphoreAvailablePermitsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SemaphoreAvailablePermitsCodec.encodeResponse(ReferenceObjects.anInt);
            SemaphoreAvailablePermitsCodec.ResponseParameters params = SemaphoreAvailablePermitsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreDrainPermitsCodec.encodeRequest(ReferenceObjects.aString);
            SemaphoreDrainPermitsCodec.RequestParameters params = SemaphoreDrainPermitsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = SemaphoreDrainPermitsCodec.encodeResponse(ReferenceObjects.anInt);
            SemaphoreDrainPermitsCodec.ResponseParameters params = SemaphoreDrainPermitsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreReducePermitsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            SemaphoreReducePermitsCodec.RequestParameters params = SemaphoreReducePermitsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.reduction));
        }
        {
            ClientMessage clientMessage = SemaphoreReducePermitsCodec.encodeResponse();
            SemaphoreReducePermitsCodec.ResponseParameters params = SemaphoreReducePermitsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreReleaseCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            SemaphoreReleaseCodec.RequestParameters params = SemaphoreReleaseCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
        }
        {
            ClientMessage clientMessage = SemaphoreReleaseCodec.encodeResponse();
            SemaphoreReleaseCodec.ResponseParameters params = SemaphoreReleaseCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = SemaphoreTryAcquireCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aLong);
            SemaphoreTryAcquireCodec.RequestParameters params = SemaphoreTryAcquireCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.permits));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = SemaphoreTryAcquireCodec.encodeResponse(ReferenceObjects.aBoolean);
            SemaphoreTryAcquireCodec.ResponseParameters params = SemaphoreTryAcquireCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = SemaphoreIncreasePermitsCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            SemaphoreIncreasePermitsCodec.RequestParameters params = SemaphoreIncreasePermitsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.increase));
        }
        {
            ClientMessage clientMessage = SemaphoreIncreasePermitsCodec.encodeResponse();
            SemaphoreIncreasePermitsCodec.ResponseParameters params = SemaphoreIncreasePermitsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            ReplicatedMapPutCodec.RequestParameters params = ReplicatedMapPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutCodec.encodeResponse(ReferenceObjects.aData);
            ReplicatedMapPutCodec.ResponseParameters params = ReplicatedMapPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapSizeCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapSizeCodec.RequestParameters params = ReplicatedMapSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            ReplicatedMapSizeCodec.ResponseParameters params = ReplicatedMapSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapIsEmptyCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapIsEmptyCodec.RequestParameters params = ReplicatedMapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            ReplicatedMapIsEmptyCodec.ResponseParameters params = ReplicatedMapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ReplicatedMapContainsKeyCodec.RequestParameters params = ReplicatedMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            ReplicatedMapContainsKeyCodec.ResponseParameters params = ReplicatedMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ReplicatedMapContainsValueCodec.RequestParameters params = ReplicatedMapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = ReplicatedMapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            ReplicatedMapContainsValueCodec.ResponseParameters params = ReplicatedMapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ReplicatedMapGetCodec.RequestParameters params = ReplicatedMapGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapGetCodec.encodeResponse(ReferenceObjects.aData);
            ReplicatedMapGetCodec.ResponseParameters params = ReplicatedMapGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            ReplicatedMapRemoveCodec.RequestParameters params = ReplicatedMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveCodec.encodeResponse(ReferenceObjects.aData);
            ReplicatedMapRemoveCodec.ResponseParameters params = ReplicatedMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry);
            ReplicatedMapPutAllCodec.RequestParameters params = ReplicatedMapPutAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = ReplicatedMapPutAllCodec.encodeResponse();
            ReplicatedMapPutAllCodec.ResponseParameters params = ReplicatedMapPutAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ReplicatedMapClearCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapClearCodec.RequestParameters params = ReplicatedMapClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapClearCodec.encodeResponse();
            ReplicatedMapClearCodec.ResponseParameters params = ReplicatedMapClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters params = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.ResponseParameters params = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyWithPredicateCodecHandler();
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters params = ReplicatedMapAddEntryListenerWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeResponse(ReferenceObjects.aString);
            ReplicatedMapAddEntryListenerWithPredicateCodec.ResponseParameters params = ReplicatedMapAddEntryListenerWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerWithPredicateCodecHandler extends ReplicatedMapAddEntryListenerWithPredicateCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerWithPredicateCodecHandler handler = new ReplicatedMapAddEntryListenerWithPredicateCodecHandler();
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerWithPredicateCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean);
            ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters params = ReplicatedMapAddEntryListenerToKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeResponse(ReferenceObjects.aString);
            ReplicatedMapAddEntryListenerToKeyCodec.ResponseParameters params = ReplicatedMapAddEntryListenerToKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerToKeyCodecHandler extends ReplicatedMapAddEntryListenerToKeyCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerToKeyCodecHandler handler = new ReplicatedMapAddEntryListenerToKeyCodecHandler();
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerToKeyCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            ReplicatedMapAddEntryListenerCodec.RequestParameters params = ReplicatedMapAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            ReplicatedMapAddEntryListenerCodec.ResponseParameters params = ReplicatedMapAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddEntryListenerCodecHandler extends ReplicatedMapAddEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddEntryListenerCodecHandler handler = new ReplicatedMapAddEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = ReplicatedMapAddEntryListenerCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ReplicatedMapRemoveEntryListenerCodec.RequestParameters params = ReplicatedMapRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = ReplicatedMapRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            ReplicatedMapRemoveEntryListenerCodec.ResponseParameters params = ReplicatedMapRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapKeySetCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapKeySetCodec.RequestParameters params = ReplicatedMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            ReplicatedMapKeySetCodec.ResponseParameters params = ReplicatedMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapValuesCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapValuesCodec.RequestParameters params = ReplicatedMapValuesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            ReplicatedMapValuesCodec.ResponseParameters params = ReplicatedMapValuesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapEntrySetCodec.encodeRequest(ReferenceObjects.aString);
            ReplicatedMapEntrySetCodec.RequestParameters params = ReplicatedMapEntrySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = ReplicatedMapEntrySetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            ReplicatedMapEntrySetCodec.ResponseParameters params = ReplicatedMapEntrySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters params = ReplicatedMapAddNearCacheEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.includeValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            ReplicatedMapAddNearCacheEntryListenerCodec.ResponseParameters params = ReplicatedMapAddNearCacheEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ReplicatedMapAddNearCacheEntryListenerCodecHandler extends ReplicatedMapAddNearCacheEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleEntryEventV10(com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, com.hazelcast.nio.serialization.Data oldValue, com.hazelcast.nio.serialization.Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, value));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, oldValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, mergingValue));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, eventType));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, numberOfAffectedEntries));
                }
            }
            ReplicatedMapAddNearCacheEntryListenerCodecHandler handler = new ReplicatedMapAddNearCacheEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = ReplicatedMapAddNearCacheEntryListenerCodec.encodeEntryEvent(ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = MapReduceCancelCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MapReduceCancelCodec.RequestParameters params = MapReduceCancelCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
        }
        {
            ClientMessage clientMessage = MapReduceCancelCodec.encodeResponse(ReferenceObjects.aBoolean);
            MapReduceCancelCodec.ResponseParameters params = MapReduceCancelCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceJobProcessInformationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            MapReduceJobProcessInformationCodec.RequestParameters params = MapReduceJobProcessInformationCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
        }
        {
            ClientMessage clientMessage = MapReduceJobProcessInformationCodec.encodeResponse(ReferenceObjects.jobPartitionStates, ReferenceObjects.anInt);
            MapReduceJobProcessInformationCodec.ResponseParameters params = MapReduceJobProcessInformationCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.jobPartitionStates, params.jobPartitionStates));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.processRecords));
        }
        {
            ClientMessage clientMessage = MapReduceForMapCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.aString);
            MapReduceForMapCodec.RequestParameters params = MapReduceForMapCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForMapCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapReduceForMapCodec.ResponseParameters params = MapReduceForMapCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForListCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.aString);
            MapReduceForListCodec.RequestParameters params = MapReduceForListCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.listName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForListCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapReduceForListCodec.ResponseParameters params = MapReduceForListCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.aString);
            MapReduceForSetCodec.RequestParameters params = MapReduceForSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.setName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForSetCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapReduceForSetCodec.ResponseParameters params = MapReduceForSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForMultiMapCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.aString);
            MapReduceForMultiMapCodec.RequestParameters params = MapReduceForMultiMapCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.multiMapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForMultiMapCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapReduceForMultiMapCodec.ResponseParameters params = MapReduceForMultiMapCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = MapReduceForCustomCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.aString);
            MapReduceForCustomCodec.RequestParameters params = MapReduceForCustomCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.jobId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapper));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.combinerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.reducerFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.keyValueSource));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.chunkSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topologyChangedStrategy));
        }
        {
            ClientMessage clientMessage = MapReduceForCustomCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            MapReduceForCustomCodec.ResponseParameters params = MapReduceForCustomCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapContainsKeyCodec.RequestParameters params = TransactionalMapContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMapContainsKeyCodec.ResponseParameters params = TransactionalMapContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapGetCodec.RequestParameters params = TransactionalMapGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapGetCodec.ResponseParameters params = TransactionalMapGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetForUpdateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapGetForUpdateCodec.RequestParameters params = TransactionalMapGetForUpdateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapGetForUpdateCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapGetForUpdateCodec.ResponseParameters params = TransactionalMapGetForUpdateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalMapSizeCodec.RequestParameters params = TransactionalMapSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalMapSizeCodec.ResponseParameters params = TransactionalMapSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapIsEmptyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalMapIsEmptyCodec.RequestParameters params = TransactionalMapIsEmptyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapIsEmptyCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMapIsEmptyCodec.ResponseParameters params = TransactionalMapIsEmptyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aLong);
            TransactionalMapPutCodec.RequestParameters params = TransactionalMapPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.ttl));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapPutCodec.ResponseParameters params = TransactionalMapPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapSetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMapSetCodec.RequestParameters params = TransactionalMapSetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapSetCodec.encodeResponse();
            TransactionalMapSetCodec.ResponseParameters params = TransactionalMapSetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMapPutIfAbsentCodec.RequestParameters params = TransactionalMapPutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapPutIfAbsentCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapPutIfAbsentCodec.ResponseParameters params = TransactionalMapPutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMapReplaceCodec.RequestParameters params = TransactionalMapReplaceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapReplaceCodec.ResponseParameters params = TransactionalMapReplaceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMapReplaceIfSameCodec.RequestParameters params = TransactionalMapReplaceIfSameCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.oldValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
        }
        {
            ClientMessage clientMessage = TransactionalMapReplaceIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMapReplaceIfSameCodec.ResponseParameters params = TransactionalMapReplaceIfSameCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapRemoveCodec.RequestParameters params = TransactionalMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalMapRemoveCodec.ResponseParameters params = TransactionalMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapDeleteCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapDeleteCodec.RequestParameters params = TransactionalMapDeleteCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMapDeleteCodec.encodeResponse();
            TransactionalMapDeleteCodec.ResponseParameters params = TransactionalMapDeleteCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveIfSameCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMapRemoveIfSameCodec.RequestParameters params = TransactionalMapRemoveIfSameCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapRemoveIfSameCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMapRemoveIfSameCodec.ResponseParameters params = TransactionalMapRemoveIfSameCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalMapKeySetCodec.RequestParameters params = TransactionalMapKeySetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMapKeySetCodec.ResponseParameters params = TransactionalMapKeySetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapKeySetWithPredicateCodec.RequestParameters params = TransactionalMapKeySetWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = TransactionalMapKeySetWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMapKeySetWithPredicateCodec.ResponseParameters params = TransactionalMapKeySetWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalMapValuesCodec.RequestParameters params = TransactionalMapValuesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMapValuesCodec.ResponseParameters params = TransactionalMapValuesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesWithPredicateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapValuesWithPredicateCodec.RequestParameters params = TransactionalMapValuesWithPredicateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
        }
        {
            ClientMessage clientMessage = TransactionalMapValuesWithPredicateCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMapValuesWithPredicateCodec.ResponseParameters params = TransactionalMapValuesWithPredicateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMapContainsValueCodec.RequestParameters params = TransactionalMapContainsValueCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMapContainsValueCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMapContainsValueCodec.ResponseParameters params = TransactionalMapContainsValueCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapPutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMultiMapPutCodec.RequestParameters params = TransactionalMultiMapPutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapPutCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMultiMapPutCodec.ResponseParameters params = TransactionalMultiMapPutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMultiMapGetCodec.RequestParameters params = TransactionalMultiMapGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapGetCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMultiMapGetCodec.ResponseParameters params = TransactionalMultiMapGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMultiMapRemoveCodec.RequestParameters params = TransactionalMultiMapRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveCodec.encodeResponse(ReferenceObjects.datas);
            TransactionalMultiMapRemoveCodec.ResponseParameters params = TransactionalMultiMapRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveEntryCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aData);
            TransactionalMultiMapRemoveEntryCodec.RequestParameters params = TransactionalMultiMapRemoveEntryCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapRemoveEntryCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalMultiMapRemoveEntryCodec.ResponseParameters params = TransactionalMultiMapRemoveEntryCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapValueCountCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalMultiMapValueCountCodec.RequestParameters params = TransactionalMultiMapValueCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapValueCountCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalMultiMapValueCountCodec.ResponseParameters params = TransactionalMultiMapValueCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalMultiMapSizeCodec.RequestParameters params = TransactionalMultiMapSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalMultiMapSizeCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalMultiMapSizeCodec.ResponseParameters params = TransactionalMultiMapSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalSetAddCodec.RequestParameters params = TransactionalSetAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalSetAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalSetAddCodec.ResponseParameters params = TransactionalSetAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalSetRemoveCodec.RequestParameters params = TransactionalSetRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalSetRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalSetRemoveCodec.ResponseParameters params = TransactionalSetRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalSetSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalSetSizeCodec.RequestParameters params = TransactionalSetSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalSetSizeCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalSetSizeCodec.ResponseParameters params = TransactionalSetSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalListAddCodec.RequestParameters params = TransactionalListAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalListAddCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalListAddCodec.ResponseParameters params = TransactionalListAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData);
            TransactionalListRemoveCodec.RequestParameters params = TransactionalListRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
        }
        {
            ClientMessage clientMessage = TransactionalListRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalListRemoveCodec.ResponseParameters params = TransactionalListRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalListSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalListSizeCodec.RequestParameters params = TransactionalListSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalListSizeCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalListSizeCodec.ResponseParameters params = TransactionalListSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueOfferCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aData, ReferenceObjects.aLong);
            TransactionalQueueOfferCodec.RequestParameters params = TransactionalQueueOfferCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.item));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueueOfferCodec.encodeResponse(ReferenceObjects.aBoolean);
            TransactionalQueueOfferCodec.ResponseParameters params = TransactionalQueueOfferCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueTakeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalQueueTakeCodec.RequestParameters params = TransactionalQueueTakeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalQueueTakeCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalQueueTakeCodec.ResponseParameters params = TransactionalQueueTakeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePollCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            TransactionalQueuePollCodec.RequestParameters params = TransactionalQueuePollCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePollCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalQueuePollCodec.ResponseParameters params = TransactionalQueuePollCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePeekCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aLong);
            TransactionalQueuePeekCodec.RequestParameters params = TransactionalQueuePeekCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = TransactionalQueuePeekCodec.encodeResponse(ReferenceObjects.aData);
            TransactionalQueuePeekCodec.ResponseParameters params = TransactionalQueuePeekCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = TransactionalQueueSizeCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionalQueueSizeCodec.RequestParameters params = TransactionalQueueSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.txnId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionalQueueSizeCodec.encodeResponse(ReferenceObjects.anInt);
            TransactionalQueueSizeCodec.ResponseParameters params = TransactionalQueueSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            CacheAddEntryListenerCodec.RequestParameters params = CacheAddEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeResponse(ReferenceObjects.aString);
            CacheAddEntryListenerCodec.ResponseParameters params = CacheAddEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddEntryListenerCodecHandler extends CacheAddEntryListenerCodec.AbstractEventHandler {
                @Override
                public void handleCacheEventV10(int type, Collection<com.hazelcast.cache.impl.CacheEventData> keys, int completionId) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, type));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.cacheEventDatas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, completionId));
                }
            }
            CacheAddEntryListenerCodecHandler handler = new CacheAddEntryListenerCodecHandler();
            {
                ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeCacheEvent(ReferenceObjects.anInt, ReferenceObjects.cacheEventDatas, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            CacheAddInvalidationListenerCodec.RequestParameters params = CacheAddInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeResponse(ReferenceObjects.aString);
            CacheAddInvalidationListenerCodec.ResponseParameters params = CacheAddInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddInvalidationListenerCodecHandler extends CacheAddInvalidationListenerCodec.AbstractEventHandler {
                @Override
                public void handleCacheInvalidationEventV10(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                }

                @Override
                public void handleCacheInvalidationEventV14(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleCacheBatchInvalidationEventV10(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                }

                @Override
                public void handleCacheBatchInvalidationEventV14(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            CacheAddInvalidationListenerCodecHandler handler = new CacheAddInvalidationListenerCodecHandler();
            {
                ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeCacheInvalidationEvent(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = CacheAddInvalidationListenerCodec.encodeCacheBatchInvalidationEvent(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.strings, ReferenceObjects.uuids, ReferenceObjects.longs);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheClearCodec.encodeRequest(ReferenceObjects.aString);
            CacheClearCodec.RequestParameters params = CacheClearCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheClearCodec.encodeResponse();
            CacheClearCodec.ResponseParameters params = CacheClearCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllKeysCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.anInt);
            CacheRemoveAllKeysCodec.RequestParameters params = CacheRemoveAllKeysCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllKeysCodec.encodeResponse();
            CacheRemoveAllKeysCodec.ResponseParameters params = CacheRemoveAllKeysCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            CacheRemoveAllCodec.RequestParameters params = CacheRemoveAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveAllCodec.encodeResponse();
            CacheRemoveAllCodec.ResponseParameters params = CacheRemoveAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheContainsKeyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            CacheContainsKeyCodec.RequestParameters params = CacheContainsKeyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
        }
        {
            ClientMessage clientMessage = CacheContainsKeyCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheContainsKeyCodec.ResponseParameters params = CacheContainsKeyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheCreateConfigCodec.encodeRequest(ReferenceObjects.aData, ReferenceObjects.aBoolean);
            CacheCreateConfigCodec.RequestParameters params = CacheCreateConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.cacheConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.createAlsoOnOthers));
        }
        {
            ClientMessage clientMessage = CacheCreateConfigCodec.encodeResponse(ReferenceObjects.aData);
            CacheCreateConfigCodec.ResponseParameters params = CacheCreateConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheDestroyCodec.encodeRequest(ReferenceObjects.aString);
            CacheDestroyCodec.RequestParameters params = CacheDestroyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheDestroyCodec.encodeResponse();
            CacheDestroyCodec.ResponseParameters params = CacheDestroyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheEntryProcessorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.datas, ReferenceObjects.anInt);
            CacheEntryProcessorCodec.RequestParameters params = CacheEntryProcessorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.entryProcessor));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.arguments));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheEntryProcessorCodec.encodeResponse(ReferenceObjects.aData);
            CacheEntryProcessorCodec.ResponseParameters params = CacheEntryProcessorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aData);
            CacheGetAllCodec.RequestParameters params = CacheGetAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
        }
        {
            ClientMessage clientMessage = CacheGetAllCodec.encodeResponse(ReferenceObjects.aListOfEntry);
            CacheGetAllCodec.ResponseParameters params = CacheGetAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAndRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt);
            CacheGetAndRemoveCodec.RequestParameters params = CacheGetAndRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheGetAndRemoveCodec.encodeResponse(ReferenceObjects.aData);
            CacheGetAndRemoveCodec.ResponseParameters params = CacheGetAndRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetAndReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt);
            CacheGetAndReplaceCodec.RequestParameters params = CacheGetAndReplaceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheGetAndReplaceCodec.encodeResponse(ReferenceObjects.aData);
            CacheGetAndReplaceCodec.ResponseParameters params = CacheGetAndReplaceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            CacheGetConfigCodec.RequestParameters params = CacheGetConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.simpleName));
        }
        {
            ClientMessage clientMessage = CacheGetConfigCodec.encodeResponse(ReferenceObjects.aData);
            CacheGetConfigCodec.ResponseParameters params = CacheGetConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData);
            CacheGetCodec.RequestParameters params = CacheGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
        }
        {
            ClientMessage clientMessage = CacheGetCodec.encodeResponse(ReferenceObjects.aData);
            CacheGetCodec.ResponseParameters params = CacheGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheIterateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            CacheIterateCodec.RequestParameters params = CacheIterateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = CacheIterateCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas);
            CacheIterateCodec.ResponseParameters params = CacheIterateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
        }
        {
            ClientMessage clientMessage = CacheListenerRegistrationCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anAddress);
            CacheListenerRegistrationCodec.RequestParameters params = CacheListenerRegistrationCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.listenerConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.shouldRegister));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = CacheListenerRegistrationCodec.encodeResponse();
            CacheListenerRegistrationCodec.ResponseParameters params = CacheListenerRegistrationCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheLoadAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aBoolean);
            CacheLoadAllCodec.RequestParameters params = CacheLoadAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.replaceExistingValues));
        }
        {
            ClientMessage clientMessage = CacheLoadAllCodec.encodeResponse();
            CacheLoadAllCodec.ResponseParameters params = CacheLoadAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheManagementConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.anAddress);
            CacheManagementConfigCodec.RequestParameters params = CacheManagementConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isStat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.enabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = CacheManagementConfigCodec.encodeResponse();
            CacheManagementConfigCodec.ResponseParameters params = CacheManagementConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CachePutIfAbsentCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt);
            CachePutIfAbsentCodec.RequestParameters params = CachePutIfAbsentCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutIfAbsentCodec.encodeResponse(ReferenceObjects.aBoolean);
            CachePutIfAbsentCodec.ResponseParameters params = CachePutIfAbsentCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CachePutCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aBoolean, ReferenceObjects.anInt);
            CachePutCodec.RequestParameters params = CachePutCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.get));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutCodec.encodeResponse(ReferenceObjects.aData);
            CachePutCodec.ResponseParameters params = CachePutCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveEntryListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            CacheRemoveEntryListenerCodec.RequestParameters params = CacheRemoveEntryListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemoveEntryListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheRemoveEntryListenerCodec.ResponseParameters params = CacheRemoveEntryListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            CacheRemoveInvalidationListenerCodec.RequestParameters params = CacheRemoveInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemoveInvalidationListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheRemoveInvalidationListenerCodec.ResponseParameters params = CacheRemoveInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheRemoveCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt);
            CacheRemoveCodec.RequestParameters params = CacheRemoveCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.currentValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheRemoveCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheRemoveCodec.ResponseParameters params = CacheRemoveCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CacheReplaceCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.aData, ReferenceObjects.anInt);
            CacheReplaceCodec.RequestParameters params = CacheReplaceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.key));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.oldValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.newValue));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CacheReplaceCodec.encodeResponse(ReferenceObjects.aData);
            CacheReplaceCodec.ResponseParameters params = CacheReplaceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CacheSizeCodec.encodeRequest(ReferenceObjects.aString);
            CacheSizeCodec.RequestParameters params = CacheSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheSizeCodec.encodeResponse(ReferenceObjects.anInt);
            CacheSizeCodec.ResponseParameters params = CacheSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            CacheAddPartitionLostListenerCodec.RequestParameters params = CacheAddPartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeResponse(ReferenceObjects.aString);
            CacheAddPartitionLostListenerCodec.ResponseParameters params = CacheAddPartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddPartitionLostListenerCodecHandler extends CacheAddPartitionLostListenerCodec.AbstractEventHandler {
                @Override
                public void handleCachePartitionLostEventV10(int partitionId, String uuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, uuid));
                }
            }
            CacheAddPartitionLostListenerCodecHandler handler = new CacheAddPartitionLostListenerCodecHandler();
            {
                ClientMessage clientMessage = CacheAddPartitionLostListenerCodec.encodeCachePartitionLostEvent(ReferenceObjects.anInt, ReferenceObjects.aString);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheRemovePartitionLostListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            CacheRemovePartitionLostListenerCodec.RequestParameters params = CacheRemovePartitionLostListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.registrationId));
        }
        {
            ClientMessage clientMessage = CacheRemovePartitionLostListenerCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheRemovePartitionLostListenerCodec.ResponseParameters params = CacheRemovePartitionLostListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = CachePutAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfEntry, ReferenceObjects.aData, ReferenceObjects.anInt);
            CachePutAllCodec.RequestParameters params = CachePutAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.completionId));
        }
        {
            ClientMessage clientMessage = CachePutAllCodec.encodeResponse();
            CachePutAllCodec.ResponseParameters params = CachePutAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheIterateEntriesCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt);
            CacheIterateEntriesCodec.RequestParameters params = CacheIterateEntriesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.partitionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batch));
        }
        {
            ClientMessage clientMessage = CacheIterateEntriesCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.aListOfEntry);
            CacheIterateEntriesCodec.ResponseParameters params = CacheIterateEntriesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.tableIndex));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.entries));
        }
        {
            ClientMessage clientMessage = CacheAddNearCacheInvalidationListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            CacheAddNearCacheInvalidationListenerCodec.RequestParameters params = CacheAddNearCacheInvalidationListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = CacheAddNearCacheInvalidationListenerCodec.encodeResponse(ReferenceObjects.aString);
            CacheAddNearCacheInvalidationListenerCodec.ResponseParameters params = CacheAddNearCacheInvalidationListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class CacheAddNearCacheInvalidationListenerCodecHandler extends CacheAddNearCacheInvalidationListenerCodec.AbstractEventHandler {
                @Override
                public void handleCacheInvalidationEventV10(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                }

                @Override
                public void handleCacheInvalidationEventV14(String name, com.hazelcast.nio.serialization.Data key, String sourceUuid, UUID partitionUuid, long sequence) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, key));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, sourceUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aUUID, partitionUuid));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, sequence));
                }

                @Override
                public void handleCacheBatchInvalidationEventV10(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                }

                @Override
                public void handleCacheBatchInvalidationEventV14(String name, Collection<com.hazelcast.nio.serialization.Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, name));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, keys));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, sourceUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.uuids, partitionUuids));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.longs, sequences));
                }
            }
            CacheAddNearCacheInvalidationListenerCodecHandler handler = new CacheAddNearCacheInvalidationListenerCodecHandler();
            {
                ClientMessage clientMessage = CacheAddNearCacheInvalidationListenerCodec.encodeCacheInvalidationEvent(ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.aUUID, ReferenceObjects.aLong);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = CacheAddNearCacheInvalidationListenerCodec.encodeCacheBatchInvalidationEvent(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.strings, ReferenceObjects.uuids, ReferenceObjects.longs);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = CacheFetchNearCacheInvalidationMetadataCodec.encodeRequest(ReferenceObjects.strings, ReferenceObjects.anAddress);
            CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters params = CacheFetchNearCacheInvalidationMetadataCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.strings, params.names));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = CacheFetchNearCacheInvalidationMetadataCodec.encodeResponse(ReferenceObjects.aNamePartitionSequenceList, ReferenceObjects.aPartitionUuidList);
            CacheFetchNearCacheInvalidationMetadataCodec.ResponseParameters params = CacheFetchNearCacheInvalidationMetadataCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aNamePartitionSequenceList, params.namePartitionSequenceList));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionUuidList, params.partitionUuidList));
        }
        {
            ClientMessage clientMessage = CacheAssignAndGetUuidsCodec.encodeRequest();
            CacheAssignAndGetUuidsCodec.RequestParameters params = CacheAssignAndGetUuidsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CacheAssignAndGetUuidsCodec.encodeResponse(ReferenceObjects.aPartitionUuidList);
            CacheAssignAndGetUuidsCodec.ResponseParameters params = CacheAssignAndGetUuidsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aPartitionUuidList, params.partitionUuidList));
        }
        {
            ClientMessage clientMessage = CacheEventJournalSubscribeCodec.encodeRequest(ReferenceObjects.aString);
            CacheEventJournalSubscribeCodec.RequestParameters params = CacheEventJournalSubscribeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CacheEventJournalSubscribeCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aLong);
            CacheEventJournalSubscribeCodec.ResponseParameters params = CacheEventJournalSubscribeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.oldestSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.newestSequence));
        }
        {
            ClientMessage clientMessage = CacheEventJournalReadCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aData, ReferenceObjects.aData);
            CacheEventJournalReadCodec.RequestParameters params = CacheEventJournalReadCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.startSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.minSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.predicate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.projection));
        }
        {
            ClientMessage clientMessage = CacheEventJournalReadCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.arrLongs, ReferenceObjects.aLong);
            CacheEventJournalReadCodec.ResponseParameters params = CacheEventJournalReadCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.readCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.arrLongs, params.itemSeqs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.nextSeq));
        }
        {
            ClientMessage clientMessage = CacheSetExpiryPolicyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.aData);
            CacheSetExpiryPolicyCodec.RequestParameters params = CacheSetExpiryPolicyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.keys));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.expiryPolicy));
        }
        {
            ClientMessage clientMessage = CacheSetExpiryPolicyCodec.encodeResponse(ReferenceObjects.aBoolean);
            CacheSetExpiryPolicyCodec.ResponseParameters params = CacheSetExpiryPolicyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = XATransactionClearRemoteCodec.encodeRequest(ReferenceObjects.anXid);
            XATransactionClearRemoteCodec.RequestParameters params = XATransactionClearRemoteCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
        }
        {
            ClientMessage clientMessage = XATransactionClearRemoteCodec.encodeResponse();
            XATransactionClearRemoteCodec.ResponseParameters params = XATransactionClearRemoteCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCollectTransactionsCodec.encodeRequest();
            XATransactionCollectTransactionsCodec.RequestParameters params = XATransactionCollectTransactionsCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCollectTransactionsCodec.encodeResponse(ReferenceObjects.datas);
            XATransactionCollectTransactionsCodec.ResponseParameters params = XATransactionCollectTransactionsCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = XATransactionFinalizeCodec.encodeRequest(ReferenceObjects.anXid, ReferenceObjects.aBoolean);
            XATransactionFinalizeCodec.RequestParameters params = XATransactionFinalizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.isCommit));
        }
        {
            ClientMessage clientMessage = XATransactionFinalizeCodec.encodeResponse();
            XATransactionFinalizeCodec.ResponseParameters params = XATransactionFinalizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCommitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            XATransactionCommitCodec.RequestParameters params = XATransactionCommitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.onePhase));
        }
        {
            ClientMessage clientMessage = XATransactionCommitCodec.encodeResponse();
            XATransactionCommitCodec.ResponseParameters params = XATransactionCommitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = XATransactionCreateCodec.encodeRequest(ReferenceObjects.anXid, ReferenceObjects.aLong);
            XATransactionCreateCodec.RequestParameters params = XATransactionCreateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anXid, params.xid));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
        }
        {
            ClientMessage clientMessage = XATransactionCreateCodec.encodeResponse(ReferenceObjects.aString);
            XATransactionCreateCodec.ResponseParameters params = XATransactionCreateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = XATransactionPrepareCodec.encodeRequest(ReferenceObjects.aString);
            XATransactionPrepareCodec.RequestParameters params = XATransactionPrepareCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
        }
        {
            ClientMessage clientMessage = XATransactionPrepareCodec.encodeResponse();
            XATransactionPrepareCodec.ResponseParameters params = XATransactionPrepareCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = XATransactionRollbackCodec.encodeRequest(ReferenceObjects.aString);
            XATransactionRollbackCodec.RequestParameters params = XATransactionRollbackCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
        }
        {
            ClientMessage clientMessage = XATransactionRollbackCodec.encodeResponse();
            XATransactionRollbackCodec.ResponseParameters params = XATransactionRollbackCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = TransactionCommitCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionCommitCodec.RequestParameters params = TransactionCommitCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionCommitCodec.encodeResponse();
            TransactionCommitCodec.ResponseParameters params = TransactionCommitCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = TransactionCreateCodec.encodeRequest(ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong);
            TransactionCreateCodec.RequestParameters params = TransactionCreateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.timeout));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.durability));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.transactionType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionCreateCodec.encodeResponse(ReferenceObjects.aString);
            TransactionCreateCodec.ResponseParameters params = TransactionCreateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            ClientMessage clientMessage = TransactionRollbackCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            TransactionRollbackCodec.RequestParameters params = TransactionRollbackCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.transactionId));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.threadId));
        }
        {
            ClientMessage clientMessage = TransactionRollbackCodec.encodeResponse();
            TransactionRollbackCodec.ResponseParameters params = TransactionRollbackCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateWithValueCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            ContinuousQueryPublisherCreateWithValueCodec.RequestParameters params = ContinuousQueryPublisherCreateWithValueCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
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
            ContinuousQueryPublisherCreateWithValueCodec.ResponseParameters params = ContinuousQueryPublisherCreateWithValueCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfEntry, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryPublisherCreateCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean);
            ContinuousQueryPublisherCreateCodec.RequestParameters params = ContinuousQueryPublisherCreateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
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
            ContinuousQueryPublisherCreateCodec.ResponseParameters params = ContinuousQueryPublisherCreateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryMadePublishableCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ContinuousQueryMadePublishableCodec.RequestParameters params = ContinuousQueryMadePublishableCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
        }
        {
            ClientMessage clientMessage = ContinuousQueryMadePublishableCodec.encodeResponse(ReferenceObjects.aBoolean);
            ContinuousQueryMadePublishableCodec.ResponseParameters params = ContinuousQueryMadePublishableCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean);
            ContinuousQueryAddListenerCodec.RequestParameters params = ContinuousQueryAddListenerCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.listenerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.localOnly));
        }
        {
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeResponse(ReferenceObjects.aString);
            ContinuousQueryAddListenerCodec.ResponseParameters params = ContinuousQueryAddListenerCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.response));
        }
        {
            class ContinuousQueryAddListenerCodecHandler extends ContinuousQueryAddListenerCodec.AbstractEventHandler {
                @Override
                public void handleQueryCacheSingleEventV10(com.hazelcast.map.impl.querycache.event.QueryCacheEventData data) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aQueryCacheEventData, data));
                }

                @Override
                public void handleQueryCacheBatchEventV10(Collection<com.hazelcast.map.impl.querycache.event.QueryCacheEventData> events, String source, int partitionId) {
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.queryCacheEventDatas, events));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, source));
                    Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, partitionId));
                }
            }
            ContinuousQueryAddListenerCodecHandler handler = new ContinuousQueryAddListenerCodecHandler();
            {
                ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheSingleEvent(ReferenceObjects.aQueryCacheEventData);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
            {
                ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheBatchEvent(ReferenceObjects.queryCacheEventDatas, ReferenceObjects.aString, ReferenceObjects.anInt);
                handler.handle(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            }
        }
        {
            ClientMessage clientMessage = ContinuousQuerySetReadCursorCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aLong);
            ContinuousQuerySetReadCursorCodec.RequestParameters params = ContinuousQuerySetReadCursorCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.sequence));
        }
        {
            ClientMessage clientMessage = ContinuousQuerySetReadCursorCodec.encodeResponse(ReferenceObjects.aBoolean);
            ContinuousQuerySetReadCursorCodec.ResponseParameters params = ContinuousQuerySetReadCursorCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ContinuousQueryDestroyCacheCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ContinuousQueryDestroyCacheCodec.RequestParameters params = ContinuousQueryDestroyCacheCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
        }
        {
            ClientMessage clientMessage = ContinuousQueryDestroyCacheCodec.encodeResponse(ReferenceObjects.aBoolean);
            ContinuousQueryDestroyCacheCodec.ResponseParameters params = ContinuousQueryDestroyCacheCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferSizeCodec.encodeRequest(ReferenceObjects.aString);
            RingbufferSizeCodec.RequestParameters params = RingbufferSizeCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferSizeCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferSizeCodec.ResponseParameters params = RingbufferSizeCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferTailSequenceCodec.encodeRequest(ReferenceObjects.aString);
            RingbufferTailSequenceCodec.RequestParameters params = RingbufferTailSequenceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferTailSequenceCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferTailSequenceCodec.ResponseParameters params = RingbufferTailSequenceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferHeadSequenceCodec.encodeRequest(ReferenceObjects.aString);
            RingbufferHeadSequenceCodec.RequestParameters params = RingbufferHeadSequenceCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferHeadSequenceCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferHeadSequenceCodec.ResponseParameters params = RingbufferHeadSequenceCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferCapacityCodec.encodeRequest(ReferenceObjects.aString);
            RingbufferCapacityCodec.RequestParameters params = RingbufferCapacityCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferCapacityCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferCapacityCodec.ResponseParameters params = RingbufferCapacityCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferRemainingCapacityCodec.encodeRequest(ReferenceObjects.aString);
            RingbufferRemainingCapacityCodec.RequestParameters params = RingbufferRemainingCapacityCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = RingbufferRemainingCapacityCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferRemainingCapacityCodec.ResponseParameters params = RingbufferRemainingCapacityCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aData);
            RingbufferAddCodec.RequestParameters params = RingbufferAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.overflowPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.value));
        }
        {
            ClientMessage clientMessage = RingbufferAddCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferAddCodec.ResponseParameters params = RingbufferAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferReadOneCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            RingbufferReadOneCodec.RequestParameters params = RingbufferReadOneCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.sequence));
        }
        {
            ClientMessage clientMessage = RingbufferReadOneCodec.encodeResponse(ReferenceObjects.aData);
            RingbufferReadOneCodec.ResponseParameters params = RingbufferReadOneCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferAddAllCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.datas, ReferenceObjects.anInt);
            RingbufferAddAllCodec.RequestParameters params = RingbufferAddAllCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.valueList));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.overflowPolicy));
        }
        {
            ClientMessage clientMessage = RingbufferAddAllCodec.encodeResponse(ReferenceObjects.aLong);
            RingbufferAddAllCodec.ResponseParameters params = RingbufferAddAllCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = RingbufferReadManyCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aData);
            RingbufferReadManyCodec.RequestParameters params = RingbufferReadManyCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.startSequence));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.minCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.filter));
        }
        {
            ClientMessage clientMessage = RingbufferReadManyCodec.encodeResponse(ReferenceObjects.anInt, ReferenceObjects.datas, ReferenceObjects.arrLongs, ReferenceObjects.aLong);
            RingbufferReadManyCodec.ResponseParameters params = RingbufferReadManyCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.readCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.datas, params.items));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.arrLongs, params.itemSeqs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.nextSeq));
        }
        {
            ClientMessage clientMessage = DurableExecutorShutdownCodec.encodeRequest(ReferenceObjects.aString);
            DurableExecutorShutdownCodec.RequestParameters params = DurableExecutorShutdownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = DurableExecutorShutdownCodec.encodeResponse();
            DurableExecutorShutdownCodec.ResponseParameters params = DurableExecutorShutdownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DurableExecutorIsShutdownCodec.encodeRequest(ReferenceObjects.aString);
            DurableExecutorIsShutdownCodec.RequestParameters params = DurableExecutorIsShutdownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = DurableExecutorIsShutdownCodec.encodeResponse(ReferenceObjects.aBoolean);
            DurableExecutorIsShutdownCodec.ResponseParameters params = DurableExecutorIsShutdownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorSubmitToPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aData);
            DurableExecutorSubmitToPartitionCodec.RequestParameters params = DurableExecutorSubmitToPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.callable));
        }
        {
            ClientMessage clientMessage = DurableExecutorSubmitToPartitionCodec.encodeResponse(ReferenceObjects.anInt);
            DurableExecutorSubmitToPartitionCodec.ResponseParameters params = DurableExecutorSubmitToPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            DurableExecutorRetrieveResultCodec.RequestParameters params = DurableExecutorRetrieveResultCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveResultCodec.encodeResponse(ReferenceObjects.aData);
            DurableExecutorRetrieveResultCodec.ResponseParameters params = DurableExecutorRetrieveResultCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = DurableExecutorDisposeResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            DurableExecutorDisposeResultCodec.RequestParameters params = DurableExecutorDisposeResultCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorDisposeResultCodec.encodeResponse();
            DurableExecutorDisposeResultCodec.ResponseParameters params = DurableExecutorDisposeResultCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveAndDisposeResultCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters params = DurableExecutorRetrieveAndDisposeResultCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.sequence));
        }
        {
            ClientMessage clientMessage = DurableExecutorRetrieveAndDisposeResultCodec.encodeResponse(ReferenceObjects.aData);
            DurableExecutorRetrieveAndDisposeResultCodec.ResponseParameters params = DurableExecutorRetrieveAndDisposeResultCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong);
            CardinalityEstimatorAddCodec.RequestParameters params = CardinalityEstimatorAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.hash));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorAddCodec.encodeResponse();
            CardinalityEstimatorAddCodec.ResponseParameters params = CardinalityEstimatorAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorEstimateCodec.encodeRequest(ReferenceObjects.aString);
            CardinalityEstimatorEstimateCodec.RequestParameters params = CardinalityEstimatorEstimateCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = CardinalityEstimatorEstimateCodec.encodeResponse(ReferenceObjects.aLong);
            CardinalityEstimatorEstimateCodec.ResponseParameters params = CardinalityEstimatorEstimateCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorShutdownCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorShutdownCodec.RequestParameters params = ScheduledExecutorShutdownCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorShutdownCodec.encodeResponse();
            ScheduledExecutorShutdownCodec.ResponseParameters params = ScheduledExecutorShutdownCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorSubmitToPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            ScheduledExecutorSubmitToPartitionCodec.RequestParameters params = ScheduledExecutorSubmitToPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.type));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.task));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.initialDelayInMillis));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.periodInMillis));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorSubmitToPartitionCodec.encodeResponse();
            ScheduledExecutorSubmitToPartitionCodec.ResponseParameters params = ScheduledExecutorSubmitToPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorSubmitToAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anAddress, ReferenceObjects.aByte, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aLong, ReferenceObjects.aLong);
            ScheduledExecutorSubmitToAddressCodec.RequestParameters params = ScheduledExecutorSubmitToAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aByte, params.type));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.task));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.initialDelayInMillis));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.periodInMillis));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorSubmitToAddressCodec.encodeResponse();
            ScheduledExecutorSubmitToAddressCodec.ResponseParameters params = ScheduledExecutorSubmitToAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetAllScheduledFuturesCodec.encodeRequest(ReferenceObjects.aString);
            ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters params = ScheduledExecutorGetAllScheduledFuturesCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetAllScheduledFuturesCodec.encodeResponse(ReferenceObjects.taskHandlers);
            ScheduledExecutorGetAllScheduledFuturesCodec.ResponseParameters params = ScheduledExecutorGetAllScheduledFuturesCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.taskHandlers, params.handlers));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetStatsFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters params = ScheduledExecutorGetStatsFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetStatsFromPartitionCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            ScheduledExecutorGetStatsFromPartitionCodec.ResponseParameters params = ScheduledExecutorGetStatsFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lastIdleTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalIdleTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalRuns));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalRunTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lastRunDurationNanos));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetStatsFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorGetStatsFromAddressCodec.RequestParameters params = ScheduledExecutorGetStatsFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetStatsFromAddressCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aLong);
            ScheduledExecutorGetStatsFromAddressCodec.ResponseParameters params = ScheduledExecutorGetStatsFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lastIdleTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalIdleTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalRuns));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.totalRunTimeNanos));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.lastRunDurationNanos));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetDelayFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters params = ScheduledExecutorGetDelayFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetDelayFromPartitionCodec.encodeResponse(ReferenceObjects.aLong);
            ScheduledExecutorGetDelayFromPartitionCodec.ResponseParameters params = ScheduledExecutorGetDelayFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetDelayFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorGetDelayFromAddressCodec.RequestParameters params = ScheduledExecutorGetDelayFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetDelayFromAddressCodec.encodeResponse(ReferenceObjects.aLong);
            ScheduledExecutorGetDelayFromAddressCodec.ResponseParameters params = ScheduledExecutorGetDelayFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorCancelFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean);
            ScheduledExecutorCancelFromPartitionCodec.RequestParameters params = ScheduledExecutorCancelFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.mayInterruptIfRunning));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorCancelFromPartitionCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorCancelFromPartitionCodec.ResponseParameters params = ScheduledExecutorCancelFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorCancelFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress, ReferenceObjects.aBoolean);
            ScheduledExecutorCancelFromAddressCodec.RequestParameters params = ScheduledExecutorCancelFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.mayInterruptIfRunning));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorCancelFromAddressCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorCancelFromAddressCodec.ResponseParameters params = ScheduledExecutorCancelFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsCancelledFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorIsCancelledFromPartitionCodec.RequestParameters params = ScheduledExecutorIsCancelledFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsCancelledFromPartitionCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorIsCancelledFromPartitionCodec.ResponseParameters params = ScheduledExecutorIsCancelledFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsCancelledFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters params = ScheduledExecutorIsCancelledFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsCancelledFromAddressCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorIsCancelledFromAddressCodec.ResponseParameters params = ScheduledExecutorIsCancelledFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsDoneFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorIsDoneFromPartitionCodec.RequestParameters params = ScheduledExecutorIsDoneFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsDoneFromPartitionCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorIsDoneFromPartitionCodec.ResponseParameters params = ScheduledExecutorIsDoneFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsDoneFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorIsDoneFromAddressCodec.RequestParameters params = ScheduledExecutorIsDoneFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorIsDoneFromAddressCodec.encodeResponse(ReferenceObjects.aBoolean);
            ScheduledExecutorIsDoneFromAddressCodec.ResponseParameters params = ScheduledExecutorIsDoneFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetResultFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorGetResultFromPartitionCodec.RequestParameters params = ScheduledExecutorGetResultFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetResultFromPartitionCodec.encodeResponse(ReferenceObjects.aData);
            ScheduledExecutorGetResultFromPartitionCodec.ResponseParameters params = ScheduledExecutorGetResultFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetResultFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorGetResultFromAddressCodec.RequestParameters params = ScheduledExecutorGetResultFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorGetResultFromAddressCodec.encodeResponse(ReferenceObjects.aData);
            ScheduledExecutorGetResultFromAddressCodec.ResponseParameters params = ScheduledExecutorGetResultFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.response));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorDisposeFromPartitionCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            ScheduledExecutorDisposeFromPartitionCodec.RequestParameters params = ScheduledExecutorDisposeFromPartitionCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorDisposeFromPartitionCodec.encodeResponse();
            ScheduledExecutorDisposeFromPartitionCodec.ResponseParameters params = ScheduledExecutorDisposeFromPartitionCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorDisposeFromAddressCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anAddress);
            ScheduledExecutorDisposeFromAddressCodec.RequestParameters params = ScheduledExecutorDisposeFromAddressCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.schedulerName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.taskName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.address));
        }
        {
            ClientMessage clientMessage = ScheduledExecutorDisposeFromAddressCodec.encodeResponse();
            ScheduledExecutorDisposeFromAddressCodec.ResponseParameters params = ScheduledExecutorDisposeFromAddressCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMultiMapConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddMultiMapConfigCodec.RequestParameters params = DynamicConfigAddMultiMapConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.collectionType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.binary));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMultiMapConfigCodec.encodeResponse();
            DynamicConfigAddMultiMapConfigCodec.ResponseParameters params = DynamicConfigAddMultiMapConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddRingbufferConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.ringbufferStore, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddRingbufferConfigCodec.RequestParameters params = DynamicConfigAddRingbufferConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.capacity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.timeToLiveSeconds));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.inMemoryFormat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.ringbufferStore, params.ringbufferStoreConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddRingbufferConfigCodec.encodeResponse();
            DynamicConfigAddRingbufferConfigCodec.ResponseParameters params = DynamicConfigAddRingbufferConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCardinalityEstimatorConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters params = DynamicConfigAddCardinalityEstimatorConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCardinalityEstimatorConfigCodec.encodeResponse();
            DynamicConfigAddCardinalityEstimatorConfigCodec.ResponseParameters params = DynamicConfigAddCardinalityEstimatorConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddLockConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            DynamicConfigAddLockConfigCodec.RequestParameters params = DynamicConfigAddLockConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddLockConfigCodec.encodeResponse();
            DynamicConfigAddLockConfigCodec.ResponseParameters params = DynamicConfigAddLockConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddListConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddListConfigCodec.RequestParameters params = DynamicConfigAddListConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddListConfigCodec.encodeResponse();
            DynamicConfigAddListConfigCodec.ResponseParameters params = DynamicConfigAddListConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddSetConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddSetConfigCodec.RequestParameters params = DynamicConfigAddSetConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddSetConfigCodec.encodeResponse();
            DynamicConfigAddSetConfigCodec.ResponseParameters params = DynamicConfigAddSetConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddReplicatedMapConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddReplicatedMapConfigCodec.RequestParameters params = DynamicConfigAddReplicatedMapConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.inMemoryFormat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.asyncFillup));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddReplicatedMapConfigCodec.encodeResponse();
            DynamicConfigAddReplicatedMapConfigCodec.ResponseParameters params = DynamicConfigAddReplicatedMapConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddTopicConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.listenerConfigs);
            DynamicConfigAddTopicConfigCodec.RequestParameters params = DynamicConfigAddTopicConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.globalOrderingEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.multiThreadingEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddTopicConfigCodec.encodeResponse();
            DynamicConfigAddTopicConfigCodec.ResponseParameters params = DynamicConfigAddTopicConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddExecutorConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString);
            DynamicConfigAddExecutorConfigCodec.RequestParameters params = DynamicConfigAddExecutorConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.poolSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.queueCapacity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddExecutorConfigCodec.encodeResponse();
            DynamicConfigAddExecutorConfigCodec.ResponseParameters params = DynamicConfigAddExecutorConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddDurableExecutorConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString);
            DynamicConfigAddDurableExecutorConfigCodec.RequestParameters params = DynamicConfigAddDurableExecutorConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.poolSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.durability));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.capacity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddDurableExecutorConfigCodec.encodeResponse();
            DynamicConfigAddDurableExecutorConfigCodec.ResponseParameters params = DynamicConfigAddDurableExecutorConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddScheduledExecutorConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters params = DynamicConfigAddScheduledExecutorConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.poolSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.durability));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.capacity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddScheduledExecutorConfigCodec.encodeResponse();
            DynamicConfigAddScheduledExecutorConfigCodec.ResponseParameters params = DynamicConfigAddScheduledExecutorConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddSemaphoreConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString);
            DynamicConfigAddSemaphoreConfigCodec.RequestParameters params = DynamicConfigAddSemaphoreConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.initialPermits));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddSemaphoreConfigCodec.encodeResponse();
            DynamicConfigAddSemaphoreConfigCodec.ResponseParameters params = DynamicConfigAddSemaphoreConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddQueueConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.queueStoreConfig, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddQueueConfigCodec.RequestParameters params = DynamicConfigAddQueueConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.emptyQueueTtl));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.queueStoreConfig, params.queueStoreConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddQueueConfigCodec.encodeResponse();
            DynamicConfigAddQueueConfigCodec.ResponseParameters params = DynamicConfigAddQueueConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMapConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.listenerConfigs, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.mapStoreConfig, ReferenceObjects.nearCacheConfig, ReferenceObjects.wanReplicationRef, ReferenceObjects.mapIndexConfigs, ReferenceObjects.mapAttributeConfigs, ReferenceObjects.queryCacheConfigs, ReferenceObjects.aString, ReferenceObjects.aData, ReferenceObjects.hotRestartConfig, ReferenceObjects.anInt, ReferenceObjects.anInt);
            DynamicConfigAddMapConfigCodec.RequestParameters params = DynamicConfigAddMapConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.timeToLiveSeconds));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxIdleSeconds));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.evictionPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.readBackupData));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheDeserializedValues));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.inMemoryFormat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.partitionLostListenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.mapEvictionPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.maxSizeConfigMaxSizePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.maxSizeConfigSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.mapStoreConfig, params.mapStoreConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.nearCacheConfig, params.nearCacheConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.wanReplicationRef, params.wanReplicationRef));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.mapIndexConfigs, params.mapIndexConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.mapAttributeConfigs, params.mapAttributeConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.queryCacheConfigs, params.queryCacheConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.partitioningStrategyClassName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.partitioningStrategyImplementation));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.hotRestartConfig, params.hotRestartConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.metadataPolicy));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMapConfigCodec.encodeResponse();
            DynamicConfigAddMapConfigCodec.ResponseParameters params = DynamicConfigAddMapConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddReliableTopicConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.listenerConfigs, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aData);
            DynamicConfigAddReliableTopicConfigCodec.RequestParameters params = DynamicConfigAddReliableTopicConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.listenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.readBatchSize));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.topicOverloadPolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aData, params.executor));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddReliableTopicConfigCodec.encodeResponse();
            DynamicConfigAddReliableTopicConfigCodec.ResponseParameters params = DynamicConfigAddReliableTopicConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCacheConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.aBoolean, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.anInt, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.listenerConfigs, ReferenceObjects.aString, ReferenceObjects.timedExpiryPolicyFactoryConfig, ReferenceObjects.cacheEntryListenerConfigs, ReferenceObjects.evictionConfig, ReferenceObjects.wanReplicationRef, ReferenceObjects.hotRestartConfig);
            DynamicConfigAddCacheConfigCodec.RequestParameters params = DynamicConfigAddCacheConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.keyType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.valueType));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.managementEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.readThrough));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.writeThrough));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheLoaderFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheWriterFactory));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheLoader));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheWriter));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.backupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.asyncBackupCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.inMemoryFormat));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.disablePerEntryInvalidationEvents));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.listenerConfigs, params.partitionLostListenerConfigs));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.expiryPolicyFactoryClassName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.timedExpiryPolicyFactoryConfig, params.timedExpiryPolicyFactoryConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.cacheEntryListenerConfigs, params.cacheEntryListeners));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.evictionConfig, params.evictionConfig));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.wanReplicationRef, params.wanReplicationRef));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.hotRestartConfig, params.hotRestartConfig));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCacheConfigCodec.encodeResponse();
            DynamicConfigAddCacheConfigCodec.ResponseParameters params = DynamicConfigAddCacheConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddEventJournalConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.anInt, ReferenceObjects.anInt);
            DynamicConfigAddEventJournalConfigCodec.RequestParameters params = DynamicConfigAddEventJournalConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.cacheName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.enabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.capacity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.timeToLiveSeconds));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddEventJournalConfigCodec.encodeResponse();
            DynamicConfigAddEventJournalConfigCodec.ResponseParameters params = DynamicConfigAddEventJournalConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddFlakeIdGeneratorConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aLong);
            DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters params = DynamicConfigAddFlakeIdGeneratorConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.prefetchCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.prefetchValidity));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.idOffset));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.nodeIdOffset));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddFlakeIdGeneratorConfigCodec.encodeResponse();
            DynamicConfigAddFlakeIdGeneratorConfigCodec.ResponseParameters params = DynamicConfigAddFlakeIdGeneratorConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddAtomicLongConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddAtomicLongConfigCodec.RequestParameters params = DynamicConfigAddAtomicLongConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddAtomicLongConfigCodec.encodeResponse();
            DynamicConfigAddAtomicLongConfigCodec.ResponseParameters params = DynamicConfigAddAtomicLongConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddAtomicReferenceConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.aString, ReferenceObjects.anInt);
            DynamicConfigAddAtomicReferenceConfigCodec.RequestParameters params = DynamicConfigAddAtomicReferenceConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mergePolicy));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.mergeBatchSize));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddAtomicReferenceConfigCodec.encodeResponse();
            DynamicConfigAddAtomicReferenceConfigCodec.ResponseParameters params = DynamicConfigAddAtomicReferenceConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCountDownLatchConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aString);
            DynamicConfigAddCountDownLatchConfigCodec.RequestParameters params = DynamicConfigAddCountDownLatchConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddCountDownLatchConfigCodec.encodeResponse();
            DynamicConfigAddCountDownLatchConfigCodec.ResponseParameters params = DynamicConfigAddCountDownLatchConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddPNCounterConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt, ReferenceObjects.aBoolean, ReferenceObjects.aString);
            DynamicConfigAddPNCounterConfigCodec.RequestParameters params = DynamicConfigAddPNCounterConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.replicaCount));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.statisticsEnabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.quorumName));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddPNCounterConfigCodec.encodeResponse();
            DynamicConfigAddPNCounterConfigCodec.ResponseParameters params = DynamicConfigAddPNCounterConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMerkleTreeConfigCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aBoolean, ReferenceObjects.anInt);
            DynamicConfigAddMerkleTreeConfigCodec.RequestParameters params = DynamicConfigAddMerkleTreeConfigCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.mapName));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.enabled));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.depth));
        }
        {
            ClientMessage clientMessage = DynamicConfigAddMerkleTreeConfigCodec.encodeResponse();
            DynamicConfigAddMerkleTreeConfigCodec.ResponseParameters params = DynamicConfigAddMerkleTreeConfigCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
        }
        {
            ClientMessage clientMessage = FlakeIdGeneratorNewIdBatchCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.anInt);
            FlakeIdGeneratorNewIdBatchCodec.RequestParameters params = FlakeIdGeneratorNewIdBatchCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batchSize));
        }
        {
            ClientMessage clientMessage = FlakeIdGeneratorNewIdBatchCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aLong, ReferenceObjects.anInt);
            FlakeIdGeneratorNewIdBatchCodec.ResponseParameters params = FlakeIdGeneratorNewIdBatchCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.base));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.increment));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.batchSize));
        }
        {
            ClientMessage clientMessage = PNCounterGetCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aListOfStringToLong, ReferenceObjects.anAddress);
            PNCounterGetCodec.RequestParameters params = PNCounterGetCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToLong, params.replicaTimestamps));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.targetReplica));
        }
        {
            ClientMessage clientMessage = PNCounterGetCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aListOfStringToLong, ReferenceObjects.anInt);
            PNCounterGetCodec.ResponseParameters params = PNCounterGetCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToLong, params.replicaTimestamps));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.replicaCount));
        }
        {
            ClientMessage clientMessage = PNCounterAddCodec.encodeRequest(ReferenceObjects.aString, ReferenceObjects.aLong, ReferenceObjects.aBoolean, ReferenceObjects.aListOfStringToLong, ReferenceObjects.anAddress);
            PNCounterAddCodec.RequestParameters params = PNCounterAddCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.delta));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aBoolean, params.getBeforeUpdate));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToLong, params.replicaTimestamps));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anAddress, params.targetReplica));
        }
        {
            ClientMessage clientMessage = PNCounterAddCodec.encodeResponse(ReferenceObjects.aLong, ReferenceObjects.aListOfStringToLong, ReferenceObjects.anInt);
            PNCounterAddCodec.ResponseParameters params = PNCounterAddCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aLong, params.value));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aListOfStringToLong, params.replicaTimestamps));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.replicaCount));
        }
        {
            ClientMessage clientMessage = PNCounterGetConfiguredReplicaCountCodec.encodeRequest(ReferenceObjects.aString);
            PNCounterGetConfiguredReplicaCountCodec.RequestParameters params = PNCounterGetConfiguredReplicaCountCodec.decodeRequest(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.aString, params.name));
        }
        {
            ClientMessage clientMessage = PNCounterGetConfiguredReplicaCountCodec.encodeResponse(ReferenceObjects.anInt);
            PNCounterGetConfiguredReplicaCountCodec.ResponseParameters params = PNCounterGetConfiguredReplicaCountCodec.decodeResponse(ClientMessage.createForDecode(clientMessage.buffer(), 0));
            Assert.assertTrue(ReferenceObjects.isEqual(ReferenceObjects.anInt, params.response));
        }
    }
}

