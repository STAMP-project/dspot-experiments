package io.dropwizard.jersey.filter;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class RuntimeFilterTest {
    private ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);

    private ContainerResponseContext response = Mockito.mock(ContainerResponseContext.class);

    private RuntimeFilter runtimeFilter = new RuntimeFilter();

    @Test
    public void testSetsCurrentTimeProperty() throws Exception {
        runtimeFilter.setCurrentTimeProvider(() -> 1510330745000000L);
        runtimeFilter.filter(request);
        Mockito.verify(request).setProperty("io.dropwizard.jersey.filter.runtime", 1510330745000000L);
    }

    @Test
    public void testAddsXRuntimeHeader() throws Exception {
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        Mockito.when(response.getHeaders()).thenReturn(headers);
        Mockito.when(request.getProperty("io.dropwizard.jersey.filter.runtime")).thenReturn(1510330745000000L);
        runtimeFilter.setCurrentTimeProvider(() -> 1510330868000000L);
        runtimeFilter.filter(request, response);
        assertThat(headers.getFirst("X-Runtime")).isEqualTo("0.123000");
    }
}

