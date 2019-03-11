package org.opentripplanner.routing.algorithm;


import junit.framework.TestCase;
import org.opentripplanner.ConstantsForTests;
import org.opentripplanner.model.Route;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.edgetype.PatternHop;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;
import org.opentripplanner.util.TestUtils;


public class TestBanning extends TestCase {
    AStar aStar = new AStar();

    public void testBannedRoutes() {
        Graph graph = ConstantsForTests.getInstance().getPortlandGraph();
        String feedId = graph.getFeedIds().iterator().next();
        RoutingRequest options = new RoutingRequest();
        Vertex start = graph.getVertex((feedId + ":8371"));
        Vertex end = graph.getVertex((feedId + ":8374"));
        options.dateTime = TestUtils.dateInSeconds("America/Los_Angeles", 2009, 11, 1, 12, 34, 25);
        // must set routing context _after_ options is fully configured (time)
        options.setRoutingContext(graph, start, end);
        ShortestPathTree spt = null;
        /* The MAX Red, Blue, and Green lines all run along the same trackage between the stops 8374 and 8371. Together, they form the white line. No,
        wait, that's light. They make a pretty good test case for banned routes, since if one is banned, you can always take another.
         */
        String[][] maxLines = new String[][]{ new String[]{ "MAX Red Line", null }, new String[]{ "MAX Blue Line", null }, new String[]{ "MAX Green Line", null }, new String[]{ null, "90" }, new String[]{ null, "100" }, new String[]{ null, "200" } };
        for (int i = 0; i < (maxLines.length); ++i) {
            String lineName = maxLines[i][0];
            String lineId = maxLines[i][1];
            String routeSpecStr = ((feedId + "_") + (lineName != null ? lineName : "")) + (lineId != null ? "_" + lineId : "");
            options.setBannedRoutes(routeSpecStr);
            spt = aStar.getShortestPathTree(options);
            GraphPath path = spt.getPath(end, true);
            for (State s : path.states) {
                if ((s.getBackEdge()) instanceof PatternHop) {
                    PatternHop e = ((PatternHop) (s.getBackEdge()));
                    Route route = e.getPattern().route;
                    TestCase.assertFalse(options.bannedRoutes.matches(route));
                    boolean foundMaxLine = false;
                    for (int j = 0; j < (maxLines.length); ++j) {
                        if (j != i) {
                            if (e.getName().equals(maxLines[j][0])) {
                                foundMaxLine = true;
                            }
                        }
                    }
                    TestCase.assertTrue(foundMaxLine);
                }
            }
        }
    }

    public void testWhiteListedRoutes() {
        Graph graph = ConstantsForTests.getInstance().getPortlandGraph();
        String feedId = graph.getFeedIds().iterator().next();
        RoutingRequest options = new RoutingRequest();
        Vertex start = graph.getVertex((feedId + ":8371"));
        Vertex end = graph.getVertex((feedId + ":8374"));
        options.dateTime = TestUtils.dateInSeconds("America/Los_Angeles", 2009, 11, 1, 12, 34, 25);
        // must set routing context _after_ options is fully configured (time)
        options.setRoutingContext(graph, start, end);
        ShortestPathTree spt = null;
        /* Same as testBannedRoutes, only for whitelisted routes. The last three entries in maxLines are removed, because
        it only matches against lineName, not lineId.
         */
        String[][] maxLines = new String[][]{ new String[]{ "MAX Red Line", null }, new String[]{ "MAX Blue Line", null }, new String[]{ "MAX Green Line", null } };
        for (int i = 0; i < (maxLines.length); ++i) {
            String lineName = maxLines[i][0];
            String lineId = maxLines[i][1];
            String routeSpecStr = ((feedId + "_") + (lineName != null ? lineName : "")) + (lineId != null ? "_" + lineId : "");
            options.setWhiteListedRoutes(routeSpecStr);
            spt = aStar.getShortestPathTree(options);
            GraphPath path = spt.getPath(end, true);
            for (State s : path.states) {
                if ((s.getBackEdge()) instanceof PatternHop) {
                    PatternHop e = ((PatternHop) (s.getBackEdge()));
                    Route route = e.getPattern().route;
                    TestCase.assertTrue(options.whiteListedRoutes.matches(route));
                    boolean notFoundMaxLine = true;
                    boolean foundMaxLine = false;
                    for (int j = 0; j < (maxLines.length); ++j) {
                        if (e.getName().equals(maxLines[j][0])) {
                            if (j != i) {
                                notFoundMaxLine = false;
                            } else {
                                foundMaxLine = true;
                            }
                        }
                    }
                    TestCase.assertTrue(notFoundMaxLine);
                    TestCase.assertTrue(foundMaxLine);
                }
            }
        }
    }

    public void testWholeBannedTrips() {
        doTestBannedTrips(false, 42);
    }

    public void testPartialBannedTrips() {
        doTestBannedTrips(true, 43);
    }
}

