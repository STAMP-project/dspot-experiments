package generic;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


public class CommandsTest {
    // startupCommand {
    @Rule
    public GenericContainer redisWithCustomPort = // }
    new GenericContainer("redis:5.0").withCommand("redis-server --port 7777").withExposedPorts(7777);

    @Test
    public void testStartupCommandOverrideApplied() {
        Assert.assertTrue(redisWithCustomPort.isRunning());// good enough to check that the container started listening

    }
}

