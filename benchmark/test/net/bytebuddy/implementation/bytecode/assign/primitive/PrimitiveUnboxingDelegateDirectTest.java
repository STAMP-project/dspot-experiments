package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class PrimitiveUnboxingDelegateDirectTest {
    private final Class<?> primitiveType;

    private final Class<?> wrapperType;

    private final String unboxingMethodName;

    private final String unboxingMethodDescriptor;

    private final int sizeChange;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic primitiveTypeDescription;

    @Mock
    private TypeDescription.Generic wrapperTypeDescription;

    @Mock
    private TypeDescription rawPrimitiveTypeDescription;

    @Mock
    private TypeDescription rawWrapperTypeDescription;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private StackManipulation stackManipulation;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public PrimitiveUnboxingDelegateDirectTest(Class<?> primitiveType, Class<?> wrapperType, String unboxingMethodName, String unboxingMethodDescriptor, int sizeChange) {
        this.primitiveType = primitiveType;
        this.wrapperType = wrapperType;
        this.unboxingMethodName = unboxingMethodName;
        this.unboxingMethodDescriptor = unboxingMethodDescriptor;
        this.sizeChange = sizeChange;
    }

    @Test
    public void testTrivialBoxing() throws Exception {
        StackManipulation stackManipulation = PrimitiveUnboxingDelegate.forReferenceType(wrapperTypeDescription).assignUnboxedTo(primitiveTypeDescription, chainedAssigner, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(sizeChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(sizeChange));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(wrapperType), unboxingMethodName, unboxingMethodDescriptor, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(chainedAssigner);
        Mockito.verifyZeroInteractions(this.stackManipulation);
    }

    @Test
    public void testImplicitBoxing() throws Exception {
        TypeDescription.Generic referenceTypeDescription = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(referenceTypeDescription.asGenericType()).thenReturn(referenceTypeDescription);
        StackManipulation primitiveStackManipulation = PrimitiveUnboxingDelegate.forReferenceType(referenceTypeDescription).assignUnboxedTo(primitiveTypeDescription, chainedAssigner, DYNAMIC);
        MatcherAssert.assertThat(primitiveStackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = primitiveStackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(sizeChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(sizeChange));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(wrapperType), unboxingMethodName, unboxingMethodDescriptor, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(chainedAssigner).assign(referenceTypeDescription, of(wrapperType), DYNAMIC);
        Mockito.verifyNoMoreInteractions(chainedAssigner);
        Mockito.verify(stackManipulation, Mockito.atLeast(1)).isValid();
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(stackManipulation);
    }
}

