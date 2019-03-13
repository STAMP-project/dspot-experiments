package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackSize;
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
public class MethodInvocationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final int ARGUMENT_STACK_SIZE = 1;

    private final StackSize stackSize;

    private final int expectedSize;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private TypeDescription.Generic otherType;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription rawOtherType;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodVisitor methodVisitor;

    public MethodInvocationTest(StackSize stackSize) {
        this.stackSize = stackSize;
        this.expectedSize = (stackSize.getSize()) - (MethodInvocationTest.ARGUMENT_STACK_SIZE);
    }

    @Test
    public void testTypeInitializerInvocation() throws Exception {
        Mockito.when(methodDescription.isTypeInitializer()).thenReturn(true);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testStaticMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESTATIC, MethodInvocationTest.FOO, false);
    }

    @Test
    public void testStaticPrivateMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESTATIC, MethodInvocationTest.FOO, false);
    }

    @Test
    public void testPrivateMethodInvocationVirtualLegacy() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        Mockito.when(implementationContext.getClassFileVersion()).thenReturn(JAVA_V10);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESPECIAL, MethodInvocationTest.FOO, false);
        Mockito.verify(implementationContext).getClassFileVersion();
    }

    @Test
    public void testPrivateMethodInvocationVirtualJava11() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        Mockito.when(implementationContext.getClassFileVersion()).thenReturn(JAVA_V11);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKEVIRTUAL, MethodInvocationTest.FOO, false);
        Mockito.verify(implementationContext).getClassFileVersion();
    }

    @Test
    public void testPrivateMethodInvocationVirtualInterfaceLegacy() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        Mockito.when(implementationContext.getClassFileVersion()).thenReturn(JAVA_V10);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESPECIAL, MethodInvocationTest.FOO, true);
        Mockito.verify(implementationContext).getClassFileVersion();
    }

    @Test
    public void testPrivateMethodInvocationVirtualInterfaceJava11() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        Mockito.when(implementationContext.getClassFileVersion()).thenReturn(JAVA_V11);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKEINTERFACE, MethodInvocationTest.FOO, true);
        Mockito.verify(implementationContext).getClassFileVersion();
    }

    @Test
    public void testConstructorMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESPECIAL, MethodInvocationTest.FOO, false);
    }

    @Test
    public void testPublicMethodInvocation() throws Exception {
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKEVIRTUAL, MethodInvocationTest.FOO, false);
    }

    @Test
    public void testInterfaceMethodInvocation() throws Exception {
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKEINTERFACE, MethodInvocationTest.FOO, true);
    }

    @Test
    public void testStaticInterfaceMethodInvocation() throws Exception {
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKESTATIC, MethodInvocationTest.FOO, true);
    }

    @Test
    public void testDefaultInterfaceMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isDefaultMethod()).thenReturn(true);
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription), Opcodes.INVOKEINTERFACE, MethodInvocationTest.FOO, true);
    }

    @Test
    public void testExplicitlySpecialDefaultInterfaceMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isDefaultMethod()).thenReturn(true);
        Mockito.when(methodDescription.isSpecializableFor(declaringType)).thenReturn(true);
        Mockito.when(declaringType.isInterface()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription).special(declaringType), Opcodes.INVOKESPECIAL, MethodInvocationTest.FOO, true);
    }

    @Test
    public void testExplicitlySpecialDefaultInterfaceMethodInvocationOnOther() throws Exception {
        Mockito.when(methodDescription.isDefaultMethod()).thenReturn(true);
        Mockito.when(methodDescription.isSpecializableFor(rawOtherType)).thenReturn(false);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).special(rawOtherType).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testExplicitlySpecialMethodInvocation() throws Exception {
        Mockito.when(methodDescription.isSpecializableFor(rawOtherType)).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription).special(rawOtherType), Opcodes.INVOKESPECIAL, MethodInvocationTest.BAZ, false);
    }

    @Test
    public void testIllegalSpecialMethodInvocation() throws Exception {
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).special(rawOtherType).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testExplicitlyVirtualMethodInvocation() throws Exception {
        Mockito.when(declaringType.isAssignableFrom(rawOtherType)).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription).virtual(rawOtherType), Opcodes.INVOKEVIRTUAL, MethodInvocationTest.BAZ, false);
    }

    @Test
    public void testExplicitlyVirtualMethodInvocationOfInterface() throws Exception {
        Mockito.when(declaringType.isAssignableFrom(rawOtherType)).thenReturn(true);
        Mockito.when(rawOtherType.isInterface()).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription).virtual(rawOtherType), Opcodes.INVOKEINTERFACE, MethodInvocationTest.BAZ, true);
    }

    @Test
    public void testExplicitlyVirtualMethodInvocationOfInterfaceOfObjectMethod() throws Exception {
        Mockito.when(declaringType.isAssignableFrom(rawOtherType)).thenReturn(true);
        Mockito.when(rawOtherType.isInterface()).thenReturn(true);
        Mockito.when(declaringType.represents(Object.class)).thenReturn(true);
        assertInvocation(MethodInvocation.invoke(methodDescription).virtual(rawOtherType), Opcodes.INVOKEVIRTUAL, MethodInvocationTest.FOO, false);
    }

    @Test
    public void testStaticVirtualInvocation() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).virtual(rawOtherType).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testPrivateVirtualInvocation() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).virtual(rawOtherType).isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testPrivateVirtualInvocationOnSelf() throws Exception {
        Mockito.when(methodDescription.isPrivate()).thenReturn(true);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).virtual(declaringType).isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testConstructorVirtualInvocation() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        MatcherAssert.assertThat(MethodInvocation.invoke(methodDescription).virtual(rawOtherType).isValid(), CoreMatchers.is(false));
    }
}

