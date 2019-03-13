package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCommitStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testBegin() {
        Assert.assertTrue((((OCommitStatementExecutionTest.db.getTransaction()) == null) || (!(OCommitStatementExecutionTest.db.getTransaction().isActive()))));
        OCommitStatementExecutionTest.db.begin();
        Assert.assertFalse((((OCommitStatementExecutionTest.db.getTransaction()) == null) || (!(OCommitStatementExecutionTest.db.getTransaction().isActive()))));
        OResultSet result = OCommitStatementExecutionTest.db.command("commit");
        ExecutionPlanPrintUtils.printExecutionPlan(null, result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("commit", item.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        Assert.assertTrue((((OCommitStatementExecutionTest.db.getTransaction()) == null) || (!(OCommitStatementExecutionTest.db.getTransaction().isActive()))));
        OCommitStatementExecutionTest.db.commit();
    }
}

