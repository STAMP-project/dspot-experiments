package net.bytebuddy.implementation.bytecode.assign.primitive;


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


@RunWith(Parameterized.class)
public class PrimitiveWideningDelegateIllegalTest {
    private final TypeDescription sourceTypeDescription;

    private final TypeDescription targetTypeDescription;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public PrimitiveWideningDelegateIllegalTest(Class<?> sourceType, Class<?> targetType) {
        sourceTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(sourceTypeDescription.isPrimitive()).thenReturn(true);
        Mockito.when(sourceTypeDescription.represents(sourceType)).thenReturn(true);
        targetTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(targetTypeDescription.isPrimitive()).thenReturn(true);
        Mockito.when(targetTypeDescription.represents(targetType)).thenReturn(true);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBoolean() throws Exception {
        StackManipulation stackManipulation = PrimitiveWideningDelegate.forPrimitive(sourceTypeDescription).widenTo(targetTypeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        stackManipulation.apply(methodVisitor, implementationContext);
    }
}

