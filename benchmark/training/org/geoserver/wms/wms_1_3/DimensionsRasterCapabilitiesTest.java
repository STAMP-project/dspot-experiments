/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import ResourceInfo.TIME;
import Strategy.FIXED;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class DimensionsRasterCapabilitiesTest extends WMSDimensionsTestSupport {
    @Test
    public void testNoDimension() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//wms:Layer[wms:Name='sf:watertemp'])", dom);
        assertXpathEvaluatesTo("0", "count(//wms:Layer/wms:Dimension)", dom);
    }

    @Test
    public void testDefaultElevationUnits() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ResourceInfo.ELEVATION, DimensionPresentation.LIST, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        assertXpathEvaluatesTo(DimensionInfo.ELEVATION_UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(DimensionInfo.ELEVATION_UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
    }

    @Test
    public void testElevationList() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ResourceInfo.ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0,100.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationContinuous() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ResourceInfo.ELEVATION, DimensionPresentation.CONTINUOUS_INTERVAL, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/100.0/0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationDiscreteNoResolution() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ResourceInfo.ELEVATION, DimensionPresentation.DISCRETE_INTERVAL, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/100.0/100.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationDiscreteManualResolution() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ResourceInfo.ELEVATION, DimensionPresentation.DISCRETE_INTERVAL, 10.0, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/100.0/10.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeList() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("", "//wms:Layer/wms:Dimension/@nearestValue", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z,2008-11-01T00:00:00.000Z", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeNearestMatch() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        setupNearestMatch(WMSDimensionsTestSupport.WATTEMP, TIME, true);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("1", "//wms:Layer/wms:Dimension/@nearestValue", dom);
    }

    @Test
    public void testTimeContinuous() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.CONTINUOUS_INTERVAL, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z/2008-11-01T00:00:00.000Z/PT1S", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeResolution() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.DISCRETE_INTERVAL, new Double((((1000 * 60) * 60) * 12)), null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the wms:Dimension
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z/2008-11-01T00:00:00.000Z/PT12H", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testDefaultTimeRangeFixed() throws Exception {
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("P1M/PRESENT");
        setupResourceDimensionDefaultValue(WMSDimensionsTestSupport.WATTEMP, TIME, defaultValueSetting);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        print(dom);
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("P1M/PRESENT", "//wms:Layer/wms:Dimension/@default", dom);
    }
}

