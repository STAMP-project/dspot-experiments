package org.testcontainers.junit;


import java.io.File;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;


public class DockerComposeLocalImageTest {
    @Test
    public void usesLocalImageEvenWhenPullFails() throws InterruptedException {
        tagImage("redis:4.0.10", "redis-local", "latest");
        DockerComposeContainer composeContainer = new DockerComposeContainer(new File("src/test/resources/local-compose-test.yml")).withExposedService("redis", 6379);
        composeContainer.start();
    }
}

