package fj;


import Semigroup.intAdditionSemigroup;
import fj.data.Option;
import fj.data.Stream;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class MonoidTest {
    @Test
    public void lifted_sum_of_two_numbers() {
        Monoid<Option<Integer>> optionMonoid = intAdditionSemigroup.lift();
        Assert.assertThat(optionMonoid.sum(Option.some(3), Option.some(5)), Is.is(Option.some(8)));
        Assert.assertThat(optionMonoid.sumLeft(Stream.arrayStream(Option.some(3), Option.some(5))), Is.is(Option.some(8)));
    }
}

