package net.bytebuddy.matcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ClassLoaderHierarchyMatcherTest extends AbstractElementMatcherTest<ClassLoaderHierarchyMatcher<?>> {
    @Mock
    private ElementMatcher<? super ClassLoader> classLoaderMatcher;

    @Mock
    private ClassLoader classLoader;

    @SuppressWarnings("unchecked")
    public ClassLoaderHierarchyMatcherTest() {
        super(((Class<ClassLoaderHierarchyMatcher<?>>) ((Object) (ClassLoaderHierarchyMatcher.class))), "hasChild");
    }

    @Test
    public void testMatchesChild() throws Exception {
        Mockito.when(classLoaderMatcher.matches(classLoader)).thenReturn(true);
        MatcherAssert.assertThat(new ClassLoaderHierarchyMatcher<ClassLoader>(classLoaderMatcher).matches(classLoader), CoreMatchers.is(true));
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
    }

    @Test
    public void testMatchesParent() throws Exception {
        Mockito.when(classLoaderMatcher.matches(null)).thenReturn(true);
        MatcherAssert.assertThat(new ClassLoaderHierarchyMatcher<ClassLoader>(classLoaderMatcher).matches(classLoader), CoreMatchers.is(true));
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verify(classLoaderMatcher).matches(null);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new ClassLoaderHierarchyMatcher<ClassLoader>(classLoaderMatcher).matches(classLoader), CoreMatchers.is(false));
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verify(classLoaderMatcher).matches(null);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
    }
}

