package org.mockserver.serialization.model;


import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Delay;


/**
 *
 *
 * @author jamesdbloom
 */
public class DelayDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        DelayDTO delay = new DelayDTO(new Delay(TimeUnit.DAYS, 5));
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.DAYS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(5L));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // when
        DelayDTO delay = new DelayDTO();
        delay.setTimeUnit(TimeUnit.DAYS);
        delay.setValue(5);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(TimeUnit.DAYS));
        MatcherAssert.assertThat(delay.getValue(), Is.is(5L));
    }

    @Test
    public void shouldHandleNullInput() {
        // when
        DelayDTO delay = new DelayDTO(null);
        // then
        MatcherAssert.assertThat(delay.getTimeUnit(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(delay.getValue(), Is.is(0L));
    }
}

