package net.bytebuddy.implementation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class ImplementationSpecialMethodInvocationSimpleTest extends AbstractSpecialMethodInvocationTest {
    @Test
    public void testHashCode() throws Exception {
        MethodDescription firstMethod = Mockito.mock(MethodDescription.class);
        MethodDescription secondMethod = Mockito.mock(MethodDescription.class);
        MethodDescription.SignatureToken firstToken = Mockito.mock(MethodDescription.SignatureToken.class);
        MethodDescription.SignatureToken secondToken = Mockito.mock(MethodDescription.SignatureToken.class);
        Mockito.when(firstMethod.asSignatureToken()).thenReturn(firstToken);
        Mockito.when(secondMethod.asSignatureToken()).thenReturn(secondToken);
        TypeDescription firstType = Mockito.mock(TypeDescription.class);
        TypeDescription secondType = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)).hashCode(), CoreMatchers.is(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)).hashCode()));
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)).hashCode(), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(secondMethod, firstType, Mockito.mock(StackManipulation.class)).hashCode()));
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)).hashCode(), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(firstMethod, secondType, Mockito.mock(StackManipulation.class)).hashCode()));
    }

    @Test
    public void testEquality() throws Exception {
        MethodDescription firstMethod = Mockito.mock(MethodDescription.class);
        MethodDescription secondMethod = Mockito.mock(MethodDescription.class);
        MethodDescription.SignatureToken firstToken = Mockito.mock(MethodDescription.SignatureToken.class);
        MethodDescription.SignatureToken secondToken = Mockito.mock(MethodDescription.SignatureToken.class);
        Mockito.when(firstMethod.asSignatureToken()).thenReturn(firstToken);
        Mockito.when(secondMethod.asSignatureToken()).thenReturn(secondToken);
        TypeDescription firstType = Mockito.mock(TypeDescription.class);
        TypeDescription secondType = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)), CoreMatchers.is(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class))));
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(secondMethod, firstType, Mockito.mock(StackManipulation.class))));
        MatcherAssert.assertThat(new Implementation.SpecialMethodInvocation.Simple(firstMethod, firstType, Mockito.mock(StackManipulation.class)), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(firstMethod, secondType, Mockito.mock(StackManipulation.class))));
    }
}

