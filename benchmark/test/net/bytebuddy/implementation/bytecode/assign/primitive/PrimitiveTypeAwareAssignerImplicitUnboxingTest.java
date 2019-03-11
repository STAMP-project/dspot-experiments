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

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;


@RunWith(Parameterized.class)
public class PrimitiveTypeAwareAssignerImplicitUnboxingTest {
    private final Class<?> sourceType;

    private final Class<?> wrapperType;

    private final Class<?> targetType;

    private final boolean assignable;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private TypeDescription.Generic target;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private StackManipulation chainedStackManipulation;

    private Assigner primitiveAssigner;

    public PrimitiveTypeAwareAssignerImplicitUnboxingTest(Class<?> sourceType, Class<?> wrapperType, Class<?> targetType, boolean assignable) {
        this.sourceType = sourceType;
        this.wrapperType = wrapperType;
        this.targetType = targetType;
        this.assignable = assignable;
    }

    @Test
    public void testImplicitUnboxingAssignment() {
        StackManipulation stackManipulation = primitiveAssigner.assign(source, target, DYNAMIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(assignable));
        Mockito.verify(chainedStackManipulation).isValid();
        Mockito.verifyNoMoreInteractions(chainedStackManipulation);
        Mockito.verify(source, Mockito.atLeast(0)).represents(ArgumentMatchers.any(Class.class));
        Mockito.verify(source, Mockito.atLeast(1)).isPrimitive();
        Mockito.verify(source).asGenericType();
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verify(target, Mockito.atLeast(0)).represents(ArgumentMatchers.any(Class.class));
        Mockito.verify(target, Mockito.atLeast(1)).isPrimitive();
        Mockito.verifyNoMoreInteractions(target);
        Mockito.verify(chainedAssigner).assign(source, of(wrapperType), DYNAMIC);
        Mockito.verifyNoMoreInteractions(chainedAssigner);
    }
}

