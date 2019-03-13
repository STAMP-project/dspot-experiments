package org.baeldung.httpclient.sec;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/* NOTE : Need module spring-security-rest-basic-auth to be running */
public class HttpClientAuthLiveTest {
    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://localhost:8081/spring-security-rest-basic-auth/api/foos/1";

    private static final String DEFAULT_USER = "user1";

    private static final String DEFAULT_PASS = "user1Pass";

    private CloseableHttpClient client;

    private CloseableHttpResponse response;

    // tests
    @Test
    public final void whenExecutingBasicGetRequestWithBasicAuthenticationEnabled_thenSuccess() throws IOException {
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider()).build();
        response = client.execute(new HttpGet(HttpClientAuthLiveTest.URL_SECURED_BY_BASIC_AUTHENTICATION));
        final int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthenticationIsPreemptive_whenExecutingBasicGetRequestWithBasicAuthenticationEnabled_thenSuccess() throws IOException {
        client = HttpClientBuilder.create().build();
        response = client.execute(new HttpGet(HttpClientAuthLiveTest.URL_SECURED_BY_BASIC_AUTHENTICATION), context());
        final int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthorizationHeaderIsSetManually_whenExecutingGetRequest_thenSuccess() throws IOException {
        client = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet(HttpClientAuthLiveTest.URL_SECURED_BY_BASIC_AUTHENTICATION);
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader(HttpClientAuthLiveTest.DEFAULT_USER, HttpClientAuthLiveTest.DEFAULT_PASS));
        response = client.execute(request);
        final int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthorizationHeaderIsSetManually_whenExecutingGetRequest_thenSuccess2() throws IOException {
        final HttpGet request = new HttpGet(HttpClientAuthLiveTest.URL_SECURED_BY_BASIC_AUTHENTICATION);
        final String auth = ((HttpClientAuthLiveTest.DEFAULT_USER) + ":") + (HttpClientAuthLiveTest.DEFAULT_PASS);
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        final String authHeader = "Basic " + (new String(encodedAuth));
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        client = HttpClientBuilder.create().build();
        response = client.execute(request);
        final int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
    }
}

