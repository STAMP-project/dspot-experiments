package io.fabric8.maven.docker.config;


import org.junit.Assert;
import org.junit.Test;

import static LogConfiguration.DEFAULT;


/**
 * Tests LogConfiguration
 */
public class LogConfigurationTest {
    @Test
    public void testDefaultConfiguration() {
        LogConfiguration cfg = DEFAULT;
        Assert.assertNull(cfg.isEnabled());
        Assert.assertFalse(cfg.isActivated());
    }

    @Test
    public void testEmptyBuiltConfiguration() {
        LogConfiguration cfg = new LogConfiguration.Builder().build();
        Assert.assertNull(cfg.isEnabled());
        Assert.assertFalse(cfg.isActivated());
    }

    @Test
    public void testNonEmptyBuiltConfiguration() {
        LogConfiguration cfg = new LogConfiguration.Builder().file("test").build();
        Assert.assertNull(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
        cfg = new LogConfiguration.Builder().color("test").build();
        Assert.assertNull(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
        cfg = new LogConfiguration.Builder().logDriverName("test").build();
        Assert.assertNull(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
        cfg = new LogConfiguration.Builder().date("1234").build();
        Assert.assertNull(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
    }

    @Test
    public void testEnabled() {
        LogConfiguration cfg = new LogConfiguration.Builder().enabled(true).build();
        Assert.assertTrue(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
        cfg = new LogConfiguration.Builder().enabled(true).color("red").build();
        Assert.assertTrue(cfg.isEnabled());
        Assert.assertTrue(cfg.isActivated());
        Assert.assertEquals("red", cfg.getColor());
    }

    @Test
    public void testDisabled() {
        LogConfiguration cfg = new LogConfiguration.Builder().color("red").enabled(false).build();
        Assert.assertFalse(cfg.isEnabled());
        Assert.assertFalse(cfg.isActivated());
        Assert.assertEquals("red", cfg.getColor());
    }
}

