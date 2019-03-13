/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import DimensionDefaultValueSetting.TIME_CURRENT;
import ResourceInfo.TIME;
import Strategy.FIXED;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSDimensionsTestSupport;
import org.geotools.feature.type.DateUtil;
import org.geotools.util.Range;
import org.junit.Assert;
import org.junit.Test;

import static java.sql.Date.valueOf;


/**
 * Tests the WMS default value support for TIME dimension raster layers.
 *
 * @author Ilkka Rinne <ilkka.rinne@spatineo.com>
 */
public class RasterTimeDimensionDefaultValueTest extends WMSDimensionsTestSupport {
    static final QName WATTEMP_FUTURE = new QName(MockData.SF_URI, "watertemp_future_generated", MockData.SF_PREFIX);

    WMS wms;

    @Test
    public void testDefaultTimeCoverageSelector() throws Exception {
        // Use default default value strategy:
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, null);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        long todayMidnight = cal.getTimeInMillis();
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == todayMidnight));
    }

    @Test
    public void testExplicitCurrentTimeCoverageSelector() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(TIME_CURRENT);
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        long todayMidnight = cal.getTimeInMillis();
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == todayMidnight));
    }

    @Test
    public void testFixedTimeRange() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("P1M/PRESENT");
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        // the default is a single value, as we get the nearest to the range
        Date curr = new Date();
        Range d = ((Range) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default range", (d != null));
        // check "now" it's in the same minute... should work for even the slowest build server
        WMSDimensionsTestSupport.assertDateEquals(curr, ((Date) (d.getMaxValue())), WMSDimensionsTestSupport.MILLIS_IN_MINUTE);
        // the beginning
        WMSDimensionsTestSupport.assertDateEquals(new java.sql.Date(((curr.getTime()) - (30L * (WMSDimensionsTestSupport.MILLIS_IN_DAY)))), ((Date) (d.getMinValue())), 60000);
    }

    @Test
    public void testExplicitMinTimeCoverageSelector() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        // From src/test/resources/org/geoserver/wms/watertemp.zip:
        java.sql.Date expected = valueOf("2008-10-31");
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the smallest one", ((d.getTime()) == (expected.getTime())));
    }

    @Test
    public void testExplicitMaxTimeCoverageSelector() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        // This is what the test data setup does, and it makes a difference at the
        // end of the month (e.g. 29 Jan)
        cal.set(Calendar.MONTH, ((cal.get(Calendar.MONTH)) + 1));
        cal.set(Calendar.MONTH, ((cal.get(Calendar.MONTH)) - 1));
        cal.set(Calendar.YEAR, ((cal.get(Calendar.YEAR)) + 1));
        long oneYearInFuture = cal.getTimeInMillis();
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the biggest one", ((d.getTime()) == oneYearInFuture));
    }

    @Test
    public void testExplicitFixedTimeCoverageSelector() throws Exception {
        String fixedTimeStr = "2012-06-01T03:00:00.000Z";
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue(fixedTimeStr);
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        long fixedTime = DateUtil.parseDateTime(fixedTimeStr);
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the fixed one", ((d.getTime()) == fixedTime));
    }

    @Test
    public void testExplicitNearestToGivenTimeCoverageSelector() throws Exception {
        String preferredTimeStr = "2009-01-01T00:00:00.000Z";
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(preferredTimeStr);
        setupResourceDimensionDefaultValue(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE, TIME, defaultValueSetting);
        // From src/test/resources/org/geoserver/wms/watertemp.zip:
        java.sql.Date expected = valueOf("2008-11-01");
        CoverageInfo coverage = getCatalog().getCoverageByName(RasterTimeDimensionDefaultValueTest.WATTEMP_FUTURE.getLocalPart());
        Date d = ((Date) (wms.getDefaultTime(coverage)));
        Assert.assertTrue("Returns a valid Default time", (d != null));
        Assert.assertTrue("Default time should be the closest one", ((d.getTime()) == (expected.getTime())));
    }
}

