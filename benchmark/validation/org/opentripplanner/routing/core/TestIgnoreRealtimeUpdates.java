package org.opentripplanner.routing.core;


import junit.framework.TestCase;
import org.mockito.Mockito;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;
import org.opentripplanner.routing.edgetype.TimetableSnapshot;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.updater.stoptime.TimetableSnapshotSource;


/**
 * Test ignoring realtime updates.
 */
public class TestIgnoreRealtimeUpdates extends TestCase {
    public void testIgnoreRealtimeUpdates() throws Exception {
        // Create routing request
        RoutingRequest options = new RoutingRequest();
        // Check that realtime updates are not ignored
        TestCase.assertFalse(options.ignoreRealtimeUpdates);
        // Create (very simple) new graph
        Graph graph = new Graph();
        Stop stop1 = new Stop();
        stop1.setId(new FeedScopedId("agency", "stop1"));
        Stop stop2 = new Stop();
        stop2.setId(new FeedScopedId("agency", "stop2"));
        Vertex from = new org.opentripplanner.routing.vertextype.TransitStop(graph, stop1);
        Vertex to = new org.opentripplanner.routing.vertextype.TransitStop(graph, stop2);
        // Create dummy TimetableSnapshot
        TimetableSnapshot snapshot = new TimetableSnapshot();
        // Mock TimetableSnapshotSource to return dummy TimetableSnapshot
        TimetableSnapshotSource source = Mockito.mock(TimetableSnapshotSource.class);
        Mockito.when(source.getTimetableSnapshot()).thenReturn(snapshot);
        graph.timetableSnapshotSource = source;
        // Create routing context
        RoutingContext rctx = new RoutingContext(options, graph, from, to);
        // Check that the resolver is set as timetable snapshot
        TestCase.assertNotNull(rctx.timetableSnapshot);
        // Now set routing request to ignore realtime updates
        options.ignoreRealtimeUpdates = true;
        // Check that realtime updates are ignored
        TestCase.assertTrue(options.ignoreRealtimeUpdates);
        // Create new routing context
        rctx = new RoutingContext(options, graph, from, to);
        // Check that the timetable snapshot is null in the new routing context
        TestCase.assertNull(rctx.timetableSnapshot);
    }
}

