package net.bytebuddy.agent.builder;


import java.lang.instrument.Instrumentation;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.Default.NativeMethodStrategy.Disabled.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.Default.NativeMethodStrategy.ForPrefix.of;


public class AgentBuilderDefaultNativeMethodStrategyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test(expected = IllegalStateException.class)
    public void testDisabledStrategyThrowsExceptionForPrefix() throws Exception {
        INSTANCE.getPrefix();
    }

    @Test
    public void testDisabledStrategyIsDisabled() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isEnabled(Mockito.mock(Instrumentation.class)), CoreMatchers.is(false));
    }

    @Test
    public void testDisabledStrategySuffixesNames() throws Exception {
        MatcherAssert.assertThat(INSTANCE.resolve().transform(methodDescription), CoreMatchers.startsWith(AgentBuilderDefaultNativeMethodStrategyTest.BAR));
        MatcherAssert.assertThat(INSTANCE.resolve().transform(methodDescription), CoreMatchers.not(AgentBuilderDefaultNativeMethodStrategyTest.BAR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnabledStrategyMustNotBeEmptyString() throws Exception {
        of("");
    }

    @Test
    public void testEnabledStrategyReturnsPrefix() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.Default.NativeMethodStrategy.ForPrefix(AgentBuilderDefaultNativeMethodStrategyTest.FOO).getPrefix(), CoreMatchers.is(AgentBuilderDefaultNativeMethodStrategyTest.FOO));
    }

    @Test
    public void testEnabledStrategyIsEnabled() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isNativeMethodPrefixSupported()).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.Default.NativeMethodStrategy.ForPrefix(AgentBuilderDefaultNativeMethodStrategyTest.FOO).isEnabled(instrumentation), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnabledStrategyThrowsExceptionIfNotSupported() throws Exception {
        new AgentBuilder.Default.NativeMethodStrategy.ForPrefix(AgentBuilderDefaultNativeMethodStrategyTest.FOO).isEnabled(Mockito.mock(Instrumentation.class));
    }

    @Test
    public void testEnabledStrategySuffixesNames() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.Default.NativeMethodStrategy.ForPrefix(AgentBuilderDefaultNativeMethodStrategyTest.FOO).resolve().transform(methodDescription), CoreMatchers.is(((AgentBuilderDefaultNativeMethodStrategyTest.FOO) + (AgentBuilderDefaultNativeMethodStrategyTest.BAR))));
    }
}

