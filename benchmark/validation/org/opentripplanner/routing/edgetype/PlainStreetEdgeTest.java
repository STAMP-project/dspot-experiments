package org.opentripplanner.routing.edgetype;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.PEDESTRIAN;
import StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE;
import TraverseMode.BICYCLE;
import TraverseMode.CAR;
import TraverseMode.WALK;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.routing.vertextype.StreetVertex;


public class PlainStreetEdgeTest {
    private Graph graph;

    private IntersectionVertex v0;

    private IntersectionVertex v1;

    private IntersectionVertex v2;

    private RoutingRequest proto;

    @Test
    public void testInAndOutAngles() {
        // An edge heading straight West
        StreetEdge e1 = edge(v1, v2, 1.0, ALL);
        // Edge has same first and last angle.
        Assert.assertEquals(90, e1.getInAngle());
        Assert.assertEquals(90, e1.getOutAngle());
        // 2 new ones
        StreetVertex u = vertex("test1", 2.0, 1.0);
        StreetVertex v = vertex("test2", 2.0, 2.0);
        // Second edge, heading straight North
        StreetEdge e2 = edge(u, v, 1.0, ALL);
        // 180 degrees could be expressed as 180 or -180. Our implementation happens to use -180.
        Assert.assertEquals(180, Math.abs(e2.getInAngle()));
        Assert.assertEquals(180, Math.abs(e2.getOutAngle()));
    }

    @Test
    public void testTraverseAsPedestrian() {
        StreetEdge e1 = edge(v1, v2, 100.0, ALL);
        e1.setCarSpeed(10.0F);
        RoutingRequest options = proto.clone();
        options.setMode(WALK);
        options.setRoutingContext(graph, v1, v2);
        State s0 = new State(options);
        State s1 = e1.traverse(s0);
        // Should use the speed on the edge.
        double expectedWeight = (e1.getDistance()) / (options.walkSpeed);
        long expectedDuration = ((long) (Math.ceil(expectedWeight)));
        Assert.assertEquals(expectedDuration, s1.getElapsedTimeSeconds(), 0.0);
        Assert.assertEquals(expectedWeight, s1.getWeight(), 0.0);
    }

    @Test
    public void testTraverseAsCar() {
        StreetEdge e1 = edge(v1, v2, 100.0, ALL);
        e1.setCarSpeed(10.0F);
        RoutingRequest options = proto.clone();
        options.setMode(CAR);
        options.setRoutingContext(graph, v1, v2);
        State s0 = new State(options);
        State s1 = e1.traverse(s0);
        // Should use the speed on the edge.
        double expectedWeight = (e1.getDistance()) / (e1.getCarSpeed());
        long expectedDuration = ((long) (Math.ceil(expectedWeight)));
        Assert.assertEquals(expectedDuration, s1.getElapsedTimeSeconds(), 0.0);
        Assert.assertEquals(expectedWeight, s1.getWeight(), 0.0);
    }

    @Test
    public void testModeSetCanTraverse() {
        StreetEdge e = edge(v1, v2, 1.0, ALL);
        TraverseModeSet modes = TraverseModeSet.allModes();
        Assert.assertTrue(e.canTraverse(modes));
        modes = new TraverseModeSet(TraverseMode.BICYCLE, TraverseMode.WALK);
        Assert.assertTrue(e.canTraverse(modes));
        e = edge(v1, v2, 1.0, StreetTraversalPermission.CAR);
        Assert.assertFalse(e.canTraverse(modes));
        modes = new TraverseModeSet(TraverseMode.CAR, TraverseMode.WALK);
        Assert.assertTrue(e.canTraverse(modes));
    }

    /**
     * Test the traversal of two edges with different traverse modes, with a focus on cycling.
     * This test will fail unless the following three conditions are met:
     * 1. Turn costs are computed based on the back edge's traverse mode during reverse traversal.
     * 2. Turn costs are computed such that bike walking is taken into account correctly.
     * 3. User-specified bike speeds are applied correctly during turn cost computation.
     */
    @Test
    public void testTraverseModeSwitchBike() {
        StreetEdge e0 = edge(v0, v1, 50.0, PEDESTRIAN);
        StreetEdge e1 = edge(v1, v2, 18.4, PEDESTRIAN_AND_BICYCLE);
        v1.trafficLight = true;
        RoutingRequest forward = proto.clone();
        forward.setMode(BICYCLE);
        forward.bikeSpeed = 3.0F;
        forward.setRoutingContext(graph, v0, v2);
        State s0 = new State(forward);
        State s1 = e0.traverse(s0);
        State s2 = e1.traverse(s1);
        RoutingRequest reverse = proto.clone();
        reverse.setMode(BICYCLE);
        reverse.setArriveBy(true);
        reverse.bikeSpeed = 3.0F;
        reverse.setRoutingContext(graph, v0, v2);
        State s3 = new State(reverse);
        State s4 = e1.traverse(s3);
        State s5 = e0.traverse(s4);
        Assert.assertEquals(73, s2.getElapsedTimeSeconds());
        Assert.assertEquals(73, s5.getElapsedTimeSeconds());
    }

