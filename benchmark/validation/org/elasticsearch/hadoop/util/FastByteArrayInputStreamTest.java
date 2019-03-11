package org.elasticsearch.hadoop.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class FastByteArrayInputStreamTest {
    private FastByteArrayInputStream whole;

    private FastByteArrayInputStream middle;

    @Test
    public void read() throws Exception {
        Assert.assertEquals(1, whole.read());
        Assert.assertEquals(2, middle.read());
    }

    @Test
    public void read1() throws Exception {
        byte[] array = new byte[6];
        int bytesRead = whole.read(array, 0, array.length);
        Assert.assertEquals(6, bytesRead);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5, 6 }, array);
        Arrays.fill(array, ((byte) (0)));
        bytesRead = middle.read(array, 0, array.length);
        Assert.assertEquals(4, bytesRead);
        Assert.assertArrayEquals(new byte[]{ 2, 3, 4, 5, 0, 0 }, array);
    }

    @Test
    public void skip() throws Exception {
        {
            long skipped = whole.skip(1);
            int value = whole.read();
            Assert.assertEquals(1L, skipped);
            Assert.assertEquals(2, value);
        }
        {
            long skipped = middle.skip(1);
            int value = middle.read();
            Assert.assertEquals(1L, skipped);
            Assert.assertEquals(3, value);
        }
    }

    @Test
    public void available() throws Exception {
        Assert.assertEquals(6, whole.available());
        Assert.assertEquals(4, middle.available());
    }

    @Test
    public void position() throws Exception {
        {
            int pos0 = whole.position();
            whole.skip(1);
            int pos1 = whole.position();
            whole.skip(6);
            int pos2 = whole.position();
            Assert.assertEquals(0, pos0);
            Assert.assertEquals(1, pos1);
            Assert.assertEquals(6, pos2);
        }
        {
            int pos0 = middle.position();
            middle.skip(1);
            int pos1 = middle.position();
            middle.skip(6);
            int pos2 = middle.position();
            Assert.assertEquals(0, pos0);
            Assert.assertEquals(1, pos1);
            Assert.assertEquals(4, pos2);
        }
    }

    @Test
    public void markSupported() throws Exception {
        Assert.assertTrue(whole.markSupported());
        Assert.assertTrue(middle.markSupported());
    }

    @Test
    public void markAndReset() throws Exception {
        Assume.assumeTrue(whole.markSupported());
        whole.mark(1024);
        int read = whole.read();
        whole.reset();
        int reread = whole.read();
        Assert.assertEquals(read, reread);
    }
}

