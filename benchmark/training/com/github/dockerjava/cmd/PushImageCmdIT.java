package com.github.dockerjava.cmd;


import RemoteApiVersion.VERSION_1_24;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.utils.RegistryUtils;
import com.github.dockerjava.utils.TestUtils;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PushImageCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(PushImageCmdIT.class);

    private String username;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private AuthConfig authConfig;

    @Test
    public void pushLatest() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").exec();
        PushImageCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        PushImageCmdIT.LOG.info("Committing container: {}", container.toString());
        String imgName = (((authConfig.getRegistryAddress()) + "/") + (dockerRule.getKind())) + "-push-latest";
        String imageId = dockerRule.getClient().commitCmd(container.getId()).withRepository(imgName).exec();
        // we have to block until image is pushed
        dockerRule.getClient().pushImageCmd(imgName).withAuthConfig(authConfig).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
        PushImageCmdIT.LOG.info("Removing image: {}", imageId);
        dockerRule.getClient().removeImageCmd(imageId).exec();
        dockerRule.getClient().pullImageCmd(imgName).withTag("latest").withAuthConfig(authConfig).exec(new PullImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
    }

    @Test
    public void pushNonExistentImage() throws Exception {
        if ((TestUtils.isNotSwarm(dockerRule.getClient())) && (TestUtils.getVersion(dockerRule.getClient()).isGreaterOrEqual(VERSION_1_24))) {
            exception.expect(DockerClientException.class);
        } else {
            exception.expect(NotFoundException.class);
        }
        dockerRule.getClient().pushImageCmd(((username) + "/xxx")).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);// exclude infinite await sleep

    }

    @Test
    public void testPushImageWithValidAuth() throws Exception {
        String imgName = RegistryUtils.createTestImage(dockerRule, "push-image-with-valid-auth");
        // stream needs to be fully read in order to close the underlying connection
        dockerRule.getClient().pushImageCmd(imgName).withAuthConfig(authConfig).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
    }

    @Test
    public void testPushImageWithNoAuth() throws Exception {
        String imgName = RegistryUtils.createTestImage(dockerRule, "push-image-with-no-auth");
        exception.expect(DockerClientException.class);
        // stream needs to be fully read in order to close the underlying connection
        dockerRule.getClient().pushImageCmd(imgName).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
    }

    @Test
    public void testPushImageWithInvalidAuth() throws Exception {
        AuthConfig invalidAuthConfig = new AuthConfig().withUsername("testuser").withPassword("testwrongpassword").withEmail("foo@bar.de").withRegistryAddress(authConfig.getRegistryAddress());
        String imgName = RegistryUtils.createTestImage(dockerRule, "push-image-with-invalid-auth");
        exception.expect(DockerClientException.class);
        // stream needs to be fully read in order to close the underlying connection
        dockerRule.getClient().pushImageCmd(imgName).withAuthConfig(invalidAuthConfig).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
    }
}

