package net.bytebuddy.dynamic.scaffold;


import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


public class TypeWriterDefaultForInliningWithFullProcessingInitializationHandlerAppendingFrameWriterNoOpTest {
    @Test
    public void testFrame() throws Exception {
        WithFullProcessing.onFrame(0, 0);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        WithFullProcessing.emitFrame(methodVisitor);
        Mockito.verifyZeroInteractions(methodVisitor);
    }
}

