package com.orientechnologies.orient.server.distributed.impl.coordinator.transaction;


import OClass.INDEX_TYPE.UNIQUE;
import OTransactionFirstPhaseOperation.useDeltas;
import OTransactionFirstPhaseResult.Type.CONCURRENT_MODIFICATION_EXCEPTION;
import OTransactionFirstPhaseResult.Type.SUCCESS;
import OTransactionFirstPhaseResult.Type.UNIQUE_KEY_VIOLATION;
import OType.STRING;
import com.orientechnologies.orient.client.remote.message.tx.ORecordOperationRequest;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.tx.OTransactionIndexChanges;
import com.orientechnologies.orient.core.tx.OTransactionOptimistic;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.distributed.impl.coordinator.ONodeResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class FirstPhaseOperationTest {
    private OrientDB orientDB;

    private OServer server;

    @Test
    public void testExecuteSuccess() {
        List<ORecordOperationRequest> networkOps;
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement ele = session.newElement("simple");
            ele.setProperty("one", "val");
            session.save(ele);
            Collection<ORecordOperation> txOps = getRecordOperations();
            networkOps = OTransactionSubmit.genOps(txOps, useDeltas);
        }
        OTransactionFirstPhaseOperation ops = new OTransactionFirstPhaseOperation(new OSessionOperationId(), networkOps, new ArrayList());
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            ONodeResponse res = ops.execute(null, null, null, ((ODatabaseDocumentInternal) (session)));
            Assert.assertEquals(getType(), SUCCESS);
        }
    }

    @Test
    public void testConcurrentModification() {
        List<ORecordOperationRequest> networkOps;
        ORID id;
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement ele = session.newElement("simple");
            ele.setProperty("one", "val");
            id = session.save(ele).getIdentity();
            session.commit();
        }
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement ele = session.load(id);
            ele.setProperty("one", "val10");
            session.save(ele);
            Collection<ORecordOperation> txOps = getRecordOperations();
            networkOps = OTransactionSubmit.genOps(txOps, useDeltas);
        }
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement ele = session.load(id);
            ele.setProperty("one", "val11");
            session.save(ele);
            session.commit();
        }
        OTransactionFirstPhaseOperation ops = new OTransactionFirstPhaseOperation(new OSessionOperationId(), networkOps, new ArrayList());
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            ONodeResponse res = ops.execute(null, null, null, ((ODatabaseDocumentInternal) (session)));
            Assert.assertEquals(getType(), CONCURRENT_MODIFICATION_EXCEPTION);
        }
    }

    @Test
    public void testDuplicateKey() {
        List<ORecordOperationRequest> networkOps;
        List<OIndexOperationRequest> indexes;
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            OProperty pro = session.getClass("simple").createProperty("indexed", STRING);
            pro.createIndex(UNIQUE);
            session.begin();
            OElement ele = session.newElement("simple");
            ele.setProperty("indexed", "val");
            session.save(ele);
            session.commit();
        }
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            session.begin();
            OElement ele = session.newElement("simple");
            ele.setProperty("indexed", "val");
            session.save(ele);
            OTransactionOptimistic tx = ((OTransactionOptimistic) (session.getTransaction()));
            Collection<ORecordOperation> txOps = tx.getRecordOperations();
            networkOps = OTransactionSubmit.genOps(txOps, useDeltas);
            Map<String, OTransactionIndexChanges> indexOperations = tx.getIndexOperations();
            indexes = OTransactionSubmit.genIndexes(indexOperations, tx);
        }
        OTransactionFirstPhaseOperation ops = new OTransactionFirstPhaseOperation(new OSessionOperationId(), networkOps, indexes);
        try (ODatabaseSession session = orientDB.open(FirstPhaseOperationTest.class.getSimpleName(), "admin", "admin")) {
            ONodeResponse res = ops.execute(null, null, null, ((ODatabaseDocumentInternal) (session)));
            Assert.assertEquals(getType(), UNIQUE_KEY_VIOLATION);
        }
    }
}

