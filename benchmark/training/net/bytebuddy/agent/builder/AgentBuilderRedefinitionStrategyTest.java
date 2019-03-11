package net.bytebuddy.agent.builder;


import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DISABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.REDEFINITION;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;


public class AgentBuilderRedefinitionStrategyTest {
    @Test
    public void testDisabledRedefinitionStrategyIsDisabled() throws Exception {
        MatcherAssert.assertThat(DISABLED.isEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void testRetransformationStrategyIsEnabled() throws Exception {
        MatcherAssert.assertThat(RETRANSFORMATION.isEnabled(), CoreMatchers.is(true));
    }

    @Test
    public void testRedefinitionStrategyIsEnabled() throws Exception {
        MatcherAssert.assertThat(REDEFINITION.isEnabled(), CoreMatchers.is(true));
    }

    @Test
    public void testDisabledRedefinitionStrategyIsRetransforming() throws Exception {
        MatcherAssert.assertThat(DISABLED.isRetransforming(), CoreMatchers.is(false));
    }

    @Test
    public void testRetransformationStrategyIsRetransforming() throws Exception {
        MatcherAssert.assertThat(RETRANSFORMATION.isRetransforming(), CoreMatchers.is(true));
    }

    @Test
    public void testRedefinitionStrategyIsRetransforming() throws Exception {
        MatcherAssert.assertThat(REDEFINITION.isRetransforming(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testDisabledRedefinitionStrategyIsNotChecked() throws Exception {
        DISABLED.check(Mockito.mock(Instrumentation.class));
    }

    @Test
    public void testRetransformationStrategyIsChecked() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        RETRANSFORMATION.check(instrumentation);
    }

    @Test(expected = IllegalStateException.class)
    public void testRetransformationStrategyNotSupportedThrowsException() throws Exception {
        RETRANSFORMATION.check(Mockito.mock(Instrumentation.class));
    }

    @Test
    public void testRedefinitionStrategyIsChecked() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        REDEFINITION.check(instrumentation);
    }

    @Test(expected = IllegalStateException.class)
    public void testRedefinitionStrategyNotSupportedThrowsException() throws Exception {
        REDEFINITION.check(Mockito.mock(Instrumentation.class));
    }

    @Test
    public void testPrependableIterator() throws Exception {
        AgentBuilder.RedefinitionStrategy.Collector.PrependableIterator iterator = new AgentBuilder.RedefinitionStrategy.Collector.PrependableIterator(Collections.singleton(Collections.<Class<?>>singletonList(Void.class)));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        iterator.prepend(Collections.<List<Class<?>>>emptyList());
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(Collections.<Class<?>>singletonList(Void.class)));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        iterator.prepend(Collections.singleton(Collections.<Class<?>>singletonList(String.class)));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(Collections.<Class<?>>singletonList(String.class)));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }
}

