package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODropClassStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlain";
        OSchema schema = ODropClassStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className);
        schema.reload();
        Assert.assertNotNull(schema.getClass(className));
        OResultSet result = ODropClassStatementExecutionTest.db.command(("drop class " + className));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop class", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        schema.reload();
        Assert.assertNull(schema.getClass(className));
    }

    @Test
    public void testUnsafe() {
        String className = "testUnsafe";
        OSchema schema = ODropClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass v = schema.getClass("V");
        schema.createClass(className, v);
        ODropClassStatementExecutionTest.db.command((("insert into " + className) + " set foo = 'bar'"));
        try {
            OResultSet result = ODropClassStatementExecutionTest.db.command(("drop class " + className));
            Assert.fail();
        } catch (OCommandExecutionException ex1) {
        } catch (Exception ex2) {
            Assert.fail();
        }
        OResultSet result = ODropClassStatementExecutionTest.db.command((("drop class " + className) + " unsafe"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop class", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        schema.reload();
        Assert.assertNull(schema.getClass(className));
    }

    @Test
    public void testIfExists() {
        String className = "testIfExists";
        OSchema schema = ODropClassStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass(className);
        schema.reload();
        Assert.assertNotNull(schema.getClass(className));
        OResultSet result = ODropClassStatementExecutionTest.db.command((("drop class " + className) + " if exists"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop class", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        schema.reload();
        Assert.assertNull(schema.getClass(className));
        result = ODropClassStatementExecutionTest.db.command((("drop class " + className) + " if exists"));
        result.close();
        schema.reload();
        Assert.assertNull(schema.getClass(className));
    }
}

