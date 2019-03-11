/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util.graph;


import AttributedDirectedGraph.AttributedEdgeFactory;
import Graphs.FrozenGraph;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link DirectedGraph}.
 */
public class DirectedGraphTest {
    public DirectedGraphTest() {
    }

    @Test
    public void testOne() {
        DirectedGraph<String, DefaultEdge> g = DefaultDirectedGraph.create();
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.addVertex("E");
        g.addVertex("F");
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("D", "C");
        g.addEdge("C", "D");
        g.addEdge("E", "F");
        g.addEdge("C", "C");
        Assert.assertEquals("[A, B, C, D]", shortestPath(g, "A", "D").toString());
        g.addEdge("B", "D");
        Assert.assertEquals("[A, B, D]", shortestPath(g, "A", "D").toString());
        Assert.assertNull("There is no path from A to E", shortestPath(g, "A", "E"));
        Assert.assertEquals("[]", shortestPath(g, "D", "D").toString());
        Assert.assertNull("Node X is not in the graph", shortestPath(g, "X", "A"));
        Assert.assertEquals("[[A, B, C, D], [A, B, D]]", paths(g, "A", "D").toString());
    }

    @Test
    public void testVertexMustExist() {
        DirectedGraph<String, DefaultEdge> g = DefaultDirectedGraph.create();
        final boolean b = g.addVertex("A");
        Assert.assertTrue(b);
        final boolean b2 = g.addVertex("A");
        Assert.assertFalse(b2);
        try {
            DefaultEdge x = g.addEdge("A", "B");
            Assert.fail(("expected exception, got " + x));
        } catch (IllegalArgumentException e) {
            // ok
        }
        g.addVertex("B");
        DefaultEdge x = g.addEdge("A", "B");
        Assert.assertNotNull(x);
        DefaultEdge x2 = g.addEdge("A", "B");
        Assert.assertNull(x2);
        try {
            DefaultEdge x3 = g.addEdge("Z", "A");
            Assert.fail(("expected exception, got " + x3));
        } catch (IllegalArgumentException e) {
            // ok
        }
        g.addVertex("Z");
        DefaultEdge x3 = g.addEdge("Z", "A");
        Assert.assertNotNull(x3);
        DefaultEdge x4 = g.addEdge("Z", "A");
        Assert.assertNull(x4);
        // Attempting to add a vertex already present does not change the graph.
        final List<DefaultEdge> in1 = g.getInwardEdges("A");
        final List<DefaultEdge> out1 = g.getOutwardEdges("A");
        final boolean b3 = g.addVertex("A");
        Assert.assertFalse(b3);
        final List<DefaultEdge> in2 = g.getInwardEdges("A");
        final List<DefaultEdge> out2 = g.getOutwardEdges("A");
        Assert.assertEquals(in1, in2);
        Assert.assertEquals(out1, out2);
    }

    /**
     * Unit test for {@link DepthFirstIterator}.
     */
    @Test
    public void testDepthFirst() {
        final DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        final List<String> list = new ArrayList<String>();
        for (String s : DepthFirstIterator.of(graph, "A")) {
            list.add(s);
        }
        Assert.assertThat(list.toString(), CoreMatchers.equalTo("[A, B, C, D, E, C, D, F]"));
        list.clear();
        DepthFirstIterator.reachable(list, graph, "A");
        Assert.assertThat(list.toString(), CoreMatchers.equalTo("[A, B, C, D, E, C, D, F]"));
    }

    /**
     * Unit test for {@link DepthFirstIterator}.
     */
    @Test
    public void testPredecessorList() {
        final DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        final List<String> list = Graphs.predecessorListOf(graph, "C");
        Assert.assertEquals("[B, E]", list.toString());
    }

    /**
     * Unit test for
     * {@link DefaultDirectedGraph#removeAllVertices(java.util.Collection)}.
     */
    @Test
    public void testRemoveAllVertices() {
        final DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        graph.removeAllVertices(Arrays.asList("B", "E"));
        Assert.assertEquals("[A, C, D, F]", graph.vertexSet().toString());
    }

