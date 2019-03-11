package fj.data.optic;


import Monoid.intMinMonoid;
import fj.data.Either;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class TraversalTest {
    @Test
    public void testTraversalLeft() {
        final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
        Assert.assertThat(t.fold(intMinMonoid).f(Either.left(3)), Is.is(3));
    }

    @Test
    public void testTraversalRight() {
        final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
        Assert.assertThat(t.fold(intMinMonoid).f(Either.right(2)), Is.is(2));
    }
}

