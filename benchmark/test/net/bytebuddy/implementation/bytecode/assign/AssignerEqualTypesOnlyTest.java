package net.bytebuddy.implementation.bytecode.assign;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
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

import static net.bytebuddy.implementation.bytecode.assign.Assigner.EqualTypesOnly.ERASURE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.EqualTypesOnly.GENERIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.of;


@RunWith(Parameterized.class)
public class AssignerEqualTypesOnlyTest {
    private final boolean dynamicallyTyped;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic first;

    @Mock
    private TypeDescription.Generic second;

    @Mock
    private TypeDescription firstRaw;

    @Mock
    private TypeDescription secondRaw;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public AssignerEqualTypesOnlyTest(boolean dynamicallyTyped) {
        this.dynamicallyTyped = dynamicallyTyped;
    }

    @Test
    public void testAssignmentGenericEqual() throws Exception {
        StackManipulation stackManipulation = GENERIC.assign(first, first, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(first);
    }

    @Test
    public void testAssignmentGenericNotEqual() throws Exception {
        StackManipulation stackManipulation = GENERIC.assign(first, second, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(first);
        Mockito.verifyZeroInteractions(second);
    }

    @Test
    public void testAssignmentErausreEqual() throws Exception {
        StackManipulation stackManipulation = ERASURE.assign(first, first, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(first, Mockito.times(2)).asErasure();
        Mockito.verifyNoMoreInteractions(first);
    }

    @Test
    public void testAssignmentErasureNotEqual() throws Exception {
        StackManipulation stackManipulation = ERASURE.assign(first, second, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
        Mockito.verify(first).asErasure();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).asErasure();
        Mockito.verifyNoMoreInteractions(second);
    }
}

