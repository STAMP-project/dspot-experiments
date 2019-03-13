package org.testcontainers.junit.wait.strategy;


import java.time.Duration;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;


/**
 * Test wait strategy with overloaded waitingFor methods.
 * Other implementations of WaitStrategy are tested through backwards compatible wait strategy tests
 */
public class HostPortWaitStrategyTest {
    private static final String IMAGE_NAME = "alpine:3.7";

    @ClassRule
    public static GenericContainer container = new GenericContainer(HostPortWaitStrategyTest.IMAGE_NAME).withExposedPorts().withCommand("sh", "-c", "while true; do nc -lp 8080; done").withExposedPorts(8080).waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(10)));

    @Test
    public void testWaiting() {
        pass("Container starts after waiting");
    }
}

