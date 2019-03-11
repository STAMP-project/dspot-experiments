package org.opentripplanner.graph_builder.module.linking;


import com.google.common.collect.Iterables;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.common.geometry.SphericalDistanceLibrary;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.graph_builder.module.FakeGraph;
import org.opentripplanner.profile.StopTreeCache;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTransitLink;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.impl.DefaultStreetVertexIndexFactory;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.routing.vertextype.SplitterVertex;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.opentripplanner.routing.vertextype.TransitStop;


public class LinkingTest {
    /**
     * maximum difference in walk distance, in meters, that is acceptable between the graphs
     */
    public static final int EPSILON = 1;

    /**
     * Ensure that splitting edges yields edges that are identical in length for forward and back edges.
     * StreetEdges have lengths expressed internally in mm, and we want to be sure that not only do they
     * sum to the same values but also that they
     */
    @Test
    public void testSplitting() {
        GeometryFactory gf = GeometryUtils.getGeometryFactory();
        double x = -122.123;
        double y = 37.363;
        for (double delta = 0; delta <= 2; delta += 0.005) {
            StreetVertex v0 = new IntersectionVertex(null, "zero", x, y);
            StreetVertex v1 = new IntersectionVertex(null, "one", (x + delta), (y + delta));
            LineString geom = gf.createLineString(new Coordinate[]{ v0.getCoordinate(), v1.getCoordinate() });
            double dist = SphericalDistanceLibrary.distance(v0.getCoordinate(), v1.getCoordinate());
            StreetEdge s0 = new StreetEdge(v0, v1, geom, "test", dist, StreetTraversalPermission.ALL, false);
            StreetEdge s1 = new StreetEdge(v1, v0, ((LineString) (geom.reverse())), "back", dist, StreetTraversalPermission.ALL, true);
            // split it but not too close to the end
            double splitVal = ((Math.random()) * 0.95) + 0.025;
            SplitterVertex sv0 = new SplitterVertex(null, "split", (x + (delta * splitVal)), (y + (delta * splitVal)), s0);
            SplitterVertex sv1 = new SplitterVertex(null, "split", (x + (delta * splitVal)), (y + (delta * splitVal)), s1);
            P2<StreetEdge> sp0 = s0.split(sv0, true);
            P2<StreetEdge> sp1 = s1.split(sv1, true);
            // distances expressed internally in mm so this epsilon is plenty good enough to ensure that they
            // have the same values
            Assert.assertEquals(sp0.first.getDistance(), sp1.second.getDistance(), 1.0E-7);
            Assert.assertEquals(sp0.second.getDistance(), sp1.first.getDistance(), 1.0E-7);
            Assert.assertFalse(sp0.first.isBack());
            Assert.assertFalse(sp0.second.isBack());
            Assert.assertTrue(sp1.first.isBack());
            Assert.assertTrue(sp1.second.isBack());
        }
    }

    /**
     * Test that all the stops are linked identically
     * to the street network on two builds of similar graphs
     * with additional stops in one.
     *
     * We do this by building the graphs and then comparing the stop tree caches.
     */
    @Test
    public void testStopsLinkedIdentically() throws UnsupportedEncodingException {
        // build the graph without the added stops
        Graph g1 = FakeGraph.buildGraphNoTransit();
        FakeGraph.addRegularStopGrid(g1);
        FakeGraph.link(g1);
        Graph g2 = FakeGraph.buildGraphNoTransit();
        FakeGraph.addExtraStops(g2);
        FakeGraph.addRegularStopGrid(g2);
        FakeGraph.link(g2);
        // compare the linkages
        for (TransitStop ts : Iterables.filter(g1.getVertices(), TransitStop.class)) {
            Collection<Edge> stls = LinkingTest.stls(ts.getOutgoing());
            Assert.assertTrue(((stls.size()) >= 1));
            StreetTransitLink exemplar = ((StreetTransitLink) (stls.iterator().next()));
            TransitStop other = ((TransitStop) (g2.getVertex(ts.getLabel())));
            Collection<Edge> ostls = LinkingTest.stls(other.getOutgoing());
            Assert.assertEquals(("Unequal number of links from stop " + ts), stls.size(), ostls.size());
            StreetTransitLink oe = ((StreetTransitLink) (ostls.iterator().next()));
            Assert.assertEquals(exemplar.getToVertex().getLat(), oe.getToVertex().getLat(), 1.0E-10);
            Assert.assertEquals(exemplar.getToVertex().getLon(), oe.getToVertex().getLon(), 1.0E-10);
        }
        // compare the stop tree caches
        g1.index(new DefaultStreetVertexIndexFactory());
        g2.index(new DefaultStreetVertexIndexFactory());
        g1.rebuildVertexAndEdgeIndices();
        g2.rebuildVertexAndEdgeIndices();
        StopTreeCache s1 = g1.index.getStopTreeCache();
        StopTreeCache s2 = g2.index.getStopTreeCache();
        // convert the caches to be by stop label
        Map<String, int[]> l1 = LinkingTest.cacheByLabel(s1);
        Map<String, int[]> l2 = LinkingTest.cacheByLabel(s2);
        // do the comparison
        for (Map.Entry<String, int[]> e : l1.entrySet()) {
            // graph 2 should contain all stops in graph 1 (and a few more)
            Assert.assertTrue(l2.containsKey(e.getKey()));
            TObjectIntMap<String> g1t = jaggedArrayToVertexMap(e.getValue(), g1);
            TObjectIntMap<String> g2t = jaggedArrayToVertexMap(l2.get(e.getKey()), g2);
            for (TObjectIntIterator<String> it = g1t.iterator(); it.hasNext();) {
                it.advance();
                Assert.assertTrue(g2t.containsKey(it.key()));
                int newv = g2t.get(it.key());
                Assert.assertTrue((((((((("At " + (it.key())) + " from stop ") + (g1.getVertex(e.getKey()))) + ", difference in walk distances: ") + (it.value())) + "m without extra stops,") + newv) + "m with"), ((Math.abs(((it.value()) - newv))) <= (LinkingTest.EPSILON)));
            }
        }
    }
}

