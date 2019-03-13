package com.orientechnologies.orient.distributed.impl.coordinator.transaction;


import OTransactionFirstPhaseResult.Type.SUCCESS;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.tx.OTransactionIndexChanges;
import com.orientechnologies.orient.core.tx.OTransactionOptimistic;
import com.orientechnologies.orient.distributed.impl.OIncrementOperationalLog;
import com.orientechnologies.orient.distributed.impl.coordinator.ONodeRequest;
import com.orientechnologies.orient.distributed.impl.coordinator.lock.ODistributedLockManagerImpl;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralNodeRequest;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralNodeResponse;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralSubmitRequest;
import com.orientechnologies.orient.distributed.impl.structural.OStructuralSubmitResponse;
import com.orientechnologies.orient.server.OServer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


public class TestTransactionFlow {
    private OrientDB orientDB;

    private OServer server0;

    @Test
    public void testFlowWithIndexes() {
        Collection<ORecordOperation> txOps;
        List<OIndexOperationRequest> indexes;
        OTransactionSubmit submit;
        try (ODatabaseSession session = orientDB.open(TestTransactionFlow.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement el = session.newElement("test");
            el.setProperty("name", "john");
            session.save(el);
            OElement el1 = session.newElement("test");
            el.setProperty("link", el);
            session.save(el1);
            OTransactionOptimistic tx = ((OTransactionOptimistic) (session.getTransaction()));
            txOps = tx.getRecordOperations();
            Map<String, OTransactionIndexChanges> indexOperations = tx.getIndexOperations();
            indexes = OTransactionSubmit.genIndexes(indexOperations, tx);
            submit = new OTransactionSubmit(txOps, indexes, false);
        }
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), new OIncrementOperationalLog(), new ODistributedLockManagerImpl(), new OMockAllocator());
        TestTransactionFlow.RecordChannel channel = new TestTransactionFlow.RecordChannel();
        ODistributedMember member = new ODistributedMember("one", "test", channel);
        coordinator.join(member);
        submit.begin(member, new OSessionOperationId(), coordinator);
        OTransactionFirstPhaseOperation ops = ((OTransactionFirstPhaseOperation) (channel.fistPhase));
        try (ODatabaseSession session = orientDB.open(TestTransactionFlow.class.getSimpleName(), "admin", "admin")) {
            ONodeResponse res = ops.execute(null, null, null, ((ODatabaseDocumentInternal) (session)));
            Assert.assertEquals(getType(), SUCCESS);
        }
        OTransactionSecondPhaseOperation second = new OTransactionSecondPhaseOperation(ops.getOperationId(), true);
        try (ODatabaseSession session = orientDB.open(TestTransactionFlow.class.getSimpleName(), "admin", "admin")) {
            ONodeResponse res = second.execute(null, null, null, ((ODatabaseDocumentInternal) (session)));
            Assert.assertTrue(isSuccess());
        }
    }

    private static class RecordChannel implements ODistributedChannel {
        private ONodeRequest fistPhase;

        @Override
        public void submit(String database, OSessionOperationId operationId, OSubmitRequest request) {
        }

        @Override
        public void reply(String database, OSessionOperationId operationId, OSubmitResponse response) {
        }

        @Override
        public void sendRequest(String database, OLogId id, ONodeRequest nodeRequest) {
            this.fistPhase = nodeRequest;
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
    }
}

