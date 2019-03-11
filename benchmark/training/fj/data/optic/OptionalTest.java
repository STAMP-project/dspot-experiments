package fj.data.optic;


import fj.data.Option;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class OptionalTest {
    @Test
    public void testOptionalSome() {
        Optional<String, Integer> o = Optional.optional(this::decode, ( i) -> ( s) -> s);
        Assert.assertThat(o.getOption("18"), Is.is(Option.some(18)));
    }

    @Test
    public void testOptionalNone() {
        Optional<String, Integer> o = Optional.optional(this::decode, ( i) -> ( s) -> s);
        Assert.assertThat(o.getOption("Z"), Is.is(Option.none()));
    }
}

