package io.dropwizard.jetty.setup;


import ServletRegistration.Dynamic;
import io.dropwizard.jetty.MutableServletContextHandler;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.WelcomeFilter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ServletEnvironmentTest {
    private final ServletHandler servletHandler = Mockito.mock(ServletHandler.class);

    private final MutableServletContextHandler handler = Mockito.mock(MutableServletContextHandler.class);

    private final ServletEnvironment environment = new ServletEnvironment(handler);

    @Test
    public void addsServletInstances() throws Exception {
        final Servlet servlet = Mockito.mock(Servlet.class);
        final ServletRegistration.Dynamic builder = environment.addServlet("servlet", servlet);
        assertThat(builder).isNotNull();
        final ArgumentCaptor<ServletHolder> holder = ArgumentCaptor.forClass(ServletHolder.class);
        Mockito.verify(servletHandler).addServlet(holder.capture());
        assertThat(holder.getValue().getName()).isEqualTo("servlet");
        assertThat(holder.getValue().getServlet()).isEqualTo(servlet);
    }

    @Test
    public void addsServletClasses() throws Exception {
        final ServletRegistration.Dynamic builder = environment.addServlet("servlet", GenericServlet.class);
        assertThat(builder).isNotNull();
        final ArgumentCaptor<ServletHolder> holder = ArgumentCaptor.forClass(ServletHolder.class);
        Mockito.verify(servletHandler).addServlet(holder.capture());
        assertThat(holder.getValue().getName()).isEqualTo("servlet");
        // this is ugly, but comparing classes sucks with these type bounds
        assertThat(holder.getValue().getHeldClass().equals(GenericServlet.class)).isTrue();
    }

    @Test
    public void addsFilterInstances() throws Exception {
        final Filter filter = Mockito.mock(Filter.class);
        final FilterRegistration.Dynamic builder = environment.addFilter("filter", filter);
        assertThat(builder).isNotNull();
        final ArgumentCaptor<FilterHolder> holder = ArgumentCaptor.forClass(FilterHolder.class);
        Mockito.verify(servletHandler).addFilter(holder.capture());
        assertThat(holder.getValue().getName()).isEqualTo("filter");
        assertThat(holder.getValue().getFilter()).isEqualTo(filter);
    }

    @Test
    public void addsFilterClasses() throws Exception {
        final FilterRegistration.Dynamic builder = environment.addFilter("filter", WelcomeFilter.class);
        assertThat(builder).isNotNull();
        final ArgumentCaptor<FilterHolder> holder = ArgumentCaptor.forClass(FilterHolder.class);
        Mockito.verify(servletHandler).addFilter(holder.capture());
        assertThat(holder.getValue().getName()).isEqualTo("filter");
        // this is ugly, but comparing classes sucks with these type bounds
        assertThat(holder.getValue().getHeldClass().equals(WelcomeFilter.class)).isTrue();
    }

    @Test
    public void addsServletListeners() throws Exception {
        final ServletContextListener listener = Mockito.mock(ServletContextListener.class);
        environment.addServletListeners(listener);
        Mockito.verify(handler).addEventListener(listener);
    }

    @Test
    public void addsProtectedTargets() throws Exception {
        environment.setProtectedTargets("/woo");
        Mockito.verify(handler).setProtectedTargets(new String[]{ "/woo" });
    }

    @Test
    public void setsResourceBase() throws Exception {
        environment.setResourceBase("/woo");
        Mockito.verify(handler).setResourceBase("/woo");
    }

    @Test
    public void setsInitParams() throws Exception {
        environment.setInitParameter("a", "b");
        Mockito.verify(handler).setInitParameter("a", "b");
    }

    @Test
    public void setsSessionHandlers() throws Exception {
        final SessionHandler sessionHandler = Mockito.mock(SessionHandler.class);
        environment.setSessionHandler(sessionHandler);
        Mockito.verify(handler).setSessionHandler(sessionHandler);
        Mockito.verify(handler).setSessionsEnabled(true);
    }

    @Test
    public void setsSecurityHandlers() throws Exception {
        final SecurityHandler securityHandler = Mockito.mock(SecurityHandler.class);
        environment.setSecurityHandler(securityHandler);
        Mockito.verify(handler).setSecurityHandler(securityHandler);
        Mockito.verify(handler).setSecurityEnabled(true);
    }

    @Test
    public void addsMimeMapping() {
        final MimeTypes mimeTypes = Mockito.mock(MimeTypes.class);
        Mockito.when(handler.getMimeTypes()).thenReturn(mimeTypes);
        environment.addMimeMapping("example/foo", "foo");
        Mockito.verify(mimeTypes).addMimeMapping("example/foo", "foo");
    }
}

