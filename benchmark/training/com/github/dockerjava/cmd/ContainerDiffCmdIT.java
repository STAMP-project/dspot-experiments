package com.github.dockerjava.cmd;


import ch.lambdaj.Lambda;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ChangeLog;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.junit.DockerRule;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContainerDiffCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(ContainerDiffCmdIT.class);

    @Test
    public void testContainerDiff() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("touch", "/test").exec();
        ContainerDiffCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        int exitCode = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        MatcherAssert.assertThat(exitCode, Matchers.equalTo(0));
        List<ChangeLog> filesystemDiff = dockerRule.getClient().containerDiffCmd(container.getId()).exec();
        ContainerDiffCmdIT.LOG.info("Container DIFF: {}", filesystemDiff.toString());
        MatcherAssert.assertThat(filesystemDiff.size(), Matchers.equalTo(1));
        ChangeLog testChangeLog = Lambda.selectUnique(filesystemDiff, hasField("path", Matchers.equalTo("/test")));
        MatcherAssert.assertThat(testChangeLog, hasField("path", Matchers.equalTo("/test")));
        MatcherAssert.assertThat(testChangeLog, hasField("kind", Matchers.equalTo(1)));
    }

    @Test(expected = NotFoundException.class)
    public void testContainerDiffWithNonExistingContainer() throws DockerException {
        dockerRule.getClient().containerDiffCmd("non-existing").exec();
    }
}

