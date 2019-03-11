/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import DimensionPresentation.CONTINUOUS_INTERVAL;
import DimensionPresentation.DISCRETE_INTERVAL;
import DimensionPresentation.LIST;
import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.impl.CoverageDimensionImpl;
import org.geoserver.data.test.MockData;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DescribeCoverageTest extends WCSTestSupport {
    protected static final String DESCRIBE_URL = ("wcs?service=WCS&version=" + (WCSTestSupport.VERSION)) + "&request=DescribeCoverage";

    private static final QName MOSAIC = new QName(MockData.SF_URI, "rasterFilter", MockData.SF_PREFIX);

    private static final QName RAIN = new QName(MockData.SF_URI, "rain", MockData.SF_PREFIX);

    protected static QName WATTEMP = new QName(MockData.SF_URI, "watertemp", MockData.SF_PREFIX);

    protected static QName TIMERANGES = new QName(MockData.SF_URI, "timeranges", MockData.SF_PREFIX);

    protected static QName MULTIDIM = new QName(MockData.SF_URI, "multidim", MockData.SF_PREFIX);

    protected static QName PK50095 = new QName(MockData.SF_URI, "pk50095", MockData.SF_PREFIX);

    @Test
    public void testBasicKVP() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__BlueMarble"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testProjectectedKVP() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__utm11"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("E N", "//gml:boundedBy/gml:Envelope/@axisLabels", dom);
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testCustomUnit() throws Exception {
        CoverageInfo ciRain = getCatalog().getCoverageByName(getLayerId(DescribeCoverageTest.RAIN));
        ciRain.getDimensions().get(0).setUnit("mm");
        getCatalog().save(ciRain);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__rain"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription/gmlcov:rangeType/swe:DataRecord/swe:field)", dom);
        assertXpathEvaluatesTo("rain", "//wcs:CoverageDescription/gmlcov:rangeType//swe:DataRecord/swe:field/@name", dom);
        assertXpathEvaluatesTo("mm", "//wcs:CoverageDescription/gmlcov:rangeType/swe:DataRecord/swe:field/swe:Quantity/swe:uom/@code", dom);
        assertXpathEvaluatesTo("text/plain", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testAxisOrderUtm() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__pk50095"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("347660.5162105911 5191763.949937257", "//gml:boundedBy/gml:Envelope/gml:lowerCorner", dom);
        assertXpathEvaluatesTo("353440.1129425911 5196950.767517257", "//gml:boundedBy/gml:Envelope/gml:upperCorner", dom);
        assertXpathEvaluatesTo("+1 +2", "//gml:coverageFunction/gml:GridFunction/gml:sequenceRule/@axisOrder", dom);
        assertXpathEvaluatesTo("347671.1015525911 5196940.182175256", "//gml:domainSet/gml:RectifiedGrid/gml:origin/gml:Point/gml:pos", dom);
    }

    @Test
    public void testCustomNullValue() throws Exception {
        CoverageInfo ciRain = getCatalog().getCoverageByName(getLayerId(DescribeCoverageTest.RAIN));
        CoverageDimensionImpl dimension = ((CoverageDimensionImpl) (ciRain.getDimensions().get(0)));
        List<Double> nullValues = new ArrayList<Double>();
        nullValues.add((-999.9));
        dimension.setNullValues(nullValues);
        getCatalog().save(ciRain);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__rain"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription/gmlcov:rangeType/swe:DataRecord/swe:field)", dom);
        assertXpathEvaluatesTo("rain", "//wcs:CoverageDescription/gmlcov:rangeType//swe:DataRecord/swe:field/@name", dom);
        assertXpathEvaluatesTo("-999.9", "//wcs:CoverageDescription/gmlcov:rangeType/swe:DataRecord/swe:field/swe:Quantity/swe:nilValues/swe:NilValues/swe:nilValue", dom);
    }

    @Test
    public void testMultiBandKVP() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__multiband"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultiBandKVPNoWs() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=multiband"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultiBandKVPLocalWs() throws Exception {
        Document dom = getAsDOM((("wcs/" + (DescribeCoverageTest.DESCRIBE_URL)) + "&coverageId=multiband"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultipleCoverages() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__multiband,wcs__BlueMarble"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("2", "count(//wcs:CoverageDescription)", dom);
        assertXpathEvaluatesTo("wcs__multiband", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]/@gml:id", dom);
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription[1]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
        assertXpathEvaluatesTo("wcs__BlueMarble", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]/@gml:id", dom);
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription[2]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultipleCoveragesNoWs() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=multiband,wcs__BlueMarble"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("2", "count(//wcs:CoverageDescription)", dom);
        assertXpathEvaluatesTo("wcs__multiband", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]/@gml:id", dom);
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription[1]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
        assertXpathEvaluatesTo("wcs__BlueMarble", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]/@gml:id", dom);
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription[2]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultipleCoveragesLocalWs() throws Exception {
        Document dom = getAsDOM((("wcs/" + (DescribeCoverageTest.DESCRIBE_URL)) + "&coverageId=multiband,BlueMarble"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("2", "count(//wcs:CoverageDescription)", dom);
        assertXpathEvaluatesTo("wcs__multiband", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]/@gml:id", dom);
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription[1]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
        assertXpathEvaluatesTo("wcs__BlueMarble", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]/@gml:id", dom);
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription[2]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[2]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testMultipleCoveragesOneNotExists() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__multiband,wcs__IAmNotThere"));
        checkOws20Exception(response, 404, "NoSuchCoverage", "coverageId");
    }

    @Test
    public void testNativeFormatMosaic() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__rasterFilter"));
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription)", dom);
        assertXpathEvaluatesTo("sf__rasterFilter", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]/@gml:id", dom);
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription[1]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void gridCellCenterEnforce() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=wcs__BlueMarble"));
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
        // enforce pixel center
        assertXpathEvaluatesTo("-43.0020833333312 146.5020833333281", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//gml:domainSet//gml:RectifiedGrid//gml:origin//gml:Point//gml:pos", dom);
    }

    @Test
    public void testNativeFormatArcGrid() throws Exception {
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__rain"));
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription)", dom);
        assertXpathEvaluatesTo("sf__rain", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]/@gml:id", dom);
        assertXpathEvaluatesTo("1", "count(//wcs:CoverageDescription[1]//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("text/plain", "//wcs:CoverageDescriptions/wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }

    @Test
    public void testDescribeTimeList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), TIME, LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__watertemp"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        checkWaterTempTimeEnvelope(dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant)", dom);
        assertXpathEvaluatesTo("sf__watertemp_td_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[1]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[1]/gml:timePosition", dom);
        assertXpathEvaluatesTo("sf__watertemp_td_1", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[2]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[2]/gml:timePosition", dom);
    }

    @Test
    public void testDescribeTimeContinousInterval() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), TIME, CONTINUOUS_INTERVAL, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__watertemp"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        checkWaterTempTimeEnvelope(dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("1", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod)", dom);
        assertXpathEvaluatesTo("sf__watertemp_tp_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:endPosition", dom);
        assertXpathEvaluatesTo("0", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:timeInterval)", dom);
    }

    @Test
    public void testDescribeTimeDiscreteInterval() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), TIME, DISCRETE_INTERVAL, (((1000 * 60) * 60) * 24.0));
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__watertemp"));
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        checkWaterTempTimeEnvelope(dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("1", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod)", dom);
        assertXpathEvaluatesTo("sf__watertemp_tp_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:endPosition", dom);
        assertXpathEvaluatesTo("1", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:timeInterval", dom);
        assertXpathEvaluatesTo("day", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/gml:timeInterval/@unit", dom);
    }

    @Test
    public void testDescribeTimeRangeList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.TIMERANGES), TIME, LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__timeranges"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        // check the envelope with time
        assertXpathEvaluatesTo("1", "count(//gml:boundedBy/gml:EnvelopeWithTimePeriod)", dom);
        assertXpathEvaluatesTo("Lat Long time", "//gml:boundedBy/gml:EnvelopeWithTimePeriod/@axisLabels", dom);
        assertXpathEvaluatesTo("Deg Deg s", "//gml:boundedBy/gml:EnvelopeWithTimePeriod/@uomLabels", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gml:boundedBy/gml:EnvelopeWithTimePeriod/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-07T00:00:00.000Z", "//gml:boundedBy/gml:EnvelopeWithTimePeriod/gml:endPosition", dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod)", dom);
        assertXpathEvaluatesTo("sf__timeranges_td_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-04T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/gml:endPosition", dom);
        assertXpathEvaluatesTo("sf__timeranges_td_1", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-11-05T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-07T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/gml:endPosition", dom);
    }

    @Test
    public void testDescribeElevationDiscreteInterval() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.TIMERANGES), ELEVATION, DISCRETE_INTERVAL, 50.0, "m");
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__timeranges"));
        // print(dom);
        checkElevationRangesEnvelope(dom);
        // check that metadata contains a list of elevations
        assertXpathEvaluatesTo("1", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range)", dom);
        assertXpathEvaluatesTo("20.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:start", dom);
        assertXpathEvaluatesTo("150.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:end", dom);
        assertXpathEvaluatesTo("50.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:Interval", dom);
        assertXpathEvaluatesTo("m", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:Interval/@unit", dom);
    }

    @Test
    public void testDescribeElevationContinuousInterval() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.TIMERANGES), ELEVATION, CONTINUOUS_INTERVAL, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__timeranges"));
        // print(dom);
        checkElevationRangesEnvelope(dom);
        // check that metadata contains elevation range
        assertXpathEvaluatesTo("1", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range)", dom);
        assertXpathEvaluatesTo("20.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:start", dom);
        assertXpathEvaluatesTo("150.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range/wcsgs:end", dom);
    }

    @Test
    public void testDescribeElevationValuesList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), ELEVATION, LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__watertemp"));
        // print(dom);
        checkWaterTempElevationEnvelope(dom);
        // check that metadata contains a list of elevations
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue)", dom);
        assertXpathEvaluatesTo("0.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue[1]", dom);
        assertXpathEvaluatesTo("100.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue[2]", dom);
    }

    @Test
    public void testDescribeElevationRangeList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.TIMERANGES), ELEVATION, LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__timeranges"));
        // print(dom);
        checkElevationRangesEnvelope(dom);
        // check that metadata contains a list of elevations
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range)", dom);
        assertXpathEvaluatesTo("20.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[1]/wcsgs:start", dom);
        assertXpathEvaluatesTo("99.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[1]/wcsgs:end", dom);
        assertXpathEvaluatesTo("100.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[2]/wcsgs:start", dom);
        assertXpathEvaluatesTo("150.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[2]/wcsgs:end", dom);
    }

    @Test
    public void testDescribeTimeElevationList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), TIME, LIST, null);
        setupRasterDimension(getLayerId(DescribeCoverageTest.WATTEMP), ELEVATION, LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__watertemp"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        checkWaterTempTimeElevationEnvelope(dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant)", dom);
        assertXpathEvaluatesTo("sf__watertemp_td_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[1]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[1]/gml:timePosition", dom);
        assertXpathEvaluatesTo("sf__watertemp_td_1", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[2]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimeInstant[2]/gml:timePosition", dom);
        // check that metadata contains a list of elevations
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue)", dom);
        assertXpathEvaluatesTo("0.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue[1]", dom);
        assertXpathEvaluatesTo("100.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:SingleValue[2]", dom);
    }

    @Test
    public void testDescribeCustomDimensionsList() throws Exception {
        setupRasterDimension(getLayerId(DescribeCoverageTest.MULTIDIM), TIME, LIST, null);
        setupRasterDimension(getLayerId(DescribeCoverageTest.MULTIDIM), ELEVATION, LIST, null);
        setupRasterDimension(getLayerId(DescribeCoverageTest.MULTIDIM), ((ResourceInfo.CUSTOM_DIMENSION_PREFIX) + "WAVELENGTH"), LIST, null);
        setupRasterDimension(getLayerId(DescribeCoverageTest.MULTIDIM), ((ResourceInfo.CUSTOM_DIMENSION_PREFIX) + "DATE"), LIST, null);
        Document dom = getAsDOM(((DescribeCoverageTest.DESCRIBE_URL) + "&coverageId=sf__multidim"));
        // print(dom);
        checkTimeElevationRangesEnvelope(dom);
        // check that metadata contains a list of times
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod)", dom);
        assertXpathEvaluatesTo("sf__multidim_td_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-04T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[1]/gml:endPosition", dom);
        assertXpathEvaluatesTo("sf__multidim_td_1", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/@gml:id", dom);
        assertXpathEvaluatesTo("2008-11-05T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/gml:beginPosition", dom);
        assertXpathEvaluatesTo("2008-11-07T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod[2]/gml:endPosition", dom);
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range)", dom);
        assertXpathEvaluatesTo("20.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[1]/wcsgs:start", dom);
        assertXpathEvaluatesTo("99.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[1]/wcsgs:end", dom);
        assertXpathEvaluatesTo("100.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[2]/wcsgs:start", dom);
        assertXpathEvaluatesTo("150.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:ElevationDomain/wcsgs:Range[2]/wcsgs:end", dom);
        // check the additional domains
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain)", dom);
        // Check the date domain
        assertXpathEvaluatesTo("DATE", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/@name", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/@default", dom);
        assertXpathEvaluatesTo("3", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/gml:TimeInstant)", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/gml:TimeInstant[1]/gml:timePosition", dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/gml:TimeInstant[2]/gml:timePosition", dom);
        assertXpathEvaluatesTo("2008-11-05T00:00:00.000Z", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[1]/gml:TimeInstant[3]/gml:timePosition", dom);
        // check the waveLength range domain
        assertXpathEvaluatesTo("WAVELENGTH", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/@name", dom);
        assertXpathEvaluatesTo("12", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/@default", dom);
        assertXpathEvaluatesTo("2", "count(//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/wcsgs:Range)", dom);
        assertXpathEvaluatesTo("12.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/wcsgs:Range[1]/wcsgs:start", dom);
        assertXpathEvaluatesTo("24.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/wcsgs:Range[1]/wcsgs:end", dom);
        assertXpathEvaluatesTo("25.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/wcsgs:Range[2]/wcsgs:start", dom);
        assertXpathEvaluatesTo("80.0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:DimensionDomain[2]/wcsgs:Range[2]/wcsgs:end", dom);
    }
}

