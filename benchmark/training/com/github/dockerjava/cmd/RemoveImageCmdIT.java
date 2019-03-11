package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemoveImageCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(RemoveImageCmdIT.class);

    @Test
    public void removeImage() throws DockerException, InterruptedException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        RemoveImageCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        RemoveImageCmdIT.LOG.info("Committing container {}", container.toString());
        String imageId = dockerRule.getClient().commitCmd(container.getId()).exec();
        dockerRule.getClient().stopContainerCmd(container.getId()).exec();
        dockerRule.getClient().removeContainerCmd(container.getId()).exec();
        RemoveImageCmdIT.LOG.info("Removing image: {}", imageId);
        dockerRule.getClient().removeImageCmd(imageId).exec();
        List<Container> containers = dockerRule.getClient().listContainersCmd().withShowAll(true).exec();
        Matcher matcher = Matchers.not(Matchers.hasItem(hasField("id", Matchers.startsWith(imageId))));
        MatcherAssert.assertThat(containers, matcher);
    }

    @Test(expected = NotFoundException.class)
    public void removeNonExistingImage() throws DockerException, InterruptedException {
        dockerRule.getClient().removeImageCmd("non-existing").exec();
    }
}

