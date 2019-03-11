package org.opentripplanner.routing.edgetype;


import ScheduleRelationship.CANCELED;
import TripDescriptor.Builder;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.gtfs.GtfsContext;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.graph.Graph;


public class TimetableSnapshotTest {
    private static Graph graph;

    private static GtfsContext context;

    private static Map<FeedScopedId, TripPattern> patternIndex;

    private static TimeZone timeZone = TimeZone.getTimeZone("GMT");

    @Test
    public void testCompare() {
        Timetable orig = new Timetable(null);
        Timetable a = new Timetable(orig, new ServiceDate().previous());
        Timetable b = new Timetable(orig, new ServiceDate());
        Assert.assertEquals((-1), new TimetableSnapshot.SortedTimetableComparator().compare(a, b));
    }

    @Test
    public void testResolve() {
        ServiceDate today = new ServiceDate();
        ServiceDate yesterday = today.previous();
        ServiceDate tomorrow = today.next();
        TripPattern pattern = TimetableSnapshotTest.patternIndex.get(new FeedScopedId("agency", "1.1"));
        TimetableSnapshot resolver = new TimetableSnapshot();
        Timetable scheduled = resolver.resolve(pattern, today);
        Assert.assertEquals(scheduled, resolver.resolve(pattern, null));
        TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        TripUpdate tripUpdate = tripUpdateBuilder.build();
        // add a new timetable for today
        updateResolver(resolver, pattern, tripUpdate, "agency", today);
        Timetable forNow = resolver.resolve(pattern, today);
        Assert.assertEquals(scheduled, resolver.resolve(pattern, yesterday));
        Assert.assertNotSame(scheduled, forNow);
        Assert.assertEquals(scheduled, resolver.resolve(pattern, tomorrow));
        Assert.assertEquals(scheduled, resolver.resolve(pattern, null));
        // add a new timetable for yesterday
        updateResolver(resolver, pattern, tripUpdate, "agency", yesterday);
        Timetable forYesterday = resolver.resolve(pattern, yesterday);
        Assert.assertNotSame(scheduled, forYesterday);
        Assert.assertNotSame(scheduled, forNow);
        Assert.assertEquals(scheduled, resolver.resolve(pattern, tomorrow));
        Assert.assertEquals(scheduled, resolver.resolve(pattern, null));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testUpdate() {
        ServiceDate today = new ServiceDate();
        ServiceDate yesterday = today.previous();
        TripPattern pattern = TimetableSnapshotTest.patternIndex.get(new FeedScopedId("agency", "1.1"));
        TimetableSnapshot resolver = new TimetableSnapshot();
        Timetable origNow = resolver.resolve(pattern, today);
        TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        TripUpdate tripUpdate = tripUpdateBuilder.build();
        // new timetable for today
        updateResolver(resolver, pattern, tripUpdate, "agency", today);
        Timetable updatedNow = resolver.resolve(pattern, today);
        Assert.assertNotSame(origNow, updatedNow);
        // reuse timetable for today
        updateResolver(resolver, pattern, tripUpdate, "agency", today);
        Assert.assertEquals(updatedNow, resolver.resolve(pattern, today));
        // create new timetable for tomorrow
        updateResolver(resolver, pattern, tripUpdate, "agency", yesterday);
        Assert.assertNotSame(origNow, resolver.resolve(pattern, yesterday));
        Assert.assertNotSame(updatedNow, resolver.resolve(pattern, yesterday));
        // exception if we try to modify a snapshot
        TimetableSnapshot snapshot = resolver.commit();
        updateResolver(snapshot, pattern, tripUpdate, "agency", yesterday);
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testCommit() {
        ServiceDate today = new ServiceDate();
        ServiceDate yesterday = today.previous();
        TripPattern pattern = TimetableSnapshotTest.patternIndex.get(new FeedScopedId("agency", "1.1"));
        TimetableSnapshot resolver = new TimetableSnapshot();
        // only return a new snapshot if there are changes
        TimetableSnapshot snapshot = resolver.commit();
        Assert.assertNull(snapshot);
        TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        TripUpdate tripUpdate = tripUpdateBuilder.build();
        // add a new timetable for today, commit, and everything should match
        Assert.assertTrue(updateResolver(resolver, pattern, tripUpdate, "agency", today));
        snapshot = resolver.commit();
        Assert.assertEquals(snapshot.resolve(pattern, today), resolver.resolve(pattern, today));
        Assert.assertEquals(snapshot.resolve(pattern, yesterday), resolver.resolve(pattern, yesterday));
        // add a new timetable for today, don't commit, and everything should not match
        Assert.assertTrue(updateResolver(resolver, pattern, tripUpdate, "agency", today));
        Assert.assertNotSame(snapshot.resolve(pattern, today), resolver.resolve(pattern, today));
        Assert.assertEquals(snapshot.resolve(pattern, yesterday), resolver.resolve(pattern, yesterday));
        // add a new timetable for today, on another day, and things should still not match
        Assert.assertTrue(updateResolver(resolver, pattern, tripUpdate, "agency", yesterday));
        Assert.assertNotSame(snapshot.resolve(pattern, yesterday), resolver.resolve(pattern, yesterday));
        // commit, and things should match
        snapshot = resolver.commit();
        Assert.assertEquals(snapshot.resolve(pattern, today), resolver.resolve(pattern, today));
        Assert.assertEquals(snapshot.resolve(pattern, yesterday), resolver.resolve(pattern, yesterday));
        // exception if we try to commit to a snapshot
        snapshot.commit();
    }

    @Test
    public void testPurge() {
        ServiceDate today = new ServiceDate();
        ServiceDate yesterday = today.previous();
        TripPattern pattern = TimetableSnapshotTest.patternIndex.get(new FeedScopedId("agency", "1.1"));
        TripDescriptor.Builder tripDescriptorBuilder = TripDescriptor.newBuilder();
        tripDescriptorBuilder.setTripId("1.1");
        tripDescriptorBuilder.setScheduleRelationship(CANCELED);
        TripUpdate.Builder tripUpdateBuilder = TripUpdate.newBuilder();
        tripUpdateBuilder.setTrip(tripDescriptorBuilder);
        TripUpdate tripUpdate = tripUpdateBuilder.build();
        TimetableSnapshot resolver = new TimetableSnapshot();
        updateResolver(resolver, pattern, tripUpdate, "agency", today);
        updateResolver(resolver, pattern, tripUpdate, "agency", yesterday);
        Assert.assertNotSame(resolver.resolve(pattern, yesterday), resolver.resolve(pattern, null));
        Assert.assertNotSame(resolver.resolve(pattern, today), resolver.resolve(pattern, null));
        Assert.assertNotNull(resolver.commit());
        Assert.assertFalse(resolver.isDirty());
        Assert.assertTrue(resolver.purgeExpiredData(yesterday));
        Assert.assertFalse(resolver.purgeExpiredData(yesterday));
        Assert.assertEquals(resolver.resolve(pattern, yesterday), resolver.resolve(pattern, null));
        Assert.assertNotSame(resolver.resolve(pattern, today), resolver.resolve(pattern, null));
        Assert.assertNull(resolver.commit());
        Assert.assertFalse(resolver.isDirty());
    }
}

