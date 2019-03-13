package io.dropwizard.jersey.jsr310;


import java.time.Instant;
import org.junit.jupiter.api.Test;


public class InstantSecondParamTest {
    @Test
    public void parsesInstants() throws Exception {
        final InstantSecondParam param = new InstantSecondParam("1488752017");
        assertThat(param.get()).isEqualTo(Instant.ofEpochSecond(1488752017L));
    }
}

