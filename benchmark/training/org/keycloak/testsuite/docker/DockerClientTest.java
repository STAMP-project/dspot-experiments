package org.keycloak.testsuite.docker;


import Container.ExecResult;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;


public class DockerClientTest extends AbstractKeycloakTest {
    public static final String REALM_ID = "docker-test-realm";

    public static final String CLIENT_ID = "docker-test-client";

    public static final String DOCKER_USER = "docker-user";

    public static final String DOCKER_USER_PASSWORD = "password";

    public static final String REGISTRY_HOSTNAME = "localhost";

    public static final Integer REGISTRY_PORT = 5000;

    public static final String MINIMUM_DOCKER_VERSION = "1.8.0";

    private GenericContainer dockerRegistryContainer = null;

    private GenericContainer dockerClientContainer = null;

    private static String hostIp;

    private static String authServerPort;

    @Test
    public void shouldPerformDockerAuthAgainstRegistry() throws Exception {
        log.info("Starting the attempt for login...");
        Container.ExecResult dockerLoginResult = dockerClientContainer.execInContainer("docker", "login", "-u", DockerClientTest.DOCKER_USER, "-p", DockerClientTest.DOCKER_USER_PASSWORD, (((DockerClientTest.REGISTRY_HOSTNAME) + ":") + (DockerClientTest.REGISTRY_PORT)));
        printCommandResult(dockerLoginResult);
        MatcherAssert.assertThat(dockerLoginResult.getStdout(), Matchers.containsString("Login Succeeded"));
    }
}

