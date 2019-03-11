package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationDefaultTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String DEFAULT_INTERFACE = "net.bytebuddy.test.precompiled.DelegationDefaultInterface";

    private static final String DELEGATION_TARGET = "net.bytebuddy.test.precompiled.DelegationDefaultTarget";

    private static final String DELEGATION_TARGET_SERIALIZABLE = "net.bytebuddy.test.precompiled.DelegationDefaultTargetSerializable";

    private static final String DELEGATION_TARGET_EXPLICIT = "net.bytebuddy.test.precompiled.DelegationDefaultTargetExplicit";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterface() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultTest.DELEGATION_TARGET))).make().load(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getDeclaredMethod(MethodDelegationDefaultTest.FOO).invoke(instance), CoreMatchers.is(((Object) ((MethodDelegationDefaultTest.FOO) + (MethodDelegationDefaultTest.BAR)))));
    }

    @Test(expected = AbstractMethodError.class)
    @JavaVersionRule.Enforce(8)
    public void testNoDefaultInterface() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(MethodDelegationDefaultTest.DelegationNoDefaultInterface.class).intercept(MethodDelegation.to(MethodDelegationDefaultTest.DelegationNoDefaultInterfaceInterceptor.class)).make().load(MethodDelegationDefaultTest.DelegationNoDefaultInterface.class.getClassLoader(), WRAPPER);
        MethodDelegationDefaultTest.DelegationNoDefaultInterface instance = ((MethodDelegationDefaultTest.DelegationNoDefaultInterface) (loaded.getLoaded().getDeclaredConstructor().newInstance()));
        instance.foo();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceSerializableProxy() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultTest.DELEGATION_TARGET_SERIALIZABLE))).make().load(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getDeclaredMethod(MethodDelegationDefaultTest.FOO).invoke(instance), CoreMatchers.is(((Object) ((MethodDelegationDefaultTest.FOO) + (MethodDelegationDefaultTest.BAR)))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceExplicitProxyType() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultTest.DELEGATION_TARGET_EXPLICIT))).make().load(Class.forName(MethodDelegationDefaultTest.DEFAULT_INTERFACE).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getDeclaredMethod(MethodDelegationDefaultTest.FOO).invoke(instance), CoreMatchers.is(((Object) ((MethodDelegationDefaultTest.FOO) + (MethodDelegationDefaultTest.BAR)))));
    }

    public interface DelegationNoDefaultInterface {
        String foo();
    }

    public static class DelegationNoDefaultInterfaceInterceptor {
        public static String intercept(@net.bytebuddy.implementation.bind.annotation.Default
        MethodDelegationDefaultTest.DelegationNoDefaultInterface proxy) {
            return proxy.foo();
        }
    }
}

