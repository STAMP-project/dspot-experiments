package io.dropwizard.jersey.params;


import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.util.Duration;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;


public class DurationParamTest {
    @Test
    public void parseDurationSeconds() {
        final DurationParam param = new DurationParam("10 seconds");
        assertThat(param.get()).isEqualTo(Duration.seconds(10));
    }

    @Test
    public void badValueThrowsException() {
        assertThatThrownBy(() -> new DurationParam("invalid", "param_name")).isInstanceOfSatisfying(WebApplicationException.class, ( e) -> {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity()).isEqualTo(new ErrorMessage(400, "param_name is not a valid duration."));
        });
    }
}

