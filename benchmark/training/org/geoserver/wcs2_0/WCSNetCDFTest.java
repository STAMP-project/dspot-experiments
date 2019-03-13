/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0;


import DataPacking.SHORT;
import DataType.DOUBLE;
import NetcdfDataset.Enhance;
import NetcdfDataset.Enhance.CoordSystems;
import NetcdfDataset.Enhance.ScaleMissing;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.data.test.CiteTestData;
import org.geoserver.web.netcdf.layer.NetCDFLayerSettingsContainer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Base support class for NetCDF wcs tests.
 *
 * @author Daniele Romagnoli, GeoSolutions
 */
public class WCSNetCDFTest extends WCSNetCDFBaseTest {
    public static final double DELTA = 1.0E-6;

    public static QName POLYPHEMUS = new QName(CiteTestData.WCS_URI, "polyphemus", CiteTestData.WCS_PREFIX);

    public static QName NO2 = new QName(CiteTestData.WCS_URI, "NO2", CiteTestData.WCS_PREFIX);

    public static QName O3 = new QName(CiteTestData.WCS_URI, "O3", CiteTestData.WCS_PREFIX);

    public static QName TEMPERATURE_SURFACE_NETCDF = new QName(CiteTestData.WCS_URI, "Temperature_surface_NetCDF", CiteTestData.WCS_PREFIX);

    public static QName TEMPERATURE_SURFACE_GRIB = new QName(CiteTestData.WCS_URI, "Temperature_surface", CiteTestData.WCS_PREFIX);

    public static QName SNOW_DEPTH_GRIB = new QName(CiteTestData.WCS_URI, "Snow_depth_water_equivalent_surface", CiteTestData.WCS_PREFIX);

    /**
     * This test checks if an exception is not thrown when is requested an image with a total size
     * lower than the maximum geoserver output size.
     */
    @Test
    public void testOutputMemoryNotExceeded() throws Exception {
        // Setting of the output limit to 40 Kb
        setOutputLimit(40);
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__NO2&format=application/x-netcdf&subset=http://www.opengis.net/def/axis/OGC/0/elevation(450)"));
        // The status code should be correct
        Assert.assertEquals(200, response.getStatus());
        // The output format should be netcdf
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        // Reset output limit
        setOutputLimit((-1));
    }

    /**
     * This test checks if an exception is thrown when is requested an image with a total size
     * greater than the maximum geoserver output memory allowed.
     */
    @Test
    public void testOutputMemoryExceeded() throws Exception {
        // Setting of the output limit to 40 Kb
        setOutputLimit(40);
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__NO2&format=application/x-netcdf"));
        // The output format should be xml because an exception must be thrown
        Assert.assertEquals("application/xml", response.getContentType());
        // Reset output limit
        setOutputLimit((-1));
    }

    /**
     * This test checks if an exception is not thrown when is requested an image with a total size
     * lower than the maximum geoserver input size.
     */
    @Test
    public void testInputMemoryCorrect() throws Exception {
        // Setting of the input limit to 40 Kb
        setInputLimit(40);
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__NO2&format=application/x-netcdf&subset=http://www.opengis.net/def/axis/OGC/0/elevation(450)"));
        // The status code should be correct
        Assert.assertEquals(200, response.getStatus());
        // The output format should be netcdf
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        // Reset input limit
        setInputLimit((-1));
    }

    /**
     * This test checks if an exception is thrown when is requested an image with a total size
     * greater than the maximum geoserver input memory allowed.
     */
    @Test
    public void testInputMemoryExceeded() throws Exception {
        // Setting of the input limit to 40 Kb
        setInputLimit(40);
        // http response from the request inside the string
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__NO2&format=application/x-netcdf"));
        // The output format should be xml because an exception must be thrown
        Assert.assertEquals("application/xml", response.getContentType());
        // Reset input limit
        setInputLimit((-1));
    }

