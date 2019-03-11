package org.opentripplanner.routing.core;


import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.model.Agency;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Trip;


public class RoutingRequestTest {
    @Test
    public void testRequest() {
        RoutingRequest request = new RoutingRequest();
        request.addMode(TraverseMode.CAR);
        Assert.assertTrue(request.modes.getCar());
        request.removeMode(TraverseMode.CAR);
        Assert.assertFalse(request.modes.getCar());
        request.setModes(new TraverseModeSet("BICYCLE,WALK"));
        Assert.assertFalse(request.modes.getCar());
        Assert.assertTrue(request.modes.getBicycle());
        Assert.assertTrue(request.modes.getWalk());
    }

    @Test
    public void testIntermediatePlaces() {
        RoutingRequest req = new RoutingRequest();
        Assert.assertFalse(req.hasIntermediatePlaces());
        req.clearIntermediatePlaces();
        Assert.assertFalse(req.hasIntermediatePlaces());
        req.addIntermediatePlace(randomLocation());
        Assert.assertTrue(req.hasIntermediatePlaces());
        req.clearIntermediatePlaces();
        Assert.assertFalse(req.hasIntermediatePlaces());
        req.addIntermediatePlace(randomLocation());
        req.addIntermediatePlace(randomLocation());
        Assert.assertTrue(req.hasIntermediatePlaces());
    }

    @Test
    public void testPreferencesPenaltyForRoute() {
        FeedScopedId id = new FeedScopedId();
        Agency agency = new Agency();
        Route route = new Route();
        Trip trip = new Trip();
        RoutingRequest routingRequest = new RoutingRequest();
        trip.setRoute(route);
        route.setId(id);
        route.setAgency(agency);
        Assert.assertEquals(0, routingRequest.preferencesPenaltyForRoute(trip.getRoute()));
    }
}

