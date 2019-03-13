package org.opentripplanner.routing.edgetype;


import StopTimeUpdate.ScheduleRelationship.SKIPPED;
import TripDescriptor.Builder;
import TripDescriptor.ScheduleRelationship.CANCELED;
import TripDescriptor.ScheduleRelationship.SCHEDULED;
import TripTimes.UNAVAILABLE;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeEvent;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.gtfs.GtfsContext;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;
import org.opentripplanner.routing.trippattern.TripTimes;
import org.opentripplanner.util.TestUtils;


public class TimetableTest {
    private static Graph graph;

    private AStar aStar = new AStar();

    private static GtfsContext context;

    private static Map<FeedScopedId, TripPattern> patternIndex;

    private static TripPattern pattern;

    private static Timetable timetable;

    private static TimeZone timeZone = TimeZone.getTimeZone("America/New_York");

    private static ServiceDate serviceDate = new ServiceDate(2009, 8, 7);

    @Test
    public void testUpdate() {
        TripUpdate tripUpdate;
        TripUpdate.Builder tripUpdateBuilder;
        TripDescriptor.Builder tripDescriptorBuilder;
        StopTimeUpdate.Builder stopTimeUpdateBuilder;
        StopTimeEvent.Builder stopTimeEventBuilder;
        String feedId = TimetableTest.graph.getFeedIds().iterator().next();
        int trip_1_1_index = TimetableTest.timetable.getTripIndex(new FeedScopedId("agency", "1.1"));
        Vertex stop_a = TimetableTest.graph.getVertex((feedId + ":A"));
        Vertex stop_c = TimetableTest.graph.getVertex((feedId + ":C"));
        RoutingRequest options = new RoutingRequest();
        ShortestPathTree spt;
        GraphPath path;
        // non-existing trip
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("b");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        tripUpdate = tripUpdateBuilder.build();
        TripTimes updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNull(updatedTripTimes);
        // update trip with bad data
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(0);
        stopTimeUpdateBuilder.setScheduleRelationship(SKIPPED);
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNull(updatedTripTimes);
        // update trip with non-increasing data
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(2);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        stopTimeEventBuilder.setTime(TestUtils.dateInSeconds("America/New_York", 2009, TestUtils.AUGUST, 7, 0, 10, 1));
        stopTimeEventBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        stopTimeEventBuilder.setTime(TestUtils.dateInSeconds("America/New_York", 2009, TestUtils.AUGUST, 7, 0, 10, 0));
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNull(updatedTripTimes);
        // ---
        long startTime = TestUtils.dateInSeconds("America/New_York", 2009, TestUtils.AUGUST, 7, 0, 0, 0);
        long endTime;
        options.dateTime = startTime;
        // ---
        options.setRoutingContext(TimetableTest.graph, stop_a, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        Assert.assertNotNull(path);
        endTime = startTime + (20 * 60);
        Assert.assertEquals(endTime, path.getEndTime());
        // update trip
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(1);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        stopTimeEventBuilder.setTime(TestUtils.dateInSeconds("America/New_York", 2009, TestUtils.AUGUST, 7, 0, 2, 0));
        stopTimeEventBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        stopTimeEventBuilder.setTime(TestUtils.dateInSeconds("America/New_York", 2009, TestUtils.AUGUST, 7, 0, 2, 0));
        tripUpdate = tripUpdateBuilder.build();
        Assert.assertEquals((20 * 60), TimetableTest.timetable.getTripTimes(trip_1_1_index).getArrivalTime(2));
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        Assert.assertEquals(((20 * 60) + 120), TimetableTest.timetable.getTripTimes(trip_1_1_index).getArrivalTime(2));
        // ---
        options.setRoutingContext(TimetableTest.graph, stop_a, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        Assert.assertNotNull(path);
        endTime = (startTime + (20 * 60)) + 120;
        Assert.assertEquals(endTime, path.getEndTime());
        // cancel trip
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        TripTimes tripTimes = TimetableTest.timetable.getTripTimes(trip_1_1_index);
        for (int i = 0; i < (tripTimes.getNumStops()); i++) {
            Assert.assertEquals(UNAVAILABLE, tripTimes.getDepartureTime(i));
            Assert.assertEquals(UNAVAILABLE, tripTimes.getArrivalTime(i));
        }
        // ---
        options.setRoutingContext(TimetableTest.graph, stop_a, stop_c);
        spt = aStar.getShortestPathTree(options);
        path = spt.getPath(stop_c, false);
        Assert.assertNotNull(path);
        endTime = startTime + (40 * 60);
        Assert.assertEquals(endTime, path.getEndTime());
        // update trip arrival time incorrectly
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(1);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        stopTimeEventBuilder.setDelay(0);
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        // update trip arrival time only
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(2);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        stopTimeEventBuilder.setDelay(1);
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        // update trip departure time only
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(2);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        stopTimeEventBuilder.setDelay((-1));
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        // update trip using stop id
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopId("B");
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        stopTimeEventBuilder.setDelay((-1));
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNotNull(updatedTripTimes);
        TimetableTest.timetable.setTripTimes(trip_1_1_index, updatedTripTimes);
        // update trip arrival time at first stop and make departure time incoherent at second stop
        tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(0);
        stopTimeUpdateBuilder.setStopSequence(1);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        stopTimeEventBuilder.setDelay(0);
        stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder(1);
        stopTimeUpdateBuilder.setStopSequence(2);
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeEventBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        stopTimeEventBuilder.setDelay((-1));
        tripUpdate = tripUpdateBuilder.build();
        updatedTripTimes = TimetableTest.timetable.createUpdatedTripTimes(tripUpdate, TimetableTest.timeZone, TimetableTest.serviceDate);
        Assert.assertNull(updatedTripTimes);
    }
}

