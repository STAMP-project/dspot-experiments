package org.javaee7.ejb.timer;


import javax.inject.Inject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * author: Jacek Jackowiak
 */
@RunWith(Arquillian.class)
public class SchedulesTimerBeanTest {
    private static final long TIMEOUT = 0L;

    private static final long TOLERANCE = 1000L;

    @Inject
    private PingsListener pings;

    @Test
    public void should_receive_three_pings() {
        await().untilCall(to(pings.getPings()).size(), Matchers.greaterThanOrEqualTo(3));
        Ping firstPing = pings.getPings().get(0);
        Ping secondPing = pings.getPings().get(1);
        Ping thirdPing = pings.getPings().get(2);
        long delay = (secondPing.getTime()) - (firstPing.getTime());
        System.out.println(("Actual timeout = " + delay));
        long delay2 = (thirdPing.getTime()) - (secondPing.getTime());
        System.out.println(("Actual timeout = " + delay2));
        long smallerDelay = Math.min(delay, delay2);
        MatcherAssert.assertThat(smallerDelay, Matchers.is(WithinWindowMatcher.withinWindow(SchedulesTimerBeanTest.TIMEOUT, SchedulesTimerBeanTest.TOLERANCE)));
    }
}

