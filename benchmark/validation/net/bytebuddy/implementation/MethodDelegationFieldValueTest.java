package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationFieldValueTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testLegalFieldAccess() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.SimpleField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.SimpleField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.SimpleField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.SimpleInterceptor.class)).make().load(MethodDelegationFieldValueTest.SimpleField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        instance.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test
    public void testLegalFieldAccessStatic() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.SimpleStaticField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.SimpleStaticField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.SimpleStaticField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.SimpleInterceptor.class)).make().load(MethodDelegationFieldValueTest.SimpleStaticField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleStaticField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MethodDelegationFieldValueTest.SimpleStaticField.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        MethodDelegationFieldValueTest.SimpleStaticField.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonAssignableFieldAccess() throws Exception {
        new ByteBuddy().subclass(MethodDelegationFieldValueTest.SimpleField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.SimpleField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.NonAssignableInterceptor.class)).make();
    }

    @Test
    public void testLegalFieldAccessDynamicTyping() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.SimpleStaticField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.SimpleStaticField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.SimpleStaticField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.DynamicInterceptor.class)).make().load(MethodDelegationFieldValueTest.SimpleStaticField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleStaticField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MethodDelegationFieldValueTest.SimpleStaticField.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        MethodDelegationFieldValueTest.SimpleStaticField.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test
    public void testExtendedFieldMostSpecific() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.ExtendedField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.ExtendedField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.ExtendedField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.SimpleInterceptor.class)).make().load(MethodDelegationFieldValueTest.ExtendedField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.ExtendedField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        instance.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test
    public void testExtendedFieldSkipsNonVisible() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.ExtendedPrivateField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.ExtendedPrivateField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.ExtendedPrivateField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.SimpleInterceptor.class)).make().load(MethodDelegationFieldValueTest.ExtendedPrivateField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        instance.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test
    public void testExtendedFieldExplicitType() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.ExtendedField> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.ExtendedField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.ExtendedField.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.ExplicitInterceptor.class)).make().load(MethodDelegationFieldValueTest.ExtendedField.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleField instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        instance.foo = MethodDelegationFieldValueTest.BAR;
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    @Test
    public void testAccessor() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldValueTest.SimpleFieldAccessor> loaded = new ByteBuddy().subclass(MethodDelegationFieldValueTest.SimpleFieldAccessor.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldValueTest.SimpleFieldAccessor.class)).intercept(MethodDelegation.to(MethodDelegationFieldValueTest.SimpleAccessorInterceptor.class)).make().load(MethodDelegationFieldValueTest.SimpleFieldAccessor.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldValueTest.SimpleFieldAccessor instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo = MethodDelegationFieldValueTest.FOO;
        MatcherAssert.assertThat(instance.getFoo(), CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.FOO))));
        instance.foo = MethodDelegationFieldValueTest.BAR;
        instance.setFoo(MethodDelegationFieldValueTest.FOO);
        MatcherAssert.assertThat(instance.foo, CoreMatchers.is(((Object) (MethodDelegationFieldValueTest.BAR))));
    }

    public static class SimpleField {
        public Object foo;

        public Object foo() {
            return null;
        }
    }

    public static class SimpleStaticField {
        public static Object foo;

        public Object foo() {
            return null;
        }
    }

    public static class SimpleInterceptor {
        public static Object intercept(@FieldValue(MethodDelegationFieldValueTest.FOO)
        Object value) {
            return value;
        }
    }

    public static class NonAssignableInterceptor {
        public static Object intercept(@FieldValue(MethodDelegationFieldValueTest.FOO)
        String value) {
            return value;
        }
    }

    public static class DynamicInterceptor {
        public static Object intercept(@RuntimeType
        @FieldValue(MethodDelegationFieldValueTest.FOO)
        String value) {
            return value;
        }
    }

    public static class ExtendedField extends MethodDelegationFieldValueTest.SimpleField {
        public Object foo;

        public Object foo() {
            return null;
        }
    }

    public static class ExtendedPrivateField extends MethodDelegationFieldValueTest.SimpleField {
        private Object foo;

        public Object foo() {
            return null;
        }
    }

    public static class ExplicitInterceptor {
        public static Object intercept(@FieldValue(value = MethodDelegationFieldValueTest.FOO, declaringType = MethodDelegationFieldValueTest.SimpleField.class)
        Object value) {
            return value;
        }
    }

    public static class SimpleFieldAccessor {
        public Object foo;

        public Object getFoo() {
            return foo;
        }

        public void setFoo(Object foo) {
            this.foo = foo;
        }
    }

    public static class SimpleAccessorInterceptor {
        public static Object intercept(@FieldValue
        Object value) {
            return value;
        }
    }
}

