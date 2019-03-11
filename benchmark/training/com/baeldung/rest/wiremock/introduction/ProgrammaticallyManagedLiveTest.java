package com.baeldung.rest.wiremock.introduction;


import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;


public class ProgrammaticallyManagedLiveTest {
    private static final String BAELDUNG_PATH = "/baeldung";

    private WireMockServer wireMockServer = new WireMockServer();

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    public void givenProgrammaticallyManagedServer_whenUsingSimpleStubbing_thenCorrect() throws IOException {
        wireMockServer.start();
        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo(ProgrammaticallyManagedLiveTest.BAELDUNG_PATH)).willReturn(aResponse().withBody("Welcome to Baeldung!")));
        HttpGet request = new HttpGet("http://localhost:8080/baeldung");
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = ProgrammaticallyManagedLiveTest.convertResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(ProgrammaticallyManagedLiveTest.BAELDUNG_PATH)));
        Assert.assertEquals("Welcome to Baeldung!", stringResponse);
        wireMockServer.stop();
    }
}

