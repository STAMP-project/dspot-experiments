package com.github.dockerjava.api.model;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ExposedPortTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void parsePortAndProtocol() {
        ExposedPort exposedPort = ExposedPort.parse("80/tcp");
        Assert.assertEquals(exposedPort, new ExposedPort(80, InternetProtocol.TCP));
    }

    @Test
    public void parsePortOnly() {
        ExposedPort exposedPort = ExposedPort.parse("80");
        Assert.assertEquals(exposedPort, new ExposedPort(80, InternetProtocol.DEFAULT));
    }

    @Test
    public void parseInvalidInput() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing ExposedPort 'nonsense'");
        ExposedPort.parse("nonsense");
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing ExposedPort 'null'");
        ExposedPort.parse(null);
    }

    @Test
    public void stringify() {
        Assert.assertEquals(ExposedPort.parse("80/tcp").toString(), "80/tcp");
    }
}

