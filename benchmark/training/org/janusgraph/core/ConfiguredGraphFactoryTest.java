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
package org.janusgraph.core;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.configuration.builder.GraphDatabaseConfigurationBuilder;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.management.ConfigurationManagementGraph;
import org.janusgraph.graphdb.management.JanusGraphManager;
import org.janusgraph.graphdb.management.utils.ConfigurationManagementGraphNotEnabledException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ConfiguredGraphFactoryTest {
    private static final JanusGraphManager gm;

    static {
        gm = new JanusGraphManager(new Settings());
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        final MapConfiguration config = new MapConfiguration(map);
        final StandardJanusGraph graph = new StandardJanusGraph(new GraphDatabaseConfigurationBuilder().build(new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(config)));
        // Instantiate the ConfigurationManagementGraph Singleton
        new ConfigurationManagementGraph(graph);
    }

    @Test
    public void shouldGetConfigurationManagementGraphInstance() throws ConfigurationManagementGraphNotEnabledException {
        final ConfigurationManagementGraph thisInstance = ConfigurationManagementGraph.getInstance();
        Assertions.assertNotNull(thisInstance);
    }

    @Test
    public void shouldOpenGraphUsingConfiguration() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
            ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void graphConfigurationShouldBeWhatWeExpect() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
            ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
            Assertions.assertEquals("graph1", graph.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.GRAPH_NAME));
            Assertions.assertEquals("inmemory", graph.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.STORAGE_BACKEND));
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void shouldCreateAndGetGraphUsingTemplateConfiguration() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            ConfiguredGraphFactory.createTemplateConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.create("graph1")));
            final StandardJanusGraph graph1 = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
            Assertions.assertEquals(graph, graph1);
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void graphConfigurationShouldBeWhatWeExpectWhenUsingTemplateConfiguration() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            ConfiguredGraphFactory.createTemplateConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.create("graph1")));
            final StandardJanusGraph graph1 = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
            Assertions.assertEquals(graph, graph1);
            Assertions.assertEquals("graph1", graph.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.GRAPH_NAME));
            Assertions.assertEquals("inmemory", graph.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.STORAGE_BACKEND));
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void shouldThrowConfigurationDoesNotExistError() {
        RuntimeException graph1 = Assertions.assertThrows(RuntimeException.class, () -> ConfiguredGraphFactory.open("graph1"));
        Assertions.assertEquals(("Please create configuration for this graph using the " + "ConfigurationManagementGraph#createConfiguration API."), graph1.getMessage());
    }

    @Test
    public void shouldThrowTemplateConfigurationDoesNotExistError() {
        RuntimeException graph1 = Assertions.assertThrows(RuntimeException.class, () -> ConfiguredGraphFactory.create("graph1"));
        Assertions.assertEquals(("Please create a template Configuration using the " + "ConfigurationManagementGraph#createTemplateConfiguration API."), graph1.getMessage());
    }

    @Test
    public void shouldFailToOpenNewGraphAfterRemoveConfiguration() {
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
        ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
        ConfiguredGraphFactory.removeConfiguration("graph1");
        RuntimeException graph1 = Assertions.assertThrows(RuntimeException.class, () -> ConfiguredGraphFactory.open("graph1"));
        Assertions.assertEquals(("Please create configuration for this graph using the " + "ConfigurationManagementGraph#createConfiguration API."), graph1.getMessage());
    }

    @Test
    public void shouldFailToCreateGraphAfterRemoveTemplateConfiguration() {
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        ConfiguredGraphFactory.createTemplateConfiguration(new MapConfiguration(map));
        ConfiguredGraphFactory.removeTemplateConfiguration();
        RuntimeException graph1 = Assertions.assertThrows(RuntimeException.class, () -> ConfiguredGraphFactory.create("graph1"));
        Assertions.assertEquals(("Please create a template Configuration using the " + "ConfigurationManagementGraph#createTemplateConfiguration API."), graph1.getMessage());
    }

    @Test
    public void shouldFailToOpenGraphAfterRemoveConfiguration() {
        final Map<String, Object> map = new HashMap<>();
        map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
        map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
        ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
        ConfiguredGraphFactory.removeConfiguration("graph1");
        RuntimeException graph1 = Assertions.assertThrows(RuntimeException.class, () -> ConfiguredGraphFactory.create("graph1"));
        Assertions.assertEquals(("Please create a template Configuration using the " + "ConfigurationManagementGraph#createTemplateConfiguration API."), graph1.getMessage());
    }

    @Test
    public void updateConfigurationShouldRemoveGraphFromCache() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
            ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "bogusBackend");
            ConfiguredGraphFactory.updateConfiguration("graph1", new MapConfiguration(map));
            Assertions.assertNull(ConfiguredGraphFactoryTest.gm.getGraph("graph1"));
            // we should throw an error since the config has been updated and we are attempting
            // to open a bogus backend
            IllegalArgumentException graph1 = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                final StandardJanusGraph graph2 = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            });
            Assertions.assertEquals("Could not find implementation class: bogusBackend", graph1.getMessage());
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void removeConfigurationShouldRemoveGraphFromCache() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
            ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertNotNull(graph);
            ConfiguredGraphFactory.removeConfiguration("graph1");
            Assertions.assertNull(ConfiguredGraphFactoryTest.gm.getGraph("graph1"));
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void shouldBeAbleToRemoveBogusConfiguration() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "bogusBackend");
            map.put(GraphDatabaseConfiguration.GRAPH_NAME.toStringWithoutRoot(), "graph1");
            ConfiguredGraphFactory.createConfiguration(new MapConfiguration(map));
            ConfiguredGraphFactory.removeConfiguration("graph1");
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }

    @Test
    public void shouldCreateTwoGraphsUsingSameTemplateConfiguration() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            ConfiguredGraphFactory.createTemplateConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph1 = ((StandardJanusGraph) (ConfiguredGraphFactory.create("graph1")));
            final StandardJanusGraph graph2 = ((StandardJanusGraph) (ConfiguredGraphFactory.create("graph2")));
            Assertions.assertNotNull(graph1);
            Assertions.assertNotNull(graph2);
            Assertions.assertEquals("graph1", graph1.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.GRAPH_NAME));
            Assertions.assertEquals("graph2", graph2.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.GRAPH_NAME));
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.removeConfiguration("graph2");
            ConfiguredGraphFactory.close("graph1");
            ConfiguredGraphFactory.close("graph2");
        }
    }

    @Test
    public void ensureCallingGraphCloseResultsInNewGraphReferenceOnNextCallToOpen() throws Exception {
        try {
            final Map<String, Object> map = new HashMap<>();
            map.put(GraphDatabaseConfiguration.STORAGE_BACKEND.toStringWithoutRoot(), "inmemory");
            ConfiguredGraphFactory.createTemplateConfiguration(new MapConfiguration(map));
            final StandardJanusGraph graph = ((StandardJanusGraph) (ConfiguredGraphFactory.create("graph1")));
            Assertions.assertNotNull(graph);
            Assertions.assertEquals("graph1", graph.getConfiguration().getConfiguration().get(GraphDatabaseConfiguration.GRAPH_NAME));
            graph.close();
            Assertions.assertTrue(graph.isClosed());
            final StandardJanusGraph newGraph = ((StandardJanusGraph) (ConfiguredGraphFactory.open("graph1")));
            Assertions.assertFalse(newGraph.isClosed());
        } finally {
            ConfiguredGraphFactory.removeConfiguration("graph1");
            ConfiguredGraphFactory.close("graph1");
        }
    }
}

