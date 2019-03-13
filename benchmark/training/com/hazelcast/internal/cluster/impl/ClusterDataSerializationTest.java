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
package com.hazelcast.internal.cluster.impl;


import ClusterState.FROZEN;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterDataSerializationTest {
    private static final SerializationService SERIALIZATION_SERVICE = new DefaultSerializationServiceBuilder().build();

    private static final ClusterStateChange<MemberVersion> VERSION_CLUSTER_STATE_CHANGE = ClusterStateChange.from(MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion()));

    private static final ClusterStateChange<ClusterState> CLUSTER_STATE_CHANGE = ClusterStateChange.from(FROZEN);

    @Test
    public void testSerializationOf_clusterStateChange_fromVersion() {
        Data serialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toData(ClusterDataSerializationTest.VERSION_CLUSTER_STATE_CHANGE);
        ClusterStateChange deserialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toObject(serialized);
        Assert.assertEquals(ClusterDataSerializationTest.VERSION_CLUSTER_STATE_CHANGE, deserialized);
    }

    @Test
    public void testSerializationOf_clusterStateChange_fromClusterState() {
        Data serialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toData(ClusterDataSerializationTest.CLUSTER_STATE_CHANGE);
        ClusterStateChange deserialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toObject(serialized);
        Assert.assertEquals(ClusterDataSerializationTest.CLUSTER_STATE_CHANGE, deserialized);
    }

    @Test
    public void testSerializationOf_clusterStateChangeTxnLogRecord_whenVersionChange() throws UnknownHostException {
        ClusterStateTransactionLogRecord txnLogRecord = new ClusterStateTransactionLogRecord(ClusterDataSerializationTest.VERSION_CLUSTER_STATE_CHANGE, new Address("127.0.0.1", 5071), new Address("127.0.0.1", 5702), UUID.randomUUID().toString(), 120, 111, 130, false);
        Data serialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toData(txnLogRecord);
        ClusterStateTransactionLogRecord deserialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toObject(serialized);
        Assert.assertEquals(txnLogRecord.stateChange, deserialized.stateChange);
        Assert.assertEquals(txnLogRecord.initiator, deserialized.initiator);
        Assert.assertEquals(txnLogRecord.target, deserialized.target);
        Assert.assertEquals(txnLogRecord.txnId, deserialized.txnId);
        Assert.assertEquals(txnLogRecord.leaseTime, deserialized.leaseTime);
        Assert.assertEquals(txnLogRecord.isTransient, deserialized.isTransient);
        Assert.assertEquals(txnLogRecord.memberListVersion, deserialized.memberListVersion);
        Assert.assertEquals(txnLogRecord.partitionStateVersion, deserialized.partitionStateVersion);
    }

    @Test
    public void testSerializationOf_clusterStateChangeTxnLogRecord_whenStateChange() throws UnknownHostException {
        ClusterStateTransactionLogRecord txnLogRecord = new ClusterStateTransactionLogRecord(ClusterDataSerializationTest.CLUSTER_STATE_CHANGE, new Address("127.0.0.1", 5071), new Address("127.0.0.1", 5702), UUID.randomUUID().toString(), 120, 111, 130, false);
        Data serialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toData(txnLogRecord);
        ClusterStateTransactionLogRecord deserialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toObject(serialized);
        Assert.assertEquals(txnLogRecord.stateChange, deserialized.stateChange);
        Assert.assertEquals(txnLogRecord.initiator, deserialized.initiator);
        Assert.assertEquals(txnLogRecord.target, deserialized.target);
        Assert.assertEquals(txnLogRecord.txnId, deserialized.txnId);
        Assert.assertEquals(txnLogRecord.leaseTime, deserialized.leaseTime);
        Assert.assertEquals(txnLogRecord.isTransient, deserialized.isTransient);
        Assert.assertEquals(txnLogRecord.memberListVersion, deserialized.memberListVersion);
        Assert.assertEquals(txnLogRecord.partitionStateVersion, deserialized.partitionStateVersion);
    }

    @Test
    public void testSerializationOf_memberInfo() throws UnknownHostException {
        Address memberAddress = new Address("127.0.0.1", 5071);
        Address clientAddress = new Address("127.0.0.1", 7654);
        Address restAddress = new Address("127.0.0.1", 8080);
        // member attributes, test an integer, a String and an IdentifiedDataSerializable as values
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("a", 2);
        attributes.put("b", "b");
        attributes.put("c", new Address("127.0.0.1", 5999));
        Map<EndpointQualifier, Address> addressMap = new HashMap<EndpointQualifier, Address>();
        addressMap.put(EndpointQualifier.MEMBER, memberAddress);
        addressMap.put(EndpointQualifier.CLIENT, clientAddress);
        addressMap.put(EndpointQualifier.REST, restAddress);
        MemberInfo memberInfo = new MemberInfo(memberAddress, UUID.randomUUID().toString(), attributes, false, MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion()), addressMap);
        Data serialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toData(memberInfo);
        MemberInfo deserialized = ClusterDataSerializationTest.SERIALIZATION_SERVICE.toObject(serialized);
        Assert.assertEquals(deserialized.getAddress(), memberInfo.getAddress());
        Assert.assertEquals(deserialized.getVersion(), memberInfo.getVersion());
        Assert.assertEquals(deserialized.getUuid(), memberInfo.getUuid());
        Assert.assertEquals(deserialized.getAttributes().get("a"), memberInfo.getAttributes().get("a"));
        Assert.assertEquals(deserialized.getAttributes().get("b"), memberInfo.getAttributes().get("b"));
        Assert.assertEquals(deserialized.getAttributes().get("c"), memberInfo.getAttributes().get("c"));
        Assert.assertEquals(3, deserialized.getAddressMap().size());
        Assert.assertEquals(memberAddress, deserialized.getAddressMap().get(EndpointQualifier.MEMBER));
        Assert.assertEquals(clientAddress, deserialized.getAddressMap().get(EndpointQualifier.CLIENT));
        Assert.assertEquals(restAddress, deserialized.getAddressMap().get(EndpointQualifier.REST));
    }
}

