package net.bytebuddy.implementation;


import java.io.Serializable;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.Pipe;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.Pipe.Binder.install;


public class MethodDelegationPipeTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final int BAZ = 42;

    @Test
    public void testPipeToIdenticalType() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new MethodDelegationPipeTest.Foo(MethodDelegationPipeTest.FOO)))).make().load(MethodDelegationPipeTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationPipeTest.QUX), CoreMatchers.is(((MethodDelegationPipeTest.FOO) + (MethodDelegationPipeTest.QUX))));
    }

    @Test
    public void testPipeToIdenticalTypeVoid() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Qux> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Qux.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new MethodDelegationPipeTest.Qux()))).make().load(MethodDelegationPipeTest.Qux.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Qux instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        instance.assertZeroCalls();
    }

    @Test
    public void testPipeToIdenticalTypePrimitive() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Baz> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Baz.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Baz.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.PrimitiveForwardingInterceptor(new MethodDelegationPipeTest.Baz()))).make().load(MethodDelegationPipeTest.Baz.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Baz instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationPipeTest.BAZ), CoreMatchers.is(((MethodDelegationPipeTest.BAZ) * 2L)));
        instance.assertZeroCalls();
    }

    @Test
    public void testPipeToSubtype() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new MethodDelegationPipeTest.Bar(MethodDelegationPipeTest.FOO)))).make().load(MethodDelegationPipeTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationPipeTest.QUX), CoreMatchers.is(((MethodDelegationPipeTest.FOO) + (MethodDelegationPipeTest.QUX))));
    }

    @Test
    public void testPipeSerialization() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.SerializableForwardingInterceptor(new MethodDelegationPipeTest.Foo(MethodDelegationPipeTest.FOO)))).make().load(MethodDelegationPipeTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationPipeTest.QUX), CoreMatchers.is(((MethodDelegationPipeTest.FOO) + (MethodDelegationPipeTest.QUX))));
    }

    @Test(expected = ClassCastException.class)
    public void testPipeToIncompatibleTypeThrowsException() throws Exception {
        DynamicType.Loaded<MethodDelegationPipeTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationPipeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()))).make().load(MethodDelegationPipeTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationPipeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo(MethodDelegationPipeTest.QUX);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeDoesNotDeclareCorrectMethodThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(Serializable.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeDoesInheritFromOtherTypeThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.InheritingForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeIsNotPublicThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.PackagePrivateForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeDoesNotDeclareCorrectMethodSignatureThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.WrongParametersForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPipeTypeIsNotInterfaceThrowsException() throws Exception {
        MethodDelegation.withDefaultConfiguration().withBinders(install(Object.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()));
    }

    @Test(expected = IllegalStateException.class)
    public void testPipeTypeOnTargetInterceptorThrowsException() throws Exception {
        new ByteBuddy().subclass(MethodDelegationPipeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationPipeTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(MethodDelegationPipeTest.WrongParameterTypeTarget.class)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPipeToPreotectedMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isClone()).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationPipeTest.ForwardingType.class)).to(new MethodDelegationPipeTest.ForwardingInterceptor(new Object()))).make();
    }

    public interface ForwardingType<T, S> {
        S doPipe(T target);
    }

    /* empty */
    public interface InheritingForwardingType extends MethodDelegationPipeTest.ForwardingType<Object, Object> {}

    interface PackagePrivateForwardingType<T, S> {
        S doPipe(T target);
    }

    public interface WrongParametersForwardingType<T extends Number, S> {
        S doPipe(T target);
    }

    public static class ForwardingInterceptor {
        private final Object target;

        public ForwardingInterceptor(Object target) {
            this.target = target;
        }

        public String intercept(@Pipe
        MethodDelegationPipeTest.ForwardingType<Object, String> pipe) {
            MatcherAssert.assertThat(pipe, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return pipe.doPipe(target);
        }
    }

    public static class Foo {
        private final String prefix;

        public Foo() {
            prefix = MethodDelegationPipeTest.BAR;
        }

        public Foo(String prefix) {
            this.prefix = prefix;
        }

        public String foo(String input) {
            return (prefix) + input;
        }
    }

    public static class Bar extends MethodDelegationPipeTest.Foo {
        public Bar(String prefix) {
            super(prefix);
        }
    }

    public static class Qux extends CallTraceable {
        public void foo() {
            register(MethodDelegationPipeTest.FOO);
        }
    }

    public static class PrimitiveForwardingInterceptor {
        private final Object target;

        public PrimitiveForwardingInterceptor(Object target) {
            this.target = target;
        }

        @RuntimeType
        public Object intercept(@Pipe
        MethodDelegationPipeTest.ForwardingType<Object, Object> pipe) {
            MatcherAssert.assertThat(pipe, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return pipe.doPipe(target);
        }
    }

    public static class Baz extends CallTraceable {
        public long foo(int value) {
            register(MethodDelegationPipeTest.FOO);
            return value * 2L;
        }
    }

    public static class WrongParameterTypeTarget {
        public static String intercept(@Pipe
        Callable<?> pipe) {
            return null;
        }
    }

    public static class SerializableForwardingInterceptor {
        private final Object target;

        public SerializableForwardingInterceptor(Object target) {
            this.target = target;
        }

        public String intercept(@Pipe(serializableProxy = true)
        MethodDelegationPipeTest.ForwardingType<Object, String> pipe) {
            MatcherAssert.assertThat(pipe, CoreMatchers.instanceOf(Serializable.class));
            return pipe.doPipe(target);
        }
    }
}

