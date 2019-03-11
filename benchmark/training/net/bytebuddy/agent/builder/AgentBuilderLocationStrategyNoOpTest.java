package net.bytebuddy.agent.builder;


import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.NoOp.INSTANCE;


public class AgentBuilderLocationStrategyNoOpTest {
    @Test
    public void testApplication() throws Exception {
        MatcherAssert.assertThat(INSTANCE.classFileLocator(Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class)), CoreMatchers.is(((ClassFileLocator) (ClassFileLocator.NoOp.INSTANCE))));
    }
}

