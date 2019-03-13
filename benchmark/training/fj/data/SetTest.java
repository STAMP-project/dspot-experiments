package fj.data;


import fj.Ord;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 18/08/2015.
 */
public class SetTest {
    @Test
    public void toStream() {
        Set<Integer> s = Set.set(Ord.intOrd, 1, 2, 3);
        Assert.assertThat(s.toStream(), CoreMatchers.equalTo(Stream.stream(1, 2, 3)));
    }

    @Test
    public void testString() {
        Set<Integer> s = Set.set(Ord.intOrd, 1, 2, 3);
        Assert.assertThat(s.toString(), CoreMatchers.equalTo("Set(1,2,3)"));
    }

    @Test
    public void testLookups() {
        Set<Integer> s = Set.set(Ord.intOrd, 5, 1, 7, 8);
        Assert.assertThat(s.lookup(7), CoreMatchers.equalTo(Option.some(7)));
        Assert.assertThat(s.lookup(4), CoreMatchers.equalTo(Option.none()));
        Assert.assertThat(s.lookupLT(6), CoreMatchers.equalTo(Option.some(5)));
        Assert.assertThat(s.lookupLT(1), CoreMatchers.equalTo(Option.none()));
        Assert.assertThat(s.lookupGT(5), CoreMatchers.equalTo(Option.some(7)));
        Assert.assertThat(s.lookupGT(9), CoreMatchers.equalTo(Option.none()));
        Assert.assertThat(s.lookupLE(8), CoreMatchers.equalTo(Option.some(8)));
        Assert.assertThat(s.lookupLE(0), CoreMatchers.equalTo(Option.none()));
        Assert.assertThat(s.lookupGE(8), CoreMatchers.equalTo(Option.some(8)));
        Assert.assertThat(s.lookupGE(9), CoreMatchers.equalTo(Option.none()));
    }
}

