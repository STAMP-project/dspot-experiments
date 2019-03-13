package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
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


@RunWith(Parameterized.class)
public class FloatConstantTest {
    private final float value;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public FloatConstantTest(float value) {
        this.value = value;
    }

    @Test
    public void testBiPush() throws Exception {
        StackManipulation floatConstant = FloatConstant.forValue(value);
        MatcherAssert.assertThat(floatConstant.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = floatConstant.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitLdcInsn(value);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

