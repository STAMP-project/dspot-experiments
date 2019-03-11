package net.bytebuddy.agent.builder;


import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AgentBuilderLocationStrategySimpleTest {
    @Test
    public void testLocation() throws Exception {
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Assert.assertThat(new AgentBuilder.LocationStrategy.Simple(classFileLocator).classFileLocator(Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class)), CoreMatchers.is(classFileLocator));
    }
}

