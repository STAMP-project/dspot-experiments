package net.bytebuddy.agent.builder;


import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static net.bytebuddy.ClassFileVersion.ofThisVm;
import static net.bytebuddy.agent.builder.AgentBuilder.Default.BootstrapInjectionStrategy.Disabled.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.Default.of;
import static net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy.Default.HYBRID;
import static net.bytebuddy.agent.builder.AgentBuilder.FallbackStrategy.Simple.ENABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DISABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.ErrorEscalating.FAIL_FAST;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.REDEFINITION;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE_FROZEN;
import static net.bytebuddy.build.EntryPoint.Default.REBASE;


public class AgentBuilderDefaultTest {
    private static final String FOO = "foo";

    private static final byte[] QUX = new byte[]{ 1, 2, 3 };

    private static final byte[] BAZ = new byte[]{ 4, 5, 6 };

    private static final Class<?> REDEFINED = AgentBuilderDefaultTest.Foo.class;

    private static final Class<?> AUXILIARY = AgentBuilderDefaultTest.Bar.class;

    private static final Class<?> OTHER = AgentBuilderDefaultTest.Qux.class;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private Instrumentation instrumentation;

    @Mock
    private ByteBuddy byteBuddy;

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private DynamicType.Unloaded<?> dynamicType;

    @Mock
    private LoadedTypeInitializer loadedTypeInitializer;

    @Mock
    private AgentBuilder.RawMatcher typeMatcher;

    @Mock
    private AgentBuilder.Transformer transformer;

    @Mock
    private AgentBuilder.PoolStrategy poolStrategy;

    @Mock
    private AgentBuilder.TypeStrategy typeStrategy;

    @Mock
    private AgentBuilder.LocationStrategy locationStrategy;

    @Mock
    private AgentBuilder.InitializationStrategy initializationStrategy;

    @Mock
    private AgentBuilder.InitializationStrategy.Dispatcher dispatcher;

    @Mock
    private TypePool typePool;

    @Mock
    private TypePool.Resolution resolution;

    @Mock
    private AgentBuilder.Listener listener;

    @Mock
    private AgentBuilder.InstallationListener installationListener;

    @Test
    public void testSuccessfulWithoutExistingClass() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(transformer).transform(builder, TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(transformer);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithoutExistingClassConjunction() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(ElementMatchers.any()).and(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithoutExistingClassDisjunction() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(ElementMatchers.none()).or(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithExistingClass() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithExistingClassFallback() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(new RuntimeException());
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).with(ENABLED).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testResetDisabled() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        classFileTransformer.reset(instrumentation, DISABLED);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testResetObsolete() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(false);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, DISABLED), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
    }

