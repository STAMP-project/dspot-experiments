package org.opentripplanner.routing.trippattern;


import BikeAccess.ALLOWED;
import BikeAccess.NOT_ALLOWED;
import TripTimes.UNAVAILABLE;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.opentripplanner.gtfs.BikeAccess;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.StopTime;
import org.opentripplanner.model.Trip;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.ServiceDay;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.SimpleConcreteVertex;
import org.opentripplanner.routing.graph.Vertex;


public class TripTimesTest {
    private static final FeedScopedId tripId = new FeedScopedId("agency", "testtrip");

    private static final FeedScopedId stop_a = new FeedScopedId("agency", "A");// 0


    private static final FeedScopedId stop_b = new FeedScopedId("agency", "B");// 1


    private static final FeedScopedId stop_c = new FeedScopedId("agency", "C");// 2


    private static final FeedScopedId stop_d = new FeedScopedId("agency", "D");// 3


    private static final FeedScopedId stop_e = new FeedScopedId("agency", "E");// 4


    private static final FeedScopedId stop_f = new FeedScopedId("agency", "F");// 5


    private static final FeedScopedId stop_g = new FeedScopedId("agency", "G");// 6


    private static final FeedScopedId stop_h = new FeedScopedId("agency", "H");// 7


    private static final FeedScopedId[] stops = new FeedScopedId[]{ TripTimesTest.stop_a, TripTimesTest.stop_b, TripTimesTest.stop_c, TripTimesTest.stop_d, TripTimesTest.stop_e, TripTimesTest.stop_f, TripTimesTest.stop_g, TripTimesTest.stop_h };

    private static final TripTimes originalTripTimes;

    static {
        Trip trip = new Trip();
        trip.setId(TripTimesTest.tripId);
        List<StopTime> stopTimes = new LinkedList<StopTime>();
        for (int i = 0; i < (TripTimesTest.stops.length); ++i) {
            StopTime stopTime = new StopTime();
            Stop stop = new Stop();
            stop.setId(TripTimesTest.stops[i]);
            stopTime.setStop(stop);
            stopTime.setArrivalTime((i * 60));
            stopTime.setDepartureTime((i * 60));
            stopTime.setStopSequence(i);
            stopTimes.add(stopTime);
        }
        originalTripTimes = new TripTimes(trip, stopTimes, new Deduplicator());
    }

    @Test
    public void testBikesAllowed() {
        Graph graph = new Graph();
        Trip trip = new Trip();
        Route route = new Route();
        trip.setRoute(route);
        List<StopTime> stopTimes = Arrays.asList(new StopTime(), new StopTime());
        TripTimes s = new TripTimes(trip, stopTimes, new Deduplicator());
        RoutingRequest request = new RoutingRequest(TraverseMode.BICYCLE);
        Vertex v = new SimpleConcreteVertex(graph, "", 0.0, 0.0);
        request.setRoutingContext(graph, v, v);
        State s0 = new State(request);
        Assert.assertFalse(s.tripAcceptable(s0, 0));
        BikeAccess.setForTrip(trip, ALLOWED);
        Assert.assertTrue(s.tripAcceptable(s0, 0));
        BikeAccess.setForTrip(trip, NOT_ALLOWED);
        Assert.assertFalse(s.tripAcceptable(s0, 0));
    }

    @Test
    public void testStopUpdate() {
        TripTimes updatedTripTimesA = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesA.updateArrivalTime(3, 190);
        updatedTripTimesA.updateDepartureTime(3, 190);
        updatedTripTimesA.updateArrivalTime(5, 311);
        updatedTripTimesA.updateDepartureTime(5, 312);
        Assert.assertEquals(((3 * 60) + 10), updatedTripTimesA.getArrivalTime(3));
        Assert.assertEquals(((3 * 60) + 10), updatedTripTimesA.getDepartureTime(3));
        Assert.assertEquals(((5 * 60) + 11), updatedTripTimesA.getArrivalTime(5));
        Assert.assertEquals(((5 * 60) + 12), updatedTripTimesA.getDepartureTime(5));
    }

    @Test
    public void testPassedUpdate() {
        TripTimes updatedTripTimesA = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesA.updateDepartureTime(0, UNAVAILABLE);
        Assert.assertEquals(UNAVAILABLE, updatedTripTimesA.getDepartureTime(0));
        Assert.assertEquals(60, updatedTripTimesA.getArrivalTime(1));
    }

    @Test
    public void testNonIncreasingUpdate() {
        TripTimes updatedTripTimesA = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesA.updateArrivalTime(1, 60);
        updatedTripTimesA.updateDepartureTime(1, 59);
        Assert.assertFalse(updatedTripTimesA.timesIncreasing());
        TripTimes updatedTripTimesB = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesB.updateDepartureTime(6, 421);
        updatedTripTimesB.updateArrivalTime(7, 420);
        Assert.assertFalse(updatedTripTimesB.timesIncreasing());
    }

    @Test
    public void testDelay() {
        TripTimes updatedTripTimesA = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesA.updateDepartureDelay(0, 10);
        updatedTripTimesA.updateArrivalDelay(6, 13);
        Assert.assertEquals(((0 * 60) + 10), updatedTripTimesA.getDepartureTime(0));
        Assert.assertEquals(((6 * 60) + 13), updatedTripTimesA.getArrivalTime(6));
    }

