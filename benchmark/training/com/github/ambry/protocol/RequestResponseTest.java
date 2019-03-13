package com.github.ambry.protocol;


import AdminRequestOrResponseType.CatchupStatus;
import BlobId.BlobDataType;
import BlobId.BlobIdType;
import BlobType.DataBlob;
import BlobType.MetadataBlob;
import DeleteRequest.DELETE_REQUEST_VERSION_1;
import GetResponse.CURRENT_VERSION;
import GetResponse.GET_RESPONSE_VERSION_V_3;
import GetResponse.GET_RESPONSE_VERSION_V_4;
import GetResponse.GET_RESPONSE_VERSION_V_5;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import ReplicaMetadataResponse.REPLICA_METADATA_RESPONSE_VERSION_V_4;
import ReplicaMetadataResponse.REPLICA_METADATA_RESPONSE_VERSION_V_5;
import ServerErrorCode.No_Error;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.clustermap.ClusterMapUtils;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.CommonTestUtils;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static AdminRequestOrResponseType.CatchupStatus;
import static DeleteRequest.DELETE_REQUEST_VERSION_1;
import static DeleteRequest.DELETE_REQUEST_VERSION_2;
import static TtlUpdateRequest.TTL_UPDATE_REQUEST_VERSION_1;


/**
 * Tests for different requests and responses in the protocol.
 */
public class RequestResponseTest {
    private static short versionSaved;

