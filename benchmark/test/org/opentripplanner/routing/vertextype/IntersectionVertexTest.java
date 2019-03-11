package org.opentripplanner.routing.vertextype;


import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;


public class IntersectionVertexTest {
    private Graph graph;

    private StreetEdge fromEdge;

    private StreetEdge straightAheadEdge;

    @Test
    public void testFreeFlowing() {
        IntersectionVertex iv = new IntersectionVertex(graph, "vertex", 1.0, 2.0);
        Assert.assertFalse(iv.freeFlowing);
        iv.freeFlowing = true;
        Assert.assertTrue(iv.freeFlowing);
    }

    @Test
    public void testInferredFreeFlowing() {
        IntersectionVertex iv = new IntersectionVertex(graph, "vertex", 1.0, 2.0);
        Assert.assertFalse(iv.trafficLight);
        Assert.assertFalse(iv.inferredFreeFlowing());
        Assert.assertEquals(0, iv.getDegreeIn());
        Assert.assertEquals(0, iv.getDegreeOut());
        iv.trafficLight = true;
        Assert.assertTrue(iv.trafficLight);
        Assert.assertFalse(iv.inferredFreeFlowing());
        iv.addIncoming(fromEdge);
        Assert.assertEquals(1, iv.getDegreeIn());
        Assert.assertEquals(0, iv.getDegreeOut());
        Assert.assertFalse(iv.inferredFreeFlowing());
        iv.addOutgoing(straightAheadEdge);
        Assert.assertEquals(1, iv.getDegreeIn());
        Assert.assertEquals(1, iv.getDegreeOut());
        Assert.assertFalse(iv.inferredFreeFlowing());
        iv.trafficLight = false;
        Assert.assertFalse(iv.trafficLight);
        Assert.assertTrue(iv.inferredFreeFlowing());
        // Set the freeFlowing bit to false.
        iv.freeFlowing = false;
        Assert.assertFalse(iv.freeFlowing);
    }
}

