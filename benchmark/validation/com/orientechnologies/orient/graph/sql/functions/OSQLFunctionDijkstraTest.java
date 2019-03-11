package com.orientechnologies.orient.graph.sql.functions;


import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionDijkstraTest {
    private OrientGraph graph;

    private Vertex v1;

    private Vertex v2;

    private Vertex v3;

    private Vertex v4;

    private OSQLFunctionDijkstra functionDijkstra;

    @Test
    public void testExecute() throws Exception {
        final List<OrientVertex> result = functionDijkstra.execute(null, null, null, new Object[]{ v1, v4, "'weight'" }, new OBasicCommandContext());
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v2, result.get(1));
        Assert.assertEquals(v3, result.get(2));
        Assert.assertEquals(v4, result.get(3));
    }
}

