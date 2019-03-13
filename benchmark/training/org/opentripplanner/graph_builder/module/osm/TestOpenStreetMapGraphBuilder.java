package org.opentripplanner.graph_builder.module.osm;


import StreetTraversalPermission.PEDESTRIAN;
import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Test;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.openstreetmap.impl.FileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.openstreetmap.model.OSMWay;
import org.opentripplanner.openstreetmap.model.OSMWithTags;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.util.LocalizedString;


// disabled pending discussion with author (AMB)
// @Test
// public void testMultipolygon() throws Exception {
// Graph gg = new Graph();
// OpenStreetMapGraphBuilderImpl loader = new OpenStreetMapGraphBuilderImpl();
// 
// FileBasedOpenStreetMapProviderImpl pr = new FileBasedOpenStreetMapProviderImpl();
// pr.setPath(new File(getClass().getResource("otp-multipolygon-test.osm").getPath()));
// loader.setProvider(pr);
// 
// loader.buildGraph(gg, extra);
// 
// assertNotNull(gg.getVertex("way -3535 from 4"));
// }
public class TestOpenStreetMapGraphBuilder extends TestCase {
    private HashMap<Class<?>, Object> extra;

    @Test
    public void testGraphBuilder() throws Exception {
        Graph gg = new Graph();
        OpenStreetMapModule loader = new OpenStreetMapModule();
        loader.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());
        FileBasedOpenStreetMapProviderImpl provider = new FileBasedOpenStreetMapProviderImpl();
        File file = new File(URLDecoder.decode(getClass().getResource("map.osm.gz").getFile(), "UTF-8"));
        provider.setPath(file);
        loader.setProvider(provider);
        loader.buildGraph(gg, extra);
        // Kamiennogorska at south end of segment
        Vertex v1 = gg.getVertex("osm:node:280592578");
        // Kamiennogorska at Mariana Smoluchowskiego
        Vertex v2 = gg.getVertex("osm:node:288969929");
        // Mariana Smoluchowskiego, north end
        Vertex v3 = gg.getVertex("osm:node:280107802");
        // Mariana Smoluchowskiego, south end (of segment connected to v2)
        Vertex v4 = gg.getVertex("osm:node:288970952");
        TestCase.assertNotNull(v1);
        TestCase.assertNotNull(v2);
        TestCase.assertNotNull(v3);
        TestCase.assertNotNull(v4);
        Edge e1 = null;
        Edge e2 = null;
        Edge e3 = null;
        for (Edge e : v2.getOutgoing()) {
            if ((e.getToVertex()) == v1) {
                e1 = e;
            } else
                if ((e.getToVertex()) == v3) {
                    e2 = e;
                } else
                    if ((e.getToVertex()) == v4) {
                        e3 = e;
                    }


        }
        TestCase.assertNotNull(e1);
        TestCase.assertNotNull(e2);
        TestCase.assertNotNull(e3);
        TestCase.assertTrue(("name of e1 must be like \"Kamiennog\u00f3rska\"; was " + (e1.getName())), e1.getName().contains("Kamiennog\u00f3rska"));
        TestCase.assertTrue(("name of e2 must be like \"Mariana Smoluchowskiego\"; was " + (e2.getName())), e2.getName().contains("Mariana Smoluchowskiego"));
    }

    /**
     * Detailed testing of OSM graph building using a very small chunk of NYC (SOHO-ish).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBuildGraphDetailed() throws Exception {
        Graph gg = new Graph();
        OpenStreetMapModule loader = new OpenStreetMapModule();
        loader.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());
        FileBasedOpenStreetMapProviderImpl provider = new FileBasedOpenStreetMapProviderImpl();
        File file = new File(URLDecoder.decode(getClass().getResource("NYC_small.osm.gz").getFile(), "UTF-8"));
        provider.setPath(file);
        loader.setProvider(provider);
        loader.buildGraph(gg, extra);
        // These vertices are labeled in the OSM file as having traffic lights.
        IntersectionVertex iv1 = ((IntersectionVertex) (gg.getVertex("osm:node:1919595918")));
        IntersectionVertex iv2 = ((IntersectionVertex) (gg.getVertex("osm:node:42442273")));
        IntersectionVertex iv3 = ((IntersectionVertex) (gg.getVertex("osm:node:1919595927")));
        IntersectionVertex iv4 = ((IntersectionVertex) (gg.getVertex("osm:node:42452026")));
        TestCase.assertTrue(iv1.trafficLight);
        TestCase.assertTrue(iv2.trafficLight);
        TestCase.assertTrue(iv3.trafficLight);
        TestCase.assertTrue(iv4.trafficLight);
        // These are not.
        IntersectionVertex iv5 = ((IntersectionVertex) (gg.getVertex("osm:node:42435485")));
        IntersectionVertex iv6 = ((IntersectionVertex) (gg.getVertex("osm:node:42439335")));
        IntersectionVertex iv7 = ((IntersectionVertex) (gg.getVertex("osm:node:42436761")));
        IntersectionVertex iv8 = ((IntersectionVertex) (gg.getVertex("osm:node:42442291")));
        TestCase.assertFalse(iv5.trafficLight);
        TestCase.assertFalse(iv6.trafficLight);
        TestCase.assertFalse(iv7.trafficLight);
        TestCase.assertFalse(iv8.trafficLight);
        Set<P2<Integer>> edgeEndpoints = new HashSet<P2<Integer>>();
        for (StreetEdge se : gg.getStreetEdges()) {
            P2<Integer> endpoints = new P2<Integer>(se.getFromVertex().getIndex(), se.getToVertex().getIndex());
            // Check that we don't get any duplicate edges on this small graph.
            if (edgeEndpoints.contains(endpoints)) {
                TestCase.assertFalse(true);
            }
            edgeEndpoints.add(endpoints);
        }
    }

    @Test
    public void testBuildAreaWithoutVisibility() throws Exception {
        testBuildingAreas(true);
    }

    @Test
    public void testBuildAreaWithVisibility() throws Exception {
        testBuildingAreas(false);
    }

    @Test
    public void testWayDataSet() {
        OSMWithTags way = new OSMWay();
        way.addTag("highway", "footway");
        way.addTag("cycleway", "lane");
        way.addTag("access", "no");
        way.addTag("surface", "gravel");
        WayPropertySet wayPropertySet = new WayPropertySet();
        // where there are no way specifiers, the default is used
        TestCase.assertEquals(wayPropertySet.getDataForWay(way), wayPropertySet.defaultProperties);
        // add two equal matches: lane only...
        OSMSpecifier lane_only = new OSMSpecifier();
        lane_only.addTag("cycleway", "lane");
        WayProperties lane_is_safer = new WayProperties();
        lane_is_safer.setSafetyFeatures(new P2<Double>(1.5, 1.5));
        wayPropertySet.addProperties(lane_only, lane_is_safer);
        // and footway only
        OSMSpecifier footway_only = new OSMSpecifier();
        footway_only.addTag("highway", "footway");
        WayProperties footways_allow_peds = new WayProperties();
        footways_allow_peds.setPermission(PEDESTRIAN);
        wayPropertySet.addProperties(footway_only, footways_allow_peds);
        WayProperties dataForWay = wayPropertySet.getDataForWay(way);
        // the first one is found
        TestCase.assertEquals(dataForWay, lane_is_safer);
        // add a better match
        OSMSpecifier lane_and_footway = new OSMSpecifier();
        lane_and_footway.addTag("cycleway", "lane");
        lane_and_footway.addTag("highway", "footway");
        WayProperties safer_and_peds = new WayProperties();
        safer_and_peds.setSafetyFeatures(new P2<Double>(0.75, 0.75));
        safer_and_peds.setPermission(PEDESTRIAN);
        wayPropertySet.addProperties(lane_and_footway, safer_and_peds);
        dataForWay = wayPropertySet.getDataForWay(way);
        TestCase.assertEquals(dataForWay, safer_and_peds);
        // add a mixin
        OSMSpecifier gravel = new OSMSpecifier("surface=gravel");
        WayProperties gravel_is_dangerous = new WayProperties();
        gravel_is_dangerous.setSafetyFeatures(new P2<Double>(2.0, 2.0));
        wayPropertySet.addProperties(gravel, gravel_is_dangerous, true);
        dataForWay = wayPropertySet.getDataForWay(way);
        TestCase.assertEquals(dataForWay.getSafetyFeatures().first, 1.5);
        // test a left-right distinction
        way = new OSMWay();
        way.addTag("highway", "footway");
        way.addTag("cycleway", "lane");
        way.addTag("cycleway:right", "track");
        OSMSpecifier track_only = new OSMSpecifier("highway=footway;cycleway=track");
        WayProperties track_is_safest = new WayProperties();
        track_is_safest.setSafetyFeatures(new P2<Double>(0.25, 0.25));
        wayPropertySet.addProperties(track_only, track_is_safest);
        dataForWay = wayPropertySet.getDataForWay(way);
        TestCase.assertEquals(0.25, dataForWay.getSafetyFeatures().first);// right (with traffic) comes

        // from track
        TestCase.assertEquals(0.75, dataForWay.getSafetyFeatures().second);// left comes from lane

        way = new OSMWay();
        way.addTag("highway", "footway");
        way.addTag("footway", "sidewalk");
        way.addTag("RLIS:reviewed", "no");
        WayPropertySet propset = new WayPropertySet();
        CreativeNamer namer = new CreativeNamer("platform");
        propset.addCreativeNamer(new OSMSpecifier("railway=platform;highway=footway;footway=sidewalk"), namer);
        namer = new CreativeNamer("sidewalk");
        propset.addCreativeNamer(new OSMSpecifier("highway=footway;footway=sidewalk"), namer);
        TestCase.assertEquals("sidewalk", propset.getCreativeNameForWay(way).toString());
    }

    @Test
    public void testCreativeNaming() {
        OSMWithTags way = new OSMWay();
        way.addTag("highway", "footway");
        way.addTag("cycleway", "lane");
        way.addTag("access", "no");
        CreativeNamer namer = new CreativeNamer();
        namer.setCreativeNamePattern("Highway with cycleway {cycleway} and access {access} and morx {morx}");
        TestCase.assertEquals("Highway with cycleway lane and access no and morx ", namer.generateCreativeName(way).toString());
    }

    @Test
    public void testLocalizedString() {
        LocalizedString localizedString = new LocalizedString("corner", new String[]{ "first", "second" });
        TestCase.assertEquals("corner of first and second", localizedString.toString());
        TestCase.assertEquals("Kreuzung first mit second", localizedString.toString(new Locale("de")));
    }
}

