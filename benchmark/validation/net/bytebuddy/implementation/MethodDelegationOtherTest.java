package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.DEFAULT;
import static net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS;


public class MethodDelegationOtherTest {
    @Test(expected = IllegalStateException.class)
    public void testDelegationToInvisibleInstanceThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.isToString()).intercept(MethodDelegation.to(new MethodDelegationOtherTest.Foo())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testDelegationToInvisibleFieldTypeThrowsException() throws Exception {
        new ByteBuddy().with(TypeValidation.DISABLED).subclass(Object.class).defineField("foo", MethodDelegationOtherTest.Foo.class).method(ElementMatchers.isToString()).intercept(MethodDelegation.toField("foo")).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelegationWithIllegalType() throws Exception {
        MethodDelegation.to(new Object(), String.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldDoesNotExist() throws Exception {
        new ByteBuddy().subclass(Object.class).method(ElementMatchers.any()).intercept(MethodDelegation.toField("foo")).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotDelegateToInstanceFieldFromStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField("foo", Object.class).defineMethod("bar", void.class, Ownership.STATIC).intercept(MethodDelegation.toField("foo")).make();
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        MatcherAssert.assertThat(MethodDelegation.withEmptyConfiguration().withBinders(DEFAULTS).withResolvers(DEFAULT), FieldByFieldComparison.hasPrototype(MethodDelegation.withDefaultConfiguration()));
    }

    /* empty */
    static class Foo {}
}

