package org.opentripplanner.model.impl;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.model.Agency;
import org.opentripplanner.model.FareAttribute;
import org.opentripplanner.model.FareRule;
import org.opentripplanner.model.FeedInfo;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Frequency;
import org.opentripplanner.model.OtpTransitService;
import org.opentripplanner.model.Pathway;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.ServiceCalendar;
import org.opentripplanner.model.ServiceCalendarDate;
import org.opentripplanner.model.ShapePoint;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.StopTime;
import org.opentripplanner.model.Transfer;
import org.opentripplanner.model.Trip;


public class OtpTransitServiceImplTest {
    private static final String FEED_ID = "Z";

    private static final FeedScopedId SERVICE_ALLDAYS_ID = new FeedScopedId(OtpTransitServiceImplTest.FEED_ID, "alldays");

    private static final FeedScopedId SERVICE_WEEKDAYS_ID = new FeedScopedId(OtpTransitServiceImplTest.FEED_ID, "weekdays");

    private static final FeedScopedId STATION_ID = new FeedScopedId(OtpTransitServiceImplTest.FEED_ID, "station");

    // The subject is used as read only; hence static is ok
    private static OtpTransitService subject;

    private static Agency agency;

    @Test
    public void testGetAllAgencies() {
        Collection<Agency> agencies = OtpTransitServiceImplTest.subject.getAllAgencies();
        Agency agency = OtpTransitServiceImplTest.first(agencies);
        Assert.assertEquals(1, agencies.size());
        Assert.assertEquals("agency", agency.getId());
        Assert.assertEquals("Fake Agency", agency.getName());
    }

    @Test
    public void testGetAllCalendarDates() {
        Collection<ServiceCalendarDate> calendarDates = OtpTransitServiceImplTest.subject.getAllCalendarDates();
        Assert.assertEquals(1, calendarDates.size());
        Assert.assertEquals("<CalendarDate serviceId=Z_weekdays date=ServiceIdDate(2017-8-31) exception=2>", OtpTransitServiceImplTest.first(calendarDates).toString());
    }

    @Test
    public void testGetAllCalendars() {
        Collection<ServiceCalendar> calendars = OtpTransitServiceImplTest.subject.getAllCalendars();
        Assert.assertEquals(2, calendars.size());
        Assert.assertEquals("<ServiceCalendar Z_alldays [1111111]>", OtpTransitServiceImplTest.first(calendars).toString());
    }

    @Test
    public void testGetAllFareAttributes() {
        Collection<FareAttribute> fareAttributes = OtpTransitServiceImplTest.subject.getAllFareAttributes();
        Assert.assertEquals(1, fareAttributes.size());
        Assert.assertEquals("<FareAttribute agency_FA>", OtpTransitServiceImplTest.first(fareAttributes).toString());
    }

    @Test
    public void testGetAllFareRules() {
        Collection<FareRule> fareRules = OtpTransitServiceImplTest.subject.getAllFareRules();
        Assert.assertEquals(1, fareRules.size());
        Assert.assertEquals("<FareRule  origin='Zone A' contains='Zone B' destination='Zone C'>", OtpTransitServiceImplTest.first(fareRules).toString());
    }

    @Test
    public void testGetAllFeedInfos() {
        Collection<FeedInfo> feedInfos = OtpTransitServiceImplTest.subject.getAllFeedInfos();
        Assert.assertEquals(1, feedInfos.size());
        Assert.assertEquals("<FeedInfo 1>", OtpTransitServiceImplTest.first(feedInfos).toString());
    }

    @Test
    public void testGetAllFrequencies() {
        Collection<Frequency> frequencies = OtpTransitServiceImplTest.subject.getAllFrequencies();
        Assert.assertEquals(2, frequencies.size());
        Assert.assertEquals("<Frequency trip=agency_15.1 start=06:00:00 end=10:00:01>", OtpTransitServiceImplTest.first(frequencies).toString());
    }

    @Test
    public void testGetAllPathways() {
        Collection<Pathway> pathways = OtpTransitServiceImplTest.subject.getAllPathways();
        Assert.assertEquals(4, pathways.size());
        Assert.assertEquals("<Pathway Z_pathways_1_1>", OtpTransitServiceImplTest.first(pathways).toString());
    }

    @Test
    public void testGetAllRoutes() {
        Collection<Route> routes = OtpTransitServiceImplTest.subject.getAllRoutes();
        Assert.assertEquals(18, routes.size());
        Assert.assertEquals("<Route agency_1 1>", OtpTransitServiceImplTest.first(routes).toString());
    }

    @Test
    public void testGetAllTransfers() {
        Collection<Transfer> transfers = OtpTransitServiceImplTest.subject.getAllTransfers();
        Assert.assertEquals(9, transfers.size());
        Assert.assertEquals("<Transfer stop=Z_F..Z_E>", OtpTransitServiceImplTest.first(transfers).toString());
    }

