/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import DimensionPresentation.CONTINUOUS_INTERVAL;
import DimensionPresentation.DISCRETE_INTERVAL;
import DimensionPresentation.LIST;
import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import Strategy.FIXED;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.catalog.DimensionPresentation;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class DimensionsVectorCapabilitiesTest extends WMSDimensionsTestSupport {
    @Test
    public void testNoDimension() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        assertXpathEvaluatesTo("1", "count(//wms:Layer[wms:Name='sf:TimeElevation'])", dom);
        assertXpathEvaluatesTo("0", "count(//wms:Layer/wms:Dimension)", dom);
    }

    @Test
    public void testDefaultElevationUnits() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", LIST, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        assertXpathEvaluatesTo(DimensionInfo.ELEVATION_UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(DimensionInfo.ELEVATION_UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
    }

    @Test
    public void testEmptyDataSet() throws Exception {
        for (DimensionPresentation p : DimensionPresentation.values()) {
            setupVectorDimension(V_TIME_ELEVATION_EMPTY.getLocalPart(), TIME, "time", p, null, null, null);
            checkEmptyTimeDimensionAndExtent();
        }
        // clear time metadata
        FeatureTypeInfo info = getCatalog().getFeatureTypeByName(V_TIME_ELEVATION_EMPTY.getLocalPart());
        info.getMetadata().remove(TIME);
        getCatalog().save(info);
        for (DimensionPresentation p : DimensionPresentation.values()) {
            setupVectorDimension(V_TIME_ELEVATION_EMPTY.getLocalPart(), ELEVATION, "elevation", p, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
            checkEmptyElevationDimensionAndExtent();
        }
    }

    @Test
    public void testElevationList() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the extent
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0,1.0,2.0,3.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationContinuous() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", CONTINUOUS_INTERVAL, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/3.0/0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationDiscrerteNoResolution() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", DISCRETE_INTERVAL, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/3.0/1.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testElevationDiscrerteManualResolution() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", DISCRETE_INTERVAL, 2.0, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension/@unitSymbol", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("0.0/3.0/2.0", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeList() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("", "//wms:Layer/wms:Dimension/@nearestValue", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2011-05-01T00:00:00.000Z,2011-05-02T00:00:00.000Z,2011-05-03T00:00:00.000Z,2011-05-04T00:00:00.000Z", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testNearestMatch() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        setupNearestMatch(V_TIME_ELEVATION, TIME, true);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("1", "//wms:Layer/wms:Dimension/@nearestValue", dom);
    }

    @Test
    public void testTimeContinuous() throws Exception {
        setupVectorDimension(TIME, "time", CONTINUOUS_INTERVAL, null, null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2011-05-01T00:00:00.000Z/2011-05-04T00:00:00.000Z/PT1S", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeResolution() throws Exception {
        setupVectorDimension(TIME, "time", DISCRETE_INTERVAL, new Double((((1000 * 60) * 60) * 24)), null, null);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2011-05-01T00:00:00.000Z/2011-05-04T00:00:00.000Z/P1D", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testTimeElevation() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        // check both dimension has been declared
        assertXpathEvaluatesTo("2", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNITS, "//wms:Layer/wms:Dimension[@name='elevation']/@units", dom);
        assertXpathEvaluatesTo(WMSDimensionsTestSupport.UNIT_SYMBOL, "//wms:Layer/wms:Dimension[@name='elevation']/@unitSymbol", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension[@name='time']/@units", dom);
        // check we have the extent for elevation
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension[@name='elevation'])", dom);
        assertXpathEvaluatesTo("0.0", "//wms:Layer/wms:Dimension[@name='elevation']/@default", dom);
        assertXpathEvaluatesTo("0.0,1.0,2.0,3.0", "//wms:Layer/wms:Dimension[@name='elevation']", dom);
        // check we have the extent for time
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension[@name='time'])", dom);
        assertXpathEvaluatesTo(DimensionDefaultValueSetting.TIME_CURRENT, "//wms:Layer/wms:Dimension[@name='time']/@default", dom);
        assertXpathEvaluatesTo("2011-05-01T00:00:00.000Z,2011-05-02T00:00:00.000Z,2011-05-03T00:00:00.000Z,2011-05-04T00:00:00.000Z", "//wms:Layer/wms:Dimension[@name='time']", dom);
    }

    @Test
    public void testDefaultTimeRangeFixed() throws Exception {
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("P1M/PRESENT");
        setupResourceDimensionDefaultValue(V_TIME_ELEVATION, TIME, defaultValueSetting);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("P1M/PRESENT", "//wms:Layer/wms:Dimension/@default", dom);
    }

    @Test
    public void testDefaultElevationRangeFixed() throws Exception {
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("-100/0");
        setupResourceDimensionDefaultValue(V_TIME_ELEVATION, ELEVATION, defaultValueSetting);
        Document dom = dom(get("wms?request=getCapabilities&version=1.3.0"), false);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("elevation", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("-100/0", "//wms:Layer/wms:Dimension/@default", dom);
    }
}

