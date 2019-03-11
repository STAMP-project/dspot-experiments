package io.dropwizard.jersey.jsr310;


import java.time.Year;
import org.junit.jupiter.api.Test;


public class YearParamTest {
    @Test
    public void parsesDateTimes() throws Exception {
        final YearParam param = new YearParam("2012");
        assertThat(param.get()).isEqualTo(Year.of(2012));
    }
}

