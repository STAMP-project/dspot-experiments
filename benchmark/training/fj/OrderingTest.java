package fj;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class OrderingTest {
    @Test
    public void reverse() throws Exception {
        Assert.assertThat(Ordering.GT.reverse(), Is.is(Ordering.LT));
        Assert.assertThat(Ordering.LT.reverse(), Is.is(Ordering.GT));
        Assert.assertThat(Ordering.EQ.reverse(), Is.is(Ordering.EQ));
    }
}

