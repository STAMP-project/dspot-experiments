package fj.data.optic;


import fj.data.Option;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class PrismTest {
    @Test
    public void testPrismSome() {
        Prism<String, Integer> prism = Prism.prism(( s) -> decode(s), ( i) -> i.toString());
        Assert.assertThat(prism.getOption("18"), Is.is(Option.some(18)));
    }

    @Test
    public void testPrismNone() {
        Prism<String, Integer> prism = Prism.prism(( s) -> decode(s), ( i) -> i.toString());
        Assert.assertThat(prism.getOption("Z"), Is.is(Option.none()));
    }
}

