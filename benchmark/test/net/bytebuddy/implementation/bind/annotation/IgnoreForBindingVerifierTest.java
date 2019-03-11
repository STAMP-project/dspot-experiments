package net.bytebuddy.implementation.bind.annotation;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
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

import static net.bytebuddy.implementation.bind.annotation.IgnoreForBinding.Verifier.check;


public class IgnoreForBindingVerifierTest extends AbstractAnnotationTest<IgnoreForBinding> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private AnnotationList annotationList;

    public IgnoreForBindingVerifierTest() {
        super(IgnoreForBinding.class);
    }

    @Test
    public void testIsPresent() throws Exception {
        Mockito.when(annotationList.isAnnotationPresent(IgnoreForBinding.class)).thenReturn(true);
        MatcherAssert.assertThat(check(methodDescription), CoreMatchers.is(true));
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(methodDescription);
        Mockito.verify(annotationList).isAnnotationPresent(IgnoreForBinding.class);
        Mockito.verifyNoMoreInteractions(annotationList);
    }

    @Test
    public void testIsNotPresent() throws Exception {
        MatcherAssert.assertThat(check(methodDescription), CoreMatchers.is(false));
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(methodDescription);
        Mockito.verify(annotationList).isAnnotationPresent(IgnoreForBinding.class);
        Mockito.verifyNoMoreInteractions(annotationList);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInstantiation() throws Exception {
        Constructor<?> constructor = IgnoreForBinding.Verifier.class.getDeclaredConstructor();
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

