package net.bytebuddy.utility.visitor;


import java.lang.reflect.Method;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


@RunWith(Parameterized.class)
public class ExceptionTableSensitiveMethodVisitorTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    private final int api;

    private final String name;

    private final Class<?>[] type;

    private final Object[] argument;

    public ExceptionTableSensitiveMethodVisitorTest(int api, String name, Class<?>[] type, Object[] argument) {
        this.api = api;
        this.name = name;
        this.type = type;
        this.argument = argument;
    }

    @Test
    public void testCallback() throws Exception {
        Method method = MethodVisitor.class.getDeclaredMethod(name, type);
        ExceptionTableSensitiveMethodVisitorTest.PseudoVisitor pseudoVisitor = new ExceptionTableSensitiveMethodVisitorTest.PseudoVisitor(api, methodVisitor);
        method.invoke(pseudoVisitor, argument);
        method.invoke(pseudoVisitor, argument);
        method.invoke(Mockito.verify(methodVisitor, Mockito.times(2)), argument);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        pseudoVisitor.check();
    }

    private static class PseudoVisitor extends ExceptionTableSensitiveMethodVisitor {
        private boolean called;

        public PseudoVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        protected void onAfterExceptionTable() {
            if (called) {
                throw new AssertionError();
            }
            called = true;
            Mockito.verifyZeroInteractions(mv);
        }

        protected void check() {
            if (!(called)) {
                throw new AssertionError();
            }
        }
    }
}

