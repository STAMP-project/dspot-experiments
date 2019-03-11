/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.wfs.servlets;


import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests the functionality of the {@link TestWfsPost} servlet on a running geoserver.
 *
 * <p>This test assumes a running GeoServer on port 8080 with the release data dir.
 *
 * @author Torben Barsballe
 */
public class TestWfsPostOnlineIntegrationTest {
    public static final String WFS_REQUEST = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\"\n" + ((((((((((("  xmlns:ne=\"http://www.naturalearthdata.com\"\n" + "  xmlns:wfs=\"http://www.opengis.net/wfs\"\n") + "  xmlns:ogc=\"http://www.opengis.net/ogc\"\n") + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "  xsi:schemaLocation=\"http://www.opengis.net/wfs\n") + "                      http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\">\n") + "  <wfs:Query typeName=\"states\">\n") + "    <ogc:Filter>\n") + "       <ogc:FeatureId fid=\"states.3\"/>\n") + "    </ogc:Filter>\n") + "    </wfs:Query>\n") + "</wfs:GetFeature>");

    @Test
    public void testWfsPost() throws IOException, ServletException {
        Assume.assumeTrue(isOnline());
        MockHttpServletResponse response = doWfsPost();
        Assert.assertTrue(response.getContentAsString().contains("wfs:FeatureCollection"));
    }

    @Test
    public void testWfsPostAuthenticated() throws IOException, ServletException {
        Assume.assumeTrue(isOnline());
        MockHttpServletResponse response = doWfsPost("admin", "geoserver");
        Assert.assertTrue(response.getContentAsString().contains("wfs:FeatureCollection"));
    }

    @Test
    public void testWfsPostInvalidAuth() throws IOException, ServletException {
        Assume.assumeTrue(isOnline());
        MockHttpServletResponse response = doWfsPost("admin", "badpassword");
        Assert.assertFalse(response.getContentAsString().contains("wfs:FeatureCollection"));
        Assert.assertTrue(response.getContentAsString().contains("HTTP response: 401"));
    }
}

