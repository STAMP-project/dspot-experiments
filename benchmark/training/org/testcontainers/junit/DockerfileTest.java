package org.testcontainers.junit;


import com.github.dockerjava.api.command.BuildImageCmd;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.images.builder.Transferable;


public class DockerfileTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerfileTest.class);

    @Test
    public void simpleDockerfileWorks() {
        ImageFromDockerfile image = new ImageFromDockerfile().withFileFromString("folder/someFile.txt", "hello").withFileFromClasspath("test.txt", "mappable-resource/test-resource.txt").withFileFromClasspath("Dockerfile", "mappable-dockerfile/Dockerfile");
        verifyImage(image);
    }

    @Test
    public void customizableImage() {
        ImageFromDockerfile image = new ImageFromDockerfile() {
            @Override
            protected void configure(BuildImageCmd buildImageCmd) {
                super.configure(buildImageCmd);
                List<String> dockerfile = Arrays.asList("FROM alpine:3.2", "RUN echo 'hello from Docker build process'", "CMD yes");
                withFileFromString("Dockerfile", String.join("\n", dockerfile));
                buildImageCmd.withNoCache(true);
            }
        };
        verifyImage(image);
    }

    @Test
    public void dockerfileBuilderWorks() {
        ImageFromDockerfile image = withFileFromString("folder/someFile.txt", "hello").withDockerfileFromBuilder(( builder) -> builder.from("alpine:3.2").workDir("/app").add("test.txt", "test file.txt").run("ls", "-la", "/app/test file.txt").copy("folder/someFile.txt", "/someFile.txt").expose(80, 8080).cmd("while true; do cat /someFile.txt | nc -l -p 80; done"));
        verifyImage(image);
    }

    @Test
    public void filePermissions() throws TimeoutException {
        WaitingConsumer consumer = new WaitingConsumer();
        ImageFromDockerfile image = new ImageFromDockerfile().withFileFromTransferable("/someFile.txt", new Transferable() {
            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public String getDescription() {
                return "test file";
            }

            @Override
            public int getFileMode() {
                return 83;
            }
        }).withDockerfileFromBuilder(( builder) -> builder.from("alpine:3.2").copy("someFile.txt", "/someFile.txt").cmd("stat -c \"%a\" /someFile.txt"));
        GenericContainer container = new GenericContainer(image).withStartupCheckStrategy(new OneShotStartupCheckStrategy()).withLogConsumer(consumer);
        try {
            container.start();
            consumer.waitUntil(( frame) -> ((frame.getType()) == (STDOUT)) && (frame.getUtf8String().contains("123")), 5, TimeUnit.SECONDS);
        } finally {
            container.stop();
        }
    }
}

