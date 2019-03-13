package org.stagemonitor.core;


import com.codahale.metrics.health.HealthCheckRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationRegistry;


public class StagemonitorTest {
    private static ConfigurationRegistry originalConfiguration;

    private final HealthCheckRegistry healthCheckRegistry = Stagemonitor.getHealthCheckRegistry();

    private ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);

    private CorePlugin corePlugin = Mockito.mock(CorePlugin.class);

    @Test
    public void testStartMonitoring() throws Exception {
        Mockito.when(corePlugin.isStagemonitorActive()).thenReturn(true);
        Stagemonitor.setConfiguration(configuration);
        Stagemonitor.reset();
        final MeasurementSession measurementSession = new MeasurementSession("StagemonitorTest", "testHost", "testInstance");
        Stagemonitor.reset(measurementSession);
        Assert.assertTrue(Stagemonitor.isStarted());
        Assert.assertTrue(isInitialized());
        Assert.assertSame(measurementSession, Stagemonitor.getMeasurementSession());
        assertThat(healthCheckRegistry.runHealthCheck("TestPlugin").isHealthy()).isTrue();
        assertThat(healthCheckRegistry.runHealthCheck("TestExceptionPlugin").isHealthy()).isFalse();
    }

    @Test
    public void testStartMonitoringNotActive() throws Exception {
        Mockito.when(corePlugin.isStagemonitorActive()).thenReturn(false);
        final MeasurementSession measurementSession = new MeasurementSession("StagemonitorTest", "testHost", "testInstance");
        Stagemonitor.reset(measurementSession);
        Assert.assertTrue(Stagemonitor.isDisabled());
        Assert.assertFalse(Stagemonitor.isStarted());
        Assert.assertTrue(isInitialized());
        assertThat(healthCheckRegistry.getNames()).doesNotContain("TestPlugin", "TestExceptionPlugin");
    }

    @Test
    public void testDisabledPlugin() throws Exception {
        Mockito.when(corePlugin.isStagemonitorActive()).thenReturn(true);
        Mockito.when(corePlugin.getDisabledPlugins()).thenReturn(Collections.singletonList("TestExceptionPlugin"));
        Stagemonitor.reset(new MeasurementSession("StagemonitorTest", "testHost", "testInstance"));
        assertThat(healthCheckRegistry.runHealthCheck("TestPlugin").isHealthy()).isTrue();
        assertThat(healthCheckRegistry.runHealthCheck("TestExceptionPlugin").isHealthy()).isFalse();
        assertThat(healthCheckRegistry.runHealthCheck("TestExceptionPlugin").getMessage()).isEqualTo("disabled via configuration");
    }

    @Test
    public void testNotInitialized() throws Exception {
        Mockito.when(corePlugin.isStagemonitorActive()).thenReturn(true);
        final MeasurementSession measurementSession = new MeasurementSession(null, "testHost", "testInstance");
        Stagemonitor.reset(measurementSession);
        Assert.assertFalse(Stagemonitor.isStarted());
    }

    public static class PluginNoDependency extends StagemonitorPlugin {
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Collections.emptyList();
        }
    }

    public static class PluginSimpleDependency extends StagemonitorPlugin {
        @Override
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Collections.singletonList(StagemonitorTest.PluginNoDependency.class);
        }
    }

    public static class PluginSimpleDependency2 extends StagemonitorPlugin {
        @Override
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Collections.singletonList(StagemonitorTest.PluginNoDependency.class);
        }
    }

    public static class PluginMultipleDependencies extends StagemonitorPlugin {
        @Override
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Arrays.asList(StagemonitorTest.PluginSimpleDependency.class, StagemonitorTest.PluginSimpleDependency2.class, StagemonitorTest.PluginNoDependency.class);
        }
    }

    public static class PluginCyclicA extends StagemonitorPlugin {
        @Override
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Collections.singletonList(StagemonitorTest.PluginCyclicB.class);
        }
    }

    public static class PluginCyclicB extends StagemonitorPlugin {
        @Override
        public List<Class<? extends StagemonitorPlugin>> dependsOn() {
            return Collections.singletonList(StagemonitorTest.PluginCyclicA.class);
        }
    }

    @Test
    public void testInitPluginsCyclicDependency() {
        final List<StagemonitorPlugin> plugins = Arrays.asList(new StagemonitorTest.PluginNoDependency(), new StagemonitorTest.PluginCyclicA(), new StagemonitorTest.PluginCyclicB());
        assertThatThrownBy(() -> Stagemonitor.initializePluginsInOrder(Collections.emptyList(), plugins)).isInstanceOf(IllegalStateException.class).hasMessageContaining("PluginCyclicA").hasMessageContaining("PluginCyclicB");
    }

    @Test
    public void testInitPlugins_NoDependency() {
        final StagemonitorTest.PluginNoDependency pluginNoDependency = new StagemonitorTest.PluginNoDependency();
        Stagemonitor.initializePluginsInOrder(Collections.emptyList(), Collections.singletonList(pluginNoDependency));
        assertThat(isInitialized()).isTrue();
    }

    @Test
    public void testInitPlugins_SimpleDependency() {
        final StagemonitorTest.PluginNoDependency pluginNoDependency = new StagemonitorTest.PluginNoDependency();
        final StagemonitorTest.PluginSimpleDependency pluginSimpleDependency = new StagemonitorTest.PluginSimpleDependency();
        Stagemonitor.initializePluginsInOrder(Collections.emptyList(), Arrays.asList(pluginNoDependency, pluginSimpleDependency));
        assertThat(isInitialized()).isTrue();
        assertThat(isInitialized()).isTrue();
    }

    @Test
    public void testInitPlugins_MultipleDependencies() {
        final List<StagemonitorPlugin> plugins = Arrays.asList(new StagemonitorTest.PluginMultipleDependencies(), new StagemonitorTest.PluginSimpleDependency2(), new StagemonitorTest.PluginNoDependency(), new StagemonitorTest.PluginSimpleDependency());
        Stagemonitor.initializePluginsInOrder(Collections.emptyList(), plugins);
        for (StagemonitorPlugin plugin : plugins) {
            assertThat(plugin.isInitialized()).describedAs("{} is not initialized", plugin.getClass().getSimpleName()).isTrue();
        }
    }
}

