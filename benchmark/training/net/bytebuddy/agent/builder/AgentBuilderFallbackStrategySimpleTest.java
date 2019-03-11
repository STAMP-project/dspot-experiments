package net.bytebuddy.agent.builder;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.agent.builder.AgentBuilder.FallbackStrategy.Simple.DISABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.FallbackStrategy.Simple.ENABLED;


public class AgentBuilderFallbackStrategySimpleTest {
    @Test
    public void testEnabled() throws Exception {
        Assert.assertThat(ENABLED.isFallback(Object.class, new Throwable()), CoreMatchers.is(true));
    }

    @Test
    public void testDisabled() throws Exception {
        Assert.assertThat(DISABLED.isFallback(Object.class, new Throwable()), CoreMatchers.is(false));
    }
}

