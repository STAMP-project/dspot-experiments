/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs;


import DimensionInfo.DEFAULT_MAX_REQUESTED_DIMENSION_VALUES;
import DimensionPresentation.LIST;
import PixelInCell.CELL_CENTER;
import PixelInCell.CELL_CORNER;
import PreventLocalEntityResolver.ERROR_MESSAGE_BASE;
import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.servlet.ServletResponse;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.MockData;
import org.geoserver.wcs.kvp.Wcs10GetCoverageRequestReader;
import org.geoserver.wcs.test.CoverageTestSupport;
import org.geoserver.wcs.test.WCSTestSupport;
import org.geoserver.wcs.xml.v1_0_0.WcsXmlReader;
import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.processing.operation.MultiplyConst;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.PixelTranslation;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.geotools.wcs.WCSConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Tests for GetCoverage operation on WCS.
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 */
public class GetCoverageTest extends WCSTestSupport {
    private Wcs10GetCoverageRequestReader kvpreader;

    private WebCoverageService100 service;

    private WCSConfiguration configuration;

    private WcsXmlReader xmlReader;

    private Catalog catalog;

    private static final QName MOSAIC = new QName(MockData.SF_URI, "rasterFilter", MockData.SF_PREFIX);

    private static final QName SPATIO_TEMPORAL = new QName(MockData.SF_URI, "spatio-temporal", MockData.SF_PREFIX);

    @Test
    public void testDomainSubsetRxRy() throws Exception {
        // get base  coverage
        final GridCoverage baseCoverage = catalog.getCoverageByName(TASMANIA_BM.getLocalPart()).getGridCoverage(null, null);
        final AffineTransform2D expectedTx = ((AffineTransform2D) (baseCoverage.getGridGeometry().getGridToCRS()));
        final GeneralEnvelope originalEnvelope = ((GeneralEnvelope) (baseCoverage.getEnvelope()));
        final GeneralEnvelope newEnvelope = new GeneralEnvelope(originalEnvelope);
        newEnvelope.setEnvelope(originalEnvelope.getMinimum(0), ((originalEnvelope.getMaximum(1)) - ((originalEnvelope.getSpan(1)) / 2)), ((originalEnvelope.getMinimum(0)) + ((originalEnvelope.getSpan(0)) / 2)), originalEnvelope.getMaximum(1));
        final MathTransform cornerWorldToGrid = PixelTranslation.translate(expectedTx, CELL_CENTER, CELL_CORNER);
        final GeneralGridEnvelope expectedGridEnvelope = new GeneralGridEnvelope(CRS.transform(cornerWorldToGrid.inverse(), newEnvelope), PixelInCell.CELL_CORNER, false);
        final StringBuilder envelopeBuilder = new StringBuilder();
        envelopeBuilder.append(newEnvelope.getMinimum(0)).append(",");
        envelopeBuilder.append(newEnvelope.getMinimum(1)).append(",");
        envelopeBuilder.append(newEnvelope.getMaximum(0)).append(",");
        envelopeBuilder.append(newEnvelope.getMaximum(1));
        Map<String, Object> raw = baseMap();
        final String layerID = getLayerId(TASMANIA_BM);
        raw.put("sourcecoverage", layerID);
        raw.put("version", "1.0.0");
        raw.put("format", "image/geotiff");
        raw.put("BBox", envelopeBuilder.toString());
        raw.put("crs", "EPSG:4326");
        raw.put("resx", Double.toString(expectedTx.getScaleX()));
        raw.put("resy", Double.toString(Math.abs(expectedTx.getScaleY())));
        final GridCoverage[] coverages = executeGetCoverageKvp(raw);
        final GridCoverage2D result = ((GridCoverage2D) (coverages[0]));
        Assert.assertTrue(((coverages.length) == 1));
        final AffineTransform2D tx = ((AffineTransform2D) (result.getGridGeometry().getGridToCRS()));
        Assert.assertEquals("resx", expectedTx.getScaleX(), tx.getScaleX(), 1.0E-6);
        Assert.assertEquals("resx", Math.abs(expectedTx.getScaleY()), Math.abs(tx.getScaleY()), 1.0E-6);
        final GridEnvelope gridEnvelope = result.getGridGeometry().getGridRange();
        Assert.assertEquals("w", 180, gridEnvelope.getSpan(0));
        Assert.assertEquals("h", 180, gridEnvelope.getSpan(1));
        Assert.assertEquals("grid envelope", expectedGridEnvelope, gridEnvelope);
        // dispose
        CoverageCleanerCallback.disposeCoverage(baseCoverage);
        CoverageCleanerCallback.disposeCoverage(coverages[0]);
    }

