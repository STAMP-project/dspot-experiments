/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import java.util.Date;
import junit.framework.TestCase;
import org.geotools.util.DateRange;
import org.junit.Test;


public class AcceptableRangeTest {
    public static final long DAY_IN_MS = ((1000 * 60) * 60) * 24;

    @Test
    public void testSymmetricTimeRange() throws Exception {
        AcceptableRange range = AcceptableRange.getAcceptableRange("P1D", Date.class);
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, range.getBefore());
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, range.getAfter());
        Date value = new Date();
        DateRange searchRange = ((DateRange) (range.getSearchRange(value)));
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, ((value.getTime()) - (searchRange.getMinValue().getTime())));
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, ((searchRange.getMaxValue().getTime()) - (value.getTime())));
    }

    @Test
    public void testPastTimeRange() throws Exception {
        AcceptableRange range = AcceptableRange.getAcceptableRange("P1D/P0D", Date.class);
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, range.getBefore());
        TestCase.assertEquals(0L, range.getAfter());
        Date value = new Date();
        DateRange searchRange = ((DateRange) (range.getSearchRange(value)));
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, ((value.getTime()) - (searchRange.getMinValue().getTime())));
        TestCase.assertEquals(0L, ((searchRange.getMaxValue().getTime()) - (value.getTime())));
    }

    @Test
    public void testFutureTimeRange() throws Exception {
        AcceptableRange range = AcceptableRange.getAcceptableRange("P0D/P1D", Date.class);
        TestCase.assertEquals(0L, range.getBefore());
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, range.getAfter());
        Date value = new Date();
        DateRange searchRange = ((DateRange) (range.getSearchRange(value)));
        TestCase.assertEquals(0L, ((value.getTime()) - (searchRange.getMinValue().getTime())));
        TestCase.assertEquals(AcceptableRangeTest.DAY_IN_MS, ((searchRange.getMaxValue().getTime()) - (value.getTime())));
    }
}

