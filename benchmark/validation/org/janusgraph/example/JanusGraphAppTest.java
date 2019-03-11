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
package org.janusgraph.example;


import Cardinality.SINGLE;
import Multiplicity.MANY2ONE;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JanusGraphAppTest {
    protected static final String CONF_FILE = "conf/jgex-inmemory.properties";

    @Test
    public void createSchema() throws ConfigurationException {
        final JanusGraphApp app = new JanusGraphApp(JanusGraphAppTest.CONF_FILE);
        final GraphTraversalSource g = app.openGraph();
        app.createSchema();
        final JanusGraph janusGraph = ((JanusGraph) (g.getGraph()));
        final JanusGraphManagement management = janusGraph.openManagement();
        final List<String> vertexLabels = StreamSupport.stream(management.getVertexLabels().spliterator(), false).map(Namifiable::name).collect(Collectors.toList());
        final List<String> expectedVertexLabels = Stream.of("titan", "location", "god", "demigod", "human", "monster").collect(Collectors.toList());
        Assertions.assertTrue(vertexLabels.containsAll(expectedVertexLabels));
        final List<String> edgeLabels = StreamSupport.stream(management.getRelationTypes(EdgeLabel.class).spliterator(), false).map(Namifiable::name).collect(Collectors.toList());
        final List<String> expectedEdgeLabels = Stream.of("father", "mother", "brother", "pet", "lives", "battled").collect(Collectors.toList());
        Assertions.assertTrue(edgeLabels.containsAll(expectedEdgeLabels));
        final EdgeLabel father = management.getEdgeLabel("father");
        Assertions.assertTrue(father.isDirected());
        Assertions.assertFalse(father.isUnidirected());
        Assertions.assertEquals(MANY2ONE, father.multiplicity());
        final List<String> propertyKeys = StreamSupport.stream(management.getRelationTypes(PropertyKey.class).spliterator(), false).map(Namifiable::name).collect(Collectors.toList());
        final List<String> expectedPropertyKeys = Stream.of("name", "age", "time", "place", "reason").collect(Collectors.toList());
        Assertions.assertTrue(propertyKeys.containsAll(expectedPropertyKeys));
        final PropertyKey place = management.getPropertyKey("place");
        Assertions.assertEquals(SINGLE, place.cardinality());
        Assertions.assertEquals(Geoshape.class, place.dataType());
        final JanusGraphIndex nameIndex = management.getGraphIndex("nameIndex");
        Assertions.assertTrue(nameIndex.isCompositeIndex());
        Assertions.assertTrue(nameIndex.getIndexedElement().equals(JanusGraphVertex.class));
        final PropertyKey[] nameIndexKeys = nameIndex.getFieldKeys();
        Assertions.assertEquals(1, nameIndexKeys.length);
        Assertions.assertEquals("name", nameIndexKeys[0].name());
    }
}

