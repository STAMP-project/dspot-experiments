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
public class ProgrammaticTimerBeanTest {
    private static final long TIMEOUT = 5000L;

    private static final long TOLERANCE = 1000L;

    @Inject
    private PingsListener pings;

    @Test
    public void should_receive_two_pings() {
        await().untilCall(to(pings.getPings()).size(), Matchers.greaterThanOrEqualTo(2));
        Ping firstPing = pings.getPings().get(0);
        Ping secondPing = pings.getPings().get(1);
        long delay = (secondPing.getTime()) - (firstPing.getTime());
        System.out.println(("Actual timeout = " + delay));
        MatcherAssert.assertThat(delay, Matchers.is(WithinWindowMatcher.withinWindow(ProgrammaticTimerBeanTest.TIMEOUT, ProgrammaticTimerBeanTest.TOLERANCE)));
    }
}

