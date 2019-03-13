package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreateVertexStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testInsertSet() {
        String className = "testInsertSet";
        OSchema schema = OCreateVertexStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className, schema.getClass("V"));
        OResultSet result = OCreateVertexStatementExecutionTest.db.command((("create vertex " + className) + " set name = 'name1'"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result = OCreateVertexStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testInsertSetNoVertex() {
        String className = "testInsertSetNoVertex";
        OSchema schema = OCreateVertexStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className);
        try {
            OResultSet result = OCreateVertexStatementExecutionTest.db.command((("create vertex " + className) + " set name = 'name1'"));
            Assert.fail();
        } catch (OCommandExecutionException e1) {
        } catch (Exception e2) {
            Assert.fail();
        }
    }

    @Test
    public void testInsertValue() {
        String className = "testInsertValue";
        OSchema schema = OCreateVertexStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className, schema.getClass("V"));
        OResultSet result = OCreateVertexStatementExecutionTest.db.command((("create vertex " + className) + "  (name, surname) values ('name1', 'surname1')"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
            Assert.assertEquals("surname1", item.getProperty("surname"));
        }
        Assert.assertFalse(result.hasNext());
        result = OCreateVertexStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testInsertValue2() {
        String className = "testInsertValue2";
        OSchema schema = OCreateVertexStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className, schema.getClass("V"));
        OResultSet result = OCreateVertexStatementExecutionTest.db.command((("create vertex " + className) + "  (name, surname) values ('name1', 'surname1'), ('name2', 'surname2')"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals(("name" + (i + 1)), item.getProperty("name"));
            Assert.assertEquals(("surname" + (i + 1)), item.getProperty("surname"));
        }
        Assert.assertFalse(result.hasNext());
        Set<String> names = new HashSet<>();
        names.add("name1");
        names.add("name2");
        result = OCreateVertexStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertNotNull(item.getProperty("name"));
            names.remove(item.getProperty("name"));
            Assert.assertNotNull(item.getProperty("surname"));
        }
        Assert.assertFalse(result.hasNext());
        Assert.assertTrue(names.isEmpty());
        result.close();
    }

    @Test
    public void testContent() {
        String className = "testContent";
        OSchema schema = OCreateVertexStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className, schema.getClass("V"));
        OResultSet result = OCreateVertexStatementExecutionTest.db.command((("create vertex " + className) + " content {'name':'name1', 'surname':'surname1'}"));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
        }
        Assert.assertFalse(result.hasNext());
        result = OCreateVertexStatementExecutionTest.db.query(("select from " + className));
        for (int i = 0; i < 1; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotNull(item);
            Assert.assertEquals("name1", item.getProperty("name"));
            Assert.assertEquals("surname1", item.getProperty("surname"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

