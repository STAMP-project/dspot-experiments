package com.baeldung.jgrapht;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.Assert;
import org.junit.Test;


public class DirectedGraphUnitTest {
    DirectedGraph<String, DefaultEdge> directedGraph;

    @Test
    public void givenDirectedGraph_whenGetStronglyConnectedSubgraphs_thenPathExistsBetweenStronglyconnectedVertices() {
        StrongConnectivityAlgorithm<String, DefaultEdge> scAlg = new KosarajuStrongConnectivityInspector<>(directedGraph);
        List<DirectedSubgraph<String, DefaultEdge>> stronglyConnectedSubgraphs = scAlg.stronglyConnectedSubgraphs();
        List<String> stronglyConnectedVertices = new ArrayList<>(stronglyConnectedSubgraphs.get(3).vertexSet());
        String randomVertex1 = stronglyConnectedVertices.get(0);
        String randomVertex2 = stronglyConnectedVertices.get(3);
        AllDirectedPaths<String, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(directedGraph);
        List<GraphPath<String, DefaultEdge>> possiblePathList = allDirectedPaths.getAllPaths(randomVertex1, randomVertex2, false, stronglyConnectedVertices.size());
        Assert.assertTrue(((possiblePathList.size()) > 0));
    }

    @Test
    public void givenDirectedGraphWithCycle_whenCheckCycles_thenDetectCycles() {
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(directedGraph);
        Assert.assertTrue(cycleDetector.detectCycles());
        Set<String> cycleVertices = cycleDetector.findCycles();
        Assert.assertTrue(((cycleVertices.size()) > 0));
    }

    @Test
    public void givenDirectedGraph_whenCreateInstanceDepthFirstIterator_thenGetIterator() {
        DepthFirstIterator depthFirstIterator = new DepthFirstIterator<>(directedGraph);
        Assert.assertNotNull(depthFirstIterator);
    }

    @Test
    public void givenDirectedGraph_whenCreateInstanceBreadthFirstIterator_thenGetIterator() {
        BreadthFirstIterator breadthFirstIterator = new BreadthFirstIterator<>(directedGraph);
        Assert.assertNotNull(breadthFirstIterator);
    }

    @Test
    public void givenDirectedGraph_whenGetDijkstraShortestPath_thenGetNotNullPath() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(directedGraph);
        List<String> shortestPath = dijkstraShortestPath.getPath("v1", "v4").getVertexList();
        Assert.assertNotNull(shortestPath);
    }

    @Test
    public void givenDirectedGraph_whenGetBellmanFordShortestPath_thenGetNotNullPath() {
        BellmanFordShortestPath bellmanFordShortestPath = new BellmanFordShortestPath(directedGraph);
        List<String> shortestPath = bellmanFordShortestPath.getPath("v1", "v4").getVertexList();
        Assert.assertNotNull(shortestPath);
    }
}

