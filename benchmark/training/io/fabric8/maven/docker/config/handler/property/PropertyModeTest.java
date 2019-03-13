package io.fabric8.maven.docker.config.handler.property;


import PropertyMode.Fallback;
import PropertyMode.Only;
import org.junit.Assert;
import org.junit.Test;


public class PropertyModeTest {
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEmpty() {
        PropertyMode.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParse() {
        PropertyMode.parse("propertiespom");
    }

    @Test
    public void testParse() {
        Assert.assertEquals(Only, PropertyMode.parse(null));
        Assert.assertEquals(Only, PropertyMode.parse("only"));
        Assert.assertEquals(Fallback, PropertyMode.parse("fallback"));
    }
}

