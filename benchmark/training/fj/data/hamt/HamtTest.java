package fj.data.hamt;


import fj.Equal;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Mark Perry
 */
public class HamtTest {
    public static final HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.emptyKeyInteger();

    @Test
    public void empty() {
        Assert.assertThat(HamtTest.empty.length(), CoreMatchers.equalTo(0));
    }

    @Test
    public void lengthOne() {
        Assert.assertThat(HamtTest.empty.set(3, 6).length(), CoreMatchers.equalTo(1));
    }

    @Test
    public void updateLength() {
        HashArrayMappedTrie<Integer, Integer> h1 = HamtTest.empty.set(3, 3).set(3, 5);
        Assert.assertThat(h1.length(), CoreMatchers.equalTo(1));
    }

    @Test
    public void streamLength() {
        List<P2<Integer, Integer>> list = List.list(P.p(0, 1), P.p(31, 1), P.p(32, 1), P.p(33, 1));
        HashArrayMappedTrie<Integer, Integer> h2 = HamtTest.empty.set(list);
        Assert.assertThat(h2.toStream().length(), CoreMatchers.equalTo(list.length()));
    }

    @Test
    public void allIn() {
        List<P2<Integer, Integer>> list = List.list(P.p((-5), 0), P.p((-1), (-5)), P.p(2, 4), P.p(4, (-2)));
        HashArrayMappedTrie<Integer, Integer> h = HamtTest.empty.set(list);
        Boolean b = list.foldLeft(( acc, p) -> h.find(p._1()).option(false, ( i) -> true && acc), true);
        Assert.assertThat(b, CoreMatchers.equalTo(true));
    }

    @Test
    public void sampleInts() {
        List<P2<Integer, Integer>> ps = List.list(P.p((-3), 0), P.p(1, 2));
        int key = -3;
        HashArrayMappedTrie<Integer, Integer> h = HamtTest.empty.set(ps);
        Option<Integer> o1 = ps.find(( p) -> Equal.intEqual.eq(p._1(), key)).map(( p) -> p._2());
        Option<Integer> o2 = h.find(key);
        boolean b = Equal.optionEqual(Equal.intEqual).eq(o1, o2);
        Assert.assertThat(b, CoreMatchers.equalTo(true));
    }
}

