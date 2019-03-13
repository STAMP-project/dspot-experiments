package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ExceptionMethodTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testWithoutMessage() throws Exception {
        DynamicType.Loaded<ExceptionMethodTest.Foo> loaded = new ByteBuddy().subclass(ExceptionMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(ExceptionMethodTest.Foo.class)).intercept(ExceptionMethod.throwing(RuntimeException.class)).make().load(ExceptionMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        ExceptionMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(ExceptionMethodTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(ExceptionMethodTest.Foo.class));
        try {
            instance.foo();
            Assert.fail();
        } catch (RuntimeException exception) {
            MatcherAssert.assertThat(exception.getClass(), CoreMatchers.<Class<?>>is(RuntimeException.class));
            MatcherAssert.assertThat(exception.getMessage(), CoreMatchers.nullValue());
        }
        instance.assertZeroCalls();
    }

    @Test
    public void testWithMessage() throws Exception {
        DynamicType.Loaded<ExceptionMethodTest.Foo> loaded = new ByteBuddy().subclass(ExceptionMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(ExceptionMethodTest.Foo.class)).intercept(ExceptionMethod.throwing(RuntimeException.class, ExceptionMethodTest.BAR)).make().load(ExceptionMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        ExceptionMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(ExceptionMethodTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(ExceptionMethodTest.Foo.class));
        try {
            instance.foo();
            Assert.fail();
        } catch (RuntimeException exception) {
            MatcherAssert.assertThat(exception.getClass(), CoreMatchers.<Class<?>>is(RuntimeException.class));
            MatcherAssert.assertThat(exception.getMessage(), CoreMatchers.is(ExceptionMethodTest.BAR));
        }
        instance.assertZeroCalls();
    }

    @Test
    public void testWithNonDeclaredCheckedException() throws Exception {
        DynamicType.Loaded<ExceptionMethodTest.Foo> loaded = new ByteBuddy().subclass(ExceptionMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(ExceptionMethodTest.Foo.class)).intercept(ExceptionMethod.throwing(Exception.class)).make().load(ExceptionMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        ExceptionMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(ExceptionMethodTest.Foo.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(ExceptionMethodTest.Foo.class));
        try {
            instance.foo();
            Assert.fail();
        } catch (Exception exception) {
            MatcherAssert.assertThat(exception.getClass(), CoreMatchers.<Class<?>>is(Exception.class));
            MatcherAssert.assertThat(exception.getMessage(), CoreMatchers.nullValue());
        }
        instance.assertZeroCalls();
    }

    public static class Foo extends CallTraceable {
        public void foo() {
            register(ExceptionMethodTest.FOO);
        }
    }
}

