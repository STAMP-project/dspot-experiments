package net.bytebuddy.description.type;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.AnnotationReader.Dispatcher.ForLegacyVm.INSTANCE;


public class TypeDescriptionGenericAnnotationReaderTest {
    @Test
    public void testLegacyVmReturnsNoOpReaders() throws Exception {
        Assert.assertThat(INSTANCE.resolveFieldType(null), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveSuperClassType(null), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveInterfaceType(null, 0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveReturnType(null), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveParameterType(null, 0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveExceptionType(null, 0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveTypeVariable(null), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(INSTANCE.resolveReceiverType(null), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCannotResolveAnnotatedType() throws Exception {
        INSTANCE.resolve(null);
    }

    @Test
    public void testNoOpReaderReturnsZeroAnnotations() throws Exception {
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.getDeclaredAnnotations().length, CoreMatchers.is(0));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoOpReaderNoHierarchyAnnotations() throws Exception {
        TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.getAnnotations();
    }

    @Test(expected = IllegalStateException.class)
    public void testNoOpReaderNoSpecificAnnotations() throws Exception {
        TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.getAnnotation(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoOpReaderNoSpecificAnnotationPresent() throws Exception {
        TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.isAnnotationPresent(null);
    }

    @Test
    public void testAnnotationReaderNoOpTest() throws Exception {
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofComponentType(), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofOuterClass(), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofOwnerType(), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofTypeArgument(0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofTypeVariableBoundType(0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofWildcardLowerBoundType(0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
        Assert.assertThat(TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE.ofWildcardUpperBoundType(0), CoreMatchers.is(((TypeDescription.Generic.AnnotationReader) (TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE))));
    }
}

