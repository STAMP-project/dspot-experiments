package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.description.type.TypeDescription;
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
import org.objectweb.asm.Opcodes;


@RunWith(Parameterized.class)
public class ClassConstantPrimitiveTest {
    private final TypeDescription primitiveType;

    private final TypeDescription wrapperType;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public ClassConstantPrimitiveTest(Class<?> primitiveType, Class<?> wrapperType) {
        this.primitiveType = of(primitiveType);
        this.wrapperType = of(wrapperType);
    }

    @Test
    public void testClassConstant() throws Exception {
        StackManipulation stackManipulation = ClassConstant.of(primitiveType);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitFieldInsn(Opcodes.GETSTATIC, wrapperType.getInternalName(), "TYPE", "Ljava/lang/Class;");
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

