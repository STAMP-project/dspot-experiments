package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE;


public class TypeDescriptionGenericVisitorValidatorForTypeAnnotations {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AnnotationDescription legalAnnotation;

    @Mock
    private AnnotationDescription illegalAnnotation;

    @Mock
    private AnnotationDescription duplicateAnnotation;

    @Mock
    private TypeDescription legalType;

    @Mock
    private TypeDescription illegalType;

    @Mock
    private TypeDescription.Generic legal;

    @Mock
    private TypeDescription.Generic illegal;

    @Mock
    private TypeDescription.Generic duplicate;

    @Mock
    private TypeDescription.Generic otherLegal;

    @Mock
    private TypeDescription.Generic otherIllegal;

    @Test
    public void testIllegalGenericArray() throws Exception {
        Assert.assertThat(INSTANCE.onGenericArray(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testDuplicateGenericArray() throws Exception {
        Assert.assertThat(INSTANCE.onGenericArray(duplicate), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDelegatedGenericArray() throws Exception {
        Mockito.when(legal.getComponentType()).thenReturn(otherIllegal);
        Assert.assertThat(INSTANCE.onGenericArray(legal), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLegalGenericArray() throws Exception {
        Mockito.when(legal.getComponentType()).thenReturn(otherLegal);
        Assert.assertThat(INSTANCE.onGenericArray(legal), CoreMatchers.is(true));
        Mockito.verify(otherLegal).accept(INSTANCE);
    }

    @Test
    public void testIllegalNonGenericArray() throws Exception {
        Assert.assertThat(INSTANCE.onNonGenericType(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testDuplicateNonGenericArray() throws Exception {
        Assert.assertThat(INSTANCE.onNonGenericType(duplicate), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDelegatedNonGenericArray() throws Exception {
        Mockito.when(legal.isArray()).thenReturn(true);
        Mockito.when(legal.getComponentType()).thenReturn(otherIllegal);
        Assert.assertThat(INSTANCE.onNonGenericType(legal), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLegalNonGenericArray() throws Exception {
        Mockito.when(legal.isArray()).thenReturn(true);
        Mockito.when(legal.getComponentType()).thenReturn(otherLegal);
        Assert.assertThat(INSTANCE.onNonGenericType(legal), CoreMatchers.is(true));
        Mockito.verify(otherLegal).accept(INSTANCE);
    }

    @Test
    public void testIllegalNonGeneric() throws Exception {
        Assert.assertThat(INSTANCE.onNonGenericType(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testDuplicateNonGeneric() throws Exception {
        Assert.assertThat(INSTANCE.onNonGenericType(duplicate), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLegalNonGeneric() throws Exception {
        Assert.assertThat(INSTANCE.onNonGenericType(legal), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalTypeVariable() throws Exception {
        Assert.assertThat(INSTANCE.onTypeVariable(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testDuplicateTypeVariable() throws Exception {
        Assert.assertThat(INSTANCE.onTypeVariable(duplicate), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLegalTypeVariable() throws Exception {
        Assert.assertThat(INSTANCE.onTypeVariable(legal), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalParameterized() throws Exception {
        Assert.assertThat(INSTANCE.onParameterizedType(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testDuplicateParameterized() throws Exception {
        Assert.assertThat(INSTANCE.onParameterizedType(duplicate), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDelegatedOwnerTypeParameterized() throws Exception {
        Mockito.when(legal.getOwnerType()).thenReturn(otherIllegal);
        Assert.assertThat(INSTANCE.onParameterizedType(legal), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDelegatedTypeArgumentParameterized() throws Exception {
        Mockito.when(legal.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(otherIllegal));
        Assert.assertThat(INSTANCE.onParameterizedType(legal), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalDuplicateParameterized() throws Exception {
        Assert.assertThat(INSTANCE.onParameterizedType(duplicate), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testLegalParameterized() throws Exception {
        Mockito.when(legal.isArray()).thenReturn(true);
        Mockito.when(legal.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(otherLegal));
        Mockito.when(legal.getOwnerType()).thenReturn(otherLegal);
        Assert.assertThat(INSTANCE.onParameterizedType(legal), CoreMatchers.is(true));
        Mockito.verify(otherLegal, Mockito.times(2)).accept(INSTANCE);
    }

    @Test
    public void testWildcardIllegal() throws Exception {
        Assert.assertThat(INSTANCE.onWildcard(illegal), CoreMatchers.is(false));
    }

    @Test
    public void testWildcardDuplicate() throws Exception {
        Assert.assertThat(INSTANCE.onWildcard(duplicate), CoreMatchers.is(false));
    }

    @Test
    public void testWildcardIllegalUpperBounds() throws Exception {
        Mockito.when(legal.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(otherIllegal));
        Mockito.when(legal.getLowerBounds()).thenReturn(new TypeList.Generic.Empty());
        Assert.assertThat(INSTANCE.onWildcard(legal), CoreMatchers.is(false));
    }

    @Test
    public void testWildcardIllegalLowerBounds() throws Exception {
        Mockito.when(legal.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(OBJECT));
        Mockito.when(legal.getLowerBounds()).thenReturn(new TypeList.Generic.Explicit(otherIllegal));
        Assert.assertThat(INSTANCE.onWildcard(legal), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testWildcardLegal() throws Exception {
        Mockito.when(legal.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(OBJECT));
        Mockito.when(legal.getLowerBounds()).thenReturn(new TypeList.Generic.Explicit(otherLegal));
        Assert.assertThat(INSTANCE.onWildcard(legal), CoreMatchers.is(true));
    }
}

