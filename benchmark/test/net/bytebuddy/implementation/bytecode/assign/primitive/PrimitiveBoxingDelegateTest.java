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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class PrimitiveBoxingDelegateTest {
    private static final String VALUE_OF = "valueOf";

    private final Class<?> primitiveType;

    private final TypeDescription primitiveTypeDescription;

    private final TypeDescription referenceTypeDescription;

    private final String boxingMethodDescriptor;

    private final int sizeChange;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic targetType;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private StackManipulation stackManipulation;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public PrimitiveBoxingDelegateTest(Class<?> primitiveType, Class<?> referenceType, String boxingMethodDescriptor, int sizeChange) {
        this.primitiveType = primitiveType;
        primitiveTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(primitiveTypeDescription.represents(primitiveType)).thenReturn(true);
        referenceTypeDescription = of(referenceType);
        this.boxingMethodDescriptor = boxingMethodDescriptor;
        this.sizeChange = sizeChange;
    }

    @Test
    public void testBoxing() throws Exception {
        StackManipulation boxingStackManipulation = PrimitiveBoxingDelegate.forPrimitive(primitiveTypeDescription).assignBoxedTo(targetType, chainedAssigner, STATIC);
        MatcherAssert.assertThat(boxingStackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = boxingStackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(sizeChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(primitiveTypeDescription).represents(primitiveType);
        Mockito.verify(primitiveTypeDescription, Mockito.atLeast(1)).represents(ArgumentMatchers.any(Class.class));
        Mockito.verifyNoMoreInteractions(primitiveTypeDescription);
        Mockito.verify(chainedAssigner).assign(referenceTypeDescription.asGenericType(), targetType, STATIC);
        Mockito.verifyNoMoreInteractions(chainedAssigner);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, referenceTypeDescription.getInternalName(), PrimitiveBoxingDelegateTest.VALUE_OF, boxingMethodDescriptor, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(stackManipulation, Mockito.atLeast(1)).isValid();
        Mockito.verify(stackManipulation).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(stackManipulation);
    }
}

