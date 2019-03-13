package org.opentripplanner.routing.core;


import ScheduleRelationship.SCHEDULED;
import StopTransfer.FORBIDDEN_TRANSFER;
import StopTransfer.TIMED_TRANSFER;
import java.util.List;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.Trip;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.edgetype.TimedTransferEdge;
import org.opentripplanner.routing.edgetype.TransitBoardAlight;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.util.TestUtils;


/**
 * Test transfers, mostly stop-to-stop transfers.
 */
public class TestTransfers extends TestCase {
    private Graph graph;

    private AStar aStar;

    private String feedId;

    public void testStopToStopTransfer() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":N"));
        Vertex destination = graph.getVertex(((feedId) + ":H"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 11, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Add transfer to table, transfer time was 27600 seconds
        Stop stopK = new Stop();
        stopK.setId(new FeedScopedId(feedId, "K"));
        Stop stopF = new Stop();
        stopF.setId(new FeedScopedId(feedId, "F"));
        table.addTransferTime(stopK, stopF, null, null, null, null, 27601);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether a later second trip was taken
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.3", trips.get(1).getId().getId());
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testStopToStopTransferInReverse() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":N"));
        Vertex destination = graph.getVertex(((feedId) + ":H"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.setArriveBy(true);
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 12, 1, 0, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options, true);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Add transfer to table, transfer time was 27600 seconds
        Stop stopK = new Stop();
        stopK.setId(new FeedScopedId(feedId, "K"));
        Stop stopF = new Stop();
        stopF.setId(new FeedScopedId(feedId, "F"));
        table.addTransferTime(stopK, stopF, null, null, null, null, 27601);
        // Plan journey
        path = planJourney(options, true);
        trips = extractTrips(path);
        // Check whether a later second trip was taken
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.3", trips.get(1).getId().getId());
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testStopToStopTransferWithFrequency() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":O"));
        Vertex destination = graph.getVertex(((feedId) + ":V"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 13, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("10.5", trips.get(0).getId().getId());
        TestCase.assertEquals("15.1", trips.get(1).getId().getId());
        // Find state with FrequencyBoard back edge and save time of that state
        long time = -1;
        for (State s : path.states) {
            if (((s.getBackEdge()) instanceof TransitBoardAlight) && (((TransitBoardAlight) (s.getBackEdge())).boarding)) {
                time = s.getTimeSeconds();// find the final board edge, don't break

            }
        }
        TestCase.assertTrue((time >= 0));
        // Add transfer to table such that the next trip will be chosen
        // (there are 3600 seconds between trips), transfer time was 75 seconds
        Stop stopP = new Stop();
        stopP.setId(new FeedScopedId(feedId, "P"));
        Stop stopU = new Stop();
        stopU.setId(new FeedScopedId(feedId, "U"));
        table.addTransferTime(stopP, stopU, null, null, null, null, 3675);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether a later second trip was taken
        TestCase.assertEquals("10.5", trips.get(0).getId().getId());
        TestCase.assertEquals("15.1", trips.get(1).getId().getId());
        // Find state with FrequencyBoard back edge and save time of that state
        long newTime = -1;
        for (State s : path.states) {
            if (((s.getBackEdge()) instanceof TransitBoardAlight) && (((TransitBoardAlight) (s.getBackEdge())).boarding)) {
                newTime = s.getTimeSeconds();// find the final board edge, don't break

            }
        }
        TestCase.assertTrue((newTime >= 0));
        TestCase.assertTrue((newTime > time));
        TestCase.assertEquals(3600, (newTime - time));
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testStopToStopTransferWithFrequencyInReverse() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":U"));
        Vertex destination = graph.getVertex(((feedId) + ":J"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.setArriveBy(true);
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 11, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("15.1", trips.get(0).getId().getId());
        TestCase.assertEquals("5.1", trips.get(1).getId().getId());
        // Find state with FrequencyBoard back edge and save time of that state
        long time = -1;
        for (State s : path.states) {
            if (((s.getBackEdge()) instanceof TransitBoardAlight) && ((s.getBackState()) != null)) {
                time = s.getBackState().getTimeSeconds();
                break;
            }
        }
        TestCase.assertTrue((time >= 0));
        // Add transfer to table such that the next trip will be chosen
        // (there are 3600 seconds between trips), transfer time was 75 seconds
        Stop stopV = new Stop();
        stopV.setId(new FeedScopedId(feedId, "V"));
        Stop stopI = new Stop();
        stopI.setId(new FeedScopedId(feedId, "I"));
        table.addTransferTime(stopV, stopI, null, null, null, null, 3675);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether a later second trip was taken
        TestCase.assertEquals("15.1", trips.get(0).getId().getId());
        TestCase.assertEquals("5.1", trips.get(1).getId().getId());
        // Find state with FrequencyBoard back edge and save time of that state
        long newTime = -1;
        for (State s : path.states) {
            if (((s.getBackEdge()) instanceof TransitBoardAlight) && ((s.getBackState()) != null)) {
                newTime = s.getBackState().getTimeSeconds();
                break;
            }
        }
        TestCase.assertTrue((newTime >= 0));
        TestCase.assertTrue((newTime < time));
        TestCase.assertEquals(3600, (time - newTime));
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testForbiddenStopToStopTransfer() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":N"));
        Vertex destination = graph.getVertex(((feedId) + ":H"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 11, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Add forbidden transfer to table
        Stop stopK = new Stop();
        stopK.setId(new FeedScopedId(feedId, "K"));
        Stop stopF = new Stop();
        stopF.setId(new FeedScopedId(feedId, "F"));
        table.addTransferTime(stopK, stopF, null, null, null, null, FORBIDDEN_TRANSFER);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check that no trip was returned
        TestCase.assertEquals(0, trips.size());
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testForbiddenStopToStopTransferWithFrequencyInReverse() throws Exception {
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":U"));
        Vertex destination = graph.getVertex(((feedId) + ":J"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.setArriveBy(true);
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 11, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("15.1", trips.get(0).getId().getId());
        TestCase.assertEquals("5.1", trips.get(1).getId().getId());
        // Add forbidden transfer to table
        Stop stopV = new Stop();
        stopV.setId(new FeedScopedId(feedId, "V"));
        Stop stopI = new Stop();
        stopI.setId(new FeedScopedId(feedId, "I"));
        table.addTransferTime(stopV, stopI, null, null, null, null, FORBIDDEN_TRANSFER);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check that no trip was returned
        TestCase.assertEquals(0, trips.size());
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }

    public void testTimedStopToStopTransfer() throws Exception {
        ServiceDate serviceDate = new ServiceDate(2009, 7, 11);
        // Replace the transfer table with an empty table
        TransferTable table = new TransferTable();
        Mockito.when(graph.getTransferTable()).thenReturn(table);
        // Compute a normal path between two stops
        Vertex origin = graph.getVertex(((feedId) + ":N"));
        Vertex destination = graph.getVertex(((feedId) + ":H"));
        // Set options like time and routing context
        RoutingRequest options = new RoutingRequest();
        options.dateTime = TestUtils.dateInSeconds("America/New_York", 2009, 7, 11, 11, 11, 0);
        options.setRoutingContext(graph, origin, destination);
        // Plan journey
        GraphPath path;
        List<Trip> trips;
        path = planJourney(options);
        trips = extractTrips(path);
        // Validate result
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Add timed transfer to table
        Stop stopK = new Stop();
        stopK.setId(new FeedScopedId(feedId, "K"));
        Stop stopF = new Stop();
        stopF.setId(new FeedScopedId(feedId, "F"));
        table.addTransferTime(stopK, stopF, null, null, null, null, TIMED_TRANSFER);
        // Don't forget to also add a TimedTransferEdge
        Vertex fromVertex = graph.getVertex(((feedId) + ":K_arrive"));
        Vertex toVertex = graph.getVertex(((feedId) + ":F_depart"));
        TimedTransferEdge timedTransferEdge = new TimedTransferEdge(fromVertex, toVertex);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether the trips are still the same
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Now apply a real-time update: let the to-trip be early by 27600 seconds,
        // resulting in a transfer time of 0 seconds
        Trip trip = graph.index.tripForId.get(new FeedScopedId("agency", "4.2"));
        TripPattern pattern = graph.index.patternForTrip.get(trip);
        applyUpdateToTripPattern(pattern, "4.2", "F", 1, 55200, 55200, SCHEDULED, 0, serviceDate);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether the trips are still the same
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.2", trips.get(1).getId().getId());
        // Now apply a real-time update: let the to-trip be early by 27601 seconds,
        // resulting in a transfer time of -1 seconds
        applyUpdateToTripPattern(pattern, "4.2", "F", 1, 55199, 55199, SCHEDULED, 0, serviceDate);
        // Plan journey
        path = planJourney(options);
        trips = extractTrips(path);
        // Check whether a later second trip was taken
        TestCase.assertEquals("8.1", trips.get(0).getId().getId());
        TestCase.assertEquals("4.3", trips.get(1).getId().getId());
        // "Revert" the real-time update
        applyUpdateToTripPattern(pattern, "4.2", "F", 1, 82800, 82800, SCHEDULED, 0, serviceDate);
        // Remove the timed transfer from the graph
        graph.removeEdge(timedTransferEdge);
        // Revert the graph, thus using the original transfer table again
        Mockito.reset(graph);
    }
}

