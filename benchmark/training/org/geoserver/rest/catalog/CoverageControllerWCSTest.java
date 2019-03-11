/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.rest.RestBaseController;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CoverageControllerWCSTest extends CatalogRESTTestSupport {
    private static final double DELTA = 1.0E-6;

    @Test
    public void testGetAllByCoverageStore() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:usa" + ("&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" + "&gridbasecrs=EPSG:4326&store=true");
        Document dom = getAsDOM(req);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addCoverageStore(true);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("coverage").getLength());
        assertXpathEvaluatesTo("1", "count(//coverage/name[text()='usa'])", dom);
    }

    @Test
    public void testPostAsXML() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:usa" + ("&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" + "&gridbasecrs=EPSG:4326&store=true");
        Document dom = getAsDOM(req);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addCoverageStore(false);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("coverage").getLength());
        String xml = "<coverage>" + (((((((((((((((("<name>usa</name>" + "<title>usa is a A raster file accompanied by a spatial data file</title>") + "<description>Generated from WorldImage</description>") + "<srs>EPSG:4326</srs>") + /* "<latLonBoundingBox>"+
        "<minx>-130.85168</minx>"+
        "<maxx>-62.0054</maxx>"+
        "<miny>20.7052</miny>"+
        "<maxy>54.1141</maxy>"+
        "</latLonBoundingBox>"+
        "<nativeBoundingBox>"+
        "<minx>-130.85168</minx>"+
        "<maxx>-62.0054</maxx>"+
        "<miny>20.7052</miny>"+
        "<maxy>54.1141</maxy>"+
        "<crs>EPSG:4326</crs>"+
        "</nativeBoundingBox>"+
        "<grid dimension=\"2\">"+
        "<range>"+
        "<low>0 0</low>"+
        "<high>983 598</high>"+
        "</range>"+
        "<transform>"+
        "<scaleX>0.07003690742624616</scaleX>"+
        "<scaleY>-0.05586772575250837</scaleY>"+
        "<shearX>0.0</shearX>"+
        "<shearX>0.0</shearX>"+
        "<translateX>-130.81666154628687</translateX>"+
        "<translateY>54.08616613712375</translateY>"+
        "</transform>"+
        "<crs>EPSG:4326</crs>"+
        "</grid>"+
         */
        "<supportedFormats>") + "<string>PNG</string>") + "<string>GEOTIFF</string>") + "</supportedFormats>") + "<requestSRS>") + "<string>EPSG:4326</string>") + "</requestSRS>") + "<responseSRS>") + "<string>EPSG:4326</string>") + "</responseSRS>") + "<store>usaWorldImage</store>") + "<namespace>gs</namespace>") + "</coverage>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/usa"));
        dom = getAsDOM(req);
        Assert.assertEquals("wcs:Coverages", dom.getDocumentElement().getNodeName());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/usa.xml"));
        assertXpathEvaluatesTo("-130.85168", "/coverage/latLonBoundingBox/minx", dom);
        assertXpathEvaluatesTo("983 598", "/coverage/grid/range/high", dom);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(1, dom.getElementsByTagName("coverage").getLength());
    }

    @Test
    public void testPostAsJSON() throws Exception {
        // remove the test store and test that the layer is not available
        removeStore("gs", "usaWorldImage");
        String request = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:usa" + "&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff&gridbasecrs=EPSG:4326&store=true";
        Document document = getAsDOM(request);
        Assert.assertEquals("ows:ExceptionReport", document.getDocumentElement().getNodeName());
        // add the test store, no coverages should be available
        addCoverageStore(false);
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.json"))));
        Assert.assertThat(json.getString("coverages").isEmpty(), CoreMatchers.is(true));
        // content for the POST request
        String content = "{" + (((((((((((((((((((((((("    \"coverage\": {" + "        \"description\": \"Generated from WorldImage\",") + "        \"name\": \"usa\",") + "        \"namespace\": \"gs\",") + "        \"requestSRS\": {") + "            \"string\": [") + "                \"EPSG:4326\"") + "            ]") + "        },") + "        \"responseSRS\": {") + "            \"string\": [") + "                \"EPSG:4326\"") + "            ]") + "        },") + "        \"srs\": \"EPSG:4326\",") + "        \"store\": \"usaWorldImage\",") + "        \"supportedFormats\": {") + "            \"string\": [") + "                \"PNG\",") + "                \"GEOTIFF\"") + "            ]") + "        },") + "        \"title\": \"usa is a A raster file accompanied by a spatial data file\"") + "    }") + "}");
        // perform the POST request that will create the USA coverage
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/"), content, "application/json");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/usa"));
        // check that the coverage exists using the WCS service
        document = getAsDOM(request);
        Assert.assertEquals("wcs:Coverages", document.getDocumentElement().getNodeName());
        // check create coverage attributes
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/usa.json"))));
        Assert.assertThat(json.getJSONObject("coverage").getString("name"), CoreMatchers.is("usa"));
        Assert.assertThat(json.getJSONObject("coverage").getJSONObject("latLonBoundingBox").getString("minx"), CoreMatchers.is("-130.85168"));
        Assert.assertThat(json.getJSONObject("coverage").getJSONObject("grid").getJSONObject("range").getString("high"), CoreMatchers.is("983 598"));
        // check that the coverage is listed
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.json"))));
        JSONArray coverages = json.getJSONObject("coverages").getJSONArray("coverage");
        Assert.assertThat(coverages.size(), CoreMatchers.is(1));
        Assert.assertThat(coverages.getJSONObject(0).getString("name"), CoreMatchers.is("usa"));
    }

    @Test
    public void testPostAsXMLWithNativeName() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:differentName" + ("&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" + "&gridbasecrs=EPSG:4326&store=true");
        Document dom = getAsDOM(req);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addCoverageStore(false);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("coverage").getLength());
        String xml = "<coverage>" + ((((((((((((((((("<name>differentName</name>" + "<title>usa is a A raster file accompanied by a spatial data file</title>") + "<description>Generated from WorldImage</description>") + "<srs>EPSG:4326</srs>") + "<supportedFormats>") + "<string>PNG</string>") + "<string>GEOTIFF</string>") + "</supportedFormats>") + "<requestSRS>") + "<string>EPSG:4326</string>") + "</requestSRS>") + "<responseSRS>") + "<string>EPSG:4326</string>") + "</responseSRS>") + "<store>usaWorldImage</store>") + "<namespace>gs</namespace>") + "<nativeCoverageName>usa</nativeCoverageName>") + "</coverage>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName"));
        dom = getAsDOM(req);
        Assert.assertEquals("wcs:Coverages", dom.getDocumentElement().getNodeName());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName.xml"));
        assertXpathEvaluatesTo("-130.85168", "/coverage/latLonBoundingBox/minx", dom);
        assertXpathEvaluatesTo("983 598", "/coverage/grid/range/high", dom);
    }

    @Test
    public void testPostNewAsXMLWithNativeCoverageName() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:differentName" + ("&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" + "&gridbasecrs=EPSG:4326&store=true");
        Document dom = getAsDOM(req);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addCoverageStore(false);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("coverage").getLength());
        String xml = "<coverage>" + (("<name>differentName</name>" + "<nativeCoverageName>usa</nativeCoverageName>") + "</coverage>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName"));
        dom = getAsDOM(req);
        Assert.assertEquals("wcs:Coverages", dom.getDocumentElement().getNodeName());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName.xml"));
        assertXpathEvaluatesTo("differentName", "/coverage/name", dom);
        assertXpathEvaluatesTo("differentName", "/coverage/title", dom);
        assertXpathEvaluatesTo("usa", "/coverage/nativeCoverageName", dom);
    }

    @Test
    public void testPostNewAsXMLWithNativeNameFallback() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:differentName" + ("&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" + "&gridbasecrs=EPSG:4326&store=true");
        Document dom = getAsDOM(req);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addCoverageStore(false);
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages.xml"));
        Assert.assertEquals(0, dom.getElementsByTagName("coverage").getLength());
        String xml = "<coverage>" + (("<name>differentName</name>" + "<nativeName>usa</nativeName>") + "</coverage>");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName"));
        dom = getAsDOM(req);
        Assert.assertEquals("wcs:Coverages", dom.getDocumentElement().getNodeName());
        dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName.xml"));
        assertXpathEvaluatesTo("differentName", "/coverage/name", dom);
        assertXpathEvaluatesTo("differentName", "/coverage/title", dom);
        assertXpathEvaluatesTo("usa", "/coverage/nativeCoverageName", dom);
    }
}

