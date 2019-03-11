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

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class PrimitiveUnboxingDelegateWideningTest {
    private final Class<?> primitiveType;

    private final Class<?> referenceType;

    private final String unboxingMethodName;

    private final String unboxingMethodDescriptor;

    private final int wideningOpcode;

    private final int sizeChange;

    private final int interimMaximum;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic referenceTypeDescription;

    @Mock
    private TypeDescription.Generic primitiveTypeDescription;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public PrimitiveUnboxingDelegateWideningTest(Class<?> referenceType, Class<?> primitiveType, String unboxingMethodName, String unboxingMethodDescriptor, int wideningOpcode, int sizeChange, int interimMaximum) {
        this.primitiveType = primitiveType;
        this.referenceType = referenceType;
        this.unboxingMethodName = unboxingMethodName;
        this.unboxingMethodDescriptor = unboxingMethodDescriptor;
        this.wideningOpcode = wideningOpcode;
        this.sizeChange = sizeChange;
        this.interimMaximum = interimMaximum;
    }

    @Test
    public void testTrivialBoxing() throws Exception {
        StackManipulation stackManipulation = PrimitiveUnboxingDelegate.forReferenceType(referenceTypeDescription).assignUnboxedTo(primitiveTypeDescription, chainedAssigner, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(sizeChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(interimMaximum));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(referenceType), unboxingMethodName, unboxingMethodDescriptor, false);
        Mockito.verify(methodVisitor).visitInsn(wideningOpcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

