package net.bytebuddy.build;


import ToStringPlugin.Enhance;
import ToStringPlugin.Exclude;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ToStringPluginTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testPluginMatches() throws Exception {
        Plugin plugin = new ToStringPlugin();
        MatcherAssert.assertThat(plugin.matches(TypeDescription.ForLoadedType.of(ToStringPluginTest.SimpleSample.class)), Is.is(true));
        MatcherAssert.assertThat(plugin.matches(TypeDescription.OBJECT), Is.is(false));
    }

    @Test
    public void testPluginEnhance() throws Exception {
        Class<?> type = new ToStringPlugin().apply(new ByteBuddy().redefine(ToStringPluginTest.SimpleSample.class), TypeDescription.ForLoadedType.of(ToStringPluginTest.SimpleSample.class), of(ToStringPluginTest.SimpleSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        type.getDeclaredField(ToStringPluginTest.FOO).set(instance, ToStringPluginTest.FOO);
        MatcherAssert.assertThat(instance.toString(), Is.is("SimpleSample{foo=foo}"));
    }

    @Test
    public void testPluginEnhanceRedundant() throws Exception {
        Class<?> type = new ToStringPlugin().apply(new ByteBuddy().redefine(ToStringPluginTest.RedundantSample.class), TypeDescription.ForLoadedType.of(ToStringPluginTest.RedundantSample.class), of(ToStringPluginTest.RedundantSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance().toString(), Is.is(ToStringPluginTest.BAR));
    }

    @Test
    public void testPluginEnhanceIgnore() throws Exception {
        Class<?> type = new ToStringPlugin().apply(new ByteBuddy().redefine(ToStringPluginTest.IgnoredFieldSample.class), TypeDescription.ForLoadedType.of(ToStringPluginTest.IgnoredFieldSample.class), of(ToStringPluginTest.IgnoredFieldSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        type.getDeclaredField(ToStringPluginTest.FOO).set(instance, ToStringPluginTest.FOO);
        MatcherAssert.assertThat(instance.toString(), Is.is("IgnoredFieldSample{}"));
    }

    @ToStringPlugin.Enhance
    public static class SimpleSample {
        public String foo;
    }

    @ToStringPlugin.Enhance
    public static class IgnoredFieldSample {
        @ToStringPlugin.Exclude
        public String foo;
    }

    @ToStringPlugin.Enhance
    public static class RedundantSample {
        public String toString() {
            return ToStringPluginTest.BAR;
        }
    }
}

