package net.bytebuddy.utility.visitor;


import java.util.Arrays;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class StackAwareMethodVisitorTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodVisitor methodVisitor;

    @Test
    public void testDrainSingleSize() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.drainStack();
        Mockito.verify(this.methodVisitor).visitLdcInsn(1);
        Mockito.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainDoubleSize() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1L);
        methodVisitor.drainStack();
        Mockito.verify(this.methodVisitor).visitLdcInsn(1L);
        Mockito.verify(this.methodVisitor).visitInsn(Opcodes.POP2);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainOrder() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.visitLdcInsn(1L);
        methodVisitor.drainStack();
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1L);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP2);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainRetainTopSingle() throws Exception {
        Mockito.when(methodDescription.getStackSize()).thenReturn(42);
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1L);
        methodVisitor.visitLdcInsn(1);
        MatcherAssert.assertThat(methodVisitor.drainStack(Opcodes.ISTORE, Opcodes.ILOAD, StackSize.SINGLE), CoreMatchers.is(43));
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1L);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ISTORE, 42);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP2);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ILOAD, 42);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainRetainTopDouble() throws Exception {
        Mockito.when(methodDescription.getStackSize()).thenReturn(42);
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.visitLdcInsn(1L);
        MatcherAssert.assertThat(methodVisitor.drainStack(Opcodes.LSTORE, Opcodes.LLOAD, StackSize.DOUBLE), CoreMatchers.is(44));
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1L);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.LSTORE, 42);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.LLOAD, 42);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainFreeListOnly() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 41);
        methodVisitor.visitLdcInsn(1);
        MatcherAssert.assertThat(methodVisitor.drainStack(Opcodes.ISTORE, Opcodes.ILOAD, StackSize.SINGLE), CoreMatchers.is(0));
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ISTORE, 41);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testDrainFreeList() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 41);
        methodVisitor.visitLdcInsn(1);
        methodVisitor.visitLdcInsn(1);
        MatcherAssert.assertThat(methodVisitor.drainStack(Opcodes.ISTORE, Opcodes.ILOAD, StackSize.SINGLE), CoreMatchers.is(43));
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ISTORE, 41);
        inOrder.verify(this.methodVisitor, Mockito.times(2)).visitLdcInsn(1);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ISTORE, 42);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        inOrder.verify(this.methodVisitor).visitVarInsn(Opcodes.ILOAD, 42);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testManualRegistration() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        Label label = new Label();
        methodVisitor.register(label, Arrays.asList(StackSize.DOUBLE, StackSize.SINGLE));
        methodVisitor.visitLabel(label);
        methodVisitor.drainStack();
        InOrder inOrder = Mockito.inOrder(this.methodVisitor);
        inOrder.verify(this.methodVisitor).visitLabel(label);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        inOrder.verify(this.methodVisitor).visitInsn(Opcodes.POP2);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }

    @Test
    public void testStackCanUnderflow() throws Exception {
        StackAwareMethodVisitor methodVisitor = new StackAwareMethodVisitor(this.methodVisitor, methodDescription);
        methodVisitor.visitInsn(Opcodes.POP);
        methodVisitor.drainStack();
        Mockito.verify(this.methodVisitor).visitInsn(Opcodes.POP);
        Mockito.verifyNoMoreInteractions(this.methodVisitor);
    }
}

