package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class FilterableListEmptyTest {
    @SuppressWarnings("unchecked")
    private FilterableList empty = new FilterableList.Empty();

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet() throws Exception {
        empty.get(0);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetOnly() throws Exception {
        empty.getOnly();
    }

    @Test
    public void testSize() throws Exception {
        MatcherAssert.assertThat(empty.size(), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilter() throws Exception {
        MatcherAssert.assertThat(empty.filter(Mockito.mock(ElementMatcher.class)), CoreMatchers.is(empty));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubListZero() throws Exception {
        MatcherAssert.assertThat(empty.subList(0, 0), CoreMatchers.is(empty));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @SuppressWarnings("unchecked")
    public void testSubListOverflow() throws Exception {
        empty.subList(1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testSubListBounds() throws Exception {
        empty.subList(1, 0);
    }
}

