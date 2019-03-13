package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class PipeBinderTest extends AbstractAnnotationBinderTest<Pipe> {
    private TargetMethodAnnotationDrivenBinder.ParameterBinder<Pipe> binder;

    @Mock
    private MethodDescription targetMethod;

    @Mock
    private TypeDescription targetMethodType;

    @Mock
    private TypeDescription.Generic genericTargetMethodType;

    public PipeBinderTest() {
        super(Pipe.class);
    }

    @Test
    public void testParameterBinding() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericTargetMethodType);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = binder.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testCannotPipeStaticMethod() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericTargetMethodType);
        Mockito.when(source.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = binder.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterBindingOnIllegalTargetTypeThrowsException() throws Exception {
        TypeDescription.Generic targetType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription rawTargetType = Mockito.mock(TypeDescription.class);
        Mockito.when(targetType.asErasure()).thenReturn(rawTargetType);
        Mockito.when(target.getType()).thenReturn(targetType);
        binder.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

