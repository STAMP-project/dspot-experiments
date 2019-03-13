package org.stagemonitor.web.servlet.configuration;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.configuration.source.SimpleSource;


public class ConfigurationPasswordCheckerTest {
    private final String passwordKey = "configuration.password";

    private ConfigurationRegistry configuration;

    private ConfigurationPasswordChecker configurationPasswordChecker;

    @Test
    public void testIsPasswordSetTrue() throws Exception {
        ConfigurationRegistry configuration = ConfigurationRegistry.builder().build();
        configuration.addConfigurationSource(SimpleSource.forTest(passwordKey, ""));
        Assert.assertTrue(isPasswordSet());
    }

    @Test
    public void testIsPasswordSetFalse() throws Exception {
        ConfigurationRegistry configuration = ConfigurationRegistry.builder().build();
        Assert.assertFalse(isPasswordSet());
    }

    @Test
    public void testUpdateConfigurationWithoutPasswordSet() throws IOException {
        try {
            configurationPasswordChecker.assertPasswordCorrect(null);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("'configuration.password' is not set.", e.getMessage());
        }
    }

    @Test
    public void testSetNewPasswordViaQueryParamsShouldFail() throws IOException {
        configuration.addConfigurationSource(SimpleSource.forTest(passwordKey, ""));
        try {
            configurationPasswordChecker.assertPasswordCorrect("");
            configuration.save(passwordKey, "", null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Config key 'configuration.password' does not exist.", e.getMessage());
        }
    }

    @Test
    public void testUpdateConfigurationWithoutPassword() throws IOException {
        configuration.addConfigurationSource(SimpleSource.forTest(passwordKey, "pwd"));
        try {
            configurationPasswordChecker.assertPasswordCorrect(null);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Wrong password for 'configuration.password'.", e.getMessage());
        }
    }

    @Test
    public void testUpdateConfigurationWithPassword() throws IOException {
        configuration.addConfigurationSource(SimpleSource.forTest(passwordKey, "pwd"));
        configurationPasswordChecker.assertPasswordCorrect("pwd");
    }
}

