package net.bytebuddy.dynamic;


import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
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

import static net.bytebuddy.dynamic.TypeResolutionStrategy.Passive.INSTANCE;


public class TypeResolutionStrategyTest {
    private static final byte[] FOO = new byte[]{ 1, 2, 3 };

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeInitializer typeInitializer;

    @Mock
    private TypeInitializer otherTypeInitializer;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private ClassLoadingStrategy<ClassLoader> classLoadingStrategy;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private LoadedTypeInitializer loadedTypeInitializer;

    @Test
    public void testPassive() throws Exception {
        TypeResolutionStrategy.Resolved resolved = INSTANCE.resolve();
        MatcherAssert.assertThat(resolved.injectedInto(typeInitializer), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(resolved.initialize(dynamicType, classLoader, classLoadingStrategy), CoreMatchers.is(Collections.<TypeDescription, Class<?>>singletonMap(typeDescription, TypeResolutionStrategyTest.Foo.class)));
        Mockito.verify(classLoadingStrategy).load(classLoader, Collections.singletonMap(typeDescription, TypeResolutionStrategyTest.FOO));
        Mockito.verifyNoMoreInteractions(classLoadingStrategy);
        Mockito.verify(loadedTypeInitializer).onLoad(TypeResolutionStrategyTest.Foo.class);
        Mockito.verifyNoMoreInteractions(loadedTypeInitializer);
    }

    @Test
    public void testActive() throws Exception {
        TypeResolutionStrategy.Resolved resolved = new TypeResolutionStrategy.Active().resolve();
        Field field = TypeResolutionStrategy.Active.Resolved.class.getDeclaredField("identification");
        field.setAccessible(true);
        int identification = ((Integer) (field.get(resolved)));
        Mockito.when(typeInitializer.expandWith(FieldByFieldComparison.matchesPrototype(new NexusAccessor.InitializationAppender(identification)))).thenReturn(otherTypeInitializer);
        MatcherAssert.assertThat(resolved.injectedInto(typeInitializer), CoreMatchers.is(otherTypeInitializer));
        MatcherAssert.assertThat(resolved.initialize(dynamicType, classLoader, classLoadingStrategy), CoreMatchers.is(Collections.<TypeDescription, Class<?>>singletonMap(typeDescription, TypeResolutionStrategyTest.Foo.class)));
        try {
            Mockito.verify(classLoadingStrategy).load(classLoader, Collections.singletonMap(typeDescription, TypeResolutionStrategyTest.FOO));
            Mockito.verifyNoMoreInteractions(classLoadingStrategy);
            Mockito.verify(loadedTypeInitializer).isAlive();
            Mockito.verifyNoMoreInteractions(loadedTypeInitializer);
        } finally {
            Field initializers = Nexus.class.getDeclaredField("TYPE_INITIALIZERS");
            initializers.setAccessible(true);
            Constructor<Nexus> constructor = Nexus.class.getDeclaredConstructor(String.class, ClassLoader.class, ReferenceQueue.class, int.class);
            constructor.setAccessible(true);
            Object value = ((Map<?, ?>) (initializers.get(null))).remove(constructor.newInstance(TypeResolutionStrategyTest.Foo.class.getName(), TypeResolutionStrategyTest.Foo.class.getClassLoader(), null, identification));
            MatcherAssert.assertThat(value, CoreMatchers.is(((Object) (loadedTypeInitializer))));
        }
    }

    @Test
    public void testLazy() throws Exception {
        TypeResolutionStrategy.Resolved resolved = TypeResolutionStrategy.Lazy.INSTANCE.resolve();
        MatcherAssert.assertThat(resolved.injectedInto(typeInitializer), CoreMatchers.is(typeInitializer));
        MatcherAssert.assertThat(resolved.initialize(dynamicType, classLoader, classLoadingStrategy), CoreMatchers.is(Collections.<TypeDescription, Class<?>>singletonMap(typeDescription, TypeResolutionStrategyTest.Foo.class)));
        Mockito.verify(classLoadingStrategy).load(classLoader, Collections.singletonMap(typeDescription, TypeResolutionStrategyTest.FOO));
        Mockito.verifyNoMoreInteractions(classLoadingStrategy);
        Mockito.verifyNoMoreInteractions(loadedTypeInitializer);
    }

    @Test
    public void testDisabled() throws Exception {
        TypeResolutionStrategy.Resolved resolved = TypeResolutionStrategy.Disabled.INSTANCE.resolve();
        MatcherAssert.assertThat(resolved.injectedInto(typeInitializer), CoreMatchers.is(typeInitializer));
    }

    @Test(expected = IllegalStateException.class)
    public void testDisabledCannotBeApplied() throws Exception {
        TypeResolutionStrategy.Disabled.INSTANCE.resolve().initialize(dynamicType, classLoader, classLoadingStrategy);
    }

    /* empty */
    private static class Foo {}
}

