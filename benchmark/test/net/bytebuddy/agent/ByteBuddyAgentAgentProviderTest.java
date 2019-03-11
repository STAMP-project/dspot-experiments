package net.bytebuddy.agent;


import java.io.File;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.ByteBuddyAgent.AgentProvider.ForByteBuddyAgent.INSTANCE;


public class ByteBuddyAgentAgentProviderTest {
    private static final String FOO = "foo";

    @Test
    public void testKnownAgent() throws Exception {
        File agent = Mockito.mock(File.class);
        Mockito.when(agent.getAbsolutePath()).thenReturn(ByteBuddyAgentAgentProviderTest.FOO);
        ByteBuddyAgent.AgentProvider.ForExistingAgent provider = new ByteBuddyAgent.AgentProvider.ForExistingAgent(agent);
        MatcherAssert.assertThat(provider.resolve(), CoreMatchers.is(agent));
    }

    @Test
    public void testKnownAccessor() throws Exception {
        ByteBuddyAgent.AgentProvider provider = INSTANCE;
        MatcherAssert.assertThat(provider.resolve().isFile(), CoreMatchers.is(true));
    }
}

