package net.bytebuddy;


import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.TypeCache.Sort.SOFT;
import static net.bytebuddy.TypeCache.Sort.WEAK;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class TypeCacheTest {
    @Test
    public void testCache() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Object key = new Object();
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(typeCache.insert(ClassLoader.getSystemClassLoader(), key, Void.class), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(Mockito.mock(ClassLoader.class), key), CoreMatchers.nullValue(Class.class));
        typeCache.clear();
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.nullValue(Class.class));
    }

    @Test
    public void testCacheInline() throws Exception {
        TypeCache<Object> typeCache = new TypeCache.WithInlineExpunction<Object>(WEAK);
        Object key = new Object();
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(typeCache.insert(ClassLoader.getSystemClassLoader(), key, Void.class), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(Mockito.mock(ClassLoader.class), key), CoreMatchers.nullValue(Class.class));
        typeCache.clear();
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.nullValue(Class.class));
    }

    @Test
    public void testCacheNullLoader() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Object key = new Object();
        MatcherAssert.assertThat(typeCache.find(null, key), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(typeCache.insert(null, key, Void.class), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(null, key), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(Mockito.mock(ClassLoader.class), key), CoreMatchers.nullValue(Class.class));
        typeCache.clear();
        MatcherAssert.assertThat(typeCache.find(null, key), CoreMatchers.nullValue(Class.class));
    }

    @Test
    public void testCacheCollection() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Object key = new Object();
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(typeCache.insert(classLoader, key, Void.class), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.is(((Object) (Void.class))));
        classLoader = null;// Make eligible for GC

        for (int index = 0; index < 2; index++) {
            System.gc();
            Thread.sleep(50L);
        }
        typeCache.expungeStaleEntries();
        MatcherAssert.assertThat(typeCache.cache.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testCacheTypeCollection() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Object key = new Object();
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.nullValue(Class.class));
        Class<?> type = new ByteBuddy().subclass(Object.class).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(typeCache.insert(classLoader, key, type), CoreMatchers.is(((Object) (type))));
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.is(((Object) (type))));
        type = null;// Make eligible for GC

        for (int index = 0; index < 2; index++) {
            System.gc();
            Thread.sleep(50L);
        }
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(typeCache.insert(classLoader, key, Void.class), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.find(classLoader, key), CoreMatchers.is(((Object) (Void.class))));
    }

    @Test
    public void testWeakReference() throws Exception {
        Reference<Class<?>> reference = WEAK.wrap(Void.class);
        MatcherAssert.assertThat(reference, CoreMatchers.instanceOf(WeakReference.class));
        MatcherAssert.assertThat(reference.get(), CoreMatchers.is(((Object) (Void.class))));
    }

    @Test
    public void testSoftReference() throws Exception {
        Reference<Class<?>> reference = SOFT.wrap(Void.class);
        MatcherAssert.assertThat(reference, CoreMatchers.instanceOf(SoftReference.class));
        MatcherAssert.assertThat(reference.get(), CoreMatchers.is(((Object) (Void.class))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindOrInsert() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Object key = new Object();
        MatcherAssert.assertThat(typeCache.find(ClassLoader.getSystemClassLoader(), key), CoreMatchers.nullValue(Class.class));
        Callable<Class<?>> callable = Mockito.mock(Callable.class);
        Mockito.when(callable.call()).thenReturn(((Class) (Void.class)));
        MatcherAssert.assertThat(typeCache.findOrInsert(ClassLoader.getSystemClassLoader(), key, callable, new Object()), CoreMatchers.is(((Object) (Void.class))));
        Mockito.verify(callable).call();
        MatcherAssert.assertThat(typeCache.findOrInsert(ClassLoader.getSystemClassLoader(), key, callable), CoreMatchers.is(((Object) (Void.class))));
        MatcherAssert.assertThat(typeCache.findOrInsert(ClassLoader.getSystemClassLoader(), key, callable, new Object()), CoreMatchers.is(((Object) (Void.class))));
        Mockito.verifyNoMoreInteractions(callable);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testCreationException() throws Exception {
        TypeCache<Object> typeCache = new TypeCache<Object>(WEAK);
        Callable<Class<?>> callable = Mockito.mock(Callable.class);
        Mockito.when(callable.call()).thenThrow(RuntimeException.class);
        typeCache.findOrInsert(ClassLoader.getSystemClassLoader(), new Object(), callable, new Object());
    }

    @Test
    public void testSimpleKeyProperties() {
        MatcherAssert.assertThat(new TypeCache.SimpleKey(Object.class).hashCode(), CoreMatchers.is(new TypeCache.SimpleKey(Object.class).hashCode()));
        MatcherAssert.assertThat(new TypeCache.SimpleKey(Object.class), CoreMatchers.is(new TypeCache.SimpleKey(Object.class)));
        MatcherAssert.assertThat(new TypeCache.SimpleKey(Object.class).hashCode(), CoreMatchers.not(new TypeCache.SimpleKey(Void.class).hashCode()));
        MatcherAssert.assertThat(new TypeCache.SimpleKey(Object.class), CoreMatchers.not(new TypeCache.SimpleKey(Void.class)));
    }
}

