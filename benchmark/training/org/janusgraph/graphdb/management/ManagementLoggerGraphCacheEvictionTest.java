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
import org.apache.tinkerpop.gremlin.server.Settings;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.configuration.builder.GraphDatabaseConfigurationBuilder;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ManagementLoggerGraphCacheEvictionTest {
    @Test
    public void shouldNotBeAbleToEvictGraphWhenJanusGraphManagerIsNull() {
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        final MapConfiguration config = new MapConfiguration(map);
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfigurationBuilder().build(new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(config)));
        final ManagementSystem mgmt = ((ManagementSystem) (graph.openManagement()));
        mgmt.evictGraphFromCache();
        mgmt.commit();
        Assertions.assertNull(JanusGraphManager.getInstance());
    }

    @Test
    public void graphShouldBeRemovedFromCache() throws InterruptedException {
        final JanusGraphManager jgm = new JanusGraphManager(new Settings());
        Assertions.assertNotNull(jgm);
        Assertions.assertNotNull(JanusGraphManager.getInstance());
        Assertions.assertNull(jgm.getGraph("graph1"));
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
        final MapConfiguration config = new MapConfiguration(map);
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfigurationBuilder().build(new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(config)));
        jgm.putGraph("graph1", graph);
        Assertions.assertEquals("graph1", getGraphName());
        final ManagementSystem mgmt = ((ManagementSystem) (graph.openManagement()));
        mgmt.evictGraphFromCache();
        mgmt.commit();
        // wait for log to be asynchronously pulled
        Thread.sleep(10000);
        Assertions.assertNull(jgm.getGraph("graph1"));
    }
}