    /**
     * Test NetCDF output from a NetCDF file with a rotated pole projection.
     */
    @Test
    public void testNetcdfRotatedPole() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageid=wcs__Temperature_surface_NetCDF&format=application/x-netcdf"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        byte[] responseBytes = getBinary(response);
        File file = File.createTempFile("netcdf-rotated-pole-", "-wcs__Temperature_surface_NetCDF.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, responseBytes);
        try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath())) {
            Assert.assertNotNull(dataset);
            // check dimensions
            Dimension rlonDim = dataset.findDimension("rlon");
            Assert.assertNotNull(rlonDim);
            Assert.assertEquals(7, rlonDim.getLength());
            Dimension rlatDim = dataset.findDimension("rlat");
            Assert.assertNotNull(rlatDim);
            Assert.assertEquals(5, rlatDim.getLength());
            // check coordinate variables
            Variable rlonVar = dataset.findVariable("rlon");
            Assert.assertNotNull(rlonVar);
            Assert.assertEquals(1, rlonVar.getDimensions().size());
            Assert.assertEquals(rlonDim, rlonVar.getDimensions().get(0));
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlonVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -30, -20, -10, 0, 10, 20, 30 }, ((float[]) (rlonVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            Variable rlatVar = dataset.findVariable("rlat");
            Assert.assertNotNull(rlatVar);
            Assert.assertEquals(1, rlatVar.getDimensions().size());
            Assert.assertEquals(rlatDim, rlatVar.getDimensions().get(0));
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlatVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -20, -10, 0, 10, 20 }, ((float[]) (rlatVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            // check projection variable
            Variable projVar = dataset.findVariable("rotated_latitude_longitude");
            Assert.assertNotNull(projVar);
            Assert.assertEquals("rotated_latitude_longitude", projVar.findAttribute("grid_mapping_name").getStringValue());
            Assert.assertEquals(74.0, projVar.findAttribute("grid_north_pole_longitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            Assert.assertEquals(36.0, projVar.findAttribute("grid_north_pole_latitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            // check data variable
            Variable tempVar = dataset.findVariable("Temperature_surface_NetCDF");
            Assert.assertNotNull(tempVar);
            Assert.assertEquals("rotated_latitude_longitude", tempVar.findAttribute("grid_mapping").getStringValue());
            Assert.assertEquals("K", tempVar.findAttribute("units").getStringValue());
            Assert.assertEquals(2, tempVar.getDimensions().size());
            Assert.assertEquals(rlatDim, tempVar.getDimensions().get(0));
            Assert.assertEquals(rlonDim, tempVar.getDimensions().get(1));
            Assert.assertArrayEquals(new float[]{ 300, 299, 298, 297, 296, 295, 294, 299, 300, 299, 298, 297, 296, 295, 298, 299, 300, 299, 298, 297, 296, 297, 298, 299, 300, 299, 298, 297, 296, 297, 298, 299, 300, 299, 298 }, ((float[]) (tempVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Test NetCDF output from an RAP native GRIB2 file with a GDS template 32769 rotated pole
     * projection.
     */
    @Test
    public void testRapNativeGribRotatedPole() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageid=wcs__Temperature_surface&format=application/x-netcdf"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        byte[] responseBytes = getBinary(response);
        File file = File.createTempFile("rap-native-grib-rotated-pole-", "-wcs__Temperature_surface.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, responseBytes);
        try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath())) {
            Assert.assertNotNull(dataset);
            // check dimensions
            Dimension rlonDim = dataset.findDimension("rlon");
            Assert.assertNotNull(rlonDim);
            Assert.assertEquals(7, rlonDim.getLength());
            Dimension rlatDim = dataset.findDimension("rlat");
            Assert.assertNotNull(rlatDim);
            Assert.assertEquals(5, rlatDim.getLength());
            // check coordinate variables
            Variable rlonVar = dataset.findVariable("rlon");
            Assert.assertNotNull(rlonVar);
            Assert.assertEquals(1, rlonVar.getDimensions().size());
            Assert.assertEquals(rlonDim, rlonVar.getDimensions().get(0));
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlonVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -30, -20, -10, 0, 10, 20, 30 }, ((float[]) (rlonVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            Variable rlatVar = dataset.findVariable("rlat");
            Assert.assertNotNull(rlatVar);
            Assert.assertEquals(1, rlatVar.getDimensions().size());
            Assert.assertEquals(rlatDim, rlatVar.getDimensions().get(0));
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlatVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -20, -10, 0, 10, 20 }, ((float[]) (rlatVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            // check projection variable
            Variable projVar = dataset.findVariable("rotated_latitude_longitude");
            Assert.assertNotNull(projVar);
            Assert.assertEquals("rotated_latitude_longitude", projVar.findAttribute("grid_mapping_name").getStringValue());
            Assert.assertEquals(74.0, projVar.findAttribute("grid_north_pole_longitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            Assert.assertEquals(36.0, projVar.findAttribute("grid_north_pole_latitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            // check data variable
            Variable dataVar = dataset.findVariable("Temperature_surface");
            Assert.assertNotNull(dataVar);
            Assert.assertEquals("rotated_latitude_longitude", dataVar.findAttribute("grid_mapping").getStringValue());
            Assert.assertEquals("K", dataVar.findAttribute("units").getStringValue());
            Assert.assertEquals(2, dataVar.getDimensions().size());
            Assert.assertEquals(rlatDim, dataVar.getDimensions().get(0));
            Assert.assertEquals(rlonDim, dataVar.getDimensions().get(1));
            Assert.assertArrayEquals(new float[]{ 300, 299, 298, 297, 296, 295, 294, 299, 300, 299, 298, 297, 296, 295, 298, 299, 300, 299, 298, 297, 296, 297, 298, 299, 300, 299, 298, 297, 296, 297, 298, 299, 300, 299, 298 }, ((float[]) (dataVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Test NetCDF output from a COSMO EU GRIB2 file with a GDS template 1 rotated pole projection.
     */
    @Test
    public void testCosmoEuGribRotatedPole() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageid=wcs__Snow_depth_water_equivalent_surface&format=application/x-netcdf"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        byte[] responseBytes = getBinary(response);
        File file = File.createTempFile("cosmo-eu-grib-rotated-pole-", "-wcs__Snow_depth_water_equivalent_surface.nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, responseBytes);
        try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath())) {
            Assert.assertNotNull(dataset);
            // check dimensions
            Dimension rlonDim = dataset.findDimension("rlon");
            Assert.assertNotNull(rlonDim);
            Assert.assertEquals(5, rlonDim.getLength());
            Dimension rlatDim = dataset.findDimension("rlat");
            Assert.assertNotNull(rlatDim);
            Assert.assertEquals(5, rlatDim.getLength());
            // check coordinate variables
            Variable rlonVar = dataset.findVariable("rlon");
            Assert.assertNotNull(rlonVar);
            Assert.assertEquals(1, rlonVar.getDimensions().size());
            Assert.assertEquals(rlonDim, rlonVar.getDimensions().get(0));
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_longitude", rlonVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlonVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -18, -8, 2, 12, 22 }, ((float[]) (rlonVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            Variable rlatVar = dataset.findVariable("rlat");
            Assert.assertNotNull(rlatVar);
            Assert.assertEquals(1, rlatVar.getDimensions().size());
            Assert.assertEquals(rlatDim, rlatVar.getDimensions().get(0));
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("long_name").getStringValue());
            Assert.assertEquals("grid_latitude", rlatVar.findAttribute("standard_name").getStringValue());
            Assert.assertEquals("degrees", rlatVar.findAttribute("units").getStringValue());
            Assert.assertArrayEquals(new float[]{ -20, -10, 0, 10, 20 }, ((float[]) (rlatVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
            // check projection variable
            Variable projVar = dataset.findVariable("rotated_latitude_longitude");
            Assert.assertNotNull(projVar);
            Assert.assertEquals("rotated_latitude_longitude", projVar.findAttribute("grid_mapping_name").getStringValue());
            Assert.assertEquals((-170.0), projVar.findAttribute("grid_north_pole_longitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            Assert.assertEquals(40.0, projVar.findAttribute("grid_north_pole_latitude").getNumericValue().doubleValue(), WCSNetCDFTest.DELTA);
            // check data variable
            Variable dataVar = dataset.findVariable("Snow_depth_water_equivalent_surface");
            Assert.assertNotNull(dataVar);
            Assert.assertEquals("rotated_latitude_longitude", dataVar.findAttribute("grid_mapping").getStringValue());
            Assert.assertEquals(2, dataVar.getDimensions().size());
            Assert.assertEquals(rlatDim, dataVar.getDimensions().get(0));
            Assert.assertEquals(rlonDim, dataVar.getDimensions().get(1));
            Assert.assertArrayEquals(new float[]{ 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124 }, ((float[]) (dataVar.read().copyTo1DJavaArray())), ((float) (WCSNetCDFTest.DELTA)));
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void testDataPacking() throws Exception {
        // setup data packing
        CoverageInfo info = getCatalog().getCoverageByName(getLayerId(WCSNetCDFTest.O3));
        NetCDFLayerSettingsContainer container = new NetCDFLayerSettingsContainer();
        container.setCompressionLevel(0);
        container.setShuffle(true);
        container.setDataPacking(SHORT);
        String key = "NetCDFOutput.Key";
        info.getMetadata().put(key, container);
        getCatalog().save(info);
        try {
            MockHttpServletResponse response = getAsServletResponse(("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__O3&format=application/x-netcdf"));
            // The status code should be correct
            Assert.assertEquals(200, response.getStatus());
            // The output format should be netcdf
            Assert.assertEquals("application/x-netcdf", response.getContentType());
            byte[] responseBytes = getBinary(response);
            File file = File.createTempFile("polyphemus-O3", ".nc", new File("./target"));
            FileUtils.writeByteArrayToFile(file, responseBytes);
            // read non enhanced to check the data type
            try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath())) {
                Assert.assertNotNull(dataset);
                final Variable variable = dataset.findVariable("O3");
                // has been packed
                Assert.assertEquals(DataType.SHORT, variable.getDataType());
            }
            // read enhanced to validate scaling
            EnumSet<NetcdfDataset.Enhance> enhanceMode = EnumSet.of(CoordSystems);
            enhanceMode.add(ScaleMissing);
            try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath(), enhanceMode, 4096, null, null)) {
                Assert.assertNotNull(dataset);
                final Variable variable = dataset.findVariable("O3");
                // not read as packed this time
                Assert.assertEquals(DOUBLE, variable.getDataType());
                // compute min and max and compare to the ones Panoply returned for 03
                double[] data = ((double[]) (variable.read().copyTo1DJavaArray()));
                Arrays.sort(data);
                Assert.assertEquals(0.8663844, data[0], 0.1);
                Assert.assertEquals(175.7672, data[((data.length) - 1)], 0.1);
            }
        } finally {
            // revert
            info = getCatalog().getCoverageByName(getLayerId(WCSNetCDFTest.O3));
            info.getMetadata().remove(key);
            getCatalog().save(info);
        }
    }
}

