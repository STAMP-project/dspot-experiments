package fj.data;


import Equal.intEqual;
import Equal.stringEqual;
import fj.Equal;
import fj.F;
import fj.P;
import fj.test.Property;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 17/12/2014.
 */
public class WriterTest {
    @Test
    public void base() {
        Assert.assertTrue(tellTruth("a", "b", 0));
    }

    final Equal<Writer<String, Integer>> eq = Equal.writerEqual(stringEqual, intEqual);

    final F<Integer, Writer<String, Integer>> defaultWriter = Writer.<Integer>stringLogger();

    @Test
    public void testTellProp() {
        Property p = property(arbString, arbString, arbInteger, ( s1, s2, i) -> prop(tellTruth(s1, s2, i)));
        assertResult(p);
    }

    @Test
    public void testMap() {
        Property p = property(arbInteger, arbF(cogenInteger, arbInteger), ( i, f) -> {
            boolean b = eq.eq(defaultWriter.f(i).map(f), defaultWriter.f(f.f(i)));
            return prop(b);
        });
        assertResult(p);
    }

    @Test
    public void testFlatMap() {
        Property p = property(arbInteger, arbF(cogenInteger, arbWriterStringInt()), ( i, f) -> {
            boolean b = eq.eq(Writer.<Integer>stringLogger().f(i).flatMap(f), f.f(i));
            return prop(b);
        });
        assertResult(p);
    }

    // Left identity: return a >>= f == f a
    @Test
    public void testLeftIdentity() {
        Property p = Property.property(arbInteger, arbF(cogenInteger, arbWriterStringInt()), ( i, f) -> {
            return prop(eq.eq(defaultWriter.f(i).flatMap(f), f.f(i)));
        });
        assertResult(p);
    }

    // Right identity: m >>= return == m
    @Test
    public void testRightIdentity() {
        Property p = Property.property(arbWriterStringInt(), ( w) -> prop(eq.eq(w.flatMap(( a) -> defaultWriter.f(a)), w)));
        assertResult(p);
    }

    // Associativity: (m >>= f) >>= g == m >>= (\x -> f x >>= g)
    @Test
    public void testAssociativity() {
        Property p = Property.property(arbWriterStringInt(), arbF(cogenInteger, arbWriterStringInt()), arbF(cogenInteger, arbWriterStringInt()), ( w, f, g) -> {
            boolean t = eq.eq(w.flatMap(f).flatMap(g), w.flatMap(( x) -> f.f(x).flatMap(g)));
            return prop(t);
        });
        assertResult(p);
    }

    @Test
    public void testUnit() {
        Writer<String, String> w = Writer.unit("+").tell("foo").tell("bar");
        MatcherAssert.assertThat(w.run(), Is.is(P.p("foobar", "+")));
        MatcherAssert.assertThat(w.log(), Is.is("foobar"));
        MatcherAssert.assertThat(w.value(), Is.is("+"));
    }
}

