/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;


import org.junit.Test;
import org.w3c.dom.Document;


public class DescribeProcessTest extends WPSTestSupport {
    @Test
    public void testGetBuffer() throws Exception {
        // Standard Test A.4.3.1
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=JTS:buffer"));
        // print(d);
        testBufferDescription(d);
    }

    @Test
    public void testPostBuffer() throws Exception {
        // Standard Test A.4.3.2
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + ((((("<DescribeProcess xmlns=\"http://www.opengis.net/wps/1.0.0\" " + "xmlns:ows=\"http://www.opengis.net/ows/1.1\" ") + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" ") + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n") + "    <ows:Identifier>JTS:buffer</ows:Identifier>\r\n") + "</DescribeProcess>");
        Document d = postAsDOM(root(), request);
        // print(d);
        testBufferDescription(d);
    }

    @Test
    public void testGetBufferFeatureCollection() throws Exception {
        // Standard Test A.4.3.1
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:BufferFeatureCollection"));
        print(d);
        // check that we advertise the base64 encoding for application/zip
        String base = "/wps:ProcessDescriptions/ProcessDescription/DataInputs";
        assertXpathExists((base + "/Input[1]/ComplexData/Supported/Format[MimeType='application/zip']"), d);
        assertXpathEvaluatesTo("base64", (base + "/Input[1]/ComplexData/Supported/Format[MimeType='application/zip']/Encoding"), d);
    }

    /**
     * Tests encoding of bounding box outputs
     */
    @Test
    public void testBounds() throws Exception {
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:Bounds"));
        // print(d);
        checkValidationErrors(d);
        assertXpathEvaluatesTo("EPSG:4326", "//Output[ows:Identifier='bounds']/BoundingBoxOutput/Default/CRS", d);
        assertXpathEvaluatesTo("EPSG:4326", "//Output[ows:Identifier='bounds']/BoundingBoxOutput/Supported/CRS", d);
    }

    @Test
    public void testDefaultValues() throws Exception {
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:GeorectifyCoverage"));
        // print(d);
        checkValidationErrors(d);
        assertXpathEvaluatesTo("true", "//Input[ows:Identifier='transparent']/LiteralData/DefaultValue", d);
        assertXpathEvaluatesTo("false", "//Input[ows:Identifier='store']/LiteralData/DefaultValue", d);
    }

    @Test
    public void testMultiRaw() throws Exception {
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:MultiRaw"));
        // print(d);
        checkValidationErrors(d);
        // only one input (we have two, but one is the chosen mime type for the outputs
        assertXpathEvaluatesTo("1", "count(//Input)", d);
        assertXpathEvaluatesTo("1", "count(//Input[ows:Identifier='id']/LiteralData)", d);
        // three outputs, two complex, one literal
        assertXpathEvaluatesTo("3", "count(//Output)", d);
        assertXpathEvaluatesTo("text/plain", "//Output[ows:Identifier='text']/ComplexOutput/Supported/Format/MimeType", d);
        assertXpathEvaluatesTo("application/zip", "//Output[ows:Identifier='binary']/ComplexOutput/Supported/Format[1]/MimeType", d);
        assertXpathEvaluatesTo("image/png", "//Output[ows:Identifier='binary']/ComplexOutput/Supported/Format[2]/MimeType", d);
        assertXpathEvaluatesTo("1", "count(//Output[ows:Identifier='literal']/LiteralOutput)", d);
    }
}

