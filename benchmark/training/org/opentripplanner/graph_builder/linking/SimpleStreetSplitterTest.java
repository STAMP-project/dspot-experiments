package org.opentripplanner.graph_builder.linking;


import TraverseMode.CAR;
import TraverseMode.WALK;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.graph.Vertex;


public class SimpleStreetSplitterTest {
    private SimpleStreetSplitter spySimpleStreetSplitter;

    /**
     * Tests that traverse mode WALK is used when getting closest end vertex for park and ride.
     */
    @Test
    public void testFindEndVertexForParkAndRide() {
        GenericLocation genericLocation = new GenericLocation(10, 23);
        RoutingRequest routingRequest = new RoutingRequest();
        routingRequest.setMode(CAR);
        routingRequest.parkAndRide = true;
        spySimpleStreetSplitter.getClosestVertex(genericLocation, routingRequest, true);
        Mockito.verify(spySimpleStreetSplitter).link(ArgumentMatchers.any(Vertex.class), ArgumentMatchers.eq(WALK), ArgumentMatchers.eq(routingRequest));
    }
}

