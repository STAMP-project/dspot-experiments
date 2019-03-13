package net.bytebuddy.dynamic.loading;


import java.lang.reflect.Method;
import java.util.Collections;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.ClassReflectionInjectionAvailableRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingReflection.Dispatcher.Direct.make;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingReflection.Dispatcher.Unavailable.<init>;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingReflection.isAvailable;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;


public class ClassInjectorUsingReflectionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule classInjectionAvailableRule = new ClassReflectionInjectionAvailableRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private ClassLoader classLoader;

    @Test(expected = IllegalArgumentException.class)
    public void testBootstrapClassLoader() throws Exception {
        new ClassInjector.UsingReflection(ClassLoadingStrategy.BOOTSTRAP_LOADER);
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testInjection() throws Exception {
        new ClassInjector.UsingReflection(classLoader).inject(Collections.singletonMap(of(ClassInjectorUsingReflectionTest.Foo.class), read(ClassInjectorUsingReflectionTest.Foo.class)));
        MatcherAssert.assertThat(classLoader.loadClass(ClassInjectorUsingReflectionTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    @JavaVersionRule.Enforce(atMost = 8)
    public void testDirectInjection() throws Exception {
        ClassInjector.UsingReflection.Dispatcher dispatcher = make().initialize();
        MatcherAssert.assertThat(dispatcher.getPackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName()), CoreMatchers.nullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.definePackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName(), null, null, null, null, null, null, null), CoreMatchers.notNullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.findClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName()), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(dispatcher.defineClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName(), read(ClassInjectorUsingReflectionTest.Foo.class), null), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(classLoader.loadClass(ClassInjectorUsingReflectionTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    @JavaVersionRule.Enforce(atMost = 10)
    public void testUnsafeInjection() throws Exception {
        ClassInjector.UsingReflection.Dispatcher dispatcher = UsingUnsafeInjection.make().initialize();
        MatcherAssert.assertThat(dispatcher.getPackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName()), CoreMatchers.nullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.definePackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName(), null, null, null, null, null, null, null), CoreMatchers.notNullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.findClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName()), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(dispatcher.defineClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName(), read(ClassInjectorUsingReflectionTest.Foo.class), null), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(classLoader.loadClass(ClassInjectorUsingReflectionTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testUnsafeOverride() throws Exception {
        ClassInjector.UsingReflection.Dispatcher dispatcher = UsingUnsafeOverride.make().initialize();
        MatcherAssert.assertThat(dispatcher.getPackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName()), CoreMatchers.nullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.definePackage(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getPackage().getName(), null, null, null, null, null, null, null), CoreMatchers.notNullValue(Package.class));
        MatcherAssert.assertThat(dispatcher.findClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName()), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(dispatcher.defineClass(classLoader, ClassInjectorUsingReflectionTest.Foo.class.getName(), read(ClassInjectorUsingReflectionTest.Foo.class), null), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(classLoader.loadClass(ClassInjectorUsingReflectionTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
    }

    @Test
    public void testDispatcherFaultyInitializationGetClass() throws Exception {
        MatcherAssert.assertThat(new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable("foo").initialize().findClass(getClass().getClassLoader(), Object.class.getName()), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    public void testDispatcherFaultyInitializationGetClassInexistent() throws Exception {
        MatcherAssert.assertThat(new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable("foo").initialize().findClass(getClass().getClassLoader(), ClassInjectorUsingReflectionTest.FOO), CoreMatchers.nullValue(Class.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyInitializationDefineClass() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable("foo").initialize().defineClass(null, null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyInitializationGetPackage() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable("foo").initialize().getPackage(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyInitializationDefinePackage() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable("foo").initialize().definePackage(null, null, null, null, null, null, null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyDispatcherDefineClass() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Unavailable(ClassInjectorUsingReflectionTest.FOO).defineClass(null, null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyDispatcherGetPackage() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Unavailable(ClassInjectorUsingReflectionTest.FOO).getPackage(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDispatcherFaultyDispatcherDefinePackage() throws Exception {
        new ClassInjector.UsingReflection.Dispatcher.Unavailable(ClassInjectorUsingReflectionTest.FOO).definePackage(null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testLegacyDispatcherGetLock() throws Exception {
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        MatcherAssert.assertThat(new ClassInjector.UsingReflection.Dispatcher.Direct.ForLegacyVm(null, null, null, null).getClassLoadingLock(classLoader, ClassInjectorUsingReflectionTest.FOO), CoreMatchers.is(((Object) (classLoader))));
    }

    @Test
    public void testFaultyDispatcherGetLock() throws Exception {
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        MatcherAssert.assertThat(new ClassInjector.UsingReflection.Dispatcher.Unavailable(null).getClassLoadingLock(classLoader, ClassInjectorUsingReflectionTest.FOO), CoreMatchers.is(((Object) (classLoader))));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testInjectionOrderNoPrematureAuxiliaryInjection() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(ClassInjectorUsingReflectionTest.Bar.class, ClassInjectorUsingReflectionTest.Interceptor.class));
        Class<?> type = new ByteBuddy().rebase(ClassInjectorUsingReflectionTest.Bar.class).method(ElementMatchers.named(ClassInjectorUsingReflectionTest.BAR)).intercept(MethodDelegation.to(ClassInjectorUsingReflectionTest.Interceptor.class)).make().load(classLoader, INJECTION).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(ClassInjectorUsingReflectionTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), ClassInjectorUsingReflectionTest.FOO), CoreMatchers.is(((Object) (ClassInjectorUsingReflectionTest.BAR))));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testAvailability() throws Exception {
        MatcherAssert.assertThat(isAvailable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isAlive(), CoreMatchers.is(true));
        MatcherAssert.assertThat(new ClassInjector.UsingReflection.Dispatcher.Initializable.Unavailable(null).isAvailable(), CoreMatchers.is(false));
    }

    /* Note: Foo is know to the system class loader but not to the bootstrap class loader */
    private static class Foo {}

    public static class Bar {
        public String bar(String value) {
            return value;
        }
    }

    public static class Interceptor {
        @RuntimeType
        public static Object intercept(@Super(proxyType = TargetType.class)
        Object zuper, @AllArguments
        Object[] args, @Origin
        Method method) throws Throwable {
            args[0] = ClassInjectorUsingReflectionTest.BAR;
            return method.invoke(zuper, args);
        }
    }
}

