/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.xml;


import java.io.File;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.geoserver.wcs2_0.WCSTestSupport;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Testing {@link GMLCoverageResponseDelegate}
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 */
public class GMLGetCoverageTest extends WCSTestSupport {
    @Test
    public void testGMLExtension() throws Exception {
        final File xml = new File("./src/test/resources/requestGetCoverageGML.xml");
        final String request = FileUtils.readFileToString(xml);
        MockHttpServletResponse response = postAsServletResponse("wcs", request);
        TestCase.assertEquals("application/gml+xml", response.getContentType());
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes()));
        // print(dom);
        // validate
        // checkValidationErrors(dom, WCS20_SCHEMA);
        // check it is good
        // assertXpathEvaluatesTo("wcs__BlueMarble",
        // "//wcs:CoverageDescription//wcs:CoverageId", dom);
        // assertXpathEvaluatesTo("3",
        // "count(//wcs:CoverageDescription//gmlcov:rangeType//swe:DataRecord//swe:field)", dom);
    }
}

