package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OBeginStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testBegin() {
        Assert.assertTrue((((OBeginStatementExecutionTest.db.getTransaction()) == null) || (!(OBeginStatementExecutionTest.db.getTransaction().isActive()))));
        OResultSet result = OBeginStatementExecutionTest.db.command("begin");
        ExecutionPlanPrintUtils.printExecutionPlan(null, result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("begin", item.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        Assert.assertFalse((((OBeginStatementExecutionTest.db.getTransaction()) == null) || (!(OBeginStatementExecutionTest.db.getTransaction().isActive()))));
        OBeginStatementExecutionTest.db.commit();
    }
}

