package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.RawMatcher.Trivial.MATCHING;
import static net.bytebuddy.agent.builder.AgentBuilder.RawMatcher.Trivial.NON_MATCHING;


public class AgentBuilderRawMatcherTrivialTest {
    @Test
    public void testMatches() throws Exception {
        MatcherAssert.assertThat(MATCHING.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Void.class, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(true));
    }

    @Test
    public void testMatchesNot() throws Exception {
        MatcherAssert.assertThat(NON_MATCHING.matches(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), Mockito.mock(JavaModule.class), Void.class, Mockito.mock(ProtectionDomain.class)), CoreMatchers.is(false));
    }
}

