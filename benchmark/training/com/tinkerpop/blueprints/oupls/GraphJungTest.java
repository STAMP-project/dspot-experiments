package com.tinkerpop.blueprints.oupls;


import EdgeType.DIRECTED;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;
import junit.framework.TestCase;


/**
 *
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GraphJungTest extends TestCase {
    public void testTinkerGraph() {
        GraphJung<TinkerGraph> jung = new GraphJung<TinkerGraph>(TinkerGraphFactory.createTinkerGraph());
        TestCase.assertEquals(jung.getVertices().size(), 6);
        TestCase.assertEquals(jung.getEdges().size(), 6);
        TestCase.assertEquals(jung.getVertexCount(), 6);
        TestCase.assertEquals(jung.getEdgeCount(), 6);
        Vertex marko = null;
        Vertex josh = null;
        Vertex vadas = null;
        for (Vertex vertex : jung.getVertices()) {
            TestCase.assertTrue(jung.containsVertex(vertex));
            for (Edge edge : jung.getOutEdges(vertex)) {
                TestCase.assertEquals(jung.getSource(edge), vertex);
            }
            for (Edge edge : jung.getInEdges(vertex)) {
                TestCase.assertEquals(jung.getDest(edge), vertex);
            }
            if (vertex.getId().equals("1")) {
                marko = vertex;
                TestCase.assertEquals(jung.getOutEdges(vertex).size(), 3);
                TestCase.assertEquals(jung.getInEdges(vertex).size(), 0);
                TestCase.assertEquals(jung.getNeighborCount(vertex), 3);
                int count = 0;
                for (Vertex vertex2 : jung.getNeighbors(vertex)) {
                    if (vertex2.getId().equals("2"))
                        count++;
                    else
                        if (vertex2.getId().equals("4"))
                            count++;
                        else
                            if (vertex2.getId().equals("3"))
                                count++;
                            else
                                TestCase.assertTrue(false);



                }
                TestCase.assertEquals(count, 3);
                TestCase.assertEquals(jung.getSuccessorCount(vertex), 3);
                count = 0;
                for (Vertex vertex2 : jung.getSuccessors(vertex)) {
                    if (vertex2.getId().equals("2"))
                        count++;
                    else
                        if (vertex2.getId().equals("4"))
                            count++;
                        else
                            if (vertex2.getId().equals("3"))
                                count++;
                            else
                                TestCase.assertTrue(false);



                }
                TestCase.assertEquals(jung.getPredecessorCount(vertex), 0);
            } else
                if (vertex.getId().equals("2")) {
                    vadas = vertex;
                    TestCase.assertEquals(jung.getOutEdges(vertex).size(), 0);
                    TestCase.assertEquals(jung.getInEdges(vertex).size(), 1);
                    TestCase.assertEquals(jung.getNeighborCount(vertex), 1);
                    int count = 0;
                    for (Vertex vertex2 : jung.getNeighbors(vertex)) {
                        if (vertex2.getId().equals("1"))
                            count++;
                        else
                            TestCase.assertTrue(false);

                    }
                    TestCase.assertEquals(count, 1);
                    TestCase.assertEquals(jung.getSuccessorCount(vertex), 0);
                    TestCase.assertEquals(jung.getPredecessorCount(vertex), 1);
                    count = 0;
                    for (Vertex vertex2 : jung.getPredecessors(vertex)) {
                        if (vertex2.getId().equals("1"))
                            count++;
                        else
                            TestCase.assertTrue(false);

                    }
                    TestCase.assertEquals(count, 1);
                } else
                    if (vertex.getId().equals("4")) {
                        josh = vertex;
                        TestCase.assertEquals(jung.getOutEdges(vertex).size(), 2);
                        TestCase.assertEquals(jung.getInEdges(vertex).size(), 1);
                        TestCase.assertEquals(jung.getNeighborCount(vertex), 3);
                        int count = 0;
                        for (Vertex vertex2 : jung.getNeighbors(vertex)) {
                            if (vertex2.getId().equals("1"))
                                count++;
                            else
                                if (vertex2.getId().equals("3"))
                                    count++;
                                else
                                    if (vertex2.getId().equals("5"))
                                        count++;
                                    else
                                        TestCase.assertTrue(false);



                        }
                        TestCase.assertEquals(count, 3);
                        TestCase.assertEquals(jung.getSuccessorCount(vertex), 2);
                        count = 0;
                        for (Vertex vertex2 : jung.getSuccessors(vertex)) {
                            if (vertex2.getId().equals("3"))
                                count++;
                            else
                                if (vertex2.getId().equals("5"))
                                    count++;
                                else
                                    TestCase.assertTrue(false);


                        }
                        TestCase.assertEquals(count, 2);
                        TestCase.assertEquals(jung.getPredecessorCount(vertex), 1);
                        count = 0;
                        for (Vertex vertex2 : jung.getPredecessors(vertex)) {
                            if (vertex2.getId().equals("1"))
                                count++;
                            else
                                TestCase.assertTrue(false);

                        }
                        TestCase.assertEquals(count, 1);
                    }


        }
        TestCase.assertTrue((null != marko));
        TestCase.assertTrue((null != vadas));
        TestCase.assertTrue((null != josh));
        TestCase.assertEquals(jung.findEdgeSet(marko, josh).size(), 1);
        TestCase.assertTrue(jung.findEdgeSet(marko, josh).contains(jung.findEdge(marko, josh)));
        TestCase.assertEquals(jung.getDefaultEdgeType(), DIRECTED);
        for (Edge edge : jung.getEdges()) {
            TestCase.assertTrue(jung.containsEdge(edge));
            TestCase.assertEquals(jung.getEdgeType(edge), DIRECTED);
            TestCase.assertEquals(jung.getIncidentCount(edge), 2);
        }
    }
}