    @Test(expected = IllegalStateException.class)
    public void testResetRedefinitionUnsupported() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        classFileTransformer.reset(instrumentation, REDEFINITION);
    }

    @Test(expected = IllegalStateException.class)
    public void testResetRetransformationUnsupported() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        classFileTransformer.reset(instrumentation, RETRANSFORMATION);
    }

    @Test
    public void testResetRedefinitionWithError() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Throwable throwable = new RuntimeException();
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(throwable);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, REDEFINITION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testResetRetransformationWithError() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Throwable throwable = new RuntimeException();
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(throwable);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, RETRANSFORMATION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testResetRedefinitionWithErrorFromFallback() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Throwable throwable = new RuntimeException();
        Throwable suppressed = new RuntimeException();
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(suppressed);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(throwable);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, REDEFINITION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testResetRetransformationWithErrorFromFallback() throws Exception {
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Throwable throwable = new RuntimeException();
        Throwable suppressed = new RuntimeException();
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(suppressed);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(throwable);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, RETRANSFORMATION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testSuccessfulWithRetransformationMatched() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRetransformationMatchedFallback() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(new RuntimeException());
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRetransformationMatchedAndReset() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, RETRANSFORMATION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verify(instrumentation, Mockito.times(2)).getAllLoadedClasses();
        Mockito.verify(instrumentation, Mockito.times(2)).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onReset(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRetransformationMatchedFallbackAndReset() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(new RuntimeException());
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, RETRANSFORMATION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verify(instrumentation, Mockito.times(2)).getAllLoadedClasses();
        Mockito.verify(instrumentation, Mockito.times(2)).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onReset(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRetransformationWithNonRedefinable() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(false);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRetransformationWithNonMatched() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRetransformationWithNonMatchedListenerException() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.doThrow(new RuntimeException()).when(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRetransformationWithNonMatchedListenerCompleteException() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.doThrow(new RuntimeException()).when(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessfulWithRetransformationMatchedChunked() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(2, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationChunkedOneFails() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Throwable throwable = new UnmodifiableClassException();
        Mockito.doThrow(throwable).when(instrumentation).retransformClasses(AgentBuilderDefaultTest.OTHER);
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        Mockito.when(redefinitionListener.onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.emptyList())));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(2, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.singletonMap(Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable));
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationChunkedOneFailsResubmit() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Throwable throwable = new UnmodifiableClassException();
        Mockito.doThrow(throwable).when(instrumentation).retransformClasses(AgentBuilderDefaultTest.OTHER);
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        Mockito.when(redefinitionListener.onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.singleton(Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        Mockito.when(redefinitionListener.onError(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.emptyList())));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).retransformClasses(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(3, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.singletonMap(Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable));
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationChunkedOneFailsEscalated() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Throwable throwable = new RuntimeException();
        Mockito.doThrow(throwable).when(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(AgentBuilder.RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE).with(FAIL_FAST).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onError(ArgumentMatchers.eq(instrumentation), ArgumentMatchers.eq(classFileTransformer), ArgumentMatchers.argThat(new AgentBuilderDefaultTest.CauseMatcher(throwable, 2)));
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test(expected = IllegalStateException.class)
    public void testRetransformationNotSupported() throws Exception {
        new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(listener).disableNativeMethodPrefix().type(typeMatcher).transform(transformer).installOn(instrumentation);
    }

    @Test
    public void testSuccessfulWithRedefinitionMatched() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRedefinitionMatchedFallback() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(new RuntimeException());
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRedefinitionMatchedAndReset() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, REDEFINITION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verify(instrumentation, Mockito.times(2)).getAllLoadedClasses();
        Mockito.verify(instrumentation, Mockito.times(2)).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).redefineClasses(ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation, Mockito.times(2)).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onReset(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSuccessfulWithRedefinitionMatchedFallbackAndReset() throws Exception {
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(new RuntimeException());
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(ENABLED).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.when(instrumentation.removeTransformer(classFileTransformer)).thenReturn(true);
        MatcherAssert.assertThat(classFileTransformer.reset(instrumentation, REDEFINITION), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).removeTransformer(classFileTransformer);
        Mockito.verify(instrumentation, Mockito.times(2)).getAllLoadedClasses();
        Mockito.verify(instrumentation, Mockito.times(2)).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation, Mockito.times(2)).redefineClasses(ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation, Mockito.times(2)).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onReset(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithNonRedefinable() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(false);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithNonMatched() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithIgnoredType() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        @SuppressWarnings("unchecked")
        ElementMatcher<? super TypeDescription> ignoredTypes = Mockito.mock(ElementMatcher.class);
        Mockito.when(ignoredTypes.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED))).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ignoredTypes).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(ignoredTypes).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(ignoredTypes);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithIgnoredClassLoader() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        @SuppressWarnings("unchecked")
        ElementMatcher<? super TypeDescription> ignoredTypes = Mockito.mock(ElementMatcher.class);
        Mockito.when(ignoredTypes.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED))).thenReturn(true);
        @SuppressWarnings("unchecked")
        ElementMatcher<? super ClassLoader> ignoredClassLoaders = Mockito.mock(ElementMatcher.class);
        Mockito.when(ignoredClassLoaders.matches(AgentBuilderDefaultTest.REDEFINED.getClassLoader())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ignoredTypes, ignoredClassLoaders).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(ignoredClassLoaders).matches(AgentBuilderDefaultTest.REDEFINED.getClassLoader());
        Mockito.verifyNoMoreInteractions(ignoredClassLoaders);
        Mockito.verify(ignoredTypes).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(ignoredTypes);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithIgnoredTypeChainedConjunction() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        @SuppressWarnings("unchecked")
        ElementMatcher<? super TypeDescription> ignoredTypes = Mockito.mock(ElementMatcher.class);
        Mockito.when(ignoredTypes.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED))).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.any()).and(ignoredTypes).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(ignoredTypes).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(ignoredTypes);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithIgnoredTypeChainedDisjunction() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        @SuppressWarnings("unchecked")
        ElementMatcher<? super TypeDescription> ignoredTypes = Mockito.mock(ElementMatcher.class);
        Mockito.when(ignoredTypes.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED))).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).or(ignoredTypes).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(ignoredTypes).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(ignoredTypes);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithNonMatchedListenerException() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.doThrow(new RuntimeException()).when(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testSkipRedefinitionWithNonMatchedListenerFinishedException() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.doThrow(new RuntimeException()).when(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessfulWithRedefinitionMatchedChunked() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation, Mockito.times(2)).redefineClasses(ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(2, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionChunkedOneFails() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Throwable throwable = new ClassNotFoundException();
        Mockito.doThrow(throwable).when(instrumentation).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.OTHER)));
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        Mockito.when(redefinitionListener.onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.emptyList())));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.REDEFINED)));
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.OTHER)));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(2, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.singletonMap(Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable));
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionChunkedOneFailsResubmit() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Throwable throwable = new ClassNotFoundException();
        Mockito.doThrow(throwable).when(instrumentation).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.OTHER)));
        AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(redefinitionBatchAllocator.batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Arrays.asList(Collections.singletonList(AgentBuilderDefaultTest.REDEFINED), Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        AgentBuilder.RedefinitionStrategy.Listener redefinitionListener = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        Mockito.when(redefinitionListener.onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.singleton(Collections.singletonList(AgentBuilderDefaultTest.OTHER)))));
        Mockito.when(redefinitionListener.onError(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER))).thenReturn(((Iterable) (Collections.emptyList())));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(redefinitionBatchAllocator).with(redefinitionListener).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.REDEFINED)));
        Mockito.verify(instrumentation, Mockito.times(2)).redefineClasses(ArgumentMatchers.argThat(new AgentBuilderDefaultTest.ClassRedefinitionMatcher(AgentBuilderDefaultTest.OTHER)));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
        Mockito.verifyZeroInteractions(installationListener);
        Mockito.verify(redefinitionBatchAllocator).batch(Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(redefinitionListener).onBatch(0, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.REDEFINED), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onBatch(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(1, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onError(2, Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER));
        Mockito.verify(redefinitionListener).onComplete(3, Arrays.asList(AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER), Collections.singletonMap(Collections.<Class<?>>singletonList(AgentBuilderDefaultTest.OTHER), throwable));
        Mockito.verifyNoMoreInteractions(redefinitionListener);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionChunkedOneFailsEscalated() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.OTHER });
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain())).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.OTHER)).thenReturn(true);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Throwable throwable = new RuntimeException();
        Mockito.doThrow(throwable).when(instrumentation).redefineClasses(ArgumentMatchers.any(ClassDefinition.class), ArgumentMatchers.any(ClassDefinition.class));
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(AgentBuilder.RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE).with(FAIL_FAST).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verifyZeroInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(instrumentation).getAllLoadedClasses();
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.REDEFINED);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderDefaultTest.OTHER);
        Mockito.verify(instrumentation).redefineClasses(ArgumentMatchers.any(ClassDefinition.class), ArgumentMatchers.any(ClassDefinition.class));
        Mockito.verify(instrumentation).isRedefineClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verify(typeMatcher).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.OTHER), AgentBuilderDefaultTest.OTHER, AgentBuilderDefaultTest.OTHER.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onError(ArgumentMatchers.eq(instrumentation), ArgumentMatchers.eq(classFileTransformer), ArgumentMatchers.argThat(new AgentBuilderDefaultTest.CauseMatcher(throwable, 1)));
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test(expected = IllegalStateException.class)
    public void testRedefinitionNotSupported() throws Exception {
        new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(listener).disableNativeMethodPrefix().type(typeMatcher).transform(transformer).installOn(instrumentation);
    }

    @Test
    public void testTransformationWithError() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        RuntimeException exception = new RuntimeException();
        Mockito.when(resolution.resolve()).thenThrow(exception);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), null, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.nullValue(byte[].class));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verify(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false, exception);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), false);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testIgnored() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(false);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.nullValue(byte[].class));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(initializationStrategy);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPrefixThrowsException() throws Exception {
        new AgentBuilder.Default(byteBuddy).enableNativeMethodPrefix("");
    }

    @Test
    public void testAuxiliaryTypeInitialization() throws Exception {
        Mockito.when(dynamicType.getAuxiliaryTypes()).thenReturn(Collections.<TypeDescription, byte[]>singletonMap(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.AUXILIARY), AgentBuilderDefaultTest.QUX));
        Map<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers = new HashMap<TypeDescription, LoadedTypeInitializer>();
        loadedTypeInitializers.put(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), loadedTypeInitializer);
        LoadedTypeInitializer auxiliaryInitializer = Mockito.mock(LoadedTypeInitializer.class);
        loadedTypeInitializers.put(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.AUXILIARY), auxiliaryInitializer);
        Mockito.when(dynamicType.getLoadedTypeInitializers()).thenReturn(loadedTypeInitializers);
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testRedefinitionConsiderationException() throws Exception {
        RuntimeException exception = new RuntimeException();
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(exception);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testRetransformationConsiderationException() throws Exception {
        RuntimeException exception = new RuntimeException();
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(exception);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testRedefinitionConsiderationExceptionListenerException() throws Exception {
        RuntimeException exception = new RuntimeException();
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(exception);
        Mockito.doThrow(new RuntimeException()).when(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(REDEFINITION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verify(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testRetransformationConsiderationExceptionListenerException() throws Exception {
        RuntimeException exception = new RuntimeException();
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ AgentBuilderDefaultTest.REDEFINED });
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderDefaultTest.REDEFINED)).thenReturn(true);
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenThrow(exception);
        Mockito.doThrow(new RuntimeException()).when(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(RETRANSFORMATION).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).installOn(instrumentation);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, true);
        Mockito.verify(listener).onError(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, exception);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testDecoratedTransformation() throws Exception {
        Mockito.when(dynamicType.getBytes()).thenReturn(AgentBuilderDefaultTest.BAZ);
        Mockito.when(resolution.resolve()).thenReturn(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED));
        Mockito.when(typeMatcher.matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())).thenReturn(true);
        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ElementMatchers.none()).type(typeMatcher).transform(transformer).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.is(AgentBuilderDefaultTest.BAZ));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onTransformation(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true, dynamicType);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(instrumentation).addTransformer(classFileTransformer, false);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(initializationStrategy).dispatcher();
        Mockito.verifyNoMoreInteractions(initializationStrategy);
        Mockito.verify(dispatcher).apply(builder);
        Mockito.verify(dispatcher).register(ArgumentMatchers.eq(dynamicType), ArgumentMatchers.eq(AgentBuilderDefaultTest.REDEFINED.getClassLoader()), FieldByFieldComparison.matchesPrototype(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(INSTANCE, AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getProtectionDomain())));
        Mockito.verifyNoMoreInteractions(dispatcher);
        Mockito.verify(typeMatcher, Mockito.times(2)).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(transformer, Mockito.times(2)).transform(builder, TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED));
        Mockito.verifyNoMoreInteractions(transformer);
        Mockito.verify(installationListener).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(installationListener).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(installationListener);
    }

    @Test
    public void testBootstrapClassLoaderCapableInjectorFactoryReflection() throws Exception {
        AgentBuilder.Default.BootstrapInjectionStrategy bootstrapInjectionStrategy = Mockito.mock(AgentBuilder.Default.BootstrapInjectionStrategy.class);
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        ProtectionDomain protectionDomain = Mockito.mock(ProtectionDomain.class);
        MatcherAssert.assertThat(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(bootstrapInjectionStrategy, classLoader, protectionDomain).resolve(), FieldByFieldComparison.hasPrototype(((ClassInjector) (new ClassInjector.UsingReflection(classLoader, protectionDomain)))));
        Mockito.verifyZeroInteractions(bootstrapInjectionStrategy);
    }

    @Test
    public void testBootstrapClassLoaderCapableInjectorFactoryInstrumentation() throws Exception {
        AgentBuilder.Default.BootstrapInjectionStrategy bootstrapInjectionStrategy = Mockito.mock(AgentBuilder.Default.BootstrapInjectionStrategy.class);
        ProtectionDomain protectionDomain = Mockito.mock(ProtectionDomain.class);
        ClassInjector classInjector = Mockito.mock(ClassInjector.class);
        Mockito.when(bootstrapInjectionStrategy.make(protectionDomain)).thenReturn(classInjector);
        MatcherAssert.assertThat(new AgentBuilder.Default.Transformation.Simple.Resolution.BootstrapClassLoaderCapableInjectorFactory(bootstrapInjectionStrategy, null, protectionDomain).resolve(), CoreMatchers.is(classInjector));
        Mockito.verify(bootstrapInjectionStrategy).make(protectionDomain);
        Mockito.verifyNoMoreInteractions(bootstrapInjectionStrategy);
    }

    @Test
    public void testEnabledBootstrapInjection() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.Default.BootstrapInjectionStrategy.Enabled(Mockito.mock(File.class), Mockito.mock(Instrumentation.class)).make(Mockito.mock(ProtectionDomain.class)), CoreMatchers.instanceOf(ClassInjector.UsingInstrumentation.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testDisabledBootstrapInjection() throws Exception {
        INSTANCE.make(Mockito.mock(ProtectionDomain.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecutingTransformerReturnsNullValue() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.Default.ExecutingTransformer(byteBuddy, listener, poolStrategy, typeStrategy, locationStrategy, Mockito.mock(AgentBuilder.Default.NativeMethodStrategy.class), initializationStrategy, Mockito.mock(AgentBuilder.Default.BootstrapInjectionStrategy.class), AgentBuilder.LambdaInstrumentationStrategy.DISABLED, HYBRID, Mockito.mock(AgentBuilder.FallbackStrategy.class), Mockito.mock(.class), Mockito.mock(AgentBuilder.InstallationListener.class), Mockito.mock(AgentBuilder.RawMatcher.class), Mockito.mock(AgentBuilder.Default.Transformation.class), new AgentBuilder.CircularityLock.Default()).transform(Mockito.mock(ClassLoader.class), AgentBuilderDefaultTest.FOO, Object.class, Mockito.mock(ProtectionDomain.class), new byte[0]), CoreMatchers.nullValue(byte[].class));
    }

    @Test(expected = IllegalStateException.class)
    public void testExecutingTransformerReturnsRequiresLock() throws Exception {
        new AgentBuilder.Default().with(Mockito.mock(AgentBuilder.CircularityLock.class)).installOn(Mockito.mock(Instrumentation.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecutingTransformerDoesNotRecurse() throws Exception {
        final AgentBuilder.Default.ExecutingTransformer executingTransformer = new AgentBuilder.Default.ExecutingTransformer(byteBuddy, listener, poolStrategy, typeStrategy, locationStrategy, Mockito.mock(AgentBuilder.Default.NativeMethodStrategy.class), initializationStrategy, Mockito.mock(AgentBuilder.Default.BootstrapInjectionStrategy.class), AgentBuilder.LambdaInstrumentationStrategy.DISABLED, HYBRID, Mockito.mock(AgentBuilder.FallbackStrategy.class), Mockito.mock(.class), Mockito.mock(AgentBuilder.InstallationListener.class), Mockito.mock(AgentBuilder.RawMatcher.class), Mockito.mock(AgentBuilder.Default.Transformation.class), new AgentBuilder.CircularityLock.Default());
        final ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        final ProtectionDomain protectionDomain = Mockito.mock(ProtectionDomain.class);
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MatcherAssert.assertThat(executingTransformer.transform(classLoader, AgentBuilderDefaultTest.FOO, Object.class, protectionDomain, new byte[0]), CoreMatchers.nullValue(byte[].class));
                return null;
            }
        }).when(listener).onComplete(AgentBuilderDefaultTest.FOO, classLoader, JavaModule.UNSUPPORTED, true);
        MatcherAssert.assertThat(executingTransformer.transform(classLoader, AgentBuilderDefaultTest.FOO, Object.class, protectionDomain, new byte[0]), CoreMatchers.nullValue(byte[].class));
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.FOO, classLoader, JavaModule.UNSUPPORTED, true);
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testExecutingTransformerDoesNotRecurseWithModules() throws Exception {
        final AgentBuilder.Default.ExecutingTransformer executingTransformer = new AgentBuilder.Default.ExecutingTransformer(byteBuddy, listener, poolStrategy, typeStrategy, locationStrategy, Mockito.mock(AgentBuilder.Default.NativeMethodStrategy.class), initializationStrategy, Mockito.mock(AgentBuilder.Default.BootstrapInjectionStrategy.class), AgentBuilder.LambdaInstrumentationStrategy.DISABLED, HYBRID, Mockito.mock(AgentBuilder.FallbackStrategy.class), Mockito.mock(.class), Mockito.mock(AgentBuilder.InstallationListener.class), Mockito.mock(AgentBuilder.RawMatcher.class), Mockito.mock(AgentBuilder.Default.Transformation.class), new AgentBuilder.CircularityLock.Default());
        final ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        final ProtectionDomain protectionDomain = Mockito.mock(ProtectionDomain.class);
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MatcherAssert.assertThat(executingTransformer.transform(JavaModule.ofType(Object.class).unwrap(), classLoader, AgentBuilderDefaultTest.FOO, Object.class, protectionDomain, new byte[0]), CoreMatchers.nullValue(byte[].class));
                return null;
            }
        }).when(listener).onComplete(AgentBuilderDefaultTest.FOO, classLoader, JavaModule.ofType(Object.class), true);
        MatcherAssert.assertThat(executingTransformer.transform(JavaModule.ofType(Object.class).unwrap(), classLoader, AgentBuilderDefaultTest.FOO, Object.class, protectionDomain, new byte[0]), CoreMatchers.nullValue(byte[].class));
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.FOO, classLoader, JavaModule.ofType(Object.class), true);
    }

    @Test
    public void testIgnoredTypeMatcherOnlyAppliedOnceWithMultipleTransformations() throws Exception {
        AgentBuilder.RawMatcher ignored = Mockito.mock(AgentBuilder.RawMatcher.class);
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default(byteBuddy).with(initializationStrategy).with(poolStrategy).with(typeStrategy).with(installationListener).with(listener).disableNativeMethodPrefix().ignore(ignored).type(typeMatcher).transform(transformer).type(typeMatcher).transform(transformer).installOn(instrumentation);
        MatcherAssert.assertThat(AgentBuilderDefaultTest.transform(classFileTransformer, JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain(), AgentBuilderDefaultTest.QUX), CoreMatchers.nullValue(byte[].class));
        Mockito.verify(listener).onDiscovery(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onIgnored(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verify(listener).onComplete(AgentBuilderDefaultTest.REDEFINED.getName(), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(ignored).matches(TypeDescription.ForLoadedType.of(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED.getClassLoader(), JavaModule.ofType(AgentBuilderDefaultTest.REDEFINED), AgentBuilderDefaultTest.REDEFINED, AgentBuilderDefaultTest.REDEFINED.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(ignored);
    }

    @Test
    public void testDisableClassFormatChanges() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.Default().disableClassFormatChanges(), FieldByFieldComparison.hasPrototype(new AgentBuilder.Default(new ByteBuddy().with(Implementation.Context.Disabled.Factory.INSTANCE)).with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE).with(REDEFINE_FROZEN)));
    }

    @Test
    public void testBuildPlugin() throws Exception {
        Plugin plugin = Mockito.mock(Plugin.class);
        MatcherAssert.assertThat(AgentBuilder.Default.of(plugin), FieldByFieldComparison.hasPrototype(((AgentBuilder) (new AgentBuilder.Default().with(new AgentBuilder.TypeStrategy.ForBuildEntryPoint(REBASE)).type(plugin).transform(new AgentBuilder.Transformer.ForBuildPlugin(plugin))))));
    }

    @Test
    public void testBuildPluginWithEntryPoint() throws Exception {
        Plugin plugin = Mockito.mock(Plugin.class);
        EntryPoint entryPoint = Mockito.mock(EntryPoint.class);
        ByteBuddy byteBuddy = Mockito.mock(ByteBuddy.class);
        Mockito.when(byteBuddy(ofThisVm())).thenReturn(byteBuddy);
        MatcherAssert.assertThat(of(entryPoint, plugin), FieldByFieldComparison.hasPrototype(((AgentBuilder) (new AgentBuilder.Default(byteBuddy).with(new AgentBuilder.TypeStrategy.ForBuildEntryPoint(entryPoint)).type(plugin).transform(new AgentBuilder.Transformer.ForBuildPlugin(plugin))))));
    }

    @Test(expected = IllegalStateException.class)
    public void testRetransformationDisabledNotEnabledAllocator() throws Exception {
        new AgentBuilder.Default().with(DISABLED).with(Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testRetransformationDisabledNotEnabledListener() throws Exception {
        new AgentBuilder.Default().with(DISABLED).with(Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testRetransformationDisabledNotEnabledResubmission() throws Exception {
        new AgentBuilder.Default().with(DISABLED).withResubmission(Mockito.mock(AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.class));
    }

    /* empty */
    public static class Foo {}

    /* empty */
    public static class Bar {}

    /* empty */
    public static class Qux {}

    private static class ClassRedefinitionMatcher implements ArgumentMatcher<ClassDefinition> {
        private final Class<?> type;

        private ClassRedefinitionMatcher(Class<?> type) {
            this.type = type;
        }

        public boolean matches(ClassDefinition classDefinition) {
            return (classDefinition.getDefinitionClass()) == (type);
        }
    }

    private static class CauseMatcher implements ArgumentMatcher<Throwable> {
        private final Throwable throwable;

        private final int nesting;

        private CauseMatcher(Throwable throwable, int nesting) {
            this.throwable = throwable;
            this.nesting = nesting;
        }

        public boolean matches(Throwable item) {
            for (int index = 0; index < (nesting); index++) {
                item = item.getCause();
            }
            return throwable.equals(item);
        }
    }
}

