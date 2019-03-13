package org.opentripplanner.routing.algorithm;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;


public class TurnRestrictionTest {
    private Graph graph;

    private Vertex topRight;

    private Vertex bottomLeft;

    private StreetEdge maple_main1;

    private StreetEdge broad1_2;

    @Test
    public void testHasExplicitTurnRestrictions() {
        Assert.assertFalse(graph.getTurnRestrictions(maple_main1).isEmpty());
        Assert.assertTrue(graph.getTurnRestrictions(broad1_2).isEmpty());
    }

    @Test
    public void testForwardDefault() {
        RoutingRequest options = new RoutingRequest();
        options.carSpeed = 1.0;
        options.walkSpeed = 1.0;
        options.setRoutingContext(graph, topRight, bottomLeft);
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(bottomLeft, false);
        Assert.assertNotNull(path);
        // Since there are no turn restrictions applied to the default modes (walking + transit)
        // the shortest path is 1st to Main, Main to 2nd, 2nd to Broad and Broad until the
        // corner of Broad and 3rd.
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("main_2nd", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
    }

    @Test
    public void testForwardAsPedestrian() {
        RoutingRequest options = new RoutingRequest(TraverseMode.WALK);
        options.walkSpeed = 1.0;
        options.setRoutingContext(graph, topRight, bottomLeft);
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(bottomLeft, false);
        Assert.assertNotNull(path);
        // Since there are no turn restrictions applied to the default modes (walking + transit)
        // the shortest path is 1st to Main, Main to 2nd, 2nd to Broad and Broad until the
        // corner of Broad and 3rd.
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("main_2nd", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
    }

    @Test
    public void testForwardAsCar() {
        RoutingRequest options = new RoutingRequest(TraverseMode.CAR);
        options.carSpeed = 1.0;
        options.setRoutingContext(graph, topRight, bottomLeft);
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(bottomLeft, false);
        Assert.assertNotNull(path);
        // If not for turn restrictions, the shortest path would be to take 1st to Main,
        // Main to 2nd, 2nd to Broad and Broad until the corner of Broad and 3rd.
        // However, most of these turns are not allowed. Instead, the shortest allowed
        // path is 1st to Broad, Broad to 3rd.
        List<State> states = path.states;
        Assert.assertEquals(5, states.size());
        Assert.assertEquals("maple_1st", states.get(0).getVertex().getLabel());
        Assert.assertEquals("main_1st", states.get(1).getVertex().getLabel());
        Assert.assertEquals("broad_1st", states.get(2).getVertex().getLabel());
        Assert.assertEquals("broad_2nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("broad_3rd", states.get(4).getVertex().getLabel());
    }
}

