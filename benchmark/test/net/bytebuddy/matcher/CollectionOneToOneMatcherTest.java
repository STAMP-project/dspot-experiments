package net.bytebuddy.matcher;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CollectionOneToOneMatcherTest extends AbstractElementMatcherTest<CollectionOneToOneMatcher<?>> {
    private Iterable<Object> iterable;

    private Object first;

    private Object second;

    @Mock
    private ElementMatcher<Object> firstMatcher;

    @Mock
    private ElementMatcher<Object> secondMatcher;

    @SuppressWarnings("unchecked")
    public CollectionOneToOneMatcherTest() {
        super(((Class<CollectionOneToOneMatcher<?>>) ((Object) (CollectionOneToOneMatcher.class))), "containing");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatch() throws Exception {
        Mockito.when(firstMatcher.matches(first)).thenReturn(true);
        Mockito.when(secondMatcher.matches(second)).thenReturn(true);
        MatcherAssert.assertThat(new CollectionOneToOneMatcher<Object>(Arrays.asList(firstMatcher, secondMatcher)).matches(iterable), CoreMatchers.is(true));
        Mockito.verify(firstMatcher).matches(first);
        Mockito.verifyNoMoreInteractions(firstMatcher);
        Mockito.verify(secondMatcher).matches(second);
        Mockito.verifyNoMoreInteractions(secondMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatchFirst() throws Exception {
        Mockito.when(firstMatcher.matches(first)).thenReturn(false);
        MatcherAssert.assertThat(new CollectionOneToOneMatcher<Object>(Arrays.asList(firstMatcher, secondMatcher)).matches(iterable), CoreMatchers.is(false));
        Mockito.verify(firstMatcher).matches(first);
        Mockito.verifyNoMoreInteractions(firstMatcher);
        Mockito.verifyZeroInteractions(secondMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatchSecond() throws Exception {
        Mockito.when(firstMatcher.matches(first)).thenReturn(true);
        Mockito.when(secondMatcher.matches(second)).thenReturn(false);
        MatcherAssert.assertThat(new CollectionOneToOneMatcher<Object>(Arrays.asList(firstMatcher, secondMatcher)).matches(iterable), CoreMatchers.is(false));
        Mockito.verify(firstMatcher).matches(first);
        Mockito.verifyNoMoreInteractions(firstMatcher);
        Mockito.verify(secondMatcher).matches(second);
        Mockito.verifyNoMoreInteractions(secondMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatchSize() throws Exception {
        MatcherAssert.assertThat(new CollectionOneToOneMatcher<Object>(Arrays.asList(firstMatcher, secondMatcher)).matches(Collections.singletonList(firstMatcher)), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(firstMatcher);
        Mockito.verifyZeroInteractions(secondMatcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStringRepresentation() throws Exception {
        MatcherAssert.assertThat(new CollectionOneToOneMatcher<Object>(Arrays.asList(firstMatcher, secondMatcher)).toString(), CoreMatchers.is(((((((startsWith) + "(") + (firstMatcher)) + ", ") + (secondMatcher)) + ")")));
    }
}