    @Test
    public void testDeferredLoading() throws Exception {
        Map<String, Object> raw = baseMap();
        final String getLayerId = getLayerId(GetCoverageTest.SPATIO_TEMPORAL);
        raw.put("sourcecoverage", getLayerId);
        raw.put("format", "image/tiff");
        raw.put("BBox", "-90,-180,90,180");
        raw.put("crs", "EPSG:4326");
        raw.put("resx", "0.001");
        raw.put("resy", "0.001");
        GridCoverage[] coverages = executeGetCoverageKvp(raw);
        Assert.assertEquals(1, coverages.length);
        assertDeferredLoading(coverages[0].getRenderedImage());
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
        ServletResponse response = getAsServletResponse((("wcs?sourcecoverage=" + (TASMANIA_BM.getLocalPart())) + queryString));
        Assert.assertTrue(response.getContentType().startsWith("image/tiff"));
        Document dom = getAsDOM((("cdf/wcs?sourcecoverage=" + (TASMANIA_BM.getLocalPart())) + queryString));
        Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testGEOS6540_1() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&format=ArcGrid&crs=EPSG:4326") + "&bbox=0,0,1,1&width=50&height=60";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        Assert.assertEquals("text/plain", response.getContentType());
        String content = response.getContentAsString();
        Assert.assertTrue(content.startsWith((("NCOLS 50" + (System.lineSeparator())) + "NROWS 60")));
        Assert.assertEquals("inline; filename=sf:rasterFilter.asc", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testGEOS6540_2() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&format=ARCGRID&crs=EPSG:4326") + "&bbox=0,0,1,1&width=50&height=60";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        String content = response.getContentAsString();
        Assert.assertEquals("text/plain", response.getContentType());
        Assert.assertTrue(content.startsWith(((("NCOLS 50" + (System.lineSeparator())) + "NROWS 60") + (System.lineSeparator()))));
        Assert.assertEquals("inline; filename=sf:rasterFilter.asc", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testGEOS6540_3() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&format=ARCGRID-GZIP&crs=EPSG:4326") + "&bbox=0,0,1,1&width=50&height=60";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        byte[] content = response.getContentAsByteArray();
        Assert.assertEquals("application/x-gzip", response.getContentType());
        Assert.assertEquals(((byte) (31)), content[0]);
        Assert.assertEquals(((byte) (139)), content[1]);
        Assert.assertEquals(((byte) (8)), content[2]);
        Assert.assertEquals(((byte) (0)), content[3]);
        Assert.assertEquals("inline; filename=sf:rasterFilter.asc.gz", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testGEOS6540_4() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&format=application/x-gzip&crs=EPSG:4326") + "&bbox=0,0,1,1&width=50&height=60";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        byte[] content = response.getContentAsByteArray();
        Assert.assertEquals("application/x-gzip", response.getContentType());
        Assert.assertEquals(((byte) (31)), content[0]);
        Assert.assertEquals(((byte) (139)), content[1]);
        Assert.assertEquals(((byte) (8)), content[2]);
        Assert.assertEquals(((byte) (0)), content[3]);
        Assert.assertEquals("inline; filename=sf:rasterFilter.asc.gz", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testNonExistentCoverage() throws Exception {
        String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
        Document dom = getAsDOM(("wcs?sourcecoverage=NotThere" + queryString));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("InvalidParameterValue", "/ServiceExceptionReport/ServiceException/@code", dom);
        XMLAssert.assertXpathEvaluatesTo("sourcecoverage", "/ServiceExceptionReport/ServiceException/@locator", dom);
    }

    @Test
    public void testRequestDisabledResource() throws Exception {
        Catalog catalog = getCatalog();
        ResourceInfo tazbm = catalog.getResourceByName(getLayerId(MockData.TASMANIA_BM), ResourceInfo.class);
        try {
            tazbm.setEnabled(false);
            catalog.save(tazbm);
            String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
            Document dom = getAsDOM((("wcs?sourcecoverage=" + (TASMANIA_BM.getLocalPart())) + queryString));
            // print(dom);
            Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
        } finally {
            tazbm.setEnabled(true);
            catalog.save(tazbm);
        }
    }

    @Test
    public void testLayerQualified() throws Exception {
        String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
        MockHttpServletResponse response = getAsServletResponse(("wcs/BlueMarble/wcs?sourcecoverage=BlueMarble" + queryString));
        Assert.assertTrue(response.getContentType().startsWith("image/tiff"));
        String disposition = response.getHeader("Content-Disposition");
        Assert.assertTrue(disposition.endsWith("BlueMarble.tif"));
        Document dom = getAsDOM(("wcs/DEM/wcs?sourcecoverage=BlueMarble" + queryString));
        Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testInputLimits() throws Exception {
        try {
            // ridicolous limit, just one byte
            setInputLimit(1);
            String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
            Document dom = getAsDOM((("wcs/BlueMarble/wcs?sourcecoverage=" + (getLayerId(TASMANIA_BM))) + queryString));
            // print(dom);
            // check it's an error, check we're getting it because of the input limits
            Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
            String error = WCSTestSupport.xpath.evaluate("/ServiceExceptionReport/ServiceException/text()", dom).trim();
            Assert.assertTrue(error.matches(".*read too much data.*"));
        } finally {
            setInputLimit(0);
        }
    }

    @Test
    public void testOutputLimits() throws Exception {
        try {
            // ridicolous limit, just one byte
            setOutputLimit(1);
            String queryString = "&request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff&bbox=146,-45,147,-42" + "&crs=EPSG:4326&width=150&height=150";
            Document dom = getAsDOM((("wcs/BlueMarble/wcs?sourcecoverage=" + (getLayerId(TASMANIA_BM))) + queryString));
            // print(dom);
            // check it's an error, check we're getting it because of the output limits
            Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
            String error = WCSTestSupport.xpath.evaluate("/ServiceExceptionReport/ServiceException/text()", dom).trim();
            Assert.assertTrue(error.matches(".*generate too much data.*"));
        } finally {
            setOutputLimit(0);
        }
    }

    @Test
    public void testReproject() throws Exception {
        String xml = ((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((("<GetCoverage version=\"1.0.0\" service=\"WCS\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ") + "xmlns=\"http://www.opengis.net/wcs\" ") + "xmlns:ows=\"http://www.opengis.net/ows/1.1\" ") + "xmlns:gml=\"http://www.opengis.net/gml\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xsi:schemaLocation=\"http://www.opengis.net/wcs http://schemas.opengis.net/wcs/1.0.0/getCoverage.xsd\">\n") + "  <sourceCoverage>")) + (getLayerId(TASMANIA_BM))) + "</sourceCoverage>\n") + "  <domainSubset>\n") + "    <spatialSubset>\n") + "      <gml:Envelope srsName=\"EPSG:4326\">\n") + "        <gml:pos>146 -45</gml:pos>\n") + "        <gml:pos>147 42</gml:pos>\n") + "      </gml:Envelope>\n") + "      <gml:Grid dimension=\"2\">\n") + "        <gml:limits>\n") + "          <gml:GridEnvelope>\n") + "            <gml:low>0 0</gml:low>\n") + "            <gml:high>150 150</gml:high>\n") + "          </gml:GridEnvelope>\n") + "        </gml:limits>\n") + "        <gml:axisName>x</gml:axisName>\n") + "        <gml:axisName>y</gml:axisName>\n") + "      </gml:Grid>\n") + "    </spatialSubset>\n") + "  </domainSubset>\n") + "  <output>\n") + "    <crs>EPSG:3857</crs>\n") + "    <format>image/geotiff</format>\n") + "  </output>\n") + "</GetCoverage>";
        MockHttpServletResponse response = postAsServletResponse("wcs", xml);
        Assert.assertEquals("image/tiff", response.getContentType());
        GeoTiffFormat format = new GeoTiffFormat();
        GridCoverage2DReader reader = format.getReader(getBinaryInputStream(response));
        Assert.assertEquals(CRS.decode("EPSG:3857"), reader.getOriginalEnvelope().getCoordinateReferenceSystem());
    }

    @Test
    public void testEntityExpansion() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((("<!DOCTYPE GetCoverage [<!ELEMENT GetCoverage (sourceCoverage) >\n" + "  <!ATTLIST GetCoverage\n") + "            service CDATA #FIXED \"WCS\"\n") + "            version CDATA #FIXED \"1.0.0\"\n") + "            xmlns CDATA #FIXED \"http://www.opengis.net/wcs\">\n") + "  <!ELEMENT sourceCoverage (#PCDATA) >\n") + "  <!ENTITY xxe SYSTEM \"FILE:///file/not/there?.XSD\" >]>\n") + "<GetCoverage version=\"1.0.0\" service=\"WCS\"") + " xmlns=\"http://www.opengis.net/wcs\" >\n") + "  <sourceCoverage>&xxe;</sourceCoverage>\n") + "</GetCoverage>");
        Document dom = postAsDOM("wcs", xml);
        String error = WCSTestSupport.xpath.evaluate("//ServiceException", dom);
        Assert.assertTrue(error.contains(ERROR_MESSAGE_BASE));
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((("<!DOCTYPE GetCoverage [<!ELEMENT GetCoverage (sourceCoverage) >\n" + "  <!ATTLIST GetCoverage\n") + "            service CDATA #FIXED \"WCS\"\n") + "            version CDATA #FIXED \"1.0.0\"\n") + "            xmlns CDATA #FIXED \"http://www.opengis.net/wcs\">\n") + "  <!ELEMENT sourceCoverage (#PCDATA) >\n") + "  <!ENTITY xxe SYSTEM \"jar:file:///file/not/there?.XSD\" >]>\n") + "<GetCoverage version=\"1.0.0\" service=\"WCS\"") + " xmlns=\"http://www.opengis.net/wcs\" >\n") + "  <sourceCoverage>&xxe;</sourceCoverage>\n") + "</GetCoverage>");
        dom = postAsDOM("wcs", xml);
        error = WCSTestSupport.xpath.evaluate("//ServiceException", dom);
        Assert.assertTrue(error.contains(ERROR_MESSAGE_BASE));
    }

    @Test
    public void testRasterFilterGreen() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&&format=image/tiff&crs=EPSG:4326") + "&bbox=0,0,1,1&CQL_FILTER=location like 'green%25'&width=150&height=150";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        // make sure we can read the coverage back
        RenderedImage image = readTiff(response);
        // check the pixel
        int[] pixel = new int[3];
        image.getData().getPixel(0, 0, pixel);
        Assert.assertEquals(0, pixel[0]);
        Assert.assertEquals(255, pixel[1]);
        Assert.assertEquals(0, pixel[2]);
    }

    @Test
    public void testTimeFirstPOST() throws Exception {
        String request = getWaterTempTimeRequest("2008-10-31T00:00:00.000Z");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        /* gdallocationinfo NCOM_wattemp_000_20081031T0000000_12.tiff 10 10
        Report:
        Location: (10P,10L)
        Band 1:
        Value: 18.2659999176394
         */
        checkPixelValue(response, 10, 10, 18.2659999176394);
    }

    @Test
    public void testTimeFirstKVP() throws Exception {
        String queryString = ("request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff" + ("&bbox=0.237,40.562,14.593,44.558&crs=EPSG:4326&width=25&height=25&time=2008-10-31T00:00:00.000Z" + "&coverage=")) + (getLayerId(CoverageTestSupport.WATTEMP));
        MockHttpServletResponse response = getAsServletResponse(("wcs?" + queryString));
        checkPixelValue(response, 10, 10, 18.2659999176394);
    }

    @Test
    public void testTimeTooMany() throws Exception {
        GeoServer gs = getGeoServer();
        WCSInfo wcs = gs.getService(WCSInfo.class);
        wcs.setMaxRequestedDimensionValues(2);
        gs.save(wcs);
        try {
            String queryString = ("request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff" + ("&bbox=0.237,40.562,14.593,44.558&crs=EPSG:4326&width=25&height=25&time=2008-10-31/2008-11-31/PT1H" + "&coverage=")) + (getLayerId(CoverageTestSupport.WATTEMP));
            MockHttpServletResponse response = getAsServletResponse(("wcs?" + queryString));
            Assert.assertEquals("application/vnd.ogc.se_xml", response.getContentType());
            Document dom = dom(response, true);
            // print(dom);
            String text = checkLegacyException(dom, ServiceException.INVALID_PARAMETER_VALUE, "time");
            Assert.assertThat(text, CoreMatchers.containsString("More than 2 times"));
        } finally {
            wcs.setMaxRequestedDimensionValues(DEFAULT_MAX_REQUESTED_DIMENSION_VALUES);
            gs.save(wcs);
        }
    }

    @Test
    public void testTimeRangeKVP() throws Exception {
        setupRasterDimension(CoverageTestSupport.TIMERANGES, TIME, LIST, null);
        setupRasterDimension(CoverageTestSupport.TIMERANGES, ELEVATION, LIST, null);
        String baseUrl = ("wcs?request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff" + ("&bbox=0.237,40.562,14.593,44.558&crs=EPSG:4326&width=25&height=25" + "&coverage=")) + (getLayerId(CoverageTestSupport.TIMERANGES));
        // last range
        MockHttpServletResponse response = getAsServletResponse((baseUrl + "&TIME=2008-11-05T00:00:00.000Z/2008-11-06T12:00:00.000Z"));
        Assert.assertEquals("image/tiff", response.getContentType());
        checkPixelValue(response, 10, 10, 13.337999683572);
        // middle hole, no data --> we should get back an exception
        Document dom = getAsDOM((baseUrl + "&TIME=2008-11-04T12:00:00.000Z/2008-11-04T16:00:00.000Z"));
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ServiceExceptionReport)", dom);
        // first range
        response = getAsServletResponse((baseUrl + "&TIME=2008-10-31T12:00:00.000Z/2008-10-31T16:00:00.000Z"));
        Assert.assertEquals("image/tiff", response.getContentType());
        checkPixelValue(response, 10, 10, 18.2659999176394);
    }

    @Test
    public void testTimeSecond() throws Exception {
        String request = getWaterTempTimeRequest("2008-11-01T00:00:00.000Z");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        checkPixelValue(response, 10, 10, 18.2849999185419);
    }

    @Test
    public void testTimeKVPNow() throws Exception {
        String queryString = ("request=getcoverage&service=wcs&version=1.0.0&format=image/geotiff" + ("&bbox=0.237,40.562,14.593,44.558&crs=EPSG:4326&width=25&height=25&time=now" + "&coverage=")) + (getLayerId(CoverageTestSupport.WATTEMP));
        MockHttpServletResponse response = getAsServletResponse(("wcs?" + queryString));
        checkPixelValue(response, 10, 10, 18.2849999185419);
    }

    @Test
    public void testElevationFirst() throws Exception {
        String request = getWaterTempElevationRequest("0.0");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        Assert.assertEquals("image/tiff", response.getContentType());
        // same result as time first
        checkPixelValue(response, 10, 10, 18.2849999185419);
        request = request.replace("ELEVATION", "elevation");
        response = postAsServletResponse("wcs", request);
        Assert.assertEquals("image/tiff", response.getContentType());
        checkPixelValue(response, 10, 10, 18.2849999185419);
    }

    @Test
    public void testElevationSecond() throws Exception {
        String request = getWaterTempElevationRequest("100.0");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        Assert.assertEquals("image/tiff", response.getContentType());
        /* gdallocationinfo NCOM_wattemp_100_20081101T0000000_12.tiff  10 10
        Report:
        Location: (10P,10L)
        Band 1:
        Value: 13.337999683572
         */
        checkPixelValue(response, 10, 10, 13.337999683572);
        request = request.replace("ELEVATION", "elevation");
        response = postAsServletResponse("wcs", request);
        Assert.assertEquals("image/tiff", response.getContentType());
        checkPixelValue(response, 10, 10, 13.337999683572);
    }

    @Test
    public void testRasterFilterRed() throws Exception {
        String queryString = ((("wcs?sourcecoverage=" + (getLayerId(GetCoverageTest.MOSAIC))) + "&request=getcoverage") + "&service=wcs&version=1.0.0&format=image/tiff&crs=EPSG:4326") + "&bbox=0,0,1,1&CQL_FILTER=location like 'red%25'&width=150&height=150";
        MockHttpServletResponse response = getAsServletResponse(queryString);
        RenderedImage image = readTiff(response);
        // check the pixel
        int[] pixel = new int[3];
        image.getData().getPixel(0, 0, pixel);
        Assert.assertEquals(255, pixel[0]);
        Assert.assertEquals(0, pixel[1]);
        Assert.assertEquals(0, pixel[2]);
    }

    @Test
    public void testCategoriesToArray() throws Exception {
        CoverageInfo myCoverage = getCatalog().getCoverageByName("category");
        GridCoverage gridCoverage = myCoverage.getGridCoverage(null, null);
        MultiplyConst op = new MultiplyConst();
        final ParameterValueGroup param = op.getParameters();
        param.parameter("Source").setValue(gridCoverage);
        param.parameter("constants").setValue(new double[]{ 0.1 });
        boolean exception = false;
        try {
            op.doOperation(param, null);
        } catch (Exception e) {
            exception = true;
        }
        Assert.assertFalse(exception);
    }
}

