package org.opentripplanner.routing.impl;


import InputStreamGraphSource.FileFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Test;
import org.opentripplanner.graph_builder.module.EmbedConfig;
import org.opentripplanner.routing.error.GraphNotFoundException;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.services.GraphService;

import static InputStreamGraphSource.GRAPH_FILENAME;


public class GraphServiceTest extends TestCase {
    File basePath;

    Graph emptyGraph;

    Graph smallGraph;

    byte[] emptyGraphData;

    byte[] smallGraphData;

    @Test
    public final void testGraphServiceMemory() {
        GraphService graphService = new GraphService();
        graphService.registerGraph("A", new MemoryGraphSource("A", emptyGraph));
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        Graph graph = graphService.getRouter("A").graph;
        TestCase.assertNotNull(graph);
        TestCase.assertEquals(emptyGraph, graph);
        TestCase.assertEquals("A", emptyGraph.routerId);
        try {
            graph = graphService.getRouter("inexistant").graph;
            TestCase.assertTrue(false);// Should not be there

        } catch (GraphNotFoundException e) {
        }
        graphService.setDefaultRouterId("A");
        graph = graphService.getRouter().graph;
        TestCase.assertEquals(emptyGraph, graph);
        graphService.registerGraph("B", new MemoryGraphSource("B", smallGraph));
        TestCase.assertEquals(2, graphService.getRouterIds().size());
        graph = graphService.getRouter("B").graph;
        TestCase.assertNotNull(graph);
        TestCase.assertEquals(smallGraph, graph);
        TestCase.assertEquals("B", graph.routerId);
        graphService.evictRouter("A");
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        try {
            graph = graphService.getRouter("A").graph;
            TestCase.assertTrue(false);// Should not be there

        } catch (GraphNotFoundException e) {
        }
        try {
            graph = graphService.getRouter().graph;
            TestCase.assertTrue(false);// Should not be there

        } catch (GraphNotFoundException e) {
        }
        graphService.evictAll();
        TestCase.assertEquals(0, graphService.getRouterIds().size());
    }

    @Test
    public final void testGraphServiceFile() throws IOException {
        // Create a GraphService and a GraphSourceFactory
        GraphService graphService = new GraphService();
        InputStreamGraphSource.FileFactory graphSourceFactory = new InputStreamGraphSource.FileFactory(basePath);
        graphSourceFactory.save("A", new ByteArrayInputStream(emptyGraphData));
        // Check if the graph has been saved
        TestCase.assertTrue(new File(new File(basePath, "A"), GRAPH_FILENAME).canRead());
        // Register this empty graph, reloading it from disk
        boolean registered = graphService.registerGraph("A", graphSourceFactory.createGraphSource("A"));
        TestCase.assertTrue(registered);
        // Check if the loaded graph is the one we saved earlier
        Graph graph = graphService.getRouter("A").graph;
        TestCase.assertNotNull(graph);
        TestCase.assertEquals("A", graph.routerId);
        TestCase.assertEquals(0, graph.getVertices().size());
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        // Save A again, with more data this time
        int verticesCount = smallGraph.getVertices().size();
        int edgesCount = smallGraph.getEdges().size();
        graphSourceFactory.save("A", new ByteArrayInputStream(smallGraphData));
        // Force a reload, get again the graph
        graphService.reloadGraphs(false, true);
        graph = graphService.getRouter("A").graph;
        // Check if loaded graph is the one modified
        TestCase.assertEquals(verticesCount, graph.getVertices().size());
        TestCase.assertEquals(edgesCount, graph.getEdges().size());
        // Remove the file from disk and reload
        boolean deleted = new File(new File(basePath, "A"), GRAPH_FILENAME).delete();
        TestCase.assertTrue(deleted);
        graphService.reloadGraphs(false, true);
        // Check that the graph have been evicted
        TestCase.assertEquals(0, graphService.getRouterIds().size());
        // Register it manually
        registered = graphService.registerGraph("A", graphSourceFactory.createGraphSource("A"));
        // Check that it fails again (file still deleted)
        TestCase.assertFalse(registered);
        TestCase.assertEquals(0, graphService.getRouterIds().size());
        // Re-save the graph file, register it again
        graphSourceFactory.save("A", new ByteArrayInputStream(smallGraphData));
        registered = graphService.registerGraph("A", graphSourceFactory.createGraphSource("A"));
        // This time registering is OK
        TestCase.assertTrue(registered);
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        // Evict the graph, should be OK
        boolean evicted = graphService.evictRouter("A");
        TestCase.assertTrue(evicted);
        TestCase.assertEquals(0, graphService.getRouterIds().size());
    }

    @Test
    public final void testGraphServiceAutoscan() throws IOException {
        // Check for no graphs
        GraphService graphService = new GraphService(false);
        GraphScanner graphScanner = new GraphScanner(graphService, basePath, true);
        graphScanner.startup();
        TestCase.assertEquals(0, graphService.getRouterIds().size());
        System.out.println("------------------------------------------");
        // Add a single default graph
        InputStreamGraphSource.FileFactory graphSourceFactory = new InputStreamGraphSource.FileFactory(basePath);
        graphSourceFactory.save("", new ByteArrayInputStream(smallGraphData));
        // Check that the single graph is there
        graphService = new GraphService(false);
        graphScanner = new GraphScanner(graphService, basePath, true);
        graphScanner.startup();
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        TestCase.assertEquals("", graphService.getRouter().graph.routerId);
        TestCase.assertEquals("", graphService.getRouter("").graph.routerId);
        System.out.println("------------------------------------------");
        // Add another graph in a sub-directory
        graphSourceFactory.save("A", new ByteArrayInputStream(smallGraphData));
        graphService = new GraphService(false);
        graphScanner = new GraphScanner(graphService, basePath, true);
        graphScanner.startup();
        TestCase.assertEquals(2, graphService.getRouterIds().size());
        TestCase.assertEquals("", graphService.getRouter().graph.routerId);
        TestCase.assertEquals("A", graphService.getRouter("A").graph.routerId);
        System.out.println("------------------------------------------");
        // Remove default Graph
        new File(basePath, GRAPH_FILENAME).delete();
        // Check that default is A this time
        graphService = new GraphService(false);
        graphScanner = new GraphScanner(graphService, basePath, true);
        graphScanner.startup();
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        TestCase.assertEquals("A", graphService.getRouter().graph.routerId);
        TestCase.assertEquals("A", graphService.getRouter("A").graph.routerId);
    }

    @Test
    public final void testGraphServiceMemoryRouterConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode buildConfig = MissingNode.getInstance();
        ObjectNode routerConfig = mapper.createObjectNode();
        routerConfig.put("timeout", 8);
        EmbedConfig embedConfig = new EmbedConfig(buildConfig, routerConfig);
        embedConfig.buildGraph(emptyGraph, null);
        GraphService graphService = new GraphService();
        graphService.registerGraph("A", new MemoryGraphSource("A", emptyGraph));
        TestCase.assertEquals(1, graphService.getRouterIds().size());
        Graph graph = graphService.getRouter("A").graph;
        TestCase.assertNotNull(graph);
        TestCase.assertEquals(emptyGraph, graph);
        TestCase.assertEquals("A", emptyGraph.routerId);
        JsonNode graphRouterConfig = mapper.readTree(graph.routerConfig);
        TestCase.assertEquals(graphRouterConfig, routerConfig);
        TestCase.assertEquals(graphRouterConfig.get("timeout"), routerConfig.get("timeout"));
    }
}

