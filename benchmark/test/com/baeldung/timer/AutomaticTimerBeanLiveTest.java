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
public class AutomaticTimerBeanLiveTest {
    // the @AutomaticTimerBean has a method called every 10 seconds
    // testing the difference ==> 100000
    static final long TIMEOUT = 10000L;

    // the tolerance accepted , so if between two consecutive calls there has to be at least 9 or max 11 seconds.
    // because the timer service is not intended for real-time applications so it will not be exactly 10 seconds
    static final long TOLERANCE = 1000L;

    @Inject
    TimerEventListener timerEventListener;

    @Test
    public void should_receive_two_pings() {
        Awaitility.setDefaultTimeout(30, TimeUnit.SECONDS);
        // the test will wait here until two events are triggered
        await().untilCall(to(timerEventListener.getEvents()).size(), Matchers.equalTo(2));
        TimerEvent firstEvent = timerEventListener.getEvents().get(0);
        TimerEvent secondEvent = timerEventListener.getEvents().get(1);
        long delay = (secondEvent.getTime()) - (firstEvent.getTime());
        System.out.println(("Actual timeout = " + delay));
        // ensure that the delay between the events is more or less 10 seconds (no real time precision)
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(AutomaticTimerBeanLiveTest.TIMEOUT, AutomaticTimerBeanLiveTest.TOLERANCE)));
    }
}

