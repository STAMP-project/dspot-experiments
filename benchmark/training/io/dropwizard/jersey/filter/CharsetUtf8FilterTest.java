package io.dropwizard.jersey.filter;


import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON_TYPE;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class CharsetUtf8FilterTest {
    private ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);

    private ContainerResponseContext response = Mockito.mock(ContainerResponseContext.class);

    private CharsetUtf8Filter charsetUtf8Filter = new CharsetUtf8Filter();

    @Test
    public void testSetsCharsetEncoding() throws Exception {
        Mockito.when(response.getMediaType()).thenReturn(APPLICATION_JSON_TYPE);
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_TYPE);
        Mockito.when(response.getHeaders()).thenReturn(headers);
        charsetUtf8Filter.filter(request, response);
        assertThat(((MediaType) (headers.getFirst(CONTENT_TYPE)))).isEqualTo(MediaType.valueOf("application/json;charset=UTF-8"));
    }
}

