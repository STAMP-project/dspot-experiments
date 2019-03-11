/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.response;


import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.wcs2_0.kvp.WCSKVPTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GdalWcsTest extends WCSKVPTestSupport {
    private static final QName GRAYALPHA = new QName(MockData.SF_URI, "grayAlpha", MockData.SF_PREFIX);

    private static final QName PALETTED = new QName(MockData.SF_URI, "paletted", MockData.SF_PREFIX);

    @Test
    public void testUnsupportedFormat() throws Exception {
        // MrSID format is not among the default ones
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&Format=image/x-mrsid"));
        checkOws20Exception(response, 400, "InvalidParameterValue", "format");
    }

    @Test
    public void testGetCoverageJP2K() throws Exception {
        Assume.assumeTrue(isFormatSupported("image/jp2"));
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&Format=image/jp2"));
        Assert.assertEquals("image/jp2", response.getContentType());
    }

    @Test
    public void testGetCoveragePdfByMimeType() throws Exception {
        Assume.assumeTrue(isFormatSupported("application/pdf"));
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&Format=application/pdf"));
        Assert.assertEquals("application/pdf", response.getContentType());
    }

    @Test
    public void testGrayAlphaGetCoveragePdf() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=") + (getLayerId(GdalWcsTest.GRAYALPHA))) + "&Format=application/pdf"));
        Assert.assertEquals("application/pdf", response.getContentType());
    }

    @Test
    public void testPalettedGetCoveragePdf() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=") + (getLayerId(GdalWcsTest.PALETTED))) + "&Format=application/pdf"));
        Assert.assertEquals("application/pdf", response.getContentType());
    }

    @Test
    public void testGetCoveragePdfByName() throws Exception {
        Assume.assumeTrue(isFormatSupported("application/pdf"));
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&Format=gdal-pdf"));
        // assertEquals("application/pdf", response.getContentType());
        Assert.assertEquals("gdal-pdf", response.getContentType());
    }

    @Test
    public void testGetCoverageArcInfoGrid() throws Exception {
        Assume.assumeTrue(isFormatSupported("application/zip"));
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=DEM&Format=GDAL-ArcInfoGrid"));
        // assertEquals("application/zip", response.getContentType());
        Assert.assertEquals("GDAL-ArcInfoGrid", response.getContentType());
        GdalTestUtil.checkZippedGridData(getBinaryInputStream(response));
    }

    @Test
    public void testGetCoverageXyzGrid() throws Exception {
        Assume.assumeTrue(isFormatSupported("text/plain"));
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=DEM&Format=GDAL-XYZ"));
        // assertEquals("text/plain", response.getContentType());
        Assert.assertEquals("GDAL-XYZ", response.getContentType());
        GdalTestUtil.checkXyzData(getBinaryInputStream(response));
    }
}

