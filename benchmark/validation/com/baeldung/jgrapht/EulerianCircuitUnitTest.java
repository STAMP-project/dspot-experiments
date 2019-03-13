package com.baeldung.jgrapht;


import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Assert;
import org.junit.Test;


public class EulerianCircuitUnitTest {
    SimpleWeightedGraph<String, DefaultEdge> simpleGraph;

    @Test
    public void givenGraph_whenCheckEluerianCycle_thenGetResult() {
        HierholzerEulerianCycle eulerianCycle = new HierholzerEulerianCycle<>();
        Assert.assertTrue(eulerianCycle.isEulerian(simpleGraph));
    }

    @Test
    public void givenGraphWithEulerianCircuit_whenGetEulerianCycle_thenGetGraphPath() {
        HierholzerEulerianCycle eulerianCycle = new HierholzerEulerianCycle<>();
        GraphPath path = eulerianCycle.getEulerianCycle(simpleGraph);
        Assert.assertTrue(path.getEdgeList().containsAll(simpleGraph.edgeSet()));
    }
}

