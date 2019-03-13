/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo.kvp;


import org.junit.Test;


public class RangePatternsTest {
    @Test
    public void testFullRangePattern() {
        // check matches
        assertFullRangeMatch("[10,20]", "[", "10", "20", "]");
        assertFullRangeMatch("]10,20[", "]", "10", "20", "[");
        assertFullRangeMatch("[2010-09-21,2010-09-22]", "[", "2010-09-21", "2010-09-22", "]");
        // check failures
        assertPatternNotMatch(FULL_RANGE_PATTERN, "abcd");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "10");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "10,20");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "2010-09-21");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "[10");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "10]");
        assertPatternNotMatch(FULL_RANGE_PATTERN, "10,20,30");
    }

    @Test
    public void testLeftRangePattern() {
        // check matches
        assertLeftRangeMatch("[10", "[", "10");
        assertLeftRangeMatch("]10", "]", "10");
        assertLeftRangeMatch("[2010-09-21", "[", "2010-09-21");
        // check failures
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "abcd");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10,20");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "2010-09-21");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "[10,20]");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10]");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10,20,30");
    }

    @Test
    public void testRightRangePattern() {
        // check matches
        assertRightRangeMatch("10]", "10", "]");
        assertRightRangeMatch("10[", "10", "[");
        assertRightRangeMatch("2010-09-21]", "2010-09-21", "]");
        // check failures
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "abcd");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10,20");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "2010-09-21");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "[10,20]");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10]");
        assertPatternNotMatch(LEFT_RANGE_PATTERN, "10,20,30");
    }
}

