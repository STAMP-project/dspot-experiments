package net.bytebuddy.matcher;


import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CollectionSizeMatcherTest extends AbstractElementMatcherTest<CollectionSizeMatcher<?>> {
    @Mock
    private Iterable<Object> collection;

    @SuppressWarnings("unchecked")
    public CollectionSizeMatcherTest() {
        super(((Class<CollectionSizeMatcher<?>>) ((Object) (CollectionSizeMatcher.class))), "ofSize");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(collection.iterator()).thenReturn(Collections.singletonList(new Object()).iterator());
        MatcherAssert.assertThat(new CollectionSizeMatcher<Iterable<?>>(1).matches(collection), CoreMatchers.is(true));
        Mockito.verify(collection).iterator();
        Mockito.verifyNoMoreInteractions(collection);
    }

    @Test
    public void testMatchCollection() throws Exception {
        MatcherAssert.assertThat(new CollectionSizeMatcher<Iterable<?>>(1).matches(Collections.singletonList(new Object())), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(collection.iterator()).thenReturn(Collections.emptyList().iterator());
        MatcherAssert.assertThat(new CollectionSizeMatcher<Iterable<?>>(1).matches(collection), CoreMatchers.is(false));
        Mockito.verify(collection).iterator();
        Mockito.verifyNoMoreInteractions(collection);
    }

    @Test
    public void testNoMatchCollection() throws Exception {
        MatcherAssert.assertThat(new CollectionSizeMatcher<Iterable<?>>(0).matches(Collections.singletonList(new Object())), CoreMatchers.is(false));
    }
}

