package spark.embeddedserver.jetty.websocket;


import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.junit.Assert;
import org.junit.Test;
import spark.embeddedserver.jetty.websocket.WebSocketCreatorFactory.SparkWebSocketCreator;


public class WebSocketCreatorFactoryTest {
    @Test
    public void testCreateWebSocketHandler() {
        WebSocketCreator annotated = WebSocketCreatorFactory.create(new WebSocketHandlerClassWrapper(WebSocketCreatorFactoryTest.AnnotatedHandler.class));
        Assert.assertTrue((annotated instanceof SparkWebSocketCreator));
        Assert.assertTrue(((SparkWebSocketCreator.class.cast(annotated).getHandler()) instanceof WebSocketCreatorFactoryTest.AnnotatedHandler));
        WebSocketCreator listener = WebSocketCreatorFactory.create(new WebSocketHandlerClassWrapper(WebSocketCreatorFactoryTest.ListenerHandler.class));
        Assert.assertTrue((listener instanceof SparkWebSocketCreator));
        Assert.assertTrue(((SparkWebSocketCreator.class.cast(listener).getHandler()) instanceof WebSocketCreatorFactoryTest.ListenerHandler));
    }

    @Test
    public void testCannotCreateInvalidHandlers() {
        try {
            WebSocketCreatorFactory.create(new WebSocketHandlerClassWrapper(WebSocketCreatorFactoryTest.InvalidHandler.class));
            Assert.fail("Handler creation should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("WebSocket handler must implement 'WebSocketListener' or be annotated as '@WebSocket'", ex.getMessage());
        }
    }

    @Test
    public void testCreate_whenInstantiationException() throws Exception {
        try {
            WebSocketCreatorFactory.create(new WebSocketHandlerClassWrapper(WebSocketCreatorFactoryTest.FailingHandler.class));
            Assert.fail("Handler creation should have thrown a RunTimeException");
        } catch (RuntimeException ex) {
            Assert.assertEquals("Could not instantiate websocket handler", ex.getMessage());
        }
    }

    @WebSocket
    class FailingHandler {}

    @WebSocket
    static class AnnotatedHandler {}

    static class ListenerHandler extends WebSocketAdapter {}

    static class InvalidHandler {}
}

