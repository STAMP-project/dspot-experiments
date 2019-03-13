package io.dropwizard.jersey.jsr310;


import java.time.ZoneId;
import org.junit.jupiter.api.Test;


public class ZoneIdParamTest {
    @Test
    public void parsesDateTimes() throws Exception {
        final ZoneIdParam param = new ZoneIdParam("Europe/Berlin");
        assertThat(param.get()).isEqualTo(ZoneId.of("Europe/Berlin"));
    }
}

