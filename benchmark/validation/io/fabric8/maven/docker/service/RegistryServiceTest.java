package io.fabric8.maven.docker.service;


import AutoPullMode.ALWAYS;
import ImagePullPolicy.Always;
import ImagePullPolicy.IfNotPresent;
import ImagePullPolicy.Never;
import io.fabric8.maven.docker.access.DockerAccess;
import io.fabric8.maven.docker.access.DockerAccessException;
import io.fabric8.maven.docker.config.ImagePullPolicy;
import io.fabric8.maven.docker.util.AuthConfigFactory;
import io.fabric8.maven.docker.util.AutoPullMode;
import io.fabric8.maven.docker.util.Logger;
import java.util.Map;
import mockit.Mocked;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 23.11.17
 */
public class RegistryServiceTest {
    private Exception actualException;

    private String imageName;

    private ImagePullPolicy imagePullPolicy;

    private RegistryServiceTest.TestCacheStore cacheStore;

    private AutoPullMode autoPullMode;

    private RegistryService registryService;

    private boolean hasImage;

    private String registry;

    private Map authConfig;

    @Mocked
    private DockerAccess docker;

    @Mocked
    private Logger logger;

    @Mocked
    private AuthConfigFactory authConfigFactory;

    @Test
    public void pullImagePullPolicyAlways() throws Exception {
        for (boolean hasImage : new boolean[]{ false, true }) {
            reset();
            givenAnImage();
            givenImagePullPolicy(Always);
            givenHasImage(hasImage);
            checkPulledButNotTagged();
        }
    }

    @Test
    public void pullImageAutopullAlways() throws Exception {
        for (boolean hasImage : new boolean[]{ false, true }) {
            reset();
            givenAnImage();
            givenAutoPullMode(ALWAYS);
            givenHasImage(hasImage);
            checkPulledButNotTagged();
        }
    }

    @Test
    public void pullImageAlwaysWhenPreviouslyPulled() throws Exception {
        givenAnImage();
        givenHasImage(false);
        givenPreviousPulled(true);
        givenImagePullPolicy(Always);
        checkNotPulledAndNotTagged();
    }

    @Test
    public void alreadyPulled() throws DockerAccessException {
        givenAnImage();
        givenPreviousPulled(true);
        whenAutoPullImage();
        thenImageHasNotBeenPulled();
        thenImageHasNotBeenTagged();
        thenNoExceptionThrown();
    }

    @Test
    public void policyNeverWithImageAvailable() throws DockerAccessException {
        givenAnImage();
        givenHasImage(true);
        givenPreviousPulled(false);
        givenImagePullPolicy(Never);
        whenAutoPullImage();
        thenImageHasNotBeenPulled();
        thenImageHasNotBeenTagged();
    }

    @Test
    public void policyNeverWithImageNotAvailable() throws DockerAccessException {
        givenAnImage();
        givenHasImage(false);
        givenPreviousPulled(false);
        givenImagePullPolicy(Never);
        whenAutoPullImage();
        thenImageHasNotBeenPulled();
        thenImageHasNotBeenTagged();
        thenExceptionThrown();
    }

    @Test
    public void pullWithCustomRegistry() throws DockerAccessException {
        givenAnImage("myregistry.com/user/test:1.0.1");
        givenHasImage(false);
        givenPreviousPulled(false);
        givenRegistry("anotherRegistry.com");
        givenImagePullPolicy(IfNotPresent);
        whenAutoPullImage();
        thenImageHasBeenPulledWithRegistry("myregistry.com");
        thenImageHasNotBeenTagged();
        thenNoExceptionThrown();
    }

    @Test
    public void tagForCustomRegistry() throws DockerAccessException {
        givenAnImage("user/test:1.0.1");
        givenHasImage(false);
        givenPreviousPulled(false);
        givenRegistry("anotherRegistry.com");
        givenImagePullPolicy(IfNotPresent);
        whenAutoPullImage();
        thenImageHasBeenPulledWithRegistry("anotherRegistry.com");
        thenImageHasBeenTagged();
        thenNoExceptionThrown();
    }

    private class TestCacheStore implements ImagePullManager.CacheStore {
        String cache;

        @Override
        public String get(String key) {
            return cache;
        }

        @Override
        public void put(String key, String value) {
            cache = value;
        }
    }
}

