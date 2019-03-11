package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.attribute.AnnotationValueFilter.Default.APPEND_DEFAULTS;
import static net.bytebuddy.implementation.attribute.AnnotationValueFilter.Default.SKIP_DEFAULTS;


public class AnnotationValueFilterDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AnnotationDescription annotationDescription;

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Test
    public void testAppendsDefaults() throws Exception {
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        MatcherAssert.assertThat(APPEND_DEFAULTS.isRelevant(annotationDescription, methodDescription), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(annotationDescription);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testSkipDefaultsNoDefault() throws Exception {
        MatcherAssert.assertThat(SKIP_DEFAULTS.isRelevant(annotationDescription, methodDescription), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(annotationDescription);
        Mockito.verify(methodDescription).getDefaultValue();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipDefaultsNoEquality() throws Exception {
        Mockito.when(methodDescription.getDefaultValue()).thenReturn(Mockito.mock(AnnotationValue.class));
        Mockito.when(annotationDescription.getValue(methodDescription)).thenReturn(Mockito.mock(AnnotationValue.class));
        MatcherAssert.assertThat(SKIP_DEFAULTS.isRelevant(annotationDescription, methodDescription), CoreMatchers.is(true));
        Mockito.verify(annotationDescription).getValue(methodDescription);
        Mockito.verifyNoMoreInteractions(annotationDescription);
        Mockito.verify(methodDescription).getDefaultValue();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipDefaultsEquality() throws Exception {
        AnnotationValue<?, ?> annotationValue = Mockito.mock(AnnotationValue.class);
        Mockito.when(methodDescription.getDefaultValue()).thenReturn(((AnnotationValue) (annotationValue)));
        Mockito.when(annotationDescription.getValue(methodDescription)).thenReturn(((AnnotationValue) (annotationValue)));
        MatcherAssert.assertThat(SKIP_DEFAULTS.isRelevant(annotationDescription, methodDescription), CoreMatchers.is(false));
        Mockito.verify(annotationDescription).getValue(methodDescription);
        Mockito.verifyNoMoreInteractions(annotationDescription);
        Mockito.verify(methodDescription).getDefaultValue();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(SKIP_DEFAULTS.on(Mockito.mock(FieldDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (SKIP_DEFAULTS))));
        MatcherAssert.assertThat(SKIP_DEFAULTS.on(Mockito.mock(MethodDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (SKIP_DEFAULTS))));
        MatcherAssert.assertThat(SKIP_DEFAULTS.on(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (SKIP_DEFAULTS))));
        MatcherAssert.assertThat(APPEND_DEFAULTS.on(Mockito.mock(FieldDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (APPEND_DEFAULTS))));
        MatcherAssert.assertThat(APPEND_DEFAULTS.on(Mockito.mock(MethodDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (APPEND_DEFAULTS))));
        MatcherAssert.assertThat(APPEND_DEFAULTS.on(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((AnnotationValueFilter) (APPEND_DEFAULTS))));
    }
}

