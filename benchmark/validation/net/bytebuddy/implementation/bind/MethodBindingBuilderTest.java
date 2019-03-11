package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding.Unique.of;


public class MethodBindingBuilderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private ParameterList<?> methodParameterList;

    @Mock
    private MethodDelegationBinder.MethodInvoker methodInvoker;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private StackManipulation legalStackManipulation;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private StackManipulation illegalStackManipulation;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testIllegalReturnTypeBinding() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MethodDelegationBinder.MethodBinding methodBinding = builder.build(illegalStackManipulation);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(false));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testLegalReturnTypeBinding() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MethodDelegationBinder.MethodBinding methodBinding = builder.build(legalStackManipulation);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(methodDescription));
        methodBinding.apply(methodVisitor, implementationContext);
        Mockito.verify(legalStackManipulation, Mockito.times(2)).apply(methodVisitor, implementationContext);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testIllegalParameterTypeBinding() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        Mockito.when(methodParameterList.size()).thenReturn(2);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new Object())), CoreMatchers.is(true));
        MatcherAssert.assertThat(builder.append(of(illegalStackManipulation, new Object())), CoreMatchers.is(true));
        MethodDelegationBinder.MethodBinding methodBinding = builder.build(legalStackManipulation);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(false));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testLegalParameterTypeBinding() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        Mockito.when(methodParameterList.size()).thenReturn(2);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new Object())), CoreMatchers.is(true));
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new Object())), CoreMatchers.is(true));
        MethodDelegationBinder.MethodBinding methodBinding = builder.build(legalStackManipulation);
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(methodDescription));
        methodBinding.apply(methodVisitor, implementationContext);
        Mockito.verify(legalStackManipulation, Mockito.times(4)).apply(methodVisitor, implementationContext);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    public void testUniqueIdentification() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        Mockito.when(methodParameterList.size()).thenReturn(2);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.BAR))), CoreMatchers.is(true));
        MethodDelegationBinder.MethodBinding methodBinding = builder.build(legalStackManipulation);
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.FOO)), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodBinding.getTargetParameterIndex(new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.BAR)), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodBinding.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodBinding.getTarget(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testNonUniqueIdentification() throws Exception {
        Mockito.when(methodInvoker.invoke(ArgumentMatchers.any(MethodDescription.class))).thenReturn(legalStackManipulation);
        MethodDelegationBinder.MethodBinding.Builder builder = new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription);
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(builder.append(of(legalStackManipulation, new MethodBindingBuilderTest.Key(MethodBindingBuilderTest.FOO))), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterNumberInequality() throws Exception {
        Mockito.when(methodParameterList.size()).thenReturn(1);
        new MethodDelegationBinder.MethodBinding.Builder(methodInvoker, methodDescription).build(legalStackManipulation);
    }

    private static class Key {
        private final String identifier;

        private Key(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public int hashCode() {
            return identifier.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return ((this) == other) || ((!((other == null) || ((getClass()) != (other.getClass())))) && (identifier.equals(((MethodBindingBuilderTest.Key) (other)).identifier)));
        }
    }
}

