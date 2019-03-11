/**
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test the DXFOutputFormat WFS extension.
 *
 * @author Mauro Bartolomeoli, mbarto@infosia.it
 */
public class DXFOutputFormatTest extends WFSTestSupport {
    /**
     * Test a request with two queries.
     */
    @Test
    public void testMultiLayer() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points,MPoints&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "Points_MPoints");
        checkSequence(sResponse, new String[]{ "LAYER", "LAYER", "LAYER", "POINTS", "LAYER", "MPOINTS" });
    }

    /**
     * Test DXF-ZIP format.
     */
    @Test
    public void testZipOutput() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points&outputFormat=dxf-zip");
        // check mime type
        Assert.assertEquals("application/zip", resp.getContentType());
    }

    /**
     * Test a Point geometry.
     */
    @Test
    public void testPoints() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "Points");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        checkSequence(sResponse, new String[]{ "POINT" }, pos);
    }

    /**
     * Test a MultiPoint geometry.
     */
    @Test
    public void testMultiPoints() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=MPoints&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "MPoints");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert two points
        checkSequence(sResponse, new String[]{ "POINT", "POINT" }, pos);
    }

    /**
     * Test a LineString geometry.
     */
    @Test
    public void testLines() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Lines&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "Lines");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        checkSequence(sResponse, new String[]{ "LWPOLYLINE" }, pos);
    }

    /**
     * Test a MultiLineString geometry.
     */
    @Test
    public void testMultiLines() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=MLines&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "MLines");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert two lwpolyline
        checkSequence(sResponse, new String[]{ "LWPOLYLINE", "LWPOLYLINE" }, pos);
    }

    /**
     * Test a Polygon geometry.
     */
    @Test
    public void testPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "Polygons");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert an lwpolyline
        checkSequence(sResponse, new String[]{ "LWPOLYLINE" }, pos);
    }

    /**
     * Test writeattributes option.
     */
    @Test
    public void testWriteAttributes() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf&format_options=withattributes:true");
        String sResponse = testBasicResult(resp, "Polygons");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert an attribute
        checkSequence(sResponse, new String[]{ "ATTRIB", "AcDbAttribute" }, pos);
    }

    /**
     * Test writeattributes option, check position of attributes.
     */
    @Test
    public void testWriteAttributesPosition() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf&format_options=withattributes:true");
        String sResponse = testBasicResult(resp, "Polygons");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert an attribute
        checkSequence(sResponse, new String[]{ "POLYGONS_attributes", "POLYGONS_attributes", "POLYGONS_attributes", "ATTRIB", "POLYGONS_attributes", "AcDbText", "10", "500237.5", "20", "500062.5", "t0002" }, pos);
    }

    /**
     * Test a MultiPolygon geometry.
     */
    @Test
    public void testMultiPolygons() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=MPolygons&outputFormat=dxf");
        String sResponse = testBasicResult(resp, "MPolygons");
        int pos = getGeometrySearchStart(sResponse);
        Assert.assertTrue((pos != (-1)));
        // has to insert two lwpolyline
        checkSequence(sResponse, new String[]{ "LWPOLYLINE", "LWPOLYLINE" }, pos);
    }

    /**
     * Bounding Box excludes all features. Envelope is empty Envelop.expandBy(1) is still empty.
     * Division by zero. The result is an invalid character in DXF, entire file is invalid.
     */
    @Test
    public void testEmptyBbox() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=MPolygons&outputFormat=dxf&bbox=929636,6013554.5,930744,6014601.5&srsName=EPSG:900913");
        String sResponse = testBasicResult(resp, "MPolygons");
        // System.out.println(sResponse);
        for (int i = 0; i < (sResponse.length()); i++) {
            char c = sResponse.charAt(i);
            Assert.assertTrue((("Invalid non-ASCII char: '" + c) + "'"), (c < 128));
        }
    }

    /**
     * Test maxFeatures=0: No collection passed to writer, no geom. Result was: NPE Maybe exotic,
     * but should not end in NPE.
     */
    @Test
    public void testEmptyCount() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=MPolygons&outputFormat=dxf&maxFeatures=0");
        testBasicResult(resp, "MPolygons");
    }

    /**
     * Test format option asblocks.
     */
    @Test
    public void testGeometryAsBlock() {
        try {
            // geometry as blocks false
            MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf");
            String sResponse = resp.getContentAsString();
            Assert.assertNotNull(sResponse);
            // no insert block generated
            Assert.assertFalse(((sResponse.indexOf("INSERT")) != (-1)));
            // geometry as blocks true
            resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf&format_options=asblocks:true");
            sResponse = resp.getContentAsString();
            Assert.assertNotNull(sResponse);
            // one insert block generated
            Assert.assertTrue(((sResponse.indexOf("INSERT")) != (-1)));
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }
    }

    /**
     * Test format option version support.
     */
    @Test
    public void testVersion() throws Exception {
        try {
            // good request, version 14
            MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf&format_options=version:14");
            String sResponse = resp.getContentAsString();
            Assert.assertNotNull(sResponse);
            Assert.assertTrue(sResponse.startsWith("  0"));
            // bad request, version 13: not supported
            resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Polygons&outputFormat=dxf&format_options=version:13");
            sResponse = resp.getContentAsString();
            Assert.assertNotNull(sResponse);
            // has to return an exception
            Assert.assertTrue(((sResponse.indexOf("</ows:ExceptionReport>")) != (-1)));
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }
    }

    /**
     * Test the ltypes format option.
     */
    @Test
    public void testCustomLineTypes() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Lines&outputFormat=dxf&format_options=ltypes:DASHED!--_*_!0.5");
        String sResponse = testBasicResult(resp, "Lines");
        checkSequence(sResponse, new String[]{ "DASHED" });
    }

    /**
     * Test the colors format option.
     */
    @Test
    public void testCustomColors() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points,MPoints&outputFormat=dxf&format_options=colors:1,2");
        String sResponse = testBasicResult(resp, "Points_MPoints");
        checkSequence(sResponse, new String[]{ "LAYER", "LAYER", "LAYER", " 62\n     1", "LAYER", " 62\n     2" });
    }

    /**
     * Test custom naming for layers.
     */
    @Test
    public void testLayerNames() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points,MPoints&outputFormat=dxf&format_options=layers:MyLayer1,MyLayer2");
        String sResponse = testBasicResult(resp, "Points_MPoints");
        checkSequence(sResponse, new String[]{ "LAYER", "LAYER", "LAYER", "MYLAYER1", "LAYER", "MYLAYER2" });
    }

    /**
     * Test fix for GEOS-6402.
     */
    @Test
    public void testLayerNamesParsing() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points,MPoints&outputFormat=dxf&format_options=layers:MyLayer1,MyLayer2");
        String sResponse = testBasicResult(resp, "Points_MPoints");
        checkSequence(sResponse, new String[]{ "LAYER", "LAYER", "LAYER", "MYLAYER1", "LAYER", "MYLAYER2" });
        // now repeat the test parsing layers format_options as a list, instead of a string
        LayersKvpParser.parseAsList = true;
        resp = getAsServletResponse("wfs?request=GetFeature&version=1.1.0&typeName=Points,MPoints&outputFormat=dxf&format_options=layers:MyLayer1,MyLayer2");
        sResponse = testBasicResult(resp, "Points_MPoints");
        checkSequence(sResponse, new String[]{ "LAYER", "LAYER", "LAYER", "MYLAYER1", "LAYER", "MYLAYER2" });
    }
}

