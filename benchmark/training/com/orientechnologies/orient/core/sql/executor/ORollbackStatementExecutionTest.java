package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ORollbackStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testBegin() {
        Assert.assertTrue((((ORollbackStatementExecutionTest.db.getTransaction()) == null) || (!(ORollbackStatementExecutionTest.db.getTransaction().isActive()))));
        ORollbackStatementExecutionTest.db.begin();
        Assert.assertFalse((((ORollbackStatementExecutionTest.db.getTransaction()) == null) || (!(ORollbackStatementExecutionTest.db.getTransaction().isActive()))));
        OResultSet result = ORollbackStatementExecutionTest.db.command("rollback");
        ExecutionPlanPrintUtils.printExecutionPlan(null, result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("rollback", item.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        Assert.assertTrue((((ORollbackStatementExecutionTest.db.getTransaction()) == null) || (!(ORollbackStatementExecutionTest.db.getTransaction().isActive()))));
        ORollbackStatementExecutionTest.db.commit();
    }
}

