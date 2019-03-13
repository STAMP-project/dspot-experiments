package com.bumptech.glide.load.resource;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SimpleResourceTest {
    private SimpleResourceTest.Anything object;

    private SimpleResource<?> resource;

    @Test
    public void testReturnsGivenObject() {
        Assert.assertEquals(object, resource.get());
    }

    @Test
    public void testReturnsGivenObjectMultipleTimes() {
        Assert.assertEquals(object, resource.get());
        Assert.assertEquals(object, resource.get());
        Assert.assertEquals(object, resource.get());
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfGivenNullData() {
        new SimpleResource(null);
    }

    private static class Anything {}
}

