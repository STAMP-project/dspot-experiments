package io.dropwizard.jersey.params;


import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;


public class DateTimeParamTest {
    @Test
    public void parsesDateTimes() {
        final DateTimeParam param = new DateTimeParam("2012-11-19");
        assertThat(param.get()).isEqualTo(new org.joda.time.DateTime(2012, 11, 19, 0, 0, DateTimeZone.UTC));
    }
}

