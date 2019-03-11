package org.testcontainers.junit;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import redis.clients.jedis.Jedis;


/**
 * Created by rnorth on 08/08/2015.
 */
public class DockerComposeContainerScalingTest {
    private static final int REDIS_PORT = 6379;

    private Jedis[] clients = new Jedis[3];

    @Rule
    public DockerComposeContainer environment = // explicit service index
    // implicit '_1'
    new DockerComposeContainer(new File("src/test/resources/scaled-compose-test.yml")).withScaledService("redis", 3).withExposedService("redis", DockerComposeContainerScalingTest.REDIS_PORT).withExposedService("redis_2", DockerComposeContainerScalingTest.REDIS_PORT).withExposedService("redis", 3, DockerComposeContainerScalingTest.REDIS_PORT);// explicit service index via parameter


    @Test
    public void simpleTest() {
        for (int i = 0; i < 3; i++) {
            clients[i].incr("somekey");
            assertEquals("Each redis instance is separate", "1", clients[i].get("somekey"));
        }
    }
}

