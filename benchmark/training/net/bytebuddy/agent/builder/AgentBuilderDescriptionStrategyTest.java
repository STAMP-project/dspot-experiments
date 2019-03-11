package net.bytebuddy.agent.builder;


import java.util.concurrent.ExecutorService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy.Default.HYBRID;
import static net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy.Default.POOL_FIRST;
import static net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy.Default.POOL_ONLY;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;


public class AgentBuilderDescriptionStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AgentBuilder.LocationStrategy locationStrategy;

    @Mock
    private TypePool typePool;

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testDescriptionHybridWithLoaded() throws Exception {
        ClassFileLocator classFileLocator = of(Object.class.getClassLoader());
        Mockito.when(typePool.describe(Object.class.getName())).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        Mockito.when(locationStrategy.classFileLocator(Object.class.getClassLoader(), JavaModule.ofType(Object.class))).thenReturn(classFileLocator);
        TypeDescription typeDescription = HYBRID.apply(Object.class.getName(), Object.class, typePool, Mockito.mock(AgentBuilder.CircularityLock.class), Object.class.getClassLoader(), JavaModule.ofType(Object.class));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(TypeDescription.OBJECT));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.instanceOf(TypeDescription.ForLoadedType.class));
    }

    @Test
    public void testDescriptionHybridWithoutLoaded() throws Exception {
        Mockito.when(typePool.describe(Object.class.getName())).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        TypeDescription typeDescription = HYBRID.apply(Object.class.getName(), null, typePool, Mockito.mock(AgentBuilder.CircularityLock.class), Object.class.getClassLoader(), JavaModule.ofType(Object.class));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(this.typeDescription));
    }

    @Test
    public void testDescriptionPoolOnly() throws Exception {
        Mockito.when(typePool.describe(Object.class.getName())).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        MatcherAssert.assertThat(POOL_ONLY.apply(Object.class.getName(), Object.class, typePool, Mockito.mock(AgentBuilder.CircularityLock.class), Object.class.getClassLoader(), JavaModule.ofType(Object.class)), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testSuperTypeLoading() throws Exception {
        MatcherAssert.assertThat(HYBRID.withSuperTypeLoading(), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading(HYBRID)))));
        MatcherAssert.assertThat(POOL_FIRST.withSuperTypeLoading(), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading(POOL_FIRST)))));
        MatcherAssert.assertThat(POOL_ONLY.withSuperTypeLoading(), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading(POOL_ONLY)))));
    }

    @Test
    public void testAsynchronousSuperTypeLoading() throws Exception {
        ExecutorService executorService = Mockito.mock(ExecutorService.class);
        MatcherAssert.assertThat(HYBRID.withSuperTypeLoading(executorService), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading.Asynchronous(HYBRID, executorService)))));
        MatcherAssert.assertThat(POOL_FIRST.withSuperTypeLoading(executorService), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading.Asynchronous(POOL_FIRST, executorService)))));
        MatcherAssert.assertThat(POOL_ONLY.withSuperTypeLoading(executorService), FieldByFieldComparison.hasPrototype(((AgentBuilder.DescriptionStrategy) (new AgentBuilder.DescriptionStrategy.SuperTypeLoading.Asynchronous(POOL_ONLY, executorService)))));
    }
}

