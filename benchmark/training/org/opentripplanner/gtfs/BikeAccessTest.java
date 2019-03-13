package org.opentripplanner.gtfs;


import BikeAccess.ALLOWED;
import BikeAccess.NOT_ALLOWED;
import BikeAccess.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Trip;


public class BikeAccessTest {
    @Test
    public void testBikesAllowed() {
        Trip trip = new Trip();
        Route route = new Route();
        trip.setRoute(route);
        Assert.assertEquals(UNKNOWN, BikeAccess.fromTrip(trip));
        trip.setBikesAllowed(1);
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(trip));
        trip.setBikesAllowed(2);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
        route.setBikesAllowed(1);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
        trip.setBikesAllowed(0);
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(trip));
        route.setBikesAllowed(2);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTripBikesAllowed() {
        Trip trip = new Trip();
        Route route = new Route();
        trip.setRoute(route);
        Assert.assertEquals(UNKNOWN, BikeAccess.fromTrip(trip));
        trip.setTripBikesAllowed(2);
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(trip));
        trip.setTripBikesAllowed(1);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
        route.setRouteBikesAllowed(2);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
        trip.setTripBikesAllowed(0);
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(trip));
        route.setRouteBikesAllowed(1);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBikesAllowedOverridesTripBikesAllowed() {
        Trip trip = new Trip();
        Route route = new Route();
        trip.setRoute(route);
        trip.setBikesAllowed(1);
        trip.setTripBikesAllowed(1);
        Assert.assertEquals(ALLOWED, BikeAccess.fromTrip(trip));
        trip.setBikesAllowed(2);
        trip.setTripBikesAllowed(2);
        Assert.assertEquals(NOT_ALLOWED, BikeAccess.fromTrip(trip));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void setBikesAllowed() {
        Trip trip = new Trip();
        BikeAccess.setForTrip(trip, ALLOWED);
        Assert.assertEquals(1, trip.getBikesAllowed());
        Assert.assertEquals(2, trip.getTripBikesAllowed());
        BikeAccess.setForTrip(trip, NOT_ALLOWED);
        Assert.assertEquals(2, trip.getBikesAllowed());
        Assert.assertEquals(1, trip.getTripBikesAllowed());
        BikeAccess.setForTrip(trip, UNKNOWN);
        Assert.assertEquals(0, trip.getBikesAllowed());
        Assert.assertEquals(0, trip.getTripBikesAllowed());
    }
}

