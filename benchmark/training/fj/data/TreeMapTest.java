package fj.data;


import Equal.intEqual;
import Equal.stringEqual;
import Ord.intOrd;
import Ord.stringOrd;
import Show.intShow;
import Show.stringShow;
import fj.Equal;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.P3;
import fj.Show;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 11/01/2015.
 */
public class TreeMapTest {
    @Test
    public void split() {
        // do the split
        int pivot = 4;
        int max = 5;
        List<Integer> l = List.range(1, (max + 1));
        TreeMap<Integer, String> m2 = TreeMap.iterableTreeMap(intOrd, l.zip(l.map(( i) -> i.toString())));
        P3<Set<String>, Option<String>, Set<String>> p = m2.split(stringOrd, pivot);
        // print debug info
        Show<TreeMap<Integer, String>> st = Show.treeMapShow(intShow, stringShow);
        Show<Set<String>> ss = Show.setShow(stringShow);
        Show<Option<String>> so = Show.optionShow(stringShow);
        Show<P3<Set<String>, Option<String>, Set<String>>> sp3 = Show.p3Show(ss, so, ss);
        // assert equals
        Equal<Set<String>> seq = Equal.setEqual(stringEqual);
        Set<String> left = TreeMapTest.toSetString(List.range(1, pivot));
        Set<String> right = TreeMapTest.toSetString(List.range((pivot + 1), (max + 1)));
        P3<Set<String>, Option<String>, Set<String>> expected = P.p(left, Option.some(Integer.toString(pivot)), right);
        Assert.assertTrue(Equal.p3Equal(seq, Equal.optionEqual(stringEqual), seq).eq(p, expected));
    }

    @Test
    public void splitLookup() {
        // do the split
        int pivot = 4;
        int max = 5;
        List<Integer> l = List.range(1, (max + 1));
        TreeMap<Integer, String> m2 = TreeMap.iterableTreeMap(intOrd, l.zip(l.map(( i) -> i.toString())));
        P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>> p3 = m2.splitLookup(pivot);
        // create expected output
        List<Integer> leftList = List.range(1, pivot);
        TreeMap<Integer, String> leftMap = TreeMap.iterableTreeMap(intOrd, leftList.zip(leftList.map(( i) -> i.toString())));
        List<Integer> rightList = List.range((pivot + 1), (max + 1));
        TreeMap<Integer, String> rightMap = TreeMap.iterableTreeMap(intOrd, rightList.zip(rightList.map(( i) -> i.toString())));
        // do the assert
        Equal<TreeMap<Integer, String>> tme = Equal.treeMapEqual(intEqual, stringEqual);
        Equal<P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>>> eq = Equal.p3Equal(tme, Equal.optionEqual(stringEqual), tme);
        Assert.assertTrue(eq.eq(p3, P.p(leftMap, Option.some(Integer.toString(pivot)), rightMap)));
    }

    @Test
    public void toMutableMap() {
        int max = 5;
        List<List<Integer>> l = List.range(1, (max + 1)).map(( n) -> List.single(n));
        TreeMap<List<Integer>, String> m2 = TreeMap.iterableTreeMap(Ord.listOrd(intOrd), l.zip(l.map(( i) -> i.toString())));
        Map<List<Integer>, String> mm = m2.toMutableMap();
        Assert.assertEquals(m2.keys(), List.iterableList(mm.keySet()));
    }

    @Test
    public void testLargeInserts() {
        // check that inserting a large number of items performs ok
        // taken from https://code.google.com/p/functionaljava/issues/detail?id=31 and
        // https://github.com/functionaljava/functionaljava/pull/13/files
        final int n = 10000;
        TreeMap<Integer, String> m = TreeMap.empty(intOrd);
        for (int i = 0; i < n; i++) {
            m = m.set(i, ("abc " + i));
        }
    }

    @Test
    public void testString() {
        TreeMap<Integer, String> t = TreeMap.treeMap(intOrd, P.p(1, "a"), P.p(2, "b"), P.p(3, "c"));
        TreeMap<Integer, String> t2 = TreeMap.treeMap(intOrd, P.p(3, "c"), P.p(2, "b"), P.p(1, "a"));
        Stream<P2<Integer, String>> s = Stream.stream(P.p(1, "a"), P.p(2, "b"), P.p(3, "c"));
        Assert.assertThat(t.toStream(), CoreMatchers.equalTo(s));
        Assert.assertThat(t2.toStream(), CoreMatchers.equalTo(s));
    }

    @Test
    public void minKey() {
        TreeMap<Integer, Integer> t1 = TreeMap.<Integer, Integer>empty(intOrd);
        Assert.assertThat(t1.minKey(), CoreMatchers.equalTo(Option.none()));
        TreeMap<Integer, Integer> t2 = t1.set(1, 2).set(2, 4).set(10, 20).set(5, 10).set(0, 100);
        Assert.assertThat(t2.minKey(), CoreMatchers.equalTo(Option.some(0)));
        Assert.assertThat(t2.delete(0).minKey(), CoreMatchers.equalTo(Option.some(1)));
    }

    @Test
    public void emptyHashCode() {
        // Hash code of tree map should not throw NullPointerException
        // see https://github.com/functionaljava/functionaljava/issues/187
        int i = TreeMap.empty(stringOrd).hashCode();
        Assert.assertTrue(true);
    }
}

