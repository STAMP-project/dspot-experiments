package net.bytebuddy.description.annotation;


import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.annotation.AnnotationDescription.AnnotationInvocationHandler.of;
import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.RESOLVED;
import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.UNDEFINED;
import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.UNRESOLVED;


public class AnnotationDescriptionAnnotationInvocationHandlerTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AnnotationValue<?, ?> annotationValue;

    @Mock
    private AnnotationValue<?, ?> otherAnnotationValue;

    @Mock
    private AnnotationValue<?, ?> freeAnnotationValue;

    @Mock
    private AnnotationValue.Loaded<?> loadedAnnotationValue;

    @Mock
    private AnnotationValue.Loaded<?> otherLoadedAnnotationValue;

    @Test(expected = ClassNotFoundException.class)
    public void testClassNotFoundExceptionIsTransparent() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenThrow(new ClassNotFoundException());
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = AnnotationTypeMismatchException.class)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeMismatchException() throws Throwable {
        Mockito.when(loadedAnnotationValue.resolve()).thenReturn(new Object());
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = IncompleteAnnotationException.class)
    @SuppressWarnings("unchecked")
    public void testIncompleteAnnotationException() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenReturn(((AnnotationValue.Loaded) (new AnnotationDescription.AnnotationInvocationHandler.MissingValue(AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, "foo"))));
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = EnumConstantNotPresentException.class)
    @SuppressWarnings("unchecked")
    public void testEnumConstantNotPresentException() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenReturn(((AnnotationValue.Loaded) (new AnnotationValue.ForEnumerationDescription.UnknownRuntimeEnumeration(AnnotationDescriptionAnnotationInvocationHandlerTest.Bar.class, AnnotationDescriptionAnnotationInvocationHandlerTest.FOO))));
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = IncompatibleClassChangeError.class)
    @SuppressWarnings("unchecked")
    public void testEnumTypeIncompatible() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenReturn(((AnnotationValue.Loaded) (new AnnotationValue.ForEnumerationDescription.IncompatibleRuntimeType(AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class))));
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = IncompatibleClassChangeError.class)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeIncompatible() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenReturn(((AnnotationValue.Loaded) (new AnnotationValue.ForEnumerationDescription.IncompatibleRuntimeType(AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class))));
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test(expected = RuntimeException.class)
    public void testOtherExceptionIsTransparent() throws Throwable {
        Mockito.when(freeAnnotationValue.load(getClass().getClassLoader())).thenThrow(new RuntimeException());
        Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, freeAnnotationValue))).invoke(new Object(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class.getDeclaredMethod("foo"), new Object[0]);
    }

    @Test
    public void testEqualsToDirectIsTrue() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.represents(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO)).thenReturn(true);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO) }), CoreMatchers.is(((Object) (true))));
    }

    @Test
    public void testEqualsToUnresolvedIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(UNRESOLVED);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO) }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).resolve();
    }

    @Test
    public void testEqualsToUndefinedIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(UNDEFINED);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO) }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).resolve();
    }

    @Test
    public void testEqualsToIndirectIsTrue() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.represents(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO)).thenReturn(true);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class }, new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO)) }), CoreMatchers.is(((Object) (true))));
        Mockito.verify(loadedAnnotationValue).represents(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO);
    }

    @Test
    public void testEqualsToOtherHandlerIsTrue() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.resolve()).thenReturn(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue)));
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class }, invocationHandler) }), CoreMatchers.is(((Object) (true))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).resolve();
    }

    @Test
    public void testEqualsToDirectIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.represents(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR)).thenReturn(false);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR) }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue).represents(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR);
    }

    @Test
    public void testEqualsToIndirectIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.represents(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR)).thenReturn(false);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class }, new AnnotationDescriptionAnnotationInvocationHandlerTest.ExplicitFoo(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR)) }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue).represents(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR);
    }

    @Test
    public void testEqualsToOtherHandlerIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.resolve()).thenReturn(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO);
        Mockito.when(otherLoadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(otherLoadedAnnotationValue.resolve()).thenReturn(AnnotationDescriptionAnnotationInvocationHandlerTest.BAR);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, otherAnnotationValue)));
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class }, invocationHandler) }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).resolve();
        Mockito.verify(otherLoadedAnnotationValue, Mockito.never()).resolve();
    }

    @Test
    public void testEqualsToObjectIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.resolve()).thenReturn(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.Other() }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).resolve();
    }

    @Test
    public void testEqualsToInvocationExceptionIsFalse() throws Throwable {
        Mockito.when(loadedAnnotationValue.getState()).thenReturn(RESOLVED);
        Mockito.when(loadedAnnotationValue.resolve()).thenReturn(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO);
        MatcherAssert.assertThat(Proxy.getInvocationHandler(of(getClass().getClassLoader(), AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class, Collections.<String, AnnotationValue<?, ?>>singletonMap(AnnotationDescriptionAnnotationInvocationHandlerTest.FOO, annotationValue))).invoke(new Object(), Object.class.getDeclaredMethod("equals", Object.class), new Object[]{ new AnnotationDescriptionAnnotationInvocationHandlerTest.FooWithException() }), CoreMatchers.is(((Object) (false))));
        Mockito.verify(loadedAnnotationValue, Mockito.never()).represents(CoreMatchers.any(String.class));
    }

    public enum Bar {

        VALUE;}

    public @interface Foo {
        String foo();
    }

    public @interface DefaultFoo {
        String foo() default AnnotationDescriptionAnnotationInvocationHandlerTest.FOO;
    }

    private static class FooWithException implements AnnotationDescriptionAnnotationInvocationHandlerTest.Foo {
        public String foo() {
            throw new RuntimeException();
        }

        public Class<? extends Annotation> annotationType() {
            return AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class;
        }
    }

    private static class ExplicitFoo implements InvocationHandler , AnnotationDescriptionAnnotationInvocationHandlerTest.Foo {
        private final String value;

        private ExplicitFoo(String value) {
            this.value = value;
        }

        public String foo() {
            return value;
        }

        public Class<? extends Annotation> annotationType() {
            return AnnotationDescriptionAnnotationInvocationHandlerTest.Foo.class;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(this, args);
        }
    }

    private static class Other implements Annotation {
        public Class<? extends Annotation> annotationType() {
            return Annotation.class;
        }
    }
}

