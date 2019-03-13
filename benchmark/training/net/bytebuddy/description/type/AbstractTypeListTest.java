package net.bytebuddy.description.type;


import net.bytebuddy.matcher.AbstractFilterableListTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractTypeListTest<U> extends AbstractFilterableListTest<TypeDescription, TypeList, U> {
    @Test
    public void testEmptyToInternalNames() throws Exception {
        MatcherAssert.assertThat(emptyList().toInternalNames(), CoreMatchers.nullValue(String[].class));
    }

    @Test
    public void testNonEmptyToInternalNames() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).toInternalNames(), CoreMatchers.is(new String[]{ asElement(getFirst()).getInternalName() }));
    }

    @Test
    public void testEmptyStackSize() throws Exception {
        MatcherAssert.assertThat(emptyList().getStackSize(), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNonEmptyStackSize() throws Exception {
        MatcherAssert.assertThat(asList(getFirst(), getSecond()).getStackSize(), CoreMatchers.is(2));
    }

    /* empty */
    protected interface Foo {}

    /* empty */
    protected interface Bar {}

    /* empty */
    public static class Holder implements AbstractTypeListTest.Bar , AbstractTypeListTest.Foo {}
}

