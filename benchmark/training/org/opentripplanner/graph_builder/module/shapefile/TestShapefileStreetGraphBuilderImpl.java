package org.opentripplanner.graph_builder.module.shapefile;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.PEDESTRIAN;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.spt.ShortestPathTree;


public class TestShapefileStreetGraphBuilderImpl extends TestCase {
    @Test
    public void testBasic() throws Exception {
        Graph gg = new Graph();
        URL resource = getClass().getResource("nyc_streets/streets.shp");
        File file = null;
        if (resource != null) {
            file = new File(resource.getFile());
        }
        if ((file == null) || (!(file.exists()))) {
            System.out.println("No New York City basemap; skipping; see comment here for details");
            /* This test requires the New York City base map. Place it among the source 
            resources and Eclipse should automatically copy it over to the target directory.
            Once you have prepared these files, you may need to 'refresh' in Eclipse's package 
            explorer to force Eclipse to notice the new resources.

            Recent versions of this map are available only in Arcview Geodatabase format.
            For conversion to a Shapefile, you will need the archived MapInfo version at:
            http://www.nyc.gov/html/dcp/html/bytes/bytesarchive.shtml#lion
            Download the MapInfo file of Lion version 10B. 

            This must then be converted to a ShapeFile as follows:
            cd opentripplanner-graph-builder/src/test/resources/org/opentripplanner/graph_builder/module/shapefile
            mkdir nyc_streets       (this is where we will store the shapefile)
            unzip nyc_lion10ami.zip (this should place zipfile contents in a ./lion directory)
            ogr2ogr -f 'ESRI Shapefile' nyc_streets/streets.shp lion/MNLION1.tab 
            ogr2ogr -update -append -f 'ESRI Shapefile' nyc_streets lion/SILION1.tab -nln streets 
            ogr2ogr -update -append -f 'ESRI Shapefile' nyc_streets lion/QNLION1.tab -nln streets 
            ogr2ogr -update -append -f 'ESRI Shapefile' nyc_streets lion/BKLION1.tab -nln streets 
            ogr2ogr -update -append -f 'ESRI Shapefile' nyc_streets lion/BXLION1.tab -nln streets

            Testing also requires NYC Subway data in GTFS in the same location: 
            wget http://data.topplabs.org/data/mta_nyct_subway/subway.zip
             */
            return;
        }
        ShapefileFeatureSourceFactoryImpl factory = new ShapefileFeatureSourceFactoryImpl(file);
        ShapefileStreetSchema schema = new ShapefileStreetSchema();
        schema.setIdAttribute("SegmentID");
        schema.setNameAttribute("Street");
        /* only featuretyp=0 are streets */
        CaseBasedBooleanConverter selector = new CaseBasedBooleanConverter("FeatureTyp", false);
        HashMap<String, Boolean> streets = new HashMap<String, Boolean>();
        streets.put("0", true);
        selector.setValues(streets);
        schema.setFeatureSelector(selector);
        /* street directions */
        CaseBasedTraversalPermissionConverter perms = new CaseBasedTraversalPermissionConverter("TrafDir", StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE);
        perms.addPermission("W", ALL, PEDESTRIAN);
        perms.addPermission("A", PEDESTRIAN, ALL);
        perms.addPermission("T", ALL, ALL);
        schema.setPermissionConverter(perms);
        ShapefileStreetModule loader = new ShapefileStreetModule();
        loader.setFeatureSourceFactory(factory);
        loader.setSchema(schema);
        loader.buildGraph(gg, new HashMap<Class<?>, Object>());
        // find start and end vertices
        Vertex start = null;
        Vertex end = null;
        Vertex carlton = null;
        Coordinate vanderbiltAtPark = new Coordinate((-73.969178), 40.676785);
        Coordinate grandAtLafayette = new Coordinate((-73.999095), 40.720005);
        Coordinate carltonAtPark = new Coordinate((-73.972347), 40.677447);
        for (Vertex v : gg.getVertices()) {
            if ((v.getCoordinate().distance(vanderbiltAtPark)) < 5.0E-5) {
                /* we need the correct vanderbilt at park.  In this case,
                that's the one facing west on vanderbilt.
                 */
                int numParks = 0;
                int numCarltons = 0;
                for (Edge e : v.getOutgoing()) {
                    if (e.getToVertex().getName().contains("PARK")) {
                        numParks++;
                    }
                    if (e.getToVertex().getName().contains("CARLTON")) {
                        numCarltons++;
                    }
                }
                if ((numCarltons != 2) || (numParks != 1)) {
                    continue;
                }
                start = v;
            } else
                if ((v.getCoordinate().distance(grandAtLafayette)) < 1.0E-4) {
                    end = v;
                } else
                    if ((v.getCoordinate().distance(carltonAtPark)) < 5.0E-5) {
                        /* we need the correct carlton at park.  In this case,
                        that's the one facing west.
                         */
                        int numFlatbushes = 0;
                        int numParks = 0;
                        for (Edge e : v.getOutgoing()) {
                            if (e.getToVertex().getName().contains("FLATBUSH")) {
                                numFlatbushes++;
                            }
                            if (e.getToVertex().getName().contains("PARK")) {
                                numParks++;
                            }
                        }
                        if ((numFlatbushes != 2) || (numParks != 1)) {
                            continue;
                        }
                        carlton = v;
                    }


        }
        TestCase.assertNotNull(start);
        TestCase.assertNotNull(end);
        TestCase.assertNotNull(carlton);
        TestCase.assertEquals(3, start.getDegreeOut());
        TestCase.assertEquals(3, start.getDegreeIn());
        AStar aStar = new AStar();
        RoutingRequest opt = new RoutingRequest();
        opt.setRoutingContext(gg, start, end);
        ShortestPathTree spt = aStar.getShortestPathTree(opt);
        TestCase.assertNotNull(spt);
        // test that the option to walk bikes on the first or last segment works
        opt = new RoutingRequest(new org.opentripplanner.routing.core.TraverseModeSet(TraverseMode.BICYCLE));
        // Real live cyclists tell me that they would prefer to ride around the long way than to
        // walk their bikes the short way.  If we slow down the default biking speed, that will
        // force a change in preferences.
        opt.bikeSpeed = 2;
        opt.setRoutingContext(gg, start, carlton);
        spt = aStar.getShortestPathTree(opt);
        TestCase.assertNotNull(spt);
        /* commented out as bike walking is not supported */
        /* GraphPath path = spt.getPath(carlton.vertex);
        assertNotNull(path);
        assertTrue(path.edges.size() <= 3);

        wo.setArriveBy(true);
        spt = AStar.getShortestPathTreeBack(gg, start.vertex, carlton.vertex, new State(0), wo);
        assertNotNull(spt);

        path = spt.getPath(carlton.vertex);
        assertTrue(path.edges.size() <= 3);
         */
    }
}

