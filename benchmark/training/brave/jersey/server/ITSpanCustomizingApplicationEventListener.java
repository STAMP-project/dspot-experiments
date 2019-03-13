package brave.jersey.server;


import brave.test.http.ITServletContainer;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import zipkin2.Span;


public class ITSpanCustomizingApplicationEventListener extends ITServletContainer {
    @Override
    @Test
    public void reportsClientAddress() {
        throw new AssumptionViolatedException("TODO!");
    }

    @Test
    public void tagsResource() throws Exception {
        get("/foo");
        Span span = takeSpan();
        assertThat(span.tags()).containsEntry("jaxrs.resource.class", "TestResource").containsEntry("jaxrs.resource.method", "foo");
    }
}

