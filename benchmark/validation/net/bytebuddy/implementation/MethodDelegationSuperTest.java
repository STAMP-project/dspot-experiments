package net.bytebuddy.implementation;


import java.io.Serializable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy.INSTANCE;
import static net.bytebuddy.implementation.bind.annotation.Super.Instantiation.UNSAFE;


public class MethodDelegationSuperTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testSuperInstance() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.Baz.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testSuperInterface() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.FooBar.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testSuperInstanceUnsafe() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.QuxBaz.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testBridgeMethodResolution() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Bar> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Bar.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.GenericBaz.class)).make().load(MethodDelegationSuperTest.Bar.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(MethodDelegationSuperTest.BAR), CoreMatchers.is(((MethodDelegationSuperTest.BAR) + (MethodDelegationSuperTest.QUX))));
    }

    @Test(expected = AbstractMethodError.class)
    public void testSuperCallOnAbstractMethod() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.FooBarQuxBaz> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.FooBarQuxBaz.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.FooBarQuxBaz.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.FooBar.class)).make().load(MethodDelegationSuperTest.FooBarQuxBaz.class.getClassLoader(), WRAPPER);
        loaded.getLoaded().getDeclaredConstructor().newInstance().qux();
    }

    @Test
    public void testSerializableProxy() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.SerializationCheck.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testTargetTypeProxy() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.TargetTypeTest.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testExplicitTypeProxy() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperTest.ExplicitTypeTest.class)).make().load(MethodDelegationSuperTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.qux(), CoreMatchers.is(((Object) ((MethodDelegationSuperTest.FOO) + (MethodDelegationSuperTest.QUX)))));
    }

    @Test
    public void testFinalType() throws Exception {
        InjectionClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, readToNames(MethodDelegationSuperTest.SimpleInterceptor.class));
        Class<?> type = new ByteBuddy().rebase(MethodDelegationSuperTest.FinalType.class).modifiers(TypeManifestation.PLAIN, Visibility.PUBLIC).method(ElementMatchers.named(MethodDelegationSuperTest.FOO)).intercept(ExceptionMethod.throwing(RuntimeException.class)).method(ElementMatchers.named(MethodDelegationSuperTest.BAR)).intercept(MethodDelegation.to(MethodDelegationSuperTest.SimpleInterceptor.class)).make().load(classLoader, INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(MethodDelegationSuperTest.BAR).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (MethodDelegationSuperTest.FOO))));
    }

    public interface Qux {
        Object qux();
    }

    public static class Foo implements MethodDelegationSuperTest.Qux {
        public Object qux() {
            return MethodDelegationSuperTest.FOO;
        }
    }

    public static class Baz {
        public static String baz(@Super
        MethodDelegationSuperTest.Foo foo) {
            MatcherAssert.assertThat(foo, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return (foo.qux()) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static class FooBar {
        public static String baz(@Super
        MethodDelegationSuperTest.Qux foo) {
            MatcherAssert.assertThat(foo, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return (foo.qux()) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static class QuxBaz {
        public static String baz(@Super(strategy = UNSAFE)
        MethodDelegationSuperTest.Foo foo) {
            MatcherAssert.assertThat(foo, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return (foo.qux()) + (MethodDelegationSuperTest.QUX);
        }
    }

    public abstract static class FooBarQuxBaz implements MethodDelegationSuperTest.Qux {
        public abstract Object qux();
    }

    public static class GenericBase<T> {
        public T qux(T value) {
            return value;
        }
    }

    public static class Bar extends MethodDelegationSuperTest.GenericBase<String> {
        public String qux(String value) {
            return super.qux(value);
        }
    }

    public static class GenericBaz {
        public static String baz(String value, @Super
        MethodDelegationSuperTest.GenericBase<String> foo) {
            MatcherAssert.assertThat(foo, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return (foo.qux(value)) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static class SerializationCheck {
        public static String baz(@Super(serializableProxy = true)
        MethodDelegationSuperTest.Foo foo) {
            MatcherAssert.assertThat(foo, CoreMatchers.instanceOf(Serializable.class));
            return (foo.qux()) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static class TargetTypeTest {
        public static String baz(@Super(proxyType = TargetType.class)
        Object proxy) throws Exception {
            MatcherAssert.assertThat(proxy, CoreMatchers.instanceOf(MethodDelegationSuperTest.Foo.class));
            return (MethodDelegationSuperTest.Foo.class.getDeclaredMethod(MethodDelegationSuperTest.QUX).invoke(proxy)) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static class ExplicitTypeTest {
        public static String baz(@Super(proxyType = MethodDelegationSuperTest.Qux.class)
        Object proxy) throws Exception {
            MatcherAssert.assertThat(proxy, CoreMatchers.instanceOf(MethodDelegationSuperTest.Qux.class));
            MatcherAssert.assertThat(proxy, CoreMatchers.not(CoreMatchers.instanceOf(MethodDelegationSuperTest.Foo.class)));
            return (MethodDelegationSuperTest.Qux.class.getDeclaredMethod(MethodDelegationSuperTest.QUX).invoke(proxy)) + (MethodDelegationSuperTest.QUX);
        }
    }

    public static final class FinalType {
        public Object foo() {
            return MethodDelegationSuperTest.FOO;
        }

        public Object bar() {
            return null;
        }
    }

    public static class SimpleInterceptor {
        public static Object intercept(@Super
        MethodDelegationSuperTest.FinalType finalType) {
            return finalType.foo();
        }
    }
}

