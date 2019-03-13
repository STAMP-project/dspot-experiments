package net.bytebuddy.build;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;


public class PluginEngineDefaultTest {
    private static final String FOO = "foo";

    @Test
    public void testSimpleTransformation() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin plugin = new PluginEngineDefaultTest.SimplePlugin();
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).with(of(PluginEngineDefaultTest.SimplePlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(plugin));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredField(PluginEngineDefaultTest.FOO).getType(), Is.is(((Object) (Void.class))));
        MatcherAssert.assertThat(summary.getTransformed(), CoreMatchers.hasItems(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class)));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), plugin);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.singletonList(plugin));
        Mockito.verify(listener).onComplete(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSimpleTransformationIgnoredByPlugin() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin plugin = new PluginEngineDefaultTest.IgnoringPlugin();
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).with(of(PluginEngineDefaultTest.IgnoringPlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(plugin));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredFields().length, Is.is(0));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), plugin);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.singletonList(plugin));
        Mockito.verify(listener).onComplete(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSimpleTransformationIgnoredByMatcher() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin plugin = new PluginEngineDefaultTest.SimplePlugin();
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).with(of(PluginEngineDefaultTest.SimplePlugin.class.getClassLoader())).ignore(ElementMatchers.is(PluginEngineDefaultTest.Sample.class)).apply(source, target, new Plugin.Factory.Simple(plugin));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredFields().length, Is.is(0));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.singletonList(plugin));
        Mockito.verify(listener).onComplete(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test(expected = IllegalStateException.class)
    public void testSimpleTransformationError() throws Exception {
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        new Plugin.Engine.Default().with(of(PluginEngineDefaultTest.FailingPlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(new PluginEngineDefaultTest.FailingPlugin(new RuntimeException())));
    }

    @Test
    public void testSimpleTransformationErrorIgnored() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        RuntimeException exception = new RuntimeException();
        Plugin plugin = new PluginEngineDefaultTest.FailingPlugin(exception);
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).withoutErrorHandlers().with(of(PluginEngineDefaultTest.FailingPlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(plugin));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(1));
        MatcherAssert.assertThat(summary.getFailed().containsKey(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class)), Is.is(true));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        MatcherAssert.assertThat(target.getStorage().size(), Is.is(1));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredFields().length, Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onError(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), plugin, exception);
        Mockito.verify(listener).onError(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.<Throwable>singletonList(exception));
        Mockito.verify(listener).onComplete(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verify(listener).onError(Collections.singletonMap(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.<Throwable>singletonList(exception)));
        Mockito.verify(listener).onError(plugin, exception);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testLiveInitializer() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin plugin = new PluginEngineDefaultTest.LiveInitializerPlugin();
        Plugin.Engine.Source source = Engine.ofTypes(PluginEngineDefaultTest.Sample.class);
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).withoutErrorHandlers().with(of(PluginEngineDefaultTest.SimplePlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(plugin));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredField(PluginEngineDefaultTest.FOO).getType(), Is.is(((Object) (Void.class))));
        MatcherAssert.assertThat(summary.getTransformed(), CoreMatchers.hasItems(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class)));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), plugin);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), Collections.singletonList(plugin));
        Mockito.verify(listener).onLiveInitializer(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class), TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verify(listener).onComplete(TypeDescription.ForLoadedType.of(PluginEngineDefaultTest.Sample.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testUnresolved() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin plugin = new PluginEngineDefaultTest.SimplePlugin();
        Plugin.Engine.Source source = new Plugin.Engine.Source.InMemory(Collections.singletonMap(((PluginEngineDefaultTest.Sample.class.getName().replace('.', '/')) + ".class"), read(PluginEngineDefaultTest.Sample.class))) {
            @Override
            public ClassFileLocator getClassFileLocator() {
                return ClassFileLocator.NoOp.INSTANCE;
            }
        };
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).withoutErrorHandlers().apply(source, target, new Plugin.Factory.Simple(plugin));
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, target.toTypeMap());
        Class<?> type = classLoader.loadClass(PluginEngineDefaultTest.Sample.class.getName());
        MatcherAssert.assertThat(type.getDeclaredFields().length, Is.is(0));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(1));
        MatcherAssert.assertThat(summary.getUnresolved().contains(PluginEngineDefaultTest.Sample.class.getName()), Is.is(true));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onDiscovery(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verify(listener).onUnresolved(PluginEngineDefaultTest.Sample.class.getName());
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testResource() throws Exception {
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin.Engine.Source source = new Plugin.Engine.Source.InMemory(Collections.singletonMap(PluginEngineDefaultTest.FOO, new byte[]{ 1, 2, 3 }));
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).with(of(PluginEngineDefaultTest.SimplePlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(new PluginEngineDefaultTest.SimplePlugin()));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(Engine);
        Mockito.verify(listener).onResource(PluginEngineDefaultTest.FOO);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testManifest() throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manifest.write(outputStream);
        Plugin.Engine.Listener listener = Mockito.mock(.class);
        Plugin.Engine.Source source = new Plugin.Engine.Source.InMemory(Collections.singletonMap(JarFile.MANIFEST_NAME, outputStream.toByteArray()));
        Plugin.Engine.Target.InMemory target = new Plugin.Engine.Target.InMemory();
        Plugin.Engine.Summary summary = new Plugin.Engine.Default().with(listener).with(of(PluginEngineDefaultTest.SimplePlugin.class.getClassLoader())).apply(source, target, new Plugin.Factory.Simple(new PluginEngineDefaultTest.SimplePlugin()));
        MatcherAssert.assertThat(summary.getTransformed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getFailed().size(), Is.is(0));
        MatcherAssert.assertThat(summary.getUnresolved().size(), Is.is(0));
        Mockito.verify(listener).onManifest(manifest);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testImplicitFileInput() throws Exception {
        File file = File.createTempFile("foo", "bar");
        JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(file));
        try {
            outputStream.putNextEntry(new JarEntry("dummy"));
            outputStream.write(new byte[]{ 1, 2, 3 });
        } finally {
            outputStream.close();
        }
        Plugin.Engine engine = Mockito.spy(new Plugin.Engine.Default());
        Mockito.doAnswer(new org.mockito.stubbing.Answer<Plugin.Engine.Summary>() {
            public Plugin.Engine.Summary answer(InvocationOnMock invocationOnMock) {
                if (!((invocationOnMock.getArgument(0)) instanceof Plugin.Engine.Source.ForJarFile)) {
                    throw new AssertionError();
                } else
                    if (!((invocationOnMock.getArgument(1)) instanceof Plugin.Engine.Target.ForJarFile)) {
                        throw new AssertionError();
                    }

                return null;
            }
        }).when(engine).apply(ArgumentMatchers.any(.class), ArgumentMatchers.any(.class), ArgumentMatchers.<Plugin.Factory>anyList());
        MatcherAssert.assertThat(engine.apply(file, file), CoreMatchers.nullValue(.class));
        MatcherAssert.assertThat(file.delete(), Is.is(true));
    }

    @Test
    public void testImplicitFolderInput() throws Exception {
        File file = File.createTempFile("foo", "bar");
        MatcherAssert.assertThat(file.delete(), Is.is(true));
        MatcherAssert.assertThat(file.mkdir(), Is.is(true));
        Plugin.Engine engine = Mockito.spy(new Plugin.Engine.Default());
        Mockito.doAnswer(new org.mockito.stubbing.Answer<Plugin.Engine.Summary>() {
            public Plugin.Engine.Summary answer(InvocationOnMock invocationOnMock) {
                if (!((invocationOnMock.getArgument(0)) instanceof Plugin.Engine.Source.ForFolder)) {
                    throw new AssertionError();
                } else
                    if (!((invocationOnMock.getArgument(1)) instanceof Plugin.Engine.Target.ForFolder)) {
                        throw new AssertionError();
                    }

                return null;
            }
        }).when(engine).apply(ArgumentMatchers.any(.class), ArgumentMatchers.any(.class), ArgumentMatchers.<Plugin.Factory>anyList());
        MatcherAssert.assertThat(engine.apply(file, file), CoreMatchers.nullValue(.class));
        MatcherAssert.assertThat(file.delete(), Is.is(true));
    }

    @Test
    public void testOfEntryPoint() {
        EntryPoint entryPoint = Mockito.mock(EntryPoint.class);
        ClassFileVersion classFileVersion = Mockito.mock(ClassFileVersion.class);
        MethodNameTransformer methodNameTransformer = Mockito.mock(MethodNameTransformer.class);
        ByteBuddy byteBuddy = Mockito.mock(ByteBuddy.class);
        Mockito.when(byteBuddy(classFileVersion)).thenReturn(byteBuddy);
        MatcherAssert.assertThat(Engine.of(entryPoint, classFileVersion, methodNameTransformer), FieldByFieldComparison.hasPrototype(new Plugin.Engine.Default().with(byteBuddy).with(new Plugin.Engine.TypeStrategy.ForEntryPoint(entryPoint, methodNameTransformer))));
        byteBuddy(classFileVersion);
        Mockito.verifyNoMoreInteractions(entryPoint);
    }

    @Test
    public void testMain() throws Exception {
        File source = File.createTempFile("foo", "bar");
        File target = File.createTempFile("qux", "baz");
        MatcherAssert.assertThat(target.delete(), Is.is(true));
        JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(source));
        try {
            outputStream.putNextEntry(new JarEntry("dummy"));
            outputStream.write(new byte[]{ 1, 2, 3 });
        } finally {
            outputStream.close();
        }
        Engine.main(source.getAbsolutePath(), target.getAbsolutePath(), .class.getName());
        MatcherAssert.assertThat(target.isFile(), Is.is(true));
        MatcherAssert.assertThat(target.delete(), Is.is(true));
    }

    @Test
    public void testConfiguration() {
        Plugin.Engine.ErrorHandler errorHandler = Mockito.mock(.class);
        Plugin.Engine.TypeStrategy typeStrategy = Mockito.mock(.class);
        Plugin.Engine.PoolStrategy poolStrategy = Mockito.mock(.class);
        MatcherAssert.assertThat(new Plugin.Engine.Default().with(poolStrategy).with(typeStrategy).withErrorHandlers(errorHandler), FieldByFieldComparison.hasPrototype(new Plugin.Engine.Default().with(poolStrategy).with(typeStrategy).withErrorHandlers(errorHandler)));
    }

    /* empty */
    private static class Sample {}

    private static class SimplePlugin implements Plugin {
        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            return builder.defineField(PluginEngineDefaultTest.FOO, Void.class);
        }

        public boolean matches(TypeDescription target) {
            return target.represents(PluginEngineDefaultTest.Sample.class);
        }

        public void close() {
            /* empty */
        }
    }

    private static class LiveInitializerPlugin implements Plugin {
        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            return builder.defineField(PluginEngineDefaultTest.FOO, Void.class).initializer(new LoadedTypeInitializer() {
                public void onLoad(Class<?> type) {
                    /* do nothing */
                }

                public boolean isAlive() {
                    return true;
                }
            });
        }

        public boolean matches(TypeDescription target) {
            return target.represents(PluginEngineDefaultTest.Sample.class);
        }

        public void close() {
            /* empty */
        }
    }

    private static class IgnoringPlugin implements Plugin {
        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new AssertionError();
        }

        public boolean matches(TypeDescription target) {
            return false;
        }

        public void close() {
            /* empty */
        }
    }

    private static class FailingPlugin implements Plugin {
        private final RuntimeException exception;

        public FailingPlugin(RuntimeException exception) {
            this.exception = exception;
        }

        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw exception;
        }

        public boolean matches(TypeDescription target) {
            return target.represents(PluginEngineDefaultTest.Sample.class);
        }

        public void close() {
            throw exception;
        }
    }
}

