package org.opentripplanner.graph_builder.module.osm;


import java.util.TimeZone;
import org.junit.Test;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.impl.GraphPathFinder;


/**
 * Tests for planning with intermediate places
 */
public class TestIntermediatePlaces {
    /**
     * The spatial deviation that we allow in degrees
     */
    public static final double DELTA = 0.005;

    private static TimeZone timeZone;

    private static GraphPathFinder graphPathFinder;

    @Test
    public void testWithoutIntermediatePlaces() {
        GenericLocation fromLocation = new GenericLocation(39.9308, (-82.98522));
        GenericLocation toLocation = new GenericLocation(39.96383, (-82.96291));
        GenericLocation[] intermediateLocations = new GenericLocation[]{  };
        handleRequest(fromLocation, toLocation, intermediateLocations, "WALK", false);
        handleRequest(fromLocation, toLocation, intermediateLocations, "WALK", true);
    }

    @Test
    public void testTransitWithoutIntermediatePlaces() {
        GenericLocation fromLocation = new GenericLocation(39.9308, (-83.0118));
        GenericLocation toLocation = new GenericLocation(39.9998, (-83.0198));
        GenericLocation[] intermediateLocations = new GenericLocation[]{  };
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", false);
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", true);
    }

    @Test
    public void testThreeBusStopPlaces() {
        GenericLocation fromLocation = new GenericLocation(39.9058, (-83.1341));
        GenericLocation toLocation = new GenericLocation(39.9058, (-82.8841));
        GenericLocation[] intermediateLocations = new GenericLocation[]{ new GenericLocation(39.9058, (-82.9841)) };
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT", false);
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT", true);
    }

    @Test
    public void testTransitOneIntermediatePlace() {
        GenericLocation fromLocation = new GenericLocation(39.9108, (-83.0118));
        GenericLocation toLocation = new GenericLocation(39.9698, (-83.0198));
        GenericLocation[] intermediateLocations = new GenericLocation[]{ new GenericLocation(39.9948, (-83.0148)) };
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", false);
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", true);
    }

    @Test
    public void testTransitTwoIntermediatePlaces() {
        GenericLocation fromLocation = new GenericLocation(39.9908, (-83.0118));
        GenericLocation toLocation = new GenericLocation(39.9998, (-83.0198));
        GenericLocation[] intermediateLocations = new GenericLocation[2];
        intermediateLocations[0] = new GenericLocation(40.0, (-82.9));
        intermediateLocations[1] = new GenericLocation(39.91, (-83.1));
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", false);
        handleRequest(fromLocation, toLocation, intermediateLocations, "TRANSIT,WALK", true);
    }
}

