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
public class MultipleScheduleTimerBeanTest {
    private static final long TIMEOUT = 0L;

    private static final long TOLERANCE = 4000L;

    @Inject
    private PingsListener pings;

    @Test
    public void should_receive_three_pings() {
        await().untilCall(to(pings.getPings()).size(), Matchers.greaterThanOrEqualTo(3));
        Ping firstPing = pings.getPings().get(0);
        Ping secondPing = pings.getPings().get(1);
        Ping thirdPing = pings.getPings().get(2);
        long timeBetweenFirstAndSecondPing = (secondPing.getTime()) - (firstPing.getTime());
        System.out.println(("Actual timeout = " + timeBetweenFirstAndSecondPing));
        long timeBetweenSecondAndThirdPing = (thirdPing.getTime()) - (secondPing.getTime());
        System.out.println(("Actual timeout = " + timeBetweenSecondAndThirdPing));
        long smallerDelay = Math.min(timeBetweenFirstAndSecondPing, timeBetweenSecondAndThirdPing);
        // Note; this is quite sensitive to slow CI systems.
        MatcherAssert.assertThat(smallerDelay, Matchers.is(WithinWindowMatcher.withinWindow(MultipleScheduleTimerBeanTest.TIMEOUT, MultipleScheduleTimerBeanTest.TOLERANCE)));
    }
}

