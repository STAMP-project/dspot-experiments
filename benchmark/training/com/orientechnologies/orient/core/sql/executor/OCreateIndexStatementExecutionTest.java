package com.orientechnologies.orient.core.sql.executor;


import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreateIndexStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlain";
        OClass clazz = OCreateIndexStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        clazz.createProperty("name", STRING);
        Assert.assertNull(OCreateIndexStatementExecutionTest.db.getMetadata().getIndexManager().getIndex((className + ".name")));
        OResultSet result = OCreateIndexStatementExecutionTest.db.command((((("create index " + className) + ".name on ") + className) + " (name) notunique"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertFalse(result.hasNext());
        Assert.assertNotNull(next);
        result.close();
        OIndex<?> idx = OCreateIndexStatementExecutionTest.db.getMetadata().getIndexManager().getIndex((className + ".name"));
        Assert.assertNotNull(idx);
        Assert.assertFalse(idx.isUnique());
    }

    @Test
    public void testIfNotExists() {
        String className = "testIfNotExists";
        OClass clazz = OCreateIndexStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        clazz.createProperty("name", STRING);
        Assert.assertNull(OCreateIndexStatementExecutionTest.db.getMetadata().getIndexManager().getIndex((className + ".name")));
        OResultSet result = OCreateIndexStatementExecutionTest.db.command((((("create index " + className) + ".name IF NOT EXISTS on ") + className) + " (name) notunique"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertFalse(result.hasNext());
        Assert.assertNotNull(next);
        result.close();
        OIndex<?> idx = OCreateIndexStatementExecutionTest.db.getMetadata().getIndexManager().getIndex((className + ".name"));
        Assert.assertNotNull(idx);
        Assert.assertFalse(idx.isUnique());
        result = OCreateIndexStatementExecutionTest.db.command((((("create index " + className) + ".name IF NOT EXISTS on ") + className) + " (name) notunique"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

