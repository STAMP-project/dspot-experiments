/**
 * Copyright 2018 JanusGraph Authors
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
package org.janusgraph.graphdb.management;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.configuration.builder.GraphDatabaseConfigurationBuilder;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConfigurationManagementGraphTest {
    @Test
    public void shouldReindexIfPropertyKeyExists() {
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        final MapConfiguration config = new MapConfiguration(map);
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfigurationBuilder().build(new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(config)));
        final String propertyKeyName = "Created_Using_Template";
        final Class dataType = Boolean.class;
        JanusGraphManagement management = graph.openManagement();
        management.makePropertyKey(propertyKeyName).dataType(dataType).make();
        management.commit();
        // Instantiate the ConfigurationManagementGraph Singleton
        // This is purposefully done after a property key is created to ensure that a REDINDEX is initiated
        new ConfigurationManagementGraph(graph);
        management = graph.openManagement();
        final JanusGraphIndex index = management.getGraphIndex("Created_Using_Template_Index");
        final PropertyKey propertyKey = management.getPropertyKey("Created_Using_Template");
        Assertions.assertNotNull(index);
        Assertions.assertNotNull(propertyKey);
        Assertions.assertEquals(ENABLED, index.getIndexStatus(propertyKey));
        management.commit();
    }

    @Test
    public void shouldCloseAllTxsIfIndexExists() {
        final StandardJanusGraph graph = ((StandardJanusGraph) (JanusGraphFactory.open("inmemory")));
        // Emulate ConfigurationManagementGraph indices already exists
        JanusGraphManagement management = graph.openManagement();
        PropertyKey key = management.makePropertyKey("some_property").dataType(String.class).make();
        management.buildIndex("Created_Using_Template_Index", Vertex.class).addKey(key).buildCompositeIndex();
        management.buildIndex("Template_Index", Vertex.class).addKey(key).buildCompositeIndex();
        management.buildIndex("Graph_Name_Index", Vertex.class).addKey(key).buildCompositeIndex();
        management.commit();
        new ConfigurationManagementGraph(graph);
        Assertions.assertEquals(0, graph.getOpenTransactions().size());
    }
}

