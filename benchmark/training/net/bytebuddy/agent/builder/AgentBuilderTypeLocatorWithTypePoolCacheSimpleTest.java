package net.bytebuddy.agent.builder;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.pool.TypePool.Default.ReaderMode.FAST;


public class AgentBuilderTypeLocatorWithTypePoolCacheSimpleTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private ClassLoader first;

    @Mock
    private ClassLoader second;

    @Mock
    private TypePool.CacheProvider firstCache;

    @Mock
    private TypePool.CacheProvider secondCache;

    @Test
    public void testSimpleImplementation() throws Exception {
        ConcurrentMap<ClassLoader, TypePool.CacheProvider> cacheProviders = new ConcurrentHashMap<ClassLoader, TypePool.CacheProvider>();
        cacheProviders.put(first, firstCache);
        cacheProviders.put(second, secondCache);
        AgentBuilder.PoolStrategy poolStrategy = new AgentBuilder.PoolStrategy.WithTypePoolCache.Simple(FAST, cacheProviders);
        MatcherAssert.assertThat(poolStrategy.typePool(classFileLocator, first), FieldByFieldComparison.hasPrototype(poolStrategy.typePool(classFileLocator, first)));
        MatcherAssert.assertThat(poolStrategy.typePool(classFileLocator, first), CoreMatchers.not(FieldByFieldComparison.hasPrototype(poolStrategy.typePool(classFileLocator, second))));
    }

    @Test
    public void testSimpleImplementationBootstrap() throws Exception {
        ConcurrentMap<ClassLoader, TypePool.CacheProvider> cacheProviders = new ConcurrentHashMap<ClassLoader, TypePool.CacheProvider>();
        cacheProviders.put(ClassLoader.getSystemClassLoader(), firstCache);
        cacheProviders.put(second, secondCache);
        AgentBuilder.PoolStrategy poolStrategy = new AgentBuilder.PoolStrategy.WithTypePoolCache.Simple(FAST, cacheProviders);
        MatcherAssert.assertThat(poolStrategy.typePool(classFileLocator, null), FieldByFieldComparison.hasPrototype(poolStrategy.typePool(classFileLocator, null)));
        MatcherAssert.assertThat(poolStrategy.typePool(classFileLocator, null), CoreMatchers.not(FieldByFieldComparison.hasPrototype(poolStrategy.typePool(classFileLocator, second))));
    }
}

