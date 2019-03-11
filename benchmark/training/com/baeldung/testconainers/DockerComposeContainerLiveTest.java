package com.baeldung.testconainers;


import java.io.File;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;


public class DockerComposeContainerLiveTest {
    @ClassRule
    public static DockerComposeContainer compose = new DockerComposeContainer(new File("src/test/resources/test-compose.yml")).withExposedService("simpleWebServer_1", 80);

    @Test
    public void givenSimpleWebServerContainer_whenGetReuqest_thenReturnsResponse() throws Exception {
        String address = (("http://" + (DockerComposeContainerLiveTest.compose.getServiceHost("simpleWebServer_1", 80))) + ":") + (DockerComposeContainerLiveTest.compose.getServicePort("simpleWebServer_1", 80));
        String response = simpleGetRequest(address);
        Assert.assertEquals(response, "Hello World!");
    }
}

