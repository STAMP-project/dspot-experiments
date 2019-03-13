package net.bytebuddy.agent.builder;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentBuilderFallbackStrategyByThrowableTypeTest {
    // In absence of @SafeVarargs
    @Test
    @SuppressWarnings("unchecked")
    public void testIsFallback() throws Exception {
        Assert.assertThat(new AgentBuilder.FallbackStrategy.ByThrowableType(Exception.class).isFallback(Object.class, new Exception()), CoreMatchers.is(true));
    }

    // In absence of @SafeVarargs
    @Test
    @SuppressWarnings("unchecked")
    public void testIsFallbackInherited() throws Exception {
        Assert.assertThat(new AgentBuilder.FallbackStrategy.ByThrowableType(Exception.class).isFallback(Object.class, new RuntimeException()), CoreMatchers.is(true));
    }

    // In absence of @SafeVarargs
    @Test
    @SuppressWarnings("unchecked")
    public void testIsNoFallback() throws Exception {
        Assert.assertThat(new AgentBuilder.FallbackStrategy.ByThrowableType(RuntimeException.class).isFallback(Object.class, new Exception()), CoreMatchers.is(false));
    }
}

