package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class DefaultMethodCallTest {
    private static final String FOO = "foo";

    private static final String QUX = "qux";

    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String SINGLE_DEFAULT_METHOD_CLASS = "net.bytebuddy.test.precompiled.SingleDefaultMethodClass";

    private static final String CONFLICTING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    private static final String NON_OVERRIDING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodNonOverridingInterface";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(8)
    public void testUnambiguousDefaultMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD)).intercept(DefaultMethodCall.unambiguousOnly()).make().load(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(DefaultMethodCallTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (DefaultMethodCallTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testAmbiguousDefaultMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD), Class.forName(DefaultMethodCallTest.CONFLICTING_INTERFACE)).intercept(DefaultMethodCall.unambiguousOnly()).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testAmbiguousDefaultMethodWithExplicitPreference() throws Exception {
        Class<?> singleMethodInterface = Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD);
        Class<?> conflictingInterface = Class.forName(DefaultMethodCallTest.CONFLICTING_INTERFACE);
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.FOO, singleMethodInterface);
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.QUX, conflictingInterface);
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.FOO, singleMethodInterface, conflictingInterface);
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.QUX, conflictingInterface, singleMethodInterface);
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.FOO, singleMethodInterface, Class.forName(DefaultMethodCallTest.NON_OVERRIDING_INTERFACE));
        assertConflictChoice(singleMethodInterface, conflictingInterface, DefaultMethodCallTest.FOO, Class.forName(DefaultMethodCallTest.NON_OVERRIDING_INTERFACE), singleMethodInterface);
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testUnrelatedPreferredDefaultMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD), Class.forName(DefaultMethodCallTest.CONFLICTING_INTERFACE)).intercept(DefaultMethodCall.prioritize(Class.forName(DefaultMethodCallTest.NON_OVERRIDING_INTERFACE))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testNonDeclaredDefaultMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD_CLASS)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(DefaultMethodCall.unambiguousOnly()).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testNonDeclaredPreferredDefaultMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD_CLASS)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(DefaultMethodCall.prioritize(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD))).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDeclaredAndImplementedMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD_CLASS)).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(DefaultMethodCall.unambiguousOnly()).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(DefaultMethodCallTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (DefaultMethodCallTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testDeclaredAndImplementedAmbiguousMethodThrowsException() throws Exception {
        new ByteBuddy().subclass(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD_CLASS)).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD), Class.forName(DefaultMethodCallTest.CONFLICTING_INTERFACE)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(DefaultMethodCall.unambiguousOnly()).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDeclaredAndImplementedAmbiguousMethodWithPreference() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD_CLASS)).implement(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD), Class.forName(DefaultMethodCallTest.CONFLICTING_INTERFACE)).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(DefaultMethodCall.prioritize(Class.forName(DefaultMethodCallTest.SINGLE_DEFAULT_METHOD))).make().load(getClass().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        Method method = loaded.getLoaded().getDeclaredMethod(DefaultMethodCallTest.FOO);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (DefaultMethodCallTest.FOO))));
    }
}

