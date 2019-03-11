package org.slf4j.log4j12;


import org.junit.Assert;
import org.junit.Test;


public class UtilVersionTest {
    @Test
    public void test() {
        System.out.println(System.getProperty("java.version"));
        Assert.assertEquals(6, VersionUtil.getJavaMajorVersion("1.6"));
        Assert.assertEquals(7, VersionUtil.getJavaMajorVersion("1.7.0_21-b11"));
        Assert.assertEquals(8, VersionUtil.getJavaMajorVersion("1.8.0_25"));
    }
}