    @Test
    public void putRequestResponseTest() throws IOException {
        MockClusterMap clusterMap = new MockClusterMap();
        int correlationId = 5;
        String clientId = "client";
        BlobId blobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        byte[] userMetadata = new byte[50];
        RANDOM.nextBytes(userMetadata);
        int blobKeyLength = RANDOM.nextInt(4096);
        byte[] blobKey = new byte[blobKeyLength];
        RANDOM.nextBytes(blobKey);
        int blobSize = 100;
        byte[] blob = new byte[blobSize];
        RANDOM.nextBytes(blob);
        BlobProperties blobProperties = new BlobProperties(blobSize, "serviceID", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), RANDOM.nextBoolean(), null);
        testPutRequest(clusterMap, correlationId, clientId, blobId, blobProperties, userMetadata, DataBlob, blob, blobSize, blobKey);
        doTest(InvalidVersionPutRequest.Put_Request_Invalid_version, clusterMap, correlationId, clientId, blobId, blobProperties, userMetadata, DataBlob, blob, blobSize, blobKey, null);
        // Put Request with size in blob properties different from the data size and blob type: Data blob.
        blobProperties = new BlobProperties((blobSize * 10), "serviceID", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), RANDOM.nextBoolean(), null);
        testPutRequest(clusterMap, correlationId, clientId, blobId, blobProperties, userMetadata, DataBlob, blob, blobSize, blobKey);
        // Put Request with size in blob properties different from the data size and blob type: Metadata blob.
        blobProperties = new BlobProperties((blobSize * 10), "serviceID", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), RANDOM.nextBoolean(), null);
        testPutRequest(clusterMap, correlationId, clientId, blobId, blobProperties, userMetadata, MetadataBlob, blob, blobSize, blobKey);
        // Put Request with empty user metadata.
        byte[] emptyUserMetadata = new byte[0];
        blobProperties = new BlobProperties(blobSize, "serviceID", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), RANDOM.nextBoolean(), null);
        testPutRequest(clusterMap, correlationId, clientId, blobId, blobProperties, emptyUserMetadata, DataBlob, blob, blobSize, blobKey);
        // Response test
        PutResponse response = new PutResponse(1234, clientId, ServerErrorCode.No_Error);
        DataInputStream responseStream = serAndPrepForRead(response, (-1), false);
        PutResponse deserializedPutResponse = PutResponse.readFrom(responseStream);
        Assert.assertEquals(deserializedPutResponse.getCorrelationId(), 1234);
        Assert.assertEquals(deserializedPutResponse.getError(), No_Error);
    }

    @Test
    public void getRequestResponseTest() throws IOException {
        testGetRequestResponse(CURRENT_VERSION);
        testGetRequestResponse(GET_RESPONSE_VERSION_V_5);
        testGetRequestResponse(GET_RESPONSE_VERSION_V_4);
        testGetRequestResponse(GET_RESPONSE_VERSION_V_3);
    }

    @Test
    public void deleteRequestResponseTest() throws IOException {
        MockClusterMap clusterMap = new MockClusterMap();
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        BlobId id1 = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, accountId, containerId, clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        short[] versions = new short[]{ DELETE_REQUEST_VERSION_1, DELETE_REQUEST_VERSION_2 };
        for (short version : versions) {
            long deletionTimeMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
            int correlationId = RANDOM.nextInt();
            DeleteRequest deleteRequest;
            if (version == (DELETE_REQUEST_VERSION_1)) {
                deleteRequest = new RequestResponseTest.DeleteRequestV1(correlationId, "client", id1);
            } else {
                deleteRequest = new DeleteRequest(correlationId, "client", id1, deletionTimeMs);
            }
            DataInputStream requestStream = serAndPrepForRead(deleteRequest, (-1), true);
            DeleteRequest deserializedDeleteRequest = DeleteRequest.readFrom(requestStream, clusterMap);
            Assert.assertEquals(deserializedDeleteRequest.getClientId(), "client");
            Assert.assertEquals(deserializedDeleteRequest.getBlobId(), id1);
            if (version == (DELETE_REQUEST_VERSION_2)) {
                Assert.assertEquals("AccountId mismatch ", id1.getAccountId(), deserializedDeleteRequest.getAccountId());
                Assert.assertEquals("ContainerId mismatch ", id1.getContainerId(), deserializedDeleteRequest.getContainerId());
                Assert.assertEquals("DeletionTime mismatch ", deletionTimeMs, deserializedDeleteRequest.getDeletionTimeInMs());
            } else {
                Assert.assertEquals("AccountId mismatch ", accountId, deserializedDeleteRequest.getAccountId());
                Assert.assertEquals("ContainerId mismatch ", containerId, deserializedDeleteRequest.getContainerId());
                Assert.assertEquals("DeletionTime mismatch ", Infinite_Time, deserializedDeleteRequest.getDeletionTimeInMs());
            }
            DeleteResponse response = new DeleteResponse(correlationId, "client", ServerErrorCode.No_Error);
            requestStream = serAndPrepForRead(response, (-1), false);
            DeleteResponse deserializedDeleteResponse = DeleteResponse.readFrom(requestStream);
            Assert.assertEquals(deserializedDeleteResponse.getCorrelationId(), correlationId);
            Assert.assertEquals(deserializedDeleteResponse.getError(), No_Error);
        }
    }

    @Test
    public void replicaMetadataRequestTest() throws IOException {
        doReplicaMetadataRequestTest(ReplicaMetadataResponse.CURRENT_VERSION);
        doReplicaMetadataRequestTest(REPLICA_METADATA_RESPONSE_VERSION_V_4);
        doReplicaMetadataRequestTest(REPLICA_METADATA_RESPONSE_VERSION_V_5);
    }

    /**
     * Tests the ser/de of {@link AdminRequest} and {@link AdminResponse} and checks for equality of fields with
     * reference data.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void adminRequestResponseTest() throws IOException {
        int correlationId = 1234;
        String clientId = "client";
        for (AdminRequestOrResponseType type : AdminRequestOrResponseType.values()) {
            MockClusterMap clusterMap = new MockClusterMap();
            PartitionId id = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0);
            // with a valid partition id
            AdminRequest adminRequest = new AdminRequest(type, id, correlationId, clientId);
            DataInputStream requestStream = serAndPrepForRead(adminRequest, (-1), true);
            deserAdminRequestAndVerify(requestStream, clusterMap, correlationId, clientId, type, id);
            // with a null partition id
            adminRequest = new AdminRequest(type, null, correlationId, clientId);
            requestStream = serAndPrepForRead(adminRequest, (-1), true);
            deserAdminRequestAndVerify(requestStream, clusterMap, correlationId, clientId, type, null);
            // response
            ServerErrorCode[] values = ServerErrorCode.values();
            int indexToPick = RANDOM.nextInt(values.length);
            ServerErrorCode responseErrorCode = values[indexToPick];
            AdminResponse response = new AdminResponse(correlationId, clientId, responseErrorCode);
            DataInputStream responseStream = serAndPrepForRead(response, (-1), false);
            AdminResponse deserializedAdminResponse = AdminResponse.readFrom(responseStream);
            Assert.assertEquals(deserializedAdminResponse.getCorrelationId(), correlationId);
            Assert.assertEquals(deserializedAdminResponse.getClientId(), clientId);
            Assert.assertEquals(deserializedAdminResponse.getError(), responseErrorCode);
        }
    }

    /**
     * Tests the ser/de of {@link RequestControlAdminRequest} and checks for equality of fields with reference data.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void requestControlAdminRequestTest() throws IOException {
        for (RequestOrResponseType requestOrResponseType : RequestOrResponseType.values()) {
            doRequestControlAdminRequestTest(requestOrResponseType, true);
            doRequestControlAdminRequestTest(requestOrResponseType, false);
        }
    }

    /**
     * Tests the ser/de of {@link CatchupStatusAdminRequest} and checks for equality of fields with reference data.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void catchupStatusAdminRequestTest() throws IOException {
        MockClusterMap clusterMap = new MockClusterMap();
        PartitionId id = clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0);
        int correlationId = 1234;
        String clientId = "client";
        // request
        long acceptableLag = Utils.getRandomLong(RANDOM, 10000);
        short numCaughtUpPerPartition = Utils.getRandomShort(RANDOM);
        AdminRequest adminRequest = new AdminRequest(CatchupStatus, id, correlationId, clientId);
        CatchupStatusAdminRequest catchupStatusRequest = new CatchupStatusAdminRequest(acceptableLag, numCaughtUpPerPartition, adminRequest);
        DataInputStream requestStream = serAndPrepForRead(catchupStatusRequest, (-1), true);
        AdminRequest deserializedAdminRequest = deserAdminRequestAndVerify(requestStream, clusterMap, correlationId, clientId, CatchupStatus, id);
        CatchupStatusAdminRequest deserializedCatchupStatusRequest = CatchupStatusAdminRequest.readFrom(requestStream, deserializedAdminRequest);
        Assert.assertEquals("Acceptable lag not as set", acceptableLag, deserializedCatchupStatusRequest.getAcceptableLagInBytes());
        Assert.assertEquals("Num caught up per partition not as set", numCaughtUpPerPartition, deserializedCatchupStatusRequest.getNumReplicasCaughtUpPerPartition());
        // response
        boolean isCaughtUp = RANDOM.nextBoolean();
        ServerErrorCode[] values = ServerErrorCode.values();
        int indexToPick = RANDOM.nextInt(values.length);
        ServerErrorCode responseErrorCode = values[indexToPick];
        AdminResponse adminResponse = new AdminResponse(correlationId, clientId, responseErrorCode);
        CatchupStatusAdminResponse catchupStatusResponse = new CatchupStatusAdminResponse(isCaughtUp, adminResponse);
        DataInputStream responseStream = serAndPrepForRead(catchupStatusResponse, (-1), false);
        CatchupStatusAdminResponse deserializedCatchupStatusResponse = CatchupStatusAdminResponse.readFrom(responseStream);
        Assert.assertEquals(deserializedCatchupStatusResponse.getCorrelationId(), correlationId);
        Assert.assertEquals(deserializedCatchupStatusResponse.getClientId(), clientId);
        Assert.assertEquals(deserializedCatchupStatusResponse.getError(), responseErrorCode);
        Assert.assertEquals(deserializedCatchupStatusResponse.isCaughtUp(), isCaughtUp);
    }

    /**
     * Tests the ser/de of {@link BlobStoreControlAdminRequest} and checks for equality of fields with reference data.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void blobStoreControlAdminRequestTest() throws IOException {
        doBlobStoreControlAdminRequestTest(true);
        doBlobStoreControlAdminRequestTest(false);
    }

    /**
     * Tests the ser/de of {@link ReplicationControlAdminRequest} and checks for equality of fields with reference data.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void replicationControlAdminRequestTest() throws IOException {
        int numOrigins = (RANDOM.nextInt(8)) + 2;
        List<String> origins = new ArrayList<>();
        for (int i = 0; i < numOrigins; i++) {
            origins.add(UtilsTest.getRandomString(((RANDOM.nextInt(8)) + 2)));
        }
        doReplicationControlAdminRequestTest(origins, true);
        doReplicationControlAdminRequestTest(origins, false);
        doReplicationControlAdminRequestTest(Collections.EMPTY_LIST, true);
    }

    /**
     * Tests for {@link TtlUpdateRequest} and {@link TtlUpdateResponse}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void ttlUpdateRequestResponseTest() throws IOException {
        MockClusterMap clusterMap = new MockClusterMap();
        int correlationId = RANDOM.nextInt();
        long opTimeMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        long expiresAtMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        BlobId id1 = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, accountId, containerId, clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        short[] versions = new short[]{ TTL_UPDATE_REQUEST_VERSION_1 };
        for (short version : versions) {
            TtlUpdateRequest ttlUpdateRequest = new TtlUpdateRequest(correlationId, "client", id1, expiresAtMs, opTimeMs, version);
            DataInputStream requestStream = serAndPrepForRead(ttlUpdateRequest, (-1), true);
            TtlUpdateRequest deserializedTtlUpdateRequest = TtlUpdateRequest.readFrom(requestStream, clusterMap);
            Assert.assertEquals("Request type mismatch", RequestOrResponseType.TtlUpdateRequest, deserializedTtlUpdateRequest.getRequestType());
            Assert.assertEquals("Correlation ID mismatch", correlationId, deserializedTtlUpdateRequest.getCorrelationId());
            Assert.assertEquals("Client ID mismatch", "client", deserializedTtlUpdateRequest.getClientId());
            Assert.assertEquals("Blob ID mismatch", id1, deserializedTtlUpdateRequest.getBlobId());
            Assert.assertEquals("AccountId mismatch ", id1.getAccountId(), deserializedTtlUpdateRequest.getAccountId());
            Assert.assertEquals("ContainerId mismatch ", id1.getContainerId(), deserializedTtlUpdateRequest.getContainerId());
            Assert.assertEquals("ExpiresAtMs mismatch ", expiresAtMs, deserializedTtlUpdateRequest.getExpiresAtMs());
            Assert.assertEquals("DeletionTime mismatch ", opTimeMs, deserializedTtlUpdateRequest.getOperationTimeInMs());
            TtlUpdateResponse response = new TtlUpdateResponse(correlationId, "client", ServerErrorCode.No_Error);
            requestStream = serAndPrepForRead(response, (-1), false);
            TtlUpdateResponse deserializedTtlUpdateResponse = TtlUpdateResponse.readFrom(requestStream);
            Assert.assertEquals("Response type mismatch", RequestOrResponseType.TtlUpdateResponse, deserializedTtlUpdateResponse.getRequestType());
            Assert.assertEquals("Correlation ID mismatch", correlationId, deserializedTtlUpdateResponse.getCorrelationId());
            Assert.assertEquals("Client ID mismatch", "client", deserializedTtlUpdateResponse.getClientId());
            Assert.assertEquals("Server error code mismatch", No_Error, deserializedTtlUpdateResponse.getError());
        }
    }

    /**
     * Class representing {@link DeleteRequest} in version {@link DeleteRequest#DELETE_REQUEST_VERSION_1}
     */
    private class DeleteRequestV1 extends DeleteRequest {
        /**
         * Constructs {@link DeleteRequest} in {@link #DELETE_REQUEST_VERSION_1}
         *
         * @param correlationId
         * 		correlationId of the delete request
         * @param clientId
         * 		clientId of the delete request
         * @param blobId
         * 		blobId of the delete request
         */
        private DeleteRequestV1(int correlationId, String clientId, BlobId blobId) {
            super(correlationId, clientId, blobId, Infinite_Time, DELETE_REQUEST_VERSION_1);
        }
    }
}

