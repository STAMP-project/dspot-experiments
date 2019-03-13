package org.testcontainers.junit;


import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


/**
 * Simple test case / demonstration of creating a fresh container image from a Dockerfile DSL
 */
public class DockerfileContainerTest {
    @Rule
    public GenericContainer dslContainer = withExposedPorts(80);

    @Test
    public void simpleDslTest() throws IOException {
        String address = String.format("http://%s:%s", dslContainer.getContainerIpAddress(), dslContainer.getMappedPort(80));
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(address);
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals("A container built from a dockerfile can run nginx as expected, and returns a good status code", 200, response.getStatusLine().getStatusCode());
            assertTrue("A container built from a dockerfile can run nginx as expected, and returns an expected Server header", response.getHeaders("Server")[0].getValue().contains("nginx"));
        }
    }
}

