/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0;


import DataPacking.ADD_OFFSET;
import DataPacking.SCALE_FACTOR;
import DataType.FLOAT;
import DataType.SHORT;
import NetCDFUtilities.FILL_VALUE;
import NetCDFUtilities.STANDARD_NAME;
import PixelInCell.CELL_CENTER;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageView;
import org.geoserver.data.test.CiteTestData;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geoserver.wcs.WCSInfo;
import org.geoserver.wcs2_0.response.GranuleStack;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.netcdf.NetCDFReader;
import org.geotools.feature.NameImpl;
import org.geotools.imageio.netcdf.utilities.NetCDFUtilities;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.mock.web.MockHttpServletResponse;
import ucar.ma2.Array;
import ucar.ma2.Range;
import ucar.ma2.Section;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;


@TestSetup(run = TestSetupFrequency.ONCE)
public class WCSNetCDFMosaicTest extends WCSNetCDFBaseTest {
    private static final double DELTA = 1.0E-6;

    private static final double DELTA2 = 1.0E-4;

    private static final double PACKED_FILL_VALUE = -32768.0;

    private static final String ORIGINAL_UNIT = "km";

    private static final String CANONICAL_UNIT = "m";

    private static final double ORIGINAL_FILL_VALUE = -9999.0;

    private static final double ORIGINAL_PIXEL_VALUE = 9219.328;

    public static QName LATLONMOSAIC = new QName(CiteTestData.WCS_URI, "2DLatLonCoverage", CiteTestData.WCS_PREFIX);

    public static QName DUMMYMOSAIC = new QName(CiteTestData.WCS_URI, "DummyCoverage", CiteTestData.WCS_PREFIX);

    public static QName VISIBILITYCF = new QName(CiteTestData.WCS_URI, "visibilityCF", CiteTestData.WCS_PREFIX);

    public static QName VISIBILITYPACKED = new QName(CiteTestData.WCS_URI, "visibilityPacked", CiteTestData.WCS_PREFIX);

    public static QName VISIBILITYCOMPRESSED = new QName(CiteTestData.WCS_URI, "visibilityCompressed", CiteTestData.WCS_PREFIX);

    public static QName VISIBILITYCFPACKED = new QName(CiteTestData.WCS_URI, "visibilityCFPacked", CiteTestData.WCS_PREFIX);

    public static QName VISIBILITYNANPACKED = new QName(CiteTestData.WCS_URI, "visibilityNaNPacked", CiteTestData.WCS_PREFIX);

    public static QName TEMPERATURE_SURFACE = new QName(CiteTestData.WCS_URI, "Temperature_surface", CiteTestData.WCS_PREFIX);

    public static QName BANDWITHCRS = new QName(CiteTestData.WCS_URI, "Band1", CiteTestData.WCS_PREFIX);

    private static final String STANDARD_NAME = "visibility_in_air";

    private static final Section NETCDF_SECTION;

    private CoverageView coverageView = null;

    static {
        System.setProperty("org.geotools.referencing.forceXY", "true");
        final List<Range> ranges = new LinkedList<Range>();
        ranges.add(new Range(1));
        ranges.add(new Range(1));
        NETCDF_SECTION = new Section(ranges);
    }

