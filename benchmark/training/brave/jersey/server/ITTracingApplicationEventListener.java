package brave.jersey.server;


import brave.test.http.ITServletContainer;
import okhttp3.Response;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import zipkin2.Span;


public class ITTracingApplicationEventListener extends ITServletContainer {
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

    /**
     * Tests that the span propagates between under asynchronous callbacks managed by jersey.
     */
    @Test
    public void managedAsync() throws Exception {
        Response response = get("/managedAsync");
        assertThat(response.isSuccessful()).withFailMessage(("not successful: " + response)).isTrue();
        takeSpan();
    }
}

