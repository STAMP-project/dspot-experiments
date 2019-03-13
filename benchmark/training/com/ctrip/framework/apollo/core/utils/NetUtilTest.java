package com.ctrip.framework.apollo.core.utils;


import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_OK;
import com.ctrip.framework.apollo.BaseIntegrationTest;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by gl49 on 2018/6/8.
 */
public class NetUtilTest extends BaseIntegrationTest {
    @Test
    public void testPingUrlWithStatusCode200() throws Exception {
        String someResponse = "some response";
        ContextHandler handler = mockServerHandler(SC_OK, someResponse);
        startServerWithHandlers(handler);
        Assert.assertTrue(NetUtil.pingUrl(("http://localhost:" + (BaseIntegrationTest.PORT))));
    }

    @Test
    public void testPingUrlWithStatusCode404() throws Exception {
        String someResponse = "some response";
        startServerWithHandlers(mockServerHandler(SC_NOT_FOUND, someResponse));
        Assert.assertFalse(NetUtil.pingUrl(("http://localhost:" + (BaseIntegrationTest.PORT))));
    }

    @Test
    public void testPingUrlWithServerNotStarted() throws Exception {
        Assert.assertFalse(NetUtil.pingUrl(("http://localhost:" + (BaseIntegrationTest.PORT))));
    }
}

