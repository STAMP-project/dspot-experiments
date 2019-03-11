package org.opentripplanner.routing.edgetype;


import TraverseMode.CAR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opentripplanner.routing.core.IntersectionTraversalCostModel;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.impl.StreetVertexIndexServiceImpl;
import org.opentripplanner.routing.location.TemporaryStreetLocation;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.util.NonLocalizedString;


public class TemporaryPartialStreetEdgeTest {
    private Graph graph;

    private IntersectionVertex v1;

    private IntersectionVertex v2;

    private IntersectionVertex v3;

    private IntersectionVertex v4;

    private StreetEdge e1;

    private StreetEdge e1Reverse;

    private StreetEdge e2;

    private StreetEdge e3;

    @Test
    public void testConstruction() {
        TemporaryPartialStreetEdge pEdge = TemporaryPartialStreetEdgeTest.newTemporaryPartialStreetEdge(e1, v1, v2, e1.getGeometry(), "partial e1", e1.getDistance());
        Assert.assertTrue(pEdge.isEquivalentTo(e1));
        Assert.assertTrue(pEdge.isPartial());
        Assert.assertFalse(pEdge.isBack());
        Assert.assertFalse(pEdge.isReverseOf(e1));
        Assert.assertTrue(pEdge.isReverseOf(e1Reverse));
        Assert.assertEquals(e1.getId(), pEdge.getId());
        Assert.assertEquals(e1.getPermission(), pEdge.getPermission());
        Assert.assertEquals(e1.getCarSpeed(), pEdge.getCarSpeed(), 0.0);
    }

    @Test
    public void testTraversal() {
        RoutingRequest options = new RoutingRequest();
        options.setMode(CAR);
        options.setRoutingContext(graph, v1, v2);
        // Partial edge with same endpoints as the parent.
        TemporaryPartialStreetEdge pEdge1 = TemporaryPartialStreetEdgeTest.newTemporaryPartialStreetEdge(e1, v1, v2, e1.getGeometry(), "partial e1", e1.getDistance());
        TemporaryPartialStreetEdge pEdge2 = TemporaryPartialStreetEdgeTest.newTemporaryPartialStreetEdge(e2, v2, v3, e2.getGeometry(), "partial e2", e2.getDistance());
        // Traverse both the partial and parent edges.
        State s0 = new State(options);
        State s1 = e1.traverse(s0);
        State partialS0 = new State(options);
        State partialS1 = pEdge1.traverse(partialS0);
        // Traversal of original and partial edges should yield the same results.
        Assert.assertEquals(s1.getTimeSeconds(), partialS1.getTimeSeconds());
        Assert.assertEquals(s1.getElapsedTimeSeconds(), partialS1.getElapsedTimeSeconds());
        Assert.assertEquals(s1.getWeight(), partialS1.getWeight(), 0.0);
        // Now traverse the second partial/parent edge pair.
        State s2 = e2.traverse(s1);
        State partialS2 = pEdge2.traverse(partialS1);
        // Same checks as above.
        Assert.assertEquals(s2.getTimeSeconds(), partialS2.getTimeSeconds());
        Assert.assertEquals(s2.getElapsedTimeSeconds(), partialS2.getElapsedTimeSeconds());
        Assert.assertEquals(s2.getWeight(), partialS2.getWeight(), 0.0);
    }

