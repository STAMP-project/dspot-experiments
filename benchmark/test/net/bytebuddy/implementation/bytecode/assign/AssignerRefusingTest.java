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
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Refusing.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.of;


@RunWith(Parameterized.class)
public class AssignerRefusingTest {
    private final boolean dynamicallyTyped;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic first;

    @Mock
    private TypeDescription.Generic second;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public AssignerRefusingTest(boolean dynamicallyTyped) {
        this.dynamicallyTyped = dynamicallyTyped;
    }

    @Test
    public void testAssignmentEqual() throws Exception {
        StackManipulation stackManipulation = INSTANCE.assign(first, first, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testAssignmentNotEqual() throws Exception {
        StackManipulation stackManipulation = INSTANCE.assign(first, second, of(dynamicallyTyped));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }
}

