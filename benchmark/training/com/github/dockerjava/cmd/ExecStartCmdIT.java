package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.utils.TestUtils;
import java.io.InputStream;
import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecStartCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(ExecStartCmdIT.class);

    @Test
    public void execStart() throws Exception {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("top").withName(containerName).exec();
        ExecStartCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        ExecCreateCmdResponse execCreateCmdResponse = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withCmd("touch", "/execStartTest.log").withUser("root").exec();
        dockerRule.getClient().execStartCmd(execCreateCmdResponse.getId()).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        InputStream response = dockerRule.getClient().copyArchiveFromContainerCmd(container.getId(), "/execStartTest.log").exec();
        Boolean bytesAvailable = (response.available()) > 0;
        Assert.assertTrue("The file was not copied from the container.", bytesAvailable);
        // read the stream fully. Otherwise, the underlying stream will not be closed.
        String responseAsString = TestUtils.asString(response);
        Assert.assertNotNull(responseAsString);
        Assert.assertTrue(((responseAsString.length()) > 0));
    }

    @Test
    public void execStartAttached() throws Exception {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(containerName).exec();
        ExecStartCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        ExecCreateCmdResponse execCreateCmdResponse = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withCmd("touch", "/execStartTest.log").exec();
        dockerRule.getClient().execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        InputStream response = dockerRule.getClient().copyArchiveFromContainerCmd(container.getId(), "/execStartTest.log").exec();
        Boolean bytesAvailable = (response.available()) > 0;
        Assert.assertTrue("The file was not copied from the container.", bytesAvailable);
        // read the stream fully. Otherwise, the underlying stream will not be closed.
        String responseAsString = TestUtils.asString(response);
        Assert.assertNotNull(responseAsString);
        Assert.assertTrue(((responseAsString.length()) > 0));
    }

    @Test(expected = NotFoundException.class)
    public void execStartWithNonExistentUser() throws Exception {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(containerName).exec();
        ExecStartCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        ExecCreateCmdResponse execCreateCmdResponse = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withCmd("touch", "/execStartTest.log").withUser("NonExistentUser").exec();
        dockerRule.getClient().execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        dockerRule.getClient().copyArchiveFromContainerCmd(container.getId(), "/execStartTest.log").exec();
    }
}

