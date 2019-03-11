package net.bytebuddy.dynamic;


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.NexusAccessor.Dispatcher.Unavailable.<init>;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.LATENT;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.MANIFEST;
import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.NoOp.INSTANCE;


public class NexusTest {
    private static final String FOO = "foo";

    private static final int BAR = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private LoadedTypeInitializer loadedTypeInitializer;

    @Mock
    private ClassLoader classLoader;

    @Test
    public void testNexusIsPublic() throws Exception {
        MatcherAssert.assertThat(Modifier.isPublic(Nexus.class.getModifiers()), CoreMatchers.is(true));
    }

    @Test
    public void testNexusHasNoDeclaringType() throws Exception {
        MatcherAssert.assertThat(Nexus.class.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
    }

    @Test
    public void testNexusHasNoDeclaredTypes() throws Exception {
        MatcherAssert.assertThat(Nexus.class.getDeclaredClasses().length, CoreMatchers.is(0));
    }

    @Test
    public void testNexusAccessorNonActive() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader.ChildFirst(getClass().getClassLoader(), readToNames(Nexus.class, NexusAccessor.class, NexusAccessor.Dispatcher.class, NexusAccessor.Dispatcher.CreationAction.class, NexusAccessor.Dispatcher.Available.class, NexusAccessor.Dispatcher.Unavailable.class), null, MANIFEST, INSTANCE);
        Field duplicateInitializers = classLoader.loadClass(Nexus.class.getName()).getDeclaredField("TYPE_INITIALIZERS");
        duplicateInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
        Field actualInitializers = Nexus.class.getDeclaredField("TYPE_INITIALIZERS");
        actualInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(0));
        Class<?> accessor = classLoader.loadClass(NexusAccessor.class.getName());
        ClassLoader qux = Mockito.mock(ClassLoader.class);
        Mockito.when(loadedTypeInitializer.isAlive()).thenReturn(false);
        MatcherAssert.assertThat(accessor.getDeclaredMethod("register", String.class, ClassLoader.class, int.class, LoadedTypeInitializer.class).invoke(accessor.getDeclaredConstructor().newInstance(), NexusTest.FOO, qux, NexusTest.BAR, loadedTypeInitializer), CoreMatchers.nullValue(Object.class));
        try {
            MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
            MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(0));
        } finally {
            Constructor<Nexus> constructor = Nexus.class.getDeclaredConstructor(String.class, ClassLoader.class, ReferenceQueue.class, int.class);
            constructor.setAccessible(true);
            Object value = ((Map<?, ?>) (actualInitializers.get(null))).remove(constructor.newInstance(NexusTest.FOO, qux, null, NexusTest.BAR));
            MatcherAssert.assertThat(value, CoreMatchers.nullValue());
        }
    }

    @Test
    public void testNexusAccessorClassLoaderBoundary() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader.ChildFirst(getClass().getClassLoader(), readToNames(Nexus.class, NexusAccessor.class, NexusAccessor.Dispatcher.class, NexusAccessor.Dispatcher.CreationAction.class, NexusAccessor.Dispatcher.Available.class, NexusAccessor.Dispatcher.Unavailable.class), null, MANIFEST, INSTANCE);
        Field duplicateInitializers = classLoader.loadClass(Nexus.class.getName()).getDeclaredField("TYPE_INITIALIZERS");
        duplicateInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
        Field actualInitializers = Nexus.class.getDeclaredField("TYPE_INITIALIZERS");
        actualInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(0));
        Class<?> accessor = classLoader.loadClass(NexusAccessor.class.getName());
        ClassLoader qux = Mockito.mock(ClassLoader.class);
        Mockito.when(loadedTypeInitializer.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(accessor.getDeclaredMethod("register", String.class, ClassLoader.class, int.class, LoadedTypeInitializer.class).invoke(accessor.getDeclaredConstructor().newInstance(), NexusTest.FOO, qux, NexusTest.BAR, loadedTypeInitializer), CoreMatchers.nullValue(Object.class));
        try {
            MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
            MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(1));
        } finally {
            Constructor<Nexus> constructor = Nexus.class.getDeclaredConstructor(String.class, ClassLoader.class, ReferenceQueue.class, int.class);
            constructor.setAccessible(true);
            Object value = ((Map<?, ?>) (actualInitializers.get(null))).remove(constructor.newInstance(NexusTest.FOO, qux, null, NexusTest.BAR));
            MatcherAssert.assertThat(value, CoreMatchers.is(((Object) (loadedTypeInitializer))));
        }
    }

    @Test
    public void testNexusAccessorClassLoaderNoResource() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader.ChildFirst(getClass().getClassLoader(), readToNames(Nexus.class, NexusAccessor.class, NexusAccessor.Dispatcher.class, NexusAccessor.Dispatcher.CreationAction.class, NexusAccessor.Dispatcher.Available.class, NexusAccessor.Dispatcher.Unavailable.class), null, LATENT, INSTANCE);
        Field duplicateInitializers = classLoader.loadClass(Nexus.class.getName()).getDeclaredField("TYPE_INITIALIZERS");
        duplicateInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
        Field actualInitializers = Nexus.class.getDeclaredField("TYPE_INITIALIZERS");
        actualInitializers.setAccessible(true);
        MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(0));
        Class<?> accessor = classLoader.loadClass(NexusAccessor.class.getName());
        ClassLoader qux = Mockito.mock(ClassLoader.class);
        Mockito.when(loadedTypeInitializer.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(accessor.getDeclaredMethod("register", String.class, ClassLoader.class, int.class, LoadedTypeInitializer.class).invoke(accessor.getDeclaredConstructor().newInstance(), NexusTest.FOO, qux, NexusTest.BAR, loadedTypeInitializer), CoreMatchers.nullValue(Object.class));
        try {
            MatcherAssert.assertThat(((Map<?, ?>) (duplicateInitializers.get(null))).size(), CoreMatchers.is(0));
            MatcherAssert.assertThat(((Map<?, ?>) (actualInitializers.get(null))).size(), CoreMatchers.is(1));
        } finally {
            Constructor<Nexus> constructor = Nexus.class.getDeclaredConstructor(String.class, ClassLoader.class, ReferenceQueue.class, int.class);
            constructor.setAccessible(true);
            Object value = ((Map<?, ?>) (actualInitializers.get(null))).remove(constructor.newInstance(NexusTest.FOO, qux, null, NexusTest.BAR));
            MatcherAssert.assertThat(value, CoreMatchers.is(((Object) (loadedTypeInitializer))));
        }
    }

    @Test
    public void testNexusClean() throws Exception {
        Field typeInitializers = ClassLoader.getSystemClassLoader().loadClass(Nexus.class.getName()).getDeclaredField("TYPE_INITIALIZERS");
        typeInitializers.setAccessible(true);
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        Mockito.when(loadedTypeInitializer.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(((Map<?, ?>) (typeInitializers.get(null))).isEmpty(), CoreMatchers.is(true));
        ReferenceQueue<ClassLoader> referenceQueue = new ReferenceQueue<ClassLoader>();
        NexusAccessor nexusAccessor = new NexusAccessor(referenceQueue);
        nexusAccessor.register(NexusTest.FOO, classLoader, NexusTest.BAR, loadedTypeInitializer);
        MatcherAssert.assertThat(((Map<?, ?>) (typeInitializers.get(null))).isEmpty(), CoreMatchers.is(false));
        classLoader = null;
        System.gc();
        NexusAccessor.clean(referenceQueue.remove(100L));
        MatcherAssert.assertThat(((Map<?, ?>) (typeInitializers.get(null))).isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testNexusAccessorIsAvailable() throws Exception {
        MatcherAssert.assertThat(NexusAccessor.isAlive(), CoreMatchers.is(true));
    }

    @Test
    public void testUnavailableState() throws Exception {
        MatcherAssert.assertThat(new NexusAccessor.Dispatcher.Unavailable("unavailable").isAlive(), CoreMatchers.is(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnavailableDispatcherRegisterThrowsException() throws Exception {
        new NexusAccessor.Dispatcher.Unavailable("unavailable").register(NexusTest.FOO, classLoader, null, NexusTest.BAR, loadedTypeInitializer);
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("unchecked")
    public void testUnavailableDispatcherCleanThrowsException() throws Exception {
        new NexusAccessor.Dispatcher.Unavailable("unavailable").clean(Mockito.mock(Reference.class));
    }

    @Test
    public void testNexusEquality() throws Exception {
        Constructor<Nexus> constructor = Nexus.class.getDeclaredConstructor(String.class, ClassLoader.class, ReferenceQueue.class, int.class);
        constructor.setAccessible(true);
        MatcherAssert.assertThat(constructor.newInstance(NexusTest.FOO, classLoader, null, NexusTest.BAR), CoreMatchers.is(constructor.newInstance(NexusTest.FOO, classLoader, null, NexusTest.BAR)));
        MatcherAssert.assertThat(constructor.newInstance(NexusTest.FOO, classLoader, null, NexusTest.BAR).hashCode(), CoreMatchers.is(constructor.newInstance(NexusTest.FOO, classLoader, null, NexusTest.BAR).hashCode()));
    }
}

