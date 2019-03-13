/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.graph.sql.functions;


import Direction.BOTH;
import Direction.OUT;
import HeuristicFormula.CUSTOM;
import HeuristicFormula.EUCLIDEANNOSQR;
import HeuristicFormula.MAXAXIS;
import OSQLFunctionAstar.PARAM_CUSTOM_HEURISTIC_FORMULA;
import OSQLFunctionAstar.PARAM_DIRECTION;
import OSQLFunctionAstar.PARAM_EDGE_TYPE_NAMES;
import OSQLFunctionAstar.PARAM_EMPTY_IF_MAX_DEPTH;
import OSQLFunctionAstar.PARAM_HEURISTIC_FORMULA;
import OSQLFunctionAstar.PARAM_MAX_DEPTH;
import OSQLFunctionAstar.PARAM_PARALLEL;
import OSQLFunctionAstar.PARAM_TIE_BREAKER;
import OSQLFunctionAstar.PARAM_VERTEX_AXIS_NAMES;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/* @author Saeed Tabrizi (saeed a_t  nowcando.com) */
public class OSQLFunctionAstarTest {
    private static int dbCounter = 0;

    private OrientGraph graph;

    private Vertex v0;

    private Vertex v1;

    private Vertex v2;

    private Vertex v3;

    private Vertex v4;

    private Vertex v5;

    private Vertex v6;

    private OSQLFunctionAstar functionAstar;

    @Test
    public void test1Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v1, v4, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v2, result.get(1));
        Assert.assertEquals(v3, result.get(2));
        Assert.assertEquals(v4, result.get(3));
    }

    @Test
    public void test2Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v1, v6, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v6, result.get(2));
    }

    @Test
    public void test3Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v1, v6, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v6, result.get(2));
    }

    @Test
    public void test4Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon", "alt" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v1, v6, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v6, result.get(2));
    }

    @Test
    public void test5Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v3, v5, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(v3, result.get(0));
        Assert.assertEquals(v6, result.get(1));
        Assert.assertEquals(v5, result.get(2));
    }

    @Test
    public void test6Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v2, result.get(2));
        Assert.assertEquals(v3, result.get(3));
        Assert.assertEquals(v4, result.get(4));
        Assert.assertEquals(v1, result.get(5));
    }

    @Test
    public void test7Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, "out");
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, "EucliDEAN");
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v2, result.get(2));
        Assert.assertEquals(v3, result.get(3));
        Assert.assertEquals(v4, result.get(4));
        Assert.assertEquals(v1, result.get(5));
    }

    @Test
    public void test8Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, OUT);
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_TIE_BREAKER, false);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, EUCLIDEANNOSQR);
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v2, result.get(2));
        Assert.assertEquals(v4, result.get(3));
        Assert.assertEquals(v1, result.get(4));
    }

    @Test
    public void test9Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, BOTH);
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_TIE_BREAKER, false);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, MAXAXIS);
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v1, result.get(2));
    }

    @Test
    public void test10Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, OUT);
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_TIE_BREAKER, false);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, CUSTOM);
        options.put(PARAM_CUSTOM_HEURISTIC_FORMULA, "myCustomHeuristic");
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v2, result.get(2));
        Assert.assertEquals(v3, result.get(3));
        Assert.assertEquals(v4, result.get(4));
        Assert.assertEquals(v1, result.get(5));
    }

    @Test
    public void test11Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, OUT);
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_TIE_BREAKER, false);
        options.put(PARAM_EMPTY_IF_MAX_DEPTH, true);
        options.put(PARAM_MAX_DEPTH, 3);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, CUSTOM);
        options.put(PARAM_CUSTOM_HEURISTIC_FORMULA, "myCustomHeuristic");
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void test12Execute() throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PARAM_DIRECTION, OUT);
        options.put(PARAM_PARALLEL, true);
        options.put(PARAM_TIE_BREAKER, false);
        options.put(PARAM_EMPTY_IF_MAX_DEPTH, false);
        options.put(PARAM_MAX_DEPTH, 3);
        options.put(PARAM_EDGE_TYPE_NAMES, new String[]{ "has_path" });
        options.put(PARAM_VERTEX_AXIS_NAMES, new String[]{ "lat", "lon" });
        options.put(PARAM_HEURISTIC_FORMULA, CUSTOM);
        options.put(PARAM_CUSTOM_HEURISTIC_FORMULA, "myCustomHeuristic");
        final List<OrientVertex> result = functionAstar.execute(null, null, null, new Object[]{ v6, v1, "'weight'", options }, new OBasicCommandContext());
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(v6, result.get(0));
        Assert.assertEquals(v5, result.get(1));
        Assert.assertEquals(v2, result.get(2));
        Assert.assertEquals(v3, result.get(3));
    }

    @Test
    public void testSql() {
        Iterable r = graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery((((("select expand(astar(" + (v1.getId())) + ", ") + (v4.getId())) + ", 'weight', {'direction':'out', 'parallel':true, 'edgeTypeNames':'has_path'}))"))).execute();
        List result = new ArrayList();
        for (Object x : r) {
            result.add(x);
        }
        Assert.assertEquals(16, graph.countEdges("has_path"));
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v2, result.get(1));
        Assert.assertEquals(v3, result.get(2));
        Assert.assertEquals(v4, result.get(3));
    }
}

