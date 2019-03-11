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


public class KillContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(KillContainerCmdIT.class);

    @Test
    public void killContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        KillContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        KillContainerCmdIT.LOG.info("Killing container: {}", container.getId());
        dockerRule.getClient().killContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        KillContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(Matchers.equalTo(false)));
        MatcherAssert.assertThat(inspectContainerResponse.getState().getExitCode(), Matchers.not(Matchers.equalTo(0)));
    }

    @Test(expected = NotFoundException.class)
    public void killNonExistingContainer() throws DockerException {
        dockerRule.getClient().killContainerCmd("non-existing").exec();
    }
}

