package org.testcontainers.junit;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.GenericContainer;


/**
 * Simple test case / demonstration of creating a fresh container image from a Dockerfile DSL when the test
 * is parameterized.
 */
@RunWith(Parameterized.class)
public class ParameterizedDockerfileContainerTest {
    private final String expectedVersion;

    @Rule
    public GenericContainer container;

    public ParameterizedDockerfileContainerTest(String baseImage, String expectedVersion) {
        container = withCommand("top");
        this.expectedVersion = expectedVersion;
    }

    @Test
    public void simpleTest() throws Exception {
        final String release = container.execInContainer("cat", "/etc/alpine-release").getStdout();
        assertTrue(("/etc/alpine-release starts with " + (expectedVersion)), release.startsWith(expectedVersion));
    }
}

