/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.flow.controller;


import javax.servlet.http.Cookie;
import org.geoserver.ows.HttpErrorCodeException;
import org.geoserver.ows.Request;
import org.junit.Assert;
import org.junit.Test;


public class RateFlowControllerTest extends AbstractFlowControllerTest {
    @Test
    public void testCookieRateControl() {
        RateFlowController controller = new RateFlowController(new OWSRequestMatcher(), 2, Long.MAX_VALUE, 1000, new CookieKeyGenerator());
        // run the first request
        Request firstRequest = buildCookieRequest(null);
        Assert.assertTrue(controller.requestIncoming(firstRequest, Integer.MAX_VALUE));
        checkHeaders(firstRequest, "Any OGC request", 2, 1);
        // grab the cookie
        Cookie cookie = ((Cookie) (getCookies()[0]));
        String cookieValue = cookie.getValue();
        // second request
        Request request = buildCookieRequest(cookieValue);
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        checkHeaders(request, "Any OGC request", 2, 0);
        // third one, this one will have to wait
        long start = System.currentTimeMillis();
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        long end = System.currentTimeMillis();
        long delay = end - start;
        Assert.assertTrue(("Request was not delayed enough: " + delay), (delay >= 1000));
        checkHeaders(request, "Any OGC request", 2, 0);
        // fourth one, this one will bail out immediately because we give it not enough wait
        Assert.assertFalse(controller.requestIncoming(request, 500));
        checkHeaders(request, "Any OGC request", 2, 0);
    }

    @Test
    public void testCookie429() {
        RateFlowController controller = new RateFlowController(new OWSRequestMatcher(), 2, Long.MAX_VALUE, 0, new CookieKeyGenerator());
        // run the first request
        Request firstRequest = buildCookieRequest(null);
        Assert.assertTrue(controller.requestIncoming(firstRequest, Integer.MAX_VALUE));
        // grab the cookie
        Cookie cookie = ((Cookie) (getCookies()[0]));
        String cookieValue = cookie.getValue();
        // second request
        Request request = buildCookieRequest(cookieValue);
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        // this one should fail with a 429
        try {
            Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        } catch (HttpErrorCodeException e) {
            Assert.assertEquals(429, e.getErrorCode());
        }
    }

    @Test
    public void testIpRateControl() {
        RateFlowController controller = new RateFlowController(new OWSRequestMatcher(), 2, Long.MAX_VALUE, 1000, new IpKeyGenerator());
        // run two requests
        Request request = buildIpRequest("127.0.0.1", "");
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        // third one, this one will have to wait
        long start = System.currentTimeMillis();
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        long end = System.currentTimeMillis();
        long delay = end - start;
        Assert.assertTrue(("Request was not delayed enough: " + delay), (delay >= 1000));
        // fourth one, this one will bail out immediately because we give it not enough wait
        Assert.assertFalse(controller.requestIncoming(request, 500));
    }

    @Test
    public void testIp429() {
        RateFlowController controller = new RateFlowController(new OWSRequestMatcher(), 2, Long.MAX_VALUE, 0, new IpKeyGenerator());
        // run two requests
        Request request = buildIpRequest("127.0.0.1", "");
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        // this one should fail with a 429
        try {
            Assert.assertTrue(controller.requestIncoming(request, Integer.MAX_VALUE));
        } catch (HttpErrorCodeException e) {
            Assert.assertEquals(429, e.getErrorCode());
        }
    }
}

