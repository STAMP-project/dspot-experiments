package org.testcontainers.containers.wait.strategy;


import org.junit.Test;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;


public class DockerHealthcheckWaitStrategyTest {
    private GenericContainer container;

    @Test
    public void startsOnceHealthy() {
        container.start();
    }

    @Test
    public void containerStartFailsIfContainerIsUnhealthy() {
        container.withCommand("tail", "-f", "/dev/null");
        assertThrows("Container launch fails when unhealthy", ContainerLaunchException.class, container::start);
    }
}

