package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.of;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded;


public class FixedValueTest {
    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private FixedValueTest.Bar bar;

    @Test
    public void testTypeDescriptionConstantPool() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(TypeDescription.OBJECT)).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(qux.getDeclaredConstructor().newInstance().bar(), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    public void testClassConstantPool() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(Object.class)).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(qux.getDeclaredConstructor().newInstance().bar(), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodTypeConstantPool() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(net.bytebuddy.utility.JavaConstant.MethodType.of(void.class, Object.class))).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(qux.getDeclaredConstructor().newInstance().bar(), CoreMatchers.is(FixedValueTest.makeMethodType(void.class, Object.class)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodTypeConstantPoolValue() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(FixedValueTest.makeMethodType(void.class, Object.class))).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(qux.getDeclaredConstructor().newInstance().bar(), CoreMatchers.is(FixedValueTest.makeMethodType(void.class, Object.class)));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testMethodHandleConstantPool() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(of(FixedValueTest.Qux.class.getDeclaredMethod("bar")))).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(ofLoaded(qux.getDeclaredConstructor().newInstance().bar()), CoreMatchers.is(ofLoaded(FixedValueTest.makeMethodHandle())));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, hotSpot = 7)
    public void testMethodHandleConstantPoolValue() throws Exception {
        Class<? extends FixedValueTest.Qux> qux = new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.value(FixedValueTest.makeMethodHandle())).make().load(FixedValueTest.Qux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(qux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(ofLoaded(qux.getDeclaredConstructor().newInstance().bar()), CoreMatchers.is(ofLoaded(FixedValueTest.makeMethodHandle())));
    }

    @Test
    public void testReferenceCall() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.Qux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Qux.class)).intercept(FixedValue.reference(bar)).make();
    }

    @Test
    public void testValueCall() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.Foo.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Foo.class)).intercept(FixedValue.reference(bar)).make();
    }

    @Test
    public void testNullValue() throws Exception {
        Class<? extends FixedValueTest.Foo> foo = new ByteBuddy().subclass(FixedValueTest.Foo.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Foo.class)).intercept(FixedValue.nullValue()).make().load(FixedValueTest.Foo.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(foo.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(foo.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getDeclaredMethod(FixedValueTest.BAR).invoke(foo.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testNullValueNonAssignable() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.FooBar.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.FooBar.class)).intercept(FixedValue.nullValue()).make();
    }

    @Test
    public void testThisValue() throws Exception {
        Class<? extends FixedValueTest.QuxBaz> quxbaz = new ByteBuddy().subclass(FixedValueTest.QuxBaz.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.QuxBaz.class)).intercept(FixedValue.self()).make().load(FixedValueTest.QuxBaz.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(quxbaz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(quxbaz.getDeclaredMethods().length, CoreMatchers.is(1));
        FixedValueTest.QuxBaz self = quxbaz.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(self.bar(), CoreMatchers.sameInstance(((Object) (self))));
    }

    @Test(expected = IllegalStateException.class)
    public void testThisValueStatic() throws Exception {
        new ByteBuddy().redefine(FixedValueTest.FooBarQuxBaz.class).method(ElementMatchers.named("bar")).intercept(FixedValue.self()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testThisValueNonAssignable() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.Foo.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Foo.class)).intercept(FixedValue.self()).make();
    }

    @Test
    public void testOriginType() throws Exception {
        Class<? extends FixedValueTest.Baz> baz = new ByteBuddy().subclass(FixedValueTest.Baz.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.Baz.class)).intercept(FixedValue.originType()).make().load(FixedValueTest.QuxBaz.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(baz.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(baz.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(baz.getDeclaredMethod(FixedValueTest.BAR).invoke(baz.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (FixedValueTest.Baz.class))));
    }

    @Test
    public void testArgument() throws Exception {
        Class<? extends FixedValueTest.FooQux> fooQux = new ByteBuddy().subclass(FixedValueTest.FooQux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.FooQux.class)).intercept(FixedValue.argument(1)).make().load(FixedValueTest.FooQux.class.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(fooQux.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(fooQux.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(fooQux.getDeclaredMethod(FixedValueTest.BAR, Integer.class, String.class).invoke(fooQux.getDeclaredConstructor().newInstance(), 0, FixedValueTest.BAR), CoreMatchers.is(((Object) (FixedValueTest.BAR))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArgumentNegative() throws Exception {
        FixedValue.argument((-1));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentNotAssignable() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.FooQux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.FooQux.class)).intercept(FixedValue.argument(0)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentNonExistent() throws Exception {
        new ByteBuddy().subclass(FixedValueTest.FooQux.class).method(ElementMatchers.isDeclaredBy(FixedValueTest.FooQux.class)).intercept(FixedValue.argument(2)).make();
    }

    public static class Foo extends CallTraceable {
        public FixedValueTest.Bar bar() {
            register(FixedValueTest.BAR);
            return new FixedValueTest.Bar();
        }
    }

    /* empty */
    public static class Bar {}

    public static class Qux extends CallTraceable {
        public Object bar() {
            register(FixedValueTest.BAR);
            return null;
        }
    }

    public static class Baz {
        public Class<?> bar() {
            return null;
        }
    }

    public static class FooBar {
        public void bar() {
            /* empty */
        }
    }

    public static class QuxBaz {
        public Object bar() {
            return null;
        }
    }

    public static class FooBarQuxBaz {
        public static Object bar() {
            return null;
        }
    }

    public static class FooQux {
        public String bar(Integer arg0, String arg1) {
            return null;
        }
    }
}

