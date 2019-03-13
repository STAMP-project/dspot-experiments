package com.simpligility.maven.plugins.android;


import org.junit.Assert;
import org.junit.Test;


public class PluginInfoTest {
    @Test
    public void confirmGroupId() {
        Assert.assertEquals("com.simpligility.maven.plugins", PluginInfo.getGroupId());
    }

    @Test
    public void confirmArtifactId() {
        Assert.assertEquals("android-maven-plugin", PluginInfo.getArtifactId());
    }

    @Test
    public void confirmVersion() {
        Assert.assertNotNull(PluginInfo.getVersion());
    }

    @Test
    public void confirmGav() {
        Assert.assertTrue(PluginInfo.getGAV().startsWith("com.simpligility.maven.plugins:android-maven-plugin:"));
    }
}

