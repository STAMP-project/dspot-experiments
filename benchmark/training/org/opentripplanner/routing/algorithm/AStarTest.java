package org.opentripplanner.routing.algorithm;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opentripplanner.routing.algorithm.strategies.SearchTerminationStrategy;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.TemporaryConcreteEdge;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.location.TemporaryStreetLocation;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.spt.ShortestPathTree;
import org.opentripplanner.util.NonLocalizedString;


public class AStarTest {
    private Graph graph;

    @Test
    public void testForward() {
        RoutingRequest options = new RoutingRequest();
        options.walkSpeed = 1.0;
        options.setRoutingContext(graph, graph.getVertex("56th_24th"), graph.getVertex("leary_20th"));
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(graph.getVertex("leary_20th"), false);
        List<State> states = path.states;
        Assert.assertEquals(7, states.size());
        Assert.assertEquals("56th_24th", states.get(0).getVertex().getLabel());
        Assert.assertEquals("market_24th", states.get(1).getVertex().getLabel());
        Assert.assertEquals("market_ballard", states.get(2).getVertex().getLabel());
        Assert.assertEquals("market_22nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("market_leary", states.get(4).getVertex().getLabel());
        Assert.assertEquals("leary_vernon", states.get(5).getVertex().getLabel());
        Assert.assertEquals("leary_20th", states.get(6).getVertex().getLabel());
    }

    @Test
    public void testBack() {
        RoutingRequest options = new RoutingRequest();
        options.walkSpeed = 1.0;
        options.setArriveBy(true);
        options.setRoutingContext(graph, graph.getVertex("56th_24th"), graph.getVertex("leary_20th"));
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(graph.getVertex("56th_24th"), false);
        List<State> states = path.states;
        Assert.assertTrue((((states.size()) == 6) || ((states.size()) == 7)));
        Assert.assertEquals("56th_24th", states.get(0).getVertex().getLabel());
        int n;
        // we could go either way around the block formed by 56th, 22nd, market, and 24th.
        if ((states.size()) == 7) {
            Assert.assertEquals("market_24th", states.get(1).getVertex().getLabel());
            Assert.assertEquals("market_ballard", states.get(2).getVertex().getLabel());
            n = 0;
        } else {
            Assert.assertEquals("56th_22nd", states.get(1).getVertex().getLabel());
            n = -1;
        }
        Assert.assertEquals("market_22nd", states.get((n + 3)).getVertex().getLabel());
        Assert.assertEquals("market_leary", states.get((n + 4)).getVertex().getLabel());
        Assert.assertEquals("leary_vernon", states.get((n + 5)).getVertex().getLabel());
        Assert.assertEquals("leary_20th", states.get((n + 6)).getVertex().getLabel());
    }

    @Test
    public void testForwardExtraEdges() {
        RoutingRequest options = new RoutingRequest();
        options.walkSpeed = 1.0;
        TemporaryStreetLocation from = new TemporaryStreetLocation("near_shilshole_22nd", new Coordinate((-122.38505), 47.66662), new NonLocalizedString("near_shilshole_22nd"), false);
        new TemporaryConcreteEdge(from, graph.getVertex("shilshole_22nd"));
        TemporaryStreetLocation to = new TemporaryStreetLocation("near_56th_20th", new Coordinate((-122.382347), 47.669518), new NonLocalizedString("near_56th_20th"), true);
        new TemporaryConcreteEdge(graph.getVertex("56th_20th"), to);
        options.setRoutingContext(graph, from, to);
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        options.cleanup();
        GraphPath path = tree.getPath(to, false);
        List<State> states = path.states;
        Assert.assertEquals(9, states.size());
        Assert.assertEquals("near_shilshole_22nd", states.get(0).getVertex().getLabel());
        Assert.assertEquals("shilshole_22nd", states.get(1).getVertex().getLabel());
        Assert.assertEquals("ballard_22nd", states.get(2).getVertex().getLabel());
        Assert.assertEquals("market_22nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("market_leary", states.get(4).getVertex().getLabel());
        Assert.assertEquals("market_russell", states.get(5).getVertex().getLabel());
        Assert.assertEquals("market_20th", states.get(6).getVertex().getLabel());
        Assert.assertEquals("56th_20th", states.get(7).getVertex().getLabel());
        Assert.assertEquals("near_56th_20th", states.get(8).getVertex().getLabel());
    }

    @Test
    public void testBackExtraEdges() {
        RoutingRequest options = new RoutingRequest();
        options.walkSpeed = 1.0;
        options.setArriveBy(true);
        TemporaryStreetLocation from = new TemporaryStreetLocation("near_shilshole_22nd", new Coordinate((-122.38505), 47.66662), new NonLocalizedString("near_shilshole_22nd"), false);
        new TemporaryConcreteEdge(from, graph.getVertex("shilshole_22nd"));
        TemporaryStreetLocation to = new TemporaryStreetLocation("near_56th_20th", new Coordinate((-122.382347), 47.669518), new NonLocalizedString("near_56th_20th"), true);
        new TemporaryConcreteEdge(graph.getVertex("56th_20th"), to);
        options.setRoutingContext(graph, from, to);
        ShortestPathTree tree = new AStar().getShortestPathTree(options);
        GraphPath path = tree.getPath(from, false);
        List<State> states = path.states;
        Assert.assertEquals(9, states.size());
        Assert.assertEquals("near_shilshole_22nd", states.get(0).getVertex().getLabel());
        Assert.assertEquals("shilshole_22nd", states.get(1).getVertex().getLabel());
        Assert.assertEquals("ballard_22nd", states.get(2).getVertex().getLabel());
        Assert.assertEquals("market_22nd", states.get(3).getVertex().getLabel());
        Assert.assertEquals("market_leary", states.get(4).getVertex().getLabel());
        Assert.assertEquals("market_russell", states.get(5).getVertex().getLabel());
        Assert.assertEquals("market_20th", states.get(6).getVertex().getLabel());
        Assert.assertEquals("56th_20th", states.get(7).getVertex().getLabel());
        Assert.assertEquals("near_56th_20th", states.get(8).getVertex().getLabel());
        options.cleanup();
    }

    @Test
    public void testMultipleTargets() {
        RoutingRequest options = new RoutingRequest();
        options.walkSpeed = 1.0;
        options.batch = true;
        options.setRoutingContext(graph, graph.getVertex("56th_24th"), graph.getVertex("leary_20th"));
        Set<Vertex> targets = new HashSet<Vertex>();
        targets.add(graph.getVertex("shilshole_22nd"));
        targets.add(graph.getVertex("market_russell"));
        targets.add(graph.getVertex("56th_20th"));
        targets.add(graph.getVertex("leary_20th"));
        SearchTerminationStrategy strategy = new org.opentripplanner.routing.algorithm.strategies.MultiTargetTerminationStrategy(targets);
        ShortestPathTree tree = new AStar().getShortestPathTree(options, (-1), strategy);
        for (Vertex v : targets) {
            GraphPath path = tree.getPath(v, false);
            Assert.assertNotNull(("No path found for target " + (v.getLabel())), path);
        }
    }
}

