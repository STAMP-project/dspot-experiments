/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.filters;


import java.io.BufferedReader;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class BufferedRequestWrapperTest extends RequestWrapperTestSupport {
    @Test
    public void testGetInputStream() throws Exception {
        for (int i = 0; i < (testStrings.length); i++) {
            doInputStreamTest(testStrings[i]);
        }
    }

    @Test
    public void testGetReader() throws Exception {
        for (int i = 0; i < (testStrings.length); i++) {
            doGetReaderTest(testStrings[i]);
        }
    }

    @Test
    public void testMixedRequest() throws Exception {
        String body = "a=1&b=2";
        String queryString = "c=3&d=4";
        HttpServletRequest req = makeRequest(body, queryString);
        BufferedReader br = req.getReader();
        while ((br.readLine()) != null) {
            /* clear out the body */
        } 
        BufferedRequestWrapper wrapper = new BufferedRequestWrapper(req, "UTF-8", body.getBytes());
        Map params = wrapper.getParameterMap();
        Assert.assertEquals(4, params.size());
        Assert.assertEquals("1", ((String[]) (params.get("a")))[0]);
        Assert.assertEquals("2", ((String[]) (params.get("b")))[0]);
        Assert.assertEquals("3", ((String[]) (params.get("c")))[0]);
        Assert.assertEquals("4", ((String[]) (params.get("d")))[0]);
    }

    @Test
    public void testNoContentType() throws Exception {
        String body = "a=1&b=2";
        String queryString = "c=3&d=4";
        MockHttpServletRequest req = makeRequest(body, queryString);
        // reset the content type
        req.setContentType(null);
        BufferedReader br = req.getReader();
        while ((br.readLine()) != null) {
            /* clear out the body */
        } 
        // should not NPE like it did
        BufferedRequestWrapper wrapper = new BufferedRequestWrapper(req, "UTF-8", body.getBytes());
        Map params = wrapper.getParameterMap();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testEmptyPost() throws Exception {
        MockHttpServletRequest req = makeRequest("", "");
        // reset the content type
        req.setContentType(null);
        BufferedReader br = req.getReader();
        while ((br.readLine()) != null) {
            /* clear out the body */
        } 
        // should not NPE like it did
        BufferedRequestWrapper wrapper = new BufferedRequestWrapper(req, "UTF-8", "".getBytes());
        Map params = wrapper.getParameterMap();
        Assert.assertEquals(0, params.size());
    }
}

