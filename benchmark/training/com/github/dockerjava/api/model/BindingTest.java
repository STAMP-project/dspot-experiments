package com.github.dockerjava.api.model;


import com.github.dockerjava.api.model.Ports.Binding;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BindingTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void parseIpAndPort() {
        Assert.assertEquals(Binding.parse("127.0.0.1:80"), Binding.bindIpAndPort("127.0.0.1", 80));
    }

    @Test
    public void parsePortOnly() {
        Assert.assertEquals(Binding.parse("80"), Binding.bindPort(80));
    }

    @Test
    public void parseIPOnly() {
        Assert.assertEquals(Binding.parse("127.0.0.1"), Binding.bindIp("127.0.0.1"));
    }

    @Test
    public void parseEmptyString() {
        Assert.assertEquals(Binding.parse(""), Binding.empty());
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Binding 'null'");
        Binding.parse(null);
    }

    @Test
    public void toStringIpAndHost() {
        Assert.assertEquals(Binding.parse("127.0.0.1:80").toString(), "127.0.0.1:80");
    }

    @Test
    public void toStringPortOnly() {
        Assert.assertEquals(Binding.parse("80").toString(), "80");
    }

    @Test
    public void toStringIpOnly() {
        Assert.assertEquals(Binding.parse("127.0.0.1").toString(), "127.0.0.1");
    }
}

