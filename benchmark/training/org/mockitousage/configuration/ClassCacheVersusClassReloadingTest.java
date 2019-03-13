/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.configuration;


import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.configuration.ConfigurationAccess;
import org.mockitoutil.SimplePerRealmReloadingClassLoader;


public class ClassCacheVersusClassReloadingTest {
    // TODO refactor to use ClassLoaders
    private SimplePerRealmReloadingClassLoader testMethodClassLoaderRealm = new SimplePerRealmReloadingClassLoader(ClassCacheVersusClassReloadingTest.reloadMockito());

    @Test
    public void should_not_throw_ClassCastException_when_objenesis_cache_disabled() throws Exception {
        prepareMockitoAndDisableObjenesisCache();
        ClassCacheVersusClassReloadingTest.doInNewChildRealm(testMethodClassLoaderRealm, "org.mockitousage.configuration.ClassCacheVersusClassReloadingTest$DoTheMocking");
        ClassCacheVersusClassReloadingTest.doInNewChildRealm(testMethodClassLoaderRealm, "org.mockitousage.configuration.ClassCacheVersusClassReloadingTest$DoTheMocking");
    }

    public static class DoTheMocking implements Callable<Object> {
        public Object call() throws Exception {
            Class<?> clazz = this.getClass().getClassLoader().loadClass("org.mockitousage.configuration.ClassToBeMocked");
            return Mockito.mock(clazz);
        }
    }

    public static class PrepareMockito implements Callable<Boolean> {
        public Boolean call() throws Exception {
            Class.forName("org.mockito.Mockito");
            ConfigurationAccess.getConfig().overrideEnableClassCache(false);
            return Boolean.TRUE;
        }
    }
}

