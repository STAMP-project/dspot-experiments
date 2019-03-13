package net.bytebuddy.dynamic.loading;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.SynchronizationStrategy.ForLegacyVm.INSTANCE;


public class ByteArrayClassLoaderSynchronizationStrategyTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ByteArrayClassLoader classLoader;

    @Test
    public void testInitialize() throws Exception {
        MatcherAssert.assertThat(INSTANCE.initialize(), CoreMatchers.is(((ByteArrayClassLoader.SynchronizationStrategy) (INSTANCE))));
    }

    @Test
    public void testLegacyVm() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getClassLoadingLock(classLoader, ByteArrayClassLoaderSynchronizationStrategyTest.FOO), CoreMatchers.is(((Object) (classLoader))));
    }
}

