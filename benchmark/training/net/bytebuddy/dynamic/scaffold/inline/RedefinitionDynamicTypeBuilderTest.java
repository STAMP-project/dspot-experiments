package net.bytebuddy.dynamic.scaffold.inline;


import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class RedefinitionDynamicTypeBuilderTest extends AbstractDynamicTypeBuilderForInliningTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Test
    public void testConstructorRetentionNoAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().redefine(RedefinitionDynamicTypeBuilderTest.Bar.class).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        Field field = type.getDeclaredField(RedefinitionDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(RedefinitionDynamicTypeBuilderTest.FOO)), CoreMatchers.is(((Object) (RedefinitionDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testConstructorRebaseSingleAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().redefine(RedefinitionDynamicTypeBuilderTest.Bar.class).constructor(ElementMatchers.any()).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor())).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        Field field = type.getDeclaredField(RedefinitionDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(RedefinitionDynamicTypeBuilderTest.FOO)), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testMethodRebase() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().redefine(RedefinitionDynamicTypeBuilderTest.Qux.class).method(ElementMatchers.named(RedefinitionDynamicTypeBuilderTest.BAR)).intercept(StubMethod.INSTANCE).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getDeclaredMethod(RedefinitionDynamicTypeBuilderTest.FOO).invoke(null), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(RedefinitionDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (RedefinitionDynamicTypeBuilderTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredMethod(RedefinitionDynamicTypeBuilderTest.BAR).invoke(null), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(RedefinitionDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (RedefinitionDynamicTypeBuilderTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceSubInterface() throws Exception {
        Class<?> interfaceType = Class.forName(RedefinitionDynamicTypeBuilderTest.DEFAULT_METHOD_INTERFACE);
        Class<?> dynamicInterfaceType = new ByteBuddy().redefine(interfaceType).method(ElementMatchers.named(RedefinitionDynamicTypeBuilderTest.FOO)).intercept(new Implementation.Simple(new TextConstant(RedefinitionDynamicTypeBuilderTest.BAR), MethodReturn.REFERENCE)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        Class<?> dynamicClassType = new ByteBuddy().subclass(dynamicInterfaceType).make().load(dynamicInterfaceType.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicClassType.getMethod(RedefinitionDynamicTypeBuilderTest.FOO).invoke(dynamicClassType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (RedefinitionDynamicTypeBuilderTest.BAR))));
        MatcherAssert.assertThat(dynamicInterfaceType.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicClassType.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    public static class Bar {
        public final String bar;

        public Bar(String bar) {
            this.bar = bar;
        }
    }

    public static class Qux {
        public static String foo;

        public static String foo() {
            try {
                return RedefinitionDynamicTypeBuilderTest.Qux.foo;
            } finally {
                RedefinitionDynamicTypeBuilderTest.Qux.foo = RedefinitionDynamicTypeBuilderTest.FOO;
            }
        }

        public static String bar() {
            try {
                return RedefinitionDynamicTypeBuilderTest.Qux.foo;
            } finally {
                RedefinitionDynamicTypeBuilderTest.Qux.foo = RedefinitionDynamicTypeBuilderTest.FOO;
            }
        }
    }
}

