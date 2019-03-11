package com.baeldung.tomcat;


import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;


/**
 * Created by adi on 1/14/18.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ProgrammaticTomcatIntegrationTest {
    private ProgrammaticTomcat tomcat = new ProgrammaticTomcat();

    @Test
    public void givenTomcatStarted_whenAccessServlet_responseIsTestAndResponseHeaderIsSet() throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getServlet = new HttpGet("http://localhost:8080/my-servlet");
        HttpResponse response = httpClient.execute(getServlet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String myHeaderValue = response.getFirstHeader("myHeader").getValue();
        Assert.assertEquals("myHeaderValue", myHeaderValue);
        HttpEntity responseEntity = response.getEntity();
        assertNotNull(responseEntity);
        String responseString = EntityUtils.toString(responseEntity, "UTF-8");
        Assert.assertEquals("test", responseString);
    }
}

