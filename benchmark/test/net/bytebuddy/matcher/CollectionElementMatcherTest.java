package net.bytebuddy.matcher;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CollectionElementMatcherTest extends AbstractElementMatcherTest<CollectionElementMatcher<?>> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private Iterable<Object> iterable;

    private Object element;

    @Mock
    private ElementMatcher<? super Object> elementMatcher;

    @SuppressWarnings("unchecked")
    public CollectionElementMatcherTest() {
        super(((Class<CollectionElementMatcher<?>>) ((Object) (CollectionElementMatcher.class))), "with");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(elementMatcher.matches(element)).thenReturn(true);
        MatcherAssert.assertThat(new CollectionElementMatcher<Object>(1, elementMatcher).matches(iterable), CoreMatchers.is(true));
        Mockito.verify(elementMatcher).matches(element);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(elementMatcher.matches(element)).thenReturn(false);
        MatcherAssert.assertThat(new CollectionElementMatcher<Object>(1, elementMatcher).matches(iterable), CoreMatchers.is(false));
        Mockito.verify(elementMatcher).matches(element);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testNoMatchIndex() throws Exception {
        MatcherAssert.assertThat(new CollectionElementMatcher<Object>(2, elementMatcher).matches(iterable), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(elementMatcher);
    }
}

