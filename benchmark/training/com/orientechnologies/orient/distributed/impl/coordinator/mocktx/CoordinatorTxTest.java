package com.orientechnologies.orient.distributed.impl.coordinator.mocktx;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBInternal;
import com.orientechnologies.orient.distributed.impl.coordinator.MockOperationLog;
import com.orientechnologies.orient.distributed.impl.coordinator.transaction.OSessionOperationId;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralNodeRequest;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralNodeResponse;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralSubmitRequest;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralSubmitResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


public class CoordinatorTxTest {
    private OrientDB one;

    private OrientDB two;

    private OrientDB three;

    @Test
    public void testTxCoordinator() throws InterruptedException {
        ODistributedExecutor eOne = new ODistributedExecutor(Executors.newSingleThreadExecutor(), new MockOperationLog(), OrientDBInternal.extract(this.one), "none");
        ODistributedExecutor eTwo = new ODistributedExecutor(Executors.newSingleThreadExecutor(), new MockOperationLog(), OrientDBInternal.extract(this.two), "none");
        ODistributedExecutor eThree = new ODistributedExecutor(Executors.newSingleThreadExecutor(), new MockOperationLog(), OrientDBInternal.extract(this.three), "none");
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), new MockOperationLog(), null, null);
        CoordinatorTxTest.MemberChannel cOne = new CoordinatorTxTest.MemberChannel(eOne, coordinator);
        ODistributedMember mOne = new ODistributedMember("one", null, cOne);
        cOne.member = mOne;
        coordinator.join(mOne);
        CoordinatorTxTest.MemberChannel cTwo = new CoordinatorTxTest.MemberChannel(eOne, coordinator);
        ODistributedMember mTwo = new ODistributedMember("two", null, cTwo);
        cTwo.member = mTwo;
        coordinator.join(mTwo);
        CoordinatorTxTest.MemberChannel cThree = new CoordinatorTxTest.MemberChannel(eOne, coordinator);
        ODistributedMember mThree = new ODistributedMember("three", null, cThree);
        cThree.member = mThree;
        coordinator.join(mThree);
        OSubmitTx submit = new OSubmitTx();
        coordinator.submit(mOne, new OSessionOperationId(), submit);
        Assert.assertTrue(cOne.latch.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(submit.firstPhase);
        Assert.assertTrue(submit.secondPhase);
        eOne.close();
        eTwo.close();
        eThree.close();
        coordinator.close();
        Assert.assertEquals(cOne.callCount.get(), 0);
    }

    /**
     * This mock channel unify the channels in two nodes, in real implementation there would be two different channels on two
     * different nodes that would do the half of this job.
     */
    private static class MemberChannel implements ODistributedChannel {
        public com.orientechnologies.orient.distributed.impl.coordinator.ODistributedExecutor executor;

        public com.orientechnologies.orient.distributed.impl.coordinator.ODistributedCoordinator coordinator;

        public com.orientechnologies.orient.distributed.impl.coordinator.ODistributedMember member;

        public CountDownLatch latch = new CountDownLatch(1);

        private AtomicLong callCount = new AtomicLong(1);

        public MemberChannel(ODistributedExecutor executor, ODistributedCoordinator coordinator) {
            this.executor = executor;
            this.coordinator = coordinator;
        }

        @Override
        public void sendRequest(String database, OLogId id, ONodeRequest nodeRequest) {
            // Here in real case should be a network call and this method should be call on the other node
            executor.receive(member, id, nodeRequest);
        }

        @Override
        public void sendResponse(String database, OLogId id, ONodeResponse nodeResponse) {
            // This in real case should do a network call on the side of the executor node and this call should be in the coordinator node.
            coordinator.receive(member, id, nodeResponse);
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
            latch.countDown();
            callCount.decrementAndGet();
        }
    }
}

