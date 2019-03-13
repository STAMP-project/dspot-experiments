package io.dropwizard.jersey.filter;


import java.util.UUID;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class RequestIdFilterTest {
    private ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);

    private ContainerResponseContext response = Mockito.mock(ContainerResponseContext.class);

    private Logger logger = Mockito.mock(Logger.class);

    private RequestIdFilter requestIdFilter = new RequestIdFilter();

    private MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();

    @Test
    public void addsRandomRequestIdHeader() throws Exception {
        requestIdFilter.filter(request, response);
        String requestId = ((String) (headers.getFirst("X-Request-Id")));
        assertThat(requestId).isNotNull();
        assertThat(UUID.fromString(requestId)).isNotNull();
        Mockito.verify(logger).trace("method={} path={} request_id={} status={} length={}", "GET", "/some/path", requestId, 200, 2048);
    }

    @Test
    public void doesNotAddRandomRequestIdHeaderIfItExists() throws Exception {
        String existedRequestId = "e286b503-aa36-43fe-8312-95ee8773e348";
        headers.add("X-Request-Id", existedRequestId);
        Mockito.when(request.getHeaderString("X-Request-Id")).thenReturn(existedRequestId);
        requestIdFilter.filter(request, response);
        String requestId = ((String) (headers.getFirst("X-Request-Id")));
        assertThat(requestId).isEqualTo(existedRequestId);
        Mockito.verify(logger).trace("method={} path={} request_id={} status={} length={}", "GET", "/some/path", requestId, 200, 2048);
    }
}

