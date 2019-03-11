/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import java.util.List;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.geotools.util.NumberRange;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Testing {@link GMLCoverageResponseDelegate}
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 */
public class GMLGetCoverageKVPTest extends WCSTestSupport {
    private static final double DELTA = 1.0E-6;

    @Test
    public void gmlFormat() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__BlueMarble&format=application%2Fgml%2Bxml"));
        Assert.assertEquals("application/gml+xml", response.getContentType());
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes()));
    }

    @Test
    public void gmlFormatCoverageBandDetails() throws Exception {
        Catalog catalog = getCatalog();
        CoverageInfo c = catalog.getCoverageByName("wcs", "BlueMarble");
        List<CoverageDimensionInfo> dimensions = c.getDimensions();
        CoverageDimensionInfo dimension = dimensions.get(0);
        Assert.assertEquals("RED_BAND", dimension.getName());
        NumberRange range = dimension.getRange();
        Assert.assertEquals(Double.NEGATIVE_INFINITY, range.getMinimum(), GMLGetCoverageKVPTest.DELTA);
        Assert.assertEquals(Double.POSITIVE_INFINITY, range.getMaximum(), GMLGetCoverageKVPTest.DELTA);
        Assert.assertEquals("GridSampleDimension[-Infinity,Infinity]", dimension.getDescription());
        List<Double> nullValues = dimension.getNullValues();
        Assert.assertEquals(0, nullValues.size());
        Assert.assertEquals("W.m-2.Sr-1", dimension.getUnit());
        int i = 1;
        for (CoverageDimensionInfo dimensionInfo : dimensions) {
            // Updating dimension properties
            dimensionInfo.getNullValues().add((-999.0));
            dimensionInfo.setDescription("GridSampleDimension[-100.0,1000.0]");
            dimensionInfo.setUnit("m");
            dimensionInfo.setRange(NumberRange.create((-100), 1000));
            dimensionInfo.setName(("Band" + (i++)));
        }
        catalog.save(c);
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=wcs__BlueMarble&format=application%2Fgml%2Bxml"));
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes()));
        String name = WCSTestSupport.xpath.evaluate("//swe:field/@name", dom);
        Assert.assertEquals("Band1", name);
        String interval = WCSTestSupport.xpath.evaluate("//swe:interval", dom);
        Assert.assertEquals("-100 1000", interval);
        String unit = WCSTestSupport.xpath.evaluate("//swe:uom/@code", dom);
        Assert.assertEquals("m", unit);
        String noData = WCSTestSupport.xpath.evaluate("//swe:nilValue", dom);
        Assert.assertEquals("-999.0", noData);
    }
}

