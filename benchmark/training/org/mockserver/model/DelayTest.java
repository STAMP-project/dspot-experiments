package org.mockserver.model;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class DelayTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        Delay.Delay delay = new Delay.Delay(TimeUnit.DAYS, 5);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.DAYS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(5L));
    }

    @Test
    public void shouldReturnValuesFromMillisecondsStaticBuilder() {
        // when
        Delay.Delay delay = milliseconds(2);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(2L));
    }

    @Test
    public void shouldReturnValuesFromSecondsStaticBuilder() {
        // when
        Delay.Delay delay = seconds(3);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.SECONDS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(3L));
    }

    @Test
    public void shouldReturnValuesFromMinutesStaticBuilder() {
        // when
        Delay.Delay delay = minutes(4);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.MINUTES));
        MatcherAssert.assertThat(delay.getValue(), Is.is(4L));
    }

    @Test
    public void shouldReturnValuesFromDelayStaticBuilder() {
        // when
        Delay.Delay delay = delay(TimeUnit.DAYS, 5);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.DAYS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(5L));
    }
}

