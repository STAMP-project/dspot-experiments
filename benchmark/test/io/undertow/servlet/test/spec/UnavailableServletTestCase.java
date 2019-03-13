package io.undertow.servlet.test.spec;


import StatusCodes.NOT_FOUND;
import StatusCodes.OK;
import StatusCodes.SERVICE_UNAVAILABLE;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class UnavailableServletTestCase {
    @Test
    public void testPermanentUnavailableServlet() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/p"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(NOT_FOUND, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testTempUnavailableServlet() throws IOException, InterruptedException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/t"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(SERVICE_UNAVAILABLE, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            Thread.sleep(1001);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/t"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

