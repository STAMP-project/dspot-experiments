package com.baeldung.blade.sample;


import AttributesExampleController.HEADER;
import AttributesExampleController.REQUEST_VALUE;
import AttributesExampleController.SESSION_VALUE;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


public class AttributesExampleControllerLiveTest {
    @Test
    public void givenRequestAttribute_whenSet_thenRetrieveWithGet() throws Exception {
        final HttpUriRequest request = new HttpGet("http://localhost:9000/request-attribute-example");
        try (final CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request)) {
            assertThat(EntityUtils.toString(httpResponse.getEntity())).isEqualTo(REQUEST_VALUE);
        }
    }

    @Test
    public void givenSessionAttribute_whenSet_thenRetrieveWithGet() throws Exception {
        final HttpUriRequest request = new HttpGet("http://localhost:9000/session-attribute-example");
        try (final CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request)) {
            assertThat(EntityUtils.toString(httpResponse.getEntity())).isEqualTo(SESSION_VALUE);
        }
    }

    @Test
    public void givenHeader_whenSet_thenRetrieveWithGet() throws Exception {
        final HttpUriRequest request = new HttpGet("http://localhost:9000/header-example");
        request.addHeader("a-header", "foobar");
        try (final CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request)) {
            assertThat(httpResponse.getHeaders("a-header")[0].getValue()).isEqualTo("foobar");
        }
    }

    @Test
    public void givenNoHeader_whenSet_thenRetrieveDefaultValueWithGet() throws Exception {
        final HttpUriRequest request = new HttpGet("http://localhost:9000/header-example");
        try (final CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request)) {
            assertThat(httpResponse.getHeaders("a-header")[0].getValue()).isEqualTo(HEADER);
        }
    }
}

