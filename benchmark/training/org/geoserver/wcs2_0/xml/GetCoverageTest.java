/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.xml;


import DefaultGeographicCRS.WGS84;
import DimensionPresentation.LIST;
import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import WCS20Exception.WCS20ExceptionCode.InvalidAxisLabel;
import Wcs20Factory.eINSTANCE;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import net.opengis.wcs20.GetCoverageType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.wcs2_0.DefaultWebCoverageService20;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Testing WCS 2.0 Core {@link GetCoverage}
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 * @author Emanuele Tajariol, GeoSolutions SAS
 */
public class GetCoverageTest extends WCSTestSupport {
    protected static QName WATTEMP = new QName(MockData.SF_URI, "watertemp", MockData.SF_PREFIX);

    protected static QName WATTEMP_DILATED = new QName(MockData.SF_URI, "watertemp_dilated", MockData.SF_PREFIX);

    protected static QName TIMERANGES = new QName(MockData.SF_URI, "timeranges", MockData.SF_PREFIX);

    protected static QName CUSTOMDIMS = new QName(MockData.SF_URI, "customdimensions", MockData.SF_PREFIX);

    private static final QName RAIN = new QName(MockData.SF_URI, "rain", MockData.SF_PREFIX);

    private static final QName BORDERS = new QName(MockData.SF_URI, "borders", MockData.SF_PREFIX);

    private static final QName SPATIO_TEMPORAL = new QName(MockData.SF_URI, "spatio-temporal", MockData.SF_PREFIX);

