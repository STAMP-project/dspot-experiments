package net.bytebuddy.dynamic.scaffold.subclass;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.AbstractImplementationTargetTest;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class SubclassImplementationTargetTest extends AbstractImplementationTargetTest {
    private static final String BAR = "bar";

    private static final String BAZ = "baz";

    @Mock
    private TypeDescription.Generic superClass;

    @Mock
    private TypeDescription rawSuperClass;

    @Mock
    private MethodDescription.InGenericShape superClassConstructor;

    @Mock
    private MethodDescription.InDefinedShape definedSuperClassConstructor;

    @Mock
    private MethodDescription.SignatureToken superConstructorToken;

    @Test
    public void testSuperTypeMethodIsInvokable() throws Exception {
        Mockito.when(invokableMethod.isSpecializableFor(rawSuperClass)).thenReturn(true);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(invokableToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (invokableMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(rawSuperClass));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, SubclassImplementationTargetTest.BAR, AbstractImplementationTargetTest.FOO, AbstractImplementationTargetTest.QUX, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test
    public void testNonSpecializableSuperClassMethodIsNotInvokable() throws Exception {
        Mockito.when(invokableMethod.isSpecializableFor(rawSuperClass)).thenReturn(false);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(invokableToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testSuperConstructorIsInvokable() throws Exception {
        Mockito.when(invokableMethod.isConstructor()).thenReturn(true);
        Mockito.when(definedSuperClassConstructor.isSpecializableFor(rawSuperClass)).thenReturn(true);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(superConstructorToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (superClassConstructor))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(rawSuperClass));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, SubclassImplementationTargetTest.BAR, AbstractImplementationTargetTest.QUX, SubclassImplementationTargetTest.BAZ, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }
}

