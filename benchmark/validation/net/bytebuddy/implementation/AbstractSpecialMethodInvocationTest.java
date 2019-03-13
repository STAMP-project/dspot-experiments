package net.bytebuddy.implementation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public abstract class AbstractSpecialMethodInvocationTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription otherMethod;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private MethodDescription.SignatureToken otherToken;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription otherType;

    @Mock
    private StackManipulation stackManipulation;

    @Test
    public void testEquality() throws Exception {
        MatcherAssert.assertThat(make(methodDescription, typeDescription).hashCode(), CoreMatchers.is(new Implementation.SpecialMethodInvocation.Simple(methodDescription, typeDescription, stackManipulation).hashCode()));
        MatcherAssert.assertThat(make(methodDescription, typeDescription), CoreMatchers.is(((Implementation.SpecialMethodInvocation) (new Implementation.SpecialMethodInvocation.Simple(methodDescription, typeDescription, stackManipulation)))));
    }

    @Test
    public void testTypeInequality() throws Exception {
        MatcherAssert.assertThat(make(methodDescription, typeDescription).hashCode(), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(methodDescription, otherType, stackManipulation).hashCode()));
        MatcherAssert.assertThat(make(methodDescription, typeDescription), CoreMatchers.not(((Implementation.SpecialMethodInvocation) (new Implementation.SpecialMethodInvocation.Simple(methodDescription, otherType, stackManipulation)))));
    }

    @Test
    public void testTokenInequality() throws Exception {
        MatcherAssert.assertThat(make(methodDescription, typeDescription).hashCode(), CoreMatchers.not(new Implementation.SpecialMethodInvocation.Simple(otherMethod, typeDescription, stackManipulation).hashCode()));
        MatcherAssert.assertThat(make(methodDescription, typeDescription), CoreMatchers.not(((Implementation.SpecialMethodInvocation) (new Implementation.SpecialMethodInvocation.Simple(otherMethod, typeDescription, stackManipulation)))));
    }

    @Test
    public void testValidity() throws Exception {
        MatcherAssert.assertThat(make(methodDescription, typeDescription).isValid(), CoreMatchers.is(true));
    }
}

