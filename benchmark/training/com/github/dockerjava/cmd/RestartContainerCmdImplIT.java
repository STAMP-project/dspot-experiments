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


public class RestartContainerCmdImplIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(RestartContainerCmdImplIT.class);

    @Test
    public void restartContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        RestartContainerCmdImplIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        RestartContainerCmdImplIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        String startTime = inspectContainerResponse.getState().getStartedAt();
        dockerRule.getClient().restartContainerCmd(container.getId()).withtTimeout(2).exec();
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        RestartContainerCmdImplIT.LOG.info("Container Inspect After Restart: {}", inspectContainerResponse2.toString());
        String startTime2 = inspectContainerResponse2.getState().getStartedAt();
        MatcherAssert.assertThat(startTime, Matchers.not(Matchers.equalTo(startTime2)));
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(Matchers.equalTo(true)));
        dockerRule.getClient().killContainerCmd(container.getId()).exec();
    }

    @Test(expected = NotFoundException.class)
    public void restartNonExistingContainer() throws DockerException, InterruptedException {
        dockerRule.getClient().restartContainerCmd("non-existing").exec();
    }
}

