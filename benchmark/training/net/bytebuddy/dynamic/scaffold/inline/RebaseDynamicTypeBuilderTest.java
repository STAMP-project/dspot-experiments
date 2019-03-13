package net.bytebuddy.dynamic.scaffold.inline;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.dynamic.AbstractDynamicTypeBuilderTest;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.visibility.PackageAnnotation;
import net.bytebuddy.test.visibility.Sample;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class RebaseDynamicTypeBuilderTest extends AbstractDynamicTypeBuilderForInliningTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Test
    public void testConstructorRetentionNoAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().rebase(RebaseDynamicTypeBuilderTest.Bar.class).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        Field field = type.getDeclaredField(RebaseDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(RebaseDynamicTypeBuilderTest.FOO)), CoreMatchers.is(((Object) (RebaseDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testConstructorRebaseSingleAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().rebase(RebaseDynamicTypeBuilderTest.Bar.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        Field field = type.getDeclaredField(RebaseDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(RebaseDynamicTypeBuilderTest.FOO)), CoreMatchers.is(((Object) (RebaseDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testMethodRebase() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().rebase(RebaseDynamicTypeBuilderTest.Qux.class).method(ElementMatchers.named(RebaseDynamicTypeBuilderTest.BAR)).intercept(StubMethod.INSTANCE).make();
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(type.getDeclaredMethod(RebaseDynamicTypeBuilderTest.FOO).invoke(null), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(RebaseDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (RebaseDynamicTypeBuilderTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredMethod(RebaseDynamicTypeBuilderTest.BAR).invoke(null), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(RebaseDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (RebaseDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testPackageRebasement() throws Exception {
        Class<?> packageType = new ByteBuddy().rebase(Sample.class.getPackage(), of(getClass().getClassLoader())).annotateType(ofType(RebaseDynamicTypeBuilderTest.Baz.class).build()).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(packageType.getSimpleName(), CoreMatchers.is(PackageDescription.PACKAGE_CLASS_NAME));
        MatcherAssert.assertThat(packageType.getName(), CoreMatchers.is((((Sample.class.getPackage().getName()) + ".") + (PackageDescription.PACKAGE_CLASS_NAME))));
        MatcherAssert.assertThat(packageType.getModifiers(), CoreMatchers.is(PackageDescription.PACKAGE_MODIFIERS));
        MatcherAssert.assertThat(packageType.getDeclaredFields().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(packageType.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(packageType.getDeclaredAnnotations().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(packageType.getAnnotation(PackageAnnotation.class), CoreMatchers.notNullValue(PackageAnnotation.class));
        MatcherAssert.assertThat(packageType.getAnnotation(RebaseDynamicTypeBuilderTest.Baz.class), CoreMatchers.notNullValue(RebaseDynamicTypeBuilderTest.Baz.class));
    }

    @Test
    public void testRebaseOfRenamedType() throws Exception {
        Class<?> rebased = new ByteBuddy().rebase(Sample.class).name(((Sample.class.getName()) + (RebaseDynamicTypeBuilderTest.FOO))).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(rebased.getName(), CoreMatchers.is(((Sample.class.getName()) + (RebaseDynamicTypeBuilderTest.FOO))));
        MatcherAssert.assertThat(rebased.getDeclaredConstructors().length, CoreMatchers.is(2));
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotRebaseDefinedMethod() throws Exception {
        new ByteBuddy().rebase(AbstractDynamicTypeBuilderTest.Foo.class).defineMethod(RebaseDynamicTypeBuilderTest.FOO, void.class).intercept(SuperMethodCall.INSTANCE).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceSubInterface() throws Exception {
        Class<?> interfaceType = Class.forName(RebaseDynamicTypeBuilderTest.DEFAULT_METHOD_INTERFACE);
        Class<?> dynamicInterfaceType = new ByteBuddy().rebase(interfaceType).method(ElementMatchers.named(RebaseDynamicTypeBuilderTest.FOO)).intercept(MethodDelegation.to(AbstractDynamicTypeBuilderTest.InterfaceOverrideInterceptor.class)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        Class<?> dynamicClassType = new ByteBuddy().subclass(dynamicInterfaceType).make().load(dynamicInterfaceType.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicClassType.getMethod(RebaseDynamicTypeBuilderTest.FOO).invoke(dynamicClassType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((RebaseDynamicTypeBuilderTest.FOO) + (RebaseDynamicTypeBuilderTest.BAR)))));
        MatcherAssert.assertThat(dynamicInterfaceType.getDeclaredMethods().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(dynamicClassType.getDeclaredMethods().length, CoreMatchers.is(0));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Baz {}

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
                return RebaseDynamicTypeBuilderTest.Qux.foo;
            } finally {
                RebaseDynamicTypeBuilderTest.Qux.foo = RebaseDynamicTypeBuilderTest.FOO;
            }
        }

        public static String bar() {
            try {
                return RebaseDynamicTypeBuilderTest.Qux.foo;
            } finally {
                RebaseDynamicTypeBuilderTest.Qux.foo = RebaseDynamicTypeBuilderTest.FOO;
            }
        }
    }
}

