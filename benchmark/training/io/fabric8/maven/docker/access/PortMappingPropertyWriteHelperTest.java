package io.fabric8.maven.docker.access;


import PortMapping.PropertyWriteHelper;
import java.util.Properties;
import org.junit.Test;


public class PortMappingPropertyWriteHelperTest {
    private Properties loadedProperties;

    private Properties projProperties;

    private PropertyWriteHelper propertyWriteHelper;

    @Test
    public void testWriteGlobalOnly() throws Exception {
        String globalFile = createTmpFile();
        PortMapping mapping = createPortMapping("jolokia.port:8080", "18181:8181", "127.0.0.1:9090:9090", "127.0.0.1:other.port:5678");
        givenAPortMappingWriter(globalFile);
        whenUpdateDynamicMapping(mapping, "0.0.0.0", 8080, 49900);
        whenUpdateDynamicMapping(mapping, "127.0.0.1", 5678, 49901);
        whenWritePortMappings(null, mapping);
        thenPropsFileExists(globalFile);
        thenPropsSizeIs(2);
        thenPropsContains("jolokia.port", 49900);
        thenPropsContains("other.port", 49901);
    }

    @Test
    public void testWriteImageAndGlobal() throws Exception {
        String imageFile = createTmpFile();
        String globalFile = createTmpFile();
        PortMapping mapping1 = createPortMapping("jolokia.port1:8080", "18181:8181", "127.0.0.1:9090:9090", "+other.ip1:other.port1:5678");
        PortMapping mapping2 = createPortMapping("jolokia.port2:8080", "18182:8181", "127.0.0.2:9090:9090", "+other.ip2:other.port2:5678");
        PortMapping mapping3 = createPortMapping("+other.ip3:other.port3:5678");
        givenADockerHostAddress("5.6.7.8");
        givenAHostIpProperty("other.ip1", "1.2.3.4");
        givenAHostIpProperty("other.ip2", "1.2.3.4");
        givenAPortMappingWriter(globalFile);
        whenUpdateDynamicMapping(mapping1, "0.0.0.0", 8080, 49900);
        whenUpdateDynamicMapping(mapping1, "1.2.3.4", 5678, 49901);
        whenUpdateDynamicMapping(mapping2, "0.0.0.0", 8080, 49902);
        whenUpdateDynamicMapping(mapping2, "1.2.3.4", 5678, 49903);
        whenUpdateDynamicMapping(mapping3, "0.0.0.0", 5678, 49904);
        whenWritePortMappings(imageFile, mapping1);
        whenWritePortMappings(null, mapping2);
        whenWritePortMappings(null, mapping3);
        // test all file criteria in order as 'properties' is reset via the exists check
        thenPropsFileExists(globalFile);
        thenPropsSizeIs(8);
        thenPropsContains("jolokia.port1", 49900);
        thenPropsContains("other.port1", 49901);
        thenPropsContains("other.ip1", "1.2.3.4");
        thenPropsContains("jolokia.port2", 49902);
        thenPropsContains("other.port2", 49903);
        thenPropsContains("other.ip2", "1.2.3.4");
        thenPropsContains("other.port3", 49904);
        thenPropsContains("other.ip3", "5.6.7.8");
        thenPropsFileExists(imageFile);
        thenPropsSizeIs(3);
        thenPropsContains("jolokia.port1", 49900);
        thenPropsContains("other.port1", 49901);
        thenPropsContains("other.ip1", "1.2.3.4");
    }

    @Test
    public void testWriteImageOnly() throws Exception {
        String imageFile = createTmpFile();
        PortMapping mapping = createPortMapping("jolokia.port:8080", "18181:8181", "127.0.0.1:9090:9090", "127.0.0.1:other.port:5678");
        givenAPortMappingWriter(null);
        whenUpdateDynamicMapping(mapping, "0.0.0.0", 8080, 49900);
        whenUpdateDynamicMapping(mapping, "127.0.0.1", 5678, 49901);
        whenWritePortMappings(imageFile, mapping);
        thenPropsFileExists(imageFile);
        thenPropsSizeIs(2);
        thenPropsContains("jolokia.port", 49900);
        thenPropsContains("other.port", 49901);
    }
}

