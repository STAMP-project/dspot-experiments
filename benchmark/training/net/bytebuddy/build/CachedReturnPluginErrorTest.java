package net.bytebuddy.build;


import CachedReturnPlugin.Enhance;
import net.bytebuddy.ByteBuddy;
import org.junit.Test;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;


public class CachedReturnPluginErrorTest {
    private Plugin plugin;

    @Test(expected = IllegalStateException.class)
    public void testCacheVoid() {
        plugin.apply(new ByteBuddy().redefine(CachedReturnPluginErrorTest.VoidCache.class), net.bytebuddy.description.type.TypeDescription.ForLoadedType.of(CachedReturnPluginErrorTest.VoidCache.class), of(CachedReturnPluginErrorTest.VoidCache.class.getClassLoader()));
    }

    @Test(expected = IllegalStateException.class)
    public void testAbstractMethod() {
        plugin.apply(new ByteBuddy().redefine(CachedReturnPluginErrorTest.AbstractCache.class), net.bytebuddy.description.type.TypeDescription.ForLoadedType.of(CachedReturnPluginErrorTest.AbstractCache.class), of(CachedReturnPluginErrorTest.AbstractCache.class.getClassLoader()));
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterMethod() {
        plugin.apply(new ByteBuddy().redefine(CachedReturnPluginErrorTest.ParameterCache.class), net.bytebuddy.description.type.TypeDescription.ForLoadedType.of(CachedReturnPluginErrorTest.ParameterCache.class), of(CachedReturnPluginErrorTest.ParameterCache.class.getClassLoader()));
    }

    private static class VoidCache {
        @CachedReturnPlugin.Enhance
        private void foo() {
            /* do nothing */
        }
    }

    private abstract static class AbstractCache {
        @CachedReturnPlugin.Enhance
        protected abstract void foo();
    }

    private static class ParameterCache {
        @CachedReturnPlugin.Enhance
        private void foo(Void argument) {
            /* do nothing */
        }
    }
}

