package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.bind.annotation.StubValue.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class StubValueBinderTest extends AbstractAnnotationBinderTest<StubValue> {
    @Mock
    private TypeDescription type;

    @Mock
    private TypeDescription.Generic genericType;

    public StubValueBinderTest() {
        super(StubValue.class);
    }

    @Test
    public void testVoidReturnType() throws Exception {
        Mockito.when(target.getType()).thenReturn(OBJECT);
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        MatcherAssert.assertThat(INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC).isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testNonVoidAssignableReturnType() throws Exception {
        Mockito.when(target.getType()).thenReturn(OBJECT);
        Mockito.when(source.getReturnType()).thenReturn(genericType);
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        MatcherAssert.assertThat(INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC).isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testNonVoidNonAssignableReturnType() throws Exception {
        Mockito.when(target.getType()).thenReturn(OBJECT);
        Mockito.when(source.getReturnType()).thenReturn(OBJECT);
        Mockito.when(stackManipulation.isValid()).thenReturn(false);
        MatcherAssert.assertThat(INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC).isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalParameter() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericType);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

