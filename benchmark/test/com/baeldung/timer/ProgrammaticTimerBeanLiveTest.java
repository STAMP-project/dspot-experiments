package com.baeldung.timer;


import javax.inject.Inject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ProgrammaticTimerBeanLiveTest {
    static final long TIMEOUT = 5000L;

    static final long TOLERANCE = 1000L;

    @Inject
    TimerEventListener timerEventListener;

    @Test
    public void should_receive_two_pings() {
        await().untilCall(to(timerEventListener.getEvents()).size(), Matchers.equalTo(2));
        TimerEvent firstEvent = timerEventListener.getEvents().get(0);
        TimerEvent secondEvent = timerEventListener.getEvents().get(1);
        long delay = (secondEvent.getTime()) - (firstEvent.getTime());
        System.out.println(("Actual timeout = " + delay));
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ProgrammaticTimerBeanLiveTest.TIMEOUT, ProgrammaticTimerBeanLiveTest.TOLERANCE)));
    }
}

