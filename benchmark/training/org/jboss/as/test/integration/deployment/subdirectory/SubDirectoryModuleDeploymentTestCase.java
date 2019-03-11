package org.jboss.as.test.integration.deployment.subdirectory;


import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * WFLY-29
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SubDirectoryModuleDeploymentTestCase {
    @ArquillianResource
    protected URL url;

    @Test
    public void testModulesInSubDeployments() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(((url.toExternalForm()) + "message"));
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            Assert.assertEquals(200, statusLine.getStatusCode());
            String result = EntityUtils.toString(entity);
            Assert.assertEquals("Hello World", result);
        }
    }
}

