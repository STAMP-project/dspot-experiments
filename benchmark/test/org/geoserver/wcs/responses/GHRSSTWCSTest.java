/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.responses;


import DataType.BYTE;
import DataType.SHORT;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.geoserver.data.test.CiteTestData;
import org.geoserver.wcs2_0.kvp.WCSKVPTestSupport;
import org.geotools.imageio.netcdf.utilities.NetCDFUtilities;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Base support class for NetCDF wcs tests.
 *
 * @author Daniele Romagnoli, GeoSolutions
 */
public class GHRSSTWCSTest extends WCSKVPTestSupport {
    public static QName SST = new QName(CiteTestData.WCS_URI, "sst", CiteTestData.WCS_PREFIX);

    /**
     * Test NetCDF output from a coverage view having the required GHRSST bands/variables
     */
    @Test
    public void testGHRSST() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageid=") + (getLayerId(GHRSSTWCSTest.SST).replace(":", "__"))) + "&format=application/x-netcdf"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-netcdf", response.getContentType());
        // hope the file name structure is correct, not 100% sure
        String contentDispostion = response.getHeader("Content-disposition");
        Assert.assertEquals("inline; filename=19121213214553-EUR-L3U_GHRSST-SSTint-AVHRR_METOP_A-v02.0-fv01.0.nc", contentDispostion);
        byte[] responseBytes = getBinary(response);
        File file = File.createTempFile("ghrsst", ".nc", new File("./target"));
        FileUtils.writeByteArrayToFile(file, responseBytes);
        try (NetcdfDataset dataset = NetcdfDataset.openDataset(file.getAbsolutePath(), true, null)) {
            Assert.assertNotNull(dataset);
            // check global attributes
            Map<String, Attribute> globalAttributes = getGlobalAttributeMap(dataset);
            Assert.assertNotNull(globalAttributes.get("uuid"));
            UUID.fromString(globalAttributes.get("uuid").getStringValue());
            Assert.assertNotNull(globalAttributes.get("date_created"));
            Assert.assertNotNull(globalAttributes.get("spatial_resolution"));
            // input test file is a freak that really has this resolution, as verified with gdalinfo
            // e.g. gdalinfo NETCDF:"sst-orbit053.nc":"sea_surface_temperature"
            Assert.assertEquals("179.9499969482422 degrees", globalAttributes.get("spatial_resolution").getStringValue());
            // likewise, it really has time time
            Assert.assertNotNull(globalAttributes.get("start_time"));
            Assert.assertNotNull(globalAttributes.get("time_coverage_start"));
            Assert.assertEquals("19121213T204553Z", globalAttributes.get("start_time").getStringValue());
            Assert.assertEquals("19121213T204553Z", globalAttributes.get("time_coverage_start").getStringValue());
            Assert.assertNotNull(globalAttributes.get("stop_time"));
            Assert.assertNotNull(globalAttributes.get("time_coverage_end"));
            Assert.assertEquals("19121213T204553Z", globalAttributes.get("stop_time").getStringValue());
            Assert.assertEquals("19121213T204553Z", globalAttributes.get("time_coverage_end").getStringValue());
            // and these bounds
            double EPS = 0.001;
            Assert.assertNotNull(globalAttributes.get("northernmost_latitude"));
            Assert.assertNotNull(globalAttributes.get("southernmost_latitude"));
            Assert.assertNotNull(globalAttributes.get("westernmost_longitude"));
            Assert.assertNotNull(globalAttributes.get("easternmost_longitude"));
            Assert.assertEquals(119.925, globalAttributes.get("northernmost_latitude").getNumericValue().doubleValue(), EPS);
            Assert.assertEquals((-119.925), globalAttributes.get("southernmost_latitude").getNumericValue().doubleValue(), EPS);
            Assert.assertEquals((-269.925), globalAttributes.get("westernmost_longitude").getNumericValue().doubleValue(), EPS);
            Assert.assertEquals(269.925, globalAttributes.get("easternmost_longitude").getNumericValue().doubleValue(), EPS);
            // resolution, take 2
            Assert.assertNotNull(globalAttributes.get("geospatial_lat_units"));
            Assert.assertNotNull(globalAttributes.get("geospatial_lon_units"));
            Assert.assertEquals("degrees", globalAttributes.get("geospatial_lat_units").getStringValue());
            Assert.assertEquals("degrees", globalAttributes.get("geospatial_lon_units").getStringValue());
            // sea surface temperature
            Variable sst = dataset.findVariable("sea_surface_temperature");
            Assert.assertNotNull(sst);
            Assert.assertEquals(SHORT, sst.getDataType());
            Assert.assertNotNull(sst.findAttribute("scale_factor"));
            Assert.assertNotNull(sst.findAttribute("add_offset"));
            assertAttributeValue(sst, "comment", "Marine skin surface temperature");
            assertAttributeValue(sst, "long_name", "sea surface skin temperature");
            assertAttributeValue(sst, "standard_name", "sea_surface_skin_temperature");
            assertAttributeValue(sst, "units", "kelvin");
            assertAttributeValue(sst, "depth", "10 micrometres");
            // wind speed
            Variable windSpeed = dataset.findVariable("wind_speed");
            Assert.assertNotNull(windSpeed);
            Assert.assertEquals(BYTE, windSpeed.getDataType());
            Assert.assertNotNull(windSpeed.findAttribute("scale_factor"));
            Assert.assertNotNull(windSpeed.findAttribute("add_offset"));
            assertAttributeValue(windSpeed, "comment", ("Typically represents surface winds (10 meters above the sea " + "surface)"));
            assertAttributeValue(windSpeed, "long_name", "wind speed");
            assertAttributeValue(windSpeed, "standard_name", "wind_speed");
            assertAttributeValue(windSpeed, "units", "m s-1");
            // enable ehancing to check values
            dataset.enhance(NetcdfDataset.getEnhanceAll());
            assertValues(dataset, "sea_surface_temperature", new double[]{ 301, 302, 303, 304, 305, 306, 307, 308, 309 }, 0.002);
            assertValues(dataset, "wind_speed", new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 0.2);
        } finally {
            // FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Test NetCDF output from a coverage view having the required GHRSST bands/variables
     */
    @Test
    public void testGHRSSTSubset() throws Exception {
        // test requires NetCDF-4 native libs to be available
        Assume.assumeTrue(NetCDFUtilities.isNC4CAvailable());
        // this used to crash
        MockHttpServletResponse response = getAsServletResponse((((("ows?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageid=") + (getLayerId(GHRSSTWCSTest.SST).replace(":", "__"))) + "&subset=Long(-10,10)&subset=Lat(-10,10)") + "&format=application/x-netcdf4"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-netcdf4", response.getContentType());
    }
}

