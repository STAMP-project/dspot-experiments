package com.thinkaurelius.titan.graphdb.configuration;


import Graph.Features.EdgeFeatures;
import Graph.Features.VertexFeatures;
import com.thinkaurelius.titan.core.TitanGraph;
import junit.framework.TestCase;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Assert;
import org.junit.Test;


public class TinkerpopFeaturesTest {
    private TitanGraph graph;

    @Test
    public void testVertexFeatures() {
        Graph.Features.VertexFeatures vf = graph.features().vertex();
        Assert.assertFalse(vf.supportsCustomIds());
        Assert.assertFalse(vf.supportsStringIds());
        Assert.assertFalse(vf.supportsUuidIds());
        Assert.assertFalse(vf.supportsAnyIds());
        Assert.assertFalse(vf.supportsCustomIds());
        TestCase.assertTrue(vf.supportsNumericIds());
        Assert.assertFalse(vf.supportsUserSuppliedIds());
        graph.close();
        graph = open(true);
        vf = graph.features().vertex();
        Assert.assertFalse(vf.supportsCustomIds());
        Assert.assertFalse(vf.supportsStringIds());
        Assert.assertFalse(vf.supportsUuidIds());
        Assert.assertFalse(vf.supportsAnyIds());
        Assert.assertFalse(vf.supportsCustomIds());
        TestCase.assertTrue(vf.supportsNumericIds());
        TestCase.assertTrue(vf.supportsUserSuppliedIds());
    }

    @Test
    public void testEdgeFeatures() {
        Graph.Features.EdgeFeatures ef = graph.features().edge();
        Assert.assertFalse(ef.supportsStringIds());
        Assert.assertFalse(ef.supportsUuidIds());
        Assert.assertFalse(ef.supportsAnyIds());
        Assert.assertFalse(ef.supportsNumericIds());
        TestCase.assertTrue(ef.supportsCustomIds());
        Assert.assertFalse(ef.supportsUserSuppliedIds());
    }
}

