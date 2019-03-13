package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.Argument.Binder.INSTANCE;
import static net.bytebuddy.implementation.bind.annotation.Argument.BindingMechanic.ANONYMOUS;
import static net.bytebuddy.implementation.bind.annotation.Argument.BindingMechanic.UNIQUE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class ArgumentBinderTest extends AbstractAnnotationBinderTest<Argument> {
    @Mock
    private TypeDescription sourceType;

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic genericSourceType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    public ArgumentBinderTest() {
        super(Argument.class);
    }

    @Test
    public void testLegalBindingNoRuntimeTypeUnique() throws Exception {
        assertBinding(STATIC, UNIQUE);
    }

    @Test
    public void testLegalBindingRuntimeTypeUnique() throws Exception {
        assertBinding(DYNAMIC, UNIQUE);
    }

    @Test
    public void testLegalBindingNoRuntimeTypeAnonymous() throws Exception {
        assertBinding(STATIC, ANONYMOUS);
    }

    @Test
    public void testLegalBindingRuntimeTypeAnonymous() throws Exception {
        assertBinding(DYNAMIC, ANONYMOUS);
    }

    @Test
    public void testIllegalBinding() throws Exception {
        final int sourceIndex = 0;
        final int targetIndex = 0;
        Mockito.when(annotation.value()).thenReturn(sourceIndex);
        Mockito.when(target.getIndex()).thenReturn(targetIndex);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(annotation, Mockito.atLeast(1)).value();
        Mockito.verify(source, Mockito.atLeast(1)).getParameters();
        Mockito.verifyZeroInteractions(assigner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeAnnotationValue() throws Exception {
        Mockito.when(annotation.value()).thenReturn((-1));
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @SuppressWarnings("unused")
    private static class Carrier {
        private void method(@Argument(0)
        Void parameter) {
            /* do nothing */
        }
    }
}

