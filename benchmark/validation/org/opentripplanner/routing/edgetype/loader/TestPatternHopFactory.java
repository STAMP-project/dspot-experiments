package org.opentripplanner.routing.edgetype.loader;


import StopTransfer.FORBIDDEN_TRANSFER;
import StopTransfer.PREFERRED_TRANSFER;
import StopTransfer.TIMED_TRANSFER;
import StopTransfer.UNKNOWN_TRANSFER;
import com.google.common.collect.Iterables;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.locationtech.jts.geom.Geometry;
import org.opentripplanner.graph_builder.annotation.GraphBuilderAnnotation;
import org.opentripplanner.graph_builder.annotation.NegativeHopTime;
import org.opentripplanner.gtfs.GtfsContext;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.Trip;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.OptimizeType;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.TransferTable;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.edgetype.TransitBoardAlight;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;
import org.opentripplanner.routing.vertextype.TransitStop;
import org.opentripplanner.util.TestUtils;


public class TestPatternHopFactory extends TestCase {
    private Graph graph;

    private AStar aStar = new AStar();

    private GtfsContext context;

    private String feedId;

    public void testAnnotation() {
        boolean found = false;
        for (GraphBuilderAnnotation annotation : graph.getBuilderAnnotations()) {
            if (annotation instanceof NegativeHopTime) {
                NegativeHopTime nht = ((NegativeHopTime) (annotation));
                TestCase.assertTrue(((nht.st0.getDepartureTime()) > (nht.st1.getArrivalTime())));
                found = true;
            }
        }
        TestCase.assertTrue(found);
    }

    public void testBoardAlight() throws Exception {
        Vertex stop_a_depart = graph.getVertex(((feedId) + ":A_depart"));
        Vertex stop_b_depart = graph.getVertex(((feedId) + ":B_depart"));
        TestCase.assertEquals(1, stop_a_depart.getDegreeOut());
        TestCase.assertEquals(3, stop_b_depart.getDegreeOut());
        for (Edge e : stop_a_depart.getOutgoing()) {
            TestCase.assertEquals(TransitBoardAlight.class, e.getClass());
            TestCase.assertTrue(((TransitBoardAlight) (e)).boarding);
        }
        TransitBoardAlight pb = ((TransitBoardAlight) (stop_a_depart.getOutgoing().iterator().next()));
        Vertex journey_a_1 = pb.getToVertex();
        TestCase.assertEquals(1, journey_a_1.getDegreeIn());
        for (Edge e : journey_a_1.getOutgoing()) {
            if ((e.getToVertex()) instanceof TransitStop) {
                TestCase.assertEquals(TransitBoardAlight.class, e.getClass());
            } else {
                TestCase.assertEquals(PatternHop.class, e.getClass());
            }
        }
    }

    public void testBoardAlightStopIndex() {
        Vertex stop_b_arrive = graph.getVertex(((feedId) + ":C_arrive"));
        Vertex stop_b_depart = graph.getVertex(((feedId) + ":C_depart"));
        Map<TripPattern, Integer> stopIndex = new HashMap<TripPattern, Integer>();
        for (Edge edge : stop_b_depart.getOutgoing()) {
            TransitBoardAlight tba = ((TransitBoardAlight) (edge));
            stopIndex.put(tba.getPattern(), tba.getStopIndex());
        }
        for (Edge edge : stop_b_arrive.getIncoming()) {
            TransitBoardAlight tba = ((TransitBoardAlight) (edge));
            if (stopIndex.containsKey(tba.getPattern()))
                TestCase.assertEquals(((Integer) (stopIndex.get(tba.getPattern()))), new Integer(tba.getStopIndex()));

        }
    }

