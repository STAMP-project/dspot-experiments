/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.xml;


import PreventLocalEntityResolver.ERROR_MESSAGE_BASE;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class DescribeCoverageTest extends WCSTestSupport {
    @Test
    public void testEntityExpansion() throws Exception {
        final File xml = new File("./src/test/resources/testDescribeCoverageEntityExpansion.xml");
        final String request = FileUtils.readFileToString(xml);
        Document dom = postAsDOM("wcs", request);
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        String text = WCSTestSupport.xpath.evaluate("//ows:ExceptionText", dom);
        Assert.assertTrue(text.contains(ERROR_MESSAGE_BASE));
    }

    @Test
    public void testDescribeCoverageSimple() throws Exception {
        final File xml = new File("./src/test/resources/testDescribeCoverage.xml");
        final String request = FileUtils.readFileToString(xml);
        Document dom = postAsDOM("wcs", request);
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        // validate
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        // check it is good
        assertXpathEvaluatesTo("wcs__BlueMarble", "//wcs:CoverageDescription//wcs:CoverageId", dom);
        assertXpathEvaluatesTo("3", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
        // enforce pixel center
        assertXpathEvaluatesTo("-43.0020833333312 146.5020833333281", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//gml:domainSet//gml:RectifiedGrid//gml:origin//gml:Point//gml:pos", dom);
    }

    @Test
    public void testDescribeCoverageMultiband() throws Exception {
        final File xml = new File("./src/test/resources/testDescribeCoverageMultiBand.xml");
        final String request = FileUtils.readFileToString(xml);
        Document dom = postAsDOM("wcs", request);
        Assert.assertNotNull(dom);
        // print(dom, System.out);
        checkValidationErrors(dom, WCSTestSupport.getWcs20Schema());
        // check it is good
        assertXpathEvaluatesTo("wcs__multiband", "//wcs:CoverageDescription//wcs:CoverageId", dom);
        assertXpathEvaluatesTo("9", "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
        assertXpathEvaluatesTo("image/tiff", "//wcs:CoverageDescriptions//wcs:CoverageDescription[1]//wcs:ServiceParameters//wcs:nativeFormat", dom);
    }
}

