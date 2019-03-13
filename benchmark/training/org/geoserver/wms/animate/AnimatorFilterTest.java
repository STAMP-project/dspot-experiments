/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.animate;


import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Some unit and functional tests for animator filter
 *
 * @author Tom Kunicki, Boundless
 */
public class AnimatorFilterTest extends WMSTestSupport {
    @Test
    public void testDefaults() throws Exception {
        String requestURL = "cite/wms/animate?aparam=layers&avalues=MapNeatline,Buildings,Lakes";
        MockHttpServletResponse resp = getAsServletResponse(requestURL);
        // check mime type
        Assert.assertEquals("image/gif", resp.getContentType());
        // check for multiple (3) frames
        Assert.assertEquals(3, extractImageCountFromGIF(resp));
    }

    @Test
    public void testGEOS_6006() throws Exception {
        String requestURL = "cite/wms/animate?request=getmap&aparam=layers&avalues=MapNeatline,Buildings,Lakes";
        MockHttpServletResponse resp = getAsServletResponse(requestURL);
        // check mime type
        Assert.assertEquals("image/gif", resp.getContentType());
        // check for multiple (3) frames
        Assert.assertEquals(3, extractImageCountFromGIF(resp));
    }
}

