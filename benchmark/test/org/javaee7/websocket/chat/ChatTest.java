package org.javaee7.websocket.chat;


import ChatClientEndpoint1.TEXT;
import ChatClientEndpoint1.latch;
import ChatClientEndpoint1.response;
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

import static ChatClientEndpoint1.latch;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class ChatTest {
    @ArquillianResource
    URI base;

    @Test
    @RunAsClient
    public void testConnect() throws IOException, InterruptedException, URISyntaxException, DeploymentException {
        latch = new CountDownLatch(1);
        final Session session1 = connectToServer(ChatClientEndpoint1.class);
        Assert.assertNotNull(session1);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(TEXT, response);
        latch = new CountDownLatch(1);
        ChatClientEndpoint2.latch = new CountDownLatch(1);
        final Session session2 = connectToServer(ChatClientEndpoint2.class);
        Assert.assertNotNull(session2);
        Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(ChatClientEndpoint2.latch.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(ChatClientEndpoint2.TEXT, response);
        Assert.assertEquals(ChatClientEndpoint2.TEXT, ChatClientEndpoint2.response);
    }
}

