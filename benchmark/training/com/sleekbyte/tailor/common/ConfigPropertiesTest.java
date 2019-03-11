package com.sleekbyte.tailor.common;


import ConfigProperties.CONFIG_RESOURCE_PATH;
import ConfigProperties.DEFAULT_VERSION;
import ConfigProperties.VERSION_PROPERTY;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConfigPropertiesTest {
    @Test
    public void testGetVersion() throws IOException {
        ConfigProperties configProperties = new ConfigProperties();
        Properties prop = new Properties();
        try (InputStream in = getClass().getResourceAsStream(CONFIG_RESOURCE_PATH)) {
            prop.load(in);
        }
        Assert.assertEquals(prop.getProperty(VERSION_PROPERTY), configProperties.getVersion());
    }

    @Test
    public void testMissingConfigVersion() throws IOException {
        ConfigProperties configProperties = Mockito.spy(ConfigProperties.class);
        Mockito.when(configProperties.getConfigResource()).thenReturn(null);
        Assert.assertEquals(DEFAULT_VERSION, configProperties.getVersion());
    }

    @Test
    public void testVersionNumberMatchesSemanticVersionFormat() throws IOException {
        ConfigProperties configProperties = new ConfigProperties();
        Assert.assertTrue("Version number should match MAJOR.MINOR.PATCH format from http://semver.org.", configProperties.getVersion().matches("\\d+\\.\\d+\\.\\d+"));
    }
}

