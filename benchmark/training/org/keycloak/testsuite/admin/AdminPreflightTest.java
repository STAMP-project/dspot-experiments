package org.keycloak.testsuite.admin;


import Cors.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import Cors.ACCESS_CONTROL_ALLOW_HEADERS;
import Cors.ACCESS_CONTROL_ALLOW_METHODS;
import Cors.ACCESS_CONTROL_ALLOW_ORIGIN;
import Cors.ACCESS_CONTROL_MAX_AGE;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;


public class AdminPreflightTest extends AbstractAdminTest {
    private CloseableHttpClient client;

    @Test
    public void testPreflight() throws IOException {
        HttpOptions options = new HttpOptions(getAdminUrl("realms/master/users"));
        options.setHeader("Origin", "http://test");
        CloseableHttpResponse response = client.execute(options);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals("true", response.getFirstHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS).getValue());
        Assert.assertEquals("DELETE, POST, GET, PUT", response.getFirstHeader(ACCESS_CONTROL_ALLOW_METHODS).getValue());
        Assert.assertEquals("http://test", response.getFirstHeader(ACCESS_CONTROL_ALLOW_ORIGIN).getValue());
        Assert.assertEquals("3600", response.getFirstHeader(ACCESS_CONTROL_MAX_AGE).getValue());
        Assert.assertTrue(response.getFirstHeader(ACCESS_CONTROL_ALLOW_HEADERS).getValue().contains("Authorization"));
        Assert.assertTrue(response.getFirstHeader(ACCESS_CONTROL_ALLOW_HEADERS).getValue().contains("Content-Type"));
    }
}

