package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.core.db.record.OIdentifiable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class OrientDeleteVertexTest {
    @Test
    public void shouldDeleteEdgesWhenDeletingVertex() {
        // Create a node v1 with at least two edges
        OrientGraph g = createGraph();
        OrientVertex v1 = g.addVertex("class:V1");
        OrientVertex v2 = g.addVertex("class:V2");
        OrientVertex v22 = g.addVertex("class:V2");
        g.addEdge(null, v1, v2, "edgeType1");
        g.addEdge(null, v1, v22, "edgeType1");
        g.shutdown();
        // delete connected vertex v2
        g = createGraph();
        long total = g.countVertices();
        g.getVertex(v2.getId()).remove();
        Assert.assertEquals((total - 1), g.countVertices());
        // the v1 out_edgeType1 property should not contain a reference to
        // deleted node v2
        // OK INSIDE THE TRANSACTION
        Iterable<OrientEdge> out_edge = g.getVertex(v1.getId()).getProperty("out_edgeType1");
        boolean contains = false;
        for (OIdentifiable id : out_edge)
            if (id.equals(v2.getId()))
                contains = true;


        Assert.assertFalse(contains);
        g.shutdown();
        // the v1 node should only have one edge left
        // OK
        Assert.assertEquals(1, getEdgeCount(v1.getId()));
        g = createGraph();
        // v2 vertex sould be deleted
        // OK
        Assert.assertNull(g.getVertex(v2.getId()));
        // the v1 out_edgeType1 property should not contain a reference to
        // deleted v2
        // FAILS HERE OUTSIDE OF THE TRANSACTION
        out_edge = g.getVertex(v1.getId()).getProperty("out_edgeType1");
        contains = false;
        for (OIdentifiable id : out_edge)
            if (id.equals(v2.getId()))
                contains = true;


        Assert.assertFalse(contains);
        g.shutdown();
    }
}

