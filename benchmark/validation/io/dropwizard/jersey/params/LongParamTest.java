package io.dropwizard.jersey.params;


import io.dropwizard.jersey.errors.ErrorMessage;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;


public class LongParamTest {
    @Test
    public void aLongReturnsALong() {
        final LongParam param = new LongParam("200");
        assertThat(param.get()).isEqualTo(200L);
    }

    @Test
    public void nullThrowsAnException() {
        assertThatThrownBy(() -> new LongParam(null)).isInstanceOfSatisfying(WebApplicationException.class, ( e) -> {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity()).isEqualTo(new ErrorMessage(400, "Parameter is not a number."));
        });
    }

    @Test
    public void emptyStringThrowsAnException() {
        assertThatThrownBy(() -> new LongParam("")).isInstanceOfSatisfying(WebApplicationException.class, ( e) -> {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity()).isEqualTo(new ErrorMessage(400, "Parameter is not a number."));
        });
    }

    @Test
    public void aNonIntegerThrowsAnException() {
        assertThatThrownBy(() -> new LongParam("foo")).isInstanceOfSatisfying(WebApplicationException.class, ( e) -> {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity()).isEqualTo(new ErrorMessage(400, "Parameter is not a number."));
        });
    }

    @Test
    public void aNonIntegerThrowsAnExceptionWithCustomName() {
        assertThatThrownBy(() -> new LongParam("foo", "customName")).isInstanceOfSatisfying(WebApplicationException.class, ( e) -> {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity()).isEqualTo(new ErrorMessage(400, "customName is not a number."));
        });
    }
}

