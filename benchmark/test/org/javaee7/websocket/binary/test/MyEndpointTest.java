package org.javaee7.websocket.binary.test;


import MyEndpointClient.latch;
import MyEndpointClient.response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import org.javaee7.websocket.binary.MyEndpointClient;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Nikos Ballas
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyEndpointTest {
    private static final String RESPONSE = "Hello World!";

    @ArquillianResource
    URI base;

    /**
     * The basic test method for the class {@link MyEndpointByteBuffer}
     *
     * @throws URISyntaxException
     * 		
     * @throws DeploymentException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    @RunAsClient
    public void testEndpointByteBuffer() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointClient.latch = new CountDownLatch(1);
        Session session = connectToServer("bytebuffer");
        Assert.assertNotNull(session);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertNotNull(response);
        Assert.assertArrayEquals(MyEndpointTest.RESPONSE.getBytes(), response);
    }

    /**
     * The basic test method for the class {
     *
     * @unknown }
     * @throws DeploymentException
     * 		
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    @RunAsClient
    public void testEndpointByteArray() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointClient.latch = new CountDownLatch(1);
        Session session = connectToServer("bytearray");
        Assert.assertNotNull(session);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertNotNull(response);
        Assert.assertArrayEquals(MyEndpointTest.RESPONSE.getBytes(), response);
    }

    /**
     * The basic test method for the class {
     *
     * @unknown }
     * @throws DeploymentException
     * 		
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    @RunAsClient
    public void testEndpointInputStream() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointClient.latch = new CountDownLatch(1);
        Session session = connectToServer("inputstream");
        Assert.assertNotNull(session);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertNotNull(response);
        Assert.assertArrayEquals(MyEndpointTest.RESPONSE.getBytes(), response);
    }
}

