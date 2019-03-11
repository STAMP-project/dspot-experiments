package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.description.annotation.AnnotationSource.Empty.INSTANCE;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE_SYMBOLIC;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class TypeDescriptionGenericOfTypeVariableSymbolicTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    public TestRule mockitoRule = new MockitoRule(this);

    private TypeDescription.Generic typeVariable;

    @Mock
    private AnnotationDescription annotationDescription;

    @Test
    public void testSymbol() throws Exception {
        Assert.assertThat(typeVariable.getSymbol(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO));
    }

    @Test
    public void testTypeName() throws Exception {
        Assert.assertThat(typeVariable.getTypeName(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(typeVariable.toString(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO));
    }

    @Test
    public void testSort() throws Exception {
        Assert.assertThat(typeVariable.getSort(), CoreMatchers.is(VARIABLE_SYMBOLIC));
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
        Assert.assertThat(typeVariable, CoreMatchers.is(((TypeDescription.Generic) (new TypeDescription.Generic.OfTypeVariable.Symbolic(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO, new AnnotationSource.Explicit(annotationDescription))))));
        Assert.assertThat(typeVariable, CoreMatchers.is(((TypeDescription.Generic) (new TypeDescription.Generic.OfTypeVariable.Symbolic(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO, INSTANCE)))));
        Assert.assertThat(typeVariable, CoreMatchers.not(((TypeDescription.Generic) (new TypeDescription.Generic.OfTypeVariable.Symbolic(TypeDescriptionGenericOfTypeVariableSymbolicTest.BAR, INSTANCE)))));
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
        Assert.assertThat(typeVariable.hashCode(), CoreMatchers.is(TypeDescriptionGenericOfTypeVariableSymbolicTest.FOO.hashCode()));
    }

    @Test(expected = IllegalStateException.class)
    public void testRawTypeThrowsException() throws Exception {
        typeVariable.asRawType();
    }

    @Test(expected = IllegalStateException.class)
    public void testErasureThrowsException() throws Exception {
        typeVariable.asErasure();
    }

    @Test(expected = IllegalStateException.class)
    public void testComponentTypeThrowsException() throws Exception {
        typeVariable.getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredFieldsThrowsException() throws Exception {
        typeVariable.getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredMethodsThrowsException() throws Exception {
        typeVariable.getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundsThrowsException() throws Exception {
        typeVariable.getLowerBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundsThrowsException() throws Exception {
        typeVariable.getUpperBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testParametersThrowsException() throws Exception {
        typeVariable.getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testVariableSourceThrowsException() throws Exception {
        typeVariable.getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void getOwnerTypeThrowsException() throws Exception {
        typeVariable.getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testSuperClassThrowsException() throws Exception {
        typeVariable.getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testInterfacesThrowsException() throws Exception {
        typeVariable.getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorThrowsException() throws Exception {
        typeVariable.iterator();
    }

    @Test
    public void testRepresents() throws Exception {
        Assert.assertThat(typeVariable.represents(Object.class), CoreMatchers.is(false));
    }

    @Test(expected = NullPointerException.class)
    public void testRepresentsNullPointer() throws Exception {
        typeVariable.represents(null);
    }
}

