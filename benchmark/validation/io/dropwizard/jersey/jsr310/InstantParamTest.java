package io.dropwizard.jersey.jsr310;


import java.time.Instant;
import org.junit.jupiter.api.Test;


public class InstantParamTest {
    @Test
    public void parsesInstants() throws Exception {
        final InstantParam param = new InstantParam("1488751730055");
        assertThat(param.get()).isEqualTo(Instant.ofEpochMilli(1488751730055L));
    }
}

