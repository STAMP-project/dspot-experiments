package com.github.dockerjava.cmd;


import InspectExecResponse.Container;
import RemoteApiVersion.VERSION_1_22;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.test.serdes.JSONTestHelper;
import com.github.dockerjava.utils.TestUtils;
import java.io.IOException;
import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InspectExecCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(InspectExecCmdIT.class);

    @Test
    public void inspectExec() throws Exception {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(containerName).exec();
        InspectExecCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        // Check that file does not exist
        ExecCreateCmdResponse checkFileExec1 = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withAttachStderr(true).withCmd("test", "-e", "/marker").exec();
        InspectExecCmdIT.LOG.info("Created exec {}", checkFileExec1.toString());
        MatcherAssert.assertThat(checkFileExec1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().execStartCmd(checkFileExec1.getId()).withDetach(false).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        InspectExecResponse first = dockerRule.getClient().inspectExecCmd(checkFileExec1.getId()).exec();
        MatcherAssert.assertThat(first.isRunning(), Matchers.is(false));
        MatcherAssert.assertThat(first.getExitCode(), Matchers.is(1));
        // Create the file
        ExecCreateCmdResponse touchFileExec = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withAttachStderr(true).withCmd("touch", "/marker").exec();
        InspectExecCmdIT.LOG.info("Created exec {}", touchFileExec.toString());
        MatcherAssert.assertThat(touchFileExec.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().execStartCmd(touchFileExec.getId()).withDetach(false).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        InspectExecResponse second = dockerRule.getClient().inspectExecCmd(touchFileExec.getId()).exec();
        MatcherAssert.assertThat(second.isRunning(), Matchers.is(false));
        MatcherAssert.assertThat(second.getExitCode(), Matchers.is(0));
        // Check that file does exist now
        ExecCreateCmdResponse checkFileExec2 = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withAttachStderr(true).withCmd("test", "-e", "/marker").exec();
        InspectExecCmdIT.LOG.info("Created exec {}", checkFileExec2.toString());
        MatcherAssert.assertThat(checkFileExec2.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().execStartCmd(checkFileExec2.getId()).withDetach(false).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        InspectExecResponse third = dockerRule.getClient().inspectExecCmd(checkFileExec2.getId()).exec();
        MatcherAssert.assertThat(third.isRunning(), Matchers.is(false));
        MatcherAssert.assertThat(third.getExitCode(), Matchers.is(0));
        // Get container info and check its roundtrip to ensure the consistency
        InspectContainerResponse containerInfo = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        Assert.assertEquals(containerInfo.getId(), container.getId());
        JSONTestHelper.testRoundTrip(containerInfo);
    }

    @Test
    public void inspectExecNetworkSettings() throws IOException {
        final RemoteApiVersion apiVersion = TestUtils.getVersion(dockerRule.getClient());
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(containerName).exec();
        InspectExecCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        ExecCreateCmdResponse exec = dockerRule.getClient().execCreateCmd(container.getId()).withAttachStdout(true).withAttachStderr(true).withCmd("/bin/bash").exec();
        InspectExecCmdIT.LOG.info("Created exec {}", exec.toString());
        MatcherAssert.assertThat(exec.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectExecResponse inspectExecResponse = dockerRule.getClient().inspectExecCmd(exec.getId()).exec();
        if (apiVersion.isGreaterOrEqual(VERSION_1_22)) {
            MatcherAssert.assertThat(inspectExecResponse.getExitCode(), Matchers.is(Matchers.nullValue()));
            MatcherAssert.assertThat(inspectExecResponse.getCanRemove(), Matchers.is(false));
            MatcherAssert.assertThat(inspectExecResponse.getContainerID(), Matchers.is(container.getId()));
        } else {
            MatcherAssert.assertThat(inspectExecResponse.getExitCode(), Matchers.is(0));
            Assert.assertNotNull(inspectExecResponse.getContainer().getNetworkSettings().getNetworks().get("bridge"));
        }
        MatcherAssert.assertThat(inspectExecResponse.isOpenStdin(), Matchers.is(false));
        MatcherAssert.assertThat(inspectExecResponse.isOpenStdout(), Matchers.is(true));
        MatcherAssert.assertThat(inspectExecResponse.isRunning(), Matchers.is(false));
        final InspectExecResponse.Container inspectContainer = inspectExecResponse.getContainer();
        if (apiVersion.isGreaterOrEqual(RemoteApiVersion.VERSION_1_22)) {
            MatcherAssert.assertThat(inspectContainer, Matchers.nullValue());
        } else {
            MatcherAssert.assertThat(inspectContainer, Matchers.notNullValue());
            Assert.assertNotNull(inspectContainer.getNetworkSettings().getNetworks().get("bridge"));
        }
    }
}

