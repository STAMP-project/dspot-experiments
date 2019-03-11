package fj.data;


import fj.P2;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 16/01/2015.
 */
public class SeqTest {
    @Test
    public void objectMethods() {
        Seq<Integer> s1 = Seq.seq(1, 2, 3);
        Seq<Integer> s2 = Seq.seq(1, 2, 3);
        Assert.assertTrue(s1.toString().equals("Seq(1,2,3)"));
        Assert.assertTrue(s1.equals(s2));
        Assert.assertFalse((s1 == s2));
    }

    @Test
    public void convertToString() {
        final int n = 10000;
        final StringBuilder expected = new StringBuilder("Seq(");
        for (int i = 0; i < n; i++) {
            expected.append(i);
            if (i < (n - 1)) {
                expected.append(',');
            }
        }
        expected.append(')');
        Assert.assertEquals(expected.toString(), Seq.seq(Array.range(0, 10000).array()).toString());
    }

    @Test
    public void test() {
        P2<Seq<Integer>, Seq<Integer>> p2 = Seq.single(1).split(5);
        MatcherAssert.assertThat(p2._1(), Is.is(Seq.single(1)));
        MatcherAssert.assertThat(p2._2(), Is.is(Seq.empty()));
    }
}

