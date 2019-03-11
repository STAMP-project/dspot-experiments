package cc.blynk.integration.http;


import cc.blynk.integration.BaseTest;
import cc.blynk.server.servers.BaseServer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore("due to security reasons, upload via http is not supported")
public class UploadAPITest extends BaseTest {
    private BaseServer httpServer;

    protected CloseableHttpClient httpclient;

    @Test
    public void uploadFileToServer() throws Exception {
        String pathToImage = upload("static/ota/test.bin");
        HttpGet index = new HttpGet((("http://localhost:" + (BaseTest.properties.getHttpPort())) + pathToImage));
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/octet-stream", response.getHeaders("Content-Type")[0].getValue());
        }
    }
}

