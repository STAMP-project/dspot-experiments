package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemoveContainerCmdImplIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(RemoveContainerCmdImplIT.class);

    @Test
    public void removeContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        RemoveContainerCmdImplIT.LOG.info("Removing container: {}", container.getId());
        dockerRule.getClient().removeContainerCmd(container.getId()).exec();
        List<Container> containers2 = dockerRule.getClient().listContainersCmd().withShowAll(true).exec();
        Matcher matcher = Matchers.not(Matchers.hasItem(hasField("id", Matchers.startsWith(container.getId()))));
        MatcherAssert.assertThat(containers2, matcher);
    }

    @Test(expected = NotFoundException.class)
    public void removeNonExistingContainer() throws DockerException {
        dockerRule.getClient().removeContainerCmd("non-existing").exec();
    }
}

