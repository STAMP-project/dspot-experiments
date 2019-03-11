package net.bytebuddy.agent.builder;


import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.agent.builder.AgentBuilder.CircularityLock.Inactive.INSTANCE;


public class AgentBuilderCircularityLockTest {
    @Test
    public void testCircularityLockDefault() throws Exception {
        AgentBuilder.CircularityLock.Default circularityLock = new AgentBuilder.CircularityLock.Default();
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(false));
        circularityLock.release();
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(false));
        circularityLock.release();
        MatcherAssert.assertThat(circularityLock.get(), CoreMatchers.nullValue(Boolean.class));
    }

    @Test
    public void testCircularityLockInactive() throws Exception {
        AgentBuilder.CircularityLock circularityLock = INSTANCE;
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        circularityLock.release();
    }

    @Test
    public void testGlobalLock() throws Exception {
        AgentBuilder.CircularityLock circularityLock = new AgentBuilder.CircularityLock.Global();
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        circularityLock.release();
    }

    @Test
    public void testGlobalLockWithTimeout() throws Exception {
        AgentBuilder.CircularityLock circularityLock = new AgentBuilder.CircularityLock.Global(10, TimeUnit.MILLISECONDS);
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        MatcherAssert.assertThat(circularityLock.acquire(), CoreMatchers.is(true));
        circularityLock.release();
    }
}

