package net.bytebuddy.matcher;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class InheritedAnnotationMatcherTest extends AbstractElementMatcherTest<InheritedAnnotationMatcher<?>> {
    @Mock
    private ElementMatcher<? super AnnotationList> annotationMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private AnnotationList annotationList;

    @SuppressWarnings("unchecked")
    public InheritedAnnotationMatcherTest() {
        super(((Class<InheritedAnnotationMatcher<?>>) ((Object) (InheritedAnnotationMatcher.class))), "inheritsAnnotations");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeDescription.getInheritedAnnotations()).thenReturn(annotationList);
        Mockito.when(annotationMatcher.matches(annotationList)).thenReturn(true);
        MatcherAssert.assertThat(new InheritedAnnotationMatcher<TypeDescription>(annotationMatcher).matches(typeDescription), CoreMatchers.is(true));
        Mockito.verify(annotationMatcher).matches(annotationList);
        Mockito.verifyNoMoreInteractions(annotationMatcher);
        Mockito.verify(typeDescription).getInheritedAnnotations();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeDescription.getInheritedAnnotations()).thenReturn(annotationList);
        Mockito.when(annotationMatcher.matches(annotationList)).thenReturn(false);
        MatcherAssert.assertThat(new InheritedAnnotationMatcher<TypeDescription>(annotationMatcher).matches(typeDescription), CoreMatchers.is(false));
        Mockito.verify(annotationMatcher).matches(annotationList);
        Mockito.verifyNoMoreInteractions(annotationMatcher);
        Mockito.verify(typeDescription).getInheritedAnnotations();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }
}

