package org.testcontainers.junit;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;


/**
 * Created by rnorth on 08/08/2015.
 */
public class DockerComposeContainerTest extends BaseDockerComposeTest {
    @Rule
    public DockerComposeContainer environment = new DockerComposeContainer(new File("src/test/resources/compose-test.yml")).withExposedService("redis_1", BaseDockerComposeTest.REDIS_PORT).withExposedService("db_1", 3306);

    @Test
    public void testGetServicePort() {
        int serviceWithInstancePort = environment.getServicePort("redis_1", BaseDockerComposeTest.REDIS_PORT);
        assertNotNull("Port is set for service with instance number", serviceWithInstancePort);
        int serviceWithoutInstancePort = environment.getServicePort("redis", BaseDockerComposeTest.REDIS_PORT);
        assertNotNull("Port is set for service with instance number", serviceWithoutInstancePort);
        assertEquals("Service ports are the same", serviceWithInstancePort, serviceWithoutInstancePort);
    }
}

