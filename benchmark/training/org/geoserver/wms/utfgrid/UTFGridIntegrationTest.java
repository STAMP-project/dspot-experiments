/**
 * (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.utfgrid;


import net.sf.json.JSONObject;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class UTFGridIntegrationTest extends WMSTestSupport {
    /**
     * The UTF grid format shows up in the caps document. The format name is freeform
     */
    @Test
    public void testCapabilities11() throws Exception {
        Document dom = getAsDOM("wms?service=WMS&request=GetCapabilities&version=1.1.0");
        // print(dom);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals("1", xpath.evaluate("count(//GetMap[Format='utfgrid'])", dom));
    }

    /**
     * The UTF grid format shows up in the caps document. WMS 1.3 requires the usage of mime types
     * that will match the result content type
     */
    @Test
    public void testCapabilities13() throws Exception {
        Document dom = getAsDOM("wms?service=WMS&request=GetCapabilities&version=1.3.0");
        // print(dom);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals("1", xpath.evaluate("count(//wms:GetMap[wms:Format='application/json;type=utfgrid'])", dom));
    }

    @Test
    public void testEmptyOutput() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Forests" + "&styles=&bbox=-10.0028,-0.0028,-9.0048,0.0048&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        Assert.assertEquals(1, tester.getKeyCount());
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                tester.assertGridPixel(' ', i, j);
            }
        }
    }

    @Test
    public void testPolygonGraphicFill() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Forests" + "&styles=&bbox=-0.0028,-0.0028,0.0048,0.0048&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        // sample some pixels
        tester.assertGridPixel(' ', 10, 20);
        tester.assertGridPixel('!', 60, 20);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("Green Forest", f.getString("NAME"));
    }

    @Test
    public void testPolygonReproject() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Forests" + "&styles=polygon&bbox=-280,-280,480,480&width=256&height=256&srs=EPSG:3857&format=utfgrid"));
        // sample some pixels
        tester.assertGridPixel(' ', 10, 10);
        tester.assertGridPixel('!', 60, 10);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("Green Forest", f.getString("NAME"));
    }

    @Test
    public void testAlternateMimetype() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=line" + "&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=application/json;type=utfgrid"));
        checkRoadSegments(tester);
    }

    /**
     * Using a color classified style. Should not make any different to UTFGrid, as long as we paint
     * all features
     */
    @Test
    public void testLineSymbolizerClassified() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=" + "&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        checkRoadSegments(tester);
    }

    @Test
    public void testLineSymbolizer() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=line" + "&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        checkRoadSegments(tester);
    }

    /**
     * Check we get a usable result even with super-thin lines
     */
    @Test
    public void testThinLineSymbolizer() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=thin_line" + "&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        checkRoadSegments(tester);
    }

    /**
     * Check we get a correct result with graphic stroked + dash array
     */
    @Test
    public void testDotted() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=dotted" + "&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=utfgrid"));
        checkRoadSegments(tester);
    }

    @Test
    public void testFilteredLineSymbolizer() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:RoadSegments&styles=line" + ("&bbox=-0.0042,-0.0042,0.0042,0.0042&width=256&height=256&srs=EPSG:4326&format=utfgrid" + "&CQL_FILTER=NAME%3D%27Main Street%27")));
        tester.assertGridPixel(' ', 15, 54);
        tester.assertGridPixel('!', 22, 49);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("105", f.getString("FID"));
        Assert.assertEquals("Main Street", f.getString("NAME"));
        tester.assertGridPixel(' ', 36, 1);
        tester.assertGridPixel(' ', 36, 21);
    }

    @Test
    public void testSolidFillAndRuleWithTextSymbolizerOnly() throws Exception {
        // used to blow up due to the text symbolizer alone
        UTFGridTester tester = getAsGridTester(("wms?LAYERS=sf%3Astates&STYLES=population&FORMAT=utfgrid" + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&BBOX=-95.8506355,24.955967,-66.969849,53.8367535&WIDTH=256&HEIGHT=256"));
    }

    @Test
    public void testCircle() throws Exception {
        UTFGridTester tester = getAsGridTester("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Bridges&styles=circle&bbox=0,0.0005,0.0004,0.0009&width=256&height=256&srs=EPSG:4326&format=utfgrid");
        tester.assertGridPixel(' ', 25, 30);
        tester.assertGridPixel('!', 32, 32);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("110", f.getString("FID"));
        Assert.assertEquals("Cam Bridge", f.getString("NAME"));
    }

    @Test
    public void testLargeCircle() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Bridges&styles=circle&bbox=0,0.0005,0.0004,0.0009&width=256&height=256&srs=EPSG:4326&format=utfgrid" + "&env=radius:64"));
        tester.assertGridPixel('!', 25, 30);
        tester.assertGridPixel('!', 32, 32);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("110", f.getString("FID"));
        Assert.assertEquals("Cam Bridge", f.getString("NAME"));
    }

    @Test
    public void testDecoratedCircle() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap&layers=cite:Bridges&styles=circle&bbox=0,0.0005,0.0004,0.0009&width=256&height=256&srs=EPSG:4326&format=utfgrid" + "&env=radius:64"));
        tester.assertGridPixel('!', 25, 30);
        tester.assertGridPixel('!', 32, 32);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("110", f.getString("FID"));
        Assert.assertEquals("Cam Bridge", f.getString("NAME"));
    }

    @Test
    public void testMultiLayer() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap" + ("&layers=cite:Forests,cite:Lakes,cite:Ponds,cite:DividedRoutes,cite:RoadSegments,cite:Buildings,cite:Streams,cite:Bridges" + "&styles=&bbox=-0.003,-0.003,0.003,0.003&width=256&height=256&srs=EPSG:4326&format=utfgrid")));
        tester.assertGridPixel('!', 6, 4);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("119", f.getString("FID"));
        Assert.assertEquals("Route 75", f.getString("NAME"));
        Assert.assertEquals(0, f.getInt("NUM_LANES"));
        tester.assertGridPixel('#', 6, 27);
        f = tester.getFeature('#');
        Assert.assertEquals("111", f.getString("FID"));
        Assert.assertEquals("Cam Stream", f.getString("NAME"));
        tester.assertGridPixel('%', 10, 12);
        f = tester.getFeature('%');
        Assert.assertEquals("120", f.getString("FID"));
        Assert.assertEquals(" ", f.getString("NAME"));
        Assert.assertEquals("Stock Pond", f.getString("TYPE"));
        tester.assertGridPixel('$', 10, 62);
        f = tester.getFeature('$');
        Assert.assertEquals("103", f.getString("FID"));
        Assert.assertEquals("Route 5", f.getString("NAME"));
        tester.assertGridPixel('(', 22, 56);
        f = tester.getFeature('(');
        Assert.assertEquals("114", f.getString("FID"));
        Assert.assertEquals("215 Main Street", f.getString("ADDRESS"));
        tester.assertGridPixel(')', 24, 33);
        f = tester.getFeature(')');
        Assert.assertEquals("110", f.getString("FID"));
        Assert.assertEquals("Cam Bridge", f.getString("NAME"));
        tester.assertGridPixel('&', 24, 35);
        f = tester.getFeature('&');
        Assert.assertEquals("105", f.getString("FID"));
        Assert.assertEquals("Main Street", f.getString("NAME"));
        tester.assertGridPixel('+', 24, 43);
        f = tester.getFeature('+');
        Assert.assertEquals("113", f.getString("FID"));
        Assert.assertEquals("123 Main Street", f.getString("ADDRESS"));
        tester.assertGridPixel('-', 45, 48);
        f = tester.getFeature('-');
        Assert.assertEquals("101", f.getString("FID"));
        Assert.assertEquals("Blue Lake", f.getString("NAME"));
        tester.assertGridPixel(',', 35, 17);
        f = tester.getFeature(',');
        Assert.assertEquals("106", f.getString("FID"));
        Assert.assertEquals("Dirt Road by Green Forest", f.getString("NAME"));
        tester.assertGridPixel('\'', 38, 25);
        f = tester.getFeature('\'');
        Assert.assertEquals("109", f.getString("FID"));
        Assert.assertEquals("Green Forest", f.getString("NAME"));
        tester.assertGridPixel('*', 32, 9);
        f = tester.getFeature('*');
        Assert.assertEquals("102", f.getString("FID"));
        Assert.assertEquals("Route 5", f.getString("NAME"));
    }

    @Test
    public void testMultiLayerForestOnTop() throws Exception {
        UTFGridTester tester = getAsGridTester(("wms?service=WMS&version=1.1.0&request=GetMap" + ("&layers=cite:Lakes,cite:Ponds,cite:DividedRoutes,cite:RoadSegments,cite:Buildings,cite:Streams,cite:Bridges,cite:Forests" + "&styles=&bbox=-0.003,-0.003,0.003,0.003&width=256&height=256&srs=EPSG:4326&format=utfgrid")));
        tester.assertGridPixel('!', 6, 4);
        JSONObject f = tester.getFeature('!');
        Assert.assertEquals("119", f.getString("FID"));
        Assert.assertEquals("Route 75", f.getString("NAME"));
        Assert.assertEquals(0, f.getInt("NUM_LANES"));
        tester.assertGridPixel('#', 6, 27);
        f = tester.getFeature('#');
        Assert.assertEquals("111", f.getString("FID"));
        Assert.assertEquals("Cam Stream", f.getString("NAME"));
        tester.assertGridPixel('%', 10, 12);
        f = tester.getFeature('%');
        Assert.assertEquals("120", f.getString("FID"));
        Assert.assertEquals(" ", f.getString("NAME"));
        Assert.assertEquals("Stock Pond", f.getString("TYPE"));
        tester.assertGridPixel('$', 10, 62);
        f = tester.getFeature('$');
        Assert.assertEquals("103", f.getString("FID"));
        Assert.assertEquals("Route 5", f.getString("NAME"));
        tester.assertGridPixel('\'', 23, 33);
        f = tester.getFeature('\'');
        Assert.assertEquals("110", f.getString("FID"));
        Assert.assertEquals("Cam Bridge", f.getString("NAME"));
        tester.assertGridPixel('&', 22, 56);
        tester.assertGridPixel('&', 24, 35);
        tester.assertGridPixel('&', 24, 43);
        tester.assertGridPixel('&', 45, 48);
        tester.assertGridPixel('&', 35, 17);
        tester.assertGridPixel('&', 24, 33);
        f = tester.getFeature('&');
        Assert.assertEquals("109", f.getString("FID"));
        Assert.assertEquals("Green Forest", f.getString("NAME"));
        tester.assertGridPixel('(', 32, 9);
        f = tester.getFeature('(');
        Assert.assertEquals("102", f.getString("FID"));
        Assert.assertEquals("Route 5", f.getString("NAME"));
    }

    @Test
    public void testPolygonExtractionFromRaster() throws Exception {
        String url = (((("wms?LAYERS=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=polygonExtract&") + "FORMAT=utfgrid&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=145,-43,146,-41&WIDTH=100&HEIGHT=200";
        UTFGridTester tester = getAsGridTester(url, 100, 200, 4);
        Assert.assertTrue(((tester.getKeyCount()) > 0));
    }
}

