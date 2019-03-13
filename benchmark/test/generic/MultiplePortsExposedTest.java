package generic;


import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;


public class MultiplePortsExposedTest {
    private static final Logger log = LoggerFactory.getLogger(MultiplePortsExposedTest.class);

    // rule {
    @Rule
    public GenericContainer container = new GenericContainer("orientdb:3.0.13").withExposedPorts(2424, 2480).withLogConsumer(new Slf4jLogConsumer(MultiplePortsExposedTest.log));

    // }
    @Test
    public void fetchPortsByNumber() {
        Integer firstMappedPort = container.getMappedPort(2424);
        Integer secondMappedPort = container.getMappedPort(2480);
    }

    @Test
    public void fetchFirstMappedPort() {
        Integer firstMappedPort = container.getFirstMappedPort();
    }

    @Test
    public void getContainerIpAddressOnly() {
        String ipAddress = container.getContainerIpAddress();
    }

    @Test
    public void getContainerIpAddressAndMappedPort() {
        String address = ((container.getContainerIpAddress()) + ":") + (container.getMappedPort(2424));
    }
}