    @Test
    public void testGetAllShapePoints() {
        Collection<ShapePoint> shapePoints = OtpTransitServiceImplTest.subject.getAllShapePoints();
        Assert.assertEquals(9, shapePoints.size());
        Assert.assertEquals("<ShapePoint Z_4 #1 (41.0,-75.0)>", OtpTransitServiceImplTest.first(shapePoints).toString());
    }

    @Test
    public void testGetAllStops() {
        Collection<Stop> stops = OtpTransitServiceImplTest.subject.getAllStops();
        Assert.assertEquals(25, stops.size());
        Assert.assertEquals("<Stop Z_A>", OtpTransitServiceImplTest.first(stops).toString());
    }

    @Test
    public void testGetAllStopTimes() {
        Collection<StopTime> stopTimes = OtpTransitServiceImplTest.subject.getAllStopTimes();
        Assert.assertEquals(80, stopTimes.size());
        Assert.assertEquals("StopTime(seq=1 stop=Z_A trip=agency_1.1 times=00:00:00-00:00:00)", OtpTransitServiceImplTest.first(stopTimes).toString());
    }

    @Test
    public void testGetAllTrips() {
        Collection<Trip> trips = OtpTransitServiceImplTest.subject.getAllTrips();
        Assert.assertEquals(33, trips.size());
        Assert.assertEquals("<Trip agency_1.1>", OtpTransitServiceImplTest.first(trips).toString());
    }

    @Test
    public void testGetStopForId() {
        Stop stop = OtpTransitServiceImplTest.subject.getStopForId(new FeedScopedId("Z", "P"));
        Assert.assertEquals("<Stop Z_P>", stop.toString());
    }

    @Test
    public void testGetTripAgencyIdsReferencingServiceId() {
        List<String> agencyIds;
        agencyIds = OtpTransitServiceImplTest.subject.getTripAgencyIdsReferencingServiceId(OtpTransitServiceImplTest.SERVICE_ALLDAYS_ID);
        Assert.assertEquals("[agency]", agencyIds.toString());
        agencyIds = OtpTransitServiceImplTest.subject.getTripAgencyIdsReferencingServiceId(OtpTransitServiceImplTest.SERVICE_WEEKDAYS_ID);
        Assert.assertEquals("[agency]", agencyIds.toString());
    }

    @Test
    public void testGetStopsForStation() {
        List<Stop> stops = OtpTransitServiceImplTest.subject.getStopsForStation(OtpTransitServiceImplTest.subject.getStopForId(OtpTransitServiceImplTest.STATION_ID));
        Assert.assertEquals("[<Stop Z_A>]", stops.toString());
    }

    @Test
    public void testGetShapePointsForShapeId() {
        List<ShapePoint> shapePoints = OtpTransitServiceImplTest.subject.getShapePointsForShapeId(new FeedScopedId("Z", "5"));
        Assert.assertEquals("[#1 (41,-72), #2 (41,-72), #3 (40,-72), #4 (41,-73), #5 (41,-74)]", shapePoints.stream().map(OtpTransitServiceImplTest::toString).collect(Collectors.toList()).toString());
    }

    @Test
    public void testGetStopTimesForTrip() {
        List<StopTime> stopTimes = OtpTransitServiceImplTest.subject.getStopTimesForTrip(OtpTransitServiceImplTest.first(OtpTransitServiceImplTest.subject.getAllTrips()));
        Assert.assertEquals("[<Stop Z_A>, <Stop Z_B>, <Stop Z_C>]", stopTimes.stream().map(StopTime::getStop).collect(Collectors.toList()).toString());
    }

    @Test
    public void testGetAllServiceIds() {
        List<FeedScopedId> serviceIds = OtpTransitServiceImplTest.subject.getAllServiceIds();
        Assert.assertEquals(2, serviceIds.size());
        Assert.assertEquals("Z_alldays", OtpTransitServiceImplTest.first(serviceIds).toString());
    }

    @Test
    public void testGetCalendarDatesForServiceId() {
        List<ServiceCalendarDate> dates = OtpTransitServiceImplTest.subject.getCalendarDatesForServiceId(OtpTransitServiceImplTest.SERVICE_WEEKDAYS_ID);
        Assert.assertEquals("[<CalendarDate serviceId=Z_weekdays date=ServiceIdDate(2017-8-31) exception=2>]", dates.toString());
    }

    @Test
    public void testGetCalendarForServiceId() {
        ServiceCalendar calendar = OtpTransitServiceImplTest.subject.getCalendarForServiceId(OtpTransitServiceImplTest.SERVICE_ALLDAYS_ID);
        Assert.assertEquals("<ServiceCalendar Z_alldays [1111111]>", calendar.toString());
    }
}

