package org.opentripplanner.graph_builder.module;


import BikeAccess.ALLOWED;
import BikeAccess.NOT_ALLOWED;
import BikeAccess.UNKNOWN;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.gtfs.BikeAccess;
import org.opentripplanner.gtfs.MockGtfs;
import org.opentripplanner.model.Trip;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.impl.DefaultStreetVertexIndexFactory;


public class GtfsGraphBuilderModuleTest {
    private static final HashMap<Class<?>, Object> _extra = new HashMap<>();

    private GtfsModule builder;

    @Test
    public void testNoBikesByDefault() throws IOException {
        // We configure two trip: one with unknown bikes_allowed and the second with bikes
        // allowed.
        MockGtfs gtfs = getSimpleGtfs();
        gtfs.putTrips(2, "r0", "sid0", "bikes_allowed=0,1");
        gtfs.putStopTimes("t0,t1", "s0,s1");
        List<GtfsBundle> bundleList = GtfsGraphBuilderModuleTest.getGtfsAsBundleList(gtfs);
        bundleList.get(0).setDefaultBikesAllowed(false);
        builder = new GtfsModule(bundleList);
        Graph graph = new Graph();
        builder.buildGraph(graph, GtfsGraphBuilderModuleTest._extra);
        graph.index(new DefaultStreetVertexIndexFactory());
        // Feed id is used instead of the agency id for OBA entities.
        GtfsBundle gtfsBundle = bundleList.get(0);
        GtfsFeedId feedId = gtfsBundle.getFeedId();
        Trip trip = graph.index.tripForId.get(new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t0"));
        TripPattern pattern = graph.index.patternForTrip.get(trip);
        List<Trip> trips = pattern.getTrips();
        Assert.assertEquals(UNKNOWN, BikeAccess.fromTrip(GtfsGraphBuilderModuleTest.withId(trips, new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t0"))));
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(GtfsGraphBuilderModuleTest.withId(trips, new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t1"))));
    }

    @Test
    public void testBikesByDefault() throws IOException {
        // We configure two trip: one with unknown bikes_allowed and the second with no bikes
        // allowed.
        MockGtfs gtfs = getSimpleGtfs();
        gtfs.putTrips(2, "r0", "sid0", "bikes_allowed=0,2");
        gtfs.putStopTimes("t0,t1", "s0,s1");
        List<GtfsBundle> bundleList = GtfsGraphBuilderModuleTest.getGtfsAsBundleList(gtfs);
        bundleList.get(0).setDefaultBikesAllowed(true);
        builder = new GtfsModule(bundleList);
        Graph graph = new Graph();
        builder.buildGraph(graph, GtfsGraphBuilderModuleTest._extra);
        graph.index(new DefaultStreetVertexIndexFactory());
        // Feed id is used instead of the agency id for OBA entities.
        GtfsBundle gtfsBundle = bundleList.get(0);
        GtfsFeedId feedId = gtfsBundle.getFeedId();
        Trip trip = graph.index.tripForId.get(new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t0"));
        TripPattern pattern = graph.index.patternForTrip.get(trip);
        List<Trip> trips = pattern.getTrips();
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(GtfsGraphBuilderModuleTest.withId(trips, new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t0"))));
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(GtfsGraphBuilderModuleTest.withId(trips, new org.opentripplanner.model.FeedScopedId(feedId.getId(), "t1"))));
    }
}

