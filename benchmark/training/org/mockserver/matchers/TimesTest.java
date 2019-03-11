package org.mockserver.matchers;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimesTest {
    @Test
    public void shouldCreateCorrectObjects() {
        // when
        MatcherAssert.assertThat(Times.unlimited().isUnlimited(), Is.is(true));
        MatcherAssert.assertThat(Times.once().isUnlimited(), Is.is(false));
        MatcherAssert.assertThat(Times.once().getRemainingTimes(), Is.is(1));
        MatcherAssert.assertThat(Times.exactly(5).isUnlimited(), Is.is(false));
        MatcherAssert.assertThat(Times.exactly(5).getRemainingTimes(), Is.is(5));
    }

    @Test
    public void shouldUpdateCountCorrectly() {
        // given
        Times times = Times.exactly(2);
        // then
        MatcherAssert.assertThat(times.greaterThenZero(), Is.is(true));
        times.decrement().decrement();
        MatcherAssert.assertThat(times.greaterThenZero(), Is.is(false));
    }
}

