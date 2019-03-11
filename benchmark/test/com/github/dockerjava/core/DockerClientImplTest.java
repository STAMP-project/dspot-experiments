package com.github.dockerjava.core;


import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class DockerClientImplTest {
    @Test
    public void configuredInstanceAuthConfig() throws Exception {
        // given a config with null serverAddress
        DefaultDockerClientConfig dockerClientConfig = new DefaultDockerClientConfig(URI.create("tcp://foo"), null, null, null, "", "", "", null);
        DockerClientImpl dockerClient = DockerClientImpl.getInstance(dockerClientConfig);
        // when we get the auth config
        try {
            dockerClient.authConfig();
            throw new AssertionError();
        } catch (NullPointerException e) {
            // then we get a NPE with expected message
            Assert.assertEquals(e.getMessage(), "Configured serverAddress is null.");
        }
    }

    @Test
    public void defaultInstanceAuthConfig() throws Exception {
        System.setProperty("user.home", "target/test-classes/someHomeDir");
        // given a default client
        DockerClientImpl dockerClient = DockerClientImpl.getInstance();
        // when we get the auth config
        dockerClient.authConfig();
        // then we do not get an exception
    }
}

