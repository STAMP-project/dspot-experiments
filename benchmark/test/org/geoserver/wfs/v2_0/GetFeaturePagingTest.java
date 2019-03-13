/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.FeatureTypeInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class GetFeaturePagingTest extends WFS20TestSupport {
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
        // doTestStartIndexMultipleTypes("gs:Fifteen", "cdf:Seven");
        // doTestStartIndexMultipleTypes("cdf:Fifteen", "gs:Seven");
    }

    @Test
    public void testStartIndexMultipleTypesPOST() throws Exception {
        doTestStartIndexMultipleTypesPOST("gs:Fifteen", "gs:Seven");
        doTestStartIndexMultipleTypesPOST("cdf:Fifteen", "cdf:Seven");
        // doTestStartIndexMultipleTypesPOST("gs:Fifteen", "cdf:Seven");
        // doTestStartIndexMultipleTypesPOST("cdf:Fifteen", "gs:Seven");
    }

    @Test
    public void testWithFilter() throws Exception {
        doTestWithFilter("gs:Fifteen");
        doTestWithFilter("cdf:Fifteen");
    }

    @Test
    public void testNextPreviousGET() throws Exception {
        doTestNextPreviousGET("gs:Fifteen");
        doTestNextPreviousGET("cdf:Fifteen");
    }

    @Test
    public void testNextPreviousSkipNumberMatchedGET() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName("Fifteen");
        fti.setSkipNumberMatched(true);
        getCatalog().save(fti);
        try {
            Assert.assertEquals(true, fti.getSkipNumberMatched());
            doTestNextPreviousGET("gs:Fifteen");
            doTestNextPreviousGET("cdf:Fifteen");
        } finally {
            fti.setSkipNumberMatched(false);
            getCatalog().save(fti);
        }
    }

    @Test
    public void testNextPreviousPOST() throws Exception {
        doTestNextPreviousPOST("gs:Fifteen");
        doTestNextPreviousPOST("cdf:Fifteen");
    }

    @Test
    public void testNextPreviousLinksPOST() throws Exception {
        doTestNextPreviousLinksPOST("gs:Fifteen");
    }

    @Test
    public void testSortingGET() throws Exception {
        Document dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=GetFeature&typeName=gs:Fifteen&sortBy=num ASC&count=1");
        XMLAssert.assertXpathExists("//gs:Fifteen/gs:num[text() = '0']", dom);
        dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=GetFeature&typeName=gs:Fifteen&sortBy=num DESC&count=1");
        XMLAssert.assertXpathExists("//gs:Fifteen/gs:num[text() = '14']", dom);
    }

    @Test
    public void testNextPreviousHitsGET() throws Exception {
        doTestNextPreviousHitsGET("gs:Fifteen");
        doTestNextPreviousHitsGET("cdf:Fifteen");
    }

    @Test
    public void testCountZero() throws Exception {
        Document doc = getAsDOM(("/wfs?request=GetFeature&version=2.0.0&service=wfs&" + "typename=gs:Fifteen&count=0"));
        XMLAssert.assertXpathExists("/wfs:FeatureCollection", doc);
        XMLAssert.assertXpathEvaluatesTo("0", "/wfs:FeatureCollection/@numberMatched", doc);
        XMLAssert.assertXpathEvaluatesTo("0", "/wfs:FeatureCollection/@numberReturned", doc);
    }
}

