package net.bytebuddy.agent.builder;


import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.agent.builder.AgentBuilder.PoolStrategy.Default.EXTENDED;
import static net.bytebuddy.agent.builder.AgentBuilder.PoolStrategy.Default.FAST;


public class AgentBuilderPoolStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassLoader classLoader;

    @Mock
    private ClassFileLocator classFileLocator;

    @Test
    public void testFastTypePool() throws Exception {
        MatcherAssert.assertThat(FAST.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }

    @Test
    public void testExtendedTypePool() throws Exception {
        MatcherAssert.assertThat(EXTENDED.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }

    @Test
    public void testFastEagerTypePool() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.PoolStrategy.Eager.FAST.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }

    @Test
    public void testExtendedEagerTypePool() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.PoolStrategy.Eager.EXTENDED.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }

    @Test
    public void testFastLoadingTypePool() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.PoolStrategy.ClassLoading.FAST.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }

    @Test
    public void testExtendedLoadingTypePool() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.PoolStrategy.ClassLoading.EXTENDED.typePool(classFileLocator, classLoader), CoreMatchers.notNullValue(TypePool.class));
    }
}

