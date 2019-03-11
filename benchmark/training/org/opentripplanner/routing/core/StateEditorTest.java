package org.opentripplanner.routing.core;


import TraverseMode.CAR;
import TraverseMode.WALK;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.graph.Graph;

import static TraverseMode.WALK;


public class StateEditorTest {
    @Test
    public final void testIncrementTimeInSeconds() {
        RoutingRequest routingRequest = new RoutingRequest();
        StateEditor stateEditor = new StateEditor(routingRequest, null);
        stateEditor.setTimeSeconds(0);
        stateEditor.incrementTimeInSeconds(999999999);
        Assert.assertEquals(999999999, stateEditor.child.getTimeSeconds());
    }

    /**
     * Test update of non transit options.
     */
    @Test
    public final void testSetNonTransitOptionsFromState() {
        RoutingRequest request = new RoutingRequest();
        request.setMode(CAR);
        request.parkAndRide = true;
        Graph graph = new Graph();
        graph.streetIndex = new org.opentripplanner.routing.impl.StreetVertexIndexServiceImpl(graph);
        request.rctx = new RoutingContext(request, graph);
        State state = new State(request);
        state.stateData.carParked = true;
        state.stateData.bikeParked = true;
        state.stateData.usingRentedBike = false;
        state.stateData.nonTransitMode = WALK;
        StateEditor se = new StateEditor(request, null);
        se.setNonTransitOptionsFromState(state);
        State updatedState = se.makeState();
        Assert.assertEquals(WALK, updatedState.getNonTransitMode());
        Assert.assertEquals(true, updatedState.isCarParked());
        Assert.assertEquals(true, updatedState.isBikeParked());
        Assert.assertEquals(false, updatedState.isBikeRenting());
    }
}

