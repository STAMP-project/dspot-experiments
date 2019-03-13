package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.ByteBuddy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy.DISABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy.ENABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy.of;


public class AgentBuilderLambdaInstrumentationStrategyTest {
    @Test
    public void testEnabled() throws Exception {
        MatcherAssert.assertThat(of(true).isEnabled(), CoreMatchers.is(true));
        MatcherAssert.assertThat(of(false).isEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void testEnabledStrategyNeverThrowsException() throws Exception {
        ClassFileTransformer initialClassFileTransformer = Mockito.mock(ClassFileTransformer.class);
        MatcherAssert.assertThat(LambdaFactory.register(initialClassFileTransformer, Mockito.mock(AgentBuilder.LambdaInstrumentationStrategy.LambdaInstanceFactory.class)), CoreMatchers.is(true));
        try {
            ByteBuddy byteBuddy = Mockito.mock(ByteBuddy.class);
            Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
            ClassFileTransformer classFileTransformer = Mockito.mock(ClassFileTransformer.class);
            try {
                ENABLED.apply(byteBuddy, instrumentation, classFileTransformer);
            } finally {
                MatcherAssert.assertThat(LambdaFactory.release(classFileTransformer), CoreMatchers.is(false));
            }
        } finally {
            MatcherAssert.assertThat(LambdaFactory.release(initialClassFileTransformer), CoreMatchers.is(true));
        }
    }

    @Test
    public void testDisabledStrategyIsNoOp() throws Exception {
        ByteBuddy byteBuddy = Mockito.mock(ByteBuddy.class);
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        ClassFileTransformer classFileTransformer = Mockito.mock(ClassFileTransformer.class);
        DISABLED.apply(byteBuddy, instrumentation, classFileTransformer);
        Mockito.verifyZeroInteractions(byteBuddy);
        Mockito.verifyZeroInteractions(instrumentation);
        Mockito.verifyZeroInteractions(classFileTransformer);
    }

    @Test
    public void testEnabledIsInstrumented() throws Exception {
        MatcherAssert.assertThat(ENABLED.isInstrumented(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(ENABLED.isInstrumented(null), CoreMatchers.is(true));
    }

    @Test
    public void testDisabledIsInstrumented() throws Exception {
        MatcherAssert.assertThat(DISABLED.isInstrumented(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(DISABLED.isInstrumented(null), CoreMatchers.is(true));
    }
}