    @Test
    public void testRequestCoverage() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__2DLatLonCoverage&format=application/custom&subset=time,http://www.opengis.net/def/trs/ISO-8601/0/Gregorian UTC(\"2013-11-01T00:00:00.000Z\")&subset=BANDS(\"MyBand\")"));
        Assert.assertNotNull(response);
        GridCoverage2D lastResult = applicationContext.getBean(WCSResponseInterceptor.class).getLastResult();
        Assert.assertTrue((lastResult instanceof GranuleStack));
        GranuleStack stack = ((GranuleStack) (lastResult));
        // we expect a single granule which covers the entire mosaic
        for (GridCoverage2D c : stack.getGranules()) {
            Assert.assertEquals(30.0, c.getEnvelope2D().getHeight(), 0.001);
            Assert.assertEquals(45.0, c.getEnvelope2D().getWidth(), 0.001);
        }
        Assert.assertEquals(1, stack.getGranules().size());
    }

    @Test
    public void testRequestCoverageLatLon() throws Exception {
        final WCSInfo wcsInfo = getWCS();
        final boolean oldLatLon = wcsInfo.isLatLon();
        wcsInfo.setLatLon(true);
        getGeoServer().save(wcsInfo);
        try {
            // http response from the request inside the string
            MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__2DLatLonCoverage&format=application/custom&subset=time,http://www.opengis.net/def/trs/ISO-8601/0/Gregorian UTC(\"2013-11-01T00:00:00.000Z\")&subset=BANDS(\"MyBand\")"));
            Assert.assertNotNull(response);
            GridCoverage2D lastResult = applicationContext.getBean(WCSResponseInterceptor.class).getLastResult();
            Assert.assertTrue((lastResult instanceof GranuleStack));
            GranuleStack stack = ((GranuleStack) (lastResult));
            // we expect a single granule which covers the entire mosaic
            for (GridCoverage2D c : stack.getGranules()) {
                System.out.println(c.getEnvelope());
                Assert.assertEquals(45.0, c.getEnvelope2D().getHeight(), 0.001);
                Assert.assertEquals(30.0, c.getEnvelope2D().getWidth(), 0.001);
            }
            Assert.assertEquals(1, stack.getGranules().size());
        } finally {
            wcsInfo.setLatLon(oldLatLon);
            getGeoServer().save(wcsInfo);
        }
    }

    @Test
    public void testRequestCoverageView() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__dummyView&format=application/x-netcdf&subset=http://www.opengis.net/def/axis/OGC/0/time(\"2013-01-08T00:00:00.000Z\")"));
        Assert.assertNotNull(response);
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "out.nc", new File("./target"));
        try {
            FileUtils.writeByteArrayToFile(file, netcdfOut);
            NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
            Assert.assertNotNull(dataset);
            dataset.close();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testRequestNetCDF4() throws Exception {
        boolean isNC4Available = NetCDFUtilities.isNC4CAvailable();
        if ((!isNC4Available) && (LOGGER.isLoggable(Level.INFO))) {
            LOGGER.info("NetCDF C library not found. NetCDF4 output will not be created");
        }
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__dummyView&format=application/x-netcdf4&subset=http://www.opengis.net/def/axis/OGC/0/time(\"2013-01-08T00:00:00.000Z\")"));
        Assert.assertNotNull(response);
        Assert.assertEquals((isNC4Available ? "application/x-netcdf4" : "application/xml"), response.getContentType());
        if (isNC4Available) {
            byte[] netcdfOut = getBinary(response);
            File file = File.createTempFile("netcdf", "out.nc", new File("./target"));
            FileUtils.writeByteArrayToFile(file, netcdfOut);
            NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
            Assert.assertNotNull(dataset);
            dataset.close();
        }
    }

    @Test
    public void testRequestNetCDFUomConversion() throws Exception {
        // http response from the request inside the string
        CoverageInfo info = getCatalog().getCoverageByName(new NameImpl("wcs", "visibilityCF"));
        Assert.assertTrue(info.getDimensions().get(0).getUnit().equalsIgnoreCase(WCSNetCDFMosaicTest.ORIGINAL_UNIT));
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__visibilityCF&format=application/x-netcdf"));
        Assert.assertNotNull(response);
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "outCF.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, netcdfOut);
        NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
        Variable var = dataset.findVariable(WCSNetCDFMosaicTest.STANDARD_NAME);
        Assert.assertNotNull(var);
        // Check the unit has been converted to meter
        String unit = var.getUnitsString();
        Assert.assertEquals(WCSNetCDFMosaicTest.CANONICAL_UNIT, unit);
        Array readData = var.read(WCSNetCDFMosaicTest.NETCDF_SECTION);
        Assert.assertEquals(FLOAT, readData.getDataType());
        float data = readData.getFloat(0);
        // Data have been converted to canonical unit (m) from km.
        // Data value is bigger
        Assert.assertEquals(data, ((WCSNetCDFMosaicTest.ORIGINAL_PIXEL_VALUE) * 1000), WCSNetCDFMosaicTest.DELTA);
        Attribute fillValue = var.findAttribute(FILL_VALUE);
        Attribute standardName = var.findAttribute(NetCDFUtilities.STANDARD_NAME);
        Assert.assertNotNull(standardName);
        Assert.assertEquals(WCSNetCDFMosaicTest.STANDARD_NAME, standardName.getStringValue());
        Assert.assertNotNull(fillValue);
        Assert.assertEquals(WCSNetCDFMosaicTest.ORIGINAL_FILL_VALUE, fillValue.getNumericValue().doubleValue(), WCSNetCDFMosaicTest.DELTA);
        // Check global attributes have been added
        Attribute attribute = dataset.findGlobalAttribute("custom_attribute");
        Assert.assertNotNull(attribute);
        Assert.assertEquals("testing WCS", attribute.getStringValue());
        dataset.close();
    }

    @Test
    public void testRequestNetCDFCompression() throws Exception {
        boolean isNC4Available = NetCDFUtilities.isNC4CAvailable();
        if ((!isNC4Available) && (LOGGER.isLoggable(Level.INFO))) {
            LOGGER.info("NetCDF C library not found. NetCDF4 output will not be created");
        }
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__visibilityCompressed&format=application/x-netcdf4"));
        Assert.assertNotNull(response);
        Assert.assertEquals((isNC4Available ? "application/x-netcdf4" : "application/xml"), response.getContentType());
        if (isNC4Available) {
            byte[] netcdfOut = getBinary(response);
            File file = File.createTempFile("netcdf", "outCompressed.nc", new File("./target"));
            FileUtils.writeByteArrayToFile(file, netcdfOut);
            NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
            Assert.assertNotNull(dataset);
            Variable var = dataset.findVariable(WCSNetCDFMosaicTest.STANDARD_NAME);
            Assert.assertNotNull(var);
            final long varByteSize = (var.getSize()) * (var.getDataType().getSize());
            // The output file is smaller than the size of the underlying variable.
            // Compression successfully occurred
            Assert.assertTrue(((netcdfOut.length) < varByteSize));
            dataset.close();
        }
    }

    @Test
    public void testRequestNetCDFDataPacking() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__visibilityPacked&format=application/x-netcdf"));
        Assert.assertNotNull(response);
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "outPK.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, netcdfOut);
        NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
        Variable var = dataset.findVariable(WCSNetCDFMosaicTest.STANDARD_NAME);
        Assert.assertNotNull(var);
        // Check the unit hasn't been converted
        String unit = var.getUnitsString();
        Assert.assertEquals(WCSNetCDFMosaicTest.ORIGINAL_UNIT, unit);
        Attribute fillValue = var.findAttribute(FILL_VALUE);
        Assert.assertNotNull(fillValue);
        // There is dataPacking, therefore, fillValue should have been changed
        Assert.assertEquals(WCSNetCDFMosaicTest.PACKED_FILL_VALUE, fillValue.getNumericValue().doubleValue(), 1.0E-6);
        Attribute addOffsetAttr = var.findAttribute(ADD_OFFSET);
        Assert.assertNotNull(addOffsetAttr);
        Attribute scaleFactorAttr = var.findAttribute(SCALE_FACTOR);
        Assert.assertNotNull(scaleFactorAttr);
        double scaleFactor = scaleFactorAttr.getNumericValue().doubleValue();
        double addOffset = addOffsetAttr.getNumericValue().doubleValue();
        Array readData = var.read(WCSNetCDFMosaicTest.NETCDF_SECTION);
        Assert.assertEquals(SHORT, readData.getDataType());
        short data = readData.getShort(0);
        // Data has been packed to short
        double packedData = ((WCSNetCDFMosaicTest.ORIGINAL_PIXEL_VALUE) - addOffset) / scaleFactor;
        Assert.assertEquals(((short) (packedData + 0.5)), data, WCSNetCDFMosaicTest.DELTA);
        dataset.close();
    }

    @Test
    public void testRequestNetCDFCFDataPacking() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__visibilityCFPacked&format=application/x-netcdf"));
        Assert.assertNotNull(response);
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "outCFPK.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, netcdfOut);
        NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
        Variable var = dataset.findVariable(WCSNetCDFMosaicTest.STANDARD_NAME);
        Assert.assertNotNull(var);
        // Check the unit has been converted to meter
        String unit = var.getUnitsString();
        Assert.assertEquals(WCSNetCDFMosaicTest.CANONICAL_UNIT, unit);
        Attribute addOffsetAttr = var.findAttribute(ADD_OFFSET);
        Assert.assertNotNull(addOffsetAttr);
        Attribute scaleFactorAttr = var.findAttribute(SCALE_FACTOR);
        Assert.assertNotNull(scaleFactorAttr);
        double scaleFactor = scaleFactorAttr.getNumericValue().doubleValue();
        double addOffset = addOffsetAttr.getNumericValue().doubleValue();
        Array readData = var.read(WCSNetCDFMosaicTest.NETCDF_SECTION);
        Assert.assertEquals(SHORT, readData.getDataType());
        short data = readData.getShort(0);
        // Data has been packed to short
        // Going from original unit to canonical, then packing
        double packedData = (((WCSNetCDFMosaicTest.ORIGINAL_PIXEL_VALUE) * 1000) - addOffset) / scaleFactor;
        Assert.assertEquals(((short) (packedData + 0.5)), data, WCSNetCDFMosaicTest.DELTA);
        Attribute fillValue = var.findAttribute(FILL_VALUE);
        Assert.assertNotNull(fillValue);
        // There is dataPacking, therefore, fillValue should have been changed
        Assert.assertEquals(WCSNetCDFMosaicTest.PACKED_FILL_VALUE, fillValue.getNumericValue().doubleValue(), WCSNetCDFMosaicTest.DELTA);
        // Check global attributes have been added
        Attribute attribute = dataset.findGlobalAttribute("custom_attribute");
        Assert.assertNotNull(attribute);
        Assert.assertEquals("testing WCS", attribute.getStringValue());
        dataset.close();
    }

    @Test
    public void testRequestNetCDFNaNDataPacking() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__visibilityNaNPacked&format=application/x-netcdf"));
        Assert.assertNotNull(response);
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "outNaNPK.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, netcdfOut);
        NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
        Variable var = dataset.findVariable(WCSNetCDFMosaicTest.STANDARD_NAME);
        Assert.assertNotNull(var);
        Array readData = var.read(WCSNetCDFMosaicTest.NETCDF_SECTION);
        Assert.assertEquals(SHORT, readData.getDataType());
        // Check the fix on dataPacking NaN management
        Assert.assertNotEquals(readData.getShort(0), (-32768), 1.0E-6);
        dataset.close();
    }

    @Test
    public void testRequestNetCDFCrs() throws Exception {
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + (("&coverageId=wcs__Band1&format=application/x-netcdf" + "&subsettingcrs=http://www.opengis.net/def/crs/EPSG/0/4326") + "&subset=Long(-118,-116)&subset=Lat(56,58)")));
        // Original data was UTM 32611
        Assert.assertNotNull(response);
        byte[] netcdfOut = getBinary(response);
        File file = File.createTempFile("netcdf", "outcrs.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, netcdfOut);
        // Retrieve the GeoTransform attribute from the output NetCDF
        NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath());
        String geoTransform = dataset.findGlobalAttribute("GeoTransform").getStringValue();
        dataset.close();
        Assert.assertNotNull(geoTransform);
        String[] coefficients = geoTransform.split(" ");
        double m00 = Double.parseDouble(coefficients[1]);
        double m01 = Double.parseDouble(coefficients[2]);
        double m02 = Double.parseDouble(coefficients[0]);
        double m10 = Double.parseDouble(coefficients[4]);
        double m11 = Double.parseDouble(coefficients[5]);
        double m12 = Double.parseDouble(coefficients[3]);
        NetCDFReader reader = new NetCDFReader(file, null);
        MathTransform transform = reader.getOriginalGridToWorld(CELL_CENTER);
        AffineTransform2D affineTransform = ((AffineTransform2D) (transform));
        reader.dispose();
        // Check the GeoTransform coefficients are valid
        Assert.assertEquals(m02, affineTransform.getTranslateX(), WCSNetCDFMosaicTest.DELTA2);
        Assert.assertEquals(m12, affineTransform.getTranslateY(), WCSNetCDFMosaicTest.DELTA2);
        Assert.assertEquals(m00, affineTransform.getScaleX(), WCSNetCDFMosaicTest.DELTA2);
        Assert.assertEquals(m11, affineTransform.getScaleY(), WCSNetCDFMosaicTest.DELTA2);
        Assert.assertEquals(m01, affineTransform.getShearX(), WCSNetCDFMosaicTest.DELTA2);
        Assert.assertEquals(m10, affineTransform.getShearY(), WCSNetCDFMosaicTest.DELTA2);
    }

    /**
     * Test <code>Temperature_surface</code> extra variables, variable attributes, and global
     * attributes of different types, for NetCDF-3 output.
     */
    @Test
    public void testExtraVariablesNetcdf3() throws Exception {
        checkExtraVariables("application/x-netcdf");
    }

    /**
     * Test <code>Temperature_surface</code> extra variables, variable attributes, and global
     * attributes of different types, for NetCDF-4 output.
     */
    @Test
    public void testExtraVariablesNetcdf4() throws Exception {
        Assume.assumeTrue(NetCDFUtilities.isNC4CAvailable());
        checkExtraVariables("application/x-netcdf4");
    }
}

