package org.baeldung.httpclient.base;


import ContentType.TEXT_HTML;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientBasicLiveTest {
    private static final String SAMPLE_URL = "http://www.github.com";

    private CloseableHttpClient instance;

    private CloseableHttpResponse response;

    // tests
    // simple request - response
    @Test
    public final void whenExecutingBasicGetRequest_thenNoExceptions() throws IOException, ClientProtocolException {
        response = instance.execute(new HttpGet(HttpClientBasicLiveTest.SAMPLE_URL));
    }

    @Test
    public final void givenGetRequestExecuted_whenAnalyzingTheResponse_thenCorrectStatusCode() throws IOException, ClientProtocolException {
        response = instance.execute(new HttpGet(HttpClientBasicLiveTest.SAMPLE_URL));
        final int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenGetRequestExecuted_whenAnalyzingTheResponse_thenCorrectMimeType() throws IOException, ClientProtocolException {
        response = instance.execute(new HttpGet(HttpClientBasicLiveTest.SAMPLE_URL));
        final String contentMimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        Assert.assertThat(contentMimeType, Matchers.equalTo(TEXT_HTML.getMimeType()));
    }

    @Test
    public final void givenGetRequestExecuted_whenAnalyzingTheResponse_thenCorrectBody() throws IOException, ClientProtocolException {
        response = instance.execute(new HttpGet(HttpClientBasicLiveTest.SAMPLE_URL));
        final String bodyAsString = EntityUtils.toString(response.getEntity());
        Assert.assertThat(bodyAsString, Matchers.notNullValue());
    }
}

