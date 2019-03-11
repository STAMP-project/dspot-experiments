/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.netcdf;


import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for WMS GetFeatureInfo on a layer sourced from NetCDF.
 */
public class NetCDFGetFeatureInfoTest extends WMSTestSupport {
    /**
     * Test that an XML GetFeatureInfo response contains a property whose name has been normalised
     * to a valid NCName.
     *
     * <p>The NetCDF source has <code>analyzed_sst:long_name ="Analyzed Sea Surface Temperature"
     * </code>, which must be converted to a valid NCName before it can be used in an XML response.
     * The implementation converts spaces to underscores to achieve this.
     */
    @Test
    public void testValidXmlNcName() throws Exception {
        String response = getAsString(("wms?service=WMS&version=1.3.0&request=GetFeatureInfo" + (("&layers=sf%3Aanalyzed_sst&query_layers=sf%3Aanalyzed_sst" + "&format=image/png&info_format=text/xml") + "&srs=EPSG%3A4326&bbox=25,-86,27,-83&width=100&height=100&x=37&y=50")));
        Assert.assertTrue(response.contains("<wfs:FeatureCollection"));
        Assert.assertTrue(response.contains("<sf:Analyzed_Sea_Surface_Temperature>23.0</sf:Analyzed_Sea_Surface_Temperature>"));
    }
}

