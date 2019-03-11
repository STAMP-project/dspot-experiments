package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class AgentBuilderClassFileBufferStrategyTest {
    @Test
    public void testRetainingClassFileBufferStrategy() throws Exception {
        ClassFileLocator classFileLocator = ClassFileBufferStrategy.resolve("foo", new byte[]{ 123 }, Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Mockito.mock(ProtectionDomain.class));
        MatcherAssert.assertThat(classFileLocator.locate("foo").isResolved(), Is.is(true));
        MatcherAssert.assertThat(classFileLocator.locate("bar").isResolved(), Is.is(false));
    }

    @Test
    public void testDiscardingClassFileBufferStrategy() throws Exception {
        ClassFileLocator classFileLocator = ClassFileBufferStrategy.resolve("foo", new byte[]{ 123 }, Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Mockito.mock(ProtectionDomain.class));
        MatcherAssert.assertThat(classFileLocator.locate("foo").isResolved(), Is.is(false));
        MatcherAssert.assertThat(classFileLocator.locate("bar").isResolved(), Is.is(false));
    }
}

