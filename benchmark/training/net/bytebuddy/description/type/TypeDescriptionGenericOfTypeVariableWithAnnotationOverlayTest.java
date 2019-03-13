package net.bytebuddy.description.type;


import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private TypeDescription.Generic typeVariable;

    @Mock
    private TypeDescription.Generic original;

    @Mock
    private TypeDescription.Generic upperBound;

    @Mock
    private TypeDescription.Generic lowerBound;

    @Mock
    private TypeVariableSource typeVariableSource;

    @Mock
    private AnnotationDescription annotationDescription;

    @Test
    public void testSymbol() throws Exception {
        Assert.assertThat(typeVariable.getSymbol(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO));
    }

    @Test
    public void testTypeName() throws Exception {
        Assert.assertThat(typeVariable.getTypeName(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(typeVariable.toString(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO));
    }

    @Test
    public void testSort() throws Exception {
        Assert.assertThat(typeVariable.getSort(), CoreMatchers.is(VARIABLE));
    }

    @Test
    public void testStackSize() throws Exception {
        Assert.assertThat(typeVariable.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
    }

    @Test
    public void testArray() throws Exception {
        Assert.assertThat(typeVariable.isArray(), CoreMatchers.is(false));
    }

    @Test
    public void testPrimitive() throws Exception {
        Assert.assertThat(typeVariable.isPrimitive(), CoreMatchers.is(false));
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertThat(typeVariable, CoreMatchers.is(typeVariable));
        Assert.assertThat(typeVariable, CoreMatchers.is(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.typeVariable(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO, typeVariableSource, annotationDescription)));
        Assert.assertThat(typeVariable, CoreMatchers.is(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.typeVariable(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO, typeVariableSource)));
        Assert.assertThat(typeVariable, CoreMatchers.not(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.typeVariable(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.BAR, typeVariableSource, annotationDescription)));
        Assert.assertThat(typeVariable, CoreMatchers.not(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.typeVariable(TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO, Mockito.mock(TypeVariableSource.class), annotationDescription)));
        Assert.assertThat(typeVariable, CoreMatchers.not(OBJECT));
        Assert.assertThat(typeVariable, CoreMatchers.not(new Object()));
        Assert.assertThat(typeVariable, CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test
    public void testAnnotations() throws Exception {
        Assert.assertThat(typeVariable.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        Assert.assertThat(typeVariable.getDeclaredAnnotations().contains(annotationDescription), CoreMatchers.is(true));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertThat(typeVariable.hashCode(), CoreMatchers.is(((typeVariableSource.hashCode()) ^ (TypeDescriptionGenericOfTypeVariableWithAnnotationOverlayTest.FOO.hashCode()))));
    }
}

