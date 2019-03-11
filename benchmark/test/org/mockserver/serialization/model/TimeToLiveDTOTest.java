package org.mockserver.serialization.model;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.matchers.TimeToLive;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimeToLiveDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        TimeToLiveDTO timeToLive = new TimeToLiveDTO(TimeToLive.exactly(TimeUnit.MINUTES, 5L));
        // then
        MatcherAssert.assertThat(timeToLive.getTimeUnit(), Is.is(TimeUnit.MINUTES));
        MatcherAssert.assertThat(timeToLive.getTimeToLive(), Is.is(5L));
        MatcherAssert.assertThat(timeToLive.isUnlimited(), Is.is(false));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        TimeToLive timeToLive = buildObject();
        // then
        MatcherAssert.assertThat(timeToLive.isUnlimited(), Is.is(true));
        // when
        timeToLive = new TimeToLiveDTO(TimeToLive.exactly(TimeUnit.MINUTES, 5L)).buildObject();
        // then
        MatcherAssert.assertThat(timeToLive.getTimeUnit(), Is.is(TimeUnit.MINUTES));
        MatcherAssert.assertThat(timeToLive.getTimeToLive(), Is.is(5L));
        MatcherAssert.assertThat(timeToLive.isUnlimited(), Is.is(false));
    }
}

