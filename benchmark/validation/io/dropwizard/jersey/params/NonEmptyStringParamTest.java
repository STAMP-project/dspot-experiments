package io.dropwizard.jersey.params;


import java.util.Optional;
import org.junit.jupiter.api.Test;


public class NonEmptyStringParamTest {
    @Test
    public void aBlankStringIsAnAbsentString() {
        final NonEmptyStringParam param = new NonEmptyStringParam("");
        assertThat(param.get()).isEqualTo(Optional.empty());
    }

    @Test
    public void aNullStringIsAnAbsentString() {
        final NonEmptyStringParam param = new NonEmptyStringParam(null);
        assertThat(param.get()).isEqualTo(Optional.empty());
    }

    @Test
    public void aStringWithContentIsItself() {
        final NonEmptyStringParam param = new NonEmptyStringParam("hello");
        assertThat(param.get()).isEqualTo(Optional.of("hello"));
    }
}

