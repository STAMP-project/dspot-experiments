package net.bytebuddy.implementation;


import java.io.Serializable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.Morph.Binder.install;


public class MethodDelegationMorphTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final int BAZ = 42;

    private static final String DEFAULT_INTERFACE = "net.bytebuddy.test.precompiled.MorphDefaultInterface";

    private static final String DEFAULT_INTERFACE_TARGET_EXPLICIT = "net.bytebuddy.test.precompiled.MorphDefaultDelegationTargetExplicit";

    private static final String DEFAULT_INTERFACE_TARGET_IMPLICIT = "net.bytebuddy.test.precompiled.MorphDefaultDelegationTargetImplicit";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testMorph() throws Exception {
        DynamicType.Loaded<MethodDelegationMorphTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationMorphTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(new MethodDelegationMorphTest.SimpleMorph(MethodDelegationMorphTest.QUX))).make().load(MethodDelegationMorphTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationMorphTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationMorphTest.FOO), CoreMatchers.is(((MethodDelegationMorphTest.QUX) + (MethodDelegationMorphTest.BAR))));
    }

    @Test
    public void testMorphVoid() throws Exception {
        MethodDelegationMorphTest.SimpleMorph simpleMorph = new MethodDelegationMorphTest.SimpleMorph(MethodDelegationMorphTest.QUX);
        DynamicType.Loaded<MethodDelegationMorphTest.Bar> loaded = new ByteBuddy().subclass(MethodDelegationMorphTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Bar.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(simpleMorph)).make().load(MethodDelegationMorphTest.Bar.class.getClassLoader(), WRAPPER);
        MethodDelegationMorphTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        instance.assertOnlyCall(MethodDelegationMorphTest.FOO);
        simpleMorph.assertOnlyCall(MethodDelegationMorphTest.BAR);
    }

    @Test
    public void testMorphPrimitive() throws Exception {
        DynamicType.Loaded<MethodDelegationMorphTest.Qux> loaded = new ByteBuddy().subclass(MethodDelegationMorphTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Qux.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(new MethodDelegationMorphTest.PrimitiveMorph(MethodDelegationMorphTest.BAZ))).make().load(MethodDelegationMorphTest.Qux.class.getClassLoader(), WRAPPER);
        MethodDelegationMorphTest.Qux instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(0), CoreMatchers.is(((MethodDelegationMorphTest.BAZ) * 2L)));
    }

    @Test
    public void testMorphSerializable() throws Exception {
        DynamicType.Loaded<MethodDelegationMorphTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationMorphTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(MethodDelegationMorphTest.SimpleMorphSerializable.class)).make().load(MethodDelegationMorphTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationMorphTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationMorphTest.FOO), CoreMatchers.is(((MethodDelegationMorphTest.QUX) + (MethodDelegationMorphTest.BAR))));
    }

    @Test(expected = IllegalStateException.class)
    public void testMorphIllegal() throws Exception {
        new ByteBuddy().subclass(MethodDelegationMorphTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(MethodDelegationMorphTest.SimpleMorphIllegal.class)).make();
    }

    @Test(expected = ClassCastException.class)
    public void testMorphToIncompatibleTypeThrowsException() throws Exception {
        DynamicType.Loaded<MethodDelegationMorphTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationMorphTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationMorphTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(new MethodDelegationMorphTest.SimpleMorph(new Object()))).make().load(MethodDelegationMorphTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationMorphTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo(MethodDelegationMorphTest.QUX);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMorphTypeDoesNotDeclareCorrectMethodThrowsException() throws Exception {
        install(Serializable.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMorphTypeDoesInheritFromOtherTypeThrowsException() throws Exception {
        install(MethodDelegationMorphTest.InheritingMorphingType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMorphTypeIsNotPublicThrowsException() throws Exception {
        install(MethodDelegationMorphTest.PackagePrivateMorphing.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeDoesNotDeclareCorrectMethodSignatureThrowsException() throws Exception {
        install(MethodDelegationMorphTest.WrongParametersMorphing.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeIsNotInterfaceThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(Object.class)).to(new MethodDelegationMorphTest.SimpleMorph(MethodDelegationMorphTest.QUX));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodExplicit() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationMorphTest.DEFAULT_INTERFACE)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(Class.forName(MethodDelegationMorphTest.DEFAULT_INTERFACE_TARGET_EXPLICIT))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getDeclaredMethod(MethodDelegationMorphTest.FOO, String.class).invoke(instance, MethodDelegationMorphTest.QUX), CoreMatchers.is(((Object) ((MethodDelegationMorphTest.FOO) + (MethodDelegationMorphTest.BAR)))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodImplicit() throws Exception {
        DynamicType.Loaded<Object> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationMorphTest.DEFAULT_INTERFACE)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationMorphTest.Morphing.class)).to(Class.forName(MethodDelegationMorphTest.DEFAULT_INTERFACE_TARGET_IMPLICIT))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getDeclaredMethod(MethodDelegationMorphTest.FOO, String.class).invoke(instance, MethodDelegationMorphTest.QUX), CoreMatchers.is(((Object) ((MethodDelegationMorphTest.FOO) + (MethodDelegationMorphTest.BAR)))));
    }

    public interface Morphing<T> {
        T morph(Object... arguments);
    }

    /* empty */
    public interface InheritingMorphingType<T> extends MethodDelegationMorphTest.Morphing<T> {}

    @SuppressWarnings("unused")
    private interface PackagePrivateMorphing<T> {
        T morph(Object... arguments);
    }

    @SuppressWarnings("unused")
    private interface WrongParametersMorphing<T> {
        T morph(Object arguments);
    }

    public static class Foo {
        public String foo(String foo) {
            return foo + (MethodDelegationMorphTest.BAR);
        }
    }

    public static class Bar extends CallTraceable {
        public void foo() {
            register(MethodDelegationMorphTest.FOO);
        }
    }

    public static class Qux {
        public long foo(int argument) {
            return argument * 2L;
        }
    }

    public static class SimpleMorph extends CallTraceable {
        private final Object[] arguments;

        public SimpleMorph(Object... arguments) {
            this.arguments = arguments;
        }

        public String intercept(@Morph
        MethodDelegationMorphTest.Morphing<String> morphing) {
            register(MethodDelegationMorphTest.BAR);
            MatcherAssert.assertThat(morphing, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return morphing.morph(arguments);
        }
    }

    public static class PrimitiveMorph {
        private final int argument;

        public PrimitiveMorph(int argument) {
            this.argument = argument;
        }

        public Long intercept(@Morph
        MethodDelegationMorphTest.Morphing<Long> morphing) {
            MatcherAssert.assertThat(morphing, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return morphing.morph(argument);
        }
    }

    public static class SimpleMorphSerializable {
        public static String intercept(@Morph(serializableProxy = true)
        MethodDelegationMorphTest.Morphing<String> morphing) {
            MatcherAssert.assertThat(morphing, CoreMatchers.instanceOf(Serializable.class));
            return morphing.morph(MethodDelegationMorphTest.QUX);
        }
    }

    @SuppressWarnings("unused")
    public static class SimpleMorphIllegal {
        public static String intercept(@Morph
        Void morphing) {
            return null;
        }
    }
}

