/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import DimensionPresentation.LIST;
import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import ResourceInfo.TIME_UNIT;
import Strategy.FIXED;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.geoserver.wms.featureinfo.VectorRenderingLayerIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class DimensionsVectorGetFeatureInfoTest extends WMSDimensionsTestSupport {
    String baseFeatureInfo;

    XpathEngine xpath;

    String baseFeatureInfoStacked;

    @Test
    public void testNoDimension() throws Exception {
        Assert.assertEquals("TimeElevation.0", getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testElevationDefault() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        // we should get only one square
        Assert.assertEquals("TimeElevation.0", getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testElevationSingle() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        String base = (baseFeatureInfo) + "&elevation=1.0";
        // we should get only one square
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertNull(getFeatureAt(base, 60, 30));
    }

    @Test
    public void testElevationListMulti() throws Exception {
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        String base = (baseFeatureInfo) + "&elevation=1.0,3.0";
        // we should get second and last
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testElevationListExtra() throws Exception {
        // adding a extra elevation that is simply not there, should not break
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        String base = (baseFeatureInfo) + "&elevation=1.0,3.0,5.0";
        // we should get second and last
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testElevationInterval() throws Exception {
        // adding a extra elevation that is simply not there, should not break
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        String base = (baseFeatureInfo) + "&elevation=1.0/3.0";
        // we should get all but the first
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testElevationIntervalResolution() throws Exception {
        // adding a extra elevation that is simply not there, should not break
        setupVectorDimension(ELEVATION, "elevation", LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        String base = (baseFeatureInfo) + "&elevation=1.0/4.0/2.0";
        // we should get only one square
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testTimeDefault() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        // we should get only one square
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testTimeCurrent() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        String base = (baseFeatureInfo) + "&time=CURRENT";
        // we should get only one square
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertNull(getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testTimeSingle() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        String base = (baseFeatureInfo) + "&time=2011-05-02";
        // we should get the second
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertNull(getFeatureAt(base, 60, 30));
    }

    @Test
    public void testTimeSingleNoNearestClose() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        String base = (baseFeatureInfo) + "&time=2011-05-02T012:00:00Z";
        // we should get none, as there is no nearest treatment
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertNull(getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertNull(getFeatureAt(base, 60, 30));
    }

    @Test
    public void testTimeSingleNearestClose() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, TIME_UNIT, null);
        setupNearestMatch(V_TIME_ELEVATION, TIME, true);
        String base = (baseFeatureInfo) + "&time=2011-05-02T01:00:00Z";
        // we should get the second, it's the nearest
        Assert.assertNull(getFeatureAt(base, 20, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-02T00:00:00.000Z");
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-02T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 20, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-02T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 60, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-02T00:00:00.000Z");
    }

    @Test
    public void testTimeSingleNearestAfter() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, TIME_UNIT, null);
        setupNearestMatch(V_TIME_ELEVATION, TIME, true);
        String base = (baseFeatureInfo) + "&time=2013-05-02";
        // we should get the last, it's the nearest
        Assert.assertNull(getFeatureAt(base, 20, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-04T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 60, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-04T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 20, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-04T00:00:00.000Z");
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-04T00:00:00.000Z");
    }

    @Test
    public void testTimeSingleNearestBefore() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, TIME_UNIT, null);
        setupNearestMatch(V_TIME_ELEVATION, TIME, true);
        String base = (baseFeatureInfo) + "&time=1190-05-02";
        // we should get the first, it's the nearest
        Assert.assertEquals("TimeElevation.0", getFeatureAt(base, 20, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-01T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 60, 10));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-01T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 20, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-01T00:00:00.000Z");
        Assert.assertNull(getFeatureAt(base, 60, 30));
        assertWarningCount(1);
        assertNearestTimeWarning(getLayerId(V_TIME_ELEVATION), "2011-05-01T00:00:00.000Z");
    }

    @Test
    public void testTimeSingleNearestBeforeBasicIdentifier() throws Exception {
        // a test for the old identifier, which someone might still be using for performance
        // purposes
        VectorRenderingLayerIdentifier.RENDERING_FEATUREINFO_ENABLED = false;
        try {
            testTimeSingleNearestBefore();
        } finally {
            VectorRenderingLayerIdentifier.RENDERING_FEATUREINFO_ENABLED = true;
        }
    }

    @Test
    public void testTimeListMulti() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        String base = (baseFeatureInfo) + "&time=2011-05-02,2011-05-04";
        // we should get the second and fourth
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testTimeListExtra() throws Exception {
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        // adding a extra elevation that is simply not there, should not break
        String base = (baseFeatureInfo) + "&time=2011-05-02,2011-05-04,2011-05-10";
        // we should get the second and fourth
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertNull(getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testTimeInterval() throws Exception {
        // adding a extra elevation that is simply not there, should not break
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        String base = (baseFeatureInfo) + "&time=2011-05-02/2011-05-05";
        // last three squares
        Assert.assertNull(getFeatureAt(base, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(base, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(base, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(base, 60, 30));
    }

    @Test
    public void testElevationDefaultAsRange() throws Exception {
        // setup a default
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("1/3");
        setupResourceDimensionDefaultValue(V_TIME_ELEVATION, ELEVATION, defaultValueSetting, "elevation");
        // the last three show up, the first does not
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testTimeDefaultAsRange() throws Exception {
        // setup a default
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("2011-05-02/2011-05-03");
        setupResourceDimensionDefaultValue(V_TIME_ELEVATION, TIME, defaultValueSetting, "time");
        // the last three show up, the first does not
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testSortTimeElevationAscending() throws Exception {
        // check consistency with the visual output of GetMap
        Assert.assertEquals("TimeElevationStacked.3", getFeatureAt(((baseFeatureInfoStacked) + "&sortBy=time,elevation"), 20, 10, "sf:TimeElevationStacked"));
    }

    @Test
    public void testSortTimeElevationDescending() throws Exception {
        // check consistency with the visual output of GetMap
        Assert.assertEquals("TimeElevationStacked.0", getFeatureAt(((baseFeatureInfoStacked) + "&sortBy=time D,elevation D"), 20, 10, "sf:TimeElevationStacked"));
    }

    @Test
    public void testSortTimeElevationAscendingLegacyIdentifier() throws Exception {
        VectorRenderingLayerIdentifier.RENDERING_FEATUREINFO_ENABLED = false;
        Assert.assertEquals("TimeElevationStacked.3", getFeatureAt(((baseFeatureInfoStacked) + "&sortBy=time,elevation"), 20, 10, "sf:TimeElevationStacked"));
    }

    @Test
    public void testSortTimeElevationDescendingLegacyIdentifier() throws Exception {
        VectorRenderingLayerIdentifier.RENDERING_FEATUREINFO_ENABLED = false;
        Assert.assertEquals("TimeElevationStacked.0", getFeatureAt(((baseFeatureInfoStacked) + "&sortBy=time D,elevation D"), 20, 10, "sf:TimeElevationStacked"));
    }
}

