package generic;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;


public class WaitStrategiesTest {
    // waitForNetworkListening {
    @Rule
    public GenericContainer nginx = new GenericContainer("nginx:1.9.4").withExposedPorts(80);

    // }
    // waitForSimpleHttp {
    @Rule
    public GenericContainer nginxWithHttpWait = new GenericContainer("nginx:1.9.4").withExposedPorts(80).waitingFor(Wait.forHttp("/"));

    // }
    // logMessageWait {
    @Rule
    public GenericContainer containerWithLogWait = new GenericContainer("redis:5.0.3").withExposedPorts(6379).waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));

    // }
    // waitForHttpWithMultipleStatusCodes {
    // }
    private static final HttpWaitStrategy MULTI_CODE_HTTP_WAIT = Wait.forHttp("/").forStatusCode(200).forStatusCode(301);

    // waitForHttpWithStatusCodePredicate {
    // }
    private static final HttpWaitStrategy PREDICATE_HTTP_WAIT = Wait.forHttp("/all").forStatusCodeMatching(( it) -> ((it >= 200) && (it < 300)) || (it == 401));

    // waitForHttpWithTls {
    // }
    private static final HttpWaitStrategy TLS_HTTP_WAIT = Wait.forHttp("/all").usingTls();

    // healthcheckWait {
    // }
    private static final WaitStrategy HEALTHCHECK_WAIT = Wait.forHealthcheck();

    @Test
    public void testContainersAllStarted() {
        Assert.assertTrue(nginx.isRunning());
        Assert.assertTrue(nginxWithHttpWait.isRunning());
        Assert.assertTrue(containerWithLogWait.isRunning());
    }
}

