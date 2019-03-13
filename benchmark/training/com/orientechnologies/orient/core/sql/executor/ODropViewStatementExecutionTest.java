package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODropViewStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlainClass";
        String viewName = "testPlain";
        OSchema schema = ODropViewStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className);
        schema.createView(viewName, ("SELECT FROM " + className));
        schema.reload();
        Assert.assertNotNull(schema.getView(viewName));
        OResultSet result = ODropViewStatementExecutionTest.db.command(("drop view " + viewName));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop view", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        schema.reload();
        Assert.assertNull(schema.getView(viewName));
    }

    @Test
    public void testIfExists() {
        String className = "testIfExistsClass";
        String viewName = "testIfExists";
        OSchema schema = ODropViewStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className);
        schema.createView(viewName, ("SELECT FROM " + className));
        schema.reload();
        Assert.assertNotNull(schema.getView(viewName));
        OResultSet result = ODropViewStatementExecutionTest.db.command((("drop view " + viewName) + " if exists"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop view", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        schema.reload();
        Assert.assertNull(schema.getView(viewName));
        result = ODropViewStatementExecutionTest.db.command((("drop view " + viewName) + " if exists"));
        result.close();
        schema.reload();
        Assert.assertNull(schema.getView(viewName));
    }
}

