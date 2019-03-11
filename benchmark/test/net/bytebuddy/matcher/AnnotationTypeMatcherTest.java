package net.bytebuddy.matcher;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AnnotationTypeMatcherTest extends AbstractElementMatcherTest<AnnotationTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription> typeMatcher;

    @Mock
    private AnnotationDescription annotatedElement;

    @Mock
    private TypeDescription annotationType;

    @SuppressWarnings("unchecked")
    public AnnotationTypeMatcherTest() {
        super(((Class<AnnotationTypeMatcher<?>>) ((Object) (AnnotationTypeMatcher.class))), "ofAnnotationType");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeMatcher.matches(annotationType)).thenReturn(true);
        MatcherAssert.assertThat(new AnnotationTypeMatcher<AnnotationDescription>(typeMatcher).matches(annotatedElement), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(annotationType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeMatcher.matches(annotationType)).thenReturn(false);
        MatcherAssert.assertThat(new AnnotationTypeMatcher<AnnotationDescription>(typeMatcher).matches(annotatedElement), CoreMatchers.is(false));
        Mockito.verify(typeMatcher).matches(annotationType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }
}

