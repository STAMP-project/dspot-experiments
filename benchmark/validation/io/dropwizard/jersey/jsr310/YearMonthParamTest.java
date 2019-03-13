package io.dropwizard.jersey.jsr310;


import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;


public class YearMonthParamTest {
    @Test
    public void parsesDateTimes() throws Exception {
        final YearMonthParam param = new YearMonthParam("2012-11");
        assertThat(param.get()).isEqualTo(YearMonth.of(2012, Month.NOVEMBER));
    }
}

