package net.bytebuddy.build;


import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.pool.TypePool.Default.ReaderMode.EXTENDED;
import static net.bytebuddy.pool.TypePool.Default.ReaderMode.FAST;


public class PluginEnginePoolStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileLocator classFileLocator;

    @Test
    public void testWithLazyResolutionFast() {
        MatcherAssert.assertThat(Engine.typePool(classFileLocator), FieldByFieldComparison.hasPrototype(((TypePool) (new TypePool.Default.WithLazyResolution(new TypePool.CacheProvider.Simple(), classFileLocator, FAST, ofPlatformLoader())))));
    }

    @Test
    public void testWithLazyResolutionExtended() {
        MatcherAssert.assertThat(Engine.typePool(classFileLocator), FieldByFieldComparison.hasPrototype(((TypePool) (new TypePool.Default.WithLazyResolution(new TypePool.CacheProvider.Simple(), classFileLocator, EXTENDED, ofPlatformLoader())))));
    }

    @Test
    public void testWithEagerResolutionFast() {
        MatcherAssert.assertThat(Engine.typePool(classFileLocator), FieldByFieldComparison.hasPrototype(((TypePool) (new TypePool.Default(new TypePool.CacheProvider.Simple(), classFileLocator, FAST, ofPlatformLoader())))));
    }

    @Test
    public void testWithEagerResolutionExtended() {
        MatcherAssert.assertThat(Engine.typePool(classFileLocator), FieldByFieldComparison.hasPrototype(((TypePool) (new TypePool.Default(new TypePool.CacheProvider.Simple(), classFileLocator, EXTENDED, ofPlatformLoader())))));
    }
}

