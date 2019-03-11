package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WaitContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(BuildImageCmd.class);

    @Test
    public void testWaitContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").exec();
        WaitContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        int exitCode = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        WaitContainerCmdIT.LOG.info("Container exit code: {}", exitCode);
        MatcherAssert.assertThat(exitCode, Matchers.equalTo(0));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        WaitContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(Matchers.equalTo(false)));
        MatcherAssert.assertThat(inspectContainerResponse.getState().getExitCode(), Matchers.is(Matchers.equalTo(exitCode)));
    }

    @Test(expected = NotFoundException.class)
    public void testWaitNonExistingContainer() throws DockerException {
        WaitContainerResultCallback callback = new WaitContainerResultCallback() {
            public void onNext(WaitResponse waitResponse) {
                throw new AssertionError("expected NotFoundException");
            }
        };
        dockerRule.getClient().waitContainerCmd("non-existing").exec(callback).awaitStatusCode();
    }

    @Test
    public void testWaitContainerAbort() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        WaitContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        WaitContainerResultCallback callback = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback());
        Thread.sleep(5000);
        callback.close();
        dockerRule.getClient().killContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        WaitContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(Matchers.equalTo(false)));
    }

    @Test
    public void testWaitContainerTimeout() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "10").exec();
        WaitContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        WaitContainerResultCallback callback = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback());
        try {
            callback.awaitStatusCode(100, TimeUnit.MILLISECONDS);
            throw new AssertionError("Should throw exception on timeout.");
        } catch (DockerClientException e) {
            WaitContainerCmdIT.LOG.info(e.getMessage());
        }
    }
}

