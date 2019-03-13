package net.bytebuddy.implementation.bind;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.TerminationHandler.Default.DROPPING;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.TerminationHandler.Default.RETURNING;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class MethodDelegationBinderTerminationHandlerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Assigner assigner;

    @Mock
    private MethodDescription source;

    @Mock
    private MethodDescription target;

    @Mock
    private TypeDescription sourceType;

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic genericSourceType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    @Mock
    private StackManipulation stackManipulation;

    @Test
    public void testDropping() throws Exception {
        Mockito.when(target.getReturnType()).thenReturn(genericSourceType);
        Mockito.when(genericSourceType.getStackSize()).thenReturn(StackSize.SINGLE);
        StackManipulation stackManipulation = DROPPING.resolve(assigner, STATIC, source, target);
        MatcherAssert.assertThat(stackManipulation, CoreMatchers.is(((StackManipulation) (Removal.SINGLE))));
        Mockito.verify(genericSourceType).getStackSize();
        Mockito.verifyNoMoreInteractions(genericSourceType);
    }

    @Test
    public void testReturning() throws Exception {
        Mockito.when(source.getReturnType()).thenReturn(genericSourceType);
        Mockito.when(target.getReturnType()).thenReturn(genericTargetType);
        Mockito.when(genericSourceType.asErasure()).thenReturn(sourceType);
        Mockito.when(genericTargetType.asErasure()).thenReturn(targetType);
        Mockito.when(assigner.assign(genericTargetType, genericSourceType, STATIC)).thenReturn(stackManipulation);
        StackManipulation stackManipulation = RETURNING.resolve(assigner, STATIC, source, target);
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (new StackManipulation.Compound(this.stackManipulation, MethodReturn.REFERENCE)))));
        Mockito.verify(assigner).assign(genericTargetType, genericSourceType, STATIC);
    }
}

