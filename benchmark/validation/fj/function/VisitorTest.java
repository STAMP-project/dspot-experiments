package fj.function;


import fj.F;
import fj.P;
import fj.P1;
import fj.data.List;
import fj.data.Option;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class VisitorTest {
    @Test
    public void testFindFirst() {
        Assert.assertThat(findFirst(List.list(Option.none(), Option.some(1), Option.none()), () -> -1), Is.is(1));
    }

    @Test
    public void testFindFirstDef() {
        Assert.assertThat(findFirst(List.list(Option.none(), Option.none(), Option.none()), () -> -1), Is.is((-1)));
    }

    @Test
    public void testNullableFindFirst() {
        Assert.assertThat(nullablefindFirst(List.list(null, 1, null), () -> -1), Is.is(1));
    }

    @Test
    public void testNullableFindFirstDef() {
        Assert.assertThat(nullablefindFirst(List.list(null, null, null), () -> -1), Is.is((-1)));
    }

    @Test
    public void testVisitor() {
        Assert.assertThat(visitor(List.list(( i) -> some((2 * i))), () -> -1, 10), Is.is(20));
    }

    @Test
    public void testVisitorDef() {
        Assert.assertThat(visitor(List.list(( i) -> none()), () -> "foo", 10), Is.is("foo"));
    }

    @Test
    public void testNullableVisitor() {
        Assert.assertThat(nullableVisitor(List.list(( i) -> 2 * i), () -> -1, 10), Is.is(20));
    }

    @Test
    public void testNullableVisitorDef() {
        Assert.assertThat(nullableVisitor(List.list(( i) -> null), () -> "foo", 10), Is.is("foo"));
    }

    @Test
    public void testAssociation() {
        final F<String, F<Integer, String>> a = association(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
        Assert.assertThat(a.f("foo").f(2), Is.is("two"));
    }

    @Test
    public void testAssociationDef() {
        final F<String, F<Integer, String>> a = association(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
        Assert.assertThat(a.f("foo").f(3), Is.is("foo"));
    }

    @Test
    public void testAssociationLazy() {
        final F<P1<String>, F<Integer, String>> a = associationLazy(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
        Assert.assertThat(a.f(P.p("foo")).f(2), Is.is("two"));
    }

    @Test
    public void testAssociationLazyDef() {
        final F<P1<String>, F<Integer, String>> a = associationLazy(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
        Assert.assertThat(a.f(P.p("foo")).f(3), Is.is("foo"));
    }
}