    /**
     * Unit test for {@link TopologicalOrderIterator}.
     */
    @Test
    public void testTopologicalOrderIterator() {
        final DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        final List<String> list = new ArrayList<String>();
        for (String s : TopologicalOrderIterator.of(graph)) {
            list.add(s);
        }
        Assert.assertEquals("[A, B, E, C, F, D]", list.toString());
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.util.graph.Graphs.FrozenGraph}.
     */
    @Test
    public void testPaths() {
        // B -> C
        // /      \
        // A        E
        // \      /
        // D -->
        final DefaultDirectedGraph<String, DefaultEdge> graph = DefaultDirectedGraph.create();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "D");
        graph.addEdge("D", "E");
        graph.addEdge("C", "E");
        final FrozenGraph<String, DefaultEdge> frozenGraph = Graphs.makeImmutable(graph);
        Assert.assertEquals("[A, B]", frozenGraph.getShortestPath("A", "B").toString());
        Assert.assertEquals("[[A, B]]", frozenGraph.getPaths("A", "B").toString());
        Assert.assertEquals("[A, D, E]", frozenGraph.getShortestPath("A", "E").toString());
        Assert.assertEquals("[[A, B, C, E], [A, D, E]]", frozenGraph.getPaths("A", "E").toString());
        Assert.assertNull(frozenGraph.getShortestPath("B", "A"));
        Assert.assertNull(frozenGraph.getShortestPath("D", "C"));
        Assert.assertEquals("[[D, E]]", frozenGraph.getPaths("D", "E").toString());
        Assert.assertEquals("[D, E]", frozenGraph.getShortestPath("D", "E").toString());
    }

    /**
     * Unit test for {@link org.apache.calcite.util.graph.CycleDetector}.
     */
    @Test
    public void testCycleDetection() {
        // A - B - C - D
        // \     /
        // +- E - F
        DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of()));
        // Add cycle C-D-E-C
        // 
        // A - B - C - D
        // \     /     \
        // +- E - F   |
        // ^      /
        // \_____/
        graph.addEdge("D", "E");
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of("C", "D", "E", "F")));
        // Add another cycle, D-C-D in addition to C-D-E-C.
        // __
        // /  \
        // A - B - C - D
        // \     /     \
        // +- E - F   |
        // ^      /
        // \_____/
        graph.addEdge("D", "C");
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of("C", "D", "E", "F")));
        graph.removeEdge("D", "E");
        graph.removeEdge("D", "C");
        graph.addEdge("C", "B");
        // Add cycle of length 2, C-B-C
        // __
        // /  \
        // A - B - C - D
        // \     /
        // +- E - F
        // 
        // Detected cycle contains "D", which is downstream from the cycle but not
        // in the cycle. Not sure whether that is correct.
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of("B", "C", "D")));
        // Add single-node cycle, C-C
        // 
        // ___
        // \ /
        // A - B - C - D
        // \     /
        // +- E - F
        graph.removeEdge("C", "B");
        graph.addEdge("C", "C");
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of("C", "D")));
        // Empty graph is not cyclic.
        graph.removeAllVertices(graph.vertexSet());
        Assert.assertThat(new CycleDetector<String, DefaultEdge>(graph).findCycles(), CoreMatchers.equalTo(ImmutableSet.of()));
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.util.graph.BreadthFirstIterator}.
     */
    @Test
    public void testBreadthFirstIterator() {
        DefaultDirectedGraph<String, DefaultEdge> graph = createDag();
        final List<String> expected = ImmutableList.of("A", "B", "E", "C", "F", "D");
        Assert.assertThat(getA(graph, "A"), CoreMatchers.equalTo(expected));
        Assert.assertThat(Lists.newArrayList(getB(graph, "A")), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testAttributed() {
        AttributedDirectedGraph<String, DefaultEdge> g = AttributedDirectedGraph.create(new DirectedGraphTest.DefaultAttributedEdgeFactory());
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.addVertex("E");
        g.addVertex("F");
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("D", "C", 1);
        g.addEdge("C", "D", 1);
        g.addEdge("E", "F", 1);
        g.addEdge("C", "C", 1);
        Assert.assertEquals("[A, B, C, D]", shortestPath(g, "A", "D").toString());
        g.addEdge("B", "D", 1);
        Assert.assertEquals("[A, B, D]", shortestPath(g, "A", "D").toString());
        Assert.assertNull("There is no path from A to E", shortestPath(g, "A", "E"));
        Assert.assertEquals("[]", shortestPath(g, "D", "D").toString());
        Assert.assertNull("Node X is not in the graph", shortestPath(g, "X", "A"));
        Assert.assertEquals("[[A, B, C, D], [A, B, D]]", paths(g, "A", "D").toString());
        Assert.assertThat(g.addVertex("B"), CoreMatchers.is(false));
        Assert.assertThat(Iterables.size(g.getEdges("A", "B")), CoreMatchers.is(1));
        Assert.assertThat(g.addEdge("A", "B", 1), CoreMatchers.nullValue());
        Assert.assertThat(Iterables.size(g.getEdges("A", "B")), CoreMatchers.is(1));
        Assert.assertThat(g.addEdge("A", "B", 2), CoreMatchers.notNullValue());
        Assert.assertThat(Iterables.size(g.getEdges("A", "B")), CoreMatchers.is(2));
    }

    /**
     * Edge that stores its attributes in a list.
     */
    private static class DefaultAttributedEdge extends DefaultEdge {
        private final List list;

        DefaultAttributedEdge(String source, String target, List list) {
            super(source, target);
            this.list = ImmutableList.copyOf(list);
        }

        @Override
        public int hashCode() {
            return ((super.hashCode()) * 31) + (list.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            return ((this) == obj) || ((((obj instanceof DirectedGraphTest.DefaultAttributedEdge) && (((DirectedGraphTest.DefaultAttributedEdge) (obj)).source.equals(source))) && (((DirectedGraphTest.DefaultAttributedEdge) (obj)).target.equals(target))) && (((DirectedGraphTest.DefaultAttributedEdge) (obj)).list.equals(list)));
        }
    }

    /**
     * Factory for {@link DefaultAttributedEdge}.
     */
    private static class DefaultAttributedEdgeFactory implements AttributedEdgeFactory<String, DefaultEdge> {
        public DefaultEdge createEdge(String v0, String v1, Object... attributes) {
            return new DirectedGraphTest.DefaultAttributedEdge(v0, v1, ImmutableList.copyOf(attributes));
        }

        public DefaultEdge createEdge(String v0, String v1) {
            throw new UnsupportedOperationException();
        }
    }
}

/**
 * End DirectedGraphTest.java
 */
