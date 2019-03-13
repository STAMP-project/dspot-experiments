package cc.blynk.integration.http;


import cc.blynk.integration.BaseTest;
import cc.blynk.integration.Holder;
import cc.blynk.integration.TestUtil;
import cc.blynk.server.servers.BaseServer;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpAPIKeepAliveServerTest extends BaseTest {
    private BaseServer httpServer;

    private CloseableHttpClient httpclient;

    private String httpServerUrl;

    @Test
    public void testKeepAlive() throws Exception {
        HttpPut request = new HttpPut(((httpServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14"));
        request.setHeader("Connection", "keep-alive");
        HttpGet getRequest = new HttpGet(((httpServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/a14"));
        getRequest.setHeader("Connection", "keep-alive");
        for (int i = 0; i < 100; i++) {
            request.setEntity(new StringEntity((("[\"" + i) + "\"]"), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                Assert.assertEquals(200, response.getStatusLine().getStatusCode());
                Assert.assertEquals("keep-alive", response.getFirstHeader("Connection").getValue());
                EntityUtils.consume(response.getEntity());
            }
            try (CloseableHttpResponse response2 = httpclient.execute(getRequest)) {
                Assert.assertEquals(200, response2.getStatusLine().getStatusCode());
                List<String> values = TestUtil.consumeJsonPinValues(response2);
                Assert.assertEquals("keep-alive", response2.getFirstHeader("Connection").getValue());
                Assert.assertEquals(1, values.size());
                Assert.assertEquals(String.valueOf(i), values.get(0));
            }
        }
    }

    @Test(expected = Exception.class)
    public void keepAliveIsSupported() throws Exception {
        String url = (httpServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14";
        HttpPut request = new HttpPut(url);
        request.setHeader("Connection", "close");
        request.setEntity(new StringEntity((("[\"" + 0) + "\"]"), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("close", response.getFirstHeader("Connection").getValue());
            EntityUtils.consume(response.getEntity());
        }
        request = new HttpPut(url);
        request.setHeader("Connection", "close");
        request.setEntity(new StringEntity((("[\"" + 0) + "\"]"), ContentType.APPLICATION_JSON));
        // this should fail as connection is closed and httpClient is reusing connections
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testHttpAPICounters() throws Exception {
        HttpGet getRequest = new HttpGet(((httpServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v11?value=11"));
        try (CloseableHttpResponse response = httpclient.execute(getRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals(1, holder.stats.specificCounters[HTTP_UPDATE_PIN_DATA].intValue());
            Assert.assertEquals(0, holder.stats.specificCounters[HTTP_GET_PIN_DATA].intValue());
            Assert.assertEquals(1, holder.stats.totalMessages.getCount());
        }
        getRequest = new HttpGet(((httpServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v11"));
        try (CloseableHttpResponse response = httpclient.execute(getRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals(1, holder.stats.specificCounters[HTTP_UPDATE_PIN_DATA].intValue());
            Assert.assertEquals(1, holder.stats.specificCounters[HTTP_GET_PIN_DATA].intValue());
            Assert.assertEquals(2, holder.stats.totalMessages.getCount());
        }
    }
}

