package com.baeldung.jgrapht;


import java.util.List;
import org.jgrapht.alg.HamiltonianCycle;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Assert;
import org.junit.Test;


public class CompleteGraphUnitTest {
    static SimpleWeightedGraph<String, DefaultEdge> completeGraph;

    static int size = 10;

    @Test
    public void givenCompleteGraph_whenGetHamiltonianCyclePath_thenGetVerticeListInSequence() {
        List<String> verticeList = HamiltonianCycle.getApproximateOptimalForCompleteGraph(CompleteGraphUnitTest.completeGraph);
        Assert.assertEquals(verticeList.size(), CompleteGraphUnitTest.completeGraph.vertexSet().size());
    }
}

