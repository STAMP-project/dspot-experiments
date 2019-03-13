package net.bytebuddy.utility.visitor;


import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


public class LineNumberPrependingMethodVisitorTest {
    private static final int LINE = 42;

    @Test
    public void testPrepending() throws Exception {
        MethodVisitor delegate = Mockito.mock(MethodVisitor.class);
        LineNumberPrependingMethodVisitor methodVisitor = new LineNumberPrependingMethodVisitor(delegate);
        methodVisitor.onAfterExceptionTable();
        Label label = new Label();
        methodVisitor.visitLineNumber(LineNumberPrependingMethodVisitorTest.LINE, label);
        Mockito.verify(delegate, Mockito.times(2)).visitLabel(ArgumentMatchers.any(Label.class));
        Mockito.verify(delegate).visitLineNumber(ArgumentMatchers.eq(LineNumberPrependingMethodVisitorTest.LINE), AdditionalMatchers.not(ArgumentMatchers.eq(label)));
        Mockito.verifyNoMoreInteractions(delegate);
    }
}

