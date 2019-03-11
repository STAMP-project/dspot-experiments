package org.mockserver.matchers;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimeToLiveTest {
    @Test
    public void shouldCreateCorrectObjects() {
        // when
        MatcherAssert.assertThat(TimeToLive.unlimited().isUnlimited(), Is.is(true));
        MatcherAssert.assertThat(TimeToLive.exactly(TimeUnit.MINUTES, 5L).isUnlimited(), Is.is(false));
        MatcherAssert.assertThat(TimeToLive.exactly(TimeUnit.MINUTES, 5L).getTimeUnit(), Is.is(TimeUnit.MINUTES));
        MatcherAssert.assertThat(TimeToLive.exactly(TimeUnit.MINUTES, 5L).getTimeToLive(), Is.is(5L));
    }

    @Test
    public void shouldCalculateStillLive() throws InterruptedException {
        // when
        TimeToLive timeToLive = TimeToLive.exactly(TimeUnit.MILLISECONDS, 0L);
        TimeUnit.MILLISECONDS.sleep(5);
        // then
        MatcherAssert.assertThat(timeToLive.stillAlive(), Is.is(false));
        MatcherAssert.assertThat(TimeToLive.exactly(TimeUnit.MINUTES, 10L).stillAlive(), Is.is(true));
    }
}

