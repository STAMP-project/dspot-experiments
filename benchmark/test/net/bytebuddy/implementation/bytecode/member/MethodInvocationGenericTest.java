package net.bytebuddy.implementation.bytecode.member;


import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodInvocationGenericTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape declaredMethod;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private TypeDescription.Generic methodReturnType;

    @Mock
    private TypeDescription.Generic declaredReturnType;

    @Mock
    private TypeDescription declaredErasure;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription otherType;

    @Mock
    private MethodDescription.SignatureToken token;

    @Test
    public void testGenericMethod() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(methodReturnType.asErasure()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (new MethodInvocation.OfGenericMethod(genericErasure, MethodInvocation.invoke(declaredMethod))))));
    }

    @Test
    public void testGenericMethodErasureEqual() throws Exception {
        Mockito.when(methodReturnType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (MethodInvocation.invoke(declaredMethod)))));
    }

    @Test
    public void testGenericMethodVirtual() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(methodReturnType.asErasure()).thenReturn(genericErasure);
        Mockito.when(genericErasure.asErasure()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).virtual(targetType);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (new StackManipulation.Compound(MethodInvocation.invoke(declaredMethod).virtual(targetType), TypeCasting.to(genericErasure))))));
    }

    @Test
    public void testGenericMethodVirtualErasureEqual() throws Exception {
        Mockito.when(methodReturnType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).virtual(targetType);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(MethodInvocation.invoke(declaredMethod).virtual(targetType)));
    }

    @Test
    public void testGenericMethodSpecial() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(methodReturnType.asErasure()).thenReturn(genericErasure);
        Mockito.when(genericErasure.asErasure()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).special(targetType);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (new StackManipulation.Compound(MethodInvocation.invoke(declaredMethod).special(targetType), TypeCasting.to(genericErasure))))));
    }

    @Test
    public void testGenericMethodSpecialErasureEqual() throws Exception {
        Mockito.when(methodReturnType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).special(targetType);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(MethodInvocation.invoke(declaredMethod).special(targetType)));
    }

    @Test
    public void testGenericMethodDynamic() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(methodReturnType.asErasure()).thenReturn(genericErasure);
        Mockito.when(isInvokeBootstrap()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(MethodInvocationGenericTest.FOO, otherType, Collections.<TypeDescription>emptyList(), Collections.emptyList());
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(MethodInvocation.invoke(declaredMethod).dynamic(MethodInvocationGenericTest.FOO, otherType, Collections.<TypeDescription>emptyList(), Collections.emptyList())));
    }

    @Test
    public void testGenericMethodDynamicErasureEqual() throws Exception {
        Mockito.when(methodReturnType.asErasure()).thenReturn(declaredErasure);
        Mockito.when(isInvokeBootstrap()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(MethodInvocationGenericTest.FOO, otherType, Collections.<TypeDescription>emptyList(), Collections.emptyList());
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(MethodInvocation.invoke(declaredMethod).dynamic(MethodInvocationGenericTest.FOO, otherType, Collections.<TypeDescription>emptyList(), Collections.emptyList())));
    }

    @Test
    public void testIllegal() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(methodReturnType.asErasure()).thenReturn(genericErasure);
        Mockito.when(declaredMethod.isTypeInitializer()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }
}

