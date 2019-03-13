package org.opentripplanner.graph_builder.linking;


import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.routing.graph.Graph;


public class LinkStopToPlatformTest {
    private static GeometryFactory geometryFactory = GeometryUtils.getGeometryFactory();

    private Graph graph;

    /**
     * Tests that extra edges are added when linking stops to platform areas to prevent detours around the platform.
     */
    @Test
    public void testLinkStopWithoutExtraEdges() {
        SimpleStreetSplitter splitter = new SimpleStreetSplitter(graph);
        splitter.link();
        Assert.assertEquals(16, graph.getEdges().size());
    }

    @Test
    public void testLinkStopWithExtraEdges() {
        SimpleStreetSplitter splitter = new SimpleStreetSplitter(graph);
        splitter.setAddExtraEdgesToAreas(true);
        splitter.link();
        Assert.assertEquals(38, graph.getEdges().size());
    }
}

