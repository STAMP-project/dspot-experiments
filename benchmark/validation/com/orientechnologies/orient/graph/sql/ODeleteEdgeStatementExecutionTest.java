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
public class ODeleteEdgeStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testDeleteSingleEdge() {
        String vertexClassName = "testDeleteSingleEdgeV";
        ODeleteEdgeStatementExecutionTest.db.createVertexClass(vertexClassName);
        String edgeClassName = "testDeleteSingleEdgeE";
        ODeleteEdgeStatementExecutionTest.db.createEdgeClass(edgeClassName);
        OVertex prev = null;
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteEdgeStatementExecutionTest.db.newVertex(vertexClassName);
            v1.setProperty("name", ("a" + i));
            v1.save();
            if (prev != null) {
                prev.addEdge(v1, edgeClassName);
            }
            prev = v1;
        }
        OResultSet rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(out()) FROM " + vertexClassName));
        Assert.assertEquals(9, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(in()) FROM " + vertexClassName));
        Assert.assertEquals(9, rs.stream().count());
        rs.close();
        ODeleteEdgeStatementExecutionTest.db.command((((((("DELETE EDGE " + edgeClassName) + " from (SELECT FROM ") + vertexClassName) + " where name = 'a1') to (SELECT FROM ") + vertexClassName) + " where name = 'a2')")).close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT FROM " + edgeClassName));
        Assert.assertEquals(8, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query((("SELECT expand(out()) FROM " + vertexClassName) + " where name = 'a1'"));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query((("SELECT expand(in()) FROM " + vertexClassName) + " where name = 'a2'"));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
    }

    @Test
    public void testDeleteAll() {
        String vertexClassName = "testDeleteAllV";
        ODeleteEdgeStatementExecutionTest.db.createVertexClass(vertexClassName);
        String edgeClassName = "testDeleteAllE";
        ODeleteEdgeStatementExecutionTest.db.createEdgeClass(edgeClassName);
        OVertex prev = null;
        for (int i = 0; i < 10; i++) {
            OVertex v1 = ODeleteEdgeStatementExecutionTest.db.newVertex(vertexClassName);
            v1.setProperty("name", ("a" + i));
            v1.save();
            if (prev != null) {
                prev.addEdge(v1, edgeClassName).save();
            }
            prev = v1;
        }
        OResultSet rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(out()) FROM " + vertexClassName));
        Assert.assertEquals(9, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(in()) FROM " + vertexClassName));
        Assert.assertEquals(9, rs.stream().count());
        rs.close();
        ODeleteEdgeStatementExecutionTest.db.command(("DELETE EDGE " + edgeClassName)).close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT FROM " + edgeClassName));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(out()) FROM " + vertexClassName));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
        rs = ODeleteEdgeStatementExecutionTest.db.query(("SELECT expand(in()) FROM " + vertexClassName));
        Assert.assertEquals(0, rs.stream().count());
        rs.close();
    }
}

