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
import org.mockito.Mockito;


public class AgentBuilderLocationStrategyCompoundTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AgentBuilder.LocationStrategy first;

    @Mock
    private AgentBuilder.LocationStrategy second;

    @Mock
    private ClassFileLocator firstLocator;

    @Mock
    private ClassFileLocator secondLocator;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Test
    public void testApplication() throws Exception {
        AgentBuilder.LocationStrategy locationStrategy = new AgentBuilder.LocationStrategy.Compound(first, second);
        Mockito.when(first.classFileLocator(classLoader, module)).thenReturn(firstLocator);
        Mockito.when(second.classFileLocator(classLoader, module)).thenReturn(secondLocator);
        MatcherAssert.assertThat(locationStrategy.classFileLocator(classLoader, module), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.Compound(firstLocator, secondLocator)))));
        Mockito.verify(first).classFileLocator(classLoader, module);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).classFileLocator(classLoader, module);
        Mockito.verifyNoMoreInteractions(second);
    }
}

