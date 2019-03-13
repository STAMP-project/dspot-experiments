package integration;


import io.fabric8.maven.docker.access.DockerAccessException;
import io.fabric8.maven.docker.access.hc.DockerAccessWithHcClient;
import io.fabric8.maven.docker.util.AnsiLogger;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/* if run from your ide, this test assumes you have configured the runner w/ the appropriate env variables

it also assumes that 'removeImage' does what it's supposed to do as it's used in test setup.
 */
@Ignore
public class DockerAccessIT {
    private static final String CONTAINER_NAME = "integration-test";

    private static final String IMAGE = "busybox:buildroot-2014.02";

    private static final String IMAGE_TAG = "busybox:tagged";

    private static final String IMAGE_LATEST = "busybox:latest";

    private static final int PORT = 5677;

    private String containerId;

    private final DockerAccessWithHcClient dockerClient;

    public DockerAccessIT() throws IOException {
        AnsiLogger logger = new AnsiLogger(new SystemStreamLog(), true, true);
        String url = createDockerConnectionDetector(logger).detectConnectionParameter(null, null).getUrl();
        this.dockerClient = createClient(url, logger);
    }

    @Test
    public void testPullStartStopRemove() throws DockerAccessException, InterruptedException {
        testDoesNotHave();
        try {
            testPullImage();
            testTagImage();
            testCreateContainer();
            testStartContainer();
            testExecContainer();
            testQueryPortMapping();
            testStopContainer();
            Thread.sleep(2000);
            testRemoveContainer();
        } finally {
            testRemoveImage(DockerAccessIT.IMAGE);
        }
    }

    @Test
    public void testLoadImage() throws DockerAccessException {
        testDoesNotHave();
        dockerClient.loadImage(DockerAccessIT.IMAGE_LATEST, new File("integration/busybox-image-test.tar.gz"));
        Assert.assertTrue(hasImage(DockerAccessIT.IMAGE_LATEST));
        testRemoveImage(DockerAccessIT.IMAGE_LATEST);
    }
}

