package com.tinkerpop.blueprints.impls.orient;


import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@RunWith(JUnit4.class)
public class OrientGraphNoTxTest extends GraphTest {
    private Map<String, OrientGraphNoTx> currentGraphs = new HashMap<String, OrientGraphNoTx>();

    // testing only those suites that are read-only
    @Test
    public void testVertexQueryTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new VertexQueryTestSuite(this));
        printTestPerformance("VertexQueryTestSuite", stopWatch());
    }

    @Test
    public void testGraphQueryTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphQueryTestSuite(this));
        printTestPerformance("GraphQueryTestSuite", stopWatch());
    }

    @Test
    public void testIndexableGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new IndexableGraphTestSuite(this));
        printTestPerformance("IndexableGraphTestSuite", stopWatch());
    }

    @Test
    public void testKeyIndexableGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new KeyIndexableGraphTestSuite(this));
        printTestPerformance("KeyIndexableGraphTestSuite", stopWatch());
    }

    @Test
    public void testGraphMLReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", stopWatch());
    }

    @Test
    public void testGraphSONReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", stopWatch());
    }

    @Test
    public void testGMLReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", stopWatch());
    }

    @Test
    public void testRemoveUnlinked() {
        Graph graph = generateGraph("graph");
        try {
            Vertex x = graph.addVertex(null);
            Vertex y = graph.addVertex(null);
            graph.addEdge(null, x, y, "connected_to");
            for (Vertex v : graph.getVertices()) {
                graph.removeVertex(v);
            }
            for (Edge e : graph.getEdges()) {
                System.out.println(("e: " + e));
            }
            graph.shutdown();
        } finally {
            dropGraph("graph");
        }
        graph = generateGraph("graph");
        try {
            for (Edge e : graph.getEdges()) {
                graph.removeEdge(e);
            }
            graph.shutdown();
        } finally {
            dropGraph("graph");
        }
    }
}

