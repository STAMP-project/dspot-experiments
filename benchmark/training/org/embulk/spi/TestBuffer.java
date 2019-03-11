package org.embulk.spi;


import org.junit.Assert;
import org.junit.Test;


public class TestBuffer {
    @Test
    public void testEquals() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3, 2, 3 };
        Buffer b1 = Buffer.wrap(bytes, 0, 2);// [1, 2]

        Buffer b2 = Buffer.wrap(bytes, 1, 2);// [2, 3]

        Buffer b3 = Buffer.wrap(bytes, 3, 2);// [2, 3]

        Assert.assertFalse(b1.equals(b2));
        Assert.assertTrue(b2.equals(b3));
        Assert.assertFalse(((b1.hashCode()) == (b2.hashCode())));
        Assert.assertTrue(((b2.hashCode()) == (b3.hashCode())));
    }
}

