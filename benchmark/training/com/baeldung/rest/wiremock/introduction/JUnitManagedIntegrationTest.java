package com.baeldung.rest.wiremock.introduction;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class JUnitManagedIntegrationTest {
    private static final String BAELDUNG_WIREMOCK_PATH = "/baeldung/wiremock";

    private static final String APPLICATION_JSON = "application/json";

    static int port;

    static {
        try {
            // Get a free port
            ServerSocket s = new ServerSocket(0);
            JUnitManagedIntegrationTest.port = s.getLocalPort();
            s.close();
        } catch (IOException e) {
            // No OPS
        }
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(JUnitManagedIntegrationTest.port);

    @Test
    public void givenJUnitManagedServer_whenMatchingURL_thenCorrect() throws IOException {
        stubFor(get(urlPathMatching("/baeldung/.*")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", JUnitManagedIntegrationTest.APPLICATION_JSON).withBody("\"testing-library\": \"WireMock\"")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(String.format("http://localhost:%s/baeldung/wiremock", JUnitManagedIntegrationTest.port));
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = JUnitManagedIntegrationTest.convertHttpResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)));
        Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        Assert.assertEquals(JUnitManagedIntegrationTest.APPLICATION_JSON, httpResponse.getFirstHeader("Content-Type").getValue());
        Assert.assertEquals("\"testing-library\": \"WireMock\"", stringResponse);
    }

    @Test
    public void givenJUnitManagedServer_whenMatchingHeaders_thenCorrect() throws IOException {
        stubFor(get(urlPathEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)).withHeader("Accept", matching("text/.*")).willReturn(aResponse().withStatus(503).withHeader("Content-Type", "text/html").withBody("!!! Service Unavailable !!!")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(String.format("http://localhost:%s/baeldung/wiremock", JUnitManagedIntegrationTest.port));
        request.addHeader("Accept", "text/html");
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = JUnitManagedIntegrationTest.convertHttpResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)));
        Assert.assertEquals(503, httpResponse.getStatusLine().getStatusCode());
        Assert.assertEquals("text/html", httpResponse.getFirstHeader("Content-Type").getValue());
        Assert.assertEquals("!!! Service Unavailable !!!", stringResponse);
    }

    @Test
    public void givenJUnitManagedServer_whenMatchingBody_thenCorrect() throws IOException {
        stubFor(post(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)).withHeader("Content-Type", equalTo(JUnitManagedIntegrationTest.APPLICATION_JSON)).withRequestBody(containing("\"testing-library\": \"WireMock\"")).withRequestBody(containing("\"creator\": \"Tom Akehurst\"")).withRequestBody(containing("\"website\": \"wiremock.org\"")).willReturn(aResponse().withStatus(200)));
        InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
        String jsonString = JUnitManagedIntegrationTest.convertInputStreamToString(jsonInputStream);
        StringEntity entity = new StringEntity(jsonString);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("http://localhost:%s/baeldung/wiremock", JUnitManagedIntegrationTest.port));
        request.addHeader("Content-Type", JUnitManagedIntegrationTest.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        verify(postRequestedFor(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)).withHeader("Content-Type", equalTo(JUnitManagedIntegrationTest.APPLICATION_JSON)));
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void givenJUnitManagedServer_whenNotUsingPriority_thenCorrect() throws IOException {
        stubFor(get(urlPathMatching("/baeldung/.*")).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlPathEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)).withHeader("Accept", matching("text/.*")).willReturn(aResponse().withStatus(503)));
        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();
        verify(getRequestedFor(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)));
        Assert.assertEquals(503, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void givenJUnitManagedServer_whenUsingPriority_thenCorrect() throws IOException {
        stubFor(get(urlPathMatching("/baeldung/.*")).atPriority(1).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlPathEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)).atPriority(2).withHeader("Accept", matching("text/.*")).willReturn(aResponse().withStatus(503)));
        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();
        verify(getRequestedFor(urlEqualTo(JUnitManagedIntegrationTest.BAELDUNG_WIREMOCK_PATH)));
        Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }
}

