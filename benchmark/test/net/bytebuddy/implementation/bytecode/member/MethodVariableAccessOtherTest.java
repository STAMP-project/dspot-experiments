package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


public class MethodVariableAccessOtherTest {
    @Test(expected = IllegalArgumentException.class)
    public void testVoidArgument() throws Exception {
        TypeDescription voidTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(voidTypeDescription.isPrimitive()).thenReturn(true);
        Mockito.when(voidTypeDescription.represents(void.class)).thenReturn(true);
        MethodVariableAccess.of(voidTypeDescription);
    }

    @Test
    public void testIncrement() throws Exception {
        StackManipulation stackManipulation = MethodVariableAccess.INTEGER.increment(4, 1);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitIincInsn(4, 1);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testThisReference() throws Exception {
        MatcherAssert.assertThat(MethodVariableAccess.loadThis(), FieldByFieldComparison.hasPrototype(MethodVariableAccess.REFERENCE.loadFrom(0)));
    }

    @Test
    public void testLoadParameter() throws Exception {
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(of(int.class));
        Mockito.when(parameterDescription.getOffset()).thenReturn(4);
        MatcherAssert.assertThat(MethodVariableAccess.load(parameterDescription), FieldByFieldComparison.hasPrototype(MethodVariableAccess.INTEGER.loadFrom(4)));
    }

    @Test
    public void testStoreParameter() throws Exception {
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(of(int.class));
        Mockito.when(parameterDescription.getOffset()).thenReturn(4);
        MatcherAssert.assertThat(MethodVariableAccess.store(parameterDescription), FieldByFieldComparison.hasPrototype(MethodVariableAccess.INTEGER.storeAt(4)));
    }

    @Test
    public void testIncrementParameter() throws Exception {
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(of(int.class));
        Mockito.when(parameterDescription.getOffset()).thenReturn(4);
        MatcherAssert.assertThat(MethodVariableAccess.increment(parameterDescription, 42), FieldByFieldComparison.hasPrototype(MethodVariableAccess.INTEGER.increment(4, 42)));
    }

    @Test(expected = IllegalStateException.class)
    public void testReferenceCannotIncrement() throws Exception {
        MethodVariableAccess.REFERENCE.increment(0, 1);
    }

    @Test(expected = IllegalStateException.class)
    public void testLongCannotIncrement() throws Exception {
        MethodVariableAccess.LONG.increment(0, 1);
    }

    @Test(expected = IllegalStateException.class)
    public void testFloatCannotIncrement() throws Exception {
        MethodVariableAccess.FLOAT.increment(0, 1);
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleCannotIncrement() throws Exception {
        MethodVariableAccess.DOUBLE.increment(0, 1);
    }
}

