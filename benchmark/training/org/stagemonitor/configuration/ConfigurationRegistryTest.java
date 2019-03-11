package org.stagemonitor.configuration;


import SimpleSource.NAME;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.source.ConfigurationSource;
import org.stagemonitor.configuration.source.SimpleSource;
import org.stagemonitor.configuration.source.SystemPropertyConfigurationSource;
import org.stagemonitor.core.CorePlugin;


public class ConfigurationRegistryTest {
    private ConfigurationRegistry configuration;

    private CorePlugin corePlugin;

    @Test
    public void testGetConfigSubclass() {
        final CorePlugin corePluginMock = Mockito.mock(CorePlugin.class);
        configuration = ConfigurationRegistry.builder().addOptionProvider(corePluginMock).addConfigSource(new SimpleSource()).build();
        Assert.assertSame(corePluginMock, configuration.getConfig(CorePlugin.class));
        Assert.assertNull(configuration.getConfig(new ConfigurationOptionProvider() {}.getClass()));
    }

    @Test
    public void testUpdateConfiguration() throws IOException {
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
        configuration.save("stagemonitor.internal.monitoring", "true", NAME);
        Assert.assertTrue(corePlugin.isInternalMonitoringActive());
    }

    @Test
    public void testUpdateConfigurationWrongDatatype() throws IOException {
        configuration.addConfigurationSource(SimpleSource.forTest("stagemonitor.internal.monitoring", "1"));
        configuration.reloadAllConfigurationOptions();
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
        Assert.assertEquals("Error in Test Configuration Source: Can't convert '1' to Boolean.", configuration.getConfigurationOptionByKey("stagemonitor.internal.monitoring").getErrorMessage());
        configuration.save("stagemonitor.internal.monitoring", "true", "Test Configuration Source");
        Assert.assertTrue(corePlugin.isInternalMonitoringActive());
        Assert.assertNull(configuration.getConfigurationOptionByKey("stagemonitor.internal.monitoring").getErrorMessage());
    }

    @Test
    public void testUpdateConfigurationWrongConfigurationSource() throws IOException {
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
        try {
            configuration.save("stagemonitor.internal.monitoring", "true", "foo");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Configuration source 'foo' does not exist.", e.getMessage());
        }
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
    }

    @Test
    public void testUpdateConfigurationNotSaveableConfigurationSource() throws IOException {
        configuration.addConfigurationSource(new SystemPropertyConfigurationSource());
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
        try {
            configuration.save("stagemonitor.internal.monitoring", "true", "Java System Properties");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("Saving to Java System Properties is not possible.", e.getMessage());
        }
        Assert.assertFalse(corePlugin.isInternalMonitoringActive());
    }

    @Test
    public void testUpdateConfigurationNonDynamicTransient() throws IOException {
        Assert.assertEquals(0, corePlugin.getConsoleReportingInterval());
        try {
            configuration.save("stagemonitor.reporting.interval.console", "1", NAME);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Non dynamic options can't be saved to a transient configuration source.", e.getMessage());
        }
        Assert.assertEquals(0, corePlugin.getConsoleReportingInterval());
    }

    @Test
    public void testUpdateConfigurationNonDynamicPersistent() throws IOException {
        final ConfigurationSource persistentSourceMock = Mockito.mock(ConfigurationSource.class);
        Mockito.when(persistentSourceMock.isSavingPossible()).thenReturn(true);
        Mockito.when(persistentSourceMock.isSavingPersistent()).thenReturn(true);
        Mockito.when(persistentSourceMock.getName()).thenReturn("Test Persistent");
        configuration.addConfigurationSource(persistentSourceMock);
        Assert.assertEquals(0, corePlugin.getConsoleReportingInterval());
        configuration.save("stagemonitor.reporting.interval.console", "1", "Test Persistent");
        Mockito.verify(persistentSourceMock).save("stagemonitor.reporting.interval.console", "1");
    }
}

