package org.opentripplanner.routing.edgetype.loader;


import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.edgetype.PatternHop;
import org.opentripplanner.routing.edgetype.TransitBoardAlight;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;
import org.opentripplanner.routing.vertextype.TransitStop;
import org.opentripplanner.util.TestUtils;


public class TestHopFactory extends TestCase {
    private Graph graph;

    private AStar aStar = new AStar();

    private String feedId;

    public void testBoardAlight() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A_depart"));
        Vertex stop_b_depart = graph.getVertex(((feedId) + ":B_depart"));
        TestCase.assertEquals(1, stop_a.getDegreeOut());
        TestCase.assertEquals(3, stop_b_depart.getDegreeOut());
        for (Edge e : stop_a.getOutgoing()) {
            TestCase.assertEquals(TransitBoardAlight.class, e.getClass());
            TestCase.assertTrue(((TransitBoardAlight) (e)).boarding);
        }
        // TODO: could this ever be a PatternAlight? I think not.
        TransitBoardAlight pb = ((TransitBoardAlight) (stop_a.getOutgoing().iterator().next()));
        Vertex journey_a_1 = pb.getToVertex();
        TestCase.assertEquals(1, journey_a_1.getDegreeIn());
        for (Edge e : journey_a_1.getOutgoing()) {
            if ((e.getToVertex()) instanceof TransitStop) {
                TestCase.assertEquals(TransitBoardAlight.class, e.getClass());
                TestCase.assertTrue(((TransitBoardAlight) (e)).boarding);
            } else {
                TestCase.assertEquals(PatternHop.class, e.getClass());
            }
        }
    }

    public void testDwell() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A_depart"));
        Vertex stop_c = graph.getVertex(((feedId) + ":C_arrive"));
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 8, 0, 0);
        options.setRoutingContext(graph, stop_a, stop_c);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(stop_c, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(6, path.states.size());
        long endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 8, 30, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
    }

    public void testRouting() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A"));
        Vertex stop_b = graph.getVertex(((feedId) + ":B"));
        Vertex stop_c = graph.getVertex(((feedId) + ":C"));
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        Vertex stop_e = graph.getVertex(((feedId) + ":E"));
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 0, 0, 0);
        ShortestPathTree spt;
        GraphPath path;
        // A to B
        options.setRoutingContext(graph, stop_a, stop_b);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_b, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(extractStopVertices(path), Lists.newArrayList(stop_a, stop_b));
        // A to C
        options.setRoutingContext(graph, stop_a, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(extractStopVertices(path), Lists.newArrayList(stop_a, stop_c));
        // A to D
        options.setRoutingContext(graph, stop_a, stop_d);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_d, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(extractStopVertices(path), Lists.newArrayList(stop_a, stop_c, stop_d));
        long endTime = (TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 0, 0, 0)) + (40 * 60);
        TestCase.assertEquals(endTime, path.getEndTime());
        // A to E
        options.setRoutingContext(graph, stop_a, stop_e);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_e, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(extractStopVertices(path), Lists.newArrayList(stop_a, stop_c, stop_e));
        endTime = (TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 0, 0, 0)) + (70 * 60);
        TestCase.assertEquals(endTime, path.getEndTime());
    }
}

