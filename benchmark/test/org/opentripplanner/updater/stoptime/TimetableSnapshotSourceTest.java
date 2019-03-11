package org.opentripplanner.updater.stoptime;


import RealTimeState.CANCELED;
import RealTimeState.UPDATED;
import StopTimeUpdate.ScheduleRelationship.SKIPPED;
import TripDescriptor.Builder;
import TripDescriptor.ScheduleRelationship.ADDED;
import TripDescriptor.ScheduleRelationship.MODIFIED;
import TripDescriptor.ScheduleRelationship.SCHEDULED;
import TripTimes.UNAVAILABLE;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeEvent;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.gtfs.GtfsContext;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.Trip;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.edgetype.Timetable;
import org.opentripplanner.routing.edgetype.TimetableSnapshot;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.trippattern.TripTimes;
import org.opentripplanner.routing.vertextype.TransitStopDepart;


public class TimetableSnapshotSourceTest {
    private static byte[] cancellation;

    private static Graph graph = new Graph();

    private static boolean fullDataset = false;

    private static GtfsContext context;

    private static ServiceDate serviceDate = new ServiceDate();

    private static String feedId;

    private TimetableSnapshotSource updater;

    @Test
    public void testGetSnapshot() throws InvalidProtocolBufferException {
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(TripUpdate.parseFrom(TimetableSnapshotSourceTest.cancellation)), TimetableSnapshotSourceTest.feedId);
        final TimetableSnapshot snapshot = updater.getTimetableSnapshot();
        Assert.assertNotNull(snapshot);
        Assert.assertSame(snapshot, updater.getTimetableSnapshot());
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(TripUpdate.parseFrom(TimetableSnapshotSourceTest.cancellation)), TimetableSnapshotSourceTest.feedId);
        Assert.assertSame(snapshot, updater.getTimetableSnapshot());
        updater.maxSnapshotFrequency = -1;
        final TimetableSnapshot newSnapshot = updater.getTimetableSnapshot();
        Assert.assertNotNull(newSnapshot);
        Assert.assertNotSame(snapshot, newSnapshot);
    }

    @Test
    public void testHandleCanceledTrip() throws InvalidProtocolBufferException {
        final FeedScopedId tripId = new FeedScopedId(TimetableSnapshotSourceTest.feedId, "1.1");
        final FeedScopedId tripId2 = new FeedScopedId(TimetableSnapshotSourceTest.feedId, "1.2");
        final Trip trip = TimetableSnapshotSourceTest.graph.index.tripForId.get(tripId);
        final TripPattern pattern = TimetableSnapshotSourceTest.graph.index.patternForTrip.get(trip);
        final int tripIndex = pattern.scheduledTimetable.getTripIndex(tripId);
        final int tripIndex2 = pattern.scheduledTimetable.getTripIndex(tripId2);
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(TripUpdate.parseFrom(TimetableSnapshotSourceTest.cancellation)), TimetableSnapshotSourceTest.feedId);
        final TimetableSnapshot snapshot = updater.getTimetableSnapshot();
        final Timetable forToday = snapshot.resolve(pattern, TimetableSnapshotSourceTest.serviceDate);
        final Timetable schedule = snapshot.resolve(pattern, null);
        Assert.assertNotSame(forToday, schedule);
        Assert.assertNotSame(forToday.getTripTimes(tripIndex), schedule.getTripTimes(tripIndex));
        Assert.assertSame(forToday.getTripTimes(tripIndex2), schedule.getTripTimes(tripIndex2));
        final TripTimes tripTimes = forToday.getTripTimes(tripIndex);
        for (int i = 0; i < (tripTimes.getNumStops()); i++) {
            Assert.assertEquals(UNAVAILABLE, tripTimes.getDepartureTime(i));
            Assert.assertEquals(UNAVAILABLE, tripTimes.getArrivalTime(i));
        }
        Assert.assertEquals(CANCELED, tripTimes.getRealTimeState());
    }

    @Test
    public void testHandleDelayedTrip() {
        final FeedScopedId tripId = new FeedScopedId(TimetableSnapshotSourceTest.feedId, "1.1");
        final FeedScopedId tripId2 = new FeedScopedId(TimetableSnapshotSourceTest.feedId, "1.2");
        final Trip trip = TimetableSnapshotSourceTest.graph.index.tripForId.get(tripId);
        final TripPattern pattern = TimetableSnapshotSourceTest.graph.index.patternForTrip.get(trip);
        final int tripIndex = pattern.scheduledTimetable.getTripIndex(tripId);
        final int tripIndex2 = pattern.scheduledTimetable.getTripIndex(tripId2);
        final TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(SCHEDULED);
        final TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
        stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
        stopTimeUpdateBuilder.setStopSequence(2);
        final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
        final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
        arrivalBuilder.setDelay(1);
        departureBuilder.setDelay(1);
        final TripUpdate tripUpdate = tripUpdateBuilder.build();
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(tripUpdate), TimetableSnapshotSourceTest.feedId);
        final TimetableSnapshot snapshot = updater.getTimetableSnapshot();
        final Timetable forToday = snapshot.resolve(pattern, TimetableSnapshotSourceTest.serviceDate);
        final Timetable schedule = snapshot.resolve(pattern, null);
        Assert.assertNotSame(forToday, schedule);
        Assert.assertNotSame(forToday.getTripTimes(tripIndex), schedule.getTripTimes(tripIndex));
        Assert.assertSame(forToday.getTripTimes(tripIndex2), schedule.getTripTimes(tripIndex2));
        Assert.assertEquals(1, forToday.getTripTimes(tripIndex).getArrivalDelay(1));
        Assert.assertEquals(1, forToday.getTripTimes(tripIndex).getDepartureDelay(1));
        Assert.assertEquals(RealTimeState.SCHEDULED, schedule.getTripTimes(tripIndex).getRealTimeState());
        Assert.assertEquals(UPDATED, forToday.getTripTimes(tripIndex).getRealTimeState());
        Assert.assertEquals(RealTimeState.SCHEDULED, schedule.getTripTimes(tripIndex2).getRealTimeState());
        Assert.assertEquals(RealTimeState.SCHEDULED, forToday.getTripTimes(tripIndex2).getRealTimeState());
    }

    @Test
    public void testHandleAddedTrip() throws ParseException {
        // GIVEN
        // Get service date of today because old dates will be purged after applying updates
        final ServiceDate serviceDate = new ServiceDate(Calendar.getInstance());
        final String addedTripId = "added_trip";
        TripUpdate tripUpdate;
        {
            final TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
            tripDescriptorBuilder.setTripId(addedTripId);
            tripDescriptorBuilder.setScheduleRelationship(ADDED);
            tripDescriptorBuilder.setStartDate(serviceDate.getAsString());
            final Calendar calendar = serviceDate.getAsCalendar(TimetableSnapshotSourceTest.graph.getTimeZone());
            final long midnightSecondsSinceEpoch = (calendar.getTimeInMillis()) / 1000;
            final TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
            tripUpdateBuilder.setTrip(tripDescriptorBuilder);
            {
                // Stop A
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("A");
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (30 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (30 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            {
                // Stop C
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("C");
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (40 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (45 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            {
                // Stop E
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("E");
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (55 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (8 * 3600)) + (55 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            tripUpdate = tripUpdateBuilder.build();
        }
        // WHEN
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(tripUpdate), TimetableSnapshotSourceTest.feedId);
        // THEN
        // Find new pattern in graph starting from stop A
        Stop stopA = TimetableSnapshotSourceTest.graph.index.stopForId.get(new FeedScopedId(TimetableSnapshotSourceTest.feedId, "A"));
        TransitStopDepart transitStopDepartA = TimetableSnapshotSourceTest.graph.index.stopVertexForStop.get(stopA).departVertex;
        // Get trip pattern of last (most recently added) outgoing edge
        final List<Edge> outgoingEdges = ((List<Edge>) (transitStopDepartA.getOutgoing()));
        final TripPattern tripPattern = getPattern();
        Assert.assertNotNull("Added trip pattern should be found", tripPattern);
        final TimetableSnapshot snapshot = updater.getTimetableSnapshot();
        final Timetable forToday = snapshot.resolve(tripPattern, serviceDate);
        final Timetable schedule = snapshot.resolve(tripPattern, null);
        Assert.assertNotSame(forToday, schedule);
        final int forTodayAddedTripIndex = forToday.getTripIndex(addedTripId);
        Assert.assertTrue("Added trip should be found in time table for service date", (forTodayAddedTripIndex > (-1)));
        Assert.assertEquals(RealTimeState.ADDED, forToday.getTripTimes(forTodayAddedTripIndex).getRealTimeState());
        final int scheduleTripIndex = schedule.getTripIndex(addedTripId);
        Assert.assertEquals("Added trip should not be found in scheduled time table", (-1), scheduleTripIndex);
    }

    @Test
    public void testHandleModifiedTrip() throws ParseException {
        // TODO
        // GIVEN
        // Get service date of today because old dates will be purged after applying updates
        ServiceDate serviceDate = new ServiceDate(Calendar.getInstance());
        String modifiedTripId = "10.1";
        TripUpdate tripUpdate;
        {
            final TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
            tripDescriptorBuilder.setTripId(modifiedTripId);
            tripDescriptorBuilder.setScheduleRelationship(MODIFIED);
            tripDescriptorBuilder.setStartDate(serviceDate.getAsString());
            final Calendar calendar = serviceDate.getAsCalendar(TimetableSnapshotSourceTest.graph.getTimeZone());
            final long midnightSecondsSinceEpoch = (calendar.getTimeInMillis()) / 1000;
            final TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
            tripUpdateBuilder.setTrip(tripDescriptorBuilder);
            {
                // Stop O
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("O");
                stopTimeUpdateBuilder.setStopSequence(10);
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (30 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (30 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            {
                // Stop C
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("C");
                stopTimeUpdateBuilder.setStopSequence(30);
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (40 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (45 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            {
                // Stop D
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(SKIPPED);
                stopTimeUpdateBuilder.setStopId("D");
                stopTimeUpdateBuilder.setStopSequence(40);
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (50 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (51 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            {
                // Stop P
                final StopTimeUpdate.Builder stopTimeUpdateBuilder = tripUpdateBuilder.addStopTimeUpdateBuilder();
                stopTimeUpdateBuilder.setScheduleRelationship(StopTimeUpdate.ScheduleRelationship.SCHEDULED);
                stopTimeUpdateBuilder.setStopId("P");
                stopTimeUpdateBuilder.setStopSequence(50);
                {
                    // Arrival
                    final StopTimeEvent.Builder arrivalBuilder = stopTimeUpdateBuilder.getArrivalBuilder();
                    arrivalBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (55 * 60)));
                    arrivalBuilder.setDelay(0);
                }
                {
                    // Departure
                    final StopTimeEvent.Builder departureBuilder = stopTimeUpdateBuilder.getDepartureBuilder();
                    departureBuilder.setTime(((midnightSecondsSinceEpoch + (12 * 3600)) + (55 * 60)));
                    departureBuilder.setDelay(0);
                }
            }
            tripUpdate = tripUpdateBuilder.build();
        }
        // WHEN
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(tripUpdate), TimetableSnapshotSourceTest.feedId);
        // THEN
        final TimetableSnapshot snapshot = updater.getTimetableSnapshot();
        // Original trip pattern
        {
            final FeedScopedId tripId = new FeedScopedId(TimetableSnapshotSourceTest.feedId, modifiedTripId);
            final Trip trip = TimetableSnapshotSourceTest.graph.index.tripForId.get(tripId);
            final TripPattern originalTripPattern = TimetableSnapshotSourceTest.graph.index.patternForTrip.get(trip);
            final Timetable originalTimetableForToday = snapshot.resolve(originalTripPattern, serviceDate);
            final Timetable originalTimetableScheduled = snapshot.resolve(originalTripPattern, null);
            Assert.assertNotSame(originalTimetableForToday, originalTimetableScheduled);
            final int originalTripIndexScheduled = originalTimetableScheduled.getTripIndex(modifiedTripId);
            Assert.assertTrue("Original trip should be found in scheduled time table", (originalTripIndexScheduled > (-1)));
            final TripTimes originalTripTimesScheduled = originalTimetableScheduled.getTripTimes(originalTripIndexScheduled);
            Assert.assertFalse("Original trip times should not be canceled in scheduled time table", originalTripTimesScheduled.isCanceled());
            Assert.assertEquals(RealTimeState.SCHEDULED, originalTripTimesScheduled.getRealTimeState());
            final int originalTripIndexForToday = originalTimetableForToday.getTripIndex(modifiedTripId);
            Assert.assertTrue("Original trip should be found in time table for service date", (originalTripIndexForToday > (-1)));
            final TripTimes originalTripTimesForToday = originalTimetableForToday.getTripTimes(originalTripIndexForToday);
            Assert.assertTrue("Original trip times should be canceled in time table for service date", originalTripTimesForToday.isCanceled());
            Assert.assertEquals(CANCELED, originalTripTimesForToday.getRealTimeState());
        }
        // New trip pattern
        {
            final TripPattern newTripPattern = snapshot.getLastAddedTripPattern(TimetableSnapshotSourceTest.feedId, modifiedTripId, serviceDate);
            Assert.assertNotNull("New trip pattern should be found", newTripPattern);
            final Timetable newTimetableForToday = snapshot.resolve(newTripPattern, serviceDate);
            final Timetable newTimetableScheduled = snapshot.resolve(newTripPattern, null);
            Assert.assertNotSame(newTimetableForToday, newTimetableScheduled);
            final int newTimetableForTodayModifiedTripIndex = newTimetableForToday.getTripIndex(modifiedTripId);
            Assert.assertTrue("New trip should be found in time table for service date", (newTimetableForTodayModifiedTripIndex > (-1)));
            Assert.assertEquals(RealTimeState.MODIFIED, newTimetableForToday.getTripTimes(newTimetableForTodayModifiedTripIndex).getRealTimeState());
            Assert.assertEquals("New trip should not be found in scheduled time table", (-1), newTimetableScheduled.getTripIndex(modifiedTripId));
        }
    }

    @Test
    public void testPurgeExpiredData() throws InvalidProtocolBufferException {
        final FeedScopedId tripId = new FeedScopedId(TimetableSnapshotSourceTest.feedId, "1.1");
        final ServiceDate previously = TimetableSnapshotSourceTest.serviceDate.previous().previous();// Just to be safe...

        final Trip trip = TimetableSnapshotSourceTest.graph.index.tripForId.get(tripId);
        final TripPattern pattern = TimetableSnapshotSourceTest.graph.index.patternForTrip.get(trip);
        updater.maxSnapshotFrequency = 0;
        updater.purgeExpiredData = false;
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(TripUpdate.parseFrom(TimetableSnapshotSourceTest.cancellation)), TimetableSnapshotSourceTest.feedId);
        final TimetableSnapshot snapshotA = updater.getTimetableSnapshot();
        updater.purgeExpiredData = true;
        final TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(TripDescriptor.ScheduleRelationship.CANCELED);
        tripDescriptorBuilder.setStartDate(previously.getAsString());
        final TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        final TripUpdate tripUpdate = tripUpdateBuilder.build();
        updater.applyTripUpdates(TimetableSnapshotSourceTest.graph, TimetableSnapshotSourceTest.fullDataset, Arrays.asList(tripUpdate), TimetableSnapshotSourceTest.feedId);
        final TimetableSnapshot snapshotB = updater.getTimetableSnapshot();
        Assert.assertNotSame(snapshotA, snapshotB);
        Assert.assertSame(snapshotA.resolve(pattern, null), snapshotB.resolve(pattern, null));
        Assert.assertSame(snapshotA.resolve(pattern, TimetableSnapshotSourceTest.serviceDate), snapshotB.resolve(pattern, TimetableSnapshotSourceTest.serviceDate));
        Assert.assertNotSame(snapshotA.resolve(pattern, null), snapshotA.resolve(pattern, TimetableSnapshotSourceTest.serviceDate));
        Assert.assertSame(snapshotB.resolve(pattern, null), snapshotB.resolve(pattern, previously));
    }
}

