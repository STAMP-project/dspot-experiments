/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.junit.Test;


public class GetFeaturePagingTest extends WFSTestSupport {
    @Test
    public void testSingleType() throws Exception {
        doTestSingleType("gs:Fifteen");
        doTestSingleType("cdf:Fifteen");
    }

    @Test
    public void testStartIndexSimplePOST() throws Exception {
        doTestStartIndexSimplePOST("gs:Fifteen");
        doTestStartIndexSimplePOST("cdf:Fifteen");
    }

    @Test
    public void testStartIndexMultipleTypes() throws Exception {
        doTestStartIndexMultipleTypes("gs:Fifteen", "gs:Seven");
        doTestStartIndexMultipleTypes("cdf:Fifteen", "cdf:Seven");
        doTestStartIndexMultipleTypes("gs:Fifteen", "cdf:Seven");
        doTestStartIndexMultipleTypes("cdf:Fifteen", "gs:Seven");
    }

    @Test
    public void testStartIndexMultipleTypesPOST() throws Exception {
        doTestStartIndexMultipleTypesPOST("gs:Fifteen", "gs:Seven");
        doTestStartIndexMultipleTypesPOST("cdf:Fifteen", "cdf:Seven");
        doTestStartIndexMultipleTypesPOST("gs:Fifteen", "cdf:Seven");
        doTestStartIndexMultipleTypesPOST("cdf:Fifteen", "gs:Seven");
    }

    @Test
    public void testWithFilter() throws Exception {
        doTestWithFilter("gs:Fifteen");
        doTestWithFilter("cdf:Fifteen");
    }
}

