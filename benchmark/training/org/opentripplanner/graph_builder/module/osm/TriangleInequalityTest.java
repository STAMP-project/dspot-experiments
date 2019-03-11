package org.opentripplanner.graph_builder.module.osm;


import java.util.HashMap;
import org.junit.Test;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;


public class TriangleInequalityTest {
    private static HashMap<Class<?>, Object> extra;

    private static Graph graph;

    private Vertex start;

    private Vertex end;

    @Test
    public void testTriangleInequalityDefaultModes() {
        checkTriangleInequality();
    }

    @Test
    public void testTriangleInequalityWalkingOnly() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityDrivingOnly() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.CAR);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkTransit() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.TRANSIT);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkBike() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.BICYCLE);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityDefaultModesBasicSPT() {
        checkTriangleInequality(null);
    }

    @Test
    public void testTriangleInequalityWalkingOnlyBasicSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityDrivingOnlyBasicSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.CAR);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkTransitBasicSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.TRANSIT);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkBikeBasicSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.BICYCLE);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityDefaultModesMultiSPT() {
        checkTriangleInequality(null);
    }

    @Test
    public void testTriangleInequalityWalkingOnlyMultiSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityDrivingOnlyMultiSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.CAR);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkTransitMultiSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.TRANSIT);
        checkTriangleInequality(modes);
    }

    @Test
    public void testTriangleInequalityWalkBikeMultiSPT() {
        TraverseModeSet modes = new TraverseModeSet(TraverseMode.WALK, TraverseMode.BICYCLE);
        checkTriangleInequality(modes);
    }
}

