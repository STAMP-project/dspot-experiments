package com.simpligility.maven.plugins.android.config;


import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.junit.Assert;
import org.junit.Test;


public class ConfigHandlerTest {
    private DummyMojo mojo = new DummyMojo();

    private MavenSession session;

    private MojoExecution execution;

    @Test
    public void testParseConfigurationDefault() throws Exception {
        ConfigHandler configHandler = new ConfigHandler(mojo, this.session, this.execution);
        configHandler.parseConfiguration();
        Assert.assertTrue(mojo.getParsedBooleanValue());
    }

    @Test
    public void testParseConfigurationFromConfigPojo() throws Exception {
        mojo.setConfigPojo(new DummyConfigPojo("from config pojo", null));
        ConfigHandler configHandler = new ConfigHandler(mojo, this.session, this.execution);
        configHandler.parseConfiguration();
        Assert.assertEquals("from config pojo", mojo.getParsedStringValue());
    }

    @Test
    public void testParseConfigurationFromMaven() throws Exception {
        mojo.setConfigPojoStringValue("maven value");
        ConfigHandler configHandler = new ConfigHandler(mojo, this.session, this.execution);
        configHandler.parseConfiguration();
        Assert.assertEquals("maven value", mojo.getParsedStringValue());
    }

    @Test
    public void testParseConfigurationDefaultMethodValue() throws Exception {
        ConfigHandler configHandler = new ConfigHandler(mojo, this.session, this.execution);
        configHandler.parseConfiguration();
        Assert.assertArrayEquals(new String[]{ "a", "b" }, mojo.getParsedMethodValue());
    }
}

