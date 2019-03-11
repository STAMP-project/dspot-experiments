package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bind.annotation.Empty.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class EmptyBinderTest extends AbstractAnnotationBinderTest<Empty> {
    private final TypeDescription.Generic typeDescription;

    private final int opcode;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public EmptyBinderTest(Class<?> type, int opcode) {
        super(Empty.class);
        typeDescription = of(type);
        this.opcode = opcode;
    }

    @Test
    public void testEmptyValue() throws Exception {
        Mockito.when(target.getType()).thenReturn(typeDescription);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = binding.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(typeDescription.getStackSize().getSize()));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(typeDescription.getStackSize().getSize()));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

