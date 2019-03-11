package org.mp4parser.muxer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class MultiFileDataSourceImplTest {
    File a;

    File b;

    File c;

    @Test
    public void testWithIn() throws Exception {
        DataSource ds = new MultiFileDataSourceImpl(a, b, c);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Assert.assertEquals("a", check(ds, 0, 1));
        Assert.assertEquals("aa", check(ds, 0, 2));
        Assert.assertEquals("a", check(ds, 1, 1));
        Assert.assertEquals("aa", check(ds, 1, 2));
        Assert.assertEquals("aaaaaaaaaa", check(ds, 0, 10));
        Assert.assertEquals("aaaaaaaaaab", check(ds, 0, 11));
        Assert.assertEquals("aaaaaaab", check(ds, 3, 8));
        Assert.assertEquals("aaaaabbbbbbbbbbccccc", check(ds, 5, 20));
    }
}

