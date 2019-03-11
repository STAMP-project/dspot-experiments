/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.wcs.test.CoverageTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for custom dimensions in WCS requests.
 *
 * @author Andrea Aime
 */
public class DynamicDimensionsTest extends CoverageTestSupport {
    private static final String DIMENSION_NAME = "wavelength";

    private static final QName WATTEMP = new QName(MockData.DEFAULT_URI, "watertemp", MockData.DEFAULT_PREFIX);

    @Test
    public void testGetCoverageBadValue() throws Exception {
        // check that we get no data when requesting an incorrect value for custom dimension
        String request = getWaterTempRequest("bad_dimension_value");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNull(image);
        request = request.replace(DynamicDimensionsTest.DIMENSION_NAME, DynamicDimensionsTest.DIMENSION_NAME.toUpperCase());
        response = postAsServletResponse("wcs", request);
        image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNull(image);
    }

    @Test
    public void testGetCoverageGoodValue() throws Exception {
        // check that we get data when requesting a correct value for custom dimension
        String request = getWaterTempRequest("100");
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNotNull(image);
        Assert.assertEquals("image/tiff", response.getContentType());
        request = request.replace(DynamicDimensionsTest.DIMENSION_NAME, DynamicDimensionsTest.DIMENSION_NAME.toUpperCase());
        response = postAsServletResponse("wcs", request);
        image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNotNull(image);
        Assert.assertEquals("image/tiff", response.getContentType());
    }
}

