package net.bytebuddy.pool;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.pool.TypePool.CacheProvider.NoOp.INSTANCE;
import static net.bytebuddy.pool.TypePool.CacheProvider.Simple.<init>;


public class TypePoolCacheProviderTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool.Resolution resolution;

    @Test
    public void testNoOp() throws Exception {
        MatcherAssert.assertThat(INSTANCE.find(TypePoolCacheProviderTest.FOO), CoreMatchers.nullValue(TypePool.Resolution.class));
        MatcherAssert.assertThat(INSTANCE.register(TypePoolCacheProviderTest.FOO, resolution), CoreMatchers.sameInstance(resolution));
        MatcherAssert.assertThat(INSTANCE.find(TypePoolCacheProviderTest.FOO), CoreMatchers.nullValue(TypePool.Resolution.class));
        INSTANCE.clear();
    }

    @Test
    public void testSimple() throws Exception {
        TypePool.CacheProvider simple = new TypePool.CacheProvider.Simple();
        MatcherAssert.assertThat(simple.find(TypePoolCacheProviderTest.FOO), CoreMatchers.nullValue(TypePool.Resolution.class));
        MatcherAssert.assertThat(simple.register(TypePoolCacheProviderTest.FOO, resolution), CoreMatchers.sameInstance(resolution));
        MatcherAssert.assertThat(simple.find(TypePoolCacheProviderTest.FOO), CoreMatchers.sameInstance(resolution));
        TypePool.Resolution resolution = Mockito.mock(TypePool.Resolution.class);
        MatcherAssert.assertThat(simple.register(TypePoolCacheProviderTest.FOO, resolution), CoreMatchers.sameInstance(this.resolution));
        MatcherAssert.assertThat(simple.find(TypePoolCacheProviderTest.FOO), CoreMatchers.sameInstance(this.resolution));
        simple.clear();
        MatcherAssert.assertThat(simple.find(TypePoolCacheProviderTest.FOO), CoreMatchers.nullValue(TypePool.Resolution.class));
        MatcherAssert.assertThat(simple.register(TypePoolCacheProviderTest.FOO, resolution), CoreMatchers.sameInstance(resolution));
        MatcherAssert.assertThat(simple.find(TypePoolCacheProviderTest.FOO), CoreMatchers.sameInstance(resolution));
    }

    @Test
    public void testSimpleMap() {
        ConcurrentMap<String, TypePool.Resolution> storage = new ConcurrentHashMap<String, TypePool.Resolution>();
        TypePool.CacheProvider.Simple cacheProvider = new TypePool.CacheProvider.Simple(storage);
        MatcherAssert.assertThat(getStorage(), CoreMatchers.sameInstance(storage));
    }
}

