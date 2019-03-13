package com.github.dockerjava.api.model;


import com.github.dockerjava.api.model.Ports.Binding;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * As there may be several {@link Binding}s per {@link ExposedPort}, it makes a difference if you add {@link PortBinding}s for the same or
 * different {@link ExposedPort}s to {@link Ports}. This test verifies that the Map in {@link Ports} is populated correctly in both cases.
 */
public class PortsAddBindingsTest {
    private static final ExposedPort TCP_80 = ExposedPort.tcp(80);

    private static final ExposedPort TCP_90 = ExposedPort.tcp(90);

    private static final Binding BINDING_8080 = Binding.bindPort(8080);

    private static final Binding BINDING_9090 = Binding.bindPort(9090);

    private Ports ports;

    @Test
    public void addTwoBindingsForDifferentExposedPorts() {
        ports.add(new PortBinding(PortsAddBindingsTest.BINDING_8080, PortsAddBindingsTest.TCP_80), new PortBinding(PortsAddBindingsTest.BINDING_9090, PortsAddBindingsTest.TCP_90));
        Map<ExposedPort, Binding[]> bindings = ports.getBindings();
        // two keys with one value each
        Assert.assertEquals(bindings.size(), 2);
        Assert.assertEquals(bindings.get(PortsAddBindingsTest.TCP_80), new Binding[]{ PortsAddBindingsTest.BINDING_8080 });
        Assert.assertEquals(bindings.get(PortsAddBindingsTest.TCP_90), new Binding[]{ PortsAddBindingsTest.BINDING_9090 });
    }

    @Test
    public void addTwoBindingsForSameExposedPort() {
        ports.add(new PortBinding(PortsAddBindingsTest.BINDING_8080, PortsAddBindingsTest.TCP_80), new PortBinding(PortsAddBindingsTest.BINDING_9090, PortsAddBindingsTest.TCP_80));
        Map<ExposedPort, Binding[]> bindings = ports.getBindings();
        // one key with two values
        Assert.assertEquals(bindings.size(), 1);
        Assert.assertEquals(bindings.get(PortsAddBindingsTest.TCP_80), new Binding[]{ PortsAddBindingsTest.BINDING_8080, PortsAddBindingsTest.BINDING_9090 });
    }

    @Test
    public void addNullBindings() {
        ports.add(new PortBinding(null, PortsAddBindingsTest.TCP_80));
        Map<ExposedPort, Binding[]> bindings = ports.getBindings();
        // one key with two values
        Assert.assertEquals(bindings.size(), 1);
        Assert.assertEquals(bindings.get(PortsAddBindingsTest.TCP_80), null);
    }
}

