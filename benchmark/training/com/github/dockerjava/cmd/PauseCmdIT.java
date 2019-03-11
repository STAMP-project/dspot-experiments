package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.utils.ContainerUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PauseCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(PauseCmdIT.class);

    @Test
    public void pauseRunningContainer() {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        PauseCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        ContainerUtils.startContainer(dockerRule.getClient(), container);
        ContainerUtils.pauseContainer(dockerRule.getClient(), container);
    }

    @Test(expected = NotFoundException.class)
    public void pauseNonExistingContainer() {
        dockerRule.getClient().pauseContainerCmd("non-existing").exec();
    }

    @Test(expected = InternalServerErrorException.class)
    public void pauseStoppedContainer() {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        PauseCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        ContainerUtils.startContainer(dockerRule.getClient(), container);
        ContainerUtils.stopContainer(dockerRule.getClient(), container);
        dockerRule.getClient().pauseContainerCmd(container.getId()).exec();
    }

    @Test(expected = InternalServerErrorException.class)
    public void pausePausedContainer() {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        PauseCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        ContainerUtils.startContainer(dockerRule.getClient(), container);
        ContainerUtils.pauseContainer(dockerRule.getClient(), container);
        dockerRule.getClient().pauseContainerCmd(container.getId()).exec();
    }

    @Test(expected = InternalServerErrorException.class)
    public void pauseCreatedContainer() {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        PauseCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().pauseContainerCmd(container.getId()).exec();
    }
}

