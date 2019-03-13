package org.testcontainers.utility;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;


/**
 * This test checks the integration between Testcontainers and an authenticated registry, but uses
 * a mock instance of {@link RegistryAuthLocator} - the purpose of the test is solely to ensure that
 * the auth locator is utilised, and that the credentials it provides flow through to the registry.
 *
 * {@link RegistryAuthLocatorTest} covers actual credential scenarios at a lower level, which are
 * impractical to test end-to-end.
 */
public class AuthenticatedImagePullTest {
    @ClassRule
    public static GenericContainer authenticatedRegistry = withExposedPorts(5000).waitingFor(new HttpWaitStrategy());

    private static RegistryAuthLocator originalAuthLocatorSingleton;

    private static DockerClient client;

    private static String testRegistryAddress;

    private static String testImageName;

    private static String testImageNameWithTag;

    @Test
    public void testThatAuthLocatorIsUsed() throws Exception {
        final DockerImageName expectedName = new DockerImageName(AuthenticatedImagePullTest.testImageNameWithTag);
        final AuthConfig authConfig = new AuthConfig().withUsername("testuser").withPassword("notasecret").withRegistryAddress(("http://" + (AuthenticatedImagePullTest.testRegistryAddress)));
        // Replace the RegistryAuthLocator singleton with our mock, for the duration of this test
        final RegistryAuthLocator mockAuthLocator = Mockito.mock(RegistryAuthLocator.class);
        RegistryAuthLocator.setInstance(mockAuthLocator);
        Mockito.when(mockAuthLocator.lookupAuthConfig(ArgumentMatchers.eq(expectedName), ArgumentMatchers.any())).thenReturn(authConfig);
        // a push will use the auth locator for authentication, although that isn't the goal of this test
        putImageInRegistry();
        // actually start a container, which will require an authenticated pull
        try (final GenericContainer container = new GenericContainer(AuthenticatedImagePullTest.testImageNameWithTag).withCommand("/bin/sh", "-c", "sleep 10")) {
            container.start();
            assertTrue("container started following an authenticated pull", container.isRunning());
        }
    }
}

