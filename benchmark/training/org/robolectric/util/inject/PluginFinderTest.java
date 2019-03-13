package org.robolectric.util.inject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Priority;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PluginFinderTest {
    private final List<Class<?>> pluginClasses = new ArrayList<>();

    private PluginFinder pluginFinder;

    @Test
    public void findPlugin_shouldPickHighestPriorityClass() throws Exception {
        pluginClasses.addAll(Arrays.asList(PluginFinderTest.ImplMinus1.class, PluginFinderTest.ImplZeroA.class, PluginFinderTest.ImplOne.class, PluginFinderTest.ImplZeroB.class));
        assertThat(pluginFinder.findPlugin(PluginFinderTest.Iface.class)).isEqualTo(PluginFinderTest.ImplOne.class);
    }

    @Test
    public void findPlugin_shouldThrowIfAmbiguous() throws Exception {
        pluginClasses.addAll(Arrays.asList(PluginFinderTest.ImplMinus1.class, PluginFinderTest.ImplZeroA.class, PluginFinderTest.ImplZeroB.class));
        try {
            pluginFinder.findPlugin(PluginFinderTest.Iface.class);
            Assert.fail();
        } catch (Exception exception) {
            assertThat(exception).isInstanceOf(InjectionException.class);
        }
    }

    @Test
    public void findPlugins_shouldSortClassesInReversePriority() throws Exception {
        pluginClasses.addAll(Arrays.asList(PluginFinderTest.ImplMinus1.class, PluginFinderTest.ImplZeroA.class, PluginFinderTest.ImplOne.class, PluginFinderTest.ImplZeroB.class));
        assertThat(pluginFinder.findPlugins(PluginFinderTest.Iface.class)).containsExactly(PluginFinderTest.ImplOne.class, PluginFinderTest.ImplZeroA.class, PluginFinderTest.ImplZeroB.class, PluginFinderTest.ImplMinus1.class).inOrder();
    }

    @Test
    public void findPlugins_whenAnnotatedSupercedes_shouldExcludeSuperceded() throws Exception {
        pluginClasses.addAll(Arrays.asList(PluginFinderTest.ImplMinus1.class, PluginFinderTest.ImplZeroXSupercedesA.class, PluginFinderTest.ImplZeroA.class, PluginFinderTest.ImplOne.class, PluginFinderTest.ImplZeroB.class));
        List<Class<? extends PluginFinderTest.Iface>> plugins = pluginFinder.findPlugins(PluginFinderTest.Iface.class);
        assertThat(plugins).containsExactly(PluginFinderTest.ImplOne.class, PluginFinderTest.ImplZeroB.class, PluginFinderTest.ImplZeroXSupercedesA.class, PluginFinderTest.ImplMinus1.class).inOrder();
    }

    // //////////////
    @Priority(-1)
    private static class ImplMinus1 implements PluginFinderTest.Iface {}

    @Priority(0)
    private static class ImplZeroA implements PluginFinderTest.Iface {}

    private static class ImplZeroB implements PluginFinderTest.Iface {}

    @Priority(1)
    private static class ImplOne implements PluginFinderTest.Iface {}

    @Supercedes(PluginFinderTest.ImplZeroA.class)
    private static class ImplZeroXSupercedesA implements PluginFinderTest.Iface {}

    private interface Iface {}
}

