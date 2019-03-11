package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceImplementationTest {
    private static final String FOO = "foo";

    @Test(expected = IllegalStateException.class)
    public void testAbstractMethod() throws Exception {
        new ByteBuddy(ClassFileVersion.of(AdviceImplementationTest.Foo.class)).subclass(AdviceImplementationTest.Foo.class).method(ElementMatchers.named(AdviceImplementationTest.FOO)).intercept(Advice.to(AdviceImplementationTest.Foo.class)).make();
    }

    @Test
    public void testActualMethod() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy(ClassFileVersion.of(AdviceImplementationTest.Bar.class)).subclass(AdviceImplementationTest.Bar.class).method(ElementMatchers.named(AdviceImplementationTest.FOO)).intercept(Advice.to(AdviceImplementationTest.Bar.class)).make().load(AdviceImplementationTest.Bar.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(((Object) (AdviceImplementationTest.FOO))));
    }

    @Test
    public void testExplicitWrap() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy(ClassFileVersion.of(AdviceImplementationTest.Qux.class)).subclass(AdviceImplementationTest.Qux.class).method(ElementMatchers.named(AdviceImplementationTest.FOO)).intercept(Advice.to(AdviceImplementationTest.Qux.class).wrap(StubMethod.INSTANCE)).make().load(AdviceImplementationTest.Qux.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(AdviceImplementationTest.FOO));
    }

    @Test
    public void testExplicitWrapMultiple() throws Exception {
        Class<?> type = new ByteBuddy(ClassFileVersion.of(AdviceImplementationTest.Baz.class)).redefine(AdviceImplementationTest.Baz.class).method(ElementMatchers.named(AdviceImplementationTest.FOO)).intercept(Advice.to(AdviceImplementationTest.Baz.class).wrap(Advice.to(AdviceImplementationTest.Baz.class).wrap(StubMethod.INSTANCE))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object baz = type.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceImplementationTest.FOO).invoke(baz), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceImplementationTest.FOO).getInt(null), CoreMatchers.is(2));
    }

    public abstract static class Foo {
        public abstract String foo();

        @Advice.OnMethodExit
        public static void exit(@Advice.Return(readOnly = false)
        String returned) {
            returned = AdviceImplementationTest.FOO;
        }
    }

    public static class Bar {
        public Object foo() {
            throw new RuntimeException();
        }

        @Advice.OnMethodExit(onThrowable = RuntimeException.class)
        public static void exit(@Advice.Thrown(readOnly = false)
        Throwable throwable, @Advice.Return(readOnly = false)
        Object returned) {
            if (!(throwable instanceof RuntimeException)) {
                throw new AssertionError();
            }
            throwable = null;
            returned = AdviceImplementationTest.FOO;
        }
    }

    public static class Qux {
        public String foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.Return(readOnly = false)
        String returned) {
            if (returned != null) {
                throw new AssertionError();
            }
            returned = AdviceImplementationTest.FOO;
        }
    }

    public static class Baz {
        public static int foo;

        public void foo() {
            /* empty */
        }

        @Advice.OnMethodExit
        public static void exit() {
            AdviceImplementationTest.Baz.foo += 1;
        }
    }
}