    /**
     * Trimming only on Longitude
     */
    @Test
    public void testCoverageTrimmingLatitudeNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingLatitudeNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        checkCoverageTrimmingLatitudeNativeCRS(tiffContents);
    }

    /**
     * Trimming only on Longitude, plus multipart encoding
     */
    @Test
    public void testCoverageTrimmingLatitudeNativeCRSXMLMultipart() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageTrimmingLatitudeNativeCRSXMLMultipart.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("multipart/related", response.getContentType());
        // parse the multipart, check there are two parts
        Multipart multipart = getMultipart(response);
        TestCase.assertEquals(2, multipart.getCount());
        BodyPart xmlPart = multipart.getBodyPart(0);
        TestCase.assertEquals("application/gml+xml", xmlPart.getHeader("Content-Type")[0]);
        TestCase.assertEquals("wcs", xmlPart.getHeader("Content-ID")[0]);
        Document gml = dom(xmlPart.getInputStream());
        // print(gml);
        // check the gml part refers to the file as its range
        XMLAssert.assertXpathEvaluatesTo("fileReference", "//gml:rangeSet/gml:File/gml:rangeParameters/@xlink:arcrole", gml);
        XMLAssert.assertXpathEvaluatesTo("cid:/coverages/wcs__BlueMarble.tif", "//gml:rangeSet/gml:File/gml:rangeParameters/@xlink:href", gml);
        XMLAssert.assertXpathEvaluatesTo("http://www.opengis.net/spec/GMLCOV_geotiff-coverages/1.0/conf/geotiff-coverage", "//gml:rangeSet/gml:File/gml:rangeParameters/@xlink:role", gml);
        XMLAssert.assertXpathEvaluatesTo("cid:/coverages/wcs__BlueMarble.tif", "//gml:rangeSet/gml:File/gml:fileReference", gml);
        XMLAssert.assertXpathEvaluatesTo("image/tiff", "//gml:rangeSet/gml:File/gml:mimeType", gml);
        BodyPart coveragePart = multipart.getBodyPart(1);
        TestCase.assertEquals("/coverages/wcs__BlueMarble.tif", coveragePart.getHeader("Content-ID")[0]);
        TestCase.assertEquals("image/tiff", coveragePart.getContentType());
        // make sure we can read the coverage back and perform checks on it
        byte[] tiffContents = IOUtils.toByteArray(coveragePart.getInputStream());
        checkCoverageTrimmingLatitudeNativeCRS(tiffContents);
    }

    @Test
    public void testCoverageTrimmingNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // checks
            final GridEnvelope gridRange = targetCoverage.getGridGeometry().getGridRange();
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 146.5, -43.5 }, new double[]{ 147.0, -43.0 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            final double scale = WCSTestSupport.getScale(targetCoverage);
            WCSTestSupport.assertEnvelopeEquals(expectedEnvelope, scale, ((GeneralEnvelope) (targetCoverage.getEnvelope())), scale);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(targetCoverage.getCoordinateReferenceSystem(), expectedEnvelope.getCoordinateReferenceSystem()));
            TestCase.assertEquals(gridRange.getSpan(0), 120);
            TestCase.assertEquals(gridRange.getSpan(1), 120);
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testCoverageTrimmingBorders() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingBorders.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        // make sure we are not getting a service exception
        TestCase.assertEquals("image/tiff", response.getContentType());
    }

    @Test
    public void testCoverageTrimmingOutsideBorders() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingOutsideBorders.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        // make sure we are not getting a service exception
        checkOws20Exception(response, 404, "InvalidSubsetting", null);
    }

    @Test
    public void testGetFullCoverageXML() throws Exception {
        final File xml = new File("./src/test/resources/requestGetFullCoverage.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        // check the headers
        TestCase.assertEquals("image/tiff", response.getContentType());
        String contentDisposition = response.getHeader("Content-disposition");
        TestCase.assertEquals("inline; filename=wcs__BlueMarble.tif", contentDisposition);
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        // check we can read it as a TIFF and it is similare to the origina one
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        GridCoverage2D sourceCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            sourceCoverage = ((GridCoverage2D) (getCatalog().getCoverageByName("BlueMarble").getGridCoverageReader(null, null).read(null)));
            // checks
            TestCase.assertEquals(sourceCoverage.getGridGeometry().getGridRange(), targetCoverage.getGridGeometry().getGridRange());
            TestCase.assertEquals(sourceCoverage.getCoordinateReferenceSystem(), targetCoverage.getCoordinateReferenceSystem());
            TestCase.assertEquals(sourceCoverage.getEnvelope(), targetCoverage.getEnvelope());
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(sourceCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testInputLimits() throws Exception {
        final File xml = new File("./src/test/resources/requestGetFullCoverage.xml");
        final String request = FileUtils.readFileToString(xml);
        // set limits
        setInputLimit(1);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        // System.out.println(new String(this.getBinary(response)));
        TestCase.assertEquals("application/xml", response.getContentType());
        // reset imits
        setInputLimit((-1));
    }

    @Test
    public void testOutputLimits() throws Exception {
        final File xml = new File("./src/test/resources/requestGetFullCoverage.xml");
        final String request = FileUtils.readFileToString(xml);// set limits

        // set output limits
        setOutputLimit(1);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("application/xml", response.getContentType());
        // reset imits
        setOutputLimit((-1));
    }

    /**
     * Trimming only on Longitude
     */
    @Test
    public void testCoverageTrimmingLongitudeNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingLongNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // checks
            final GridEnvelope gridRange = targetCoverage.getGridGeometry().getGridRange();
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 146.5, targetCoverage.getEnvelope().getMinimum(1) }, new double[]{ 147.0, targetCoverage.getEnvelope().getMaximum(1) });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            final double scale = WCSTestSupport.getScale(targetCoverage);
            WCSTestSupport.assertEnvelopeEquals(expectedEnvelope, scale, ((GeneralEnvelope) (targetCoverage.getEnvelope())), scale);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(targetCoverage.getCoordinateReferenceSystem(), expectedEnvelope.getCoordinateReferenceSystem()));
            TestCase.assertEquals(gridRange.getSpan(0), 120);
            TestCase.assertEquals(gridRange.getSpan(1), 360);
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testCoverageTrimmingSlicingNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingSlicingNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // checks
            final GridEnvelope gridRange = targetCoverage.getGridGeometry().getGridRange();
            // 1 dimensional slice along latitude
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 146.49999999999477, -43.5 }, new double[]{ 146.99999999999477, -43.49583333333119 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            final double scale = WCSTestSupport.getScale(targetCoverage);
            WCSTestSupport.assertEnvelopeEquals(expectedEnvelope, scale, ((GeneralEnvelope) (targetCoverage.getEnvelope())), scale);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(targetCoverage.getCoordinateReferenceSystem(), expectedEnvelope.getCoordinateReferenceSystem()));
            TestCase.assertEquals(gridRange.getSpan(1), 1);
            TestCase.assertEquals(gridRange.getSpan(0), 120);
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testCoverageTrimmingDuplicatedNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingDuplicatedNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("application/xml", response.getContentType());
        // checkOws20Exception(response, 404, "InvalidAxisLabel", "coverageId");
    }

    @Test
    public void testCoverageTrimmingBordersOverlap() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingBordersOverlap.xml");
        testCoverageResult(xml, ( targetCoverage) -> {
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 7, 40 }, new double[]{ 11, 43 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            double pixelSize = 0.057934032977228;
            // check the whole extent has been returned
            TestCase.assertTrue(expectedEnvelope.equals(targetCoverage.getEnvelope(), pixelSize, false));
        });
    }

    @Test
    public void testCoverageTrimmingBordersOverlapVertical() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingBordersOverlapVertical.xml");
        testCoverageResult(xml, ( targetCoverage) -> {
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 13, 37 }, new double[]{ 14, 39 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            double pixelSize = 0.057934032977228;
            // check the whole extent has been returned
            TestCase.assertTrue(expectedEnvelope.equals(targetCoverage.getEnvelope(), pixelSize, false));
        });
    }

    @Test
    public void testCoverageTrimmingBordersOverlapOutside() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTrimmingBordersOverlapOutside.xml");
        testCoverageResult(xml, ( targetCoverage) -> {
            // the expected envelope is the intersection between the requested and native
            // one
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 6.344, 40 }, new double[]{ 11, 46.59 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            double pixelSize = 0.057934032977228;
            // check the whole extent has been returned
            TestCase.assertTrue(expectedEnvelope.equals(targetCoverage.getEnvelope(), pixelSize, false));
        });
    }

    @FunctionalInterface
    public interface GridTester {
        void test(GridCoverage2D coverage) throws Exception;
    }

    @Test
    public void testCoverageSlicingLongitudeNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageSlicingLongitudeNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // checks
            final GridEnvelope gridRange = targetCoverage.getGridGeometry().getGridRange();
            // 1 dimensional slice along longitude
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 146.5, -44.49999999999784 }, new double[]{ 146.50416666666143, -42.99999999999787 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            final double scale = WCSTestSupport.getScale(targetCoverage);
            WCSTestSupport.assertEnvelopeEquals(expectedEnvelope, scale, ((GeneralEnvelope) (targetCoverage.getEnvelope())), scale);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(targetCoverage.getCoordinateReferenceSystem(), expectedEnvelope.getCoordinateReferenceSystem()));
            TestCase.assertEquals(gridRange.getSpan(0), 1);
            TestCase.assertEquals(gridRange.getSpan(1), 360);
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testCoverageSlicingLatitudeNativeCRSXML() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageSlicingLatitudeNativeCRSXML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("bm_gtiff", "bm_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // checks
            final GridEnvelope gridRange = targetCoverage.getGridGeometry().getGridRange();
            // 1 dimensional slice along latitude
            final GeneralEnvelope expectedEnvelope = new GeneralEnvelope(new double[]{ 146.49999999999477, -43.499999999997854 }, new double[]{ 147.99999999999474, -43.49583333333119 });
            expectedEnvelope.setCoordinateReferenceSystem(CRS.decode("EPSG:4326", true));
            final double scale = WCSTestSupport.getScale(targetCoverage);
            WCSTestSupport.assertEnvelopeEquals(expectedEnvelope, scale, ((GeneralEnvelope) (targetCoverage.getEnvelope())), scale);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(targetCoverage.getCoordinateReferenceSystem(), expectedEnvelope.getCoordinateReferenceSystem()));
            TestCase.assertEquals(gridRange.getSpan(1), 1);
            TestCase.assertEquals(gridRange.getSpan(0), 360);
        } finally {
            try {
                readerTarget.dispose();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                scheduleForCleaning(targetCoverage);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Test
    public void testCoverageTimeSlicingNoTimeConfigured() throws Exception {
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2000-10-31T00:00:00.000Z");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        checkOws20Exception(response, 404, InvalidAxisLabel.getExceptionCode(), null);
    }

    @Test
    public void testCoverageTimeSlicingTimeBefore() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP), TIME, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2000-10-31T00:00:00.000Z");
        // nearest neighbor match, lowest time returned
        checkWaterTempValue(request, 14.897999757668003);
    }

    @Test
    public void testCoverageTimeSlicingTimeFirst() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP), TIME, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2008-10-31T00:00:00.000Z");
        checkWaterTempValue(request, 14.897999757668003);
    }

    @Test
    public void testCoverageTimeSlicingTimeClosest() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP), TIME, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2000-10-31T11:30:00.000Z");
        // nearest neighbor match, lowest time returned
        checkWaterTempValue(request, 14.897999757668003);
    }

    @Test
    public void testCoverageTimeSlicingTimeSecond() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP), TIME, LIST, null);
        // System.out.println(getDataDirectory().root());
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2008-11-01T00:00:00.000Z");
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageTimeSlicingTimeAfter() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP), TIME, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp");
        request = request.replace("${slicePoint}", "2011-11-01T00:00:00.000Z");
        // nearest neighbor match, highest time returned
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageTimeSlicingAgainstFirstRange() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), TIME, LIST, null);
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), ELEVATION, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePoint}", "2008-10-31T00:00:00.000Z");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 18.478999927756377);
    }

    @Test
    public void testCoverageTimeSlicingAgainstRangeHole() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), TIME, LIST, null);
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), ELEVATION, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePoint}", "2008-11-04T11:00:00.000Z");
        // timeranges is really just an expanded watertemp, and we expect NN
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageTimeSlicingAgainstSecondRange() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), TIME, LIST, null);
        setupRasterDimension(getLayerId(GetCoverageTest.TIMERANGES), ELEVATION, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePoint}", "2008-11-06T00:00:00.000Z");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageTimeElevationSlicingAgainstLowestOldestGranule() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeElevationCustomSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePointElevation}", "20");
        request = request.replace("${slicePointTime}", "2008-10-31T00:00:00.000Z");
        request = request.replace("${Custom}", "WAVELENGTH");
        request = request.replace("${slicePointCustom}", "20");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 18.478999927756377);
        request = request.replace("WAVELENGTH", "wavelength");
        checkWaterTempValue(request, 18.478999927756377);
    }

    @Test
    public void testCoverageTimeElevationSlicingAgainstHighestNewestGranuleLatestWavelength() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeElevationCustomSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePointElevation}", "140");
        request = request.replace("${slicePointTime}", "2008-11-07T00:00:00.000Z");
        request = request.replace("${Custom}", "WAVELENGTH");
        request = request.replace("${slicePointCustom}", "80");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 14.529999740188941);
        request = request.replace("WAVELENGTH", "wavelength");
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageMultipleCustomSubsets() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.CUSTOMDIMS, TIME, ELEVATION, "WAVELENGTH");
        setupRasterDimension(getLayerId(GetCoverageTest.CUSTOMDIMS), ((ResourceInfo.CUSTOM_DIMENSION_PREFIX) + "CUSTOM"), LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageMultipleCustomSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__customdimensions");
        request = request.replace("${slicePointElevation}", "140");
        request = request.replace("${slicePointTime}", "2008-11-07T00:00:00.000Z");
        request = request.replace("${CustomOne}", "WAVELENGTH");
        request = request.replace("${slicePointCustomOne}", "80");
        request = request.replace("${CustomTwo}", "CUSTOM");
        request = request.replace("${slicePointCustomTwo}", "99");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 14.529999740188941);
        request = request.replace("WAVELENGTH", "wavelength").replace("CUSTOM", "custom");
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageTimeElevationSlicingAgainstHighestNewestGranule() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeElevationSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePointElevation}", "140");
        request = request.replace("${slicePointTime}", "2008-11-07T00:00:00.000Z");
        // timeranges is really just an expanded watertemp
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testCoverageElevationSlicingDefaultTime() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageElevationSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePoint}", "140");
        // check we get a proper exception
        checkWaterTempValue(request, 14.529999740188941);
    }

    @Test
    public void testDatelineCrossingMinGreaterThanMax() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageAcrossDateline.xml");
        checkDatelineCrossing(xml);
    }

    @Test
    public void testDatelineCrossingPositiveCoordinates() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageAcrossDateline2.xml");
        checkDatelineCrossing(xml);
    }

    @Test
    public void testDatelineCrossingPolar() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageAcrossDatelinePolar.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("polar_gtiff", "polar_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // check we got the right envelope
            Envelope2D envelope = targetCoverage.getEnvelope2D();
            // System.out.println(envelope);
            TestCase.assertEquals((-1139998), envelope.getMinX(), 1.0);
            TestCase.assertEquals((-3333134), envelope.getMinY(), 1.0);
            TestCase.assertEquals(1139998, envelope.getMaxX(), 1.0);
            TestCase.assertEquals((-1023493), envelope.getMaxY(), 1.0);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:3031", true), targetCoverage.getCoordinateReferenceSystem2D()));
            // we don't check the values, as we don't have the smarts available in the
            // rendering subsystem to read a larger area also when the
            // reprojection makes the pixel shrink
        } finally {
            readerTarget.dispose();
            scheduleForCleaning(targetCoverage);
        }
    }

    @Test
    public void testDatelineCrossingMercatorPDC() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageAcrossDatelineMercatorPacific.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("image/tiff", response.getContentType());
        byte[] tiffContents = getBinary(response);
        File file = File.createTempFile("polar_gtiff", "polar_gtiff.tiff", new File("./target"));
        FileUtils.writeByteArrayToFile(file, tiffContents);
        GeoTiffReader readerTarget = new GeoTiffReader(file);
        GridCoverage2D targetCoverage = null;
        try {
            targetCoverage = readerTarget.read(null);
            // check we got the right envelope
            Envelope2D envelope = targetCoverage.getEnvelope2D();
            TestCase.assertEquals(160, envelope.getMinX(), 0.0);
            TestCase.assertEquals(0, envelope.getMinY(), 0.0);
            TestCase.assertEquals(200, envelope.getMaxX(), 0.0);
            TestCase.assertEquals(40, envelope.getMaxY(), 0.0);
            TestCase.assertTrue(CRS.equalsIgnoreMetadata(WGS84, targetCoverage.getCoordinateReferenceSystem2D()));
            // check we actually read the right stuff. For this case, we
            // just check we have the pixels in the range of values of that area
            RenderedImage renderedImage = targetCoverage.getRenderedImage();
            Raster data = renderedImage.getData();
            double[] pixel = new double[1];
            for (int i = data.getMinY(); i < ((data.getMinY()) + (data.getHeight())); i++) {
                for (int j = data.getMinX(); j < ((data.getMinX()) + (data.getWidth())); j++) {
                    data.getPixel(i, j, pixel);
                    double d = pixel[0];
                    TestCase.assertTrue(String.valueOf(d), ((d > 500) && (d < 5500)));
                }
            }
        } finally {
            readerTarget.dispose();
            scheduleForCleaning(targetCoverage);
        }
    }

    @Test
    public void testDeferredLoading() throws Exception {
        DefaultWebCoverageService20 wcs = GeoServerExtensions.bean(DefaultWebCoverageService20.class);
        GetCoverageType getCoverage = eINSTANCE.createGetCoverageType();
        getCoverage.setCoverageId(getLayerId(GetCoverageTest.SPATIO_TEMPORAL));
        getCoverage.setVersion("2.0.0");
        getCoverage.setService("WCS");
        GridCoverage coverage = null;
        try {
            coverage = wcs.getCoverage(getCoverage);
            Assert.assertNotNull(coverage);
            assertDeferredLoading(coverage.getRenderedImage());
        } finally {
            scheduleForCleaning(coverage);
        }
    }

    @Test
    public void testInvalidElevationTrimmingOutsideRange() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageDimensionTrimmingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${dimension}", "elevation");
        request = request.replace("${trimLow}", "-500");
        request = request.replace("${trimHigh}", "-400");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        String errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested elevation subset does not intersect the declared range 20.0/150.0", errorMessage);
    }

    @Test
    public void testInvalidElevationTrimmingInsideRange() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageDimensionTrimmingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${dimension}", "elevation");
        request = request.replace("${trimLow}", "99.5");// 

        request = request.replace("${trimHigh}", "99.7");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        String errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested elevation subset does not intersect available values [[20.0, 99.0], [100.0, 150.0]]", errorMessage);
    }

    @Test
    public void testInvalidTimeTrimmingOutsideRange() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageDimensionTrimmingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${dimension}", "time");
        request = request.replace("${trimLow}", "1990-11-01T00:00:00.000Z");
        request = request.replace("${trimHigh}", "1991-11-01T00:00:00.000Z");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        String errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested time subset does not intersect the declared range 2008-10-31T00:00:00.000Z/2008-11-07T00:00:00.000Z", errorMessage);
    }

    @Test
    public void testInvalidTimeTrimmingInsideRange() throws Exception {
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP_DILATED), TIME, LIST, null);
        setupRasterDimension(getLayerId(GetCoverageTest.WATTEMP_DILATED), ELEVATION, LIST, null);
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageDimensionTrimmingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__watertemp_dilated");
        request = request.replace("${dimension}", "time");
        request = request.replace("${trimLow}", "2008-11-01T11:00:00.000Z");
        request = request.replace("${trimHigh}", "2008-11-01T12:01:00.000Z");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        String errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested time subset does not intersect available values [2008-10-31T00:00:00.000Z, 2008-11-03T00:00:00.000Z]", errorMessage);
    }

    @Test
    public void testInvalidCustomDimensionSlicing() throws Exception {
        setupTimeRangesTimeElevationCustom(GetCoverageTest.TIMERANGES, TIME, ELEVATION, "WAVELENGTH");
        final File xml = new File("./src/test/resources/trimming/requestGetCoverageTimeElevationCustomSlicingXML.xml");
        String request = FileUtils.readFileToString(xml);
        request = request.replace("${coverageId}", "sf__timeranges");
        request = request.replace("${slicePointElevation}", "20");
        request = request.replace("${slicePointTime}", "2008-10-31T00:00:00.000Z");
        request = request.replace("${Custom}", "WAVELENGTH");
        request = request.replace("${slicePointCustom}", "-300");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        String errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested WAVELENGTH subset does not intersect the available values [12/24, 25/80]", errorMessage);
        request = request.replace("WAVELENGTH", "wavelength");
        response = postAsServletResponse("wcs", request);
        errorMessage = checkOws20Exception(response, 404, InvalidSubsetting.getExceptionCode(), "subset");
        TestCase.assertEquals("Requested WAVELENGTH subset does not intersect the available values [12/24, 25/80]", errorMessage);
    }
}

