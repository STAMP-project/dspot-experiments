package org.opentripplanner.routing.core;


import junit.framework.TestCase;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;


public class TestOverlayGraph extends TestCase {
    public void testBasic() throws Exception {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "a", 5, 5);
        Vertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "b", 6, 5);
        Vertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "c", 7, 5);
        Vertex d = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "d", 8, 5);
        // vary weights so edges are not considered equal
        Edge ab = new org.opentripplanner.routing.edgetype.SimpleEdge(a, b, 1, 1);
        Edge bc1 = new org.opentripplanner.routing.edgetype.SimpleEdge(b, c, 1, 1);
        Edge bc2 = new org.opentripplanner.routing.edgetype.SimpleEdge(b, c, 2, 2);
        Edge bc3 = new org.opentripplanner.routing.edgetype.SimpleEdge(b, c, 3, 3);
        Edge cd1 = new org.opentripplanner.routing.edgetype.SimpleEdge(c, d, 1, 1);
        Edge cd2 = new org.opentripplanner.routing.edgetype.SimpleEdge(c, d, 2, 2);
        Edge cd3 = new org.opentripplanner.routing.edgetype.SimpleEdge(c, d, 3, 3);
        OverlayGraph og = new OverlayGraph(g);
        TestCase.assertEquals(g.countVertices(), og.countVertices());
        TestCase.assertEquals(g.countEdges(), og.countEdges());
        for (Vertex v : g.getVertices()) {
            for (Edge e : v.getOutgoing()) {
                TestCase.assertTrue(og.getOutgoing(v).contains(e));
                TestCase.assertTrue(og.getIncoming(e.getToVertex()).contains(e));
            }
            for (Edge e : v.getIncoming()) {
                TestCase.assertTrue(og.getIncoming(v).contains(e));
                TestCase.assertTrue(og.getOutgoing(e.getFromVertex()).contains(e));
            }
        }
        TestCase.assertTrue(((og.getIncoming(a).size()) == 0));
        TestCase.assertTrue(((og.getOutgoing(d).size()) == 0));
        // add an edge that is not in the overlay
        Edge ad = new org.opentripplanner.routing.edgetype.FreeEdge(a, d);
        TestCase.assertTrue(((d.getIncoming().size()) == 4));
        TestCase.assertTrue(((og.getIncoming(d).size()) == 3));
        TestCase.assertTrue(((a.getOutgoing().size()) == 2));
        TestCase.assertTrue(((og.getOutgoing(a).size()) == 1));
        // remove edges from overlaygraph
        og.removeEdge(bc1);
        og.removeEdge(bc2);
        TestCase.assertEquals(og.countEdges(), ((g.countEdges()) - 3));
        TestCase.assertTrue(((og.getOutgoing(b).size()) == 1));
        TestCase.assertTrue(((og.getIncoming(c).size()) == 1));
    }
}

