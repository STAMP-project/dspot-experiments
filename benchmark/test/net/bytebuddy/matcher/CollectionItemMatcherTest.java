package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CollectionItemMatcherTest extends AbstractElementMatcherTest<CollectionItemMatcher<?>> {
    private Iterable<Object> iterable;

    private Object first;

    private Object second;

    @Mock
    private ElementMatcher<Object> elementMatcher;

    @SuppressWarnings("unchecked")
    public CollectionItemMatcherTest() {
        super(((Class<CollectionItemMatcher<?>>) ((Object) (CollectionItemMatcher.class))), "whereOne");
    }

    @Test
    public void testMatchFirst() throws Exception {
        Mockito.when(elementMatcher.matches(first)).thenReturn(true);
        MatcherAssert.assertThat(new CollectionItemMatcher<Object>(elementMatcher).matches(iterable), CoreMatchers.is(true));
        Mockito.verify(elementMatcher).matches(first);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testMatchSecond() throws Exception {
        Mockito.when(elementMatcher.matches(first)).thenReturn(false);
        Mockito.when(elementMatcher.matches(second)).thenReturn(true);
        MatcherAssert.assertThat(new CollectionItemMatcher<Object>(elementMatcher).matches(iterable), CoreMatchers.is(true));
        Mockito.verify(elementMatcher).matches(first);
        Mockito.verify(elementMatcher).matches(second);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(elementMatcher.matches(first)).thenReturn(false);
        Mockito.when(elementMatcher.matches(second)).thenReturn(false);
        MatcherAssert.assertThat(new CollectionItemMatcher<Object>(elementMatcher).matches(iterable), CoreMatchers.is(false));
        Mockito.verify(elementMatcher).matches(first);
        Mockito.verify(elementMatcher).matches(second);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }
}

