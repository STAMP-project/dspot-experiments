package com.orientechnologies.orient.server.distributed.impl;


import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OLogSequenceNumber;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.distributed.ODistributedRequestId;
import com.orientechnologies.orient.server.distributed.impl.task.OTransactionPhase1Task;
import com.orientechnologies.orient.server.distributed.impl.task.OTransactionPhase2Task;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OTransactionPhase2TaskTest {
    private ODatabaseSession session;

    private OServer server;

    @Test
    public void testOkSecondPhase() throws Exception {
        OIdentifiable id = session.save(new ODocument("TestClass"));
        List<ORecordOperation> operations = new ArrayList<>();
        ODocument rec1 = new ODocument(id.getIdentity());
        rec1.setClassName("TestClass");
        rec1.field("one", "two");
        operations.add(new ORecordOperation(rec1, ORecordOperation.UPDATED));
        OTransactionPhase1Task task = new OTransactionPhase1Task(operations);
        task.execute(new ODistributedRequestId(10, 20), server, null, ((ODatabaseDocumentInternal) (session)));
        OTransactionPhase2Task task2 = new OTransactionPhase2Task(new ODistributedRequestId(10, 20), true, new int[]{ rec1.getIdentity().getClusterId() }, new OLogSequenceNumber(0, 1));
        task2.execute(new ODistributedRequestId(10, 21), server, null, ((ODatabaseDocumentInternal) (session)));
        Assert.assertEquals(2, session.load(id.getIdentity()).getVersion());
    }
}

