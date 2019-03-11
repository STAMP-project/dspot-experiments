package com.github.dockerjava.cmd;


import InspectContainerResponse.Node;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.utils.TestUtils;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InspectContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(InspectContainerCmdIT.class);

    @Test
    public void inspectContainer() throws DockerException {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("top").withName(containerName).exec();
        InspectContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse containerInfo = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        Assert.assertEquals(containerInfo.getId(), container.getId());
    }

    @Test
    public void inspectContainerNodeProperty() throws DockerException {
        Map<String, String> label = Collections.singletonMap("inspectContainerNodeProperty", UUID.randomUUID().toString());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withLabels(label).exec();
        Container containerResult = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(label).exec().get(0);
        String name = containerResult.getNames()[0];
        InspectContainerResponse containerInfo = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        InspectContainerResponse.Node node = containerInfo.getNode();
        if (TestUtils.isSwarm(dockerRule.getClient())) {
            MatcherAssert.assertThat(node, Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat(node.getAddr(), Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat(node.getId(), Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat(node.getIp(), Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat(node.getLabels(), Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat(node.getLabels().get("com.github.dockerjava.test"), Matchers.is("docker-java"));
            MatcherAssert.assertThat(node.getCpus(), Matchers.is(Matchers.greaterThanOrEqualTo(1)));
            MatcherAssert.assertThat(node.getMemory(), Matchers.is(Matchers.greaterThanOrEqualTo(((64 * 1024) * 1024L))));
            MatcherAssert.assertThat((("/" + (node.getName())) + (containerInfo.getName())), Matchers.is(name));
        } else {
            MatcherAssert.assertThat(node, Matchers.is(Matchers.nullValue()));
        }
    }

    @Test
    public void inspectContainerWithSize() throws DockerException {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("top").withName(containerName).exec();
        InspectContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerCmd command = dockerRule.getClient().inspectContainerCmd(container.getId()).withSize(true);
        Assert.assertTrue(command.getSize());
        InspectContainerResponse containerInfo = command.exec();
        Assert.assertEquals(containerInfo.getId(), container.getId());
        // TODO check swarm
        if (TestUtils.isNotSwarm(dockerRule.getClient())) {
            Assert.assertNotNull(containerInfo.getSizeRootFs());
            Assert.assertTrue(((containerInfo.getSizeRootFs().intValue()) > 0));
        }
    }

    @Test(expected = NotFoundException.class)
    public void inspectNonExistingContainer() throws DockerException {
        dockerRule.getClient().inspectContainerCmd("non-existing").exec();
    }

    @Test
    public void inspectContainerRestartCount() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("env").exec();
        InspectContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getRestartCount(), Matchers.equalTo(0));
    }

    @Test
    public void inspectContainerNetworkSettings() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("env").exec();
        InspectContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        Assert.assertFalse(inspectContainerResponse.getNetworkSettings().getHairpinMode());
    }
}

