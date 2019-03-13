package io.fabric8.maven.docker.access;


import java.util.Properties;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 04.04.14
 */
public class PortMappingTest {
    private PortMapping mapping;

    private Properties properties;

    @Test
    public void testComplexMapping() {
        givenAHostIpProperty("other.ip", "127.0.0.1");
        givenAPortMapping("jolokia.port:8080", "18181:8181", "127.0.0.1:9090:9090", "+other.ip:${other.port}:5678");
        whenUpdateDynamicMapping(443);
        whenUpdateDynamicMapping(8080, 49900, "0.0.0.0");
        whenUpdateDynamicMapping(5678, 49901, "127.0.0.1");
        thenMapAndVerifyReplacement("http://localhost:49900/", "http://localhost:${jolokia.port}/", "http://pirx:49900/", "http://pirx:${jolokia.port}/");
        thenMapAndVerifyReplacement("http://localhost:49901/", "http://localhost:${other.port}/", "http://pirx:49901/", "http://pirx:${other.port}/", "http://49900/49901", "http://${jolokia.port}/${other.port}");
        thenNeedsPropertyUpdate();
        thenDynamicHostPortsSizeIs(2);
        thenHostPortVariableEquals("jolokia.port", 49900);
        thenHostPortVariableEquals("other.port", 49901);
        thenDynamicHostIpsSizeIs(1);
        thenHostIpVariableEquals("other.ip", "127.0.0.1");
        thenContainerPortToHostPortMapSizeIs(4);
        thenContainerPortToHostPortMapHasOnlyPortSpec("8080/tcp");
        thenContainerPortToHostPortMapHasOnlyPortSpec("5678/tcp");
        thenContainerPortToHostPortMapHasPortSpecAndPort("8181/tcp", 18181);
        thenContainerPortToHostPortMapHasPortSpecAndPort("9090/tcp", 9090);
        thenBindToHostMapSizeIs(2);
        thenBindToHostMapContains("9090/tcp", "127.0.0.1");
        thenBindToHostMapContains("5678/tcp", "127.0.0.1");
    }

    @Test
    public void testHostIpAsPropertyOnly() {
        givenADockerHostAddress("1.2.3.4");
        givenAPortMapping("${other.ip}:5677:5677");
        whenUpdateDynamicMapping(5677, 5677, "0.0.0.0");
        thenContainerPortToHostPortMapSizeIs(1);
        thenDynamicHostPortsSizeIs(0);
        thenDynamicHostIpsSizeIs(1);
        thenBindToHostMapSizeIs(0);
        thenHostIpVariableEquals("other.ip", "1.2.3.4");
    }

    @Test
    public void testHostIpPortAsProperties() {
        givenADockerHostAddress("5.6.7.8");
        givenAPortMapping("+other.ip:other.port:5677");
        whenUpdateDynamicMapping(5677, 49900, "1.2.3.4");
        thenContainerPortToHostPortMapHasOnlyPortSpec("5677/tcp");
        thenDynamicHostPortsSizeIs(1);
        thenDynamicHostIpsSizeIs(1);
        thenHostPortVariableEquals("other.port", 49900);
        thenHostIpVariableEquals("other.ip", "1.2.3.4");
    }

    @Test
    public void testHostIpVariableReplacement() {
        givenAPortMapping("jolokia.port:8080");
        whenUpdateDynamicMapping(8080, 49900, "0.0.0.0");
        thenNeedsPropertyUpdate();
        thenDynamicHostPortsSizeIs(1);
        thenHostPortVariableEquals("jolokia.port", 49900);
    }

    @Test
    public void testHostnameAsBindHost() {
        givenAPortMapping("localhost:80:80");
        thenBindToHostMapContainsValue("127.0.0.1");
    }

    @Test
    public void testSingleContainerPort() {
        givenAPortMapping("8080");
        thenContainerPortToHostPortMapSizeIs(1);
        thenContainerPortToHostPortMapHasOnlyPortSpec("8080");
    }

    @Test
    public void testHostPortAsPropertyOnly() {
        givenAPortMapping("other.port:5677");
        whenUpdateDynamicMapping(5677, 49900, "0.0.0.0");
        thenContainerPortToHostPortMapSizeIs(1);
        thenDynamicHostPortsSizeIs(1);
        thenDynamicHostIpsSizeIs(0);
        thenHostPortVariableEquals("other.port", 49900);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMapping() {
        givenAPortMapping("bla");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMapping2() {
        givenAPortMapping("jolokia.port:bla");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProtocol() {
        givenAPortMapping("49000:8080/abc");
    }

    @Test
    public void testIpAsBindHost() {
        givenAPortMapping("127.0.0.1:80:80");
        thenBindToHostMapContainsValue("127.0.0.1");
    }

    @Test
    public void testUdpAsProtocol() {
        givenAPortMapping("49000:8080/udp", "127.0.0.1:49001:8081/udp");
        thenContainerPortToHostPortMapSizeIs(2);
        thenContainerPortToHostPortMapHasPortSpecAndPort("8080/udp", 49000);
        thenContainerPortToHostPortMapHasPortSpecAndPort("8081/udp", 49001);
        thenBindToHostMapContains("8081/udp", "127.0.0.1");
        thenBindToHostMapContainsOnlySpec("8080/udp");
    }

    @Test
    public void testVariableReplacementWithProps() {
        givenExistingProperty("jolokia.port", "50000");
        givenAPortMapping("jolokia.port:8080");
        whenUpdateDynamicMapping(8080, 49900, "0.0.0.0");
        thenMapAndVerifyReplacement("http://localhost:50000/", "http://localhost:${jolokia.port}/");
    }

    @Test
    public void testVariableReplacementWithSystemPropertyOverwrite() {
        try {
            System.setProperty("jolokia.port", "99999");
            givenExistingProperty("jolokia.port", "50000");
            givenAPortMapping("jolokia.port:8080");
            thenMapAndVerifyReplacement("http://localhost:99999/", "http://localhost:${jolokia.port}/");
        } finally {
            System.getProperties().remove("jolokia.port");
        }
    }

    @Test
    public void testToJson() {
        givenAPortMapping("49000:8080/udp", "127.0.0.1:49001:8081");
        thenAssertJsonEquals(("[{ hostPort: 49000, containerPort: 8080, protocol: udp }," + " { hostIP: '127.0.0.1', hostPort: 49001, containerPort: 8081, protocol: tcp}]"));
    }
}

