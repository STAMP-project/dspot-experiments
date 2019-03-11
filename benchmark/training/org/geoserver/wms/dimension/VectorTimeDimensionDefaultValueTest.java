/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import DimensionDefaultValueSetting.TIME_CURRENT;
import Strategy.FIXED;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import java.sql.Date;
import javax.xml.namespace.QName;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.geotools.feature.type.DateUtil;
import org.geotools.util.Range;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the WMS default value support for TIME dimension for vector layers.
 *
 * @author Ilkka Rinne <ilkka.rinne@spatineo.com>
 */
public class VectorTimeDimensionDefaultValueTest extends WMSDimensionsTestSupport {
    static final QName TIME_WITH_START_END = new QName(MockData.SF_URI, "TimeWithStartEnd", MockData.SF_PREFIX);

    WMS wms;

    @Test
    public void testDefaultTimeVectorSelector() throws Exception {
        int fid = 1000;
        // Use default DimensionInfo setup, should return the "current" time:
        setupFeatureTimeDimension(null);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (twoDaysAgo.getTime())));
        // Add some features with timestamps in the future:
        Date dayAfterTomorrow = addFeatureWithTimeDayAfterTomorrow((fid++));
        addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (dayAfterTomorrow.getTime())));
        Date todayMidnight = addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (todayMidnight.getTime())));
    }

    @Test
    public void testExplicitCurrentTimeVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(TIME_CURRENT);
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (twoDaysAgo.getTime())));
        // Add some features with timestamps in the future:
        Date dayAfterTomorrow = addFeatureWithTimeDayAfterTomorrow((fid++));
        addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (dayAfterTomorrow.getTime())));
        Date todayMidnight = addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (todayMidnight.getTime())));
    }

    @Test
    public void testExplicitMinTimeVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date smallest = Date.valueOf("2012-02-11");
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the smallest one", ((d.getTime()) == (smallest.getTime())));
        // Add some features with timestamps in the future:
        addFeatureWithTimeDayAfterTomorrow((fid++));
        addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the smallest one", ((d.getTime()) == (smallest.getTime())));
        addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the smallest one", ((d.getTime()) == (smallest.getTime())));
    }

    @Test
    public void testExplicitMaxTimeVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the biggest one", ((d.getTime()) == (twoDaysAgo.getTime())));
        // Add some features with timestamps in the future:
        addFeatureWithTimeDayAfterTomorrow((fid++));
        Date oneYearFromNow = addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the biggest one", ((d.getTime()) == (oneYearFromNow.getTime())));
        addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the biggest one", ((d.getTime()) == (oneYearFromNow.getTime())));
    }

    @Test
    public void testExplicitFixedTimeVectorSelector() throws Exception {
        int fid = 1000;
        String fixedTimeStr = "2012-06-01T03:00:00.000Z";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue(fixedTimeStr);
        long fixedTime = DateUtil.parseDateTime(fixedTimeStr);
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the fixed one", ((d.getTime()) == fixedTime));
        // Add some features with timestamps in the future:
        addFeatureWithTimeDayAfterTomorrow((fid++));
        addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the fixed one", ((d.getTime()) == fixedTime));
        addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the fixed one", ((d.getTime()) == fixedTime));
    }

    @Test
    public void testFixedRange() throws Exception {
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("P1M/PRESENT");
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        // the default should be the range we requested
        java.util.Date curr = new java.util.Date();
        Range d = ((Range) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Returns a valid Default range", (d != null));
        // check "now" it's in the same minute... should work for even the slowest build server
        WMSDimensionsTestSupport.assertDateEquals(curr, ((java.util.Date) (d.getMaxValue())), WMSDimensionsTestSupport.MILLIS_IN_MINUTE);
        // the beginning
        WMSDimensionsTestSupport.assertDateEquals(new Date(((curr.getTime()) - (30L * (WMSDimensionsTestSupport.MILLIS_IN_DAY)))), ((java.util.Date) (d.getMinValue())), 60000);
    }

    @Test
    public void testExplicitNearestToGivenTimeVectorSelector() throws Exception {
        int fid = 1000;
        String preferredTimeStr = "2012-06-01T03:00:00.000Z";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(preferredTimeStr);
        // From src/test/resources/org/geoserver/wms/TimeElevationWithStartEnd.properties:
        Date expected = Date.valueOf("2012-02-12");
        setupFeatureTimeDimension(defaultValueSetting);
        FeatureTypeInfo timeWithStartEnd = getCatalog().getFeatureTypeByName(VectorTimeDimensionDefaultValueTest.TIME_WITH_START_END.getLocalPart());
        Date twoDaysAgo = addFeatureWithTimeTwoDaysAgo((fid++));
        this.addFeature((fid++), twoDaysAgo, Double.valueOf(0.0));
        java.util.Date d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (expected.getTime())));
        // Add some features with timestamps in the future:
        addFeatureWithTimeDayAfterTomorrow((fid++));
        addFeatureWithTimeOneYearFromNow((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (expected.getTime())));
        addFeatureWithTimeTodayMidnight((fid++));
        d = ((java.util.Date) (wms.getDefaultTime(timeWithStartEnd)));
        Assert.assertTrue("Default time is null", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (expected.getTime())));
    }
}

