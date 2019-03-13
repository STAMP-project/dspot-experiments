/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.hadoop;


import Cardinality.LIST;
import Direction.BOTH;
import Direction.IN;
import Direction.OUT;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tinkerpop.gremlin.process.computer.Computer;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.janusgraph.graphdb.JanusGraphBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public abstract class AbstractInputFormatIT extends JanusGraphBaseTest {
    @Test
    public void testReadGraphOfTheGods() throws Exception {
        GraphOfTheGodsFactory.load(graph, null, true);
        Assertions.assertEquals(12L, ((long) (graph.traversal().V().count().next())));
        Graph g = getGraph();
        GraphTraversalSource t = g.traversal().withComputer(SparkGraphComputer.class);
        Assertions.assertEquals(12L, ((long) (t.V().count().next())));
    }

    @Test
    public void testReadWideVertexWithManyProperties() throws Exception {
        int numProps = 1 << 16;
        long numV = 1;
        mgmt.makePropertyKey("p").cardinality(LIST).dataType(Integer.class).make();
        mgmt.commit();
        finishSchema();
        for (int j = 0; j < numV; j++) {
            Vertex v = graph.addVertex();
            for (int i = 0; i < numProps; i++) {
                v.property("p", i);
            }
        }
        graph.tx().commit();
        Assertions.assertEquals(numV, ((long) (graph.traversal().V().count().next())));
        Map<String, Object> propertiesOnVertex = graph.traversal().V().valueMap().next();
        List<?> valuesOnP = ((List) (propertiesOnVertex.values().iterator().next()));
        Assertions.assertEquals(numProps, valuesOnP.size());
        for (int i = 0; i < numProps; i++) {
            Assertions.assertEquals(Integer.toString(i), valuesOnP.get(i).toString());
        }
        Graph g = getGraph();
        GraphTraversalSource t = g.traversal().withComputer(SparkGraphComputer.class);
        Assertions.assertEquals(numV, ((long) (t.V().count().next())));
        propertiesOnVertex = t.V().valueMap().next();
        final Set<?> observedValuesOnP = ImmutableSet.copyOf(((List) (propertiesOnVertex.values().iterator().next())));
        Assertions.assertEquals(numProps, observedValuesOnP.size());
        // order may not be preserved in multi-value properties
        Assertions.assertEquals(ImmutableSet.copyOf(valuesOnP), observedValuesOnP, "Unexpected values");
    }

    @Test
    public void testReadSelfEdge() throws Exception {
        GraphOfTheGodsFactory.load(graph, null, true);
        Assertions.assertEquals(12L, ((long) (graph.traversal().V().count().next())));
        // Add a self-loop on sky with edge label "lives"; it's nonsense, but at least it needs no schema changes
        JanusGraphVertex sky = graph.query().has("name", "sky").vertices().iterator().next();
        Assertions.assertNotNull(sky);
        Assertions.assertEquals("sky", sky.value("name"));
        Assertions.assertEquals(1L, sky.query().direction(IN).edgeCount());
        Assertions.assertEquals(0L, sky.query().direction(OUT).edgeCount());
        Assertions.assertEquals(1L, sky.query().direction(BOTH).edgeCount());
        sky.addEdge("lives", sky, "reason", "testReadSelfEdge");
        Assertions.assertEquals(2L, sky.query().direction(IN).edgeCount());
        Assertions.assertEquals(1L, sky.query().direction(OUT).edgeCount());
        Assertions.assertEquals(3L, sky.query().direction(BOTH).edgeCount());
        graph.tx().commit();
        // Read the new edge using the inputformat
        Graph g = getGraph();
        GraphTraversalSource t = g.traversal().withComputer(SparkGraphComputer.class);
        Iterator<Object> edgeIdIterator = t.V().has("name", "sky").bothE().id();
        Assertions.assertNotNull(edgeIdIterator);
        Assertions.assertTrue(edgeIdIterator.hasNext());
        Set<Object> edges = Sets.newHashSet(edgeIdIterator);
        Assertions.assertEquals(2, edges.size());
    }

    @Test
    public void testGeoshapeGetValues() throws Exception {
        GraphOfTheGodsFactory.load(graph, null, true);
        // Read geoshape using the input format
        Graph g = getGraph();
        GraphTraversalSource t = g.traversal().withComputer(SparkGraphComputer.class);
        Iterator<Object> geoIterator = t.E().values("place");
        Assertions.assertNotNull(geoIterator);
        Assertions.assertTrue(geoIterator.hasNext());
        Set<Object> geoShapes = Sets.newHashSet(geoIterator);
        Assertions.assertEquals(3, geoShapes.size());
    }

    @Test
    public void testReadGraphOfTheGodsWithEdgeFiltering() throws Exception {
        GraphOfTheGodsFactory.load(graph, null, true);
        Assertions.assertEquals(17L, ((long) (graph.traversal().E().count().next())));
        // Read graph filtering out "battled" edges.
        Graph g = getGraph();
        Computer computer = Computer.compute(SparkGraphComputer.class).edges(__.bothE().hasLabel(P.neq("battled")));
        GraphTraversalSource t = g.traversal().withComputer(computer);
        Assertions.assertEquals(14L, ((long) (t.E().count().next())));
    }
}

