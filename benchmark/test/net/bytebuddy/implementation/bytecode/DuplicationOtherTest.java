package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.description.type.TypeDefinition;
import org.junit.Test;
import org.mockito.Mockito;


public class DuplicationOtherTest {
    @Test(expected = IllegalStateException.class)
    public void testZeroFlip() throws Exception {
        Duplication.ZERO.flipOver(Mockito.mock(TypeDefinition.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleToZeroFlip() throws Exception {
        TypeDefinition typeDefinition = Mockito.mock(TypeDefinition.class);
        Mockito.when(typeDefinition.getStackSize()).thenReturn(StackSize.ZERO);
        Duplication.SINGLE.flipOver(typeDefinition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleToZeroFlip() throws Exception {
        TypeDefinition typeDefinition = Mockito.mock(TypeDefinition.class);
        Mockito.when(typeDefinition.getStackSize()).thenReturn(StackSize.ZERO);
        Duplication.DOUBLE.flipOver(typeDefinition);
    }
}

