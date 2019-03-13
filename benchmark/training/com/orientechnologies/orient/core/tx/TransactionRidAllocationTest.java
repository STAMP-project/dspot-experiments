package com.orientechnologies.orient.core.tx;


import ODatabaseType.MEMORY;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.exception.OConcurrentCreateException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 28/08/17.
 */
public class TransactionRidAllocationTest {
    private OrientDB orientDB;

    private ODatabaseDocumentInternal db;

    @Test
    public void testAllocation() {
        db.begin();
        OVertex v = db.newVertex("V");
        db.save(v);
        preallocateRids(((OTransactionInternal) (db.getTransaction())));
        ORID generated = v.getIdentity();
        Assert.assertTrue(generated.isValid());
        ODatabaseDocument db1 = orientDB.open("test", "admin", "admin");
        Assert.assertNull(db1.load(generated));
        db1.close();
    }

    @Test
    public void testAllocationCommit() {
        db.begin();
        OVertex v = db.newVertex("V");
        db.save(v);
        preallocateRids(((OTransactionInternal) (db.getTransaction())));
        ORID generated = v.getIdentity();
        commitPreAllocated(((OTransactionInternal) (db.getTransaction())));
        ODatabaseDocument db1 = orientDB.open("test", "admin", "admin");
        TestCase.assertNotNull(db1.load(generated));
        db1.close();
    }

    @Test
    public void testMultipleDbAllocationAndCommit() {
        ODatabaseDocumentInternal second;
        orientDB.create("secondTest", MEMORY);
        second = ((ODatabaseDocumentInternal) (orientDB.open("secondTest", "admin", "admin")));
        db.activateOnCurrentThread();
        db.begin();
        OVertex v = db.newVertex("V");
        db.save(v);
        preallocateRids(((OTransactionInternal) (db.getTransaction())));
        ORID generated = v.getIdentity();
        OTransaction transaction = db.getTransaction();
        second.activateOnCurrentThread();
        second.begin();
        OTransactionOptimistic transactionOptimistic = ((OTransactionOptimistic) (second.getTransaction()));
        for (ORecordOperation operation : transaction.getRecordOperations()) {
            transactionOptimistic.addRecord(operation.getRecord().copy(), operation.getType(), null);
        }
        ((OAbstractPaginatedStorage) (second.getStorage())).preallocateRids(transactionOptimistic);
        db.activateOnCurrentThread();
        commitPreAllocated(((OTransactionInternal) (db.getTransaction())));
        ODatabaseDocument db1 = orientDB.open("test", "admin", "admin");
        TestCase.assertNotNull(db1.load(generated));
        db1.close();
        second.activateOnCurrentThread();
        commitPreAllocated(((OTransactionInternal) (second.getTransaction())));
        second.close();
        ODatabaseDocument db2 = orientDB.open("secondTest", "admin", "admin");
        TestCase.assertNotNull(db2.load(generated));
        db2.close();
    }

    @Test(expected = OConcurrentCreateException.class)
    public void testMultipleDbAllocationNotAlignedFailure() {
        ODatabaseDocumentInternal second;
        orientDB.create("secondTest", MEMORY);
        second = ((ODatabaseDocumentInternal) (orientDB.open("secondTest", "admin", "admin")));
        db.activateOnCurrentThread();
        // THIS OFFSET FIRST DB FROM THE SECOND
        for (int i = 0; i < 20; i++) {
            db.save(db.newVertex("V"));
        }
        db.begin();
        OVertex v = db.newVertex("V");
        db.save(v);
        ((OAbstractPaginatedStorage) (db.getStorage())).preallocateRids(((OTransactionOptimistic) (db.getTransaction())));
        OTransaction transaction = db.getTransaction();
        second.activateOnCurrentThread();
        second.begin();
        OTransactionOptimistic transactionOptimistic = ((OTransactionOptimistic) (second.getTransaction()));
        for (ORecordOperation operation : transaction.getRecordOperations()) {
            transactionOptimistic.addRecord(operation.getRecord().copy(), operation.getType(), null);
        }
        ((OAbstractPaginatedStorage) (second.getStorage())).preallocateRids(transactionOptimistic);
    }

    @Test
    public void testAllocationMultipleCommit() {
        db.begin();
        List<ORecord> orecords = new ArrayList<>();
        OVertex v0 = db.newVertex("V");
        db.save(v0);
        for (int i = 0; i < 20; i++) {
            OVertex v = db.newVertex("V");
            OEdge edge = v0.addEdge(v);
            orecords.add(db.save(edge));
            orecords.add(db.save(v));
        }
        preallocateRids(((OTransactionInternal) (db.getTransaction())));
        List<ORID> allocated = new ArrayList<>();
        for (ORecord rec : orecords) {
            allocated.add(rec.getIdentity());
        }
        commitPreAllocated(((OTransactionInternal) (db.getTransaction())));
        ODatabaseDocument db1 = orientDB.open("test", "admin", "admin");
        for (ORID id : allocated) {
            TestCase.assertNotNull(db1.load(id));
        }
        db1.close();
    }
}

