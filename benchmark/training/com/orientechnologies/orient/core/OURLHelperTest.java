package com.orientechnologies.orient.core;


import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.util.OURLConnection;
import com.orientechnologies.orient.core.util.OURLHelper;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 21/02/17.
 */
public class OURLHelperTest {
    @Test
    public void testSimpleUrl() {
        OURLConnection parsed = OURLHelper.parse("plocal:/path/test/to");
        Assert.assertEquals(parsed.getType(), "plocal");
        Assert.assertEquals(parsed.getPath(), new File("/path/test").getAbsolutePath());
        Assert.assertEquals(parsed.getDbName(), "to");
        parsed = OURLHelper.parse("memory:some");
        Assert.assertEquals(parsed.getType(), "memory");
        // assertEquals(parsed.getPath(), "");
        Assert.assertEquals(parsed.getDbName(), "some");
        parsed = OURLHelper.parse("remote:localhost/to");
        Assert.assertEquals(parsed.getType(), "remote");
        Assert.assertEquals(parsed.getPath(), "localhost");
        Assert.assertEquals(parsed.getDbName(), "to");
    }

    @Test
    public void testSimpleNewUrl() {
        OURLConnection parsed = OURLHelper.parseNew("plocal:/path/test/to");
        Assert.assertEquals(parsed.getType(), "embedded");
        Assert.assertEquals(parsed.getPath(), new File("/path/test").getAbsolutePath());
        Assert.assertEquals(parsed.getDbName(), "to");
        parsed = OURLHelper.parseNew("memory:some");
        Assert.assertEquals(parsed.getType(), "embedded");
        Assert.assertEquals(parsed.getPath(), "");
        Assert.assertEquals(parsed.getDbName(), "some");
        parsed = OURLHelper.parseNew("embedded:/path/test/to");
        Assert.assertEquals(parsed.getType(), "embedded");
        Assert.assertEquals(parsed.getPath(), new File("/path/test").getAbsolutePath());
        Assert.assertEquals(parsed.getDbName(), "to");
        parsed = OURLHelper.parseNew("remote:localhost/to");
        Assert.assertEquals(parsed.getType(), "remote");
        Assert.assertEquals(parsed.getPath(), "localhost");
        Assert.assertEquals(parsed.getDbName(), "to");
    }

    @Test(expected = OConfigurationException.class)
    public void testWrongPrefix() {
        OURLHelper.parseNew("embd:/path/test/to");
    }

    @Test(expected = OConfigurationException.class)
    public void testNoPrefix() {
        OURLHelper.parseNew("/embd/path/test/to");
    }

    @Test
    public void testRemoteNoDatabase() {
        OURLConnection parsed = OURLHelper.parseNew("remote:localhost");
        Assert.assertEquals(parsed.getType(), "remote");
        Assert.assertEquals(parsed.getPath(), "localhost");
        Assert.assertEquals(parsed.getDbName(), "");
        parsed = OURLHelper.parseNew("remote:localhost:2424");
        Assert.assertEquals(parsed.getType(), "remote");
        Assert.assertEquals(parsed.getPath(), "localhost:2424");
        Assert.assertEquals(parsed.getDbName(), "");
        parsed = OURLHelper.parseNew("remote:localhost:2424/db1");
        Assert.assertEquals(parsed.getType(), "remote");
        Assert.assertEquals(parsed.getPath(), "localhost:2424");
        Assert.assertEquals(parsed.getDbName(), "db1");
    }
}

