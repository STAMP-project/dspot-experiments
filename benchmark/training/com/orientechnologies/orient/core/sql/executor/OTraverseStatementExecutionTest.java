package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OTraverseStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlainTraverse() {
        String classPrefix = "testPlainTraverse_";
        OTraverseStatementExecutionTest.db.createVertexClass((classPrefix + "V"));
        OTraverseStatementExecutionTest.db.createEdgeClass((classPrefix + "E"));
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'a'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'b'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'c'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'd'")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'a') to (select from ") + classPrefix) + "V where name = 'b')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'b') to (select from ") + classPrefix) + "V where name = 'c')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'c') to (select from ") + classPrefix) + "V where name = 'd')")).close();
        OResultSet result = OTraverseStatementExecutionTest.db.query((("traverse out() from (select from " + classPrefix) + "V where name = 'a')"));
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals(i, item.getMetadata("$depth"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testWithDepth() {
        String classPrefix = "testWithDepth_";
        OTraverseStatementExecutionTest.db.createVertexClass((classPrefix + "V"));
        OTraverseStatementExecutionTest.db.createEdgeClass((classPrefix + "E"));
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'a'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'b'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'c'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'd'")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'a') to (select from ") + classPrefix) + "V where name = 'b')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'b') to (select from ") + classPrefix) + "V where name = 'c')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'c') to (select from ") + classPrefix) + "V where name = 'd')")).close();
        OResultSet result = OTraverseStatementExecutionTest.db.query((("traverse out() from (select from " + classPrefix) + "V where name = 'a') WHILE $depth < 2"));
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals(i, item.getMetadata("$depth"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testMaxDepth() {
        String classPrefix = "testMaxDepth";
        OTraverseStatementExecutionTest.db.createVertexClass((classPrefix + "V"));
        OTraverseStatementExecutionTest.db.createEdgeClass((classPrefix + "E"));
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'a'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'b'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'c'")).close();
        OTraverseStatementExecutionTest.db.command((("create vertex " + classPrefix) + "V set name = 'd'")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'a') to (select from ") + classPrefix) + "V where name = 'b')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'b') to (select from ") + classPrefix) + "V where name = 'c')")).close();
        OTraverseStatementExecutionTest.db.command((((((("create edge " + classPrefix) + "E from (select from ") + classPrefix) + "V where name = 'c') to (select from ") + classPrefix) + "V where name = 'd')")).close();
        OResultSet result = OTraverseStatementExecutionTest.db.query((("traverse out() from (select from " + classPrefix) + "V where name = 'a') MAXDEPTH 1"));
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals(i, item.getMetadata("$depth"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
        result = OTraverseStatementExecutionTest.db.query((("traverse out() from (select from " + classPrefix) + "V where name = 'a') MAXDEPTH 2"));
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals(i, item.getMetadata("$depth"));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

