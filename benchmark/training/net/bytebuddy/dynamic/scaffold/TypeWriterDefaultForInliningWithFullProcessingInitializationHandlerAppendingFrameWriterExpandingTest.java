package net.bytebuddy.dynamic.scaffold;


import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TypeWriterDefaultForInliningWithFullProcessingInitializationHandlerAppendingFrameWriterExpandingTest {
    @Test
    public void testFrame() throws Exception {
        WithFullProcessing.onFrame(0, 0);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        WithFullProcessing.emitFrame(methodVisitor);
        Mockito.verify(methodVisitor).visitFrame(Opcodes.F_NEW, 0, new Object[0], 0, new Object[0]);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

