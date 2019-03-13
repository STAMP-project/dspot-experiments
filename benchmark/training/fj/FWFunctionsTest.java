package fj;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class FWFunctionsTest {
    @Test
    public void testLift1() {
        F<Integer, Integer> f = ( i) -> i + 1;
        F1W<Integer, Integer> f1w = F1W.lift(f);
        Assert.assertThat(f1w.f(1), Is.is(2));
    }

    @Test
    public void testLift2() {
        F2<Integer, Integer, Integer> f2 = ( i, j) -> i + j;
        F2W<Integer, Integer, Integer> f2w = F2W.lift(f2);
        Assert.assertThat(f2w.f(1, 2), Is.is(3));
    }
}

