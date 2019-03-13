package com.ctrip.framework.foundation.internals.provider;


import java.io.File;
import java.io.FileInputStream;
import org.junit.Assert;
import org.junit.Test;


public class DefaultServerProviderTest {
    private DefaultServerProvider defaultServerProvider;

    @Test
    public void testEnvWithSystemProperty() throws Exception {
        String someEnv = "someEnv";
        String someDc = "someDc";
        System.setProperty("env", someEnv);
        System.setProperty("idc", someDc);
        defaultServerProvider.initialize(null);
        Assert.assertEquals(someEnv, defaultServerProvider.getEnvType());
        Assert.assertEquals(someDc, defaultServerProvider.getDataCenter());
    }

    @Test
    public void testWithPropertiesStream() throws Exception {
        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));
        Assert.assertEquals("SHAJQ", defaultServerProvider.getDataCenter());
        Assert.assertTrue(defaultServerProvider.isEnvTypeSet());
        Assert.assertEquals("DEV", defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithUTF8BomPropertiesStream() throws Exception {
        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server-with-utf8bom.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));
        Assert.assertEquals("SHAJQ", defaultServerProvider.getDataCenter());
        Assert.assertTrue(defaultServerProvider.isEnvTypeSet());
        Assert.assertEquals("DEV", defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithPropertiesStreamAndEnvFromSystemProperty() throws Exception {
        String prodEnv = "pro";
        System.setProperty("env", prodEnv);
        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));
        String predefinedDataCenter = "SHAJQ";
        Assert.assertEquals(predefinedDataCenter, defaultServerProvider.getDataCenter());
        Assert.assertTrue(defaultServerProvider.isEnvTypeSet());
        Assert.assertEquals(prodEnv, defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithNoPropertiesStream() throws Exception {
        defaultServerProvider.initialize(null);
        Assert.assertNull(defaultServerProvider.getDataCenter());
        Assert.assertFalse(defaultServerProvider.isEnvTypeSet());
        Assert.assertNull(defaultServerProvider.getEnvType());
    }
}

