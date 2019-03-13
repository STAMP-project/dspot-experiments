package io.dropwizard.jetty;


import io.dropwizard.util.Maps;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class RoutingHandlerTest {
    private final Connector connector1 = Mockito.mock(Connector.class);

    private final Connector connector2 = Mockito.mock(Connector.class);

    private final Handler handler1 = Mockito.spy(new ContextHandler());

    private final Handler handler2 = Mockito.spy(new ContextHandler());

    private final RoutingHandler handler = new RoutingHandler(Maps.of(connector1, handler1, connector2, handler2));

    @Test
    public void startsAndStopsAllHandlers() throws Exception {
        handler1.setServer(Mockito.mock(Server.class));
        handler2.setServer(Mockito.mock(Server.class));
        handler.start();
        try {
            assertThat(handler1.isStarted()).isTrue();
            assertThat(handler2.isStarted()).isTrue();
        } finally {
            handler.stop();
        }
        assertThat(handler1.isStopped()).isTrue();
        assertThat(handler2.isStopped()).isTrue();
    }

    @Test
    public void routesRequestsToTheConnectorSpecificHandler() throws Exception {
        final HttpChannel channel = Mockito.mock(HttpChannel.class);
        Mockito.when(channel.getConnector()).thenReturn(connector1);
        final Request baseRequest = Mockito.mock(Request.class);
        Mockito.when(baseRequest.getHttpChannel()).thenReturn(channel);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        handler.handle("target", baseRequest, request, response);
        Mockito.verify(handler1).handle("target", baseRequest, request, response);
    }

    @Test
    public void withSessionHandler() throws Exception {
        final ContextHandler handler1 = new ContextHandler();
        final ServletContextHandler handler2 = new ServletContextHandler();
        final SessionHandler childHandler1 = new SessionHandler();
        handler2.setSessionHandler(childHandler1);
        final RoutingHandler handler = new RoutingHandler(Maps.of(connector1, handler1, connector2, handler2));
        new Server().setHandler(handler);
        handler.start();
        try {
            assertThat(getSessionHandlers(handler)).containsOnly(childHandler1);
        } finally {
            handler.stop();
        }
    }

    @Test
    public void withoutSessionHandler() throws Exception {
        new Server().setHandler(handler);
        handler.start();
        try {
            assertThat(getSessionHandlers(handler)).isEmpty();
        } finally {
            handler.stop();
        }
    }
}

