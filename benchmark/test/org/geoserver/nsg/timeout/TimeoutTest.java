/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg.timeout;


import org.geoserver.wfs.v2_0.WFS20TestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class TimeoutTest extends WFS20TestSupport {
    @Test
    public void testNoTimeout() throws Exception {
        setTimeout(0);
        // no timeout happening
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        assertXpathEvaluatesTo("1", "count(/wfs:FeatureCollection)", dom);
        assertXpathEvaluatesTo("480", "count(//cdf:Fifteen)", dom);
    }

    @Test
    public void testTimeoutBeforeEncoding() throws Exception {
        // timeout in one, but delay two, should not even start encoding
        setTimeout(1);
        setExecutionDelay(2);
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        // print(dom);
        checkOws11Exception(dom, "2.0.0", TimeoutVerifier.TIMEOUT_EXCEPTION_CODE, "GetFeature");
    }

    @Test
    public void testTimeoutOnGMLEncodingStart() throws Exception {
        // timeout in two, but delay three right away, so that streaming does not even start writing
        // stuff out
        setTimeout(2);
        setExecutionDelay(0);
        setEncodeDelay(3, 0);
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        // print(dom);
        checkOws11Exception(dom, "2.0.0", TimeoutVerifier.TIMEOUT_EXCEPTION_CODE, "GetFeature");
    }

    @Test
    public void testTimeoutAfterStreamingEncodingStart() throws Exception {
        setTimeout(2);
        setExecutionDelay(0);
        setEncodeDelay(3, 400);
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        // print(dom);
        assertXpathEvaluatesTo("1", "count(/wfs:FeatureCollection)", dom);
        assertXpathEvaluatesTo("480", "count(//cdf:Fifteen)", dom);
    }

    @Test
    public void testTimeoutShapefileEncoding() throws Exception {
        // timeout in 2 seconds, encode 14 feature before delay, the stream is not yet written
        // to though, so the results should be an exception
        setTimeout(2);
        setExecutionDelay(0);
        setEncodeDelay(3, 14);
        MockHttpServletResponse response = getAsServletResponse("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&outputFormat=SHAPE-ZIP");
        Assert.assertEquals("application/xml", response.getContentType());
        // This one does not work due to a bug in MockHttpServletResponse, asking for header values
        // to be non null, while the javadoc does not make any such request
        // assertNull(response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsByteArray()));
        // print(dom);
        checkOws11Exception(dom, "2.0.0", TimeoutVerifier.TIMEOUT_EXCEPTION_CODE, "GetFeature");
    }
}

