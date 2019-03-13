package com.orientechnologies.orient.graph.blueprints;


import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;


public class GraphPolymorphicTest {
    private OrientGraph graph;

    @Test
    public void checkBrowsingAVertexClassAsEdgeClass() {
        Vertex v = graph.addVertex("class:TestVertex");
        try {
            graph.getEdge(v.getId()).getClass();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            graph.getEdgesOfClass("TestVertex").iterator().next().getClass();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void checkBrowsingAnUnknownClassAsEdgeClass() {
        Vertex v = graph.addVertex("class:TestVertex");
        try {
            graph.getEdge(v.getId()).getClass();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            graph.getEdgesOfClass("Unknown").iterator().next().getClass();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void checkBrowsingAnUnknownClassAsVertexClass() {
        Vertex v = graph.addVertex("class:TestVertex");
        try {
            graph.getVerticesOfClass("Unknown").iterator().next().getClass();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }
}

