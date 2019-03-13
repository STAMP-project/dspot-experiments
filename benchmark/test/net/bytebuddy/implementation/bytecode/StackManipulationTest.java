package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bytecode.StackManipulation.Trivial.INSTANCE;


public class StackManipulationTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testLegalIsValid() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalIsNotValid() throws Exception {
        MatcherAssert.assertThat(StackManipulation.Illegal.INSTANCE.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testLegalIsApplicable() throws Exception {
        StackManipulation.Size size = INSTANCE.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalIsNotApplicable() throws Exception {
        StackManipulation.Illegal.INSTANCE.apply(methodVisitor, implementationContext);
    }
}

