package org.opentripplanner.routing.graph;


import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.vertextype.StreetVertex;


public class EdgeTest {
    @Test
    public void testConstruct() {
        Graph graph = new Graph();
        Vertex head = new SimpleConcreteVertex(graph, "head", 47.669457, (-122.387577));
        Vertex tail = new SimpleConcreteVertex(graph, "tail", 47.669462, (-122.384739));
        Edge e = new SimpleConcreteEdge(head, tail);
        Assert.assertEquals(head, e.getFromVertex());
        Assert.assertEquals(tail, e.getToVertex());
        Assert.assertTrue(((e.getId()) >= 0));
    }

    @Test
    public void testEdgeRemoval() {
        Graph graph = new Graph();
        StreetVertex va = new org.opentripplanner.routing.vertextype.IntersectionVertex(graph, "A", 10.0, 10.0);
        StreetVertex vb = new org.opentripplanner.routing.vertextype.IntersectionVertex(graph, "B", 10.1, 10.1);
        StreetVertex vc = new org.opentripplanner.routing.vertextype.IntersectionVertex(graph, "C", 10.2, 10.2);
        StreetVertex vd = new org.opentripplanner.routing.vertextype.IntersectionVertex(graph, "D", 10.3, 10.3);
        Edge eab = new org.opentripplanner.routing.edgetype.StreetEdge(va, vb, null, "AB", 10, StreetTraversalPermission.ALL, false);
        Edge ebc = new org.opentripplanner.routing.edgetype.StreetEdge(vb, vc, null, "BC", 10, StreetTraversalPermission.ALL, false);
        Edge ecd = new org.opentripplanner.routing.edgetype.StreetEdge(vc, vd, null, "CD", 10, StreetTraversalPermission.ALL, false);
        // remove an edge that is not connected to this vertex
        va.removeOutgoing(ecd);
        Assert.assertEquals(va.getDegreeOut(), 1);
        // remove an edge from an edgelist that is empty
        vd.removeOutgoing(eab);
        Assert.assertEquals(vd.getDegreeOut(), 0);
        // remove an edge that is actually connected
        Assert.assertEquals(va.getDegreeOut(), 1);
        va.removeOutgoing(eab);
        Assert.assertEquals(va.getDegreeOut(), 0);
        // remove an edge that is actually connected
        Assert.assertEquals(vb.getDegreeIn(), 1);
        Assert.assertEquals(vb.getDegreeOut(), 1);
        vb.removeIncoming(eab);
        Assert.assertEquals(vb.getDegreeIn(), 0);
        Assert.assertEquals(vb.getDegreeOut(), 1);
        vb.removeOutgoing(ebc);
        Assert.assertEquals(vb.getDegreeIn(), 0);
        Assert.assertEquals(vb.getDegreeOut(), 0);
    }
}

