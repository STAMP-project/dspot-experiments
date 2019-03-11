package com.github.dockerjava.api.model;


import InternetProtocol.DEFAULT;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class InternetProtocolTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void defaultProtocol() {
        Assert.assertEquals(DEFAULT, InternetProtocol.TCP);
    }

    @Test
    public void stringify() {
        Assert.assertEquals(InternetProtocol.TCP.toString(), "tcp");
    }

    @Test
    public void parseUpperCase() {
        Assert.assertEquals(InternetProtocol.parse("TCP"), InternetProtocol.TCP);
    }

    @Test
    public void parseLowerCase() {
        Assert.assertEquals(InternetProtocol.parse("tcp"), InternetProtocol.TCP);
    }

    @Test
    public void parseInvalidInput() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Protocol 'xx'");
        InternetProtocol.parse("xx");
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Protocol 'null'");
        InternetProtocol.parse(null);
    }
}

