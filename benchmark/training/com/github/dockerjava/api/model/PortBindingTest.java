package com.github.dockerjava.api.model;


import com.github.dockerjava.api.model.Ports.Binding;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PortBindingTest {
    private static final ExposedPort TCP_8080 = ExposedPort.tcp(8080);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void fullDefinition() {
        Assert.assertEquals(PortBinding.parse("127.0.0.1:80:8080/tcp"), new PortBinding(Binding.bindIpAndPort("127.0.0.1", 80), PortBindingTest.TCP_8080));
    }

    @Test
    public void noProtocol() {
        Assert.assertEquals(PortBinding.parse("127.0.0.1:80:8080"), new PortBinding(Binding.bindIpAndPort("127.0.0.1", 80), PortBindingTest.TCP_8080));
    }

    @Test
    public void noHostIp() {
        Assert.assertEquals(PortBinding.parse("80:8080/tcp"), new PortBinding(Binding.bindPort(80), PortBindingTest.TCP_8080));
    }

    @Test
    public void portsOnly() {
        Assert.assertEquals(PortBinding.parse("80:8080"), new PortBinding(Binding.bindPort(80), PortBindingTest.TCP_8080));
    }

    @Test
    public void exposedPortOnly() {
        Assert.assertEquals(PortBinding.parse("8080"), new PortBinding(Binding.empty(), PortBindingTest.TCP_8080));
    }

    @Test
    public void dynamicHostPort() {
        Assert.assertEquals(PortBinding.parse("127.0.0.1::8080"), new PortBinding(Binding.bindIp("127.0.0.1"), PortBindingTest.TCP_8080));
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing PortBinding 'null'");
        PortBinding.parse(null);
    }
}

