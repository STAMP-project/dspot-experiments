package org.opentripplanner.routing.core;


import StopTransfer.FORBIDDEN_TRANSFER;
import StopTransfer.PREFERRED_TRANSFER;
import StopTransfer.TIMED_TRANSFER;
import StopTransfer.UNKNOWN_TRANSFER;
import junit.framework.TestCase;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Trip;

import static StopTransfer.FORBIDDEN_TRANSFER;
import static StopTransfer.PREFERRED_TRANSFER;
import static StopTransfer.TIMED_TRANSFER;
import static StopTransfer.UNKNOWN_TRANSFER;


public class TestStopTransfer extends TestCase {
    /**
     * Test different stop transfers
     */
    public void testStopTransfer() {
        // Setup from trip with route
        Route fromRoute = new Route();
        fromRoute.setId(new FeedScopedId("A1", "R1"));
        Trip fromTrip = new Trip();
        fromTrip.setId(new FeedScopedId("A1", "T1"));
        fromTrip.setRoute(fromRoute);
        // Setup to trip with route
        Route toRoute = new Route();
        toRoute.setId(new FeedScopedId("A1", "R2"));
        Trip toTrip = new Trip();
        toTrip.setId(new FeedScopedId("A1", "T2"));
        toTrip.setRoute(toRoute);
        // Setup second to trip with route
        Route toRoute2 = new Route();
        toRoute2.setId(new FeedScopedId("A1", "R3"));
        Trip toTrip2 = new Trip();
        toTrip2.setId(new FeedScopedId("A1", "T3"));
        toTrip2.setRoute(toRoute2);
        // Create StopTransfer
        StopTransfer transfer = new StopTransfer();
        TestCase.assertEquals(UNKNOWN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(UNKNOWN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
        // Add empty SpecificTransfer, specificity 0
        transfer.addSpecificTransfer(new SpecificTransfer(((FeedScopedId) (null)), null, null, null, FORBIDDEN_TRANSFER));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
        // Add SpecificTransfer one route, specificity 1
        transfer.addSpecificTransfer(new SpecificTransfer(null, toRoute2.getId(), null, null, PREFERRED_TRANSFER));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(PREFERRED_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
        // Add SpecificTransfer one trip (and one ignored route), specificity 2
        transfer.addSpecificTransfer(new SpecificTransfer(null, toRoute2.getId(), null, toTrip2.getId(), TIMED_TRANSFER));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(TIMED_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
        // Add SpecificTransfer one trip and one route, specificity 3
        transfer.addSpecificTransfer(new SpecificTransfer(fromRoute.getId(), toRoute2.getId(), fromTrip.getId(), null, UNKNOWN_TRANSFER));
        TestCase.assertEquals(FORBIDDEN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(UNKNOWN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
        // Add SpecificTransfer one route, specificity 1
        transfer.addSpecificTransfer(new SpecificTransfer(fromRoute.getId(), null, null, null, 3));
        TestCase.assertEquals(3, transfer.getTransferTime(fromTrip, toTrip));
        TestCase.assertEquals(UNKNOWN_TRANSFER, transfer.getTransferTime(fromTrip, toTrip2));
    }
}

