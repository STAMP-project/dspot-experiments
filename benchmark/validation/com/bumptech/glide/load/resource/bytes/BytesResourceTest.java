package com.bumptech.glide.load.resource.bytes;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BytesResourceTest {
    @Test
    public void testReturnsGivenBytes() {
        byte[] bytes = new byte[0];
        BytesResource resource = new BytesResource(bytes);
        Assert.assertEquals(bytes, resource.get());
    }

    @Test
    public void testReturnsSizeOfGivenBytes() {
        byte[] bytes = new byte[123];
        BytesResource resource = new BytesResource(bytes);
        Assert.assertEquals(bytes.length, resource.getSize());
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfGivenNullBytes() {
        new BytesResource(null);
    }
}