    @Test
    public void testCancel() {
        TripTimes updatedTripTimesA = new TripTimes(TripTimesTest.originalTripTimes);
        updatedTripTimesA.cancel();
        for (int i = 0; i < ((TripTimesTest.stops.length) - 1); i++) {
            Assert.assertEquals(TripTimesTest.originalTripTimes.getDepartureTime(i), updatedTripTimesA.getScheduledDepartureTime(i));
            Assert.assertEquals(TripTimesTest.originalTripTimes.getArrivalTime(i), updatedTripTimesA.getScheduledArrivalTime(i));
            Assert.assertEquals(UNAVAILABLE, updatedTripTimesA.getDepartureTime(i));
            Assert.assertEquals(UNAVAILABLE, updatedTripTimesA.getArrivalTime(i));
        }
    }

    @Test
    public void testApply() {
        Trip trip = new Trip();
        trip.setId(TripTimesTest.tripId);
        List<StopTime> stopTimes = new LinkedList<StopTime>();
        StopTime stopTime0 = new StopTime();
        StopTime stopTime1 = new StopTime();
        StopTime stopTime2 = new StopTime();
        Stop stop0 = new Stop();
        Stop stop1 = new Stop();
        Stop stop2 = new Stop();
        stop0.setId(TripTimesTest.stops[0]);
        stop1.setId(TripTimesTest.stops[1]);
        stop2.setId(TripTimesTest.stops[2]);
        stopTime0.setStop(stop0);
        stopTime0.setDepartureTime(0);
        stopTime0.setStopSequence(0);
        stopTime1.setStop(stop1);
        stopTime1.setArrivalTime(30);
        stopTime1.setDepartureTime(60);
        stopTime1.setStopSequence(1);
        stopTime2.setStop(stop2);
        stopTime2.setArrivalTime(90);
        stopTime2.setStopSequence(2);
        stopTimes.add(stopTime0);
        stopTimes.add(stopTime1);
        stopTimes.add(stopTime2);
        TripTimes differingTripTimes = new TripTimes(trip, stopTimes, new Deduplicator());
        TripTimes updatedTripTimesA = new TripTimes(differingTripTimes);
        updatedTripTimesA.updateArrivalTime(1, 89);
        updatedTripTimesA.updateDepartureTime(1, 98);
        Assert.assertFalse(updatedTripTimesA.timesIncreasing());
    }

    @Test
    public void testGetRunningTime() {
        for (int i = 0; i < ((TripTimesTest.stops.length) - 1); i++) {
            Assert.assertEquals(60, TripTimesTest.originalTripTimes.getRunningTime(i));
        }
        TripTimes updatedTripTimes = new TripTimes(TripTimesTest.originalTripTimes);
        for (int i = 0; i < ((TripTimesTest.stops.length) - 1); i++) {
            updatedTripTimes.updateDepartureDelay(i, i);
        }
        for (int i = 0; i < ((TripTimesTest.stops.length) - 1); i++) {
            Assert.assertEquals((60 - i), updatedTripTimes.getRunningTime(i));
        }
    }

    @Test
    public void testGetDwellTime() {
        for (int i = 0; i < (TripTimesTest.stops.length); i++) {
            Assert.assertEquals(0, TripTimesTest.originalTripTimes.getDwellTime(i));
        }
        TripTimes updatedTripTimes = new TripTimes(TripTimesTest.originalTripTimes);
        for (int i = 0; i < (TripTimesTest.stops.length); i++) {
            updatedTripTimes.updateArrivalDelay(i, (-i));
        }
        for (int i = 0; i < (TripTimesTest.stops.length); i++) {
            Assert.assertEquals(i, updatedTripTimes.getDwellTime(i));
        }
    }

    @Test
    public void testCallAndRideBoardTime() {
        // times: 0, 60, 120
        ServiceDay sd = Mockito.mock(ServiceDay.class);
        Mockito.when(sd.secondsSinceMidnight(Matchers.anyLong())).thenCallRealMethod();
        int time;
        // time before interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 50, 20, sd, false, 0);
        Assert.assertEquals(60, time);
        // time in interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 70, 20, sd, false, 0);
        Assert.assertEquals(70, time);
        // time would overlap end of interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 105, 20, sd, false, 0);
        Assert.assertTrue((time < 105));
        // time after end of interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 125, 20, sd, false, 0);
        Assert.assertTrue((time < 105));
        // clock time before
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 50, 20, sd, true, 30);
        Assert.assertEquals(60, time);
        // clock time in interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 50, 20, sd, true, 70);
        Assert.assertEquals(70, time);
        // clock time after interval
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 50, 20, sd, true, 130);
        Assert.assertTrue((time < 50));
        // clock time would cause overlap
        time = TripTimesTest.originalTripTimes.getCallAndRideBoardTime(1, 50, 20, sd, true, 105);
        Assert.assertTrue((time < 50));
    }

    @Test
    public void testCallAndRideAlightTime() {
        ServiceDay sd = Mockito.mock(ServiceDay.class);
        Mockito.when(sd.secondsSinceMidnight(Matchers.anyLong())).thenCallRealMethod();
        int time;
        // time after interval
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 130, 20, sd, false, 0);
        Assert.assertEquals(120, time);
        // time in interval
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 110, 20, sd, false, 0);
        Assert.assertEquals(110, time);
        // time in interval, would cause overlap
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 65, 20, sd, false, 0);
        Assert.assertTrue(((time == (-1)) || (time > 65)));
        // time after interval
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 55, 20, sd, false, 0);
        Assert.assertTrue(((time == (-1)) || (time > 65)));
        // clock time after interval
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 130, 20, sd, true, 130);
        Assert.assertEquals((-1), time);
        // clock time before board
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 110, 20, sd, true, 85);
        Assert.assertEquals(110, time);
        // clock time after board
        time = TripTimesTest.originalTripTimes.getCallAndRideAlightTime(2, 110, 20, sd, true, 100);
        Assert.assertEquals((-1), time);
    }
}

