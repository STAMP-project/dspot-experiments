package org.baeldung.client;


import HttpMethod.GET;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;


/**
 * This test requires a localhost server over HTTPS <br>
 * It should only be manually run, not part of the automated build
 */
public class RestClientLiveManualTest {
    final String urlOverHttps = "http://localhost:8082/spring-security-rest-basic-auth/api/bars/1";

    @Test
    public final void givenAcceptingAllCertificatesUsing4_4_whenHttpsUrlIsConsumed_thenCorrect() throws IOException, ClientProtocolException {
        final CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        final HttpGet getMethod = new HttpGet(urlOverHttps);
        final HttpResponse response = httpClient.execute(getMethod);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenAcceptingAllCertificatesUsing4_4_whenHttpsUrlIsConsumedUsingRestTemplate_thenCorrect() throws IOException, ClientProtocolException {
        final CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        final ResponseEntity<String> response = new org.springframework.web.client.RestTemplate(requestFactory).exchange(urlOverHttps, GET, null, String.class);
        Assert.assertThat(response.getStatusCode().value(), Matchers.equalTo(200));
    }
}

