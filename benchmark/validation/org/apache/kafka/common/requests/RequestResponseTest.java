/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.requests;


import ApiKeys.CONTROLLED_SHUTDOWN;
import ApiKeys.CREATE_ACLS;
import ApiKeys.DELETE_ACLS;
import ApiKeys.DESCRIBE_ACLS;
import ApiKeys.FETCH;
import ApiKeys.PRODUCE;
import Errors.FETCH_SESSION_ID_NOT_FOUND;
import Errors.NOT_ENOUGH_REPLICAS;
import FindCoordinatorRequest.CoordinatorType;
import IsolationLevel.READ_COMMITTED;
import IsolationLevel.READ_UNCOMMITTED;
import MetadataRequest.Builder;
import ProduceResponse.INVALID_OFFSET;
import ProduceResponse.PartitionResponse;
import RecordBatch.NO_TIMESTAMP;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.NotCoordinatorException;
import org.apache.kafka.common.errors.NotEnoughReplicasException;
import org.apache.kafka.common.errors.SecurityDisabledException;
import org.apache.kafka.common.errors.UnknownServerException;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.record.RecordBatch;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class RequestResponseTest {
    @Test
    public void testSerialization() throws Exception {
        checkRequest(createFindCoordinatorRequest(0));
        checkRequest(createFindCoordinatorRequest(1));
        checkErrorResponse(createFindCoordinatorRequest(0), new UnknownServerException());
        checkErrorResponse(createFindCoordinatorRequest(1), new UnknownServerException());
        checkResponse(createFindCoordinatorResponse(), 0);
        checkResponse(createFindCoordinatorResponse(), 1);
        checkRequest(createControlledShutdownRequest());
        checkResponse(createControlledShutdownResponse(), 1);
        checkErrorResponse(createControlledShutdownRequest(), new UnknownServerException());
        checkErrorResponse(createControlledShutdownRequest(0), new UnknownServerException());
        checkRequest(createFetchRequest(4));
        checkResponse(createFetchResponse(), 4);
        List<TopicPartition> toForgetTopics = new ArrayList<>();
        toForgetTopics.add(new TopicPartition("foo", 0));
        toForgetTopics.add(new TopicPartition("foo", 2));
        toForgetTopics.add(new TopicPartition("bar", 0));
        checkRequest(createFetchRequest(7, new FetchMetadata(123, 456), toForgetTopics));
        checkResponse(createFetchResponse(123), 7);
        checkResponse(createFetchResponse(FETCH_SESSION_ID_NOT_FOUND, 123), 7);
        checkErrorResponse(createFetchRequest(4), new UnknownServerException());
        checkRequest(createHeartBeatRequest());
        checkErrorResponse(createHeartBeatRequest(), new UnknownServerException());
        checkResponse(createHeartBeatResponse(), 0);
        checkRequest(createJoinGroupRequest(1));
        checkErrorResponse(createJoinGroupRequest(0), new UnknownServerException());
        checkErrorResponse(createJoinGroupRequest(1), new UnknownServerException());
        checkResponse(createJoinGroupResponse(), 0);
        checkRequest(createLeaveGroupRequest());
        checkErrorResponse(createLeaveGroupRequest(), new UnknownServerException());
        checkResponse(createLeaveGroupResponse(), 0);
        checkRequest(createListGroupsRequest());
        checkErrorResponse(createListGroupsRequest(), new UnknownServerException());
        checkResponse(createListGroupsResponse(), 0);
        checkRequest(createDescribeGroupRequest());
        checkErrorResponse(createDescribeGroupRequest(), new UnknownServerException());
        checkResponse(createDescribeGroupResponse(), 0);
        checkRequest(createDeleteGroupsRequest());
        checkErrorResponse(createDeleteGroupsRequest(), new UnknownServerException());
        checkResponse(createDeleteGroupsResponse(), 0);
        checkRequest(createListOffsetRequest(1));
        checkErrorResponse(createListOffsetRequest(1), new UnknownServerException());
        checkResponse(createListOffsetResponse(1), 1);
        checkRequest(createListOffsetRequest(2));
        checkErrorResponse(createListOffsetRequest(2), new UnknownServerException());
        checkResponse(createListOffsetResponse(2), 2);
        checkRequest(Builder.allTopics().build(((short) (2))));
        checkRequest(createMetadataRequest(1, Collections.singletonList("topic1")));
        checkErrorResponse(createMetadataRequest(1, Collections.singletonList("topic1")), new UnknownServerException());
        checkResponse(createMetadataResponse(), 2);
        checkErrorResponse(createMetadataRequest(2, Collections.singletonList("topic1")), new UnknownServerException());
        checkResponse(createMetadataResponse(), 3);
        checkErrorResponse(createMetadataRequest(3, Collections.singletonList("topic1")), new UnknownServerException());
        checkResponse(createMetadataResponse(), 4);
        checkErrorResponse(createMetadataRequest(4, Collections.singletonList("topic1")), new UnknownServerException());
        checkRequest(OffsetFetchRequest.forAllPartitions("group1"));
        checkErrorResponse(OffsetFetchRequest.forAllPartitions("group1"), new NotCoordinatorException("Not Coordinator"));
        checkRequest(createOffsetFetchRequest(0));
        checkRequest(createOffsetFetchRequest(1));
        checkRequest(createOffsetFetchRequest(2));
        checkRequest(OffsetFetchRequest.forAllPartitions("group1"));
        checkErrorResponse(createOffsetFetchRequest(0), new UnknownServerException());
        checkErrorResponse(createOffsetFetchRequest(1), new UnknownServerException());
        checkErrorResponse(createOffsetFetchRequest(2), new UnknownServerException());
        checkResponse(createOffsetFetchResponse(), 0);
        checkRequest(createProduceRequest(2));
        checkErrorResponse(createProduceRequest(2), new UnknownServerException());
        checkRequest(createProduceRequest(3));
        checkErrorResponse(createProduceRequest(3), new UnknownServerException());
        checkResponse(createProduceResponse(), 2);
        checkRequest(createStopReplicaRequest(0, true));
        checkRequest(createStopReplicaRequest(0, false));
        checkErrorResponse(createStopReplicaRequest(0, true), new UnknownServerException());
        checkRequest(createStopReplicaRequest(1, true));
        checkRequest(createStopReplicaRequest(1, false));
        checkErrorResponse(createStopReplicaRequest(1, true), new UnknownServerException());
        checkResponse(createStopReplicaResponse(), 0);
        checkRequest(createLeaderAndIsrRequest(0));
        checkErrorResponse(createLeaderAndIsrRequest(0), new UnknownServerException());
        checkRequest(createLeaderAndIsrRequest(1));
        checkErrorResponse(createLeaderAndIsrRequest(1), new UnknownServerException());
        checkResponse(createLeaderAndIsrResponse(), 0);
        checkRequest(createSaslHandshakeRequest());
        checkErrorResponse(createSaslHandshakeRequest(), new UnknownServerException());
        checkResponse(createSaslHandshakeResponse(), 0);
        checkRequest(createSaslAuthenticateRequest());
        checkErrorResponse(createSaslAuthenticateRequest(), new UnknownServerException());
        checkResponse(createSaslAuthenticateResponse(), 0);
        checkResponse(createSaslAuthenticateResponse(), 1);
        checkRequest(createApiVersionRequest());
        checkErrorResponse(createApiVersionRequest(), new UnknownServerException());
        checkResponse(createApiVersionResponse(), 0);
        checkRequest(createCreateTopicRequest(0));
        checkErrorResponse(createCreateTopicRequest(0), new UnknownServerException());
        checkResponse(createCreateTopicResponse(), 0);
        checkRequest(createCreateTopicRequest(1));
        checkErrorResponse(createCreateTopicRequest(1), new UnknownServerException());
        checkResponse(createCreateTopicResponse(), 1);
        checkRequest(createDeleteTopicsRequest());
        checkErrorResponse(createDeleteTopicsRequest(), new UnknownServerException());
        checkResponse(createDeleteTopicsResponse(), 0);
        checkRequest(createInitPidRequest());
        checkErrorResponse(createInitPidRequest(), new UnknownServerException());
        checkResponse(createInitPidResponse(), 0);
        checkRequest(createAddPartitionsToTxnRequest());
        checkResponse(createAddPartitionsToTxnResponse(), 0);
        checkErrorResponse(createAddPartitionsToTxnRequest(), new UnknownServerException());
        checkRequest(createAddOffsetsToTxnRequest());
        checkResponse(createAddOffsetsToTxnResponse(), 0);
        checkErrorResponse(createAddOffsetsToTxnRequest(), new UnknownServerException());
        checkRequest(createEndTxnRequest());
        checkResponse(createEndTxnResponse(), 0);
        checkErrorResponse(createEndTxnRequest(), new UnknownServerException());
        checkRequest(createWriteTxnMarkersRequest());
        checkResponse(createWriteTxnMarkersResponse(), 0);
        checkErrorResponse(createWriteTxnMarkersRequest(), new UnknownServerException());
        checkRequest(createTxnOffsetCommitRequest());
        checkResponse(createTxnOffsetCommitResponse(), 0);
        checkErrorResponse(createTxnOffsetCommitRequest(), new UnknownServerException());
        checkOlderFetchVersions();
        checkResponse(createMetadataResponse(), 0);
        checkResponse(createMetadataResponse(), 1);
        checkErrorResponse(createMetadataRequest(1, Collections.singletonList("topic1")), new UnknownServerException());
        checkRequest(createOffsetCommitRequest(0));
        checkErrorResponse(createOffsetCommitRequest(0), new UnknownServerException());
        checkRequest(createOffsetCommitRequest(1));
        checkErrorResponse(createOffsetCommitRequest(1), new UnknownServerException());
        checkRequest(createOffsetCommitRequest(2));
        checkErrorResponse(createOffsetCommitRequest(2), new UnknownServerException());
        checkRequest(createOffsetCommitRequest(3));
        checkErrorResponse(createOffsetCommitRequest(3), new UnknownServerException());
        checkRequest(createOffsetCommitRequest(4));
        checkErrorResponse(createOffsetCommitRequest(4), new UnknownServerException());
        checkResponse(createOffsetCommitResponse(), 4);
        checkRequest(createOffsetCommitRequest(5));
        checkErrorResponse(createOffsetCommitRequest(5), new UnknownServerException());
        checkResponse(createOffsetCommitResponse(), 5);
        checkRequest(createJoinGroupRequest(0));
        checkRequest(createUpdateMetadataRequest(0, null));
        checkErrorResponse(createUpdateMetadataRequest(0, null), new UnknownServerException());
        checkRequest(createUpdateMetadataRequest(1, null));
        checkRequest(createUpdateMetadataRequest(1, "rack1"));
        checkErrorResponse(createUpdateMetadataRequest(1, null), new UnknownServerException());
        checkRequest(createUpdateMetadataRequest(2, "rack1"));
        checkRequest(createUpdateMetadataRequest(2, null));
        checkErrorResponse(createUpdateMetadataRequest(2, "rack1"), new UnknownServerException());
        checkRequest(createUpdateMetadataRequest(3, "rack1"));
        checkRequest(createUpdateMetadataRequest(3, null));
        checkErrorResponse(createUpdateMetadataRequest(3, "rack1"), new UnknownServerException());
        checkRequest(createUpdateMetadataRequest(4, "rack1"));
        checkRequest(createUpdateMetadataRequest(4, null));
        checkErrorResponse(createUpdateMetadataRequest(4, "rack1"), new UnknownServerException());
        checkRequest(createUpdateMetadataRequest(5, "rack1"));
        checkRequest(createUpdateMetadataRequest(5, null));
        checkErrorResponse(createUpdateMetadataRequest(5, "rack1"), new UnknownServerException());
        checkResponse(createUpdateMetadataResponse(), 0);
        checkRequest(createListOffsetRequest(0));
        checkErrorResponse(createListOffsetRequest(0), new UnknownServerException());
        checkResponse(createListOffsetResponse(0), 0);
        checkRequest(createLeaderEpochRequest());
        checkResponse(createLeaderEpochResponse(), 0);
        checkErrorResponse(createLeaderEpochRequest(), new UnknownServerException());
        checkRequest(createAddPartitionsToTxnRequest());
        checkErrorResponse(createAddPartitionsToTxnRequest(), new UnknownServerException());
        checkResponse(createAddPartitionsToTxnResponse(), 0);
        checkRequest(createAddOffsetsToTxnRequest());
        checkErrorResponse(createAddOffsetsToTxnRequest(), new UnknownServerException());
        checkResponse(createAddOffsetsToTxnResponse(), 0);
        checkRequest(createEndTxnRequest());
        checkErrorResponse(createEndTxnRequest(), new UnknownServerException());
        checkResponse(createEndTxnResponse(), 0);
        checkRequest(createWriteTxnMarkersRequest());
        checkErrorResponse(createWriteTxnMarkersRequest(), new UnknownServerException());
        checkResponse(createWriteTxnMarkersResponse(), 0);
        checkRequest(createTxnOffsetCommitRequest());
        checkErrorResponse(createTxnOffsetCommitRequest(), new UnknownServerException());
        checkResponse(createTxnOffsetCommitResponse(), 0);
        checkRequest(createListAclsRequest());
        checkErrorResponse(createListAclsRequest(), new SecurityDisabledException("Security is not enabled."));
        checkResponse(createDescribeAclsResponse(), DESCRIBE_ACLS.latestVersion());
        checkRequest(createCreateAclsRequest());
        checkErrorResponse(createCreateAclsRequest(), new SecurityDisabledException("Security is not enabled."));
        checkResponse(createCreateAclsResponse(), CREATE_ACLS.latestVersion());
        checkRequest(createDeleteAclsRequest());
        checkErrorResponse(createDeleteAclsRequest(), new SecurityDisabledException("Security is not enabled."));
        checkResponse(createDeleteAclsResponse(), DELETE_ACLS.latestVersion());
        checkRequest(createAlterConfigsRequest());
        checkErrorResponse(createAlterConfigsRequest(), new UnknownServerException());
        checkResponse(createAlterConfigsResponse(), 0);
        checkRequest(createDescribeConfigsRequest(0));
        checkRequest(createDescribeConfigsRequestWithConfigEntries(0));
        checkErrorResponse(createDescribeConfigsRequest(0), new UnknownServerException());
        checkResponse(createDescribeConfigsResponse(), 0);
        checkRequest(createDescribeConfigsRequest(1));
        checkRequest(createDescribeConfigsRequestWithConfigEntries(1));
        checkErrorResponse(createDescribeConfigsRequest(1), new UnknownServerException());
        checkResponse(createDescribeConfigsResponse(), 1);
        checkDescribeConfigsResponseVersions();
        checkRequest(createCreatePartitionsRequest());
        checkRequest(createCreatePartitionsRequestWithAssignments());
        checkErrorResponse(createCreatePartitionsRequest(), new InvalidTopicException());
        checkResponse(createCreatePartitionsResponse(), 0);
        checkRequest(createCreateTokenRequest());
        checkErrorResponse(createCreateTokenRequest(), new UnknownServerException());
        checkResponse(createCreateTokenResponse(), 0);
        checkRequest(createDescribeTokenRequest());
        checkErrorResponse(createDescribeTokenRequest(), new UnknownServerException());
        checkResponse(createDescribeTokenResponse(), 0);
        checkRequest(createExpireTokenRequest());
        checkErrorResponse(createExpireTokenRequest(), new UnknownServerException());
        checkResponse(createExpireTokenResponse(), 0);
        checkRequest(createRenewTokenRequest());
        checkErrorResponse(createRenewTokenRequest(), new UnknownServerException());
        checkResponse(createRenewTokenResponse(), 0);
        checkRequest(createElectPreferredLeadersRequest());
        checkRequest(createElectPreferredLeadersRequestNullPartitions());
        checkErrorResponse(createElectPreferredLeadersRequest(), new UnknownServerException());
        checkResponse(createElectPreferredLeadersResponse(), 0);
    }

    @Test
    public void testResponseHeader() {
        ResponseHeader header = createResponseHeader();
        ByteBuffer buffer = TestUtils.toBuffer(header.toStruct());
        ResponseHeader deserialized = ResponseHeader.parse(buffer);
        Assert.assertEquals(header.correlationId(), deserialized.correlationId());
    }

    @Test(expected = UnsupportedVersionException.class)
    public void cannotUseFindCoordinatorV0ToFindTransactionCoordinator() {
        FindCoordinatorRequest.Builder builder = new FindCoordinatorRequest.Builder(CoordinatorType.TRANSACTION, "foobar");
        builder.build(((short) (0)));
    }

    @Test
    public void produceRequestToStringTest() {
        ProduceRequest request = createProduceRequest(PRODUCE.latestVersion());
        Assert.assertEquals(1, request.partitionRecordsOrFail().size());
        Assert.assertFalse(request.toString(false).contains("partitionSizes"));
        Assert.assertTrue(request.toString(false).contains("numPartitions=1"));
        Assert.assertTrue(request.toString(true).contains("partitionSizes"));
        Assert.assertFalse(request.toString(true).contains("numPartitions"));
        request.clearPartitionRecords();
        try {
            request.partitionRecordsOrFail();
            Assert.fail("partitionRecordsOrFail should fail after clearPartitionRecords()");
        } catch (IllegalStateException e) {
            // OK
        }
        // `toString` should behave the same after `clearPartitionRecords`
        Assert.assertFalse(request.toString(false).contains("partitionSizes"));
        Assert.assertTrue(request.toString(false).contains("numPartitions=1"));
        Assert.assertTrue(request.toString(true).contains("partitionSizes"));
        Assert.assertFalse(request.toString(true).contains("numPartitions"));
    }

    @Test
    public void produceRequestGetErrorResponseTest() {
        ProduceRequest request = createProduceRequest(PRODUCE.latestVersion());
        Set<TopicPartition> partitions = new java.util.HashSet(request.partitionRecordsOrFail().keySet());
        ProduceResponse errorResponse = ((ProduceResponse) (request.getErrorResponse(new NotEnoughReplicasException())));
        Assert.assertEquals(partitions, errorResponse.responses().keySet());
        ProduceResponse.PartitionResponse partitionResponse = errorResponse.responses().values().iterator().next();
        Assert.assertEquals(NOT_ENOUGH_REPLICAS, partitionResponse.error);
        Assert.assertEquals(INVALID_OFFSET, partitionResponse.baseOffset);
        Assert.assertEquals(NO_TIMESTAMP, partitionResponse.logAppendTime);
        request.clearPartitionRecords();
        // `getErrorResponse` should behave the same after `clearPartitionRecords`
        errorResponse = ((ProduceResponse) (request.getErrorResponse(new NotEnoughReplicasException())));
        Assert.assertEquals(partitions, errorResponse.responses().keySet());
        partitionResponse = errorResponse.responses().values().iterator().next();
        Assert.assertEquals(NOT_ENOUGH_REPLICAS, partitionResponse.error);
        Assert.assertEquals(INVALID_OFFSET, partitionResponse.baseOffset);
        Assert.assertEquals(NO_TIMESTAMP, partitionResponse.logAppendTime);
    }

    @Test
    public void produceResponseV5Test() {
        Map<TopicPartition, ProduceResponse.PartitionResponse> responseData = new HashMap<>();
        TopicPartition tp0 = new TopicPartition("test", 0);
        responseData.put(tp0, new ProduceResponse.PartitionResponse(Errors.NONE, 10000, RecordBatch.NO_TIMESTAMP, 100));
        ProduceResponse v5Response = new ProduceResponse(responseData, 10);
        short version = 5;
        ByteBuffer buffer = v5Response.serialize(version, new ResponseHeader(0));
        buffer.rewind();
        ResponseHeader.parse(buffer);// throw away.

        Struct deserializedStruct = PRODUCE.parseResponse(version, buffer);
        ProduceResponse v5FromBytes = ((ProduceResponse) (AbstractResponse.parseResponse(PRODUCE, deserializedStruct, version)));
        Assert.assertEquals(1, v5FromBytes.responses().size());
        Assert.assertTrue(v5FromBytes.responses().containsKey(tp0));
        ProduceResponse.PartitionResponse partitionResponse = v5FromBytes.responses().get(tp0);
        Assert.assertEquals(100, partitionResponse.logStartOffset);
        Assert.assertEquals(10000, partitionResponse.baseOffset);
        Assert.assertEquals(10, v5FromBytes.throttleTimeMs());
        Assert.assertEquals(responseData, v5Response.responses());
    }

    @Test
    public void produceResponseVersionTest() {
        Map<TopicPartition, ProduceResponse.PartitionResponse> responseData = new HashMap<>();
        responseData.put(new TopicPartition("test", 0), new ProduceResponse.PartitionResponse(Errors.NONE, 10000, RecordBatch.NO_TIMESTAMP, 100));
        ProduceResponse v0Response = new ProduceResponse(responseData);
        ProduceResponse v1Response = new ProduceResponse(responseData, 10);
        ProduceResponse v2Response = new ProduceResponse(responseData, 10);
        Assert.assertEquals("Throttle time must be zero", 0, v0Response.throttleTimeMs());
        Assert.assertEquals("Throttle time must be 10", 10, v1Response.throttleTimeMs());
        Assert.assertEquals("Throttle time must be 10", 10, v2Response.throttleTimeMs());
        Assert.assertEquals("Should use schema version 0", PRODUCE.responseSchema(((short) (0))), v0Response.toStruct(((short) (0))).schema());
        Assert.assertEquals("Should use schema version 1", PRODUCE.responseSchema(((short) (1))), v1Response.toStruct(((short) (1))).schema());
        Assert.assertEquals("Should use schema version 2", PRODUCE.responseSchema(((short) (2))), v2Response.toStruct(((short) (2))).schema());
        Assert.assertEquals("Response data does not match", responseData, v0Response.responses());
        Assert.assertEquals("Response data does not match", responseData, v1Response.responses());
        Assert.assertEquals("Response data does not match", responseData, v2Response.responses());
    }

    @Test
    public void fetchResponseVersionTest() {
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> responseData = new LinkedHashMap<>();
        MemoryRecords records = MemoryRecords.readableRecords(ByteBuffer.allocate(10));
        responseData.put(new TopicPartition("test", 0), new FetchResponse.PartitionData<>(Errors.NONE, 1000000, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, records));
        FetchResponse<MemoryRecords> v0Response = new FetchResponse(Errors.NONE, responseData, 0, FetchMetadata.INVALID_SESSION_ID);
        FetchResponse<MemoryRecords> v1Response = new FetchResponse(Errors.NONE, responseData, 10, FetchMetadata.INVALID_SESSION_ID);
        Assert.assertEquals("Throttle time must be zero", 0, v0Response.throttleTimeMs());
        Assert.assertEquals("Throttle time must be 10", 10, v1Response.throttleTimeMs());
        Assert.assertEquals("Should use schema version 0", FETCH.responseSchema(((short) (0))), v0Response.toStruct(((short) (0))).schema());
        Assert.assertEquals("Should use schema version 1", FETCH.responseSchema(((short) (1))), v1Response.toStruct(((short) (1))).schema());
        Assert.assertEquals("Response data does not match", responseData, v0Response.responseData());
        Assert.assertEquals("Response data does not match", responseData, v1Response.responseData());
    }

    @Test
    public void testFetchResponseV4() {
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> responseData = new LinkedHashMap<>();
        MemoryRecords records = MemoryRecords.readableRecords(ByteBuffer.allocate(10));
        List<FetchResponse.AbortedTransaction> abortedTransactions = Arrays.asList(new FetchResponse.AbortedTransaction(10, 100), new FetchResponse.AbortedTransaction(15, 50));
        responseData.put(new TopicPartition("bar", 0), new FetchResponse.PartitionData<>(Errors.NONE, 100000, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, abortedTransactions, records));
        responseData.put(new TopicPartition("bar", 1), new FetchResponse.PartitionData<>(Errors.NONE, 900000, 5, FetchResponse.INVALID_LOG_START_OFFSET, null, records));
        responseData.put(new TopicPartition("foo", 0), new FetchResponse.PartitionData<>(Errors.NONE, 70000, 6, FetchResponse.INVALID_LOG_START_OFFSET, Collections.emptyList(), records));
        FetchResponse<MemoryRecords> response = new FetchResponse(Errors.NONE, responseData, 10, FetchMetadata.INVALID_SESSION_ID);
        FetchResponse deserialized = FetchResponse.parse(TestUtils.toBuffer(response.toStruct(((short) (4)))), ((short) (4)));
        Assert.assertEquals(responseData, deserialized.responseData());
    }

    @Test
    public void verifyFetchResponseFullWrites() throws Exception {
        verifyFetchResponseFullWrite(FETCH.latestVersion(), createFetchResponse(123));
        verifyFetchResponseFullWrite(FETCH.latestVersion(), createFetchResponse(FETCH_SESSION_ID_NOT_FOUND, 123));
        for (short version = 0; version <= (FETCH.latestVersion()); version++) {
            verifyFetchResponseFullWrite(version, createFetchResponse());
        }
    }

    @Test
    public void testControlledShutdownResponse() {
        ControlledShutdownResponse response = createControlledShutdownResponse();
        short version = CONTROLLED_SHUTDOWN.latestVersion();
        Struct struct = response.toStruct(version);
        ByteBuffer buffer = TestUtils.toBuffer(struct);
        ControlledShutdownResponse deserialized = ControlledShutdownResponse.parse(buffer, version);
        Assert.assertEquals(response.error(), deserialized.error());
        Assert.assertEquals(response.partitionsRemaining(), deserialized.partitionsRemaining());
    }

    @Test(expected = UnsupportedVersionException.class)
    public void testCreateTopicRequestV0FailsIfValidateOnly() {
        createCreateTopicRequest(0, true);
    }

    @Test
    public void testFetchRequestMaxBytesOldVersions() throws Exception {
        final short version = 1;
        FetchRequest fr = createFetchRequest(version);
        FetchRequest fr2 = new FetchRequest(fr.toStruct(), version);
        Assert.assertEquals(fr2.maxBytes(), fr.maxBytes());
    }

    @Test
    public void testFetchRequestIsolationLevel() throws Exception {
        FetchRequest request = createFetchRequest(4, READ_COMMITTED);
        Struct struct = request.toStruct();
        FetchRequest deserialized = ((FetchRequest) (deserialize(request, struct, request.version())));
        Assert.assertEquals(request.isolationLevel(), deserialized.isolationLevel());
        request = createFetchRequest(4, READ_UNCOMMITTED);
        struct = request.toStruct();
        deserialized = ((FetchRequest) (deserialize(request, struct, request.version())));
        Assert.assertEquals(request.isolationLevel(), deserialized.isolationLevel());
    }

    @Test
    public void testFetchRequestWithMetadata() throws Exception {
        FetchRequest request = createFetchRequest(4, READ_COMMITTED);
        Struct struct = request.toStruct();
        FetchRequest deserialized = ((FetchRequest) (deserialize(request, struct, request.version())));
        Assert.assertEquals(request.isolationLevel(), deserialized.isolationLevel());
        request = createFetchRequest(4, READ_UNCOMMITTED);
        struct = request.toStruct();
        deserialized = ((FetchRequest) (deserialize(request, struct, request.version())));
        Assert.assertEquals(request.isolationLevel(), deserialized.isolationLevel());
    }

    @Test
    public void testJoinGroupRequestVersion0RebalanceTimeout() {
        final short version = 0;
        JoinGroupRequest jgr = createJoinGroupRequest(version);
        JoinGroupRequest jgr2 = new JoinGroupRequest(jgr.toStruct(), version);
        Assert.assertEquals(jgr2.rebalanceTimeout(), jgr.rebalanceTimeout());
    }

    @Test
    public void testOffsetFetchRequestBuilderToString() {
        String allTopicPartitionsString = OffsetFetchRequest.Builder.allTopicPartitions("someGroup").toString();
        Assert.assertTrue(allTopicPartitionsString.contains("<ALL>"));
        String string = new OffsetFetchRequest.Builder("group1", Collections.singletonList(new TopicPartition("test11", 1))).toString();
        Assert.assertTrue(string.contains("test11"));
        Assert.assertTrue(string.contains("group1"));
    }
}

