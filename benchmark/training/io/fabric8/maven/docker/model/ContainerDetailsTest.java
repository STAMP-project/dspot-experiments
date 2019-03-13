package io.fabric8.maven.docker.model;


import com.google.gson.JsonObject;
import org.junit.Test;


public class ContainerDetailsTest {
    private Container container;

    private JsonObject json;

    @Test
    public void testCustomNetworkIpAddresses() {
        givenNetworkSettings("custom1", "1.2.3.4", "custom2", "5.6.7.8");
        whenCreateContainer();
        thenMappingSize(2);
        thenMappingMatches("custom1", "1.2.3.4", "custom2", "5.6.7.8");
    }

    @Test
    public void testEmptyNetworkSettings() {
        givenNetworkSettings();
        whenCreateContainer();
        thenMappingIsNull();
    }

    @Test
    public void testContainerWithMappedPorts() {
        givenAContainerWithMappedPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(2);
        thenMapContainsSpecAndBinding("80/tcp", 32771, "0.0.0.0");
        thenMapContainsSpecAndBinding("52/udp", 32772, "1.2.3.4");
    }

    @Test
    public void testContainerWithPorts() {
        givenAContainerWithPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(2);
        thenMapContainsPortSpecOnly("80/tcp");
        thenMapContainsPortSpecOnly("52/udp");
    }

    @Test
    public void testContainerWithoutPorts() {
        givenAContainerWithoutPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(0);
    }

    @Test
    public void testContainerWithLabels() {
        givenAContainerWithLabels();
        whenCreateContainer();
        thenLabelsSizeIs(2);
        thenLabelsContains("key1", "value1");
        thenLabelsContains("key2", "value2");
    }

    @Test
    public void testCreateContainer() throws Exception {
        givenContainerData();
        whenCreateContainer();
        thenValidateContainer();
    }
}

