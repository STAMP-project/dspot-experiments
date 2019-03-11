package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.junit.DockerRule;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommitCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CommitCmdIT.class);

    @Test
    public void commit() throws DockerException, InterruptedException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("touch", "/test").exec();
        CommitCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        CommitCmdIT.LOG.info("Committing container: {}", container.toString());
        String imageId = dockerRule.getClient().commitCmd(container.getId()).exec();
        // swarm needs some time to reflect new images
        synchronized(this) {
            wait(5000);
        }
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        CommitCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        MatcherAssert.assertThat(inspectImageResponse, hasField("container", Matchers.startsWith(container.getId())));
        MatcherAssert.assertThat(inspectImageResponse.getContainerConfig().getImage(), Matchers.equalTo(DockerRule.DEFAULT_IMAGE));
        InspectImageResponse busyboxImg = dockerRule.getClient().inspectImageCmd("busybox").exec();
        MatcherAssert.assertThat(inspectImageResponse.getParent(), Matchers.equalTo(busyboxImg.getId()));
    }

    @Test
    public void commitWithLabels() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("touch", "/test").exec();
        CommitCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        Integer status = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        MatcherAssert.assertThat(status, Matchers.is(0));
        CommitCmdIT.LOG.info("Committing container: {}", container.toString());
        Map<String, String> labels = ImmutableMap.of("label1", "abc", "label2", "123");
        String imageId = dockerRule.getClient().commitCmd(container.getId()).withLabels(labels).exec();
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        CommitCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        // use config here since containerConfig contains the configuration of the container which was
        // committed to the container
        // https://stackoverflow.com/questions/36216220/what-is-different-of-config-and-containerconfig-of-docker-inspect
        Map<String, String> responseLabels = inspectImageResponse.getConfig().getLabels();
        // swarm will attach additional labels here
        MatcherAssert.assertThat(responseLabels.size(), Matchers.greaterThanOrEqualTo(2));
        MatcherAssert.assertThat(responseLabels.get("label1"), Matchers.equalTo("abc"));
        MatcherAssert.assertThat(responseLabels.get("label2"), Matchers.equalTo("123"));
    }

    @Test(expected = NotFoundException.class)
    public void commitNonExistingContainer() throws DockerException {
        dockerRule.getClient().commitCmd("non-existent").exec();
    }
}

