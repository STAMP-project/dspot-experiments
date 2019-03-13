package io.dropwizard.jersey.caching;


import HttpHeaders.CACHE_CONTROL;
import io.dropwizard.jersey.AbstractJerseyTest;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class CacheControlledResponseFeatureTest extends AbstractJerseyTest {
    @Test
    public void immutableResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/immutable").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-transform, max-age=31536000");
    }

    @Test
    public void privateResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/private").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("private, no-transform");
    }

    @Test
    public void maxAgeResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/max-age").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-transform, max-age=1123200");
    }

    @Test
    public void noCacheResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/no-cache").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-cache, no-transform");
    }

    @Test
    public void noStoreResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/no-store").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-store, no-transform");
    }

    @Test
    public void noTransformResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/no-transform").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).isNull();
    }

    @Test
    public void mustRevalidateResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/must-revalidate").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-transform, must-revalidate");
    }

    @Test
    public void proxyRevalidateResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/proxy-revalidate").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-transform, proxy-revalidate");
    }

    @Test
    public void sharedMaxAgeResponsesHaveCacheControlHeaders() throws Exception {
        final Response response = target("/caching/shared-max-age").request().get();
        assertThat(response.getHeaders().get(CACHE_CONTROL)).containsOnly("no-transform, s-maxage=46800");
    }
}

