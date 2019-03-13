/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import MockData.BASIC_POLYGONS;
import OpenLayers3MapOutputFormat.MIME_TYPE;
import java.awt.Color;
import java.util.regex.Pattern;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.TestHttpClientRule;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.data.FeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Style;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.mock.web.MockHttpServletResponse;


public class OpenLayersMapOutputFormatTest extends WMSTestSupport {
    Pattern lookForEscapedParam = Pattern.compile(Pattern.quote("\"</script><script>alert(\'x-scripted\');</script><script>\": \'foo\'"));

    @Rule
    public TestHttpClientRule clientMocker = new TestHttpClientRule();

    /**
     * Test for GEOS-5318: xss vulnerability when a weird parameter is added to the request
     * (something like: %3C%2Fscript%
     * 3E%3Cscript%3Ealert%28%27x-scripted%27%29%3C%2Fscript%3E%3Cscript%3E=foo) the causes js code
     * execution.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testXssFix() throws Exception {
        Catalog catalog = getCatalog();
        final FeatureSource fs = catalog.getFeatureTypeByName(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart()).getFeatureSource(null, null);
        final Envelope env = fs.getBounds();
        LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        GetMapRequest request = createGetMapRequest(BASIC_POLYGONS);
        request.getRawKvp().put("</script><script>alert('x-scripted');</script><script>", "foo");
        request.getRawKvp().put("25064;ALERT(1)//419", "1");
        final WMSMapContent map = new WMSMapContent();
        map.getViewport().setBounds(new org.geotools.geometry.jts.ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);
        map.setRequest(request);
        StyleInfo styleByName = catalog.getStyleByName("Default");
        Style basicStyle = styleByName.getStyle();
        FeatureLayer layer = new FeatureLayer(fs, basicStyle);
        layer.setTitle("Title");
        map.addLayer(layer);
        request.setFormat("application/openlayers");
        String htmlDoc = getAsHTML(map);
        // check that weird param is correctly encoded to avoid js code execution
        int index = htmlDoc.replace("\\n", "").replace("\\r", "").indexOf("\"</script\\><script\\>alert(\\\'x-scripted\\\');</script\\><script\\>\": \'foo\'");
        Assert.assertTrue((index > (-1)));
        index = htmlDoc.replace("\\n", "").replace("\\r", "").indexOf("\"25064;ALERT(1)//419\": \'1\'");
        Assert.assertTrue((index > (-1)));
    }

    @Test
    public void testRastersFilteringCapabilities() throws Exception {
        // static raster layer supports filtering
        MockHttpServletResponse response = getAsServletResponse(("wms?service=WMS&version=1.1.0&request=GetMap&layers=gs:staticRaster" + (("&styles=&bbox=0.2372206885127698,40.562080748421806," + "14.592757149389236,44.55808294568743&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers")));
        String content = response.getContentAsString();
        Assert.assertThat(content.contains("var supportsFiltering = true;"), CoreMatchers.is(true));
        // world raster layer doesn't support filtering
        response = getAsServletResponse(("wms?service=WMS&version=1.1.0&request=GetMap&layers=wcs:World" + (("&styles=&bbox=0.2372206885127698,40.562080748421806," + "14.592757149389236,44.55808294568743&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers")));
        content = response.getContentAsString();
        Assert.assertThat(content.contains("var supportsFiltering = false;"), CoreMatchers.is(true));
        // if at least one layer supports filtering, overall filtering should be supported
        response = getAsServletResponse(("wms?service=WMS&version=1.1.0&request=GetMap&layers=wcs:World,gs:staticRaster" + (("&styles=&bbox=0.2372206885127698,40.562080748421806," + "14.592757149389236,44.55808294568743&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers")));
        content = response.getContentAsString();
        Assert.assertThat(content.contains("var supportsFiltering = true;"), CoreMatchers.is(true));
    }

    @Test
    public void testWMTSFilteringCapabilities() throws Exception {
        // Create a cascading layer
        createWMTSCatalogStuff();
        // wmts by itself should not support filtering
        MockHttpServletResponse response = getAsServletResponse(("wms?service=WMS&version=1.1.0&request=GetMap&layers=gs:wmtslayername" + (("&styles=&bbox=0.2372206885127698,40.562080748421806," + "14.592757149389236,44.55808294568743&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers")));
        String content = response.getContentAsString();
        Assert.assertThat(content.contains("var supportsFiltering = false;"), CoreMatchers.is(true));
        // wmts along with filterable layer should support filtering
        response = getAsServletResponse(("wms?service=WMS&version=1.1.0&request=GetMap&layers=gs:wmtslayername,gs:staticRaster" + (("&styles=&bbox=0.2372206885127698,40.562080748421806," + "14.592757149389236,44.55808294568743&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers")));
        content = response.getContentAsString();
        Assert.assertThat(content.contains("var supportsFiltering = true;"), CoreMatchers.is(true));
    }

    /**
     * Test for GEOS-8178: OpenLayersOutputFormat NoSuchAuthorityCodeExceptions being thrown due to
     * malformed URN codes.
     *
     * <p>Exception is thrown when decoding CRS in isWms13FlippedCRS which is called by produceMap,
     * test uses produceMap and reads the resulting output steam to ensure "yx: true" is returned
     * for EPSG:4326, output is false before fix
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUrnCodeFix() throws Exception {
        Catalog catalog = getCatalog();
        final FeatureSource fs = catalog.getFeatureTypeByName(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart()).getFeatureSource(null, null);
        final Envelope env = fs.getBounds();
        LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        GetMapRequest request = createGetMapRequest(BASIC_POLYGONS);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        request.setCrs(crs);
        final WMSMapContent map = new WMSMapContent();
        map.setRequest(request);
        request.setFormat("application/openlayers");
        String htmlDoc = getAsHTML(map);
        // System.out.println(htmlDoc);
        int index = htmlDoc.indexOf("yx : {'EPSG:4326' : true}");
        Assert.assertTrue((index > (-1)));
    }

    @Test
    public void testOL3vsOL2() throws Exception {
        // the base request
        String path = (("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(BASIC_POLYGONS))) + "&styles=&bbox=-180,-90,180,90&width=768&height=330") + "&srs=EPSG:4326&format=";
        final String firefoxAgent = "Firefox 40.1";
        String ie8Agent = "MSIE 8.";
        // generic request on browser supporting OL3
        String contentFirefox = getResponseContent((path + "application/openlayers"), firefoxAgent, MIME_TYPE);
        Assert.assertThat(contentFirefox, CoreMatchers.containsString("openlayers3/ol.js"));
        // generic request on browser not supporting OL3
        String contentIE8 = getResponseContent((path + "application/openlayers"), ie8Agent, OpenLayers2MapOutputFormat.MIME_TYPE);
        Assert.assertThat(contentIE8, CoreMatchers.containsString("OpenLayers.js"));
        // ask explicitly for OL2
        String contentOL2 = getResponseContent((path + "application/openlayers2"), firefoxAgent, OpenLayers2MapOutputFormat.MIME_TYPE);
        Assert.assertThat(contentOL2, CoreMatchers.containsString("OpenLayers.js"));
        // ask explicitly for OL3
        String contentOL3 = getResponseContent((path + "application/openlayers3"), firefoxAgent, MIME_TYPE);
        Assert.assertThat(contentOL3, CoreMatchers.containsString("openlayers3/ol.js"));
        // ask explicitly for OL3 on a non supporting browser
        String exception = getResponseContent((path + "application/openlayers3"), ie8Agent, "application/vnd.ogc.se_xml");
        Assert.assertThat(exception, CoreMatchers.containsString("not supported"));
    }

    @Test
    public void testExceptionsInImage() throws Exception {
        // the base request
        String path = (("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(BASIC_POLYGONS))) + "&styles=&bbox=-180,-90,180,90&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers";
        String html = getAsString(path);
        Assert.assertThat(html, CoreMatchers.containsString("\"exceptions\": \'application/vnd.ogc.se_inimage\'"));
    }

    @Test
    public void testExceptionsXML() throws Exception {
        // the base request
        String path = ((("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(BASIC_POLYGONS))) + "&styles=&bbox=-180,-90,180,90&width=768&height=330") + "&srs=EPSG:4326&format=application/openlayers") + "&exceptions=application/vnd.ogc.se_xml";
        String html = getAsString(path);
        Assert.assertThat(html, CoreMatchers.containsString("\"EXCEPTIONS\": \'application/vnd.ogc.se_xml\'"));
        Assert.assertThat(html, CoreMatchers.not(CoreMatchers.containsString("\"exceptions\": \'application/vnd.ogc.se_inimage\'")));
    }

    @Test
    public void testXssOL3() throws Exception {
        Catalog catalog = getCatalog();
        final FeatureSource fs = catalog.getFeatureTypeByName(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart()).getFeatureSource(null, null);
        final Envelope env = fs.getBounds();
        LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        GetMapRequest request = createGetMapRequest(BASIC_POLYGONS);
        request.putHttpRequestHeader("USER-AGENT", "Firefox 40.1");
        request.getRawKvp().put("</script><script>alert('x-scripted');</script><script>", "foo");
        request.getRawKvp().put("25064;ALERT(1)//419", "1");
        final WMSMapContent map = new WMSMapContent();
        map.getViewport().setBounds(new org.geotools.geometry.jts.ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);
        map.setRequest(request);
        StyleInfo styleByName = catalog.getStyleByName("Default");
        Style basicStyle = styleByName.getStyle();
        FeatureLayer layer = new FeatureLayer(fs, basicStyle);
        layer.setTitle("Title");
        map.addLayer(layer);
        request.setFormat("application/openlayers3");
        String htmlDoc = getAsHTMLOL3(map);
        // check that weird param is correctly encoded to avoid js code execution
        int index = htmlDoc.replace("\\n", "").replace("\\r", "").indexOf("\"</script\\><script\\>alert(\\\'x-scripted\\\');</script\\><script\\>\": \'foo\'");
        Assert.assertTrue((index > (-1)));
        index = htmlDoc.replace("\\n", "").replace("\\r", "").indexOf("\"25064;ALERT(1)//419\": \'1\'");
        Assert.assertTrue((index > (-1)));
    }
}

