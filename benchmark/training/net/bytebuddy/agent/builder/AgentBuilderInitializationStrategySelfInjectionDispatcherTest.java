package net.bytebuddy.agent.builder;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.Nexus;
import net.bytebuddy.dynamic.NexusAccessor;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentBuilderInitializationStrategySelfInjectionDispatcherTest {
    private static final int IDENTIFIER = 42;

    private static final byte[] FOO = new byte[]{ 1, 2, 3 };

    private static final byte[] BAR = new byte[]{ 4, 5, 6 };

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private DynamicType.Builder<?> appendedBuilder;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private AgentBuilder.InitializationStrategy.Dispatcher.InjectorFactory injectorFactory;

    @Mock
    private ClassInjector classInjector;

    @Mock
    private TypeDescription instrumented;

    @Mock
    private TypeDescription dependent;

    @Mock
    private TypeDescription independent;

    @Mock
    private LoadedTypeInitializer instrumentedInitializer;

    @Mock
    private LoadedTypeInitializer dependentInitializer;

    @Mock
    private LoadedTypeInitializer independentInitializer;

    private NexusAccessor nexusAccessor = new NexusAccessor();

    @Test
    @SuppressWarnings("unchecked")
    public void testSplitInitialization() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Split.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        MatcherAssert.assertThat(dispatcher.apply(builder), CoreMatchers.is(((DynamicType.Builder) (appendedBuilder))));
        Mockito.verify(builder).initializer(FieldByFieldComparison.matchesPrototype(new NexusAccessor.InitializationAppender(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER)));
        Mockito.verifyNoMoreInteractions(builder);
        Mockito.verifyZeroInteractions(appendedBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLazyInitialization() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Lazy.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        MatcherAssert.assertThat(dispatcher.apply(builder), CoreMatchers.is(((DynamicType.Builder) (appendedBuilder))));
        Mockito.verify(builder).initializer(FieldByFieldComparison.matchesPrototype(new NexusAccessor.InitializationAppender(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER)));
        Mockito.verifyNoMoreInteractions(builder);
        Mockito.verifyZeroInteractions(appendedBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEagerInitialization() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Eager.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        MatcherAssert.assertThat(dispatcher.apply(builder), CoreMatchers.is(((DynamicType.Builder) (appendedBuilder))));
        Mockito.verify(builder).initializer(FieldByFieldComparison.matchesPrototype(new NexusAccessor.InitializationAppender(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER)));
        Mockito.verifyNoMoreInteractions(builder);
        Mockito.verifyZeroInteractions(appendedBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSplit() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Split.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        dispatcher.register(dynamicType, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class.getClassLoader(), injectorFactory);
        Mockito.verify(classInjector).inject(Collections.singletonMap(independent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.BAR));
        Mockito.verifyNoMoreInteractions(classInjector);
        Mockito.verify(independentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Bar.class);
        Mockito.verifyNoMoreInteractions(independentInitializer);
        Nexus.initialize(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        Mockito.verify(classInjector).inject(Collections.singletonMap(dependent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.FOO));
        Mockito.verifyNoMoreInteractions(classInjector);
        Mockito.verify(dependentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Foo.class);
        Mockito.verifyNoMoreInteractions(dependentInitializer);
        Mockito.verify(instrumentedInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class);
        Mockito.verifyNoMoreInteractions(instrumentedInitializer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEager() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Eager.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        dispatcher.register(dynamicType, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class.getClassLoader(), injectorFactory);
        Map<TypeDescription, byte[]> injected = new HashMap<TypeDescription, byte[]>();
        injected.put(independent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.BAR);
        injected.put(dependent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.FOO);
        Mockito.verify(classInjector).inject(injected);
        Mockito.verifyNoMoreInteractions(classInjector);
        Mockito.verify(independentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Bar.class);
        Mockito.verifyNoMoreInteractions(independentInitializer);
        Mockito.verify(dependentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Foo.class);
        Mockito.verifyNoMoreInteractions(dependentInitializer);
        Nexus.initialize(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        Mockito.verify(instrumentedInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class);
        Mockito.verify(instrumentedInitializer).isAlive();
        Mockito.verifyNoMoreInteractions(instrumentedInitializer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLazy() throws Exception {
        AgentBuilder.InitializationStrategy.Dispatcher dispatcher = new AgentBuilder.InitializationStrategy.SelfInjection.Lazy.Dispatcher(nexusAccessor, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        dispatcher.register(dynamicType, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class.getClassLoader(), injectorFactory);
        Mockito.verifyZeroInteractions(classInjector, dependentInitializer, independentInitializer);
        Nexus.initialize(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.IDENTIFIER);
        Map<TypeDescription, byte[]> injected = new HashMap<TypeDescription, byte[]>();
        injected.put(independent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.BAR);
        injected.put(dependent, AgentBuilderInitializationStrategySelfInjectionDispatcherTest.FOO);
        Mockito.verify(classInjector).inject(injected);
        Mockito.verifyNoMoreInteractions(classInjector);
        Mockito.verify(independentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Bar.class);
        Mockito.verifyNoMoreInteractions(independentInitializer);
        Mockito.verify(dependentInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Foo.class);
        Mockito.verifyNoMoreInteractions(dependentInitializer);
        Mockito.verify(instrumentedInitializer).onLoad(AgentBuilderInitializationStrategySelfInjectionDispatcherTest.Qux.class);
        Mockito.verifyNoMoreInteractions(instrumentedInitializer);
    }

    @Test
    public void testDispatcherCreation() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.InitializationStrategy.SelfInjection.Split().dispatcher(), CoreMatchers.instanceOf(AgentBuilder.InitializationStrategy.SelfInjection.Split.Dispatcher.class));
        MatcherAssert.assertThat(new AgentBuilder.InitializationStrategy.SelfInjection.Eager().dispatcher(), CoreMatchers.instanceOf(AgentBuilder.InitializationStrategy.SelfInjection.Eager.Dispatcher.class));
        MatcherAssert.assertThat(new AgentBuilder.InitializationStrategy.SelfInjection.Lazy().dispatcher(), CoreMatchers.instanceOf(AgentBuilder.InitializationStrategy.SelfInjection.Lazy.Dispatcher.class));
    }

    /* empty */
    private static class Foo {}

    /* empty */
    private static class Bar {}

    /* empty */
    private static class Qux {}
}

