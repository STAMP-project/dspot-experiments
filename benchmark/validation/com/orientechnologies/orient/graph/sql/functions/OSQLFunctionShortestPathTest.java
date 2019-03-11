package com.orientechnologies.orient.graph.sql.functions;


import OSQLFunctionShortestPath.PARAM_MAX_DEPTH;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.id.ORID;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionShortestPathTest {
    private OrientGraph graph;

    private Map<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();

    private OSQLFunctionShortestPath function;

    @Test
    public void testExecute() throws Exception {
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(4) }, new OBasicCommandContext());
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(vertices.get(1).getId(), result.get(0));
        Assert.assertEquals(vertices.get(3).getId(), result.get(1));
        Assert.assertEquals(vertices.get(4).getId(), result.get(2));
    }

    @Test
    public void testExecuteOut() throws Exception {
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(4), "out", null }, new OBasicCommandContext());
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(vertices.get(1).getId(), result.get(0));
        Assert.assertEquals(vertices.get(2).getId(), result.get(1));
        Assert.assertEquals(vertices.get(3).getId(), result.get(2));
        Assert.assertEquals(vertices.get(4).getId(), result.get(3));
    }

    @Test
    public void testExecuteOnlyEdge1() throws Exception {
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(4), null, "Edge1" }, new OBasicCommandContext());
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(vertices.get(1).getId(), result.get(0));
        Assert.assertEquals(vertices.get(2).getId(), result.get(1));
        Assert.assertEquals(vertices.get(3).getId(), result.get(2));
        Assert.assertEquals(vertices.get(4).getId(), result.get(3));
    }

    @Test
    public void testLong() throws Exception {
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(20) }, new OBasicCommandContext());
        Assert.assertEquals(11, result.size());
        Assert.assertEquals(vertices.get(1).getId(), result.get(0));
        Assert.assertEquals(vertices.get(3).getId(), result.get(1));
        int next = 2;
        for (int i = 4; i <= 20; i += 2) {
            Assert.assertEquals(vertices.get(i).getId(), result.get((next++)));
        }
    }

    @Test
    public void testMaxDepth1() throws Exception {
        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(PARAM_MAX_DEPTH, 11);
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(20), null, null, additionalParams }, new OBasicCommandContext());
        Assert.assertEquals(11, result.size());
    }

    @Test
    public void testMaxDepth2() throws Exception {
        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(PARAM_MAX_DEPTH, 12);
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(20), null, null, additionalParams }, new OBasicCommandContext());
        Assert.assertEquals(11, result.size());
    }

    @Test
    public void testMaxDepth3() throws Exception {
        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(PARAM_MAX_DEPTH, 10);
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(20), null, null, additionalParams }, new OBasicCommandContext());
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testMaxDepth4() throws Exception {
        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(PARAM_MAX_DEPTH, 3);
        final List<ORID> result = function.execute(null, null, null, new Object[]{ vertices.get(1), vertices.get(20), null, null, additionalParams }, new OBasicCommandContext());
        Assert.assertEquals(0, result.size());
    }
}

