package org.javaee7.websocket.encoder.programmatic;


import MyClient.latch;
import MyClient.response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyClientTest {
    @ArquillianResource
    URI base;

    @Test
    @RunAsClient
    public void testEndpoint() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        final String JSON = "{\"apple\":\"red\",\"banana\":\"yellow\"}";
        Session session = connectToServer(MyClient.class);
        Assert.assertNotNull(session);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(JSON, response.toString());
    }
}

