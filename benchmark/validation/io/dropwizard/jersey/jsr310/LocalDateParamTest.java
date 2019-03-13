package io.dropwizard.jersey.jsr310;


import java.time.LocalDate;
import org.junit.jupiter.api.Test;


public class LocalDateParamTest {
    @Test
    public void parsesDateTimes() throws Exception {
        final LocalDateParam param = new LocalDateParam("2012-11-19");
        assertThat(param.get()).isEqualTo(LocalDate.of(2012, 11, 19));
    }
}

