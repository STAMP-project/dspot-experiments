package org.mockserver.serialization.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.matchers.Times;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimesDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        org.mockserver.serialization.model.TimesDTO times = new org.mockserver.serialization.model.TimesDTO(Times.exactly(5));
        // then
        MatcherAssert.assertThat(times.getRemainingTimes(), Is.is(5));
        MatcherAssert.assertThat(times.isUnlimited(), Is.is(false));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        Times times = buildObject();
        // then
        MatcherAssert.assertThat(times.isUnlimited(), Is.is(true));
        // when
        times = new org.mockserver.serialization.model.TimesDTO(Times.exactly(5)).buildObject();
        // then
        MatcherAssert.assertThat(times.getRemainingTimes(), Is.is(5));
        MatcherAssert.assertThat(times.isUnlimited(), Is.is(false));
    }
}

