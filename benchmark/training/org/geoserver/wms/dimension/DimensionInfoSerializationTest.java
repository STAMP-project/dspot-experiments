/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import Strategy.FIXED;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;


public class DimensionInfoSerializationTest extends WMSTestSupport {
    @Test
    public void testMinStrategyXMLSerialization() throws Exception {
        assertBackAndForthSerialization(MINIMUM);
    }

    @Test
    public void testMaxStrategyXMLSerialization() throws Exception {
        assertBackAndForthSerialization(MAXIMUM);
    }

    @Test
    public void testFixedStrategyXMLSerialization() throws Exception {
        assertBackAndForthSerialization(FIXED);
        assertBackAndForthSerialization(FIXED, "2014-01-24T13:25:00.000Z");
    }

    @Test
    public void testNearestStrategyXMLSerialization() throws Exception {
        assertBackAndForthSerialization(NEAREST);
        assertBackAndForthSerialization(NEAREST, "2014-01-24T13:25:00.000Z");
    }
}

