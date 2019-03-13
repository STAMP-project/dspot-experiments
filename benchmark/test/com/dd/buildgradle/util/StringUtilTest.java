package com.dd.buildgradle.util;


import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void isMavenArtifact() {
        Assert.assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4@aar"));
        Assert.assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4@jar"));
        Assert.assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4"));
        Assert.assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0"));
        Assert.assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder"));
    }

    @Test
    public void notMavenArtifact() {
        Assert.assertFalse(StringUtil.isMavenArtifact("app"));
        Assert.assertFalse(StringUtil.isMavenArtifact(":app"));
        Assert.assertFalse(StringUtil.isMavenArtifact(":modules:umeng_bbs"));
        Assert.assertFalse(StringUtil.isMavenArtifact("modules:umeng_bbs"));
    }
}

