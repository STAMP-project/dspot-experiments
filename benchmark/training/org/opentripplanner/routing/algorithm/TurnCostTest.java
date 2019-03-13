package org.opentripplanner.routing.algorithm;


import TraverseMode.CAR;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.core.ConstantIntersectionTraversalCostModel;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;


public class TurnCostTest {
    private Graph graph;

    private Vertex topRight;

    private Vertex bottomLeft;

    private StreetEdge maple_main1;

    private StreetEdge broad1_2;

    private RoutingRequest proto;

    @Test
    public void testForwardDefaultNoTurnCosts() {
        RoutingRequest options = proto.clone();
        options.setRoutingContext(graph, topRight, bottomLeft);
        // Without turn costs, this path costs 2x100 + 2x50 = 300.
        checkForwardRouteDuration(options, 300);
    }

    @Test
    public void testForwardDefaultConstTurnCosts() {
        RoutingRequest options = proto.clone();
        options.traversalCostModel = new ConstantIntersectionTraversalCostModel(10.0);
        options.setRoutingContext(graph, topRight, bottomLeft);
        // Without turn costs, this path costs 2x100 + 2x50 = 300.
        // Since we traverse 3 intersections, the total cost should be 330.
        GraphPath path = checkForwardRouteDuration(options, 330);
        // The intersection traversal cost should be applied to the state *after*
        // the intersection itself.
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("main_2nd", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
        Assert.assertEquals(0, states.get(0).getElapsedTimeSeconds());
        Assert.assertEquals(50, states.get(1).getElapsedTimeSeconds());// maple_main1 = 50

        Assert.assertEquals(160, states.get(2).getElapsedTimeSeconds());// main1_2 = 100

        Assert.assertEquals(220, states.get(3).getElapsedTimeSeconds());// main_broad2 = 50

        Assert.assertEquals(330, states.get(4).getElapsedTimeSeconds());// broad2_3 = 100

    }

    @Test
    public void testForwardCarNoTurnCosts() {
        RoutingRequest options = proto.clone();
        options.setMode(CAR);
        options.setRoutingContext(graph, topRight, bottomLeft);
        // Without turn costs, this path costs 3x100 + 1x50 = 300.
        GraphPath path = checkForwardRouteDuration(options, 350);
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("broad_1st", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
    }

    @Test
    public void testForwardCarConstTurnCosts() {
        RoutingRequest options = proto.clone();
        options.traversalCostModel = new ConstantIntersectionTraversalCostModel(10.0);
        options.setMode(CAR);
        options.setRoutingContext(graph, topRight, bottomLeft);
        // Without turn costs, this path costs 3x100 + 1x50 = 350.
        // Since there are 3 turns, the total cost should be 380.
        GraphPath path = checkForwardRouteDuration(options, 380);
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("broad_1st", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
        Assert.assertEquals(0, states.get(0).getElapsedTimeSeconds());
        Assert.assertEquals(50, states.get(1).getElapsedTimeSeconds());// maple_main1 = 50

        Assert.assertEquals(160, states.get(2).getElapsedTimeSeconds());// main1_2 = 100

        Assert.assertEquals(270, states.get(3).getElapsedTimeSeconds());// broad1_2 = 100

        Assert.assertEquals(380, states.get(4).getElapsedTimeSeconds());// broad2_3 = 100

    }
}

