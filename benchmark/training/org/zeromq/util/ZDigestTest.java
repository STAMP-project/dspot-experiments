package org.zeromq.util;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ZDigestTest {
    @Test
    public void testData() {
        byte[] buf = new byte[1024];
        Arrays.fill(buf, ((byte) (170)));
        ZDigest digest = new ZDigest();
        digest.update(buf);
        byte[] data = digest.data();
        Assert.assertThat(byt(data[0]), CoreMatchers.is(222));
        Assert.assertThat(byt(data[1]), CoreMatchers.is(178));
        Assert.assertThat(byt(data[2]), CoreMatchers.is(56));
        Assert.assertThat(byt(data[3]), CoreMatchers.is(7));
    }

    @Test
    public void testSize() {
        byte[] buf = new byte[1024];
        Arrays.fill(buf, ((byte) (170)));
        ZDigest digest = new ZDigest();
        digest.update(buf);
        int size = digest.size();
        Assert.assertThat(size, CoreMatchers.is(20));
    }

    @Test
    public void testString() {
        byte[] buf = new byte[1024];
        Arrays.fill(buf, ((byte) (170)));
        ZDigest digest = new ZDigest();
        digest.update(buf);
        String string = digest.string();
        Assert.assertThat(string, CoreMatchers.is("DEB23807D4FE025E900FE9A9C7D8410C3DDE9671"));
    }
}

