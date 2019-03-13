package com.github.dockerjava.core.command;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.junit.category.Integration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(Integration.class)
public class FrameReaderITest {
    private DockerClient dockerClient;

    private DockerfileFixture dockerfileFixture;

    @Test
    public void canCloseFrameReaderAndReadExpectedLines() throws Exception {
        DockerAssume.assumeNotSwarm("", dockerClient);
        // wait for the container to be successfully executed
        int exitCode = dockerClient.waitContainerCmd(dockerfileFixture.getContainerId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        Assert.assertEquals(0, exitCode);
        final List<Frame> loggingFrames = getLoggingFrames();
        final Frame outFrame = new Frame(StreamType.STDOUT, "to stdout\n".getBytes());
        final Frame errFrame = new Frame(StreamType.STDERR, "to stderr\n".getBytes());
        MatcherAssert.assertThat(loggingFrames, Matchers.containsInAnyOrder(outFrame, errFrame));
        MatcherAssert.assertThat(loggingFrames, Matchers.hasSize(2));
    }

    @Test
    public void canLogInOneThreadAndExecuteCommandsInAnother() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Iterator<Frame> frames = getLoggingFrames().iterator();
                    while (frames.hasNext()) {
                        frames.next();
                    } 
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        try (DockerfileFixture busyboxDockerfile = new DockerfileFixture(dockerClient, "busyboxDockerfile")) {
            busyboxDockerfile.open();
        }
        thread.join();
    }

    public static class FrameReaderITestCallback extends LogContainerResultCallback {
        public List<Frame> frames = new ArrayList<>();

        @Override
        public void onNext(Frame item) {
            frames.add(item);
            super.onNext(item);
        }
    }
}

