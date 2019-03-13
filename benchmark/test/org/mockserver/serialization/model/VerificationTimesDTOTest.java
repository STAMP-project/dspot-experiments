package org.mockserver.serialization.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.verify.VerificationTimes;


public class VerificationTimesDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        VerificationTimesDTO times = new VerificationTimesDTO(VerificationTimes.between(1, 2));
        // then
        MatcherAssert.assertThat(times.getAtLeast(), Is.is(1));
        MatcherAssert.assertThat(times.getAtMost(), Is.is(2));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        VerificationTimes times = buildObject();
        // then
        MatcherAssert.assertThat(times.getAtLeast(), Is.is(1));
        MatcherAssert.assertThat(times.getAtMost(), Is.is(1));
        // when
        times = new VerificationTimesDTO(VerificationTimes.exactly(2)).buildObject();
        // then
        MatcherAssert.assertThat(times.getAtLeast(), Is.is(2));
        MatcherAssert.assertThat(times.getAtMost(), Is.is(2));
        // when
        times = new VerificationTimesDTO(VerificationTimes.atLeast(3)).buildObject();
        // then
        MatcherAssert.assertThat(times.getAtLeast(), Is.is(3));
        MatcherAssert.assertThat(times.getAtMost(), Is.is((-1)));
        // when
        times = new VerificationTimesDTO(VerificationTimes.atMost(4)).buildObject();
        // then
        MatcherAssert.assertThat(times.getAtLeast(), Is.is((-1)));
        MatcherAssert.assertThat(times.getAtMost(), Is.is(4));
    }
}

