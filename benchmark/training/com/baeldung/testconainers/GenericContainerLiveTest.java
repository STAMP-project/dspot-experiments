package com.baeldung.testconainers;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;
import org.testcontainers.containers.GenericContainer;


@Testable
public class GenericContainerLiveTest {
    @ClassRule
    public static GenericContainer simpleWebServer = new GenericContainer("alpine:3.2").withExposedPorts(80).withCommand("/bin/sh", "-c", ("while true; do echo " + "\"HTTP/1.1 200 OK\n\nHello World!\" | nc -l -p 80; done"));

    @Test
    public void givenSimpleWebServerContainer_whenGetReuqest_thenReturnsResponse() throws Exception {
        String address = (("http://" + (GenericContainerLiveTest.simpleWebServer.getContainerIpAddress())) + ":") + (GenericContainerLiveTest.simpleWebServer.getMappedPort(80));
        String response = simpleGetRequest(address);
        Assert.assertEquals(response, "Hello World!");
    }
}

