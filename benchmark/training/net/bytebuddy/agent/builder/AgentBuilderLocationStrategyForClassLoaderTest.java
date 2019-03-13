package net.bytebuddy.agent.builder;


import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.ForClassLoader.STRONG;
import static net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.ForClassLoader.WEAK;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.WeaklyReferenced.of;


public class AgentBuilderLocationStrategyForClassLoaderTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private AgentBuilder.LocationStrategy fallback;

    @Mock
    private ClassFileLocator classFileLocator;

    @Test
    public void testStrongLocationStrategy() throws Exception {
        MatcherAssert.assertThat(STRONG.classFileLocator(classLoader, module), FieldByFieldComparison.hasPrototype(ClassFileLocator.ForClassLoader.of(classLoader)));
    }

    @Test
    public void testWeakLocationStrategy() throws Exception {
        MatcherAssert.assertThat(WEAK.classFileLocator(classLoader, module), FieldByFieldComparison.hasPrototype(of(classLoader)));
    }

    @Test
    public void testFallback() throws Exception {
        MatcherAssert.assertThat(STRONG.withFallbackTo(fallback), FieldByFieldComparison.hasPrototype(((AgentBuilder.LocationStrategy) (new AgentBuilder.LocationStrategy.Compound(STRONG, fallback)))));
        MatcherAssert.assertThat(WEAK.withFallbackTo(fallback), FieldByFieldComparison.hasPrototype(((AgentBuilder.LocationStrategy) (new AgentBuilder.LocationStrategy.Compound(WEAK, fallback)))));
    }

    @Test
    public void testFallbackLocator() throws Exception {
        MatcherAssert.assertThat(STRONG.withFallbackTo(classFileLocator), FieldByFieldComparison.hasPrototype(((AgentBuilder.LocationStrategy) (new AgentBuilder.LocationStrategy.Compound(STRONG, new AgentBuilder.LocationStrategy.Simple(classFileLocator))))));
        MatcherAssert.assertThat(WEAK.withFallbackTo(classFileLocator), FieldByFieldComparison.hasPrototype(((AgentBuilder.LocationStrategy) (new AgentBuilder.LocationStrategy.Compound(WEAK, new AgentBuilder.LocationStrategy.Simple(classFileLocator))))));
    }
}

