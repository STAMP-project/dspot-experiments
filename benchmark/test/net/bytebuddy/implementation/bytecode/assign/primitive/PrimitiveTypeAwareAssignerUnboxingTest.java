package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


@RunWith(Parameterized.class)
public class PrimitiveTypeAwareAssignerUnboxingTest {
    private final Class<?> sourceType;

    private final Class<?> targetType;

    private final boolean assignable;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic sourceTypeDescription;

    @Mock
    private TypeDescription.Generic targetTypeDescription;

    @Mock
    private Assigner chainedAssigner;

    private Assigner primitiveAssigner;

    public PrimitiveTypeAwareAssignerUnboxingTest(Class<?> sourceType, Class<?> targetType, boolean assignable) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.assignable = assignable;
    }

    @Test
    public void testUnboxingAssignment() throws Exception {
        StackManipulation stackManipulation = primitiveAssigner.assign(sourceTypeDescription, targetTypeDescription, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(assignable));
        Mockito.verify(sourceTypeDescription, Mockito.atLeast(0)).represents(ArgumentMatchers.any(Class.class));
        Mockito.verify(sourceTypeDescription).represents(sourceType);
        Mockito.verify(sourceTypeDescription, Mockito.atLeast(1)).isPrimitive();
        Mockito.verifyNoMoreInteractions(sourceTypeDescription);
        Mockito.verify(targetTypeDescription, Mockito.atLeast(0)).represents(ArgumentMatchers.any(Class.class));
        Mockito.verify(targetTypeDescription).represents(targetType);
        Mockito.verify(targetTypeDescription, Mockito.atLeast(1)).isPrimitive();
        Mockito.verifyNoMoreInteractions(targetTypeDescription);
        Mockito.verifyZeroInteractions(chainedAssigner);
    }
}

