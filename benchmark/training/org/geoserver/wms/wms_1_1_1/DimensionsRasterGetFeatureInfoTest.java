/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import ResourceInfo.ELEVATION;
import ResourceInfo.TIME;
import Strategy.FIXED;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.wms.NearestMatchFinder;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class DimensionsRasterGetFeatureInfoTest extends WMSDimensionsTestSupport {
    static final String BASE_URL_NO_COUNT = "wms?service=WMS&version=1.1.0&request=GetFeatureInfo" + (("&layers=watertemp&styles=&bbox=0.237,40.562,14.593,44.558&width=200&height=80" + "&srs=EPSG:4326&format=image/png") + "&query_layers=watertemp");

    static final String BASE_URL = (DimensionsRasterGetFeatureInfoTest.BASE_URL_NO_COUNT) + "&feature_count=50";

    static final String BASE_URL_ONE = (DimensionsRasterGetFeatureInfoTest.BASE_URL_NO_COUNT) + "&feature_count=1";

    static final double EPS = 0.001;

    private XpathEngine xpath;

    @Test
    public void testDefaultValues() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        // this one should be medium
        Assert.assertEquals(14.51, getFeatureAt(DimensionsRasterGetFeatureInfoTest.BASE_URL, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // this one hot
        Assert.assertEquals(19.15, getFeatureAt(DimensionsRasterGetFeatureInfoTest.BASE_URL, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testSortTime() throws Exception {
        // do not setup time, only elevation, and sort by time
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        // this one should be medium
        Assert.assertEquals(14.51, getFeatureAt(((DimensionsRasterGetFeatureInfoTest.BASE_URL_ONE) + "&sortBy=ingestion D"), 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // this one hot
        Assert.assertEquals(19.15, getFeatureAt(((DimensionsRasterGetFeatureInfoTest.BASE_URL_ONE) + "&sortBy=ingestion D"), 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testSortTimeElevation() throws Exception {
        // do not setup anything, only sort
        // this one should be medium
        Assert.assertEquals(14.51, getFeatureAt(((DimensionsRasterGetFeatureInfoTest.BASE_URL_ONE) + "&sortBy=ingestion D,elevation"), 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // this one hot
        Assert.assertEquals(19.15, getFeatureAt(((DimensionsRasterGetFeatureInfoTest.BASE_URL_ONE) + "&sortBy=ingestion D,elevation"), 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testElevation() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        // this one should be the no-data
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&elevation=100";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // and this one should be medium
        Assert.assertEquals(14.492, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testTime() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2008-10-31T00:00:00.000Z";
        // should be similar to the default, but with different shades of color
        Assert.assertEquals(14.592, getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        Assert.assertEquals(19.371, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testTimeNoNearestClose() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2008-10-31T08:00:00.000Z";
        // no match without nearest match support
        Assert.assertNull(getFeatureAt(url, 36, 31, "sf:watertemp"));
        Assert.assertNull(getFeatureAt(url, 68, 72, "sf:watertemp"));
    }

    @Test
    public void testTimeNearestClose() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupNearestMatch(WMSDimensionsTestSupport.WATTEMP, TIME, true);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2008-10-31T08:00:00.000Z";
        // same as testTime
        Assert.assertEquals(14.592, getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-10-31T00:00:00.000Z");
        Assert.assertEquals(19.371, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-10-31T00:00:00.000Z");
    }

    @Test
    public void testTimeNearestBefore() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupNearestMatch(WMSDimensionsTestSupport.WATTEMP, TIME, true);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=1990-10-31";
        // same as testTime
        Assert.assertEquals(14.592, getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-10-31T00:00:00.000Z");
        Assert.assertEquals(19.371, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-10-31T00:00:00.000Z");
    }

    @Test
    public void testTimeNearestAfter() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupNearestMatch(WMSDimensionsTestSupport.WATTEMP, TIME, true);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2018-10-31";
        // same as testDefaultValues
        Assert.assertEquals(14.51, getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-11-01T00:00:00.000Z");
        Assert.assertEquals(19.15, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-11-01T00:00:00.000Z");
    }

    @Test
    public void testTimeNearestCloseNonStructured() throws Exception {
        NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = false;
        try {
            testTimeNearestClose();
        } finally {
            NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = true;
        }
    }

    @Test
    public void testTimeNearestBeforeNonStructured() throws Exception {
        NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = false;
        try {
            testTimeNearestBefore();
        } finally {
            NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = true;
        }
    }

    @Test
    public void testTimeNearestAfterNonStructured() throws Exception {
        NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = false;
        try {
            testTimeNearestAfter();
        } finally {
            NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = true;
        }
    }

    @Test
    public void testTimeElevation() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2008-10-31T00:00:00.000Z&elevation=100";
        // this one should be the no-data
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // and this one should be medium
        Assert.assertEquals(14.134, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testTimeRange() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, TIME, DimensionPresentation.LIST, null, null, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "wavelength", DimensionPresentation.LIST, null, null, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "date", DimensionPresentation.LIST, null, null, null);
        String layer = getLayerId(WMSDimensionsTestSupport.TIMERANGES);
        String baseUrl = ((("wms?LAYERS=" + layer) + "&STYLES=temperature&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&SRS=EPSG:4326") + "&BBOX=-0.89131513678082,40.246933882167,15.721292974683,44.873229811941&WIDTH=200&HEIGHT=80&query_layers=") + layer;
        // last range
        String url = baseUrl + "&TIME=2008-11-05T00:00:00.000Z/2008-11-06T12:00:00.000Z";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        Assert.assertEquals(14.782, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        // in the middle hole, no data
        url = baseUrl + "&TIME=2008-11-04T12:00:00.000Z/2008-11-04T16:00:00.000Z";
        Assert.assertNull(getFeatureAt(url, 36, 31, layer));
        // first range
        url = baseUrl + "&TIME=2008-10-31T12:00:00.000Z/2008-10-31T16:00:00.000Z";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        Assert.assertEquals(20.027, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testTimeRangeNearestMatch() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "wavelength", DimensionPresentation.LIST, null, null, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "date", DimensionPresentation.LIST, null, null, null);
        setupNearestMatch(WMSDimensionsTestSupport.TIMERANGES, TIME, true);
        String layer = getLayerId(WMSDimensionsTestSupport.TIMERANGES);
        String baseUrl = ((("wms?LAYERS=" + layer) + "&STYLES=temperature&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&SRS=EPSG:4326") + "&BBOX=-0.89131513678082,40.246933882167,15.721292974683,44.873229811941&WIDTH=200&HEIGHT=80&query_layers=") + layer;
        // after last range, as a range
        String url = baseUrl + "&TIME=2018-11-05/2018-11-06";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-07T00:00:00.000Z");
        Assert.assertEquals(14.782, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-07T00:00:00.000Z");
        // after last range, point in time
        url = baseUrl + "&TIME=2018-11-05";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-07T00:00:00.000Z");
        Assert.assertEquals(14.782, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-07T00:00:00.000Z");
        // in the middle hole, close to latest value
        url = baseUrl + "&TIME=2008-11-04T12:00:00.000Z/2008-11-04T16:00:00.000Z";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-05T00:00:00.000Z");
        Assert.assertEquals(14.782, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-05T00:00:00.000Z");
        // before first range, as a range
        url = baseUrl + "&TIME=2005-10-30/2005-10-31";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-10-31T00:00:00.000Z");
        Assert.assertEquals(20.027, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-10-31T00:00:00.000Z");
        // before first range, as a point
        url = baseUrl + "&TIME=2005-10-30";
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-10-31T00:00:00.000Z");
        Assert.assertEquals(20.027, getFeatureAt(url, 68, 72, layer), DimensionsRasterGetFeatureInfoTest.EPS);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-10-31T00:00:00.000Z");
    }

    @Test
    public void testTimeRangeNearestMatchNonStructured() throws Exception {
        NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = false;
        try {
            testTimeRangeNearestMatch();
        } finally {
            NearestMatchFinder.ENABLE_STRUCTURED_READER_SUPPORT = true;
        }
    }

    @Test
    public void testTimeDefaultAsRange() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        // setup a default
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("2008-10-30T23:00:00.000Z/2008-10-31T01:00:00.000Z");
        setupResourceDimensionDefaultValue(WMSDimensionsTestSupport.WATTEMP, TIME, defaultValueSetting);
        // use the default time range, specify elevation
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&elevation=100";
        // this one should be the no-data
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // and this one should be medium
        Assert.assertEquals(14.134, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testElevationDefaultAsRange() throws Exception {
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, null, null);
        // setup a default
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("99/101");
        setupResourceDimensionDefaultValue(WMSDimensionsTestSupport.WATTEMP, ELEVATION, defaultValueSetting);
        // default elevation, specific time
        String url = (DimensionsRasterGetFeatureInfoTest.BASE_URL) + "&time=2008-10-31T00:00:00.000Z";
        // this one should be the no-data
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // and this one should be medium
        Assert.assertEquals(14.134, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testTimeElevationDefaultAsRange() throws Exception {
        // setup a range default for time
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("2008-10-30T23:00:00.000Z/2008-10-31T01:00:00.000Z");
        setupResourceDimensionDefaultValue(WMSDimensionsTestSupport.WATTEMP, TIME, defaultValueSetting);
        // setup a range default for elevation
        defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("99/101");
        setupResourceDimensionDefaultValue(WMSDimensionsTestSupport.WATTEMP, ELEVATION, defaultValueSetting);
        // default elevation, default time
        String url = DimensionsRasterGetFeatureInfoTest.BASE_URL;
        // this one should be the no-data
        Assert.assertEquals((-30000), getFeatureAt(url, 36, 31, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
        // and this one should be medium
        Assert.assertEquals(14.134, getFeatureAt(url, 68, 72, "sf:watertemp"), DimensionsRasterGetFeatureInfoTest.EPS);
    }

    @Test
    public void testNearestMatchTwoLayers() throws Exception {
        // setup time ranges
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "wavelength", DimensionPresentation.LIST, null, null, null);
        setupRasterDimension(WMSDimensionsTestSupport.TIMERANGES, "date", DimensionPresentation.LIST, null, null, null);
        setupNearestMatch(WMSDimensionsTestSupport.TIMERANGES, TIME, true);
        // setup water temp
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, ELEVATION, DimensionPresentation.LIST, null, WMSDimensionsTestSupport.UNITS, WMSDimensionsTestSupport.UNIT_SYMBOL);
        setupRasterDimension(WMSDimensionsTestSupport.WATTEMP, TIME, DimensionPresentation.LIST, null, ResourceInfo.TIME_UNIT, null);
        setupNearestMatch(WMSDimensionsTestSupport.WATTEMP, TIME, true);
        String layers = ((getLayerId(WMSDimensionsTestSupport.TIMERANGES)) + ",") + (getLayerId(WMSDimensionsTestSupport.WATTEMP));
        String baseUrl = ((((("wms?LAYERS=" + layers) + "&STYLES=,&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo") + "&SRS=EPSG:4326&BBOX=-0.89131513678082,40.246933882167,15.721292974683,44.873229811941") + "&WIDTH=200&HEIGHT=80&query_layers=") + layers) + "&FEATURE_COUNT=50";
        // run time before both (don't care about results, just check the headers)
        String url = baseUrl + "&TIME=2000-01-01";
        getFeatureAt(url, 68, 72, getLayerId(WMSDimensionsTestSupport.TIMERANGES));
        assertWarningCount(2);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-10-31T00:00:00.000Z");
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-10-31T00:00:00.000Z");
        // after both
        url = baseUrl + "&TIME=2100-01-01";
        getFeatureAt(url, 68, 72, getLayerId(WMSDimensionsTestSupport.TIMERANGES));
        assertWarningCount(2);
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.TIMERANGES), "2008-11-07T00:00:00.000Z");
        assertNearestTimeWarning(getLayerId(WMSDimensionsTestSupport.WATTEMP), "2008-11-01T00:00:00.000Z");
    }
}

