package com.baeldung.timer;


import com.jayway.awaitility.Awaitility;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ProgrammaticWithFixedDelayTimerBeanLiveTest {
    static final long TIMEOUT = 15000L;

    static final long TOLERANCE = 1000L;

    @Inject
    TimerEventListener timerEventListener;

    @Test
    public void should_receive_two_pings() {
        Awaitility.setDefaultTimeout(30, TimeUnit.SECONDS);
        // 10 seconds pause so we get the startTime and it will trigger first event
        long startTime = System.currentTimeMillis();
        await().untilCall(to(timerEventListener.getEvents()).size(), Matchers.equalTo(2));
        TimerEvent firstEvent = timerEventListener.getEvents().get(0);
        TimerEvent secondEvent = timerEventListener.getEvents().get(1);
        long delay = (secondEvent.getTime()) - startTime;
        System.out.println(("Actual timeout = " + delay));
        // apx 15 seconds = 10 delay + 2 timers (first after a pause of 10 seconds and the next others every 5 seconds)
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ProgrammaticWithFixedDelayTimerBeanLiveTest.TIMEOUT, ProgrammaticWithFixedDelayTimerBeanLiveTest.TOLERANCE)));
    }
}

