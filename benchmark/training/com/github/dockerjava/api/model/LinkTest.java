package com.github.dockerjava.api.model;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LinkTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void parse() {
        Link link = Link.parse("name:alias");
        Assert.assertEquals(link.getName(), "name");
        Assert.assertEquals(link.getAlias(), "alias");
    }

    @Test
    public void parseWithContainerNames() {
        Link link = Link.parse("/name:/conatiner/alias");
        Assert.assertEquals(link.getName(), "name");
        Assert.assertEquals(link.getAlias(), "alias");
    }

    @Test
    public void parseInvalidInput() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Link 'nonsense'");
        Link.parse("nonsense");
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Link 'null'");
        Link.parse(null);
    }

    @Test
    public void stringify() {
        Assert.assertEquals(Link.parse("name:alias").toString(), "name:alias");
    }
}

