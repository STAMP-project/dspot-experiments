package com.orientechnologies.orient.graph.blueprints;


import OType.STRING;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;


public class BlueprintsTest {
    private static String DB_URL = "memory:" + (BlueprintsTest.class.getSimpleName());

    private static OrientGraph graph;

    public BlueprintsTest() {
    }

    @Test
    public void testSubVertex() {
        if ((BlueprintsTest.graph.getVertexType("SubVertex")) == null)
            BlueprintsTest.graph.createVertexType("SubVertex");

        Vertex v = BlueprintsTest.graph.addVertex("class:SubVertex");
        v.setProperty("key", "subtype");
        Assert.assertEquals(getRecord().getSchemaClass().getName(), "SubVertex");
        // TEST QUERY AGAINST SUB-TYPE IN TX
        Iterable<Vertex> vertices = labels("SubVertex").vertices();
        Assert.assertTrue(vertices.iterator().hasNext());
        Assert.assertEquals(vertices.iterator().next().getProperty("key"), "subtype");
        BlueprintsTest.graph.commit();
        // TEST QUERY AGAINST SUB-TYPE NON IN TX
        vertices = labels("SubVertex").vertices();
        Assert.assertTrue(vertices.iterator().hasNext());
        Assert.assertEquals(vertices.iterator().next().getProperty("key"), "subtype");
    }

    @Test
    public void testPolymorphicVertex() {
        if ((BlueprintsTest.graph.getVertexType("Node")) == null) {
            OrientVertexType node = BlueprintsTest.graph.createVertexType("Node");
            node.createProperty("name", STRING);
        }
        if ((BlueprintsTest.graph.getVertexType("SubNode")) == null) {
            OrientVertexType subNode = BlueprintsTest.graph.createVertexType("SubNode");
            subNode.addSuperClass(BlueprintsTest.graph.getVertexType("Node"));
        }
        BlueprintsTest.graph.begin();
        Vertex v = BlueprintsTest.graph.addVertex("class:SubNode");
        v.setProperty("name", "subtype");
        Assert.assertEquals(getRecord().getSchemaClass().getName(), "SubNode");
        // TEST QUERY AGAINST SUB-TYPE IN TX
        Iterable<Vertex> vertices = BlueprintsTest.graph.getVertices("Node.name", "subtype");
        Assert.assertTrue(vertices.iterator().hasNext());
        Assert.assertEquals(vertices.iterator().next().getProperty("name"), "subtype");
        BlueprintsTest.graph.commit();
        // TEST QUERY AGAINST SUB-TYPE NON IN TX
        vertices = BlueprintsTest.graph.getVertices("Node.name", "subtype");
        Assert.assertTrue(vertices.iterator().hasNext());
        Assert.assertEquals(vertices.iterator().next().getProperty("name"), "subtype");
    }

    @Test
    public void testSubEdge() {
        if ((BlueprintsTest.graph.getEdgeType("SubEdge")) == null)
            BlueprintsTest.graph.createEdgeType("SubEdge");

        if ((BlueprintsTest.graph.getVertexType("SubVertex")) == null)
            BlueprintsTest.graph.createVertexType("SubVertex");

        Vertex v1 = BlueprintsTest.graph.addVertex("class:SubVertex");
        v1.setProperty("key", "subtype+subedge");
        Assert.assertEquals(getRecord().getSchemaClass().getName(), "SubVertex");
        Vertex v2 = BlueprintsTest.graph.addVertex("class:SubVertex");
        v2.setProperty("key", "subtype+subedge");
        Assert.assertEquals(getRecord().getSchemaClass().getName(), "SubVertex");
        Edge e = BlueprintsTest.graph.addEdge("class:SubEdge", v1, v2, null);
        e.setProperty("key", "subedge");
        Assert.assertEquals(getRecord().getSchemaClass().getName(), "SubEdge");
        BlueprintsTest.graph.commit();
    }

    @Test
    public void testEdgePhysicalRemoval() {
        BlueprintsTest.graph.command(new OCommandSQL("delete from e where name = 'forceCreationOfDocument'")).execute();
        Vertex v1 = BlueprintsTest.graph.addVertex(null);
        Vertex v2 = BlueprintsTest.graph.addVertex(null);
        OrientEdge e = BlueprintsTest.graph.addEdge(null, v1, v2, "anyLabel");
        e.setProperty("key", "forceCreationOfDocument");
        Iterable<Edge> result = BlueprintsTest.graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Edge>("select from e where key = 'forceCreationOfDocument'")).execute();
        Assert.assertTrue(result.iterator().hasNext());
        Assert.assertTrue(((result.iterator().next()) instanceof Edge));
        e.remove();
        BlueprintsTest.graph.commit();
        result = BlueprintsTest.graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Edge>("select from e where key = 'forceCreationOfDocument'")).execute();
        Assert.assertFalse(result.iterator().hasNext());
    }

    @Test
    public void testQueryWithSpecialCharacters() {
        BlueprintsTest.graph.setAutoStartTx(false);
        BlueprintsTest.graph.addVertex(null).setProperty("name", "Jay");
        BlueprintsTest.graph.addVertex(null).setProperty("name", "Smith's");
        BlueprintsTest.graph.addVertex(null).setProperty("name", "Smith\"s");
        BlueprintsTest.graph.commit();// transaction not-reopened

        Assert.assertTrue(BlueprintsTest.graph.getVertices("name", "Jay").iterator().hasNext());
        Assert.assertTrue(BlueprintsTest.graph.getVertices("name", "Smith's").iterator().hasNext());
        Assert.assertTrue(BlueprintsTest.graph.getVertices("name", "Smith\"s").iterator().hasNext());
    }

    @Test
    public void testInvalidVertexRID() {
        OrientVertex v = BlueprintsTest.graph.getVertex(new ORecordId("9:9999"));
        System.out.println(v);
    }

    @Test
    public void testInvalidEdgeRID() {
        try {
            OrientEdge e = BlueprintsTest.graph.addEdge(null, new OrientVertex(BlueprintsTest.graph, new ORecordId("9:9999")), new OrientVertex(BlueprintsTest.graph, new ORecordId("9:99999")), "E");
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testSetEvenParams() {
        try {
            BlueprintsTest.graph.addVertex(null, "name", "Luca", "surname");
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistentRIDAfterCommit() {
        Vertex v = BlueprintsTest.graph.addVertex(null);
        v.setProperty("test", "value");
        BlueprintsTest.graph.commit();
        // System.out.println(v.getId());
        Assert.assertTrue(isPersistent());
    }
}

