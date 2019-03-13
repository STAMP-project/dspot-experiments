package io.dropwizard.jetty;


import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class NonblockingServletHolderTest {
    private final Servlet servlet = Mockito.mock(Servlet.class);

    private final NonblockingServletHolder holder = new NonblockingServletHolder(servlet);

    private final Request baseRequest = Mockito.mock(Request.class);

    private final ServletRequest request = Mockito.mock(ServletRequest.class);

    private final ServletResponse response = Mockito.mock(ServletResponse.class);

    @Test
    public void hasAServlet() throws Exception {
        assertThat(holder.getServlet()).isEqualTo(servlet);
    }

    @Test
    public void servicesRequests() throws Exception {
        holder.handle(baseRequest, request, response);
        Mockito.verify(servlet).service(request, response);
    }

    @Test
    public void servicesRequestHandleEofException() throws Exception {
        Mockito.doThrow(new EofException()).when(servlet).service(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        assertThatCode(() -> {
            holder.handle(baseRequest, request, response);
        }).doesNotThrowAnyException();
        Mockito.verify(servlet).service(request, response);
    }

    @Test
    public void servicesRequestException() throws Exception {
        Mockito.doThrow(new IOException()).when(servlet).service(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        assertThatExceptionOfType(IOException.class).isThrownBy(() -> {
            holder.handle(baseRequest, request, response);
        });
    }

    @Test
    public void temporarilyDisablesAsyncRequestsIfDisabled() throws Exception {
        holder.setAsyncSupported(false);
        holder.handle(baseRequest, request, response);
        final InOrder inOrder = Mockito.inOrder(baseRequest, servlet);
        inOrder.verify(baseRequest).setAsyncSupported(false, null);
        inOrder.verify(servlet).service(request, response);
    }

    @Test
    public void isEagerlyInitialized() throws Exception {
        assertThat(holder.getInitOrder()).isEqualTo(1);
    }
}

