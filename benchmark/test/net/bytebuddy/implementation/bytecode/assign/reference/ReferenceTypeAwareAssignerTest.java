package net.bytebuddy.implementation.bytecode.assign.reference;


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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class ReferenceTypeAwareAssignerTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private TypeDescription.Generic target;

    @Mock
    private TypeDescription rawSource;

    @Mock
    private TypeDescription rawTarget;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testMutualAssignable() throws Exception {
        defineAssignability(true, true);
        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(source, target, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testSourceToTargetAssignable() throws Exception {
        defineAssignability(true, false);
        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(source, target, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test(expected = IllegalStateException.class)
    public void testTargetToSourceAssignable() throws Exception {
        defineAssignability(false, true);
        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(source, target, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        stackManipulation.apply(methodVisitor, implementationContext);
    }

    @Test
    public void testTargetToSourceAssignableRuntimeType() throws Exception {
        defineAssignability(false, false);
        Mockito.when(rawTarget.getInternalName()).thenReturn(ReferenceTypeAwareAssignerTest.FOO);
        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(source, target, DYNAMIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.CHECKCAST, ReferenceTypeAwareAssignerTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testPrimitiveAssignabilityWhenEqual() throws Exception {
        TypeDescription.Generic primitiveType = of(int.class);// Note: cannot mock equals

        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(primitiveType, primitiveType, DYNAMIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveAssignabilityWhenNotEqual() throws Exception {
        TypeDescription.Generic primitiveType = of(int.class);// Note: cannot mock equals

        TypeDescription.Generic otherPrimitiveType = of(long.class);// Note: cannot mock equals

        StackManipulation stackManipulation = ReferenceTypeAwareAssigner.INSTANCE.assign(primitiveType, otherPrimitiveType, DYNAMIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        stackManipulation.apply(methodVisitor, implementationContext);
    }
}

