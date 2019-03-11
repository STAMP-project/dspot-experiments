package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class AnnotationAppenderForTypeAnnotationsTest {
    private static final String FOO = "foo";

    private static final int BAR = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AnnotationAppender annotationAppender;

    @Mock
    private AnnotationAppender result;

    @Mock
    private AnnotationValueFilter annotationValueFilter;

    @Mock
    private AnnotationDescription annotationDescription;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription.Generic second;

    @Mock
    private TypeDescription.Generic third;

    @Mock
    private TypeDescription erasure;

    private TypeDescription.Generic.Visitor<AnnotationAppender> visitor;

    @Test
    public void testGenericArray() throws Exception {
        Mockito.when(typeDescription.getComponentType()).thenReturn(second);
        MatcherAssert.assertThat(visitor.onGenericArray(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "["))));
    }

    @Test
    public void testWildcardUpperBound() throws Exception {
        Mockito.when(typeDescription.getLowerBounds()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(typeDescription.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(second));
        MatcherAssert.assertThat(visitor.onWildcard(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "*"))));
    }

    @Test
    public void testWildcardLowerBound() throws Exception {
        Mockito.when(typeDescription.getLowerBounds()).thenReturn(new TypeList.Generic.Explicit(second));
        Mockito.when(typeDescription.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(OBJECT));
        MatcherAssert.assertThat(visitor.onWildcard(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "*"))));
    }

    @Test
    public void testTypeVariable() throws Exception {
        MatcherAssert.assertThat(visitor.onTypeVariable(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
    }

    @Test
    public void testNonGenericArray() throws Exception {
        Mockito.when(typeDescription.isArray()).thenReturn(true);
        Mockito.when(typeDescription.getComponentType()).thenReturn(second);
        MatcherAssert.assertThat(visitor.onNonGenericType(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "["))));
    }

    @Test
    public void testNonGeneric() throws Exception {
        MatcherAssert.assertThat(visitor.onNonGenericType(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
    }

    @Test
    public void testParameterized() throws Exception {
        Mockito.when(getInnerClassCount()).thenReturn(1);
        Mockito.when(typeDescription.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(second));
        Mockito.when(typeDescription.getOwnerType()).thenReturn(third);
        MatcherAssert.assertThat(visitor.onParameterizedType(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "."));
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + ".0;"))));
        Mockito.verify(third).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + ""))));
    }

    @Test
    public void testParameterizedNoOwner() throws Exception {
        Mockito.when(typeDescription.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(second));
        MatcherAssert.assertThat(visitor.onParameterizedType(typeDescription), CoreMatchers.is(result));
        Mockito.verify(annotationAppender).append(annotationDescription, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, AnnotationAppenderForTypeAnnotationsTest.FOO);
        Mockito.verifyNoMoreInteractions(annotationAppender);
        Mockito.verify(second).accept(FieldByFieldComparison.matchesPrototype(new AnnotationAppender.ForTypeAnnotations(result, annotationValueFilter, AnnotationAppenderForTypeAnnotationsTest.BAR, ((AnnotationAppenderForTypeAnnotationsTest.FOO) + "0;"))));
    }
}

