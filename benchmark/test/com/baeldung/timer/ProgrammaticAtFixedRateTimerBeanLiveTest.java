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
public class ProgrammaticAtFixedRateTimerBeanLiveTest {
    static final long TIMEOUT = 1000;

    static final long TOLERANCE = 500L;

    @Inject
    TimerEventListener timerEventListener;

    @Test
    public void should_receive_ten_pings() {
        Awaitility.setDefaultTimeout(30, TimeUnit.SECONDS);
        await().untilCall(to(timerEventListener.getEvents()).size(), Matchers.equalTo(10));
        TimerEvent firstEvent = timerEventListener.getEvents().get(0);
        TimerEvent secondEvent = timerEventListener.getEvents().get(1);
        long delay = (secondEvent.getTime()) - (firstEvent.getTime());
        System.out.println(("Actual timeout = " + delay));
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ProgrammaticAtFixedRateTimerBeanLiveTest.TIMEOUT, ProgrammaticAtFixedRateTimerBeanLiveTest.TOLERANCE)));
    }
}

