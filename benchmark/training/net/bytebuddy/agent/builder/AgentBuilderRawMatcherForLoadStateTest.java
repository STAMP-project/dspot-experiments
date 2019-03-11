package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.RawMatcher.ForLoadState.LOADED;
import static net.bytebuddy.agent.builder.AgentBuilder.RawMatcher.ForLoadState.UNLOADED;


public class AgentBuilderRawMatcherForLoadStateTest {
    @Test
    public void testLoadedOnLoaded() throws Exception {
        Assert.assertThat(LOADED.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Object.class, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(true));
    }

    @Test
    public void testLoadedOnUnloaded() throws Exception {
        Assert.assertThat(LOADED.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), null, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(false));
    }

    @Test
    public void testUnloadedOnLoaded() throws Exception {
        Assert.assertThat(UNLOADED.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Object.class, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(false));
    }

    @Test
    public void testUnloadedOnUnloaded() throws Exception {
        Assert.assertThat(UNLOADED.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), null, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(true));
    }
}

