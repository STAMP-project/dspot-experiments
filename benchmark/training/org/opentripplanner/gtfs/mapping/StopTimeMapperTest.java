package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.org.opentripplanner.model.StopTime;


public class StopTimeMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final int ARRIVAL_TIME = 1000;

    private static final int DEPARTURE_TIME = 2000;

    private static final int DROP_OFF_TYPE = 2;

    private static final String FARE_PERIOD_ID = "Fare Period Id";

    private static final int PICKUP_TYPE = 3;

    private static final String ROUTE_SHORT_NAME = "Route Short Name";

    private static final double SHAPE_DIST_TRAVELED = 2.5;

    private static final Stop STOP = new Stop();

    private static final String HEAD_SIGN = "Head Sign";

    private static final int STOP_SEQUENCE = 4;

    private static final int TIMEPOINT = 50;

    private static final Trip TRIP = new Trip();

    private static final StopTime STOP_TIME = new StopTime();

    static {
        StopTimeMapperTest.TRIP.setId(StopTimeMapperTest.AGENCY_AND_ID);
        StopTimeMapperTest.STOP.setId(StopTimeMapperTest.AGENCY_AND_ID);
        StopTimeMapperTest.STOP_TIME.setId(StopTimeMapperTest.ID);
        StopTimeMapperTest.STOP_TIME.setArrivalTime(StopTimeMapperTest.ARRIVAL_TIME);
        StopTimeMapperTest.STOP_TIME.setDepartureTime(StopTimeMapperTest.DEPARTURE_TIME);
        StopTimeMapperTest.STOP_TIME.setDropOffType(StopTimeMapperTest.DROP_OFF_TYPE);
        StopTimeMapperTest.STOP_TIME.setFarePeriodId(StopTimeMapperTest.FARE_PERIOD_ID);
        StopTimeMapperTest.STOP_TIME.setPickupType(StopTimeMapperTest.PICKUP_TYPE);
        StopTimeMapperTest.STOP_TIME.setRouteShortName(StopTimeMapperTest.ROUTE_SHORT_NAME);
        StopTimeMapperTest.STOP_TIME.setShapeDistTraveled(StopTimeMapperTest.SHAPE_DIST_TRAVELED);
        StopTimeMapperTest.STOP_TIME.setStop(StopTimeMapperTest.STOP);
        StopTimeMapperTest.STOP_TIME.setStopHeadsign(StopTimeMapperTest.HEAD_SIGN);
        StopTimeMapperTest.STOP_TIME.setStopSequence(StopTimeMapperTest.STOP_SEQUENCE);
        StopTimeMapperTest.STOP_TIME.setTimepoint(StopTimeMapperTest.TIMEPOINT);
        StopTimeMapperTest.STOP_TIME.setTrip(StopTimeMapperTest.TRIP);
    }

    private StopTimeMapper subject = new StopTimeMapper(new StopMapper(), new TripMapper(new RouteMapper(new AgencyMapper())));

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<StopTime>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(StopTimeMapperTest.STOP_TIME)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.StopTime result = subject.map(StopTimeMapperTest.STOP_TIME);
        Assert.assertEquals(StopTimeMapperTest.ARRIVAL_TIME, result.getArrivalTime());
        Assert.assertEquals(StopTimeMapperTest.DEPARTURE_TIME, result.getDepartureTime());
        Assert.assertEquals(StopTimeMapperTest.DROP_OFF_TYPE, result.getDropOffType());
        Assert.assertEquals(StopTimeMapperTest.FARE_PERIOD_ID, result.getFarePeriodId());
        Assert.assertEquals(StopTimeMapperTest.PICKUP_TYPE, result.getPickupType());
        Assert.assertEquals(StopTimeMapperTest.ROUTE_SHORT_NAME, result.getRouteShortName());
        Assert.assertEquals(StopTimeMapperTest.SHAPE_DIST_TRAVELED, result.getShapeDistTraveled(), 1.0E-4);
        Assert.assertNotNull(result.getStop());
        Assert.assertEquals(StopTimeMapperTest.HEAD_SIGN, result.getStopHeadsign());
        Assert.assertEquals(StopTimeMapperTest.STOP_SEQUENCE, result.getStopSequence());
        Assert.assertEquals(StopTimeMapperTest.TIMEPOINT, result.getTimepoint());
        Assert.assertNotNull(result.getTrip());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.StopTime result = subject.map(new StopTime());
        Assert.assertFalse(result.isArrivalTimeSet());
        Assert.assertFalse(result.isDepartureTimeSet());
        Assert.assertEquals(0, result.getDropOffType());
        Assert.assertNull(result.getFarePeriodId());
        Assert.assertEquals(0, result.getPickupType());
        Assert.assertNull(result.getRouteShortName());
        Assert.assertFalse(result.isShapeDistTraveledSet());
        Assert.assertNull(result.getStop());
        Assert.assertNull(result.getStopHeadsign());
        Assert.assertEquals(0, result.getStopSequence());
        Assert.assertFalse(result.isTimepointSet());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.StopTime result1 = subject.map(StopTimeMapperTest.STOP_TIME);
        org.opentripplanner.model.StopTime result2 = subject.map(StopTimeMapperTest.STOP_TIME);
        Assert.assertTrue((result1 == result2));
    }
}

