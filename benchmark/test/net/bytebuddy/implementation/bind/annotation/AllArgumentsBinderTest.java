package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.implementation.bind.annotation.AllArguments.Assignment.SLACK;
import static net.bytebuddy.implementation.bind.annotation.AllArguments.Assignment.STRICT;
import static net.bytebuddy.implementation.bind.annotation.AllArguments.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class AllArgumentsBinderTest extends AbstractAnnotationBinderTest<AllArguments> {
    private static final String FOO = "foo";

    @Mock
    private TypeDescription.Generic firstSourceType;

    @Mock
    private TypeDescription.Generic secondSourceType;

    @Mock
    private TypeDescription.Generic genericInstrumentedType;

    @Mock
    private TypeDescription rawTargetType;

    @Mock
    private TypeDescription rawComponentType;

    @Mock
    private TypeDescription.Generic targetType;

    @Mock
    private TypeDescription.Generic componentType;

    public AllArgumentsBinderTest() {
        super(AllArguments.class);
    }

    @Test
    public void testLegalStrictBindingRuntimeType() throws Exception {
        Mockito.when(target.getIndex()).thenReturn(1);
        testLegalStrictBinding(STATIC);
    }

    @Test
    public void testLegalStrictBindingNoRuntimeType() throws Exception {
        Mockito.when(target.getIndex()).thenReturn(1);
        RuntimeType runtimeType = Mockito.mock(RuntimeType.class);
        Mockito.doReturn(RuntimeType.class).when(runtimeType).annotationType();
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(runtimeType));
        testLegalStrictBinding(DYNAMIC);
    }

    @Test
    public void testIllegalBinding() throws Exception {
        Mockito.when(target.getIndex()).thenReturn(1);
        Mockito.when(annotation.value()).thenReturn(STRICT);
        Mockito.when(stackManipulation.isValid()).thenReturn(false);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, firstSourceType, secondSourceType));
        Mockito.when(source.isStatic()).thenReturn(false);
        Mockito.when(targetType.isArray()).thenReturn(true);
        Mockito.when(targetType.getComponentType()).thenReturn(componentType);
        Mockito.when(componentType.getStackSize()).thenReturn(StackSize.SINGLE);
        Mockito.when(target.getType()).thenReturn(targetType);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(source, Mockito.atLeast(1)).isStatic();
        Mockito.verify(target, Mockito.atLeast(1)).getType();
        Mockito.verify(target, Mockito.never()).getDeclaredAnnotations();
        Mockito.verify(assigner).assign(firstSourceType, componentType, STATIC);
        Mockito.verifyNoMoreInteractions(assigner);
    }

    @Test
    public void testLegalSlackBinding() throws Exception {
        Mockito.when(target.getIndex()).thenReturn(1);
        Mockito.when(annotation.value()).thenReturn(SLACK);
        Mockito.when(stackManipulation.isValid()).thenReturn(false);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, firstSourceType, secondSourceType));
        Mockito.when(source.isStatic()).thenReturn(false);
        Mockito.when(targetType.isArray()).thenReturn(true);
        Mockito.when(targetType.getComponentType()).thenReturn(componentType);
        Mockito.when(componentType.getStackSize()).thenReturn(StackSize.SINGLE);
        Mockito.when(target.getType()).thenReturn(targetType);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(rawComponentType.getInternalName()).thenReturn(AllArgumentsBinderTest.FOO);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = parameterBinding.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ICONST_0);
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.ANEWARRAY, AllArgumentsBinderTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verify(source, Mockito.atLeast(1)).isStatic();
        Mockito.verify(target, Mockito.atLeast(1)).getType();
        Mockito.verify(target, Mockito.never()).getDeclaredAnnotations();
        Mockito.verify(assigner).assign(firstSourceType, componentType, STATIC);
        Mockito.verify(assigner).assign(secondSourceType, componentType, STATIC);
        Mockito.verifyNoMoreInteractions(assigner);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonArrayTypeBinding() throws Exception {
        Mockito.when(target.getIndex()).thenReturn(0);
        TypeDescription.Generic targetType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription rawTargetType = Mockito.mock(TypeDescription.class);
        Mockito.when(targetType.asErasure()).thenReturn(rawTargetType);
        Mockito.when(targetType.isArray()).thenReturn(false);
        Mockito.when(target.getType()).thenReturn(targetType);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

