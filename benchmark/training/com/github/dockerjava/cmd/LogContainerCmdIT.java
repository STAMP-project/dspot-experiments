package com.github.dockerjava.cmd;


import StreamType.RAW;
import StreamType.STDOUT;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.utils.LogContainerTestCallback;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogContainerCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(LogContainerCmdIT.class);

    @Test
    public void asyncLogContainerWithTtyEnabled() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("/bin/sh", "-c", "while true; do echo hello; sleep 1; done").withTty(true).exec();
        LogContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(true);
        // this essentially test the since=0 case
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).withFollowStream(true).withTailAll().exec(loggingCallback);
        loggingCallback.awaitCompletion(3, TimeUnit.SECONDS);
        Assert.assertTrue(loggingCallback.toString().contains("hello"));
        Assert.assertEquals(loggingCallback.getCollectedFrames().get(0).getStreamType(), RAW);
    }

    @Test
    public void asyncLogContainerWithTtyDisabled() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("/bin/sh", "-c", "while true; do echo hello; sleep 1; done").withTty(false).exec();
        LogContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(true);
        // this essentially test the since=0 case
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).withFollowStream(true).withTailAll().exec(loggingCallback);
        loggingCallback.awaitCompletion(3, TimeUnit.SECONDS);
        Assert.assertTrue(loggingCallback.toString().contains("hello"));
        Assert.assertEquals(loggingCallback.getCollectedFrames().get(0).getStreamType(), STDOUT);
    }

    @Test
    public void asyncLogNonExistingContainer() throws Exception {
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback() {
            @Override
            public void onError(Throwable throwable) {
                Assert.assertEquals(throwable.getClass().getName(), NotFoundException.class.getName());
                try {
                    // close the callback to prevent the call to onComplete
                    close();
                } catch (IOException e) {
                    throw new RuntimeException();
                }
                super.onError(throwable);
            }

            public void onComplete() {
                super.onComplete();
                throw new AssertionError("expected NotFoundException");
            }
        };
        awaitCompletion();
    }

    @Test
    public void asyncMultipleLogContainer() throws Exception {
        String snippet = "hello world";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("/bin/echo", snippet).exec();
        LogContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        int exitCode = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        MatcherAssert.assertThat(exitCode, Matchers.equalTo(0));
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback();
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).exec(loggingCallback);
        close();
        loggingCallback = new LogContainerTestCallback();
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).exec(loggingCallback);
        close();
        loggingCallback = new LogContainerTestCallback();
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).exec(loggingCallback);
        awaitCompletion();
        Assert.assertTrue(loggingCallback.toString().contains(snippet));
    }

    @Test
    public void asyncLogContainerWithSince() throws Exception {
        String snippet = "hello world";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("/bin/echo", snippet).exec();
        LogContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        int timestamp = ((int) ((System.currentTimeMillis()) / 1000));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        int exitCode = dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        MatcherAssert.assertThat(exitCode, Matchers.equalTo(0));
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback();
        dockerRule.getClient().logContainerCmd(container.getId()).withStdErr(true).withStdOut(true).withSince(timestamp).exec(loggingCallback);
        awaitCompletion();
        MatcherAssert.assertThat(loggingCallback.toString(), Matchers.containsString(snippet));
    }
}

