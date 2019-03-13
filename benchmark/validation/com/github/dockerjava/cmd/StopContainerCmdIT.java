package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StopContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(StopContainerCmdIT.class);

    @Test
    public void testStopContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        StopContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        StopContainerCmdIT.LOG.info("Stopping container: {}", container.getId());
        dockerRule.getClient().stopContainerCmd(container.getId()).withTimeout(2).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        StopContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(Matchers.equalTo(false)));
        final Integer exitCode = inspectContainerResponse.getState().getExitCode();
        MatcherAssert.assertThat(exitCode, Matchers.is(137));
    }

    @Test(expected = NotFoundException.class)
    public void testStopNonExistingContainer() throws DockerException {
        dockerRule.getClient().stopContainerCmd("non-existing").withTimeout(2).exec();
    }
}

