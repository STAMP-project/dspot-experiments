package com.orientechnologies.orient.core.tx;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 02/01/17.
 */
public class TransactionChangesDetectionTest {
    private OrientDB factory;

    private ODatabaseDocument database;

    @Test
    public void testTransactionChangeTracking() {
        database.begin();
        OTransactionOptimistic currentTx = ((OTransactionOptimistic) (database.getTransaction()));
        database.save(new ODocument("test"));
        currentTx.resetChangesTracking();
        database.save(new ODocument("test"));
        Assert.assertTrue(currentTx.isChanged());
    }
}

