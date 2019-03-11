package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODeleteVertexStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testDeleteSingleVertex() {
        String className = "testDeleteSingleVertex";
        ODeleteVertexStatementExecutionTest.db.createVertexClass(className);
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteVertexStatementExecutionTest.db.newVertex(className);
            v1.setProperty("name", ("a" + i));
            v1.save();
        }
        ODeleteVertexStatementExecutionTest.db.command((("DELETE VERTEX " + className) + " WHERE name = 'a3'")).close();
        OResultSet rs = ODeleteVertexStatementExecutionTest.db.query(("SELECT FROM " + className));
        Assert.assertEquals(9, rs.stream().count());
        rs.close();
    }

    @Test
    public void testDeleteAllVertices() {
        String className = "testDeleteAllVertices";
        ODeleteVertexStatementExecutionTest.db.createVertexClass(className);
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteVertexStatementExecutionTest.db.newVertex(className);
            v1.setProperty("name", ("a" + i));
            v1.save();
        }
        ODeleteVertexStatementExecutionTest.db.command(("DELETE VERTEX " + className)).close();
        OResultSet rs = ODeleteVertexStatementExecutionTest.db.query(("SELECT FROM " + className));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
    }

    @Test
    public void testFilterClass() {
        String className1 = "testDeleteAllVertices1";
        ODeleteVertexStatementExecutionTest.db.createVertexClass(className1);
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteVertexStatementExecutionTest.db.newVertex(className1);
            v1.setProperty("name", ("a" + i));
            v1.save();
        }
        String className2 = "testDeleteAllVertices2";
        ODeleteVertexStatementExecutionTest.db.createVertexClass(className2);
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteVertexStatementExecutionTest.db.newVertex(className2);
            v1.setProperty("name", ("a" + i));
            v1.save();
        }
        ODeleteVertexStatementExecutionTest.db.command(("DELETE VERTEX " + className1)).close();
        OResultSet rs = ODeleteVertexStatementExecutionTest.db.query(("SELECT FROM " + className1));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
        rs = ODeleteVertexStatementExecutionTest.db.query(("SELECT FROM " + className2));
        Assert.assertEquals(10, rs.stream().count());
        rs.close();
    }
}

