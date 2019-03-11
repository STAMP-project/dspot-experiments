package net.bytebuddy.implementation.bind.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder.ParameterBinder.ForFixedValue.OfConstant.of;


@RunWith(Parameterized.class)
public class TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest {
    private static final String FOO = "foo";

    private static final byte NUMERIC_VALUE = 42;

    private final Object value;

    public TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest(Object value) {
        this.value = value;
    }

    @Test
    public void testConstant() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.Foo.class).method(ElementMatchers.named(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.FOO)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(of(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.Bar.class, value)).to(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.Foo.class)).make().load(TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(value));
    }

    public static class Foo {
        public static Object intercept(@TargetMethodAnnotationDriverBinderParameterBinderForFixedValueOfConstantTest.Bar
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

