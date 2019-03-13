/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import it.geosolutions.imageio.utilities.ImageIOUtilities;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.rest.RestBaseController;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.util.NumberRange;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverageReader;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CoverageControllerTest extends CatalogRESTTestSupport {
    private static final double DELTA = 1.0E-6;

    @Test
    public void testGetAllByWorkspaceXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coverages.xml"));
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getCoveragesByNamespace(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("wcs")).size(), dom.getElementsByTagName("coverage").getLength());
    }

    @Test
    public void testGetAllByWorkspaceJSON() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coverages.json"))));
        JSONArray coverages = json.getJSONObject("coverages").getJSONArray("coverage");
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getCoveragesByNamespace(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("wcs")).size(), coverages.size());
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages")).getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble.xml"));
        assertXpathEvaluatesTo("BlueMarble", "/coverage/name", dom);
        assertXpathEvaluatesTo("1", "count(//latLonBoundingBox)", dom);
        assertXpathEvaluatesTo("1", "count(//nativeFormat)", dom);
        assertXpathEvaluatesTo("1", "count(//grid)", dom);
        assertXpathEvaluatesTo("1", "count(//supportedFormats)", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble.json"));
        JSONObject coverage = getJSONObject("coverage");
        Assert.assertNotNull(coverage);
        Assert.assertEquals("BlueMarble", coverage.get("name"));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble.html"));
        Assert.assertEquals("html", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testGetWrongCoverage() throws Exception {
        // Parameters for the request
        String ws = "wcs";
        String cs = "BlueMarble";
        String c = "BlueMarblesssss";
        // Request path
        String requestPath = (((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/coverages/") + c) + ".html";
        String requestPath2 = (((((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/coveragestores/") + cs) + "/coverages/") + c) + ".html";
        // Exception path
        String exception = (("No such coverage: " + ws) + ",") + c;
        String exception2 = (((("No such coverage: " + ws) + ",") + cs) + ",") + c;
        // CASE 1: No coveragestore set
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
        // CASE 2: coveragestore set
        // First request should thrown an exception
        response = getAsServletResponse(requestPath2);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(exception2));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath2 + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(response.getContentAsString().contains(exception2));
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testPutWithCalculation() throws Exception {
        String path = (RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/DEM/coverages/DEM.xml";
        String clearLatLonBoundingBox = "<coverage>" + ("<latLonBoundingBox/>" + "</coverage>");
        MockHttpServletResponse response = putAsServletResponse(path, clearLatLonBoundingBox, "text/xml");
        Assert.assertEquals(("Couldn\'t remove lat/lon bounding box: \n" + (response.getContentAsString())), 200, response.getStatus());
        Document dom = getAsDOM(path);
        assertXpathEvaluatesTo("0.0", "/coverage/latLonBoundingBox/minx", dom);
        print(dom);
        String updateNativeBounds = "<coverage>" + ("<srs>EPSG:3785</srs>" + "</coverage>");
        response = putAsServletResponse(path, updateNativeBounds, "text/xml");
        Assert.assertEquals(("Couldn\'t update native bounding box: \n" + (response.getContentAsString())), 200, response.getStatus());
        dom = getAsDOM(path);
        print(dom);
        assertXpathExists("/coverage/nativeBoundingBox/minx[text()!='0.0']", dom);
    }

    // public void testPostAsJSON() throws Exception {
    // Document dom = getAsDOM( "wfs?request=getfeature&typename=wcs:pdsa");
    // assertEquals( "ows:ExceptionReport", dom.getDocumentElement().getNodeName());
    // 
    // addPropertyDataStore(false);
    // String json =
    // "{" +
    // "'coverage':{" +
    // "'name':'pdsa'," +
    // "'nativeName':'pdsa'," +
    // "'srs':'EPSG:4326'," +
    // "'nativeBoundingBox':{" +
    // "'minx':0.0," +
    // "'maxx':1.0," +
    // "'miny':0.0," +
    // "'maxy':1.0," +
    // "'crs':'EPSG:4326'" +
    // "}," +
    // "'nativeCRS':'EPSG:4326'," +
    // "'store':'pds'" +
    // "}" +
    // "}";
    // MockHttpServletResponse response =
    // postAsServletResponse( RestBaseController.ROOT_PATH +
    // "/workspaces/gs/coveragestores/pds/coverages/", json, "text/json");
    // 
    // assertEquals( 201, response.getStatusCode() );
    // assertNotNull( response.getHeader( "Location") );
    // assertTrue( response.getHeader("Location").endsWith(
    // "/workspaces/gs/coveragestores/pds/coverages/pdsa" ) );
    // 
    // dom = getAsDOM( "wfs?request=getfeature&typename=gs:pdsa");
    // assertEquals( "wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
    // assertEquals( 2, dom.getElementsByTagName( "gs:pdsa").getLength());
    // }
    // 
    @Test
    public void testPostToResource() throws Exception {
        String xml = "<coverage>" + ("<name>foo</name>" + "</coverage>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testPutXML() throws Exception {
        String xml = "<coverage>" + ("<title>new title</title>" + "</coverage>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble.xml"));
        assertXpathEvaluatesTo("new title", "/coverage/title", dom);
        CoverageInfo c = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble");
        Assert.assertEquals("new title", c.getTitle());
    }

    @Test
    public void testPutJSON() throws Exception {
        // update the coverage title
        String jsonPayload = "{\n" + ((("    \"coverage\": {\n" + "        \"title\": \"new title 2\"\n") + "    }\n") + "}");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble"), jsonPayload, "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check that the coverage title was correctly updated
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble.json"))));
        Assert.assertThat(json.getJSONObject("coverage").getString("title"), CoreMatchers.is("new title 2"));
        CoverageInfo coverage = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble");
        Assert.assertEquals("new title 2", coverage.getTitle());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        CoverageInfo c = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble");
        Assert.assertTrue(c.isEnabled());
        boolean isAdvertised = c.isAdvertised();
        String xml = "<coverage>" + ("<title>new title</title>" + "</coverage>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        c = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble");
        Assert.assertTrue(c.isEnabled());
        Assert.assertEquals(isAdvertised, c.isAdvertised());
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<coverage>" + ("<title>new title</title>" + "</coverage>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/NonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble"));
        for (LayerInfo l : CatalogRESTTestSupport.catalog.getLayers(CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble"))) {
            CatalogRESTTestSupport.catalog.remove(l);
        }
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble"));
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/NonExistant")).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble"));
        Assert.assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("wcs:BlueMarble"));
        Assert.assertEquals(403, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble")).getStatus());
        Assert.assertEquals(200, deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/BlueMarble/coverages/BlueMarble?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "BlueMarble"));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("wcs:BlueMarble"));
    }

    @Test
    public void testCoverageWrapping() throws Exception {
        String xml = "<coverage>" + (("<name>tazdem</name>" + "<title>new title</title>") + "</coverage>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/DEM/coverages/DEM"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/DEM/coverages/tazdem.xml"));
        assertXpathEvaluatesTo("new title", "/coverage/title", dom);
        CoverageInfo c = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "tazdem");
        Assert.assertEquals("new title", c.getTitle());
        List<CoverageDimensionInfo> dimensions = c.getDimensions();
        CoverageDimensionInfo dimension = dimensions.get(0);
        Assert.assertEquals("GRAY_INDEX", dimension.getName());
        NumberRange range = dimension.getRange();
        Assert.assertEquals(Double.NEGATIVE_INFINITY, range.getMinimum(), CoverageControllerTest.DELTA);
        Assert.assertEquals(Double.POSITIVE_INFINITY, range.getMaximum(), CoverageControllerTest.DELTA);
        Assert.assertEquals("GridSampleDimension[-Infinity,Infinity]", dimension.getDescription());
        List<Double> nullValues = dimension.getNullValues();
        Assert.assertEquals((-9999.0), nullValues.get(0), CoverageControllerTest.DELTA);
        // Updating dimension properties
        xml = "<coverage>" + ((((((((((((((("<name>tazdem</name>" + "<title>new title</title>") + "<dimensions>") + "<coverageDimension>") + "<name>Elevation</name>") + "<description>GridSampleDimension[-100.0,1000.0]</description>") + "<nullValues>") + "<double>-999</double>") + "</nullValues>") + "<range>") + "<min>-100</min>") + "<max>1000</max>") + "</range>") + "</coverageDimension>") + "</dimensions>") + "</coverage>");
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/DEM/coverages/tazdem"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        c = CatalogRESTTestSupport.catalog.getCoverageByName("wcs", "tazdem");
        dimensions = c.getDimensions();
        dimension = dimensions.get(0);
        Assert.assertEquals("Elevation", dimension.getName());
        range = dimension.getRange();
        Assert.assertEquals((-100.0), range.getMinimum(), CoverageControllerTest.DELTA);
        Assert.assertEquals(1000.0, range.getMaximum(), CoverageControllerTest.DELTA);
        Assert.assertEquals("GridSampleDimension[-100.0,1000.0]", dimension.getDescription());
        nullValues = dimension.getNullValues();
        Assert.assertEquals((-999.0), nullValues.get(0), CoverageControllerTest.DELTA);
        CoverageStoreInfo coverageStore = CatalogRESTTestSupport.catalog.getStoreByName("wcs", "DEM", CoverageStoreInfo.class);
        GridCoverageReader reader = null;
        GridCoverage2D coverage = null;
        try {
            reader = CatalogRESTTestSupport.catalog.getResourcePool().getGridCoverageReader(coverageStore, "tazdem", null);
            coverage = ((GridCoverage2D) (reader.read("tazdem", null)));
            GridSampleDimension sampleDim = coverage.getSampleDimension(0);
            double[] noDataValues = sampleDim.getNoDataValues();
            Assert.assertEquals((-999.0), noDataValues[0], CoverageControllerTest.DELTA);
            range = sampleDim.getRange();
            Assert.assertEquals((-100.0), range.getMinimum(), CoverageControllerTest.DELTA);
            Assert.assertEquals(1000.0, range.getMaximum(), CoverageControllerTest.DELTA);
        } finally {
            if (coverage != null) {
                try {
                    ImageIOUtilities.disposeImage(coverage.getRenderedImage());
                    coverage.dispose(true);
                } catch (Throwable t) {
                    // Does nothing;
                }
            }
            if (reader != null) {
                try {
                    reader.dispose();
                } catch (Throwable t) {
                    // Does nothing;
                }
            }
        }
    }
}

