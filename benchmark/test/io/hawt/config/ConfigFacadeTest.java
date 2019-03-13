package io.hawt.config;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class ConfigFacadeTest {
    ConfigFacade facade = new ConfigFacade();

    @Test
    public void testDefaultsToHomeDirectory() throws Exception {
        File configDirectory = facade.getConfigDirectory();
        Assert.assertTrue(("the config directory should exist " + configDirectory), configDirectory.exists());
        Assert.assertTrue(("the config directory should be a directory " + configDirectory), configDirectory.isDirectory());
        Assert.assertEquals("The config directory name", ".hawtio", configDirectory.getName());
        File homeDir = new File(System.getProperty("user.home", "noHomeDir"));
        File parentFile = configDirectory.getParentFile();
        Assert.assertNotNull("Should have a parent directory", parentFile);
        Assert.assertEquals("config dir should be in the home directory", homeDir.getCanonicalPath(), parentFile.getCanonicalPath());
    }

    @Test
    public void testHasVersion() throws Exception {
        String version = facade.getVersion();
        System.out.println(("Has version: " + version));
    }
}

