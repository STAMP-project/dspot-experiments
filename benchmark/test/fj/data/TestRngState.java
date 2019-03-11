package fj.data;


import Equal.intEqual;
import fj.test.Property;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by mperry on 4/08/2014.
 */
public class TestRngState {
    static List<Integer> expected1 = List.list(4, 4, 2, 2, 2, 5, 3, 3, 1, 5);

    static int size = 10;

    static final Equal<List<Integer>> listIntEqual = Equal.listEqual(intEqual);

    @Test
    public void testUnfold() {
        Stream<Integer> s = Stream.unfold(( r) -> some(num(r).swap()), TestRngState.defaultRng());
        Assert.assertTrue(TestRngState.listIntEqual.eq(s.take(TestRngState.size).toList(), TestRngState.expected1));
    }

    @Test
    public void testTransitions() {
        P2<List<State<Rng, Integer>>, State<Rng, Integer>> p = List.replicate(TestRngState.size, TestRngState.nextState()).foldLeft((P2<List<State<Rng, Integer>>, State<Rng, Integer>> p2,F<State<Rng, Integer>, State<Rng, Integer>> f) -> {
            State<Rng, Integer> s = f.f(p2._2());
            return P.p(p2._1().snoc(p2._2()), s);
        }, P.p(List.nil(), TestRngState.defaultState()));
        List<Integer> ints = p._1().map(( s) -> s.eval(defaultRng()));
        Assert.assertTrue(TestRngState.listIntEqual.eq(ints, TestRngState.expected1));
    }

    @Test
    public void testSequence() {
        List<Integer> list = State.sequence(List.replicate(TestRngState.size, TestRngState.defaultState())).eval(TestRngState.defaultRng());
        Assert.assertTrue(TestRngState.listIntEqual.eq(list, TestRngState.expected1));
    }

    @Test
    public void testTraverse() {
        List<Integer> list = State.traverse(List.range(1, 10), ( a) -> State.unit((Rng s) -> num(s, a))).eval(TestRngState.defaultRng());
        // System.out.println(list.toString());
        List<Integer> expected = List.list(1, 2, 3, 5, 6, 7, 7, 9, 10);
        Assert.assertTrue(TestRngState.listIntEqual.eq(list, expected));
    }

    // Left identity: return i >>= f == f i
    @Test
    public void testLeftIdentity() {
        Property p = property(TestRngState.arbBindable(), arbInteger, arbLcgRng(), ( f, i, r) -> {
            int a = State.<LcgRng, Integer>constant(i).flatMap(f).eval(r);
            int b = f.f(i).eval(r);
            // System.out.println(String.format("a=%d, b=%d", a, b));
            return prop((a == b));
        });
        assertResult(p);
    }

    // Right identity: m >>= return == m
    @Test
    public void testRightIdentity() {
        Property p = Property.property(TestRngState.arbState(), arbLcgRng(), ( s, r) -> {
            int x = s.flatMap(( a) -> State.constant(a)).eval(r);
            int y = s.eval(r);
            // System.out.println(String.format("x=%d, y=%d", x, y));
            return prop((x == y));
        });
        assertResult(p);
    }

    // Associativity: (m >>= f) >>= g == m >>= (\x -> f x >>= g)
    @Test
    public void testAssociativity() {
        Property p = Property.property(TestRngState.arbState(), TestRngState.arbBindable(), TestRngState.arbBindable(), arbLcgRng(), ( s, f, g, r) -> {
            int t = s.flatMap(f).flatMap(g).eval(r);
            int u = s.flatMap(( x) -> f.f(x).flatMap(g)).eval(r);
            // System.out.println(String.format("x=%d, y=%d", t, u));
            return prop((t == u));
        });
        assertResult(p);
    }
}

