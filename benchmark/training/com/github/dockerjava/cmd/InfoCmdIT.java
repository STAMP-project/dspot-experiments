package com.github.dockerjava.cmd;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.junit.DockerRule;
import com.github.dockerjava.utils.TestUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class InfoCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(InfoCmdIT.class);

    @Test
    public void infoTest() throws DockerException {
        DockerClient dockerClient = dockerRule.getClient();
        // Make sure that there is at least one container for the assertion
        // TODO extract this into a shared method
        if ((dockerClient.listContainersCmd().withShowAll(true).exec().size()) == 0) {
            CreateContainerResponse container = dockerClient.createContainerCmd(DockerRule.DEFAULT_IMAGE).withName("docker-java-itest-info").withCmd("touch", "/test").exec();
            InfoCmdIT.LOG.info("Created container: {}", container);
            MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyOrNullString()));
            dockerClient.startContainerCmd(container.getId()).exec();
        }
        Info dockerInfo = dockerClient.infoCmd().exec();
        InfoCmdIT.LOG.info(dockerInfo.toString());
        MatcherAssert.assertThat(dockerInfo.getContainers(), Matchers.notNullValue());
        MatcherAssert.assertThat(dockerInfo.getContainers(), Matchers.greaterThan(0));
        MatcherAssert.assertThat(dockerInfo.getImages(), Matchers.notNullValue());
        MatcherAssert.assertThat(dockerInfo.getImages(), Matchers.greaterThan(0));
        MatcherAssert.assertThat(dockerInfo.getDebug(), Matchers.notNullValue());
        if (TestUtils.isNotSwarm(dockerClient)) {
            MatcherAssert.assertThat(dockerInfo.getNFd(), Matchers.greaterThan(0));
            MatcherAssert.assertThat(dockerInfo.getNGoroutines(), Matchers.greaterThan(0));
            MatcherAssert.assertThat(dockerInfo.getNCPU(), Matchers.greaterThan(0));
        }
    }
}

