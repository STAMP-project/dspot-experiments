package fj.test;


import Equal.intEqual;
import Ord.intOrd;
import Rand.standard;
import fj.data.List;
import fj.data.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 4/06/2015.
 */
public class TestRand {
    @Test
    public void testRandLowHighInclusive() {
        int min = 5;
        int max = 10;
        int n = 100;
        Stream<Integer> s = Stream.range(0, n).map(( i) -> Rand.standard.choose(min, max)).sort(intOrd);
        // System.out.println(s.toList());
        Assert.assertTrue((((s.head()) == min) && ((s.last()) == max)));
    }

    @Test
    public void testReseed() {
        Rand rand1 = standard.reseed(42);
        List<Integer> s1 = List.range(0, 10).map(( i) -> rand1.choose(Integer.MIN_VALUE, Integer.MAX_VALUE));
        Rand rand2 = rand1.reseed(42);
        List<Integer> s2 = List.range(0, 10).map(( i) -> rand2.choose(Integer.MIN_VALUE, Integer.MAX_VALUE));
        Assert.assertTrue(s1.zip(s2).forall(( p) -> p._1().equals(p._2())));
        Assert.assertFalse(s1.allEqual(intEqual));
    }
}

