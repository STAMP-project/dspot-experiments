package org.mockserver.filters;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;


/**
 *
 *
 * @author jamesdbloom
 */
public class HopByHopHeaderFilterTest {
    @Test
    public void shouldNotForwardHopByHopHeaders() throws Exception {
        // given
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.withHeaders(new Header("some_other_header"), new Header("proxy-connection"), new Header("connection"), new Header("keep-alive"), new Header("transfer-encoding"), new Header("te"), new Header("trailer"), new Header("proxy-authorization"), new Header("proxy-authenticate"), new Header("upgrade"));
        // when
        httpRequest = new HopByHopHeaderFilter().onRequest(httpRequest);
        // then
        Assert.assertEquals(httpRequest.getHeaderList().size(), 1);
    }

    @Test
    public void shouldNotHandleNullRequest() throws Exception {
        TestCase.assertNull(new HopByHopHeaderFilter().onRequest(null));
    }
}

