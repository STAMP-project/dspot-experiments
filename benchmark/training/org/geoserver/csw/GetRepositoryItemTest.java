/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw;


import junit.framework.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class GetRepositoryItemTest extends CSWSimpleTestSupport {
    @Test
    public void testGetMissingId() throws Exception {
        Document dom = getAsDOM(((CSWSimpleTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetRepositoryItem"));
        checkOws10Exception(dom, ServiceException.MISSING_PARAMETER_VALUE, "id");
    }

    @Test
    public void testGetMissing() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((CSWSimpleTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetRepositoryItem&id=foo"));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetSingle() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((CSWSimpleTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetRepositoryItem&id=urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f"));
        String content = response.getContentAsString();
        // System.out.println(content);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/xml", response.getContentType());
        String expected = "This is a random comment that will show up only when fetching the repository item";
        Assert.assertTrue(content.contains(expected));
    }
}

