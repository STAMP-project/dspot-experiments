package org.mockserver.verify;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationTimesTest {
    @Test
    public void shouldCreateCorrectObjectForAtLeast() {
        // when
        VerificationTimes.VerificationTimes times = atLeast(2);
        // then
        MatcherAssert.assertThat(times.getAtLeast(), CoreMatchers.is(2));
        MatcherAssert.assertThat(times.getAtMost(), CoreMatchers.is((-1)));
    }

    @Test
    public void shouldCreateCorrectObjectForAtMost() {
        // when
        VerificationTimes.VerificationTimes times = atMost(2);
        // then
        MatcherAssert.assertThat(times.getAtLeast(), CoreMatchers.is((-1)));
        MatcherAssert.assertThat(times.getAtMost(), CoreMatchers.is(2));
    }

    @Test
    public void shouldCreateCorrectObjectForOnce() {
        // when
        VerificationTimes.VerificationTimes times = once();
        // then
        MatcherAssert.assertThat(times.getAtLeast(), CoreMatchers.is(1));
        MatcherAssert.assertThat(times.getAtMost(), CoreMatchers.is(1));
    }

    @Test
    public void shouldCreateCorrectObjectForExactly() {
        // when
        VerificationTimes.VerificationTimes times = exactly(2);
        // then
        MatcherAssert.assertThat(times.getAtLeast(), CoreMatchers.is(2));
        MatcherAssert.assertThat(times.getAtMost(), CoreMatchers.is(2));
    }

    @Test
    public void shouldCreateCorrectObjectForBetween() {
        // when
        VerificationTimes.VerificationTimes times = between(1, 2);
        // then
        MatcherAssert.assertThat(times.getAtLeast(), CoreMatchers.is(1));
        MatcherAssert.assertThat(times.getAtMost(), CoreMatchers.is(2));
    }

    @Test
    public void shouldMatchBetweenCorrectly() {
        // when
        VerificationTimes.VerificationTimes times = between(1, 2);
        // then
        MatcherAssert.assertThat(times.matches(0), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(2), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(3), CoreMatchers.is(false));
    }

    @Test
    public void shouldMatchExactCorrectly() {
        // when
        VerificationTimes.VerificationTimes times = exactly(2);
        // then
        MatcherAssert.assertThat(times.matches(0), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(2), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(3), CoreMatchers.is(false));
    }

    @Test
    public void shouldMatchAtLeastCorrectly() {
        // when
        VerificationTimes.VerificationTimes times = atLeast(2);
        // then
        MatcherAssert.assertThat(times.matches(0), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(2), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(3), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchAtMostCorrectly() {
        // when
        VerificationTimes.VerificationTimes times = atMost(2);
        // then
        MatcherAssert.assertThat(times.matches(0), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(2), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(3), CoreMatchers.is(false));
    }

    @Test
    public void shouldMatchAtMostZeroCorrectly() {
        // when
        VerificationTimes.VerificationTimes times = atMost(0);
        // then
        MatcherAssert.assertThat(times.matches(0), CoreMatchers.is(true));
        MatcherAssert.assertThat(times.matches(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(2), CoreMatchers.is(false));
        MatcherAssert.assertThat(times.matches(3), CoreMatchers.is(false));
    }

    @Test
    public void shouldGenerateCorrectToString() {
        // then
        MatcherAssert.assertThat(once().toString(), CoreMatchers.is("exactly once"));
        MatcherAssert.assertThat(exactly(0).toString(), CoreMatchers.is("exactly 0 times"));
        MatcherAssert.assertThat(exactly(1).toString(), CoreMatchers.is("exactly once"));
        MatcherAssert.assertThat(exactly(2).toString(), CoreMatchers.is("exactly 2 times"));
        MatcherAssert.assertThat(atLeast(0).toString(), CoreMatchers.is("at least 0 times"));
        MatcherAssert.assertThat(atLeast(1).toString(), CoreMatchers.is("at least once"));
        MatcherAssert.assertThat(atLeast(2).toString(), CoreMatchers.is("at least 2 times"));
        MatcherAssert.assertThat(atMost(0).toString(), CoreMatchers.is("at most 0 times"));
        MatcherAssert.assertThat(atMost(1).toString(), CoreMatchers.is("at most once"));
        MatcherAssert.assertThat(atMost(2).toString(), CoreMatchers.is("at most 2 times"));
        MatcherAssert.assertThat(between(1, 2).toString(), CoreMatchers.is("between 1 and 2 times"));
        MatcherAssert.assertThat(between(1, 1).toString(), CoreMatchers.is("exactly once"));
        MatcherAssert.assertThat(between(2, 2).toString(), CoreMatchers.is("exactly 2 times"));
    }
}

