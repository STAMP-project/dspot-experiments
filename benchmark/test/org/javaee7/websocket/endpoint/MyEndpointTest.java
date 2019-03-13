package org.javaee7.websocket.endpoint;


import MyEndpointTextClient.latch;
import MyEndpointTextClient.response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static MyEndpointTextClient.latch;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyEndpointTest {
    final String TEXT = "Hello World!";

    @ArquillianResource
    URI base;

    @Test
    @RunAsClient
    public void testTextEndpoint() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        latch = new CountDownLatch(1);
        Session session = connectToServer(MyEndpointTextClient.class, "text");
        Assert.assertNotNull(session);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(TEXT, response);
    }

    @Test
    @RunAsClient
    public void testEndpointByteBuffer() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointByteBufferClient.latch = new CountDownLatch(1);
        Session session = connectToServer(MyEndpointByteBufferClient.class, "bytebuffer");
        Assert.assertNotNull(session);
        Assert.assertTrue(MyEndpointByteBufferClient.latch.await(2, TimeUnit.SECONDS));
        Assert.assertArrayEquals(TEXT.getBytes(), MyEndpointByteBufferClient.response);
    }

    @Test
    @RunAsClient
    public void testEndpointByteArray() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointByteArrayClient.latch = new CountDownLatch(1);
        Session session = connectToServer(MyEndpointByteArrayClient.class, "bytearray");
        Assert.assertNotNull(session);
        Assert.assertTrue(MyEndpointByteArrayClient.latch.await(2, TimeUnit.SECONDS));
        Assert.assertNotNull(MyEndpointByteArrayClient.response);
        Assert.assertArrayEquals(TEXT.getBytes(), MyEndpointByteArrayClient.response);
    }

    @Test
    @RunAsClient
    public void testEndpointInputStream() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        MyEndpointInputStreamClient.latch = new CountDownLatch(1);
        Session session = connectToServer(MyEndpointInputStreamClient.class, "inputstream");
        Assert.assertNotNull(session);
        Assert.assertTrue(MyEndpointInputStreamClient.latch.await(2, TimeUnit.SECONDS));
        Assert.assertNotNull(MyEndpointInputStreamClient.response);
        Assert.assertArrayEquals(TEXT.getBytes(), MyEndpointInputStreamClient.response);
    }
}