    public void testRouting() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A"));
        Vertex stop_b = graph.getVertex(((feedId) + ":B"));
        Vertex stop_c = graph.getVertex(((feedId) + ":C"));
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        Vertex stop_e = graph.getVertex(((feedId) + ":E"));
        RoutingRequest options = new RoutingRequest();
        // test feed is designed for instantaneous transfers
        options.transferSlack = 0;
        long startTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 0, 0, 0);
        options.dateTime = startTime;
        ShortestPathTree spt;
        GraphPath path;
        // A to B
        options.setRoutingContext(graph, stop_a, stop_b);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_b, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(6, path.states.size());
        // A to C
        options.setRoutingContext(graph, stop_a, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(8, path.states.size());
        // A to D (change at C)
        options.setRoutingContext(graph, stop_a, stop_d);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_d, false);
        TestCase.assertNotNull(path);
        // there are two paths of different lengths
        // both arrive at 40 minutes after midnight
        List<TransitStop> stops = extractStopVertices(path);
        TestCase.assertEquals(stops.size(), 3);
        TestCase.assertEquals(stops.get(1), stop_c);
        long endTime = startTime + (40 * 60);
        TestCase.assertEquals(endTime, path.getEndTime());
        // A to E (change at C)
        options.setRoutingContext(graph, stop_a, stop_e);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_e, false);
        TestCase.assertNotNull(path);
        stops = extractStopVertices(path);
        TestCase.assertEquals(stops.size(), 3);
        TestCase.assertEquals(stops.get(1), stop_c);
        endTime = startTime + (70 * 60);
        TestCase.assertEquals(endTime, path.getEndTime());
    }

    public void testRoutingOverMidnight() throws Exception {
        // this route only runs on weekdays
        Vertex stop_g = graph.getVertex(((feedId) + ":G_depart"));
        Vertex stop_h = graph.getVertex(((feedId) + ":H_arrive"));
        ShortestPathTree spt;
        GraphPath path;
        RoutingRequest options = new RoutingRequest();
        // Friday evening
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 18, 23, 20, 0);
        options.setRoutingContext(graph, stop_g, stop_h);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_h, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(4, path.states.size());
        // Saturday morning
        long startTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 19, 0, 5, 0);
        options.dateTime = startTime;
        options.setRoutingContext(graph, stop_g.getLabel(), stop_h.getLabel());
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_h, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(4, path.states.size());
        long endTime = path.getEndTime();
        TestCase.assertTrue((endTime < (startTime + (60 * 60))));
    }

    public void testShapeByLocation() throws Exception {
        PatternHop hop;
        Geometry geom;
        // Only route 4 goes through stop G and route 4 should contain only one pattern.
        hop = getHopEdge("G", "4");
        geom = hop.getGeometry();
        TestCase.assertEquals(geom.getLength(), 1.16, 0.1);
        // Only route 1 goes through stop A, and route 1 should contain only one pattern.
        hop = getHopEdge("A", "1");
        geom = hop.getGeometry();
        TestCase.assertEquals(geom.getLength(), 0.01, 0.005);
    }

    public void testShapeByDistance() throws Exception {
        PatternHop hop;
        Geometry geom;
        // Both routes 5 and 6 go through stop I.
        hop = getHopEdge("I", "5");
        geom = hop.getGeometry();
        TestCase.assertEquals(geom.getLength(), 1.73, 0.1);
    }

    public void testPickupDropoff() throws Exception {
        Vertex stop_o = graph.getVertex(((feedId) + ":O_depart"));
        Vertex stop_p = graph.getVertex(((feedId) + ":P"));
        TestCase.assertEquals(2, stop_o.getOutgoing().size());
        long startTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 19, 12, 0, 0);
        RoutingRequest options = new RoutingRequest();
        options.dateTime = startTime;
        options.setRoutingContext(graph, stop_o, stop_p);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(stop_p, false);
        TestCase.assertNotNull(path);
        long endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 19, 12, 10, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
        startTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 19, 12, 0, 1);
        options.dateTime = startTime;
        options.setRoutingContext(graph, stop_o, stop_p);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_p, false);
        TestCase.assertNotNull(path);
        endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 19, 15, 10, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
    }

    public void testTransfers() throws Exception {
        TransferTable transferTable = graph.getTransferTable();
        // create dummy routes and trips
        // In tests we don't patch entities with the feed id, only default agency id is used.
        Route fromRoute = new Route();
        fromRoute.setId(new FeedScopedId("agency", "1"));
        Trip fromTrip = new Trip();
        fromTrip.setId(new FeedScopedId("agency", "1.1"));
        fromTrip.setRoute(fromRoute);
        Route toRoute = new Route();
        toRoute.setId(new FeedScopedId("agency", "2"));
        Trip toTrip = new Trip();
        toTrip.setId(new FeedScopedId("agency", "2.1"));
        toTrip.setRoute(toRoute);
        Trip toTrip2 = new Trip();
        toTrip2.setId(new FeedScopedId("agency", "2.2"));
        toTrip2.setRoute(toRoute);
        // find stops
        Stop stopK = getStop();
        Stop stopN = getStop();
        Stop stopM = getStop();
        TestCase.assertTrue(transferTable.hasPreferredTransfers());
        TestCase.assertEquals(UNKNOWN_TRANSFER, transferTable.getTransferTime(stopN, stopM, fromTrip, toTrip, true));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transferTable.getTransferTime(stopK, stopM, fromTrip, toTrip, true));
        TestCase.assertEquals(PREFERRED_TRANSFER, transferTable.getTransferTime(stopN, stopK, toTrip, toTrip2, true));
        TestCase.assertEquals(TIMED_TRANSFER, transferTable.getTransferTime(stopN, stopK, fromTrip, toTrip, true));
        TestCase.assertEquals(15, transferTable.getTransferTime(stopN, stopK, fromTrip, toTrip2, true));
        TransitStop e_arrive = ((TransitStop) (graph.getVertex(((feedId) + ":E"))));
        TransitStop f_depart = ((TransitStop) (graph.getVertex(((feedId) + ":F"))));
        Edge edge = new TransferEdge(e_arrive, f_depart, 10000, 10000);
        long startTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 18, 0, 50, 0);
        Vertex stop_b = graph.getVertex(((feedId) + ":B_depart"));
        Vertex stop_g = graph.getVertex(((feedId) + ":G_arrive"));
        RoutingRequest options = new RoutingRequest();
        options.dateTime = startTime;
        options.setRoutingContext(graph, stop_b, stop_g);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(stop_g, false);
        TestCase.assertNotNull(path);
        TestCase.assertTrue("expected to use much later trip due to min transfer time", (((path.getEndTime()) - startTime) > ((4.5 * 60) * 60)));
        /* cleanup */
        e_arrive.removeOutgoing(edge);
        f_depart.removeIncoming(edge);
    }

    public void testTraverseMode() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A_depart"));
        Vertex stop_b = graph.getVertex(((feedId) + ":B_arrive"));
        ShortestPathTree spt;
        RoutingRequest options = new RoutingRequest();
        options.setModes(new TraverseModeSet("TRAM,RAIL,SUBWAY,FUNICULAR,GONDOLA"));
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 0, 0, 0, 0);
        options.setRoutingContext(graph, stop_a, stop_b);
        spt = aStar.getShortestPathTree(options);
        // a to b is bus only
        TestCase.assertNull(spt.getPath(stop_b, false));
        options.setModes(new TraverseModeSet("TRAM,RAIL,SUBWAY,FUNICULAR,GONDOLA,CABLE_CAR,BUS"));
        spt = aStar.getShortestPathTree(options);
        TestCase.assertNotNull(spt.getPath(stop_b, false));
    }

    public void testTimelessStops() throws Exception {
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        Vertex stop_c = graph.getVertex(((feedId) + ":C"));
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 10, 0, 0);
        options.setRoutingContext(graph, stop_d, stop_c);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(stop_c, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 11, 0, 0), path.getEndTime());
    }

    public void testTripBikesAllowed() throws Exception {
        Vertex stop_a = graph.getVertex(((feedId) + ":A"));
        Vertex stop_b = graph.getVertex(((feedId) + ":B"));
        Vertex stop_c = graph.getVertex(((feedId) + ":C"));
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        RoutingRequest options = new RoutingRequest();
        options.modes.setWalk(false);
        options.modes.setBicycle(true);
        options.modes.setTransit(true);
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 18, 0, 0, 0);
        options.setRoutingContext(graph, stop_a, stop_b);
        ShortestPathTree spt;
        GraphPath path;
        // route: bikes allowed, trip: no value
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_b, false);
        TestCase.assertNotNull(path);
        // route: bikes allowed, trip: bikes not allowed
        options.setRoutingContext(graph, stop_d, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        TestCase.assertNull(path);
        // route: bikes not allowed, trip: bikes allowed
        options.setRoutingContext(graph, stop_c, stop_d);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_d, false);
        TestCase.assertNotNull(path);
    }

    public void testWheelchairAccessible() throws Exception {
        Vertex near_a = graph.getVertex((("near_1_" + (feedId)) + "_entrance_a"));
        Vertex near_b = graph.getVertex((("near_1_" + (feedId)) + "_entrance_b"));
        Vertex near_c = graph.getVertex((("near_1_" + (feedId)) + "_C"));
        Vertex near_e = graph.getVertex((("near_1_" + (feedId)) + "_E"));
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        Vertex split_d = null;
        for (StreetTransitLink e : Iterables.filter(stop_d.getOutgoing(), StreetTransitLink.class)) {
            split_d = e.getToVertex();
        }
        RoutingRequest options = new RoutingRequest();
        options.wheelchairAccessible = true;
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 18, 0, 0, 0);
        ShortestPathTree spt;
        GraphPath path;
        // stop B is accessible, so there should be a path.
        options.setRoutingContext(graph, near_a, near_b);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(near_b, false);
        TestCase.assertNotNull(path);
        // stop C is not accessible, so there should be no path.
        options.setRoutingContext(graph, near_a, near_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(near_c, false);
        TestCase.assertNull(path);
        // stop E has no accessibility information, but we should still be able to route to it.
        options.setRoutingContext(graph, near_a, near_e);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(near_e, false);
        TestCase.assertNotNull(path);
        // from stop A to stop D would normally be trip 1.1 to trip 2.1, arriving at 00:30. But trip
        // 2 is not accessible, so we'll do 1.1 to 3.1, arriving at 01:00
        GregorianCalendar time = new GregorianCalendar(2009, 8, 18, 0, 0, 0);
        time.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        options.dateTime = TestUtils.toSeconds(time);
        options.setRoutingContext(graph, near_a, split_d);
        spt = aStar.getShortestPathTree(options);
        time.add(Calendar.HOUR, 1);
        time.add(Calendar.SECOND, 1);// for the StreetTransitLink

        path = spt.getPath(split_d, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(TestUtils.toSeconds(time), path.getEndTime());
    }

    public void testRunForTrain() {
        /**
         * This is the notorious Byrd bug: we're going from Q to T at 8:30.
         *  There's a trip from S to T at 8:50 and a second one at 9:50.
         *  To get to S by 8:50, we need to take trip 12.1 from Q to R, and 13.1
         *  from R to S.  If we take the direct-but-slower 11.1, we'll miss
         *  the 8:50 and have to catch the 9:50.
         */
        Vertex destination = graph.getVertex(((feedId) + ":T"));
        RoutingRequest options = new RoutingRequest();
        // test is designed such that transfers must be instantaneous
        options.transferSlack = 0;
        GregorianCalendar startTime = new GregorianCalendar(2009, 11, 2, 8, 30, 0);
        startTime.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        options.dateTime = TestUtils.toSeconds(startTime);
        options.setRoutingContext(graph, ((feedId) + ":Q"), destination.getLabel());
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(destination, false);
        long endTime = path.getEndTime();
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis((endTime * 1000L));
        TestCase.assertTrue(((endTime - (TestUtils.toSeconds(startTime))) < 7200));
    }

    public void testFrequencies() {
        Vertex stop_u = graph.getVertex(((feedId) + ":U_depart"));
        Vertex stop_v = graph.getVertex(((feedId) + ":V_arrive"));
        ShortestPathTree spt;
        GraphPath path;
        RoutingRequest options = new RoutingRequest();
        options.setModes(new TraverseModeSet("TRANSIT"));
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 0, 0, 0);
        options.setRoutingContext(graph, stop_u, stop_v);
        // U to V - original stop times - shouldn't be used
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_v, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(4, path.states.size());
        long endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 6, 40, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
        // U to V - first frequency
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 7, 0, 0);
        options.setRoutingContext(graph, stop_u, stop_v);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_v, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(4, path.states.size());
        endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 7, 40, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
        // U to V - second frequency
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 14, 0, 0);
        options.setRoutingContext(graph, stop_u, stop_v);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_v, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(4, path.states.size());
        endTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 7, 14, 40, 0);
        TestCase.assertEquals(endTime, path.getEndTime());
        // TODO more detailed testing of frequencies
    }

    public void testFewestTransfers() {
        Vertex stop_c = graph.getVertex(((feedId) + ":C"));
        Vertex stop_d = graph.getVertex(((feedId) + ":D"));
        RoutingRequest options = new RoutingRequest();
        options.optimize = OptimizeType.QUICK;
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 16, 0, 0);
        options.setRoutingContext(graph, stop_c, stop_d);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        // when optimizing for speed, take the fast two-bus path
        GraphPath path = spt.getPath(stop_d, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 16, 20, 0), path.getEndTime());
        // when optimizing for fewest transfers, take the slow one-bus path
        options.transferPenalty = 1800;
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_d, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 16, 50, 0), path.getEndTime());
    }

    public void testPathways() throws Exception {
        Vertex entrance = graph.getVertex(((feedId) + ":entrance_a"));
        TestCase.assertNotNull(entrance);
        Vertex stop = graph.getVertex(((feedId) + ":A"));
        TestCase.assertNotNull(stop);
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 16, 0, 0);
        options.setRoutingContext(graph, entrance, stop);
        ShortestPathTree spt = aStar.getShortestPathTree(options);
        GraphPath path = spt.getPath(stop, false);
        TestCase.assertNotNull(path);
        TestCase.assertEquals(TestUtils.dateInSeconds("America/New_York", 2009, 8, 1, 16, 0, 34), path.getEndTime());
    }
}

