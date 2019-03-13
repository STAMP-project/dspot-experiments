package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreateClassStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlain";
        OResultSet result = OCreateClassStatementExecutionTest.db.command(("create class " + className));
        OSchema schema = OCreateClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        Assert.assertFalse(clazz.isAbstract());
        result.close();
    }

    @Test
    public void testAbstract() {
        String className = "testAbstract";
        OResultSet result = OCreateClassStatementExecutionTest.db.command((("create class " + className) + " abstract "));
        OSchema schema = OCreateClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        Assert.assertTrue(clazz.isAbstract());
        result.close();
    }

    @Test
    public void testCluster() {
        String className = "testCluster";
        OResultSet result = OCreateClassStatementExecutionTest.db.command((("create class " + className) + " cluster 1235, 1236, 1255"));
        OSchema schema = OCreateClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        Assert.assertFalse(clazz.isAbstract());
        Assert.assertEquals(3, clazz.getClusterIds().length);
        result.close();
    }

    @Test
    public void testClusters() {
        String className = "testClusters";
        OResultSet result = OCreateClassStatementExecutionTest.db.command((("create class " + className) + " clusters 32"));
        OSchema schema = OCreateClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        Assert.assertFalse(clazz.isAbstract());
        Assert.assertEquals(32, clazz.getClusterIds().length);
        result.close();
    }

    @Test
    public void testIfNotExists() {
        String className = "testIfNotExists";
        OResultSet result = OCreateClassStatementExecutionTest.db.command((("create class " + className) + " if not exists"));
        OSchema schema = OCreateClassStatementExecutionTest.db.getMetadata().getSchema();
        OClass clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        result.close();
        result = OCreateClassStatementExecutionTest.db.command((("create class " + className) + " if not exists"));
        clazz = schema.getClass(className);
        Assert.assertNotNull(clazz);
        result.close();
    }
}

