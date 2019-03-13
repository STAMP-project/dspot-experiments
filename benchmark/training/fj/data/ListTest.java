package fj.data;


import Equal.intEqual;
import fj.Equal;
import fj.P2;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 16/01/2015.
 */
public class ListTest {
    @Test
    public void objectMethods() {
        int max = 5;
        List<Integer> list = List.range(1, max);
        Assert.assertTrue(list.equals(list));
        Assert.assertTrue(list.equals(List.range(1, max)));
        Assert.assertFalse(list.equals(List.single(1)));
        Assert.assertFalse(list.equals(true));
        Assert.assertFalse(list.equals(null));
        Assert.assertTrue(List.list(1, 2).toString().equals("List(1,2)"));
    }

    @Test
    public void integration() {
        java.util.List<Integer> ul = Arrays.asList(1, 2, 3);
        List<Integer> dl = List.iterableList(ul);
        Assert.assertTrue(ul.equals(dl.toJavaList()));
    }

    @Test
    public void convertToString() {
        final int n = 10000;
        final StringBuilder expected = new StringBuilder("List(");
        for (int i = 0; i < n; i++) {
            expected.append(i);
            if (i < (n - 1)) {
                expected.append(',');
            }
        }
        expected.append(')');
        Assert.assertEquals(expected.toString(), List.range(0, n).toString());
    }

    @Test
    public void partition() {
        P2<List<Integer>, List<Integer>> p = List.range(1, 5).partition(( i) -> (i % 2) == 0);
        Equal<List<Integer>> e = Equal.listEqual(intEqual);
        Assert.assertTrue(e.eq(p._1(), List.list(2, 4)));
        Assert.assertTrue(e.eq(p._2(), List.list(1, 3)));
    }

    @Test
    public void intersperseOverflow() {
        // should not overflow
        int n = 100000;
        List<Integer> list = List.replicate(n, 1).intersperse(2);
        String s = list.toString();
    }

    @Test
    public void listReduce() {
        String list = List.range(1, 11).uncons(( a, la) -> List.cons(a, la).toString(), "");
        String expected = List.range(1, 11).toString();
        Assert.assertThat(expected, CoreMatchers.equalTo(list));
    }

    @Test
    public void array() {
        final int max = 3;
        Integer[] ints = new Integer[max];
        for (int i = 0; i < max; i++) {
            ints[i] = i + 1;
        }
        Assert.assertThat(List.range(1, (max + 1)).array(Integer[].class), CoreMatchers.equalTo(ints));
    }
}

