package net.bytebuddy.implementation;


import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofConstant;


public class FieldAccessorOtherTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testArgumentSetter() throws Exception {
        Class<? extends FieldAccessorOtherTest.SampleArgumentSetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt(0)).make().load(FieldAccessorOtherTest.SampleArgumentSetter.class.getClassLoader(), WRAPPER).getLoaded();
        FieldAccessorOtherTest.SampleArgumentSetter sampleArgumentSetter = loaded.getDeclaredConstructor().newInstance();
        sampleArgumentSetter.foo(FieldAccessorOtherTest.FOO);
        MatcherAssert.assertThat(sampleArgumentSetter.foo, CoreMatchers.is(((Object) (FieldAccessorOtherTest.FOO))));
    }

    @Test
    public void testArgumentSetterChained() throws Exception {
        Class<? extends FieldAccessorOtherTest.SampleArgumentSetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.BAR)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt(0).andThen(FixedValue.value(FieldAccessorOtherTest.BAR))).make().load(FieldAccessorOtherTest.SampleArgumentSetter.class.getClassLoader(), WRAPPER).getLoaded();
        FieldAccessorOtherTest.SampleArgumentSetter sampleArgumentSetter = loaded.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(sampleArgumentSetter.bar(FieldAccessorOtherTest.FOO), CoreMatchers.is(((Object) (FieldAccessorOtherTest.BAR))));
        MatcherAssert.assertThat(sampleArgumentSetter.foo, CoreMatchers.is(((Object) (FieldAccessorOtherTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentSetterNonVoid() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.SampleArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.BAR)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt(0)).make();
    }

    @Test
    public void testArgumentSetterConstructor() throws Exception {
        Class<?> loaded = new ByteBuddy().subclass(Object.class, NO_CONSTRUCTORS).defineField(FieldAccessorOtherTest.FOO, String.class, Visibility.PUBLIC, FieldManifestation.FINAL).defineConstructor(Visibility.PUBLIC).withParameters(String.class).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()).andThen(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt(0))).make().load(null, WRAPPER).getLoaded();
        MatcherAssert.assertThat(loaded.getDeclaredField(FieldAccessorOtherTest.FOO).get(loaded.getDeclaredConstructor(String.class).newInstance(FieldAccessorOtherTest.FOO)), CoreMatchers.is(((Object) (FieldAccessorOtherTest.FOO))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArgumentCannotBeNegative() throws Exception {
        FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt((-1));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentSetterNoParameterAtIndex() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.SampleArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsArgumentAt(1).andThen(FixedValue.value(FieldAccessorOtherTest.BAR))).make();
    }

    @Test
    public void testExplicitNameSetter() throws Exception {
        DynamicType.Loaded<FieldAccessorOtherTest.SampleSetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleSetter.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.SampleSetter.class)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO)).make().load(FieldAccessorOtherTest.SampleSetter.class.getClassLoader(), WRAPPER);
        FieldAccessorOtherTest.SampleSetter sampleSetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Field field = FieldAccessorOtherTest.SampleSetter.class.getDeclaredField(FieldAccessorOtherTest.FOO);
        field.setAccessible(true);
        MatcherAssert.assertThat(field.get(sampleSetter), CoreMatchers.is(((Object) (FieldAccessorOtherTest.QUX))));
        sampleSetter.bar(FieldAccessorOtherTest.BAZ);
        MatcherAssert.assertThat(field.get(sampleSetter), CoreMatchers.is(((Object) (FieldAccessorOtherTest.BAZ))));
        sampleSetter.assertZeroCalls();
    }

    @Test
    public void testExplicitNameGetter() throws Exception {
        DynamicType.Loaded<FieldAccessorOtherTest.SampleGetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleGetter.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.SampleGetter.class)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO)).make().load(FieldAccessorOtherTest.SampleSetter.class.getClassLoader(), WRAPPER);
        FieldAccessorOtherTest.SampleGetter sampleGetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Field field = FieldAccessorOtherTest.SampleGetter.class.getDeclaredField(FieldAccessorOtherTest.FOO);
        field.setAccessible(true);
        MatcherAssert.assertThat(field.get(sampleGetter), CoreMatchers.is(((Object) (FieldAccessorOtherTest.BAZ))));
        MatcherAssert.assertThat(sampleGetter.bar(), CoreMatchers.is(((Object) (FieldAccessorOtherTest.BAZ))));
        MatcherAssert.assertThat(field.get(sampleGetter), CoreMatchers.is(((Object) (FieldAccessorOtherTest.BAZ))));
        sampleGetter.assertZeroCalls();
    }

    @Test
    public void testClassConstant() throws Exception {
        DynamicType.Loaded<FieldAccessorOtherTest.SampleNoArgumentSetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleNoArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO).setsValue(TypeDescription.OBJECT)).make().load(FieldAccessorOtherTest.SampleNoArgumentSetter.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        FieldAccessorOtherTest.SampleNoArgumentSetter instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        MatcherAssert.assertThat(instance.foo, CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testJavaConstant() throws Exception {
        DynamicType.Loaded<FieldAccessorOtherTest.SampleNoArgumentSetter> loaded = new ByteBuddy().subclass(FieldAccessorOtherTest.SampleNoArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(setsValue(ofConstant(Object.class))).make().load(FieldAccessorOtherTest.SampleNoArgumentSetter.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        FieldAccessorOtherTest.SampleNoArgumentSetter instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        MatcherAssert.assertThat(instance.foo, CoreMatchers.instanceOf(JavaType.METHOD_TYPE.load()));
    }

    @Test
    public void testStaticFieldOfOtherClass() throws Exception {
        DynamicType.Loaded<Callable> loaded = new ByteBuddy().subclass(Callable.class).method(ElementMatchers.named("call")).intercept(FieldAccessor.of(FieldAccessorOtherTest.StaticFieldHolder.class.getField(FieldAccessorOtherTest.FOO.toUpperCase()))).make().load(FieldAccessorOtherTest.StaticFieldHolder.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Callable<?> instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.call(), CoreMatchers.is(((Object) (FieldAccessorOtherTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testNotAssignable() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Baz.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Baz.class)).intercept(FieldAccessor.ofField(FieldAccessorOtherTest.FOO)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalFieldSetter() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Foo.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Foo.class)).intercept(FieldAccessor.ofBeanProperty()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldNoVisibleField() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Bar.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Bar.class)).intercept(FieldAccessor.ofBeanProperty()).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldNoBeanMethodName() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Qux.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Qux.class)).intercept(FieldAccessor.ofBeanProperty()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleExplicitField() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Qux.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Qux.class)).intercept(FieldAccessor.of(FieldAccessorOtherTest.Bar.class.getDeclaredField(FieldAccessorOtherTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInaccessibleExplicitField() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.Bar.class).method(ElementMatchers.isDeclaredBy(FieldAccessorOtherTest.Bar.class)).intercept(FieldAccessor.of(FieldAccessorOtherTest.Bar.class.getDeclaredField(FieldAccessorOtherTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testSetterInaccessibleSource() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.SampleNoArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(setsDefaultValue()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testSetterInaccessibleTarget() throws Exception {
        new ByteBuddy().subclass(FieldAccessorOtherTest.SampleNoArgumentSetter.class).method(ElementMatchers.named(FieldAccessorOtherTest.FOO)).intercept(setsFieldValueOf(FieldAccessorOtherTest.Bar.class.getDeclaredField(FieldAccessorOtherTest.BAR))).make();
    }

    @SuppressWarnings("unused")
    public static class Foo {
        protected final Object foo = null;

        public void setFoo(Object o) {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class Bar {
        private Object bar;

        public void setBar(Object o) {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class Qux {
        private Object qux;

        public void qux(Object o) {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class Baz {
        public String foo;

        public void qux(Object o) {
            /* empty */
        }
    }

    public static class SampleArgumentSetter {
        public Object foo;

        public void foo(String value) {
            throw new AssertionError();
        }

        public Object bar(String value) {
            throw new AssertionError();
        }
    }

    public static class SampleNoArgumentSetter {
        public Object foo;

        public void foo() {
            throw new AssertionError();
        }

        public Object bar() {
            throw new AssertionError();
        }
    }

    public static class SampleGetter extends CallTraceable {
        protected Object foo = FieldAccessorOtherTest.BAZ;

        public Object bar() {
            register(FieldAccessorOtherTest.FOO);
            return FieldAccessorOtherTest.QUX;
        }
    }

    public static class SampleSetter extends CallTraceable {
        protected Object foo = FieldAccessorOtherTest.QUX;

        public void bar(Object foo) {
            register(FieldAccessorOtherTest.FOO, foo);
        }
    }

    public static class StaticFieldHolder {
        public static final String FOO = "foo";
    }
}

