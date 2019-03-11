package net.bytebuddy.implementation;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationOriginTest {
    private static final String FOO = "foo";

    private static final String TYPE = "TYPE";

    private static final String ORIGIN_METHOD_HANDLE = "net.bytebuddy.test.precompiled.OriginMethodHandle";

    private static final String ORIGIN_METHOD_TYPE = "net.bytebuddy.test.precompiled.OriginMethodType";

    private static final String ORIGIN_EXECUTABLE = "net.bytebuddy.test.precompiled.OriginExecutable";

    private static final String ORIGIN_EXECUTABLE_CACHED = "net.bytebuddy.test.precompiled.OriginExecutableWithCache";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testOriginClass() throws Exception {
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginClass.class)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.instanceOf(Class.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class))));
    }

    @Test
    public void testOriginMethodWithoutCache() throws Exception {
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginMethod.class)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object method = instance.foo();
        MatcherAssert.assertThat(method, CoreMatchers.instanceOf(Method.class));
        MatcherAssert.assertThat(method, CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO)))));
        MatcherAssert.assertThat(method, CoreMatchers.not(CoreMatchers.sameInstance(instance.foo())));
    }

    @Test
    public void testOriginMethodWithCache() throws Exception {
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginMethodWithCache.class)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object method = instance.foo();
        MatcherAssert.assertThat(method, CoreMatchers.instanceOf(Method.class));
        MatcherAssert.assertThat(method, CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO)))));
        MatcherAssert.assertThat(method, CoreMatchers.sameInstance(instance.foo()));
    }

    @Test
    public void testOriginMethodWithPrivilege() throws Exception {
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginMethodWithPrivilege.class)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(1));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object method = instance.foo();
        MatcherAssert.assertThat(method, CoreMatchers.instanceOf(Method.class));
        MatcherAssert.assertThat(method, CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO)))));
        MatcherAssert.assertThat(method, CoreMatchers.sameInstance(instance.foo()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOriginConstructorWithoutCache() throws Exception {
        MethodDelegationOriginTest.OriginConstructor originConstructor = new MethodDelegationOriginTest.OriginConstructor();
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(originConstructor))).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.is(((Constructor) (loaded.getLoaded().getDeclaredConstructor()))));
        Constructor<?> previous = originConstructor.constructor;
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.is(((Constructor) (loaded.getLoaded().getDeclaredConstructor()))));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.not(CoreMatchers.sameInstance(((Constructor) (previous)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOriginConstructorWithCache() throws Exception {
        MethodDelegationOriginTest.OriginConstructorWithCache originConstructor = new MethodDelegationOriginTest.OriginConstructorWithCache();
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(originConstructor))).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.is(((Constructor) (loaded.getLoaded().getDeclaredConstructor()))));
        Constructor<?> previous = originConstructor.constructor;
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.sameInstance(((Constructor) (previous))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOriginConstructorWithPrivilege() throws Exception {
        MethodDelegationOriginTest.OriginConstructorWithPrivilege originConstructor = new MethodDelegationOriginTest.OriginConstructorWithPrivilege();
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(originConstructor))).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(1));
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.is(((Constructor) (loaded.getLoaded().getDeclaredConstructor()))));
        Constructor<?> previous = originConstructor.constructor;
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(originConstructor.constructor, CoreMatchers.sameInstance(((Constructor) (previous))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testOriginExecutableOnMethodWithoutCache() throws Exception {
        Object origin = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE).getDeclaredConstructor().newInstance();
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(origin)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object method = instance.foo();
        MatcherAssert.assertThat(method, CoreMatchers.instanceOf(Method.class));
        MatcherAssert.assertThat(method, CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO)))));
        MatcherAssert.assertThat(method, CoreMatchers.not(CoreMatchers.sameInstance(instance.foo())));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testOriginExecutableOnMethodWithCache() throws Exception {
        Object origin = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE_CACHED).getDeclaredConstructor().newInstance();
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(origin)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object method = instance.foo();
        MatcherAssert.assertThat(method, CoreMatchers.instanceOf(Method.class));
        MatcherAssert.assertThat(method, CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO)))));
        MatcherAssert.assertThat(method, CoreMatchers.sameInstance(instance.foo()));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testOriginExecutableConstructorWithoutCache() throws Exception {
        Object originConstructor = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE).getDeclaredConstructor().newInstance();
        Field constructor = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE).getDeclaredField("executable");
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(originConstructor))).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.is(((Object) (loaded.getLoaded().getDeclaredConstructor()))));
        Object previous = constructor.get(originConstructor);
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.is(((Object) (loaded.getLoaded().getDeclaredConstructor()))));
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.not(CoreMatchers.sameInstance(previous)));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testOriginExecutableConstructorWithCache() throws Exception {
        Object originConstructor = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE_CACHED).getDeclaredConstructor().newInstance();
        Field constructor = Class.forName(MethodDelegationOriginTest.ORIGIN_EXECUTABLE_CACHED).getDeclaredField("executable");
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(originConstructor))).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.is(((Object) (loaded.getLoaded().getDeclaredConstructor()))));
        Object previous = constructor.get(originConstructor);
        loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.instanceOf(Constructor.class));
        MatcherAssert.assertThat(constructor.get(originConstructor), CoreMatchers.sameInstance(previous));
    }

    @Test
    public void testOriginString() throws Exception {
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginString.class)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.instanceOf(String.class));
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationOriginTest.Foo.class.getDeclaredMethod(MethodDelegationOriginTest.FOO).toString()))));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testOriginMethodHandle() throws Throwable {
        Class<?> originMethodHandle = Class.forName(MethodDelegationOriginTest.ORIGIN_METHOD_HANDLE);
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(originMethodHandle)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.instanceOf(((Class<?>) (originMethodHandle.getDeclaredField(MethodDelegationOriginTest.TYPE).get(null)))));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testOriginMethodType() throws Throwable {
        Class<?> originMethodType = Class.forName(MethodDelegationOriginTest.ORIGIN_METHOD_TYPE);
        DynamicType.Loaded<MethodDelegationOriginTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(originMethodType)).make().load(MethodDelegationOriginTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodDelegationOriginTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.instanceOf(((Class<?>) (originMethodType.getDeclaredField(MethodDelegationOriginTest.TYPE).get(null)))));
    }

    @Test(expected = IllegalStateException.class)
    public void testOriginIllegal() throws Exception {
        new ByteBuddy().subclass(MethodDelegationOriginTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationOriginTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationOriginTest.OriginIllegal.class)).make();
    }

    public static class Foo {
        public Object foo() {
            return null;
        }
    }

    public static class OriginClass {
        public static Object foo(@Origin
        Class<?> type) {
            return type;
        }
    }

    public static class OriginMethod {
        public static Object foo(@Origin(cache = false)
        Method method) {
            return method;
        }
    }

    public static class OriginMethodWithCache {
        public static Object foo(@Origin
        Method method) {
            return method;
        }
    }

    public static class OriginMethodWithPrivilege {
        public static Object foo(@Origin(privileged = true)
        Method method) {
            return method;
        }
    }

    public static class OriginConstructor {
        private Constructor<?> constructor;

        public void foo(@Origin(cache = false)
        Constructor<?> constructor) {
            this.constructor = constructor;
        }
    }

    public static class OriginConstructorWithCache {
        private Constructor<?> constructor;

        public void foo(@Origin
        Constructor<?> constructor) {
            this.constructor = constructor;
        }
    }

    public static class OriginConstructorWithPrivilege {
        private Constructor<?> constructor;

        public void foo(@Origin(privileged = true)
        Constructor<?> constructor) {
            this.constructor = constructor;
        }
    }

    public static class OriginString {
        public static Object foo(@Origin
        String string) {
            return string;
        }
    }

    public static class OriginIllegal {
        public static Object foo(@Origin
        Object object) {
            return object;
        }
    }
}

