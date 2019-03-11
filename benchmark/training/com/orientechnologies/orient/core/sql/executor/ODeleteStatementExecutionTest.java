package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODeleteStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSimple() {
        String className = "testSimple";
        ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        for (int i = 0; i < 10; i++) {
            ODocument doc = ODeleteStatementExecutionTest.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.save();
        }
        OResultSet result = ODeleteStatementExecutionTest.db.command((("delete from  " + className) + " where name = 'name4'"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals(((Object) (1L)), item.getProperty("count"));
        }
        Assert.assertFalse(result.hasNext());
        result = ODeleteStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertNotEquals("name4", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testUnsafe1() {
        String className = "testUnsafe1";
        OClass v = ODeleteStatementExecutionTest.db.getMetadata().getSchema().getClass("V");
        if (v == null) {
            ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass("V");
        }
        ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass(className, v);
        for (int i = 0; i < 10; i++) {
            ODocument doc = ODeleteStatementExecutionTest.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.save();
        }
        try {
            OResultSet result = ODeleteStatementExecutionTest.db.command((("delete from  " + className) + " where name = 'name4'"));
            Assert.fail();
        } catch (OCommandExecutionException ex) {
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUnsafe2() {
        String className = "testUnsafe2";
        OClass v = ODeleteStatementExecutionTest.db.getMetadata().getSchema().getClass("V");
        if (v == null) {
            ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass("V");
        }
        ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass(className, v);
        for (int i = 0; i < 10; i++) {
            ODocument doc = ODeleteStatementExecutionTest.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.save();
        }
        OResultSet result = ODeleteStatementExecutionTest.db.command((("delete from  " + className) + " where name = 'name4' unsafe"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals(((Object) (1L)), item.getProperty("count"));
        }
        Assert.assertFalse(result.hasNext());
        result = ODeleteStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertNotEquals("name4", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testReturnBefore() {
        String className = "testReturnBefore";
        ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        for (int i = 0; i < 10; i++) {
            ODocument doc = ODeleteStatementExecutionTest.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.save();
        }
        OResultSet result = ODeleteStatementExecutionTest.db.command((("delete from  " + className) + " return before where name = 'name4' "));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name4", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result = ODeleteStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertNotEquals("name4", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testLimit() {
        String className = "testLimit";
        ODeleteStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        for (int i = 0; i < 10; i++) {
            ODocument doc = ODeleteStatementExecutionTest.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.save();
        }
        OResultSet result = ODeleteStatementExecutionTest.db.command((("delete from  " + className) + " limit 5"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals(((Object) (5L)), item.getProperty("count"));
        }
        Assert.assertFalse(result.hasNext());
        result = ODeleteStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

