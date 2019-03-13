package org.web3j.utils;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class VersionTest {
    @Test
    public void testGetVersion() throws IOException {
        Assert.assertThat(Version.getVersion(), Is.is(Version.DEFAULT));
    }

    @Test
    public void testGetTimestamp() throws IOException {
        Assert.assertThat(Version.getTimestamp(), Is.is("2017-01-31 01:21:09.843 UTC"));
    }
}

