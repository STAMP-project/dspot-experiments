package org.testcontainers.dockerclient;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;


@RunWith(Parameterized.class)
public class ImagePullTest {
    private String image;

    public ImagePullTest(String image) {
        this.image = image;
    }

    @Test
    public void test() {
        try (final GenericContainer container = new GenericContainer(image).withCommand("/bin/sh", "-c", "sleep 0").withStartupCheckStrategy(new OneShotStartupCheckStrategy())) {
            container.start();
            // do nothing other than start and stop
        }
    }
}

