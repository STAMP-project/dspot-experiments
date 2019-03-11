package net.bytebuddy.implementation;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.Implementation.SpecialMethodInvocation.Illegal.INSTANCE;


public class ImplementationSpecialMethodInvocationIllegalTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testIsInvalid() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodDescriptionIllegal() throws Exception {
        INSTANCE.getMethodDescription();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeDescriptionIllegal() throws Exception {
        INSTANCE.getTypeDescription();
    }

    @Test(expected = IllegalStateException.class)
    public void testApplicationIllegal() throws Exception {
        INSTANCE.apply(methodVisitor, implementationContext);
    }
}

