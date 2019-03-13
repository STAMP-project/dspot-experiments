package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DISABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.REDEFINITION;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Disabled.INSTANCE;


public class AgentBuilderRedefinitionStrategyResubmissionStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Instrumentation instrumentation;

    @Mock
    private AgentBuilder.RedefinitionStrategy.ResubmissionScheduler resubmissionScheduler;

    @Mock
    private AgentBuilder.LocationStrategy locationStrategy;

    @Mock
    private AgentBuilder.Listener listener;

    @Mock
    private AgentBuilder.InstallationListener installationListener;

    @Mock
    private ResettableClassFileTransformer classFileTransformer;

    @Mock
    private AgentBuilder.CircularityLock circularityLock;

    @Mock
    private AgentBuilder.RawMatcher rawMatcher;

    @Mock
    private ElementMatcher<? super Throwable> matcher;

    @Mock
    private Throwable error;

    @Mock
    private AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator;

    @Mock
    private AgentBuilder.RedefinitionStrategy.Listener redefinitionListener;

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformation() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verify(instrumentation).retransformClasses(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verify(redefinitionBatchAllocator).batch(Collections.<Class<?>>singletonList(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinition() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verify(instrumentation).redefineClasses(Mockito.argThat(new ArgumentMatcher<ClassDefinition>() {
            public boolean matches(ClassDefinition classDefinition) {
                return ((classDefinition.getDefinitionClass()) == (AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)) && (Arrays.equals(classDefinition.getDefinitionClassFile(), new byte[]{ 1, 2, 3 }));
            }
        }));
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verify(redefinitionBatchAllocator).batch(Collections.<Class<?>>singletonList(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class));
        Mockito.verifyNoMoreInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationNonModifiable() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(false);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionNonModifiable() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(false);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoRetransformation() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(false);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(false);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, DISABLED, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyZeroInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationNonAlive() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(false);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(false);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionNonAlive() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(false);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(false);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationNonMatched() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(false);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionNonMatched() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(false);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationAlreadyLoaded() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(false);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionAlreadyLoaded() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(false);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyZeroInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationNonMatchedError() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(false);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionNonMatchedError() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenReturn(true);
        Mockito.when(matcher.matches(error)).thenReturn(false);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetransformationError() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        RuntimeException runtimeException = new RuntimeException();
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenThrow(runtimeException);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, RETRANSFORMATION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, runtimeException);
        Mockito.verify(listener).onComplete(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefinitionError() throws Exception {
        Mockito.when(instrumentation.isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class)).thenReturn(true);
        Mockito.when(redefinitionBatchAllocator.batch(Mockito.any(List.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Collections.singleton(invocationOnMock.getArgument(0));
            }
        });
        RuntimeException runtimeException = new RuntimeException();
        Mockito.when(rawMatcher.matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain())).thenThrow(runtimeException);
        Mockito.when(matcher.matches(error)).thenReturn(true);
        Mockito.when(resubmissionScheduler.isAlive()).thenReturn(true);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(locationStrategy.classFileLocator(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class))).thenReturn(classFileLocator);
        Mockito.when(classFileLocator.locate(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName())).thenReturn(new ClassFileLocator.Resolution.Explicit(new byte[]{ 1, 2, 3 }));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Installation installation = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled(resubmissionScheduler, matcher).apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener);
        installation.getInstallationListener().onInstall(instrumentation, classFileTransformer);
        installation.getListener().onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(resubmissionScheduler).isAlive();
        Mockito.verify(resubmissionScheduler).schedule(argumentCaptor.capture());
        argumentCaptor.getValue().run();
        Mockito.verifyNoMoreInteractions(resubmissionScheduler);
        Mockito.verify(instrumentation).isModifiableClass(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verify(rawMatcher).matches(of(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class, AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getProtectionDomain());
        Mockito.verifyNoMoreInteractions(rawMatcher);
        Mockito.verifyZeroInteractions(redefinitionBatchAllocator);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), false, error);
        Mockito.verify(listener).onError(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true, runtimeException);
        Mockito.verify(listener).onComplete(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getName(), AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class.getClassLoader(), JavaModule.ofType(AgentBuilderRedefinitionStrategyResubmissionStrategyTest.Foo.class), true);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verify(matcher).matches(error);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    public void testDisabledListener() throws Exception {
        Assert.assertThat(INSTANCE.apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener).getListener(), CoreMatchers.sameInstance(listener));
    }

    @Test
    public void testDisabledInstallationListener() throws Exception {
        Assert.assertThat(INSTANCE.apply(instrumentation, locationStrategy, listener, installationListener, circularityLock, rawMatcher, REDEFINITION, redefinitionBatchAllocator, redefinitionListener).getInstallationListener(), CoreMatchers.sameInstance(installationListener));
    }

    @Test
    public void testLookupKeyBootstrapLoaderReference() throws Exception {
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey key = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        Assert.assertThat(key.hashCode(), CoreMatchers.is(0));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey other = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(new URLClassLoader(new URL[0]));
        System.gc();
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(other)));
        Assert.assertThat(key, CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(ClassLoadingStrategy.BOOTSTRAP_LOADER)));
        Assert.assertThat(key, CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(ClassLoadingStrategy.BOOTSTRAP_LOADER)))));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(new URLClassLoader(new URL[0])))))));
        Assert.assertThat(key, CoreMatchers.is(key));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new Object())));
    }

    @Test
    public void testLookupKeyNonBootstrapReference() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey key = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(classLoader);
        Assert.assertThat(key, CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(classLoader)));
        Assert.assertThat(key.hashCode(), CoreMatchers.is(classLoader.hashCode()));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(ClassLoadingStrategy.BOOTSTRAP_LOADER))));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(new URLClassLoader(new URL[0])))))));
        Assert.assertThat(key, CoreMatchers.is(key));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new Object())));
    }

    @Test
    public void testStorageKeyBootstrapLoaderReference() throws Exception {
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey key = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        Assert.assertThat(key.isBootstrapLoader(), CoreMatchers.is(true));
        Assert.assertThat(key.hashCode(), CoreMatchers.is(0));
        Assert.assertThat(key.get(), CoreMatchers.nullValue(ClassLoader.class));
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey other = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(new URLClassLoader(new URL[0]));
        System.gc();
        Assert.assertThat(other.get(), CoreMatchers.nullValue(ClassLoader.class));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(other)));
        Assert.assertThat(key, CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(ClassLoadingStrategy.BOOTSTRAP_LOADER)));
        Assert.assertThat(key, CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(ClassLoadingStrategy.BOOTSTRAP_LOADER)))));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(new URLClassLoader(new URL[0])))))));
        Assert.assertThat(key, CoreMatchers.is(key));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new Object())));
    }

    @Test
    public void testStorageKeyNonBootstrapReference() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey key = new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(classLoader);
        Assert.assertThat(key.isBootstrapLoader(), CoreMatchers.is(false));
        Assert.assertThat(key, CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(classLoader)));
        Assert.assertThat(key.hashCode(), CoreMatchers.is(classLoader.hashCode()));
        Assert.assertThat(key.get(), CoreMatchers.is(classLoader));
        classLoader = null;// Make GC eligible.

        System.gc();
        Assert.assertThat(key.get(), CoreMatchers.nullValue(ClassLoader.class));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.StorageKey(ClassLoadingStrategy.BOOTSTRAP_LOADER))));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(((Object) (new AgentBuilder.RedefinitionStrategy.ResubmissionStrategy.Enabled.LookupKey(new URLClassLoader(new URL[0])))))));
        Assert.assertThat(key, CoreMatchers.is(key));
        Assert.assertThat(key, CoreMatchers.not(CoreMatchers.is(new Object())));
        Assert.assertThat(key.isBootstrapLoader(), CoreMatchers.is(false));
    }

    @Test
    public void testSchedulerNoOp() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.NoOp.INSTANCE.schedule(runnable);
        Mockito.verifyZeroInteractions(runnable);
        Assert.assertThat(AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.NoOp.INSTANCE.isAlive(), CoreMatchers.is(false));
    }

    @Test
    public void testSchedulerAtFixedRate() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        Runnable runnable = Mockito.mock(Runnable.class);
        new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.AtFixedRate(scheduledExecutorService, 42L, TimeUnit.SECONDS).schedule(runnable);
        Mockito.verify(scheduledExecutorService).scheduleAtFixedRate(runnable, 42L, 42L, TimeUnit.SECONDS);
    }

    @Test
    public void testSchedulerAtFixedRateIsAlive() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        Assert.assertThat(new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.AtFixedRate(scheduledExecutorService, 42L, TimeUnit.SECONDS).isAlive(), CoreMatchers.is(true));
        Mockito.verify(scheduledExecutorService).isShutdown();
    }

    @Test
    public void testSchedulerWithFixedDelay() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        Assert.assertThat(new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.WithFixedDelay(scheduledExecutorService, 42L, TimeUnit.SECONDS).isAlive(), CoreMatchers.is(true));
        Mockito.verify(scheduledExecutorService).isShutdown();
    }

    /* empty */
    private static class Foo {}
}

