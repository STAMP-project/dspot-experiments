package io.fabric8.maven.docker.model;


import com.google.gson.JsonObject;
import org.junit.Test;


public class ContainerListElementTest {
    private Container container;

    private JsonObject json;

    @Test
    public void testContaierWithMappedPorts() {
        givenAContainerWithMappedPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(2);
        thenMapContainsSpecAndBinding("80/tcp", 32771, "0.0.0.0");
        thenMapContainsSpecAndBinding("52/udp", 32772, "1.2.3.4");
    }

    @Test
    public void testContaierWithPorts() {
        givenAContainerWithPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(2);
        thenMapContainsPortSpecOnly("80/tcp");
        thenMapContainsPortSpecOnly("52/udp");
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
    public void testContainerWithoutLabels() {
        givenContainerData();
        whenCreateContainer();
        thenLabelsSizeIs(0);
    }

    @Test
    public void testContainerWithoutPorts() {
        givenAContainerWithoutPorts();
        whenCreateContainer();
        thenPortBindingSizeIs(0);
    }

    @Test
    public void testCreateContainer() throws Exception {
        givenContainerData();
        whenCreateContainer();
        thenValidateContainer();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoNameInListElement() {
        new ContainersListElement(new JsonObject()).getName();
    }
}

