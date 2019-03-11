package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Trip;


public class TripMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final int BIKES_ALLOWED = 1;

    private static final String BLOCK_ID = "Block Id";

    private static final String DIRECTION_ID = "Direction Id";

    private static final String FARE_ID = "Fare Id";

    private static final Route ROUTE = new Route();

    private static final String ROUTE_SHORT_NAME = "Route Short Name";

    private static final String TRIP_HEADSIGN = "Trip Headsign";

    private static final String TRIP_SHORT_NAME = "Trip Short Name";

    private static final int WHEELCHAIR_ACCESSIBLE = 2;

    private static final int TRIP_BIKES_ALLOWED = 2;

    private static final Trip TRIP = new Trip();

    static {
        TripMapperTest.ROUTE.setId(TripMapperTest.AGENCY_AND_ID);
        TripMapperTest.TRIP.setId(TripMapperTest.AGENCY_AND_ID);
        TripMapperTest.TRIP.setBikesAllowed(TripMapperTest.BIKES_ALLOWED);
        TripMapperTest.TRIP.setBlockId(TripMapperTest.BLOCK_ID);
        TripMapperTest.TRIP.setDirectionId(TripMapperTest.DIRECTION_ID);
        TripMapperTest.TRIP.setFareId(TripMapperTest.FARE_ID);
        TripMapperTest.TRIP.setRoute(TripMapperTest.ROUTE);
        TripMapperTest.TRIP.setRouteShortName(TripMapperTest.ROUTE_SHORT_NAME);
        TripMapperTest.TRIP.setServiceId(TripMapperTest.AGENCY_AND_ID);
        TripMapperTest.TRIP.setShapeId(TripMapperTest.AGENCY_AND_ID);
        TripMapperTest.TRIP.setTripHeadsign(TripMapperTest.TRIP_HEADSIGN);
        TripMapperTest.TRIP.setTripShortName(TripMapperTest.TRIP_SHORT_NAME);
        TripMapperTest.TRIP.setWheelchairAccessible(TripMapperTest.WHEELCHAIR_ACCESSIBLE);
        TripMapperTest.TRIP.setTripBikesAllowed(TripMapperTest.TRIP_BIKES_ALLOWED);
    }

    private TripMapper subject = new TripMapper(new RouteMapper(new AgencyMapper()));

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<Trip>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(TripMapperTest.TRIP)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Trip result = subject.map(TripMapperTest.TRIP);
        Assert.assertEquals("A_1", result.getId().toString());
        Assert.assertEquals(TripMapperTest.BIKES_ALLOWED, result.getBikesAllowed());
        Assert.assertEquals(TripMapperTest.BLOCK_ID, result.getBlockId());
        Assert.assertEquals(TripMapperTest.DIRECTION_ID, result.getDirectionId());
        Assert.assertEquals(TripMapperTest.FARE_ID, result.getFareId());
        Assert.assertNotNull(result.getRoute());
        Assert.assertEquals(TripMapperTest.ROUTE_SHORT_NAME, result.getRouteShortName());
        Assert.assertEquals("A_1", result.getServiceId().toString());
        Assert.assertEquals("A_1", result.getShapeId().toString());
        Assert.assertEquals(TripMapperTest.TRIP_HEADSIGN, result.getTripHeadsign());
        Assert.assertEquals(TripMapperTest.TRIP_SHORT_NAME, result.getTripShortName());
        Assert.assertEquals(TripMapperTest.WHEELCHAIR_ACCESSIBLE, result.getWheelchairAccessible());
        Assert.assertEquals(TripMapperTest.TRIP_BIKES_ALLOWED, result.getTripBikesAllowed());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        Trip input = new Trip();
        input.setId(TripMapperTest.AGENCY_AND_ID);
        org.opentripplanner.model.Trip result = subject.map(input);
        Assert.assertNotNull(result.getId());
        Assert.assertEquals(0, result.getBikesAllowed());
        Assert.assertNull(result.getBlockId());
        Assert.assertNull(result.getDirectionId());
        Assert.assertNull(result.getFareId());
        Assert.assertNull(result.getRoute());
        Assert.assertNull(result.getRouteShortName());
        Assert.assertNull(result.getServiceId());
        Assert.assertNull(result.getShapeId());
        Assert.assertNull(result.getTripHeadsign());
        Assert.assertNull(result.getTripShortName());
        Assert.assertEquals(0, result.getWheelchairAccessible());
        Assert.assertEquals(0, result.getTripBikesAllowed());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Trip result1 = subject.map(TripMapperTest.TRIP);
        org.opentripplanner.model.Trip result2 = subject.map(TripMapperTest.TRIP);
        Assert.assertTrue((result1 == result2));
    }
}

