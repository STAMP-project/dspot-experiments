package brave.jaxrs2;


import brave.test.http.ITServletContainer;
import java.util.Arrays;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.ws.rs.core.Application;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import zipkin2.Span;


public class ITSpanCustomizingContainerFilter extends ITServletContainer {
    @Override
    @Test
    public void reportsClientAddress() {
        throw new AssumptionViolatedException("ContainerRequestContext doesn't include remote address");
    }

    @Test
    public void tagsResource() throws Exception {
        get("/foo");
        Span span = takeSpan();
        assertThat(span.tags()).containsEntry("jaxrs.resource.class", "TestResource").containsEntry("jaxrs.resource.method", "foo");
    }

    static class TaggingBootstrap extends ResteasyBootstrap {
        TaggingBootstrap(Object resource) {
            deployment = new ResteasyDeployment();
            deployment.setApplication(new Application() {
                @Override
                public Set<Object> getSingletons() {
                    return new java.util.LinkedHashSet(Arrays.asList(resource, SpanCustomizingContainerFilter.create()));
                }
            });
        }

        @Override
        public void contextInitialized(ServletContextEvent event) {
            ServletContext servletContext = event.getServletContext();
            ListenerBootstrap config = new ListenerBootstrap(servletContext);
            servletContext.setAttribute(ResteasyDeployment.class.getName(), deployment);
            deployment.getDefaultContextObjects().put(ResteasyConfiguration.class, config);
            config.createDeployment();
            deployment.start();
        }
    }
}

