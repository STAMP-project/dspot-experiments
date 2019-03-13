package org.testcontainers.junit;


import java.util.concurrent.TimeoutException;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


/**
 * Simple tests of named network modes - more may be possible, but may not be reproducible
 * without other setup steps.
 */
public class DockerNetworkModeTest {
    @ClassRule
    public static GenericContainer noNetwork = new GenericContainer("alpine:3.2").withNetworkMode("none").withCommand("ping -c 5 www.google.com");

    @ClassRule
    public static GenericContainer hostNetwork = new GenericContainer("alpine:3.2").withNetworkMode("host").withCommand("ping -c 5 www.google.com");

    @ClassRule
    public static GenericContainer bridgedNetwork = new GenericContainer("alpine:3.2").withNetworkMode("bridge").withCommand("ping -c 5 www.google.com");

    @Test
    public void testNoNetworkContainer() throws TimeoutException {
        String output = getContainerOutput(DockerNetworkModeTest.noNetwork);
        assertTrue("'none' network causes a network access error", output.contains("bad address"));
    }

    @Test
    public void testHostNetworkContainer() throws TimeoutException {
        String output = getContainerOutput(DockerNetworkModeTest.hostNetwork);
        assertTrue("'host' network can access the internet", output.contains("seq=1"));
    }

    @Test
    public void testBridgedNetworkContainer() throws TimeoutException {
        String output = getContainerOutput(DockerNetworkModeTest.bridgedNetwork);
        assertTrue("'bridge' network can access the internet", output.contains("seq=1"));
    }
}

