/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import java.util.Map;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import net.opengis.wcs20.GetCoverageType;
import net.opengis.wcs20.InterpolationType;
import net.opengis.wcs20.RangeItemType;
import net.opengis.wcs20.RangeSubsetType;
import net.opengis.wcs20.ScaleAxisByFactorType;
import net.opengis.wcs20.ScaleAxisType;
import net.opengis.wcs20.ScaleByFactorType;
import net.opengis.wcs20.ScaleToExtentType;
import net.opengis.wcs20.ScaleToSizeType;
import net.opengis.wcs20.ScalingType;
import net.opengis.wcs20.TargetAxisExtentType;
import net.opengis.wcs20.TargetAxisSizeType;
import org.eclipse.emf.common.util.EList;
import org.geoserver.data.test.MockData;
import org.geoserver.wcs2_0.WCS20Const;
import org.geotools.wcs.v2_0.RangeSubset;
import org.geotools.wcs.v2_0.Scaling;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GetCoverageKvpTest extends WCSKVPTestSupport {
    private static final QName RAIN = new QName(MockData.SF_URI, "rain", MockData.SF_PREFIX);

    @Test
    public void testParseBasic() throws Exception {
        GetCoverageType gc = parse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=theCoverage");
        TestCase.assertEquals("theCoverage", gc.getCoverageId());
    }

    @Test
    public void testGetCoverageNoWs() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&Format=image/tiff"));
        TestCase.assertEquals("image/tiff", response.getContentType());
    }

    @Test
    public void testGetCoverageNativeFormat() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=sf__rain"));
        // we got back an ArcGrid response
        TestCase.assertEquals("text/plain", response.getContentType());
    }

    @Test
    public void testNotExistent() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=NotThere&&Format=image/tiff"));
        checkOws20Exception(response, 404, "NoSuchCoverage", "coverageId");
    }

    @Test
    public void testGetCoverageLocalWs() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wcs/wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=BlueMarble&&Format=image/tiff"));
        TestCase.assertEquals("image/tiff", response.getContentType());
    }

    @Test
    public void testExtensionScaleFactor() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&scaleFactor=2"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        ScalingType scaling = ((ScalingType) (extensions.get(((Scaling.NAMESPACE) + ":Scaling"))));
        ScaleByFactorType sbf = scaling.getScaleByFactor();
        TestCase.assertEquals(2.0, sbf.getScaleFactor(), 0.0);
    }

    @Test
    public void testExtensionScaleAxes() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + ("&coverageId=theCoverage&scaleaxes=http://www.opengis.net/def/axis/OGC/1/i(3.5)," + "http://www.opengis.net/def/axis/OGC/1/j(5.0),http://www.opengis.net/def/axis/OGC/1/k(2.0)")));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        ScalingType scaling = ((ScalingType) (extensions.get(((Scaling.NAMESPACE) + ":Scaling"))));
        ScaleAxisByFactorType sax = scaling.getScaleAxesByFactor();
        EList<ScaleAxisType> saxes = sax.getScaleAxis();
        TestCase.assertEquals(3, saxes.size());
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/i", saxes.get(0).getAxis());
        TestCase.assertEquals(3.5, saxes.get(0).getScaleFactor(), 0.0);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/j", saxes.get(1).getAxis());
        TestCase.assertEquals(5.0, saxes.get(1).getScaleFactor(), 0.0);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/k", saxes.get(2).getAxis());
        TestCase.assertEquals(2.0, saxes.get(2).getScaleFactor(), 0.0);
    }

    @Test
    public void testExtensionScaleSize() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + ("&coverageId=theCoverage&scalesize=http://www.opengis.net/def/axis/OGC/1/i(1000)," + "http://www.opengis.net/def/axis/OGC/1/j(1000),http://www.opengis.net/def/axis/OGC/1/k(10)")));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        ScalingType scaling = ((ScalingType) (extensions.get(((Scaling.NAMESPACE) + ":Scaling"))));
        ScaleToSizeType sts = scaling.getScaleToSize();
        EList<TargetAxisSizeType> scaleAxes = sts.getTargetAxisSize();
        TestCase.assertEquals(3, scaleAxes.size());
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/i", scaleAxes.get(0).getAxis());
        TestCase.assertEquals(1000.0, scaleAxes.get(0).getTargetSize(), 0.0);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/j", scaleAxes.get(1).getAxis());
        TestCase.assertEquals(1000.0, scaleAxes.get(1).getTargetSize(), 0.0);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/k", scaleAxes.get(2).getAxis());
        TestCase.assertEquals(10.0, scaleAxes.get(2).getTargetSize(), 0.0);
    }

    @Test
    public void testExtensionScaleExtent() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&scaleextent=http://www.opengis.net/def/axis/OGC/1/i(10,20),http://www.opengis.net/def/axis/OGC/1/j(20,30)"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        ScalingType scaling = ((ScalingType) (extensions.get(((Scaling.NAMESPACE) + ":Scaling"))));
        ScaleToExtentType ste = scaling.getScaleToExtent();
        TestCase.assertEquals(2, ste.getTargetAxisExtent().size());
        TargetAxisExtentType tax = ste.getTargetAxisExtent().get(0);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/i", tax.getAxis());
        TestCase.assertEquals(10.0, tax.getLow(), 0.0);
        TestCase.assertEquals(20.0, tax.getHigh(), 0.0);
        tax = ste.getTargetAxisExtent().get(1);
        TestCase.assertEquals("http://www.opengis.net/def/axis/OGC/1/j", tax.getAxis());
        TestCase.assertEquals(20.0, tax.getLow(), 0.0);
        TestCase.assertEquals(30.0, tax.getHigh(), 0.0);
    }

    @Test
    public void testExtensionRangeSubset() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&rangesubset=band01,band03:band05,band10,band19:band21"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        RangeSubsetType rangeSubset = ((RangeSubsetType) (extensions.get(((RangeSubset.NAMESPACE) + ":RangeSubset"))));
        EList<RangeItemType> items = rangeSubset.getRangeItems();
        TestCase.assertEquals(4, items.size());
        RangeItemType i1 = items.get(0);
        TestCase.assertEquals("band01", i1.getRangeComponent());
        RangeItemType i2 = items.get(1);
        TestCase.assertEquals("band03", i2.getRangeInterval().getStartComponent());
        TestCase.assertEquals("band05", i2.getRangeInterval().getEndComponent());
        RangeItemType i3 = items.get(2);
        TestCase.assertEquals("band10", i3.getRangeComponent());
        RangeItemType i4 = items.get(3);
        TestCase.assertEquals("band19", i4.getRangeInterval().getStartComponent());
        TestCase.assertEquals("band21", i4.getRangeInterval().getEndComponent());
    }

    @Test
    public void testExtensionCRS() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&SUBSETTINGCRS=http://www.opengis.net/def/crs/EPSG/0/4326&outputcrs=http://www.opengis.net/def/crs/EPSG/0/32632"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(2, extensions.size());
        TestCase.assertEquals("http://www.opengis.net/def/crs/EPSG/0/4326", extensions.get("http://www.opengis.net/wcs/service-extension/crs/1.0:subsettingCrs"));
        TestCase.assertEquals("http://www.opengis.net/def/crs/EPSG/0/32632", extensions.get("http://www.opengis.net/wcs/service-extension/crs/1.0:outputCrs"));
    }

    @Test
    public void testExtensionInterpolationLinear() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&interpolation=http://www.opengis.net/def/interpolation/OGC/1/linear"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        InterpolationType interp = ((InterpolationType) (extensions.get("http://www.opengis.net/WCS_service-extension_interpolation/1.0:Interpolation")));
        TestCase.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/linear", interp.getInterpolationMethod().getInterpolationMethod());
    }

    @Test
    public void testExtensionInterpolationMixed() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&interpolation=http://www.opengis.net/def/interpolation/OGC/1/linear"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        InterpolationType interp = ((InterpolationType) (extensions.get("http://www.opengis.net/WCS_service-extension_interpolation/1.0:Interpolation")));
        TestCase.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/linear", interp.getInterpolationMethod().getInterpolationMethod());
    }

    @Test
    public void testExtensionOverview() throws Exception {
        GetCoverageType gc = parse(("wcs?request=GetCoverage&service=WCS&version=2.0.1" + "&coverageId=theCoverage&overviewPolicy=QUALITY"));
        Map<String, Object> extensions = getExtensionsMap(gc);
        TestCase.assertEquals(1, extensions.size());
        String overviewPolicy = ((String) (extensions.get((((WCS20Const.OVERVIEW_POLICY_EXTENSION_NAMESPACE) + ":") + (WCS20Const.OVERVIEW_POLICY_EXTENSION)))));
        TestCase.assertEquals(overviewPolicy, "QUALITY");
    }

    @Test
    public void testGetMissingCoverage() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=notThereBaby");
        checkOws20Exception(response, 404, "NoSuchCoverage", "coverageId");
    }

    @Test
    public void testCqlFilterRed() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=sf__mosaic&CQL_FILTER=location like 'red%25'");
        assertOriginPixelColor(response, new int[]{ 255, 0, 0 });
    }

    @Test
    public void testCqlFilterGreen() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=sf__mosaic&CQL_FILTER=location like 'green%25'");
        assertOriginPixelColor(response, new int[]{ 0, 255, 0 });
    }

    @Test
    public void testSortByLocationAscending() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=sf__mosaic&sortBy=location");
        // green is the lowest, lexicographically
        assertOriginPixelColor(response, new int[]{ 0, 255, 0 });
    }

    @Test
    public void testSortByLocationDescending() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wcs?request=GetCoverage&service=WCS&version=2.0.1&coverageId=sf__mosaic&sortBy=location D");
        // yellow is the highest, lexicographically
        assertOriginPixelColor(response, new int[]{ 255, 255, 0 });
    }
}

