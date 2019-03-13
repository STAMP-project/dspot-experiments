package net.bytebuddy.implementation.bytecode.member;


import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bytecode.member.MethodInvocation.IllegalInvocation.INSTANCE;


public class MethodInvocationOtherTest {
    private static final String FOO = "foo";

    @Test(expected = IllegalStateException.class)
    public void testIllegal() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(false));
        MatcherAssert.assertThat(INSTANCE.special(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((StackManipulation) (StackManipulation.Illegal.INSTANCE))));
        MatcherAssert.assertThat(INSTANCE.virtual(Mockito.mock(TypeDescription.class)), CoreMatchers.is(((StackManipulation) (StackManipulation.Illegal.INSTANCE))));
        MatcherAssert.assertThat(INSTANCE.dynamic(MethodInvocationOtherTest.FOO, Mockito.mock(TypeDescription.class), Mockito.mock(TypeList.class), Mockito.mock(List.class)), CoreMatchers.is(((StackManipulation) (StackManipulation.Illegal.INSTANCE))));
        MatcherAssert.assertThat(INSTANCE.onHandle(null), CoreMatchers.is(((StackManipulation) (StackManipulation.Illegal.INSTANCE))));
        INSTANCE.apply(Mockito.mock(MethodVisitor.class), Mockito.mock(Implementation.Context.class));
    }
}

