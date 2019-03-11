package net.bytebuddy.agent.builder;


import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;


public class AgentBuilderInitializationStrategyTest {
    private static final byte[] QUX = new byte[]{ 1, 2, 3 };

    private static final byte[] BAZ = new byte[]{ 4, 5, 6 };

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private LoadedTypeInitializer loadedTypeInitializer;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private AgentBuilder.InitializationStrategy.Dispatcher.InjectorFactory injectorFactory;

    @Test
    public void testNoOp() throws Exception {
        MatcherAssert.assertThat(INSTANCE.dispatcher(), CoreMatchers.is(((AgentBuilder.InitializationStrategy.Dispatcher) (INSTANCE))));
    }

    @Test
    public void testNoOpApplication() throws Exception {
        MatcherAssert.assertThat(INSTANCE.apply(builder), CoreMatchers.is(((DynamicType.Builder) (builder))));
    }

    @Test
    public void testNoOpRegistration() throws Exception {
        INSTANCE.register(dynamicType, classLoader, injectorFactory);
        Mockito.verifyZeroInteractions(dynamicType);
        Mockito.verifyZeroInteractions(classLoader);
        Mockito.verifyZeroInteractions(injectorFactory);
    }

    @Test
    public void testPremature() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.InitializationStrategy.Minimal.INSTANCE.dispatcher(), CoreMatchers.is(((AgentBuilder.InitializationStrategy.Dispatcher) (AgentBuilder.InitializationStrategy.Minimal.INSTANCE))));
    }

    @Test
    public void testPrematureApplication() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.InitializationStrategy.Minimal.INSTANCE.apply(builder), CoreMatchers.is(((DynamicType.Builder) (builder))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMinimalRegistrationIndependentType() throws Exception {
        Annotation eagerAnnotation = Mockito.mock(AuxiliaryType.SignatureRelevant.class);
        Mockito.when(eagerAnnotation.annotationType()).thenReturn(((Class) (AuxiliaryType.SignatureRelevant.class)));
        TypeDescription independent = Mockito.mock(TypeDescription.class);
        TypeDescription dependent = Mockito.mock(TypeDescription.class);
        Mockito.when(independent.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(eagerAnnotation));
        Mockito.when(dependent.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Map<TypeDescription, byte[]> map = new HashMap<TypeDescription, byte[]>();
        map.put(independent, AgentBuilderInitializationStrategyTest.QUX);
        map.put(dependent, AgentBuilderInitializationStrategyTest.BAZ);
        Mockito.when(dynamicType.getAuxiliaryTypes()).thenReturn(map);
        ClassInjector classInjector = Mockito.mock(ClassInjector.class);
        Mockito.when(injectorFactory.resolve()).thenReturn(classInjector);
        Mockito.when(classInjector.inject(Collections.singletonMap(independent, AgentBuilderInitializationStrategyTest.QUX))).thenReturn(Collections.<TypeDescription, Class<?>>singletonMap(independent, AgentBuilderInitializationStrategyTest.Foo.class));
        LoadedTypeInitializer loadedTypeInitializer = Mockito.mock(LoadedTypeInitializer.class);
        Mockito.when(dynamicType.getLoadedTypeInitializers()).thenReturn(Collections.singletonMap(independent, loadedTypeInitializer));
        AgentBuilder.InitializationStrategy.Minimal.INSTANCE.register(dynamicType, classLoader, injectorFactory);
        Mockito.verify(classInjector).inject(Collections.singletonMap(independent, AgentBuilderInitializationStrategyTest.QUX));
        Mockito.verifyNoMoreInteractions(classInjector);
        Mockito.verify(loadedTypeInitializer).onLoad(AgentBuilderInitializationStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(loadedTypeInitializer);
    }

    @Test
    public void testMinimalRegistrationDependentType() throws Exception {
        TypeDescription dependent = Mockito.mock(TypeDescription.class);
        Mockito.when(dependent.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(dynamicType.getAuxiliaryTypes()).thenReturn(Collections.singletonMap(dependent, AgentBuilderInitializationStrategyTest.BAZ));
        AgentBuilder.InitializationStrategy.Minimal.INSTANCE.register(dynamicType, classLoader, injectorFactory);
        Mockito.verifyZeroInteractions(injectorFactory);
    }

    /* empty */
    private static class Foo {}
}

