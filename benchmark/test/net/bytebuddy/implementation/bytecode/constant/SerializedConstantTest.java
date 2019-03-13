package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


public class SerializedConstantTest {
    private static final String FOO = "foo";

    @Test
    public void testNullValue() throws Exception {
        Assert.assertThat(SerializedConstant.of(null), CoreMatchers.is(((StackManipulation) (NullConstant.INSTANCE))));
    }

    @Test
    public void testSerialization() throws Exception {
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        SerializedConstant.of(SerializedConstantTest.FOO).apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitLdcInsn(ArgumentMatchers.contains(SerializedConstantTest.FOO));
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

