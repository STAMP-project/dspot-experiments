package org.kairosdb.core;


import ConfigFormat.HOCON;
import ConfigFormat.JSON;
import ConfigFormat.PROPERTIES;
import org.junit.Assert;
import org.junit.Test;


public class ConfigFormatTest {
    @Test
    public void getExtension() throws Exception {
        Assert.assertEquals("properties", PROPERTIES.getExtension());
        Assert.assertEquals("json", JSON.getExtension());
        Assert.assertEquals("conf", HOCON.getExtension());
    }

    @Test
    public void fromFileName() throws Exception {
        Assert.assertEquals(PROPERTIES, KairosRootConfig.ConfigFormat.fromFileName("config.properties"));
        Assert.assertEquals(JSON, KairosRootConfig.ConfigFormat.fromFileName("config.json"));
        Assert.assertEquals(HOCON, KairosRootConfig.ConfigFormat.fromFileName("config.conf"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromFileName_withInvalidFileType() throws Exception {
        KairosRootConfig.ConfigFormat.fromFileName("config.yaml");
    }

    @Test
    public void fromExtension() throws Exception {
        Assert.assertEquals(PROPERTIES, KairosRootConfig.ConfigFormat.fromExtension("properties"));
        Assert.assertEquals(JSON, KairosRootConfig.ConfigFormat.fromExtension("json"));
        Assert.assertEquals(HOCON, KairosRootConfig.ConfigFormat.fromExtension("conf"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromExtension_withInvalidExtension() throws Exception {
        KairosRootConfig.ConfigFormat.fromExtension("yaml");
    }
}

