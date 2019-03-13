/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.eo;


import WCSEOMetadata.ENABLED.key;
import org.geoserver.wcs.WCSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class DescribeCoverageTest extends WCSEOTestSupport {
    @Test
    public void testEOExtensions() throws Exception {
        Document dom = getAsDOM("wcs?request=DescribeCoverage&version=2.0.1&service=WCS&coverageid=sf__timeranges");
        // print(dom);
        // we have one eo metadata in the right place
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//gmlcov:metadata/gmlcov:Extension/wcseo:EOMetadata/eop:EarthObservation)", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation)", dom));
        Assert.assertEquals("2008-10-31T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:phenomenonTime/gml:TimePeriod/gml:beginPosition", dom));
        Assert.assertEquals("2008-11-07T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:phenomenonTime/gml:TimePeriod/gml:endPosition", dom));
        Assert.assertEquals("2008-11-07T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:resultTime/gml:TimeInstant/gml:timePosition", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation/om:featureOfInterest/eop:Footprint/eop:multiExtentOf/gml:MultiSurface)", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation/om:featureOfInterest/eop:Footprint/eop:centerOf/gml:Point)", dom));
        Assert.assertEquals("sf__timeranges", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:identifier", dom));
        Assert.assertEquals("NOMINAL", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:acquisitionType", dom));
        Assert.assertEquals("ARCHIVED", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:status", dom));
    }

    @Test
    public void testEOExtensionsDisabled() throws Exception {
        // disable EO extensions
        WCSInfo wcs = getGeoServer().getService(WCSInfo.class);
        wcs.getMetadata().put(key, false);
        getGeoServer().save(wcs);
        Document dom = getAsDOM("wcs?request=DescribeCoverage&version=2.0.1&service=WCS&coverageid=sf__timeranges");
        // print(dom);
        // we don't have the EO extensions
        Assert.assertEquals("0", WCSEOTestSupport.xpath.evaluate("count(//gmlcov:metadata/gmlcov:Extension/wcseo:EOMetadata)", dom));
    }

    @Test
    public void testSingleGranule() throws Exception {
        Document dom = getAsDOM("wcs?request=DescribeCoverage&version=2.0.1&service=WCS&coverageid=sf__timeranges_granule_timeranges.1");
        // print(dom);
        assertXpathEvaluatesTo("sf__timeranges_granule_timeranges.1_td_0", "//gmlcov:metadata/gmlcov:Extension/wcsgs:TimeDomain/gml:TimePeriod/@gml:id", dom);
        // we have one eo metadata in the right place
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//gmlcov:metadata/gmlcov:Extension/wcseo:EOMetadata/eop:EarthObservation)", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation)", dom));
        Assert.assertEquals("2008-11-05T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:phenomenonTime/gml:TimePeriod/gml:beginPosition", dom));
        Assert.assertEquals("2008-11-07T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:phenomenonTime/gml:TimePeriod/gml:endPosition", dom));
        Assert.assertEquals("2008-11-07T00:00:00.000Z", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/om:resultTime/gml:TimeInstant/gml:timePosition", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation/om:featureOfInterest/eop:Footprint/eop:multiExtentOf/gml:MultiSurface)", dom));
        Assert.assertEquals("1", WCSEOTestSupport.xpath.evaluate("count(//eop:EarthObservation/om:featureOfInterest/eop:Footprint/eop:centerOf/gml:Point)", dom));
        Assert.assertEquals("sf__timeranges", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:identifier", dom));
        Assert.assertEquals("NOMINAL", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:acquisitionType", dom));
        Assert.assertEquals("ARCHIVED", WCSEOTestSupport.xpath.evaluate("//eop:EarthObservation/eop:metaDataProperty/eop:EarthObservationMetaData/eop:status", dom));
    }
}

