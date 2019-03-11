package net.bytebuddy.description.type;


import net.bytebuddy.matcher.AbstractFilterableListTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.NoOp.INSTANCE;


public abstract class AbstractTypeListGenericTest<U> extends AbstractFilterableListTest<TypeDescription.Generic, TypeList.Generic, U> {
    @Test
    public void testErasures() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asErasures().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(asList(getFirst()).asErasures().getOnly(), CoreMatchers.is(asElement(getFirst()).asErasure()));
    }

    @Test
    public void testRawTypes() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asRawTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(asList(getFirst()).asRawTypes().getOnly(), CoreMatchers.is(asElement(getFirst()).asRawType()));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).accept(INSTANCE), CoreMatchers.is(asList(getFirst())));
    }

    @Test
    public void testStackSizeEmpty() throws Exception {
        MatcherAssert.assertThat(emptyList().getStackSize(), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStackSizeNonEmpty() throws Exception {
        MatcherAssert.assertThat(asList(getFirst(), getSecond()).getStackSize(), CoreMatchers.is(2));
    }

    /* empty */
    protected interface Foo<T> {}

    /* empty */
    protected interface Bar<S> {}

    /* empty */
    public static class Holder implements AbstractTypeListGenericTest.Bar<Integer> , AbstractTypeListGenericTest.Foo<String> {}
}

