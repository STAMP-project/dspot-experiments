package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
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
import org.objectweb.asm.Type;


public class ClassConstantReferenceTest {
    private static final String FOO = "Lfoo;";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testClassConstantModernVisible() throws Exception {
        Mockito.when(typeDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        Mockito.when(typeDescription.getDescriptor()).thenReturn(ClassConstantReferenceTest.FOO);
        StackManipulation stackManipulation = ClassConstant.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(typeDescription).getDescriptor();
        Mockito.verify(typeDescription).isVisibleTo(instrumentedType);
        Mockito.verify(typeDescription).isPrimitive();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(methodVisitor).visitLdcInsn(Type.getType(ClassConstantReferenceTest.FOO));
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testClassConstantModernInvisible() throws Exception {
        Mockito.when(typeDescription.isVisibleTo(instrumentedType)).thenReturn(false);
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(true);
        Mockito.when(typeDescription.getName()).thenReturn(ClassConstantReferenceTest.FOO);
        Mockito.when(typeDescription.isPrimitiveWrapper()).thenReturn(false);
        StackManipulation stackManipulation = ClassConstant.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(typeDescription).isPrimitive();
        Mockito.verify(typeDescription).isVisibleTo(instrumentedType);
        Mockito.verify(typeDescription).getName();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(methodVisitor).visitLdcInsn(ClassConstantReferenceTest.FOO);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class), "forName", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class)), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testClassConstantLegacy() throws Exception {
        Mockito.when(typeDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5)).thenReturn(false);
        Mockito.when(typeDescription.getName()).thenReturn(ClassConstantReferenceTest.FOO);
        StackManipulation stackManipulation = ClassConstant.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(typeDescription).getName();
        Mockito.verify(typeDescription).isPrimitive();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(methodVisitor).visitLdcInsn(ClassConstantReferenceTest.FOO);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class), "forName", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class)), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

