package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
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

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class VoidAwareAssignerVoidToNonVoidTest {
    private final Class<?> targetType;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private TypeDescription.Generic target;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public VoidAwareAssignerVoidToNonVoidTest(Class<?> targetType, int opcode) {
        this.targetType = targetType;
        this.opcode = opcode;
    }

    @Test
    public void testAssignDefaultValue() throws Exception {
        Assigner voidAwareAssigner = new VoidAwareAssigner(chainedAssigner);
        StackManipulation stackManipulation = voidAwareAssigner.assign(source, target, DYNAMIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(StackSize.of(targetType).getSize()));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(StackSize.of(targetType).getSize()));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test(expected = IllegalStateException.class)
    public void testAssignNoDefaultValue() throws Exception {
        Assigner voidAwareAssigner = new VoidAwareAssigner(chainedAssigner);
        StackManipulation stackManipulation = voidAwareAssigner.assign(source, target, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        stackManipulation.apply(methodVisitor, implementationContext);
    }
}

