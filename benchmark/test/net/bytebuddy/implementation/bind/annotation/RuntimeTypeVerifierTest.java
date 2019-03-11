package net.bytebuddy.implementation.bind.annotation;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.implementation.bind.annotation.RuntimeType.Verifier.check;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class RuntimeTypeVerifierTest extends AbstractAnnotationTest<RuntimeType> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AnnotationSource annotationSource;

    @Mock
    private RuntimeType runtimeType;

    public RuntimeTypeVerifierTest() {
        super(RuntimeType.class);
    }

    @Test
    public void testCheckElementValid() throws Exception {
        Mockito.when(annotationSource.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(runtimeType));
        MatcherAssert.assertThat(check(annotationSource), CoreMatchers.is(DYNAMIC));
        Mockito.verify(annotationSource).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(annotationSource);
    }

    @Test
    public void testCheckElementInvalid() throws Exception {
        Mockito.when(annotationSource.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations());
        MatcherAssert.assertThat(check(annotationSource), CoreMatchers.is(STATIC));
        Mockito.verify(annotationSource).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(annotationSource);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInstantiation() throws Exception {
        Constructor<?> constructor = RuntimeType.Verifier.class.getDeclaredConstructor();
        MatcherAssert.assertThat(constructor.getModifiers(), CoreMatchers.is(Opcodes.ACC_PRIVATE));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((UnsupportedOperationException) (exception.getCause()));
        }
    }
}

