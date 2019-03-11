package net.bytebuddy.implementation.bind.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder.ParameterBinder.ForFixedValue.OfConstant.of;
import static net.bytebuddy.utility.JavaConstant.MethodType.ofLoaded;


public class TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest {
    private static final String FOO = "foo";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testTypeDescription() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, TypeDescription.OBJECT)).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    public void testNull() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, null)).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.nullValue(Object.class));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodHandleLoaded() throws Exception {
        Method publicLookup = Class.forName("java.lang.invoke.MethodHandles").getDeclaredMethod("publicLookup");
        Object lookup = publicLookup.invoke(null);
        Method unreflected = Class.forName("java.lang.invoke.MethodHandles$Lookup").getDeclaredMethod("unreflect", Method.class);
        Object methodHandleLoaded = unreflected.invoke(lookup, TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getDeclaredMethod(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO));
        MatcherAssert.assertThat(net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, methodHandleLoaded)).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo()), CoreMatchers.is(net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(methodHandleLoaded)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodHandle() throws Exception {
        Method publicLookup = Class.forName("java.lang.invoke.MethodHandles").getDeclaredMethod("publicLookup");
        Object lookup = publicLookup.invoke(null);
        Method unreflected = Class.forName("java.lang.invoke.MethodHandles$Lookup").getDeclaredMethod("unreflect", Method.class);
        Object methodHandleLoaded = unreflected.invoke(lookup, TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getDeclaredMethod(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO));
        MatcherAssert.assertThat(net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(methodHandleLoaded))).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo()), CoreMatchers.is(net.bytebuddy.utility.JavaConstant.MethodHandle.ofLoaded(methodHandleLoaded)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodTypeLoaded() throws Exception {
        Object loadedMethodType = JavaType.METHOD_TYPE.load().getDeclaredMethod("methodType", Class.class, Class[].class).invoke(null, void.class, new Class<?>[]{ Object.class });
        MatcherAssert.assertThat(ofLoaded(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, loadedMethodType)).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo()), CoreMatchers.is(ofLoaded(loadedMethodType)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodType() throws Exception {
        Object loadedMethodType = JavaType.METHOD_TYPE.load().getDeclaredMethod("methodType", Class.class, Class[].class).invoke(null, void.class, new Class<?>[]{ Object.class });
        MatcherAssert.assertThat(ofLoaded(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, ofLoaded(loadedMethodType))).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo()), CoreMatchers.is(ofLoaded(loadedMethodType)));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalArgument() throws Exception {
        new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar.class, new Object())).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Foo.class)).make();
    }

    public static class Foo {
        public static Object intercept(@TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantOtherTest.Bar
        Object value) {
            return value;
        }

        public Object foo() {
            throw new AssertionError();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Bar {}
}

