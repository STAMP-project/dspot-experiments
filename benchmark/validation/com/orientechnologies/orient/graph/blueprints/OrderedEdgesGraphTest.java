package com.orientechnologies.orient.graph.blueprints;


import Direction.IN;
import Direction.OUT;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


public class OrderedEdgesGraphTest {
    private static String DB_URL = "memory:" + (OrderedEdgesGraphTest.class.getSimpleName());

    private static OrientGraph graph;

    private final OrientVertex mainPerson;

    public OrderedEdgesGraphTest() {
        OrderedEdgesGraphTest.graph = new OrientGraph(OrderedEdgesGraphTest.DB_URL);
        OrderedEdgesGraphTest.graph.setUseLightweightEdges(true);
        OrderedEdgesGraphTest.graph.setAutoStartTx(false);
        OrderedEdgesGraphTest.graph.commit();
        if ((OrderedEdgesGraphTest.graph.getEdgeType("Knows")) == null) {
            OrientEdgeType knows = OrderedEdgesGraphTest.graph.createEdgeType("Knows");
            OrientVertexType person = OrderedEdgesGraphTest.graph.createVertexType("Person");
            person.createEdgeProperty(OUT, "Knows").setOrdered(true);
        }
        OrderedEdgesGraphTest.graph.setAutoStartTx(true);
        mainPerson = OrderedEdgesGraphTest.graph.addVertex("class:Person", new Object[]{ "index", 0 });
        for (int i = 1; i < 101; ++i) {
            final Vertex newVertex = OrderedEdgesGraphTest.graph.addVertex("class:Person", new Object[]{ "index", i });
            mainPerson.addEdge("Knows", newVertex);
        }
    }

    @Test
    public void testEdgeOrder() {
        try {
            OrientVertex loadedPerson = OrderedEdgesGraphTest.graph.getVertex(mainPerson.getId());
            OrderedEdgesGraphTest.graph.setUseLightweightEdges(true);
            int i = 1;
            for (Edge e : loadedPerson.getEdges(OUT)) {
                TestCase.assertEquals(e.getVertex(IN).<Object>getProperty("index"), (i++));
            }
        } finally {
            OrderedEdgesGraphTest.graph.shutdown();
        }
    }

    @Test
    public void testReplacePosition() {
        OrientVertex loadedPerson;
        List<ODocument> edges;
        try {
            loadedPerson = OrderedEdgesGraphTest.graph.getVertex(mainPerson.getId());
            OrderedEdgesGraphTest.graph.setUseLightweightEdges(true);
            int i = 1;
            edges = loadedPerson.getRecord().field("out_Knows");
            ODocument edge10 = edges.remove(9);
            edges.add(edge10);
        } finally {
            OrderedEdgesGraphTest.graph.shutdown();
        }
        OrderedEdgesGraphTest.graph = new OrientGraph(OrderedEdgesGraphTest.DB_URL);
        try {
            OrderedEdgesGraphTest.graph.setUseLightweightEdges(true);
            loadedPerson = OrderedEdgesGraphTest.graph.getVertex(mainPerson.getId());
            edges = loadedPerson.getRecord().field("out_Knows");
            TestCase.assertEquals(OrderedEdgesGraphTest.graph.getVertex(edges.get(9)).<Object>getProperty("index"), 11);
            TestCase.assertEquals(OrderedEdgesGraphTest.graph.getVertex(edges.get(99)).<Object>getProperty("index"), 10);
        } finally {
            OrderedEdgesGraphTest.graph.shutdown();
        }
    }
}

