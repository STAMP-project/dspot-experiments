package com.orientechnologies.orient.test.server.network.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;


public class HttpDisabledTokenTest extends BaseHttpDatabaseTest {
    @Test
    public void testTokenRequest() throws IOException, ClientProtocolException {
        HttpPost request = new HttpPost((((getBaseURL()) + "/token/") + (getDatabaseName())));
        request.setEntity(new StringEntity("grant_type=password&username=admin&password=admin"));
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 400);
        HttpEntity entity = response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        entity.writeTo(out);
        Assert.assertTrue(out.toString().toString().contains("unsupported_grant_type"));
    }
}

