package io.dropwizard.jetty;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ContextRoutingHandlerTest {
    private final Request baseRequest = Mockito.mock(Request.class);

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final Handler handler1 = Mockito.mock(Handler.class);

    private final Handler handler2 = Mockito.mock(Handler.class);

    private ContextRoutingHandler handler;

    @Test
    public void routesToTheBestPrefixMatch() throws Exception {
        Mockito.when(baseRequest.getRequestURI()).thenReturn("/hello-world");
        handler.handle("/hello-world", baseRequest, request, response);
        Mockito.verify(handler1).handle("/hello-world", baseRequest, request, response);
        Mockito.verify(handler2, Mockito.never()).handle("/hello-world", baseRequest, request, response);
    }

    @Test
    public void routesToTheLongestPrefixMatch() throws Exception {
        Mockito.when(baseRequest.getRequestURI()).thenReturn("/admin/woo");
        handler.handle("/admin/woo", baseRequest, request, response);
        Mockito.verify(handler1, Mockito.never()).handle("/admin/woo", baseRequest, request, response);
        Mockito.verify(handler2).handle("/admin/woo", baseRequest, request, response);
    }

    @Test
    public void passesHandlingNonMatchingRequests() throws Exception {
        Mockito.when(baseRequest.getRequestURI()).thenReturn("WAT");
        handler.handle("WAT", baseRequest, request, response);
        Mockito.verify(handler1, Mockito.never()).handle("WAT", baseRequest, request, response);
        Mockito.verify(handler2, Mockito.never()).handle("WAT", baseRequest, request, response);
    }

    @Test
    public void startsAndStopsAllHandlers() throws Exception {
        handler.start();
        handler.stop();
        final InOrder inOrder = Mockito.inOrder(handler1, handler2);
        inOrder.verify(handler1).start();
        inOrder.verify(handler2).start();
    }
}

