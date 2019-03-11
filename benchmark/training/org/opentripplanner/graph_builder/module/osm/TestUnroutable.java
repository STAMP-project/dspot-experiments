package org.opentripplanner.graph_builder.module.osm;


import TraverseMode.BICYCLE;
import junit.framework.TestCase;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;


/**
 * Verify that OSM ways that represent proposed or as yet unbuilt roads are not used for routing.
 * This tests functionality in or around the method isWayRoutable() in the OSM graph builder module.
 *
 * @author abyrd
 */
public class TestUnroutable extends TestCase {
    private Graph graph = new Graph();

    private AStar aStar = new AStar();

    /**
     * Search for a path across the Willamette river. This OSM data includes a bridge that is not yet built and is
     * therefore tagged highway=construction.
     * TODO also test unbuilt, proposed, raceways etc.
     */
    public void testOnBoardRouting() throws Exception {
        RoutingRequest options = new RoutingRequest();
        Vertex from = graph.getVertex("osm:node:2003617278");
        Vertex to = graph.getVertex("osm:node:40446276");
        options.setRoutingContext(graph, from, to);
        options.setMode(BICYCLE);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(to, false);
        // At the time of writing this test, the router simply doesn't find a path at all when highway=construction
        // is filtered out, thus the null check.
        if (path != null) {
            for (Edge edge : path.edges) {
                TestCase.assertFalse("Path should not use the as-yet unbuilt Tilikum Crossing bridge.", "Tilikum Crossing".equals(edge.getName()));
            }
        }
    }
}

