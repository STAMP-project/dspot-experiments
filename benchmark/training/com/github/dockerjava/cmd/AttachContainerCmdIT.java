package com.github.dockerjava.cmd;


import StreamType.RAW;
import StreamType.STDOUT;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.junit.DockerRule;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dockerjava.cmd.CmdIT.FactoryType.JERSEY;
import static com.github.dockerjava.cmd.CmdIT.FactoryType.NETTY;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class AttachContainerCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(AttachContainerCmdIT.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void attachContainerWithStdin() throws Exception {
        DockerClient dockerClient = dockerRule.getClient();
        Assume.assumeThat(getFactoryType(), CoreMatchers.is(NETTY));
        String snippet = "hello world";
        CreateContainerResponse container = dockerClient.createContainerCmd("busybox").withCmd("/bin/sh", "-c", "sleep 1 && read line && echo $line").withTty(false).withStdinOpen(true).exec();
        AttachContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerClient.startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), CoreMatchers.is(true));
        AttachContainerCmdIT.AttachContainerTestCallback callback = new AttachContainerCmdIT.AttachContainerTestCallback() {
            @Override
            public void onNext(Frame frame) {
                Assert.assertEquals(frame.getStreamType(), STDOUT);
                super.onNext(frame);
            }
        };
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        dockerClient.attachContainerCmd(container.getId()).withStdErr(true).withStdOut(true).withFollowStream(true).withStdIn(in).exec(callback);
        out.write((snippet + "\n").getBytes());
        out.flush();
        awaitCompletion(15, TimeUnit.SECONDS);
        close();
        MatcherAssert.assertThat(callback.toString(), Matchers.containsString(snippet));
    }

    @Test
    public void attachContainerWithoutTTY() throws Exception {
        DockerClient dockerClient = dockerRule.getClient();
        String snippet = "hello world";
        CreateContainerResponse container = dockerClient.createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("echo", snippet).withTty(false).exec();
        AttachContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerClient.startContainerCmd(container.getId()).exec();
        AttachContainerCmdIT.AttachContainerTestCallback callback = new AttachContainerCmdIT.AttachContainerTestCallback() {
            @Override
            public void onNext(Frame frame) {
                MatcherAssert.assertThat(frame.getStreamType(), Matchers.equalTo(STDOUT));
                super.onNext(frame);
            }
        };
        awaitCompletion(30, TimeUnit.SECONDS);
        close();
        MatcherAssert.assertThat(callback.toString(), Matchers.containsString(snippet));
    }

    @Test
    public void attachContainerWithTTY() throws Exception {
        DockerClient dockerClient = dockerRule.getClient();
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource("attachContainerTestDockerfile").getFile());
        String imageId = dockerRule.buildImage(baseDir);
        CreateContainerResponse container = dockerClient.createContainerCmd(imageId).withTty(true).exec();
        AttachContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerClient.startContainerCmd(container.getId()).exec();
        AttachContainerCmdIT.AttachContainerTestCallback callback = new AttachContainerCmdIT.AttachContainerTestCallback() {
            @Override
            public void onNext(Frame frame) {
                MatcherAssert.assertThat(frame.getStreamType(), Matchers.equalTo(RAW));
                super.onNext(frame);
            }
        };
        awaitCompletion(15, TimeUnit.SECONDS);
        close();
        AttachContainerCmdIT.LOG.debug("log: {}", callback.toString());
        // HexDump.dump(collectFramesCallback.toString().getBytes(), 0, System.out, 0);
        MatcherAssert.assertThat(callback.toString(), Matchers.containsString("stdout\r\nstderr"));
    }

    @Test
    public void attachContainerStdinUnsupported() throws Exception {
        DockerClient dockerClient = dockerRule.getClient();
        if ((getFactoryType()) == (JERSEY)) {
            expectedException.expect(UnsupportedOperationException.class);
        }
        String snippet = "hello world";
        CreateContainerResponse container = dockerClient.createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("echo", snippet).withTty(false).exec();
        AttachContainerCmdIT.LOG.info("Created container: {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerClient.startContainerCmd(container.getId()).exec();
        AttachContainerCmdIT.AttachContainerTestCallback callback = new AttachContainerCmdIT.AttachContainerTestCallback() {
            @Override
            public void onNext(Frame frame) {
                MatcherAssert.assertThat(frame.getStreamType(), Matchers.equalTo(STDOUT));
                super.onNext(frame);
            }
        };
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        awaitCompletion(30, TimeUnit.SECONDS);
        close();
    }

    public static class AttachContainerTestCallback extends AttachContainerResultCallback {
        private StringBuffer log = new StringBuffer();

        @Override
        public void onNext(Frame item) {
            log.append(new String(item.getPayload()));
            super.onNext(item);
        }

        @Override
        public String toString() {
            return log.toString();
        }
    }
}

