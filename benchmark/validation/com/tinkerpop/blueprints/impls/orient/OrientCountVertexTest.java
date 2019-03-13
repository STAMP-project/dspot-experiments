package com.tinkerpop.blueprints.impls.orient;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class OrientCountVertexTest {
    @Test
    public void countVertexShouldWorkWithinOrOutsideTransactions() {
        // Create a node v1 with at least two edges
        OrientGraph g = createGraph();
        Assert.assertEquals(0, g.countVertices());
        g.addVertex("class:V1");
        g.addVertex("class:V2");
        g.addVertex("class:V2");
        g.shutdown();
        g = createGraph();
        long allCount = g.countVertices();
        Assert.assertEquals(3, allCount);
        long v1Count = g.countVertices("V1");
        Assert.assertEquals(1, v1Count);
        Assert.assertEquals(2, g.countVertices("V2"));
        g.addVertex("class:V1");
        Assert.assertEquals((allCount + 1), g.countVertices());
        Assert.assertEquals((v1Count + 1), g.countVertices("V1"));
        g.shutdown();
    }
}

