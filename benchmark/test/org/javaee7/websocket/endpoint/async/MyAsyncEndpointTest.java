package org.javaee7.websocket.endpoint.async;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jacek Jackowiak
 */
@RunWith(Arquillian.class)
public class MyAsyncEndpointTest {
    private static final String TEST_MESSAGE = "test";

    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void shouldReceiveAsyncTextMessage() throws IOException, URISyntaxException, DeploymentException {
        MyAsyncEndpointTextClient endpoint = new MyAsyncEndpointTextClient();
        Session session = connectToEndpoint(endpoint, "text");
        session.getAsyncRemote().sendText(MyAsyncEndpointTest.TEST_MESSAGE);
        await().untilCall(to(endpoint).getReceivedMessage(), is(equalTo(MyAsyncEndpointTest.TEST_MESSAGE)));
    }

    @Test
    @RunAsClient
    public void shouldReceiveAsyncByteBufferMessage() throws IOException, URISyntaxException, DeploymentException {
        final ByteBuffer buffer = ByteBuffer.wrap(MyAsyncEndpointTest.TEST_MESSAGE.getBytes());
        MyAsyncEndpointByteBufferClient endpoint = new MyAsyncEndpointByteBufferClient();
        Session session = connectToEndpoint(endpoint, "bytebuffer");
        session.getAsyncRemote().sendBinary(buffer);
        await().untilCall(to(endpoint).getReceivedMessage(), is(notNullValue()));
        String receivedString = bufferToString(endpoint.getReceivedMessage());
        MatcherAssert.assertThat(receivedString, is(equalTo(MyAsyncEndpointTest.TEST_MESSAGE)));
    }
}

