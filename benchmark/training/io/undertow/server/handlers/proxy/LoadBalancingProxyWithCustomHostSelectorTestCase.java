package io.undertow.server.handlers.proxy;


import StatusCodes.OK;
import io.undertow.Undertow;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(DefaultServer.class)
public class LoadBalancingProxyWithCustomHostSelectorTestCase {
    protected static Undertow server1;

    protected static Undertow server2;

    // https://issues.jboss.org/browse/UNDERTOW-289
    @Test
    public void testDistributeLoadToGivenHost() throws Throwable {
        final StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < 6; ++i) {
            TestHttpClient client = new TestHttpClient();
            try {
                HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/name"));
                HttpResponse result = client.execute(get);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                resultString.append(HttpClientUtils.readResponse(result));
                resultString.append(' ');
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
        Assert.assertTrue(resultString.toString().contains("server1"));
        Assert.assertFalse(resultString.toString().contains("server2"));
    }
}

