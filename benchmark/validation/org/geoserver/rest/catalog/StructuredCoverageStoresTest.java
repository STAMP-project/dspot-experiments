/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.rest.RestBaseController;
import org.geotools.util.URLs;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class StructuredCoverageStoresTest extends CatalogRESTTestSupport {
    private static final String WATER_VIEW = "waterView";

    protected static QName WATTEMP = new QName(MockData.WCS_PREFIX, "watertemp", MockData.WCS_PREFIX);

    protected static QName S2_OVR = new QName(MockData.WCS_PREFIX, "s2_ovr", MockData.WCS_PREFIX);

    protected static QName IR_RGB = new QName(MockData.SF_URI, "ir-rgb", MockData.SF_PREFIX);

    private static final String RGB_IR_VIEW = "RgbIrView";

    List<File> movedFiles = new ArrayList<>();

    private XpathEngine xpath;

    private File mosaic;

    @Test
    public void testIndexResourcesXML() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("watertemp", "/coverageStore/name", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("watertemp", "/coverage/name", dom);
        assertXpathEvaluatesTo("watertemp", "/coverage/nativeName", dom);
        // todo: check there is a link to the index
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("4", "count(//Schema/attributes/Attribute)", dom);
        assertXpathEvaluatesTo("org.locationtech.jts.geom.MultiPolygon", "/Schema/attributes/Attribute[name='the_geom']/binding", dom);
        assertXpathEvaluatesTo("java.lang.String", "/Schema/attributes/Attribute[name='location']/binding", dom);
        assertXpathEvaluatesTo("java.util.Date", "/Schema/attributes/Attribute[name='ingestion']/binding", dom);
        assertXpathEvaluatesTo("java.lang.Integer", "/Schema/attributes/Attribute[name='elevation']/binding", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        assertXpathEvaluatesTo("2", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:elevation", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:elevation", dom);
        // get the granules ids
        String octoberId = xpath.evaluate("//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/@fid", dom);
        String novemberId = xpath.evaluate("//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/@fid", dom);
        dom = getAsDOM(((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/") + octoberId) + ".xml"));
        // print(dom);
        assertXpathEvaluatesTo(octoberId, "//gf:watertemp/@fid", dom);
        assertXpathEvaluatesTo("NCOM_wattemp_000_20081031T0000000_12.tiff", "//gf:watertemp/gf:location", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp/gf:elevation", dom);
        dom = getAsDOM(((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/") + novemberId) + ".xml"));
        // print(dom);
        assertXpathEvaluatesTo(novemberId, "//gf:watertemp/@fid", dom);
        assertXpathEvaluatesTo("NCOM_wattemp_000_20081101T0000000_12.tiff", "//gf:watertemp/gf:location", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp/gf:elevation", dom);
    }

    @Test
    public void testGranulesOnRenamedCoverage() throws Exception {
        // rename the watertemp coverage
        CoverageStoreInfo store = CatalogRESTTestSupport.catalog.getCoverageStoreByName("watertemp");
        CoverageInfo coverage = CatalogRESTTestSupport.catalog.getCoverageByCoverageStore(store, "watertemp");
        coverage.setName("renamed");
        CatalogRESTTestSupport.catalog.save(coverage);
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/renamed/index.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("4", "count(//Schema/attributes/Attribute)", dom);
        assertXpathEvaluatesTo("org.locationtech.jts.geom.MultiPolygon", "/Schema/attributes/Attribute[name='the_geom']/binding", dom);
        assertXpathEvaluatesTo("java.lang.String", "/Schema/attributes/Attribute[name='location']/binding", dom);
        assertXpathEvaluatesTo("java.util.Date", "/Schema/attributes/Attribute[name='ingestion']/binding", dom);
        assertXpathEvaluatesTo("java.lang.Integer", "/Schema/attributes/Attribute[name='elevation']/binding", dom);
    }

    @Test
    public void testIndexResourcesJSON() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index.json"))));
        // print(json);
        JSONObject schema = json.getJSONObject("Schema");
        JSONObject external = schema.getJSONObject("attributes");
        JSONArray attributes = external.getJSONArray("Attribute");
        Assert.assertEquals(4, attributes.size());
        Assert.assertEquals("org.locationtech.jts.geom.MultiPolygon", attributes.getJSONObject(0).get("binding"));
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.json"))));
        // print(json);
        JSONArray features = json.getJSONArray("features");
        String octoberId = null;
        for (int i = 0; i < (features.size()); i++) {
            JSONObject feature = features.getJSONObject(i);
            String location = feature.getJSONObject("properties").getString("location");
            if ("NCOM_wattemp_000_20081031T0000000_12.tiff".equals(location)) {
                octoberId = feature.getString("id");
            }
        }
        json = ((JSONObject) (getAsJSON(((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/") + octoberId) + ".json"), 200)));
        // print(json);
        features = json.getJSONArray("features");
        Assert.assertEquals(1, features.size());
        JSONObject feature = features.getJSONObject(0);
        Assert.assertEquals(octoberId, feature.get("id"));
        JSONObject properties = feature.getJSONObject("properties");
        Assert.assertEquals("NCOM_wattemp_000_20081031T0000000_12.tiff", properties.get("location"));
        Assert.assertEquals(0, properties.get("elevation"));
    }

    @Test
    public void testMissingGranule() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/notThere.xml"));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetWrongGranule() throws Exception {
        // Parameters for the request
        String ws = "wcs";
        String cs = "watertemp";
        String g = "notThere";
        // Request path
        String requestPath = ((((((((RestBaseController.ROOT_PATH) + "/workspaces/") + ws) + "/coveragestores/") + cs) + "/coverages/") + cs) + "/index/granules/") + g;
        // Exception path
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), CoreMatchers.containsString(g));
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteSingleGranule() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        // get the granule ids
        String octoberId = xpath.evaluate("//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/@fid", dom);
        Assert.assertNotNull(octoberId);
        // delete it
        MockHttpServletResponse response = deleteAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/") + octoberId));
        Assert.assertEquals(200, response.getStatus());
        // check it's gone from the index
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        assertXpathEvaluatesTo("1", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("0", "count(//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff'])", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:elevation", dom);
    }

    @Test
    public void testDeleteSingleGranuleGsConfigStyle() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        // get the granule ids
        String octoberId = xpath.evaluate("//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/@fid", dom);
        Assert.assertNotNull(octoberId);
        // delete it like gsconfig does (yes, it really appens "./json" at the end)
        MockHttpServletResponse response = deleteAsServletResponse(((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules/") + octoberId) + "/.json"));
        Assert.assertEquals(200, response.getStatus());
        // check it's gone from the index
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        assertXpathEvaluatesTo("1", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("0", "count(//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff'])", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:elevation", dom);
    }

    @Test
    public void testDeleteAllGranules() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"), 200);
        assertXpathEvaluatesTo("2", "count(//gf:watertemp)", dom);
        // print(dom);
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules"));
        Assert.assertEquals(200, response.getStatus());
        // check it's gone from the index
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        assertXpathEvaluatesTo("0", "count(//gf:watertemp)", dom);
    }

    @Test
    public void testDeleteByFilter() throws Exception {
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        assertXpathEvaluatesTo("2", "count(//gf:watertemp)", dom);
        // print(dom);
        MockHttpServletResponse response = deleteAsServletResponse((((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages") + "/watertemp/index/granules?filter=ingestion=2008-11-01T00:00:00Z"));
        Assert.assertEquals(200, response.getStatus());
        // check it's gone from the index
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:elevation", dom);
    }

    @Test
    public void testHarvestSingle() throws Exception {
        File file = movedFiles.get(0);
        File target = new File(mosaic, file.getName());
        Assert.assertTrue(file.renameTo(target));
        URL url = URLs.fileToUrl(target.getCanonicalFile());
        String body = url.toExternalForm();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/external.imagemosaic"), body, "text/plain");
        Assert.assertEquals(202, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        assertXpathEvaluatesTo("3", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("1", (("count(//gf:watertemp[gf:location = '" + (file.getName())) + "'])"), dom);
    }

    @Test
    public void testHarvestSingleSimplePath() throws Exception {
        File file = movedFiles.get(0);
        File target = new File(mosaic, file.getName());
        Assert.assertTrue(file.renameTo(target));
        String body = target.getCanonicalPath();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/external.imagemosaic"), body, "text/plain");
        Assert.assertEquals(202, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        assertXpathEvaluatesTo("3", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("1", (("count(//gf:watertemp[gf:location = '" + (file.getName())) + "'])"), dom);
    }

    @Test
    public void testHarvestMulti() throws Exception {
        for (File file : movedFiles) {
            File target = new File(mosaic, file.getName());
            Assert.assertTrue(file.renameTo(target));
        }
        // re-harvest the entire mosaic (two files refreshed, two files added)
        URL url = URLs.fileToUrl(mosaic.getCanonicalFile());
        String body = url.toExternalForm();
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/external.imagemosaic"), body, "text/plain");
        Assert.assertEquals(202, response.getStatus());
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/watertemp/index/granules.xml"));
        // print(dom);
        assertXpathEvaluatesTo("4", "count(//gf:watertemp)", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081031T0000000_12.tiff']/gf:elevation", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("0", "//gf:watertemp[gf:location = 'NCOM_wattemp_000_20081101T0000000_12.tiff']/gf:elevation", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_100_20081031T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("100", "//gf:watertemp[gf:location = 'NCOM_wattemp_100_20081031T0000000_12.tiff']/gf:elevation", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00Z", "//gf:watertemp[gf:location = 'NCOM_wattemp_100_20081101T0000000_12.tiff']/gf:ingestion", dom);
        assertXpathEvaluatesTo("100", "//gf:watertemp[gf:location = 'NCOM_wattemp_100_20081101T0000000_12.tiff']/gf:elevation", dom);
    }

    @Test
    public void testGetIndexAndGranulesOnView() throws Exception {
        // get and test the index
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/waterView/index.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("4", "count(//Schema/attributes/Attribute)", dom);
        assertXpathEvaluatesTo("org.locationtech.jts.geom.MultiPolygon", "/Schema/attributes/Attribute[name='the_geom']/binding", dom);
        assertXpathEvaluatesTo("java.lang.String", "/Schema/attributes/Attribute[name='location']/binding", dom);
        assertXpathEvaluatesTo("java.util.Date", "/Schema/attributes/Attribute[name='ingestion']/binding", dom);
        assertXpathEvaluatesTo("java.lang.Integer", "/Schema/attributes/Attribute[name='elevation']/binding", dom);
        // get and test the granules
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/waterView/index/granules.xml"), 200);
        print(dom);
        assertXpathEvaluatesTo("2", "count(//gf:waterView)", dom);
    }

    @Test
    public void testGetIndexAndGranulesOnMultiCoverageView() throws Exception {
        // get and test the index
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index.xml"), 200);
        // print(dom);
        assertXpathEvaluatesTo("2", "count(//Schema/attributes/Attribute)", dom);
        assertXpathEvaluatesTo("org.locationtech.jts.geom.Polygon", "/Schema/attributes/Attribute[name='the_geom']/binding", dom);
        assertXpathEvaluatesTo("java.lang.String", "/Schema/attributes/Attribute[name='location']/binding", dom);
        // get and test the granules
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index/granules.xml"), 200);
        print(dom);
        assertXpathEvaluatesTo("2", "count(//gf:RgbIrView)", dom);
        assertXpathExists("//gf:RgbIrView[@fid='RgbIrView.rgb.1']", dom);
        assertXpathExists("//gf:RgbIrView[@fid='RgbIrView.ir.1']", dom);
    }

    @Test
    public void testGetGranuleInMultiCoverageView() throws Exception {
        // get a single granule
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index/granules/RgbIrView.rgb.1"), 200);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//gf:RgbIrView)", dom);
        assertXpathExists("//gf:RgbIrView[@fid='RgbIrView.rgb.1']", dom);
        // and get the other
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index/granules/RgbIrView.ir.1"), 200);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//gf:RgbIrView)", dom);
        assertXpathExists("//gf:RgbIrView[@fid='RgbIrView.ir.1']", dom);
    }

    @Test
    public void testDeleteGranuleInMultiCoverageView() throws Exception {
        // delete granule
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index/granules/RgbIrView.rgb.1"));
        Assert.assertEquals(200, response.getStatus());
        // try to get it, it should provide a 404 now
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/sf/coveragestores/ir-rgb/coverages/RgbIrView/index/granules/RgbIrView.rgb.1"));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetGranuleInSingleCoverageView() throws Exception {
        // get a single granule
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/waterView/index/granules/waterView.watertemp.1"), 200);
        print(dom);
        assertXpathEvaluatesTo("1", "count(//gf:waterView)", dom);
        assertXpathExists("//gf:waterView[@fid='waterView.watertemp.1']", dom);
    }

    @Test
    public void testDeleteGranuleInSingleCoverageView() throws Exception {
        // delete granule
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/waterView/index/granules/waterView.watertemp.1"));
        Assert.assertEquals(200, response.getStatus());
        // try to get it, it should provide a 404 now
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/wcs/coveragestores/watertemp/coverages/waterView/index/granules/waterView.watertemp.1"));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDeletePurgeNoneFilter() throws Exception {
        List<String> filesAfter = removeWithPurge("&purge=none");
        // list the files, they must still contain g3.tif
        Assert.assertThat(filesAfter, Matchers.allOf(Matchers.hasItem("g3.tif"), Matchers.hasItem("g3.tif.ovr")));
    }

    @Test
    public void testDeletePurgeAll() throws Exception {
        List<String> filesAfter = removeWithPurge("&purge=all");
        // list the files, g3 must be gone
        Assert.assertThat(filesAfter, Matchers.not(Matchers.hasItem(CoreMatchers.containsString("g3.tif"))));
    }
}