    @Test
    public void testTraversalOfSubdividedEdge() {
        Coordinate nearestPoint = new Coordinate(0.5, 2.0);
        List<StreetEdge> edges = new ArrayList<StreetEdge>();
        edges.add(e2);
        TemporaryStreetLocation end = StreetVertexIndexServiceImpl.createTemporaryStreetLocation(graph, "middle of e2", new NonLocalizedString("foo"), edges, nearestPoint, true);
        TemporaryStreetLocation start = StreetVertexIndexServiceImpl.createTemporaryStreetLocation(graph, "middle of e2", new NonLocalizedString("foo"), edges, nearestPoint, false);
        RoutingRequest options = new RoutingRequest();
        options.setMode(CAR);
        options.setRoutingContext(graph, v1, v2);
        options.rctx.temporaryVertices.addAll(Arrays.asList(end, start));
        // All intersections take 10 minutes - we'll notice if one isn't counted.
        double turnDurationSecs = 10.0 * 60.0;
        options.traversalCostModel = new TemporaryPartialStreetEdgeTest.DummyCostModel(turnDurationSecs);
        options.turnReluctance = 1.0;
        State s0 = new State(options);
        State s1 = e1.traverse(s0);
        State s2 = e2.traverse(s1);
        State s3 = e3.traverse(s2);
        Edge partialE2First = end.getIncoming().iterator().next();
        Edge partialE2Second = start.getOutgoing().iterator().next();
        State partialS0 = new State(options);
        State partialS1 = e1.traverse(partialS0);
        State partialS2A = partialE2First.traverse(partialS1);
        State partialS2B = partialE2Second.traverse(partialS2A);
        State partialS3 = e3.traverse(partialS2B);
        // Should start at the same time.
        Assert.assertEquals(s0.getTimeSeconds(), partialS0.getTimeSeconds());
        // Time and cost should be the same up to a rounding difference.
        Assert.assertTrue(((Math.abs(((s3.getTimeSeconds()) - (partialS3.getTimeSeconds())))) <= 1));
        Assert.assertTrue(((Math.abs(((s3.getElapsedTimeSeconds()) - (partialS3.getElapsedTimeSeconds())))) <= 1));
        Assert.assertTrue(((Math.abs(((s3.getWeight()) - (partialS3.getWeight())))) <= 1));
        // All intersections take 0 seconds now.
        options.traversalCostModel = new TemporaryPartialStreetEdgeTest.DummyCostModel(0.0);
        State s0NoCost = new State(options);
        State s1NoCost = e1.traverse(s0NoCost);
        State s2NoCost = e2.traverse(s1NoCost);
        State s3NoCost = e3.traverse(s2NoCost);
        State partialS0NoCost = new State(options);
        State partialS1NoCost = e1.traverse(partialS0NoCost);
        State partialS2ANoCost = partialE2First.traverse(partialS1NoCost);
        State partialS2BNoCost = partialE2Second.traverse(partialS2ANoCost);
        State partialS3NoCost = e3.traverse(partialS2BNoCost);
        // Time and cost should be the same up to a rounding difference.
        Assert.assertTrue(((Math.abs(((s3NoCost.getTimeSeconds()) - (partialS3NoCost.getTimeSeconds())))) <= 1));
        Assert.assertTrue(((Math.abs(((s3NoCost.getElapsedTimeSeconds()) - (partialS3NoCost.getElapsedTimeSeconds())))) <= 1));
        Assert.assertTrue(((Math.abs(((s3NoCost.getWeight()) - (partialS3NoCost.getWeight())))) <= 1));
        // Difference in duration and weight between now and before should be
        // entirely due to the crossing of 2 intersections at v2 and v3.
        double expectedDifference = (2 * 10) * 60.0;
        double durationDiff = (s3.getTimeSeconds()) - (s3NoCost.getTimeSeconds());
        double partialDurationDiff = (partialS3.getTimeSeconds()) - (partialS3NoCost.getTimeSeconds());
        Assert.assertTrue(((Math.abs((durationDiff - expectedDifference))) <= 1));
        Assert.assertTrue(((Math.abs((partialDurationDiff - expectedDifference))) <= 1));
        // Turn reluctance is 1.0, so weight == duration.
        double weightDiff = (s3.getWeight()) - (s3NoCost.getWeight());
        double partialWeightDiff = (partialS3.getWeight()) - (partialS3NoCost.getWeight());
        Assert.assertTrue(((Math.abs((weightDiff - expectedDifference))) <= 1));
        Assert.assertTrue(((Math.abs((partialWeightDiff - expectedDifference))) <= 1));
    }

    @Test
    public void testReverseEdge() {
        TemporaryPartialStreetEdge pEdge1 = TemporaryPartialStreetEdgeTest.newTemporaryPartialStreetEdge(e1, v1, v2, e1.getGeometry(), "partial e1", e1.getDistance());
        TemporaryPartialStreetEdge pEdge2 = TemporaryPartialStreetEdgeTest.newTemporaryPartialStreetEdge(e1Reverse, v2, v1, e1Reverse.getGeometry(), "partial e2", e1Reverse.getDistance());
        Assert.assertFalse(e1.isReverseOf(pEdge1));
        Assert.assertFalse(pEdge1.isReverseOf(e1));
        Assert.assertFalse(e1Reverse.isReverseOf(pEdge2));
        Assert.assertFalse(pEdge2.isReverseOf(e1Reverse));
        Assert.assertTrue(e1.isReverseOf(pEdge2));
        Assert.assertTrue(e1Reverse.isReverseOf(pEdge1));
        Assert.assertTrue(e1Reverse.isReverseOf(e1));
        Assert.assertTrue(e1.isReverseOf(e1Reverse));
        Assert.assertTrue(pEdge1.isReverseOf(pEdge2));
        Assert.assertTrue(pEdge2.isReverseOf(pEdge1));
    }

    /**
     * Dummy cost model. Returns what you put in.
     */
    private static class DummyCostModel implements IntersectionTraversalCostModel {
        private double turnCostSecs;

        public DummyCostModel(double turnCostSecs) {
            this.turnCostSecs = turnCostSecs;
        }

        @Override
        public double computeTraversalCost(IntersectionVertex v, StreetEdge from, StreetEdge to, TraverseMode mode, RoutingRequest options, float fromSpeed, float toSpeed) {
            return this.turnCostSecs;
        }
    }
}

