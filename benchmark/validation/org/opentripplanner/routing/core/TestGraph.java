package org.opentripplanner.routing.core;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.opentripplanner.routing.edgetype.FreeEdge;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vertextype.StreetVertex;


public class TestGraph extends TestCase {
    public void testBasic() throws Exception {
        Graph g = new Graph();
        TestCase.assertNotNull(g);
    }

    public void testAddVertex() throws Exception {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        TestCase.assertEquals(a.getLabel(), "A");
    }

    public void testGetVertex() throws Exception {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        Vertex b = g.getVertex("A");
        TestCase.assertEquals(a, b);
    }

    public void testAddEdge() throws Exception {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        Vertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        FreeEdge ee = new FreeEdge(a, b);
        TestCase.assertNotNull(ee);
    }

    public void testGetEdgesOneEdge() {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        Vertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        FreeEdge ee = new FreeEdge(a, b);
        List<Edge> edges = new ArrayList<Edge>(g.getEdges());
        TestCase.assertEquals(1, edges.size());
        TestCase.assertEquals(ee, edges.get(0));
    }

    public void testGetEdgesMultiple() {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        Vertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        Vertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "C", 3, 2);
        Set<Edge> expectedEdges = new HashSet<Edge>(4);
        expectedEdges.add(new FreeEdge(a, b));
        expectedEdges.add(new FreeEdge(b, c));
        expectedEdges.add(new FreeEdge(c, b));
        expectedEdges.add(new FreeEdge(c, a));
        Set<Edge> edges = new HashSet<Edge>(g.getEdges());
        TestCase.assertEquals(4, edges.size());
        TestCase.assertEquals(expectedEdges, edges);
    }

    public void testGetStreetEdgesNone() {
        Graph g = new Graph();
        Vertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        Vertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        Vertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "C", 3, 2);
        Set<Edge> allEdges = new HashSet<Edge>(4);
        allEdges.add(new FreeEdge(a, b));
        allEdges.add(new FreeEdge(b, c));
        allEdges.add(new FreeEdge(c, b));
        allEdges.add(new FreeEdge(c, a));
        Set<StreetEdge> edges = new HashSet<StreetEdge>(g.getStreetEdges());
        TestCase.assertEquals(0, edges.size());
    }

    public void testGetStreetEdgesSeveral() {
        Graph g = new Graph();
        StreetVertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        StreetVertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        StreetVertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "C", 3, 2);
        Set<Edge> allStreetEdges = new HashSet<Edge>(4);
        allStreetEdges.add(edge(a, b, 1.0));
        allStreetEdges.add(edge(b, c, 1.0));
        allStreetEdges.add(edge(c, b, 1.0));
        allStreetEdges.add(edge(c, a, 1.0));
        Set<StreetEdge> edges = new HashSet<StreetEdge>(g.getStreetEdges());
        TestCase.assertEquals(4, edges.size());
        TestCase.assertEquals(allStreetEdges, edges);
    }

    public void testGetEdgesAndVerticesById() {
        Graph g = new Graph();
        StreetVertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 5, 5);
        StreetVertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 6, 6);
        StreetVertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "C", 3, 2);
        Set<Edge> allEdges = new HashSet<Edge>(4);
        allEdges.add(edge(a, b, 1.0));
        allEdges.add(edge(b, c, 1.0));
        allEdges.add(edge(c, b, 1.0));
        allEdges.add(edge(c, a, 1.0));
        // Before rebuilding the indices, they are empty.
        for (Edge e : allEdges) {
            TestCase.assertNull(g.getEdgeById(e.getId()));
        }
        for (Vertex v : g.getVertices()) {
            TestCase.assertNull(g.getVertexById(v.getIndex()));
        }
        g.rebuildVertexAndEdgeIndices();
        for (Edge e : allEdges) {
            TestCase.assertEquals(e, g.getEdgeById(e.getId()));
        }
        for (Vertex v : g.getVertices()) {
            TestCase.assertEquals(v, g.getVertexById(v.getIndex()));
        }
    }
}

