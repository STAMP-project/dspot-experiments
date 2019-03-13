package org.testcontainers.utility;


import DockerStatus.DOCKER_TIMESTAMP_ZERO;
import InspectContainerResponse.ContainerState;
import java.time.Duration;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DockerStatusTest {
    private static Instant now = Instant.now();

    private static ContainerState running = DockerStatusTest.buildState(true, false, DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(30)), DOCKER_TIMESTAMP_ZERO);

    private static ContainerState runningVariant = DockerStatusTest.buildState(true, false, DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(30)), "");

    private static ContainerState shortRunning = DockerStatusTest.buildState(true, false, DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(10)), DOCKER_TIMESTAMP_ZERO);

    private static ContainerState created = DockerStatusTest.buildState(false, false, DOCKER_TIMESTAMP_ZERO, DOCKER_TIMESTAMP_ZERO);

    // a container in the "created" state is not running, and has both startedAt and finishedAt empty.
    private static ContainerState createdVariant = DockerStatusTest.buildState(false, false, null, null);

    private static ContainerState exited = DockerStatusTest.buildState(false, false, DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(100)), DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(90)));

    private static ContainerState paused = DockerStatusTest.buildState(false, true, DockerStatusTest.buildTimestamp(DockerStatusTest.now.minusMillis(100)), DOCKER_TIMESTAMP_ZERO);

    private static Duration minimumDuration = Duration.ofMillis(20);

    @Test
    public void testRunning() throws Exception {
        Assert.assertTrue(DockerStatus.isContainerRunning(DockerStatusTest.running, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertTrue(DockerStatus.isContainerRunning(DockerStatusTest.runningVariant, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertFalse(DockerStatus.isContainerRunning(DockerStatusTest.shortRunning, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertFalse(DockerStatus.isContainerRunning(DockerStatusTest.created, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertFalse(DockerStatus.isContainerRunning(DockerStatusTest.createdVariant, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertFalse(DockerStatus.isContainerRunning(DockerStatusTest.exited, DockerStatusTest.minimumDuration, DockerStatusTest.now));
        Assert.assertFalse(DockerStatus.isContainerRunning(DockerStatusTest.paused, DockerStatusTest.minimumDuration, DockerStatusTest.now));
    }

    @Test
    public void testStopped() throws Exception {
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.running));
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.runningVariant));
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.shortRunning));
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.created));
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.createdVariant));
        Assert.assertTrue(DockerStatus.isContainerStopped(DockerStatusTest.exited));
        Assert.assertFalse(DockerStatus.isContainerStopped(DockerStatusTest.paused));
    }
}

