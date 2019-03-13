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
public class ScheduleTimerBeanLiveTest {
    private static final long TIMEOUT = 5000L;

    private static final long TOLERANCE = 1000L;

    @Inject
    TimerEventListener timerEventListener;

    @Test
    public void should_receive_three_pings() {
        Awaitility.setDefaultTimeout(30, TimeUnit.SECONDS);
        await().untilCall(to(timerEventListener.getEvents()).size(), Matchers.equalTo(3));
        TimerEvent firstEvent = timerEventListener.getEvents().get(0);
        TimerEvent secondEvent = timerEventListener.getEvents().get(1);
        TimerEvent thirdEvent = timerEventListener.getEvents().get(2);
        long delay = (secondEvent.getTime()) - (firstEvent.getTime());
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ScheduleTimerBeanLiveTest.TIMEOUT, ScheduleTimerBeanLiveTest.TOLERANCE)));
        delay = (thirdEvent.getTime()) - (secondEvent.getTime());
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ScheduleTimerBeanLiveTest.TIMEOUT, ScheduleTimerBeanLiveTest.TOLERANCE)));
    }
}

