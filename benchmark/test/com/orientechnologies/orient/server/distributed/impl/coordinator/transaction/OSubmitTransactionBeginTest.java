package com.orientechnologies.orient.server.distributed.impl.coordinator.transaction;


import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.distributed.impl.coordinator.MockOperationLog;
import com.orientechnologies.orient.server.distributed.impl.coordinator.lock.ODistributedLockManagerImpl;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralNodeRequest;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralNodeResponse;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralSubmitRequest;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralSubmitResponse;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class OSubmitTransactionBeginTest {
    @Test
    public void testBegin() throws InterruptedException {
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), new MockOperationLog(), new ODistributedLockManagerImpl(), new OMockAllocator());
        OSubmitTransactionBeginTest.MockDistributedChannel cOne = new OSubmitTransactionBeginTest.MockDistributedChannel();
        ODistributedMember mOne = new ODistributedMember("one", null, cOne);
        coordinator.join(mOne);
        OSubmitTransactionBeginTest.MockDistributedChannel cTwo = new OSubmitTransactionBeginTest.MockDistributedChannel();
        ODistributedMember mTwo = new ODistributedMember("two", null, cTwo);
        coordinator.join(mTwo);
        OSubmitTransactionBeginTest.MockDistributedChannel cThree = new OSubmitTransactionBeginTest.MockDistributedChannel();
        ODistributedMember mThree = new ODistributedMember("three", null, cThree);
        coordinator.join(mThree);
        ArrayList<ORecordOperation> recordOps = new ArrayList<>();
        ORecordOperation op = new ORecordOperation(new ORecordId(10, 10), ORecordOperation.CREATED);
        op.setRecord(new ODocument("aaaa"));
        recordOps.add(op);
        coordinator.submit(mOne, new OSessionOperationId(), new OTransactionSubmit(recordOps, new ArrayList(), false));
        Assert.assertTrue(cOne.sentRequest.await(1, TimeUnit.SECONDS));
        Assert.assertTrue(cTwo.sentRequest.await(1, TimeUnit.SECONDS));
        Assert.assertTrue(cThree.sentRequest.await(1, TimeUnit.SECONDS));
    }

    private class MockDistributedChannel implements ODistributedChannel {
        private CountDownLatch sentRequest = new CountDownLatch(1);

        @Override
        public void sendRequest(String database, OLogId id, ONodeRequest nodeRequest) {
            sentRequest.countDown();
        }

        @Override
        public void sendResponse(String database, OLogId id, ONodeResponse nodeResponse) {
        }

        @Override
        public void sendResponse(OLogId opId, OStructuralNodeResponse response) {
        }

        @Override
        public void sendRequest(OLogId id, OStructuralNodeRequest request) {
        }

        @Override
        public void reply(OSessionOperationId operationId, OStructuralSubmitResponse response) {
        }

        @Override
        public void submit(OSessionOperationId operationId, OStructuralSubmitRequest request) {
        }

        @Override
        public void submit(String database, OSessionOperationId operationId, OSubmitRequest request) {
        }

        @Override
        public void reply(String database, OSessionOperationId operationId, OSubmitResponse response) {
        }
    }
}