    /**
     * Test the traversal of two edges with different traverse modes, with a focus on walking.
     * This test will fail unless the following three conditions are met:
     * 1. Turn costs are computed based on the back edge's traverse mode during reverse traversal.
     * 2. Turn costs are computed such that bike walking is taken into account correctly.
     * 3. Enabling bike mode on a routing request bases the bike walking speed on the walking speed.
     */
    @Test
    public void testTraverseModeSwitchWalk() {
        StreetEdge e0 = edge(v0, v1, 50.0, PEDESTRIAN_AND_BICYCLE);
        StreetEdge e1 = edge(v1, v2, 18.4, PEDESTRIAN);
        v1.trafficLight = true;
        RoutingRequest forward = proto.clone();
        forward.setMode(BICYCLE);
        forward.setRoutingContext(graph, v0, v2);
        State s0 = new State(forward);
        State s1 = e0.traverse(s0);
        State s2 = e1.traverse(s1);
        RoutingRequest reverse = proto.clone();
        reverse.setMode(BICYCLE);
        reverse.setArriveBy(true);
        reverse.setRoutingContext(graph, v0, v2);
        State s3 = new State(reverse);
        State s4 = e1.traverse(s3);
        State s5 = e0.traverse(s4);
        Assert.assertEquals(42, s2.getElapsedTimeSeconds());
        Assert.assertEquals(42, s5.getElapsedTimeSeconds());
    }

    /**
     * Test the bike switching penalty feature, both its cost penalty and its separate time penalty.
     */
    @Test
    public void testBikeSwitch() {
        StreetEdge e0 = edge(v0, v1, 0.0, PEDESTRIAN);
        StreetEdge e1 = edge(v1, v2, 0.0, StreetTraversalPermission.BICYCLE);
        StreetEdge e2 = edge(v2, v0, 0.0, PEDESTRIAN_AND_BICYCLE);
        RoutingRequest noPenalty = proto.clone();
        noPenalty.setMode(BICYCLE);
        noPenalty.setRoutingContext(graph, v0, v0);
        State s0 = new State(noPenalty);
        State s1 = e0.traverse(s0);
        State s2 = e1.traverse(s1);
        State s3 = e2.traverse(s2);
        RoutingRequest withPenalty = proto.clone();
        withPenalty.bikeSwitchTime = 42;
        withPenalty.bikeSwitchCost = 23;
        withPenalty.setMode(BICYCLE);
        withPenalty.setRoutingContext(graph, v0, v0);
        State s4 = new State(withPenalty);
        State s5 = e0.traverse(s4);
        State s6 = e1.traverse(s5);
        State s7 = e2.traverse(s6);
        Assert.assertEquals(0, s0.getElapsedTimeSeconds());
        Assert.assertEquals(0, s1.getElapsedTimeSeconds());
        Assert.assertEquals(0, s2.getElapsedTimeSeconds());
        Assert.assertEquals(0, s3.getElapsedTimeSeconds());
        Assert.assertEquals(0.0, s0.getWeight(), 0.0);
        Assert.assertEquals(0.0, s1.getWeight(), 0.0);
        Assert.assertEquals(0.0, s2.getWeight(), 0.0);
        Assert.assertEquals(0.0, s3.getWeight(), 0.0);
        Assert.assertEquals(0.0, s4.getWeight(), 0.0);
        Assert.assertEquals(23.0, s5.getWeight(), 0.0);
        Assert.assertEquals(23.0, s6.getWeight(), 0.0);
        Assert.assertEquals(23.0, s7.getWeight(), 0.0);
        Assert.assertEquals(0, s4.getElapsedTimeSeconds());
        Assert.assertEquals(42, s5.getElapsedTimeSeconds());
        Assert.assertEquals(42, s6.getElapsedTimeSeconds());
        Assert.assertEquals(42, s7.getElapsedTimeSeconds());
    }

    @Test
    public void testTurnRestriction() {
        StreetEdge e0 = edge(v0, v1, 50.0, ALL);
        StreetEdge e1 = edge(v1, v2, 18.4, ALL);
        State state = new State(v2, 0, proto.clone());
        state.getOptions().setArriveBy(true);
        graph.addTurnRestriction(e1, new org.opentripplanner.common.TurnRestriction(e1, e0, null, TraverseModeSet.allModes()));
        Assert.assertNotNull(e0.traverse(e1.traverse(state)));
    }
}

