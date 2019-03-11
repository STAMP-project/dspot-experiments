package net.bytebuddy.matcher;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DeclaringAnnotationMatcherTest extends AbstractElementMatcherTest<DeclaringAnnotationMatcher<?>> {
    @Mock
    private ElementMatcher<? super AnnotationList> annotationMatcher;

    @Mock
    private AnnotationSource annotationSource;

    @Mock
    private AnnotationList annotationList;

    @SuppressWarnings("unchecked")
    public DeclaringAnnotationMatcherTest() {
        super(((Class<DeclaringAnnotationMatcher<?>>) ((Object) (DeclaringAnnotationMatcher.class))), "declaresAnnotations");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(annotationSource.getDeclaredAnnotations()).thenReturn(annotationList);
        Mockito.when(annotationMatcher.matches(annotationList)).thenReturn(true);
        MatcherAssert.assertThat(new DeclaringAnnotationMatcher<AnnotationSource>(annotationMatcher).matches(annotationSource), CoreMatchers.is(true));
        Mockito.verify(annotationMatcher).matches(annotationList);
        Mockito.verifyNoMoreInteractions(annotationMatcher);
        Mockito.verify(annotationSource).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(annotationSource);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(annotationSource.getDeclaredAnnotations()).thenReturn(annotationList);
        Mockito.when(annotationMatcher.matches(annotationList)).thenReturn(false);
        MatcherAssert.assertThat(new DeclaringAnnotationMatcher<AnnotationSource>(annotationMatcher).matches(annotationSource), CoreMatchers.is(false));
        Mockito.verify(annotationMatcher).matches(annotationList);
        Mockito.verifyNoMoreInteractions(annotationMatcher);
        Mockito.verify(annotationSource).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(annotationSource);
    }
}

