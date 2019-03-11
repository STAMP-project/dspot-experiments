package net.bytebuddy.matcher;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractFilterableListTest<T, S extends FilterableList<T, S>, U> {
    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testGetOnlyTwoElementList() throws Exception {
        asList(Arrays.asList(getFirst(), getSecond())).getOnly();
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testGetOnlyEmptyList() throws Exception {
        asList(Collections.<U>emptyList()).getOnly();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetOnlySingleList() throws Exception {
        MatcherAssert.assertThat(asList(Collections.singletonList(getFirst())).getOnly(), CoreMatchers.is(asElement(getFirst())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilter() throws Exception {
        MatcherAssert.assertThat(asList(Arrays.asList(getFirst(), getSecond())).filter(ElementMatchers.is(asElement(getFirst()))).getOnly(), CoreMatchers.is(asElement(getFirst())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubList() throws Exception {
        MatcherAssert.assertThat(asList(Arrays.asList(getFirst(), getSecond())).subList(0, 1).getOnly(), CoreMatchers.is(asElement(getFirst())));
    }
}

